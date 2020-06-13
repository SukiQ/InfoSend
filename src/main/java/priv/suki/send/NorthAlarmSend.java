package priv.suki.send;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;

import priv.suki.msg.OmcInfo;
import priv.suki.util.BuiltFunc;
import priv.suki.util.IpUtil;
import priv.suki.util.Propert;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import priv.suki.controller.ContralCenter;
import priv.suki.msg.OrgInfo;
import priv.suki.util.northalarm.CommonACKMsgVO;
import priv.suki.util.northalarm.HeartUtil;
import priv.suki.util.northalarm.NorthUtil;

/**
 * OMC����ӿ�
 *
 * @author ��С��
 * @version 1.0.1
 */
public class NorthAlarmSend implements Send {
    private static Log log = LogFactory.getLog(NorthAlarmSend.class);
    private static Logger logger = Logger.getLogger("Log4jMain");
    private ServerSocket server = null;
    private Socket socket = null;
    private String charset;
    private BufferedInputStream bis = null;
    private BufferedOutputStream bos = null;
    private Thread heartThread;
    private HeartUtil heartUtil;
    private NorthUtil northUtil;

    public NorthAlarmSend() {
    }

    /**
     * OMC�����ͷ���
     *
     * @throws InterruptedException ����쳣
     */
    @Override
    public boolean send(OrgInfo object) throws InterruptedException {
        try {

            /*
             * ����ģʽ����
             */
            if (Propert.getPropert().isFast()) {

                if (ContralCenter.getContral().isNorth_sync_switch()) {
                    ContralCenter.getContral().setNorth_sync_switch(false);
                    int alarmId = Propert.getPropert().getOmcAlarmId();
                    int interval = Propert.getPropert().getInterval();
                    long startTime;
                    long endTime;
                    BuiltFunc func;
                    OrgInfo buildMsg;
                    OrgInfo orgInfo = new OmcInfo();
                    func = new BuiltFunc();
                    orgInfo.initializeMsg();

                    //���ü�ʱ��������������λ��
                    func.setId(ContralCenter.getContral().getNorth_sync_number() - 1);

                    logger.info("��ʼ�����澯�������к�" + ContralCenter.getContral().getNorth_sync_number() + "֮��ʼ����");
                    int tempAlarmId = alarmId - ContralCenter.getContral().getNorth_sync_number();
                    int sendNum = Propert.getPropert().getMsgNum();
                    for (int i = tempAlarmId / Propert.getPropert().getMsgNum(); i >= 0; i--) {

                        startTime = System.currentTimeMillis();

                        if (i == 0) {
                            sendNum = tempAlarmId % Propert.getPropert().getMsgNum();
                        }

                        buildMsg = orgInfo.appendMsg(sendNum, func);

                        byte[] msg = (byte[]) buildMsg.getMsg();
                        bos.write(msg);
                        bos.flush();

                        endTime = System.currentTimeMillis();
                        /*������ʱ*/
                        if (endTime - startTime < interval) {
                            Thread.sleep(interval - (endTime - startTime));
                            continue;

                        }
                        logger.info("����" + buildMsg + "��");
                        log.info("���ͳ�ʱ" + (endTime - startTime - interval) + "����");
                    }
                    logger.info("������������ʼ��������");
                }
                byte[] msg = (byte[]) object.getMsg();
                bos.write(msg);
                bos.flush();
                return true;
            }

            String msg = object.getMsg().toString();
            int alarmId = Propert.getPropert().getOmcAlarmId();
            // ����List
            List<String> cachelist = ContralCenter.getContral().getCachelist();

            /* ��ʼͬ�������澯 */
            if (ContralCenter.getContral().isNorth_sync_switch()) {
                ContralCenter.getContral().setNorth_sync_switch(false);
                logger.info("��ʼ�����澯�������к�" + ContralCenter.getContral().getNorth_sync_number() + "֮��ʼ����");
                int tempAlarmId = alarmId - ContralCenter.getContral().getNorth_sync_number();
                for (int i = tempAlarmId; i > 0; i--) {
                    String cacheMsg = cachelist.remove(i);
                    log.info("ͬ��������Ϣ:" + cacheMsg);
                    northUtil.send(bos, northUtil.buildAlarmReqMsg(cacheMsg));
                    Thread.sleep(1000);
                }
                logger.info("������������ʼ��������");
                ContralCenter.getContral().setCachelist(cachelist);
            }

            northUtil.send(bos, northUtil.buildAlarmReqMsg(msg));

        } catch (InterruptedException interrupted) {

            throw interrupted;


        } catch (Exception e) {

            log.error("����ʧ��", e);
            return false;
        }
        return true;
    }

    @Override
    public boolean init() {
        int port = Propert.getPropert().getNorth_alarm_port();
        charset = Propert.getPropert().getCharset();
        northUtil = new NorthUtil();

        if (server == null) {
            try {
                server = new ServerSocket();
                if (!server.getReuseAddress()) {
                    server.setReuseAddress(true);
                }
                server.bind(new InetSocketAddress(port));
            } catch (IOException e) {
                log.error("�󶨶˿ڳ����쳣", e);
                return false;

            }

            logger.info("OMC����ӿڷ����ip:" + IpUtil.getLocalIpByNetcard() + "�˿�:" + port + "�Ѿ�����");
            try {
                socket = server.accept();
                socket.setTcpNoDelay(true);
                socket.setKeepAlive(true);
            } catch (SocketException so) {
                log.info("��ʼ����ֹͣ");
                return false;
            } catch (IOException e) {
                log.error("�ͻ������ӳ����쳣", e);
                return false;
            }

            String ip = socket.getInetAddress().getHostAddress();
            logger.info("�ͻ���[" + ip + "]���ӷ���˳ɹ�");

            log.info("���õ�ǰ�ַ�����Ϊ��" + charset);

            try {
                bis = new BufferedInputStream(socket.getInputStream());
                bos = new BufferedOutputStream(socket.getOutputStream());

            } catch (IOException e) {
                log.error("��ʼ����Ϣ������", e);
                return false;
            }
            return loginSucc(northUtil);
        }

        return true;
    }

    /**
     * ��½��֤
     *
     * @param northUtil ���Ӷ���
     * @return �Ƿ��½�ɹ�
     */
    private boolean loginSucc(NorthUtil northUtil) {
        CommonACKMsgVO loginAct;
        boolean loginSucc = false;
        try {
            for (int i = 0; i < 2; i++) {
                loginAct = northUtil.receive(bis, charset);

                if (loginAct.getMsgType() == CommonACKMsgVO.REQLOGINALARM) {
                    logger.info("�յ���½����" + loginAct.getMsgBody());

                    if (northUtil.parseRecCom(loginAct, bos)) {
                        logger.info("��½�ɹ�");
                        loginSucc = true;
                        break;
                    }
                }
                logger.error("��½��Ϣ��½ʧ�ܣ��������ԣ����ǵ�" + (i + 1) + "������");
            }
            if (loginSucc) {
                heartUtil = new HeartUtil(northUtil, bis, bos, charset);
                heartThread = new Thread(heartUtil);
                heartThread.start();
                log.info("����������Ϣ�߳��߳�");

                return true;
            }
            logger.error("���γ���ʧ�ܣ��ϵ�����");
            return false;
        } catch (Exception e) {
            logger.error("��½�����쳣����������");
        }
        return false;
    }


    @Override
    public void close() {

        /* �رս����߳� */
        if (heartThread != null) {
            heartUtil.setIsstop(false);
        }

        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                log.error("�ر��������쳣", e);
            } finally {
                socket = null;
            }
        }

        if (server != null) {
            try {
                server.close();
            } catch (IOException e) {
                log.error("�ر��������쳣", e);
            } finally {
                server = null;
            }
        }

        if (bis != null) {
            try {
                bis.close();
            } catch (IOException e) {
                log.error("�ر��������쳣", e);
            } finally {
                bis = null;
            }
        }
        if (bos != null) {
            try {
                bos.close();
            } catch (IOException e) {
                log.error("�ر��������쳣", e);
            } finally {
                bos = null;
            }

        }

    }

}

package priv.suki.send;

import priv.suki.controller.ContralCenter;
import priv.suki.msg.I1AlarmInfo;
import priv.suki.msg.OrgInfo;
import priv.suki.util.BuiltFunc;
import priv.suki.util.IpUtil;
import priv.suki.util.Propert;
import priv.suki.util.i1.I1CommonACKMsgVO;
import priv.suki.util.i1.I1HeartUtil;
import priv.suki.util.i1.I1Util;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;

/**
 * OMC����ӿ�
 *
 * @author ��С��
 * @version 1.0.3
 */
public class I1AlarmSend implements Send {
    private static final Log log = LogFactory.getLog(I1AlarmSend.class);
    private static final Logger logger = Logger.getLogger("Log4jMain");
    private ServerSocket server = null;
    private Socket socket = null;
    private String charset;
    private BufferedInputStream bis = null;
    private BufferedOutputStream bos = null;
    private Thread heartThread;
    private I1Util i1Util;

    I1AlarmSend() {
    }

    /**
     * OMC�����ͷ���
     *
     * @throws InterruptedException ����쳣
     */
    @Override
    public boolean send(OrgInfo object) throws InterruptedException {
        try {

            if (Propert.getPropert().isFast()) {

                if (ContralCenter.getContral().isNorth_sync_switch()) {
                    ContralCenter.getContral().setNorth_sync_switch(false);
                    //�յ�Ϊ0��ͬ���澯��������
                    if (ContralCenter.getContral().getNorth_sync_number() != 0) {
                        int alarmId = Propert.getPropert().getOmcAlarmId();
                        int interval = Propert.getPropert().getInterval();
                        long startTime;
                        long endTime;
                        BuiltFunc func;
                        OrgInfo buildMsg;
                        OrgInfo orgInfo = new I1AlarmInfo();
                        func = new BuiltFunc();
                        orgInfo.initializeMsg();

                        if (ContralCenter.getContral().getNorth_sync_number() == -1) {
                            logger.info("��ʼ����ȫ������澯");
                        } else {
                            //���ü�ʱ��������������λ��
                            logger.info("��ʼ�����澯�������к�" + ContralCenter.getContral().getNorth_sync_number() + "֮��ʼ����");
                            func.setId(ContralCenter.getContral().getNorth_sync_number() - 1);
                        }
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
                }
                byte[] msg = (byte[]) object.getMsg();
                bos.write(msg);
                bos.flush();
                return true;
            }
            String msg = object.getMsg().toString();
            int alarmId = Propert.getPropert().getOmcAlarmId();


            /* ��ʼͬ�������澯 */
            if (ContralCenter.getContral().isNorth_sync_switch()) {
                ContralCenter.getContral().setNorth_sync_switch(false);

                //�յ�Ϊ0��ͬ���澯��������
                if (ContralCenter.getContral().getNorth_sync_number() != 0) {

                    int tempAlarmId;
                    if (ContralCenter.getContral().getNorth_sync_number() == -1) {
                        logger.info("��ʼ����ȫ������澯");
                        tempAlarmId = alarmId;
                    } else {
                        logger.info("��ʼ�����澯�������к�" + ContralCenter.getContral().getNorth_sync_number() + "֮��ʼ����");
                        tempAlarmId = alarmId - ContralCenter.getContral().getNorth_sync_number();
                    }

                    // ����List
                    List<String> cachelist = ContralCenter.getContral().getCachelist();
                    for (int i = tempAlarmId; i > 0; i--) {
                        String cacheMsg = cachelist.remove(i);
                        log.info("ͬ��������Ϣ:" + cacheMsg);
                        i1Util.send(bos, i1Util.buildAlarmReqMsg(cacheMsg));
                        Thread.sleep(1000);
                    }
                    ContralCenter.getContral().setCachelist(cachelist);


                    i1Util.send(bos, i1Util.buildSyncEndMsg());
                    logger.info("������������ʼ��������");
                }
            }

            i1Util.send(bos, i1Util.buildAlarmReqMsg(msg));

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
        int port = Propert.getPropert().getI1_alarm_port();
        charset = Propert.getPropert().getCharset();
        i1Util = new I1Util();

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

            logger.info("����I1�ӿڷ����ip:" + IpUtil.getLocalIpByNetcard() + "�˿�:" + port + "�Ѿ�����");
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

            return loginSucc(i1Util);

        }

        return true;
    }

    /**
     * ��½��֤
     *
     * @param i1Util
     * @return
     */
    private boolean loginSucc(I1Util i1Util) {
        I1CommonACKMsgVO loginAct;
        boolean loginSucc = false;
        try {
            for (int i = 0; i < 2; i++) {
                loginAct = i1Util.receive(bis, charset);

                if (loginAct.getMsgType() == I1CommonACKMsgVO.I1LOGINALARM) {
                    logger.info("�յ���½����" + loginAct.getMsgBody());

                    if (i1Util.parseRecCom(loginAct, bos)) {
                        logger.info("��½�ɹ�");
                        loginSucc = true;
                        break;
                    }
                }
                logger.error("��½��Ϣ��½ʧ�ܣ��������ԣ����ǵ�" + (i + 1) + "������");
            }
            if (loginSucc) {
                I1HeartUtil heartUtil = new I1HeartUtil(i1Util, bis, bos, charset);
                heartThread = new Thread(heartUtil);
                heartThread.start();
                log.info("����������Ϣ�߳��߳�");

                return true;
            }
            logger.error("���γ���ʧ�ܣ��ϵ�����");
            return false;
        } catch (Exception e) {
            logger.error("��½�����쳣����������");
            log.error("�쳣��Ϣ", e);
        }
        return false;
    }

    /**
     * �ر�����
     */
    @Override
    public void close() {


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

        /* �رս����߳� */
        if (heartThread != null && heartThread.isAlive()) {
            heartThread.interrupt();
        }

    }

}

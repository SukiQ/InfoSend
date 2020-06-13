package priv.suki.send;

import priv.suki.msg.OrgInfo;
import priv.suki.util.IpUtil;
import priv.suki.util.Propert;
import priv.suki.util.cuccalarm.CUCCACKMsgVO;
import priv.suki.util.cuccalarm.CUCCHeartUtil;
import priv.suki.util.cuccalarm.CUCCUtil;
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

/**
 * ��ͨ���Ÿ澯�ӿ�
 *
 * @author ��С��
 * @version 1.0.4
 */
public class CUCCAlarmSend implements Send {
    private static final Log log = LogFactory.getLog(CUCCAlarmSend.class);
    private static final Logger logger = Logger.getLogger("Log4jMain");
    private ServerSocket server = null;
    private Socket socket = null;
    private String charset;
    private BufferedInputStream bis = null;
    private BufferedOutputStream bos = null;
    private Thread heartThread;
    private CUCCHeartUtil cuccHeartUtil;
    private CUCCUtil cuccUtil;

    public CUCCAlarmSend() {
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
                byte[] msg = (byte[]) object.getMsg();
                bos.write(msg);
                bos.flush();
                return true;
            }

            String msg = object.getMsg().toString();
            cuccUtil.send(bos, cuccUtil.buildAlarmReqMsg(msg));

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
        int port = Propert.getPropert().getCucc_alarm_port();
        charset = Propert.getPropert().getCharset();
        cuccUtil = new CUCCUtil();

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

            logger.info("CUCC�澯�ӿڷ����ip:" + IpUtil.getLocalIpByNetcard() + "�˿�:" + port + "�Ѿ�����");
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
            return loginSucc(cuccUtil);
        }

        return true;
    }

    /**
     * ��½��֤
     *
     * @param cuccUtil ���Ӷ���
     * @return �Ƿ��½�ɹ�
     */
    private boolean loginSucc(CUCCUtil cuccUtil) {
        CUCCACKMsgVO loginAct;
        boolean loginSucc = false;
        try {
            for (int i = 0; i < 2; i++) {
                loginAct = cuccUtil.receive(bis, charset);

                if (loginAct.getMsgType() == CUCCACKMsgVO.REQLOGINALARM) {
                    logger.info("�յ���½����" + loginAct.getMsgBody());

                    if (cuccUtil.parseRecCom(loginAct, bos)) {
                        logger.info("��½�ɹ�");
                        loginSucc = true;
                        break;
                    }
                }
                logger.error("��½��Ϣ��½ʧ�ܣ��������ԣ����ǵ�" + (i + 1) + "������");
            }
            if (loginSucc) {
                cuccHeartUtil = new CUCCHeartUtil(cuccUtil, bis, bos, charset);
                heartThread = new Thread(cuccHeartUtil);
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

    /**
     * �ر�����
     */
    @Override
    public void close() {

        /* �رս����߳� */
        if (heartThread != null) {
            cuccHeartUtil.setIsstop(false);
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

package priv.suki.util.northalarm;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;

import priv.suki.controller.ContralCenter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * OMC������շ�����Ϣ�߳�
 *
 * @author ��С��
 * @version 1.0
 */
public class HeartUtil implements Runnable {
    private Boolean isstop = true;
    private NorthUtil northUtil;
    private BufferedInputStream bis;
    private BufferedOutputStream bos;
    private String charset;
    private static final Log log = LogFactory.getLog(HeartUtil.class);

    public HeartUtil(NorthUtil northUtil, BufferedInputStream bis, BufferedOutputStream bos, String charset) {
        this.northUtil = northUtil;
        this.bis = bis;
        this.bos = bos;
        this.charset = charset;
    }

    @Override
    public void run() {
        long heartTimeStamp = System.currentTimeMillis();
        while (isstop) {

            try {
                if (System.currentTimeMillis() - heartTimeStamp > 3 * 60 * 1000) {
                    log.info("����3����û���յ�������Ϣ�������Ͽ�����");
                    ContralCenter.getContral().setNoSend(true);
                    return;
                }
                CommonACKMsgVO receiveMsg = northUtil.receive(bis, charset);
                if (receiveMsg.getMsgType() == CommonACKMsgVO.REQHEARTBEAT) {
                    log.info("�յ���������Ϣ��" + receiveMsg.getMsgBody());
                    if (northUtil.parseHeartCom(receiveMsg, bos)) {
                        heartTimeStamp = System.currentTimeMillis();
                    }
                }
                if (receiveMsg.getMsgType() == CommonACKMsgVO.REQSYNCALARMMSG) {
                    log.info("�յ��˸澯ͬ������" + receiveMsg.getMsgBody());
                    northUtil.parseSyncCom(receiveMsg, bos);
                }
                if (receiveMsg.getMsgType() == CommonACKMsgVO.CLOSECONNALARM) {
                    log.info("�յ��˹ر���������" + receiveMsg.getMsgBody());
                    //�ر�����
                    ContralCenter.getContral().closeCon();
                }
            } catch (InterruptedException e) {
                log.error("���OMC����澯�����߳�");
                return;
            } catch (Exception e) {
//				log.error("�����߳̽����쳣", e);
            }

        }

    }

    public Boolean getIsstop() {
        return isstop;
    }

    public void setIsstop(Boolean isstop) {
        this.isstop = isstop;
    }


}
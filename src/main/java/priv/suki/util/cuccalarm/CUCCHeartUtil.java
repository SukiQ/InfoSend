package priv.suki.util.cuccalarm;

import priv.suki.controller.ContralCenter;
import priv.suki.util.cuccalarm.CUCCUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;

/**
 * CUCC�澯�ӿڷ�����Ϣ�߳�
 *
 * @author ��С��
 * @version 1.0
 */
public class CUCCHeartUtil implements Runnable {
    private Boolean isstop = true;
    private CUCCUtil cuccUtil;
    private BufferedInputStream bis;
    private BufferedOutputStream bos;
    private String charset;
    private static Log log = LogFactory.getLog(CUCCHeartUtil.class);

    public CUCCHeartUtil(CUCCUtil cuccUtil, BufferedInputStream bis, BufferedOutputStream bos, String charset) {
        this.cuccUtil = cuccUtil;
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
                CUCCACKMsgVO receiveMsg = cuccUtil.receive(bis, charset);
                if (receiveMsg.getMsgType() == CUCCACKMsgVO.REQHEARTBEAT) {
                    log.info("�յ���������Ϣ��" + receiveMsg.getMsgBody());
                    if (cuccUtil.parseHeartCom(receiveMsg, bos)) {
                        heartTimeStamp = System.currentTimeMillis();
                    }
                }
                if (receiveMsg.getMsgType() == CUCCACKMsgVO.CLOSECONNALARM) {
                    log.info("�յ��˹ر���������" + receiveMsg.getMsgBody());
                    //�ر�����
                    ContralCenter.getContral().closeCon();
                }
            } catch (InterruptedException e) {
                log.error("���CUCC�澯�����߳�");
                return;
            } catch (Exception e) {
//				log.error("�����߳̽����쳣", e);
            }

        }

    }

    public void setIsstop(Boolean isstop) {
        this.isstop = isstop;
    }


}
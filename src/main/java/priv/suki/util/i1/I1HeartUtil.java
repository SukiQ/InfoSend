package priv.suki.util.i1;

import priv.suki.controller.ContralCenter;
import priv.suki.util.Propert;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;

/**
 * OMC������շ�����Ϣ�߳�
 *
 * @author ��С��
 * @version 1.0
 */
public class I1HeartUtil implements Runnable {
    private Boolean isstop = true;
    private I1Util i1Util;
    private BufferedInputStream bis;
    private BufferedOutputStream bos;
    private final String charset;
    private static final Log log = LogFactory.getLog(I1HeartUtil.class);
    private static final Logger logger = Logger.getLogger("Log4jMain");

    public I1HeartUtil(I1Util northUtil, BufferedInputStream bis, BufferedOutputStream bos, String charset) {
        this.i1Util = northUtil;
        this.bis = bis;
        this.bos = bos;
        this.charset = charset;
    }

    @Override
    public void run() {
        long heartTimeStamp = System.currentTimeMillis();
        while (isstop) {

            try {

                if (System.currentTimeMillis() - heartTimeStamp > Propert.getPropert().getI1_heart_interval() * 2) {
                    log.info("����" + Propert.getPropert().getI1_heart_interval() * 2 + "����(2������)û���յ�������Ϣ�������Ͽ�����");
                    ContralCenter.getContral().setNoSend(true);
                    return;
                }
                I1CommonACKMsgVO receiveMsg = i1Util.receive(bis, charset);
                if (receiveMsg.getMsgType() == I1CommonACKMsgVO.I1HEARTBEAT) {
                    log.info("�յ���������Ϣ,������������");
                    heartTimeStamp = System.currentTimeMillis();
                    i1Util.parseHeartCom(bos);
                }
                if (receiveMsg.getMsgType() == I1CommonACKMsgVO.I1SYNCALARMMSG) {
                    log.info("�յ��˸澯ͬ������" + receiveMsg.getMsgBody());
                    i1Util.parseSyncCom(receiveMsg, bos);
                }


            } catch (InterruptedException e) {
                log.error("��ϵ���I1�澯�����߳�");
                return;
            } catch (Exception e) {
//				log.error("�����߳̽����쳣", e);
            }

        }

    }


}
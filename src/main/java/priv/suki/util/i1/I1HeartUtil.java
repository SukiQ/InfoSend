package priv.suki.util.i1;

import priv.suki.controller.ContralCenter;
import priv.suki.util.Propert;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;

/**
 * OMC北向接收反馈消息线程
 *
 * @author 花小琪
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
                    log.info("超过" + Propert.getPropert().getI1_heart_interval() * 2 + "分钟(2个周期)没有收到心跳消息，主动断开连接");
                    ContralCenter.getContral().setNoSend(true);
                    return;
                }
                I1CommonACKMsgVO receiveMsg = i1Util.receive(bis, charset);
                if (receiveMsg.getMsgType() == I1CommonACKMsgVO.I1HEARTBEAT) {
                    log.info("收到了心跳消息,发送心跳反馈");
                    heartTimeStamp = System.currentTimeMillis();
                    i1Util.parseHeartCom(bos);
                }
                if (receiveMsg.getMsgType() == I1CommonACKMsgVO.I1SYNCALARMMSG) {
                    log.info("收到了告警同步请求：" + receiveMsg.getMsgBody());
                    i1Util.parseSyncCom(receiveMsg, bos);
                }


            } catch (InterruptedException e) {
                log.error("打断电信I1告警心跳线程");
                return;
            } catch (Exception e) {
//				log.error("接收线程接收异常", e);
            }

        }

    }


}
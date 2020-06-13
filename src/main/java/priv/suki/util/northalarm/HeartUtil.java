package priv.suki.util.northalarm;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;

import priv.suki.controller.ContralCenter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * OMC北向接收反馈消息线程
 *
 * @author 花小琪
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
                    log.info("超过3分钟没有收到心跳消息，主动断开连接");
                    ContralCenter.getContral().setNoSend(true);
                    return;
                }
                CommonACKMsgVO receiveMsg = northUtil.receive(bis, charset);
                if (receiveMsg.getMsgType() == CommonACKMsgVO.REQHEARTBEAT) {
                    log.info("收到了心跳消息：" + receiveMsg.getMsgBody());
                    if (northUtil.parseHeartCom(receiveMsg, bos)) {
                        heartTimeStamp = System.currentTimeMillis();
                    }
                }
                if (receiveMsg.getMsgType() == CommonACKMsgVO.REQSYNCALARMMSG) {
                    log.info("收到了告警同步请求：" + receiveMsg.getMsgBody());
                    northUtil.parseSyncCom(receiveMsg, bos);
                }
                if (receiveMsg.getMsgType() == CommonACKMsgVO.CLOSECONNALARM) {
                    log.info("收到了关闭连接请求：" + receiveMsg.getMsgBody());
                    //关闭连接
                    ContralCenter.getContral().closeCon();
                }
            } catch (InterruptedException e) {
                log.error("打断OMC北向告警心跳线程");
                return;
            } catch (Exception e) {
//				log.error("接收线程接收异常", e);
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
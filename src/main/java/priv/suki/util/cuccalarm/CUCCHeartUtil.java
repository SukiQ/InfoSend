package priv.suki.util.cuccalarm;

import priv.suki.controller.ContralCenter;
import priv.suki.util.cuccalarm.CUCCUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;

/**
 * CUCC告警接口反馈消息线程
 *
 * @author 花小琪
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
                    log.info("超过3分钟没有收到心跳消息，主动断开连接");
                    ContralCenter.getContral().setNoSend(true);
                    return;
                }
                CUCCACKMsgVO receiveMsg = cuccUtil.receive(bis, charset);
                if (receiveMsg.getMsgType() == CUCCACKMsgVO.REQHEARTBEAT) {
                    log.info("收到了心跳消息：" + receiveMsg.getMsgBody());
                    if (cuccUtil.parseHeartCom(receiveMsg, bos)) {
                        heartTimeStamp = System.currentTimeMillis();
                    }
                }
                if (receiveMsg.getMsgType() == CUCCACKMsgVO.CLOSECONNALARM) {
                    log.info("收到了关闭连接请求：" + receiveMsg.getMsgBody());
                    //关闭连接
                    ContralCenter.getContral().closeCon();
                }
            } catch (InterruptedException e) {
                log.error("打断CUCC告警心跳线程");
                return;
            } catch (Exception e) {
//				log.error("接收线程接收异常", e);
            }

        }

    }

    public void setIsstop(Boolean isstop) {
        this.isstop = isstop;
    }


}
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
 * 联通集团告警接口
 *
 * @author 花小琪
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
     * OMC北向发送方法
     *
     * @throws InterruptedException 打断异常
     */
    @Override
    public boolean send(OrgInfo object) throws InterruptedException {
        try {

            /*
             * 极速模式发送
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

            log.error("发送失败", e);
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
                log.error("绑定端口出现异常", e);
                return false;

            }

            logger.info("CUCC告警接口服务端ip:" + IpUtil.getLocalIpByNetcard() + "端口:" + port + "已经开启");
            try {
                socket = server.accept();
                socket.setTcpNoDelay(true);
                socket.setKeepAlive(true);
            } catch (SocketException so) {
                log.info("初始化中停止");
                return false;
            } catch (IOException e) {
                log.error("客户端连接出现异常", e);
                return false;
            }

            String ip = socket.getInetAddress().getHostAddress();
            logger.info("客户端[" + ip + "]连接服务端成功");

            log.info("设置当前字符编码为：" + charset);

            try {
                bis = new BufferedInputStream(socket.getInputStream());
                bos = new BufferedOutputStream(socket.getOutputStream());

            } catch (IOException e) {
                log.error("初始化消息流出错", e);
                return false;
            }
            return loginSucc(cuccUtil);
        }

        return true;
    }

    /**
     * 登陆验证
     *
     * @param cuccUtil 连接对象
     * @return 是否登陆成功
     */
    private boolean loginSucc(CUCCUtil cuccUtil) {
        CUCCACKMsgVO loginAct;
        boolean loginSucc = false;
        try {
            for (int i = 0; i < 2; i++) {
                loginAct = cuccUtil.receive(bis, charset);

                if (loginAct.getMsgType() == CUCCACKMsgVO.REQLOGINALARM) {
                    logger.info("收到登陆请求：" + loginAct.getMsgBody());

                    if (cuccUtil.parseRecCom(loginAct, bos)) {
                        logger.info("登陆成功");
                        loginSucc = true;
                        break;
                    }
                }
                logger.error("登陆消息登陆失败，进行重试，这是第" + (i + 1) + "次重试");
            }
            if (loginSucc) {
                cuccHeartUtil = new CUCCHeartUtil(cuccUtil, bis, bos, charset);
                heartThread = new Thread(cuccHeartUtil);
                heartThread.start();
                log.info("启动反馈消息线程线程");

                return true;
            }
            logger.error("三次尝试失败，断掉连接");
            return false;
        } catch (Exception e) {
            logger.error("登陆出现异常，放弃连接");
        }
        return false;
    }

    /**
     * 关闭连接
     */
    @Override
    public void close() {

        /* 关闭接收线程 */
        if (heartThread != null) {
            cuccHeartUtil.setIsstop(false);
        }

        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                log.error("关闭流出现异常", e);
            } finally {
                socket = null;
            }
        }

        if (server != null) {
            try {
                server.close();
            } catch (IOException e) {
                log.error("关闭流出现异常", e);
            } finally {
                server = null;
            }
        }

        if (bis != null) {
            try {
                bis.close();
            } catch (IOException e) {
                log.error("关闭流出现异常", e);
            } finally {
                bis = null;
            }
        }
        if (bos != null) {
            try {
                bos.close();
            } catch (IOException e) {
                log.error("关闭流出现异常", e);
            } finally {
                bos = null;
            }

        }

    }

}

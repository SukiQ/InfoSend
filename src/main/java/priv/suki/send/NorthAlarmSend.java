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
 * OMC北向接口
 *
 * @author 花小琪
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

                    //重置计时器，调整到补发位置
                    func.setId(ContralCenter.getContral().getNorth_sync_number() - 1);

                    logger.info("开始补发告警，从序列号" + ContralCenter.getContral().getNorth_sync_number() + "之后开始补发");
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
                        /*发送延时*/
                        if (endTime - startTime < interval) {
                            Thread.sleep(interval - (endTime - startTime));
                            continue;

                        }
                        logger.info("补发" + buildMsg + "条");
                        log.info("发送超时" + (endTime - startTime - interval) + "毫秒");
                    }
                    logger.info("补发结束，开始正常接收");
                }
                byte[] msg = (byte[]) object.getMsg();
                bos.write(msg);
                bos.flush();
                return true;
            }

            String msg = object.getMsg().toString();
            int alarmId = Propert.getPropert().getOmcAlarmId();
            // 缓存List
            List<String> cachelist = ContralCenter.getContral().getCachelist();

            /* 开始同步补发告警 */
            if (ContralCenter.getContral().isNorth_sync_switch()) {
                ContralCenter.getContral().setNorth_sync_switch(false);
                logger.info("开始补发告警，从序列号" + ContralCenter.getContral().getNorth_sync_number() + "之后开始补发");
                int tempAlarmId = alarmId - ContralCenter.getContral().getNorth_sync_number();
                for (int i = tempAlarmId; i > 0; i--) {
                    String cacheMsg = cachelist.remove(i);
                    log.info("同步补发消息:" + cacheMsg);
                    northUtil.send(bos, northUtil.buildAlarmReqMsg(cacheMsg));
                    Thread.sleep(1000);
                }
                logger.info("补发结束，开始正常接收");
                ContralCenter.getContral().setCachelist(cachelist);
            }

            northUtil.send(bos, northUtil.buildAlarmReqMsg(msg));

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
                log.error("绑定端口出现异常", e);
                return false;

            }

            logger.info("OMC北向接口服务端ip:" + IpUtil.getLocalIpByNetcard() + "端口:" + port + "已经开启");
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
            return loginSucc(northUtil);
        }

        return true;
    }

    /**
     * 登陆验证
     *
     * @param northUtil 连接对象
     * @return 是否登陆成功
     */
    private boolean loginSucc(NorthUtil northUtil) {
        CommonACKMsgVO loginAct;
        boolean loginSucc = false;
        try {
            for (int i = 0; i < 2; i++) {
                loginAct = northUtil.receive(bis, charset);

                if (loginAct.getMsgType() == CommonACKMsgVO.REQLOGINALARM) {
                    logger.info("收到登陆请求：" + loginAct.getMsgBody());

                    if (northUtil.parseRecCom(loginAct, bos)) {
                        logger.info("登陆成功");
                        loginSucc = true;
                        break;
                    }
                }
                logger.error("登陆消息登陆失败，进行重试，这是第" + (i + 1) + "次重试");
            }
            if (loginSucc) {
                heartUtil = new HeartUtil(northUtil, bis, bos, charset);
                heartThread = new Thread(heartUtil);
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


    @Override
    public void close() {

        /* 关闭接收线程 */
        if (heartThread != null) {
            heartUtil.setIsstop(false);
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

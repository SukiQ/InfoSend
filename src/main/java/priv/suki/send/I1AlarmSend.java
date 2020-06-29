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
 * OMC北向接口
 *
 * @author 花小琪
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
     * OMC北向发送方法
     *
     * @throws InterruptedException 打断异常
     */
    @Override
    public boolean send(OrgInfo object) throws InterruptedException {
        try {

            if (Propert.getPropert().isFast()) {

                if (ContralCenter.getContral().isNorth_sync_switch()) {
                    ContralCenter.getContral().setNorth_sync_switch(false);
                    //收到为0的同步告警不做处理
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
                            logger.info("开始补发全部缓存告警");
                        } else {
                            //重置计时器，调整到补发位置
                            logger.info("开始补发告警，从序列号" + ContralCenter.getContral().getNorth_sync_number() + "之后开始补发");
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
                }
                byte[] msg = (byte[]) object.getMsg();
                bos.write(msg);
                bos.flush();
                return true;
            }
            String msg = object.getMsg().toString();
            int alarmId = Propert.getPropert().getOmcAlarmId();


            /* 开始同步补发告警 */
            if (ContralCenter.getContral().isNorth_sync_switch()) {
                ContralCenter.getContral().setNorth_sync_switch(false);

                //收到为0的同步告警不做处理
                if (ContralCenter.getContral().getNorth_sync_number() != 0) {

                    int tempAlarmId;
                    if (ContralCenter.getContral().getNorth_sync_number() == -1) {
                        logger.info("开始补发全部缓存告警");
                        tempAlarmId = alarmId;
                    } else {
                        logger.info("开始补发告警，从序列号" + ContralCenter.getContral().getNorth_sync_number() + "之后开始补发");
                        tempAlarmId = alarmId - ContralCenter.getContral().getNorth_sync_number();
                    }

                    // 缓存List
                    List<String> cachelist = ContralCenter.getContral().getCachelist();
                    for (int i = tempAlarmId; i > 0; i--) {
                        String cacheMsg = cachelist.remove(i);
                        log.info("同步补发消息:" + cacheMsg);
                        i1Util.send(bos, i1Util.buildAlarmReqMsg(cacheMsg));
                        Thread.sleep(1000);
                    }
                    ContralCenter.getContral().setCachelist(cachelist);


                    i1Util.send(bos, i1Util.buildSyncEndMsg());
                    logger.info("补发结束，开始正常接收");
                }
            }

            i1Util.send(bos, i1Util.buildAlarmReqMsg(msg));

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
                log.error("绑定端口出现异常", e);
                return false;

            }

            logger.info("电信I1接口服务端ip:" + IpUtil.getLocalIpByNetcard() + "端口:" + port + "已经开启");
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

            return loginSucc(i1Util);

        }

        return true;
    }

    /**
     * 登陆验证
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
                    logger.info("收到登陆请求：" + loginAct.getMsgBody());

                    if (i1Util.parseRecCom(loginAct, bos)) {
                        logger.info("登陆成功");
                        loginSucc = true;
                        break;
                    }
                }
                logger.error("登陆消息登陆失败，进行重试，这是第" + (i + 1) + "次重试");
            }
            if (loginSucc) {
                I1HeartUtil heartUtil = new I1HeartUtil(i1Util, bis, bos, charset);
                heartThread = new Thread(heartUtil);
                heartThread.start();
                log.info("启动反馈消息线程线程");

                return true;
            }
            logger.error("三次尝试失败，断掉连接");
            return false;
        } catch (Exception e) {
            logger.error("登陆出现异常，放弃连接");
            log.error("异常信息", e);
        }
        return false;
    }

    /**
     * 关闭连接
     */
    @Override
    public void close() {


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

        /* 关闭接收线程 */
        if (heartThread != null && heartThread.isAlive()) {
            heartThread.interrupt();
        }

    }

}

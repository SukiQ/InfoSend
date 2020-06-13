package priv.suki.send;

import java.util.List;

import priv.suki.process.Judge;
import priv.suki.util.Propert;
import lombok.SneakyThrows;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import priv.suki.controller.ProductionThread;
import priv.suki.msg.OrgInfo;
import priv.suki.controller.ContralCenter;

/**
 * 发送线程，每次发送都会创建新的线程
 *
 * @author 花小琪
 * @version 1.0.1
 */
public class SendFactory extends Thread {
    private static final Log log = LogFactory.getLog(SendFactory.class);
    private static final Logger logger = Logger.getLogger("Log4jMain");
    private boolean initSucc = false;
    private boolean end = false;
    private Long startTime;
    private Long endTime;
    private int interval;
    private Send send;
    private OrgInfo msg = null;

    @Override
    public void run() {

        // 初始化//
        init();

        //初始化失败，直接退出//
        if (!initSucc) {
            return;
        }

        //发送//
        send();
    }


    /**
     * 发送方法
     */
    private void send() {

        Long statisticsStartTime = System.currentTimeMillis();
        int sendNumber = 0;
        ContralCenter.getContral().setNobuildsendNum(0);

        while (!end && Judge.isEnd()) {
            try {

                if (ContralCenter.getContral().isParse()) {
                    Thread.sleep(1);
                    continue;
                }
                try {
                    interval = Propert.getPropert().getInterval();
                    startTime = System.currentTimeMillis();
                    msg = ProductionThread.getProduction().receive();

                    /* 正常更新消息缓存 */
                    if (Judge.isCacheInterface()) {
                        cacheMsg();
                    }
                    /*
                     * 发送消息
                     */
                    if (!ContralCenter.getContral().isCutSwitch()) {
                        log.info("发送消息：" + msg);
                        if (!send.send(msg)) {
                            logger.error("本次发送失败");
                        }

                        /*
                         * 不拼接发送
                         * */
                        if (!Propert.getPropert().isBulidsend()) {
                            //自增发送次数
                            ContralCenter.getContral().addNobuildSendNum();
                            //无间隔发送

                            sendNumber = ContralCenter.getContral().getSendNum() * ContralCenter.getContral().getNobuildrate() + ContralCenter.getContral().getNobuildsendNum();
                            log.info("已发送消息：" + sendNumber + "条");
                            logger.info("已发送消息：" + sendNumber + "条");

                            if (Judge.needNoInvSend()) {
                                continue;
                            }

                            ContralCenter.getContral().setNobuildsendNum(0);
                            ContralCenter.getContral().updateSendNum();// 更新发送次数

                        } else {

                            ContralCenter.getContral().updateSendNum();// 更新发送次数
                            sendNumber = ContralCenter.getContral().getSendNum() * Propert.getPropert().getMsgNum();
                            log.info("已发送消息：" + sendNumber + "条");
                            logger.info("已发送消息：" + sendNumber + "条");
                        }


                    }
                    /*
                     * 风暴发送逻辑
                     */
                    if (Judge.isStorm()) {
                        ContralCenter.getContral().stormSend();
                    }
                    endTime = System.currentTimeMillis();
                    if (ContralCenter.getContral().isParse()) {
                        continue;
                    }
                    if (ContralCenter.getContral().isNoSend()) {
                        break;
                    }
                } catch (InterruptedException e) {
                    throw new InterruptedException();
                } catch (Exception e) {
                    log.error("消息发送失败", e);
                }


                /*发送延时*/
                if (endTime - startTime < interval) {
                    Thread.sleep(interval - (endTime - startTime));
                    continue;

                }
                log.info("发送超时" + (endTime - startTime - interval) + "毫秒");


            } catch (InterruptedException e) {
                log.info("打断发送线程");
                break;
            }
        }
        Long statisticsEndTime = System.currentTimeMillis();

        /*
         * 汇总日志模块
         */
        logger.info("-----统计结果-----");
        logger.info("发送数量：" + sendNumber);
        logger.info("发送总耗时：" + (statisticsEndTime - statisticsStartTime) + "毫秒");
        Thread.currentThread().interrupt();
    }

    /**
     * 特殊接口会缓存消息
     */
    private void cacheMsg() {
        /*极速模式会拼接消息，也不缓存消息*/
        if (Propert.getPropert().isFast()) {
            Propert.getPropert().setOmcAlarmId(Propert.getPropert().getOmcAlarmId() + (Propert.getPropert().getMsgNum()));
        } else {
            List<String> cachelist = ContralCenter.getContral().getCachelist();

            if (cachelist.size() > 1000) {
                cachelist.remove(1000);
            }

            cachelist.add(0, msg.getMsg().toString());
            Propert.getPropert().setOmcAlarmId(Propert.getPropert().getOmcAlarmId() + 1);
            ContralCenter.getContral().setCachelist(cachelist);
        }
    }

    /**
     * 初始化接口
     */
    @SneakyThrows
    private void init() {
        send = (Send) ContralCenter.getContral().getSelInterface().getSendType().newInstance();

        if (send.init()) {
            initSucc = true;
            logger.info("初始化接口连接成功");

        } else {
            initSucc = false;
            logger.info("初始化失败");
            send.close();
        }

    }

    /**
     * 关闭接口
     */
    public void close() {
        this.stopThread();
        send.close();
        log.info("接口关闭完成");

    }

    /**
     * 关闭本线程
     */
    private void stopThread() {
        end = true;
        this.interrupt();
    }


}

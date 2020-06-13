package priv.suki.controller;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import priv.suki.controller.ContralCenter;
import priv.suki.msg.*;
import priv.suki.process.Judge;
import priv.suki.util.InterfaceEnum;
import priv.suki.util.Propert;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import priv.suki.util.BuiltFunc;
import priv.suki.msg.OrgInfo;
import priv.suki.msg.StringInfo;

/**
 * 缓存构造线程
 *
 * @author 花小琪
 * @version 1.0.0
 */
public class ProductionThread extends Thread {

    /**
     * log
     */
    private static final Log log = LogFactory.getLog(ProductionThread.class);

    /**
     * 消息通道
     */
    private static final BlockingQueue<OrgInfo> QUEUE = new LinkedBlockingQueue<>(30);
    private static final ProductionThread PRODUCTION = new ProductionThread();
    private OrgInfo orgInfo;
    private BuiltFunc func;
    private Boolean isFrist = true;

    /**
     * ProductionThread构造
     */
    private ProductionThread() {

    }

    /**
     * ProductionThread单例模式
     *
     * @return production对象
     */
    public static ProductionThread getProduction() {
        return PRODUCTION;
    }

    @Override
    public void run() {

        while (true) {
            if (ContralCenter.getContral().isNoSend() && !isFrist) {
                try {
                    Thread.sleep(1);
                    continue;
                } catch (InterruptedException e) {
                    log.error("缓存计算线程被打断", e);
                }
            }
            isFrist = false;

            try {
                initMsg();
            } catch (Exception e) {
                log.error("构造线程初始化失败，关闭", e);
                return;
            }

            while (!ContralCenter.getContral().isNoSend() && !ContralCenter.getContral().isStormSend()) {

                try {
                    QUEUE.put(orgInfo.appendMsg(Propert.getPropert().getMsgNum(), func));
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    log.info("构造线程关闭");

                } catch (Exception e) {
                    log.error("构造线程发送出错", e);

                }
            }
            clean();

            /*
             * 清空队列，可以风暴发送了
             */
            if (ContralCenter.getContral().isStormSend()) {
                ContralCenter.getContral().setStormSend(false);
                ContralCenter.getContral().setStormReady(true);
                log.info("开始风暴发送，清空缓存队列");
            }

        }
    }

    /**
     * 初始化消息
     */
    private void initMsg() throws Exception {
        func = new BuiltFunc();
        InterfaceEnum interfaceType = ContralCenter.getContral().getSelInterface();

        //特殊接口需要变换消息类型
        if (Judge.isSpecialInterface(interfaceType)) {
            orgInfo = StringInfo.class.newInstance();
        } else {
            orgInfo = (OrgInfo) interfaceType.getInfoType().newInstance();
        }

        //初始化消息体
        orgInfo.initializeMsg();
    }

    /**
     * 从队列中取出并移除头元素，若队列为空，发生阻塞，等待有元素
     *
     * @return 取出一条消息
     */
    public OrgInfo receive() {
        try {
            // take方法， 从队列中取出并移除头元素，若队列为空，发生阻塞，等待有元素。
            return QUEUE.take();

        } catch (InterruptedException e) {
            log.error("从消息队列取出的DataObject对象异常", e);
        }
        return null;
    }

    /**
     * 清除通道数据
     */
    private void clean() {
        QUEUE.clear();// 清空队列
    }

}

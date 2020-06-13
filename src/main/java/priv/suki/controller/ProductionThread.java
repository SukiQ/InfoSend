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
 * ���湹���߳�
 *
 * @author ��С��
 * @version 1.0.0
 */
public class ProductionThread extends Thread {

    /**
     * log
     */
    private static final Log log = LogFactory.getLog(ProductionThread.class);

    /**
     * ��Ϣͨ��
     */
    private static final BlockingQueue<OrgInfo> QUEUE = new LinkedBlockingQueue<>(30);
    private static final ProductionThread PRODUCTION = new ProductionThread();
    private OrgInfo orgInfo;
    private BuiltFunc func;
    private Boolean isFrist = true;

    /**
     * ProductionThread����
     */
    private ProductionThread() {

    }

    /**
     * ProductionThread����ģʽ
     *
     * @return production����
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
                    log.error("��������̱߳����", e);
                }
            }
            isFrist = false;

            try {
                initMsg();
            } catch (Exception e) {
                log.error("�����̳߳�ʼ��ʧ�ܣ��ر�", e);
                return;
            }

            while (!ContralCenter.getContral().isNoSend() && !ContralCenter.getContral().isStormSend()) {

                try {
                    QUEUE.put(orgInfo.appendMsg(Propert.getPropert().getMsgNum(), func));
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    log.info("�����̹߳ر�");

                } catch (Exception e) {
                    log.error("�����̷߳��ͳ���", e);

                }
            }
            clean();

            /*
             * ��ն��У����Է籩������
             */
            if (ContralCenter.getContral().isStormSend()) {
                ContralCenter.getContral().setStormSend(false);
                ContralCenter.getContral().setStormReady(true);
                log.info("��ʼ�籩���ͣ���ջ������");
            }

        }
    }

    /**
     * ��ʼ����Ϣ
     */
    private void initMsg() throws Exception {
        func = new BuiltFunc();
        InterfaceEnum interfaceType = ContralCenter.getContral().getSelInterface();

        //����ӿ���Ҫ�任��Ϣ����
        if (Judge.isSpecialInterface(interfaceType)) {
            orgInfo = StringInfo.class.newInstance();
        } else {
            orgInfo = (OrgInfo) interfaceType.getInfoType().newInstance();
        }

        //��ʼ����Ϣ��
        orgInfo.initializeMsg();
    }

    /**
     * �Ӷ�����ȡ�����Ƴ�ͷԪ�أ�������Ϊ�գ������������ȴ���Ԫ��
     *
     * @return ȡ��һ����Ϣ
     */
    public OrgInfo receive() {
        try {
            // take������ �Ӷ�����ȡ�����Ƴ�ͷԪ�أ�������Ϊ�գ������������ȴ���Ԫ�ء�
            return QUEUE.take();

        } catch (InterruptedException e) {
            log.error("����Ϣ����ȡ����DataObject�����쳣", e);
        }
        return null;
    }

    /**
     * ���ͨ������
     */
    private void clean() {
        QUEUE.clear();// ��ն���
    }

}

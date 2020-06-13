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
 * �����̣߳�ÿ�η��Ͷ��ᴴ���µ��߳�
 *
 * @author ��С��
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

        // ��ʼ��//
        init();

        //��ʼ��ʧ�ܣ�ֱ���˳�//
        if (!initSucc) {
            return;
        }

        //����//
        send();
    }


    /**
     * ���ͷ���
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

                    /* ����������Ϣ���� */
                    if (Judge.isCacheInterface()) {
                        cacheMsg();
                    }
                    /*
                     * ������Ϣ
                     */
                    if (!ContralCenter.getContral().isCutSwitch()) {
                        log.info("������Ϣ��" + msg);
                        if (!send.send(msg)) {
                            logger.error("���η���ʧ��");
                        }

                        /*
                         * ��ƴ�ӷ���
                         * */
                        if (!Propert.getPropert().isBulidsend()) {
                            //�������ʹ���
                            ContralCenter.getContral().addNobuildSendNum();
                            //�޼������

                            sendNumber = ContralCenter.getContral().getSendNum() * ContralCenter.getContral().getNobuildrate() + ContralCenter.getContral().getNobuildsendNum();
                            log.info("�ѷ�����Ϣ��" + sendNumber + "��");
                            logger.info("�ѷ�����Ϣ��" + sendNumber + "��");

                            if (Judge.needNoInvSend()) {
                                continue;
                            }

                            ContralCenter.getContral().setNobuildsendNum(0);
                            ContralCenter.getContral().updateSendNum();// ���·��ʹ���

                        } else {

                            ContralCenter.getContral().updateSendNum();// ���·��ʹ���
                            sendNumber = ContralCenter.getContral().getSendNum() * Propert.getPropert().getMsgNum();
                            log.info("�ѷ�����Ϣ��" + sendNumber + "��");
                            logger.info("�ѷ�����Ϣ��" + sendNumber + "��");
                        }


                    }
                    /*
                     * �籩�����߼�
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
                    log.error("��Ϣ����ʧ��", e);
                }


                /*������ʱ*/
                if (endTime - startTime < interval) {
                    Thread.sleep(interval - (endTime - startTime));
                    continue;

                }
                log.info("���ͳ�ʱ" + (endTime - startTime - interval) + "����");


            } catch (InterruptedException e) {
                log.info("��Ϸ����߳�");
                break;
            }
        }
        Long statisticsEndTime = System.currentTimeMillis();

        /*
         * ������־ģ��
         */
        logger.info("-----ͳ�ƽ��-----");
        logger.info("����������" + sendNumber);
        logger.info("�����ܺ�ʱ��" + (statisticsEndTime - statisticsStartTime) + "����");
        Thread.currentThread().interrupt();
    }

    /**
     * ����ӿڻỺ����Ϣ
     */
    private void cacheMsg() {
        /*����ģʽ��ƴ����Ϣ��Ҳ��������Ϣ*/
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
     * ��ʼ���ӿ�
     */
    @SneakyThrows
    private void init() {
        send = (Send) ContralCenter.getContral().getSelInterface().getSendType().newInstance();

        if (send.init()) {
            initSucc = true;
            logger.info("��ʼ���ӿ����ӳɹ�");

        } else {
            initSucc = false;
            logger.info("��ʼ��ʧ��");
            send.close();
        }

    }

    /**
     * �رսӿ�
     */
    public void close() {
        this.stopThread();
        send.close();
        log.info("�ӿڹر����");

    }

    /**
     * �رձ��߳�
     */
    private void stopThread() {
        end = true;
        this.interrupt();
    }


}

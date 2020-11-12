package priv.suki.send;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import priv.suki.controller.ContralCenter;
import priv.suki.msg.OrgInfo;
import priv.suki.util.Propert;
import priv.suki.util.kafka.MessageProducer;

/**
 * Kafka�ӿ�ʵ����
 *
 * @author ��С��
 * @version 1.0.1
 * @see 1.0 ��ʱ���Ӳ���kafkaʱ���п���ʱû������host����û�йرշ���ǽ��windows��hosts�ļ���c:/windows/system32/drivers/etcĿ¼�£��رշ���ǽͨ������service
 * iptables stop ����systemctl stop firewalld
 */
public class KafkaSend implements Send {
    private static final Log log = LogFactory.getLog(KafkaSend.class);
    private static final Logger logger = Logger.getLogger("Log4jMain");
    private String charset;
    private MessageProducer producer;

    /**
     * KafkaSend���캯��
     */
    public KafkaSend() {
    }

    @Override
    public boolean send(OrgInfo object) {
        String msg = object.getMsg().toString();
        try {
            /* ������� */
            int partition = 0;
            /* ָ���������� */
            if (ContralCenter.getContral().getSendType() == Propert.PARTITION_SEND) {
                if (Propert.getPropert().getSend_partition() == -1) {
                    partition = getPartition();
                    producer.send(null, msg.getBytes(charset), Propert.getPropert().getTopic(), partition, false);
                } else {
                    partition = Propert.getPropert().getSend_partition();
                    producer.send(null, msg.getBytes(charset), Propert.getPropert().getTopic(), partition, false);
                }

            } else {

                //����������ɢ�з���
                if (Propert.getPropert().getParitition() > 0) {
                    partition = getPartition();
                    producer.send(null, msg.getBytes(charset), Propert.getPropert().getTopic(), partition, false);
                } else {
                    producer.send(msg.getBytes(charset), Propert.getPropert().getTopic());
                }

            }
            logger.info("������Ϣ�Ѿ�������" + partition + "����");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            log.error("kafka���η���ʧ��", e);
            return false;
        }
        return true;
    }

    @Override
    public boolean init() {
        try {
            charset = Propert.getPropert().getCharset();

            /*
             * ��ʼ��kafka������
             */
            producer = new MessageProducer();
            logger.info("��ʼ��kafka���ӳɹ�");
            return true;
        } catch (Exception e) {
            log.error("��ʼ��kafkaʧ��", e);
            return false;
        }
    }

    /**
     * �ر�kafka����
     */
    @Override
    public void close() {
        if (null != producer) {
            producer.close();
            producer = null;
        }

    }

    /**
     * ɢ�м��������
     *
     * @return ������
     */
    private static int getPartition() {
        /*
         * ��ƴ�ӷ���
         */
        if (!Propert.getPropert().isBulidsend()) {
            int sendNumber = ContralCenter.getContral().getSendNum() * ContralCenter.getContral().getNobuildrate() + ContralCenter.getContral().getNobuildsendNum();
            return sendNumber % Propert.getPropert().getParitition();
        }
        return ContralCenter.getContral().getSendNum() % Propert.getPropert().getParitition();
    }

}

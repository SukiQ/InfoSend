package priv.suki.send;

import priv.suki.util.Propert;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;

import priv.suki.controller.ContralCenter;
import priv.suki.msg.OrgInfo;
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
    public boolean send(OrgInfo object) throws InterruptedException {
        String msg = object.getMsg().toString();
        try {
            /* ������� */
            int partition = 0;
            /* ָ���������� */
            if (ContralCenter.getContral().getSendType() == Propert.PARTITION_SEND) {
                if (Propert.getPropert().getSend_partition() == -1) {
                    partition = ContralCenter.getContral().getSendNum() % Propert.getPropert().getParitition();
                    producer.send(null, msg.getBytes(charset), Propert.getPropert().getTopic(), partition, false);
                } else {
                    partition = Propert.getPropert().getSend_partition();
                    producer.send(null, msg.getBytes(charset), Propert.getPropert().getTopic(), partition, false);
                }

            } else {
                producer.send(msg.getBytes(charset), Propert.getPropert().getTopic());
            }
            logger.info("������Ϣ�Ѿ�������" + partition + "����");
        } catch (InterruptedException Interrupted) {

            throw Interrupted;

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

}

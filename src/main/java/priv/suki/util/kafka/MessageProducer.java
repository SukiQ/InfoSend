package priv.suki.util.kafka;

import java.util.Properties;

import priv.suki.util.Propert;
import priv.suki.util.StringUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;


/**
 * kafka�����߹�����
 *
 * @author ��С��
 */
public class MessageProducer {
    public static Log log = LogFactory.getLog(MessageProducer.class);
    KafkaProducer<Object, Object> producer;

    /**
     * ����,��ʼ��kafka����
     */
    public MessageProducer() {

        Properties props = new Properties();
        /* ��ʽΪhost[:port]����localhost:9092,��kafka���ӵ�broker��ַ�б�,�����Ƕ�̨,�ö��ŷָ� */
        props.put("bootstrap.servers", Propert.getPropert().getKafkaUrl());

        if (!StringUtil.isBlank(Propert.getPropert().getZookUrl())) {
            props.put("zookeeper.connect", Propert.getPropert().getZookUrl());
        }

        /*
         * ����kafka�յ���Ϣ�Ĵ���,0���ǲ�Ҫ��,���յ�û�յ�.1������һ��leader broker�𸴾���,all������broker��Ҫ�յ�����
         * 0:Producer���ȴ�kafka�������Ĵ�,��Ϣ���̷���socket
         * 1: �ȴ�leader��¼���ݵ�broker����log����
         * all:�ȴ�����broker��¼��Ϣ.��֤��Ϣ���ᶪʧ(ֻҪ�ӽڵ�ûȫ��),���ַ�ʽ����߿��õ� acksĬ��ֵ��1
         */
        props.put("acks", "1");
        /*���Է��ʹ���,��ʱ��������ֶ��ݵ������ʱ��,���Զ��ط���Ϣ,ǰ���ᵽ�����ֵ����Ҫ��acks=1��allʱ�����Ч*/
        props.put("retries", 1);
        /*��������Ϣ�ֽ���,����broker����Ϣ��������batches,ÿ��������Ӧһ��batch,batchС�˻��С��������,batchΪ0�Ļ��ͽ�����batch����*/
        props.put("batch.size", 16384);
        /*����ʱ��,�������ָ������Ϣ����������,���Ƕ������ʱ���һ�鷢��*/
        props.put("linger.ms", 1000);
        /*buffer_memory��������ȴ����͵���Ϣ*/
        props.put("buffer.memory", 33554432);
        /* key���л�����. Ĭ��ֵ: None. */
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        /* ֵ���л�����Ĭ��ֵ: None. */
        props.put("value.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer");
        /*zookeeper��Ⱥ��ַ�����������zookeeper���ͨ��zookeeperȥ��kafka*/
        if (props.containsKey("zookeeper.connect")) {
            String zkServers = props.getProperty("zookeeper.connect");
            String kafkaBrokers = KafkaHelper.getBrokers(zkServers);
            props.put("bootstrap.servers", kafkaBrokers);
            log.info("ͨ��zookeeper" + zkServers + "������kafka���У�" + kafkaBrokers);
        }
        producer = new KafkaProducer<>(props);
    }

    /**
     * kafka���ͷ���
     *
     * @param key         �������
     * @param value       ��������Ϣ
     * @param topic       topic
     * @param partitionId ������
     * @param isTimeStamp �Ƿ��¼ʱ��
     */
    public void send(Object key, Object value, String topic, Integer partitionId, boolean isTimeStamp) {
        ProducerRecord<Object, Object> record;
        if (null == key) {
            /* ����round-robinģʽ���͵�ÿ��Partition */
            record = new ProducerRecord<>(topic, value);
        } else if (null == partitionId) {
            /* �ᰴ��hasy(key)��������ӦPartition */
            record = new ProducerRecord<>(topic, key, value);
        } else if (isTimeStamp) {
            /* ������ָ��Partition,����¼ʱ��� */
            record = new ProducerRecord<>(topic, partitionId, System.currentTimeMillis(), key, value);
        } else {
            /* ������ָ��Partition */
            record = new ProducerRecord<>(topic, partitionId, key, value);
        }

        this.producer.send(record, new KafkaSendCallback(record, null, log));

    }

    /**
     * kafka���ͷ���
     *
     * @param recordKey �������
     * @param msg       ��������Ϣ
     * @param topic     topic
     */
    public void send(Object recordKey, byte[] msg, String topic) {
        this.send(recordKey, msg, topic, null, false);
    }

    /**
     * kafka���ɢ�з��ͷ���
     *
     * @param msg   ��������Ϣ
     * @param topic topic
     */
    public void send(byte[] msg, String topic) {
        this.send(null, msg, topic, null, false);
    }

    /**
     * �����߹رշ���
     */
    public void close() {
        this.producer.close();
    }

}

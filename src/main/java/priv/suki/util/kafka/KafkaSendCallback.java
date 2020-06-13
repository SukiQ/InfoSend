package priv.suki.util.kafka;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.commons.logging.Log;

/**
 * Kafka�ص�����
 *
 * @author ��С��
 */
public class KafkaSendCallback implements Callback {

    private Log log = null;
    private ProducerRecord<Object, Object> record = null;
    @SuppressWarnings("unused")
    private MessageProducer producer_apache = null;

    public KafkaSendCallback(ProducerRecord<Object, Object> record_, MessageProducer producer_apache_, Log log) {
        this.record = record_;
        this.producer_apache = producer_apache_;
        this.log = log;
    }

    public void onCompletion(RecordMetadata recordMetadata, Exception e) {


        if (null == e) {
            log.info("******������Ϣ�ɹ�, record[" + record.toString() + "]");
            return;
        }
        //send failed
        log.error("������Ϣʧ��,record[" + record.toString() + "]", e);
    }

}

package priv.suki.util.kafka;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.commons.logging.Log;

/**
 * Kafka回调函数
 *
 * @author 花小琪
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
            log.info("******发送消息成功, record[" + record.toString() + "]");
            return;
        }
        //send failed
        log.error("发送消息失败,record[" + record.toString() + "]", e);
    }

}

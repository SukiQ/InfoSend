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

    private final Log log;
    private final ProducerRecord<Object, Object> record;

    public KafkaSendCallback(ProducerRecord<Object, Object> record_, MessageProducer producerApache, Log log) {
        this.record = record_;
        this.log = log;
    }

    @Override
    public void onCompletion(RecordMetadata recordMetadata, Exception e) {


        if (null == e) {
            log.info("******发送消息成功, record[" + record.toString() + "]");
            return;
        }
        //send failed
        log.error("发送消息失败,record[" + record.toString() + "]", e);
    }

}

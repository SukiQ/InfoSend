package priv.suki.util.kafka;

import java.util.Properties;

import priv.suki.util.Propert;
import priv.suki.util.StringUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;


/**
 * kafka生产者工具类
 *
 * @author 花小琪
 */
public class MessageProducer {
    public static Log log = LogFactory.getLog(MessageProducer.class);
    KafkaProducer<Object, Object> producer;

    /**
     * 构造,初始化kafka参数
     */
    public MessageProducer() {

        Properties props = new Properties();
        /* 格式为host[:port]例如localhost:9092,是kafka连接的broker地址列表,可以是多台,用逗号分隔 */
        props.put("bootstrap.servers", Propert.getPropert().getKafkaUrl());

        if (!StringUtil.isBlank(Propert.getPropert().getZookUrl())) {
            props.put("zookeeper.connect", Propert.getPropert().getZookUrl());
        }

        /*
         * 代表kafka收到消息的答复数,0就是不要答复,爱收到没收到.1就是有一个leader broker答复就行,all是所有broker都要收到才行
         * 0:Producer不等待kafka服务器的答复,消息立刻发往socket
         * 1: 等待leader记录数据到broker本地log即可
         * all:等待所有broker记录消息.保证消息不会丢失(只要从节点没全挂),这种方式是最高可用的 acks默认值是1
         */
        props.put("acks", "1");
        /*重试发送次数,有时候网络出现短暂的问题的时候,会自动重发消息,前面提到了这个值是需要在acks=1或all时候才有效*/
        props.put("retries", 1);
        /*批处理消息字节数,发往broker的消息会包含多个batches,每个分区对应一个batch,batch小了会减小响吞吐量,batch为0的话就禁用了batch发送*/
        props.put("batch.size", 16384);
        /*逗留时间,这个逗留指的是消息不立即发送,而是逗留这个时间后一块发送*/
        props.put("linger.ms", 1000);
        /*buffer_memory用来保存等待发送的消息*/
        props.put("buffer.memory", 33554432);
        /* key序列化函数. 默认值: None. */
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        /* 值序列化函数默认值: None. */
        props.put("value.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer");
        /*zookeeper集群地址，如果配置了zookeeper则会通过zookeeper去找kafka*/
        if (props.containsKey("zookeeper.connect")) {
            String zkServers = props.getProperty("zookeeper.connect");
            String kafkaBrokers = KafkaHelper.getBrokers(zkServers);
            props.put("bootstrap.servers", kafkaBrokers);
            log.info("通过zookeeper" + zkServers + "搜索到kafka队列：" + kafkaBrokers);
        }
        producer = new KafkaProducer<>(props);
    }

    /**
     * kafka发送方法
     *
     * @param key         发送序号
     * @param value       待发送消息
     * @param topic       topic
     * @param partitionId 分区号
     * @param isTimeStamp 是否记录时间
     */
    public void send(Object key, Object value, String topic, Integer partitionId, boolean isTimeStamp) {
        ProducerRecord<Object, Object> record;
        if (null == key) {
            /* 按照round-robin模式发送到每个Partition */
            record = new ProducerRecord<>(topic, value);
        } else if (null == partitionId) {
            /* 会按照hasy(key)发送至对应Partition */
            record = new ProducerRecord<>(topic, key, value);
        } else if (isTimeStamp) {
            /* 发送至指定Partition,并记录时间戳 */
            record = new ProducerRecord<>(topic, partitionId, System.currentTimeMillis(), key, value);
        } else {
            /* 发送至指定Partition */
            record = new ProducerRecord<>(topic, partitionId, key, value);
        }

        this.producer.send(record, new KafkaSendCallback(record, null, log));

    }

    /**
     * kafka发送方法
     *
     * @param recordKey 发送序号
     * @param msg       待发送消息
     * @param topic     topic
     */
    public void send(Object recordKey, byte[] msg, String topic) {
        this.send(recordKey, msg, topic, null, false);
    }

    /**
     * kafka随机散列发送方法
     *
     * @param msg   待发送消息
     * @param topic topic
     */
    public void send(byte[] msg, String topic) {
        this.send(null, msg, topic, null, false);
    }

    /**
     * 生产者关闭方法
     */
    public void close() {
        this.producer.close();
    }

}

package priv.suki.send;

import priv.suki.util.Propert;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;

import priv.suki.controller.ContralCenter;
import priv.suki.msg.OrgInfo;
import priv.suki.util.kafka.MessageProducer;

/**
 * Kafka接口实现类
 *
 * @author 花小琪
 * @version 1.0.1
 * @see 1.0 有时连接不上kafka时，有可能时没有配置host或者没有关闭防火墙，windows的hosts文件在c:/windows/system32/drivers/etc目录下，关闭防火墙通过命令service
 * iptables stop 或是systemctl stop firewalld
 */
public class KafkaSend implements Send {
    private static final Log log = LogFactory.getLog(KafkaSend.class);
    private static final Logger logger = Logger.getLogger("Log4jMain");
    private String charset;
    private MessageProducer producer;

    /**
     * KafkaSend构造函数
     */
    public KafkaSend() {
    }

    @Override
    public boolean send(OrgInfo object) throws InterruptedException {
        String msg = object.getMsg().toString();
        try {
            /* 算分区数 */
            int partition = 0;
            /* 指定分区发送 */
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
            logger.info("本次消息已经发送至" + partition + "分区");
        } catch (InterruptedException Interrupted) {

            throw Interrupted;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            log.error("kafka本次发送失败", e);
            return false;
        }
        return true;
    }

    @Override
    public boolean init() {
        try {
            charset = Propert.getPropert().getCharset();

            /*
             * 初始化kafka生产者
             */
            producer = new MessageProducer();
            logger.info("初始化kafka连接成功");
            return true;
        } catch (Exception e) {
            log.error("初始化kafka失败", e);
            return false;
        }
    }

    /**
     * 关闭kafka连接
     */
    @Override
    public void close() {
        if (null != producer) {
            producer.close();
            producer = null;
        }

    }

}

package priv.suki.send;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import priv.suki.util.Propert;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import priv.suki.msg.OrgInfo;

/**
 * ActiveMq接口实现类
 *
 * @author 花小琪
 * @version 1.0.2
 */
public class ActiveMQSend implements Send {
    private static Log log = LogFactory.getLog(ActiveMQSend.class);
    private static Logger logger = Logger.getLogger("Log4jMain");
    private String charset;
    private Destination destination;
    private MessageProducer producer;
    private Session queueSession;
    private Connection queueConnection;

    /**
     * ActiveMQSend构造函数
     */
    public ActiveMQSend() {
    }

    @Override
    public boolean send(OrgInfo object) {

        try {
            String msg = object.toString();
            if (producer == null) {
                producer = queueSession.createProducer(destination);
                producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
            }

            TextMessage textMsg = queueSession.createTextMessage();
            textMsg.setText(new String(msg.getBytes(), charset));
            producer.send(textMsg);

            return true;
        } catch (Exception e) {
            log.error("ActiveMq发送失败", e);
            return false;
        }
    }

    @Override
    public boolean init() {
        try {
            charset = Propert.getPropert().getCharset();
            String username = Propert.getPropert().getActmquser();
            // ActiveMq 的默认登录密码
            String password = Propert.getPropert().getActmqpwd();
            // ActiveMQ 的链接地址
            String brokenUrl = Propert.getPropert().getActmqurl();
            String queueName = Propert.getPropert().getActmqname();

            // 创建一个链接工厂
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(username, password, brokenUrl);
            // 从工厂中创建一个链接
            queueConnection = connectionFactory.createConnection();
            // 开启链接
            queueConnection.start();
            // 创建一个事务（这里通过参数可以设置事务的级别）
            queueSession = queueConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            destination = queueSession.createQueue(queueName);
            logger.info("队列" + Propert.getPropert().getActmqname() + "创建完毕");

            return true;

        } catch (Exception e) {
            log.error("初始化ActiveMQ失败", e);
            return false;
        }
    }

    /**
     * 关闭ActiveMq连接
     */
    @Override
    public void close() {
        try {
            if (queueConnection != null) {
                queueConnection.close();

            }
            producer = null;
        } catch (Exception e) {
            log.error("关闭ActiveMq失败", e);
            queueConnection = null;
        }
    }

}

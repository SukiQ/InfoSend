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
 * ActiveMq�ӿ�ʵ����
 *
 * @author ��С��
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
     * ActiveMQSend���캯��
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
            log.error("ActiveMq����ʧ��", e);
            return false;
        }
    }

    @Override
    public boolean init() {
        try {
            charset = Propert.getPropert().getCharset();
            String username = Propert.getPropert().getActmquser();
            // ActiveMq ��Ĭ�ϵ�¼����
            String password = Propert.getPropert().getActmqpwd();
            // ActiveMQ �����ӵ�ַ
            String brokenUrl = Propert.getPropert().getActmqurl();
            String queueName = Propert.getPropert().getActmqname();

            // ����һ�����ӹ���
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(username, password, brokenUrl);
            // �ӹ����д���һ������
            queueConnection = connectionFactory.createConnection();
            // ��������
            queueConnection.start();
            // ����һ����������ͨ������������������ļ���
            queueSession = queueConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            destination = queueSession.createQueue(queueName);
            logger.info("����" + Propert.getPropert().getActmqname() + "�������");

            return true;

        } catch (Exception e) {
            log.error("��ʼ��ActiveMQʧ��", e);
            return false;
        }
    }

    /**
     * �ر�ActiveMq����
     */
    @Override
    public void close() {
        try {
            if (queueConnection != null) {
                queueConnection.close();

            }
            producer = null;
        } catch (Exception e) {
            log.error("�ر�ActiveMqʧ��", e);
            queueConnection = null;
        }
    }

}

package priv.suki.send;

import priv.suki.util.Propert;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import priv.suki.msg.OrgInfo;
import com.ibm.mq.MQC;
import com.ibm.mq.MQEnvironment;
import com.ibm.mq.MQMessage;
import com.ibm.mq.MQPutMessageOptions;
import com.ibm.mq.MQQueue;
import com.ibm.mq.MQQueueManager;

/**
 * IBMMQ�ӿ�ʵ����
 *
 * @author ��С��
 * @version 1.0.2
 */
public class IBMMQSend implements Send {
	private static Log log = LogFactory.getLog(IBMMQSend.class);
	private static Logger logger = Logger.getLogger("Log4jMain");
	private String charset;
	private MQQueue qQueue;
	private MQQueueManager queueMgr;

	/**
	 * IBMMQSend���캯��
	 */
	public IBMMQSend() {
	}

	@Override
	public boolean send(OrgInfo object) throws InterruptedException {

		try {
			String msg = object.toString();
			MQMessage qMsg = new MQMessage();
			qMsg.write(msg.getBytes(charset));
			MQPutMessageOptions pmo = new MQPutMessageOptions();
			qQueue.put(qMsg, pmo);
			return true;
		} catch (Exception e) {
			log.error("IBMMQ����ʧ��", e);
			return false;
		}
	}

	@Override
	/*
	  ��ʼ��IBMMQ
	 */
	public boolean init() {
		try {
			charset = Propert.getPropert().getCharset();

			MQEnvironment.hostname = Propert.getPropert().getIbmmqIp();
			MQEnvironment.channel = Propert.getPropert().getIbmmqChannel();
			MQEnvironment.CCSID = Propert.getPropert().getIbmmq_CCSID();
			MQEnvironment.port = Propert.getPropert().getIbmPort();
//			MQEnvironment.properties.put(MQC.TRANSPORT_PROPERTY,MQC.TRANSPORT_MQSERIES);
			MQEnvironment.disableTracing();

			queueMgr = new MQQueueManager(Propert.getPropert().getIbmmqQueueManager());
			int qOptioin = MQC.MQOO_INPUT_AS_Q_DEF | MQC.MQOO_INQUIRE | MQC.MQOO_OUTPUT;
			qQueue = queueMgr.accessQueue(Propert.getPropert().getIbmmqQueueName(), qOptioin);
			logger.info("����" + Propert.getPropert().getIbmmqQueueName() + "�������");

			return true;

		} catch (Exception e) {
			log.error("��ʼ��MQʧ��", e);
			return false;
		}
	}

	/**
	 * �ر�IBMMQ����
	 */
	@Override
	public void close() {
		try {
			if (qQueue != null) {
				qQueue.close();
			}
		} catch (Exception e) {
			log.error("�ر�IBMMQʧ��", e);
			qQueue = null;
		}
		try {
			if (queueMgr != null) {
				queueMgr.disconnect();
			}
		} catch (Exception e) {
			log.error("�ر�IBMMQʧ��", e);
			queueMgr = null;
		}
	}

}

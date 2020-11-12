package priv.suki.send;

import com.ibm.mq.*;
import com.ibm.mq.constants.MQConstants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import priv.suki.msg.OrgInfo;
import priv.suki.util.Propert;

/**
 * IBMMQ接口实现类
 *
 * @author 花小琪
 * @version 1.0.2
 */
public class IBMMQSend implements Send {
	private static final Log log = LogFactory.getLog(IBMMQSend.class);
	private static final Logger logger = Logger.getLogger("Log4jMain");
	private String charset;
	private MQQueue qQueue;
	private MQQueueManager queueMgr;

	/**
	 * IBMMQSend构造函数
	 */
	public IBMMQSend() {
	}

	@Override
	public boolean send(OrgInfo object) {

		try {
			String msg = object.toString();
			MQMessage qMsg = new MQMessage();
			qMsg.write(msg.getBytes(charset));
			MQPutMessageOptions pmo = new MQPutMessageOptions();
			qQueue.put(qMsg, pmo);
			return true;
		} catch (Exception e) {
			log.error("IBMMQ发送失败", e);
			return false;
		}
	}

	@Override
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
			int qOptioin = MQConstants.MQOO_INPUT_AS_Q_DEF | MQConstants.MQOO_INQUIRE | MQConstants.MQOO_OUTPUT;
			qQueue = queueMgr.accessQueue(Propert.getPropert().getIbmmqQueueName(), qOptioin);
			logger.info("队列" + Propert.getPropert().getIbmmqQueueName() + "创建完毕");

			return true;

		} catch (Exception e) {
			log.error("初始化MQ失败", e);
			return false;
		}
	}

	/**
	 * 关闭IBMMQ连接
	 */
	@Override
	public void close() {
		try {
			if (qQueue != null) {
				qQueue.close();
			}
		} catch (Exception e) {
			log.error("关闭IBMMQ失败", e);
			qQueue = null;
		}
		try {
			if (queueMgr != null) {
				queueMgr.disconnect();
			}
		} catch (Exception e) {
			log.error("关闭IBMMQ失败", e);
			queueMgr = null;
		}
	}

}

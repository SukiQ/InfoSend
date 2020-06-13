package priv.suki.send;

import priv.suki.util.Propert;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import priv.suki.msg.OrgInfo;
import com.ibm.mq.MQMessage;
import com.ibm.mq.MQPutMessageOptions;
import com.ibm.mq.MQQueue;
import com.ibm.mq.MQQueueManager;

/**
 * DB�ӿ�ʵ����
 *
 * @author ��С��
 * @version 1.0.3
 */
public class DBSend implements Send {
    private static Log log = LogFactory.getLog(DBSend.class);
    private String charset;
    private MQQueue qQueue;
    private MQQueueManager queueMgr;

    /**
     * DBSend���캯��
     */
    public DBSend() {
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
            log.error("DB����ʧ��", e);
            return false;
        }
    }

    @Override
    /**
     * ��ʼ��DB
     */
    public boolean init() {
        try {
            charset = Propert.getPropert().getCharset();


            return true;

        } catch (Exception e) {
            log.error("��ʼ��MQʧ��", e);
            return false;
        }
    }

    /**
     * �ر�DB����
     */
    @Override
    public void close() {
        try {
            if (qQueue != null) {
                qQueue.close();
            }
        } catch (Exception e) {
            log.error("�ر�DBʧ��", e);
            qQueue = null;
        }
        try {
            if (queueMgr != null) {
                queueMgr.disconnect();
            }
        } catch (Exception e) {
            log.error("�ر�DBʧ��", e);
            queueMgr = null;
        }
    }

}

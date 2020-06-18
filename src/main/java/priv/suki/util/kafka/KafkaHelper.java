package priv.suki.util.kafka;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author ��С��
 * @version 1.0.2
 * Kafka���߷���
 */
public class KafkaHelper {
    private static CountDownLatch connectedSignal;
    public static Log log = LogFactory.getLog(KafkaHelper.class);

    /**
     * ͨ��zookeeper��ȡkafka��ַ
     *
     * @param zkServers zk��ַ
     * @return kafka��ַ
     */
    public static String getBrokers(String zkServers) {
        connectedSignal = new CountDownLatch(1);
        StringBuilder buffer = new StringBuilder();
        ZooKeeper zkClient = null;

        try {
            zkClient = new ZooKeeper(zkServers, 120000, event -> {
                if (event.getState() == Watcher.Event.KeeperState.SyncConnected) {
                    connectedSignal.countDown();
                }
            });
            connectedSignal.await();

            String offsetPath = "/brokers/ids";
            List<String> brokerIds = zkClient.getChildren(offsetPath, false);

            for (int i = 0; i < brokerIds.size(); ++i) {
                String brokerId = brokerIds.get(i);
                String brokerInfo = new String(zkClient.getData(offsetPath + "/" + brokerId, false, null));
                JSONObject jsonObject = JSON.parseObject(brokerInfo);
                String host = jsonObject.getString("host");
                String port = jsonObject.getString("port");
                if (i == brokerIds.size() - 1) {
                    buffer.append(host).append(":").append(port);
                } else {
                    buffer.append(host).append(":").append(port).append(",");
                }
            }
        } catch (Exception var14) {
            log.error("get broker error:", var14);
        } finally {
            if (zkClient != null) {
                try {
                    zkClient.close();
                } catch (InterruptedException e) {
                    log.info("�ر�����ʧ��", e);
                }
            }

        }

        return buffer.toString();
    }
}

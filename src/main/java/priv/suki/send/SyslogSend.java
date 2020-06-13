package priv.suki.send;

import priv.suki.msg.OrgInfo;
import priv.suki.util.Propert;
import lombok.NoArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * @author 花小琪
 * @version 1.0.4
 */
@NoArgsConstructor
public class SyslogSend implements Send {

    private static final Log log = LogFactory.getLog(SOCKET_SERVER.class);
    private int targetPort;
    private String targetHost;
    private String charset;
    private DatagramSocket datagramSocket;

    @Override
    public boolean send(OrgInfo object) {
        try {
            String msg = object.getMsg().toString();
            byte[] buf = msg.getBytes(charset);
            DatagramPacket dp = new DatagramPacket(buf, buf.length, InetAddress.getByName(targetHost), targetPort);
            datagramSocket.send(dp);
            return true;
        } catch (Exception e) {
            log.error("发送失败", e);
            return false;
        }
    }

    @Override
    public boolean init() {
        int port = Propert.getPropert().getSyslog_port();
        targetPort = Propert.getPropert().getSyslog_target_port();
        targetHost = Propert.getPropert().getSyslog_target_host();
        charset = Propert.getPropert().getCharset();
        try {
            if ("server".equalsIgnoreCase(Propert.getPropert().getSyslog_type())) {
                datagramSocket = new DatagramSocket(port);
                return true;
            }
            if ("client".equalsIgnoreCase(Propert.getPropert().getSyslog_type())) {
                datagramSocket = new DatagramSocket();
                return true;
            }
            log.error("Syslog类型配置错误，退出");
            return false;
        } catch (Exception e) {
            log.error("Syslog接口初始化失败", e);
            return false;
        }
    }

    @Override
    public void close() {
        if (datagramSocket != null) {
            try {
                datagramSocket.close();
            } catch (Exception e) {
                log.error("Syslog关闭流失败");
            }

        }
    }
}

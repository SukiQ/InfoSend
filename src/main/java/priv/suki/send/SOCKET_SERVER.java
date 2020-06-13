package priv.suki.send;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import priv.suki.util.IpUtil;
import priv.suki.util.Propert;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import priv.suki.msg.OrgInfo;

/**
 * Socket����˽ӿ�
 *
 * @author ��С��
 * @version 1.0.1
 */
public class SOCKET_SERVER implements Send {
    private static Log log = LogFactory.getLog(SOCKET_SERVER.class);
    private static Logger logger = Logger.getLogger("Log4jMain");
    private ServerSocket server = null;
    private Socket socket = null;
    private String charset;
    private InputStreamReader inputStreamReader;
    private OutputStream outputStream = null;

    public SOCKET_SERVER() {
    }

    @Override
    public boolean send(OrgInfo object) {

        try {
            String msg = object.getMsg().toString();
            outputStream.write(msg.getBytes(charset));
            outputStream.flush();
        } catch (IOException e) {
            log.error("����ʧ��", e);
            return false;
        }
        return true;
    }

    @Override
    public boolean init() {
        int port = Propert.getPropert().getSocket_server_port();
        charset = Propert.getPropert().getCharset();

        if (server == null) {
            try {
                server = new ServerSocket();
                if (!server.getReuseAddress()) {
                    server.setReuseAddress(true);
                }
                server.bind(new InetSocketAddress(port));
            } catch (IOException e) {
                log.error("�󶨶˿ڳ����쳣", e);
                return false;

            }

            logger.info("SOCKET�����ip:" + IpUtil.getLocalIpByNetcard() + "�˿�:" + port + "�Ѿ�����");
//			log.info("SOCKET�����ip:" + IpUtil.getLocalIpByNetcard() + "�˿�:" + port + "�Ѿ�����");
            try {
                socket = server.accept();
                socket.setTcpNoDelay(true);
                socket.setKeepAlive(true);
            } catch (SocketException so) {
                logger.info("��ʼ����ֹͣ");
                return false;
            } catch (IOException e) {
                log.error("�ͻ������ӳ����쳣", e);
                logger.error("�ͻ������ӳ����쳣" + e.getMessage());
                return false;
            }

            String ip = socket.getInetAddress().getHostAddress();
//			log.info("�ͻ���[" + ip + "]���ӷ���˳ɹ�");
            logger.info("�ͻ���[" + ip + "]���ӷ���˳ɹ�");

            log.info("���õ�ǰ�ַ�����Ϊ��" + charset);

            try {
                if (charset != null && charset.trim().length() > 0) {
                    inputStreamReader = new InputStreamReader(socket.getInputStream(), charset);

                } else {
                    inputStreamReader = new InputStreamReader(socket.getInputStream());
                }
                outputStream = socket.getOutputStream();

            } catch (IOException e) {
                log.error("��ʼ����Ϣ������", e);
                logger.error("��ʼ����Ϣ������" + e.getMessage());
                return false;
            }
        }

        return true;
    }

    @Override
    public void close() {

        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                log.error("�ر��������쳣", e);
            } finally {
                socket = null;
            }
        }

        if (server != null) {
            try {
                server.close();
            } catch (IOException e) {
                log.error("�ر��������쳣", e);
            } finally {
                server = null;
            }
        }

        if (inputStreamReader != null) {
            try {
                inputStreamReader.close();
            } catch (IOException e) {
                log.error("�ر��������쳣", e);
            } finally {
                inputStreamReader = null;
            }
        }
        if (outputStream != null) {
            try {
                outputStream.close();
            } catch (IOException e) {
                log.error("�ر��������쳣", e);
            } finally {
                outputStream = null;
            }

        }

    }

}

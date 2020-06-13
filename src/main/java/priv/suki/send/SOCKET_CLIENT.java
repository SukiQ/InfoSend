package priv.suki.send;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import priv.suki.util.Propert;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import priv.suki.msg.OrgInfo;

/**
 * Socket�ͻ��˽ӿ�
 *
 * @author ��С��
 * @version 1.0.1
 */
public class SOCKET_CLIENT implements Send {
    private static Log log = LogFactory.getLog(SOCKET_CLIENT.class);
    private static Logger logger = Logger.getLogger("Log4jMain");
    private ServerSocket server = null;
    private Socket socket = null;
    private String charset;
    private InputStreamReader inputStreamReader;
    private OutputStream outputStream = null;

    /**
     * Socket_client���췽��
     */
    public SOCKET_CLIENT() {
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
        String ip = Propert.getPropert().getSocket_client_ip();
        int port = Propert.getPropert().getSocket_client_port();
        charset = Propert.getPropert().getCharset();

        logger.info("׼�����ӵ�ip:" + ip + "�˿�:" + port);

        try {
            socket = new Socket(ip, port);
        } catch (IOException e) {
            logger.error("����" + ip + "�����쳣" + e.getMessage());
            return false;

        }

        try {
            socket.setTcpNoDelay(true);
            socket.setKeepAlive(true);
        } catch (IOException e) {
            logger.error("�ͻ������ӳ����쳣" + e.getMessage());
            return false;
        }

        logger.info("�ͻ����Ѿ��ɹ����ӵ�ip:" + ip + "�˿�:" + port);
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

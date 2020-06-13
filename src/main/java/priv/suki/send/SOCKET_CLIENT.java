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
 * Socket客户端接口
 *
 * @author 花小琪
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
     * Socket_client构造方法
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
            log.error("发送失败", e);
            return false;
        }
        return true;
    }

    @Override
    public boolean init() {
        String ip = Propert.getPropert().getSocket_client_ip();
        int port = Propert.getPropert().getSocket_client_port();
        charset = Propert.getPropert().getCharset();

        logger.info("准备连接到ip:" + ip + "端口:" + port);

        try {
            socket = new Socket(ip, port);
        } catch (IOException e) {
            logger.error("连接" + ip + "出现异常" + e.getMessage());
            return false;

        }

        try {
            socket.setTcpNoDelay(true);
            socket.setKeepAlive(true);
        } catch (IOException e) {
            logger.error("客户端连接出现异常" + e.getMessage());
            return false;
        }

        logger.info("客户端已经成功连接到ip:" + ip + "端口:" + port);
        log.info("设置当前字符编码为：" + charset);

        try {
            if (charset != null && charset.trim().length() > 0) {
                inputStreamReader = new InputStreamReader(socket.getInputStream(), charset);

            } else {
                inputStreamReader = new InputStreamReader(socket.getInputStream());
            }
            outputStream = socket.getOutputStream();

        } catch (IOException e) {
            log.error("初始化消息流出错", e);
            logger.error("初始化消息流出错" + e.getMessage());
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
                log.error("关闭流出现异常", e);
            } finally {
                socket = null;
            }
        }

        if (server != null) {
            try {
                server.close();
            } catch (IOException e) {
                log.error("关闭流出现异常", e);
            } finally {
                server = null;
            }
        }

        if (inputStreamReader != null) {
            try {
                inputStreamReader.close();
            } catch (IOException e) {
                log.error("关闭流出现异常", e);
            } finally {
                inputStreamReader = null;
            }
        }
        if (outputStream != null) {
            try {
                outputStream.close();
            } catch (IOException e) {
                log.error("关闭流出现异常", e);
            } finally {
                outputStream = null;
            }

        }

    }

}

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
 * Socket服务端接口
 *
 * @author 花小琪
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
            log.error("发送失败", e);
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
                log.error("绑定端口出现异常", e);
                return false;

            }

            logger.info("SOCKET服务端ip:" + IpUtil.getLocalIpByNetcard() + "端口:" + port + "已经开启");
//			log.info("SOCKET服务端ip:" + IpUtil.getLocalIpByNetcard() + "端口:" + port + "已经开启");
            try {
                socket = server.accept();
                socket.setTcpNoDelay(true);
                socket.setKeepAlive(true);
            } catch (SocketException so) {
                logger.info("初始化中停止");
                return false;
            } catch (IOException e) {
                log.error("客户端连接出现异常", e);
                logger.error("客户端连接出现异常" + e.getMessage());
                return false;
            }

            String ip = socket.getInetAddress().getHostAddress();
//			log.info("客户端[" + ip + "]连接服务端成功");
            logger.info("客户端[" + ip + "]连接服务端成功");

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

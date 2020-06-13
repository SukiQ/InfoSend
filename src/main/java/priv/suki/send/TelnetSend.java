package priv.suki.send;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import priv.suki.util.FileUtil;
import priv.suki.util.Propert;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.log4j.Logger;
import priv.suki.msg.OrgInfo;

import java.io.File;
import java.io.FileInputStream;

/**
 * Telnet接口
 *
 * @author 花小琪
 * @version 1.0.2
 */
public class TelnetSend implements Send {
    private static Log log = LogFactory.getLog(TelnetSend.class);
    private static Logger logger = Logger.getLogger("Log4jMain");
    private FTPClient ftpClient;
    private String charset;
    private InputStream uploadInputStream;
    private OutputStream outputStream;

    public TelnetSend() {
    }

    @Override
    public boolean send(OrgInfo object) {
        // TODO Auto-generated method stub

        try {
            String msg = object.getMsg().toString() + Propert.SYS_LINE_SPARATOR;
            outputStream.write(msg.getBytes(charset));
            outputStream.flush();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            log.error("发送失败", e);
            return false;
        }
        return true;
    }

    /**
     * Telnet初始化
     */
    @Override
    public boolean init() {
        try {
            String remote_host = Propert.getPropert().getTelnetFtpIp(); // 服务器IP地址
            String username = Propert.getPropert().getTelnetFtpuser(); // 用于登陆服务器的用户名
            String password = Propert.getPropert().getTelnetFtppassword(); // 登陆密码
            String remoteDirectoryPath = Propert.getPropert().getTelnetFtpSource();
            String filename = "infoSend." + Propert.getPropert().getTelnetFileType();
            String remoteFilePath = ".." + Propert.SYS_FILE_SPARATOR + "data" + Propert.SYS_FILE_SPARATOR + filename;

            charset = Propert.getPropert().getCharset();

            if (!FileUtil.createFile(remoteFilePath)) {
                logger.error("创建文件失败");
                return false;
            }
            ftpClient = new FTPClient();

            ftpClient.connect(remote_host);
            if (ftpClient.login(username, password)) {
                logger.info("FTP登陆远程服务器" + remote_host + "成功");
            } else {
                logger.info("FTP登陆远程服务器" + remote_host + "失败");
                return false;
            }

            ftpClient.changeWorkingDirectory(remoteDirectoryPath);
            uploadInputStream = new FileInputStream(new File(remoteFilePath));
            if (ftpClient.storeFile(filename, uploadInputStream)) {
                logger.info("FTP已经将文件上传到远程服务器");
            } else {
                logger.info("FTP上传文件到远程服务器失败");
                return false;
            }
            outputStream = ftpClient.appendFileStream(filename);

            return true;
        } catch (Exception e) {
            log.error("Telnet初始化失败", e);
            return false;
        } finally {

            try {
                if (uploadInputStream != null) {
                    uploadInputStream.close();
                }
            } catch (IOException e) {
                log.error("关闭FTP输入流失败", e);
            }
        }
    }

    /**
     * Telnet关闭方法
     */
    @Override
    public void close() {

        if (ftpClient != null) {
            try {
                ftpClient.logout();
                ftpClient.disconnect();
            } catch (IOException e) {
                log.error("关闭FTP客户端失败", e);
            }

        }
        if (outputStream != null) {
            try {
                outputStream.close();
            } catch (IOException e) {
                log.error("关闭Telnet输出流失败", e);
            }
        }
    }

}

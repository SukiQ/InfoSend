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
 * Telnet�ӿ�
 *
 * @author ��С��
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
            log.error("����ʧ��", e);
            return false;
        }
        return true;
    }

    /**
     * Telnet��ʼ��
     */
    @Override
    public boolean init() {
        try {
            String remote_host = Propert.getPropert().getTelnetFtpIp(); // ������IP��ַ
            String username = Propert.getPropert().getTelnetFtpuser(); // ���ڵ�½���������û���
            String password = Propert.getPropert().getTelnetFtppassword(); // ��½����
            String remoteDirectoryPath = Propert.getPropert().getTelnetFtpSource();
            String filename = "infoSend." + Propert.getPropert().getTelnetFileType();
            String remoteFilePath = ".." + Propert.SYS_FILE_SPARATOR + "data" + Propert.SYS_FILE_SPARATOR + filename;

            charset = Propert.getPropert().getCharset();

            if (!FileUtil.createFile(remoteFilePath)) {
                logger.error("�����ļ�ʧ��");
                return false;
            }
            ftpClient = new FTPClient();

            ftpClient.connect(remote_host);
            if (ftpClient.login(username, password)) {
                logger.info("FTP��½Զ�̷�����" + remote_host + "�ɹ�");
            } else {
                logger.info("FTP��½Զ�̷�����" + remote_host + "ʧ��");
                return false;
            }

            ftpClient.changeWorkingDirectory(remoteDirectoryPath);
            uploadInputStream = new FileInputStream(new File(remoteFilePath));
            if (ftpClient.storeFile(filename, uploadInputStream)) {
                logger.info("FTP�Ѿ����ļ��ϴ���Զ�̷�����");
            } else {
                logger.info("FTP�ϴ��ļ���Զ�̷�����ʧ��");
                return false;
            }
            outputStream = ftpClient.appendFileStream(filename);

            return true;
        } catch (Exception e) {
            log.error("Telnet��ʼ��ʧ��", e);
            return false;
        } finally {

            try {
                if (uploadInputStream != null) {
                    uploadInputStream.close();
                }
            } catch (IOException e) {
                log.error("�ر�FTP������ʧ��", e);
            }
        }
    }

    /**
     * Telnet�رշ���
     */
    @Override
    public void close() {

        if (ftpClient != null) {
            try {
                ftpClient.logout();
                ftpClient.disconnect();
            } catch (IOException e) {
                log.error("�ر�FTP�ͻ���ʧ��", e);
            }

        }
        if (outputStream != null) {
            try {
                outputStream.close();
            } catch (IOException e) {
                log.error("�ر�Telnet�����ʧ��", e);
            }
        }
    }

}

package priv.suki.util;

import java.io.PipedReader;
import java.io.PipedWriter;
import java.io.Writer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Appender;
import org.apache.log4j.Logger;
import org.apache.log4j.WriterAppender;

/**
 * ��̬��־�࣬������ģʽ��ʵʱ��ȡlogger������������
 *
 * @author ��С��
 * @version 1.0
 */
public class DynamicLogUtil {
    /**
     * log
     */
    private static final Log log = LogFactory.getLog(DynamicLogUtil.class);

    private static final DynamicLogUtil dynamicLog = new DynamicLogUtil();
    private AppenderThread appenderThread;

    /**
     * ��̬��־�๹�캯������ȡlog4j��WriterAppender����
     */
    public DynamicLogUtil() {

        Logger root = Logger.getLogger("Log4jMain");
        try {
            // ��ȡ�Ӽ�¼�������Դ
            Appender appender = root.getAppender("WriterAppender");
            // ����һ��δ���ӵ��������ܵ�
            PipedReader reader = new PipedReader();
            // ����һ�������ӵ���������������ӵ�reader
            Writer writer = new PipedWriter(reader);
            // ���� appender �����
            ((WriterAppender) appender).setWriter(writer);

            appenderThread = new AppenderThread(reader);
            appenderThread.start();

        } catch (Exception e) {
            log.error("��ʼ����̬��־ģ��ʧ��", e);
        }
    }

    /**
     * ��ȡ��̬��־�����
     *
     * @return ��̬��־�����
     */
    public static DynamicLogUtil getDynamicLogUtil() {
        return dynamicLog;
    }

    /**
     * ��ȡ��̬��־����
     *
     * @return ��̬��־������ַ���
     */
    public String getDynamicLog() {
        return appenderThread.getDynamicLog();
    }

    /**
     * �����̬��־����
     */
    public void clearDynamicLog() {
        appenderThread.clearDynamicLog();

    }
}

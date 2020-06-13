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
 * 动态日志类，（单例模式）实时读取logger输入流并缓存
 *
 * @author 花小琪
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
     * 动态日志类构造函数，读取log4j的WriterAppender配置
     */
    public DynamicLogUtil() {

        Logger root = Logger.getLogger("Log4jMain");
        try {
            // 获取子记录器的输出源
            Appender appender = root.getAppender("WriterAppender");
            // 定义一个未连接的输入流管道
            PipedReader reader = new PipedReader();
            // 定义一个已连接的输出流管理，并连接到reader
            Writer writer = new PipedWriter(reader);
            // 设置 appender 输出流
            ((WriterAppender) appender).setWriter(writer);

            appenderThread = new AppenderThread(reader);
            appenderThread.start();

        } catch (Exception e) {
            log.error("初始化动态日志模块失败", e);
        }
    }

    /**
     * 获取动态日志类对象
     *
     * @return 动态日志类对象
     */
    public static DynamicLogUtil getDynamicLogUtil() {
        return dynamicLog;
    }

    /**
     * 获取动态日志缓存
     *
     * @return 动态日志缓存的字符串
     */
    public String getDynamicLog() {
        return appenderThread.getDynamicLog();
    }

    /**
     * 清除动态日志缓存
     */
    public void clearDynamicLog() {
        appenderThread.clearDynamicLog();

    }
}

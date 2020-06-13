package priv.suki.util;

import java.io.IOException;
import java.io.PipedReader;
import java.util.Scanner;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 动态日志加载类，实时读取logger输入流并缓存
 *
 * @author 花小琪
 * @version 1.0
 */
public class AppenderThread extends Thread {
    // Log
    private static Log log = LogFactory.getLog(AppenderThread.class);

    private PipedReader pipedreader;
    private StringBuilder stringBuilder = new StringBuilder();
    private Boolean isstart = true;

    /**
     * 动态日志加载类构造
     *
     * @param reader 输入流
     */
    AppenderThread(PipedReader reader) {
        this.pipedreader = reader;
    }

    @Override
    public void run() {
        // 不间断地扫描输入流

        stringBuilder = new StringBuilder();
        Scanner scanner = new Scanner(pipedreader);

        // 将扫描到的字符流打印在屏目
        while (isstart) {
            try {
                if (stringBuilder.toString().length() > 10000) {
                    stringBuilder.delete(0, stringBuilder.length());//日志超过范围，清除缓存
                }

                if (pipedreader.ready()) {
                    scanner = new Scanner(pipedreader);
                }
                while (scanner.hasNext()) {
                    stringBuilder.append(scanner.nextLine()).append(System.getProperty("line.separator"));
//				System.out.println("打印消息"+stringBuilder);
                }

                Thread.sleep(1);
            } catch (IOException e) {
                // 解决当某个log4j线程关闭后，流关闭的问题
            } catch (Exception e) {
                log.error("动态日志执行失败", e);
            }
        }
        scanner.close();

    }

    /**
     * 获取动态日志缓存
     *
     * @return 动态日志缓存stringBuilder的字符串
     */
    public String getDynamicLog() {
        return stringBuilder.toString();
    }

    /**
     * 清除动态日志缓存
     */
    public void clearDynamicLog() {
//		stringBuilder = new StringBuilder();
        stringBuilder.delete(0, stringBuilder.length());
    }

}
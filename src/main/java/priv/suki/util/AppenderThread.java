package priv.suki.util;

import java.io.IOException;
import java.io.PipedReader;
import java.util.Scanner;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * ��̬��־�����࣬ʵʱ��ȡlogger������������
 *
 * @author ��С��
 * @version 1.0
 */
public class AppenderThread extends Thread {
    // Log
    private static Log log = LogFactory.getLog(AppenderThread.class);

    private PipedReader pipedreader;
    private StringBuilder stringBuilder = new StringBuilder();
    private Boolean isstart = true;

    /**
     * ��̬��־�����๹��
     *
     * @param reader ������
     */
    AppenderThread(PipedReader reader) {
        this.pipedreader = reader;
    }

    @Override
    public void run() {
        // ����ϵ�ɨ��������

        stringBuilder = new StringBuilder();
        Scanner scanner = new Scanner(pipedreader);

        // ��ɨ�赽���ַ�����ӡ����Ŀ
        while (isstart) {
            try {
                if (stringBuilder.toString().length() > 10000) {
                    stringBuilder.delete(0, stringBuilder.length());//��־������Χ���������
                }

                if (pipedreader.ready()) {
                    scanner = new Scanner(pipedreader);
                }
                while (scanner.hasNext()) {
                    stringBuilder.append(scanner.nextLine()).append(System.getProperty("line.separator"));
//				System.out.println("��ӡ��Ϣ"+stringBuilder);
                }

                Thread.sleep(1);
            } catch (IOException e) {
                // �����ĳ��log4j�̹߳رպ����رյ�����
            } catch (Exception e) {
                log.error("��̬��־ִ��ʧ��", e);
            }
        }
        scanner.close();

    }

    /**
     * ��ȡ��̬��־����
     *
     * @return ��̬��־����stringBuilder���ַ���
     */
    public String getDynamicLog() {
        return stringBuilder.toString();
    }

    /**
     * �����̬��־����
     */
    public void clearDynamicLog() {
//		stringBuilder = new StringBuilder();
        stringBuilder.delete(0, stringBuilder.length());
    }

}
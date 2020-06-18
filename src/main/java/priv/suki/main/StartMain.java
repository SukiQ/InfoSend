package priv.suki.main;

import priv.suki.process.Judge;
import priv.suki.util.Propert;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.PropertyConfigurator;

import priv.suki.controller.ContralCenter;
import priv.suki.controller.SetParamForLinux;
import priv.suki.frame.HomePage;

/**
 * ����
 *
 * @author ��С��
 * @version 1.0
 */
public class StartMain {

    private static final Log log = LogFactory.getLog(StartMain.class);

    static {
        PropertyConfigurator.configure("." + Propert.SYS_FILE_SPARATOR + "InfoSend" + Propert.SYS_FILE_SPARATOR + "cfg" + Propert.SYS_FILE_SPARATOR + "log4j.properties");

    }

    public static void main(String[] args) {

        Thread.currentThread().setName("StartMain");
        log.info("InfoSendV1.0.3_@Suki��С�� already start");

        /*
         * ���������߳�
         */
        ContralCenter.getContral();

        /*
         * ���ֲ���ϵͳ
         */
        if (Judge.judgeWindows()) { //windows

            /* �򿪳�ʼҳ���� */
            HomePage.getHomePage();

        } else { //linux

            /*��ȡ�����ļ�*/
            SetParamForLinux.setParam();
        }


    }
}

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
 * 主类
 *
 * @author 花小琪
 * @version 1.0
 */
public class StartMain {

    private static final Log log = LogFactory.getLog(StartMain.class);

    static {
        PropertyConfigurator.configure("." + Propert.SYS_FILE_SPARATOR + "InfoSend" + Propert.SYS_FILE_SPARATOR + "cfg" + Propert.SYS_FILE_SPARATOR + "log4j.properties");

    }

    public static void main(String[] args) {

        Thread.currentThread().setName("StartMain");
        log.info("InfoSendV1.0.3_@Suki花小琪 already start");

        /*
         * 启动控制线程
         */
        ContralCenter.getContral();

        /*
         * 区分操作系统
         */
        if (Judge.judgeWindows()) { //windows

            /* 打开初始页界面 */
            HomePage.getHomePage();

        } else { //linux

            /*读取配置文件*/
            SetParamForLinux.setParam();
        }


    }
}

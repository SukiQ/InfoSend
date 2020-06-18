package priv.suki.controller;

import priv.suki.process.FileListener;
import priv.suki.process.Judge;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import priv.suki.util.Propert;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * 命令监控线程，实时获取command指令
 *
 * @author 花小琪
 * @version 1.0.3
 */
public class CmdMonitor implements Runnable {

    private static final Log log = LogFactory.getLog(CmdMonitor.class);
    private InputStream inputStream = null;
    private final boolean _Current = true;


    @Override
    public void run() {

        //命令检查周期
        long interval = 1000L;
        new FileListener(".." + Propert.SYS_FILE_SPARATOR + "data" + Propert.SYS_FILE_SPARATOR + "cmd.tmp");

        while (_Current) {
            if (Propert.getPropert().isChanged()) {
                Propert.getPropert().setChanged(false);
                Properties properties = new Properties();
                try {
                    inputStream = new BufferedInputStream(new FileInputStream(".." + Propert.SYS_FILE_SPARATOR + "data" + Propert.SYS_FILE_SPARATOR + "cmd.tmp"));
                    properties.load(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

                    /*如果当前处于cut状态，命令为cut_over,关闭cut*/
                    if (Judge.needCutOver(properties.getProperty("cut"))) {
                        ContralCenter.getContral().setCutSwitch(false);
                    }

                    /*如果当前处于cut_over状态，命令为cut,开启cut*/
                    if (Judge.needCut(properties.getProperty("cut"))) {
                        ContralCenter.getContral().setCutSwitch(true);
                    }

                    /*如果当前处于parse状态，命令为parse_over,关闭parse*/
                    if (Judge.needParseOver(properties.getProperty("parse"))) {
                        ContralCenter.getContral().setParse(false);
                    }

                    /*如果当前处于parse_over状态，命令为parse,开启parse*/
                    if (Judge.needParse(properties.getProperty("parse"))) {
                        ContralCenter.getContral().setParse(true);
                    }

                } catch (Exception e) {
                    log.error("更新临时配置文件异常", e);

                } finally {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        log.error("监控线程关闭流异常", e);
                    }
                }
            }

            try {
                Thread.sleep(interval);
            } catch (InterruptedException e) {
                log.error("，命令监控线程被打断");
            }

        }

    }


}

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
 * �������̣߳�ʵʱ��ȡcommandָ��
 *
 * @author ��С��
 * @version 1.0.3
 */
public class CmdMonitor implements Runnable {

    private static final Log log = LogFactory.getLog(CmdMonitor.class);
    private InputStream inputStream = null;
    private final boolean _Current = true;


    @Override
    public void run() {

        //����������
        long interval = 1000L;
        new FileListener(".." + Propert.SYS_FILE_SPARATOR + "data" + Propert.SYS_FILE_SPARATOR + "cmd.tmp");

        while (_Current) {
            if (Propert.getPropert().isChanged()) {
                Propert.getPropert().setChanged(false);
                Properties properties = new Properties();
                try {
                    inputStream = new BufferedInputStream(new FileInputStream(".." + Propert.SYS_FILE_SPARATOR + "data" + Propert.SYS_FILE_SPARATOR + "cmd.tmp"));
                    properties.load(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

                    /*�����ǰ����cut״̬������Ϊcut_over,�ر�cut*/
                    if (Judge.needCutOver(properties.getProperty("cut"))) {
                        ContralCenter.getContral().setCutSwitch(false);
                    }

                    /*�����ǰ����cut_over״̬������Ϊcut,����cut*/
                    if (Judge.needCut(properties.getProperty("cut"))) {
                        ContralCenter.getContral().setCutSwitch(true);
                    }

                    /*�����ǰ����parse״̬������Ϊparse_over,�ر�parse*/
                    if (Judge.needParseOver(properties.getProperty("parse"))) {
                        ContralCenter.getContral().setParse(false);
                    }

                    /*�����ǰ����parse_over״̬������Ϊparse,����parse*/
                    if (Judge.needParse(properties.getProperty("parse"))) {
                        ContralCenter.getContral().setParse(true);
                    }

                } catch (Exception e) {
                    log.error("������ʱ�����ļ��쳣", e);

                } finally {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        log.error("����̹߳ر����쳣", e);
                    }
                }
            }

            try {
                Thread.sleep(interval);
            } catch (InterruptedException e) {
                log.error("���������̱߳����");
            }

        }

    }


}

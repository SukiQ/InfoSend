package priv.suki.main;

import priv.suki.frame.util.FrameHelper;
import priv.suki.util.StringUtil;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CmdDeal {

    public static void main(String[] args) {
        boolean parse = false;
        boolean cut = false;
        if (StringUtil.isBlank(args[0])) {
            System.out.println("ָ���Ϊ�գ���������ȷָ��");
            return;
        }

        if ("cut".equalsIgnoreCase(args[0])) {
            System.out.println("����Ϊ����ȡ��ʼ����");
            cut = true;
        }
        if ("cut_over".equalsIgnoreCase(args[0])) {
            System.out.println("����Ϊ����ȡֹͣ����");
            cut = false;
        }
        if ("parse".equalsIgnoreCase(args[0])) {
            System.out.println("����Ϊ����ͣ��ʼ����");
            parse = true;
        }
        if ("parse_over".equalsIgnoreCase(args[0])) {
            System.out.println("����Ϊ����ֹͣͣ����");
            parse = true;
        }

        File file = new File(FrameHelper.CMD_FILE_PATH);
        if (!file.exists()) {
            System.out.println("�ļ������ڣ������Ƿ����� InfoSend");
            return;
        }
        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write("parse=" + parse + "\ncut=" + cut);
            fileWriter.flush();
        } catch (IOException e) {
            System.out.println("д�������");
            e.printStackTrace();
        }
    }

}

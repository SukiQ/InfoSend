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
            System.out.println("指令不能为空，请输入正确指令");
            return;
        }

        if ("cut".equalsIgnoreCase(args[0])) {
            System.out.println("输入为：截取开始命令");
            cut = true;
        }
        if ("cut_over".equalsIgnoreCase(args[0])) {
            System.out.println("输入为：截取停止命令");
            cut = false;
        }
        if ("parse".equalsIgnoreCase(args[0])) {
            System.out.println("输入为：暂停开始命令");
            parse = true;
        }
        if ("parse_over".equalsIgnoreCase(args[0])) {
            System.out.println("输入为：暂停停止命令");
            parse = true;
        }

        File file = new File(FrameHelper.CMD_FILE_PATH);
        if (!file.exists()) {
            System.out.println("文件不存在，请检查是否启动 InfoSend");
            return;
        }
        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write("parse=" + parse + "\ncut=" + cut);
            fileWriter.flush();
        } catch (IOException e) {
            System.out.println("写命令出错");
            e.printStackTrace();
        }
    }

}

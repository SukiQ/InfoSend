package priv.suki.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileUtil {


    /**
     * 创建文件方法，若创建失败返回false
     *
     * @param fileAllPath 待创建文件的路径
     * @return 是否创建成功
     */
    public static boolean createFile(String fileAllPath) {
        File file = new File(fileAllPath);
        boolean result = false;
        if (!file.exists()) {
            try {
                result = file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return result;
    }


    /**
     * 创建文件方法，若有则初始化文件
     *
     * @param fileName 文件名
     * @throws IOException io异常
     */
    public static void initFileAndCreate(String fileName, String txt) throws IOException {
        File file = new File(fileName);
        if (!file.exists()) {
            boolean result = file.createNewFile();
            if (!result) {
                throw new IOException();
            }
        }


        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(txt);
            fileWriter.flush();
        }
    }
}

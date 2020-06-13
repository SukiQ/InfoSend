package priv.suki.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileUtil {


    /**
     * �����ļ�������������ʧ�ܷ���false
     *
     * @param fileAllPath �������ļ���·��
     * @return �Ƿ񴴽��ɹ�
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
     * �����ļ��������������ʼ���ļ�
     *
     * @param fileName �ļ���
     * @throws IOException io�쳣
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

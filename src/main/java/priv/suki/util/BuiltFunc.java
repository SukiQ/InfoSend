package priv.suki.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * ���������࣬�ṩInfoSend���õĸ��ַ���
 *
 * @author ��С��
 * @version 1.o
 */
public class BuiltFunc {

    private int num = 0;


    /**
     * ��ȡ��ǰʱ��
     *
     * @return ��ǰʱ�䣬Ĭ�ϸ�ʽΪyyyy-MM-dd HH:mm:ss
     */
    public String date() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// �������ڸ�ʽ
        return df.format(new Date());
    }

    /**
     * ��ȡ��Ϣ��ˮ�ţ�����
     *
     * @return ��ˮ��id
     */
    public String id() {
        num++;
        return String.valueOf(num);
    }

    public void setId(int num) {
        this.num = num;
    }

}

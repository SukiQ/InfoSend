package priv.suki.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 函数方法类，提供InfoSend可用的各种方法
 *
 * @author 花小琪
 * @version 1.o
 */
public class BuiltFunc {

    private int num = 0;


    /**
     * 获取当前时间
     *
     * @return 当前时间，默认格式为yyyy-MM-dd HH:mm:ss
     */
    public String date() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
        return df.format(new Date());
    }

    /**
     * 获取消息流水号，递增
     *
     * @return 流水号id
     */
    public String id() {
        num++;
        return String.valueOf(num);
    }

    public void setId(int num) {
        this.num = num;
    }

}

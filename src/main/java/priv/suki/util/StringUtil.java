package priv.suki.util;

import priv.suki.util.BuiltFunc;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author 花小琪
 * @version 1.0.1
 */
public class StringUtil {
    public static final int BYTE1 = 1;
    public static final int BYTE2 = 2;
    public static final int BYTE3 = 3;
    public static final int BYTE4 = 4;

    /**
     * 根据正则，获取需要的String（正则中括号包裹范围内，是需要取出的部分）
     *
     * @param expr  正则表达式
     * @param str   需要获取的String
     * @param index 取第几个部分，从1开始
     * @return 取出结果值
     */
    public static String getSuiltString(String expr, String str, int index) {
        Pattern pattern = Pattern.compile(expr, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(str);
        if (matcher.find()) {
            return matcher.group(index);
        }
        return null;
    }

    /**
     * 判定字符是否为空
     *
     * @return 是否为O空
     */
    public static boolean isBlank(String str) {
        return str == null || "".equals(str.trim());
    }

    /**
     * 构造消息体
     *
     * @return 消息
     */
    public static String buildeMsg(List<String> list, BuiltFunc builtFunc) {

        StringBuilder resultMsg = new StringBuilder();
        for (String s : list) {
            /* 当前时间函数 */
            if ("${date}".equals(s)) {
                resultMsg.append(builtFunc.date());
                continue;
            }
            /* 消息流水号函数 */
            if ("${id}".equals(s)) {
                resultMsg.append(builtFunc.id());
                continue;
            }
            resultMsg.append(s);
        }
        return resultMsg.toString();

    }

    /**
     * String 类型转为Date类型
     *
     * @param strTime    时间
     * @param formatType 时间格式
     * @return 时间
     * @throws ParseException 转换异常
     */
    public static Date stringToDate(String strTime, String formatType) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(formatType);
        Date date;
        date = formatter.parse(strTime);
        return date;
    }


    /**
     * 拼接数组
     *
     * @param byte1 数组1
     * @param byte2 数组2
     * @return 合成数组
     */
    public static byte[] byteMerger(byte[] byte1, byte[] byte2) {
        if (byte1 == null) {
            return byte2;
        }
        byte[] byteTmp = new byte[byte1.length + byte2.length];
        System.arraycopy(byte1, 0, byteTmp, 0, byte1.length);
        System.arraycopy(byte2, 0, byteTmp, byte1.length, byte2.length);
        return byteTmp;
    }

    public static byte[] byteMergerAll(byte[]... values) {
        int length_byte = 0;
        for (byte[] value : values) {
            length_byte += value.length;
        }
        byte[] all_byte = new byte[length_byte];
        int countLength = 0;
        for (byte[] b : values) {
            System.arraycopy(b, 0, all_byte, countLength, b.length);
            countLength += b.length;
        }
        return all_byte;
    }


    public static byte[] IntToBytes(int num, int len) {
        byte[] tmpbyte;
        if (len <= 0) {
            return null;
        }
        if (len == BYTE1) {
            tmpbyte = new byte[len];
            byte p0 = (byte) (num);
            tmpbyte[0] = p0;
            return tmpbyte;
        }
        if (len == BYTE2) {
            tmpbyte = new byte[len];
            byte p0 = (byte) (num >> 8);
            byte p1 = (byte) (num & 0xff);
            tmpbyte[0] = p0;
            tmpbyte[1] = p1;
            return tmpbyte;
        }
        if (len == BYTE3) {
            tmpbyte = new byte[len];
            byte p0 = (byte) (num >> 16);
            byte p1 = (byte) ((num & 0xff00) >> 8);
            byte p2 = (byte) (num & 0xff);
            tmpbyte[0] = p0;
            tmpbyte[1] = p1;
            tmpbyte[2] = p2;
            return tmpbyte;
        }

        if (len == BYTE4) {
            tmpbyte = new byte[len];
            byte p0 = (byte) (num >> 24);
            byte p1 = (byte) ((num & 0xff0000) >> 16);
            byte p2 = (byte) ((num & 0xff00) >> 8);
            byte p3 = (byte) (num & 0xff);
            tmpbyte[0] = p0;
            tmpbyte[1] = p1;
            tmpbyte[2] = p2;
            tmpbyte[3] = p3;
            return tmpbyte;
        }

        return null;
    }

}

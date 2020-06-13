package priv.suki.util;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;


/**
 * @author hxq
 */
public class FrameUtil {
    public static String[] infArray;
    public static String[] infHomeArray;
    public static Map<String, String[]> stringMap;


    /**
     * 获取接口的中文名数组
     *
     * @return 接口名数组（中文）
     */
    public static String[] getInfArray() {
        if (infArray == null) {
            infArray = Arrays.stream(InterfaceEnum.values()).map(InterfaceEnum::getCName).toArray(String[]::new);
        }
        return infArray;
    }

    /**
     * 获取接口的中文名数组，并将传入接口置于首位
     *
     * @return 接口名数组（中文）
     */
    public static String[] getInfArray(String topInf) {
        if (stringMap == null) {
            stringMap = new HashMap<>(InterfaceEnum.values().length);
        }
        if (stringMap.get(topInf) == null) {
            String[] infTmp = Arrays.stream(getInfArray()).sorted(Comparator.comparing(in -> !in.equals(topInf))).toArray(String[]::new);
            stringMap.put(topInf, infTmp);
            return infTmp;
        }
        return stringMap.get(topInf);
    }

    /**
     * 获取接口的中文名数组
     *
     * @return 接口名数组（中文）
     */
    public static String[] getHomeInfArray() {
        if (infHomeArray == null) {
            infHomeArray = new String[InterfaceEnum.values().length + 1];
            infHomeArray[0] = "\u9009\u62E9\u63A5\u53E3\u7C7B\u578B";
            String[] tmpArray = getInfArray();
            System.arraycopy(tmpArray, 0, infHomeArray, 1, tmpArray.length);
        }
        return infHomeArray;
    }


}

package priv.suki.process;

import priv.suki.controller.ContralCenter;
import priv.suki.util.InterfaceEnum;
import priv.suki.util.Propert;

/**
 * @author 花小琪
 * @version 1.0.3
 */
public class Judge {

    /**
     * 判断是否为Windows系统
     *
     * @return 是否为Windows系统
     */
    public static Boolean judgeWindows() {
        return System.getProperties().getProperty("os.name").toUpperCase().contains("WINDOWS");
    }

    /**
     * 是否为需要缓存的接口类型
     *
     * @return 是否为需要缓存的接口类型
     */
    public static Boolean isCacheInterface() {
        switch (ContralCenter.getContral().getSelInterface()) {
            case I1:
            case OMC:
                return true;
            default:
                return false;
        }
    }

    /**
     * (linux配置)是否为特殊接口（不能开启拼接模式/能够开启极速模式）
     *
     * @return 是否为特殊接口
     */
    public static Boolean isSpecialInterface(InterfaceEnum interfacetype) {
        switch (interfacetype) {
            case I1:
            case OMC:
            case CUCC:
                return true;
            default:
                return false;
        }
    }

    /**
     * 判断是否停止发送
     *
     * @return 是否停止发送
     */
    public static boolean isEnd() {
        return !ContralCenter.getContral().isNoSend() && (ContralCenter.getContral().getNumber() == -1
                || ContralCenter.getContral().getSendNum() < ContralCenter.getContral().getNumber());
    }

    /**
     * 判断是否开始风暴发送
     *
     * @return 是否开始风暴发送
     */
    public static boolean isStorm() {
        return ContralCenter.getContral().getSendType() == Propert.STORM_SEND
                && ContralCenter.getContral().getSendNum() == ContralCenter.getContral().getNumber()
                && !ContralCenter.getContral().isStormSend() && ContralCenter.getContral().isPreparing();
    }


    public static boolean needNoInvSend() {
        return ContralCenter.getContral().getNobuildsendNum() < ContralCenter.getContral().getNobuildrate();
    }


    /**
     * 判断当前是否需要结束cut
     *
     * @param currentState 当前命令状态
     * @return 是否需要结束cut
     */
    public static boolean needCutOver(String currentState) {
        return ContralCenter.getContral().isCutSwitch() && !Boolean.parseBoolean(currentState);
    }

    /**
     * 判断当前是否需要cut
     *
     * @param currentState 当前命令状态
     * @return 是否需要cut
     */
    public static boolean needCut(String currentState) {
        return !ContralCenter.getContral().isCutSwitch() && Boolean.parseBoolean(currentState);
    }

    /**
     * 判断当前是否需要parse
     *
     * @param currentState 当前命令状态
     * @return 是否需要parse
     */
    public static boolean needParse(String currentState) {
        return !ContralCenter.getContral().isParse() && Boolean.parseBoolean(currentState);
    }

    /**
     * 判断当前是否需要结束parse
     *
     * @param currentState 当前命令状态
     * @return 是否需要结束parse
     */
    public static boolean needParseOver(String currentState) {
        return ContralCenter.getContral().isParse() && !Boolean.parseBoolean(currentState);
    }

    /**
     * 判断是否需要重连
     *
     * @return 是否需要重连
     */
    public static Boolean judgeReconn() {
        return Propert.getPropert().isAutoReconn() && !ContralCenter.getContral().isNoSend();
    }


}

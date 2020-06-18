package priv.suki.process;

import priv.suki.controller.ContralCenter;
import priv.suki.util.InterfaceEnum;
import priv.suki.util.Propert;

/**
 * @author ��С��
 * @version 1.0.3
 */
public class Judge {

    /**
     * �ж��Ƿ�ΪWindowsϵͳ
     *
     * @return �Ƿ�ΪWindowsϵͳ
     */
    public static Boolean judgeWindows() {
        return System.getProperties().getProperty("os.name").toUpperCase().contains("WINDOWS");
    }

    /**
     * �Ƿ�Ϊ��Ҫ����Ľӿ�����
     *
     * @return �Ƿ�Ϊ��Ҫ����Ľӿ�����
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
     * (linux����)�Ƿ�Ϊ����ӿڣ����ܿ���ƴ��ģʽ/�ܹ���������ģʽ��
     *
     * @return �Ƿ�Ϊ����ӿ�
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
     * �ж��Ƿ�ֹͣ����
     *
     * @return �Ƿ�ֹͣ����
     */
    public static boolean isEnd() {
        return !ContralCenter.getContral().isNoSend() && (ContralCenter.getContral().getNumber() == -1
                || ContralCenter.getContral().getSendNum() < ContralCenter.getContral().getNumber());
    }

    /**
     * �ж��Ƿ�ʼ�籩����
     *
     * @return �Ƿ�ʼ�籩����
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
     * �жϵ�ǰ�Ƿ���Ҫ����cut
     *
     * @param currentState ��ǰ����״̬
     * @return �Ƿ���Ҫ����cut
     */
    public static boolean needCutOver(String currentState) {
        return ContralCenter.getContral().isCutSwitch() && !Boolean.parseBoolean(currentState);
    }

    /**
     * �жϵ�ǰ�Ƿ���Ҫcut
     *
     * @param currentState ��ǰ����״̬
     * @return �Ƿ���Ҫcut
     */
    public static boolean needCut(String currentState) {
        return !ContralCenter.getContral().isCutSwitch() && Boolean.parseBoolean(currentState);
    }

    /**
     * �жϵ�ǰ�Ƿ���Ҫparse
     *
     * @param currentState ��ǰ����״̬
     * @return �Ƿ���Ҫparse
     */
    public static boolean needParse(String currentState) {
        return !ContralCenter.getContral().isParse() && Boolean.parseBoolean(currentState);
    }

    /**
     * �жϵ�ǰ�Ƿ���Ҫ����parse
     *
     * @param currentState ��ǰ����״̬
     * @return �Ƿ���Ҫ����parse
     */
    public static boolean needParseOver(String currentState) {
        return ContralCenter.getContral().isParse() && !Boolean.parseBoolean(currentState);
    }

    /**
     * �ж��Ƿ���Ҫ����
     *
     * @return �Ƿ���Ҫ����
     */
    public static Boolean judgeReconn() {
        return Propert.getPropert().isAutoReconn() && !ContralCenter.getContral().isNoSend();
    }


}

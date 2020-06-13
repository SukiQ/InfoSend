package priv.suki.util.cuccalarm;

import lombok.Getter;
import lombok.Setter;

/**
 * OMC北向接口消息对象
 *
 * @author 花小琪
 * @version 1.0
 */
@Getter
@Setter
public class CUCCACKMsgVO {

    public static final int REALTIMEALARM = 0;
    public static final int REQLOGINALARM = 1;
    public static final int ACKLOGINALARM = 2;
    public static final int REQHEARTBEAT = 3;
    public static final int ACKHEARTBEAT = 4;
    public static final int CLOSECONNALARM = 5;
    public static final int NOTNORMALMSG = -1;


    public static final short startSign = (short) 0xffff;
    private String msgBody = null;
    private int msgType = -1;
    private int timeStamp = 0;
    private String desc;
}

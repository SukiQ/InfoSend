package priv.suki.util.i1;

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
public class I1CommonACKMsgVO {


    public static final int I1LOGINALARM = 1;
    public static final int I1ACKLOGINALARM = 2;
    public static final int I1HEARTBEAT = 3;
    public static final int I1SYNCALARMMSG = 4;
    public static final int I1ALARMMSG = 5;
    public static final int I1ALARMSTART = 6;
    public static final int I1ALARMEND = 7;


    public static final short startSign = (short) 0x7EE7;
    private String msgBody = null;
    private int msgType = -1;
    private int msgFormat = -1;
    private int timeStamp = 0;
    private int timeStamp_ms = 0;
    private int reserve;
    private String desc;


}

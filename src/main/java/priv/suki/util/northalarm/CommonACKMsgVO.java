package priv.suki.util.northalarm;

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
public class CommonACKMsgVO {

    public static final int REALTIMEALARM = 0;
    public static final int REQLOGINALARM = 1;
    public static final int ACKLOGINALARM = 2;
    public static final int REQSYNCALARMMSG = 3;
    public static final int ACKSYNCALARMMSG = 4;
    public static final int REQSYNCALARMFILE = 5;
    public static final int ACKSYNCALARMFILE = 6;
    public static final int ACKSYNCALARMFILERESULT = 7;
    public static final int REQHEARTBEAT = 8;
    public static final int ACKHEARTBEAT = 9;
    public static final int CLOSECONNALARM = 10;
    public static final int NOTNORMALMSG = -1;
//	public static final int EXCEPTIONMSG=-2;

    public static final short START_SIGN = (short) 0xffff;
    private String msgBody = null;
    private int msgType = -1;
    private int timeStamp = 0;
    private String desc;


}

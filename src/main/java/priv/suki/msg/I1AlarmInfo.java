package priv.suki.msg;

import priv.suki.util.BuiltFunc;
import priv.suki.util.Propert;
import priv.suki.util.StringUtil;
import priv.suki.util.i1.I1CommonACKMsgVO;
import priv.suki.util.i1.I1Util;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class I1AlarmInfo implements OrgInfo {

    private List<String> msglist;
    private byte[] resultMsg;
    private I1Util i1Util;

    public I1AlarmInfo() {

    }

    private I1AlarmInfo(byte[] msg) {
        resultMsg = msg;
    }

    @Override
    public void initializeMsg() {
        i1Util = new I1Util();
        msglist = new ArrayList<>();
        String msg = Propert.getPropert().getMsg();
        while (msg.contains("${")) {
            String tempMsg = msg.substring(0, msg.indexOf("${"));
            msg = msg.substring(msg.indexOf("${"));
            msglist.add(tempMsg);
            msglist.add(msg.substring(0, msg.indexOf("}") + 1));
            msg = msg.substring(msg.indexOf("}") + 1);
        }
        msglist.add(msg);

    }

    @Override
    public String structureMsg(BuiltFunc builtFunc) {
        // TODO Auto-generated method stub
        return StringUtil.buildeMsg(msglist, builtFunc);
    }

    @Override
    public OrgInfo appendMsg(int appendNum, BuiltFunc builtFunc) throws UnsupportedEncodingException {
        // TODO Auto-generated method stub
        byte[] result = null;
        byte[] oneMsg;
        I1CommonACKMsgVO vo;
        byte[] startSign;
        byte[] timeStamp;
        byte[] timeStamp_ms;
        byte[] msgType;
        byte[] msgFormat;
        byte[] reserve;
        byte[] msgLength;

        for (int i = 0; i < appendNum; i++) {

            vo = i1Util.buildAlarmReqMsg(this.structureMsg(builtFunc));

            startSign = StringUtil.intToBytes(I1CommonACKMsgVO.startSign, 2);
            timeStamp = StringUtil.intToBytes(vo.getTimeStamp(), 4);
            timeStamp_ms = StringUtil.intToBytes(vo.getTimeStamp_ms(), 2);
            msgType = StringUtil.intToBytes(vo.getMsgType(), 2);
            msgFormat = StringUtil.intToBytes(vo.getMsgFormat(), 2);
            reserve = StringUtil.intToBytes(vo.getReserve(), 2);
            msgLength = StringUtil.intToBytes(vo.getMsgBody().getBytes(Propert.getPropert().getCharset()).length, 2);

            byte[] msgbyte = vo.getMsgBody().getBytes(Propert.getPropert().getCharset());

            //拼接字节
            oneMsg = StringUtil.byteMergerAll(startSign, timeStamp, timeStamp_ms, msgType, msgFormat, reserve, msgLength, msgbyte);

            result = StringUtil.byteMerger(result, oneMsg);
        }
        resultMsg = result;
        return new I1AlarmInfo(resultMsg);
    }

    /**
     * StringInfo可以多消息拼接
     */

    @Override
    public byte[] getMsg() {
        // TODO Auto-generated method stub
        return resultMsg;
    }

    @Override
    public String toString() {
        return "";
    }

}

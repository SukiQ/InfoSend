package priv.suki.msg;

import priv.suki.util.BuiltFunc;
import priv.suki.util.Propert;
import priv.suki.util.StringUtil;
import priv.suki.util.northalarm.CommonACKMsgVO;
import priv.suki.util.northalarm.NorthUtil;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class OmcInfo implements OrgInfo {

    private List<String> msglist;
    private byte[] resultMsg;
    private NorthUtil northUtil;

    public OmcInfo() {

    }

    private OmcInfo(byte[] msg) {
        resultMsg = msg;
    }

    @Override
    public void initializeMsg() {
        northUtil = new NorthUtil();
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
        CommonACKMsgVO vo;
        byte[] startSign;
        byte[] msgType;
        byte[] timeStamp;
        byte[] msgLength;

        for (int i = 0; i < appendNum; i++) {

            vo = northUtil.buildAlarmReqMsg(this.structureMsg(builtFunc));

            startSign = StringUtil.IntToBytes(CommonACKMsgVO.START_SIGN, 2);
            msgType = StringUtil.IntToBytes(vo.getMsgType(), 1);
            timeStamp = StringUtil.IntToBytes(vo.getTimeStamp(), 4);
            msgLength = StringUtil.IntToBytes(vo.getMsgBody().getBytes(Propert.getPropert().getCharset()).length, 2);

            byte[] msgbyte = vo.getMsgBody().getBytes(Propert.getPropert().getCharset());

            //拼接字节
            oneMsg = StringUtil.byteMergerAll(startSign, msgType, timeStamp, msgLength, msgbyte);

            result = StringUtil.byteMerger(result, oneMsg);
        }
        resultMsg = result;
        return new OmcInfo(resultMsg);
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

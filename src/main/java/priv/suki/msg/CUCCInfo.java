package priv.suki.msg;

import priv.suki.util.BuiltFunc;
import priv.suki.util.Propert;
import priv.suki.util.StringUtil;
import priv.suki.util.cuccalarm.CUCCACKMsgVO;
import priv.suki.util.cuccalarm.CUCCUtil;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class CUCCInfo implements OrgInfo {

    private List<String> msglist;
    private byte[] resultMsg;
    private CUCCUtil cuccUtil;

    public CUCCInfo() {

    }

    private CUCCInfo(byte[] msg) {
        resultMsg = msg;
    }

    @Override
    public void initializeMsg() {
        cuccUtil = new CUCCUtil();
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
        return StringUtil.buildeMsg(msglist, builtFunc);
    }

    @Override
    public OrgInfo appendMsg(int appendNum, BuiltFunc builtFunc) throws UnsupportedEncodingException {
        byte[] result = null;
        byte[] oneMsg;
        CUCCACKMsgVO vo;
        byte[] startSign;
        byte[] msgType;
        byte[] timeStamp;
        byte[] msgLength;

        for (int i = 0; i < appendNum; i++) {

            vo = cuccUtil.buildAlarmReqMsg(this.structureMsg(builtFunc));

            startSign = StringUtil.IntToBytes(CUCCACKMsgVO.startSign, 2);
            msgType = StringUtil.IntToBytes(vo.getMsgType(), 1);
            timeStamp = StringUtil.IntToBytes(vo.getTimeStamp(), 4);
            msgLength = StringUtil.IntToBytes(vo.getMsgBody().getBytes(Propert.getPropert().getCharset()).length, 2);

            byte[] msgbyte = vo.getMsgBody().getBytes(Propert.getPropert().getCharset());

            //拼接字节
            oneMsg = StringUtil.byteMergerAll(startSign, msgType, timeStamp, msgLength, msgbyte);

            result = StringUtil.byteMerger(result, oneMsg);
        }
        resultMsg = result;
        return new CUCCInfo(resultMsg);
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

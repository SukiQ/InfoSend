package priv.suki.msg;

import java.util.ArrayList;
import java.util.List;

import priv.suki.util.BuiltFunc;
import priv.suki.util.Propert;
import priv.suki.util.StringUtil;

public class StringInfo implements OrgInfo {

    private List<String> msglist;
    private String resultMsg;

    public StringInfo() {

    }

    private StringInfo(String msg) {
        resultMsg = msg;
    }

    @Override
    public void initializeMsg() {

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
    public OrgInfo appendMsg(int appendNum, BuiltFunc builtFunc) {
        // TODO Auto-generated method stub
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < appendNum; i++) {
            result.append(this.structureMsg(builtFunc));
        }
        resultMsg = result.toString();
        return new StringInfo(resultMsg);
    }

    /**
     * StringInfo可以多消息拼接
     */

    @Override
    public String getMsg() {
        // TODO Auto-generated method stub
        return resultMsg;
    }

    @Override
    public String toString() {
        return resultMsg;
    }

}

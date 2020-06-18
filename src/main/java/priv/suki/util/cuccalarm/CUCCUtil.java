package priv.suki.util.cuccalarm;


import priv.suki.util.Propert;
import priv.suki.util.StringUtil;
import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 读取CUCC告警接口工具类
 *
 * @author 花小琪
 * @version 1.0.3
 */
public class CUCCUtil {
    private static final Log log = LogFactory.getLog(CUCCUtil.class);
    // 锁对象
    private final Object sendLock = new Object();
    private final Object receiveLock = new Object();

    /**
     * CUCCUtil构造
     */
    public CUCCUtil() {

    }

    /**
     * 读取CUCC告警接口的消息流
     *
     * @param buis 消息流
     * @return 消息对象
     * @throws Exception 读取异常
     */
    public CUCCACKMsgVO receive(BufferedInputStream buis) throws Exception {
        return receive(buis, "utf-8");
    }

    /**
     * 读取CUCC告警接口的消息流
     *
     * @param buis    消息流
     * @param charSet 字符编码
     * @return 消息对象
     * @throws Exception 读取异常
     */
    public CUCCACKMsgVO receive(BufferedInputStream buis, String charSet) throws Exception {
        CUCCACKMsgVO msg = new CUCCACKMsgVO();
        DataInputStream dis;
        DataInputStream disHead = null;
        ByteInputStream byis = null;
        byte[] headByte;
        synchronized (receiveLock) {
            if (buis == null) {
                log.error("传入的输入流参数为null");
            } else {
                dis = new DataInputStream(buis);
                headByte = new byte[9];
                try {
                    /*
                     * CUCC接口消息数据由消息头和消息体组成。消息头由9个字节（byte）表示 消息头由开始标志（2）-消息类型（1）-秒时间戳（4）-长度（2）
                     */
                    dis.readFully(headByte);
                    byis = new ByteInputStream(headByte, 0, headByte.length);

                    /* 开始标志：固定为0xFFFF，消息开始标识。 */
                    disHead = new DataInputStream(byis);
                    short startsign = disHead.readShort();
                    if (startsign == CUCCACKMsgVO.START_SIGN) {
                        /*
                         * 消息类型：单字节整型数，类型编码含义如下：
                         * 0:realTimeAlarm
                         * 1:reqLoginAlarm
                         * 2:ackLoginAlarm
                         * 3:reqHeartBeat
                         * 4:ackHeartBeat
                         * 5:closeConnAlarm
                         */
                        int type = disHead.readByte();
                        /* 秒时间戳,字节整型数，字节顺序为Big-Endian，表示消息产生时间,为距离1970-01-01 00:00:00时间偏移的秒数。 */
                        int timestamp = disHead.readInt();
                        /* 长度,字节整型数，字节顺序为Big-Endian，表示消息体字节长度，取值范围0~32767。 */
                        int bodylength = disHead.readShort();
                        headByte = new byte[bodylength];
                        dis.readFully(headByte);
                        String bodymsg = new String(headByte, charSet);
                        msg.setTimeStamp(timestamp * 1000);
                        msg.setMsgType(type);
                        msg.setMsgBody(bodymsg);
                    } else {
                        log.error("读取到的不是消息开始的标记");
                        msg.setMsgType(-1);
                        msg.setDesc("读取到的不是消息开始的标记");
                    }
                } catch (Exception e) {
                    throw new Exception(e);
                } finally {
                    if (byis != null) {
                        byis.close();
                    }
                    if (disHead != null) {
                        disHead.close();
                    }
                }

            }
        }

        return msg;
    }

    public boolean send(BufferedOutputStream bos, CUCCACKMsgVO msg) throws Exception {
        synchronized (sendLock) {
            if (bos == null) {
                log.error("传入的输入流参数为null");
                return false;
            } else {
                try (ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream(9); DataOutputStream oos = new DataOutputStream(byteOutStream)) {
                    oos.writeShort(CUCCACKMsgVO.START_SIGN);
                    oos.writeByte(msg.getMsgType());
                    oos.writeInt(msg.getTimeStamp());
                    oos.writeShort(msg.getMsgBody().getBytes(Propert.getPropert().getCharset()).length);
                    bos.write(byteOutStream.toByteArray());
                    bos.write(msg.getMsgBody().getBytes(Propert.getPropert().getCharset()));
                    bos.flush();
                } catch (Exception e) {
                    throw new Exception(e);
                }

            }
        }

        return true;
    }

    /**
     * 解析响应消息体，将消息以键值对存放
     *
     * @param bodymsg 消息体
     * @return 转换后消息体
     */
    public Map<String, String> changemsg2map(String bodymsg) {
        Map<String, String> msgs = new HashMap<>();
        if (bodymsg != null && !bodymsg.trim().equals("") && bodymsg.contains(";")) {
            String[] keyvaluemsgs = bodymsg.split(";", bodymsg.length());
            for (String keyvaluemsg : keyvaluemsgs) {
                if (keyvaluemsg.contains("=")) {
                    String[] values = keyvaluemsg.trim().split("=", keyvaluemsg.length());
                    if (values.length > 2) {
                        log.error("不正确的响应消息格式:" + bodymsg);
                        return null;
                    } else {
                        msgs.put(values[0], values[1]);
                    }
                } else {
                    msgs.put("msgtype", keyvaluemsg);
                }
            }
        } else if (bodymsg != null && !"".equals(bodymsg.trim())) {
            msgs.put("single", bodymsg);
        } else {
            log.error("传入的参数为空");
            return null;
        }
        return msgs;
    }

    /**
     * 判断登陆消息体
     *
     * @param msg 消息
     * @param bos 输出流
     * @throws Exception 判断登录异常
     */
    public boolean parseRecCom(CUCCACKMsgVO msg, BufferedOutputStream bos) throws Exception {
        Map<String, String> msgs = changemsg2map(msg.getMsgBody());
        if (msgs.containsKey("user") && msgs.containsKey("key")) {
            String username = msgs.get("user");

            if (!StringUtil.isBlank(username)) {
                if (!username.trim().equalsIgnoreCase(Propert.getPropert().getCucc_alarm_user())) {
                    log.error("用户名不存在");
                    send(bos, buildLoginReqMsg(false, "username does not exist"));
                    return false;
                }
            }

            String pwd = msgs.get("key");
            if (!StringUtil.isBlank(pwd)) {
                if (!pwd.trim().equalsIgnoreCase(Propert.getPropert().getCucc_alarm_pwd())) {
                    log.error("密码不正确");
                    send(bos, buildLoginReqMsg(false, "Incorrect password"));
                    return false;
                }

            }
            log.info("发送登陆成功反馈消息");
            send(bos, buildLoginReqMsg(true, "Incorrect message body"));
            return true;
        } else {
            log.error("消息体不正确，没有user或key参数");
            send(bos, buildLoginReqMsg(false, "Incorrect message body"));
            return false;
        }
    }


    /**
     * 判断心跳消息体
     */
    public boolean parseHeartCom(CUCCACKMsgVO msg, BufferedOutputStream bos) throws Exception {
        Map<String, String> msgs = changemsg2map(msg.getMsgBody());
        if (msgs.containsKey("reqId")) {
            if (!StringUtil.isBlank(msgs.get("reqId"))) {
                int reqId = Integer.parseInt(msgs.get("reqId"));
                send(bos, buildHeartReqMsg(reqId));
                return true;
            } else {
                log.error("收到的心跳消息不正确，reqId为空");
            }
        }
        log.error("收到的心跳消息不正确，没有reqId");
        return false;

    }


    /**
     * 拼接ackLoginAlarm心跳反馈消息
     *
     * @param reqId 告警序列号
     */
    public CUCCACKMsgVO buildHeartReqMsg(int reqId) {
        return commonBuildReqMsg(
                "ackHeartBeat;reqId=" + Integer.valueOf(reqId).toString(),
                CUCCACKMsgVO.ACKHEARTBEAT);

    }

    /**
     * 拼接ackLoginAlarm登陆反馈消息
     *
     * @param loginAct 是否登陆成功
     * @param result   登陆信息
     */
    public CUCCACKMsgVO buildLoginReqMsg(Boolean loginAct, String result) {
        return commonBuildReqMsg("ackLoginAlarm;result=" + (loginAct ? "succ" : "fail") +
                ";resDesc=" + result, CUCCACKMsgVO.ACKLOGINALARM);

    }

    /**
     * 拼接realTimeAlarm告警消息
     *
     * @param msg 告警信息
     */
    public CUCCACKMsgVO buildAlarmReqMsg(String msg) {
        return commonBuildReqMsg(msg, CUCCACKMsgVO.REALTIMEALARM);

    }

    /**
     * 拼接消息头和时间戳
     *
     * @param msgBodyStr 消息体
     * @param type       消息类型
     * @return 消息体
     */
    public CUCCACKMsgVO commonBuildReqMsg(String msgBodyStr, int type) {
        CUCCACKMsgVO reqLoginMsg = new CUCCACKMsgVO();
        reqLoginMsg.setMsgBody(msgBodyStr);
        reqLoginMsg.setMsgType(type);
        int timeStamp = (int) (System.currentTimeMillis() / 1000);
        reqLoginMsg.setTimeStamp(timeStamp);
        return reqLoginMsg;

    }
}

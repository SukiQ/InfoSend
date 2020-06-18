package priv.suki.util.northalarm;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.HashMap;
import java.util.Map;

import priv.suki.controller.ContralCenter;
import priv.suki.util.Propert;
import priv.suki.util.StringUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;

/**
 * OMC北向接口工具类
 *
 * @author 花小琪
 * @version 1.0
 */
public class NorthUtil {
    private static final Log log = LogFactory.getLog(NorthUtil.class);
    // 锁对象
    private final Object sendLock = new Object();
    private final Object receiveLock = new Object();

    /**
     * NorthUtil构造
     */
    public NorthUtil() {

    }

    /**
     * 读取OMC北向接口的消息流
     *
     * @param buis 消息流
     * @return 消息对象
     */
    public CommonACKMsgVO receive(BufferedInputStream buis) throws Exception {
        return receive(buis, "utf-8");
    }

    /**
     * 读取OMC北向接口的消息流
     *
     * @param buis    消息流
     * @param charSet 字符编码
     * @return 消息对象
     */
    public CommonACKMsgVO receive(BufferedInputStream buis, String charSet) throws Exception {
        CommonACKMsgVO msg = new CommonACKMsgVO();
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
                    // OMC北向接口消息数据由消息头和消息体组成。消息头由9个字节（byte）表示 消息头由开始标志（2）-消息类型（1）-秒时间戳（4）-长度（2）//
                    dis.readFully(headByte);
                    byis = new ByteInputStream(headByte, 0, headByte.length);

                    /* 开始标志：固定为0xFFFF，消息开始标识。 */
                    disHead = new DataInputStream(byis);
                    short startsign = disHead.readShort();
                    if (startsign == CommonACKMsgVO.START_SIGN) {
                        /*
                         * 消息类型：单字节整型数，类型编码含义如下： 0:realTimeAlarm 1:reqLoginAlarm 2:ackLoginAlarm
                         * 3:reqSyncAlarmMsg 4:ackSyncAlarmMsg 5:reqSyncAlarmFile 6:ackSyncAlarmFile
                         * 7:ackSyncAlarmFileResult 8:reqHeartBeat 9:ackHeartBeat 10:closeConnAlarm
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

    public boolean send(BufferedOutputStream bos, CommonACKMsgVO msg) throws Exception {
        boolean sendsucc = true;
        synchronized (sendLock) {
            if (bos == null) {
                log.error("传入的输入流参数为null");
                return false;
            } else {
                try (ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream(9); DataOutputStream oos = new DataOutputStream(byteOutStream)) {
                    oos.writeShort(CommonACKMsgVO.START_SIGN);
                    oos.writeByte(msg.getMsgType());
                    oos.writeInt(msg.getTimeStamp());
                    oos.writeShort(msg.getMsgBody().getBytes(Propert.getPropert().getCharset()).length);
                    bos.write(byteOutStream.toByteArray());
                    bos.write(msg.getMsgBody().getBytes(Propert.getPropert().getCharset()));
                    bos.flush();
                } catch (Exception e) {
                    log.error("发送异常", e);
                    throw new Exception(e);
                }

            }
        }

        return sendsucc;
    }

    /**
     * 解析响应消息体，将消息以键值对存放
     *
     * @param bodymsg msg
     * @return map
     */
    public Map<String, String> changemsg2map(String bodymsg) {
        Map<String, String> msgs = new HashMap<>();
        if (bodymsg != null && !"".equals(bodymsg.trim()) && bodymsg.contains(";")) {
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
     */
    public boolean parseRecCom(CommonACKMsgVO msg, BufferedOutputStream bos) throws Exception {
        Map<String, String> msgs = changemsg2map(msg.getMsgBody());
        if (msgs.containsKey("user") && msgs.containsKey("key")) {
            String username = msgs.get("user");

            if (!StringUtil.isBlank(username)) {
                if (!username.trim().equalsIgnoreCase(Propert.getPropert().getNorth_alarm_user())) {
                    log.error("用户名不存在");
                    send(bos, buildLoginReqMsg(false, "username does not exist"));
                    return false;
                }
            }

            String pwd = msgs.get("key");
            if (!StringUtil.isBlank(pwd)) {
                if (!pwd.trim().equalsIgnoreCase(Propert.getPropert().getNorth_alarm_pwd())) {
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
     * 判断同步告警消息体
     *
     * @param msg msg
     * @param bos 输出流
     */
    public boolean parseSyncCom(CommonACKMsgVO msg, BufferedOutputStream bos) throws Exception {
        Map<String, String> msgs = changemsg2map(msg.getMsgBody());
        int reqId;
        if (msgs.containsKey("reqId")) {
            if (!StringUtil.isBlank(msgs.get("reqId"))) {
                reqId = Integer.parseInt(msgs.get("reqId"));
            } else {
                log.error("收到的告警同步消息不正确，reqId为空");
                return false;
            }
        } else {
            log.error("收到的告警同步消息不正确，没有reqId");
            return false;
        }
        if (msgs.containsKey("alarmSeq")) {
            if (!StringUtil.isBlank(msgs.get("alarmSeq"))) {
                int alarmSeq = Integer.parseInt(msgs.get("alarmSeq"));
                ContralCenter.getContral().setNorth_sync_switch(true);
                ContralCenter.getContral().setNorth_sync_number(alarmSeq);
                send(bos, buildSyncReqMsg(reqId));
            } else {
                log.error("收到的告警同步消息不正确，alarmSeq为空");
                return false;
            }
        } else {
            log.error("收到的告警同步消息不正确，没有alarmSeq");
            return false;
        }
        return false;
    }

    /**
     * 判断心跳消息体
     */
    public boolean parseHeartCom(CommonACKMsgVO msg, BufferedOutputStream bos) throws Exception {
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
     * 拼接ackSyncAlarmMsg同步消息反馈消息
     *
     * @param reqId 告警序列号
     */
    public CommonACKMsgVO buildSyncReqMsg(int reqId) {
        return commonBuildReqMsg("ackSyncAlarmMsg;reqId=" + Integer.valueOf(reqId).toString() +
                ";result=succ;resDesc=null", CommonACKMsgVO.ACKSYNCALARMMSG);

    }

    /**
     * 拼接ackLoginAlarm心跳反馈消息
     *
     * @param reqId 告警序列号
     */
    public CommonACKMsgVO buildHeartReqMsg(int reqId) {
        return commonBuildReqMsg(
                "reqHeartBeat;reqId=" + Integer.valueOf(reqId).toString(),
                CommonACKMsgVO.ACKHEARTBEAT);

    }

    /**
     * 拼接ackLoginAlarm登陆反馈消息
     *
     * @param loginAct 是否登陆成功
     * @param result   登陆信息
     */
    public CommonACKMsgVO buildLoginReqMsg(Boolean loginAct, String result) {
        return commonBuildReqMsg("ackLoginAlarm;result=" + (loginAct ? "succ" : "fail") +
                ";resDesc=" + result, CommonACKMsgVO.ACKLOGINALARM);

    }

    /**
     * 拼接realTimeAlarm告警消息
     *
     * @param msg 告警信息
     */
    public CommonACKMsgVO buildAlarmReqMsg(String msg) {
        return commonBuildReqMsg(msg, CommonACKMsgVO.REALTIMEALARM);

    }

    /**
     * 拼接消息头和时间戳
     *
     * @param msgBodyStr 消息体
     * @param type       消息类型
     * @return 消息体
     */
    public CommonACKMsgVO commonBuildReqMsg(String msgBodyStr, int type) {
        CommonACKMsgVO reqLoginMsg = new CommonACKMsgVO();
        reqLoginMsg.setMsgBody(msgBodyStr);
        reqLoginMsg.setMsgType(type);
        int timeStamp = (int) (System.currentTimeMillis() / 1000);
        reqLoginMsg.setTimeStamp(timeStamp);
        return reqLoginMsg;

    }
}

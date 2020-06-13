package priv.suki.util.i1;


import priv.suki.controller.ContralCenter;
import priv.suki.util.Propert;
import priv.suki.util.StringUtil;
import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.alibaba.fastjson.JSONObject;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * OMC北向接口工具类
 *
 * @author 花小琪
 * @version 1.0
 */
public class I1Util {
    private static Log log = LogFactory.getLog(I1Util.class);
    // 锁对象
    private final Object sendLock = new Object();
    private final Object receiveLock = new Object();

    /**
     * NorthUtil构造
     */
    public I1Util() {

    }

    /**
     * 读取OMC北向接口的消息流
     *
     * @return 消息对象
     * @throws Exception 接收异常
     */
    public I1CommonACKMsgVO receive(BufferedInputStream buis) throws Exception {
        return receive(buis, "utf-8");
    }

    /**
     * 读取OMC北向接口的消息流
     *
     * @return 消息对象
     * @throws Exception 接收异常
     */
    public I1CommonACKMsgVO receive(BufferedInputStream buis, String charSet) throws Exception {
        I1CommonACKMsgVO msg = new I1CommonACKMsgVO();
        DataInputStream dis;
        DataInputStream disHead = null;
        ByteInputStream byis = null;
        byte[] headByte;
        synchronized (receiveLock) {
            if (buis == null) {
                log.error("传入的输入流参数为null");
            } else {
                dis = new DataInputStream(buis);
                headByte = new byte[16];
                try {
                    dis.readFully(headByte);
                    byis = new ByteInputStream(headByte, 0, headByte.length);

                    /* 开始标志：固定为0xFFFF，消息开始标识。 */
                    disHead = new DataInputStream(byis);
                    short startsign = disHead.readShort();
                    if (startsign == I1CommonACKMsgVO.startSign) {

                        /* 秒时间戳,字节整型数*/
                        int timestamp = disHead.readInt();
						/*
						/* 毫秒时间戳,字节整型数*/
                        int timestamp_ms = disHead.readShort();
                        /*
                         * 消息类型：单字节整型数，类型编码含义如下： 0:realTimeAlarm 1:reqLoginAlarm 2:ackLoginAlarm
                         * 3:reqSyncAlarmMsg 4:ackSyncAlarmMsg 5:reqSyncAlarmFile 6:ackSyncAlarmFile
                         * 7:ackSyncAlarmFileResult 8:reqHeartBeat 9:ackHeartBeat 10:closeConnAlarm
                         */
                        int type = disHead.readShort();
                        /*消息格式*/
                        int format = disHead.readShort();
                        /*备用*/
                        int reserve = disHead.readShort();
                        /* 长度,字节整型数，字节顺序为Big-Endian，表示消息体字节长度，取值范围0~32767。 */
                        int bodylength = disHead.readShort();
//                        log.info("[startsign]" + startsign + "[timestamp]" + timestamp + "[timestamp_ms]" + timestamp_ms + "[type]" + type + "[format]" + format + "[reserve]" + reserve + "[bodylength]" + bodylength);
                        headByte = new byte[bodylength];
                        dis.readFully(headByte);
                        String bodymsg = new String(headByte, charSet);
                        msg.setReserve(reserve);
                        msg.setMsgFormat(format);
                        msg.setTimeStamp_ms(timestamp_ms);
                        msg.setTimeStamp(timestamp);
                        msg.setMsgType(type);
                        msg.setMsgBody(bodymsg);
                    } else {
                        log.error("读取到的不是消息开始的标记");
                        msg.setMsgType(-1);
                        msg.setDesc("读取到的不是消息开始的标记");
                    }
                } catch (Exception e) {
                    log.error("读取异常", e);
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

    public boolean send(BufferedOutputStream bos, I1CommonACKMsgVO msg) throws Exception {
        boolean sendsucc = true;
        synchronized (sendLock) {
            if (bos == null) {
                log.error("传入的输入流参数为null");
                return false;
            } else {
                try (ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream(16); DataOutputStream oos = new DataOutputStream(byteOutStream)) {
                    oos.writeShort(I1CommonACKMsgVO.startSign);
                    oos.writeInt(msg.getTimeStamp());
                    oos.writeShort(msg.getTimeStamp_ms());
                    oos.writeShort(msg.getMsgType());
                    oos.writeShort(msg.getMsgFormat());
                    oos.writeShort(msg.getReserve());
                    oos.writeShort(msg.getMsgBody().getBytes(Propert.getPropert().getCharset()).length);
                    bos.write(byteOutStream.toByteArray());
                    bos.write(msg.getMsgBody().getBytes(Propert.getPropert().getCharset()));
                    bos.flush();
                } catch (Exception e) {
                    throw new Exception(e);
                }

            }
        }

        return sendsucc;
    }


    private Map<String, String> changemsg2map(String bodymsg) {
        Map<String, String> msgs = new HashMap<>();
        if (!StringUtil.isBlank(bodymsg)) {
            JSONObject jsonObject = JSONObject.parseObject(bodymsg.trim());
            for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
                msgs.put(entry.getKey(), entry.getValue().toString());
            }
        } else {
            log.error("传入的参数为空");
            return null;
        }
        return msgs;
    }

    /**
     * 判断登陆消息体
     *
     * @throws Exception 判断异常
     */
    public boolean parseRecCom(I1CommonACKMsgVO msg, BufferedOutputStream bos) throws Exception {
        Map<String, String> msgs = changemsg2map(msg.getMsgBody());
        if (msgs == null || msgs.isEmpty()) {
            return false;
        }
        if (msgs.containsKey("user") && msgs.containsKey("passwd")) {
            String username = msgs.get("user");

            if (!StringUtil.isBlank(username)) {
                if (!username.trim().equalsIgnoreCase(Propert.getPropert().getI1_alarm_user())) {
                    log.error("用户名不存在");
                    send(bos, buildLoginReqMsg(false, "username does not exist"));
                    return false;
                }
            }

            String pwd = msgs.get("passwd");
            if (!StringUtil.isBlank(pwd)) {
                if (!pwd.trim().equalsIgnoreCase(Propert.getPropert().getI1_alarm_pwd())) {
                    log.error("密码不正确");
                    send(bos, buildLoginReqMsg(false, "Incorrect password"));
                    return false;
                }

            }
            log.info("发送登陆成功反馈消息");
            send(bos, buildLoginReqMsg(true, "Incorrect message body"));
            return true;
        } else {
            log.error("消息体不正确，没有user或passwd参数");
            send(bos, buildLoginReqMsg(false, "Incorrect message body"));
            return false;
        }
    }

    /**
     * 判断同步告警消息体
     */
    public boolean parseSyncCom(I1CommonACKMsgVO msg, BufferedOutputStream bos) throws Exception {
        Map<String, String> msgs = changemsg2map(msg.getMsgBody());
        if (msgs == null || msgs.isEmpty()) {
            return false;
        }
        if (msgs.containsKey("from_seq_no")) {
            if (!StringUtil.isBlank(msgs.get("from_seq_no"))) {
                int alarmSeq = Integer.parseInt(msgs.get("from_seq_no"));
                ContralCenter.getContral().setNorth_sync_switch(true);
                ContralCenter.getContral().setNorth_sync_number(alarmSeq);
            } else {
                log.error("收到的告警同步消息不正确，from_seq_no为空");
                return false;
            }
        } else {
            log.error("收到的告警同步消息不正确，没有from_seq_no");
            return false;
        }
        return false;
    }

    /**
     * 判断心跳消息体
     */
    public boolean parseHeartCom(BufferedOutputStream bos) throws Exception {
        send(bos, buildHeartReqMsg());
        log.info("心跳已发送");
        return true;
    }


    /**
     * 拼接ackLoginAlarm心跳反馈消息
     */
    public I1CommonACKMsgVO buildHeartReqMsg() {
        return commonBuildReqMsg("",
                I1CommonACKMsgVO.I1HEARTBEAT);

    }

    /**
     * 拼接ackLoginAlarm登陆反馈消息
     *
     * @param loginAct 是否登陆成功
     * @param result   登陆信息
     */
    public I1CommonACKMsgVO buildLoginReqMsg(Boolean loginAct, String result) {
        return commonBuildReqMsg("{\"is_success\" : " + loginAct +
                ",\"server_time\" : " + System.currentTimeMillis() + ",\"heart_beat_period\" : " +
                Propert.getPropert().getI1_heart_interval() + (loginAct == true ? ",\"info\" : \"Info=Login Fail.\"} " : ",\"info\" : \"Info=Login Ok.\"} "), I1CommonACKMsgVO.I1ACKLOGINALARM);

    }

    /**
     * 拼接realTimeAlarm告警消息
     *
     * @param msg 告警信息
     */
    public I1CommonACKMsgVO buildAlarmReqMsg(String msg) {
        return commonBuildReqMsg(msg, I1CommonACKMsgVO.I1ALARMMSG);

    }

    /**
     * 拼接realTimeAlarm告警消息
     */
    public I1CommonACKMsgVO buildSyncStartMsg() {
        return commonBuildReqMsg("{\"sync_start\" : \"BEGIN ACT ALM\"}", I1CommonACKMsgVO.I1ALARMSTART);
    }

    /**
     * 拼接realTimeAlarm告警消息
     */
    public I1CommonACKMsgVO buildSyncEndMsg() {
        return commonBuildReqMsg("{\"sync_end\" : \"END ACT ALM\"}", I1CommonACKMsgVO.I1ALARMEND);
    }

    /**
     * 拼接消息头和时间戳
     */
    public I1CommonACKMsgVO commonBuildReqMsg(String msgBodyStr, int type) {
        I1CommonACKMsgVO reqLoginMsg = new I1CommonACKMsgVO();
        reqLoginMsg.setMsgBody(msgBodyStr);
        reqLoginMsg.setMsgType(type);
        long timeStamp = System.currentTimeMillis();
        reqLoginMsg.setTimeStamp_ms((int) (timeStamp / 1000));
        reqLoginMsg.setTimeStamp((int) (timeStamp % 1000));
        reqLoginMsg.setMsgFormat(0x2);
        return reqLoginMsg;

    }
}

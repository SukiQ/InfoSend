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
 * OMC����ӿڹ�����
 *
 * @author ��С��
 * @version 1.0
 */
public class I1Util {
    private static Log log = LogFactory.getLog(I1Util.class);
    // ������
    private final Object sendLock = new Object();
    private final Object receiveLock = new Object();

    /**
     * NorthUtil����
     */
    public I1Util() {

    }

    /**
     * ��ȡOMC����ӿڵ���Ϣ��
     *
     * @return ��Ϣ����
     * @throws Exception �����쳣
     */
    public I1CommonACKMsgVO receive(BufferedInputStream buis) throws Exception {
        return receive(buis, "utf-8");
    }

    /**
     * ��ȡOMC����ӿڵ���Ϣ��
     *
     * @return ��Ϣ����
     * @throws Exception �����쳣
     */
    public I1CommonACKMsgVO receive(BufferedInputStream buis, String charSet) throws Exception {
        I1CommonACKMsgVO msg = new I1CommonACKMsgVO();
        DataInputStream dis;
        DataInputStream disHead = null;
        ByteInputStream byis = null;
        byte[] headByte;
        synchronized (receiveLock) {
            if (buis == null) {
                log.error("���������������Ϊnull");
            } else {
                dis = new DataInputStream(buis);
                headByte = new byte[16];
                try {
                    dis.readFully(headByte);
                    byis = new ByteInputStream(headByte, 0, headByte.length);

                    /* ��ʼ��־���̶�Ϊ0xFFFF����Ϣ��ʼ��ʶ�� */
                    disHead = new DataInputStream(byis);
                    short startsign = disHead.readShort();
                    if (startsign == I1CommonACKMsgVO.startSign) {

                        /* ��ʱ���,�ֽ�������*/
                        int timestamp = disHead.readInt();
						/*
						/* ����ʱ���,�ֽ�������*/
                        int timestamp_ms = disHead.readShort();
                        /*
                         * ��Ϣ���ͣ����ֽ������������ͱ��뺬�����£� 0:realTimeAlarm 1:reqLoginAlarm 2:ackLoginAlarm
                         * 3:reqSyncAlarmMsg 4:ackSyncAlarmMsg 5:reqSyncAlarmFile 6:ackSyncAlarmFile
                         * 7:ackSyncAlarmFileResult 8:reqHeartBeat 9:ackHeartBeat 10:closeConnAlarm
                         */
                        int type = disHead.readShort();
                        /*��Ϣ��ʽ*/
                        int format = disHead.readShort();
                        /*����*/
                        int reserve = disHead.readShort();
                        /* ����,�ֽ����������ֽ�˳��ΪBig-Endian����ʾ��Ϣ���ֽڳ��ȣ�ȡֵ��Χ0~32767�� */
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
                        log.error("��ȡ���Ĳ�����Ϣ��ʼ�ı��");
                        msg.setMsgType(-1);
                        msg.setDesc("��ȡ���Ĳ�����Ϣ��ʼ�ı��");
                    }
                } catch (Exception e) {
                    log.error("��ȡ�쳣", e);
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
                log.error("���������������Ϊnull");
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
            log.error("����Ĳ���Ϊ��");
            return null;
        }
        return msgs;
    }

    /**
     * �жϵ�½��Ϣ��
     *
     * @throws Exception �ж��쳣
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
                    log.error("�û���������");
                    send(bos, buildLoginReqMsg(false, "username does not exist"));
                    return false;
                }
            }

            String pwd = msgs.get("passwd");
            if (!StringUtil.isBlank(pwd)) {
                if (!pwd.trim().equalsIgnoreCase(Propert.getPropert().getI1_alarm_pwd())) {
                    log.error("���벻��ȷ");
                    send(bos, buildLoginReqMsg(false, "Incorrect password"));
                    return false;
                }

            }
            log.info("���͵�½�ɹ�������Ϣ");
            send(bos, buildLoginReqMsg(true, "Incorrect message body"));
            return true;
        } else {
            log.error("��Ϣ�岻��ȷ��û��user��passwd����");
            send(bos, buildLoginReqMsg(false, "Incorrect message body"));
            return false;
        }
    }

    /**
     * �ж�ͬ���澯��Ϣ��
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
                log.error("�յ��ĸ澯ͬ����Ϣ����ȷ��from_seq_noΪ��");
                return false;
            }
        } else {
            log.error("�յ��ĸ澯ͬ����Ϣ����ȷ��û��from_seq_no");
            return false;
        }
        return false;
    }

    /**
     * �ж�������Ϣ��
     */
    public boolean parseHeartCom(BufferedOutputStream bos) throws Exception {
        send(bos, buildHeartReqMsg());
        log.info("�����ѷ���");
        return true;
    }


    /**
     * ƴ��ackLoginAlarm����������Ϣ
     */
    public I1CommonACKMsgVO buildHeartReqMsg() {
        return commonBuildReqMsg("",
                I1CommonACKMsgVO.I1HEARTBEAT);

    }

    /**
     * ƴ��ackLoginAlarm��½������Ϣ
     *
     * @param loginAct �Ƿ��½�ɹ�
     * @param result   ��½��Ϣ
     */
    public I1CommonACKMsgVO buildLoginReqMsg(Boolean loginAct, String result) {
        return commonBuildReqMsg("{\"is_success\" : " + loginAct +
                ",\"server_time\" : " + System.currentTimeMillis() + ",\"heart_beat_period\" : " +
                Propert.getPropert().getI1_heart_interval() + (loginAct == true ? ",\"info\" : \"Info=Login Fail.\"} " : ",\"info\" : \"Info=Login Ok.\"} "), I1CommonACKMsgVO.I1ACKLOGINALARM);

    }

    /**
     * ƴ��realTimeAlarm�澯��Ϣ
     *
     * @param msg �澯��Ϣ
     */
    public I1CommonACKMsgVO buildAlarmReqMsg(String msg) {
        return commonBuildReqMsg(msg, I1CommonACKMsgVO.I1ALARMMSG);

    }

    /**
     * ƴ��realTimeAlarm�澯��Ϣ
     */
    public I1CommonACKMsgVO buildSyncStartMsg() {
        return commonBuildReqMsg("{\"sync_start\" : \"BEGIN ACT ALM\"}", I1CommonACKMsgVO.I1ALARMSTART);
    }

    /**
     * ƴ��realTimeAlarm�澯��Ϣ
     */
    public I1CommonACKMsgVO buildSyncEndMsg() {
        return commonBuildReqMsg("{\"sync_end\" : \"END ACT ALM\"}", I1CommonACKMsgVO.I1ALARMEND);
    }

    /**
     * ƴ����Ϣͷ��ʱ���
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

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
 * OMC����ӿڹ�����
 *
 * @author ��С��
 * @version 1.0
 */
public class NorthUtil {
    private static final Log log = LogFactory.getLog(NorthUtil.class);
    // ������
    private final Object sendLock = new Object();
    private final Object receiveLock = new Object();

    /**
     * NorthUtil����
     */
    public NorthUtil() {

    }

    /**
     * ��ȡOMC����ӿڵ���Ϣ��
     *
     * @param buis ��Ϣ��
     * @return ��Ϣ����
     */
    public CommonACKMsgVO receive(BufferedInputStream buis) throws Exception {
        return receive(buis, "utf-8");
    }

    /**
     * ��ȡOMC����ӿڵ���Ϣ��
     *
     * @param buis    ��Ϣ��
     * @param charSet �ַ�����
     * @return ��Ϣ����
     */
    public CommonACKMsgVO receive(BufferedInputStream buis, String charSet) throws Exception {
        CommonACKMsgVO msg = new CommonACKMsgVO();
        DataInputStream dis;
        DataInputStream disHead = null;
        ByteInputStream byis = null;
        byte[] headByte;
        synchronized (receiveLock) {
            if (buis == null) {
                log.error("���������������Ϊnull");
            } else {
                dis = new DataInputStream(buis);
                headByte = new byte[9];
                try {
                    // OMC����ӿ���Ϣ��������Ϣͷ����Ϣ����ɡ���Ϣͷ��9���ֽڣ�byte����ʾ ��Ϣͷ�ɿ�ʼ��־��2��-��Ϣ���ͣ�1��-��ʱ�����4��-���ȣ�2��//
                    dis.readFully(headByte);
                    byis = new ByteInputStream(headByte, 0, headByte.length);

                    /* ��ʼ��־���̶�Ϊ0xFFFF����Ϣ��ʼ��ʶ�� */
                    disHead = new DataInputStream(byis);
                    short startsign = disHead.readShort();
                    if (startsign == CommonACKMsgVO.START_SIGN) {
                        /*
                         * ��Ϣ���ͣ����ֽ������������ͱ��뺬�����£� 0:realTimeAlarm 1:reqLoginAlarm 2:ackLoginAlarm
                         * 3:reqSyncAlarmMsg 4:ackSyncAlarmMsg 5:reqSyncAlarmFile 6:ackSyncAlarmFile
                         * 7:ackSyncAlarmFileResult 8:reqHeartBeat 9:ackHeartBeat 10:closeConnAlarm
                         */
                        int type = disHead.readByte();
                        /* ��ʱ���,�ֽ����������ֽ�˳��ΪBig-Endian����ʾ��Ϣ����ʱ��,Ϊ����1970-01-01 00:00:00ʱ��ƫ�Ƶ������� */
                        int timestamp = disHead.readInt();
                        /* ����,�ֽ����������ֽ�˳��ΪBig-Endian����ʾ��Ϣ���ֽڳ��ȣ�ȡֵ��Χ0~32767�� */
                        int bodylength = disHead.readShort();
                        headByte = new byte[bodylength];
                        dis.readFully(headByte);
                        String bodymsg = new String(headByte, charSet);
                        msg.setTimeStamp(timestamp * 1000);
                        msg.setMsgType(type);
                        msg.setMsgBody(bodymsg);
                    } else {
                        log.error("��ȡ���Ĳ�����Ϣ��ʼ�ı��");
                        msg.setMsgType(-1);
                        msg.setDesc("��ȡ���Ĳ�����Ϣ��ʼ�ı��");
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
                log.error("���������������Ϊnull");
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
                    log.error("�����쳣", e);
                    throw new Exception(e);
                }

            }
        }

        return sendsucc;
    }

    /**
     * ������Ӧ��Ϣ�壬����Ϣ�Լ�ֵ�Դ��
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
                        log.error("����ȷ����Ӧ��Ϣ��ʽ:" + bodymsg);
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
            log.error("����Ĳ���Ϊ��");
            return null;
        }
        return msgs;
    }

    /**
     * �жϵ�½��Ϣ��
     *
     * @param msg ��Ϣ
     * @param bos �����
     */
    public boolean parseRecCom(CommonACKMsgVO msg, BufferedOutputStream bos) throws Exception {
        Map<String, String> msgs = changemsg2map(msg.getMsgBody());
        if (msgs.containsKey("user") && msgs.containsKey("key")) {
            String username = msgs.get("user");

            if (!StringUtil.isBlank(username)) {
                if (!username.trim().equalsIgnoreCase(Propert.getPropert().getNorth_alarm_user())) {
                    log.error("�û���������");
                    send(bos, buildLoginReqMsg(false, "username does not exist"));
                    return false;
                }
            }

            String pwd = msgs.get("key");
            if (!StringUtil.isBlank(pwd)) {
                if (!pwd.trim().equalsIgnoreCase(Propert.getPropert().getNorth_alarm_pwd())) {
                    log.error("���벻��ȷ");
                    send(bos, buildLoginReqMsg(false, "Incorrect password"));
                    return false;
                }

            }
            log.info("���͵�½�ɹ�������Ϣ");
            send(bos, buildLoginReqMsg(true, "Incorrect message body"));
            return true;
        } else {
            log.error("��Ϣ�岻��ȷ��û��user��key����");
            send(bos, buildLoginReqMsg(false, "Incorrect message body"));
            return false;
        }
    }

    /**
     * �ж�ͬ���澯��Ϣ��
     *
     * @param msg msg
     * @param bos �����
     */
    public boolean parseSyncCom(CommonACKMsgVO msg, BufferedOutputStream bos) throws Exception {
        Map<String, String> msgs = changemsg2map(msg.getMsgBody());
        int reqId;
        if (msgs.containsKey("reqId")) {
            if (!StringUtil.isBlank(msgs.get("reqId"))) {
                reqId = Integer.parseInt(msgs.get("reqId"));
            } else {
                log.error("�յ��ĸ澯ͬ����Ϣ����ȷ��reqIdΪ��");
                return false;
            }
        } else {
            log.error("�յ��ĸ澯ͬ����Ϣ����ȷ��û��reqId");
            return false;
        }
        if (msgs.containsKey("alarmSeq")) {
            if (!StringUtil.isBlank(msgs.get("alarmSeq"))) {
                int alarmSeq = Integer.parseInt(msgs.get("alarmSeq"));
                ContralCenter.getContral().setNorth_sync_switch(true);
                ContralCenter.getContral().setNorth_sync_number(alarmSeq);
                send(bos, buildSyncReqMsg(reqId));
            } else {
                log.error("�յ��ĸ澯ͬ����Ϣ����ȷ��alarmSeqΪ��");
                return false;
            }
        } else {
            log.error("�յ��ĸ澯ͬ����Ϣ����ȷ��û��alarmSeq");
            return false;
        }
        return false;
    }

    /**
     * �ж�������Ϣ��
     */
    public boolean parseHeartCom(CommonACKMsgVO msg, BufferedOutputStream bos) throws Exception {
        Map<String, String> msgs = changemsg2map(msg.getMsgBody());
        if (msgs.containsKey("reqId")) {
            if (!StringUtil.isBlank(msgs.get("reqId"))) {
                int reqId = Integer.parseInt(msgs.get("reqId"));
                send(bos, buildHeartReqMsg(reqId));
                return true;
            } else {
                log.error("�յ���������Ϣ����ȷ��reqIdΪ��");
            }
        }
        log.error("�յ���������Ϣ����ȷ��û��reqId");
        return false;

    }


    /**
     * ƴ��ackSyncAlarmMsgͬ����Ϣ������Ϣ
     *
     * @param reqId �澯���к�
     */
    public CommonACKMsgVO buildSyncReqMsg(int reqId) {
        return commonBuildReqMsg("ackSyncAlarmMsg;reqId=" + Integer.valueOf(reqId).toString() +
                ";result=succ;resDesc=null", CommonACKMsgVO.ACKSYNCALARMMSG);

    }

    /**
     * ƴ��ackLoginAlarm����������Ϣ
     *
     * @param reqId �澯���к�
     */
    public CommonACKMsgVO buildHeartReqMsg(int reqId) {
        return commonBuildReqMsg(
                "reqHeartBeat;reqId=" + Integer.valueOf(reqId).toString(),
                CommonACKMsgVO.ACKHEARTBEAT);

    }

    /**
     * ƴ��ackLoginAlarm��½������Ϣ
     *
     * @param loginAct �Ƿ��½�ɹ�
     * @param result   ��½��Ϣ
     */
    public CommonACKMsgVO buildLoginReqMsg(Boolean loginAct, String result) {
        return commonBuildReqMsg("ackLoginAlarm;result=" + (loginAct ? "succ" : "fail") +
                ";resDesc=" + result, CommonACKMsgVO.ACKLOGINALARM);

    }

    /**
     * ƴ��realTimeAlarm�澯��Ϣ
     *
     * @param msg �澯��Ϣ
     */
    public CommonACKMsgVO buildAlarmReqMsg(String msg) {
        return commonBuildReqMsg(msg, CommonACKMsgVO.REALTIMEALARM);

    }

    /**
     * ƴ����Ϣͷ��ʱ���
     *
     * @param msgBodyStr ��Ϣ��
     * @param type       ��Ϣ����
     * @return ��Ϣ��
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

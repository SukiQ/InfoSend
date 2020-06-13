package priv.suki.util;

import priv.suki.msg.*;


/**
 * 接口信息整合
 *
 * @author hxq
 * @version 1.0.4
 */
public enum InterfaceEnum {

    /**
     * SOCKET服务端
     */
    SOCKET_SERVER("SOCKET服务端", "socket_server", StringInfo.class, priv.suki.send.SOCKET_SERVER.class),
    /**
     * SOCKET客户端
     */
    SOCKET_CLIENT("SOCKET客户端", "socket_client", StringInfo.class, priv.suki.send.SOCKET_CLIENT.class),
    /**
     * SNMP
     */
    SNMP("SNMP", "snmp", SNMPInfo.class, priv.suki.send.SNMPSend.class),
    /**
     * Telnet
     */
    TELNET("Telnet", "telnet", StringInfo.class, priv.suki.send.TelnetSend.class),
    /**
     * IBMMQ
     */
    IBMMQ("IBMMQ", "ibmmq", StringInfo.class, priv.suki.send.IBMMQSend.class),
    /**
     * ActiveMQ
     */
    ACTIVEMQ("ActiveMQ", "activemq", StringInfo.class, priv.suki.send.ActiveMQSend.class),
    /**
     * OMC北向告警
     */
    OMC("OMC北向告警", "omc", OrgInfo.class, priv.suki.send.NorthAlarmSend.class),
    /**
     * Kafka
     */
    KAFKA("Kafka", "kafka", StringInfo.class, priv.suki.send.KafkaSend.class),
    /**
     * 电信I1告警
     */
    I1("电信I1告警", "i1", I1AlarmInfo.class, priv.suki.send.I1AlarmSend.class),
    /**
     * SOCKET客户端
     */
    CUCC("CUCC告警", "cucc", CUCCInfo.class, priv.suki.send.CUCCAlarmSend.class),
    /**
     * CUCC告警
     */
    SYSLOG("Syslog", "syslog", StringInfo.class, priv.suki.send.SyslogSend.class);

    private final String cname;
    private final String name;
    private final Class infoType;
    private final Class sendType;

    InterfaceEnum(String cname, String name, Class infoType, Class sendType) {
        this.cname = cname;
        this.name = name;
        this.infoType = infoType;
        this.sendType = sendType;
    }

    public String getCName() {
        return cname;
    }

    public String getName() {
        return name;
    }

    public Class getInfoType() {
        return infoType;
    }

    public Class getSendType() {
        return sendType;
    }

    /**
     * 通过value取枚举
     * * @param value
     * * @return enum
     */
    public static InterfaceEnum getTypeByCname(String value) {
        if (StringUtil.isBlank(value)) {
            return null;
        }

        for (InterfaceEnum enums : InterfaceEnum.values()) {
            if (value.equals(enums.getCName())) {
                return enums;
            }
        }
        return null;
    }

    public static InterfaceEnum getTypeByName(String value) {
        if (StringUtil.isBlank(value)) {
            return null;
        }

        for (InterfaceEnum enums : InterfaceEnum.values()) {
            if (value.equals(enums.getName())) {
                return enums;
            }
        }
        return null;
    }

    public static void main(String[] args) {
        System.out.println(getTypeByName("cucc"));
    }

}

package priv.suki.controller;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

import com.alibaba.fastjson.parser.Feature;
import priv.suki.controller.CmdMonitor;
import priv.suki.controller.ContralCenter;
import priv.suki.process.Judge;
import priv.suki.util.FileUtil;
import priv.suki.msg.SNMPBindVar;
import priv.suki.util.InterfaceEnum;
import priv.suki.util.Propert;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.alibaba.fastjson.JSONObject;

/**
 * Linux��ʼ������
 *
 * @author ��С��
 * @version 1.0.2
 */
public class SetParamForLinux {
    private static final Log log = LogFactory.getLog(SetParamForLinux.class);

    /**
     * Linux��ʼ������
     */
    public static void setParam() {
        Properties properties = new Properties();
        InputStream inputStream;
        try {
            log.info("��ʼ��ȡ�����ļ�");
            inputStream = new BufferedInputStream(new FileInputStream(
                    ".." + Propert.SYS_FILE_SPARATOR + "cfg" + Propert.SYS_FILE_SPARATOR + "infosend.properties"));
            properties.load(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            initParam(properties);

        } catch (Exception e) {
            log.error("��ʼ�������ļ��쳣,�����˳�", e);
            System.exit(0);
        }

        try {
            log.info("��ʼ����command��ʱ�ļ�");
            FileUtil.initFileAndCreate(".." + Propert.SYS_FILE_SPARATOR + "data" + Propert.SYS_FILE_SPARATOR + "cmd.tmp", "parse=false\ncut=false");
        } catch (Exception e) {
            log.error("����command��ʱ�ļ��쳣,�����˳�", e);
            System.exit(0);
        }

        /*�����������߳�*/
        Thread cmdThread = new Thread(new CmdMonitor());
        cmdThread.start();

        try {
            ContralCenter.getContral().setNoSend(false);
        } catch (Exception e) {
            log.error("��������ʧ��", e);
            System.exit(0);
        }
    }

    private static void initParam(Properties properties) throws Exception {



        /* �������� */
        ContralCenter.getContral().setParam("msg", "string", properties.getProperty("msg"),
                Propert.PARAM_CANT_BE_EMPTY);
        ContralCenter.getContral().setParam("charset", "string", properties.getProperty("charset"),
                Propert.PARAM_CAN_BE_EMPTY);

        /* ���÷������� */
        ContralCenter.getContral().setSendType(properties.getProperty("sendtype"));

        /*
         * ��ƴ��/ƴ��ģʽ
         */
        String buildsend;
        InterfaceEnum inf = InterfaceEnum.getTypeByName(properties.getProperty("interfacetype"));
        if (inf == null) {
            throw new Exception("interfacetype�ӿڲ������ô���");
        }

        if (Judge.isSpecialInterface(inf)) {
            log.info("����ӿڲ�����ƴ��ģʽ");
            buildsend = "false";
        } else {
            buildsend = properties.getProperty("buildsend");
            log.info("ƴ��ģʽ��" + ("true".equals(buildsend) ? "����" : "�ر�"));
        }

        ContralCenter.getContral().setParam("buildsend", "boolean", buildsend,
                Propert.PARAM_CAN_BE_EMPTY);

        /* ����ģʽ */
        ContralCenter.getContral().setParam("fastmodel", "boolean", properties.getProperty("fastmodel"),
                Propert.PARAM_CAN_BE_EMPTY);

        /* ˳���� */
        if (ContralCenter.getContral().getSendType() == Propert.NORMAL_SEND) {
            ContralCenter.getContral().setParam("rate", "int", properties.getProperty("rate"),
                    Propert.PARAM_CANT_BE_EMPTY, properties.getProperty("rate_unit"));
            ContralCenter.getContral().setParam("duration", "int", properties.getProperty("duration"),
                    Propert.PARAM_CAN_BE_EMPTY, properties.getProperty("duration_unit"));
        }

        /* �籩���� */
        if (ContralCenter.getContral().getSendType() == Propert.STORM_SEND) {
            ContralCenter.getContral().setParam("rate", "int", properties.getProperty("steady_rate"),
                    Propert.PARAM_CANT_BE_EMPTY, properties.getProperty("steady_unit"));
            ContralCenter.getContral().setParam("duration", "int", properties.getProperty("steady_duration"),
                    Propert.PARAM_CANT_BE_EMPTY, properties.getProperty("steady_duration_unit"));
            ContralCenter.getContral().setParam("storm_rate", "int", properties.getProperty("storm_rate"),
                    Propert.PARAM_CANT_BE_EMPTY, properties.getProperty("storm_unit"));
            ContralCenter.getContral().setParam("storm_duration", "int", properties.getProperty("storm_duration"),
                    Propert.PARAM_CANT_BE_EMPTY, properties.getProperty("storm_duration_unit"));

        }
        /* ָ���������� */
        if (ContralCenter.getContral().getSendType() == Propert.PARTITION_SEND) {
            ContralCenter.getContral().setParam("rate", "int", properties.getProperty("kakfa_rate"),
                    Propert.PARAM_CANT_BE_EMPTY, properties.getProperty("kakfa_rate_unit"));
            ContralCenter.getContral().setParam("duration", "int", properties.getProperty("kakfa_duration"),
                    Propert.PARAM_CAN_BE_EMPTY, properties.getProperty("kakfa_duration_unit"));
            ContralCenter.getContral().setParam("send_partition", "int", properties.getProperty("send_partition"),
                    Propert.PARAM_CAN_BE_EMPTY);
        }

        //���ýӿ�����
        ContralCenter.getContral().setSelInterface(inf);

        /* socket��������� */
        if (inf == InterfaceEnum.SOCKET_SERVER) {
            log.info("����Ϊ��Socket�����ģ��");
            ContralCenter.getContral().setParam("socket_server_port", "int",
                    properties.getProperty("socket_server_port"), Propert.PARAM_CANT_BE_EMPTY);
            return;
        }

        /* socket�ͻ������� */
        if (inf == InterfaceEnum.SOCKET_CLIENT) {
            log.info("����Ϊ��Socket�ͻ���ģ��");
            ContralCenter.getContral().setParam("socket_client_ip", "string",
                    properties.getProperty("socket_client_ip"), Propert.PARAM_CANT_BE_EMPTY);
            ContralCenter.getContral().setParam("socket_client_port", "int",
                    properties.getProperty("socket_client_port"), Propert.PARAM_CANT_BE_EMPTY);
            return;
        }

        /* kafka�ӿ����� */
        if (inf == InterfaceEnum.KAFKA) {
            log.info("����Ϊ��Kafka�ӿ�ģ��");
            ContralCenter.getContral().setParam("zookUrl", "string", properties.getProperty("zookUrl"),
                    Propert.PARAM_CAN_BE_EMPTY);
            ContralCenter.getContral().setParam("kafkaUrl", "string", properties.getProperty("kafkaUrl"),
                    Propert.PARAM_CAN_BE_EMPTY);
            ContralCenter.getContral().setParam("topic", "string", properties.getProperty("topic"),
                    Propert.PARAM_CANT_BE_EMPTY);
            ContralCenter.getContral().setParam("paritition", "int", properties.getProperty("paritition"),
                    Propert.PARAM_CAN_BE_EMPTY);
            return;
        }

        /* OMC����ӿ����� */
        if (inf == InterfaceEnum.OMC) {
            log.info("����Ϊ��OMC����ӿ�ģ��");
            ContralCenter.getContral().setParam("north_alarm_user", "string",
                    properties.getProperty("north_alarm_user"), Propert.PARAM_CANT_BE_EMPTY);
            ContralCenter.getContral().setParam("north_alarm_pwd", "string", properties.getProperty("north_alarm_pwd"),
                    Propert.PARAM_CANT_BE_EMPTY);
            ContralCenter.getContral().setParam("north_alarm_port", "int", properties.getProperty("north_alarm_port"),
                    Propert.PARAM_CANT_BE_EMPTY);
            ContralCenter.getContral().setParam("alarm_id", "string", properties.getProperty("north_alarm_id"),
                    Propert.PARAM_CANT_BE_EMPTY);

            return;
        }

        /* ����I1�澯�ӿ����� */
        if (inf == InterfaceEnum.I1) {
            log.info("����Ϊ������I1�ӿ�ģ��");
            ContralCenter.getContral().setParam("i1_alarm_user", "string",
                    properties.getProperty("i1_alarm_user"), Propert.PARAM_CANT_BE_EMPTY);
            ContralCenter.getContral().setParam("i1_alarm_pwd", "string", properties.getProperty("i1_alarm_pwd"),
                    Propert.PARAM_CANT_BE_EMPTY);
            ContralCenter.getContral().setParam("i1_alarm_port", "int", properties.getProperty("i1_alarm_port"),
                    Propert.PARAM_CANT_BE_EMPTY);
            ContralCenter.getContral().setParam("i1_alarm_id", "string", properties.getProperty("i1_alarm_id"),
                    Propert.PARAM_CANT_BE_EMPTY);
            ContralCenter.getContral().setParam("i1_heart_interval", "int", properties.getProperty("i1_heart_interval"),
                    Propert.PARAM_CANT_BE_EMPTY);
            return;
        }

        /* SNMP�ӿ����� */
        if (inf == InterfaceEnum.SNMP) {
            log.info("����Ϊ��SNMP�ӿ�ģ��");
            ContralCenter.getContral().setParam("snmp_trapOid", "string", properties.getProperty("snmp_trapOid"),
                    Propert.PARAM_CANT_BE_EMPTY);
            ContralCenter.getContral().setParam("snmp_port", "int", properties.getProperty("snmp_port"),
                    Propert.PARAM_CANT_BE_EMPTY);
            ContralCenter.getContral().setParam("snmp_ip", "String", properties.getProperty("snmp_ip"),
                    Propert.PARAM_CANT_BE_EMPTY);
            ContralCenter.getContral().setParam("snmp_version", "int", properties.getProperty("snmp_version"),
                    Propert.PARAM_CANT_BE_EMPTY);
            ContralCenter.getContral().setParam("snmp_auth", "Boolean", properties.getProperty("snmp_auth"),
                    Propert.PARAM_CAN_BE_EMPTY);
            ContralCenter.getContral().setParam("snmp_userName", "String", properties.getProperty("snmp_userName"),
                    Propert.PARAM_CAN_BE_EMPTY);
            ContralCenter.getContral().setParam("authPass", "string", properties.getProperty("authPass"),
                    Propert.PARAM_CAN_BE_EMPTY);

            /* ���������jsonģʽ����snmp��msg��Ϣ */
            List<SNMPBindVar> snmpMsg = new ArrayList<>();
            JSONObject json = JSONObject.parseObject(properties.getProperty("msg"), Feature.OrderedField);
            for (Entry<String, Object> entry : json.entrySet()) {
                SNMPBindVar snmpBindVar = new SNMPBindVar();
                snmpBindVar.setTrapOid(entry.getKey());
                snmpBindVar.setBindVar((String) entry.getValue());
                snmpBindVar.setTrapType("OctetString");
                snmpMsg.add(snmpBindVar);
            }
            ContralCenter.getContral().setParam("snmp_msg", snmpMsg);
            return;
        }

        /* Telnet�ӿ����� */
        if (inf == InterfaceEnum.TELNET) {
            ContralCenter.getContral().setSelInterface("Telnet");
            ContralCenter.getContral().setParam("telnet_ftp_user", "string", properties.getProperty("telnet_ftp_user"),
                    Propert.PARAM_CANT_BE_EMPTY);
            ContralCenter.getContral().setParam("telnet_ftp_pwd", "string", properties.getProperty("telnet_ftp_pwd"),
                    Propert.PARAM_CANT_BE_EMPTY);
            ContralCenter.getContral().setParam("telnet_ftp_source", "string",
                    properties.getProperty("telnet_ftp_source"), Propert.PARAM_CANT_BE_EMPTY);
            ContralCenter.getContral().setParam("telnet_ftp_ip", "string", properties.getProperty("telnet_ftp_ip"),
                    Propert.PARAM_CANT_BE_EMPTY);
            ContralCenter.getContral().setParam("telnet_ftp_type", "int", properties.getProperty("telnet_ftp_type"),
                    Propert.PARAM_CANT_BE_EMPTY);

            return;
        }

        /* IBMMQ�ӿ����� */
        if (inf == InterfaceEnum.IBMMQ) {
            log.info("����Ϊ��IBMMQ�ӿ�ģ��");
            ContralCenter.getContral().setParam("ibmmq_ip", "string", properties.getProperty("ibmmq_ip"),
                    Propert.PARAM_CANT_BE_EMPTY);
            ContralCenter.getContral().setParam("ibmmq_port", "int", properties.getProperty("ibmmq_port"),
                    Propert.PARAM_CANT_BE_EMPTY);
            ContralCenter.getContral().setParam("ibmmq_channel", "string", properties.getProperty("ibmmq_channel"),
                    Propert.PARAM_CANT_BE_EMPTY);
            ContralCenter.getContral().setParam("ibmmq_qManager", "string", properties.getProperty("ibmmq_qManager"),
                    Propert.PARAM_CANT_BE_EMPTY);
            ContralCenter.getContral().setParam("ibmmq_ccsid", "int", properties.getProperty("ibmmq_ccsid"),
                    Propert.PARAM_CANT_BE_EMPTY);
            ContralCenter.getContral().setParam("ibmmq_qName", "string", properties.getProperty("ibmmq_qName"),
                    Propert.PARAM_CANT_BE_EMPTY);

            return;
        }


        /* ActiveMQ�ӿ����� */
        if (inf == InterfaceEnum.ACTIVEMQ) {
            log.info("����Ϊ��ActiveMQ�ӿ�ģ��");
            ContralCenter.getContral().setParam("actmmq_url", "string", properties.getProperty("actmmq_url"),
                    Propert.PARAM_CANT_BE_EMPTY);
            ContralCenter.getContral().setParam("actmmq_qname", "string", properties.getProperty("actmmq_qname"),
                    Propert.PARAM_CANT_BE_EMPTY);
            ContralCenter.getContral().setParam("actmmq_username", "string", properties.getProperty("actmmq_username"),
                    Propert.PARAM_CANT_BE_EMPTY);
            ContralCenter.getContral().setParam("actmmq_password", "string", properties.getProperty("actmmq_password"),
                    Propert.PARAM_CANT_BE_EMPTY);

            return;
        }

        /* CUCC�澯�ӿ����� */
        if (inf == InterfaceEnum.CUCC) {
            log.info("����Ϊ��CUCC�澯�ӿ�ģ��");
            ContralCenter.getContral().setParam("cucc_alarm_user", "string",
                    properties.getProperty("cucc_alarm_user"), Propert.PARAM_CANT_BE_EMPTY);
            ContralCenter.getContral().setParam("cucc_alarm_pwd", "string", properties.getProperty("cucc_alarm_pwd"),
                    Propert.PARAM_CANT_BE_EMPTY);
            ContralCenter.getContral().setParam("cucc_alarm_port", "int", properties.getProperty("cucc_alarm_port"),
                    Propert.PARAM_CANT_BE_EMPTY);
            return;
        }

        /* Syslog�澯�ӿ����� */
        if (inf == InterfaceEnum.SYSLOG) {
            log.info("����Ϊ��Syslog�澯�ӿ�ģ��");
            ContralCenter.getContral().setParam("syslog_type", "string",
                    properties.getProperty("syslog_type"), Propert.PARAM_CANT_BE_EMPTY);
            ContralCenter.getContral().setParam("syslog_port", "int", properties.getProperty("syslog_port"),
                    Propert.PARAM_CAN_BE_EMPTY);
            ContralCenter.getContral().setParam("syslog_target_host", "string", properties.getProperty("syslog_target_host"),
                    Propert.PARAM_CANT_BE_EMPTY);
            ContralCenter.getContral().setParam("syslog_target_port", "int", properties.getProperty("syslog_target_port"),
                    Propert.PARAM_CANT_BE_EMPTY);
            return;
        }

        throw new Exception("interfacetype������д����ȷ����֧�ָ���ӿ�");
    }
}
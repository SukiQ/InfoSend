package priv.suki.controller;

import java.util.ArrayList;
import java.util.List;

import priv.suki.frame.cuccalarm.CUCCAlarmPage;
import priv.suki.frame.syslog.SyslogPage;
import priv.suki.msg.SNMPBindVar;
import priv.suki.send.SendFactory;
import priv.suki.util.InterfaceEnum;
import priv.suki.util.Propert;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;

import priv.suki.frame.activemq.ActiveMQPage;
import priv.suki.frame.i1alarm.I1AlarmPage;
import priv.suki.frame.ibmmq.IBMMQPage;
import priv.suki.frame.kafka.KafkaPage;
import priv.suki.frame.northalarm.NorthAlarmPage;
import priv.suki.frame.snmp.SNMPPage;
import priv.suki.frame.socketclient.SocketClientPage;
import priv.suki.frame.socketserver.SocketServerPage;
import priv.suki.frame.telnet.TelnetPage;
import priv.suki.util.StringUtil;

/**
 * ��������
 *
 * @author ��С��
 * @version 1.0.0
 */
public class ContralCenter extends Thread {
    private static final Log log = LogFactory.getLog(ContralCenter.class);
    private static final Logger logger = Logger.getLogger("Log4jMain");
    private static final ContralCenter contral = new ContralCenter();
    // ֹͣ/��ʼ
    private boolean noSend = true;
    /**
     * ��ͣ
     */
    private boolean parse = false;
    private static SendFactory sendFactory;
    /**
     * �ܷ��ʹ���
     */
    private int number = -2;
    // �ѷ��ʹ���
    private int sendNum = 0;
    // ��ƴ��ÿ�η�������
    private int nobuildsendNum = 0;
    // ��ƴ��ÿ�η��͵�������
    private int nobuildrate;
    private ProductionThread production;
    // չʾ��־
    private boolean showDynamicLog = false;
    // �ӿ�����
    private InterfaceEnum selInterface;
    // ��������
    private int sendType;
    // ��ʼ�籩����
    private boolean stormSend = false;
    // �籩����׼�����
    private boolean stormReady = false;
    // ��ȡ��Ϣ����
    private boolean cutSwitch = false;
    // OMC������ʷ�澯List
    private List<String> cachelist = new ArrayList<>();
    // OMC��Ϣͬ������
    private boolean north_sync_switch = false;
    // OMC��Ϣͬ�����к�
    private int north_sync_number = 0;

    public ContralCenter() {
        Thread.currentThread().setName("ContralThread");
    }

    @Override
    public void run() {

    }

    /**
     * ���⸳ֵ
     *
     * @param paramName ��������
     * @param paraValue ����ֵ
     */
    @SuppressWarnings("unchecked")
    public void setParam(String paramName, Object paraValue) {
        if ("snmp_msg".equals(paramName)) {
            Propert.getPropert().setSnmpMsg((ArrayList<SNMPBindVar>) paraValue);
        }

    }


    public void setParam(String paramName, String paramType, String paraValue, Boolean unempty) throws Exception {
        setParam(paramName, paramType, paraValue, unempty, null);
    }


    public void setParam(String paramName, String paramType, String paraValue, Boolean unempty, String timeType)
            throws Exception {

        if (StringUtil.isBlank((paraValue))) {
            if (Propert.PARAM_CANT_BE_EMPTY == unempty) {
                throw new Exception(paramName + "��������Ϊ�գ�");
            }
        }

        //��������Ƿ����ת���ɹ����ų�����Ϊ�յ����
        try {
            /* �������String���� */
            if ("string".equals(paramType)) {
                if (StringUtil.isBlank(paraValue)) {
                    paraValue = "";
                }
            }
            /* �������Integer���� */
            if ("int".equals(paramType)) {
                if (StringUtil.isBlank(paraValue)) {
                    paraValue = "-1";
                }
                Integer.parseInt(paraValue);
            }
            /* �������Long���� */
            if ("long".equals(paramType)) {
                if (StringUtil.isBlank(paraValue)) {
                    paraValue = "-1";
                }
                Long.parseLong(paraValue);
            }
            /* �������Boolean���� */
            if ("boolean".equals(paramType)) {
                if (StringUtil.isBlank(paraValue)) {
                    paraValue = "false";
                }
                Boolean.parseBoolean(paraValue);
            }
            /* �������Float���� */
            if ("float".equals(paramType)) {
                if (StringUtil.isBlank(paraValue)) {
                    paraValue = "-1";
                }
                Float.parseFloat(paraValue);
            }
        } catch (NumberFormatException e) {
            throw new Exception(paraValue + "��������" + paramType + "���ͣ������");
        }

        /*
         * ������ֵ
         */

        /*
         * ��������
         */
        if ("msg".equals(paramName)) {
            Propert.getPropert().setMsg(paraValue);
        }
        if ("charset".equals(paramName)) {
            if ("".equals(paraValue)) {
                Propert.getPropert().setCharset("UTF-8");// Ĭ���ַ�����ΪUTF-8
            } else {
                Propert.getPropert().setCharset(paraValue);
            }
        }

        /* ˳���Ͳ��� */
        if ("rate".equals(paramName)) {
            calInterval(paraValue, timeType);
        }
        if ("duration".equals(paramName)) {
            calDuration(paraValue, timeType);
        }

        /* �籩���Ͳ��� */
        if ("storm_rate".equals(paramName)) {
            Propert.getPropert().setStormRate(paraValue);
            Propert.getPropert().setStormRateType(timeType);
        }
        if ("storm_duration".equals(paramName)) {
            Propert.getPropert().setStormDuration(paraValue);
            Propert.getPropert().setStormDurationType(timeType);
        }

        if ("fastmodel".equals(paramName)) {
            boolean fast = Boolean.parseBoolean(paraValue);
            Propert.getPropert().setFast(fast);
            if (fast) {
                log.info("����ģʽ�ѿ���");
                Propert.getPropert().setBulidsend(true);
            }
        }

        if ("buildsend".equals(paramName)) {
            Propert.getPropert().setBulidsend(Boolean.parseBoolean(paraValue));
        }


        /* socket_server�ӿڲ��� */
        if ("socket_server_port".equals(paramName)) {
            Propert.getPropert().setSocket_server_port(Integer.parseInt(paraValue));
        }

        /* socket_client�ӿڲ��� */
        if ("socket_client_ip".equals(paramName)) {
            Propert.getPropert().setSocket_client_ip(paraValue);
        }
        if ("socket_client_port".equals(paramName)) {
            Propert.getPropert().setSocket_client_port(Integer.parseInt(paraValue));
        }

        /* kafka�ӿڲ��� */
        if ("zookUrl".equals(paramName)) {
            Propert.getPropert().setZookUrl(paraValue);
        }
        if ("kafkaUrl".equals(paramName)) {
            Propert.getPropert().setKafkaUrl(paraValue);
        }
        if ("topic".equals(paramName)) {
            Propert.getPropert().setTopic(paraValue);
        }
        if ("paritition".equals(paramName)) {
            Propert.getPropert().setParitition(Integer.parseInt(paraValue));
        }
        if ("send_partition".equals(paramName)) {
            int sendPartition = Integer.parseInt(paraValue);
            {
                if (Propert.getPropert().getParitition() == -1) {
                    throw new Exception("û�����÷�������");
                }
                if (sendPartition + 1 > Propert.getPropert().getParitition()) {
                    throw new Exception("���͵ķ����ų�������������Χ");
                }
            }
            Propert.getPropert().setSend_partition(sendPartition);
        }

        /* OMC����ӿڲ��� */
        if ("north_alarm_port".equals(paramName)) {
            Propert.getPropert().setNorth_alarm_port(Integer.parseInt(paraValue));
        }
        if ("north_alarm_user".equals(paramName)) {
            Propert.getPropert().setNorth_alarm_user(paraValue);
        }
        if ("north_alarm_pwd".equals(paramName)) {
            Propert.getPropert().setNorth_alarm_pwd(paraValue);
        }
        if ("alarm_id".equals(paramName)) {
            setOmcAlarmId(paraValue);
        }

        /* I1�ӿڲ��� */
        if ("i1_alarm_port".equals(paramName)) {
            Propert.getPropert().setI1_alarm_port(Integer.parseInt(paraValue));
        }
        if ("i1_alarm_user".equals(paramName)) {
            Propert.getPropert().setI1_alarm_user(paraValue);
        }
        if ("i1_alarm_pwd".equals(paramName)) {
            Propert.getPropert().setI1_alarm_pwd(paraValue);
        }
        if ("i1_alarm_id".equals(paramName)) {
            setOmcAlarmId(paraValue);
        }
        if ("i1_heart_interval".equals(paramName)) {
            Propert.getPropert().setI1_heart_interval(Integer.parseInt(paraValue));
        }

        /* CUCC�ӿڲ��� */
        if ("cucc_alarm_port".equals(paramName)) {
            Propert.getPropert().setCucc_alarm_port(Integer.parseInt(paraValue));
        }
        if ("cucc_alarm_user".equals(paramName)) {
            Propert.getPropert().setCucc_alarm_user(paraValue);
        }
        if ("cucc_alarm_pwd".equals(paramName)) {
            Propert.getPropert().setCucc_alarm_pwd(paraValue);
        }

        /* SNMP�ӿڲ��� */
        if ("snmp_port".equals(paramName)) {
            Propert.getPropert().setSnmp_port(Integer.parseInt(paraValue));
        }
        if ("snmp_trapOid".equals(paramName)) {
            Propert.getPropert().setSnmp_trapOid(paraValue);
        }
        if ("snmp_ip".equals(paramName)) {
            Propert.getPropert().setSnmp_ip(paraValue);
        }
        if ("snmp_version".equals(paramName)) {
            Propert.getPropert().setSnmp_version(Integer.parseInt(paraValue));
        }
        if ("snmp_auth".equals(paramName)) {
            Propert.getPropert().setSnmp_auth(Boolean.parseBoolean(paraValue));
        }
        if ("snmp_userName".equals(paramName)) {
            Propert.getPropert().setSnmp_userName(paraValue);
        }
        if ("authPass".equals(paramName)) {
            Propert.getPropert().setAuthPass(paraValue);
        }

        /* IBMQ�ӿڲ��� */
        if ("ibmmq_port".equals(paramName)) {
            Propert.getPropert().setIbmPort(Integer.parseInt(paraValue));
        }
        if ("ibmmq_ip".equals(paramName)) {
            Propert.getPropert().setIbmmqIp(paraValue);
        }
        if ("ibmmq_channel".equals(paramName)) {
            Propert.getPropert().setIbmmqChannel(paraValue);
        }
        if ("ibmmq_qManager".equals(paramName)) {
            Propert.getPropert().setIbmmqQueueManager(paraValue);
        }
        if ("ibmmq_ccsid".equals(paramName)) {
            Propert.getPropert().setIbmmq_CCSID(Integer.parseInt(paraValue));
        }
        if ("ibmmq_qName".equals(paramName)) {
            Propert.getPropert().setIbmmqQueueName(paraValue);
        }

        /* Telnet�ӿڲ��� */
        if ("telnet_ftp_user".equals(paramName)) {
            Propert.getPropert().setTelnetFtpuser(paraValue);
        }
        if ("telnet_ftp_pwd".equals(paramName)) {
            Propert.getPropert().setTelnetFtppassword(paraValue);
        }
        if ("telnet_ftp_source".equals(paramName)) {
            Propert.getPropert().setTelnetFtpSource(paraValue);
        }
        if ("telnet_ftp_ip".equals(paramName)) {
            Propert.getPropert().setTelnetFtpIp(paraValue);
        }
        if ("telnet_ftp_type".equals(paramName)) {
            Propert.getPropert().setTelnetFileType(paraValue);
        }

        /* ActivesMQ�ӿڲ��� */
        if ("actmmq_url".equals(paramName)) {
            Propert.getPropert().setActmqurl(paraValue);
        }
        if ("actmmq_qname".equals(paramName)) {
            Propert.getPropert().setActmqname(paraValue);
        }
        if ("actmmq_username".equals(paramName)) {
            Propert.getPropert().setActmquser(paraValue);
        }
        if ("actmmq_password".equals(paramName)) {
            Propert.getPropert().setActmqpwd(paraValue);
        }

        /* GNDP�ӿڲ��� */
        if ("gndp_conn_type".equals(paramName)) {
            Propert.getPropert().setGndp_conn_type(Integer.parseInt(paraValue));
        }
        if ("gndp_ftp_ip".equals(paramName)) {
            Propert.getPropert().setGndp_ftp_ip(paraValue);
        }
        if ("gndp_ftp_username".equals(paramName)) {
            Propert.getPropert().setGndp_ftp_username(paraValue);
        }
        if ("gndp_password".equals(paramName)) {
            Propert.getPropert().setGndp_password(paraValue);
        }

        /* Syslog�ӿڲ��� */
        if ("syslog_type".equals(paramName)) {
            Propert.getPropert().setSyslog_type(paraValue);
        }
        if ("syslog_port".equals(paramName)) {
            Propert.getPropert().setSyslog_port(Integer.parseInt(paraValue));
        }
        if ("syslog_target_host".equals(paramName)) {
            Propert.getPropert().setSyslog_target_host(paraValue);
        }
        if ("syslog_target_port".equals(paramName)) {
            Propert.getPropert().setSyslog_target_port(Integer.parseInt(paraValue));
        }
    }

    public int getNobuildsendNum() {
        return nobuildsendNum;
    }

    public void setNobuildsendNum(int nobuildsendNum) {
        this.nobuildsendNum = nobuildsendNum;
    }

    public List<String> getCachelist() {
        return cachelist;
    }

    public void setCachelist(List<String> cachelist) {
        this.cachelist = cachelist;
    }

    public boolean isNorth_sync_switch() {
        return north_sync_switch;
    }

    public void setNorth_sync_switch(boolean northSyncSwitch) {
        this.north_sync_switch = northSyncSwitch;
    }

    public int getNorth_sync_number() {
        return north_sync_number;
    }

    public void setNorth_sync_number(int northSyncNumber) {
        this.north_sync_number = northSyncNumber;
    }

    private void setOmcAlarmId(String alarmIdField) throws Exception {
        String alarmId = StringUtil.getSuiltString(alarmIdField,
                Propert.getPropert().getMsg(), 1);
        if (StringUtil.isBlank(alarmId)) {
            throw new Exception("û�н�ȡ���澯��ˮ�ţ����������ֶ�");
        }
        log.info("��ȡ����ʼ���̺ź���Ϊ��" + alarmId);
        if ("${id}".equals(alarmId)) {
            Propert.getPropert().setOmcAlarmId(0);
            return;
        }
        throw new Exception("����ʶ��澯��ˮ�ŵĺ���" + alarmId + ",���������ֶ�");

    }

    public boolean isCutSwitch() {
        return cutSwitch;
    }

    public void setCutSwitch(boolean cutSwitch) {
        this.cutSwitch = cutSwitch;
        if (cutSwitch) {
            logger.info("��ʼ��ȡ��Ϣ");
        } else {
            logger.info("ȡ����ȡ����������");
        }
    }

    /**
     * ��ȡ��������
     *
     * @return ��������
     */
    public int getSendType() {
        return sendType;
    }


    public void setSendType(String sendType) throws Exception {

        if ("normal".equals(sendType)) {
            this.sendType = Propert.NORMAL_SEND;
            return;
        }
        if ("storm".equals(sendType)) {
            this.sendType = Propert.STORM_SEND;
            return;
        }
        if ("partition".equals(sendType)) {
            this.sendType = Propert.PARTITION_SEND;
            return;
        }
        throw new Exception("�����ڸ÷������ͣ������");

    }

    public InterfaceEnum getSelInterface() {
        return selInterface;
    }

    /**
     * ���ýӿ�����
     *
     * @param selectInterface Linuxѡ���Ľӿ�����
     */
    public void setSelInterface(InterfaceEnum selectInterface) {
        selInterface = selectInterface;
    }

    /**
     * ���ýӿ�����
     *
     * @param selectInterface ����ѡ���Ľӿ�����
     */
    public void setSelInterface(String selectInterface) {
        selInterface = InterfaceEnum.getTypeByCname(selectInterface);

        switch (selInterface) {
            case SOCKET_SERVER:
                SocketServerPage.initInterfacePage().getInterfacePage();
                break;
            case SOCKET_CLIENT:
                SocketClientPage.initInterfacePage().getInterfacePage();
                break;
            case KAFKA:
                KafkaPage.initInterfacePage().getInterfacePage();
                break;
            case OMC:
                NorthAlarmPage.initInterfacePage().getInterfacePage();
                break;
            case SNMP:
                SNMPPage.initInterfacePage().getInterfacePage();
                break;
            case IBMMQ:
                IBMMQPage.initInterfacePage().getInterfacePage();
                break;
            case TELNET:
                TelnetPage.initInterfacePage().getInterfacePage();
                break;
            case ACTIVEMQ:
                ActiveMQPage.initInterfacePage().getInterfacePage();
                break;
            case I1:
                I1AlarmPage.initInterfacePage().getInterfacePage();
                break;
            case CUCC:
                CUCCAlarmPage.initInterfacePage().getInterfacePage();
                break;
            case SYSLOG:
                SyslogPage.initInterfacePage().getInterfacePage();
                break;
            default:
        }

    }


    public boolean isStormSend() {
        return stormSend;
    }

    void setStormSend(boolean stormSend) {
        this.stormSend = stormSend;
    }

    public boolean isPreparing() {
        return !stormReady;
    }

    void setStormReady(boolean stormReady) {
        this.stormReady = stormReady;
    }

    public boolean isShowDynamicLog() {
        return showDynamicLog;
    }

    public void setShowDynamicLog(boolean showLog) {
        this.showDynamicLog = showLog;

    }

    public int getNobuildrate() {
        return nobuildrate;
    }

    private void setNobuildrate(int nobuildrate) {
        this.nobuildrate = nobuildrate;
    }

    private void setNumber(int number) {
        this.number = number;
    }

    private void setSendNum(int sendNum) {
        this.sendNum = sendNum;
    }

    /**
     * ��ȡ�ܷ��ʹ���
     *
     * @return ��ȡ�ܷ��ʹ���
     */
    public int getNumber() {
        if (number == -2) {
            if (Propert.getPropert().getDuration() != -1) {
                number = Propert.getPropert().getDuration() * 1000 / Propert.getPropert().getInterval();
                return number;
            }
            return -1;
        }
        return number;
    }

    public int getSendNum() {
        return sendNum;
    }

    public void updateSendNum() {
        sendNum = sendNum + 1;
    }

    public boolean isParse() {
        return parse;
    }

    public void setParse(boolean parse) {
        this.parse = parse;
    }

    public static ContralCenter getContral() {
        return contral;
    }

    public boolean isNoSend() {
        return noSend;
    }

    /**
     * �������ء�����ʱ 1.�������湹���̣߳���һ�Σ� 2.���������̣߳�ÿ�η��Ͷ��½�һ���� �ر�ʱ 1.�رշ����߳� 2.��ϻ��湹���̱߳���ѭ��
     *
     * @param nosend ��ָͣ��
     * @return �Ƿ���
     */
    public Boolean setNoSend(boolean nosend) {
        this.noSend = nosend;
        /* ��ʼ���� */
        if (!nosend) {
            if (sendFactory != null && sendFactory.isAlive()) {
                log.debug("���ӻ�δ�رգ����Ժ�");
                return false;

            }

            production = ProductionThread.getProduction();
            if (!production.isAlive()) {
                production.start();
            }

            sendFactory = new SendFactory();
            sendFactory.start();

        } else {
            if (production.isAlive()) {
                log.info("����߳�");
                production.interrupt();

            }
            sendFactory.close();
            setNumber(-2);
            setSendNum(0);
            setStormSend(false);
            setStormReady(false);
        }
        return true;
    }

    /**
     * ��ǰ�Ͽ�����
     */
    public void closeCon() {
        if (sendFactory.isAlive()) {
            log.info("����߳�");
            production.interrupt();

        }
        sendFactory.close();
        setNumber(-2);
        setSendNum(0);
        setStormSend(false);
        setStormReady(false);

    }

    /**
     * ���ݽ�����д����Ϣ���㷢���ٶ�
     *
     * @param rate ������д��rate����
     * @param type rate����ʱ�䵥λ
     */
    private void calInterval(String rate, String type) {

        if (Propert.SEC.equals(type)) {

            /* ��ƴ����� */
            if (!Propert.getPropert().isBulidsend()) {
                Propert.getPropert().setInterval(1000);
                Propert.getPropert().setMsgNum(1);
                setNobuildrate(Integer.parseInt(rate));

            } else {
                Propert.getPropert().setInterval(1000);
                Propert.getPropert().setMsgNum(Integer.parseInt(rate));
            }
        }
        if (Propert.MIN.equals(type)) {
            if (60 / Integer.parseInt(rate) > 1) {
                if (!Propert.getPropert().isBulidsend()) {
                    Propert.getPropert().setInterval(60 / Integer.parseInt(rate) * 1000);
                    Propert.getPropert().setMsgNum(1);
                    setNobuildrate(1);
                } else {
                    Propert.getPropert().setInterval(60 / Integer.parseInt(rate) * 1000);
                    Propert.getPropert().setMsgNum(1);
                }
            } else {
                /* ��ƴ����� */
                if (!Propert.getPropert().isBulidsend()) {
                    Propert.getPropert().setInterval(1000);
                    Propert.getPropert().setMsgNum(1);
                    setNobuildrate(Integer.parseInt(rate));
                } else {
                    Propert.getPropert().setInterval(1000);
                    Propert.getPropert().setMsgNum(Integer.parseInt(rate) / 60);
                }
            }
        }
        if (Propert.HOUR.equals(type)) {
            if (3600 / Integer.parseInt(rate) > 1) {
                if (!Propert.getPropert().isBulidsend()) {
                    Propert.getPropert().setInterval(3600 / Integer.parseInt(rate) * 1000);
                    Propert.getPropert().setMsgNum(1);
                    setNobuildrate(Integer.parseInt(rate));
                } else {
                    Propert.getPropert().setInterval(3600 / Integer.parseInt(rate) * 1000);
                    Propert.getPropert().setMsgNum(1);
                }
            } else {
                /* ��ƴ����� */
                if (!Propert.getPropert().isBulidsend()) {
                    Propert.getPropert().setInterval(1000);
                    Propert.getPropert().setMsgNum(1);
                    setNobuildrate(Integer.parseInt(rate));
                } else {
                    Propert.getPropert().setInterval(1000);
                    Propert.getPropert().setMsgNum(Integer.parseInt(rate) / 3600);
                }
            }
        }


    }

    /**
     * ���ݽ�����д����Ϣ���㷢�ͳ���ʱ��
     *
     * @param duration ����ʱ��
     * @param type     ����ʱ��ĵ�λ
     */
    private void calDuration(String duration, String type) {
        if ("".equals(duration)) {
            Propert.getPropert().setDuration(-1);
            return;
        }

        if ("sec".equals(type)) {
            Propert.getPropert().setDuration(Integer.parseInt(duration));
        }
        if ("min".equals(type)) {
            Propert.getPropert().setDuration(Integer.parseInt(duration) * 60);
        }
        if ("hour".equals(type)) {
            Propert.getPropert().setDuration(Integer.parseInt(duration) * 3600);
        }

    }

    /**
     * �籩���Ϳ���
     *
     * @throws Exception ��������
     */
    public void stormSend() throws Exception {
        setStormSend(true);
        ProductionThread.getProduction().interrupt();
        logger.info("ƽ���ڽ�������ʼ�籩����");
        while (ContralCenter.getContral().isPreparing()) {
            Thread.sleep(1);
        }

        setParam("rate", "int", Propert.getPropert().getStormRate(),
                Propert.PARAM_CANT_BE_EMPTY, Propert.getPropert().getStormRateType());
        setParam("duration", "int", Propert.getPropert().getStormDuration(),
                Propert.PARAM_CANT_BE_EMPTY, Propert.getPropert().getStormDurationType());
        setSendNum(0);
        setNumber(-2);
    }

    /**
     * ��ƴ��ģʽ����
     */
    public void addNobuildSendNum() {
        nobuildsendNum++;
    }


}

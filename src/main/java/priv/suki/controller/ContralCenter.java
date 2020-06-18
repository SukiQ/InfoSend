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
 * 主控制类
 *
 * @author 花小琪
 * @version 1.0.0
 */
public class ContralCenter extends Thread {
    private static final Log log = LogFactory.getLog(ContralCenter.class);
    private static final Logger logger = Logger.getLogger("Log4jMain");
    private static final ContralCenter contral = new ContralCenter();
    // 停止/开始
    private boolean noSend = true;
    /**
     * 暂停
     */
    private boolean parse = false;
    private static SendFactory sendFactory;
    /**
     * 总发送次数
     */
    private int number = -2;
    // 已发送次数
    private int sendNum = 0;
    // 不拼接每次发送条数
    private int nobuildsendNum = 0;
    // 不拼接每次发送的总条数
    private int nobuildrate;
    private ProductionThread production;
    // 展示日志
    private boolean showDynamicLog = false;
    // 接口类型
    private InterfaceEnum selInterface;
    // 发送类型
    private int sendType;
    // 开始风暴发送
    private boolean stormSend = false;
    // 风暴发送准备完毕
    private boolean stormReady = false;
    // 截取消息开关
    private boolean cutSwitch = false;
    // OMC缓存历史告警List
    private List<String> cachelist = new ArrayList<>();
    // OMC消息同步开关
    private boolean north_sync_switch = false;
    // OMC消息同步序列号
    private int north_sync_number = 0;

    public ContralCenter() {
        Thread.currentThread().setName("ContralThread");
    }

    @Override
    public void run() {

    }

    /**
     * 特殊赋值
     *
     * @param paramName 参数名称
     * @param paraValue 参数值
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
                throw new Exception(paramName + "参数不能为空！");
            }
        }

        //检测类型是否可以转换成功，排除可以为空的情况
        try {
            /* 传入的是String类型 */
            if ("string".equals(paramType)) {
                if (StringUtil.isBlank(paraValue)) {
                    paraValue = "";
                }
            }
            /* 传入的是Integer类型 */
            if ("int".equals(paramType)) {
                if (StringUtil.isBlank(paraValue)) {
                    paraValue = "-1";
                }
                Integer.parseInt(paraValue);
            }
            /* 传入的是Long类型 */
            if ("long".equals(paramType)) {
                if (StringUtil.isBlank(paraValue)) {
                    paraValue = "-1";
                }
                Long.parseLong(paraValue);
            }
            /* 传入的是Boolean类型 */
            if ("boolean".equals(paramType)) {
                if (StringUtil.isBlank(paraValue)) {
                    paraValue = "false";
                }
                Boolean.parseBoolean(paraValue);
            }
            /* 传入的是Float类型 */
            if ("float".equals(paramType)) {
                if (StringUtil.isBlank(paraValue)) {
                    paraValue = "-1";
                }
                Float.parseFloat(paraValue);
            }
        } catch (NumberFormatException e) {
            throw new Exception(paraValue + "参数不是" + paramType + "类型！请查验");
        }

        /*
         * 参数赋值
         */

        /*
         * 公共参数
         */
        if ("msg".equals(paramName)) {
            Propert.getPropert().setMsg(paraValue);
        }
        if ("charset".equals(paramName)) {
            if ("".equals(paraValue)) {
                Propert.getPropert().setCharset("UTF-8");// 默认字符编码为UTF-8
            } else {
                Propert.getPropert().setCharset(paraValue);
            }
        }

        /* 顺序发送参数 */
        if ("rate".equals(paramName)) {
            calInterval(paraValue, timeType);
        }
        if ("duration".equals(paramName)) {
            calDuration(paraValue, timeType);
        }

        /* 风暴发送参数 */
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
                log.info("极速模式已开启");
                Propert.getPropert().setBulidsend(true);
            }
        }

        if ("buildsend".equals(paramName)) {
            Propert.getPropert().setBulidsend(Boolean.parseBoolean(paraValue));
        }


        /* socket_server接口参数 */
        if ("socket_server_port".equals(paramName)) {
            Propert.getPropert().setSocket_server_port(Integer.parseInt(paraValue));
        }

        /* socket_client接口参数 */
        if ("socket_client_ip".equals(paramName)) {
            Propert.getPropert().setSocket_client_ip(paraValue);
        }
        if ("socket_client_port".equals(paramName)) {
            Propert.getPropert().setSocket_client_port(Integer.parseInt(paraValue));
        }

        /* kafka接口参数 */
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
                    throw new Exception("没有配置分区总数");
                }
                if (sendPartition + 1 > Propert.getPropert().getParitition()) {
                    throw new Exception("发送的分区号超过分区总数范围");
                }
            }
            Propert.getPropert().setSend_partition(sendPartition);
        }

        /* OMC北向接口参数 */
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

        /* I1接口参数 */
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

        /* CUCC接口参数 */
        if ("cucc_alarm_port".equals(paramName)) {
            Propert.getPropert().setCucc_alarm_port(Integer.parseInt(paraValue));
        }
        if ("cucc_alarm_user".equals(paramName)) {
            Propert.getPropert().setCucc_alarm_user(paraValue);
        }
        if ("cucc_alarm_pwd".equals(paramName)) {
            Propert.getPropert().setCucc_alarm_pwd(paraValue);
        }

        /* SNMP接口参数 */
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

        /* IBMQ接口参数 */
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

        /* Telnet接口参数 */
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

        /* ActivesMQ接口参数 */
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

        /* GNDP接口参数 */
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

        /* Syslog接口参数 */
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
            throw new Exception("没有截取到告警流水号，请检查配置字段");
        }
        log.info("截取到初始流程号函数为：" + alarmId);
        if ("${id}".equals(alarmId)) {
            Propert.getPropert().setOmcAlarmId(0);
            return;
        }
        throw new Exception("不能识别告警流水号的函数" + alarmId + ",请检查配置字段");

    }

    public boolean isCutSwitch() {
        return cutSwitch;
    }

    public void setCutSwitch(boolean cutSwitch) {
        this.cutSwitch = cutSwitch;
        if (cutSwitch) {
            logger.info("开始截取消息");
        } else {
            logger.info("取消截取，正常发送");
        }
    }

    /**
     * 获取发送类型
     *
     * @return 发送类型
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
        throw new Exception("不存在该发送类型，请查验");

    }

    public InterfaceEnum getSelInterface() {
        return selInterface;
    }

    /**
     * 设置接口类型
     *
     * @param selectInterface Linux选定的接口类型
     */
    public void setSelInterface(InterfaceEnum selectInterface) {
        selInterface = selectInterface;
    }

    /**
     * 设置接口类型
     *
     * @param selectInterface 界面选定的接口类型
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
     * 获取总发送次数
     *
     * @return 获取总发送次数
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
     * 启动开关。启动时 1.启动缓存构造线程（第一次） 2.启动发送线程（每次发送都新建一个） 关闭时 1.关闭发送线程 2.打断缓存构造线程本次循环
     *
     * @param nosend 启停指令
     * @return 是否开启
     */
    public Boolean setNoSend(boolean nosend) {
        this.noSend = nosend;
        /* 开始发送 */
        if (!nosend) {
            if (sendFactory != null && sendFactory.isAlive()) {
                log.debug("连接还未关闭，请稍后");
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
                log.info("打断线程");
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
     * 提前断开连接
     */
    public void closeCon() {
        if (sendFactory.isAlive()) {
            log.info("打断线程");
            production.interrupt();

        }
        sendFactory.close();
        setNumber(-2);
        setSendNum(0);
        setStormSend(false);
        setStormReady(false);

    }

    /**
     * 根据界面填写的信息计算发送速度
     *
     * @param rate 界面填写的rate参数
     * @param type rate参数时间单位
     */
    private void calInterval(String rate, String type) {

        if (Propert.SEC.equals(type)) {

            /* 不拼接入库 */
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
                /* 不拼接入库 */
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
                /* 不拼接入库 */
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
     * 根据界面填写的信息计算发送持续时间
     *
     * @param duration 持续时间
     * @param type     持续时间的单位
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
     * 风暴发送开启
     *
     * @throws Exception 开启报错
     */
    public void stormSend() throws Exception {
        setStormSend(true);
        ProductionThread.getProduction().interrupt();
        logger.info("平稳期结束，开始风暴发送");
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
     * 无拼接模式自增
     */
    public void addNobuildSendNum() {
        nobuildsendNum++;
    }


}

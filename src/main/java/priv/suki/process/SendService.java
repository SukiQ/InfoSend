package priv.suki.process;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import priv.suki.controller.ContralService;
import priv.suki.process.service.DataTypeEnum;
import priv.suki.process.service.SendParam;
import priv.suki.util.Propert;
import priv.suki.util.StringUtil;

/**
 * @author 花小琪
 * @version 1.0.5
 */
public class SendService {
    private static final Log log = LogFactory.getLog(SendService.class);

    public static void setParam(String paramName, DataTypeEnum paramType, String paraValue, Boolean unempty) throws Exception {
        setParam(paramName, paramType, paraValue, unempty, null);
    }


    public static void setParam(String paramName, DataTypeEnum paramType, String paraValue, Boolean unempty, String timeType)
            throws Exception {

        if (StringUtil.isBlank((paraValue))) {
            if (Propert.PARAM_CANT_BE_EMPTY == unempty) {
                throw new Exception(paramName + "参数不能为空！");
            } else {
                return;
            }
        }

        judgeFormat(paramType, paraValue);

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
                Propert.getPropert().setCharset(ContralService.DEF_CHARSET);
            } else {
                Propert.getPropert().setCharset(paraValue);
            }
        }

        /* 顺序发送参数 */
        if ("rate".equals(paramName)) {
            SendParam.getInstance().setRate(paraValue, timeType);
        }
        if ("duration".equals(paramName)) {
            SendParam.getInstance().setDuration(paraValue, timeType);
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
            SendParam.getInstance().setSeqIdRex(paraValue);
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
            SendParam.getInstance().setSeqIdRex(paraValue);
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

    /**
     * 判断参数的数据类型是否正确
     *
     * @param paramType
     * @param paraValue
     * @return
     * @throws Exception
     */
    public static void judgeFormat(DataTypeEnum paramType, String paraValue) throws NumberFormatException {
        //检测类型是否可以转换成功，排除可以为空的情况
        try {
            /* 传入的是Integer类型 */
            if (DataTypeEnum.INT == paramType) {
                Integer.parseInt(paraValue);
            }
            /* 传入的是Long类型 */
            if (DataTypeEnum.LONG == paramType) {
                Long.parseLong(paraValue);
            }
            /* 传入的是Boolean类型 */
            if (DataTypeEnum.BOOLEAN == paramType) {
                Boolean.parseBoolean(paraValue);
            }
            /* 传入的是Float类型 */
            if (DataTypeEnum.FLOAT == paramType) {
                Float.parseFloat(paraValue);
            }
        } catch (NumberFormatException e) {
            throw new NumberFormatException(paraValue + "参数不是" + paramType + "类型！请查验");
        }
    }
}
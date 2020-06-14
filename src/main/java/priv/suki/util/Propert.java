package priv.suki.util;

import java.util.ArrayList;

import priv.suki.msg.SNMPBindVar;
import lombok.Getter;
import lombok.Setter;

/**
 * 参数汇总
 *
 * @author 花小琪
 * @version 1.0
 */
@Getter
@Setter
public class Propert {

	/*常量*/
	public static final String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss"; // 标准时间格式
	public static final String SYS_FILE_SPARATOR = System.getProperty("file.separator"); // 系统分隔符
	public static final String SYS_LINE_SPARATOR = System.getProperty("line.separator"); //系统换行符
	public static final boolean PARAM_CAN_BE_EMPTY = false; // 参数可以为空
	public static final boolean PARAM_CANT_BE_EMPTY = true; // 参数不能为空
	public static final int NORMAL_SEND = 1; // 顺序发送
	public static final int STORM_SEND = 2; // 风暴发送
	public static final int PARTITION_SEND = 3; // 指定分区发送
	public static final String SEC = "sec"; // 秒
	public static final String MIN = "min"; // 分钟
	public static final String HOUR = "hour"; // 小时
	public static final int SNMP_VERSION_1 = 1; // SNMP_V1版本
	public static final int SNMP_VERSION_2 = 2; // SNMP_V2版本
	public static final int SNMP_VERSION_3 = 3; // SNMP_V3版本
	public static final int ORACLE = 1;//Oracle数据库
	public static final int FTP = 0; //
	public static final int SFTP = 1; //
	public static final int HDFS = 2; //
	public static final int SYSLOG_SEND_TCP = 1; //
	public static final int SYSLOG_SEND_UDP = 2; //

	/*发送参数*/
	private int interval;// 发送间隔（1秒或者更多）
	private String send;// 需要发送的消息体
	private String msg;// 原始消息体
	private ArrayList<SNMPBindVar> snmpMsg;// SNMP原始消息体
	private int msgNum = 1;// 发送条数
	private int duration;// 发送持续时间
	private String charset = "UTF-8";// 字符编码
	private String sendType;// 发送方式
	private boolean bulidsend = true;//拼接发送开关
	private boolean isChanged = false;//文件改变开关
	private boolean fast = false;//极速模式开关
	private boolean autoReconn = false;//自动重连开关

	/*风暴发送参数*/
	private String stormRate;// 风暴发送速率
	private String stormRateType;// 风暴发送速率时间类型
	private String stormDuration;// 风暴发送速持续时间
	private String stormDurationType;// 风暴发送持续时间类型

	/*Socket服务端接口参数*/
	private int socket_server_port;// socket_server_port

	/*Socket客户端接口参数*/
	private int socket_client_port;// socket_client_port
	private String socket_client_ip;// socket_client_ip

	/*OMC北向接口参数*/
	private int north_alarm_port;// OMC北向端口
	private String north_alarm_user;// OMC北向用户名
	private String north_alarm_pwd;// OMC北向密码
	private int omcAlarmId;// OMC北向告警流水号/I1告警流水号

	/*I1告警接口参数*/
	private int i1_alarm_port;// I1告警端口
	private String i1_alarm_user;// I1告警用户名
	private String i1_alarm_pwd;// I1告警密码
	private int i1_heart_interval;//心跳间隔

	/*kafka接口参数*/
	private String zookUrl;// zookeeper地址
	private String kafkaUrl;// kafka地址
	private String topic;// topic地址
	private int paritition;// kafka分区总数
	private int send_partition;// kafka发送分区

	/*SNMP接口参数*/
	private int snmp_port;// SNMP端口号
	private String snmp_trapOid;// SNMP-trap-oid
	private String snmp_ip;// SNMP目的地址
	private int snmp_version; //SNMP版本
	private boolean snmp_auth; //SNMP_v3是否加密
	private String snmp_userName; //SNMP_v3用户名
	private String authPass; //SNMP_v3密码

	/*IBMMQ接口参数*/
	private String ibmmqIp; //IBMMQ地址
	private String ibmmqChannel; //IBMMQ通道名
	private int ibmmq_CCSID; //IBMMQ_CCSID
	private int ibmPort; //IBMMQ端口
	private String ibmmqQueueManager; //IBMMQ队列管理器名
	private String ibmmqQueueName; //IBMMQ队列名称

	/*Telnet接口参数*/
	private String telnetFtpuser; //Telnet_FTP用户名
	private String telnetFtppassword; //Telnet_FTP密码
	private String telnetFtpIp; //Telnet_FTP远程服务器IP
	private String telnetFtpSource; //Telnet_FTP远程服务器路径
	private String telnetFileType; //Telnet文件类型

	/*ActiveMq接口参数*/
	private String actmqurl; //Telnet_FTP用户名
	private String actmqname; //Telnet_FTP密码
	private String actmquser; //Telnet_FTP远程服务器IP
	private String actmqpwd; //Telnet_FTP远程服务器路径

	/*DB接口参数*/
	private int dbType;
	private String dburl;
	private String dbuser;
	private String dbPassword;

	/*GNDP接口参数*/
	private int gndp_conn_type;
	private String gndp_ftp_ip;
	private String gndp_ftp_username;
	private String gndp_password;

	/*CUCC接口参数*/
	private int cucc_alarm_port;// OMC北向端口
	private String cucc_alarm_user;// OMC北向用户名
	private String cucc_alarm_pwd;// OMC北向密码

	/*SYSLOG接口参数*/
	private int syslog_port;//syslog端口号
	private String syslog_type;//syslog发送类型 服务端/客户端
	private int syslog_target_port;//syslog目的端口号
	private String syslog_target_host;//syslog目的地址

	/** restful接口参数*/
	/**
	 * restful客户端/服务端
	 */
	private String restfulType;
	/**
	 * 服务端启动端口
	 */
	private int restfulPort;
	/**
	 * 服务端URL
	 */
	private String restfulUrl;
	/**
	 * 消息类型json/xml
	 */
	private String restfulMsgType;

	private static Propert propert = new Propert();

	public Propert() {
	}

	/**
	 * 获取参数
	 *
	 * @return 单例
	 */
	public static Propert getPropert() {
		return propert;
	}
}

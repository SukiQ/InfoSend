package priv.suki.util;

import java.util.ArrayList;

import priv.suki.msg.SNMPBindVar;
import lombok.Getter;
import lombok.Setter;

/**
 * ��������
 *
 * @author ��С��
 * @version 1.0
 */
@Getter
@Setter
public class Propert {

	/*����*/
	public static final String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss"; // ��׼ʱ���ʽ
	public static final String SYS_FILE_SPARATOR = System.getProperty("file.separator"); // ϵͳ�ָ���
	public static final String SYS_LINE_SPARATOR = System.getProperty("line.separator"); //ϵͳ���з�
	public static final boolean PARAM_CAN_BE_EMPTY = false; // ��������Ϊ��
	public static final boolean PARAM_CANT_BE_EMPTY = true; // ��������Ϊ��
	public static final int NORMAL_SEND = 1; // ˳����
	public static final int STORM_SEND = 2; // �籩����
	public static final int PARTITION_SEND = 3; // ָ����������
	public static final String SEC = "sec"; // ��
	public static final String MIN = "min"; // ����
	public static final String HOUR = "hour"; // Сʱ
	public static final int SNMP_VERSION_1 = 1; // SNMP_V1�汾
	public static final int SNMP_VERSION_2 = 2; // SNMP_V2�汾
	public static final int SNMP_VERSION_3 = 3; // SNMP_V3�汾
	public static final int ORACLE = 1;//Oracle���ݿ�
	public static final int FTP = 0; //
	public static final int SFTP = 1; //
	public static final int HDFS = 2; //
	public static final int SYSLOG_SEND_TCP = 1; //
	public static final int SYSLOG_SEND_UDP = 2; //

	/*���Ͳ���*/
	private int interval;// ���ͼ����1����߸��ࣩ
	private String send;// ��Ҫ���͵���Ϣ��
	private String msg;// ԭʼ��Ϣ��
	private ArrayList<SNMPBindVar> snmpMsg;// SNMPԭʼ��Ϣ��
	private int msgNum = 1;// ��������
	private int duration;// ���ͳ���ʱ��
	private String charset = "UTF-8";// �ַ�����
	private String sendType;// ���ͷ�ʽ
	private boolean bulidsend = true;//ƴ�ӷ��Ϳ���
	private boolean isChanged = false;//�ļ��ı俪��
	private boolean fast = false;//����ģʽ����
	private boolean autoReconn = false;//�Զ���������

	/*�籩���Ͳ���*/
	private String stormRate;// �籩��������
	private String stormRateType;// �籩��������ʱ������
	private String stormDuration;// �籩�����ٳ���ʱ��
	private String stormDurationType;// �籩���ͳ���ʱ������

	/*Socket����˽ӿڲ���*/
	private int socket_server_port;// socket_server_port

	/*Socket�ͻ��˽ӿڲ���*/
	private int socket_client_port;// socket_client_port
	private String socket_client_ip;// socket_client_ip

	/*OMC����ӿڲ���*/
	private int north_alarm_port;// OMC����˿�
	private String north_alarm_user;// OMC�����û���
	private String north_alarm_pwd;// OMC��������
	private int omcAlarmId;// OMC����澯��ˮ��/I1�澯��ˮ��

	/*I1�澯�ӿڲ���*/
	private int i1_alarm_port;// I1�澯�˿�
	private String i1_alarm_user;// I1�澯�û���
	private String i1_alarm_pwd;// I1�澯����
	private int i1_heart_interval;//�������

	/*kafka�ӿڲ���*/
	private String zookUrl;// zookeeper��ַ
	private String kafkaUrl;// kafka��ַ
	private String topic;// topic��ַ
	private int paritition;// kafka��������
	private int send_partition;// kafka���ͷ���

	/*SNMP�ӿڲ���*/
	private int snmp_port;// SNMP�˿ں�
	private String snmp_trapOid;// SNMP-trap-oid
	private String snmp_ip;// SNMPĿ�ĵ�ַ
	private int snmp_version; //SNMP�汾
	private boolean snmp_auth; //SNMP_v3�Ƿ����
	private String snmp_userName; //SNMP_v3�û���
	private String authPass; //SNMP_v3����

	/*IBMMQ�ӿڲ���*/
	private String ibmmqIp; //IBMMQ��ַ
	private String ibmmqChannel; //IBMMQͨ����
	private int ibmmq_CCSID; //IBMMQ_CCSID
	private int ibmPort; //IBMMQ�˿�
	private String ibmmqQueueManager; //IBMMQ���й�������
	private String ibmmqQueueName; //IBMMQ��������

	/*Telnet�ӿڲ���*/
	private String telnetFtpuser; //Telnet_FTP�û���
	private String telnetFtppassword; //Telnet_FTP����
	private String telnetFtpIp; //Telnet_FTPԶ�̷�����IP
	private String telnetFtpSource; //Telnet_FTPԶ�̷�����·��
	private String telnetFileType; //Telnet�ļ�����

	/*ActiveMq�ӿڲ���*/
	private String actmqurl; //Telnet_FTP�û���
	private String actmqname; //Telnet_FTP����
	private String actmquser; //Telnet_FTPԶ�̷�����IP
	private String actmqpwd; //Telnet_FTPԶ�̷�����·��

	/*DB�ӿڲ���*/
	private int dbType;
	private String dburl;
	private String dbuser;
	private String dbPassword;

	/*GNDP�ӿڲ���*/
	private int gndp_conn_type;
	private String gndp_ftp_ip;
	private String gndp_ftp_username;
	private String gndp_password;

	/*CUCC�ӿڲ���*/
	private int cucc_alarm_port;// OMC����˿�
	private String cucc_alarm_user;// OMC�����û���
	private String cucc_alarm_pwd;// OMC��������

	/*SYSLOG�ӿڲ���*/
	private int syslog_port;//syslog�˿ں�
	private String syslog_type;//syslog�������� �����/�ͻ���
	private int syslog_target_port;//syslogĿ�Ķ˿ں�
	private String syslog_target_host;//syslogĿ�ĵ�ַ

	/** restful�ӿڲ���*/
	/**
	 * restful�ͻ���/�����
	 */
	private String restfulType;
	/**
	 * ����������˿�
	 */
	private int restfulPort;
	/**
	 * �����URL
	 */
	private String restfulUrl;
	/**
	 * ��Ϣ����json/xml
	 */
	private String restfulMsgType;

	private static Propert propert = new Propert();

	public Propert() {
	}

	/**
	 * ��ȡ����
	 *
	 * @return ����
	 */
	public static Propert getPropert() {
		return propert;
	}
}

package priv.suki.send;

import java.io.IOException;

import priv.suki.util.Propert;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.SNMP4JSettings;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.UserTarget;
import org.snmp4j.mp.MPv3;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.security.AuthMD5;
import org.snmp4j.security.PrivDES;
import org.snmp4j.security.SecurityLevel;
import org.snmp4j.security.SecurityModels;
import org.snmp4j.security.SecurityProtocols;
import org.snmp4j.security.USM;
import org.snmp4j.security.UsmUser;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import priv.suki.msg.OrgInfo;

/**
 * SNMP接口实现类 如果要发送中文，那么收发双方必须均使用GBK编码
 *
 * @author 花小琪
 * @version 1.0.2
 */
public class SNMPSend implements Send {
    private static final Log log = LogFactory.getLog(SNMPSend.class);
    private Address targetAddress;
    private Snmp snmp;
    private TransportMapping<?> transport;

    /**
     * SNMP接口构造函数
     */
    public SNMPSend() {
    }

    @Override
    public boolean send(OrgInfo object) throws InterruptedException {

        try {
            PDU msg = (PDU) object.getMsg();

            /* 指定版本 */
            if (Propert.getPropert().getSnmp_version() == Propert.SNMP_VERSION_1) {
                sendV1Trap(msg);
            }
            if (Propert.getPropert().getSnmp_version() == Propert.SNMP_VERSION_2) {
                sendV2cTrap(msg);
            }
            if (Propert.getPropert().getSnmp_version() == Propert.SNMP_VERSION_3
                    && !Propert.getPropert().isSnmp_auth()) {
                sendV3TrapNoAuthNoPriv(msg);
            }
            if (Propert.getPropert().getSnmp_version() == Propert.SNMP_VERSION_3
                    && Propert.getPropert().isSnmp_auth()) {
                sendV3(msg);

            }

        } catch (Exception e) {
            log.error("SNMP本次发送失败", e);
            return false;
        }
        return true;
    }

    /**
     * V1版本发送方法
     *
     * @param msg trap消息
     * @throws Exception
     */
    public void sendV1Trap(PDU msg) throws Exception {
        CommunityTarget target = new CommunityTarget();
        target.setCommunity(new OctetString("public"));
        target.setAddress(targetAddress);
        // retry times when commuication error
        target.setRetries(2);
        // 设置超时时间
        target.setTimeout(1500);
        // 设置版本
        target.setVersion(SnmpConstants.version1);
        // 发送
        snmp.send(msg, target);
    }

    /**
     * V2c版本发送方法
     *
     * @param msg trap消息
     * @throws Exception
     */
    public void sendV2cTrap(PDU msg) throws Exception {
        CommunityTarget target = new CommunityTarget();
        target.setCommunity(new OctetString("public"));
        target.setAddress(targetAddress);
        // retry times when commuication error
        target.setRetries(2);
        // 设置超时时间
        target.setTimeout(1500);
        // 设置版本
        target.setVersion(SnmpConstants.version2c);
        // 发送
        snmp.send(msg, target);
    }

    /**
     * V3版本发送方法
     *
     * @param msg trap消息
     * @throws Exception
     */
    public void sendV3TrapNoAuthNoPriv(PDU msg) throws Exception {
        SNMP4JSettings.setExtensibilityEnabled(true);
        SecurityProtocols.getInstance().addDefaultProtocols();

        UserTarget target = new UserTarget();
        target.setVersion(SnmpConstants.version3);

        byte[] enginId = "TEO_ID".getBytes();
        USM usm = new USM(SecurityProtocols.getInstance(), new OctetString(enginId), 500);
        SecurityModels secModels = SecurityModels.getInstance();
        if (snmp.getUSM() == null) {
            secModels.addSecurityModel(usm);
        }

        target.setSecurityLevel(SecurityLevel.NOAUTH_NOPRIV);

        target.setAddress(targetAddress);
        snmp.setLocalEngine(enginId, 500, 1);
        snmp.send(msg, target);

    }

    /**
     * V3带认证协议，加密协议发送方法
     *
     * @param msg trap消息
     * @throws Exception
     */
    public void sendV3(PDU msg) throws Exception {
        OctetString userName = new OctetString("gframealarm");
        OctetString authPass = new OctetString("gframealarm");
        OctetString privPass = new OctetString("privPassword");

        transport = new DefaultUdpTransportMapping();

        Snmp snmp = new Snmp(transport);
        // MPv3.setEnterpriseID(35904);
        USM usm = new USM(SecurityProtocols.getInstance(), new OctetString(MPv3.createLocalEngineID()), 500);
        SecurityModels.getInstance().addSecurityModel(usm);

        UserTarget target = new UserTarget();

        byte[] enginId = "TEO_ID".getBytes();
        SecurityModels secModels = SecurityModels.getInstance();
        if (snmp.getUSM() == null) {
            secModels.addSecurityModel(usm);
        }

        snmp.getUSM().addUser(userName, new UsmUser(userName, AuthMD5.ID, authPass, PrivDES.ID, privPass));

        target.setAddress(targetAddress);
        target.setRetries(2);
        target.setTimeout(3000);
        target.setVersion(SnmpConstants.version3);
        target.setSecurityLevel(SecurityLevel.AUTH_NOPRIV);
        target.setSecurityName(userName);
        snmp.setLocalEngine(enginId, 500, 1);
        snmp.send(msg, target);

    }

    @Override
    /**
     * 初始化SNMP接口
     */
    public boolean init() {
        try {

            targetAddress = GenericAddress
                    .parse("udp:" + Propert.getPropert().getSnmp_ip() + "/" + Propert.getPropert().getSnmp_port());
            TransportMapping<UdpAddress> transport = new DefaultUdpTransportMapping();
            snmp = new Snmp(transport);
            transport.listen();

            return true;
        } catch (Exception e) {
            log.error("初始化SNMP失败", e);
            return false;
        }
    }

    /**
     * 关闭SNMP连接
     */
    @Override
    public void close() {
        if (transport != null) {
            try {
                transport.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                log.error("SNMP关闭接口失败", e);
            }
        }
        if (snmp != null) {
            try {
                snmp.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                log.error("SNMP关闭接口失败", e);
            }
        }
    }

}

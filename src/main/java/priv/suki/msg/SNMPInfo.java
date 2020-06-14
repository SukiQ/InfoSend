package priv.suki.msg;

import java.util.ArrayList;
import java.util.List;

import priv.suki.util.Propert;
import org.snmp4j.PDU;
import org.snmp4j.PDUv1;
import org.snmp4j.ScopedPDU;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.TimeTicks;
import org.snmp4j.smi.VariableBinding;
import priv.suki.util.BuiltFunc;
import priv.suki.util.StringUtil;

/**
 * SNMP消息对象
 *
 * @author 花小琪
 * @version 1.1
 */
public class SNMPInfo implements OrgInfo {

    private List<List<String>> msglistBind;
    private PDU pdu;
    private List<SNMPBindVar> snmpMsg;

    /**
     * 构造函数，复制对象
     *
     * @param pdu     snmp pdu
     * @param snmpMsg snmp消息体
     */
    private SNMPInfo(PDU pdu, List<SNMPBindVar> snmpMsg) {
        this.pdu = pdu;
        this.snmpMsg = new ArrayList<>();
        for (SNMPBindVar snmpList : snmpMsg) {
            this.snmpMsg.add(snmpList.clone());
        }

    }

    public SNMPInfo() {
    }

    @Override
    public void initializeMsg() {

        snmpMsg = new ArrayList<>();
        msglistBind = new ArrayList<>();
        for (SNMPBindVar snmpList : Propert.getPropert().getSnmpMsg()) {
            snmpMsg.add(snmpList.clone());
        }

        for (SNMPBindVar list : snmpMsg) {
            String msg = list.getBindVar();
            List<String> msglist = new ArrayList<>();
            while (msg.contains("${")) {
                String tempMsg = msg.substring(0, msg.indexOf("${"));
                msg = msg.substring(msg.indexOf("${"));
                msglist.add(tempMsg);
                msglist.add(msg.substring(0, msg.indexOf("}") + 1));
                msg = msg.substring(msg.indexOf("}") + 1);
            }
            msglist.add(msg);
            msglistBind.add(msglist);
        }
    }

    @Override
    public String structureMsg(BuiltFunc builtFunc) {
        return null;
    }

    /**
     * SNMP的消息采用多行拼接法
     *
     * @param builtFunc 方法对象
     * @param rowNum    行数
     * @return 返回拼接周的消息
     */
    public String structureMsg(BuiltFunc builtFunc, int rowNum) {
        // TODO Auto-generated method stub
        return StringUtil.buildeMsg(msglistBind.get(rowNum), builtFunc);
    }

    @Override
    public OrgInfo appendMsg(int appendNum, BuiltFunc builtFunc) throws Exception {

        if (Propert.getPropert().getSnmp_version() == Propert.SNMP_VERSION_1) {
            pdu = new PDUv1();
            pdu.setType(PDU.V1TRAP);

        }
        if (Propert.getPropert().getSnmp_version() == Propert.SNMP_VERSION_2) {
            pdu = new PDU();
            pdu.setType(PDU.TRAP);
        }
        if (Propert.getPropert().getSnmp_version() == Propert.SNMP_VERSION_3 && !Propert.getPropert().isSnmp_auth()) {

            pdu = new ScopedPDU();
            pdu.setType(PDU.NOTIFICATION);

        }
        if (Propert.getPropert().getSnmp_version() == Propert.SNMP_VERSION_3 && Propert.getPropert().isSnmp_auth()) {

            pdu = new ScopedPDU();
            pdu.setType(PDU.NOTIFICATION);

        }


        pdu.add(new VariableBinding(SnmpConstants.snmpTrapOID, new OID(Propert.getPropert().getSnmp_trapOid())));
        pdu.add(new VariableBinding(SnmpConstants.snmpTrapOID, new OID(Propert.getPropert().getSnmp_trapOid())));

        for (int i = 0; i < snmpMsg.size(); i++) {
            snmpMsg.get(i).setBindVar(this.structureMsg(builtFunc, i));

            if (snmpMsg.get(i).getTrapType() == SNMPBindVar.OCTETSTRING) {
                pdu.add(new VariableBinding(new OID(snmpMsg.get(i).getTrapOid()), new OctetString(
                        new String(snmpMsg.get(i).getBindVar().getBytes(), Propert.getPropert().getCharset()))));
            }
            if (snmpMsg.get(i).getTrapType() == SNMPBindVar.INTEGER) {
                pdu.add(new VariableBinding(new OID(snmpMsg.get(i).getTrapOid()),
                        new Integer32(Integer.parseInt(snmpMsg.get(i).getBindVar()))));
            }
            if (snmpMsg.get(i).getTrapType() == SNMPBindVar.TIMETICKS) {
                pdu.add(new VariableBinding(new OID(snmpMsg.get(i).getTrapOid()), new TimeTicks(
                        StringUtil.getTimeMill(snmpMsg.get(i).getBindVar(), Propert.TIME_FORMAT))));
            }
        }
        return new SNMPInfo(pdu, snmpMsg);
    }

    /**
     * SNMPInfo不能消息拼接
     */

    @Override
    public PDU getMsg() {
        return pdu;
    }

    @Override
    public String toString() {
        StringBuilder msgBuilder = new StringBuilder();
        msgBuilder.append("<MSG_AlARM_DATA><TrapOID>").append(Propert.getPropert().getSnmp_trapOid())
                .append("</TrapOID><bind_var>");
        for (SNMPBindVar rowMsg : snmpMsg) {
            msgBuilder.append("<").append(rowMsg.getTrapOid()).append(">").append(rowMsg.getBindVar()).append("</")
                    .append(rowMsg.getTrapOid()).append(">");
        }
        msgBuilder.append("</bind_var></MSG_AlARM_DATA>");
        return msgBuilder.toString();
    }

}

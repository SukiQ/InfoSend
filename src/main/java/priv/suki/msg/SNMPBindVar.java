package priv.suki.msg;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * SNMP单行消息体
 *
 * @author 花小琪
 * @version 1.0
 */
@Setter
@Getter
@NoArgsConstructor
public class SNMPBindVar {

    static final int OCTETSTRING = 1;
    static final int INTEGER = 2;
    static final int TIMETICKS = 3;
    private String trapOid;
    private String bindVar;
    private int trapType;


    /**
     * 设置SNMP数据消息类型
     *
     * @param trapType
     */
    public void setTrapType(String trapType) {
        if ("OctetString".equals(trapType) || "1".equals(trapType)) {
            this.trapType = 1;
        }
        if ("Integer".equals(trapType) || "2".equals(trapType)) {
            this.trapType = 2;
        }
        if ("TimeTicks".equals(trapType) || "3".equals(trapType)) {
            this.trapType = 3;
        }

    }


    @Override
    public SNMPBindVar clone() {
        SNMPBindVar snmpvar = new SNMPBindVar();
        snmpvar.setBindVar(this.getBindVar());
        snmpvar.setTrapOid(this.getTrapOid());
        snmpvar.setTrapType(String.valueOf(this.getTrapType()));
        return snmpvar;
    }

}

package priv.suki.inf;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * �ӿڶ���
 *
 * @author ��С��
 * @version 1.0.5
 */
@Data
@AllArgsConstructor
public class InterfaceObject {
    /**
     * �ӿ�������
     */
    private String cname;
    /**
     * �ӿ���Ϣ������
     */
    private Class infoClass;
    /**
     * �ӿڷ�����
     */
    private Class sendClass;

}
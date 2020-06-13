package priv.suki.msg;

import priv.suki.util.BuiltFunc;

/**
 * ������Ϣ�ӿ���
 *
 * @author ��С��
 * @version 1.0
 */
public interface OrgInfo {

    /**
     * ��Ϣ��ʼ������
     */
    void initializeMsg();

    /**
     * ��Ϣ���췽��
     */
    Object structureMsg(BuiltFunc builtFunc);

    /**
     * ��Ϣƴ�ӷ���
     *
     * @throws Exception ƴ���쳣
     */
    OrgInfo appendMsg(int appendNum, BuiltFunc builtFunc) throws Exception;

    /**
     * �Ƿ���Զ���Ϣƴ��
     */
    Object getMsg();

    /**
     * ��ʾ����
     */
    String toString();

}

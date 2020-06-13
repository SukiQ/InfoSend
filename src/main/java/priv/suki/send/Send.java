package priv.suki.send;

import priv.suki.msg.OrgInfo;

/**
 * ������ӿ�
 *
 * @author ��С��
 * @version 1.0.0
 */
public interface Send {

    /**
     * ���ͷ���
     *
     * @param msg ��Ϣ��
     * @return
     * @throws InterruptedException
     */
    public abstract boolean send(OrgInfo msg) throws InterruptedException;

    /**
     * ��ʼ������
     *
     * @return �Ƿ�ɹ�
     * @throws Exception
     */
    public abstract boolean init();

    /**
     * �رսӿڷ���
     */
    public abstract void close();
}

package priv.suki.send;

import priv.suki.msg.OrgInfo;

/**
 * 发送类接口
 *
 * @author 花小琪
 * @version 1.0.0
 */
public interface Send {

    /**
     * 发送方法
     *
     * @param msg 消息体
     * @return
     * @throws InterruptedException
     */
    public abstract boolean send(OrgInfo msg) throws InterruptedException;

    /**
     * 初始化方法
     *
     * @return 是否成功
     * @throws Exception
     */
    public abstract boolean init();

    /**
     * 关闭接口方法
     */
    public abstract void close();
}

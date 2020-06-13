package priv.suki.msg;

import priv.suki.util.BuiltFunc;

/**
 * 发送消息接口类
 *
 * @author 花小琪
 * @version 1.0
 */
public interface OrgInfo {

    /**
     * 消息初始化方法
     */
    void initializeMsg();

    /**
     * 消息构造方法
     */
    Object structureMsg(BuiltFunc builtFunc);

    /**
     * 消息拼接方法
     *
     * @throws Exception 拼接异常
     */
    OrgInfo appendMsg(int appendNum, BuiltFunc builtFunc) throws Exception;

    /**
     * 是否可以多消息拼接
     */
    Object getMsg();

    /**
     * 显示方法
     */
    String toString();

}

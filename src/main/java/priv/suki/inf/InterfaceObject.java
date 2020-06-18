package priv.suki.inf;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 接口对象
 *
 * @author 花小琪
 * @version 1.0.5
 */
@Data
@AllArgsConstructor
public class InterfaceObject {
    /**
     * 接口中文名
     */
    private String cname;
    /**
     * 接口消息类型类
     */
    private Class infoClass;
    /**
     * 接口发送类
     */
    private Class sendClass;

}
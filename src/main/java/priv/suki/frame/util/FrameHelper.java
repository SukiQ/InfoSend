package priv.suki.frame.util;

import priv.suki.util.Propert;

import java.awt.*;

/**
 * @author 花小琪
 */
public class FrameHelper {
    /**
     * 开发者日志
     */
    public static final String DEVELOPER_LOG = "版本：V1.0.4\n" +
            "1.优化了发送逻辑及主体代码格式\n" +
            "2.新增了特殊接口的极速发送模式\n" +
            "3.新增了Syslog接口，联通集团告警接口\n\n\n" +
            "若在使用InfoSend中存在问题或BUG，欢迎联系作者\n" +
            "手机/微信：18706768036";

    /**
     * 版本号
     */
    public static final String VERSION = "InfoSend_v1.0.4";

    /**
     * 主色调
     */
    public static final Color MAIN_TONE = new Color(67, 182, 188);

    /**
     * 副色调
     */
    public static final Color LABEL_TONE = new Color(192, 190, 188);

    /**
     * 图标
     */
    public static final String MARK_ICON_IMAGE_PATH = ".." + Propert.SYS_FILE_SPARATOR + "img" + Propert.SYS_FILE_SPARATOR + "logo.png";

    /**
     * 签名
     */
    public static final String LOGO_ICON_IMAGE_PATH = ".." + Propert.SYS_FILE_SPARATOR + "img" + Propert.SYS_FILE_SPARATOR + "Suki.PNG";

    /**
     * 标题
     */
    public static final String TITLE_ICON_IMAGE_PATH = ".." + Propert.SYS_FILE_SPARATOR + "img" + Propert.SYS_FILE_SPARATOR + "title.PNG";

    /**
     * 命令文件缓存地址
     */
    public static final String CMD_FILE_PATH = ".." + Propert.SYS_FILE_SPARATOR + "data" + Propert.SYS_FILE_SPARATOR + "cmd.tmp";
}

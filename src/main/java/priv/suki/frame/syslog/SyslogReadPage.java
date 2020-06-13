package priv.suki.frame.syslog;


import priv.suki.frame.util.FrameHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.Dialog.ModalExclusionType;

/**
 * syslog接口参数说明界面
 *
 * @author 花小琪
 * @version 1.0.4
 */
public class SyslogReadPage {

    /**
     * log
     */
    private static final Log log = LogFactory.getLog(SyslogReadPage.class);
    private static final SyslogReadPage SYSLOG_READ_PAGE = new SyslogReadPage();
    private static JFrame InfoSend;

    /**
     * SyslogReadPage构造模块，初始化界面
     */
    public SyslogReadPage() {
        Thread.currentThread().setName("SyslogReadPage");
        initialize();
    }

    /**
     * 初始化界面
     */
    private void initialize() {
        InfoSend = new JFrame();
        InfoSend.setIconImage(Toolkit.getDefaultToolkit().getImage(FrameHelper.MARK_ICON_IMAGE_PATH));
        InfoSend.setResizable(false);
        InfoSend.setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);
        InfoSend.getContentPane().setBackground(new Color(255, 255, 255));
        InfoSend.setTitle(FrameHelper.VERSION);
        InfoSend.setForeground(new Color(0, 0, 0));
        InfoSend.setBackground(new Color(0, 0, 0));
        InfoSend.setFont(new Font("等线", Font.PLAIN, 12));
        InfoSend.getContentPane().setFont(new Font("微软雅黑 Light", Font.BOLD, 15));
        InfoSend.setBounds(100, 100, 805, 542);
        InfoSend.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        InfoSend.setLocationRelativeTo(null);

        Canvas canvas = new Canvas();
        canvas.setBounds(0, 0, 0, 0);
        canvas.setBackground(new Color(0, 0, 0));
        InfoSend.getContentPane().setLayout(null);
        InfoSend.getContentPane().add(canvas);

        Panel panel = new Panel();
        panel.setBounds(200, 0, 599, 48);
        panel.setBackground(FrameHelper.MAIN_TONE);
        InfoSend.getContentPane().add(panel);

        JLabel lblNewLabelLogo = new JLabel("");
        lblNewLabelLogo.setIcon(new ImageIcon(FrameHelper.LOGO_ICON_IMAGE_PATH));
        lblNewLabelLogo.setBounds(651, 414, 134, 80);
        InfoSend.getContentPane().add(lblNewLabelLogo);

        JButton btnNewButtonReturn = new JButton("Return");
        btnNewButtonReturn.setBackground(new Color(255, 255, 255));
        btnNewButtonReturn.setToolTipText("");

        /*Return按钮监听*/
        btnNewButtonReturn.addActionListener(e -> {
            SyslogPage.initInterfacePage().getInterfacePage();
            InfoSend.setVisible(false);
        });
        btnNewButtonReturn.setFont(new Font("Trebuchet MS", Font.PLAIN, 15));
        btnNewButtonReturn.setBounds(0, 0, 201, 48);
        InfoSend.getContentPane().add(btnNewButtonReturn);

        JEditorPane description = new JEditorPane();
        description.setFont(new Font("微软雅黑 Light", Font.PLAIN, 16));
        description.setText("\u63A5\u53E3\u8BF4\u660E\uFF1ASyslog\u63A5\u53E3\u6A21\u62DF\r\n\u53C2\u6570\u8BF4\u660E\uFF1A\r\nport\uFF1A\u5982\u679C\u4F7F\u7528\u670D\u52A1\u7AEF\uFF0C\u9700\u8981\u914D\u7F6E\u7AEF\u53E3\r\ntype\uFF1A\u670D\u52A1\u7AEF/\u5BA2\u6237\u7AEF\r\ntargetPort\uFF1A\u76EE\u7684\u7AEF\u53E3\r\ntargetHost\uFF1A\u76EE\u7684\u5730\u5740\r\ncharset\uFF1A\u5B57\u7B26\u7F16\u7801\r\n\r\n\u5907\u6CE8\uFF1A\u82E5\u5728\u4F7F\u7528InfoSend\u4E2D\u5B58\u5728\u95EE\u9898\u6216BUG\uFF0C\u6B22\u8FCE\u8054\u7CFB\u4F5C\u8005\uFF0C\u624B\u673A/\u5FAE\u4FE1\uFF1A18706768036\r\n");
        description.setBounds(14, 143, 758, 303);
        InfoSend.getContentPane().add(description);

        JEditorPane descriptionTitle = new JEditorPane();
        descriptionTitle.setText("Syslog");
        descriptionTitle.setFont(new Font("微软雅黑 Light", Font.BOLD, 20));
        descriptionTitle.setBounds(14, 91, 155, 48);
        InfoSend.getContentPane().add(descriptionTitle);

    }

    /**
     * 获取SyslogReadPage对象
     *
     * @return 获取SyslogReadPage对象
     */
    public static SyslogReadPage initInterfacePage() {
        return SYSLOG_READ_PAGE;
    }

    /**
     * 打开SyslogReadPage界面，每次获取时，更新接口选择框显示
     */
    public void getInterfacePage() {
        InfoSend.setVisible(true);

    }
}

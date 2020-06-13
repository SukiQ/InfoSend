package priv.suki.frame.activemq;

import javax.swing.JFrame;
import java.awt.Font;
import java.awt.Color;
import java.awt.Dialog.ModalExclusionType;
import java.awt.Panel;
import java.awt.Canvas;

import priv.suki.frame.util.FrameHelper;
import priv.suki.util.Propert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.JButton;
import java.awt.Toolkit;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.swing.JEditorPane;

/**
 * ActiveMQ参数说明界面
 *
 * @author 花小琪
 * @version 1.0.2
 */
public class ActiveMQReadPage {
    /**
     * log
     */
    private static final Log log = LogFactory.getLog(ActiveMQReadPage.class);
    private static ActiveMQReadPage activeMQReadPage = new ActiveMQReadPage();
    private static JFrame InfoSend;

    /**
     * ActiveMQ构造模块，初始化界面
     */
    public ActiveMQReadPage() {
        Thread.currentThread().setName("ActiveMQ");
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

        JLabel lblNewLabel_logo = new JLabel("");
        lblNewLabel_logo.setIcon(new ImageIcon(FrameHelper.LOGO_ICON_IMAGE_PATH));
        lblNewLabel_logo.setBounds(651, 414, 134, 80);
        InfoSend.getContentPane().add(lblNewLabel_logo);

        JButton btnNewButton_return = new JButton("Return");
        btnNewButton_return.setBackground(new Color(255, 255, 255));
        btnNewButton_return.setToolTipText("");

        /*Return按钮监听*/
        btnNewButton_return.addActionListener(e -> {
            ActiveMQPage.initInterfacePage().getInterfacePage();
            InfoSend.setVisible(false);
        });
        btnNewButton_return.setFont(new Font("Trebuchet MS", Font.PLAIN, 15));
        btnNewButton_return.setBounds(0, 0, 201, 48);
        InfoSend.getContentPane().add(btnNewButton_return);

        JEditorPane description = new JEditorPane();
        description.setFont(new Font("微软雅黑 Light", Font.PLAIN, 16));
        description.setText("\u63A5\u53E3\u8BF4\u660E\uFF1AActiveMQ\u5BA2\u6237\u7AEF\u63A5\u53E3\u6A21\u62DF\r\n\u53C2\u6570\u8BF4\u660E\uFF1A\r\nurl\uFF1AActiveMQ\u5730\u5740\r\nQName\uFF1AAcitveMQ\u8FDC\u7A0B\u961F\u5217\u540D\u79F0\r\nusername\uFF1AActiveMQ\u7528\u6237\u540D\r\npassword\uFF1AActiveMQ\u5BC6\u7801\r\ncharset\uFF1A\u5B57\u7B26\u7F16\u7801\r\n\u5907\u6CE8\uFF1A\u82E5\u5728\u4F7F\u7528InfoSend\u4E2D\u5B58\u5728\u95EE\u9898\u6216BUG\uFF0C\u6B22\u8FCE\u8054\u7CFB\u4F5C\u8005\uFF0C\u624B\u673A/\u5FAE\u4FE1\uFF1A18706768036\r\n");
        description.setBounds(14, 143, 758, 303);
        InfoSend.getContentPane().add(description);

        JEditorPane description_title = new JEditorPane();
        description_title.setText("ActiveMQ");
        description_title.setFont(new Font("微软雅黑 Light", Font.BOLD, 20));
        description_title.setBounds(14, 91, 155, 48);
        InfoSend.getContentPane().add(description_title);

    }

    /**
     * 获取ActiveMQ对象
     *
     * @return ActiveMQ对象
     */
    public static ActiveMQReadPage initInterfacePage() {
        return activeMQReadPage;
    }

    /**
     * 打开ActiveMQ界面，每次获取时，更新接口选择框显示
     */
    public void getInterfacePage() {
        InfoSend.setVisible(true);

    }
}

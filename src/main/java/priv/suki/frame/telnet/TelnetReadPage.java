package priv.suki.frame.telnet;

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

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import java.awt.Toolkit;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.swing.JEditorPane;

/**
 * TelnetPage接口参数说明界面
 *
 * @author 花小琪
 * @version 1.0
 */
public class TelnetReadPage {
    // Log
    private static Log log = LogFactory.getLog(TelnetReadPage.class);
    private static TelnetReadPage telnetReadPage = new TelnetReadPage();
    private static JFrame InfoSend;

    /**
     * TelnetPage构造模块，初始化界面
     */
    public TelnetReadPage() {
        Thread.currentThread().setName("TelnetPage");
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
        btnNewButton_return.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                TelnetPage.initInterfacePage().getInterfacePage();
                InfoSend.setVisible(false);
            }
        });
        btnNewButton_return.setFont(new Font("Trebuchet MS", Font.PLAIN, 15));
        btnNewButton_return.setBounds(0, 0, 201, 48);
        InfoSend.getContentPane().add(btnNewButton_return);

        JEditorPane description = new JEditorPane();
        description.setFont(new Font("微软雅黑 Light", Font.PLAIN, 16));
        description.setText("\u63A5\u53E3\u8BF4\u660E\uFF1ATelnet\u6A21\u62DF\u5B9E\u9645\u4E0A\u662F\u5C06\u6570\u636E\u4EE5\u6587\u4EF6\u7684\u5F62\u5F0F\u653E\u5230\u8FDC\u7A0B\u670D\u52A1\u5668\u4E0A\uFF0C\u4F9B\u5BA2\u6237\u7AEF\u8BBF\u95EE\u5E76\u83B7\u53D6\r\n\u53C2\u6570\u8BF4\u660E\uFF1A\r\nftpUser\uFF1A\u8FDC\u7A0B\u670D\u52A1\u5668\u7528\u6237\u540D\r\nftpPassword\uFF1A\u8FDC\u7A0B\u670D\u52A1\u5668\u5BC6\u7801\r\nftpSource\uFF1A\u8FDC\u7A0B\u670D\u52A1\u5668\u5B58\u653E\u8DEF\u5F84\r\nftpIp\uFF1A\u8FDC\u7A0B\u670D\u52A1\u5668IP\r\nfileType\uFF1A\u6301\u4E45\u5316\u6587\u4EF6\u7C7B\u578B\uFF0C\u53EF\u9009csv\u6216xml\r\ncharset:\u5B57\u7B26\u7F16\u7801\r\n\u5907\u6CE8\uFF1A\u82E5\u5728\u4F7F\u7528InfoSend\u4E2D\u5B58\u5728\u95EE\u9898\u6216BUG\uFF0C\u6B22\u8FCE\u8054\u7CFB\u4F5C\u8005\uFF0C\u624B\u673A/\u5FAE\u4FE1\uFF1A18706768036\r\n");
        description.setBounds(14, 143, 758, 303);
        InfoSend.getContentPane().add(description);

        JEditorPane description_title = new JEditorPane();
        description_title.setText("Telnet");
        description_title.setFont(new Font("微软雅黑 Light", Font.BOLD, 20));
        description_title.setBounds(14, 91, 155, 48);
        InfoSend.getContentPane().add(description_title);

    }

    /**
     * 获取TelnetPage对象
     *
     * @return TelnetPage对象
     */
    public static TelnetReadPage initInterfacePage() {
        return telnetReadPage;
    }

    /**
     * 打开TelnetPage界面，每次获取时，更新接口选择框显示
     */
    public void getInterfacePage() {
        InfoSend.setVisible(true);

    }
}

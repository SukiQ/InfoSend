package priv.suki.frame.i1alarm;

import priv.suki.frame.util.FrameHelper;
import priv.suki.util.Propert;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.Dialog.ModalExclusionType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 北向接口参数说明界面
 *
 * @author 花小琪
 * @version 1.0
 */
public class I1AlarmReadPage {
    // Log
    private static Log log = LogFactory.getLog(I1AlarmReadPage.class);
    private static I1AlarmReadPage northAlarmReadPage = new I1AlarmReadPage();
    private static JFrame InfoSend;

    /**
     * 北向接口构造模块，初始化界面
     */
    public I1AlarmReadPage() {
        Thread.currentThread().setName("北向接口");
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
                I1AlarmPage.initInterfacePage().getInterfacePage();
                InfoSend.setVisible(false);
            }
        });
        btnNewButton_return.setFont(new Font("Trebuchet MS", Font.PLAIN, 15));
        btnNewButton_return.setBounds(0, 0, 201, 48);
        InfoSend.getContentPane().add(btnNewButton_return);

        JEditorPane description = new JEditorPane();
        description.setFont(new Font("微软雅黑 Light", Font.PLAIN, 16));
        description.setText("\u63A5\u53E3\u8BF4\u660E\uFF1A\u7535\u4FE1I1\u544A\u8B66\u63A5\u53E3\uFF0C\u5B9E\u73B0\u4E86\u5B9E\u65F6\u63A5\u53E3\u8865\u53D1\u544A\u8B66\u529F\u80FD\r\n\u53C2\u6570\u8BF4\u660E\uFF1A\r\nport\uFF1A\u670D\u52A1\u7AEF\u7AEF\u53E3\r\nuser\uFF1A\u767B\u9646\u7528\u6237\u540D\r\npassword\uFF1A\u767B\u9646\u5BC6\u7801\r\nalarmid\uFF1A\u5E26\u544A\u8B66\u6D41\u6C34\u53F7\u5B57\u6BB5\u7684\u6B63\u5219\uFF0C\u6D41\u6C34\u53F7\u5FC5\u987B\u9700\u4F7F\u7528\u51FD\u6570${id}\uFF0C\u6BD4\u5982 alarmSequenceId\":(.*)\r\nheartInterval\uFF1A\u5FC3\u8DF3\u95F4\u9694\uFF0C\u5355\u4F4D\u6BEB\u79D2\r\ncharset\uFF1A\u5B57\u7B26\u7F16\u7801\r\n\r\n\r\n\r\n\r\n\u5907\u6CE8\uFF1A\u82E5\u5728\u4F7F\u7528InfoSend\u4E2D\u5B58\u5728\u95EE\u9898\u6216BUG\uFF0C\u6B22\u8FCE\u8054\u7CFB\u4F5C\u8005\uFF0C\u624B\u673A/\u5FAE\u4FE1\uFF1A18706768036\r\n");
        description.setBounds(14, 143, 758, 336);
        InfoSend.getContentPane().add(description);

        JEditorPane description_title = new JEditorPane();
        description_title.setText("\u7535\u4FE1I1\u544A\u8B66\u63A5\u53E3");
        description_title.setFont(new Font("微软雅黑 Light", Font.BOLD, 20));
        description_title.setBounds(14, 91, 155, 48);
        InfoSend.getContentPane().add(description_title);

    }

    /**
     * 获取北向接口对象
     *
     * @return 北向接口对象
     */
    public static I1AlarmReadPage initInterfacePage() {
        return northAlarmReadPage;
    }

    /**
     * 打开北向接口界面，每次获取时，更新接口选择框显示
     */
    public void getInterfacePage() {
        InfoSend.setVisible(true);

    }
}

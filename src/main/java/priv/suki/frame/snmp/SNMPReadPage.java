package priv.suki.frame.snmp;

import javax.swing.JFrame;
import java.awt.Font;
import java.awt.Color;
import java.awt.Dialog.ModalExclusionType;
import java.awt.Panel;
import java.awt.Canvas;

import priv.suki.frame.util.FrameHelper;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import java.awt.Toolkit;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.swing.JEditorPane;

/**
 * SNMP接口参数说明界面
 *
 * @author 花小琪
 * @version 1.0
 */
public class SNMPReadPage {
    // Log
//	private static Log log = LogFactory.getLog(SNMPReadPage.class);
    private static SNMPReadPage snmpReadPage = new SNMPReadPage();
    private static JFrame InfoSend;

    /**
     * SNMPReadPage构造模块，初始化界面
     */
    public SNMPReadPage() {
        Thread.currentThread().setName("SNMPReadPage");
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
        InfoSend.setTitle("InfoSend_v1.0.0");
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
            @Override
            public void actionPerformed(ActionEvent e) {
                SNMPPage.initInterfacePage().getInterfacePage();
                InfoSend.setVisible(false);
            }
        });
        btnNewButton_return.setFont(new Font("Trebuchet MS", Font.PLAIN, 15));
        btnNewButton_return.setBounds(0, 0, 201, 48);
        InfoSend.getContentPane().add(btnNewButton_return);

        JEditorPane description = new JEditorPane();
        description.setFont(new Font("微软雅黑 Light", Font.PLAIN, 16));
        description.setText("\u63A5\u53E3\u8BF4\u660E\uFF1ASNMP\u63A5\u53E3\u6A21\u62DF\uFF0C\u652F\u6301V1\uFF0CV2\uFF0CV3\u4E09\u79CD\u7248\u672C\uFF0C\u754C\u9762\u53F3\u4FA7\u914D\u7F6Etrap\u6D88\u606F\uFF0C\u5B9A\u4E49\u6BCF\u4E00\u4E2A\u5B57\u6BB5\u7684oid\uFF0C\u53D8\u91CF\u503C\u548C\u7C7B\u578B\uFF08OctetString\u5B57\u7B26\u7C7B\uFF0CInteger\u6574\u6570\u7C7B\uFF0CTimeTicks\u65F6\u95F4\u6233\uFF09\r\n\u53C2\u6570\u8BF4\u660E\uFF1A\r\nversion\uFF1A\u7248\u672C\u53F7\uFF08\u53EF\u9009V1\uFF0CV2\uFF0CV3\uFF09\uFF0C\u9009\u62E9V3\u540E\u533A\u5206\u662F\u5426\u52A0\u5BC6\r\ntrapOid\uFF1ASNMP\u544A\u8B66\u7684trap_oid\uFF0C\u5FC5\u586B,\u5E76\u7531\u6B64\u4F5C\u4E3A\u53D8\u91CFoid\u7684\u5F00\u5934(.1.3.6)\r\nport\uFF1A\u9700\u8981\u53D1\u9001\u6D88\u606F\u7684\u7AEF\u53E3\r\nip\uFF1A\u9700\u8981\u53D1\u9001\u6D88\u606F\u7684ip\r\ncharset\uFF1A\u5B57\u7B26\u7F16\u7801\r\nsnmp_auth\uFF1A\uFF08V3\uFF09\u662F\u5426\u5F00\u542F\u52A0\u5BC6\u6A21\u5F0F\r\nusername\uFF1A\u767B\u9646\u7528\u6237\u540D\r\nauthPass\uFF1A\u767B\u9646\u5BC6\u7801\r\n\u5907\u6CE8\uFF1A\u82E5\u5728\u4F7F\u7528InfoSend\u4E2D\u5B58\u5728\u95EE\u9898\u6216BUG\uFF0C\u6B22\u8FCE\u8054\u7CFB\u4F5C\u8005\uFF0C\u624B\u673A/\u5FAE\u4FE1\uFF1A18706768036\r\n");
        description.setBounds(14, 143, 758, 303);
        InfoSend.getContentPane().add(description);

        JEditorPane description_title = new JEditorPane();
        description_title.setText("SNMP");
        description_title.setFont(new Font("微软雅黑 Light", Font.BOLD, 20));
        description_title.setBounds(14, 91, 155, 48);
        InfoSend.getContentPane().add(description_title);

    }

    /**
     * 获取SNMPReadPage对象
     *
     * @return SNMPReadPage对象
     */
    public static SNMPReadPage initInterfacePage() {
        return snmpReadPage;
    }

    /**
     * 打开SNMPReadPage界面，每次获取时，更新接口选择框显示
     */
    public void getInterfacePage() {
        InfoSend.setVisible(true);

    }
}

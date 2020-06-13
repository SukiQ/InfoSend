package priv.suki.frame.kafka;

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
 * Kafka参数说明界面
 *
 * @author 花小琪
 * @version 1.0
 */
public class KafkaReadPage {
    // Log
    private static Log log = LogFactory.getLog(KafkaReadPage.class);
    private static KafkaReadPage kafkaReadPage = new KafkaReadPage();
    private static JFrame InfoSend;

    /**
     * Kafka构造模块，初始化界面
     */
    public KafkaReadPage() {
        Thread.currentThread().setName("Kafka");
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
                KafkaPage.initInterfacePage().getInterfacePage();
                InfoSend.setVisible(false);
            }
        });
        btnNewButton_return.setFont(new Font("Trebuchet MS", Font.PLAIN, 15));
        btnNewButton_return.setBounds(0, 0, 201, 48);
        InfoSend.getContentPane().add(btnNewButton_return);

        JEditorPane description = new JEditorPane();
        description.setFont(new Font("微软雅黑 Light", Font.PLAIN, 16));
        description.setText("\u63A5\u53E3\u8BF4\u660E\uFF1AKafka\u5BA2\u6237\u7AEF\u63A5\u53E3\u6A21\u62DF\uFF0C\u6CE8\uFF1A\u8FDE\u63A5kafka\u673A\u5668\u9700\u8981\u5728hosts\u6587\u4EF6\u4E2D\u914D\u7F6Ezookeeper\u96C6\u7FA4\u53CAkafka\u96C6\u7FA4\u7684\u4E3B\u673A\u540D\uFF08\u7528root\u6743\u9650\u66F4\u6539\uFF09\uFF0Cwindows\u673A\u5668hosts\u6587\u4EF6\u5728c:/windows/system32/drivers/etc\u76EE\u5F55\u4E0B\u3002\u540C\u65F6\u4E5F\u9700\u8981\u5173\u95ED\u670D\u52A1\u7AEF\u7684\u9632\u706B\u5899\uFF0C\u901A\u8FC7\u547D\u4EE4service iptables stop \u6216\u662Fsystemctl stop firewalld\r\n\u53C2\u6570\u8BF4\u660E\uFF1A\r\nzookeeper\uFF1Azookeeper\u96C6\u7FA4\u5730\u5740\r\nBootstrap\uFF1Akafka\u96C6\u7FA4\u5730\u5740\r\ntopic\uFF1Atopic\u540D\u79F0\r\ncharset\uFF1A\u5B57\u7B26\u7F16\u7801\r\nParitition\uFF1A\u53D1\u9001\u5206\u533A\u603B\u6570\uFF0C\u53EF\u4EE5\u4E0D\u586B\uFF0C\u8868\u793A\u53EA\u53D1\u52300\u5206\u533A\r\n\u5907\u6CE8\uFF1A\u82E5\u5728\u4F7F\u7528InfoSend\u4E2D\u5B58\u5728\u95EE\u9898\u6216BUG\uFF0C\u6B22\u8FCE\u8054\u7CFB\u4F5C\u8005\uFF0C\u624B\u673A/\u5FAE\u4FE1\uFF1A18706768036\r\n");
        description.setBounds(14, 143, 758, 303);
        InfoSend.getContentPane().add(description);

        JEditorPane description_title = new JEditorPane();
        description_title.setText("Kafka");
        description_title.setFont(new Font("微软雅黑 Light", Font.BOLD, 20));
        description_title.setBounds(14, 91, 155, 48);
        InfoSend.getContentPane().add(description_title);

    }

    /**
     * 获取Kafka对象
     *
     * @return Kafka对象
     */
    public static KafkaReadPage initInterfacePage() {
        return kafkaReadPage;
    }

    /**
     * 打开Kafka界面，每次获取时，更新接口选择框显示
     */
    public void getInterfacePage() {
        InfoSend.setVisible(true);

    }
}

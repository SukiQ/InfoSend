package priv.suki.frame.kafka;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;

import java.awt.Font;
import java.awt.Color;
import java.awt.Dialog.ModalExclusionType;
import java.awt.Panel;
import java.awt.Canvas;
import javax.swing.JScrollPane;

import priv.suki.controller.ContralCenter;
import priv.suki.frame.util.FrameHelper;
import priv.suki.util.FrameUtil;
import priv.suki.util.InterfaceEnum;
import priv.suki.util.Propert;

import javax.swing.JOptionPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.awt.event.ItemEvent;
import javax.swing.JButton;
import javax.swing.JTextArea;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.Label;

/**
 * socket_client接口参数配置界面
 *
 * @author 花小琪
 * @version 1.0
 */
public class KafkaPage {
    // Log
    private static final Log log = LogFactory.getLog(KafkaPage.class);
    private static final KafkaPage KAFKAPAGE = new KafkaPage();
    private static JFrame InfoSend;
    private static JComboBox comboBox_interface;
    private static JTextArea textArea_info;
    private static JTextArea textArea_kafka;
    private static JTextArea textArea_charset;
    private static JTextArea textArea_zk;
    private static JTextArea textArea_topic;
    private static JTextArea textArea_paritition;

    /**
     * KafkaPage构造模块，初始化界面
     */
    public KafkaPage() {
        Thread.currentThread().setName("KafkaPage");
        initialize();
    }

    /**
     * 初始化界面
     */
    private void initialize() {
        InfoSend = new JFrame();
        InfoSend.setIconImage(Toolkit.getDefaultToolkit()
                .getImage(FrameHelper.MARK_ICON_IMAGE_PATH));
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

        comboBox_interface = new JComboBox();
        comboBox_interface.setBounds(0, 0, 199, 48);

        /**
         * 接口选择框监听
         */
        comboBox_interface.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                ContralCenter.getContral().setSelInterface(e.getItem().toString());
                InfoSend.setVisible(false);
            }
            ;
        });
        comboBox_interface.setToolTipText("");
        comboBox_interface.setBackground(Color.WHITE);
        comboBox_interface.setForeground(Color.BLACK);
        comboBox_interface.setFont(new Font("微软雅黑 Light", Font.PLAIN, 13));
        comboBox_interface.setModel(new DefaultComboBoxModel(FrameUtil.getInfArray(InterfaceEnum.KAFKA.getCName())));

        Canvas canvas = new Canvas();
        canvas.setBounds(0, 0, 0, 0);
        canvas.setBackground(new Color(0, 0, 0));
        InfoSend.getContentPane().setLayout(null);

        JButton button = new JButton("R");
        button.addActionListener(e -> {
            KafkaReadPage.initInterfacePage().getInterfacePage();
            InfoSend.setVisible(false);
        });
        button.setToolTipText("\u53C2\u6570\u8BF4\u660E");
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Trebuchet MS", Font.PLAIN, 18));
        button.setBackground(FrameHelper.MAIN_TONE);
        button.setBounds(715, 0, 84, 48);
        InfoSend.getContentPane().add(button);
        InfoSend.getContentPane().add(comboBox_interface);
        InfoSend.getContentPane().add(canvas);

        textArea_info = new JTextArea();
        JScrollPane js = new JScrollPane(textArea_info);
        js.setBounds(251, 74, 531, 320);
        js.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        js.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        textArea_info.setFont(new Font("微软雅黑 Light", Font.PLAIN, 16));
        textArea_info.setBackground(SystemColor.control);
        InfoSend.getContentPane().add(js);

        Panel panel = new Panel();
        panel.setBounds(200, 0, 599, 48);
        panel.setBackground(FrameHelper.MAIN_TONE);
        InfoSend.getContentPane().add(panel);

        JButton btnNewButtonSend = new JButton("SEND");
        btnNewButtonSend.setBackground(Color.WHITE);

        // SEND按钮监听//
        btnNewButtonSend.addActionListener(e -> {
            try {

                /* 参数赋值 */
                ContralCenter.getContral().setParam("msg", "string", textArea_info.getText(),
                        Propert.PARAM_CANT_BE_EMPTY);
                ContralCenter.getContral().setParam("zookUrl", "string", textArea_zk.getText(),
                        Propert.PARAM_CAN_BE_EMPTY);
                ContralCenter.getContral().setParam("kafkaUrl", "string", textArea_kafka.getText(),
                        Propert.PARAM_CAN_BE_EMPTY);
                ContralCenter.getContral().setParam("topic", "string", textArea_topic.getText(),
                        Propert.PARAM_CANT_BE_EMPTY);
                ContralCenter.getContral().setParam("charset", "string", textArea_charset.getText(),
                        Propert.PARAM_CAN_BE_EMPTY);
                ContralCenter.getContral().setParam("paritition", "int", textArea_paritition.getText(),
                        Propert.PARAM_CAN_BE_EMPTY);
                /* 调用下一个界面，隐藏当前界面 */
                KafkaSend.initInterfacePage().getInterfacePage();
                InfoSend.setVisible(false);
            } catch (Throwable t) {
                log.error("提交参数异常", t);

                /* 设置报错弹窗 */
                JOptionPane.showConfirmDialog(InfoSend, t.getMessage(), "Error", JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.ERROR_MESSAGE);
            }

        });

        btnNewButtonSend.setBounds(48, 414, 141, 61);
        btnNewButtonSend.setFont(new Font("微软雅黑 Light", Font.PLAIN, 20));
        InfoSend.getContentPane().add(btnNewButtonSend);

        Label labelKafka = new Label("Bootstrap");
        labelKafka.setBounds(0, 138, 90, 36);
        labelKafka.setForeground(Color.WHITE);
        labelKafka.setAlignment(Label.CENTER);
        labelKafka.setFont(new Font("微软雅黑 Light", Font.BOLD, 13));
        labelKafka.setBackground(FrameHelper.LABEL_TONE);
        InfoSend.getContentPane().add(labelKafka);

        textArea_kafka = new JTextArea();
        textArea_kafka.setLineWrap(true);
        textArea_kafka.setFont(new Font("微软雅黑 Light", Font.PLAIN, 13));
        textArea_kafka.setBackground(SystemColor.menu);
        textArea_kafka.setBounds(100, 138, 105, 36);
        InfoSend.getContentPane().add(textArea_kafka);

        Label label_charset = new Label("Charset");
        label_charset.setForeground(Color.WHITE);
        label_charset.setFont(new Font("微软雅黑 Light", Font.BOLD, 13));
        label_charset.setBackground(FrameHelper.LABEL_TONE);
        label_charset.setAlignment(Label.CENTER);
        label_charset.setBounds(0, 222, 90, 36);
        InfoSend.getContentPane().add(label_charset);

        textArea_charset = new JTextArea();
        textArea_charset.setText("UTF-8");
        textArea_charset.setLineWrap(true);
        textArea_charset.setFont(new Font("微软雅黑 Light", Font.PLAIN, 16));
        textArea_charset.setBackground(SystemColor.menu);
        textArea_charset.setBounds(100, 222, 105, 36);
        InfoSend.getContentPane().add(textArea_charset);

        Label label_zk = new Label("Zookeeper");
        label_zk.setForeground(Color.WHITE);
        label_zk.setFont(new Font("微软雅黑 Light", Font.BOLD, 13));
        label_zk.setBackground(FrameHelper.LABEL_TONE);
        label_zk.setAlignment(Label.CENTER);
        label_zk.setBounds(0, 96, 90, 36);
        InfoSend.getContentPane().add(label_zk);

        textArea_zk = new JTextArea();
        textArea_zk.setLineWrap(true);
        textArea_zk.setFont(new Font("微软雅黑 Light", Font.PLAIN, 13));
        textArea_zk.setBackground(SystemColor.menu);
        textArea_zk.setBounds(101, 97, 105, 36);
        InfoSend.getContentPane().add(textArea_zk);

        Label label_topic = new Label("Topic");
        label_topic.setForeground(Color.WHITE);
        label_topic.setFont(new Font("微软雅黑 Light", Font.BOLD, 13));
        label_topic.setBackground(FrameHelper.LABEL_TONE);
        label_topic.setAlignment(Label.CENTER);
        label_topic.setBounds(0, 180, 90, 36);
        InfoSend.getContentPane().add(label_topic);

        textArea_topic = new JTextArea();
        textArea_topic.setLineWrap(true);
        textArea_topic.setFont(new Font("微软雅黑 Light", Font.PLAIN, 16));
        textArea_topic.setBackground(SystemColor.menu);
        textArea_topic.setBounds(100, 180, 105, 36);
        InfoSend.getContentPane().add(textArea_topic);

        Label label_paritition = new Label("Paritition");
        label_paritition.setForeground(Color.WHITE);
        label_paritition.setFont(new Font("微软雅黑 Light", Font.BOLD, 13));
        label_paritition.setBackground(FrameHelper.LABEL_TONE);
        label_paritition.setAlignment(Label.CENTER);
        label_paritition.setBounds(-1, 264, 90, 36);
        InfoSend.getContentPane().add(label_paritition);

        textArea_paritition = new JTextArea();
        textArea_paritition.setLineWrap(true);
        textArea_paritition.setFont(new Font("微软雅黑 Light", Font.PLAIN, 16));
        textArea_paritition.setBackground(SystemColor.menu);
        textArea_paritition.setBounds(100, 264, 105, 36);
        InfoSend.getContentPane().add(textArea_paritition);

        JLabel lblNewLabel_logo = new JLabel("");
        lblNewLabel_logo.setIcon(new ImageIcon(FrameHelper.LOGO_ICON_IMAGE_PATH));
        lblNewLabel_logo.setBounds(651, 414, 134, 80);
        InfoSend.getContentPane().add(lblNewLabel_logo);

    }

    /**
     * 获取KafkaPage对象
     *
     * @return KafkaPage对象
     */
    public static KafkaPage initInterfacePage() {
        return KAFKAPAGE;
    }

    /**
     * 打开KafkaPage界面，每次获取时，更新接口选择框显示
     */
    public void getInterfacePage() {
        comboBox_interface.setModel(new DefaultComboBoxModel(FrameUtil.getInfArray(InterfaceEnum.KAFKA.getCName())));
        InfoSend.getContentPane().add(comboBox_interface);
        InfoSend.setVisible(true);

    }
}

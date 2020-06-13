package priv.suki.frame.activemq;

import javax.swing.JFrame;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
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
import javax.swing.JLabel;
import javax.swing.ImageIcon;

/**
 * ActiveMQ接口参数配置界面
 *
 * @author 花小琪
 * @version 1.0.2
 */
public class ActiveMQPage {
    /**
     * log
     */
    private static final Log log = LogFactory.getLog(ActiveMQPage.class);
    private static final ActiveMQPage ACTIVEMQ_PAGE = new ActiveMQPage();
    private static JFrame InfoSend;
    private static JComboBox comboBox_interface;
    private static JTextArea textArea_info;
    private static JTextArea textArea_qname;
    private static JTextArea textArea_charset;
    private static JTextArea textArea_url;
    private static JTextArea textArea_username;
    private static JTextArea textArea_pwd;

    /**
     * ActiveMQPage构造模块，初始化界面
     */
    public ActiveMQPage() {
        Thread.currentThread().setName("ActiveMQPage");
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

        //接口选择框监听//
        comboBox_interface.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                ContralCenter.getContral().setSelInterface(e.getItem().toString());
                InfoSend.setVisible(false);
            }
        });
        comboBox_interface.setToolTipText("");
        comboBox_interface.setBackground(Color.WHITE);
        comboBox_interface.setForeground(Color.BLACK);
        comboBox_interface.setFont(new Font("微软雅黑 Light", Font.PLAIN, 13));
        comboBox_interface.setModel(new DefaultComboBoxModel(FrameUtil.getInfArray(InterfaceEnum.ACTIVEMQ.getCName())));

        Canvas canvas = new Canvas();
        canvas.setBounds(0, 0, 0, 0);
        canvas.setBackground(new Color(0, 0, 0));
        InfoSend.getContentPane().setLayout(null);

        JButton button = new JButton("R");
        button.addActionListener(e -> {
            ActiveMQReadPage.initInterfacePage().getInterfacePage();
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

        //SEND按钮监听//
        btnNewButtonSend.addActionListener(e -> {
            try {

                /* 参数赋值 */
                ContralCenter.getContral().setParam("msg", "string", textArea_info.getText(),
                        Propert.PARAM_CANT_BE_EMPTY);
                ContralCenter.getContral().setParam("actmmq_url", "string", textArea_url.getText(),
                        Propert.PARAM_CANT_BE_EMPTY);
                ContralCenter.getContral().setParam("actmmq_qname", "string", textArea_qname.getText(),
                        Propert.PARAM_CANT_BE_EMPTY);
                ContralCenter.getContral().setParam("actmmq_username", "string", textArea_username.getText(),
                        Propert.PARAM_CANT_BE_EMPTY);
                ContralCenter.getContral().setParam("actmmq_password", "string", textArea_pwd.getText(),
                        Propert.PARAM_CANT_BE_EMPTY);
                ContralCenter.getContral().setParam("charset", "string", textArea_charset.getText(),
                        Propert.PARAM_CAN_BE_EMPTY);

                /* 调用下一个界面，隐藏当前界面 */
                ActiveMQSend.initInterfacePage().getInterfacePage();
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

        Label label_qname = new Label("Qname");
        label_qname.setBounds(0, 138, 76, 36);
        label_qname.setForeground(Color.WHITE);
        label_qname.setAlignment(Label.CENTER);
        label_qname.setFont(new Font("微软雅黑 Light", Font.BOLD, 13));
        label_qname.setBackground(FrameHelper.LABEL_TONE);
        InfoSend.getContentPane().add(label_qname);

        textArea_qname = new JTextArea();
        textArea_qname.setLineWrap(true);
        textArea_qname.setFont(new Font("微软雅黑 Light", Font.PLAIN, 16));
        textArea_qname.setBackground(SystemColor.menu);
        textArea_qname.setBounds(95, 138, 116, 36);
        InfoSend.getContentPane().add(textArea_qname);

        Label label_charset = new Label("Charset");
        label_charset.setForeground(Color.WHITE);
        label_charset.setFont(new Font("微软雅黑 Light", Font.BOLD, 13));
        label_charset.setBackground(FrameHelper.LABEL_TONE);
        label_charset.setAlignment(Label.CENTER);
        label_charset.setBounds(0, 264, 76, 36);
        InfoSend.getContentPane().add(label_charset);

        textArea_charset = new JTextArea();
        textArea_charset.setText("UTF-8");
        textArea_charset.setLineWrap(true);
        textArea_charset.setFont(new Font("微软雅黑 Light", Font.PLAIN, 16));
        textArea_charset.setBackground(SystemColor.menu);
        textArea_charset.setBounds(95, 264, 116, 36);
        InfoSend.getContentPane().add(textArea_charset);

        Label label_url = new Label("url");
        label_url.setForeground(Color.WHITE);
        label_url.setFont(new Font("微软雅黑 Light", Font.BOLD, 13));
        label_url.setBackground(FrameHelper.LABEL_TONE);
        label_url.setAlignment(Label.CENTER);
        label_url.setBounds(0, 96, 76, 36);
        InfoSend.getContentPane().add(label_url);

        textArea_url = new JTextArea();
        textArea_url.setLineWrap(true);
        textArea_url.setFont(new Font("微软雅黑 Light", Font.PLAIN, 16));
        textArea_url.setBackground(SystemColor.menu);
        textArea_url.setBounds(95, 96, 116, 36);
        InfoSend.getContentPane().add(textArea_url);

        JLabel lblNewLabel_logo = new JLabel("");
        lblNewLabel_logo.setIcon(
                new ImageIcon(FrameHelper.LOGO_ICON_IMAGE_PATH));
        lblNewLabel_logo.setBounds(651, 414, 134, 80);
        InfoSend.getContentPane().add(lblNewLabel_logo);

        Label label_username = new Label("username");
        label_username.setForeground(Color.WHITE);
        label_username.setFont(new Font("微软雅黑 Light", Font.BOLD, 13));
        label_username.setBackground(FrameHelper.LABEL_TONE);
        label_username.setAlignment(Label.CENTER);
        label_username.setBounds(0, 180, 76, 36);
        InfoSend.getContentPane().add(label_username);

        textArea_username = new JTextArea();
        textArea_username.setLineWrap(true);
        textArea_username.setFont(new Font("微软雅黑 Light", Font.PLAIN, 16));
        textArea_username.setBackground(SystemColor.menu);
        textArea_username.setBounds(95, 180, 116, 36);
        InfoSend.getContentPane().add(textArea_username);

        Label label_pwd = new Label("password");
        label_pwd.setForeground(Color.WHITE);
        label_pwd.setFont(new Font("微软雅黑 Light", Font.BOLD, 13));
        label_pwd.setBackground(FrameHelper.LABEL_TONE);
        label_pwd.setAlignment(Label.CENTER);
        label_pwd.setBounds(0, 222, 76, 36);
        InfoSend.getContentPane().add(label_pwd);

        textArea_pwd = new JTextArea();
        textArea_pwd.setLineWrap(true);
        textArea_pwd.setFont(new Font("微软雅黑 Light", Font.PLAIN, 16));
        textArea_pwd.setBackground(SystemColor.menu);
        textArea_pwd.setBounds(95, 222, 116, 36);
        InfoSend.getContentPane().add(textArea_pwd);

        /*R按钮监听*/

    }

    /**
     * 获取ActiveMQPage对象
     *
     * @return ActiveMQPage对象
     */
    public static ActiveMQPage initInterfacePage() {
        return ACTIVEMQ_PAGE;
    }

    /**
     * 打开ActiveMQPage界面，每次获取时，更新接口选择框显示
     */
    public void getInterfacePage() {
        comboBox_interface.setModel(new DefaultComboBoxModel(FrameUtil.getInfArray(InterfaceEnum.ACTIVEMQ.getCName())));
        InfoSend.getContentPane().add(comboBox_interface);
        InfoSend.setVisible(true);

    }
}

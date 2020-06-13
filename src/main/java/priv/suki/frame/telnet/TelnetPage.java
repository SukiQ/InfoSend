package priv.suki.frame.telnet;

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

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.ImageIcon;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import javax.swing.JTextArea;
import java.awt.Toolkit;
import java.awt.SystemColor;
import java.awt.Label;

/**
 * Telnet接口参数配置界面
 *
 * @author 花小琪
 * @version 1.0
 */
public class TelnetPage {
    // Log
    private static Log log = LogFactory.getLog(TelnetPage.class);
    private static TelnetPage TelnetPage = new TelnetPage();
    private static JFrame InfoSend;
    private static JComboBox comboBox_interface;
    private static JTextArea textArea_info;
    private static JTextArea textArea_ftp_user;
    private static JTextArea textArea_ftp_pwd;
    private static JTextArea textArea_charset;
    private static JTextArea textArea_ftp_ip;
    private static JTextArea textArea_ftp_source;
    private static JComboBox comboBox_type;

    /**
     * TelnetPage构造模块，初始化界面
     */
    public TelnetPage() {
        Thread.currentThread().setName("TelnetPage");
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
        comboBox_interface.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    ContralCenter.getContral().setSelInterface(e.getItem().toString());
                    InfoSend.setVisible(false);
//					InfoSend.validate();
                }
                ;
            }
        });
        comboBox_interface.setToolTipText("");
        comboBox_interface.setBackground(Color.WHITE);
        comboBox_interface.setForeground(Color.BLACK);
        comboBox_interface.setFont(new Font("微软雅黑 Light", Font.PLAIN, 13));
        comboBox_interface.setModel(new DefaultComboBoxModel(FrameUtil.getInfArray(InterfaceEnum.TELNET.getCName())));

        Canvas canvas = new Canvas();
        canvas.setBounds(0, 0, 0, 0);
        canvas.setBackground(new Color(0, 0, 0));
        InfoSend.getContentPane().setLayout(null);

        /*R按钮监听*/
        JButton button = new JButton("R");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TelnetReadPage.initInterfacePage().getInterfacePage();
                InfoSend.setVisible(false);
            }
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

        JButton btnNewButton_send = new JButton("SEND");
        btnNewButton_send.setBackground(Color.WHITE);

        /**
         * SEND按钮监听
         */
        btnNewButton_send.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {

                    /* 参数赋值 */
                    ContralCenter.getContral().setParam("msg", "string", textArea_info.getText(),
                            Propert.PARAM_CANT_BE_EMPTY);
                    ContralCenter.getContral().setParam("telnet_ftp_user", "string", textArea_ftp_user.getText(),
                            Propert.PARAM_CANT_BE_EMPTY);
                    ContralCenter.getContral().setParam("telnet_ftp_pwd", "string", textArea_ftp_pwd.getText(),
                            Propert.PARAM_CANT_BE_EMPTY);
                    ContralCenter.getContral().setParam("telnet_ftp_source", "string", textArea_ftp_source.getText(),
                            Propert.PARAM_CANT_BE_EMPTY);
                    ContralCenter.getContral().setParam("telnet_ftp_ip", "string", textArea_ftp_ip.getText(),
                            Propert.PARAM_CANT_BE_EMPTY);
                    ContralCenter.getContral().setParam("telnet_ftp_type", "int", String.valueOf(comboBox_type.getSelectedIndex()),
                            Propert.PARAM_CANT_BE_EMPTY);
                    ContralCenter.getContral().setParam("charset", "string", textArea_charset.getText(),
                            Propert.PARAM_CAN_BE_EMPTY);

                    /* 调用下一个界面，隐藏当前界面 */
                    TelnetSend.initInterfacePage().getInterfacePage();
                    InfoSend.setVisible(false);
                } catch (Throwable t) {
                    log.error("提交参数异常", t);

                    /* 设置报错弹窗 */
                    JOptionPane.showConfirmDialog(InfoSend, t.getMessage(), "Error", JOptionPane.OK_CANCEL_OPTION,
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
        });

        btnNewButton_send.setBounds(48, 414, 141, 61);
        btnNewButton_send.setFont(new Font("微软雅黑 Light", Font.PLAIN, 20));
        InfoSend.getContentPane().add(btnNewButton_send);

        Label label_ftp_user = new Label("FtpUser");
        label_ftp_user.setBounds(0, 94, 89, 36);
        label_ftp_user.setForeground(Color.WHITE);
        label_ftp_user.setAlignment(Label.CENTER);
        label_ftp_user.setFont(new Font("微软雅黑 Light", Font.BOLD, 13));
        label_ftp_user.setBackground(FrameHelper.LABEL_TONE);
        InfoSend.getContentPane().add(label_ftp_user);

        textArea_ftp_user = new JTextArea();
        textArea_ftp_user.setLineWrap(true);
        textArea_ftp_user.setFont(new Font("微软雅黑 Light", Font.PLAIN, 16));
        textArea_ftp_user.setBackground(SystemColor.menu);
        textArea_ftp_user.setBounds(111, 96, 119, 34);
        InfoSend.getContentPane().add(textArea_ftp_user);

        Label label_charset = new Label("Charset");
        label_charset.setForeground(Color.WHITE);
        label_charset.setFont(new Font("微软雅黑 Light", Font.BOLD, 13));
        label_charset.setBackground(FrameHelper.LABEL_TONE);
        label_charset.setAlignment(Label.CENTER);
        label_charset.setBounds(0, 304, 89, 36);
        InfoSend.getContentPane().add(label_charset);

        textArea_charset = new JTextArea();
        textArea_charset.setText("UTF-8");
        textArea_charset.setLineWrap(true);
        textArea_charset.setFont(new Font("微软雅黑 Light", Font.PLAIN, 16));
        textArea_charset.setBackground(SystemColor.menu);
        textArea_charset.setBounds(111, 304, 119, 36);
        InfoSend.getContentPane().add(textArea_charset);

        JLabel lblNewLabel_logo = new JLabel("");
        lblNewLabel_logo.setIcon(new ImageIcon(FrameHelper.LOGO_ICON_IMAGE_PATH));
        lblNewLabel_logo.setBounds(651, 414, 134, 80);
        InfoSend.getContentPane().add(lblNewLabel_logo);

        Label label_ftp_pwd = new Label("FtpPassword");
        label_ftp_pwd.setForeground(Color.WHITE);
        label_ftp_pwd.setFont(new Font("微软雅黑 Light", Font.BOLD, 13));
        label_ftp_pwd.setBackground(FrameHelper.LABEL_TONE);
        label_ftp_pwd.setAlignment(Label.CENTER);
        label_ftp_pwd.setBounds(0, 136, 89, 36);
        InfoSend.getContentPane().add(label_ftp_pwd);

        textArea_ftp_pwd = new JTextArea();
        textArea_ftp_pwd.setLineWrap(true);
        textArea_ftp_pwd.setFont(new Font("微软雅黑 Light", Font.PLAIN, 16));
        textArea_ftp_pwd.setBackground(SystemColor.menu);
        textArea_ftp_pwd.setBounds(111, 136, 119, 34);
        InfoSend.getContentPane().add(textArea_ftp_pwd);

        Label label_ftp_source = new Label("FtpSource");
        label_ftp_source.setForeground(Color.WHITE);
        label_ftp_source.setFont(new Font("微软雅黑 Light", Font.BOLD, 13));
        label_ftp_source.setBackground(FrameHelper.LABEL_TONE);
        label_ftp_source.setAlignment(Label.CENTER);
        label_ftp_source.setBounds(0, 178, 89, 36);
        InfoSend.getContentPane().add(label_ftp_source);

        textArea_ftp_source = new JTextArea();
        textArea_ftp_source.setLineWrap(true);
        textArea_ftp_source.setFont(new Font("微软雅黑 Light", Font.PLAIN, 16));
        textArea_ftp_source.setBackground(SystemColor.menu);
        textArea_ftp_source.setBounds(111, 178, 119, 34);
        InfoSend.getContentPane().add(textArea_ftp_source);

        Label label_ftp_ip = new Label("FtpIp");
        label_ftp_ip.setForeground(Color.WHITE);
        label_ftp_ip.setFont(new Font("微软雅黑 Light", Font.BOLD, 13));
        label_ftp_ip.setBackground(FrameHelper.LABEL_TONE);
        label_ftp_ip.setAlignment(Label.CENTER);
        label_ftp_ip.setBounds(0, 220, 89, 36);
        InfoSend.getContentPane().add(label_ftp_ip);

        textArea_ftp_ip = new JTextArea();
        textArea_ftp_ip.setLineWrap(true);
        textArea_ftp_ip.setFont(new Font("微软雅黑 Light", Font.PLAIN, 16));
        textArea_ftp_ip.setBackground(SystemColor.menu);
        textArea_ftp_ip.setBounds(111, 220, 119, 34);
        InfoSend.getContentPane().add(textArea_ftp_ip);

        Label label_filetype = new Label("FileType");
        label_filetype.setForeground(Color.WHITE);
        label_filetype.setFont(new Font("微软雅黑 Light", Font.BOLD, 13));
        label_filetype.setBackground(FrameHelper.LABEL_TONE);
        label_filetype.setAlignment(Label.CENTER);
        label_filetype.setBounds(0, 262, 89, 36);
        InfoSend.getContentPane().add(label_filetype);

        comboBox_type = new JComboBox();
        comboBox_type.setBackground(Color.WHITE);
        comboBox_type.setFont(new Font("等线 Light", Font.PLAIN, 16));
        comboBox_type.setModel(new DefaultComboBoxModel(new String[]{"txt", "csv"}));
        comboBox_type.setBounds(111, 262, 119, 34);
        InfoSend.getContentPane().add(comboBox_type);

    }

    /**
     * 获取TelnetPage对象
     *
     * @return TelnetPage对象
     */
    public static TelnetPage initInterfacePage() {
        return TelnetPage;
    }

    /**
     * 打开TelnetPage界面，每次获取时，更新接口选择框显示
     */
    public void getInterfacePage() {
        comboBox_interface.setModel(new DefaultComboBoxModel(FrameUtil.getInfArray(InterfaceEnum.TELNET.getCName())));
        InfoSend.getContentPane().add(comboBox_interface);
        InfoSend.setVisible(true);
    }
}

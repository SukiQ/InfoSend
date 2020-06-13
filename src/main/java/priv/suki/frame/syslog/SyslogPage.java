package priv.suki.frame.syslog;

import priv.suki.controller.ContralCenter;
import priv.suki.frame.util.FrameHelper;
import priv.suki.util.FrameUtil;
import priv.suki.util.InterfaceEnum;
import priv.suki.util.Propert;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.Dialog.ModalExclusionType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;

/**
 * syslog接口参数配置界面
 *
 * @author 花小琪
 * @version 1.0.4
 */
public class SyslogPage {
    /**
     * log
     */
    private static final Log log = LogFactory.getLog(SyslogPage.class);
    private static SyslogPage syslogPage = new SyslogPage();
    private static JFrame InfoSend;
    private static JComboBox comboBox_interface;
    private static JTextArea textArea_info;
    private static JTextArea textArea_port;
    private static JTextArea textArea_charset;
    private static JComboBox comboBox_syslog_type;
    private static JTextArea textArea_target_port;
    private static JTextArea textArea_target_host;

    /**
     * SyslogPage构造模块，初始化界面
     */
    public SyslogPage() {
        Thread.currentThread().setName("SyslogPage");
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
        });
        comboBox_interface.setToolTipText("");
        comboBox_interface.setBackground(Color.WHITE);
        comboBox_interface.setForeground(Color.BLACK);
        comboBox_interface.setFont(new Font("微软雅黑 Light", Font.PLAIN, 13));
        comboBox_interface.setModel(new DefaultComboBoxModel(FrameUtil.getInfArray(InterfaceEnum.SYSLOG.getCName())));

        Canvas canvas = new Canvas();
        canvas.setBounds(0, 0, 0, 0);
        canvas.setBackground(new Color(0, 0, 0));
        InfoSend.getContentPane().setLayout(null);

        JButton button = new JButton("R");
        button.addActionListener(e -> {
            SyslogReadPage.initInterfacePage().getInterfacePage();
            InfoSend.setVisible(false);
        });

        button.setFont(new Font("Trebuchet MS", Font.PLAIN, 18));
        button.setForeground(new Color(255, 255, 255));
        button.setBackground(FrameHelper.MAIN_TONE);
        button.setToolTipText("\u53C2\u6570\u8BF4\u660E");
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
        btnNewButtonSend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    /* 参数赋值 */
                    ContralCenter.getContral().setParam("msg", "string", textArea_info.getText(),
                            Propert.PARAM_CANT_BE_EMPTY);
                    ContralCenter.getContral().setParam("syslog_type", "string", comboBox_syslog_type.getSelectedItem().toString(),
                            Propert.PARAM_CANT_BE_EMPTY);
                    ContralCenter.getContral().setParam("syslog_port", "int", textArea_port.getText(),
                            Propert.PARAM_CAN_BE_EMPTY);
                    ContralCenter.getContral().setParam("syslog_target_host", "string", textArea_target_host.getText(),
                            Propert.PARAM_CANT_BE_EMPTY);
                    ContralCenter.getContral().setParam("syslog_target_port", "int", textArea_target_port.getText(),
                            Propert.PARAM_CANT_BE_EMPTY);
                    ContralCenter.getContral().setParam("charset", "string", textArea_charset.getText(),
                            Propert.PARAM_CAN_BE_EMPTY);

                    /* 调用下一个界面，隐藏当前界面 */
                    SyslogSend.initInterfacePage().getInterfacePage();
                    InfoSend.setVisible(false);
                } catch (Throwable t) {
                    log.error("提交参数异常", t);

                    /* 设置报错弹窗 */
                    JOptionPane.showConfirmDialog(InfoSend, t.getMessage(), "Error", JOptionPane.OK_CANCEL_OPTION,
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnNewButtonSend.setBounds(48, 414, 141, 61);
        btnNewButtonSend.setFont(new Font("微软雅黑 Light", Font.PLAIN, 20));
        InfoSend.getContentPane().add(btnNewButtonSend);

        Label label_port = new Label("Port");
        label_port.setBounds(0, 140, 76, 36);
        label_port.setForeground(Color.WHITE);
        label_port.setAlignment(Label.CENTER);
        label_port.setFont(new Font("微软雅黑 Light", Font.BOLD, 13));
        label_port.setBackground(FrameHelper.LABEL_TONE);
        InfoSend.getContentPane().add(label_port);

        textArea_port = new JTextArea();
        textArea_port.setLineWrap(true);
        textArea_port.setFont(new Font("微软雅黑 Light", Font.PLAIN, 16));
        textArea_port.setBackground(SystemColor.menu);
        textArea_port.setBounds(95, 140, 117, 36);
        InfoSend.getContentPane().add(textArea_port);

        Label label_charset = new Label("Charset");
        label_charset.setForeground(Color.WHITE);
        label_charset.setFont(new Font("微软雅黑 Light", Font.BOLD, 13));
        label_charset.setBackground(FrameHelper.LABEL_TONE);
        label_charset.setAlignment(Label.CENTER);
        label_charset.setBounds(0, 266, 76, 36);
        InfoSend.getContentPane().add(label_charset);

        textArea_charset = new JTextArea();
        textArea_charset.setText("UTF-8");
        textArea_charset.setLineWrap(true);
        textArea_charset.setFont(new Font("微软雅黑 Light", Font.PLAIN, 16));
        textArea_charset.setBackground(SystemColor.menu);
        textArea_charset.setBounds(95, 266, 117, 36);
        InfoSend.getContentPane().add(textArea_charset);

        JLabel lblNewLabel_logo = new JLabel("");
        lblNewLabel_logo.setIcon(new ImageIcon(FrameHelper.LOGO_ICON_IMAGE_PATH));
        lblNewLabel_logo.setBounds(651, 414, 134, 80);
        InfoSend.getContentPane().add(lblNewLabel_logo);

        Label label_syslog_target_port = new Label("targetPort");
        label_syslog_target_port.setForeground(Color.WHITE);
        label_syslog_target_port.setFont(new Font("微软雅黑 Light", Font.BOLD, 13));
        label_syslog_target_port.setBackground(FrameHelper.LABEL_TONE);
        label_syslog_target_port.setAlignment(Label.CENTER);
        label_syslog_target_port.setBounds(0, 224, 76, 36);
        InfoSend.getContentPane().add(label_syslog_target_port);

        textArea_target_port = new JTextArea();
        textArea_target_port.setLineWrap(true);
        textArea_target_port.setFont(new Font("微软雅黑 Light", Font.PLAIN, 16));
        textArea_target_port.setBackground(SystemColor.menu);
        textArea_target_port.setBounds(95, 224, 117, 36);
        InfoSend.getContentPane().add(textArea_target_port);

        Label label_syslog_target_host = new Label("targetHost");
        label_syslog_target_host.setForeground(Color.WHITE);
        label_syslog_target_host.setFont(new Font("微软雅黑 Light", Font.BOLD, 13));
        label_syslog_target_host.setBackground(FrameHelper.LABEL_TONE);
        label_syslog_target_host.setAlignment(Label.CENTER);
        label_syslog_target_host.setBounds(0, 182, 76, 36);
        InfoSend.getContentPane().add(label_syslog_target_host);

        textArea_target_host = new JTextArea();
        textArea_target_host.setLineWrap(true);
        textArea_target_host.setFont(new Font("微软雅黑 Light", Font.PLAIN, 16));
        textArea_target_host.setBackground(SystemColor.menu);
        textArea_target_host.setBounds(95, 182, 117, 36);
        InfoSend.getContentPane().add(textArea_target_host);

        Label label_syslog_type = new Label("Type");
        label_syslog_type.setForeground(Color.WHITE);
        label_syslog_type.setFont(new Font("微软雅黑 Light", Font.BOLD, 13));
        label_syslog_type.setBackground(FrameHelper.LABEL_TONE);
        label_syslog_type.setAlignment(Label.CENTER);
        label_syslog_type.setBounds(0, 98, 76, 36);
        InfoSend.getContentPane().add(label_syslog_type);

        comboBox_syslog_type = new JComboBox();
        comboBox_syslog_type.setModel(new DefaultComboBoxModel(new String[]{"server", "client"}));
        comboBox_syslog_type.setFont(new Font("等线 Light", Font.PLAIN, 14));
        comboBox_syslog_type.setBackground(Color.WHITE);
        comboBox_syslog_type.setBounds(95, 98, 117, 36);
        InfoSend.getContentPane().add(comboBox_syslog_type);

        /*R按钮监听*/

    }

    /**
     * 获取SyslogPage对象
     *
     * @return SyslogPage对象
     */
    public static SyslogPage initInterfacePage() {
        return syslogPage;
    }

    /**
     * 打开SyslogPage界面，每次获取时，更新接口选择框显示
     */
    public void getInterfacePage() {
        comboBox_interface.setModel(new DefaultComboBoxModel(FrameUtil.getInfArray(InterfaceEnum.SYSLOG.getCName())));
        InfoSend.getContentPane().add(comboBox_interface);
        InfoSend.setVisible(true);

    }
}

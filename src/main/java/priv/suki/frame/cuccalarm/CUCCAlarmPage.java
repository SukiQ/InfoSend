package priv.suki.frame.cuccalarm;

import priv.suki.controller.ContralCenter;
import priv.suki.frame.util.FrameHelper;
import priv.suki.util.FrameUtil;
import priv.suki.util.InterfaceEnum;
import priv.suki.util.Propert;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import priv.suki.frame.cuccalarm.CUCCAlarmReadPage;
import priv.suki.frame.cuccalarm.CUCCAlarmSend;

import javax.swing.*;
import java.awt.*;
import java.awt.Dialog.ModalExclusionType;
import java.awt.event.ItemEvent;

/**
 * CUCC告警参数配置界面
 *
 * @author 花小琪
 * @version 1.0.4
 */
public class CUCCAlarmPage {
    /**
     * log
     */
    private static final Log log = LogFactory.getLog(CUCCAlarmPage.class);
    private static final CUCCAlarmPage CUCC_ALARM_PAGE = new CUCCAlarmPage();
    private static JFrame InfoSend;
    private static JComboBox comboBox_interface;
    private static JTextArea textArea_info;
    private static JTextArea textArea_port;
    private static JTextArea textArea_user;
    private static JTextArea textArea_pwd;
    private static JTextArea textArea_charset;

    /**
     * CUCCAlarmPage构造模块，初始化界面
     */
    public CUCCAlarmPage() {
        Thread.currentThread().setName("CUCCAlarmPage");
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

        //接口选择框监听
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
        comboBox_interface.setModel(new DefaultComboBoxModel(FrameUtil.getInfArray(InterfaceEnum.CUCC.getCName())));

        Canvas canvas = new Canvas();
        canvas.setBounds(0, 0, 0, 0);
        canvas.setBackground(new Color(0, 0, 0));
        InfoSend.getContentPane().setLayout(null);

        JButton button = new JButton("R");

        /* R按钮监听 */
        button.addActionListener(e -> {
            CUCCAlarmReadPage.initInterfacePage().getInterfacePage();
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
                ContralCenter.getContral().setParam("cucc_alarm_user", "string", textArea_user.getText(),
                        Propert.PARAM_CANT_BE_EMPTY);
                ContralCenter.getContral().setParam("cucc_alarm_pwd", "string", textArea_pwd.getText(),
                        Propert.PARAM_CANT_BE_EMPTY);
                ContralCenter.getContral().setParam("cucc_alarm_port", "int", textArea_port.getText(),
                        Propert.PARAM_CANT_BE_EMPTY);
                ContralCenter.getContral().setParam("charset", "string", textArea_charset.getText(),
                        Propert.PARAM_CAN_BE_EMPTY);

                /* 调用下一个界面，隐藏当前界面 */
                CUCCAlarmSend.initInterfacePage().getInterfacePage();
                InfoSend.setVisible(false);
            } catch (Throwable t) {
                log.error("提交参数异常", t);

                /* 设置报错弹窗 */
                JOptionPane.showConfirmDialog(InfoSend, t.getMessage(), "Error", JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

        });

        btnNewButtonSend.setBounds(48, 414, 141, 61);
        btnNewButtonSend.setFont(new Font("微软雅黑 Light", Font.PLAIN, 20));
        InfoSend.getContentPane().add(btnNewButtonSend);

        Label labelPort = new Label("Port");
        labelPort.setBounds(0, 92, 76, 36);
        labelPort.setForeground(Color.WHITE);
        labelPort.setAlignment(Label.CENTER);
        labelPort.setFont(new Font("微软雅黑 Light", Font.BOLD, 13));
        labelPort.setBackground(FrameHelper.LABEL_TONE);
        InfoSend.getContentPane().add(labelPort);

        textArea_port = new JTextArea();
        textArea_port.setLineWrap(true);
        textArea_port.setFont(new Font("微软雅黑 Light", Font.PLAIN, 16));
        textArea_port.setBackground(SystemColor.menu);
        textArea_port.setBounds(95, 92, 106, 36);
        InfoSend.getContentPane().add(textArea_port);

        Label labelUser = new Label("User");
        labelUser.setForeground(Color.WHITE);
        labelUser.setFont(new Font("微软雅黑 Light", Font.BOLD, 13));
        labelUser.setBackground(FrameHelper.LABEL_TONE);
        labelUser.setAlignment(Label.CENTER);
        labelUser.setBounds(0, 134, 76, 36);
        InfoSend.getContentPane().add(labelUser);

        textArea_user = new JTextArea();
        textArea_user.setLineWrap(true);
        textArea_user.setFont(new Font("微软雅黑 Light", Font.PLAIN, 16));
        textArea_user.setBackground(SystemColor.menu);
        textArea_user.setBounds(95, 134, 106, 36);
        InfoSend.getContentPane().add(textArea_user);

        Label labelPwd = new Label("Password");
        labelPwd.setForeground(Color.WHITE);
        labelPwd.setFont(new Font("微软雅黑 Light", Font.BOLD, 13));
        labelPwd.setBackground(FrameHelper.LABEL_TONE);
        labelPwd.setAlignment(Label.CENTER);
        labelPwd.setBounds(0, 176, 76, 36);
        InfoSend.getContentPane().add(labelPwd);

        textArea_pwd = new JTextArea();
        textArea_pwd.setLineWrap(true);
        textArea_pwd.setFont(new Font("微软雅黑 Light", Font.PLAIN, 16));
        textArea_pwd.setBackground(SystemColor.menu);
        textArea_pwd.setBounds(95, 176, 106, 36);
        InfoSend.getContentPane().add(textArea_pwd);

        Label labelCharset = new Label("Charset");
        labelCharset.setForeground(Color.WHITE);
        labelCharset.setFont(new Font("微软雅黑 Light", Font.BOLD, 13));
        labelCharset.setBackground(FrameHelper.LABEL_TONE);
        labelCharset.setAlignment(Label.CENTER);
        labelCharset.setBounds(0, 218, 76, 36);
        InfoSend.getContentPane().add(labelCharset);

        textArea_charset = new JTextArea();
        textArea_charset.setText("UTF-8");
        textArea_charset.setLineWrap(true);
        textArea_charset.setFont(new Font("微软雅黑 Light", Font.PLAIN, 16));
        textArea_charset.setBackground(SystemColor.menu);
        textArea_charset.setBounds(95, 218, 106, 36);
        InfoSend.getContentPane().add(textArea_charset);

        JLabel lblNewLabel_logo = new JLabel("");
        lblNewLabel_logo.setIcon(
                new ImageIcon(FrameHelper.LOGO_ICON_IMAGE_PATH));
        lblNewLabel_logo.setBounds(651, 414, 134, 80);
        InfoSend.getContentPane().add(lblNewLabel_logo);

    }

    /**
     * 获取CUCCAlarmPage对象
     *
     * @return CUCCAlarmPage对象
     */
    public static CUCCAlarmPage initInterfacePage() {
        return CUCC_ALARM_PAGE;
    }

    /**
     * 打开CUCCAlarmPage界面，每次获取时，更新接口选择框显示
     */
    public void getInterfacePage() {
        comboBox_interface.setModel(new DefaultComboBoxModel(FrameUtil.getInfArray(InterfaceEnum.CUCC.getCName())));
        InfoSend.getContentPane().add(comboBox_interface);
        InfoSend.setVisible(true);

    }
}

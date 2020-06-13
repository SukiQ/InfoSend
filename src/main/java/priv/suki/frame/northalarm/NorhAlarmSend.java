package priv.suki.frame.northalarm;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JComboBox;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import java.awt.Font;
import java.awt.Color;
import java.awt.Dialog.ModalExclusionType;
import java.awt.Panel;
import javax.swing.JTextField;
import java.awt.Canvas;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import priv.suki.controller.ContralCenter;
import priv.suki.frame.util.FrameHelper;
import priv.suki.frame.northalarm.NorthAlarmPage;
import priv.suki.util.DynamicLogUtil;
import priv.suki.util.Propert;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.JEditorPane;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.Label;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.border.LineBorder;
import javax.swing.JCheckBox;
import javax.swing.JLayeredPane;

/**
 * omc北向接口发送界面
 *
 * @author 花小琪
 * @version 1.0
 */
public class NorhAlarmSend {
    // Log
    private static Log log = LogFactory.getLog(NorhAlarmSend.class);
    //	private static Logger logger = Logger.getLogger("Log4jMain");
    private static NorhAlarmSend NorhAlarmSend = new NorhAlarmSend();
    private static JFrame InfoSend;
    private static JTable table_param;
    private static JEditorPane editorPane_info;
    private static JCheckBox checkbox_normal;
    private static JCheckBox checkbox_storm;
    private static JTextField rateField;
    private static JComboBox comboBox_rate;
    private static JLayeredPane panel_sendParam;
    private JLabel lblNewLabel_description_rate;
    private static JButton btnSend;
    private static JButton btnParse;
    private static ButtonGroup sendgroup;
    private static JTextField durationField;
    private Label label_duration;
    private static JComboBox comboBox_time;
    private JLabel label_description_duration;
    private static JButton btnLog;
    private static JButton button_back;
    private static JButton btnNewButton_return;
    private static JScrollPane scrollPane;
    private static JTextArea textArea_log;
    private static JLayeredPane panel_stormParam;
    private static Label label_steady_rate;
    private static JTextField textField_steady_rate;
    private static JComboBox comboBox_steady_rate;
    private static JLabel label_steady_rate_description;
    private static Label label_storm_rate;
    private static Label label_steady_duration;
    private static Label label_storm_duration;
    private static JTextField textField_steady_duration;
    private static JTextField textField_storm_rate;
    private static JTextField textField_storm_duration;
    private static JComboBox comboBox_steady_duration;
    private static JComboBox comboBox_storm_rate;
    private static JComboBox comboBox_storm_duration;
    private static JLabel label_steady_duration_description;
    private static JLabel label_storm_duration_description;
    private static JLabel label_storm_rate_description;
    private static JButton btnCut;
    private JCheckBox checkBox_build;
    private JCheckBox checkBox_fast;

    /**
     * NorhAlarmSend构造模块，初始化界面
     */
    public NorhAlarmSend() {
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

        Canvas canvas = new Canvas();
        canvas.setBounds(0, 0, 0, 0);
        canvas.setBackground(new Color(0, 0, 0));
        InfoSend.getContentPane().setLayout(null);
        InfoSend.getContentPane().add(canvas);

        Panel panel = new Panel();
        panel.setBounds(201, 0, 598, 48);
        panel.setBackground(FrameHelper.MAIN_TONE);
        InfoSend.getContentPane().add(panel);

        btnNewButton_return = new JButton("Return");

        /**
         * RETURN按钮监控
         */
        btnNewButton_return.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                /* 如果程序正在发送，无法点击return */
                if (!ContralCenter.getContral().isNoSend()) {
                    return;
                }

                NorthAlarmPage.initInterfacePage().getInterfacePage();
                InfoSend.setVisible(false);
            }
        });
        btnNewButton_return.setBounds(0, 0, 200, 48);
        btnNewButton_return.setBackground(new Color(192, 192, 192));
        btnNewButton_return.setFont(new Font("微软雅黑 Light", Font.PLAIN, 15));
        InfoSend.getContentPane().add(btnNewButton_return);

        table_param = new JTable();
        table_param.setEnabled(false);
        table_param.setBounds(0, 78, 251, 143);
        table_param.setFont(new Font("微软雅黑 Light", Font.PLAIN, 15));
        table_param.setBorder(new LineBorder(new Color(0, 0, 0)));
        table_param.setRowHeight(30);
        table_param.setModel(new DefaultTableModel(
                new Object[][]{{"port", Propert.getPropert().getNorth_alarm_port()},
                        {"User", Propert.getPropert().getNorth_alarm_user()},
                        {"Password", Propert.getPropert().getNorth_alarm_pwd()},
                        {"charset", Propert.getPropert().getCharset()}, {null, null},},
                new String[]{"New column", "New column"}));
        InfoSend.getContentPane().add(table_param);
        editorPane_info = new JEditorPane();
        editorPane_info.setEditable(false);
        editorPane_info.setFont(new Font("微软雅黑 Light", Font.PLAIN, 12));
        editorPane_info.setBounds(0, 248, 251, 229);
        editorPane_info.setText(Propert.getPropert().getMsg());
        InfoSend.getContentPane().add(editorPane_info);

        JPanel panel_send = new JPanel();
        panel_send.setBackground(Color.WHITE);
        panel_send.setBounds(340, 54, 445, 367);
        InfoSend.getContentPane().add(panel_send);
        panel_send.setLayout(null);

        sendgroup = new ButtonGroup();
        checkbox_normal = new JCheckBox("\u987A\u5E8F\u53D1\u9001");

        /**
         * 顺序发送选择框监听，选贼后会打开相关的填选框
         */
        checkbox_normal.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                panel_stormParam.setVisible(false);
                panel_sendParam.setVisible(true);
            }
        });
        scrollPane = new JScrollPane();
        scrollPane.setBounds(36, 0, 409, 367);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVisible(false);
        panel_send.add(scrollPane);

        textArea_log = new JTextArea();
        textArea_log.setEditable(false);
        textArea_log.setFont(new Font("微软雅黑 Light", Font.PLAIN, 14));
        textArea_log.setBackground(SystemColor.control);
        textArea_log.setLineWrap(true);
        scrollPane.setViewportView(textArea_log);

        checkBox_fast = new JCheckBox("\u6781\u901F\u6A21\u5F0F");
        checkBox_fast.setHorizontalAlignment(SwingConstants.LEFT);
        checkBox_fast.setFont(new Font("微软雅黑 Light", Font.PLAIN, 13));
        checkBox_fast.setBackground(Color.WHITE);
        checkBox_fast.setActionCommand("normal");
        checkBox_fast.setBounds(60, 250, 165, 30);
        panel_send.add(checkBox_fast);

        checkBox_build = new JCheckBox("\u62FC\u63A5\u6D88\u606F");
        checkBox_build.setSelected(false);
        checkBox_build.setEnabled(false);
        checkBox_build.setHorizontalAlignment(SwingConstants.LEFT);
        checkBox_build.setFont(new Font("微软雅黑 Light", Font.PLAIN, 13));
        checkBox_build.setBackground(Color.WHITE);
        checkBox_build.setActionCommand("normal");
        checkBox_build.setBounds(60, 280, 165, 30);
        panel_send.add(checkBox_build);

        checkbox_normal.setHorizontalAlignment(SwingConstants.LEFT);
        checkbox_normal.setBackground(Color.WHITE);
        checkbox_normal.setFont(new Font("微软雅黑 Light", Font.PLAIN, 13));
        checkbox_normal.setBounds(68, 37, 140, 48);
        checkbox_normal.setActionCommand("normal");
        panel_send.add(checkbox_normal);

        checkbox_storm = new JCheckBox("\u98CE\u66B4\u5F0F\u53D1\u9001");
        /**
         * 风暴发送选择框监听，选贼后会打开相关的填选框
         */
        checkbox_storm.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                panel_stormParam.setVisible(true);
                panel_sendParam.setVisible(false);
            }
        });
        checkbox_storm.setHorizontalAlignment(SwingConstants.LEFT);
        checkbox_storm.setFont(new Font("微软雅黑 Light", Font.PLAIN, 13));
        checkbox_storm.setBackground(Color.WHITE);
        checkbox_storm.setBounds(68, 90, 140, 48);
        checkbox_storm.setActionCommand("storm");
        panel_send.add(checkbox_storm);
        sendgroup.add(checkbox_normal);
        sendgroup.add(checkbox_storm);

        panel_stormParam = new JLayeredPane();
        panel_stormParam.setBackground(FrameHelper.LABEL_TONE);
        panel_stormParam.setBounds(221, 0, 210, 367);
        panel_send.add(panel_stormParam);

        label_steady_rate = new Label("Rate:");
        label_steady_rate.setFont(new Font("微软雅黑 Light", Font.PLAIN, 13));
        label_steady_rate.setBackground(FrameHelper.LABEL_TONE);
        label_steady_rate.setAlignment(Label.CENTER);
        label_steady_rate.setBounds(10, 10, 65, 36);
        panel_stormParam.add(label_steady_rate);

        textField_steady_rate = new JTextField();
        textField_steady_rate.setColumns(10);
        textField_steady_rate.setBounds(75, 10, 65, 37);
        panel_stormParam.add(textField_steady_rate);

        comboBox_steady_rate = new JComboBox();
        comboBox_steady_rate.setBackground(Color.WHITE);
        comboBox_steady_rate.setFont(new Font("微软雅黑 Light", Font.PLAIN, 15));
        comboBox_steady_rate.setBounds(139, 10, 71, 36);
        comboBox_steady_rate.setModel(new DefaultComboBoxModel(new String[]{"sec", "min", "hour"}));
        panel_stormParam.add(comboBox_steady_rate);

        label_steady_rate_description = new JLabel(
                "<html>\u5E73\u7A33\u671F\u53D1\u9001\u901F\u7387\uFF0C\u53D1\u9001\u901F\u7387\u53EF\u914D\u4E3A<br>\u6761/\u6BCF\u79D2\uFF0C\u6761/\u6BCF\u5206\uFF0C\u6761/\u6BCF\u65F6</html>");
        label_steady_rate_description.setFont(new Font("微软雅黑 Light", Font.PLAIN, 13));
        label_steady_rate_description.setBounds(10, 52, 200, 41);
        panel_stormParam.add(label_steady_rate_description);

        label_steady_duration = new Label("Duration:");
        label_steady_duration.setFont(new Font("微软雅黑 Light", Font.PLAIN, 13));
        label_steady_duration.setBackground(FrameHelper.LABEL_TONE);
        label_steady_duration.setAlignment(Label.CENTER);
        label_steady_duration.setBounds(10, 99, 65, 36);
        panel_stormParam.add(label_steady_duration);

        textField_steady_duration = new JTextField();
        textField_steady_duration.setColumns(10);
        textField_steady_duration.setBounds(75, 99, 65, 37);
        panel_stormParam.add(textField_steady_duration);

        comboBox_steady_duration = new JComboBox();
        comboBox_steady_duration.setBackground(Color.WHITE);
        comboBox_steady_duration.setFont(new Font("微软雅黑 Light", Font.PLAIN, 15));
        comboBox_steady_duration.setModel(new DefaultComboBoxModel(new String[]{"sec", "min", "hour"}));
        comboBox_steady_duration.setBounds(139, 99, 71, 36);
        panel_stormParam.add(comboBox_steady_duration);

        label_steady_duration_description = new JLabel(
                "<html>\u5E73\u7A33\u671F\u6301\u7EED\u65F6\u95F4,\u53EF\u914D\u4E3A<br>\u79D2\uFF0C\u5206\u949F\uFF0C\u5C0F\u65F6,\u5FC5\u586B</html>");
        label_steady_duration_description.setFont(new Font("微软雅黑 Light", Font.PLAIN, 13));
        label_steady_duration_description.setBounds(10, 141, 200, 41);
        panel_stormParam.add(label_steady_duration_description);

        label_storm_rate = new Label("Rate:");
        label_storm_rate.setFont(new Font("微软雅黑 Light", Font.PLAIN, 13));
        label_storm_rate.setBackground(FrameHelper.LABEL_TONE);
        label_storm_rate.setAlignment(Label.CENTER);
        label_storm_rate.setBounds(10, 188, 65, 36);
        panel_stormParam.add(label_storm_rate);

        textField_storm_rate = new JTextField();
        textField_storm_rate.setColumns(10);
        textField_storm_rate.setBounds(75, 188, 65, 37);
        panel_stormParam.add(textField_storm_rate);

        comboBox_storm_rate = new JComboBox();
        comboBox_storm_rate.setBackground(Color.WHITE);
        comboBox_storm_rate.setFont(new Font("微软雅黑 Light", Font.PLAIN, 15));
        comboBox_storm_rate.setModel(new DefaultComboBoxModel(new String[]{"sec", "min", "hour"}));
        comboBox_storm_rate.setBounds(139, 188, 71, 36);
        panel_stormParam.add(comboBox_storm_rate);

        label_storm_rate_description = new JLabel(
                "<html>\u98CE\u66B4\u671F\u53D1\u9001\u901F\u7387\uFF0C\u53D1\u9001\u901F\u7387\u53EF\u914D\u4E3A<br>\u6761/\u6BCF\u79D2\uFF0C\u6761/\u6BCF\u5206\uFF0C\u6761/\u6BCF\u65F6</html>");
        label_storm_rate_description.setFont(new Font("微软雅黑 Light", Font.PLAIN, 13));
        label_storm_rate_description.setBounds(10, 230, 200, 41);
        panel_stormParam.add(label_storm_rate_description);

        label_storm_duration = new Label("Duration:");
        label_storm_duration.setFont(new Font("微软雅黑 Light", Font.PLAIN, 13));
        label_storm_duration.setBackground(FrameHelper.LABEL_TONE);
        label_storm_duration.setAlignment(Label.CENTER);
        label_storm_duration.setBounds(10, 277, 65, 36);
        panel_stormParam.add(label_storm_duration);

        textField_storm_duration = new JTextField();
        textField_storm_duration.setColumns(10);
        textField_storm_duration.setBounds(75, 277, 65, 37);
        panel_stormParam.add(textField_storm_duration);

        comboBox_storm_duration = new JComboBox();
        comboBox_storm_duration.setBackground(Color.WHITE);
        comboBox_storm_duration.setFont(new Font("微软雅黑 Light", Font.PLAIN, 15));
        comboBox_storm_duration.setModel(new DefaultComboBoxModel(new String[]{"sec", "min", "hour"}));
        comboBox_storm_duration.setBounds(139, 277, 71, 36);
        panel_stormParam.add(comboBox_storm_duration);

        label_storm_duration_description = new JLabel(
                "<html>\u98CE\u66B4\u671F\u6301\u7EED\u65F6\u95F4,\u53EF\u914D\u4E3A<br>\u79D2\uFF0C\u5206\u949F\uFF0C\u5C0F\u65F6,\u5FC5\u586B</html>");
        label_storm_duration_description.setFont(new Font("微软雅黑 Light", Font.PLAIN, 13));
        label_storm_duration_description.setBounds(10, 319, 200, 41);
        panel_stormParam.add(label_storm_duration_description);
        panel_stormParam.setVisible(false);

        panel_sendParam = new JLayeredPane();
        panel_sendParam.setBackground(new Color(255, 255, 255));
        panel_sendParam.setBounds(221, 0, 210, 367);
        panel_send.add(panel_sendParam);
        panel_sendParam.setLayout(null);

        Label label_rate = new Label("Rate:");
        label_rate.setBounds(10, 7, 65, 36);
        panel_sendParam.add(label_rate);
        label_rate.setAlignment(Label.CENTER);
        label_rate.setBackground(FrameHelper.LABEL_TONE);
        label_rate.setFont(new Font("微软雅黑 Light", Font.PLAIN, 13));

        rateField = new JTextField();
        rateField.setBounds(76, 7, 71, 37);
        panel_sendParam.add(rateField);
        rateField.setColumns(10);

        comboBox_rate = new JComboBox();
        comboBox_rate.setBackground(Color.WHITE);
        comboBox_rate.setBounds(145, 7, 65, 36);
        panel_sendParam.add(comboBox_rate);
        comboBox_rate.setFont(new Font("微软雅黑 Light", Font.PLAIN, 15));
        comboBox_rate.setModel(new DefaultComboBoxModel(new String[]{"sec", "min", "hour"}));

        lblNewLabel_description_rate = new JLabel(
                "<html>\u987A\u5E8F\u53D1\u9001\u4FE1\u606F\uFF0C\u53D1\u9001\u901F\u7387\u53EF\u914D\u4E3A" + "<br>"
                        + "\u6761/\u6BCF\u79D2\uFF0C\u6761/\u6BCF\u5206\uFF0C\u6761/\u6BCF\u65F6</html>");
        lblNewLabel_description_rate.setFont(new Font("微软雅黑 Light", Font.PLAIN, 13));
        lblNewLabel_description_rate.setBounds(10, 62, 200, 50);
        panel_sendParam.add(lblNewLabel_description_rate);

        label_duration = new Label("Duration:");
        label_duration.setFont(new Font("微软雅黑 Light", Font.PLAIN, 13));
        label_duration.setBackground(FrameHelper.LABEL_TONE);
        label_duration.setAlignment(Label.CENTER);
        label_duration.setBounds(10, 126, 65, 36);
        panel_sendParam.add(label_duration);

        durationField = new JTextField();
        durationField.setColumns(10);
        durationField.setBounds(76, 125, 71, 37);
        panel_sendParam.add(durationField);

        comboBox_time = new JComboBox();
        comboBox_time.setBackground(Color.WHITE);
        comboBox_time.setModel(new DefaultComboBoxModel(new String[]{"sec", "min", "hour"}));
        comboBox_time.setFont(new Font("微软雅黑 Light", Font.PLAIN, 15));
        comboBox_time.setBounds(145, 126, 65, 36);
        panel_sendParam.add(comboBox_time);

        label_description_duration = new JLabel(
                "<html>\u53D1\u9001\u65F6\u95F4\uFF0C\u53EF\u914D\u79D2\uFF0C\u5206\u949F\uFF0C\u5C0F\u65F6<br>\u53EF\u4EE5\u4E0D\u586B\uFF0C\u8868\u793A\u4E00\u76F4\u53D1\u9001</html>");
        label_description_duration.setFont(new Font("微软雅黑 Light", Font.PLAIN, 13));
        label_description_duration.setBounds(14, 181, 196, 50);
        panel_sendParam.add(label_description_duration);

        btnParse = new JButton();
        btnParse.setText("Parse");

        /**
         * PARSE按钮监听，点击触发暂停并会显示Continue按钮
         */
        btnParse.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!ContralCenter.getContral().isParse()) {
                    btnParse.setBackground(Color.GRAY);
                    btnParse.setText("Continue");
                    ContralCenter.getContral().setParse(true);
                } else {
                    btnParse.setBackground(Color.WHITE);
                    btnParse.setText("PARSE");
                    ContralCenter.getContral().setParse(false);
                }
            }
        });
        btnParse.setFont(new Font("微软雅黑 Light", Font.PLAIN, 15));
        btnParse.setBackground(Color.WHITE);
        btnParse.setBounds(482, 447, 100, 47);
        InfoSend.getContentPane().add(btnParse);

        btnSend = new JButton();
        btnSend.setText("Send");

        /**
         * SEND按钮监听，点击触发发送并会显示Stop按钮s
         */
        btnSend.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                try {

                    /* 没有选中类型 */
                    if (sendgroup.getSelection() == null) {
                        throw new Exception("没有勾选发送方式！,请查验");
                    }

                    ContralCenter.getContral().setSendType(sendgroup.getSelection().getActionCommand());
                    ContralCenter.getContral().setParam("buildsend", "boolean", String.valueOf(checkBox_build.isSelected()), Propert.PARAM_CAN_BE_EMPTY);
                    ContralCenter.getContral().setParam("fastmodel", "boolean", String.valueOf(checkBox_fast.isSelected()), Propert.PARAM_CAN_BE_EMPTY);

                    /* 顺序发送 */
                    if (ContralCenter.getContral().getSendType() == Propert.NORMAL_SEND) {
                        ContralCenter.getContral().setParam("rate", "int", rateField.getText().trim(),
                                Propert.PARAM_CANT_BE_EMPTY, comboBox_rate.getSelectedItem().toString());
                        ContralCenter.getContral().setParam("duration", "int", durationField.getText().trim(),
                                Propert.PARAM_CAN_BE_EMPTY, comboBox_time.getSelectedItem().toString());
                    }
                    /* 风暴发送 */
                    if (ContralCenter.getContral().getSendType() == Propert.STORM_SEND) {
                        ContralCenter.getContral().setParam("rate", "int", textField_steady_rate.getText().trim(),
                                Propert.PARAM_CANT_BE_EMPTY, comboBox_steady_rate.getSelectedItem().toString());
                        ContralCenter.getContral().setParam("duration", "int",
                                textField_steady_duration.getText().trim(), Propert.PARAM_CANT_BE_EMPTY,
                                comboBox_steady_duration.getSelectedItem().toString());
                        ContralCenter.getContral().setParam("storm_rate", "int", textField_storm_rate.getText().trim(),
                                Propert.PARAM_CANT_BE_EMPTY, comboBox_storm_rate.getSelectedItem().toString());
                        ContralCenter.getContral().setParam("storm_duration", "int",
                                textField_storm_duration.getText().trim(), Propert.PARAM_CANT_BE_EMPTY,
                                comboBox_storm_duration.getSelectedItem().toString());

                    }

                    /* 当前是未发送状态，改变按钮，控制发送 */
                    if (ContralCenter.getContral().isNoSend()) {
                        if (!ContralCenter.getContral().setNoSend(false)) {
                            return;
                        }
                        btnSend.setText("STOP");
                        btnSend.setBackground(Color.GRAY);
                        InfoSend.getContentPane().add(btnSend);


                    } else {
                        ContralCenter.getContral().setNoSend(true);
                        btnSend.setText("START");
                        btnSend.setBackground(Color.WHITE);
                        InfoSend.getContentPane().add(btnSend);
                        if (ContralCenter.getContral().isParse()) {
                            btnParse.setBackground(Color.WHITE);
                            btnParse.setText("PARSE");
                            ContralCenter.getContral().setParse(false);
                        }
                        /* 清空动态日志 */
                        DynamicLogUtil.getDynamicLogUtil().clearDynamicLog();
                    }

                } catch (Throwable t) {
                    /* 设置报错弹窗 */
                    log.error("omc北向接口初始化发送异常", t);
                    JOptionPane.showConfirmDialog(InfoSend, t.getMessage(), "Error", JOptionPane.OK_CANCEL_OPTION,
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
        });
        btnSend.setFont(new Font("微软雅黑 Light", Font.PLAIN, 15));
        btnSend.setBackground(Color.WHITE);
        btnSend.setBounds(383, 447, 100, 47);
        InfoSend.getContentPane().add(btnSend);

        btnLog = new JButton();

        /**
         * 动态日志开关
         */
        btnLog.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                if (!ContralCenter.getContral().isShowDynamicLog()) {
                    ContralCenter.getContral().setShowDynamicLog(true);
                    scrollPane.setVisible(true);
                } else {
                    ContralCenter.getContral().setShowDynamicLog(false);
                    scrollPane.setVisible(false);
                }
            }
        });
        btnLog.setText("Log");
        btnLog.setFont(new Font("微软雅黑 Light", Font.PLAIN, 15));
        btnLog.setBackground(Color.WHITE);
        btnLog.setBounds(581, 447, 100, 47);
        InfoSend.getContentPane().add(btnLog);

        /*Cut按钮监听*/
        btnCut = new JButton();
        btnCut.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!ContralCenter.getContral().isCutSwitch()) {
                    btnCut.setBackground(Color.GRAY);
                    btnCut.setText("Recovery");
                    ContralCenter.getContral().setCutSwitch(true);
                } else {
                    btnCut.setBackground(Color.WHITE);
                    btnCut.setText("CUT");
                    ContralCenter.getContral().setCutSwitch(false);
                }
            }
        });
        btnCut.setText("Cut");
        btnCut.setFont(new Font("微软雅黑 Light", Font.PLAIN, 15));
        btnCut.setBackground(Color.WHITE);
        btnCut.setBounds(681, 447, 100, 47);
        InfoSend.getContentPane().add(btnCut);
        panel_sendParam.setVisible(false);

        /**
         * 启动动态日志模块
         */
        new Thread() {

            public void run() {
                while (true) {
                    try {
                        if (ContralCenter.getContral().isNoSend() || ContralCenter.getContral().isParse()) {
                            Thread.sleep(1000);
                            continue;
                        }
                        textArea_log.setText(DynamicLogUtil.getDynamicLogUtil().getDynamicLog());
                        textArea_log.setCaretPosition(textArea_log.getDocument().getLength());// 设置光标永远在文档的最底部

                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        log.error("Sleep被打断", e);
                    }

                }
            }

        }.start();
    }

    /**
     * 获取NorhAlarmSend对象
     *
     * @return NorhAlarmSend对象
     */
    public static NorhAlarmSend initInterfacePage() {
        return NorhAlarmSend;
    }

    /**
     * 打开NorhAlarmSend界面，每次获取时，更新参数表显示
     */
    public void getInterfacePage() {
        table_param.setModel(new DefaultTableModel(
                new Object[][]{{"port", Propert.getPropert().getNorth_alarm_port()},
                        {"User", Propert.getPropert().getNorth_alarm_user()},
                        {"Password", Propert.getPropert().getNorth_alarm_pwd()},
                        {"charset", Propert.getPropert().getCharset()}, {null, null},},
                new String[]{"New column", "New column"}));
        InfoSend.getContentPane().add(table_param);
        editorPane_info.setText(Propert.getPropert().getMsg());
        InfoSend.getContentPane().add(editorPane_info);
        InfoSend.setVisible(true);
    }

}

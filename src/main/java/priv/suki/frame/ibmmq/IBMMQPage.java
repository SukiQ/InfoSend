package priv.suki.frame.ibmmq;

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
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import javax.swing.JTextArea;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.Label;
import javax.swing.JLabel;
import javax.swing.ImageIcon;

/**
 * socket_client接口参数配置界面
 *
 * @author 花小琪
 * @version 1.0
 */
public class IBMMQPage {
    // Log
    private static Log log = LogFactory.getLog(IBMMQPage.class);
    private static IBMMQPage IBMMQPage = new IBMMQPage();
    private static JFrame InfoSend;
    private static JComboBox comboBox_interface;
    private static JTextArea textArea_info;
    private static JTextArea textArea_port;
    private static JTextArea textArea_charset;
    private static JTextArea textArea_ip;
    private static JTextArea textArea_channel;
    private static JTextArea textArea_ccsid;
    private static JTextArea textArea_manager;
    private static JTextArea textArea_name;

    /**
     * IBMMQPage构造模块，初始化界面
     */
    public IBMMQPage() {
        Thread.currentThread().setName("IBMMQPage");
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

        // 接口选择框监听//
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
        comboBox_interface.setModel(new DefaultComboBoxModel(FrameUtil.getInfArray(InterfaceEnum.IBMMQ.getCName())));

        Canvas canvas = new Canvas();
        canvas.setBounds(0, 0, 0, 0);
        canvas.setBackground(new Color(0, 0, 0));
        InfoSend.getContentPane().setLayout(null);

        JButton button = new JButton("R");

        /*R按钮监听*/
        button.addActionListener(e -> {
            IBMMQReadPage.initInterfacePage().getInterfacePage();
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

        JButton btnNewButton_send = new JButton("SEND");
        btnNewButton_send.setBackground(Color.WHITE);

        //SEND按钮监听//
        btnNewButton_send.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {

                    /* 参数赋值 */
                    ContralCenter.getContral().setParam("msg", "string", textArea_info.getText(),
                            Propert.PARAM_CANT_BE_EMPTY);
                    ContralCenter.getContral().setParam("ibmmq_ip", "string", textArea_ip.getText(),
                            Propert.PARAM_CANT_BE_EMPTY);
                    ContralCenter.getContral().setParam("ibmmq_port", "int", textArea_port.getText(),
                            Propert.PARAM_CANT_BE_EMPTY);
                    ContralCenter.getContral().setParam("ibmmq_channel", "string", textArea_channel.getText(),
                            Propert.PARAM_CANT_BE_EMPTY);
                    ContralCenter.getContral().setParam("ibmmq_qManager", "string", textArea_manager.getText(),
                            Propert.PARAM_CANT_BE_EMPTY);
                    ContralCenter.getContral().setParam("ibmmq_ccsid", "int", textArea_ccsid.getText(),
                            Propert.PARAM_CANT_BE_EMPTY);
                    ContralCenter.getContral().setParam("charset", "string", textArea_charset.getText(),
                            Propert.PARAM_CAN_BE_EMPTY);
                    ContralCenter.getContral().setParam("ibmmq_qName", "string", textArea_name.getText(),
                            Propert.PARAM_CANT_BE_EMPTY);

                    /* 调用下一个界面，隐藏当前界面 */
                    IBMMQSend.initInterfacePage().getInterfacePage();
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

        Label label_port = new Label("Port");
        label_port.setBounds(0, 138, 70, 36);
        label_port.setForeground(Color.WHITE);
        label_port.setAlignment(Label.CENTER);
        label_port.setFont(new Font("微软雅黑 Light", Font.BOLD, 13));
        label_port.setBackground(FrameHelper.LABEL_TONE);
        InfoSend.getContentPane().add(label_port);

        textArea_port = new JTextArea();
        textArea_port.setLineWrap(true);
        textArea_port.setFont(new Font("微软雅黑 Light", Font.PLAIN, 16));
        textArea_port.setBackground(SystemColor.menu);
        textArea_port.setBounds(76, 138, 123, 36);
        InfoSend.getContentPane().add(textArea_port);

        Label label_charset = new Label("Charset");
        label_charset.setForeground(Color.WHITE);
        label_charset.setFont(new Font("微软雅黑 Light", Font.BOLD, 13));
        label_charset.setBackground(FrameHelper.LABEL_TONE);
        label_charset.setAlignment(Label.CENTER);
        label_charset.setBounds(0, 348, 70, 36);
        InfoSend.getContentPane().add(label_charset);

        textArea_charset = new JTextArea();
        textArea_charset.setText("UTF-8");
        textArea_charset.setLineWrap(true);
        textArea_charset.setFont(new Font("微软雅黑 Light", Font.PLAIN, 16));
        textArea_charset.setBackground(SystemColor.menu);
        textArea_charset.setBounds(76, 348, 123, 36);
        InfoSend.getContentPane().add(textArea_charset);

        Label label_ip = new Label("IP");
        label_ip.setForeground(Color.WHITE);
        label_ip.setFont(new Font("微软雅黑 Light", Font.BOLD, 13));
        label_ip.setBackground(FrameHelper.LABEL_TONE);
        label_ip.setAlignment(Label.CENTER);
        label_ip.setBounds(0, 96, 70, 36);
        InfoSend.getContentPane().add(label_ip);

        textArea_ip = new JTextArea();
        textArea_ip.setLineWrap(true);
        textArea_ip.setFont(new Font("微软雅黑 Light", Font.PLAIN, 16));
        textArea_ip.setBackground(SystemColor.menu);
        textArea_ip.setBounds(76, 96, 123, 36);
        InfoSend.getContentPane().add(textArea_ip);

        JLabel lblNewLabel_logo = new JLabel("");
        lblNewLabel_logo.setIcon(
                new ImageIcon(FrameHelper.LOGO_ICON_IMAGE_PATH));
        lblNewLabel_logo.setBounds(651, 414, 134, 80);
        InfoSend.getContentPane().add(lblNewLabel_logo);

        Label label_channel = new Label("Channel");
        label_channel.setForeground(Color.WHITE);
        label_channel.setFont(new Font("微软雅黑 Light", Font.BOLD, 13));
        label_channel.setBackground(FrameHelper.LABEL_TONE);
        label_channel.setAlignment(Label.CENTER);
        label_channel.setBounds(0, 180, 70, 36);
        InfoSend.getContentPane().add(label_channel);

        textArea_channel = new JTextArea();
        textArea_channel.setLineWrap(true);
        textArea_channel.setFont(new Font("微软雅黑 Light", Font.PLAIN, 16));
        textArea_channel.setBackground(SystemColor.menu);
        textArea_channel.setBounds(76, 180, 123, 36);
        InfoSend.getContentPane().add(textArea_channel);

        Label label_ccsid = new Label("CCSID");
        label_ccsid.setForeground(Color.WHITE);
        label_ccsid.setFont(new Font("微软雅黑 Light", Font.BOLD, 13));
        label_ccsid.setBackground(FrameHelper.LABEL_TONE);
        label_ccsid.setAlignment(Label.CENTER);
        label_ccsid.setBounds(0, 222, 70, 36);
        InfoSend.getContentPane().add(label_ccsid);

        textArea_ccsid = new JTextArea();
        textArea_ccsid.setLineWrap(true);
        textArea_ccsid.setFont(new Font("微软雅黑 Light", Font.PLAIN, 16));
        textArea_ccsid.setBackground(SystemColor.menu);
        textArea_ccsid.setBounds(76, 222, 123, 36);
        InfoSend.getContentPane().add(textArea_ccsid);

        Label label_manager = new Label("QManager");
        label_manager.setForeground(Color.WHITE);
        label_manager.setFont(new Font("微软雅黑 Light", Font.BOLD, 13));
        label_manager.setBackground(FrameHelper.LABEL_TONE);
        label_manager.setAlignment(Label.CENTER);
        label_manager.setBounds(0, 264, 70, 36);
        InfoSend.getContentPane().add(label_manager);

        textArea_manager = new JTextArea();
        textArea_manager.setLineWrap(true);
        textArea_manager.setFont(new Font("微软雅黑 Light", Font.PLAIN, 16));
        textArea_manager.setBackground(SystemColor.menu);
        textArea_manager.setBounds(76, 264, 123, 36);
        InfoSend.getContentPane().add(textArea_manager);

        Label label_name = new Label("QName");
        label_name.setForeground(Color.WHITE);
        label_name.setFont(new Font("微软雅黑 Light", Font.BOLD, 13));
        label_name.setBackground(FrameHelper.LABEL_TONE);
        label_name.setAlignment(Label.CENTER);
        label_name.setBounds(0, 306, 70, 36);
        InfoSend.getContentPane().add(label_name);

        textArea_name = new JTextArea();
        textArea_name.setLineWrap(true);
        textArea_name.setFont(new Font("微软雅黑 Light", Font.PLAIN, 16));
        textArea_name.setBackground(SystemColor.menu);
        textArea_name.setBounds(76, 306, 123, 36);
        InfoSend.getContentPane().add(textArea_name);

    }

    /**
     * 获取IBMMQPage对象
     *
     * @return IBMMQPage对象
     */
    public static IBMMQPage initInterfacePage() {
        return IBMMQPage;
    }

    /**
     * 打开IBMMQPage界面，每次获取时，更新接口选择框显示
     */
    public void getInterfacePage() {
        comboBox_interface.setModel(new DefaultComboBoxModel(FrameUtil.getInfArray(InterfaceEnum.IBMMQ.getCName())));
        InfoSend.getContentPane().add(comboBox_interface);
        InfoSend.setVisible(true);

    }
}

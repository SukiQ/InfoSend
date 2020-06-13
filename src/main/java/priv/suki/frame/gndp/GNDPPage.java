package priv.suki.frame.gndp;

import javax.swing.JFrame;
import javax.swing.JComboBox;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import java.awt.Font;
import java.awt.Color;
import java.awt.Dialog.ModalExclusionType;
import java.awt.Panel;
import java.awt.Canvas;
import javax.swing.JScrollPane;

import priv.suki.controller.ContralCenter;
import priv.suki.frame.util.FrameHelper;
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
import javax.swing.JEditorPane;
import java.awt.Toolkit;
import java.awt.SystemColor;
import java.awt.Label;
import javax.swing.JCheckBox;
import javax.swing.JLayeredPane;

/**
 * GNDP½Ó¿Ú²ÎÊýÅäÖÃ½çÃæ
 *
 * @author »¨Ð¡ç÷
 * @version 1.0
 */
public class GNDPPage {
    // Log
    private static Log log = LogFactory.getLog(GNDPPage.class);
    private static GNDPPage gndppage = new GNDPPage();
    private static JFrame InfoSend;
    private static JComboBox comboBox_interface;
    private static JTextArea textArea_info;
    private static JTextArea textArea_charset;
    private static ButtonGroup select_group;
    private static JLayeredPane layeredPane_build;
    private static JLayeredPane layeredPane_sef;
    private static JLayeredPane layeredPane_kafka;
    private static JComboBox comboBox_version;
    private static JLayeredPane layeredPane_socket;
    private static JComboBox comboBox_connType;
    private static JEditorPane editorPane_connType;

    /**
     * GNDPPage¹¹ÔìÄ£¿é£¬³õÊ¼»¯½çÃæ
     */
    public GNDPPage() {
        Thread.currentThread().setName("GNDPPage");
        initialize();
    }

    /**
     * ³õÊ¼»¯½çÃæ
     *
     * @wbp.parser.entryPoint
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
        InfoSend.setFont(new Font("µÈÏß", Font.PLAIN, 12));
        InfoSend.getContentPane().setFont(new Font("Î¢ÈíÑÅºÚ Light", Font.BOLD, 15));
        InfoSend.setBounds(100, 100, 805, 542);
        InfoSend.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        InfoSend.setLocationRelativeTo(null);

        comboBox_interface = new JComboBox();
        comboBox_interface.setBounds(0, 0, 199, 48);

        /**
         * ½Ó¿ÚÑ¡Ôñ¿ò¼àÌý
         */
        comboBox_interface.addItemListener(new ItemListener() {
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
        comboBox_interface.setFont(new Font("Î¢ÈíÑÅºÚ Light", Font.PLAIN, 13));
        comboBox_interface.setModel(new DefaultComboBoxModel(
                new String[]{"GNDP\u5165\u5E93\u6D88\u606F", "SOCKET\u670D\u52A1\u7AEF", "SOCKET\u5BA2\u6237\u7AEF",
                        "SNMP", "Telnet", "ActiveMQ", "IBMMQ", "OMC\u5317\u5411\u544A\u8B66", "Kafka"}));

        Canvas canvas = new Canvas();
        canvas.setBounds(0, 0, 0, 0);
        canvas.setBackground(new Color(0, 0, 0));
        InfoSend.getContentPane().setLayout(null);

        JButton button = new JButton("R");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                GNDPReadPage.initInterfacePage().getInterfacePage();
                InfoSend.setVisible(false);
            }
        });

        layeredPane_sef = new JLayeredPane();
        layeredPane_sef.setBounds(228, 54, 571, 347);
        InfoSend.getContentPane().add(layeredPane_sef);

        Label label_major = new Label("\u4E13\u4E1A\u7C7B\u578B\uFF1A");
        label_major.setForeground(Color.BLACK);
        label_major.setFont(new Font("Î¢ÈíÑÅºÚ Light", Font.BOLD, 13));
        label_major.setBackground(Color.WHITE);
        label_major.setBounds(10, 10, 112, 30);
        layeredPane_sef.add(label_major);

        JComboBox comboBox_major = new JComboBox();
        comboBox_major.setModel(new DefaultComboBoxModel(new String[]{"\u8D44\u6E90", "\u6027\u80FD"}));
        comboBox_major.setFont(new Font("µÈÏß Light", Font.PLAIN, 14));
        comboBox_major.setBackground(Color.WHITE);
        comboBox_major.setBounds(128, 10, 120, 30);
        layeredPane_sef.add(comboBox_major);

        Label label_major_describe = new Label(
                "\u8D44\u6E90/\u6027\u80FD\uFF08\u8D44\u6E90\u5148\u5220\u540E\u5165\uFF0C\u6027\u80FD\u7B2C\u4E00\u6B21\u4E0D\u5220\uFF09");
        label_major_describe.setForeground(Color.BLACK);
        label_major_describe.setFont(new Font("Î¢ÈíÑÅºÚ Light", Font.BOLD, 12));
        label_major_describe.setBackground(Color.WHITE);
        label_major_describe.setBounds(254, 10, 307, 30);
        layeredPane_sef.add(label_major_describe);

        Label label_2 = new Label("\u5165\u5E93\u8868\u540D\uFF1A");
        label_2.setForeground(Color.BLACK);
        label_2.setFont(new Font("Î¢ÈíÑÅºÚ Light", Font.BOLD, 13));
        label_2.setBackground(Color.WHITE);
        label_2.setBounds(10, 44, 112, 30);
        layeredPane_sef.add(label_2);

        JTextArea textArea = new JTextArea();
        textArea.setLineWrap(true);
        textArea.setFont(new Font("Î¢ÈíÑÅºÚ Light", Font.PLAIN, 16));
        textArea.setBackground(SystemColor.menu);
        textArea.setBounds(128, 44, 120, 30);
        layeredPane_sef.add(textArea);

        Label label_3 = new Label("\u5165\u5E93\u8868\u540D");
        label_3.setForeground(Color.BLACK);
        label_3.setFont(new Font("Î¢ÈíÑÅºÚ Light", Font.BOLD, 12));
        label_3.setBackground(Color.WHITE);
        label_3.setBounds(254, 44, 307, 30);
        layeredPane_sef.add(label_3);

        Label label_4 = new Label("\u91C7\u96C6\u5355\u5143\u540D\uFF1A");
        label_4.setForeground(Color.BLACK);
        label_4.setFont(new Font("Î¢ÈíÑÅºÚ Light", Font.BOLD, 13));
        label_4.setBackground(Color.WHITE);
        label_4.setBounds(10, 78, 112, 30);
        layeredPane_sef.add(label_4);

        JTextArea textArea_1 = new JTextArea();
        textArea_1.setLineWrap(true);
        textArea_1.setFont(new Font("Î¢ÈíÑÅºÚ Light", Font.PLAIN, 16));
        textArea_1.setBackground(SystemColor.menu);
        textArea_1.setBounds(128, 78, 120, 30);
        layeredPane_sef.add(textArea_1);

        Label label_5 = new Label("\u91C7\u96C6\u5355\u5143\u540D\u79F0");
        label_5.setForeground(Color.BLACK);
        label_5.setFont(new Font("Î¢ÈíÑÅºÚ Light", Font.BOLD, 12));
        label_5.setBackground(Color.WHITE);
        label_5.setBounds(254, 78, 307, 30);
        layeredPane_sef.add(label_5);

        Label label_6 = new Label("\u4EFB\u52A1\u8D77\u59CB\u65F6\u95F4\uFF1A");
        label_6.setForeground(Color.BLACK);
        label_6.setFont(new Font("Î¢ÈíÑÅºÚ Light", Font.BOLD, 13));
        label_6.setBackground(Color.WHITE);
        label_6.setBounds(10, 111, 112, 30);
        layeredPane_sef.add(label_6);

        JTextArea textArea_2 = new JTextArea();
        textArea_2.setLineWrap(true);
        textArea_2.setFont(new Font("Î¢ÈíÑÅºÚ Light", Font.PLAIN, 16));
        textArea_2.setBackground(SystemColor.menu);
        textArea_2.setBounds(128, 111, 120, 30);
        layeredPane_sef.add(textArea_2);

        Label label_7 = new Label("\u4EFB\u52A1\u8D77\u59CB\u65F6\u95F4\uFF08scan_start_time\uFF09");
        label_7.setForeground(Color.BLACK);
        label_7.setFont(new Font("Î¢ÈíÑÅºÚ Light", Font.BOLD, 12));
        label_7.setBackground(Color.WHITE);
        label_7.setBounds(254, 111, 307, 30);
        layeredPane_sef.add(label_7);

        Label label_8 = new Label("\u4EFB\u52A1\u7ED3\u675F\u65F6\u95F4\uFF1A");
        label_8.setForeground(Color.BLACK);
        label_8.setFont(new Font("Î¢ÈíÑÅºÚ Light", Font.BOLD, 13));
        label_8.setBackground(Color.WHITE);
        label_8.setBounds(10, 145, 112, 30);
        layeredPane_sef.add(label_8);

        JTextArea textArea_3 = new JTextArea();
        textArea_3.setLineWrap(true);
        textArea_3.setFont(new Font("Î¢ÈíÑÅºÚ Light", Font.PLAIN, 16));
        textArea_3.setBackground(SystemColor.menu);
        textArea_3.setBounds(128, 147, 120, 30);
        layeredPane_sef.add(textArea_3);

        Label label_9 = new Label("\u4EFB\u52A1\u7ED3\u675F\u65F6\u95F4\uFF08scan_stop_time\uFF09");
        label_9.setForeground(Color.BLACK);
        label_9.setFont(new Font("Î¢ÈíÑÅºÚ Light", Font.BOLD, 12));
        label_9.setBackground(Color.WHITE);
        label_9.setBounds(254, 145, 307, 30);
        layeredPane_sef.add(label_9);

        Label label_connType = new Label("\u53D6\u5165\u5E93\u6587\u4EF6\u65B9\u5F0F\uFF1A");
        label_connType.setForeground(Color.BLACK);
        label_connType.setFont(new Font("Î¢ÈíÑÅºÚ Light", Font.BOLD, 13));
        label_connType.setBackground(Color.WHITE);
        label_connType.setBounds(10, 179, 112, 30);
        layeredPane_sef.add(label_connType);

        Label label_connType_describe = new Label("\u53EF\u9009FTP/SFTP/HDFS");
        label_connType_describe.setForeground(Color.BLACK);
        label_connType_describe.setFont(new Font("Î¢ÈíÑÅºÚ Light", Font.BOLD, 12));
        label_connType_describe.setBackground(Color.WHITE);
        label_connType_describe.setBounds(254, 179, 307, 30);
        layeredPane_sef.add(label_connType_describe);

        comboBox_connType = new JComboBox();

        /* Ñ¡ÔñÁ¬½Ó·½Ê½ */
        comboBox_connType.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    if (comboBox_connType.getSelectedIndex() == 2) {

                        ContralCenter.getContral().setParam("gndp_conn_type", "int", "2", Propert.PARAM_CANT_BE_EMPTY);
                        editorPane_connType.setText("");

                    } else {

                        if (comboBox_connType.getSelectedIndex() == 1) {
                            ContralCenter.getContral().setParam("gndp_conn_type", "int", "0", Propert.PARAM_CANT_BE_EMPTY);
                        }
                        if (comboBox_connType.getSelectedIndex() == 3) {
                            ContralCenter.getContral().setParam("gndp_conn_type", "int", "1", Propert.PARAM_CANT_BE_EMPTY);
                        }
                        String ftp_ip = JOptionPane.showInputDialog("IPµØÖ·");
                        String ftp_user = JOptionPane.showInputDialog("µÇÂ½ÓÃ»§Ãû");
                        String ftp_pwd = JOptionPane.showInputDialog("µÇÂ½ÃÜÂë");

                        ContralCenter.getContral().setParam("gndp_ftp_ip", "string", ftp_ip,
                                Propert.PARAM_CANT_BE_EMPTY);
                        ContralCenter.getContral().setParam("gndp_ftp_username", "string", ftp_user,
                                Propert.PARAM_CANT_BE_EMPTY);
                        ContralCenter.getContral().setParam("gndp_password", "string", ftp_pwd,
                                Propert.PARAM_CANT_BE_EMPTY);
                        editorPane_connType.setText("µÇÂ½µØÖ·Îª£º" + Propert.getPropert().getGndp_ftp_ip() + "  ÓÃ»§ÃûÎª£º" + Propert.getPropert().getGndp_ftp_username() + "  ÃÜÂëÎª£º" + Propert.getPropert().getGndp_password());

                    }
                } catch (Exception t) {
                    JOptionPane.showConfirmDialog(InfoSend, t.getMessage(), "Error", JOptionPane.OK_CANCEL_OPTION,
                            JOptionPane.ERROR_MESSAGE);

                }
            }

        });
        comboBox_connType.setModel(new DefaultComboBoxModel(new String[]{"FTP", "SFTP", "HDFS"}));
        comboBox_connType.setFont(new Font("µÈÏß Light", Font.PLAIN, 14));
        comboBox_connType.setBackground(Color.WHITE);
        comboBox_connType.setBounds(128, 179, 120, 30);
        layeredPane_sef.add(comboBox_connType);

        editorPane_connType = new JEditorPane();
        editorPane_connType.setEditable(false);
        editorPane_connType.setFont(new Font("µÈÏß Light", Font.BOLD, 13));
        editorPane_connType.setBounds(10, 213, 551, 30);
        layeredPane_sef.add(editorPane_connType);

        Label label_12 = new Label("\u5165\u5E93\u6587\u4EF6\u8DEF\u5F84\uFF1A");
        label_12.setForeground(Color.BLACK);
        label_12.setFont(new Font("Î¢ÈíÑÅºÚ Light", Font.BOLD, 13));
        label_12.setBackground(Color.WHITE);
        label_12.setBounds(10, 249, 112, 30);
        layeredPane_sef.add(label_12);

        JTextArea textArea_4 = new JTextArea();
        textArea_4.setLineWrap(true);
        textArea_4.setFont(new Font("Î¢ÈíÑÅºÚ Light", Font.PLAIN, 16));
        textArea_4.setBackground(SystemColor.menu);
        textArea_4.setBounds(128, 249, 120, 30);
        layeredPane_sef.add(textArea_4);

        Label label_13 = new Label("\u586B\u5199\u6587\u4EF6\u7684\u5B8C\u6574\u8DEF\u5F84");
        label_13.setForeground(Color.BLACK);
        label_13.setFont(new Font("Î¢ÈíÑÅºÚ Light", Font.BOLD, 12));
        label_13.setBackground(Color.WHITE);
        label_13.setBounds(254, 249, 307, 30);
        layeredPane_sef.add(label_13);

        Label label_14 = new Label("OMCID\uFF1A");
        label_14.setForeground(Color.BLACK);
        label_14.setFont(new Font("Î¢ÈíÑÅºÚ Light", Font.BOLD, 13));
        label_14.setBackground(Color.WHITE);
        label_14.setBounds(10, 283, 112, 30);
        layeredPane_sef.add(label_14);

        JTextArea textArea_5 = new JTextArea();
        textArea_5.setLineWrap(true);
        textArea_5.setFont(new Font("Î¢ÈíÑÅºÚ Light", Font.PLAIN, 16));
        textArea_5.setBackground(SystemColor.menu);
        textArea_5.setBounds(128, 283, 120, 30);
        layeredPane_sef.add(textArea_5);

        Label label_15 = new Label("omcid");
        label_15.setForeground(Color.BLACK);
        label_15.setFont(new Font("Î¢ÈíÑÅºÚ Light", Font.BOLD, 12));
        label_15.setBackground(Color.WHITE);
        label_15.setBounds(254, 283, 307, 30);
        layeredPane_sef.add(label_15);

        Label label_16 = new Label("\u6587\u4EF6\u5206\u9694\u7B26\uFF1A");
        label_16.setForeground(Color.BLACK);
        label_16.setFont(new Font("Î¢ÈíÑÅºÚ Light", Font.BOLD, 13));
        label_16.setBackground(Color.WHITE);
        label_16.setBounds(10, 317, 112, 30);
        layeredPane_sef.add(label_16);

        JTextArea textArea_6 = new JTextArea();
        textArea_6.setText("|");
        textArea_6.setLineWrap(true);
        textArea_6.setFont(new Font("Î¢ÈíÑÅºÚ Light", Font.PLAIN, 16));
        textArea_6.setBackground(SystemColor.menu);
        textArea_6.setBounds(128, 317, 120, 30);
        layeredPane_sef.add(textArea_6);

        Label label_17 = new Label("csv\u6587\u4EF6\u7684\u5206\u9694\u7B26\uFF0C\u4E00\u822C\u4E3A[ | ]");
        label_17.setForeground(Color.BLACK);
        label_17.setFont(new Font("Î¢ÈíÑÅºÚ Light", Font.BOLD, 12));
        label_17.setBackground(Color.WHITE);
        label_17.setBounds(254, 317, 307, 30);
        layeredPane_sef.add(label_17);
        layeredPane_sef.setVisible(false);

        layeredPane_build = new JLayeredPane();
        layeredPane_build.setBounds(232, 54, 567, 351);
        InfoSend.getContentPane().add(layeredPane_build);

        textArea_info = new JTextArea();
        JScrollPane js = new JScrollPane(textArea_info);
        js.setBounds(28, 31, 539, 307);
        layeredPane_build.add(js);
        layeredPane_build.setVisible(false);
        js.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        js.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        textArea_info.setFont(new Font("Î¢ÈíÑÅºÚ Light", Font.PLAIN, 16));
        textArea_info.setBackground(SystemColor.control);

        button.setFont(new Font("Trebuchet MS", Font.PLAIN, 18));
        button.setForeground(new Color(255, 255, 255));
        button.setBackground(FrameHelper.MAIN_TONE);
        button.setToolTipText("\u53C2\u6570\u8BF4\u660E");
        button.setBounds(715, 0, 84, 48);
        InfoSend.getContentPane().add(button);
        InfoSend.getContentPane().add(comboBox_interface);
        InfoSend.getContentPane().add(canvas);

        Panel panel = new Panel();
        panel.setBounds(200, 0, 599, 48);
        panel.setBackground(FrameHelper.MAIN_TONE);
        InfoSend.getContentPane().add(panel);

        JButton btnNewButton_send = new JButton("SEND");
        btnNewButton_send.setBackground(Color.WHITE);

        /**
         * SEND°´Å¥¼àÌý
         */
        btnNewButton_send.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {

                    /* ²ÎÊý¸³Öµ */
//					ContralCenter.getContral().setParam("msg", "string", textArea_info.getText(),
//							Propert.PARAM_CANT_BE_EMPTY);
//					ContralCenter.getContral().setParam("GNDP_port", "int", textArea_port.getText(),
//							Propert.PARAM_CANT_BE_EMPTY);
//					ContralCenter.getContral().setParam("charset", "string", textArea_charset.getText(),
//							Propert.PARAM_CAN_BE_EMPTY);

                    /* µ÷ÓÃÏÂÒ»¸ö½çÃæ£¬Òþ²Øµ±Ç°½çÃæ */
                    GNDPSend.initInterfacePage().getInterfacePage();
                    InfoSend.setVisible(false);
                } catch (Throwable t) {
                    log.error("Ìá½»²ÎÊýÒì³£", t);

                    /* ÉèÖÃ±¨´íµ¯´° */
                    JOptionPane.showConfirmDialog(InfoSend, t.getMessage(), "Error", JOptionPane.OK_CANCEL_OPTION,
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
        });

        btnNewButton_send.setBounds(48, 414, 141, 61);
        btnNewButton_send.setFont(new Font("Î¢ÈíÑÅºÚ Light", Font.PLAIN, 20));
        InfoSend.getContentPane().add(btnNewButton_send);

        Label label_interface = new Label("interface");
        label_interface.setBounds(0, 162, 76, 30);
        label_interface.setForeground(Color.WHITE);
        label_interface.setAlignment(Label.CENTER);
        label_interface.setFont(new Font("Î¢ÈíÑÅºÚ Light", Font.BOLD, 13));
        label_interface.setBackground(FrameHelper.LABEL_TONE);
        InfoSend.getContentPane().add(label_interface);

        Label label_charset = new Label("charset");
        label_charset.setForeground(Color.WHITE);
        label_charset.setFont(new Font("Î¢ÈíÑÅºÚ Light", Font.BOLD, 13));
        label_charset.setBackground(FrameHelper.LABEL_TONE);
        label_charset.setAlignment(Label.CENTER);
        label_charset.setBounds(0, 122, 76, 30);
        InfoSend.getContentPane().add(label_charset);

        textArea_charset = new JTextArea();
        textArea_charset.setText("UTF-8");
        textArea_charset.setLineWrap(true);
        textArea_charset.setFont(new Font("Î¢ÈíÑÅºÚ Light", Font.PLAIN, 16));
        textArea_charset.setBackground(SystemColor.menu);
        textArea_charset.setBounds(94, 122, 120, 30);
        InfoSend.getContentPane().add(textArea_charset);

        JLabel lblNewLabel_logo = new JLabel("");
        lblNewLabel_logo.setIcon(
                new ImageIcon(FrameHelper.LOGO_ICON_IMAGE_PATH));
        lblNewLabel_logo.setBounds(651, 414, 134, 80);
        InfoSend.getContentPane().add(lblNewLabel_logo);

        select_group = new ButtonGroup();
        JCheckBox checkBox_sef = new JCheckBox("\u81EA\u5B9A\u4E49\u6D88\u606F");
        checkBox_sef.setBackground(Color.WHITE);

        /* ×Ô¶¨ÒåÏûÏ¢°´Å¥¼àÌý */
        checkBox_sef.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                layeredPane_build.setVisible(true);
                layeredPane_sef.setVisible(false);
            }
        });
        checkBox_sef.setFont(new Font("Î¢ÈíÑÅºÚ Light", Font.PLAIN, 13));
        checkBox_sef.setBounds(0, 77, 93, 27);
        InfoSend.getContentPane().add(checkBox_sef);

        JCheckBox checkBox_build = new JCheckBox("\u6784\u9020\u6D88\u606F");
        checkBox_build.setBackground(Color.WHITE);

        /* ¹¹Ôì°´Å¥¼àÌý */
        checkBox_build.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                layeredPane_build.setVisible(false);
                layeredPane_sef.setVisible(true);
            }
        });
        checkBox_build.setFont(new Font("Î¢ÈíÑÅºÚ Light", Font.PLAIN, 13));
        checkBox_build.setBounds(99, 77, 93, 27);
        InfoSend.getContentPane().add(checkBox_build);

        select_group.add(checkBox_sef);
        select_group.add(checkBox_build);

        layeredPane_socket = new JLayeredPane();
        layeredPane_socket.setBounds(0, 190, 214, 154);
        InfoSend.getContentPane().add(layeredPane_socket);

        Label label_port = new Label("port");
        label_port.setForeground(Color.WHITE);
        label_port.setFont(new Font("Î¢ÈíÑÅºÚ Light", Font.BOLD, 13));
        label_port.setBackground(FrameHelper.LABEL_TONE);
        label_port.setAlignment(Label.CENTER);
        label_port.setBounds(0, 10, 76, 30);
        layeredPane_socket.add(label_port);

        JTextArea textArea_port = new JTextArea();
        textArea_port.setLineWrap(true);
        textArea_port.setFont(new Font("Î¢ÈíÑÅºÚ Light", Font.PLAIN, 16));
        textArea_port.setBackground(SystemColor.menu);
        textArea_port.setBounds(94, 10, 120, 30);
        layeredPane_socket.add(textArea_port);

        Label label_channl = new Label("channl");
        label_channl.setForeground(Color.WHITE);
        label_channl.setFont(new Font("Î¢ÈíÑÅºÚ Light", Font.BOLD, 13));
        label_channl.setBackground(FrameHelper.LABEL_TONE);
        label_channl.setAlignment(Label.CENTER);
        label_channl.setBounds(0, 50, 76, 30);
        layeredPane_socket.add(label_channl);

        JTextArea textArea_channl = new JTextArea();
        textArea_channl.setLineWrap(true);
        textArea_channl.setFont(new Font("Î¢ÈíÑÅºÚ Light", Font.PLAIN, 16));
        textArea_channl.setBackground(SystemColor.menu);
        textArea_channl.setBounds(94, 50, 120, 30);
        layeredPane_socket.add(textArea_channl);

        layeredPane_kafka = new JLayeredPane();
        layeredPane_kafka.setBounds(0, 190, 214, 152);
        InfoSend.getContentPane().add(layeredPane_kafka);

        Label label_zk = new Label("zookeeper");
        label_zk.setForeground(Color.WHITE);
        label_zk.setFont(new Font("Î¢ÈíÑÅºÚ Light", Font.BOLD, 13));
        label_zk.setBackground(FrameHelper.LABEL_TONE);
        label_zk.setAlignment(Label.CENTER);
        label_zk.setBounds(0, 10, 76, 30);
        layeredPane_kafka.add(label_zk);

        JTextArea textArea_zk = new JTextArea();
        textArea_zk.setLineWrap(true);
        textArea_zk.setFont(new Font("Î¢ÈíÑÅºÚ Light", Font.PLAIN, 16));
        textArea_zk.setBackground(SystemColor.menu);
        textArea_zk.setBounds(94, 10, 120, 30);
        layeredPane_kafka.add(textArea_zk);

        Label label_url = new Label("bootstrap");
        label_url.setForeground(Color.WHITE);
        label_url.setFont(new Font("Î¢ÈíÑÅºÚ Light", Font.BOLD, 13));
        label_url.setBackground(FrameHelper.LABEL_TONE);
        label_url.setAlignment(Label.CENTER);
        label_url.setBounds(0, 50, 76, 30);
        layeredPane_kafka.add(label_url);

        JTextArea textArea_url = new JTextArea();
        textArea_url.setLineWrap(true);
        textArea_url.setFont(new Font("Î¢ÈíÑÅºÚ Light", Font.PLAIN, 16));
        textArea_url.setBackground(SystemColor.menu);
        textArea_url.setBounds(94, 52, 120, 30);
        layeredPane_kafka.add(textArea_url);

        Label label_topic = new Label("topic");
        label_topic.setForeground(Color.WHITE);
        label_topic.setFont(new Font("Î¢ÈíÑÅºÚ Light", Font.BOLD, 13));
        label_topic.setBackground(FrameHelper.LABEL_TONE);
        label_topic.setAlignment(Label.CENTER);
        label_topic.setBounds(0, 90, 76, 30);
        layeredPane_kafka.add(label_topic);

        JTextArea textArea_topic = new JTextArea();
        textArea_topic.setLineWrap(true);
        textArea_topic.setFont(new Font("Î¢ÈíÑÅºÚ Light", Font.PLAIN, 16));
        textArea_topic.setBackground(SystemColor.menu);
        textArea_topic.setBounds(94, 90, 120, 30);
        layeredPane_kafka.add(textArea_topic);
        layeredPane_kafka.setVisible(false);

        comboBox_version = new JComboBox();
        comboBox_version.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (comboBox_version.getSelectedIndex() == 2) {
                    layeredPane_socket.setVisible(false);
                    layeredPane_kafka.setVisible(true);
                } else {
                    layeredPane_kafka.setVisible(false);
                    layeredPane_socket.setVisible(true);
                }
            }
        });
        comboBox_version.setModel(new DefaultComboBoxModel(new String[]{"SOCKET-GCP", "SOCKET-Gframe", "Kafka"}));
        comboBox_version.setFont(new Font("µÈÏß Light", Font.PLAIN, 13));
        comboBox_version.setBackground(Color.WHITE);
        comboBox_version.setBounds(94, 164, 120, 30);
        InfoSend.getContentPane().add(comboBox_version);

        /* R°´Å¥¼àÌý */

    }

    /**
     * »ñÈ¡GNDPPage¶ÔÏó
     *
     * @return GNDPPage¶ÔÏó
     */
    public static GNDPPage initInterfacePage() {
        return gndppage;
    }

    /**
     * ´ò¿ªGNDPPage½çÃæ£¬Ã¿´Î»ñÈ¡Ê±£¬¸üÐÂ½Ó¿ÚÑ¡Ôñ¿òÏÔÊ¾
     */
    public void getInterfacePage() {
        comboBox_interface.setModel(new DefaultComboBoxModel(
                new String[]{"GNDPÈë¿âÏûÏ¢", "SOCKET\u670D\u52A1\u7AEF", "SOCKET\u5BA2\u6237\u7AEF", "SNMP", "Telnet",
                        "ActiveMQ", "IBMMQ", "OMC\u5317\u5411\u544A\u8B66", "Kafka"}));
        InfoSend.getContentPane().add(comboBox_interface);
        InfoSend.setVisible(true);

    }

    public static void main(String[] args) {
        GNDPPage.initInterfacePage().getInterfacePage();
    }
}

package priv.suki.frame.snmp;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;

import java.awt.Font;
import java.awt.Color;
import java.awt.Dialog.ModalExclusionType;
import java.awt.Dimension;
import java.awt.Panel;
import java.awt.Canvas;
import javax.swing.JScrollPane;

import priv.suki.controller.ContralCenter;
import priv.suki.frame.util.FrameHelper;
import priv.suki.msg.SNMPBindVar;
import priv.suki.util.FrameUtil;
import priv.suki.util.InterfaceEnum;
import priv.suki.util.Propert;

import javax.swing.JOptionPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.awt.event.ItemEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.Label;
import javax.swing.JLayeredPane;
import javax.swing.JTable;
import javax.swing.JCheckBox;

/**
 * SNMP接口参数配置界面
 *
 * @author 花小琪
 * @version 1.0
 */
public class SNMPPage {
    // Log
    private static Log log = LogFactory.getLog(SNMPPage.class);
    private static SNMPPage SNMPPage = new SNMPPage();
    private static JFrame InfoSend;
    private static JComboBox comboBox_interface;
    private static JTextArea textArea_port;
    private static JTextArea textArea_ip;
    private static JTextArea textArea_oid;
    private static JTable msg_table;
    private static JButton button_add;
    private static DefaultTableModel model;
    private static JComboBox jBoxType;
    private static JButton button_delete;
    private static JButton button_clear;
    private static JComboBox comboBox_version;
    private int tableSelectRow;
    private static JTextArea textArea_charset;
    private JLayeredPane layeredPane_auth;
    private JCheckBox chckbxNewCheckBox_auth;
    private JLayeredPane layeredPane_pwd;
    private JTextArea textArea_username;
    private JTextArea textArea_pwd;
    private JButton button;

    /**
     * SNMPPage构造模块，初始化界面
     */
    public SNMPPage() {
        Thread.currentThread().setName("SNMPPage");
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
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    ContralCenter.getContral().setSelInterface(e.getItem().toString());
                    InfoSend.setVisible(false);
                }
                ;
            }
        });
        comboBox_interface.setToolTipText("");
        comboBox_interface.setBackground(Color.WHITE);
        comboBox_interface.setForeground(Color.BLACK);
        comboBox_interface.setFont(new Font("微软雅黑 Light", Font.PLAIN, 13));
        comboBox_interface.setModel(new DefaultComboBoxModel(FrameUtil.getInfArray(InterfaceEnum.SNMP.getCName())));

        Canvas canvas = new Canvas();
        canvas.setBounds(0, 0, 0, 0);
        canvas.setBackground(new Color(0, 0, 0));
        InfoSend.getContentPane().setLayout(null);

        button = new JButton("R");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                SNMPReadPage.initInterfacePage().getInterfacePage();
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

        Panel panel = new Panel();
        panel.setBounds(200, 0, 599, 48);
        panel.setBackground(FrameHelper.MAIN_TONE);
        InfoSend.getContentPane().add(panel);

        JButton btnNewButton_send = new JButton("SEND");
        btnNewButton_send.setBackground(Color.WHITE);
        btnNewButton_send.setIcon(null);

        /**
         * SEND按钮监听
         */
        btnNewButton_send.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    if (msg_table.getCellEditor() != null) {
                        msg_table.getCellEditor().stopCellEditing();// 保存单元格
                    }
                    /* 参数赋值 */
//					ContralCenter.getContral().setParam("msg", "string", textArea_info.getText(),
//							Propert.PARAM_CANT_BE_EMPTY);
                    ContralCenter.getContral().setParam("snmp_trapOid", "string", textArea_oid.getText(),
                            Propert.PARAM_CANT_BE_EMPTY);
                    ContralCenter.getContral().setParam("snmp_port", "int", textArea_port.getText(),
                            Propert.PARAM_CANT_BE_EMPTY);
                    ContralCenter.getContral().setParam("snmp_ip", "String", textArea_ip.getText(),
                            Propert.PARAM_CANT_BE_EMPTY);
                    ContralCenter.getContral().setParam("snmp_version", "int",
                            String.valueOf(1 + comboBox_version.getSelectedIndex()), Propert.PARAM_CANT_BE_EMPTY);
                    ContralCenter.getContral().setParam("charset", "string", textArea_charset.getText(),
                            Propert.PARAM_CAN_BE_EMPTY);
                    ContralCenter.getContral().setParam("snmp_auth", "Boolean",
                            String.valueOf(chckbxNewCheckBox_auth.isSelected()), Propert.PARAM_CAN_BE_EMPTY);
                    ContralCenter.getContral().setParam("snmp_userName", "String", textArea_username.getText(),
                            Propert.PARAM_CAN_BE_EMPTY);
                    ContralCenter.getContral().setParam("authPass", "string", textArea_pwd.getText(),
                            Propert.PARAM_CAN_BE_EMPTY);
                    List<SNMPBindVar> snmpMsg = new ArrayList<SNMPBindVar>();

                    for (int i = 0; i < msg_table.getRowCount(); i++) {

                        SNMPBindVar bind_var = new SNMPBindVar();

                        bind_var.setTrapOid(msg_table.getValueAt(i, 0).toString());
                        bind_var.setBindVar(msg_table.getValueAt(i, 1).toString());
                        bind_var.setTrapType(msg_table.getValueAt(i, 2).toString());
                        snmpMsg.add(bind_var);

                    }
                    ContralCenter.getContral().setParam("snmp_msg", snmpMsg);

                    /* 调用下一个界面，隐藏当前界面 */
                    SNMPSend.initInterfacePage().getInterfacePage();
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
        label_port.setBounds(0, 152, 76, 30);
        label_port.setForeground(Color.WHITE);
        label_port.setAlignment(Label.CENTER);
        label_port.setFont(new Font("微软雅黑 Light", Font.BOLD, 13));
        label_port.setBackground(FrameHelper.LABEL_TONE);
        InfoSend.getContentPane().add(label_port);

        textArea_port = new JTextArea();
        textArea_port.setLineWrap(true);
        textArea_port.setFont(new Font("微软雅黑 Light", Font.PLAIN, 13));
        textArea_port.setBackground(SystemColor.menu);
        textArea_port.setBounds(95, 152, 146, 30);
        InfoSend.getContentPane().add(textArea_port);

        Label label_ip = new Label("Ip");
        label_ip.setForeground(Color.WHITE);
        label_ip.setFont(new Font("微软雅黑 Light", Font.BOLD, 13));
        label_ip.setBackground(FrameHelper.LABEL_TONE);
        label_ip.setAlignment(Label.CENTER);
        label_ip.setBounds(0, 188, 76, 30);
        InfoSend.getContentPane().add(label_ip);

        textArea_ip = new JTextArea();
        textArea_ip.setLineWrap(true);
        textArea_ip.setFont(new Font("微软雅黑 Light", Font.PLAIN, 13));
        textArea_ip.setBackground(SystemColor.menu);
        textArea_ip.setBounds(95, 188, 146, 30);
        InfoSend.getContentPane().add(textArea_ip);

        Label label_TrapOid = new Label("TrapOid");
        label_TrapOid.setForeground(Color.WHITE);
        label_TrapOid.setFont(new Font("微软雅黑 Light", Font.BOLD, 13));
        label_TrapOid.setBackground(FrameHelper.LABEL_TONE);
        label_TrapOid.setAlignment(Label.CENTER);
        label_TrapOid.setBounds(0, 116, 76, 30);
        InfoSend.getContentPane().add(label_TrapOid);

        textArea_oid = new JTextArea();
        textArea_oid.setLineWrap(true);
        textArea_oid.setFont(new Font("微软雅黑 Light", Font.PLAIN, 13));
        textArea_oid.setBackground(SystemColor.menu);
        textArea_oid.setBounds(95, 116, 146, 30);
        InfoSend.getContentPane().add(textArea_oid);

        JScrollPane layeredPane = new JScrollPane();
        layeredPane.setBounds(339, 88, 294, 348);
        InfoSend.getContentPane().add(layeredPane);

        String[] columnNames = {"trap_oid", "value", "type"};
        model = new DefaultTableModel();
        model.setColumnIdentifiers(columnNames);
        msg_table = new JTable(model);
        /**
         * 表格选择行，选中行按钮监听
         */
        msg_table.addMouseListener(new MouseAdapter() {

            public void mouseClicked(MouseEvent e) {
                if ((tableSelectRow = msg_table.getSelectedRow()) == -1) {
                    return;
                }
            }
        });
        msg_table.setBounds(0, 89, 431, 457);
        msg_table.setRowHeight(18);
        msg_table.getColumnModel().getColumn(1).setPreferredWidth(80);
        msg_table.getColumnModel().getColumn(1).setPreferredWidth(100);
        msg_table.getColumnModel().getColumn(2).setPreferredWidth(50);

        /* 设置表头样式 */
        Dimension preferredSize = msg_table.getTableHeader().getPreferredSize();
        preferredSize.height = 20;
        msg_table.getTableHeader().setFont(new Font("等线 Light", Font.PLAIN, 13));
        msg_table.getTableHeader().setPreferredSize(preferredSize);
//		msg_table.getTableHeader().setBackground(new Color(192, 192, 192));
        TableColumn column = new TableColumn();
        column = msg_table.getColumnModel().getColumn(2);
        jBoxType = new JComboBox();
        jBoxType.addItem("OctetString");
        jBoxType.addItem("Integer");
        jBoxType.addItem("TimeTicks");
        column.setCellEditor(new DefaultCellEditor(jBoxType));
        layeredPane.setViewportView(msg_table);

        button_add = new JButton("\u6DFB\u52A0\u884C");
        button_add.setBackground(Color.WHITE);
        /**
         * 添加行按钮监听
         */
        button_add.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    ContralCenter.getContral().setParam("snmp_trapOid", "string", textArea_oid.getText(),
                            Propert.PARAM_CANT_BE_EMPTY);
                    Vector<String> addmsg = new Vector<String>(3);
                    addmsg.add(0, Propert.getPropert().getSnmp_trapOid());
                    addmsg.add(1, "");
                    addmsg.add(2, "OctetString");
                    model.addRow(addmsg);
                } catch (Throwable t) {

                    /* 设置报错弹窗 */
                    JOptionPane.showConfirmDialog(InfoSend, t.getMessage(), "Error", JOptionPane.OK_CANCEL_OPTION,
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
        });
        button_add.setFont(new Font("等线 Light", Font.PLAIN, 15));
        button_add.setBounds(672, 163, 113, 54);
        InfoSend.getContentPane().add(button_add);

        button_delete = new JButton("\u5220\u9664\u884C");
        button_delete.setBackground(Color.WHITE);

        /**
         * 删除行按钮监听
         */
        button_delete.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (tableSelectRow > -1 && tableSelectRow < msg_table.getRowCount() + 1) {
                    model.removeRow(tableSelectRow);
                } else {
                    /* 设置报错弹窗 */
                    JOptionPane.showConfirmDialog(InfoSend, "请选中待删除行", "Error", JOptionPane.OK_CANCEL_OPTION,
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
        });
        button_delete.setFont(new Font("等线 Light", Font.PLAIN, 15));
        button_delete.setBounds(672, 217, 113, 54);
        InfoSend.getContentPane().add(button_delete);

        button_clear = new JButton("\u6E05\u7A7A\u5217\u8868");
        button_clear.setBackground(Color.WHITE);
        /**
         * 清空列表按钮监听
         */
        button_clear.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                for (int i = msg_table.getRowCount() - 1; i > -1; i--) {
                    model.removeRow(i);
                }

            }
        });
        button_clear.setFont(new Font("等线 Light", Font.PLAIN, 15));
        button_clear.setBounds(672, 271, 113, 54);
        InfoSend.getContentPane().add(button_clear);

        comboBox_version = new JComboBox();
        /* 版本选择框监听 */
        comboBox_version.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (comboBox_version.getSelectedIndex() == 2) {
                    layeredPane_auth.setVisible(true);
                } else {
                    layeredPane_auth.setVisible(false);
                }
            }
        });
        comboBox_version.setBackground(new Color(255, 255, 255));
        comboBox_version.setModel(
                new DefaultComboBoxModel(new String[]{"SNMP_TRAP_V1.0", "SNMP_TRAP_V2.0", "SNMP_TRAP_V3.0"}));
        comboBox_version.setFont(new Font("等线 Light", Font.PLAIN, 14));
        comboBox_version.setBounds(95, 80, 146, 30);
        InfoSend.getContentPane().add(comboBox_version);

        Label label_version = new Label("Version");
        label_version.setForeground(Color.WHITE);
        label_version.setFont(new Font("微软雅黑 Light", Font.BOLD, 13));
        label_version.setBackground(FrameHelper.LABEL_TONE);
        label_version.setAlignment(Label.CENTER);
        label_version.setBounds(0, 80, 76, 30);
        InfoSend.getContentPane().add(label_version);

        Label label_charset = new Label("Charset");
        label_charset.setForeground(Color.WHITE);
        label_charset.setFont(new Font("微软雅黑 Light", Font.BOLD, 13));
        label_charset.setBackground(FrameHelper.LABEL_TONE);
        label_charset.setAlignment(Label.CENTER);
        label_charset.setBounds(0, 224, 76, 30);
        InfoSend.getContentPane().add(label_charset);

        textArea_charset = new JTextArea();
        textArea_charset.setText("UTF-8");
        textArea_charset.setLineWrap(true);
        textArea_charset.setFont(new Font("微软雅黑 Light", Font.PLAIN, 13));
        textArea_charset.setBackground(SystemColor.menu);
        textArea_charset.setBounds(95, 224, 146, 30);
        InfoSend.getContentPane().add(textArea_charset);

        JLabel lblNewLabel_logo = new JLabel("");
        lblNewLabel_logo.setIcon(
                new ImageIcon(FrameHelper.LOGO_ICON_IMAGE_PATH));
        lblNewLabel_logo.setBounds(651, 414, 134, 80);
        InfoSend.getContentPane().add(lblNewLabel_logo);

        layeredPane_auth = new JLayeredPane();
        layeredPane_auth.setBounds(0, 260, 269, 35);
        InfoSend.getContentPane().add(layeredPane_auth);

        chckbxNewCheckBox_auth = new JCheckBox("\u5F00\u542F\u52A0\u5BC6\u6A21\u5F0F");
        /* 加密复选框监听 */
        chckbxNewCheckBox_auth.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    layeredPane_pwd.setVisible(true);
                } else {
                    layeredPane_pwd.setVisible(false);
                }

            }
        });
        chckbxNewCheckBox_auth.setFont(new Font("等线 Light", Font.PLAIN, 14));
        chckbxNewCheckBox_auth.setBackground(Color.WHITE);
        chckbxNewCheckBox_auth.setBounds(96, 0, 173, 30);
        layeredPane_auth.add(chckbxNewCheckBox_auth);

        Label label_snmp_auth = new Label("snmp_auth");
        label_snmp_auth.setForeground(Color.WHITE);
        label_snmp_auth.setFont(new Font("微软雅黑 Light", Font.BOLD, 13));
        label_snmp_auth.setBackground(FrameHelper.LABEL_TONE);
        label_snmp_auth.setAlignment(Label.CENTER);
        label_snmp_auth.setBounds(0, 0, 76, 30);
        layeredPane_auth.add(label_snmp_auth);
        layeredPane_auth.setVisible(false);

        layeredPane_pwd = new JLayeredPane();
        layeredPane_pwd.setBounds(0, 298, 256, 90);
        InfoSend.getContentPane().add(layeredPane_pwd);

        Label label_username = new Label("Username");
        label_username.setForeground(Color.WHITE);
        label_username.setFont(new Font("微软雅黑 Light", Font.BOLD, 13));
        label_username.setBackground(FrameHelper.LABEL_TONE);
        label_username.setAlignment(Label.CENTER);
        label_username.setBounds(0, 0, 76, 30);
        layeredPane_pwd.add(label_username);

        textArea_username = new JTextArea();
        textArea_username.setLineWrap(true);
        textArea_username.setFont(new Font("微软雅黑 Light", Font.PLAIN, 13));
        textArea_username.setBackground(SystemColor.menu);
        textArea_username.setBounds(95, 0, 145, 30);
        layeredPane_pwd.add(textArea_username);

        Label label_pwd = new Label("AuthPass");
        label_pwd.setForeground(Color.WHITE);
        label_pwd.setFont(new Font("微软雅黑 Light", Font.BOLD, 13));
        label_pwd.setBackground(FrameHelper.LABEL_TONE);
        label_pwd.setAlignment(Label.CENTER);
        label_pwd.setBounds(0, 36, 76, 30);
        layeredPane_pwd.add(label_pwd);

        textArea_pwd = new JTextArea();
        textArea_pwd.setLineWrap(true);
        textArea_pwd.setFont(new Font("微软雅黑 Light", Font.PLAIN, 13));
        textArea_pwd.setBackground(SystemColor.menu);
        textArea_pwd.setBounds(95, 36, 145, 30);
        layeredPane_pwd.add(textArea_pwd);
        layeredPane_pwd.setVisible(false);

    }

    /**
     * 获取SNMPPage对象
     *
     * @return SNMPPage对象
     */
    public static SNMPPage initInterfacePage() {
        return SNMPPage;
    }

    /**
     * 打开SNMPPage界面，每次获取时，更新接口选择框显示
     */
    public void getInterfacePage() {
        comboBox_interface.setModel(new DefaultComboBoxModel(FrameUtil.getInfArray(InterfaceEnum.SNMP.getCName())));
        InfoSend.getContentPane().add(comboBox_interface);
        InfoSend.setVisible(true);

    }
}

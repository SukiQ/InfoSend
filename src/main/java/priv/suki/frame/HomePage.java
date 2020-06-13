package priv.suki.frame;

import priv.suki.controller.ContralCenter;
import priv.suki.frame.util.FrameHelper;
import priv.suki.util.DynamicLogUtil;
import priv.suki.util.FrameUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.Dialog.ModalExclusionType;
import java.awt.event.ItemEvent;

/**
 * 起始页
 *
 * @author 花小琪
 * @version 1.0
 */
public class HomePage {

    private static JFrame InfoSend;
    private static final HomePage HOME_PAGE = new HomePage();

    /**
     * 单例模式，获取HomePage对象
     *
     * @return HomePage对象
     */
    public static HomePage getHomePage() {
        return HOME_PAGE;
    }

    /**
     * HomePage构造模块，初始化界面
     */
    public HomePage() {
        DynamicLogUtil.getDynamicLogUtil().clearDynamicLog();
        Thread.currentThread().setName("HomePage");
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

        JComboBox comboBoxInterface = new JComboBox();
        comboBoxInterface.setBounds(0, 0, 200, 48);

        comboBoxInterface.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                ContralCenter.getContral().setSelInterface(e.getItem().toString());
                InfoSend.setVisible(false);
            }

        });

        comboBoxInterface.setToolTipText("");
        comboBoxInterface.setBackground(Color.WHITE);
        comboBoxInterface.setForeground(Color.BLACK);
        comboBoxInterface.setFont(new Font("微软雅黑 Light", Font.PLAIN, 13));
        comboBoxInterface.setModel(new DefaultComboBoxModel(FrameUtil.getHomeInfArray()));
        InfoSend.getContentPane().setLayout(null);
        JButton button = new JButton("R");

        button.addActionListener(e -> JOptionPane.showConfirmDialog(InfoSend, FrameHelper.DEVELOPER_LOG, "开发者日志", JOptionPane.CLOSED_OPTION,
                JOptionPane.INFORMATION_MESSAGE));

        button.setToolTipText("\u53C2\u6570\u8BF4\u660E");
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Trebuchet MS", Font.PLAIN, 18));
        button.setBackground(FrameHelper.MAIN_TONE);
        button.setBounds(715, 0, 84, 48);
        InfoSend.getContentPane().add(button);
        InfoSend.getContentPane().add(comboBoxInterface);

        Panel panel = new Panel();
        panel.setBounds(200, 0, 599, 48);
        panel.setBackground(FrameHelper.MAIN_TONE);
        InfoSend.getContentPane().add(panel);

        JLabel lblNewLabelTitle = new JLabel("");
        lblNewLabelTitle.setIcon(new ImageIcon(FrameHelper.TITLE_ICON_IMAGE_PATH));
        lblNewLabelTitle.setBounds(65, 114, 687, 303);
        InfoSend.getContentPane().add(lblNewLabelTitle);

        /*R按钮监听*/
        InfoSend.setVisible(true);


    }
}

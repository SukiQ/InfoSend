package priv.suki.frame.northalarm;

import javax.swing.JFrame;
import java.awt.Font;
import java.awt.Color;
import java.awt.Dialog.ModalExclusionType;
import java.awt.Panel;
import java.awt.Canvas;

import priv.suki.frame.util.FrameHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import priv.suki.frame.northalarm.NorthAlarmPage;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import java.awt.Toolkit;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.swing.JEditorPane;

/**
 * ����ӿڲ���˵������
 *
 * @author ��С��
 * @version 1.0
 */
public class NorthAlarmReadPage {
    // Log
    private static Log log = LogFactory.getLog(NorthAlarmReadPage.class);
    private static NorthAlarmReadPage northAlarmReadPage = new NorthAlarmReadPage();
    private static JFrame InfoSend;

    /**
     * ����ӿڹ���ģ�飬��ʼ������
     */
    public NorthAlarmReadPage() {
        Thread.currentThread().setName("����ӿ�");
        initialize();
    }

    /**
     * ��ʼ������
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
        InfoSend.setFont(new Font("����", Font.PLAIN, 12));
        InfoSend.getContentPane().setFont(new Font("΢���ź� Light", Font.BOLD, 15));
        InfoSend.setBounds(100, 100, 805, 542);
        InfoSend.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        InfoSend.setLocationRelativeTo(null);

        Canvas canvas = new Canvas();
        canvas.setBounds(0, 0, 0, 0);
        canvas.setBackground(new Color(0, 0, 0));
        InfoSend.getContentPane().setLayout(null);
        InfoSend.getContentPane().add(canvas);

        Panel panel = new Panel();
        panel.setBounds(200, 0, 599, 48);
        panel.setBackground(FrameHelper.MAIN_TONE);
        InfoSend.getContentPane().add(panel);

        JLabel lblNewLabel_logo = new JLabel("");
        lblNewLabel_logo.setIcon(new ImageIcon(FrameHelper.LOGO_ICON_IMAGE_PATH));
        lblNewLabel_logo.setBounds(651, 414, 134, 80);
        InfoSend.getContentPane().add(lblNewLabel_logo);

        JButton btnNewButton_return = new JButton("Return");
        btnNewButton_return.setBackground(new Color(255, 255, 255));
        btnNewButton_return.setToolTipText("");

        /*Return��ť����*/
        btnNewButton_return.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                NorthAlarmPage.initInterfacePage().getInterfacePage();
                InfoSend.setVisible(false);
            }
        });
        btnNewButton_return.setFont(new Font("Trebuchet MS", Font.PLAIN, 15));
        btnNewButton_return.setBounds(0, 0, 201, 48);
        InfoSend.getContentPane().add(btnNewButton_return);

        JEditorPane description = new JEditorPane();
        description.setFont(new Font("΢���ź� Light", Font.PLAIN, 16));
        description.setText("\u63A5\u53E3\u8BF4\u660E\uFF1AOMC\u5317\u5411\u5B9E\u65F6\u63A5\u53E3\u6A21\u62DF\uFF0C\u5B9E\u73B0\u4E86\u5B9E\u65F6\u63A5\u53E3\u8865\u53D1\u544A\u8B66\u529F\u80FD\r\n\u53C2\u6570\u8BF4\u660E\uFF1A\r\nport\uFF1A\u670D\u52A1\u7AEF\u7AEF\u53E3\r\nuser\uFF1A\u767B\u9646\u7528\u6237\u540D\r\npassword\uFF1A\u767B\u9646\u5BC6\u7801\r\nalarmid\uFF1A\u544A\u8B66\u6D41\u6C34\u53F7\u7684\u5B57\u6BB5\uFF0C\u6D41\u6C34\u53F7\u5FC5\u987B\u9700\u4F7F\u7528\u51FD\u6570${id}\r\ncharset\uFF1A\u5B57\u7B26\u7F16\u7801\r\n\u6D88\u606F\u6837\u4F8B\uFF1ABEGIN{\"alarmSeq\":${id},\"alarmTitle\":\"[GPON\u544A\u8B66]ONU\u6389/\u5173\u7535\",\"alarmStatus\":1,\"alarmType\":\"equipmentAlarm\",\"origSeverity\":3,\"eventTime\":\"2019-04-05 06:29:14\",\"alarmId\":\"1545767785054\",\"specificProblemID\":\"35124\",\"specificProblem\":\"[GPON\u544A\u8B66]ONU\u6389/\u5173\u7535\"}END\r\n\r\n\u5907\u6CE8\uFF1A\u82E5\u5728\u4F7F\u7528InfoSend\u4E2D\u5B58\u5728\u95EE\u9898\u6216BUG\uFF0C\u6B22\u8FCE\u8054\u7CFB\u4F5C\u8005\uFF0C\u624B\u673A/\u5FAE\u4FE1\uFF1A18706768036\r\n");
        description.setBounds(14, 143, 758, 303);
        InfoSend.getContentPane().add(description);

        JEditorPane description_title = new JEditorPane();
        description_title.setText("OMC\u5317\u5411\u63A5\u53E3");
        description_title.setFont(new Font("΢���ź� Light", Font.BOLD, 20));
        description_title.setBounds(14, 91, 155, 48);
        InfoSend.getContentPane().add(description_title);

    }

    /**
     * ��ȡ����ӿڶ���
     *
     * @return ����ӿڶ���
     */
    public static NorthAlarmReadPage initInterfacePage() {
        return northAlarmReadPage;
    }

    /**
     * �򿪱���ӿڽ��棬ÿ�λ�ȡʱ�����½ӿ�ѡ�����ʾ
     */
    public void getInterfacePage() {
        InfoSend.setVisible(true);

    }
}

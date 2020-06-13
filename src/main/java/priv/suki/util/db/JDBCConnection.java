package priv.suki.util.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import priv.suki.util.AppenderThread;
import priv.suki.util.Propert;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class JDBCConnection {
    private static Log log = LogFactory.getLog(AppenderThread.class);

    /**
     * jdbcִ��sql���
     *
     * @param sql
     * @return
     */
    private boolean executingSql(Connection conn, String sql) {
        PreparedStatement preparedStatement = null;
        try {
            log.info("׼��ִ�е�sqlΪ��" + sql);
            preparedStatement = conn.prepareStatement(sql);
            preparedStatement.executeUpdate();
            log.info("sql[" + sql + "]ִ�гɹ�");
            return true;
        } catch (SQLException e) {
            log.error("sql[" + sql + "]ִ�г���", e);
        } finally {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                preparedStatement = null;
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    log.error("�ر����ݿ����ӳ���");
                }
                conn = null;
            }
        }
        return false;
    }

    public Connection getConnection() throws SQLException {
        Connection conn = null;
        String CLAZZ = null;// ���ݿ�����

        String dburl = Propert.getPropert().getDburl();
        String username = Propert.getPropert().getDbuser();
        String password = Propert.getPropert().getDbPassword();
        int dbType = Propert.getPropert().getDbType();

        if (dbType == Propert.ORACLE) {
            CLAZZ = "oracle.jdbc.driver.OracleDriver";
        }

        try {
            Class.forName(CLAZZ);

            conn = DriverManager.getConnection(dburl, username, password);

        } catch (ClassNotFoundException e) {
            log.error("ClassNotFoundException", e);
        } catch (SQLException e) {
            log.error("SQLException,��ȡ����ʧ�ܣ������к��Ե�1������", e);

        }

        return conn;
    }

//	public static void main(String[] args) throws SQLException {
//		String SYS_FILE_SPARATOR = System.getProperty("file.separator");
//		PropertyConfigurator.configure("." + SYS_FILE_SPARATOR + "cfg" + SYS_FILE_SPARATOR + "log4j.properties");
//		Propert.getPropert().setDburl(" jdbc:oracle:thin:@(DESCRIPTION =(ADDRESS_LIST =(ADDRESS = (PROTOCOL = TCP)(HOST = 192.168.1.63)(PORT = 1521)))(CONNECT_DATA =(SERVER = DEDICATED)(SERVICE_NAME  = orcl)))");
//		Propert.getPropert().setDbuser("iu");
//		Propert.getPropert().setDbPassword("iu");
//		Propert.getPropert().setDbType(1);
//		JDBCConnection a=new JDBCConnection();
//		a.executingSql(a.getConnection(),"create table t_stu(   stuid      number(10)   primary key,  stuname    varchar2(20) not null,  stusex     varchar2(2)  default '��' check(stusex in('��','Ů'))) ");
//	}

}

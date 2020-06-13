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
     * jdbc执行sql语句
     *
     * @param sql
     * @return
     */
    private boolean executingSql(Connection conn, String sql) {
        PreparedStatement preparedStatement = null;
        try {
            log.info("准备执行的sql为：" + sql);
            preparedStatement = conn.prepareStatement(sql);
            preparedStatement.executeUpdate();
            log.info("sql[" + sql + "]执行成功");
            return true;
        } catch (SQLException e) {
            log.error("sql[" + sql + "]执行出错", e);
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
                    log.error("关闭数据库连接出错");
                }
                conn = null;
            }
        }
        return false;
    }

    public Connection getConnection() throws SQLException {
        Connection conn = null;
        String CLAZZ = null;// 数据库驱动

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
            log.error("SQLException,获取连接失败，三秒中后尝试第1次重试", e);

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
//		a.executingSql(a.getConnection(),"create table t_stu(   stuid      number(10)   primary key,  stuname    varchar2(20) not null,  stusex     varchar2(2)  default '男' check(stusex in('男','女'))) ");
//	}

}

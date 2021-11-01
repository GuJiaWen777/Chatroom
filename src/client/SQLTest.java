/**  

* @Title: SQLTest.java  

* @Package client  

* @Description: TODO()

* @author Ocean  

* @date 2021年10月21日  

* @version V1.0  

*/  
package client;
import java.sql.*;


public class SQLTest {
	static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";  
	static final String DB_URL = "jdbc:mysql://localhost:3306/qquser?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
	static final String USER = "root";
    static final String PASS = "gjw523624";
    public static void main(String[] args) {
    	Connection conn = null;
        Statement stmt = null;
        try{
            // 注册 JDBC 驱动
            Class.forName(JDBC_DRIVER);
        
            // 打开链接
            System.out.println("连接数据库...");
            conn = DriverManager.getConnection(DB_URL,USER,PASS);
            System.out.println(" 实例化Statement对象...");
            stmt = conn.createStatement();
            String sql;
            sql="SELECT id FROM user";
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next()){
                // 通过字段检索
                String id  = rs.getString("id");
            
    
                // 输出数据
                System.out.print("ID: " + id);
            }
            // 完成后关闭
            rs.close();
            stmt.close();
            conn.close();
      
        }catch(SQLException se){
            // 处理 JDBC 错误
            se.printStackTrace();
        }catch(Exception e){
            // 处理 Class.forName 错误
            e.printStackTrace();
        }finally{
            // 关闭资源
            try{
                if(stmt!=null) stmt.close();
            }catch(SQLException se2){
            }// 什么都不做
            try{
                if(conn!=null) conn.close();
            }catch(SQLException se){
                se.printStackTrace();
            }
        }
    	
    }

}

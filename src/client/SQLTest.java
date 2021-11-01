/**  

* @Title: SQLTest.java  

* @Package client  

* @Description: TODO()

* @author Ocean  

* @date 2021��10��21��  

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
            // ע�� JDBC ����
            Class.forName(JDBC_DRIVER);
        
            // ������
            System.out.println("�������ݿ�...");
            conn = DriverManager.getConnection(DB_URL,USER,PASS);
            System.out.println(" ʵ����Statement����...");
            stmt = conn.createStatement();
            String sql;
            sql="SELECT id FROM user";
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next()){
                // ͨ���ֶμ���
                String id  = rs.getString("id");
            
    
                // �������
                System.out.print("ID: " + id);
            }
            // ��ɺ�ر�
            rs.close();
            stmt.close();
            conn.close();
      
        }catch(SQLException se){
            // ���� JDBC ����
            se.printStackTrace();
        }catch(Exception e){
            // ���� Class.forName ����
            e.printStackTrace();
        }finally{
            // �ر���Դ
            try{
                if(stmt!=null) stmt.close();
            }catch(SQLException se2){
            }// ʲô������
            try{
                if(conn!=null) conn.close();
            }catch(SQLException se){
                se.printStackTrace();
            }
        }
    	
    }

}

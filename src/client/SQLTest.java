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
    public int login(String userid,String pass) {//id 1,2,3 password 123
    	Connection conn = null;
        Statement stmt = null;
        int flag=1;
        try{
            // ע�� JDBC ����
            Class.forName(JDBC_DRIVER);
            // ������
            System.out.println("�������ݿ�...");
            conn = DriverManager.getConnection(DB_URL,USER,PASS);
            System.out.println(" ʵ����Statement����...");
            stmt = conn.createStatement();
            String sql,sql2;
            sql="SELECT id FROM user";
            sql2="SELECT password FROM user where id ='"+userid+"'";
            ResultSet rs = stmt.executeQuery(sql);   
            while(rs.next()){
                // ͨ���ֶμ���
                String id  = rs.getString("id");
                if(id.equals(userid)) {
                	flag=0;
                	break;
                }
  
                // �������
                // System.out.print("ID: " + id);
            }
            ResultSet rs2 =stmt.executeQuery(sql2);
            if(flag==0) {
            	 while(rs2.next()){
            		 String password =rs2.getString("password");
                 	 if(!pass.equals(password)) {
                 		flag=1;
                 		break;
                 	}
            	 }
            	
            }
            // ��ɺ�ر�
            rs.close();
            rs2.close();
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
        System.out.println(flag);
    	return flag;
    }
    public static void  main(String agrs[]) {
    	String userid="1";
    	String pass="123";
    	Connection conn = null;
        Statement stmt = null;
        int flag=1;
        try{
            // ע�� JDBC ����
            Class.forName(JDBC_DRIVER);
            // ������
            System.out.println("�������ݿ�...");
            conn = DriverManager.getConnection(DB_URL,USER,PASS);
            System.out.println(" ʵ����Statement����...");
            stmt = conn.createStatement();
            String sql,sql2;
            sql="SELECT id FROM user";
            sql2="SELECT password FROM user where id ='"+userid+"'";
            ResultSet rs = stmt.executeQuery(sql);   
            while(rs.next()){
                // ͨ���ֶμ���
                String id  = rs.getString("id");
                if(id.equals(userid)) {
                	flag=0;
                	break;
                }
  
                // �������
                // System.out.print("ID: " + id);
            }
            ResultSet rs2 =stmt.executeQuery(sql2);
            if(flag==0) {
            	 while(rs2.next()){
            		 String password =rs2.getString("password");
                 	 if(!pass.equals(password)) {
                 		flag=1;
                 		break;
                 	}
            	 }
            	
            }
            // ��ɺ�ر�
            rs.close();
            rs2.close();
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
        System.out.println(flag);
    	
    }
    
   

}

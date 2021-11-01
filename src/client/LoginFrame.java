/**  

* @Title: LoginFrame.java  

* @Package client  

* @Description: TODO()

* @author Ocean  

* @date 2021年10月21日  

* @version V1.0  

*/  
package client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**  

* @ClassName: LoginFrame  

* @Description: TODO()  

* @author Ocean  

* @date 2021年10月21日  
  

*/
public class LoginFrame implements ActionListener{
	ClientFrame client;
	JFrame  frame=new JFrame("登录界面");
	JTextField fld1=new JTextField(16);//用户名
	JTextField fld2=new JTextField(16);//密码框
	JPanel jp=new JPanel();
	JLabel lb1=new JLabel(new ImageIcon(LoginFrame.class.getResource("/1.jpg")));//头图
	JLabel lb2=new JLabel(new ImageIcon(LoginFrame.class.getResource("/2.jpg")));//头像
	JCheckBox box1=new JCheckBox("记住密码");
	JButton jb=new JButton(new ImageIcon(LoginFrame.class.getResource("/3.jpg")));//登录
	JCheckBox box2=new JCheckBox("自动登录");
	JButton jb2=new JButton("注册账号");
	JButton jb3=new JButton("找回密码");
	public LoginFrame() {
	
	}
	public void Init() {	
		frame.setBounds(200,200,460,400);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		lb1.setBounds(20, 10, 100,100);
		frame.add(lb1,"North");
		jp.setLayout(null);
		lb2.setBounds(10, 10, 120, 120);
		jp.add(lb2);
		fld1.setBounds(150,10,180, 30);
		fld1.setText("");
		fld2.setText("");
		jp.add(fld1);
		jb2.setBounds(350, 10, 90, 30);
		jp.add(jb2);
		fld2.setBounds(150, 35,180, 30);
		jp.add(fld2);
		jb3.setBounds(350, 35, 90, 30);
		jp.add(jb3);
		box1.setBounds(150, 75, 80, 15);
		jp.add(box1);
		box2.setBounds(240, 75, 80, 15);
		jp.add(box2);
		jb.setBounds(150, 100, 200, 40);
		jp.add(jb);
		jb.addActionListener(this);
		frame.add(jp);
		frame.setVisible(true);
	}
	private int userVerify() {
		if(fld1.getText().equals("")) {
			return 1;
		}
		else {
			return 0;
		}
		
	}
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==jb){
			if(userVerify()==1) {
				JOptionPane.showMessageDialog(frame, "用户名不能为空");
				System.out.println(userVerify());
				return;
			}
			else {
				System.out.println(fld1.getText()+"log in");
				client=new ClientFrame();
				String user=fld1.getText();
				client.setUsername(user);
				if(client.ConnectSussess()) {
					this.frame.dispose();
				}
				
			}
			
			
		}
	}
	
	public static void  main(String agrs[]) {
		LoginFrame Login=new LoginFrame();
		Login.Init();
	}

}

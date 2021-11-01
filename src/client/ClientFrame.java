package client;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;


public class ClientFrame extends JFrame implements ActionListener,Runnable{
	static String ip="127.0.0.1";
	static int port=8080;
	public JTextField UserName=new JTextField(10); //用户名
	public JTextArea allMsg=new JTextArea(); //聊天信息显示
	public JTextField sendMsg=new JTextField(10); //发送消息消息
	JButton btnSend; //发送
	JButton btnCon; //连接
	JButton btnQuit; //退出
	JButton btnOpenFile;//发送文件
	PrintWriter pw;
	Socket socket;
	DefaultListModel<String> Model=new DefaultListModel<String>();
	JList<String> list=new JList<String>(Model);
	public ClientFrame (){
		super("客户端界面");
		this.go();
	}
	private void go() {
		this.setBounds(300,300,400,300);
		this.setSize(600, 400);	
		JPanel jp1=new JPanel(new FlowLayout());
		jp1.add(new JLabel("用户名称"));
		UserName.setText("");
		UserName.setEditable(false);
		jp1.add(UserName);
		btnCon=new JButton("等待连接");
		btnQuit=new JButton("退出");
		jp1.add(btnCon);
		btnCon.setEnabled(false);
		jp1.add(btnQuit);
		btnQuit.setEnabled(false);
		this.getContentPane().add(jp1,BorderLayout.NORTH);	
		JPanel jp2=new JPanel(new GridLayout());
		allMsg=new JTextArea();
		allMsg.setEditable(false);
		allMsg.setLineWrap(true);
		Model.addElement("全部");
		list.setSelectedIndex(0);	//设置默认选择位置
		list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);	//设置只能单选
		list.setVisibleRowCount(10);
		list.setSize(100, 200);
		JScrollPane scroll=new JScrollPane(list);		//为list添加滚动条
		scroll.setBorder(new TitledBorder("在线列表"));	
		jp2.add(scroll);
		jp2.add(new JScrollPane(allMsg));
		this.getContentPane().add(jp2,BorderLayout.CENTER);	
		JPanel jp3=new JPanel();
		jp3.add(new JLabel("消息"));
		jp3.add(sendMsg);
		btnSend=new JButton("发送");
		btnSend.setEnabled(false);
		jp3.add(btnSend);
		btnOpenFile=new JButton("发送文件");
		btnOpenFile.setEnabled(false);
		jp3.add(btnOpenFile);
		this.getContentPane().add(jp3,BorderLayout.SOUTH);	
		btnCon.addActionListener(this);
		btnQuit.addActionListener(this);
		btnSend.addActionListener(this);
		btnOpenFile.addActionListener(this);
		sendMsg.addActionListener(this);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setVisible(true);		
	}
	public boolean ConnectSussess() {
		if(UserName.getText().equals("")){
			JOptionPane.showMessageDialog(this, "用户名不能为空");
			return false;
		}
		System.out.println("用户"+UserName.getText()+"连接中");
		connect();
		btnCon.setText("已连接");
		return true;
	}
	public void actionPerformed(ActionEvent e) {
		if("等待连接".equals(e.getActionCommand())){
			//System.out.println(UserName.getText());
			ConnectSussess();
		}else if("退出".equals(e.getActionCommand())){
			if(UserName.getText()==null ){
				int result = JOptionPane.showConfirmDialog(this, "你还没登录,是否退出");
				if(result==JOptionPane.YES_OPTION){
					System.exit(0);
				}else{
					return;
				}
			}
			System.out.println(UserName.getText()+"退出");
			sendQuitMsg();
		}else if("发送".equals(e.getActionCommand())||sendMsg.equals(e.getSource())){
			if(sendMsg.getText()==null){
				JOptionPane.showMessageDialog(this, "发送消息不能为空");
				return;
			}
			String msg="send@"+list.getSelectedValue()+"@"+sendMsg.getText()+"@"+UserName.getText();
			pw.println(msg);
			//System.out.println(pw);
			sendMsg.setText("");
		}else if("发送文件".equals(e.getActionCommand())) {
			showFileOpenDialog();
		}

	}
	private void connect() {
		try {
			socket=new Socket(ip,port);
			btnCon.setEnabled(false);	//连接成功后关掉连接按钮
			btnQuit.setEnabled(true);//连接成功后打开退出按钮
			pw=new PrintWriter(socket.getOutputStream(),true);
			System.out.println("log"+"@"+UserName.getText());
			pw.println("log"+"@"+UserName.getText());
			this.setTitle("用户"+UserName.getText()+"在线");
			btnSend.setEnabled(true);		//打开发送按钮
			btnOpenFile.setEnabled(true); //打开文件按钮
			UserName.setEditable(false);		//用户名不能再修改
			
			//开一个线程单独用于跟服务器通信
			Thread receive=new Thread(this);
			receive.start();
			//开一个线程用于传输文件
			ClientFileThread fileThread = new ClientFileThread(UserName.getText(),this,pw);
			fileThread.start();

		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void run() {
		try {
			Scanner sc=new Scanner(socket.getInputStream());
			while(sc.hasNext()){
				String msg=sc.nextLine();
				String msgs[]=msg.split("@");		
				if(msgs==null){
					System.out.println("消息异常");
					}
				if("msg".equals(msgs[0])){
					//表示该信息是用来显示用的
					if("server".equals(msgs[1])){
						//表示该信息是系统信息
						msg="系统信息:"+msgs[2];
						allMsg.append(msg+"\r\n");
					}else{
						//表示该信息聊天信息
						msg=msgs[1]+msgs[2];
						allMsg.append(msg+"\r\n");
					}
				}else if("Add".equals(msgs[0])){
					//添加用户
					Model.addElement(msgs[2]);
				}else if("Del".equals(msgs[0])){
					//移除用户
					Model.removeElement(msgs[2]);
				}

				list.validate();
				}
		}catch (IOException e) {
				e.printStackTrace();
			}
	}
	private void sendQuitMsg() {
		try {
			socket=new Socket(ip, port);	
			String msg="exit@全部@null@"+UserName.getText();
			pw.println(msg);
			System.exit(0);
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void setUsername(String username) {
		this.UserName.setText(username);
	}
	private void showFileOpenDialog() {
		// 创建一个默认的文件选择器
		JFileChooser fileChooser = new JFileChooser();
		// 设置默认显示的文件夹
		fileChooser.setCurrentDirectory(new File("C:\\Users\\阿布\\Desktop"));
		// 添加可用的文件过滤器（FileNameExtensionFilter 的第一个参数是描述, 后面是需要过滤的文件扩展名）
//        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("(txt)", "txt"));
        // 设置默认使用的文件过滤器（FileNameExtensionFilter 的第一个参数是描述, 后面是需要过滤的文件扩展名 可变参数）
        fileChooser.setFileFilter(new FileNameExtensionFilter("(txt)", "txt"));
		// 打开文件选择框（线程将被堵塞，直到选择框被关闭）
		int result = fileChooser.showOpenDialog(this);  // 对话框将会尽量显示在靠近 parent 的中心
		// 点击确定
		if(result == JFileChooser.APPROVE_OPTION) {
			// 获取路径
			File file = fileChooser.getSelectedFile();
			String path = file.getAbsolutePath();
			ClientFileThread.outFileToServer(path);
			this.allMsg.append("您已成功发送文件"+file+"\r\n");
		}
	}
	
	public static void  main(String agrs[]) {
		ClientFrame c=new ClientFrame();
	}

}

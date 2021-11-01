package server;

import java.awt.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

import javax.swing.*;
import javax.swing.border.TitledBorder;


public class ServerFrame extends JFrame{
	ServerSocket server;
	JTextArea text=new JTextArea();
	DefaultListModel<String> Model=new DefaultListModel<String>();
	JList<String> list=new JList<String>(Model);
	Map<String, Socket> userMap=new HashMap<String, Socket>();
	public boolean isStart;
	public void setisstatrt(boolean st) {
		this.isStart=st;
	}
	public boolean getisStart() {
		return this.isStart;
	}
	public ServerFrame() {
		super("����������");
		this.go();
	}
	public void go() {
		this.setSize(650, 500);
		text.setEditable(false);
		text.setText("��������Ϣ:");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().add(new JScrollPane(text),BorderLayout.CENTER);		
		JScrollPane scroll=new JScrollPane(list);
		scroll.setBorder(new TitledBorder("�����б�"));
		getContentPane().add(scroll,BorderLayout.EAST);
		scroll.setBounds(10, 20, 50, 100);
		JPanel jp1=new JPanel();
		getContentPane().add(jp1,BorderLayout.NORTH);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setVisible(true);
	}
	
	public static void  main(String agrs[]) {
		ServerFrame s=new ServerFrame();
		try {
				ServerFileThread serverFileThread = new ServerFileThread(s);
				serverFileThread.start();
				ServerSocket msgServer=new ServerSocket(8080);
				s.server=msgServer;
				System.out.println("\r\n��Ϣ����������:"+s.server);
				s.text.append("\r\n��Ϣ����������:"+s.server);
				s.StartThreads(msgServer);    
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void StartThreads(ServerSocket msgServer) {	
		while(true) {
			Socket nowSocket;
			try {
				nowSocket = msgServer.accept();
				Thread transfermsg=new Transfer(nowSocket);
		    	transfermsg.start();	//��������һ���߳�������ͻ���ͨ��				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		
		}
	}
	
	
	
	class Transfer extends Thread{
		private Socket s;
		public Transfer(Socket s) {
			this.s=s;
		}
		public void run() {
			try {
				while (true) {
					//���ݽ��տͻ��˷�����Э���ж�,�ͻ��˽��е���ʲô��������
					Scanner sc=new Scanner(s.getInputStream());
					while(sc.hasNextLine()){
						String msg=sc.nextLine();
						String msgs[]=msg.split("@");
						if(msgs==null){
							System.out.println("ͨѶ�쳣");
							return;
						}	
						if("send".equals(msgs[0])){
							System.out.println(msgs);
							sendMsgTo(msgs);
							
						}else if("exit".equals(msgs[0])){
							text.append("\r\n"+msgs[3]+"������"+s);
							System.out.println("\r\n"+msgs[3]+"������"+s);
							Model.removeElement(msgs[3]);
							sendSbExitMsg(msgs);
							}
						else if("log".equals(msgs[0])) {
							text.append("\r\n"+msgs[1]+"�����ˡ�"+s);
							System.out.println("\r\n"+msgs[1]+"�����ˡ�"+s);
							Model.addElement(msgs[1]);
							userUpdate(msgs[1],s);
							userMap.put(msgs[1],s);
						}
					}
					sc.close();
				}
					
				
				
			} catch (IOException e) {
				e.printStackTrace();
				} 
		}
		
	}
	public void sendMsgTo(String[] msgs) throws IOException {
		if("ȫ��".equals(msgs[1])){
			Iterator<String> iterator = userMap.keySet().iterator();
			while(iterator.hasNext()){
				String userName=iterator.next();
				String msg=null;
				if(userName.equals(msgs[3])){
					msg="msg@"+"��"+"@˵:"+msgs[2];
				}
				else{
					msg="msg@"+msgs[3]+"@˵:"+msgs[2];
				}
				Socket s=userMap.get(userName);
				PrintWriter pw=new PrintWriter(s.getOutputStream(),true);
				pw.println(msg);
				}
		}else{
			//���͸�ĳһ����
			String userName=msgs[1];
			Socket s=userMap.get(userName);
			String msg="msg@"+msgs[3]+"@����һ����˵:"+msgs[2];
			PrintWriter pw=new PrintWriter(s.getOutputStream(), true);
			pw.println(msg);
			Socket s2 = userMap.get(msgs[3]);
			PrintWriter pw2 = new PrintWriter(s2.getOutputStream(), true);
			String str2 = "msg@"+"��"+"@�� "+userName+"˵:"+msgs[2];
			pw2.println(str2);
		}
	}
	public void sendSbExitMsg(String[] msgs) throws IOException {
		Iterator<String> iterator=userMap.keySet().iterator();
		while(iterator.hasNext()){
			String userName=iterator.next();
			Socket s=userMap.get(userName);
			PrintWriter pw=new PrintWriter(s.getOutputStream(), true);
			String msg="msg@server@�û�["+msgs[3]+"]�˳���";
			pw.println(msg);
			msg="Del@server@"+msgs[3];
			pw.println(msg);
		}
	}
	public void userUpdate(String userName,Socket s)throws IOException{
		String msg;
		Iterator<Socket> it1 = userMap.values().iterator();
		while(it1.hasNext()){
			Socket s1=it1.next();
			PrintWriter pw1=new PrintWriter(s1.getOutputStream(),true);
			msg="msg@server@"+userName+"��¼��";	//�ͻ�����ʾ.
			pw1.println(msg);
			msg="Add@server@"+userName;	//�ͻ���ά�������û��б���
			pw1.println(msg);
	
		}
		Iterator<String> it2 = userMap.keySet().iterator();
		PrintWriter pw2= new PrintWriter(s.getOutputStream(),true);
		while(it2.hasNext()){
			String user=it2.next();
			System.out.println("map:"+userMap);	
			msg="Add@server@"+user;
			pw2.println(msg);
		}
	
	}

}


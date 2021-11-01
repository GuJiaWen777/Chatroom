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
	public JTextField UserName=new JTextField(10); //�û���
	public JTextArea allMsg=new JTextArea(); //������Ϣ��ʾ
	public JTextField sendMsg=new JTextField(10); //������Ϣ��Ϣ
	JButton btnSend; //����
	JButton btnCon; //����
	JButton btnQuit; //�˳�
	JButton btnOpenFile;//�����ļ�
	PrintWriter pw;
	Socket socket;
	DefaultListModel<String> Model=new DefaultListModel<String>();
	JList<String> list=new JList<String>(Model);
	public ClientFrame (){
		super("�ͻ��˽���");
		this.go();
	}
	private void go() {
		this.setBounds(300,300,400,300);
		this.setSize(600, 400);	
		JPanel jp1=new JPanel(new FlowLayout());
		jp1.add(new JLabel("�û�����"));
		UserName.setText("");
		UserName.setEditable(false);
		jp1.add(UserName);
		btnCon=new JButton("�ȴ�����");
		btnQuit=new JButton("�˳�");
		jp1.add(btnCon);
		btnCon.setEnabled(false);
		jp1.add(btnQuit);
		btnQuit.setEnabled(false);
		this.getContentPane().add(jp1,BorderLayout.NORTH);	
		JPanel jp2=new JPanel(new GridLayout());
		allMsg=new JTextArea();
		allMsg.setEditable(false);
		allMsg.setLineWrap(true);
		Model.addElement("ȫ��");
		list.setSelectedIndex(0);	//����Ĭ��ѡ��λ��
		list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);	//����ֻ�ܵ�ѡ
		list.setVisibleRowCount(10);
		list.setSize(100, 200);
		JScrollPane scroll=new JScrollPane(list);		//Ϊlist��ӹ�����
		scroll.setBorder(new TitledBorder("�����б�"));	
		jp2.add(scroll);
		jp2.add(new JScrollPane(allMsg));
		this.getContentPane().add(jp2,BorderLayout.CENTER);	
		JPanel jp3=new JPanel();
		jp3.add(new JLabel("��Ϣ"));
		jp3.add(sendMsg);
		btnSend=new JButton("����");
		btnSend.setEnabled(false);
		jp3.add(btnSend);
		btnOpenFile=new JButton("�����ļ�");
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
			JOptionPane.showMessageDialog(this, "�û�������Ϊ��");
			return false;
		}
		System.out.println("�û�"+UserName.getText()+"������");
		connect();
		btnCon.setText("������");
		return true;
	}
	public void actionPerformed(ActionEvent e) {
		if("�ȴ�����".equals(e.getActionCommand())){
			//System.out.println(UserName.getText());
			ConnectSussess();
		}else if("�˳�".equals(e.getActionCommand())){
			if(UserName.getText()==null ){
				int result = JOptionPane.showConfirmDialog(this, "�㻹û��¼,�Ƿ��˳�");
				if(result==JOptionPane.YES_OPTION){
					System.exit(0);
				}else{
					return;
				}
			}
			System.out.println(UserName.getText()+"�˳�");
			sendQuitMsg();
		}else if("����".equals(e.getActionCommand())||sendMsg.equals(e.getSource())){
			if(sendMsg.getText()==null){
				JOptionPane.showMessageDialog(this, "������Ϣ����Ϊ��");
				return;
			}
			String msg="send@"+list.getSelectedValue()+"@"+sendMsg.getText()+"@"+UserName.getText();
			pw.println(msg);
			//System.out.println(pw);
			sendMsg.setText("");
		}else if("�����ļ�".equals(e.getActionCommand())) {
			showFileOpenDialog();
		}

	}
	private void connect() {
		try {
			socket=new Socket(ip,port);
			btnCon.setEnabled(false);	//���ӳɹ���ص����Ӱ�ť
			btnQuit.setEnabled(true);//���ӳɹ�����˳���ť
			pw=new PrintWriter(socket.getOutputStream(),true);
			System.out.println("log"+"@"+UserName.getText());
			pw.println("log"+"@"+UserName.getText());
			this.setTitle("�û�"+UserName.getText()+"����");
			btnSend.setEnabled(true);		//�򿪷��Ͱ�ť
			btnOpenFile.setEnabled(true); //���ļ���ť
			UserName.setEditable(false);		//�û����������޸�
			
			//��һ���̵߳������ڸ�������ͨ��
			Thread receive=new Thread(this);
			receive.start();
			//��һ���߳����ڴ����ļ�
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
					System.out.println("��Ϣ�쳣");
					}
				if("msg".equals(msgs[0])){
					//��ʾ����Ϣ��������ʾ�õ�
					if("server".equals(msgs[1])){
						//��ʾ����Ϣ��ϵͳ��Ϣ
						msg="ϵͳ��Ϣ:"+msgs[2];
						allMsg.append(msg+"\r\n");
					}else{
						//��ʾ����Ϣ������Ϣ
						msg=msgs[1]+msgs[2];
						allMsg.append(msg+"\r\n");
					}
				}else if("Add".equals(msgs[0])){
					//����û�
					Model.addElement(msgs[2]);
				}else if("Del".equals(msgs[0])){
					//�Ƴ��û�
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
			String msg="exit@ȫ��@null@"+UserName.getText();
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
		// ����һ��Ĭ�ϵ��ļ�ѡ����
		JFileChooser fileChooser = new JFileChooser();
		// ����Ĭ����ʾ���ļ���
		fileChooser.setCurrentDirectory(new File("C:\\Users\\����\\Desktop"));
		// ��ӿ��õ��ļ���������FileNameExtensionFilter �ĵ�һ������������, ��������Ҫ���˵��ļ���չ����
//        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("(txt)", "txt"));
        // ����Ĭ��ʹ�õ��ļ���������FileNameExtensionFilter �ĵ�һ������������, ��������Ҫ���˵��ļ���չ�� �ɱ������
        fileChooser.setFileFilter(new FileNameExtensionFilter("(txt)", "txt"));
		// ���ļ�ѡ����߳̽���������ֱ��ѡ��򱻹رգ�
		int result = fileChooser.showOpenDialog(this);  // �Ի��򽫻ᾡ����ʾ�ڿ��� parent ������
		// ���ȷ��
		if(result == JFileChooser.APPROVE_OPTION) {
			// ��ȡ·��
			File file = fileChooser.getSelectedFile();
			String path = file.getAbsolutePath();
			ClientFileThread.outFileToServer(path);
			this.allMsg.append("���ѳɹ������ļ�"+file+"\r\n");
		}
	}
	
	public static void  main(String agrs[]) {
		ClientFrame c=new ClientFrame();
	}

}

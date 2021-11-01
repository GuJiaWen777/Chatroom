/**  

* @Title: ServerFileThread.java  

* @Package server  

* @Description: TODO()

* @author Ocean  

* @date 2021��10��21��  

* @version V1.0  

*/  
package server;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ServerFileThread extends Thread{
	ServerSocket server = null;
	Socket socket = null;
	ServerFrame SF;
	static List<Socket> list = new ArrayList<Socket>();  // �洢�ͻ���

	public ServerFileThread (ServerFrame s) {
		this.SF=s;
	}
	public void run() {
		try {
			server = new ServerSocket(8090);
			System.out.println("\r\n�ļ�����������:"+server);
			SF.text.append("\r\n�ļ�����������:"+server);
			while(true) {
				socket = server.accept();
				list.add(socket);
				// �����ļ������߳�
				FileReadAndWrite fileReadAndWrite = new FileReadAndWrite(socket);
				fileReadAndWrite.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
class FileReadAndWrite extends Thread {
	private Socket nowSocket = null;
	private DataInputStream input = null;
	private DataOutputStream output = null;
	
	public FileReadAndWrite(Socket socket) {
		this.nowSocket = socket;
	}
	public void run() {
		try {
			input = new DataInputStream(nowSocket.getInputStream());  // ������
			while (true) {
				// ��ȡ�ļ����ֺ��ļ�����
				String textName = input.readUTF();
				long textLength = input.readLong();
				// �����ļ����ֺ��ļ����ȸ����пͻ���
				for(Socket socket: ServerFileThread.list) {
					output = new DataOutputStream(socket.getOutputStream());  // �����
					if(socket != nowSocket) {  // ���͸������ͻ���
					
						output.writeUTF(textName);
						output.flush();
						output.writeLong(textLength);
						output.flush();
					}
				}
				// �����ļ�����
				int length = -1;
				long curLength = 0;
				byte[] buff = new byte[1024];
				while ((length = input.read(buff)) > 0) {
					curLength += length;
					for(Socket socket: ServerFileThread.list) {
						output = new DataOutputStream(socket.getOutputStream());  // �����
						if(socket != nowSocket) {  // ���͸������ͻ���
							output.write(buff, 0, length);
							output.flush();
						}
					}
					if(curLength == textLength) {  // ǿ���˳�
						break;
					}
				}
			}
		} catch (Exception e) {
			ServerFileThread.list.remove(nowSocket);  // �̹߳رգ��Ƴ���Ӧ�׽���
		}
	}
}

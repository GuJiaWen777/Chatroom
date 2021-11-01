/**  

* @Title: ClientFileThread.java  

* @Package client  

* @Description: TODO()

* @author Ocean  

* @date 2021��10��21��  

* @version V1.0  

*/  
package client;

import java.io.*;
import java.net.*;
import javax.swing.*;

public class ClientFileThread extends Thread{
	private Socket socket = null;
	private ClientFrame Client = null;
	static String userName = null;
	static PrintWriter out = null;  // ��ͨ��Ϣ�ķ��ͣ�Server.java������ֵ��
	static DataInputStream fileIn = null;
	static DataOutputStream fileOut = null;
	static DataInputStream fileReader = null;
	static DataOutputStream fileWriter = null;
	static String ip="127.0.0.1";
	static int port=8090;
	int result;
	public ClientFileThread(String UserName, ClientFrame ClientFrame, PrintWriter out) {
		ClientFileThread.userName = UserName;
		this.Client = ClientFrame;
		ClientFileThread.out = out;
		}

	public void run() {
		try {
			socket=new Socket(ip,port);
			fileIn = new DataInputStream(socket.getInputStream());  // ������
			fileOut = new DataOutputStream(socket.getOutputStream());  // �����
			// �����ļ�
			while(true) {
				String textName = fileIn.readUTF();
				long totleLength = fileIn.readLong();
				result = JOptionPane.showConfirmDialog(Client, "�Ƿ�����ļ���", "��ʾ",
						   JOptionPane.OK_CANCEL_OPTION);
				int length = -1;
				byte[] buff = new byte[1024];
				long curLength = 0;
				if(result == JOptionPane.YES_OPTION){	
					File userFile = new File("D:\\eclipse-java\\workspace\\ChatRoom_1\\src\\"+userName);
					if(!userFile.exists()) {  // �½���ǰ�û����ļ���
						userFile.mkdir();
					}
					File file = new File("D:\\eclipse-java\\workspace\\ChatRoom_1\\src\\"+userName+"\\"+textName);
					fileWriter = new DataOutputStream(new FileOutputStream(file));
					while((length = fileIn.read(buff)) > 0) {  // ���ļ�д������
						fileWriter.write(buff, 0, length);
						fileWriter.flush();
						curLength += length;
						if(curLength == totleLength) {  // ǿ�ƽ���
							break;
						}
					}
					
					this.Client.allMsg.append(userName+"�����ļ�!"+textName+"�ɹ���\r\n");
					out.flush();
					// ��ʾ�ļ���ŵ�ַ
					JOptionPane.showMessageDialog(Client, "�ļ���ŵ�ַ��\n" +
							"D:\\eclipse-java\\workspace\\ChatRoom_1\\src\\"+userName+"\\"+textName,"��ʾ",JOptionPane.INFORMATION_MESSAGE);
				}
				else if(result == JOptionPane.CANCEL_OPTION) {
					while((length = fileIn.read(buff)) > 0) {
						curLength += length;
						if(curLength == totleLength) {  // ǿ�ƽ���
							break;
						}
					}
					this.Client.allMsg.append(userName+"�������ļ�"+textName+"\r\n");
					out.flush();
					// �������ļ�
				}
			
			
				fileWriter.close();
			}
			}catch (Exception e) {}
			
		}
	
	static void outFileToServer(String path) {
		try {
			File file = new File(path);
			fileReader = new DataInputStream(new FileInputStream(file));
			fileOut.writeUTF(file.getName());  // �����ļ�����
			fileOut.flush();
			fileOut.writeLong(file.length());  // �����ļ�����
			fileOut.flush();
			int length = -1;
			byte[] buff = new byte[1024];
			while ((length = fileReader.read(buff)) > 0) {  // ��������
				fileOut.write(buff, 0, length);
				fileOut.flush();
			}
		} catch (Exception e) {}
	}

	
}

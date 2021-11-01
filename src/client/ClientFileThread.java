/**  

* @Title: ClientFileThread.java  

* @Package client  

* @Description: TODO()

* @author Ocean  

* @date 2021年10月21日  

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
	static PrintWriter out = null;  // 普通消息的发送（Server.java传来的值）
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
			fileIn = new DataInputStream(socket.getInputStream());  // 输入流
			fileOut = new DataOutputStream(socket.getOutputStream());  // 输出流
			// 接收文件
			while(true) {
				String textName = fileIn.readUTF();
				long totleLength = fileIn.readLong();
				result = JOptionPane.showConfirmDialog(Client, "是否接收文件？", "提示",
						   JOptionPane.OK_CANCEL_OPTION);
				int length = -1;
				byte[] buff = new byte[1024];
				long curLength = 0;
				if(result == JOptionPane.YES_OPTION){	
					File userFile = new File("D:\\eclipse-java\\workspace\\ChatRoom_1\\src\\"+userName);
					if(!userFile.exists()) {  // 新建当前用户的文件夹
						userFile.mkdir();
					}
					File file = new File("D:\\eclipse-java\\workspace\\ChatRoom_1\\src\\"+userName+"\\"+textName);
					fileWriter = new DataOutputStream(new FileOutputStream(file));
					while((length = fileIn.read(buff)) > 0) {  // 把文件写进本地
						fileWriter.write(buff, 0, length);
						fileWriter.flush();
						curLength += length;
						if(curLength == totleLength) {  // 强制结束
							break;
						}
					}
					
					this.Client.allMsg.append(userName+"接收文件!"+textName+"成功！\r\n");
					out.flush();
					// 提示文件存放地址
					JOptionPane.showMessageDialog(Client, "文件存放地址：\n" +
							"D:\\eclipse-java\\workspace\\ChatRoom_1\\src\\"+userName+"\\"+textName,"提示",JOptionPane.INFORMATION_MESSAGE);
				}
				else if(result == JOptionPane.CANCEL_OPTION) {
					while((length = fileIn.read(buff)) > 0) {
						curLength += length;
						if(curLength == totleLength) {  // 强制结束
							break;
						}
					}
					this.Client.allMsg.append(userName+"不接收文件"+textName+"\r\n");
					out.flush();
					// 不接受文件
				}
			
			
				fileWriter.close();
			}
			}catch (Exception e) {}
			
		}
	
	static void outFileToServer(String path) {
		try {
			File file = new File(path);
			fileReader = new DataInputStream(new FileInputStream(file));
			fileOut.writeUTF(file.getName());  // 发送文件名字
			fileOut.flush();
			fileOut.writeLong(file.length());  // 发送文件长度
			fileOut.flush();
			int length = -1;
			byte[] buff = new byte[1024];
			while ((length = fileReader.read(buff)) > 0) {  // 发送内容
				fileOut.write(buff, 0, length);
				fileOut.flush();
			}
		} catch (Exception e) {}
	}

	
}

package com.example.demo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

/**
 * 中央服务器 保存每个peer的文件信息
 * 
 * @author michael/yk
 * 
 */
public class Center {


	//服务器的UDP嵌套字，监听peer
	private static DatagramSocket mSocket;
	//服务器端口
	private static final int PORT = 9090;
	private static InetAddress sendAddr;// 发送方地址
	private static int sendPort;// 发送方端口
	private final int bufSize = 65536;

	private ArrayList<FileInfo> fIList = new ArrayList<FileInfo>();// 文件信息链表

	/**
	 * 	文件信息保存的位置
	 */
	private String fileInfoPath = "src/main/file.in";//文件信息路径
	private FileWriter fileWriter;

	/**
	 * 文件信息类
	 * 
	 * @author michael
	 * 
	 */
	class FileInfo {
		String host;
		int port;
		String url;

		public FileInfo(String host, int port, String url) {
			this.host = host;
			this.port = port;
			this.url = url;
		}
	}


	/**
	 * 获得文件列表
	 * @return
	 */
	public String getFileList() {
		String filist="";
		for(int i=0;i<fIList.size();i++)
		filist+="host:"+this.fIList.get(i).host+" addr:"+this.fIList.get(i).url+" port:"+this.fIList.get(i).port+"\n";
		return filist;
	}

	public Center() {
		System.out.println("=========中央服务器启动==========");
		try {
			//读入已存在的文件信息
			File file = new File(fileInfoPath);
			//解决file.in为空时的报错
			boolean containsOnlySpaces = Files.lines(Path.of(fileInfoPath))
					.allMatch(line -> line.trim().isEmpty());
			if (!file.exists()) {
				file.createNewFile();
			}else if(file.length()!=0||containsOnlySpaces!=true){
				//会把路径的信息保存在FIlist的结构中
				readFileInfo(file);
			}
			else ;
			//这个是Center的私有成员变量fileWriter，可以在其他地方用
			fileWriter = new FileWriter(file, true);//append的方式打开
			//PORT是9090是服务器的端口,binds it to the specified port on the local host machine.
			//使用该类可以创建一个UDP协议的网络连接，并通过该连接发送和接收数据报文
			mSocket = new DatagramSocket(PORT);
			//这个线程是干嘛的？
			//这个线程是监听peer发送的数据包，并从数据报中提取ip和add和url保存到本地
//			ServerThread thread = new ServerThread(mSocket);
//			thread.start();
		} catch (SocketException e) {
			// TODO: handle exception
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void receiveThread(){
		ServerThread thread = new ServerThread(mSocket);
		thread.start();
	}
	/**
	 * 读取file.in文件信息
	 * @param file
	 */
	private void readFileInfo(File file){
		try {
			BufferedReader bufReader = new BufferedReader(new FileReader(file));
			String line;
			int index1,index2;
			String host,path;
			int port;
			while((line=bufReader.readLine()) != null) {
				index1 = line.indexOf(" ");
				index2 = line.indexOf(" ", index1 + 1);
				host = line.substring(0, index1);
				port = Integer.valueOf(line.substring(index1 + 1, index2));
				path = line.substring(index2 + 1);
				fIList.add(new FileInfo(host, port, path));
			}
			bufReader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 解析接收到的消息
	 * 用fileWriter保存在file.in文件中
	 * 并保存在服务器列表fIlist中
	 * @param iBuf
	 * @param iLen
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public void dataReceived(byte[] iBuf, int iLen) {
		DatagramPacket sPacket;
		try {
			String msg = new String(iBuf).trim();
			// 解析信息类型
			if (msg.startsWith("Filelist")) {// peer发来文件列表
				String peerName = getNameByMsg(msg);
				System.out.println(peerName + "发来文件信息");
				
				msg = msg.substring(msg.indexOf(":") + 1);
				System.out.println(msg);

//					int index = msg.indexOf(separator);
				String path =msg;
				fIList.add(new FileInfo(sendAddr.getHostAddress(), sendPort, path));
				//将文件信息写入磁盘
				fileWriter.write(sendAddr.getHostAddress() + " " + sendPort + " " + path + "\n");
				System.out.println(path);
//					msg = msg.substring(index + 3);
				fileWriter.flush();
			} else {// peer请求文件信息
				String res = "没有文件";
				byte[] bSend;
				if (fIList.isEmpty()) {
					bSend = res.getBytes();
					sPacket = new DatagramPacket(bSend, bSend.length, sendAddr,
							sendPort);
					mSocket.send(sPacket);
					return;
				}
				res = "";
				for (int i = 0; i < fIList.size(); i++) {
					res += (i + 1) + ".<" + fIList.get(i).url + ","
							+ fIList.get(i).host + "," + fIList.get(i).port
							+ ">\n";
				}
				bSend = res.getBytes();
				sPacket = new DatagramPacket(bSend, bSend.length, sendAddr,
						sendPort);
				mSocket.send(sPacket);
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	/**
	 * 从消息中读取Peer的名字
	 * @return
	 */
	private String getNameByMsg(String msg) {
		int index = msg.indexOf("From");
		msg = msg.substring(index + 1);
		index = msg.indexOf(" ");
		int index2 = msg.indexOf(":", index + 1);
		return msg.substring(index+1, index2);
	}

	private class ServerThread extends Thread {

		private DatagramSocket mSocket;

		public ServerThread(DatagramSocket socket) {
			mSocket = socket;
		}

		@Override
		public void run() {
			//接收数据缓冲区buf
			byte buf[] = new byte[bufSize];
			DatagramPacket rPacket = new DatagramPacket(buf, bufSize);
			try {
				while(true) {
//					for (int i = 0; i < buf.length; i++) {
//						buf[i] = 0;
//					}
					mSocket.receive(rPacket);
					//根据接收到的数据包获取发送方的地址和端口
					sendAddr = rPacket.getAddress();
					sendPort = rPacket.getPort();
					System.out.println(sendAddr);
					System.out.println(sendPort);
					//此时数据保存在buf中，把接收到的信息通过dataReceived保存在服务器
					System.out.println( new String(buf).trim());
					dataReceived(buf, buf.length);
					System.out.println(fIList);
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
	}

}

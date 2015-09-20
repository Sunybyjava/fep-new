package com.chooshine.fep.newtcpchannel;

import com.chooshine.fep.ConstAndTypeDefine.ConvertUtil;
import com.chooshine.fep.communicate.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

public class TCPControl {
	private Selector selector;

	private ServerSocketChannel serverSocket;

	private String LocalIp;

//	private int listenPort;

	private TerminalChannelSet termChannelPool;

	private int ListMaxCount;

	private int ChannelSetCheckInterval;

	private int UnNamedChannelTimeOut;

	private static int NamedChannelTimeOut;
	
	private static String CommService_Ip = ""; // 数据提交的前置机IP

	private static int CommService_Port = 0; // 数据提交的前置机端口

	private static int ListenPort = 0; // 总共TCP端口的数量

	public static void main(String[] args) {
		//从配置文件获取通道配置信息
		InputStream filecon = null;
	    try {
	      String file_name =
	          "./NewTCPChannel.config";
	      Properties prop = new Properties();
	      filecon = new FileInputStream(file_name); //读取配置文件中的内容
	      prop.load(filecon);
	      CommService_Ip = (String) prop.getProperty("CommService_Ip",
	                                                 InetAddress.getLocalHost().
	                                                 getHostAddress()); //通讯服务所在的Ip，默认为本机IP
	      CommService_Port = Integer.parseInt( (String) prop.getProperty(
	          "CommService_Port", "5000")); //通讯服务监听前置机的端口，默认为5000
	      ListenPort = Integer.parseInt( (String) prop.getProperty("ListenPort",
	          "6000")); //TCP端口数量，默认为1
	      NamedChannelTimeOut = Integer.parseInt( (String) prop.getProperty(
	          "Connection_TimeOut", "15")); //连接超时时间，默认为15分钟
	    } catch (Exception fe) { //配置文件没有找到，需要填入默认的信息
	          try {
	            CommService_Ip = InetAddress.getLocalHost().getHostAddress();
	          }
	          catch (Exception e) {
	          }
	          CommService_Port = 5000;
	          ListenPort = 6000;
	          NamedChannelTimeOut = 15;
	      }
		
		TCPControl tcp = new TCPControl();
		tcp.ChannelInit(ListenPort);
	}

	private String CalcFrameLength(String FrameContent) {
		int iLen = (FrameContent.length() - 4) / 2;
		iLen = (iLen << 2) | 1;
		String sDataLen = Integer.toHexString(iLen);
		if (sDataLen.length() > 4) {
			sDataLen = sDataLen.substring(0, 4);
		}
		sDataLen = "0000".substring(0, 4 - sDataLen.length()) + sDataLen;
		sDataLen = sDataLen.substring(2, 4) + sDataLen.substring(0, 2);
		sDataLen = sDataLen.toUpperCase();// + sDataLen.toUpperCase();
		return sDataLen;
	}

	public void ChannelInit(int listenPort) {
		ListMaxCount = 5000;
		ChannelSetCheckInterval = 1;
		UnNamedChannelTimeOut = 1000;
		try {
			LocalIp = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e1) {
			LocalIp = "127.0.0.1";
			e1.printStackTrace();
		}

//		listenPort = 6001;
		termChannelPool = new TerminalChannelSet();
		try {
			serverSocket = ServerSocketChannel.open();
			selector = Selector.open();
			serverSocket.socket().bind(new InetSocketAddress(listenPort), 10);
			serverSocket.configureBlocking(false);
			serverSocket.register(selector, SelectionKey.OP_ACCEPT);
		} catch (IOException e) {
			e.printStackTrace();
		}
		utils.PrintDebugMessage("Listening on port "+listenPort, "D");
		ListenThread lt = new ListenThread();
		lt.start();
		SendDataThread sdt = new SendDataThread();
		sdt.start();
		ChannelSetCheckerThread sct = new ChannelSetCheckerThread();
		sct.start();
		CommunicateWithCommService cc =new CommunicateWithCommService(CommService_Ip,CommService_Port,"");
		cc.start();
	}

	private String getLogicAddrFromFrameBody(HXByteBuffer bb) {
		byte[] byteLogicAddr = bb.subbyte(1, 4);
		byte temp;
		temp = byteLogicAddr[0];
		byteLogicAddr[0] = byteLogicAddr[1];
		byteLogicAddr[1] = temp;
		temp = byteLogicAddr[2];
		byteLogicAddr[2] = byteLogicAddr[3];
		byteLogicAddr[3] = temp;
		return ConvertUtil.bcd2AscStr(byteLogicAddr);
	}

	private void checkDupTerm(String logicAddr) {
		Map nc = (Map) termChannelPool.getNamedChannelSet();
		if (nc.containsKey(logicAddr)) {
			// 此终端已经连接
			SelectionKey k = (SelectionKey) nc.get(logicAddr);
			try {
				// 关闭
				k.cancel();
				((SocketChannel) (k.channel())).socket().shutdownInput();
				((SocketChannel) (k.channel())).socket().shutdownOutput();
				((SocketChannel) (k.channel())).socket().close();
				k.channel().close();
			} catch (IOException e) {
				TCPChannelConstants.log.WriteLog("checkDupTerm: close channel (" + logicAddr + ") failed.");
			}
			nc.remove(logicAddr);
			TCPChannelConstants.log.WriteLog("Duplicate term[" + logicAddr + "] is closed.");
		}
	}

	private void dealArrivedData(byte[] data, SelectionKey key) {
		SocketChannel channel = (SocketChannel) key.channel();
		IHDTermReadImpl readImpl = (IHDTermReadImpl) key.attachment();
		readImpl.setRecentCommTime(System.currentTimeMillis());
		HXByteBuffer bb = new HXByteBuffer(data);
		String strLogicAddr = getLogicAddrFromFrameBody(bb);
		String ip = channel.socket().getInetAddress().getHostAddress();
		int port = channel.socket().getPort();

		// boolean mixFlag = false;
		// 关联当前链路和终端
		if (readImpl.getTermLogicAddr() == null) {
			// 还未绑定过
			checkDupTerm(strLogicAddr);
			readImpl.setTermLogicAddr(strLogicAddr);
			termChannelPool.addNamedChannel(strLogicAddr, key);
			termChannelPool.deleteUnNamedChannel(ip + ":" + port);
		} else {
			//相同物理地址、不同终端逻辑地址
			if (strLogicAddr.equals(readImpl.getTermLogicAddr()) == false) {
				TCPChannelConstants.log.WriteLog("Term[" + strLogicAddr + "] send data to channel["	+ readImpl.getTermLogicAddr() + "]");
				// mixFlag = true;
				termChannelPool.addNamedChannel(strLogicAddr, key);
			}
		}
		try {
			String sFrameBody = ConvertUtil.bcd2AscStr(data);
			String sFullFrame = "68" + CalcFrameLength(sFrameBody) + "68" + sFrameBody;
			TCPChannelConstants.trc.TraceLog("Term[" + strLogicAddr + "] recv data:" + sFullFrame);
//			log.WriteLog("Term[" + strLogicAddr + "] recv data:" + ConvertUtil.bcd2AscStr(data));
			//判断是否是心跳、登录报文，如果是的话，需要返回确认
			LinkTestReturn(sFullFrame, strLogicAddr, ip, port);
			AddReceiveDataList(sFullFrame, strLogicAddr, ip, port);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void AddReceiveDataList(String FullFrame, String strLogicAddr, String ip, int port) {
		UpDataStruct uds = new UpDataStruct();
		uds.LocalAddr = LocalIp + ":" + ListenPort;
		uds.SourceAddr = ip + ":" + port;
		uds.TerminalAddr = strLogicAddr;
		uds.FrameContent = FullFrame;
		uds.ChannelType = 50;
		TCPChannelConstants.GlobalReceiveList.add(uds);
		if (TCPChannelConstants.GlobalReceiveList.size() > ListMaxCount) {
			TCPChannelConstants.GlobalReceiveList.remove(0);
		}
	}

	private void LinkTestReturn(String FullFrame, String strLogicAddr, String ip, int port) {
		String sGNM = FullFrame.substring(20, 22);
		String sDT = FullFrame.substring(24, 32);
		if (sGNM.equals("02")) {
			String sLinkTestReturn = "684A006800" + FullFrame.substring(10, 20) + "006"
					+ FullFrame.substring(23, 24) + "0000040002" + sDT + "00";
			String sBuf = sLinkTestReturn.substring(8);
			sLinkTestReturn = sLinkTestReturn.toUpperCase()	+ calcCheckSum(sBuf) + "16";
			TCPChannelConstants.trc.TraceLog("LinkTestReturn:" + sLinkTestReturn);
			DownDataStruct ds = new DownDataStruct();
			ds.FrameContent = sLinkTestReturn;
			ds.TerminalAddr = strLogicAddr;
			ds.DestAddr = ip + ":" + port;
			ds.LocalAddr = LocalIp + ":" + ListenPort;
			TCPChannelConstants.GlobalSendList.add(ds);
			if (TCPChannelConstants.GlobalSendList.size() > ListMaxCount) {
				TCPChannelConstants.GlobalSendList.remove(0);
			}
		}
	}

	private String calcCheckSum(String data) {
		int iChecked = 0;
		for (int i = 0; i < data.length() / 2; i++) { // 通过内容计算新的校验码
			iChecked += Integer.parseInt(data.substring(2 * i, 2 * i + 2), 16);
		}
		int iJYM = iChecked % 256;
		String sJYM = Integer.toHexString(iJYM);
		if (sJYM.length() > 2) {
			sJYM = sJYM.substring(0, 2);
		}
		sJYM = "00".substring(0, 2 - sJYM.length()) + sJYM;
		return sJYM.toUpperCase();
	}
	
	private void dealTermException(SelectionKey key,IHDTermReadImpl readImpl) throws IOException{
		SocketChannel  channel = (SocketChannel) key.channel();
		if( !channel.socket().isInputShutdown() ) {
			channel.socket().shutdownInput();
		}
		if( !channel.socket().isOutputShutdown() ) {
			channel.socket().shutdownOutput();
		}

		channel.socket().close();
		channel.close();
		if (readImpl.getTermLogicAddr() != null) {
			termChannelPool.deleteNamedChannel(readImpl.getTermLogicAddr().trim());
			TCPChannelConstants.trc.TraceLog("Link:" + readImpl.getTermLogicAddr() + " disconnect!");
		}else{
			String ip = ((SocketChannel) key.channel()).socket().getInetAddress().getHostAddress();
			int port = ((SocketChannel) key.channel()).socket().getPort();
			termChannelPool.deleteUnNamedChannel(ip + ":" + port);
			TCPChannelConstants.trc.TraceLog("Link:" + ip + "," + port + " disconnect!");
		}
	}

	private void doRead(SelectionKey key) throws IOException {
		IHDTermReadImpl readImpl = (IHDTermReadImpl) key.attachment();
		if (readImpl == null) {
			key.cancel();
			((SocketChannel) key.channel()).socket().shutdownInput();
			((SocketChannel) key.channel()).socket().shutdownOutput();
			key.channel().close();
			return;
		}
		// 接收数据
		try {
			byte[] data;
			// 接收数据
			data = readImpl.read();
			if (data != null) {
				dealArrivedData(data, key);
			} else {
				TCPChannelConstants.log.WriteLog("接收数据为空或者内容非法");
			}
		} catch (Exception e) {
			if (e.getMessage().equals("recvLen<0")){
				dealTermException(key, readImpl);
			}
		}

	}

	private void doRun() throws IOException {
		int status = selector.select();
		if (status == 0) {
		}

		Iterator iter = selector.selectedKeys().iterator();
		while (iter.hasNext()) {
			SelectionKey key = (SelectionKey) iter.next();
			iter.remove();
			handleKey(key);
		}
	}

	protected void setSocketOptions(Socket socket) throws SocketException {
		socket.setTcpNoDelay(true);
	}

	protected void handleKey(SelectionKey key) throws IOException {
		if (key.isAcceptable()) { // 接收请求
			doAccept(key);
		} else if (key.isReadable()) { // 读数据
			doRead(key);
		}
	}

	private void doWrite(String DestAddr, String LocalAddr,	String TerminalAddr, String FrameContent) {
		SelectionKey key;
		if (TerminalAddr.length() > 0) {
			key = (SelectionKey) termChannelPool.getNamedChannel(TerminalAddr);
		} else {
			key = (SelectionKey) termChannelPool.getUnNamedChannel(DestAddr);
		}
		if (key == null) {

		} else {
			IHDTermReadImpl readImpl = (IHDTermReadImpl) key.attachment();
			byte[] data = ConvertUtil.ascStr2Bcd(FrameContent);
			readImpl.write(data);
		}
	}

	private void doAccept(SelectionKey key) throws IOException {
		SocketChannel channel;
		ServerSocketChannel server;
		server = (ServerSocketChannel) key.channel();
		channel = server.accept();
		channel.configureBlocking(false);
		setSocketOptions(channel.socket());
		SelectionKey tmpKey = channel.register(selector, SelectionKey.OP_READ);

		IHDTermReadImpl readImpl = new IHDTermReadImpl();
		readImpl.setChannel(tmpKey);
		readImpl.setRecentCommTime(System.currentTimeMillis());
		tmpKey.attach(readImpl);

		termChannelPool.addUnNamedChannel(channel.socket().getInetAddress().getHostAddress()
				+ ":" + channel.socket().getPort(), tmpKey);
	}

	class ListenThread extends Thread {
		public void run() {
			while (true) {
				try {
					doRun();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	class SendDataThread extends Thread {
		public void run() {
			while (true) {
				if (TCPChannelConstants.GlobalSendList.size() > 0) {
					DownDataStruct ds = (DownDataStruct) TCPChannelConstants.GlobalSendList.get(0);
					if (ds == null) {

					} else {
						String sDestAddr = ds.DestAddr;
						String sLocalAddr = ds.LocalAddr;
						int iChannelType = ds.ChannelType;
						// int iFramePriority = ds.GetFramePriority();
						String sTerminalAddr = ds.TerminalAddr;
						String sFrameContent = ds.FrameContent;
						TCPChannelConstants.trc.TraceLog("目标地址：" + sDestAddr + " ,通道地址：" + sLocalAddr + " ,通讯方式：" + iChannelType
								+ " ,终端逻辑地址：" + sTerminalAddr + " ,命令内容：" + sFrameContent);
						doWrite(sDestAddr, sLocalAddr, sTerminalAddr, sFrameContent);
					}
					TCPChannelConstants.GlobalSendList.remove(0);
				}
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	class ChannelSetCheckerThread extends Thread {
		public void run() {
			while (true) {
				Iterator i = termChannelPool.getNamedChannelSet().entrySet().iterator();
				while (i.hasNext()) {
					try{
						long tm;
						Map.Entry en = (Map.Entry) i.next();
						SelectionKey k = (SelectionKey) en.getValue();
						String logicAddr = (String) en.getKey();
						IHDTermReadImpl readImpl = (IHDTermReadImpl) k.attachment();
						tm = System.currentTimeMillis() - readImpl.getRecentCommTime();
						SocketChannel channel = (SocketChannel) k.channel();
						String ip = channel.socket().getInetAddress().getHostAddress();
						int port = channel.socket().getPort();
						if (tm / 1000 > NamedChannelTimeOut * 60) {
							k.cancel();
							try {
								((SocketChannel) (k.channel())).socket().shutdownInput();
								((SocketChannel) (k.channel())).socket().shutdownOutput();
								((SocketChannel) (k.channel())).socket().close();
								k.channel().close();
							} catch (IOException e) {
								e.printStackTrace();
							}
							termChannelPool.deleteNamedChannel(logicAddr);
							TCPChannelConstants.trc.TraceLog("Link:" + ip + "," + port + " no data for [" + NamedChannelTimeOut
									+ "] minute,disconnect!");
						}
					}catch(Exception e){
						e.printStackTrace();
					}
				}

				i = termChannelPool.getUnNamedChannelSet().entrySet().iterator();
				while (i.hasNext()) {
					try{
						long tm;
						Map.Entry en = (Map.Entry) i.next();
						SelectionKey k = (SelectionKey) en.getValue();
						IHDTermReadImpl readImpl = (IHDTermReadImpl) k.attachment();
						tm = System.currentTimeMillis() - readImpl.getRecentCommTime();
						SocketChannel channel = (SocketChannel) k.channel();
						String ip = channel.socket().getInetAddress().getHostAddress();
						int port = channel.socket().getPort();
						if (tm / 1000 > UnNamedChannelTimeOut * 60) {
							k.cancel();
							try {
								((SocketChannel) (k.channel())).socket().shutdownInput();
								((SocketChannel) (k.channel())).socket().shutdownOutput();
								((SocketChannel) (k.channel())).socket().close();
								k.channel().close();
							} catch (IOException e) {
								e.printStackTrace();
							}
							termChannelPool.deleteUnNamedChannel(ip + ":" + port);
							TCPChannelConstants.trc.TraceLog("Link:" + ip + "," + port + " no data for [" + UnNamedChannelTimeOut
									+ "] minute,disconnect!");
						}
					}catch(Exception e){
						e.printStackTrace();
					}
				}
				try {
					Thread.sleep(ChannelSetCheckInterval *1000 * 60);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}

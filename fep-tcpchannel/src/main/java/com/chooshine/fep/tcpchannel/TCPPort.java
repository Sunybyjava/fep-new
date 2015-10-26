package com.chooshine.fep.tcpchannel;

import java.net.InetSocketAddress;
import java.nio.channels.Selector;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.net.ServerSocket;
import java.io.*;
import java.util.Iterator;
import java.nio.channels.SocketChannel;
import com.chooshine.fep.communicate.utils;
import java.nio.ByteBuffer;
import java.util.Hashtable;
import java.util.Calendar;

import com.chooshine.fep.FrameDataAreaExplain.DataSwitch;
import com.chooshine.fep.FrameDataAreaExplain.GetFrameInfo;
import java.util.Enumeration;

/**
 * <p>
 * Title: TCP端口类实现
 * </p>
 *
 * <p>
 * Description: 实现TCP的基本通讯功能，处理数据的接收、提供发送消息的接口
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2005
 * </p>
 *
 * <p>
 * Company: cnhualong
 * </p>
 *
 * @author
 * @version
 */
public class TCPPort extends Thread {
	ServerSocketChannel serverChannel;
	ServerSocket serverSocket;
	Selector selector;
	// private int Index = 0;
	private int LocalPort = 0;
	private int TimeOut = 0;
	private String LocalIp = "";
	private ByteBuffer buffer = ByteBuffer.allocateDirect(5000); // 用于发送的缓冲
	private String Debug = "";
	private Hashtable<String, SocketConnectionList> SocketConnectionList;

	public TCPPort(String DebugFlag) {
		this.Debug = DebugFlag;
	}

	public String GetDebugFlag() { // 获取当前的运行标志
		return this.Debug;
	}

	public void ChangeDebugFlag(String DebugFlag) { // 修改运行标志
		this.Debug = DebugFlag;
	}

	public int GetCurrentConnectionCount() {
		return this.SocketConnectionList.size();
	}

	public int CreateTCPPort(int PortIndex, String IpAddress, int Port, int ConnectionTimeOut) {
		// Index = PortIndex;
		LocalPort = Port;
		LocalIp = IpAddress;
		TimeOut = ConnectionTimeOut;
		SocketConnectionList = new Hashtable<String, SocketConnectionList>(10000);
		return InitSocket(Port);
	}

	public int SendTCPMessage(String IpAddress, int Port, String MessageContent) {
		String sHashKey = IpAddress + ":" + Port;
		SocketConnectionList sc = (SocketConnectionList) SocketConnectionList.get(sHashKey);
		if (sc != null) {
			buffer.clear();
			byte[] data = utils.str2bytes(MessageContent);
			buffer.put(data);
			buffer.flip();
			TCPServerConstants.Trc1.TraceLog(" 6、SendData Success.");
			try {
				sc.channel.write(buffer);
			} catch (IOException ex1) {
				buffer.clear();
				// buffer.allocate(5000);
				buffer = ByteBuffer.allocate(5000);
				TCPServerConstants.Log1.WriteLog("Fep Link Error " + ex1.toString());
			}
			return 0;
		} else {
			return -1001;
		}
	}

	public static void main(String[] args) {
		/*
		 * TCPPort tp = new TCPPort(); tp.CreateTCPPort(0, "172.19.74.13", 3000,
		 * 15); tp.start();
		 */
	}

	private int InitSocket(int ListenPort) {
		System.out.println("Listening on port " + ListenPort);
		// 分配一个ServerSocketChannel
		try {
			serverChannel = ServerSocketChannel.open();
			// 从ServerSocketChannel里获得一个对应的Socket
			serverSocket = serverChannel.socket();
			// 生成一个Selector
			selector = Selector.open();
			// 把Socket绑定到端口上
			serverSocket.bind(new InetSocketAddress(ListenPort));
			// serverChannel为非block
			serverChannel.configureBlocking(false);
			// 通过Selector注册ServerSocetChannel
			serverChannel.register(selector, SelectionKey.OP_ACCEPT);
			return 0;
		} catch (IOException ex) {
			return -1;
		}
	}

	protected void registerChannel(Selector selector, SocketChannel channel, int ops) throws Exception {
		if (channel == null) {
			return; // could happen
		}
		// set the new channel non-blocking
		channel.configureBlocking(false);
		// register it with the selector
		channel.register(selector, ops);
		String sHashKey = channel.socket().getInetAddress().getHostAddress() + ":" + channel.socket().getPort();
		SocketConnectionList sc = (SocketConnectionList) SocketConnectionList.get(sHashKey);
		if (sc == null) {
			sc = new SocketConnectionList();
			sc.ListKey = channel.socket().getInetAddress().getHostAddress() + ":" + channel.socket().getPort();
			sc.channel = channel;
			sc.LastCommDate = Calendar.getInstance();
			SocketConnectionList.put(sc.ListKey, sc);
		}
	}

	private void UpdateLasCommData(String FromIp, int FromPort) {
		String sHashKey = FromIp + ":" + FromPort;
		SocketConnectionList sc = (SocketConnectionList) SocketConnectionList.get(sHashKey);
		if (sc != null) {
			sc.LastCommDate = Calendar.getInstance();
			SocketConnectionList.remove(sHashKey);
			SocketConnectionList.put(sHashKey, sc);
		}
	}

	private void RemoveDisConnectedSocket(String FromIp, int FromPort) {
		String sHashKey = FromIp + ":" + FromPort;
		SocketConnectionList sc = (SocketConnectionList) SocketConnectionList.get(sHashKey);
		if (sc != null) {
			SocketConnectionList.remove(sHashKey);
		}

	}

	@SuppressWarnings("rawtypes")
	private void DeleteTimeOutConnection() {
		Enumeration e = SocketConnectionList.elements();
		for (; e.hasMoreElements();) {
			SocketConnectionList sc = (SocketConnectionList) e.nextElement();
			Calendar c = sc.LastCommDate;
			c.add(Calendar.MINUTE, TimeOut); // 判断链路是否超时
			try {
				if (c.before(Calendar.getInstance())) {
					try {
						sc.channel.close();
						SocketConnectionList.remove(sc.ListKey);
						TCPServer.PrintDebugMessage("remove a channel " + sc.channel.toString(), Debug);
						TCPServerConstants.Trc1.TraceLog("remove a channel " + sc.channel.toString());
					} catch (Exception ex1) {
					}
				}
			} finally {
				c.add(Calendar.MINUTE, -TimeOut); // 恢复判断时间标志
			}
		}
	}

	private void AddCompleteFrameToRecvList(String sData, int iProtocolTag, SocketChannel Channel) {
		StructRecvData sd = new StructRecvData();
		sd.DataContent = sData.trim();
		sd.DataLength = sData.length();
		sd.LocalIp = LocalIp;
		sd.LocalPort = LocalPort;
		sd.FromIp = Channel.socket().getInetAddress().getHostAddress();
		sd.FromPort = Channel.socket().getPort();

		TCPServerConstants.Trc1
				.TraceLog(" B、RecUpData. FromIp:" + sd.FromIp + " FromPort:" + sd.FromPort + " DataContent:"
						+ sd.DataContent + " ReceiveListSize:" + (TCPServerConstants.GlobalReceiveList.size() + 1));
		TCPServerConstants.GlobalReceiveList.add(0, sd);

		if (iProtocolTag == 20) /*
								 * (GetFrameInfo.
								 * gTerminalProtocolCheckOfZheJiang(sd.
								 * DataContent))
								 */ {
			String sTemp = sd.DataContent.substring(16, 18); // 对于浙规的心跳、登陆等命令返回确认
			if ((sTemp.equals("24")) || (sTemp.equals("A4")) || (sTemp.equals("21")) || (sTemp.equals("A1"))) {
				if (sTemp.substring(0, 1).equals("A")) {
					sTemp = "2" + sTemp.substring(1, 2);
				} else if (sTemp.substring(0, 1).equals("2")) {
					sTemp = "A" + sTemp.substring(1, 2);
				}
				String sQrBW = sd.DataContent.substring(0, 16) + sTemp + "00000016";
				sQrBW = GetFrameInfo.gGetParityByteOfZheJiang(sQrBW);
				SendTCPMessage(sd.FromIp, sd.FromPort, sQrBW);
				TCPServerConstants.Trc1.TraceLog(" B-1、SendHeartBeatMessage. ToIp:" + sd.FromIp + " ToPort:"
						+ sd.FromPort + " DataContent:" + sQrBW);
			}
		} else if (iProtocolTag == 10) /*
										 * (GetFrameInfo.
										 * gTerminalProtocolCheckOfQuanGuo(sd.
										 * DataContent))
										 */ {// 对于全国规约的心跳、登陆等命令返回确认
			String sTemp = sd.DataContent.substring(28, 36);
			if ((sd.DataContent.substring(24, 26).equals("02"))
					&& ((sTemp.equals("00000400")) || (sTemp.equals("00000100")) || (sTemp.equals("00000200")))) {
				String slen = sd.DataContent.substring(4, 6) + sd.DataContent.substring(2, 4);
				int ilen = 72 + (Integer.parseInt(slen, 16) & 3);
				slen = Integer.toHexString(ilen);
				slen = "0000".substring(0, 4 - slen.length()) + slen;
				slen = slen.substring(2, 4) + slen.substring(0, 2);
				String sQRBWTemp = "6832003200680B" + sd.DataContent.substring(14, 24) + "006"
						+ sd.DataContent.substring(27, 28) + "000001000016";// + "02" + sd.DataContent.substring(28, 36) + "00"
						//+ "0016";
				sQRBWTemp = GetFrameInfo.gGetParityByteOfQuanGuo(sQRBWTemp);
				SendTCPMessage(sd.FromIp, sd.FromPort, sQRBWTemp);
			}
		} else if (iProtocolTag == 30) {// 海兴集抄规约的心跳命令返回确认
			String sTemp = sd.DataContent.substring(26, 30); // 心跳命令标识
			if (sTemp.equals("1050")) {
				String sQrBW = "05640E" + sd.DataContent.substring(12, 16) + "0000";
				String sCheck = DataSwitch.MultiCRC(sQrBW, "3D65");
				sQrBW = sQrBW + DataSwitch.ReverseStringByByte(sCheck);
				String sQrBWTemp = "C00081" + sd.DataContent.substring(26, 30);
				sCheck = DataSwitch.MultiCRC(sQrBWTemp, "3D65");
				sQrBW = sQrBW + sQrBWTemp + DataSwitch.ReverseStringByByte(sCheck);
				SendTCPMessage(sd.FromIp, sd.FromPort, sQrBW);
			}
		} else if (iProtocolTag == 40) {// DLMS的心跳命令返回确认
			String sTemp = sd.DataContent.substring(16, 18); // 心跳命令标识
			if (sTemp.equals("DD")) {// 0001000100010012DD1000000000303030313839363030303132
				String sQrBW = "0001000100010001DA";
				SendTCPMessage(sd.FromIp, sd.FromPort, sQrBW);
				TCPServerConstants.Trc1.TraceLog(" B-1、SendHeartBeatMessage. ToIp:" + sd.FromIp + " ToPort:"
						+ sd.FromPort + " DataContent:" + sQrBW);

			}
		}

		UpdateLasCommData(sd.FromIp, sd.FromPort);
	}

	protected void processData(String sData, SocketChannel channel, byte[] Data) {
		String sHashKey = channel.socket().getInetAddress().getHostAddress() + ":" + channel.socket().getPort();
		SocketConnectionList sc = (SocketConnectionList) SocketConnectionList.get(sHashKey);
		int itemp = 0;
		int RecvLen = sData.length();
		try {
			if (sc != null) {
				SearchFrame sf = new SearchFrame();
				StructSearchResult ss = sf.SearchRightFrame(sData, sData.length());
				if ((ss.bFrameIsFull) && (ss.bFrameIsRight)) {
					AddCompleteFrameToRecvList(sData.substring(ss.iStartPosition, ss.iEndPos), ss.iProtocolTag,
							channel);
					int iLen = ss.iEndPos - ss.iStartPosition;
					int iDataLen = sData.length();
					if (iLen != iDataLen) { // 接收区中还存数据需要保存到缓冲中
						if (ss.iStartPosition == 0) { // 报文头部就是起始位置，要保留后面的数据
							for (int i = 0; i < RecvLen - ss.iEndPos; i++) {
								sc.Buffer[sc.BufferLen] = sData.charAt(i + ss.iEndPos);
								sc.BufferLen = sc.BufferLen + 1;
							}
						} else {
							for (int i = 0; i < ss.iStartPosition; i++) { // 前面有需要保留的内容，可能和Buffer中的内容组合成为一帧
								sc.Buffer[sc.BufferLen] = sData.charAt(i);
								sc.BufferLen = sc.BufferLen + 1;
							}
							for (int i = 0; i < RecvLen - ss.iEndPos; i++) { // 完整帧后面的内容同样需要保留，可能和后续内容组为一帧
								sc.Buffer[sc.BufferLen] = sData.charAt(i + ss.iEndPos);
								sc.BufferLen = sc.BufferLen + 1;
							}
						}
					} else {

					}
				} else {
					try {
						if ((sc.BufferLen + RecvLen > 1000) && (sc.BufferLen > 0)) {
							itemp = RecvLen - (1000 - sc.BufferLen); // 把ReceiveInfo放入buff还差的长度
							for (int i = 0; i < sc.BufferLen - itemp; i++) {
								sc.Buffer[i] = sc.Buffer[i + itemp];
							}
						}
						sc.BufferLen = sc.BufferLen - itemp;
						for (int i = 0; i < RecvLen; i++) {
							sc.Buffer[sc.BufferLen] = sData.charAt(i);
							sc.BufferLen = sc.BufferLen + 1;
						}
					} catch (Exception ex) {
					}
				}
				boolean bHavdata = true;

				while (bHavdata) {
					String gbuffer = new String(sc.Buffer).substring(0, sc.BufferLen).trim();
					ss = sf.SearchRightFrame(gbuffer, sc.BufferLen);
					if (gbuffer.length() < 2)
						break;
					if ((ss.bFrameIsFull) && (ss.bFrameIsRight)) {
						AddCompleteFrameToRecvList(gbuffer.substring(ss.iStartPosition, ss.iEndPos), ss.iProtocolTag,
								channel);
						if (ss.iEndPos - ss.iStartPosition != sc.BufferLen) { // 接收区中还存数据需要保存到缓冲中
							if (ss.iStartPosition == 0) { // 报文头部就是起始位置，要保留后面的数据
								for (int i = 0; i < sc.BufferLen - ss.iEndPos; i++) {
									sc.Buffer[i] = gbuffer.charAt(i + ss.iEndPos);
								}
								sc.BufferLen = sc.BufferLen - ss.iEndPos;
							} else {
								for (int i = 0; i < sc.BufferLen - ss.iEndPos; i++) { // 完整帧后面的内容同样需要保留，可能和后续内容组为一帧
									sc.Buffer[i] = gbuffer.charAt(i + ss.iEndPos);
								}
								sc.BufferLen = sc.BufferLen - ss.iEndPos;
							}
						} else {
							sc.BufferLen = 0;
							sc.Buffer[0] = '#';
						}
					} else {
						bHavdata = false;
						try {
							if (ss.FrameHeadPort > 0) {
								// 清除没有帧头前面无用的数据
								sc.BufferLen = sc.BufferLen - ss.FrameHeadPort;
								for (int i = 0; i < sc.BufferLen; i++) {
									sc.Buffer[i] = sc.Buffer[i + ss.FrameHeadPort];
								}
							} else if (ss.FrameHeadPort == -1) { // 整个Buf中就没有帧头,则清空Buf
								for (int i = 0; i < 1000; i++) {
									sc.Buffer[i] = '#';
								}
								sc.BufferLen = 0;
							}
						} catch (Exception ex) {
						}

						try {
							if ((!ss.bFrameIsRight) && (ss.bFrameIsFull)) {
								bHavdata = true;
								if (ss.iStartPosition == 0) { // 报文头部就是起始位置，要保留后面的数据
									sc.BufferLen = sc.BufferLen - ss.iEndPos;
									for (int i = 0; i < sc.BufferLen; i++) {
										sc.Buffer[i] = sc.Buffer[i + ss.iEndPos];
									}
								} else {
									// 前面有需要保留的内容，可能和Buffer中的内容组合成为一帧
									for (int i = 0; i < sc.BufferLen - ss.iStartPosition; i++) { // 将前面的移除掉以空出位子
										sc.Buffer[i] = sc.Buffer[i + ss.iStartPosition];
									}
									sc.BufferLen = sc.BufferLen - ss.iStartPosition;
								}
							}
						} catch (Exception ex) {
						}
					}
				}
				SocketConnectionList.remove(sc);
				SocketConnectionList.put(sHashKey, sc);
			}
		} catch (Exception ex) {
			TCPServerConstants.Log1.WriteLog("processData error " + ex.toString());
		}

	}

	/*
	 * if (GetFrameInfo.gTerminalProtocolCheckOfZheJiang(sr.DataContent)) {
	 * String sTemp = sr.DataContent.substring(16, 18); //对于浙规的心跳、登陆等命令返回确认 if (
	 * (sTemp.equals("24")) || (sTemp.equals("A4")) || (sTemp.equals("21")) ||
	 * (sTemp.equals("A1"))) { if (sTemp.substring(0, 1).equals("A")) { sTemp =
	 * "2" + sTemp.substring(1, 2); } else if (sTemp.substring(0,
	 * 1).equals("2")) { sTemp = "A" + sTemp.substring(1, 2); } String sQrBW =
	 * sr.DataContent.substring(0, 16) + sTemp + "00000016"; sQrBW =
	 * GetFrameInfo.gGetParityByteOfZheJiang(sQrBW); SendUDPMessage(sr.FromIp,
	 * sr.FromPort, sQrBW, sQrBW.length()); } } else if
	 * (GetFrameInfo.gTerminalProtocolCheckOfQuanGuo(sr.DataContent))
	 * {//对于全国规约的心跳、登陆等命令返回确认 String sTemp =sr.DataContent.substring(28,36); if
	 * ((sr.DataContent.substring(24,26).equals("02")) &&
	 * ((sTemp.equals("00000400")) || (sTemp.equals("00000100"))
	 * ||(sTemp.equals("00000200")))){ String slen=
	 * sr.DataContent.substring(4,6)+sr.DataContent.substring(2,4); int
	 * ilen=72+(Integer.parseInt(slen,16) & 3); slen=Integer.toHexString(ilen);
	 * slen="0000".substring(0,4-slen.length()) + slen;
	 * slen=slen.substring(2,4)+slen.substring(0,2); String
	 * sQRBWTemp="68"+slen+slen+"68"+"09"+sr.DataContent.substring(14,24)+"006"+
	 * sr.DataContent.substring(27,28)+ "00000400"
	 * +"02"+sr.DataContent.substring(28,36)+"00"+"0016";
	 * sQRBWTemp=GetFrameInfo.gGetParityByteOfQuanGuo(sQRBWTemp);
	 * SendUDPMessage(sr.FromIp, sr.FromPort, sQRBWTemp, sQRBWTemp.length()); }
	 * }
	 */

	protected void receiveData(SelectionKey key) throws Exception {
		SocketChannel socketChannel = (SocketChannel) key.channel();
		int count = 0;
		buffer.clear();
		count = socketChannel.read(buffer);
		if (count == -1) {
			RemoveDisConnectedSocket(socketChannel.socket().getInetAddress().getHostAddress(),
					socketChannel.socket().getPort());
			socketChannel.close();
		}
		while (count > 0) {
			try {
				buffer.flip();
				byte data[] = new byte[buffer.limit()];
				buffer.get(data, 0, buffer.limit());
				// 显示收到的消息经过处理后的实际内容和来源socket的信息
				TCPServerConstants.Trc1.TraceLog(
						" A、RecvBuffer Msg:" + utils.bytes2str(data) + " socketChannel:" + socketChannel.toString());
				int iLen = (utils.bytes2str(data)).length();
				if (iLen > 2048) {
					TCPServerConstants.Trc1.TraceLog(" A-0、 MsgLength > 2048:" + iLen);
				} else {
					processData(utils.bytes2str(data), socketChannel, data);
				}
				count = socketChannel.read(buffer);
			} catch (Exception ex) {
				TCPServerConstants.Log1.WriteLog("receiveData error " + ex.toString());
			}
		}
	}

	@SuppressWarnings("rawtypes")
	public void run() {
		Calendar detectDateTime = null;
		try {
			detectDateTime = Calendar.getInstance();
		} catch (Exception ex5) {
		}
		while (true) {
			int n = 0;
			try {
				n = selector.select(15000);
			} catch (IOException ex) {
			}
			// 维护和前置机通道的链路
			try {
				detectDateTime.add(Calendar.MINUTE, 5); // 每隔5分钟检查一次链路情况
				if (detectDateTime.before(Calendar.getInstance())) {
					detectDateTime = Calendar.getInstance(); // 更新本次更新时间
					// TCPServer.PrintDebugMessage("Current Connection Count " +
					// SocketConnectionList.size(),Debug);
					DeleteTimeOutConnection();
					TCPServer.PrintDebugMessage("Current Connection Count " + SocketConnectionList.size(), Debug);
					TCPServerConstants.Trc1.TraceLog("Current Connection Count " + SocketConnectionList.size());
				} else {
					detectDateTime.add(Calendar.MINUTE, -5);
				}
			} catch (Exception ex3) {
			}
			if (n == 0) {
				continue;
			} else if (n != 0) {
				Iterator it = selector.selectedKeys().iterator();
				while (it.hasNext()) {
					SelectionKey key = (SelectionKey) it.next();
					if (key.isValid() && key.isAcceptable()) { // socket建立成功
						ServerSocketChannel server = (ServerSocketChannel) key.channel();
						SocketChannel channel = null;
						try {
							channel = server.accept();
						} catch (IOException ex1) {
						}
						try { // 注册本次连接的通道信息
							registerChannel(selector, channel, SelectionKey.OP_READ);
						} catch (Exception ex2) {
						}
					}
					if (key.isValid() && key.isReadable()) {
						try { // 接收消息的事件
							receiveData(key);
						} catch (Exception ex4) {
						}
					}
					it.remove();
					try {
						Thread.sleep(1);
					} catch (InterruptedException ex6) {
					}
				}
			}
		}
	}

	class SocketConnectionList {
		public String ListKey = "";
		public SocketChannel channel = null;
		private Calendar LastCommDate = null; // 最后通讯时间
		private char[] Buffer = new char[3000];
		private int BufferLen = 0;
	}
}

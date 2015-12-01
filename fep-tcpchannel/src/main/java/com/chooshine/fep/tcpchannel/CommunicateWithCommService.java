package com.chooshine.fep.tcpchannel;

import java.io.IOException;
import com.chooshine.fep.communicate.utils;
import java.nio.channels.SocketChannel;
import java.nio.ByteBuffer;
import java.net.*;
import java.nio.channels.Selector;
import java.nio.channels.SelectionKey;
import java.util.Iterator;
import java.util.Set;

/**
 * <p>
 * Title: 和通讯服务通讯的类
 * </p>
 *
 * <p>
 * Description: 负责和通讯服务的socket进行通讯，进行数据的接收、发送的处理
 * </p>
 *
 * <p>
 * Copyright:
 * </p>
 *
 * <p>
 * Company:
 * </p>
 *
 * @author
 * @version
 */
public class CommunicateWithCommService extends Thread {
	private Selector selector;

	private SocketChannel UpClient; // 上行数据处理的socket客户端对象

	private SocketChannel DownClient; // 下行数据处理的socket客户端对象

	// private boolean AckFlag = true; //命令的确认返回标记，决定是否要发送下一条数据
	private String Ip = ""; // 要接入的服务器端ip

	private int Port = 0; // 要接入的服务器端的端口号

	private String Debug = "";

	private boolean UpRegisterFlag = false; // 上行连接注册是否成功的标记

	private boolean DownRegisterFlag = false; // 下行连接是否成功的标记

	private ByteBuffer buffer = ByteBuffer.allocateDirect(5000); // 用于发送的缓冲

	/*
	 * public static void main(String args[]) { CommunicateWithCommService cc =
	 * new CommunicateWithCommService( "172.16.151.136", 5000); cc.start(); }
	 */

	public String GetDebugFlag() { // 获取当前的运行标志
		return this.Debug;
	}

	public void ChangeDebugFlag(String DebugFlag) { // 修改运行标志
		this.Debug = DebugFlag;
	}

	public CommunicateWithCommService(String HostIp, int Port, String DebugFlag) {
		this.Debug = DebugFlag;
		this.Ip = HostIp;
		this.Port = Port;
		initSocket();
	}

	private void initSocket() { // 初始化上、下行的socket对象，连接服务器
		try {
			UpRegisterFlag = false;
			DownRegisterFlag = false;
			selector = Selector.open();
			InetSocketAddress isa = new InetSocketAddress(Ip, Port);
			UpClient = SocketChannel.open();
			UpClient.configureBlocking(false);
			UpClient.connect(isa);
			UpClient.register(selector, SelectionKey.OP_CONNECT);
			DownClient = SocketChannel.open();
			DownClient.configureBlocking(false);
			DownClient.connect(isa);
			DownClient.register(selector, SelectionKey.OP_CONNECT);
		} catch (IOException e) {
			TCPServerConstants.Log1.WriteLog("Func:CommunicateWithCommService__initSocket();Error:" + e.toString());
		}
	}

	// 组成向通讯服务注册的命令并通过对应的socket对象发送
	private void BuildRegisterFrameAndSend(SocketChannel keyChannel) {
		String s = "";
		if ((keyChannel == UpClient) && (!UpRegisterFlag)) {
			s = "0000000E0000000100000000320A"; // 上行连接的注册
			buffer.clear();
			buffer.put(utils.str2bytes(s));
			buffer.flip();
			TCPServer.PrintDebugMessage("Msg to CommService " + s + " " + keyChannel.toString(), Debug);
			TCPServerConstants.Trc1.TraceLog("Msg to CommService " + s + " " + keyChannel.toString());
			try {
				keyChannel.write(buffer);
			} catch (IOException ex) {
			}
		} else if ((keyChannel == DownClient) && (!DownRegisterFlag)) {
			s = "0000000E00000001000000003214"; // 下行连接的注册
			buffer.clear();
			buffer.put(utils.str2bytes(s));
			buffer.flip();
			TCPServer.PrintDebugMessage("Msg to CommService " + s + " " + keyChannel.toString(), Debug);
			TCPServerConstants.Trc1.TraceLog("Msg to CommService " + s + " " + keyChannel.toString());
			try {
				keyChannel.write(buffer);
			} catch (IOException ex) {
			}
		}
	}

	protected void receiveData(SelectionKey key) throws Exception {
		// 首先读取12个字符的报文头内容，分析报文头的有效性
		// System.out.println("enter receiveData" );
		TCPServerConstants.Trc1.TraceLog(" 1、Enter receiveData.");
		ByteBuffer[] buffer1 = new ByteBuffer[10];
		buffer1[0] = ByteBuffer.allocateDirect(12); // 用于接收的缓冲
		try {
			SocketChannel socketChannel = (SocketChannel) key.channel();
			buffer1[0].clear();
			long count = 0;
			try {
				count = socketChannel.read(buffer1, 0, 1);
			} catch (IOException ex1) {
				count = -1;
			}
			if (count == -1) { // 前置机的socket通道出现异常时，也需要处理链路队列，保证队列中的数据正确
				// 此处重连接链路
				try {
					Thread.sleep(5000);
				} catch (Exception e) {
				}
				initSocket();
				return;
			}
			if (count == 12) {
				buffer1[0].flip();
				byte data[] = new byte[10000];
				buffer1[0].get(data, 0, buffer1[0].limit());
				MessageBody mb = new MessageBody();
				String strMesshead = utils.bytes2str(data, buffer1[0].limit());
				mb.TotalLength = strMesshead.substring(0, 8);
				mb.CommandID = strMesshead.substring(8, 16);
				mb.SeqID = strMesshead.substring(16, 24);

				// 如果报文头有效，则继续读取后面的报文体内容，进行处理，否则丢失当前报文头，继续读取12个字符
				buffer1[1] = ByteBuffer.allocateDirect(Integer.parseInt(mb.TotalLength, 16) - 12); // 用于接收的缓冲
				count = socketChannel.read(buffer1, 1, 1);
				if (count > 0) {
					buffer1[1].flip();
					buffer1[1].get(data, 12, buffer1[1].limit());
					String strMessbody = utils.bytes2str(data, Integer.parseInt(mb.TotalLength, 16));
					TCPServerConstants.Trc1.TraceLog(" 2、ReceiveData Data. Length:" + mb.TotalLength + " CommandID:"
							+ mb.CommandID + " SeqID:" + mb.SeqID + " Messbody:" + strMessbody);
					processData(strMessbody, socketChannel, data);
					// 对于有效的报文处理后，退出本次数据的读取处理过程，如果还有数据需要处理会再次进入本函数处理
				}
			}
//			System.gc();
		} catch (Exception ex3) {
			// fl.WriteLog("CommunicationScheduler:receiveData() IOerror,error
			// message is " +ex3.toString());
		}
	}

	protected void AckMessage(String CommandID, String sData, SocketChannel channel) {
		try {
			String ss = "0000000D000000" + CommandID + sData.substring(16, 24) + "0A"; // 对于收到数据的确认返回
			buffer.clear();
			buffer.put(utils.str2bytes(ss));
			buffer.flip();
			try {
				channel.write(buffer);
			} catch (IOException ex) {
			}
			buffer.clear();
		} catch (Exception ex1) {
		}
	}

	// 分析得到命令的类型
	protected String AnalyzeCommandType(String Data) {
		try {
			String sMessageLen = Data.substring(1, 8);
			if (Data.length() != utils.HexStrToInt(sMessageLen) * 2) {
				return "0";
			} else {
				String sCommandType = Data.substring(14, 16);
				return (sCommandType);
			}
		} catch (Exception ex) {
		}
		return "0";
	}

	// 从字符串中分析得到ip地址
	private String ExtractIpAddr(String AddrInfo) {
		int iPos = AddrInfo.indexOf(":");
		return AddrInfo.substring(0, iPos);
	}

	// 从字符串中分析得到端口号
	private int ExtractPort(String AddrInfo) {
		int iPos = AddrInfo.indexOf(":");
		String sPort = AddrInfo.substring(iPos + 1, AddrInfo.length());
		return Integer.parseInt(sPort);
	}

	// 将收到要发送的数据加入到发送队列中
	private void AddCommandInfoToSendList(String Data, byte[] bData) {
		try {
			int MessageLength = 0;
			String MessageContent = null;
			try {
				MessageLength = utils.HexStrToInt(Data.substring(24, 28)); // 实际有效内容的长度
				byte[] Content = new byte[MessageLength];
				for (int i = 0; i < MessageLength; i++) {
					Content[i] = bData[i + 14];
				}
				MessageContent = new String(Content); // 获取实际有效的内容
			} catch (Exception ex1) {
				TCPServerConstants.Log1.WriteLog("Error1 " + ex1.toString());
			}
			byte[] Src = new byte[25];
			for (int i = 0; i < 25; i++) {
				Src[i] = bData[i + 14 + MessageLength];
			}
			String MsgSrc = new String(Src).trim(); // 消息来源物理地址
			String LocalAddr = "";
			try {
				byte[] From = new byte[25];
				for (int i = 0; i < 25; i++) {
					From[i] = bData[i + 39 + MessageLength];
				}
				LocalAddr = new String(From).trim();
			} catch (Exception ex3) {
			}
			TCPServerConstants.Trc1.TraceLog(" 3、AddToSendList MsgLen:" + MessageLength + " MsgContent:"
					+ MessageContent + " MsgSrc:" + MsgSrc + " LocalAddress:" + LocalAddr);
			StructSendData sd = new StructSendData();
			sd.DataContent = MessageContent;
			sd.DataLength = MessageLength;
			sd.ToIp = ExtractIpAddr(MsgSrc);
			sd.ToPort = ExtractPort(MsgSrc);
			sd.LocalIp = ExtractIpAddr(LocalAddr);
			sd.LocalPort = ExtractPort(LocalAddr);
			TCPServerConstants.GlobalSendList.add(0, sd);
		} catch (Exception ex) {
			TCPServerConstants.Log1.WriteLog(ex.toString());

		}
	}

	protected void processData(String sData, SocketChannel channel, byte[] Data) {
		if (channel == UpClient) {
			if (AnalyzeCommandType(sData).equals("01")) {
				String s = sData.substring(24, 26);
				if (s.equals("0A")) {
					UpRegisterFlag = true;
					CommunicationThread ct = new CommunicationThread(channel, selector);
					ct.start();
					TCPServerConstants.Trc1.TraceLog("UpClientSocket created ");
				}
			}
			/*
			 * else if (AnalyzeCommandType(sData).equals("02")) { String
			 * sBackFlag = sData.substring(8, 10); if (sBackFlag.equals("08")) {
			 * String s = sData.substring(24, 26); if (s.equals("0A")) {
			 * //AckFlag = true; } } }
			 */
		} else if (channel == DownClient) {
			if (AnalyzeCommandType(sData).equals("01")) {
				String s = sData.substring(24, 26);
				if (s.equals("0A")) {
					DownRegisterFlag = true;
					TCPServerConstants.Trc1.TraceLog("DownClientSocket created ");
				}
			} else if (AnalyzeCommandType(sData).equals("03")) {
				AddCommandInfoToSendList(sData, Data);
			}
		}
	}

	@SuppressWarnings("rawtypes")
	public void run() {
		// boolean SocketInit = true;
		while (true) {
			try {
				int n = selector.selectNow(); // 获得io准备就绪的channel数量
				if (n == 0) {
					continue;
				}
				try {
					Thread.sleep(1);
				} catch (InterruptedException ex1) {
				}

				Set readyKeys = selector.selectedKeys();
				Iterator i = readyKeys.iterator();
				while (i.hasNext()) {
					SelectionKey key = null;
					try {
						key = (SelectionKey) i.next();
						i.remove();
					} catch (Exception ex2) {
					}
					if (key != null) {
						if (key.isAcceptable()) {
							TCPServerConstants.Trc1.TraceLog("isAcceptable()");
						} else if (key.isReadable()) {
							try {
								// 接收消息的事件
								receiveData(key);
							} catch (Exception ex4) {
							}
						} else if (key.isConnectable()) {
							SocketChannel keyChannel = (SocketChannel) key.channel();
							keyChannel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
							if (keyChannel.isConnectionPending()) {
								keyChannel.finishConnect();
							}
							BuildRegisterFrameAndSend(keyChannel);
						} else if (key.isWritable()) {
						}
					}
					try {
						Thread.sleep(1);
					} catch (InterruptedException ex1) {
					}
				}
			} catch (IOException ex) {
				TCPServerConstants.Log1.WriteLog(ex.toString());
				try {
					Thread.sleep(5000);
				} catch (Exception e) {
				}
				initSocket();
			}
		}
	}

	class CommunicationThread extends Thread {
		// private Selector selector;
		private SocketChannel UpClient;

		private int Sequence = 0;

		private ByteBuffer buffer = ByteBuffer.allocateDirect(5000); // 用于发送的缓冲

		public CommunicationThread(SocketChannel UpClient, Selector selector) {
			this.UpClient = UpClient;
			// this.selector = selector;
			// CommitThread ct = new CommitThread();
			// ct.start();
		}

		protected String AnalyzeCommandType(String Data) {
			try {
				String sMessageLen = Data.substring(1, 8);
				if (Data.length() != utils.HexStrToInt(sMessageLen) * 2) {
					return "0";
				} else {
					String sCommandType = Data.substring(14, 16);
					return (sCommandType);
				}
			} catch (Exception ex) {
			}
			return "0";
		}

		protected void processData(String sData, SocketChannel channel, byte[] Data) {
			if (channel == UpClient) {
				if (AnalyzeCommandType(sData).equals("02")) {
					String s = sData.substring(24, 26);
					if (s.equals("0A")) {
						// AckFlag = true;
					}
				}
			}
		}

		private void CommitToCommService(StructRecvData sr) {
			// AckFlag = false;
			String s = sr.DataContent.trim();
			int iLen = 12 + 2 + 50 + s.trim().length();
			byte[] Msg = new byte[iLen];
			byte[] MsgLength = new byte[4];
			MsgLength = utils.int2byte(iLen);
			Msg[0] = MsgLength[0];
			Msg[1] = MsgLength[1];
			Msg[2] = MsgLength[2];
			Msg[3] = MsgLength[3];
			// 消息总长度
			Msg[4] = 0;
			Msg[5] = 0;
			Msg[6] = 0;
			Msg[7] = 2;
			// 命令类型
			byte[] MsgSequence = new byte[4];
			MsgSequence = utils.int2byte(Sequence);
			Msg[8] = MsgSequence[0];
			Msg[9] = MsgSequence[1];
			Msg[10] = MsgSequence[2];
			Msg[11] = MsgSequence[3];
			// 消息流水号
			byte[] bContentLength = new byte[2];
			byte[] data = new byte[2];
			if (sr.DataLength < 15) { // 只有小于15的数据需要特殊处理，其他都可以由统一函数实现
				data[1] = 0;
				data[0] = (byte) sr.DataLength;
			} else {
				data = utils.str2bytes(Integer.toHexString(sr.DataLength));
			}
			for (int i = 0; i < data.length; i++) {
				bContentLength[i] = data[i];
			}

			if (sr.DataLength < 255) {
				Msg[12] = bContentLength[1];
				Msg[13] = bContentLength[0];
			} else {
				Msg[12] = bContentLength[0];
				Msg[13] = bContentLength[1];
			}
			// 内容总长度
			byte[] bContent = new byte[sr.DataLength];
			bContent = sr.DataContent.getBytes();
			for (int i = 0; i < sr.DataLength; i++) {
				Msg[14 + i] = bContent[i];
			}
			String MsgTarget = sr.FromIp + ":" + String.valueOf(sr.FromPort);
			int iMsgTarget = MsgTarget.trim().length();
			byte[] bMsgTarget = new byte[iMsgTarget];
			bMsgTarget = MsgTarget.getBytes();
			for (int i = 0; i < iMsgTarget; i++) {
				Msg[14 + i + sr.DataLength] = bMsgTarget[i];
			}
			try {
				String LocalAddr = sr.LocalIp + ":" + String.valueOf(sr.LocalPort);
				int iLocalAddr = LocalAddr.trim().length();
				byte[] bLocalAddr = new byte[iLocalAddr];
				bLocalAddr = LocalAddr.getBytes();
				for (int i = 0; i < iLocalAddr; i++) {
					Msg[39 + i + sr.DataLength] = bLocalAddr[i];
				}
			} catch (Exception ex) {
			}

			// 消息内容
			buffer.clear();
			buffer.put(Msg);
			buffer.flip();
			TCPServerConstants.Trc1.TraceLog(
					" C、SendToCommServer. SendMsg:" + utils.bytes2str(Msg) + " SocketChannel:" + UpClient.toString());
			try {
				UpClient.write(buffer);
			} catch (IOException ex1) {
				buffer.clear();
				// buffer.allocate(5000);
				buffer = ByteBuffer.allocate(5000);
				TCPServerConstants.Log1.WriteLog("Fep Link Error " + ex1.toString());
			} finally {
				buffer.clear();
				Sequence = Sequence + 1;
			}
		}

		@SuppressWarnings("unused")
		public void run() {
			while (true) {
				try {
					if (!UpRegisterFlag) {
						break;
					}
					for (int i = 0; i < TCPServerConstants.GlobalReceiveList.size(); i++) {
						StructRecvData sr = (StructRecvData) TCPServerConstants.GlobalReceiveList.get(i);
						if (true) {// 通信服务暂时不回确认消息
							CommitToCommService(sr);
							// TCPServerConstants.Log1
							// .WriteLog("CommitToCommService");
							TCPServerConstants.GlobalReceiveList.remove(sr);
						}
						break;
					}
					try {
						Thread.sleep(1);
					} catch (InterruptedException ex) {
					}
				} catch (Exception ex) {
					TCPServerConstants.Log1.WriteLog(" thread2" + ex.toString());
				}
			}
		}

	}
}

class MessageBody {
	public String TotalLength;

	public String CommandID;

	public String SeqID;
}

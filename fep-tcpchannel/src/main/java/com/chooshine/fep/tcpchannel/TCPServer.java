package com.chooshine.fep.tcpchannel;

import java.io.InputStream;
import java.util.Properties;
import java.io.FileInputStream;
import java.net.InetAddress;
import java.io.FileNotFoundException;
import java.util.Calendar;
import java.text.SimpleDateFormat;

/**
 * <p>
 * Title: TCP服务类
 * </p>
 *
 * <p>
 * Description: 实现控制TCP端口的数据接收、发送
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2005
 * </p>
 *
 * <p>
 * Company:
 * </p>
 *
 * @author
 * @version
 */
public class TCPServer extends Thread {
	private static TCPPortList[] GlobalTCPPortList; // 全局的TCP对象队列
	private static String CommService_Ip = ""; // 数据提交的前置机IP
	private static int CommService_Port = 0; // 数据提交的前置机端口
	private static int TCPPortCount = 0; // 总共TCP端口的数量
	private static int Connection_TimeOut = 0; // socket连接的超时时间
	private String Debug = "";

	public TCPServer(String DebugFlag) {
		this.Debug = DebugFlag;
		for (int i = 0; i < TCPPortCount; i++) {
			GlobalTCPPortList[i].PortObject = new TCPPort(Debug); // 创建具体的TCPPort对象
			if (GlobalTCPPortList[i].PortObject.CreateTCPPort(i, GlobalTCPPortList[i].LocalIp,
					GlobalTCPPortList[i].LocalPort, Connection_TimeOut) == 0) { // 只有创建TCP端口成功，即TCP服务启动成功才启动处理TCP接收数据的线程
				GlobalTCPPortList[i].PortObject.start();
			}
		}
	}

	@SuppressWarnings("unused")
	public void run() {
		while (true) { // 轮询需要发送的全局发送队列，通过某个TCP端口发送出去
			for (int i = 0; i < TCPServerConstants.GlobalSendList.size(); i++) {
				StructSendData sd = (StructSendData) TCPServerConstants.GlobalSendList.get(i);
				try {
					TCPServerConstants.Trc1
							.TraceLog(" 4、GetSendData. LocalIp:" + sd.LocalIp + " LocalPort:" + sd.LocalPort + " ToIp:"
									+ sd.ToIp + " ToPort:" + sd.ToPort + " DataContent:" + sd.DataContent);
					for (int j = 0; j < TCPPortCount; j++) {
						if ((sd.LocalIp.equals(GlobalTCPPortList[j].LocalIp))
								&& (sd.LocalPort == GlobalTCPPortList[j].LocalPort)) {
							// 根据发送使用的TCP端口的Ip和端口，查找使用那个TCP端口对象发送TCP消息
							TCPServerConstants.Trc1.TraceLog(" 5、GetSendTCPPort. LocalIp:"
									+ GlobalTCPPortList[j].LocalIp + " LocalPort:" + GlobalTCPPortList[j].LocalPort);
							GlobalTCPPortList[j].PortObject.SendTCPMessage(sd.ToIp, sd.ToPort, sd.DataContent);
							break;
						}
					}
					TCPServerConstants.GlobalSendList.remove(i);
				} catch (Exception ex1) {
				}
				break;
			}
			try {
				Thread.sleep(1);
			} catch (InterruptedException ex) {
			}
		}
	}

	private static void InitTCPChannelInfo() { // 从配置文件读取TCP的通道配置信息
		InputStream filecon = null;
		try {
			String file_name = "./TCPChannel.config";
			Properties prop = new Properties();
			filecon = new FileInputStream(file_name); // 读取配置文件中的内容
			prop.load(filecon);
			CommService_Ip = (String) prop.getProperty("CommService_Ip", InetAddress.getLocalHost().getHostAddress()); // 通讯服务所在的Ip，默认为本机IP
			CommService_Port = Integer.parseInt((String) prop.getProperty("CommService_Port", "5000")); // 通讯服务监听前置机的端口，默认为5000
			TCPPortCount = Integer.parseInt((String) prop.getProperty("TCPPortCount", "1")); // TCP端口数量，默认为1
			Connection_TimeOut = Integer.parseInt((String) prop.getProperty("Connection_TimeOut", "15")); // 连接超时时间，默认为15分钟
			GlobalTCPPortList = new TCPPortList[TCPPortCount]; // 根据端口数量创建对应的TCP信息队列
			for (int i = 0; i < TCPPortCount; i++) {
				try {
					GlobalTCPPortList[i] = new TCPPortList();
					String sIpInfo = "TCPPort" + i + 1 + "_IP";
					String sPortInfo = "TCPPort" + i + 1 + "_Port"; // 逐个读取各个通道的配置信息
					GlobalTCPPortList[i].LocalIp = (String) prop.getProperty(sIpInfo,
							InetAddress.getLocalHost().getHostAddress());
					GlobalTCPPortList[i].LocalPort = Integer.parseInt((String) prop.getProperty(sPortInfo, "1024"));
				} catch (NumberFormatException ex) {
				} catch (Exception e) {
					TCPPortCount = i;
					break; // 如果在获取通道信息的时候出现错误，则只创建当前个数的TCP对象，后面就不再处理
				}
			}
		} catch (FileNotFoundException fe) { // 配置文件没有找到，需要填入默认的信息
			try {
				CommService_Ip = InetAddress.getLocalHost().getHostAddress();
			} catch (Exception e) {
			}
			CommService_Port = 5000;
			TCPPortCount = 1; // 默认的信息和读取配置文件的默认值相同
			GlobalTCPPortList = new TCPPortList[TCPPortCount];
			for (int i = 0; i < TCPPortCount; i++) {
				try {
					GlobalTCPPortList[i] = new TCPPortList();
					GlobalTCPPortList[i].LocalIp = InetAddress.getLocalHost().getHostAddress();
					GlobalTCPPortList[i].LocalPort = 1024;
				} catch (NumberFormatException ex) {
				} catch (Exception e) {
					TCPPortCount = i;
				}
			}
		} catch (Exception e) {
			System.out.println(e.toString());
		}
	}

	public static void main(String[] args) {
		String Debug = "";
		if (args.length == 1) {
			Debug = args[0];
		}

		InitTCPChannelInfo(); // 获取TCP通道的信息
		TCPServer ts = new TCPServer(Debug); // 启动TCP服务类
		ts.start();
		CommunicateWithCommService cc = new CommunicateWithCommService(CommService_Ip, CommService_Port, Debug); // 创建和通讯服务通讯的线程对象
		cc.start();
		DebugInfoInputThread dt = new DebugInfoInputThread(cc, GlobalTCPPortList, TCPPortCount);
		dt.start(); // 创建修改调试、记录日志的标志线程
	}

	public static void PrintDebugMessage(String Msg, String DebugFlag) {
		if (DebugFlag.indexOf("D") != -1) {
			Calendar c = Calendar.getInstance();
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			System.out.println(formatter.format(c.getTime()) + " " + Msg);
		}
	}
}

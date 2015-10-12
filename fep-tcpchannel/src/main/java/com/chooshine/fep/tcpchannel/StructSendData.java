package com.chooshine.fep.tcpchannel;

public class StructSendData {
	public String ToIp = ""; // 目标IP地址
	public int ToPort = 0; // 目标端口号
	public String LocalIp = ""; // 本地Ip
	public int LocalPort = 0; // 本地端口
	public String DataContent = ""; // 数据内容
	public int DataLength = 0; // 数据长度

	public StructSendData() {
	}
}

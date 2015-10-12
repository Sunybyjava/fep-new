package com.chooshine.fep.tcpchannel;

public class StructRecvData {
	public String FromIp = ""; // 来源IP地址
	public int FromPort = 0; // 来源端口号
	public String LocalIp = ""; // 本地Ip
	public int LocalPort = 0; // 本地端口
	public String DataContent = ""; // 数据内容
	public int DataLength = 0; // 数据长度

	public StructRecvData() {
	}
}
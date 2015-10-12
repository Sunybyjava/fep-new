package com.chooshine.fep.tcpchannel;

import java.util.Collections;
import java.util.List;
import java.util.LinkedList;
import com.chooshine.fep.ConstAndTypeDefine.Log4Fep;
import com.chooshine.fep.ConstAndTypeDefine.Trc4Fep;

public class TCPServerConstants {
	public static List<StructRecvData> GlobalReceiveList; // 全局接收队列，即从主站接收的需要发送的帧
	public static List<StructSendData> GlobalSendList; // 全局发送队列，即从前置机接收的需要提交的数据
	public static String LogFileName; // 记录错误日志的文件完整路径和文件名
	public static Log4Fep Log1 = null;
	public static Trc4Fep Trc1 = null;

	static {
		init();
	}

	static void init() {
		GlobalReceiveList = Collections.synchronizedList(new LinkedList<StructRecvData>());
		GlobalSendList = Collections.synchronizedList(new LinkedList<StructSendData>());
		LogFileName = "TCP";
		Log1 = new Log4Fep(LogFileName);
		Trc1 = new Trc4Fep(LogFileName);
	}

}

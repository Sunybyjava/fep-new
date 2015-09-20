package com.chooshine.fep.newtcpchannel;

import com.chooshine.fep.ConstAndTypeDefine.Log4Fep;
import com.chooshine.fep.ConstAndTypeDefine.Trc4Fep;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class TCPChannelConstants {
	public static List GlobalReceiveList; //全局接收队列

	public static List GlobalSendList; //全局发送队列
	
	public static Log4Fep log;
	
	public static Trc4Fep trc;

	static {
		init();
	}

	static void init() {
		GlobalReceiveList = Collections.synchronizedList(new LinkedList());
		GlobalSendList = Collections.synchronizedList(new LinkedList());
		log = new Log4Fep("NewTCPChannel");
		trc = new Trc4Fep("NewTCPChannel");
	}
}

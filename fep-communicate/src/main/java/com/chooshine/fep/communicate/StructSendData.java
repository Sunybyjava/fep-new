package com.chooshine.fep.communicate;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: </p>
 *
 * @author 
 * @version 1.0
 */
public class StructSendData {
	  public String TerminalAddress = ""; //终端逻辑地址
	  public int TerminalCommunication = 0; //终端通讯类型
	  public int MessageLength = 0; //发送消息长度
	  public String MessageContent = ""; //发送消息内容
	  public int MessageType = 0; //消息格式类型
	  public int SendDirect = 0; //1 直接发送的标志（短信下发） 10 发送到加密码机 20发送到读卡器
	  public String MobileNo = ""; //短信下发的手机号码
	  public int ArithmeticNo = 0; //算法编号
	  public int CommandSeq = 0; //命令序号
	  public int YXJ=5;
	  public StructSendData() {
	  }
	}

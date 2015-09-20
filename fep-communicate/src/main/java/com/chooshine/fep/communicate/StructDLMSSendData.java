package com.chooshine.fep.communicate;
/**
 * <p>Title:StructDLMSSendData </p>
 *
 * <p>Description: The Struct of DLMS Sending Data</p>
 *
 * <p>Copyright: Copyright (c) 2010</p>
 *
 * <p>Company: Hexing</p>
 *
 * @author bfj
 * @version 1.0
 */

public class StructDLMSSendData {
	 public String TerminalAddress = ""; //Terminal Address
	  public int TerminalCommunication = 0; //Communication type
	  public int MessageLength = 0; //Message Length
	  public String MessageContent = ""; //Message Content
	  public int MessageType = 0; //Message Type
	  public int SendDirect = 0; //1 直接发送的标志（短信下发） 10 发送到加密码机 20发送到读卡器
	  public String MobileNo = ""; //Mobile No
	  public int ArithmeticNo = 0; //Arithmetic No
	  public int CommandSeq = 0; //Command Sequence
	  public int YXJ=5;
	  public int SecurityLevel = 0;//0:lowest 1:low 2:highest
	  public String ProVer = "";//Protocol Version
	  public String Password = "";
	  public String Random = "";//Random received from ammeter;
	  public int DataBlock = 0;//Data block number,if the number bigger than 0 then send command to read the DataBlock data.
	  public int DealStep = 0;//10:AARQ; 11:AARQ->StoC; 12:StoC; 13:AARQ/StoC->Send data; 20:Send Data; 21:send->receive;22:Have next; 30:Finish
	  public boolean bHaveSend;
	  public StructDLMSSendData() {
	  }
}

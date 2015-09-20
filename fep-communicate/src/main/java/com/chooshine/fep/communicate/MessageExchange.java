package com.chooshine.fep.communicate;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;
import java.sql.*;
import java.util.Hashtable;
import java.util.List;
import java.util.LinkedList;
import java.text.SimpleDateFormat;

import com.chooshine.fep.ConstAndTypeDefine.Glu_ConstDefine;
import com.chooshine.fep.ConstAndTypeDefine.Glu_DataAccess;
import com.chooshine.fep.FrameDataAreaExplain.DataSwitch;
import com.chooshine.fep.FrameDataAreaExplain.IFE_FrameDataAreaExplain;
import com.chooshine.fep.FrameDataAreaExplain.SFE_AlarmData;
import com.chooshine.fep.FrameDataAreaExplain.SFE_DataItem;
import com.chooshine.fep.FrameDataAreaExplain.SFE_DataListInfo;
import com.chooshine.fep.FrameDataAreaExplain.SFE_HistoryData;
import com.chooshine.fep.FrameDataAreaExplain.SFE_NormalData;
import com.chooshine.fep.FrameExplain.FE_FrameExplain;
import com.chooshine.fep.communicate.CommunicationScheduler.SwitchMPInfo;
import com.chooshine.fep.communicate.CommunicationScheduler.TerminalInfo;


/*
 * 20090924 此版本暂时不采用JMS队列，历史及异常数据直接保存数据库
import hexing.fep.communicate.AlarmDataJMSQueue;
import hexing.fep.communicate.HisDataJMSQueue;
*/

/**
 * <p>Title: 报文处理类</p>
 *
 * <p>Description: 负责和主站进行通讯，进行组帧后下发；或者直接下发短信；对于前置机收到的终端原始报文进行分发</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */

class TaskInfo {
    public int TaskNo;
    public int TaskType;
    public int MeasuredPoint;
    public List <String>CommandList = new LinkedList<String>();
  }

public class MessageExchange
    extends Thread {
  private int Port = 0; //本地监听的端口号
  private int MaxCount = 0; //本地服务的最大连接数
  private int TimeOut = 0; //连接的超时时间
  private SocketCommunicationBase sc = null; //socket服务底层类对象
  private Hashtable <SocketChannel,SocketLinkList>LinkList = null; //与实时通讯模块的链路队列
  private Hashtable <String,TerminalCommandNoList>TerminalCommandList = null; //终端逻辑地址和命令序号的维护队列
  private int Sequence = 0; //流水号
  /*
   * 20090924 此版本暂时不采用JMS队列，历史及异常数据直接保存数据库   
  private HisDataJMSQueue hd = null; //发送历史数据到JMS的对象
  private AlarmDataJMSQueue ad = null; //发送异常数据到JMS的对象
  */
  private String Debug = ""; //是否显示调试信息的标志
  //private int ToJMSCount = 0;
  private boolean AckBack = true; //通讯服务下发到报文交换的确认返回标志
  private byte[] TotalMessage;  
  //20090924 此版本暂时不采用JMS队列
  /*private int JMSConnectTimes=0; //重连JMSServer的次数*/
  
  private IFE_FrameDataAreaExplain FrameDataExplain;
  private Glu_DataAccess DataAccess_Ala; //异常数据保存数据库连接
  private Glu_DataAccess DataAccess_His; //任务数据保存数据库连接
  //private Hashtable TerminalProList = new Hashtable();
  private List<TaskInfo> DeviceTaskInfo = new LinkedList<TaskInfo>();
  private int DWDM = 0; //
  
  public MessageExchange() {
  }

  //重写构造函数，传入监听端口、最大连接数、连接超时时间参数
  public MessageExchange(int ListenPort, int MaxConnCount, int ConnTimeOut,
                         String Debug_Flag) {
    Port = ListenPort;
    MaxCount = MaxConnCount;
    TimeOut = ConnTimeOut;
    Debug = Debug_Flag;
    LinkList = new Hashtable<SocketChannel,SocketLinkList>(MaxCount); //用于维护主站链路

    TerminalCommandList = new Hashtable<String,TerminalCommandNoList>(50000);
    
    sc = new SocketCommunicationBase(); //创建TCP服务，通过基类实现
    try {
      sc.init(Port); //Socket服务端初始化
    }
    catch (Exception ex) {
    	CommunicationServerConstants.Log1.WriteLog(
          "MessageExchange:Init Socket Server Port error,error is " +
          ex.toString());
    }
    
    /*
     * 20090924 此版本暂时不采用JMS队列，历史及异常数据直接保存数据库
    ad = new AlarmDataJMSQueue();
    hd = new HisDataJMSQueue();
    */
    
    //	创建数据库链接
	DataAccess_Ala = new Glu_DataAccess("");
	DataAccess_His = new Glu_DataAccess("");
	if (DataAccess_Ala.LogIn(20) == false) {
		CommunicationServerConstants.Log1.WriteLog("Database connection failed(Save the Alarm data)!");
    }
	if (DataAccess_His.LogIn(10) == false) {
		CommunicationServerConstants.Log1.WriteLog("Database connection failed(Save the history data)!");
    }
	
    //取单位代码
	try{
		DWDM = DataAccess_His.GetDWDM();
	}
	catch (Exception ex) {
		CommunicationServerConstants.Log1.WriteLog("Failed to get DWDM!");
		
	}    
    
    //	创建规约解释对象
    try {
    	FrameDataExplain = new IFE_FrameDataAreaExplain("");
	    CommunicationServerConstants.Log1.WriteLog("Succeed to create FrameDataAreaExplain object");
    }
    catch (Exception ex2) {
    	CommunicationServerConstants.Log1.WriteLog("Failed to create FrameDataAreaExplain object:" + ex2.toString());
    }
    //初始化任务对照队列
	InitDeviceAndTaskList();
    
  }

  public String GetDebugFlag() { //获取当前的运行标志
    return this.Debug;
  }

  public void ChangeDebugFlag(String DebugFlag) { //修改运行标志
    this.Debug = DebugFlag;
  }

  //将召测返回的数据提交制定的召测对象，这里需要根据不同的命令类型分别进行组包
  private void SendBackToSourceLink(SocketChannel channel,
                                    StructReceiveData rd, int AppID) {
    ByteBuffer buffer = ByteBuffer.allocateDirect(5000); //接收数据的缓存
    try {
      switch (rd.DataType) {
        //分发召测返回的历史数据
        case (Glu_ConstDefine.SJLX_LSSJZC): {
          int TotalMessageLength = 12 + 4 + 20 + 5 + 15 + 2 + rd.FrameLength +
              4;
          TotalMessage = new byte[TotalMessageLength];
          byte[] MsgLength = new byte[4];
          MsgLength = utils.int2byte(TotalMessageLength);
          TotalMessage[0] = MsgLength[0];
          TotalMessage[1] = MsgLength[1];
          TotalMessage[2] = MsgLength[2];
          TotalMessage[3] = MsgLength[3];

          TotalMessage[4] = 0;
          TotalMessage[5] = 0;
          TotalMessage[6] = 0;
          TotalMessage[7] = 8;

          byte[] MsgSequence = new byte[4];
          MsgSequence = utils.int2byte(Sequence);
          TotalMessage[8] = MsgSequence[0];
          TotalMessage[9] = MsgSequence[1];
          TotalMessage[10] = MsgSequence[2];
          TotalMessage[11] = MsgSequence[3];

          byte[] bAppID = new byte[4];
          bAppID = utils.int2byte(AppID);
          TotalMessage[12] = bAppID[0];
          TotalMessage[13] = bAppID[1];
          TotalMessage[14] = bAppID[2];
          TotalMessage[15] = bAppID[3];

          byte[] bTemp = new byte[20];
          byte[] bZDLJDZ = rd.TerminalLogicAdd.getBytes();
          for (int i = 0; i < bZDLJDZ.length; i++) {
            bTemp[i] = bZDLJDZ[i];
          }
          for (int i = 0; i < 20; i++) {
            TotalMessage[16 + i] = bTemp[i];
          }

          byte[] bGYH = new byte[1];
          bGYH = utils.str2bytes(Integer.toHexString(rd.TerminalProtocal)); //Integer.toHexString(80).getBytes();
          TotalMessage[36] = bGYH[0];
          TotalMessage[37] = 0;
          TotalMessage[38] = 10;
          TotalMessage[39] = 0;
          TotalMessage[40] = 0;

          for (int i = 0; i < 15; i++) {
            TotalMessage[41 + i] = '0';
          } //YCBM、YCFSSJ为空

          bTemp = new byte[2];
          byte[] bSJQCD = new byte[2];
          if (rd.FrameLength < 15) { //只有小于15的数据需要特殊处理，其他都可以由统一函数实现
            bSJQCD[1] = 0;
            bSJQCD[0] = (byte) rd.FrameLength;
          }
          else {
            bSJQCD = utils.str2bytes(Integer.toHexString(rd.FrameLength));
          }

          for (int i = 0; i < bSJQCD.length; i++) {
            bTemp[i] = bSJQCD[i];
          }
          if (rd.FrameLength < 256) {
            TotalMessage[56] = bTemp[1];
            TotalMessage[57] = bTemp[0];
          }
          else {
            TotalMessage[56] = bTemp[0];
            TotalMessage[57] = bTemp[1];
          }

          byte[] bSJQNR = new byte[rd.FrameLength];
          bSJQNR = rd.FrameData.trim().getBytes();
          for (int i = 0; i < rd.FrameLength; i++) {
            TotalMessage[58 + i] = bSJQNR[i];
          }

          bTemp = new byte[2];
          byte[] bGNM = rd.FunctionCode.getBytes();
          for (int i = 0; i < bGNM.length; i++) {
            bTemp[i] = bGNM[i];
          }
          for (int i = 0; i < 2; i++) {
            TotalMessage[58 + i + rd.FrameLength] = bTemp[i];
          }

          bTemp = new byte[2];
          byte[] bKZM = rd.ControlCode.getBytes();
          for (int i = 0; i < bKZM.length; i++) {
            bTemp[i] = bKZM[i];
          }
          for (int i = 0; i < 2; i++) {
            TotalMessage[60 + i + rd.FrameLength] = bTemp[i];
          }

          try {
            buffer.clear();
            buffer.put(TotalMessage);
            buffer.flip();
       //     CommunicationServerConstants.Trc1.TraceLog(
       //         "23 HisDataMsg to masterstation HostAddress:" +
       //         channel.socket().getInetAddress().getHostAddress());
            channel.write(buffer);
            AckBack = false;
            buffer.clear();
          }
          catch (IOException ex) {
        	  CommunicationServerConstants.Log1.WriteLog("Write Back HisData Error!");
          }
          finally {            
          }
          Sequence = Sequence + 1;
          break;
        }
        //分发召测返回的异常数据
        case (Glu_ConstDefine.SJLX_YCSJZC): {
          int TotalMessageLength = 12 + 4 + 20 + 3 + 10 + 15 + 2 +
              rd.FrameLength + 4;
          TotalMessage = new byte[TotalMessageLength];
          byte[] MsgLength = new byte[4];
          MsgLength = utils.int2byte(TotalMessageLength);
          TotalMessage[0] = MsgLength[0];
          TotalMessage[1] = MsgLength[1];
          TotalMessage[2] = MsgLength[2];
          TotalMessage[3] = MsgLength[3];

          TotalMessage[4] = 0;
          TotalMessage[5] = 0;
          TotalMessage[6] = 0;
          TotalMessage[7] = 9;

          byte[] MsgSequence = new byte[4];
          MsgSequence = utils.int2byte(Sequence);
          TotalMessage[8] = MsgSequence[0];
          TotalMessage[9] = MsgSequence[1];
          TotalMessage[10] = MsgSequence[2];
          TotalMessage[11] = MsgSequence[3];

          byte[] bAppID = new byte[4];
          bAppID = utils.int2byte(AppID);
          TotalMessage[12] = bAppID[0];
          TotalMessage[13] = bAppID[1];
          TotalMessage[14] = bAppID[2];
          TotalMessage[15] = bAppID[3];

          byte[] bTemp = new byte[20];
          byte[] bZDLJDZ = rd.TerminalLogicAdd.getBytes();
          for (int i = 0; i < bZDLJDZ.length; i++) {
            bTemp[i] = bZDLJDZ[i];
          }
          for (int i = 0; i < 20; i++) {
            TotalMessage[16 + i] = bTemp[i];
          }

          byte[] bGYH = new byte[1];
          bGYH = utils.str2bytes(Integer.toHexString(rd.TerminalProtocal)); //Integer.toHexString(80).getBytes();
          TotalMessage[36] = bGYH[0];
          TotalMessage[37] = 0;
          TotalMessage[38] = 10;

          for (int i = 0; i < 25; i++) {
            TotalMessage[39 + i] = '0';
          } //YCBM、YCFSSJ为空

          bTemp = new byte[2];
          byte[] bSJQCD = new byte[2];
          if (rd.FrameLength < 15) { //只有小于15的数据需要特殊处理，其他都可以由统一函数实现
            bSJQCD[1] = 0;
            bSJQCD[0] = (byte) rd.FrameLength;
          }
          else {
            bSJQCD = utils.str2bytes(Integer.toHexString(rd.FrameLength));
          }

          for (int i = 0; i < bSJQCD.length; i++) {
            bTemp[i] = bSJQCD[i];
          }

          if (rd.FrameLength < 256) {
            TotalMessage[64] = bTemp[1];
            TotalMessage[65] = bTemp[0];
          }
          else {
            TotalMessage[64] = bTemp[0];
            TotalMessage[65] = bTemp[1];
          }

          byte[] bSJQNR = new byte[rd.FrameLength];
          bSJQNR = rd.FrameData.trim().getBytes();
          for (int i = 0; i < rd.FrameLength; i++) {
            TotalMessage[66 + i] = bSJQNR[i];
          }

          bTemp = new byte[2];
          byte[] bGNM = rd.FunctionCode.getBytes();
          for (int i = 0; i < bGNM.length; i++) {
            bTemp[i] = bGNM[i];
          }
          for (int i = 0; i < 2; i++) {
            TotalMessage[66 + i + rd.FrameLength] = bTemp[i];
          }

          bTemp = new byte[2];
          byte[] bKZM = rd.ControlCode.getBytes();
          for (int i = 0; i < bKZM.length; i++) {
            bTemp[i] = bKZM[i];
          }
          for (int i = 0; i < 2; i++) {
            TotalMessage[68 + i + rd.FrameLength] = bTemp[i];
          }

          try {
            buffer.clear();
            buffer.put(TotalMessage);
            buffer.flip();
    //        CommunicationServerConstants.Trc1.TraceLog("23、SendBackAlarmMsg Host:" +
     //                               channel.socket().getInetAddress().
     //                               getHostAddress());
            channel.write(buffer);
            AckBack = false;
          }
          catch (IOException ex) {
        	  CommunicationServerConstants.Log1.WriteLog("Write Back AlarmData Error!");
          }
          finally {
          }
          Sequence = Sequence + 1;
          break;
        }
        //分发普通数据、召测返回数据
        case (Glu_ConstDefine.SJLX_SZFH):
        case (Glu_ConstDefine.SJLX_PTSJ): {
          int TotalMessageLength = 12 + 4 + 20 + 4 + 2 + rd.FrameLength + 4;
          TotalMessage = new byte[TotalMessageLength];
          byte[] MsgLength = new byte[4];
          MsgLength = utils.int2byte(TotalMessageLength);
          TotalMessage[0] = MsgLength[0];
          TotalMessage[1] = MsgLength[1];
          TotalMessage[2] = MsgLength[2];
          TotalMessage[3] = MsgLength[3];

          TotalMessage[4] = 0;
          TotalMessage[5] = 0;
          TotalMessage[6] = 0;
          TotalMessage[7] = 7;

          byte[] MsgSequence = new byte[4];
          MsgSequence = utils.int2byte(Sequence);
          TotalMessage[8] = MsgSequence[0];
          TotalMessage[9] = MsgSequence[1];
          TotalMessage[10] = MsgSequence[2];
          TotalMessage[11] = MsgSequence[3];

          byte[] bAppID = new byte[4];
          bAppID = utils.int2byte(AppID);
          TotalMessage[12] = bAppID[0];
          TotalMessage[13] = bAppID[1];
          TotalMessage[14] = bAppID[2];
          TotalMessage[15] = bAppID[3];

          byte[] bTemp = new byte[20];
          byte[] bZDLJDZ = rd.TerminalLogicAdd.getBytes();
          for (int i = 0; i < bZDLJDZ.length; i++) {
            bTemp[i] = bZDLJDZ[i];
          }
          for (int i = 0; i < 20; i++) {
            TotalMessage[16 + i] = bTemp[i];
          }

          byte[] bGYH = new byte[1];
          bGYH = utils.str2bytes(Integer.toHexString(rd.TerminalProtocal)); //Integer.toHexString(80).getBytes();
          TotalMessage[36] = bGYH[0];
          TotalMessage[37] = 0;
          TotalMessage[38] = 10;

          byte[] bSJLX = new byte[1];
          bSJLX = utils.str2bytes(Integer.toHexString(rd.DataType));
          TotalMessage[39] = bSJLX[0];

          bTemp = new byte[2];
          byte[] bSJQCD = new byte[2];
          if (rd.FrameLength < 15) { //只有小于15的数据需要特殊处理，其他都可以由统一函数实现
            bSJQCD[1] = 0;
            bSJQCD[0] = (byte) rd.FrameLength;
          }
          else {
            bSJQCD = utils.str2bytes(Integer.toHexString(rd.FrameLength));
          }

          for (int i = 0; i < bSJQCD.length; i++) {
            bTemp[i] = bSJQCD[i];
          }
          if (rd.FrameLength < 256) {
            TotalMessage[40] = bTemp[1];
            TotalMessage[41] = bTemp[0];
          }
          else {
            TotalMessage[40] = bTemp[0];
            TotalMessage[41] = bTemp[1];
          }
          byte[] bSJQNR = new byte[rd.FrameLength];
          bSJQNR = rd.FrameData.trim().getBytes();
          for (int i = 0; i < rd.FrameLength; i++) {
            TotalMessage[42 + i] = bSJQNR[i];
          }

          bTemp = new byte[2];
          byte[] bGNM = rd.FunctionCode.getBytes();
          for (int i = 0; i < bGNM.length; i++) {
            bTemp[i] = bGNM[i];
          }
          for (int i = 0; i < 2; i++) {
            TotalMessage[42 + i + rd.FrameLength] = bTemp[i];
          }

          bTemp = new byte[2];
          byte[] bKZM = rd.ControlCode.getBytes();
          for (int i = 0; i < bKZM.length; i++) {
            bTemp[i] = bKZM[i];
          }
          for (int i = 0; i < 2; i++) {
            TotalMessage[44 + i + rd.FrameLength] = bTemp[i];
          }

          try {
            buffer.clear();
            buffer.put(TotalMessage);
            buffer.flip();
     //       CommunicationServerConstants.Trc1.TraceLog("23、SendBackNormalMsg Host:" +
     //                               channel.socket().getInetAddress().
     //                               getHostAddress());
            channel.write(buffer);
            Thread.sleep(1);
            AckBack = false;
          }
          catch (Exception ex) {
        	  CommunicationServerConstants.Log1.WriteLog("Write Back NormalData Error!");
          }
          finally {
          }
          Sequence = Sequence + 1;
          break;
        }
      }
    }
    catch (Exception ex2) {
    	CommunicationServerConstants.Log1.WriteLog(
          "MessageExchange:SendBackToSourceLink() error,error is " +
          ex2.toString());
    }
  }
  
  
//将召测返回的数据提交制定的召测对象，这里需要根据不同的命令类型分别进行组包
  private void SendBackToPreSourceLink(SocketChannel channel,
                                    StructReceiveData rd, int AppID) {
    ByteBuffer buffer = ByteBuffer.allocateDirect(5000); //接收数据的缓存
    try {
      int TotalMessageLength = 12 + 4 + 20 + 2 + rd.FrameLength ;
      TotalMessage = new byte[TotalMessageLength];
      
      //消息总长度
      byte[] MsgLength = new byte[4];
      MsgLength = utils.int2byte(TotalMessageLength);
      TotalMessage[0] = MsgLength[0];
      TotalMessage[1] = MsgLength[1];
      TotalMessage[2] = MsgLength[2];
      TotalMessage[3] = MsgLength[3];

      //命令类型
      int iCommandType = 0;
      if (rd.DataType == 258) {
    	  iCommandType = 264; //加密机数据
      }
      else if (rd.DataType == 261){
    	  iCommandType = 265; //读卡器数据
      }      
      
      byte[] bCommandType = new byte[4];
      bCommandType = utils.int2byte(iCommandType);
      TotalMessage[4] = bCommandType[0];
      TotalMessage[5] = bCommandType[1];
      TotalMessage[6] = bCommandType[2];
      TotalMessage[7] = bCommandType[3];
      
      //流水号
      byte[] MsgSequence = new byte[4];
      MsgSequence = utils.int2byte(Sequence);
      TotalMessage[8] = MsgSequence[0];
      TotalMessage[9] = MsgSequence[1];
      TotalMessage[10] = MsgSequence[2];
      TotalMessage[11] = MsgSequence[3];
      
      //应用ID
      byte[] bAppID = new byte[4];
      bAppID = utils.int2byte(AppID);
      TotalMessage[12] = bAppID[0];
      TotalMessage[13] = bAppID[1];
      TotalMessage[14] = bAppID[2];
      TotalMessage[15] = bAppID[3];

      //用户户号 
      byte[] bTemp = new byte[20];
      byte[] bZDLJDZ = rd.TerminalLogicAdd.getBytes();
      for (int i = 0; i < bZDLJDZ.length; i++) {
        bTemp[i] = bZDLJDZ[i];
      }
      for (int i = 0; i < 20; i++) {
        TotalMessage[16 + i] = bTemp[i];
      }

      //消息内容长度
      bTemp = new byte[2];
      byte[] bSJQCD = new byte[2];
      if (rd.FrameLength < 15) { //只有小于15的数据需要特殊处理，其他都可以由统一函数实现
        bSJQCD[1] = 0;
        bSJQCD[0] = (byte) rd.FrameLength;
      }
      else {
        bSJQCD = utils.str2bytes(Integer.toHexString(rd.FrameLength));
      }
      for (int i = 0; i < bSJQCD.length; i++) {
        bTemp[i] = bSJQCD[i];
      }
      if (rd.FrameLength < 256) {
        TotalMessage[36] = bTemp[1];
        TotalMessage[37] = bTemp[0];
      }
      else {
        TotalMessage[36] = bTemp[0];
        TotalMessage[37] = bTemp[1];
      }
      
      //消息内容
      byte[] bSJQNR = new byte[rd.FrameLength];
      bSJQNR = rd.FrameData.trim().getBytes();
      for (int i = 0; i < rd.FrameLength; i++) {
        TotalMessage[38 + i] = bSJQNR[i];
      }

      try {
        buffer.clear();
        buffer.put(TotalMessage);
        buffer.flip();
  //      CommunicationServerConstants.Trc1.TraceLog("23、SendBackNormalMsg Host:" +
  //                              channel.socket().getInetAddress().
  //                              getHostAddress()+" FrameLength:"+rd.FrameLength+" FrameData:"+rd.FrameData.trim());
        channel.write(buffer);
        Thread.sleep(1);
        AckBack = false;
      }
      catch (Exception ex) {
    	  CommunicationServerConstants.Log1.WriteLog("Write Back NormalData Error!");
      }
      finally {
      }
      Sequence = Sequence + 1;
      
    }
    catch (Exception ex2) {
    	CommunicationServerConstants.Log1.WriteLog(
          "MessageExchange:SendBackToSourceLink() error,error is " +
          ex2.toString());
    }
  }

  //更新实时召测链路的最后通讯时间，用于判断链路超时、有效
  private void UpdateLastCommDate(SocketChannel channel) {
    try {
      SocketLinkList sc = (SocketLinkList) LinkList.get(channel);
      if (sc != null) {
        sc.SetLastCommDate(Calendar.getInstance());
        LinkList.remove(channel);
        LinkList.put(channel, sc);
      }
    }
    catch (Exception ex) {
    	CommunicationServerConstants.Log1.WriteLog(
          "MessageExchange:UpdateLastCommDate() error,error is " +
          ex.toString());
    }
  }

  //消息确认函数，组成确认报文后直接发送即可
  private void AckMessage(String CommandID, String sData,
                          SocketChannel channel, byte[] RealData) {
    ByteBuffer buffer = ByteBuffer.allocateDirect(5000); //用于发送的缓冲
    if (sData.length() < 16) {
    }
    else {
      try {
        String ss = "0000000D08" + CommandID + sData.substring(16, 24) +
            "0A";
        buffer.clear();
        buffer.put(utils.str2bytes(ss));
        buffer.flip();
        try {
          channel.write(buffer);
  //        CommunicationServerConstants.Trc1.TraceLog("02、Respond Receive Data to MasterStation");
        }
        catch (IOException ex) {
  //      	CommunicationServerConstants.Trc1.TraceLog("AckMessage Error,Error Content is " +
  //                                ex.toString());
        }
        buffer.clear();
      }
      catch (Exception ex1) {
    	  CommunicationServerConstants.Log1.WriteLog("MessageExchange:AckMessage() Error,error is " +
                    ex1.toString());
      }
    }
  }

  //更新召测链路信息，记录召测的终端逻辑地址、命令序号、召测命令类型、应用ID等，用来数据返回后进行比较匹配
 // @SuppressWarnings(value="unchecked")
  private void UpdateLinkSocketInfo(String ZDLJDZ, int GYH, int MLXH,
                                    int FrameCount,
                                    SocketChannel channel, int TaskNo,
                                    int TaskCount, int AlarmCount, int AppID,
                                    int MLLX) {
    //if (ZDLJDZ.length() < 8) { //对于终端逻辑地址不足8的命令，不做记录
    //}
    //else {
      try {
        SocketLinkList sc = (SocketLinkList) LinkList.get(channel);
        if (sc == null) {
        }
        else {
          List lTemp = sc.GetTerminalCorreCommandSeqList();
          if (lTemp == null) {
            lTemp = new LinkedList();
            TerminalAddCorreCommandSeq ta = new TerminalAddCorreCommandSeq();
            ta.ApplicationID = AppID;
            ta.TerminalAddress = ZDLJDZ.toCharArray();
            ta.TerminalProtocol = GYH;
            ta.CommandCount = FrameCount;
            ta.CommandSeq = new int[ta.CommandCount];
            ta.CommandSeq[0] = MLXH;
            ta.CommandType = MLLX;
            ta.LastUpdateTime = Calendar.getInstance();
            lTemp.add(ta);
          }
          else {
            TerminalAddCorreCommandSeq ta = new TerminalAddCorreCommandSeq();
            ta.ApplicationID = AppID;
            ta.TerminalAddress = ZDLJDZ.toCharArray();
            ta.TerminalProtocol = GYH;
            ta.CommandCount = FrameCount;
            ta.CommandSeq = new int[ta.CommandCount];
            ta.CommandSeq[0] = MLXH;
            ta.CommandType = MLLX;
            ta.LastUpdateTime = Calendar.getInstance();
            lTemp.add(ta);
          }
          sc.SetTerminalCorreCommandSeqList(lTemp);
          sc.SetTaskNo(TaskNo);
          sc.SetAlarmCount(AlarmCount);
          sc.SetTaskCount(TaskCount);
      //    CommunicationServerConstants.Trc1.TraceLog(
      //       "04、AddDataToTerminalSocketInfoList ListSize:" +
      //        sc.GetTerminalCorreCommandSeqList().size());
          LinkList.remove(channel);
          LinkList.put(channel, sc);
        }
      }
      catch (Exception ex) {
    	  CommunicationServerConstants.Log1.WriteLog(
            "MessageExchange:UpdateLinkSocketInfo() error,error is " +
            ex.toString());
      }
    }
  //}

  private void AddFrameToSendList(StructSendData SendData, int TXCS, int YXJ) {
    try {
      if (TXCS == 20) { //通讯类型是230M则，提交给230M专用发送队列
        SendData.SendDirect = 1;
        SendData.YXJ = YXJ; //230M优先级传给通道程序
        CommunicationServerConstants.Global230MSendList.add(SendData);
        //队列长度超过5000条，就删除头上的记录，控制队列长度
        if (CommunicationServerConstants.Global230MSendList.size() > 5000) {
          CommunicationServerConstants.Global230MSendList.remove(0);
        }
      }
      else {
        CommunicationServerConstants.GlobalSendList.add(SendData);
        //队列长度超过5000条，就删除头上的记录，控制队列长度
        if (CommunicationServerConstants.GlobalSendList.size() > 5000) {
          CommunicationServerConstants.GlobalSendList.remove(0);
        }
      }
  //    CommunicationServerConstants.Trc1.TraceLog("06、AddFrameToSendList ListSize " +
  //                            CommunicationServerConstants.GlobalSendList.size());
    }
    catch (Exception ex) {
    	CommunicationServerConstants.Log1.WriteLog(
          "MessageExchange:AddFrameToSendList() error,error is " +
          ex.toString());
    }
  }  

  //Build fram according to the DataArea, prepare for send it.
  private void FrameDLMSUpNormalFrameAndAddToList(String ZDLJDZ, int GYH,int SJQCD,
                                              String GNM, String SJQNR,
                                              int AppID, int TXCS, int ZNXZ,
                                              int MLXH, int YXJ, int SFBH,
                                              String SecLevel, String ProVer, String Password) {
    try {
      FE_FrameExplain FrameExplain;
      FrameExplain = new FE_FrameExplain();
      char[] FrameInfo = new char[1500];
      try {
    	  FrameInfo = FrameExplain.IFE_BuildFrame(ZDLJDZ.toCharArray(), GYH,
    			  1, MLXH, GNM.toCharArray(), SJQNR.toCharArray());
      }
      catch (Exception ex) {
    //	  CommunicationServerConstants.Trc1.TraceLog("55、BuildFrame Error!");
    	  CommunicationServerConstants.Log1.WriteLog(
    			  "MessageExchange:BuildFrame error,error is " +
    			  ex.toString());
    	  return;
      }
      if (FrameInfo == null) {
    	  CommunicationServerConstants.Trc1.TraceLog("55、can not build Frame!");
    	  return;
      }
   //   CommunicationServerConstants.Trc1.TraceLog("05、BuildFrame is " +
   //                           new String(FrameInfo).trim());
      StructDLMSSendData ss = new StructDLMSSendData();
      ss.CommandSeq = 0;
      ss.DataBlock = 0;
     // ss.DealStep = 10;
      ss.DealStep = 20;
      ss.Password = Password;
      ss.ProVer = ProVer;
      ss.Random = "";
      ss.SecurityLevel = Integer.parseInt(SecLevel);      
      ss.MessageContent = new String(FrameInfo).trim();
      ss.MessageLength = new String(FrameInfo).trim().length();
      ss.MessageType = 30; 
      ss.MobileNo = "";
      ss.SendDirect = 0;
      ss.YXJ = 0;
      ss.TerminalCommunication = TXCS;
      ss.ArithmeticNo = SFBH;
      ss.TerminalAddress = ZDLJDZ;
      
      CommunicationServerConstants.GlobalDLMSSendList.add(ss);
	  //If more than 5000,delete the first one. 
	  if (CommunicationServerConstants.GlobalDLMSSendList.size() > 5000) {
		  CommunicationServerConstants.GlobalDLMSSendList.remove(0);
	  }
	  int iSize = CommunicationServerConstants.GlobalDLMSSendList.size();
//	  CommunicationServerConstants.Trc1.TraceLog("06、AddFrameToDLMSSendList ListSize " + iSize);            
    }
    catch (Exception ex1) {
    	CommunicationServerConstants.Log1.WriteLog(
          "MessageExchange:FrameDLMSUpNormalFrameAndAddToList() error,error is " +
          ex1.toString());
    }
  }
  
//根据数据区组成相应的帧，准备下发
  private void FrameUpNormalFrameAndAddToList(String ZDLJDZ, int GYH,
                                              int CLDXH, int CLDLX, int SJQCD,
                                              String GNM, String SJQNR,
                                              int AppID, int TXCS, int ZNXZ,
                                              int MLXH, int YXJ, int SFBH) {
    try {
      FE_FrameExplain FrameExplain;
      FrameExplain = new FE_FrameExplain();
      char[] FrameInfo = new char[1500];
      try {
        FrameInfo = FrameExplain.IFE_BuildFrame(ZDLJDZ.toCharArray(), GYH,
                                                1, MLXH, GNM.toCharArray(),
                                                SJQNR.toCharArray());
      }
      catch (Exception ex) {
    //	  CommunicationServerConstants.Trc1.TraceLog("55、BuildFrame Error!");
    	  CommunicationServerConstants.Log1.WriteLog(
            "MessageExchange:BuildFrame error,error is " +
            ex.toString());
        return;
      }
      if (FrameInfo == null) {
    //	  CommunicationServerConstants.Trc1.TraceLog("55、can not build Frame!");
    	  return;
      }
   //   CommunicationServerConstants.Trc1.TraceLog("05、BuildFrame is " +
    //                          new String(FrameInfo).trim());
      StructSendData ss = new StructSendData();
      ss.MessageContent = new String(FrameInfo).trim();
      ss.MessageLength = new String(FrameInfo).trim().length();
      ss.MessageType = 30; //指定短信类型，如果需要用短信发送的话，就需要指定消息类型，否则发送类型不符
      ss.MobileNo = "";
      ss.SendDirect = 0;
      ss.TerminalCommunication = TXCS;
      ss.ArithmeticNo = SFBH;
      //这里只有终端逻辑地址需要根据规约号进行处理，其他部分都是相同的实现
      switch (GYH) {
        case Glu_ConstDefine.GY_ZD_HS: //杭州水表厂水表规约
        case Glu_ConstDefine.GY_ZD_ZHEJIANG: 
        case Glu_ConstDefine.GY_ZD_ZJZB0404: 
        case Glu_ConstDefine.GY_ZD_HEXING:
        case Glu_ConstDefine.GY_ZD_DLMS:
        {
          ss.TerminalAddress = ZDLJDZ;
          break;
        }
        case Glu_ConstDefine.GY_ZD_QUANGUO:
        case Glu_ConstDefine.GY_ZD_698:
        case Glu_ConstDefine.GY_ZD_IHD:
        {
          ss.TerminalAddress = ZDLJDZ.substring(0, 8);
          break;
        }
      }
      AddFrameToSendList(ss, TXCS, YXJ); //将组好的帧加入到发送队列中
            
    }
    catch (Exception ex1) {
    	CommunicationServerConstants.Log1.WriteLog(
          "MessageExchange:FrameUpNormalFrameAndAddToList() error,error is " +
          ex1.toString());
    }
  }

//根据数据区组成相应的帧，准备下发
  private void BuildProPayFrameAddToList(String ZDLJDZ, int GYH,
                                              int CLDXH, int CLDLX, int SJQCD,
                                              String GNM, String SJQNR,
                                              int AppID, int TXFS,int iFSFS, int ZNXZ,
                                              int MLXH,String DYIP) {
    try {
    	CommunicationServerConstants.Trc1.TraceLog("05、BuildFrame is " + SJQNR);
    	StructSendData ss = new StructSendData();
    	ss.MessageContent = SJQNR;
    	ss.MessageLength = SJQNR.length();
    	ss.MessageType = 0; 
    	ss.MobileNo = DYIP; //此处填调用IP，如调用读卡器或加密机IP
    	ss.SendDirect = iFSFS; //10 加密机 20读卡器
    	ss.TerminalCommunication = TXFS;
    	ss.ArithmeticNo = 0;
    	ss.TerminalAddress = ZDLJDZ;
    	ss.CommandSeq = MLXH;
    	AddFrameToSendList(ss, TXFS, 0); //将组好的帧加入到发送队列中
    }
    catch (Exception ex1) {
    	CommunicationServerConstants.Log1.WriteLog(
          "MessageExchange:FrameUpNormalFrameAndAddToList() error,error is " +
          ex1.toString());
    }
  }
  
//根据终端逻辑地址得到对应的命令序号，需要根据规约号不同而有不同的命令序号最大的限制
  private int GetTerminalCommandNo(String ZDLJDZ, int GYH) {
    try {
      TerminalCommandNoList cn = (TerminalCommandNoList) TerminalCommandList.
          get(ZDLJDZ);
      if (cn != null) {
        if (cn.TerminalAddress.equals(ZDLJDZ)) {
          cn.CommandNo = cn.CommandNo + 1;
          switch (GYH) {
            case Glu_ConstDefine.GY_ZD_HS:
            case Glu_ConstDefine.GY_ZD_HEXING:
            case Glu_ConstDefine.GY_ZD_DLMS:
            {
            	cn.CommandNo = 0; //水表命令不带命令序号，赋常量0
            }
            case Glu_ConstDefine.GY_ZD_ZHEJIANG: 
            case Glu_ConstDefine.GY_ZD_ZJZB0404:
            {
              if (cn.CommandNo >= 127) {
                cn.CommandNo = 0;
              }
              break;
            }
            case Glu_ConstDefine.GY_ZD_QUANGUO:
            case Glu_ConstDefine.GY_ZD_698:
            case Glu_ConstDefine.GY_ZD_IHD: {
              if (cn.CommandNo >= 15) {
                cn.CommandNo = 0;
              }
              break;
            }
            case Glu_ConstDefine.GY_PrePayApp:
            {
            	if (cn.CommandNo >= 100000000) {
                    cn.CommandNo = 0;
                  }
                  break;
            }
          }
          TerminalCommandList.remove(ZDLJDZ);
          TerminalCommandList.put(ZDLJDZ, cn); 
          return cn.CommandNo;
        }
      }
      else { //在队列中找不到对应的终端信息，则新增终端逻辑地址和命令序号的对应关系
        cn = new TerminalCommandNoList();
        cn.TerminalAddress = ZDLJDZ;
        cn.CommandNo = 0;
        TerminalCommandList.put(ZDLJDZ, cn);
        return cn.CommandNo;
      }
      return 0;
    }
    catch (Exception ex) {
    	CommunicationServerConstants.Log1.WriteLog(
          "MessageExchange:GetTerminalCommandNo() error,error is " +
          ex.toString());
    	return 0;
    }
  }

  private void SetTerminalOnLineStatus(List TerminalList) {
    try {
      for (int i = 0; i < TerminalList.size(); i++) {
        TerminalOnLineStatus to = (TerminalOnLineStatus) TerminalList.get(i);
        TerminalInfo ti = (TerminalInfo) CommunicationServerConstants.
            TerminalLocalList.get(to.TerminalAddress); //根据终端逻辑地址到Hash表中搜索
        if (ti != null) {
          ti.OnLineStatus = to.OnLineStatus;
          CommunicationServerConstants.TerminalLocalList.remove(to.
              TerminalAddress);
          CommunicationServerConstants.TerminalLocalList.put(to.TerminalAddress,
              ti);
          CommunicationServerConstants.Trc1.TraceLog("----Terminal " + to.TerminalAddress +
                                  " OnLineStaus is " + to.OnLineStatus);
        }
      }
    }
    catch (Exception ex) {
    }
  }

//获取一批终端的在线状态，用于回传
  private List<TerminalOnLineStatus> GetTerminalOnLineStatus(List TerminalList) {
    List <TerminalOnLineStatus>OnLineList = new LinkedList<TerminalOnLineStatus>();
    try {
      for (int i = 0; i < TerminalList.size(); i++) {
        TerminalOnLineStatus to = (TerminalOnLineStatus) TerminalList.get(i);
        TerminalInfo ti = (TerminalInfo) CommunicationServerConstants.
            TerminalLocalList.get(to.TerminalAddress); //根据终端逻辑地址到Hash表中搜索
        if (ti != null) {
          to.OnLineStatus = ti.OnLineStatus;
          OnLineList.add(to); //存在的则获取在线状态字段，加入到返回队列中
        }
      }
    }
    catch (Exception ex) {
    }
    return OnLineList;
  }

//处理获取终端在线状态命令的函数，获取终端在线状态队列后，组成相应命令返回到实时通讯对象
  private void ProcessTerminalOnLineList(List tempList, SocketChannel channel) {
    List OutList = GetTerminalOnLineStatus(tempList);
    if (OutList != null) {
      try {
        Thread.sleep(100);
      }
      catch (InterruptedException ex1) {
      }
      ByteBuffer bBuffer = ByteBuffer.allocate(5000);
      byte[] data = null;
      data = new byte[12 + 2 + 21 * OutList.size()]; //根据终端数量决定需要多少的byte空间
      byte[] MsgLength = new byte[4];
      MsgLength = utils.int2byte(12 + 2 + 21 * OutList.size());
      //消息头部分的消息总长度
      data[0] = MsgLength[0];
      data[1] = MsgLength[1];
      data[2] = MsgLength[2];
      data[3] = MsgLength[3];
      //消息头部分的消息类型代码
      data[4] = 0;
      data[5] = 0;
      data[6] = 0;
      data[7] = 22;
      //消息头部分的消息序号
      byte[] MsgSequence = new byte[4];
      MsgSequence = utils.int2byte(Sequence);
      data[8] = MsgSequence[0];
      data[9] = MsgSequence[1];
      data[10] = MsgSequence[2];
      data[11] = MsgSequence[3];
      //消息体部分的消息长度
      byte[] bTemp = new byte[2];
      byte[] bSJQCD = new byte[2];
      if (OutList.size() < 15) { //只有小于15的数据需要特殊处理，其他都可以由统一函数实现
        bSJQCD[1] = 0;
        bSJQCD[0] = (byte) OutList.size();
      }
      else {
        bSJQCD = utils.str2bytes(Integer.toHexString(OutList.size()));
      }
      for (int i = 0; i < bSJQCD.length; i++) {
        bTemp[i] = bSJQCD[i];
      }
      if (tempList.size() < 256) {
        data[12] = bTemp[1]; //数据区长度的字段需要做颠倒工作
        data[13] = bTemp[0];
      }
      else {
        data[12] = bTemp[0];
        data[13] = bTemp[1];
      }
      //消息体部分的消息内容：即终端逻辑地址和在线状态的内容
      int iPos = 14;
      for (int i = 0; i < OutList.size(); i++) {
        bTemp = new byte[20];
        TerminalOnLineStatus to = (TerminalOnLineStatus) OutList.get(i);
        byte[] bZDLJDZ = to.TerminalAddress.getBytes();
        for (int j = 0; j < bZDLJDZ.length; j++) {
          bTemp[j] = bZDLJDZ[j];
        }
        for (int j = 0; j < 20; j++) {
          data[iPos + j] = bTemp[j];
        } //终端逻辑地址，需要补全20位
        data[iPos + 20] = (byte) to.OnLineStatus;
        iPos = iPos + 21;
      }
      Sequence = Sequence + 1;
      if ( (data != null)) { //发送数据
        bBuffer.clear();
        bBuffer.put(data);
        bBuffer.flip();
        CommunicationServerConstants.Trc1.TraceLog("23、SendTerminalOnLineList Host:" +
                                channel.socket().getInetAddress().
                                getHostAddress());
        try {
        	channel.write(bBuffer); //如果在这里出现异常，则说明无法连接成功
        }
        catch (IOException ex) {
        	CommunicationServerConstants.Trc1.TraceLog("SendTerminalOnLineStaute Error is " +
                                  ex.toString());
        }
        bBuffer.clear();
      }
      return;
    }
  }

//分析命令内容，根据命令类型分别进行处理
  private void AnalyzeCommandContent(String sData, int CommandType,
                                     byte[] data, SocketChannel channel) {
    try {
      String sMessageLen = sData.substring(1, 8);
      if (sData.length() != utils.HexStrToInt(sMessageLen) * 2) {
        return;
      }
      else {
        String sZDLJDZ = "";
        int iGYH = 0;
        int iCLDXH = 0;
        int iCLDLX = 0;
        int iAppID = 0;
        int iTXCS = 0;
        int iZNXZ = 1;
        String sGNM = "";
        int iSJQCD = 0;
        String sSJQNR = "";
        int iMLXH = 0;
        switch (CommandType) {
          case 0x00000004: { //普通数据召测命令
            iAppID = utils.HexStrToInt(sData.substring(24, 32)); //调用的AppID
            iTXCS = utils.HexStrToInt(sData.substring(72, 74)); //传入的通讯方式
            iZNXZ = utils.HexStrToInt(sData.substring(74, 76)); //智能选择标志
            iCLDXH = utils.HexStrToInt(sData.substring(76, 78)); //测量点序号
            iCLDLX = utils.HexStrToInt(sData.substring(78, 80)); //测量点类型
            iGYH = utils.HexStrToInt(sData.substring(80, 82)); //规约号

            byte[] Content = new byte[20];
            for (int i = 0; i < 20; i++) {
              Content[i] = data[i + 16];
            }
            sZDLJDZ = new String(Content).trim();
            iMLXH = GetTerminalCommandNo(sZDLJDZ, iGYH);

            Content = new byte[2];
            for (int i = 0; i < 2; i++) {
              Content[i] = data[i + 41];
            }
            sGNM = new String(Content).trim();

            iSJQCD = utils.HexStrToInt(sData.substring(86, 90));
            Content = new byte[iSJQCD];
            for (int i = 0; i < iSJQCD; i++) {
              Content[i] = data[i + 45];
            }
            sSJQNR = new String(Content).trim();
            int iYXJ = utils.HexStrToInt(sData.substring(90 + iSJQCD * 2,
                92 + iSJQCD * 2)); //230M通讯类型的的优先级
            int iSFBH = utils.HexStrToInt(sData.substring(92 + iSJQCD * 2,
                94 + iSJQCD * 2)); //算法编号，用于加密算法
            //socket报文内容分析完成，更新对应的链路的信息，然后调用组帧后加入发送队列
    //        CommunicationServerConstants.Trc1.TraceLog("03、BuildNromalFrame ZDLJDZ:" + sZDLJDZ +
     //                               " GYH:" + iGYH + " AppID:" + iAppID
     //                               + " MLXH:" + iMLXH + " GNM:" + sGNM +
     //                               " DataArea:" + sSJQNR);
            UpdateLinkSocketInfo(sZDLJDZ, iGYH, iMLXH, 1, channel, 0, 0, 0,
                                 iAppID, Glu_ConstDefine.SJLX_PTSJ);
            //As to DLMS commands,need to send AARE and HLS authentication before the get/set/action request. 
            if (iGYH == Glu_ConstDefine.GY_ZD_DLMS){
            	//The first byte in SJQNR is Security level,0:lowest;1:low;2:highest.
            	String sSL = sSJQNR.substring(0, 2);
            	//The next 2 bytes is the protocol version.
            	String sProVer = sSJQNR.substring(2, 6);
            	//The next 8 bytes is the password;
            	String sPassword = sSJQNR.substring(6, 14);
            	sSJQNR = sSJQNR.substring(14, sSJQNR.length());
            	FrameDLMSUpNormalFrameAndAddToList(sZDLJDZ, iGYH, iSJQCD, sGNM, sSJQNR, iAppID,
                        iTXCS, iZNXZ, iMLXH, iYXJ, iSFBH, sSL, sProVer, sPassword);
            } else{
            	FrameUpNormalFrameAndAddToList(sZDLJDZ, iGYH, iCLDXH, iCLDLX,
                                           iSJQCD, sGNM, sSJQNR, iAppID,
                                           iTXCS, iZNXZ, iMLXH, iYXJ, iSFBH);
            }
            break;
            
          }
          case 0x00000005: { //历史数据召测命令处理
            iAppID = utils.HexStrToInt(sData.substring(24, 32));
            iTXCS = utils.HexStrToInt(sData.substring(72, 74));
            iZNXZ = utils.HexStrToInt(sData.substring(74, 76));
            iCLDXH = utils.HexStrToInt(sData.substring(76, 78));
            iCLDLX = utils.HexStrToInt(sData.substring(78, 80));
            iGYH = utils.HexStrToInt(sData.substring(80, 82));

            byte[] Content = new byte[20];
            for (int i = 0; i < 20; i++) {
              Content[i] = data[i + 16];
            }
            sZDLJDZ = new String(Content).trim();
            iMLXH = GetTerminalCommandNo(sZDLJDZ, iGYH);

            Content = new byte[2];
            for (int i = 0; i < 2; i++) {
              Content[i] = data[i + 41];
            }
            sGNM = new String(Content).trim();

            iSJQCD = utils.HexStrToInt(sData.substring(86, 90));
            Content = new byte[iSJQCD];
            for (int i = 0; i < iSJQCD; i++) {
              Content[i] = data[i + 45];
            }
            sSJQNR = new String(Content).trim();

            int TaskCount = utils.HexStrToInt(sData.substring(90 + iSJQCD * 2,
                92 + iSJQCD * 2));
            int iYXJ = utils.HexStrToInt(sData.substring(92 + iSJQCD * 2,
                94 + iSJQCD * 2)); //230M通讯类型的的优先级
            //socket报文内容分析完成，更新对应的链路的信息，然后调用组帧后加入发送队列
     //       CommunicationServerConstants.Trc1.TraceLog("03、BuildHiFrame ZDLJDZ:" + sZDLJDZ +
     //                               " AppID:" + iAppID + " GYH:" + iGYH +
     //                               " MLXH:" + iMLXH + " GNM:" + sGNM +
     //                               " TaskCount:" + TaskCount +
     //                               " DataArea:" + sSJQNR);
            UpdateLinkSocketInfo(sZDLJDZ, iGYH, iMLXH, 1, channel, 0, 0,
                                 TaskCount, iAppID,
                                 Glu_ConstDefine.SJLX_LSSJZC);
            if (iGYH == Glu_ConstDefine.GY_ZD_DLMS){
            	//The first byte in SJQNR is Security level,0:lowest;1:low;2:highest.
            	String sSL = sSJQNR.substring(0, 2);
            	//The next 2 bytes is the protocol version.
            	String sProVer = sSJQNR.substring(2, 6);
            	//The next 8 bytes is the password;
            	String sPassword = sSJQNR.substring(6, 14);
            	sSJQNR = sSJQNR.substring(14, sSJQNR.length());
            	FrameDLMSUpNormalFrameAndAddToList(sZDLJDZ, iGYH, iSJQCD, sGNM, sSJQNR, iAppID,
                        iTXCS, iZNXZ, iMLXH, iYXJ, 0, sSL, sProVer, sPassword);
            } else{
            	FrameUpNormalFrameAndAddToList(sZDLJDZ, iGYH, iCLDXH, iCLDLX,
                                           iSJQCD, sGNM, sSJQNR, iAppID,
                                           iTXCS, iZNXZ, iMLXH, iYXJ, 0);
            }
            break;
          }
          case 0x00000006: { //异常事件召测命令处理
            iAppID = utils.HexStrToInt(sData.substring(24, 32));
            iTXCS = utils.HexStrToInt(sData.substring(72, 74));
            iZNXZ = utils.HexStrToInt(sData.substring(74, 76));
            iCLDXH = utils.HexStrToInt(sData.substring(76, 78));
            iCLDLX = utils.HexStrToInt(sData.substring(78, 80));
            iGYH = utils.HexStrToInt(sData.substring(80, 82));

            byte[] Content = new byte[20];
            for (int i = 0; i < 20; i++) {
              Content[i] = data[i + 16];
            }
            sZDLJDZ = new String(Content).trim();
            iMLXH = GetTerminalCommandNo(sZDLJDZ, iGYH);

            Content = new byte[2];
            for (int i = 0; i < 2; i++) {
              Content[i] = data[i + 41];
            }
            sGNM = new String(Content).trim();

            iSJQCD = utils.HexStrToInt(sData.substring(86, 90));
            Content = new byte[iSJQCD];
            for (int i = 0; i < iSJQCD; i++) {
              Content[i] = data[i + 45];
            }
            sSJQNR = new String(Content).trim();

            int AlarmCount = utils.HexStrToInt(sData.substring(90 +
                iSJQCD * 2, 92 + iSJQCD * 2));
            int iYXJ = utils.HexStrToInt(sData.substring(92 + iSJQCD * 2,
                94 + iSJQCD * 2)); //230M通讯类型的的优先级
            //socket报文内容分析完成，更新对应的链路的信息，然后调用组帧后加入发送队列
    //        CommunicationServerConstants.Trc1.TraceLog("03、BuildEveFrame sZDLJDZ:" + sZDLJDZ +
     //                               " AppID:" + iAppID + " GYH:" + iGYH +
    //                                " MLXH:" + iMLXH + " GNM:" + sGNM +
    //                                " AlarmCount:" + AlarmCount +
    //                                " DataArea:" + sSJQNR);
            UpdateLinkSocketInfo(sZDLJDZ, iGYH, iMLXH, 1, channel, 0, 0,
                                 AlarmCount, iAppID,
                                 Glu_ConstDefine.SJLX_YCSJZC);
            FrameUpNormalFrameAndAddToList(sZDLJDZ, iGYH, iCLDXH, iCLDLX,
                                           iSJQCD, sGNM, sSJQNR, iAppID,
                                           iTXCS, iZNXZ, iMLXH, iYXJ, 0);
            break;
          }
          case 0x0000000A: { //下发短信
            byte[] Content = new byte[15];
            for (int i = 0; i < 15; i++) {
              Content[i] = data[i + 12];
            }
            StructSendData ss = new StructSendData();
            ss.MobileNo = new String(Content).trim();
            ss.MessageLength = utils.HexStrToInt(sData.substring(58, 62));
            ss.MessageType = utils.HexStrToInt(sData.substring(54, 58));
            Content = new byte[ss.MessageLength];
            for (int i = 0; i < ss.MessageLength; i++) {
              Content[i] = data[i + 31];
            }
            ss.MessageContent = new String(Content).trim();
            ss.SendDirect = 1;
            ss.TerminalAddress = "";
            ss.TerminalCommunication = data[ss.MessageLength + 31];
            CommunicationServerConstants.GlobalSendList.add(ss); //直接加入发送队列即可
            //队列长度超过5000条，就删除头上的记录，控制队列长度
            if (CommunicationServerConstants.GlobalSendList.size() > 5000) {
              CommunicationServerConstants.GlobalSendList.remove(0);
            }
     //       CommunicationServerConstants.Trc1.TraceLog("03、SendShotMsg MobileNo:" + ss.MobileNo +
     //                               " MessageLength:" + ss.MessageLength);
            break;
          }
          case 0x0000000C: { //下发自定义命令
            iAppID = utils.HexStrToInt(sData.substring(24, 32));
            iTXCS = utils.HexStrToInt(sData.substring(72, 74));
            iZNXZ = utils.HexStrToInt(sData.substring(74, 76));
            iMLXH = utils.HexStrToInt(sData.substring(76, 78));
            iGYH = utils.HexStrToInt(sData.substring(78, 80));

            byte[] Content = new byte[20];
            for (int i = 0; i < 20; i++) {
              Content[i] = data[i + 16];
            }
            sZDLJDZ = new String(Content).trim();

            iSJQCD = utils.HexStrToInt(sData.substring(80, 84));
            Content = new byte[iSJQCD];
            for (int i = 0; i < iSJQCD; i++) {
              Content[i] = data[i + 42];
            }
            sSJQNR = new String(Content).trim();

            int iYXJ = utils.HexStrToInt(sData.substring(84 + iSJQCD * 2,
                86 + iSJQCD * 2)); //230M通讯类型的的优先级
            //socket报文内容分析完成，更新对应的链路的信息，然后直接就可以加入发送队列
    //        CommunicationServerConstants.Trc1.TraceLog("03、SendSelfFrame " + sZDLJDZ +
    //                                " GYH:" + iGYH + " MLXH:" + iMLXH +
    //                                " AppID:" + iAppID + " DataArea:" +
    //                                sSJQNR);
            UpdateLinkSocketInfo(sZDLJDZ, iGYH, iMLXH, 1, channel, 0, 0, 0,
                                 iAppID, Glu_ConstDefine.SJLX_PTSJ);
            StructSendData ss = new StructSendData();
            ss.MessageContent = new String(sSJQNR).trim();
            ss.MessageLength = new String(sSJQNR).trim().length();
            ss.MessageType = 30;
            ss.MobileNo = "";
            ss.SendDirect = 0;
            ss.TerminalAddress = sZDLJDZ;
            ss.YXJ = iYXJ;
            ss.TerminalCommunication = iTXCS;
            if (ss.TerminalCommunication == 20) {
              ss.SendDirect = 1;
            }
            CommunicationServerConstants.GlobalSendList.add(ss);
            //队列长度超过5000条，就删除头上的记录，控制队列长度
            if (CommunicationServerConstants.GlobalSendList.size() > 5000) {
              CommunicationServerConstants.GlobalSendList.remove(0);
            }
            break;
          }
          case 0x0000000D: { //实时通讯链路登陆命令
            AddLinkToList(channel);
      //      CommunicationServerConstants.Trc1.TraceLog("MasterStation LogIn FromIP:" +
       //                             channel.socket().getInetAddress().
      //                              getHostAddress());
            break;
          }
          case 0x0000000E: { //实时通讯链路退出登陆命令
            SocketLinkList sc = (SocketLinkList) LinkList.get(channel);
            if (sc != null) {
        //    	CommunicationServerConstants.Trc1.TraceLog("MasterStation LogOut FromIP:" +
        //                              channel.socket().getInetAddress().
         //                             getHostAddress());
              LinkList.remove(sc.GetSocketChannel());
            }
            break;
          }
          case 0x00000015: { //读取终端在线状态的命令
            int iCount = utils.HexStrToInt(sData.substring(24, 28)); //返回的终端数量
            List <TerminalOnLineStatus>tempList = new LinkedList<TerminalOnLineStatus>();
            sZDLJDZ = "";
            for (int i = 0; i < iCount; i++) {
              byte[] Content = new byte[20];
              for (int j = 0; j < 20; j++) {
                Content[j] = data[i * 20 + 14 + j];
              }
              sZDLJDZ = new String(Content).trim(); //终端逻辑地址
              TerminalOnLineStatus to = new TerminalOnLineStatus();
              to.TerminalAddress = sZDLJDZ;
              to.OnLineStatus = 20;
              tempList.add(to);
            }
            ProcessTerminalOnLineList(tempList, channel);
            break;
          }
          case 0x0000001E: { //设置终端在线状态的命令
            int iCount = utils.HexStrToInt(sData.substring(24, 28)); //返回的终端数量
            List<TerminalOnLineStatus> tempList = new LinkedList<TerminalOnLineStatus>();
            sZDLJDZ = "";
            for (int i = 0; i < iCount; i++) {
              byte[] Content = new byte[20];
              for (int j = 0; j < 20; j++) {
                Content[j] = data[i * 20 + 14 + j];
              }
              sZDLJDZ = new String(Content).trim(); //终端逻辑地址
              TerminalOnLineStatus to = new TerminalOnLineStatus();
              to.TerminalAddress = sZDLJDZ;
              to.OnLineStatus = 20;
              tempList.add(to);
            }
            SetTerminalOnLineStatus(tempList);
            break;
          }
          case 0x0000001F: { //对指定的终端建档
            sZDLJDZ = "";
            byte[] Content = new byte[20];
            for (int i = 0; i < 20; i++) {
              Content[i] = data[12 + i];
            }
            sZDLJDZ = new String(Content).trim(); //终端逻辑地址
            CommunicationServerConstants.BuildTerminalTag = true;
            CommunicationServerConstants.TerminalAddr = sZDLJDZ;
            break;
          }
          case 0x00000020: { //refresh information of specified mp
              sZDLJDZ = "";
              iCLDXH = -1;
              byte[] Content = new byte[20];
              for (int i = 0; i < 20; i++) {
                Content[i] = data[12 + i];
              }
              sZDLJDZ = new String(Content).trim(); //终端逻辑地址
              iCLDXH = utils.HexStrToInt(sData.substring(64, 66));
              CommunicationServerConstants.BuildMPTag = true;
              CommunicationServerConstants.TerminalAddr = sZDLJDZ;
              CommunicationServerConstants.CLDXH = iCLDXH;
              break;
            }
          
          case 0x00000101: //调用加密机
          case 0x00000102:{ //调用读卡器    	  
        	  iAppID = utils.HexStrToInt(sData.substring(24, 32)); //调用的AppID
              iTXCS = 0; //传入的通讯方式
              iZNXZ = 0; //智能选择标志
              iCLDXH = 0; //测量点序号
              iCLDLX = 0; //测量点类型
              iGYH = Glu_ConstDefine.GY_PrePayApp; //规约号

              byte[] Content = new byte[20];
              for (int i = 0; i < 20; i++) {
                Content[i] = data[i + 16];
              }
              sZDLJDZ = new String(Content).trim();  //用户户号
              
              iMLXH = GetTerminalCommandNo("000000000000", iGYH);  //对于调用加密机、读卡器等操作，默认为12位'0'作为逻辑地址以获取命令序号
              //String sMLXH = Integer.toHexString(iMLXH);
              //sMLXH = "00000000".substring(0,8-sMLXH.length()) +sMLXH;
              int iBBH = utils.HexStrToInt(sData.substring(72, 76));  //调加密码机类型
              iSJQCD = utils.HexStrToInt(sData.substring(76, 80));
              Content = new byte[iSJQCD];
              for (int i = 0; i < iSJQCD; i++) {
                Content[i] = data[i + 40];
              }             
              sSJQNR = new String(Content).trim();
              //sSJQNR = sZDLJDZ+sMLXH+sSJQNR; 
              //iSJQCD = sSJQNR.length();
              int iFSFS = 0;
              String sDYIP = "";
              if (CommandType == 0x00000101){
            	  if (iBBH == 10)
            		  iFSFS = 10;  //国网加密机
            	  else if (iBBH == 20) 
            		  iFSFS = 30; //海兴加密机           		  
            	  sDYIP = CommunicationServerConstants.COMMSERVICE_MESSAGEEXCHANGE_ENCRYPTORIP; //加密机IP地址
              }
              else if (CommandType == 0x00000102){
            	  iFSFS = 20; //国网读卡器 
            	  sDYIP = channel.socket().getInetAddress().getHostAddress();
              }
              
       //       CommunicationServerConstants.Trc1.TraceLog("03、BuildNromalFrame ZDLJDZ:" + sZDLJDZ +
       //                               " GYH:" + iGYH + " AppID:" + iAppID
       //                               + " MLXH:" + iMLXH + " GNM:" + sGNM +
       //                               " DataArea:" + sSJQNR);
              UpdateLinkSocketInfo(sZDLJDZ, iGYH, iMLXH, 1, channel, 0, 0, 0,
                                   iAppID, 0);
              BuildProPayFrameAddToList(sZDLJDZ, iGYH, iCLDXH, iCLDLX,
                                             iSJQCD, sGNM, sSJQNR, iAppID,
                                             iTXCS,iFSFS, iZNXZ, iMLXH,sDYIP);
              break;
            }
          
          /*case 0x00000103:{//预付费直接发送       	  
        	  iAppID = utils.HexStrToInt(sData.substring(24, 32)); //调用的AppID
              iTXCS = utils.HexStrToInt(sData.substring(72, 74)); //传入的通讯方式
              iZNXZ = utils.HexStrToInt(sData.substring(74, 76)); //智能选择标志
              iCLDXH = utils.HexStrToInt(sData.substring(76, 78)); //测量点序号
              iCLDLX = utils.HexStrToInt(sData.substring(78, 80)); //测量点类型
              iGYH = utils.HexStrToInt(sData.substring(80, 82)); //规约号

              byte[] Content = new byte[20];
              for (int i = 0; i < 20; i++) {
                Content[i] = data[i + 16];
              }
              sZDLJDZ = new String(Content).trim();
              iMLXH = GetTerminalCommandNo(sZDLJDZ, iGYH);

              Content = new byte[2];
              for (int i = 0; i < 2; i++) {
                Content[i] = data[i + 41];
              }
              sGNM = new String(Content).trim();

              iSJQCD = utils.HexStrToInt(sData.substring(86, 90));
              Content = new byte[iSJQCD];
              for (int i = 0; i < iSJQCD; i++) {
                Content[i] = data[i + 45];
              }
              sSJQNR = new String(Content).trim();
              int iYXJ = utils.HexStrToInt(sData.substring(90 + iSJQCD * 2,
                  92 + iSJQCD * 2)); //230M通讯类型的的优先级
              int iSFBH = utils.HexStrToInt(sData.substring(92 + iSJQCD * 2,
                  94 + iSJQCD * 2)); //算法编号，用于加密算法
              //socket报文内容分析完成，更新对应的链路的信息，然后调用组帧后加入发送队列
              utils.PrintDebugMessage("03、BuildNromalFrame ZDLJDZ:" + sZDLJDZ +
                                      " GYH:" + iGYH + " AppID:" + iAppID
                                      + " MLXH:" + iMLXH + " GNM:" + sGNM +
                                      " DataArea:" + sSJQNR, Debug);
              UpdateLinkSocketInfo(sZDLJDZ, iGYH, iMLXH, 1, channel, 0, 0, 0,
                                   iAppID, Glu_ConstDefine.SJLX_PTSJ);
              FrameUpNormalFrameAndAddToList(sZDLJDZ, iGYH, iCLDXH, iCLDLX,
                                             iSJQCD, sGNM, sSJQNR, iAppID,
                                             iTXCS, iZNXZ, iMLXH, iYXJ, iSFBH);
              break;
          }*/
          
          
          default: {
            break;
          }
        }
      }
    }
    catch (Exception ex) {
    	CommunicationServerConstants.Log1.WriteLog(
          "MessageExchange:AnalyzeCommandContent() error,error is " +
          ex.toString());
    }
  }

  private void processData(int CommandID, SocketChannel channel,
                           String ReceiveData, byte[] Data) {
    UpdateLastCommDate(channel);
    if (CommandID == 0x08000007 || CommandID == 0x08000008 || CommandID == 0x08000009 ||
        CommandID == 0x08000016 || CommandID == 0x08000108 ||CommandID == 0x08000108) { //对于提交给实时通讯返回的确认
      AckBack = true;
    }
    else if (CommandID == 0x00000004 || CommandID == 0x00000005 || CommandID == 0x00000006 ||
             CommandID == 0x0000000A || CommandID == 0x0000000C || CommandID == 0x0000000D ||
             CommandID == 0x0000000E || CommandID == 0x00000015 || CommandID == 0x0000001E || 
             CommandID == 0x0000001F || CommandID == 0x00000101 || CommandID == 0x00000102 ||
             CommandID == 0x00000103 || CommandID == 0x00000020) {
      AckMessage(ReceiveData.substring(10, 16), ReceiveData, channel, Data); //有可能跟提交上行数据起冲突，屏蔽回确认
      AnalyzeCommandContent(ReceiveData, CommandID, Data, channel);
    }
  }

  /*private void processData(String sData, SocketChannel channel, byte[] data) {
    try {
      String CommandID = AnalyzeCommandType(sData);
      UpdateLastCommDate(channel);
      int iCommandID = Integer.parseInt(CommandID, 16);
      if (iCommandID == 7 || iCommandID == 8 || iCommandID == 9 ||
          iCommandID == 22) { //对于提交给实时通讯返回的确认
        AckBack = true;
      }
      else if (iCommandID == 4 || iCommandID == 5 || iCommandID == 6 ||
               iCommandID == 10 || iCommandID == 12 || iCommandID == 14 ||
               iCommandID == 21 || iCommandID == 30 || iCommandID == 31) {
        AckMessage(CommandID, sData, channel, data); //有可能跟提交上行数据起冲突，屏蔽回确认
        AnalyzeCommandContent(sData, iCommandID, data, channel);
      }
    }
    catch (NumberFormatException ex) {
      fl.WriteLog(
          "MessageExchange:processData() error,error message is " +
          ex.toString());
    }
  }*/

  private void receiveData(SelectionKey key) {
    //首先读取12个字符的报文头内容，分析报文头的有效性
    ByteBuffer[] buffer = new ByteBuffer[10];
    buffer[0] = ByteBuffer.allocateDirect(12); //用于接收的缓冲
    try {
      SocketChannel socketChannel = (SocketChannel) key.channel();
      buffer[0].clear();
      long count = 0;
      try {
        count = socketChannel.read(buffer, 0, 1);
      }
      catch (IOException ex1) {
        count = -1;
      }
      if (count == -1) { //前置机的socket通道出现异常时，也需要处理链路队列，保证队列中的数据正确
        SocketLinkList sc = (SocketLinkList) LinkList.get(socketChannel);
        if (sc != null) {
    //    	CommunicationServerConstants.Trc1.TraceLog("MasterStation Connection failed,LinkList size:"+LinkList.size());
        	LinkList.remove(sc.GetSocketChannel());
        	sc.GetSocketChannel().close();
        	sc.GetSocketChannel().socket().close();          
        }
        return;
      }
      if (count == 12) {
        buffer[0].flip();
        byte data[] = new byte[10000];
        buffer[0].get(data, 0, buffer[0].limit());
        MessageBody mb = new MessageBody();
        String strMesshead = utils.bytes2str(data, buffer[0].limit());
        mb.TotalLength = strMesshead.substring(0, 8);
        mb.CommandID = strMesshead.substring(8, 16);
        mb.SeqID = strMesshead.substring(16, 24);
        //如果报文头有效，则继续读取后面的报文体内容，进行处理，否则丢失当前报文头，继续读取12个字符
        int iLen = Integer.parseInt(mb.TotalLength,16) - 12;
   //     CommunicationServerConstants.Trc1.TraceLog("00、Recv Buffer From MasterStation,Message length:"+iLen);
        if (iLen > 500){
        	//if(iLen < 1000){
        		buffer[1] = ByteBuffer.allocate(Integer.parseInt(mb.TotalLength,
        	            16) - 12); //用于接收的缓冲
        		count = socketChannel.read(buffer, 1, 1);
   //     		CommunicationServerConstants.Trc1.TraceLog("00-1、IP:" 
   //     				+ socketChannel.socket().getInetAddress().getHostAddress()
   //     				+" Port: " +socketChannel.socket().getPort()
   //     				+" Message:"+utils.bytes2str(data, buffer[0].limit()));
        	//}else {
    //    		CommunicationServerConstants.Trc1.TraceLog("00-2、IP:" 
    //    				+ socketChannel.socket().getInetAddress().getHostAddress()
    //    				+" Port: " +socketChannel.socket().getPort()
    //    				+" Message:"+utils.bytes2str(data, buffer[0].limit()));
        	//}
        }else{
       // 	CommunicationServerConstants.Trc1.TraceLog("00-3、IP:" 
       // 				+ socketChannel.socket().getInetAddress().getHostAddress()
       // 				+" Port: " +socketChannel.socket().getPort()
       // 				+" Message:"+utils.bytes2str(data, buffer[0].limit()));
        	buffer[1] = ByteBuffer.allocate(Integer.parseInt(mb.TotalLength,
                    16) - 12); //用于接收的缓冲
            count = socketChannel.read(buffer, 1, 1);
        }    	
        if (count >= 0) {
          buffer[1].flip();
          buffer[1].get(data, 12, buffer[1].limit());
          String strMessbody = utils.bytes2str(data,
                                               Integer.parseInt(mb.
              TotalLength, 16));
   //       CommunicationServerConstants.Trc1.TraceLog(
    //          "01、Recv Buffer From MasterStation,ReceiveData Port: " +
   //           socketChannel.socket().getPort() + " Content: " +
   //           strMessbody);
          processData(Integer.parseInt(mb.CommandID, 16), socketChannel,
                      strMessbody, data);
          //System.out.println(mb.CommandID+" "+Integer.parseInt(mb.CommandID, 16));
        }
        //对于有效的报文处理后，退出本次数据的读取处理过程，如果还有数据需要处理会再次进入本函数处理
      }
      System.gc();
    }
    catch (Exception ex3) {
    	CommunicationServerConstants.Log1.WriteLog(
          "MessageExchange:receiveData() IOerror,error message is " +
          ex3.toString());
    }
  }

//处理召测返回数据处理,通过上行数据信息匹配下行命令获取AppID
//  @SuppressWarnings(value="unchecked")
  private void DealWithCallBackListData(StructReceiveData rd) {
    try {
      boolean FindLinkInfo = false;
      for (Enumeration e = LinkList.elements(); e.hasMoreElements(); ) {
        SocketLinkList sc = (SocketLinkList) e.nextElement();
        if (sc != null) {
          List lTemp = sc.GetTerminalCorreCommandSeqList();
          if (lTemp != null) {
            for (int j = lTemp.size() - 1; j >= 0; j--) {
              TerminalAddCorreCommandSeq ta = (TerminalAddCorreCommandSeq)
                  lTemp.get(j);
              String sTemp = new String(ta.TerminalAddress);
              if (rd.TerminalProtocal == Glu_ConstDefine.GY_ZD_QUANGUO ||                  
                  rd.TerminalProtocal == Glu_ConstDefine.GY_ZD_698 ||
                  rd.TerminalProtocal == Glu_ConstDefine.GY_ZD_IHD){ //国网系列的返回结果对应，不判断数据类型
                if ( (sTemp.substring(0,8).equals(rd.TerminalLogicAdd.substring(0,8))) && 
                		(ta.CommandSeq[0] == rd.CommandSeq)) {
                  rd.TerminalLogicAdd = sTemp;
                  CommunicationServerConstants.Trc1.TraceLog("22、FindRecord ZDLJDZ:" +
                                          rd.TerminalLogicAdd + " MLXH:" +
                                          rd.CommandSeq + " SJLX:" +
                                          rd.DataType + " AppID:" +
                                          ta.ApplicationID);
                  ta.LastUpdateTime = Calendar.getInstance();
                  lTemp.set(j, ta); //更新链路最后收到的数据时间
                  SendBackToSourceLink(sc.GetSocketChannel(), rd,
                                       ta.ApplicationID);
                  sc.SetTerminalCorreCommandSeqList(lTemp);
                  LinkList.remove(sc.GetSocketChannel());
                  LinkList.put(sc.GetSocketChannel(), sc);
                  FindLinkInfo = true;
                  break;
                }
              }
              else if (rd.TerminalProtocal == Glu_ConstDefine.GY_PrePayApp)
              {
            	  if ( (sTemp.equals(rd.TerminalLogicAdd)) && (ta.CommandSeq[0] == rd.CommandSeq)) {
            		  CommunicationServerConstants.Trc1.TraceLog("22、FindRecord ZDLJDZ:" +
                                            rd.TerminalLogicAdd + " MLXH:" +
                                            rd.CommandSeq + " SJLX:" +
                                            rd.DataType + " AppID:" +
                                            ta.ApplicationID);
                    ta.LastUpdateTime = Calendar.getInstance();
                    lTemp.set(j, ta); //更新链路最后收到的数据时间
                    SendBackToPreSourceLink(sc.GetSocketChannel(), rd,
                                         ta.ApplicationID);
                    sc.SetTerminalCorreCommandSeqList(lTemp);
                    LinkList.remove(sc.GetSocketChannel());
                    LinkList.put(sc.GetSocketChannel(), sc);
                    FindLinkInfo = true;
                    break;                    
                 }
              }
            	 
              else { //浙规系列的返回结果对应，需要判断返回和调用的数据类型
                if ( (sTemp.equals(rd.TerminalLogicAdd)) && (ta.CommandSeq[0] == rd.CommandSeq)) {
                	CommunicationServerConstants.Trc1.TraceLog("22、FindRecord ZDLJDZ:" +
                                          rd.TerminalLogicAdd + " MLXH:" +
                                          rd.CommandSeq + " SJLX:" +
                                          rd.DataType + " AppID:" +
                                          ta.ApplicationID);
                  ta.LastUpdateTime = Calendar.getInstance();
                  lTemp.set(j, ta); //更新链路最后收到的数据时间
                  SendBackToSourceLink(sc.GetSocketChannel(), rd,
                                       ta.ApplicationID);
                  sc.SetTerminalCorreCommandSeqList(lTemp);
                  LinkList.remove(sc.GetSocketChannel());
                  LinkList.put(sc.GetSocketChannel(), sc);
                  FindLinkInfo = true;
                  break;
                }
              }
            }
          }
          else if (lTemp != null && lTemp.size() == 0) { //该链路的命令维护队列中没有对象，则认为该链路可以不再使用
   //     	  CommunicationServerConstants.Trc1.TraceLog("29、This Link have no CallInfo " +
    //                                sc.GetSocketChannel());
            LinkList.remove(sc.GetSocketChannel());
            sc.GetSocketChannel().close();
          }

        }
      }
      if (!FindLinkInfo) {
    //	  CommunicationServerConstants.Trc1.TraceLog(
    //        "28、There is No Link Info,this Frame is useless!");
        AckBack = true; //对于模拟上送的数据，没有对应的链路信息，就丢弃该数据，只是作为档案的初始化，这种数据不需要进行重发
      }
    }
    catch (Exception ex) {
    	CommunicationServerConstants.Log1.WriteLog(
          "MessageExchange:DealWithCallBackListData() error,error is " +
          ex.toString() + " BackData TerminalAddress is " + rd.TerminalLogicAdd);
    }
  }
  /*
//对于上送的数据，根据数据类型进行处理，如果是上送的任务数据或者异常数据，则提交JMS；
  private void DealWithJMSListData(StructReceiveData rd) {
    try {
      
       * 20090924 此版本暂时不采用JMS队列，历史及异常数据直接保存数据库       
      if (rd.DataType == Glu_ConstDefine.SJLX_YCSJZC ||
          rd.DataType == Glu_ConstDefine.SJLX_YCSJZD) {
        //异常数据提交到异常处理的JMS队列中
        StructAutoCommitData acd = new StructAutoCommitData();
        acd.TerminalLogicAdd = rd.TerminalLogicAdd.toCharArray();
        //从终端本地列表中获取终端的真实规约号，避免在类似规约的情况下，采用帧分析后得到的大类规约号，而导致数据无法解释出来
        TerminalInfo ti = (TerminalInfo) CommunicationServerConstants.
            TerminalLocalList.get(new String(rd.TerminalLogicAdd).trim());
        if (ti != null) {
          acd.TerminalProtocal = ti.TerminalProtocol;
        }
        else {
          acd.TerminalProtocal = rd.TerminalProtocal;
        }
        acd.StationNo = rd.StationNo;
        acd.FrameSeq = rd.FrameSeq;
        acd.CommandSeq = rd.CommandSeq;
        acd.DataType = rd.DataType;
        acd.ControlCode = rd.ControlCode.toCharArray();
        acd.FunctionCode = rd.FunctionCode.toCharArray();
        acd.ManufacturerCode = rd.ManufacturerCode.toCharArray();
        acd.FrameLength = rd.FrameLength;
        acd.FrameData = rd.FrameData.toCharArray();
        try {
          ad.sendObject(acd); //通过已经存在的JMS消息对象发送符合结构要求的数据
        }
        catch (Exception ex1) {
          ad.close();
          ad = new AlarmDataJMSQueue();
          ad.sendObject(acd);
          utils.PrintDebugMessage("90、JMS Object Recreated!", Debug);
        }
        ToJMSCount = ToJMSCount + 1;
        utils.PrintDebugMessage("22、SendDataToJMSList DataType:" +
                                rd.DataType + " DataArea: " + rd.FrameData,
                                Debug);
      }
      else if (rd.DataType == Glu_ConstDefine.SJLX_LSSJZC ||
               rd.DataType == Glu_ConstDefine.SJLX_LSSJZD) { //历史数据
        StructAutoCommitData acd = new StructAutoCommitData();
        acd.TerminalLogicAdd = rd.TerminalLogicAdd.toCharArray();
        //从终端本地列表中获取终端的真实规约号，避免在类似规约的情况下，采用帧分析后得到的大类规约号，而导致数据无法解释出来
        TerminalInfo ti = (TerminalInfo) CommunicationServerConstants.
            TerminalLocalList.get(new String(rd.TerminalLogicAdd).trim());
        if (ti != null) {
          acd.TerminalProtocal = ti.TerminalProtocol;
        }
        else {
          acd.TerminalProtocal = rd.TerminalProtocal;
        }
        acd.StationNo = rd.StationNo;
        acd.FrameSeq = rd.FrameSeq;
        acd.CommandSeq = rd.CommandSeq;
        acd.DataType = rd.DataType;
        acd.ControlCode = rd.ControlCode.toCharArray();
        acd.FunctionCode = rd.FunctionCode.toCharArray();
        acd.ManufacturerCode = rd.ManufacturerCode.toCharArray();
        acd.FrameLength = rd.FrameLength;
        acd.FrameData = rd.FrameData.toCharArray();
        try {
          hd.sendObject(acd);
        }
        catch (Exception ex2) {
          hd.close();
          hd = new HisDataJMSQueue();
          hd.sendObject(acd);
          utils.PrintDebugMessage("91、JMS Object Recreated!", Debug);
        }
        ToJMSCount = ToJMSCount + 1;
        utils.PrintDebugMessage("22、SendDataToJMSList DataType:" +
                                rd.DataType + " DataArea: " + rd.FrameData,
                                Debug);
      } 
    }
    catch (Exception ex) {
      fl.WriteLog(
          "MessageExchange:DealWithJMSListData() error,error message is " +
          ex.toString());
    }
  }*/
  
  private void InitDeviceAndTaskList() { 
	  
    String sSQL1 =
        "SELECT RWH,RWLX,SJYBS,CLDXH,GYH,SJLX FROM RW_RWXX WHERE (RWLX=1 OR RWLX=2) AND GYH=100 ORDER BY RWH,SJYXH";
    try {
      ResultSet rs = DataAccess_His.executeQuery(sSQL1);
      String sOldTaskNo = "";
      int iTaskCount = 0;
      rs.next(); //打开数据集
      TaskInfo ti = new TaskInfo();
      while (!rs.isAfterLast()) {
        if (rs.isFirst()) {
          sOldTaskNo = rs.getString("RWH");
        }

        if (sOldTaskNo.equals(rs.getString("RWH"))) {
          ti.TaskNo = Integer.parseInt(sOldTaskNo);
          ti.TaskType = rs.getInt("SJLX");
          ti.MeasuredPoint = rs.getInt("CLDXH");
          if (ti.TaskType == 11) { //小时冻结任务，数据项需要重新处理
            String sTemp = rs.getString("SJYBS").trim();
            sTemp = sTemp.substring(1, 4);
            int iFn = Integer.parseInt(sTemp);
            //将1类数据的小时冻结数据标识转换为2类数据的数据标识
            if (iFn >= 81 && iFn <= 116) {
              iFn = iFn - 8;
            }
            else {
              if (iFn == 121) { //相应二类数据FN为81~116
                iFn = 138;
              }
            }
            if (iFn < 100) {
              sTemp = "D0" + iFn + "00";
            }
            else {
              sTemp = "D" + iFn + "00";
            }
            ti.CommandList.add(sTemp);
          }
          else if (ti.TaskType == 12) { //日冻结任务，数据项不需要处理
            ti.CommandList.add(rs.getString("SJYBS").trim());
          }
          if (rs.isLast()) {
            DeviceTaskInfo.add(ti);
            iTaskCount = iTaskCount + 1;
          }
          rs.next();
        }
        else {
          DeviceTaskInfo.add(ti);
          ti = new TaskInfo();
          iTaskCount = iTaskCount + 1;
          sOldTaskNo = rs.getString("RWH");
        }
      }
      rs.close();
    }
    catch (Exception ex) {
    	CommunicationServerConstants.Log1.WriteLog("Initialize the task info error,error is:" + ex.toString());
    }   

  }

  public void run() { 
	  
    ListenThread ls = new ListenThread();
    ls.start();
    JMSCommitThread js = new JMSCommitThread();
    js.start();
    int iSendWaitTimeOut = CommunicationServerConstants.
        COMMSERVICE_MESSAGEEXCHANGE_SENDWAITTIMEOUT;
    int iWaitListMaxCount = CommunicationServerConstants.
        COMMSERVICE_MESSAGEEXCHANGE_WAITLISTMAXCOUNT;
    
    
    while (true) {
      //1.维护与实时通讯模块的链路队列
      for (Enumeration e = LinkList.elements(); e.hasMoreElements(); ) {
        try {
          SocketLinkList sc = (SocketLinkList) e.nextElement();
          if (sc != null) {
            Calendar d = (Calendar) sc.GetLastCommDate().clone();
            d.add(Calendar.MINUTE, TimeOut); //对于链路超时的维护，超过超时时间还没有通讯的链路，就从链路维护队列中移出
            if (d.before(Calendar.getInstance())) {
              try {
                sc.SetTerminalCorreCommandSeqList(null);
                sc.GetSocketChannel().socket().setSoLinger(true,
                    0);
                AckMessage("0E", "00000001000000010000000100000001",
                           sc.GetSocketChannel(), null);
                sc.GetSocketChannel().close();
              }
              catch (Exception e3) {
              }
              CommunicationServerConstants.Trc1.TraceLog("80、a link timeout " +
                                      sc.GetSocketChannel());
              LinkList.remove(sc.GetSocketChannel());
              break;
            }
            else { //对没有超时的连接进行维护
              List lTemp = sc.GetTerminalCorreCommandSeqList();
              if (lTemp != null) {
                if (lTemp.size() >= iWaitListMaxCount) { //队列维护，超过指定数量清空队列
                  lTemp.clear();
                  sc.SetTerminalCorreCommandSeqList(lTemp);
                  LinkList.remove(sc.GetSocketChannel());
                  LinkList.put(sc.GetSocketChannel(), sc);
                }
                else if (lTemp.size() >= 200) { //超时判断
                  boolean bDeleteSign = false;
                  for (int i = 0; i < lTemp.size(); i++) {
                    TerminalAddCorreCommandSeq ta = (TerminalAddCorreCommandSeq)
                        lTemp.get(i);
                    Calendar LastTime = (Calendar) ta.LastUpdateTime.clone();
                    LastTime.add(Calendar.SECOND, iSendWaitTimeOut);
                    if (LastTime.before(Calendar.getInstance())) { //超时
                      lTemp.remove(ta);
                      bDeleteSign = true;
                    }
                  }
                  if (bDeleteSign) {
                    sc.SetTerminalCorreCommandSeqList(lTemp);
                    LinkList.remove(sc.GetSocketChannel());
                    LinkList.put(sc.GetSocketChannel(), sc);
                  }
                }
              }
            }
            d.add(Calendar.MINUTE, -TimeOut);
          }
        }
        catch (Exception ex1) {
        }
      }

      //2.优先处理召测返回队列，发送失败需要重发3次
      int iSize = 0, ReSendTimes = 0;
      iSize = CommunicationServerConstants.GlobalReceiveCallBackList.size();
      Calendar SendTime = Calendar.getInstance();
      StructReceiveData rd = null;
      while (iSize > 0) {
        try {
          if (AckBack) {
            ReSendTimes = 0;
            if (CommunicationServerConstants.GlobalReceiveCallBackList.size() ==
                0) {
              break;
            }
            else {
              rd = (StructReceiveData) CommunicationServerConstants.
                  GlobalReceiveCallBackList.get(0);
              CommunicationServerConstants.GlobalReceiveCallBackList.remove(0);
              CommunicationServerConstants.Trc1.TraceLog("21、GetUpFrame ZDLJDZ:" +
                                      rd.TerminalLogicAdd );
              AckBack = false;
              DealWithCallBackListData(rd);
              SendTime = Calendar.getInstance();
              SendTime.add(Calendar.MILLISECOND, 100);
            }
          }
          else if (SendTime.before(Calendar.getInstance()) && (ReSendTimes < 2)) {
            if (rd != null) {
            	CommunicationServerConstants.Trc1.TraceLog("21、ReSend:" + rd.TerminalLogicAdd);
              ReSendTimes = ReSendTimes + 1;
              DealWithCallBackListData(rd);
              SendTime = Calendar.getInstance();
              SendTime.add(Calendar.MILLISECOND, 100);
            }
            else {
              AckBack = true;
              iSize = CommunicationServerConstants.GlobalReceiveCallBackList.
                  size();
              CommunicationServerConstants.Trc1.TraceLog("21、rd is null");
            }
          }
          else if (ReSendTimes >= 2) {
        	  CommunicationServerConstants.Trc1.TraceLog("21、resent timeout");
            AckBack = true;
            iSize = CommunicationServerConstants.GlobalReceiveCallBackList.size();
          }
        }
        catch (Exception ex2) {
        	CommunicationServerConstants.Log1.WriteLog("Process CallBackData Error during Execute");
        	ex2.printStackTrace();
        }
        try {
          Thread.sleep(1);
        }
        catch (InterruptedException ex) {
        }
      }

      try {
        Thread.sleep(1);
      }
      catch (InterruptedException ex) {
      }
    }
  }

  private void AddLinkToList(SocketChannel channel) {
    if (channel == null) {
      return;
    }
    //根据传入的socket链路对象，查找链路维护队列中是否存在，如果不存在则新增
    SocketLinkList sc = (SocketLinkList) LinkList.get(channel);
    if (sc == null) {
      sc = new SocketLinkList();
      sc.SetSocketChannel(channel);
      sc.SetSocketAddress(channel.socket().getInetAddress().
                          toString());
      sc.SetSocketPort(channel.socket().getPort());
      sc.SetSocketChannel(channel);
      sc.SetLastCommDate(Calendar.getInstance());
      sc.SetListFlag(true);
      sc.SetTerminalCorreCommandSeqList(null);
      LinkList.put(channel, sc);
    }
  }

  private void AddLinkToList(Selector selector,
                             SocketChannel channel) {
    if (channel == null) {
      return;
    }
    //根据传入的socket链路对象，查找链路维护队列中是否存在，如果不存在则新增
    SocketLinkList sc = (SocketLinkList) LinkList.get(channel);
    if (sc == null) {
      sc = new SocketLinkList();
      sc.SetSocketChannel(channel);
      sc.SetSocketAddress(channel.socket().getInetAddress().
                          toString());
      sc.SetSocketPort(channel.socket().getPort());
      sc.SetSocketChannel(channel);
      sc.SetLastCommDate(Calendar.getInstance());
      sc.SetListFlag(true);
      sc.SetTerminalCorreCommandSeqList(null);
      LinkList.put(channel, sc);
    }
  }
  
  

  private void registerChannel(Selector selector,
                               SocketChannel channel, int ops) {
    try {
      channel.configureBlocking(false); //通道注册，设置socket的阻塞模式为非阻塞模式
      channel.register(selector, ops);
    }
    catch (IOException ex) {
    	CommunicationServerConstants.Log1.WriteLog(
          "MessageExchange:registerChannel() IOerror,error is " +
          ex.toString());
    }
  }
  
  private List AdjustTaskNo(List DataList) {
    List <SFE_HistoryData>l = new LinkedList<SFE_HistoryData>();
    int iCount = DataList.size();
    int iCLDXH = 0;
    String sSJXBS = "";
    int iTaskNo = 0;
    if (iCount > 0) {
      SFE_HistoryData sh = (SFE_HistoryData) DataList.get(0);
      iCLDXH = sh.GetMeasuredPointNo();
      SFE_DataItem di = (SFE_DataItem) sh.DataItemList.get(0);
      sSJXBS = new String(di.GetDataCaption()).trim();
      //DataProcessLog.WriteLog("上送数据项为：" + sSJXBS);
    }
    if (!sSJXBS.equals("")) {
      for (int k = 0; k < DeviceTaskInfo.size(); k++) {
        TaskInfo ti = (TaskInfo) DeviceTaskInfo.get(k);
        if (ti.MeasuredPoint == iCLDXH) {
          for (int i = 0; i < ti.CommandList.size(); i++) {
            String sTemp = (String) ti.CommandList.get(i);
            //DataProcessLog.WriteLog("比对任务号为：" + ti.TaskNo + "比对数据项为：" + sTemp);
            if (sSJXBS.equals(sTemp)) {
              iTaskNo = ti.TaskNo;
              //DataProcessLog.WriteLog("数据项为：" + sSJXBS + " 的任务数据对应的任务号为：" +
              //                        iTaskNo);
              break;
            }
          }
          if (iTaskNo != 0) {
            break;
          }
        }
      }
    }
    if (iTaskNo != 0) {
      l.clear();
      iCount = DataList.size();
      SFE_HistoryData[] HistoryList = new SFE_HistoryData[iCount];
      for (int i = 0; i < iCount; i++) {
        HistoryList[i] = new SFE_HistoryData(); //历史数据结构
        HistoryList[i] = (SFE_HistoryData) DataList.get(i);
        HistoryList[i].SetTaskNo(iTaskNo);
        l.add(HistoryList[i]);
      }
      return l;
    }
    else {
      return DataList;
    }
  }
  
  
  public void UpdateCLDXX(String DataCaption, String DataContent, int CLDBH,
			String ZDLJDZ, int CLDXZ) {
		String sSQL = "";
		// System.out.println("DataCaption:"+DataCaption);
		if (DataCaption.equals("4A20")) {
			sSQL = "UPDATE RUN_ZTLYC SET DBYXZTZ='" + DataContent
					+ "' WHERE CLDXH=" + CLDBH + " and ZDLJDZ='" + ZDLJDZ
					+ "' and CLDXZ =" + CLDXZ;
		} else if (DataCaption.equals("4A21")) {
			sSQL = "UPDATE RUN_ZTLYC SET DWZTZ='" + DataContent
					+ "' WHERE CLDXH=" + CLDBH + " and ZDLJDZ='" + ZDLJDZ
					+ "' and CLDXZ =" + CLDXZ;
		} else if (DataCaption.equals("4A22")) {
			sSQL = "UPDATE RUN_ZTLYC SET ZXRZTZ='" + DataContent
					+ "' WHERE CLDXH=" + CLDBH + " and ZDLJDZ='" + ZDLJDZ
					+ "' and CLDXZ =" + CLDXZ;
		} else if (DataCaption.equals("4A25")) {
			sSQL = "UPDATE RUN_ZTLYC SET SYSLZTZ='" + DataContent
					+ "' WHERE CLDXH=" + CLDBH + " and ZDLJDZ='" + ZDLJDZ
					+ "' and CLDXZ =" + CLDXZ;
		} else if (DataCaption.equals("4A26")) {
			sSQL = "UPDATE RUN_ZTLYC SET KGZTZ='" + DataContent
					+ "' WHERE CLDXH=" + CLDBH + " and ZDLJDZ='" + ZDLJDZ
					+ "' and CLDXZ =" + CLDXZ;
		}
		try {
			if ((!sSQL.equals("")) && (sSQL != null)) {
				DataAccess_Ala.executeUpdate(sSQL);
			}
		} catch (Exception ex) {
		}
	}
  
  public ArrayList ExtractAlarmZTZ(String DataCaption, String NewZtz,
			String OldZtz) {
		ArrayList <String>alarmCodeList = new ArrayList<String>();
		String sNewZtz = "", sOldZtz = "";
		String sCodeTemp = "";
		if ((DataCaption.equals("4A23")) || (DataCaption.equals("4A24"))) {
			sNewZtz = DataSwitch.Fun2HexTo8Bin(NewZtz);
			for (int i = 7; i >= 0; i--) {
				if (sNewZtz.charAt(i) == '1') {
					sCodeTemp = "C6" + DataCaption.charAt(3)
							+ Integer.toString(8 - i);
					alarmCodeList.add(sCodeTemp);
				}
			}
		} else if ((DataCaption.equals("4A20")) || (DataCaption.equals("4A21"))
				|| (DataCaption.equals("4A22")) || (DataCaption.equals("4A25"))
				|| (DataCaption.equals("4A26"))) {
			sNewZtz = DataSwitch.Fun2HexTo8Bin(NewZtz);
			sOldZtz = DataSwitch.Fun2HexTo8Bin(OldZtz);
			for (int i = 7; i >= 0; i--) {
				if ((sNewZtz.charAt(i) == '1') && ((sOldZtz.charAt(i) == '0'))) { // 异常发生
					sCodeTemp = "C6" + DataCaption.charAt(3)
							+ Integer.toString(8 - i);
					alarmCodeList.add(sCodeTemp);
				} else if ((sNewZtz.charAt(i) == '0')
						&& ((sOldZtz.charAt(i) == '1'))) { // 异常恢复
					sCodeTemp = "R6" + DataCaption.charAt(3)
							+ Integer.toString(8 - i);
					alarmCodeList.add(sCodeTemp);
				}
			}
		}
		return alarmCodeList;
	}
  
  public ArrayList ExtractAlarmData(SFE_AlarmData AlarmData, String ZDLJDZ) {
		ArrayList <SFE_AlarmData>alarmInfoList = new ArrayList<SFE_AlarmData>();
		ArrayList alTemp = new ArrayList();
		SFE_AlarmData alarmDataTemp = new SFE_AlarmData();
		String sSQL = "";
		String sDwztz = "", sKgztz = "", sSyslztz = "", sZxztz = "", sDbztz = "";
		String sOldztz = "";
		int iCldbh = 0;
		int iCount = AlarmData.GetDataItemCount();
		int CLDXH = AlarmData.GetMeasuredPointNo();
		int CLDLX = AlarmData.GetMeasuredPointType();
		String YCDM = new String(AlarmData.GetAlarmCode()).trim();
		String YCFSSJ = new String(AlarmData.GetAlarmDateTime()).trim();

		if (YCDM.equals("状态字")) {
			sSQL = "SELECT A.DBYXZTZ,A.DWZTZ,A.ZXRZTZ,A.KGZTZ,A.SYSLZTZ,A.CLDXH FROM RUN_ZTLYC A WHERE A.ZDLJDZ='"
					+ ZDLJDZ
					+ "' AND A.CLDXH="
					+ CLDXH
					+ " AND A.CLDXZ="
					+ CLDLX;
			ResultSet rset = null;
			try {
				rset = DataAccess_Ala.executeQuery(sSQL);
				if (!rset.next()) {
					if (!AlarmData.DataItemList.isEmpty()) {
						SFE_DataItem DataItem = new SFE_DataItem();
						for (int i = 0; i < iCount; i++) {
							DataItem = (SFE_DataItem) AlarmData.DataItemList
									.get(i);
							String sDataCaption = new String(DataItem
									.GetDataCaption());
							String sDataContent = new String(DataItem
									.GetDataContent());
							if (sDataCaption.equals("4A20")) {
								sDbztz = sDataContent;
							} else if (sDataCaption.equals("4A21")) {
								sDwztz = sDataContent;
							} else if (sDataCaption.equals("4A22")) {
								sZxztz = sDataContent;
							} else if (sDataCaption.equals("4A25")) {
								sSyslztz = sDataContent;
							} else if (sDataCaption.equals("4A26")) {
								sKgztz = sDataContent;
							}
						}
					}
					String sSQLTemp = "INSERT INTO RUN_ZTLYC (ZDLJDZ,CLDXH,CLDXZ,DBYXZTZ,DWZTZ,ZXRZTZ,KGZTZ,SYSLZTZ)";
					sSQLTemp = sSQLTemp + " values('" + ZDLJDZ + "'," + CLDXH
							+ "," + CLDLX + ",'" + sDbztz + "','" + sDwztz
							+ "','" + sZxztz + "','" + sKgztz + "','"
							+ sSyslztz + "')";
					DataAccess_Ala.executeUpdate(sSQLTemp);
				}
				sDwztz = rset.getString("DWZTZ"); // 电网状态字
				sDbztz = rset.getString("DBYXZTZ"); // 电表运行状态字
				sKgztz = rset.getString("KGZTZ"); // 开盖状态字
				sZxztz = rset.getString("ZXRZTZ"); // 周休状态字
				sSyslztz = rset.getString("SYSLZTZ"); // 失压失流状态字
				iCldbh = rset.getInt("CLDXH");
			} catch (Exception ex) {
			} finally {
				try {
					rset.close();
					DataAccess_Ala.close();
				} catch (SQLException ex1) {
				}
			}
			if (!AlarmData.DataItemList.isEmpty()) {
				SFE_DataItem DataItem = new SFE_DataItem();
				for (int i = 0; i < iCount; i++) {
					DataItem = (SFE_DataItem) AlarmData.DataItemList.get(i);
					String sDataCaption = new String(DataItem.GetDataCaption());
					String sDataContent = new String(DataItem.GetDataContent());
					if (sDataCaption.equals("4A20")) {
						sOldztz = sDbztz;
					} else if (sDataCaption.equals("4A21")) {
						sOldztz = sDwztz;
					} else if (sDataCaption.equals("4A22")) {
						sOldztz = sZxztz;
					} else if (sDataCaption.equals("4A25")) {
						sOldztz = sSyslztz;
					} else if (sDataCaption.equals("4A26")) {
						sOldztz = sKgztz;
					}
					if (sOldztz.equals("")) { // 如果数据库里没有初始化数据则默认为00；
						sOldztz = "00";
					}
					UpdateCLDXX(sDataCaption, sDataContent, iCldbh, ZDLJDZ,
							CLDLX);
					alTemp = ExtractAlarmZTZ(sDataCaption, sDataContent,
							sOldztz);
					if (!alTemp.isEmpty()) {
						for (int j = 0; j < alTemp.size(); j++) {
							String stemp = (String) alTemp.get(j);
							alarmDataTemp = new SFE_AlarmData();
							alarmDataTemp.SetAlarmCode(stemp);
							alarmDataTemp.SetMeasuredPointNo(CLDXH);
							alarmDataTemp.SetMeasuredPointType(CLDLX);
							alarmDataTemp.SetAlarmDateTime(YCFSSJ);
							alarmDataTemp.SetDataItemCount(0);
							alarmInfoList.add(alarmDataTemp);
						}
					}
				}
			}
		} else {
			if (YCDM.equals("C140")) { // 停电报警
				sSQL = "UPDATE DA_ZDGZ SET YXZT=2 WHERE ZDLJDZ='" + ZDLJDZ
						+ "'";
				try {
					DataAccess_Ala.executeUpdate(sSQL);
				} catch (Exception ex3) {
				} finally {
					try {
						DataAccess_Ala.close();
					} catch (Exception ex1) {
					}
				}
			} else if (YCDM.equals("R140")) { // 来电报警
				sSQL = "UPDATE DA_ZDGZ SET YXZT=1 WHERE ZDLJDZ='" + ZDLJDZ
						+ "'";
				try {
					DataAccess_Ala.executeUpdate(sSQL);
				} catch (Exception ex2) {
				} finally {
					try {
						DataAccess_Ala.close();
					} catch (Exception ex1) {
					}
				}
			}
			alarmInfoList.add(AlarmData);
		}

		return alarmInfoList;
	}
  
  private ArrayList ExtractAlarmDataInfo(SFE_DataListInfo DataListInfo,
			String ZDLJDZ) {
		ArrayList <SFE_AlarmData>alarmList = new ArrayList<SFE_AlarmData>();
		ArrayList alarmListTemp = new ArrayList();
		SFE_AlarmData AlarmDataTemp = new SFE_AlarmData();
		SFE_AlarmData alarmTemp = new SFE_AlarmData();
		int iCount = DataListInfo.DataList.size();
		for (int i = 0; i < iCount; i++) {

			AlarmDataTemp = new SFE_AlarmData(); // 历史数据结构
			AlarmDataTemp = (SFE_AlarmData) DataListInfo.DataList.get(i);
			alarmListTemp = ExtractAlarmData(AlarmDataTemp, ZDLJDZ);
			for (int j = 0; j < alarmListTemp.size(); j++) {
				alarmTemp = new SFE_AlarmData();
				alarmTemp = (SFE_AlarmData) alarmListTemp.get(j);
				alarmList.add(alarmTemp);
			}
		}
		return alarmList;
	}
  

// 对于上送的数据，根据数据类型进行处理，如果是上送的任务数据或者异常数据，则提交JMS；
  private void DealWithFrameData(StructReceiveData rd) {
    try {
    	//预付费的表计余额异常，需要经过转换生成异常数据
    	
      if (rd.DataType == Glu_ConstDefine.SJLX_YCSJZC ||
          rd.DataType == Glu_ConstDefine.SJLX_YCSJZD) {
        //异常数据提交到异常处理的JMS队列中
        StructAutoCommitData acd = new StructAutoCommitData();
        acd.TerminalLogicAdd = rd.TerminalLogicAdd.toCharArray();
        
        //System.out.println("====++++++++++DealWithFrameData LJDZ="+acd.TerminalLogicAdd.toString()+"");
        //从终端本地列表中获取终端的真实规约号，避免在类似规约的情况下，采用帧分析后得到的大类规约号，而导致数据无法解释出来
        TerminalInfo ti = (TerminalInfo) CommunicationServerConstants.
            TerminalLocalList.get(new String(rd.TerminalLogicAdd).trim());
        if (ti != null) {
          acd.TerminalProtocal = ti.TerminalProtocol;
        }
        else {
          acd.TerminalProtocal = rd.TerminalProtocal;
          //System.out.println("====++++++++++DealWithFrameData 从TerminalLocalList队列中未取到终端信息");
        }
        
        //System.out.println("====++++++++++DealWithFrameData acd.TerminalProtocal="+Integer.toString(acd.TerminalProtocal));
        
       
        acd.StationNo = rd.StationNo;
        acd.FrameSeq = rd.FrameSeq;
        acd.CommandSeq = rd.CommandSeq;
        acd.DataType = rd.DataType;
        acd.ControlCode = rd.ControlCode.toCharArray();
        acd.FunctionCode = rd.FunctionCode.toCharArray();
        acd.ManufacturerCode = rd.ManufacturerCode.toCharArray();
        acd.FrameLength = rd.FrameLength;
        acd.FrameData = rd.FrameData.toCharArray();
        try {        	
            //        	<二>、解析规约数据区内容
            SFE_AlarmData AlarmDataList[];
            SFE_DataListInfo DataInfo = null;
            try {
              //SFE_DataItem DataItem = new SFE_DataItem(); //数据项
            	CommunicationServerConstants.Trc1.TraceLog("Call FrameDataAreaExplain, FrameData=" 
            			+ new String(acd.FrameData) + " ProtocolNo=" + acd.TerminalProtocal + " ZDLJDZ=" + new String(acd.TerminalLogicAdd));
            	if(acd.TerminalProtocal == Glu_ConstDefine.GY_ZD_DLMS){
            		DataInfo = FrameDataExplain.IFE_ExplainDataAreaDLMS("".toCharArray(), acd.FrameData, acd.TerminalProtocal);
            	}else{
            		DataInfo = FrameDataExplain.IFE_ExplainDataArea(acd.FrameData,acd.TerminalLogicAdd,
            				acd.TerminalProtocal,acd.ControlCode,acd.FunctionCode);
            	}
              //dataProcessLog.WriteLog("DataInfo.ExplainResult:" + DataInfo.ExplainResult);
              //dataProcessLog.WriteLog("DataInfo.DataType:" + DataInfo.DataType);
            }
            catch (Exception ex2) {
            	CommunicationServerConstants.Log1.WriteLog("Error to call FrameDataAreaExplain:" + ex2.toString());
            }
            //fl.WriteLog("2==解释数据");

            //<三>、保存数据库
            try {
              if ( (DataInfo.ExplainResult == 0) && (DataInfo.DataType == 30)) {
                ArrayList alarmList = new ArrayList();
                String ZDLJDZ = new String(acd.TerminalLogicAdd);
                alarmList = ExtractAlarmDataInfo(DataInfo, ZDLJDZ);
                int iCount = alarmList.size();
                AlarmDataList = new SFE_AlarmData[iCount];
                for (int i = 0; i < iCount; i++) {
                  AlarmDataList[i] = new SFE_AlarmData(); //异常数据结构
                  AlarmDataList[i] = (SFE_AlarmData) alarmList.get(i);
                }
                //保存数据库
                String sZDLJDZ = GetMAddress(AlarmDataList[0].GetMeasuredPointNo(),new String(acd.TerminalLogicAdd));
                
                DataAccess_Ala.SaveAlarmData(sZDLJDZ.toCharArray(), DWDM, AlarmDataList, iCount);
              }
              else {
            	  CommunicationServerConstants.Log1.WriteLog("FrameDataAreaExplain error!ExplainResult:" +
                                        Integer.toString(DataInfo.ExplainResult) +
                                        " DataType:" + Integer.toString(DataInfo.DataType));
              }
            }
            catch (Exception ex1) {
            	CommunicationServerConstants.Log1.WriteLog("Save data error:" + ex1.toString());
            }
            //fl.WriteLog("3==保存数据");
          
        }
        catch (Exception ex1) {

        	CommunicationServerConstants.Trc1.TraceLog("90、JMS Object Recreated!");
        }
        //ToJMSCount = ToJMSCount + 1;
        CommunicationServerConstants.Trc1.TraceLog("22、SendDataToJMSList DataType:" +
                                rd.DataType + " DataArea: " + rd.FrameData);
      }
      
      else if (rd.DataType == Glu_ConstDefine.SJLX_LSSJZC ||
               rd.DataType == Glu_ConstDefine.SJLX_LSSJZD) { //历史数据
        StructAutoCommitData acd = new StructAutoCommitData();
        acd.TerminalLogicAdd = rd.TerminalLogicAdd.toCharArray();
        //从终端本地列表中获取终端的真实规约号，避免在类似规约的情况下，采用帧分析后得到的大类规约号，而导致数据无法解释出来
        TerminalInfo ti = (TerminalInfo) CommunicationServerConstants.
            TerminalLocalList.get(new String(rd.TerminalLogicAdd).trim());
        if (ti != null) {
          acd.TerminalProtocal = ti.TerminalProtocol;
        }
        else {
          acd.TerminalProtocal = rd.TerminalProtocal;
        }
        acd.StationNo = rd.StationNo;
        acd.FrameSeq = rd.FrameSeq;
        acd.CommandSeq = rd.CommandSeq;
        acd.DataType = rd.DataType;
        acd.ControlCode = rd.ControlCode.toCharArray();
        acd.FunctionCode = rd.FunctionCode.toCharArray();
        acd.ManufacturerCode = rd.ManufacturerCode.toCharArray();
        acd.FrameLength = rd.FrameLength;
        acd.FrameData = rd.FrameData.toCharArray();
        try {
        	//<二>、解析规约数据区内容
            SFE_HistoryData HistoryList[];
            SFE_DataListInfo DataInfo = null;
            try {
              //SFE_DataItem DataItem = new SFE_DataItem(); //数据项

              DataInfo = FrameDataExplain.IFE_ExplainDataArea(acd.FrameData,acd.TerminalLogicAdd,
                  acd.TerminalProtocal, acd.ControlCode, acd.FunctionCode);
              CommunicationServerConstants.Trc1.TraceLog("22-1、FrameDataExplain  GYH=" +
            		  acd.TerminalProtocal);
            }
            catch (Exception ex2) {
              //DataProcessLog.WriteLog("调用规约数据区解析出错:" + ex2.toString());
            }
            
            //<三>、保存数据库        	
            try {
            	CommunicationServerConstants.Trc1.TraceLog("22-2、SaveData ExplainResult="+DataInfo.ExplainResult);
                if ( (DataInfo.ExplainResult == 0) && (DataInfo.DataType == 20 || DataInfo.DataType == 10)) {
                  int iCount = DataInfo.DataList.size();
                  HistoryList = new SFE_HistoryData[iCount];
                  CommunicationServerConstants.Trc1.TraceLog("22-0、iCount="+iCount);	
                  Calendar cTime = Calendar.getInstance();
 				  SimpleDateFormat formatter=new SimpleDateFormat("yyyyMMddHHmmss");
 				  String SJSJ="";
 				  SJSJ=formatter.format(cTime.getTime());
                  for (int i = 0; i < iCount; i++) {
                    HistoryList[i] = new SFE_HistoryData(); //历史数据结构
                	if (DataInfo.DataType == 10) {
                		SFE_NormalData NormalData = new SFE_NormalData();
                		NormalData = (SFE_NormalData) DataInfo.DataList.get(i);
                		for(int j = 0; j < NormalData.GetDataItemCount(); j++){
                			SFE_DataItem DataItem = new SFE_DataItem();
                			DataItem = (SFE_DataItem)NormalData.DataItemList.get(j);
                			HistoryList[i].DataItemAdd(new String(DataItem.GetDataCaption()), new String(DataItem.GetDataContent()));	
                		}
                		HistoryList[i].SetMeasuredPointNo(NormalData.GetMeasuredPointNo());
                		HistoryList[i].SetMeasuredPointType(NormalData.GetMeasuredPointType());
                		HistoryList[i].SetTaskNo(0);
                		HistoryList[i].SetTaskDateTime(SJSJ);
                	}
                  }
                  if (ti != null) {
                    if (ti.TerminalProperty == 2 || ti.TerminalProperty == 01) { //只对于终端设备的任务号需要进行重新的匹配
                    	List adjustList = null;
                    	if (DataInfo.DataType == 20){
                        	adjustList = AdjustTaskNo(DataInfo.DataList);
                        	iCount = adjustList.size();
                            HistoryList = new SFE_HistoryData[iCount];
                            for (int i = 0; i < iCount; i++) {
                            	HistoryList[i] = new SFE_HistoryData(); //历史数据结构
                            	HistoryList[i] = (SFE_HistoryData) adjustList.get(i);
                            }
                        }                        
                    }
                  }
                  
                  //保存数据库
                  //DataProcessLog.WriteLog("3==保存数据");
                  String sZDLJDZ = GetMAddress(HistoryList[0].GetMeasuredPointNo(),new String(acd.TerminalLogicAdd));
                  DataAccess_His.SaveHistoryData(sZDLJDZ.toCharArray(), acd.TerminalProtocal,HistoryList,
                                             iCount);
                  CommunicationServerConstants.Trc1.TraceLog("22-3、SaveDataSuccess  ZDLJDZ:"+sZDLJDZ +" CLDXH:"+HistoryList[0].GetMeasuredPointNo());
                  //将对应的漏点进行更新
                  DataAccess_His.DeleteMissingPoint(acd.TerminalLogicAdd, HistoryList, iCount);
                  //DeleteTaskMissingInfo(s, HistoryList, iCount);
                }
              }
              catch (Exception ex1) {
            	  CommunicationServerConstants.Log1.WriteLog("Save data error:" + ex1.toString());
              }
        }
        catch (Exception ex2) {
        	CommunicationServerConstants.Trc1.TraceLog("91、JMS Object Recreated!");
        }
      } 
    }
    catch (Exception ex) {
    	CommunicationServerConstants.Log1.WriteLog(
          "MessageExchange:DealWithJMSListData() error,error is " +
          ex.toString());
    }
  }
	public String GetMAddress(int CLDXH,String ZDLJDZ){
		String sMAddress = "";
		SwitchMPInfo mpi = null;
		mpi = (SwitchMPInfo) CommunicationServerConstants.MPInfoList
				.get("" + CLDXH + "#"+ ZDLJDZ);
		if (mpi != null){
			sMAddress = mpi.MAddress;
		}else {
			sMAddress = ZDLJDZ;
		}
		return sMAddress;
	}

  /**
   * <p>Title: </p>
   *
   * <p>Description: </p>
   *
   * <p>Copyright: Copyright (c) 2005</p>
   *
   * <p>Company: </p>
   * @author not attributable
   * @version 1.0
   */
  private class JMSCommitThread
      extends Thread {
    JMSCommitThread() {
      //20090924 此版本暂时不采用JMS队列
    	/*
    	JMSConnectTimes = 0;*/
    }

    public void run() {
      while (true) {
        try {
        	//此版本不采用JMS队列形式，直接解析并保存          	
        	if (CommunicationServerConstants.GlobalReceiveJMSList.size() > 0) {
                StructReceiveData rd = (StructReceiveData)
                    CommunicationServerConstants.GlobalReceiveJMSList.get(0);
                CommunicationServerConstants.GlobalReceiveJMSList.remove(0);
                DealWithFrameData(rd);
        	}
        }
        catch (Exception e) {
        	CommunicationServerConstants.Log1.WriteLog("JMSCommitThread Throw a excpetion during execute.");
        	e.printStackTrace();
        }
        try {
          Thread.sleep(1);
        }
        catch (InterruptedException ex) {
        }
      }
    }
  }

  class ListenThread
      extends Thread {
    public void run() {
      while (true) {
        int n = 0;
        try {
          try {
            n = sc.selector.select(); //通过select函数判断当前socket是否存在事件需要处理
          }
          catch (IOException ex) {
        	  CommunicationServerConstants.Log1.WriteLog(
                "MessageExchange:ListenThread sc.selector.select() IOerror,error is " +
                ex.toString());
          }
        }
        catch (Exception ex2) {
          try {
            sc.CloseListen();
            sc.init(Port);
          }
          catch (Exception ex3) {
          }
        }
        if (n == 0) {
          continue; //select结果为0，则表示没有socket事件需要处理，继续执行select
        }
        else if (n != 0) {
          Iterator it = sc.selector.selectedKeys().iterator(); //返回结果不为0，则对于取到的socket事件，一个个的循环处理
          while (it.hasNext()) {
            try {
              SelectionKey key = (SelectionKey) it.next();
              //服务端的socket事件只需要处理客户端的接入以及数据提交即可，发送到客户端的操作不在该部分实现
              if (key.isValid() && key.isAcceptable()) {
                ServerSocketChannel server = (ServerSocketChannel) key.channel();
                SocketChannel channel = null;
                try {
                  channel = server.accept();
                }
                catch (IOException ex1) {
                	CommunicationServerConstants.Log1.WriteLog(
                      "MessageExchange:ListenThread server.accept() IOerror,error is " +
                      ex1.toString());
                }
                registerChannel(sc.selector, channel, SelectionKey.OP_READ);
              }
              if (key.isValid() && key.isReadable()) {
                //收到数据后，对于接入的链路加入到链路维护队列中，用于定位返回数据的链路对象
                AddLinkToList(sc.selector, (SocketChannel) key.channel());
                receiveData(key);
              }
              it.remove();
              try {
                Thread.sleep(1);
              }
              catch (InterruptedException ex3) {
              }
            }
            catch (Exception ex5) {
            	CommunicationServerConstants.Log1.WriteLog(
                  "MessageExchange:ListenThreadRun() error,error is " +
                  ex5.toString());
            }
          }
        }
      }
    }
  }

  class TerminalCommandNoList {
    public String TerminalAddress = ""; //终端逻辑地址
    public int TerminalProtocol = 0; //终端规约号
    public int CommandNo = 0; //终端当前命令序号
    public int CommuType = 10; //终端通讯模式：10 一般；20 应答
    public int SentFlag = 10; //命令下发标志：10 无下发；20 有下发
  }

  class TerminalAddCorreCommandSeq {
    public int ApplicationID; //应用ID
    public char[] TerminalAddress; //终端逻辑地址
    public int TerminalProtocol; //终端规约号
    public int CommandCount; //命令个数
    public int[] CommandSeq; //命令序号列表
    public int CommandType; //命令类型
    public Calendar LastUpdateTime; //最近更新时间，用来删除本条记录的依据
  }

  class TerminalOnLineStatus { //终端在线状态结构，用于返回到上层
    public String TerminalAddress; //终端逻辑地址
    public int OnLineStatus; //在线状态，10：在线，20：掉线
  }
}

class SocketLinkList {
  private String SocketAddress = "";
  private int SocketPort = 0;
  private SocketChannel Channel = null;
  private Calendar LastCommDate = null;
  private List TerminalCorreCommandSeqList = null;
  //private int ApplicationID = 0;
  private int TaskNo = 0;
  private int TaskCount = 0;
  private int AlarmCount = 0;
  private boolean ListFlag = false;

  public void SetTerminalCorreCommandSeqList(List l) {
    this.TerminalCorreCommandSeqList = l;
  }

  public List GetTerminalCorreCommandSeqList() {
    try {
      return this.TerminalCorreCommandSeqList;
    }
    catch (Exception ex) {
      return null;
    }
  }

  public void SetSocketAddress(String Ip) {
    this.SocketAddress = Ip;
  }

  public String GetSocketAddress() {
    return this.SocketAddress;
  }

  public void SetSocketPort(int Port) {
    this.SocketPort = Port;
  }

  public int GetSocketPort() {
    return this.SocketPort;
  }

  public void SetSocketChannel(SocketChannel Channel) {
    this.Channel = Channel;
  }

  public SocketChannel GetSocketChannel() {
    return this.Channel;
  }

  public void SetLastCommDate(Calendar d) {
    this.LastCommDate = d;
  }

  public Calendar GetLastCommDate() {
    return this.LastCommDate;
  }

  public void SetTaskNo(int TaskID) {
    this.TaskNo = TaskID;
  }

  public int GetTaskNo() {
    return this.TaskNo;
  }

  public void SetTaskCount(int TaskCount) {
    this.TaskCount = TaskCount;
  }

  public int GetTaskCount() {
    return this.TaskCount;
  }

  public void SetAlarmCount(int AlarmCount) {
    this.AlarmCount = AlarmCount;
  }

  public int GetAlarmCount() {
    return this.AlarmCount;
  }

  public void SetListFlag(boolean Flag) {
    this.ListFlag = Flag;
  }

  public boolean GetListFlag() {
    return this.ListFlag;
  }
}

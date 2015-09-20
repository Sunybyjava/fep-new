package com.chooshine.fep.fas.realtimecom;

import java.io.*;
import java.util.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.Calendar;

import com.chooshine.fep.ConstAndTypeDefine.FileLogger;
import com.chooshine.fep.communicate.utils;

/**
 * <p>Title: 实时通讯模块</p>
 *
 * <p>Description: 提供接口由数据采集模块调用，负责和通讯服务的通讯，进行数据的收发工作</p>
 *
 * <p>Copyright: </p>
 *
 * <p>Company: </p>
 *
 * @author 
 * @version 
 */
public class RealTimeCommunication {
  private Selector selector;
  private SocketChannel client;
  private List <RealTimeCommunicationInfo>RealTimeList; //下发和上送数据的对应关系
  private List <SendInfo>SendInfoList; //保存下发内容的队列

  private String HostName = ""; //连接的通讯服务的IP
  private int Port = 0; //连接通讯服务的端口
  private byte[] data = null; //发送内容的byte数组
  private int ReConnectTimes = 0; //重连次数
  private int Sequence = 1; //流水号
  private boolean FormData = false; //要发送的内容是否组成完成的标志
  private boolean AckBack = true; //确认信号是否返回的标志
  public boolean ConnectFlag = false; //连接前置机是否成功的标志，决定是否需要上层重新创建新对象
  private boolean FStop = false;
  private FileLogger fl = null;
  private MonitorThread mh = null;
  private RealTimeCommunicationInfoListThread rh = null;
  private boolean GetTerminalOnLineStatus = false;
  private boolean SetTerminalOnLineStatus = false;
  private boolean GetTerminalOnLineStatusBack = false;
  private boolean BuildCertainTerminalInfo = false;
  private String TerminalAddr = "";
  private List <TerminalOnLineStatus>TerminalStatusList = new LinkedList<TerminalOnLineStatus>();
  private Calendar LastTime = null;
  public RealTimeCommunication() {
    try {
      jbInit();
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  public RealTimeCommunication(String hostName, int port) { //重构构造函数，传入Ip和端口
    fl = new FileLogger("RealTimeCommunication");
    ReConnectTimes = 1;
    HostName = hostName;
    Port = port;
    LastTime = Calendar.getInstance();
  }

  public void Connect() {
    if (ConnectFlag) { //如果当前连接还存在需要先断开后，再重新连接登录
      try {
        DisConnect();
      }
      catch (Exception ex) {
      }
    }
    FStop = false;
    ConnectFlag = true;
    InitSocket(); //初始化socket连接
    RealTimeList = Collections.synchronizedList(new LinkedList<RealTimeCommunicationInfo>()); //创建对应队列
    SendInfoList = Collections.synchronizedList(new LinkedList<SendInfo>()); //创建对应队列
    mh = new MonitorThread(); //创建监听线程
    mh.start(); //启动线程
    rh = new RealTimeCommunicationInfoListThread(); //创建实时数据队列的维护线程
    rh.start(); //启动线程
    while (!ConnectFlag) {
      try {
        Thread.sleep(1);
      }
      catch (InterruptedException ex1) {
      }
    }
  }

  public void DisConnect() {
    try {
      try {
        client.socket().setSoLinger(true, 0);
      }
      catch (SocketException ex) {
      }
    }
    catch (Exception ex1) {
    }
    BuildDisConnectAndSend();
    RealTimeList = Collections.synchronizedList(new LinkedList<RealTimeCommunicationInfo>()); //创建对应队列
    SendInfoList = Collections.synchronizedList(new LinkedList<SendInfo>()); //创建对应队列
    FStop = true;
    ConnectFlag = false;
    try {
      Thread.sleep(100);
    }
    catch (InterruptedException ex2) {
    }
  }

  private void InitSocket() {
    try {
      selector = Selector.open();
      client = SocketChannel.open();
      InetSocketAddress isa = new InetSocketAddress(HostName, Port); //socket连接的参数初始化
      client.configureBlocking(false); //配置socket是否采用阻塞通讯
      client.connect(isa); //发出请求连接
      client.register(selector, SelectionKey.OP_CONNECT); //表明现在处于连接请求时间
    }
    catch (Exception ex) {
    }
  }

  private void AddCallInfoToList(int AppID, String TerminalAddr, int GYH,
                                 int CommandID,
                                 int ReadCount) {
    switch (CommandID) {
      case 10: {
        RealTimeCommunicationInfo rtci = new RealTimeCommunicationInfo();
        rtci.AppID = AppID;
        rtci.TerminalAddr = TerminalAddr.trim();
        rtci.BackFlag = false;
        rtci.GYH = GYH;
        rtci.AddInTime = Calendar.getInstance();
        RealTimeList.add(rtci); //新增内容只需要保存应用ID和终端逻辑地址来比对
        break;
      }
      case 20: {
        for (int i = 0; i < ReadCount; i++) {
          RealTimeCommunicationInfo rtci = new RealTimeCommunicationInfo();
          rtci.AppID = AppID;
          rtci.TerminalAddr = TerminalAddr.trim();
          rtci.BackFlag = false;
          rtci.GYH = GYH;
          rtci.AddInTime = Calendar.getInstance();
          RealTimeList.add(rtci); //新增内容只需要保存应用ID和终端逻辑地址来比对
        }
        break;
      }
      case 30: {
        for (int i = 0; i < ReadCount; i++) {
          RealTimeCommunicationInfo rtci = new RealTimeCommunicationInfo();
          rtci.AppID = AppID;
          rtci.TerminalAddr = TerminalAddr.trim();
          rtci.BackFlag = false;
          rtci.GYH = GYH;
          rtci.AddInTime = Calendar.getInstance();
          RealTimeList.add(rtci); //新增内容只需要保存应用ID和终端逻辑地址来比对
        }
        break;
      }
    }
  }

  private void BuildConnectAndSend(SelectionKey key) {
    ByteBuffer bBuffer = ByteBuffer.allocate(5000);
    FormData = false; //组成通讯内容开始，设置标志为无效
    data = null;
    byte[] MsgLength = new byte[4];
    data = new byte[12];
    MsgLength = utils.int2byte(12);
    //消息头部分的消息总长度
    data[0] = MsgLength[0];
    data[1] = MsgLength[1];
    data[2] = MsgLength[2];
    data[3] = MsgLength[3];
    //消息头部分的消息类型代码
    data[4] = 0;
    data[5] = 0;
    data[6] = 0;
    data[7] = 13;
    //消息头部分的消息序号
    byte[] MsgSequence = new byte[4];
    MsgSequence = utils.int2byte(Sequence);
    data[8] = MsgSequence[0];
    data[9] = MsgSequence[1];
    data[10] = MsgSequence[2];
    data[11] = MsgSequence[3];
    FormData = true;
    Sequence = Sequence + 1;
    if ( (data != null) && (FormData)) { //发送数据的条件：缓存中有数据并且消息组成完成
      SocketChannel keyChannel = (SocketChannel) key.channel(); //通过对应的通道来写入数据
      bBuffer.clear();
      bBuffer.put(data);
      bBuffer.flip();
      try {
        keyChannel.write(bBuffer); //如果在这里出现异常，则说明无法连接成功
      }
      catch (IOException ex) {
      }
      bBuffer.clear();
      //data = null; //清空要发送的数据，保证真正要发送的数据不会丢失
    }
    return;
  }

  private void BuildDisConnectAndSend() {
    FormData = false; //组成通讯内容开始，设置标志为无效
    ByteBuffer bBuffer = ByteBuffer.allocate(5000);
    data = null;
    byte[] MsgLength = new byte[4];
    data = new byte[12];
    MsgLength = utils.int2byte(12);
    //消息头部分的消息总长度
    data[0] = MsgLength[0];
    data[1] = MsgLength[1];
    data[2] = MsgLength[2];
    data[3] = MsgLength[3];
    //消息头部分的消息类型代码
    data[4] = 0;
    data[5] = 0;
    data[6] = 0;
    data[7] = 14;
    //消息头部分的消息序号
    byte[] MsgSequence = new byte[4];
    MsgSequence = utils.int2byte(Sequence);
    data[8] = MsgSequence[0];
    data[9] = MsgSequence[1];
    data[10] = MsgSequence[2];
    data[11] = MsgSequence[3];
    FormData = true;
    Sequence = Sequence + 1;
    if ( (data != null) && (FormData)) { //发送数据的条件：缓存中有数据并且消息组成完成
      bBuffer.clear();
      bBuffer.put(data);
      bBuffer.flip();
      try {
        client.write(bBuffer); //如果在这里出现异常，则说明无法连接成功
      }
      catch (Exception ex) {
      }
      try {
        client.socket().close();
        client.close();
        selector.close();
        FStop = true;
        ConnectFlag = false;
        //utils.PrintDebugMessage("实时通讯主动断开和通讯服务的连接", "D");
      }
      catch (Exception ex1) {
      }
      bBuffer.clear();
      //data = null; //清空要发送的数据，保证真正要发送的数据不会丢失
    }
    return;
  }

//根据传入的内容组成要发送的内容发送到通讯服务
  private void BuildSendFrameAndSend(int AppID, char[] TerminalAddr, int GYH,
                                     int TXFS, int RWH, int RWDS, int YCDS,
                                     char[] GNM, int SJQCD, char[] SJQNR,
                                     SelectionKey key, boolean SMS,
                                     boolean SelfDefined, int Priority,
                                     int ArithmeticNo) throws Exception {
    ByteBuffer bBuffer = ByteBuffer.allocate(5000);
    if (SMS) { //发送短信处理
      FormData = false; //组成通讯内容开始，设置标志为无效
      data = null;
      byte[] MsgLength = new byte[4];
      if (GYH == 10) {
        SJQCD = SJQCD * 2;
        data = new byte[12 + 23 + SJQCD];
        MsgLength = utils.int2byte(12 + 23 + SJQCD);
      }
      else {
        data = new byte[12 + 21 + SJQNR.length];
        MsgLength = utils.int2byte(12 + 21 + SJQNR.length);
      }
      //消息头部分的消息总长度
      data[0] = MsgLength[0];
      data[1] = MsgLength[1];
      data[2] = MsgLength[2];
      data[3] = MsgLength[3];
      //消息头部分的消息类型代码
      data[4] = 0;
      data[5] = 0;
      data[6] = 0;
      data[7] = 10;
      //消息头部分的消息序号
      byte[] MsgSequence = new byte[4];
      MsgSequence = utils.int2byte(Sequence);
      data[8] = MsgSequence[0];
      data[9] = MsgSequence[1];
      data[10] = MsgSequence[2];
      data[11] = MsgSequence[3];
      //消息体部分的目标手机号码
      byte[] bTemp = new byte[20];
      byte[] bMBHM = new String(TerminalAddr).getBytes();
      for (int i = 0; i < bMBHM.length; i++) {
        bTemp[i] = bMBHM[i];
      }
      for (int i = 0; i < 15; i++) {
        data[12 + i] = bTemp[i];
      }
      //消息体部分的消息类型
      bTemp = new byte[2];
      byte[] bTXLX = new byte[2];
      if (GYH < 15) { //只有小于15的数据需要特殊处理，其他都可以由统一函数实现
        bTXLX[1] = 0;
        bTXLX[0] = (byte) GYH;
      }
      else {
        bTXLX = utils.str2bytes(Integer.toHexString(GYH));
      }
      for (int i = 0; i < bTXLX.length; i++) {
        bTemp[i] = bTXLX[i];
      }
      data[27] = bTemp[1]; //数据区长度的字段需要做颠倒工作
      data[28] = bTemp[0];
      //消息体部分的消息长度
      bTemp = new byte[2];
      byte[] bSJQCD = new byte[2];
      if (SJQCD < 15) { //只有小于15的数据需要特殊处理，其他都可以由统一函数实现
        bSJQCD[1] = 0;
        bSJQCD[0] = (byte) SJQCD;
      }
      else {
        bSJQCD = utils.str2bytes(Integer.toHexString(SJQCD));
      }
      for (int i = 0; i < bSJQCD.length; i++) {
        bTemp[i] = bSJQCD[i];
      }
      if (SJQCD < 256) {
        data[29] = bTemp[1]; //数据区长度的字段需要做颠倒工作
        data[30] = bTemp[0];
      }
      else {
        data[29] = bTemp[0];
        data[30] = bTemp[1];
      }
      //消息体部分的消息内容
      byte[] bSJQNR = new byte[SJQCD];
      bSJQNR = new String(SJQNR).getBytes();
      if (bSJQNR.length > SJQCD) {
        for (int i = 0; i < SJQCD; i++) {
          data[31 + i] = bSJQNR[i];
        }
      }
      else {
        for (int i = 0; i < bSJQNR.length; i++) {
          data[31 + i] = bSJQNR[i];
        }
      }
      //消息体部分的短信通道
      data[31 + SJQCD] = (byte) TXFS;
      //消息体部分的智能选择
      data[32 + SJQCD] = 1;
      FormData = true;
      Sequence = Sequence + 1;
      if ( (data != null) && (FormData)) { //发送数据的条件：缓存中有数据并且消息组成完成
        SocketChannel keyChannel = (SocketChannel) key.channel(); //通过对应的通道来写入数据
        bBuffer.clear();
        bBuffer.put(data);
        bBuffer.flip();
        keyChannel.write(bBuffer); //如果在这里出现异常，则说明无法连接成功
        bBuffer.clear();
        AckBack = false;
      }
      return;
    }
    else if (SelfDefined) { //发送自定义命令的处理
      FormData = false; //组成通讯内容开始，设置标志为无效
      data = null;
      data = new byte[12 + 30 + SJQNR.length + 1];
      byte[] MsgLength = new byte[4];
      MsgLength = utils.int2byte(12 + 30 + SJQNR.length + 1);
      data[0] = MsgLength[0];
      data[1] = MsgLength[1];
      data[2] = MsgLength[2];
      data[3] = MsgLength[3];

      data[4] = 0;
      data[5] = 0;
      data[6] = 0;
      data[7] = 12;

      byte[] MsgSequence = new byte[4];
      MsgSequence = utils.int2byte(Sequence);
      data[8] = MsgSequence[0];
      data[9] = MsgSequence[1];
      data[10] = MsgSequence[2];
      data[11] = MsgSequence[3];

      byte[] bAppID = new byte[4];
      bAppID = utils.int2byte(AppID);
      data[12] = bAppID[0];
      data[13] = bAppID[1];
      data[14] = bAppID[2];
      data[15] = bAppID[3];

      //应用ID
      byte[] bTemp = new byte[20];
      byte[] bZDLJDZ = new String(TerminalAddr).getBytes();
      for (int i = 0; i < bZDLJDZ.length; i++) {
        bTemp[i] = bZDLJDZ[i];
      }
      for (int i = 0; i < 20; i++) {
        data[16 + i] = bTemp[i];
      }
      //终端逻辑地址，需要补全20位
      byte[] bTXCS = new byte[1];
      if (TXFS < 15) { //只有小于15的数据需要特殊处理，其他都可以由统一函数实现
        bTXCS[0] = (byte) TXFS;
      }
      else {
        bTXCS = utils.str2bytes(Integer.toHexString(TXFS));
      }

      data[36] = bTXCS[0];
      //通讯方式，即终端当前通讯类型
      data[37] = 1;

      //发送自定义命令的命令序号
      byte[] bMLXH = new byte[1];
      if (RWH < 15) { //只有小于15的数据需要特殊处理，其他都可以由统一函数实现
        bMLXH[0] = (byte) RWH;
      }
      else {
        bMLXH = utils.str2bytes(Integer.toHexString(RWH));
      }
      data[38] = bMLXH[0];
      //规约号
      byte[] bGYH = new byte[1];
      if (GYH < 15) { //只有小于15的数据需要特殊处理，其他都可以由统一函数实现
        bGYH[0] = (byte) GYH;
      }
      else {
        bGYH = utils.str2bytes(Integer.toHexString(GYH));
      }
      data[39] = bGYH[0];
      //数据区长度
      bTemp = new byte[2];
      byte[] bSJQCD = new byte[2];
      if (SJQCD < 15) { //只有小于15的数据需要特殊处理，其他都可以由统一函数实现
        bSJQCD[1] = 0;
        bSJQCD[0] = (byte) SJQCD;
      }
      else {
        bSJQCD = utils.str2bytes(Integer.toHexString(SJQCD));
      }
      for (int i = 0; i < bSJQCD.length; i++) {
        bTemp[i] = bSJQCD[i];
      }
      if (SJQCD < 256) {
        data[40] = bTemp[1]; //数据区长度的字段需要做颠倒工作
        data[41] = bTemp[0];
      }
      else {
        data[40] = bTemp[0];
        data[41] = bTemp[1];
      }
      //数据区内容
      byte[] bSJQNR = new byte[SJQCD];
      bSJQNR = new String(SJQNR).getBytes();
      for (int i = 0; i < SJQCD; i++) {
        data[42 + i] = bSJQNR[i];
      }

      byte[] bYXJ = new byte[1];
      if (Priority < 15) {
        bYXJ[0] = (byte) Priority;
      }
      else {
        bYXJ = utils.str2bytes(Integer.toHexString(Priority));
      }
      data[42 + SJQCD] = bYXJ[0]; //优先级

      //到此内容组成完成
      AddCallInfoToList(AppID, new String(TerminalAddr), GYH, 10, 0); //保存终端信息和AppID到队列中
      FormData = true;
      Sequence = Sequence + 1;
      if ( (data != null) && (FormData)) { //发送数据的条件：缓存中有数据并且消息组成完成
        SocketChannel keyChannel = (SocketChannel) key.channel(); //通过对应的通道来写入数据
        bBuffer.clear();
        bBuffer.put(data);
        bBuffer.flip();
        keyChannel.write(bBuffer); //如果在这里出现异常，则说明无法连接成功
        bBuffer.clear();
        AckBack = false;
      }
      return;
    }
    else if (( GYH == 1000 )||( GYH == 1001 )||( GYH == 1100)){
    	FormData = false; //组成通讯内容开始，设置标志为无效
        data = null;
        data = new byte[12 + 28 + SJQNR.length];
        
        //消息长度
        byte[] MsgLength = new byte[4];
        MsgLength = utils.int2byte(12 + 28 + SJQNR.length );
        data[0] = MsgLength[0];
        data[1] = MsgLength[1];
        data[2] = MsgLength[2];
        data[3] = MsgLength[3];
        
        //消息类型
        if (( GYH == 1000 )||( GYH == 1100)){ //调用加密机
        	data[4] = 0;
            data[5] = 0;
            data[6] = 1;
            data[7] = 1;
        }
        else { //调用读卡器
        	data[4] = 0;
            data[5] = 0;
            data[6] = 1;
            data[7] = 2;
        }

        byte[] MsgSequence = new byte[4];
        MsgSequence = utils.int2byte(Sequence);
        data[8] = MsgSequence[0];
        data[9] = MsgSequence[1];
        data[10] = MsgSequence[2];
        data[11] = MsgSequence[3];
        //应用ID
        byte[] bAppID = new byte[4];
        bAppID = utils.int2byte(AppID);
        data[12] = bAppID[0];
        data[13] = bAppID[1];
        data[14] = bAppID[2];
        data[15] = bAppID[3];
        
        //用户户号
        byte[] bTemp = new byte[20];
        byte[] bZDLJDZ = new String(TerminalAddr).getBytes();
        for (int i = 0; i < bZDLJDZ.length; i++) {
          bTemp[i] = bZDLJDZ[i];
        }
        for (int i = 0; i < 20; i++) {
          data[16 + i] = bTemp[i];
        }
        //调加密机类型,0x10:国网;0x20:海兴
        if ((GYH == 1000) || (GYH == 1001)){
        	data[36] = 0;
            data[37] = 10;
        }
        else if (GYH == 1100){
        	data[36] = 0;
            data[37] = 20;
        }
        //数据区长度
        bTemp = new byte[2];
        byte[] bSJQCD = new byte[2];
        if (SJQCD < 15) { //只有小于15的数据需要特殊处理，其他都可以由统一函数实现
          bSJQCD[1] = 0;
          bSJQCD[0] = (byte) SJQCD;
        }
        else {
          bSJQCD = utils.str2bytes(Integer.toHexString(SJQCD));
        }
        for (int i = 0; i < bSJQCD.length; i++) {
          bTemp[i] = bSJQCD[i];
        }
        if (SJQCD < 256) {
          data[38] = bTemp[1]; //数据区长度的字段需要做颠倒工作
          data[39] = bTemp[0];
        }
        else {
          data[38] = bTemp[0];
          data[39] = bTemp[1];
        }

        //数据内容
        byte[] bSJQNR = new byte[SJQCD];
        bSJQNR = new String(SJQNR).getBytes();
        for (int i = 0; i < SJQCD; i++) {
          data[40 + i] = bSJQNR[i];
        }

        //到此内容组成完成
        AddCallInfoToList(AppID, new String(TerminalAddr), GYH, 10, RWDS); //保存终端信息和AppID到队列中
        FormData = true;
        Sequence = Sequence + 1;
        if ( (data != null) && (FormData)) { //发送数据的条件：缓存中有数据并且消息组成完成
          SocketChannel keyChannel = (SocketChannel) key.channel(); //通过对应的通道来写入数据
          try {
            bBuffer.clear();
            bBuffer.put(data);
            bBuffer.flip();
            keyChannel.write(bBuffer); //如果在这里出现异常，则说明无法连接成功
            bBuffer.clear();
            AckBack = false;
            //System.out.println("实时通讯发送命令，应用ID：" + AppID + "，终端逻辑地址：" +
            //     new String(TerminalAddr) + "规约号：" + GYH + "，数据区内容" + new String(SJQNR));
          }
          catch (IOException ex) {
          }
        }
        return;
    }
    else if ( (RWH == 0) && (RWDS == 0) && (YCDS == 0)) { //只有任务号、任务点数、异常点数都为0，才认为是召测数据下发
      FormData = false; //组成通讯内容开始，设置标志为无效
      data = null;
      data = new byte[12 + 33 + SJQNR.length + 2];
      byte[] MsgLength = new byte[4];
      MsgLength = utils.int2byte(12 + 33 + SJQNR.length + 2);
      data[0] = MsgLength[0];
      data[1] = MsgLength[1];
      data[2] = MsgLength[2];
      data[3] = MsgLength[3];

      data[4] = 0;
      data[5] = 0;
      data[6] = 0;
      data[7] = 4;

      byte[] MsgSequence = new byte[4];
      MsgSequence = utils.int2byte(Sequence);
      data[8] = MsgSequence[0];
      data[9] = MsgSequence[1];
      data[10] = MsgSequence[2];
      data[11] = MsgSequence[3];

      byte[] bAppID = new byte[4];
      bAppID = utils.int2byte(AppID);
      data[12] = bAppID[0];
      data[13] = bAppID[1];
      data[14] = bAppID[2];
      data[15] = bAppID[3];

      //应用ID
      byte[] bTemp = new byte[20];
      byte[] bZDLJDZ = new String(TerminalAddr).getBytes();
      for (int i = 0; i < bZDLJDZ.length; i++) {
        bTemp[i] = bZDLJDZ[i];
      }
      for (int i = 0; i < 20; i++) {
        data[16 + i] = bTemp[i];
      }
      //终端逻辑地址，需要补全20位
      byte[] bTXCS = new byte[1];
      if (TXFS < 15) { //只有小于15的数据需要特殊处理，其他都可以由统一函数实现
        bTXCS[0] = (byte) TXFS;
      }
      else {
        bTXCS = utils.str2bytes(Integer.toHexString(TXFS));
      }

      data[36] = bTXCS[0];
      //通讯方式，即终端当前通讯类型
      data[37] = 1;
      //智能选择，默认有效
      data[38] = 0;
      //测量点序号
      data[39] = 10;
      //测量点类型
      byte[] bGYH = new byte[1];
      bGYH = utils.str2bytes(Integer.toHexString(GYH));
      data[40] = bGYH[0];
      //规约号
      bTemp = new byte[2];
      byte[] bGNM = new String(GNM).getBytes();
      for (int i = 0; i < bGNM.length; i++) {
        bTemp[i] = bGNM[i];
      }
      for (int i = 0; i < 2; i++) {
        data[41 + i] = bTemp[i];
      }
      //功能码
      bTemp = new byte[2];
      byte[] bSJQCD = new byte[2];
      if (SJQCD < 15) { //只有小于15的数据需要特殊处理，其他都可以由统一函数实现
        bSJQCD[1] = 0;
        bSJQCD[0] = (byte) SJQCD;
      }
      else {
        bSJQCD = utils.str2bytes(Integer.toHexString(SJQCD));
      }
      for (int i = 0; i < bSJQCD.length; i++) {
        bTemp[i] = bSJQCD[i];
      }
      if (SJQCD < 256) {
        data[43] = bTemp[1]; //数据区长度的字段需要做颠倒工作
        data[44] = bTemp[0];
      }
      else {
        data[43] = bTemp[0];
        data[44] = bTemp[1];
      }
      //数据区长度
      byte[] bSJQNR = new byte[SJQCD];
      bSJQNR = new String(SJQNR).getBytes();
      for (int i = 0; i < SJQCD; i++) {
        data[45 + i] = bSJQNR[i];
      }

      byte[] bYXJ = new byte[1];
      if (Priority < 15) {
        bYXJ[0] = (byte) Priority;
      }
      else {
        bYXJ = utils.str2bytes(Integer.toHexString(Priority));
      }
      data[45 + SJQCD] = bYXJ[0]; //优先级

      byte[] bSFBH = new byte[1];
      if (ArithmeticNo < 15) {
        bSFBH[0] = (byte) ArithmeticNo;
      }
      else {
        bSFBH = utils.str2bytes(Integer.toHexString(ArithmeticNo));
      }
      data[46 + SJQCD] = bSFBH[0]; //算法编号

      //数据区内容，到此内容组成完成
      AddCallInfoToList(AppID, new String(TerminalAddr), GYH, 10, 0); //保存终端信息和AppID到队列中
      FormData = true;
      Sequence = Sequence + 1;
      fl.WriteLog("实时通讯组成命令，应用ID：" + AppID + "，终端逻辑地址：" +
                  new String(TerminalAddr) + "，数据区内容" + new String(SJQNR));
      if ( (data != null) && (FormData)) { //发送数据的条件：缓存中有数据并且消息组成完成
        fl.WriteLog("实时通讯准备发送命令，应用ID：" + AppID + "，终端逻辑地址：" +
                    new String(TerminalAddr) + "，数据区内容" + new String(SJQNR));
        SocketChannel keyChannel = (SocketChannel) key.channel(); //通过对应的通道来写入数据
        bBuffer.clear();
        bBuffer.put(data);
        bBuffer.flip();
        keyChannel.write(bBuffer); //如果在这里出现异常，则说明无法连接成功
        bBuffer.clear();
        AckBack = false;
        fl.WriteLog("实时通讯发送命令，应用ID：" + AppID + "，终端逻辑地址：" +
                    new String(TerminalAddr) + "，数据区内容" + new String(SJQNR));
      }
      return;
    }
    else if ( (RWH != 0) && (RWDS != 0)) { //历史数据读取组成命令内容
      FormData = false; //组成通讯内容开始，设置标志为无效
      data = null;
      data = new byte[12 + 33 + SJQNR.length + 3];
      byte[] MsgLength = new byte[4];
      MsgLength = utils.int2byte(12 + 33 + SJQNR.length + 3);
      data[0] = MsgLength[0];
      data[1] = MsgLength[1];
      data[2] = MsgLength[2];
      data[3] = MsgLength[3];

      data[4] = 0;
      data[5] = 0;
      data[6] = 0;
      data[7] = 5;

      byte[] MsgSequence = new byte[4];
      MsgSequence = utils.int2byte(Sequence);
      data[8] = MsgSequence[0];
      data[9] = MsgSequence[1];
      data[10] = MsgSequence[2];
      data[11] = MsgSequence[3];

      byte[] bAppID = new byte[4];
      bAppID = utils.int2byte(AppID);
      data[12] = bAppID[0];
      data[13] = bAppID[1];
      data[14] = bAppID[2];
      data[15] = bAppID[3];

      //应用ID
      byte[] bTemp = new byte[20];
      byte[] bZDLJDZ = new String(TerminalAddr).getBytes();
      for (int i = 0; i < bZDLJDZ.length; i++) {
        bTemp[i] = bZDLJDZ[i];
      }
      for (int i = 0; i < 20; i++) {
        data[16 + i] = bTemp[i];
      }
      //终端逻辑地址，需要补全20位
      byte[] bTXCS = new byte[1];
      if (TXFS < 15) { //只有小于15的数据需要特殊处理，其他都可以由统一函数实现
        bTXCS[0] = (byte) TXFS;
      }
      else {
        bTXCS = utils.str2bytes(Integer.toHexString(TXFS));
      }

      data[36] = bTXCS[0];
      //通讯方式，即终端当前通讯类型
      data[37] = 1;
      //智能选择，默认有效
      data[38] = 0;
      //测量点序号
      data[39] = 10;
      //测量点类型
      byte[] bGYH = new byte[1];
      bGYH = utils.str2bytes(Integer.toHexString(GYH));
      data[40] = bGYH[0];
      //规约号
      bTemp = new byte[2];
      byte[] bGNM = new String(GNM).getBytes();
      for (int i = 0; i < bGNM.length; i++) {
        bTemp[i] = bGNM[i];
      }
      for (int i = 0; i < 2; i++) {
        data[41 + i] = bTemp[i];
      }
      //功能码
      bTemp = new byte[2];
      byte[] bSJQCD = new byte[2];
      if (SJQCD < 15) { //只有小于15的数据需要特殊处理，其他都可以由统一函数实现
        bSJQCD[1] = 0;
        bSJQCD[0] = (byte) SJQCD;
      }
      else {
        bSJQCD = utils.str2bytes(Integer.toHexString(SJQCD));
      }
      for (int i = 0; i < bSJQCD.length; i++) {
        bTemp[i] = bSJQCD[i];
      }
      if (SJQCD < 256) {
        data[43] = bTemp[1]; //数据区长度的字段需要做颠倒工作
        data[44] = bTemp[0];
      }
      else {
        data[43] = bTemp[0];
        data[44] = bTemp[1];
      }
      //数据区长度
      byte[] bSJQNR = new byte[SJQCD];
      bSJQNR = new String(SJQNR).getBytes();
      for (int i = 0; i < SJQCD; i++) {
        data[45 + i] = bSJQNR[i];
      }
      //数据区内容
      byte[] bRWH = new byte[1];
      String sTemp = Integer.toHexString(RWH);
      if (sTemp.length() % 2 != 0) {
        sTemp = "0" + sTemp;
      }
      bRWH = utils.str2bytes(sTemp);
      data[45 + SJQCD] = bRWH[0];
      //任务号
      byte[] bRWDS = new byte[1];
      sTemp = Integer.toHexString(RWDS);
      if (sTemp.length() % 2 != 0) {
        sTemp = "0" + sTemp;
      }
      bRWDS = utils.str2bytes(sTemp);
      data[46 + SJQCD] = bRWDS[0];
      //任务点数

      byte[] bYXJ = new byte[1];
      if (Priority < 15) {
        bYXJ[0] = (byte) Priority;
      }
      else {
        bYXJ = utils.str2bytes(Integer.toHexString(Priority));
      }
      data[47 + SJQCD] = bYXJ[0]; //优先级

      //到此内容组成完成
      AddCallInfoToList(AppID, new String(TerminalAddr), GYH, 20, RWDS); //保存终端信息和AppID到队列中
      FormData = true;
      Sequence = Sequence + 1;
      if ( (data != null) && (FormData)) { //发送数据的条件：缓存中有数据并且消息组成完成
        SocketChannel keyChannel = (SocketChannel) key.channel(); //通过对应的通道来写入数据
        try {
          bBuffer.clear();
          bBuffer.put(data);
          bBuffer.flip();
          keyChannel.write(bBuffer); //如果在这里出现异常，则说明无法连接成功
          bBuffer.clear();
          AckBack = false;
          fl.WriteLog("实时通讯发送命令，应用ID：" + AppID + "，终端逻辑地址：" +
                      new String(TerminalAddr) + "，数据区内容" + new String(SJQNR));
        }
        catch (IOException ex) {
        }
      }
      return;
    }
    else if ( (YCDS != 0) && (RWH == 0) && (RWDS == 0)) { //异常数据读取组成命令内容
      FormData = false; //组成通讯内容开始，设置标志为无效
      data = null;
      data = new byte[12 + 33 + SJQNR.length + 2];
      byte[] MsgLength = new byte[4];
      MsgLength = utils.int2byte(12 + 33 + SJQNR.length + 2);
      data[0] = MsgLength[0];
      data[1] = MsgLength[1];
      data[2] = MsgLength[2];
      data[3] = MsgLength[3];

      data[4] = 0;
      data[5] = 0;
      data[6] = 0;
      data[7] = 6;

      byte[] MsgSequence = new byte[4];
      MsgSequence = utils.int2byte(Sequence);
      data[8] = MsgSequence[0];
      data[9] = MsgSequence[1];
      data[10] = MsgSequence[2];
      data[11] = MsgSequence[3];

      byte[] bAppID = new byte[4];
      bAppID = utils.int2byte(AppID);
      data[12] = bAppID[0];
      data[13] = bAppID[1];
      data[14] = bAppID[2];
      data[15] = bAppID[3];

      //应用ID
      byte[] bTemp = new byte[20];
      byte[] bZDLJDZ = new String(TerminalAddr).getBytes();
      for (int i = 0; i < bZDLJDZ.length; i++) {
        bTemp[i] = bZDLJDZ[i];
      }
      for (int i = 0; i < 20; i++) {
        data[16 + i] = bTemp[i];
      }
      //终端逻辑地址，需要补全20位
      byte[] bTXCS = new byte[1];
      if (TXFS < 15) { //只有小于15的数据需要特殊处理，其他都可以由统一函数实现
        bTXCS[0] = (byte) TXFS;
      }
      else {
        bTXCS = utils.str2bytes(Integer.toHexString(TXFS));
      }

      data[36] = bTXCS[0];
      //通讯方式，即终端当前通讯类型
      data[37] = 1;
      //智能选择，默认有效
      data[38] = 0;
      //测量点序号
      data[39] = 10;
      //测量点类型
      byte[] bGYH = new byte[1];
      bGYH = utils.str2bytes(Integer.toHexString(GYH));
      data[40] = bGYH[0];
      //规约号
      bTemp = new byte[2];
      byte[] bGNM = new String(GNM).getBytes();
      for (int i = 0; i < bGNM.length; i++) {
        bTemp[i] = bGNM[i];
      }
      for (int i = 0; i < 2; i++) {
        data[41 + i] = bTemp[i];
      }
      //功能码
      bTemp = new byte[2];
      byte[] bSJQCD = new byte[2];
      if (SJQCD < 15) { //只有小于15的数据需要特殊处理，其他都可以由统一函数实现
        bSJQCD[1] = 0;
        bSJQCD[0] = (byte) SJQCD;
      }
      else {
        bSJQCD = utils.str2bytes(Integer.toHexString(SJQCD));
      }
      for (int i = 0; i < bSJQCD.length; i++) {
        bTemp[i] = bSJQCD[i];
      }
      if (SJQCD < 256) {
        data[43] = bTemp[1]; //数据区长度的字段需要做颠倒工作
        data[44] = bTemp[0];
      }
      else {
        data[43] = bTemp[0];
        data[44] = bTemp[1];
      }
      //数据区长度
      byte[] bSJQNR = new byte[SJQCD];
      bSJQNR = new String(SJQNR).getBytes();
      for (int i = 0; i < SJQCD; i++) {
        data[45 + i] = bSJQNR[i];
      }
      //数据区内容
      byte[] bYCDS = new byte[1];
      String sTemp = Integer.toHexString(YCDS);
      if (sTemp.length() % 2 != 0) {
        sTemp = "0" + sTemp;
      }
      bYCDS = utils.str2bytes(sTemp);
      data[45 + SJQCD] = bYCDS[0];
      //异常点数，到此内容组成完成

      byte[] bYXJ = new byte[1];
      if (Priority < 15) {
        bYXJ[0] = (byte) Priority;
      }
      else {
        bYXJ = utils.str2bytes(Integer.toHexString(Priority));
      }
      data[46 + SJQCD] = bYXJ[0]; //优先级

      AddCallInfoToList(AppID, new String(TerminalAddr), GYH, 30, YCDS); //保存终端信息和AppID到队列中
      FormData = true;
      Sequence = Sequence + 1;
      if ( (data != null) && (FormData)) { //发送数据的条件：缓存中有数据并且消息组成完成
        SocketChannel keyChannel = (SocketChannel) key.channel(); //通过对应的通道来写入数据
        try {
          bBuffer.clear();
          bBuffer.put(data);
          bBuffer.flip();
          keyChannel.write(bBuffer); //如果在这里出现异常，则说明无法连接成功
          bBuffer.clear();
          AckBack = false;
        }
        catch (IOException ex) {
        }
      }
      return;
    }
  }

  public boolean SendShortMsg(char[] MsgContent, int MsgLength, char[] MBHM,
                              int SendType, int SMSChannel) {
    //首先根据发送类型判断消息长度是否超过要求
    switch (SendType) {
      case 10: {
        int iLen = new String(MsgContent).trim().length();
        if (iLen != MsgLength) {
          MsgLength = iLen;
        }
        if (iLen > 70) {
          return false;
        }
        break;
      }
      case 20: {
        int iLen = new String(MsgContent).trim().length();
        if (iLen != MsgLength) {
          MsgLength = iLen;
        }
        if (iLen > 140) {
          return false;
        }
        break;
      }
      case 30: {
        int iLen = new String(MsgContent).trim().length();
        if (iLen != MsgLength) {
          MsgLength = iLen;
        }
        if ( (iLen > 280) || (iLen % 2 != 0)) {
          return false;
        }
        break;
      }
    }
    try {
      SendInfo si = new SendInfo();
      si.AppID = 0;
      si.TerminalAddress = new String(MBHM); //目标手机号码
      si.TerminalProtocol = SendType; //消息类型
      si.TerminalCommType = SMSChannel; //制定的短信通道
      si.TaskID = 0;
      si.TaskCount = 0;
      si.AlarmCount = 0;
      si.FunctionCode = new String("");
      si.ContentLength = MsgLength;
      si.DataContent = new String(MsgContent).trim(); //加入发送队列即可，调用就算完成
      si.SendShortMsg = true;
      SendInfoList.add(si);
      LastTime = Calendar.getInstance();
      return true;
    }
    catch (Exception e) {
      return false;
    }
  }

  private void BuildOffLineTerminalInfoListAndSend(SelectionKey key,
      List terminallist) {
    ByteBuffer bBuffer = ByteBuffer.allocate(5000);
    FormData = false; //组成通讯内容开始，设置标志为无效
    data = null;
    data = new byte[12 + 2 + 20 * terminallist.size()];
    byte[] MsgLength = new byte[4];
    MsgLength = utils.int2byte(12 + 2 + 20 * terminallist.size());
    //消息头部分的消息总长度
    data[0] = MsgLength[0];
    data[1] = MsgLength[1];
    data[2] = MsgLength[2];
    data[3] = MsgLength[3];
    //消息头部分的消息类型代码
    data[4] = 0;
    data[5] = 0;
    data[6] = 0;
    data[7] = 30;
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
    if (terminallist.size() < 15) { //只有小于15的数据需要特殊处理，其他都可以由统一函数实现
      bSJQCD[1] = 0;
      bSJQCD[0] = (byte) terminallist.size();
    }
    else {
      bSJQCD = utils.str2bytes(Integer.toHexString(terminallist.size()));
    }
    for (int i = 0; i < bSJQCD.length; i++) {
      bTemp[i] = bSJQCD[i];
    }
    if (terminallist.size() < 256) {
      data[12] = bTemp[1]; //数据区长度的字段需要做颠倒工作
      data[13] = bTemp[0];
    }
    else {
      data[12] = bTemp[0];
      data[13] = bTemp[1];
    }
    //消息体部分的消息内容
    for (int i = 0; i < terminallist.size(); i++) {
      bTemp = new byte[20];
      TerminalOnLineStatus to = (TerminalOnLineStatus) terminallist.get(i);
      byte[] bZDLJDZ = to.TerminalAddress.getBytes();
      for (int j = 0; j < bZDLJDZ.length; j++) {
        bTemp[j] = bZDLJDZ[j];
      }
      for (int j = 0; j < 20; j++) {
        data[14 + i * 20 + j] = bTemp[j];
      }
      //终端逻辑地址，需要补全20位
    }
    FormData = true;
    Sequence = Sequence + 1;
    if ( (data != null) && (FormData)) { //发送数据的条件：缓存中有数据并且消息组成完成
      SocketChannel keyChannel = (SocketChannel) key.channel(); //通过对应的通道来写入数据
      bBuffer.clear();
      bBuffer.put(data);
      bBuffer.flip();
      try {
        keyChannel.write(bBuffer); //如果在这里出现异常，则说明无法连接成功
      }
      catch (IOException ex) {
      }
      bBuffer.clear();
      AckBack = false;
    }
    return;
  }

  private void BuildTerminalInfoListAndSend(SelectionKey key, List terminallist) {
    ByteBuffer bBuffer = ByteBuffer.allocate(5000);
    FormData = false; //组成通讯内容开始，设置标志为无效
    data = null;
    data = new byte[12 + 2 + 20 * terminallist.size()];
    byte[] MsgLength = new byte[4];
    MsgLength = utils.int2byte(12 + 2 + 20 * terminallist.size());
    //消息头部分的消息总长度
    data[0] = MsgLength[0];
    data[1] = MsgLength[1];
    data[2] = MsgLength[2];
    data[3] = MsgLength[3];
    //消息头部分的消息类型代码
    data[4] = 0;
    data[5] = 0;
    data[6] = 0;
    data[7] = 21;
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
    if (terminallist.size() < 15) { //只有小于15的数据需要特殊处理，其他都可以由统一函数实现
      bSJQCD[1] = 0;
      bSJQCD[0] = (byte) terminallist.size();
    }
    else {
      bSJQCD = utils.str2bytes(Integer.toHexString(terminallist.size()));
    }
    for (int i = 0; i < bSJQCD.length; i++) {
      bTemp[i] = bSJQCD[i];
    }
    if (terminallist.size() < 256) {
      data[12] = bTemp[1]; //数据区长度的字段需要做颠倒工作
      data[13] = bTemp[0];
    }
    else {
      data[12] = bTemp[0];
      data[13] = bTemp[1];
    }
    //消息体部分的消息内容
    for (int i = 0; i < terminallist.size(); i++) {
      bTemp = new byte[20];
      TerminalOnLineStatus to = (TerminalOnLineStatus) terminallist.get(i);
      byte[] bZDLJDZ = to.TerminalAddress.getBytes();
      for (int j = 0; j < bZDLJDZ.length; j++) {
        bTemp[j] = bZDLJDZ[j];
      }
      for (int j = 0; j < 20; j++) {
        data[14 + i * 20 + j] = bTemp[j];
      }
      //终端逻辑地址，需要补全20位
    }
    FormData = true;
    Sequence = Sequence + 1;
    if ( (data != null) && (FormData)) { //发送数据的条件：缓存中有数据并且消息组成完成
      SocketChannel keyChannel = (SocketChannel) key.channel(); //通过对应的通道来写入数据
      bBuffer.clear();
      bBuffer.put(data);
      bBuffer.flip();
      try {
        keyChannel.write(bBuffer); //如果在这里出现异常，则说明无法连接成功
      }
      catch (IOException ex) {
      }
      bBuffer.clear();
      AckBack = false;
    }
    return;
  }

  private void BuildCertainTerminalInfoAndSend(SelectionKey key,
                                               String terminaladdr) {
    ByteBuffer bBuffer = ByteBuffer.allocate(5000);
    FormData = false; //组成通讯内容开始，设置标志为无效
    data = null;
    data = new byte[12 + 20];
    byte[] MsgLength = new byte[4];
    MsgLength = utils.int2byte(12 + 20);
    //消息头部分的消息总长度
    data[0] = MsgLength[0];
    data[1] = MsgLength[1];
    data[2] = MsgLength[2];
    data[3] = MsgLength[3];
    //消息头部分的消息类型代码
    data[4] = 0;
    data[5] = 0;
    data[6] = 0;
    data[7] = 31;
    //消息头部分的消息序号
    byte[] MsgSequence = new byte[4];
    MsgSequence = utils.int2byte(Sequence);
    data[8] = MsgSequence[0];
    data[9] = MsgSequence[1];
    data[10] = MsgSequence[2];
    data[11] = MsgSequence[3];
    //消息体部分的消息内容
    byte[] bTemp = new byte[20];
    byte[] bZDLJDZ = terminaladdr.getBytes();
    for (int i = 0; i < bZDLJDZ.length; i++) {
      bTemp[i] = bZDLJDZ[i];
    }
    for (int i = 0; i < 20; i++) {
      data[12 + i] = bTemp[i];
    }
    FormData = true;
    Sequence = Sequence + 1;
    if ( (data != null) && (FormData)) { //发送数据的条件：缓存中有数据并且消息组成完成
      SocketChannel keyChannel = (SocketChannel) key.channel(); //通过对应的通道来写入数据
      bBuffer.clear();
      bBuffer.put(data);
      bBuffer.flip();
      try {
        keyChannel.write(bBuffer); //如果在这里出现异常，则说明无法连接成功
      }
      catch (IOException ex) {
      }
      bBuffer.clear();
      AckBack = false;
    }
    return;
  }

  public boolean SetTerminalOnLineStatus(List <TerminalOnLineStatus>terminalList) {
    try {
      SetTerminalOnLineStatus = true;
      TerminalStatusList = terminalList;
      Thread.sleep(500);
      return true;
    }
    catch (Exception ex) {
    }
    return false;
  }

  public boolean BuildCertainTerminalInfo(String terminaladdr) {
    try {
      BuildCertainTerminalInfo = true;
      TerminalAddr = terminaladdr;
      Thread.sleep(500);
      return true;
    }
    catch (Exception ex) {
    }
    return false;
  }

  public List GetTerminalOnLineStatus(List <TerminalOnLineStatus>terminalList) {
    List OnLineList = null;
    try {
      OnLineList = new LinkedList();
      //组成终端逻辑地址队列，通过socket下发到通讯服务，并等待通讯服务的返回
      GetTerminalOnLineStatusBack = false;
      GetTerminalOnLineStatus = true;
      TerminalStatusList = terminalList;
      Calendar SendTime = Calendar.getInstance();
      SendTime.add(Calendar.SECOND, 2);
      //等待命令下发的返回，再处理内容后组成终端在线状态队列，返回
      while (!GetTerminalOnLineStatusBack &&
             SendTime.after(Calendar.getInstance())) {
        Thread.sleep(1);
      }
      OnLineList = TerminalStatusList;
    }
    catch (Exception ex) {
    }
    return OnLineList;
  }

  public boolean SendBatchToFep(int AppID, int TerminalCount, List TerminalInfo,
                                List DataContentInfo, char[] FunctionCode,
                                int TaskID, int TaskCount, int AlarmCount,
                                int Priority) {
    if (ConnectFlag) {
      try {
        for (int i = 0; i < TerminalCount; i++) {
          TerminalInfoStruct tis = (TerminalInfoStruct) TerminalInfo.get(i);
          DataContentStruct dcs = (DataContentStruct) DataContentInfo.get(i);
          SendInfo si = new SendInfo();
          si.AppID = AppID;
          si.TerminalAddress = new String(tis.TerminalAddress);
          si.TerminalProtocol = tis.TerminalProtocol;
          si.TerminalCommType = tis.TerminalCommType;
          si.TaskID = TaskID;
          si.TaskCount = TaskCount;
          si.AlarmCount = AlarmCount;
          si.FunctionCode = new String(FunctionCode);
          si.ContentLength = dcs.DataContentLength;
          si.DataContent = new String(dcs.DataContent);
          si.Priority = Priority;
          si.ArithmeticNo = tis.ArithmeticNo;
          SendInfoList.add(si);
          fl.WriteLog("发送数据加入队列，应用ID：" + AppID + "，终端逻辑地址：" +
                      si.TerminalAddress + "，数据区内容：" + si.DataContent);
          LastTime = Calendar.getInstance();
        }
        return true;
      }
      catch (Exception e) {
        return false;
      }
    }
    else {
      return false;
    }
  }

  public boolean SendSelfDefinedMsg(int AppID, char[] TerminalAddr, int GYH,
                                    int TXFS, int MLXH, int SJQCD, char[] SJQNR,
                                    int Priority) {
    if (ConnectFlag) {
      try {
        SendInfo si = new SendInfo();
        si.AppID = AppID;
        si.TerminalAddress = new String(TerminalAddr);
        si.TerminalProtocol = GYH;
        si.TerminalCommType = TXFS;
        si.TaskID = MLXH;
        si.TaskCount = 0;
        si.AlarmCount = 0;
        si.FunctionCode = new String("");
        si.ContentLength = SJQCD;
        si.DataContent = new String(SJQNR);
        si.Priority = Priority;
        si.SendSelfDefinedMsg = true;
        SendInfoList.add(si);
        LastTime = Calendar.getInstance();
        return true;
      }
      catch (Exception e) {
        return false;
      }
    }
    else {
      return false;
    }
  }

  public boolean SendToFep(int AppID, char[] TerminalAddr, int GYH, int TXFS,
                           int RWH, int RWDS, int YCDS, char[] GNM, int SJQCD,
                           char[] SJQNR, int Priority, int ArithmeticNo) {
    if (ConnectFlag) {
      try {
        SendInfo si = new SendInfo();
        si.AppID = AppID;
        si.TerminalAddress = new String(TerminalAddr);
        si.TerminalProtocol = GYH;
        si.TerminalCommType = TXFS;
        si.TaskID = RWH;
        si.TaskCount = RWDS;
        si.AlarmCount = YCDS;
        si.FunctionCode = new String(GNM);
        si.ContentLength = SJQCD;
        si.DataContent = new String(SJQNR);
        si.Priority = Priority;
        si.ArithmeticNo = ArithmeticNo;
        SendInfoList.add(si);
        LastTime = Calendar.getInstance();
        return true;
      }
      catch (Exception e) {
        return false;
      }
    }
    else {
      return false;
    }
  }
  public boolean SendPrepayToFep(int AppID, String strDBJH, String strSendInfo, int opType){	  
	  if (ConnectFlag) {
	      try {
	        SendInfo si = new SendInfo();
	        si.AppID = AppID;
	        si.TerminalAddress = strDBJH;
	        if (opType == 1){
	        	si.TerminalProtocol = 1000;
	        }
	        else if (opType == 2){
	        	si.TerminalProtocol = 1001;
	        }
	        else if (opType == 3){
	        	si.TerminalProtocol = 1100;
	        }
	        
	        si.TerminalCommType = 110;
	        si.TaskID = 0;
	        si.TaskCount = 0;
	        si.AlarmCount = 0;
	        si.FunctionCode = "";
	        si.ContentLength = strSendInfo.length();
	        si.DataContent = new String(strSendInfo);
	        si.Priority = 0;
	        si.ArithmeticNo = 0;
	        SendInfoList.add(si);
	        LastTime = Calendar.getInstance();
	        return true;
	      }
	      catch (Exception e) {
	        return false;
	      }
	    }
	    else {
	      return false;
	    }
  }
  
  public List GetNewListMessage(int AppID) {
    List <StructReturnMessage>nl = new LinkedList<StructReturnMessage>(); 
    for (int i = RealTimeList.size() - 1; i >= 0; i--) {
      RealTimeCommunicationInfo ci = (RealTimeCommunicationInfo) RealTimeList.
          get(i); 
      //System.out.println("ci.AppID/ci.BackFlag:"+ci.AppID+"/"+ci.BackFlag);
      if ( (ci.AppID == AppID) && (ci.BackFlag)) { //判断条件：AppID相同，并且返回标志有效
        StructReturnMessage srm = new StructReturnMessage();
        srm.AppID = ci.AppID;
        srm.DataType = ci.DataType;
        srm.GYH = ci.GYH;
        srm.SJQCD = ci.SJQCD;
        srm.ControlConde = ci.KZM.toCharArray();
        srm.FunctionCode = ci.GNM.toCharArray();
        srm.TerminalAddr = ci.TerminalAddr.toCharArray();
        srm.SJQNR = ci.SJQNR.toCharArray();
        RealTimeList.remove(ci); //从队列中删除数据
        nl.add(srm); //将需要返回的内容作为对象放入到队列中
      }
    }
    if (nl.size() > 0) { //根据List长度决定返回值
      return nl;
    }
    else {
      return null;
    }
  }

  public StructReturnMessage GetNewMessage(int AppID) { //根据应用ID找是否有消息返回
    for (int i = 0; i < RealTimeList.size(); i++) {
      RealTimeCommunicationInfo ci = (RealTimeCommunicationInfo) RealTimeList.
          get(i);
      
      if ( (ci.AppID == AppID) && (ci.BackFlag)) { //判断条件：AppID相同，并且返回标志有效
        StructReturnMessage srm = new StructReturnMessage();
        srm.AppID = ci.AppID;
        srm.DataType = ci.DataType;
        srm.GYH = ci.GYH;
        srm.SJQCD = ci.SJQCD;
        srm.ControlConde = ci.KZM.toCharArray();
        srm.FunctionCode = ci.GNM.toCharArray();
        srm.TerminalAddr = ci.TerminalAddr.toCharArray();
        srm.SJQNR = ci.SJQNR.toCharArray();
        RealTimeList.remove(ci); //从队列中删除数据
        return srm;
      }
    }
    return null;
  }
/*
  private void WaitAckBack(SocketChannel socketChannel) {
    //首先读取12个字符的报文头内容，分析报文头的有效性
    try {
      ByteBuffer[] buffer = new ByteBuffer[10];
      buffer[0] = ByteBuffer.allocate(12); //用于接收的缓冲
      buffer[0].clear();
      long count = 0;
      try {
        count = socketChannel.read(buffer, 0, 1);
      }
      catch (IOException ex1) {
        utils.PrintDebugMessage("ReadBuffer Error,Error info " + ex1.toString(),
                                "D");
        count = -1;
      }
      if (count == -1) { //前置机的socket通道出现异常时，也需要处理链路队列，保证队列中的数据正确
        try {
          FStop = true;
          ConnectFlag = false;
          client.socket().close();
          client.close();
          socketChannel.socket().close();
          socketChannel.close();
        }
        catch (Exception ex8) {
        }
      }
      if (count == 12) {
        buffer[0].flip();
        byte data[] = new byte[10000];
        buffer[0].get(data, 0, buffer[0].limit());
        MessageBody mb = new MessageBody();
        String strMesshead = utils.bytes2str(data, buffer[0].limit());
        mb.TotalLength = strMesshead.substring(1, 8);
        mb.CommandID = strMesshead.substring(14, 16);
        mb.SeqID = strMesshead.substring(16, 24);
        //如果报文头有效，则继续读取后面的报文体内容，进行处理，否则丢失当前报文头，继续读取12个字符
        buffer[1] = ByteBuffer.allocate(Integer.parseInt(mb.TotalLength,
            16) - 12); //用于接收的缓冲
        count = socketChannel.read(buffer, 1, 1);
        if (count > 0) {
          buffer[1].flip();
          buffer[1].get(data, 12, buffer[1].limit());
          String strMessbody = utils.bytes2str(data,
                                               Integer.parseInt(mb.TotalLength,
              16));
          processData(Integer.parseInt(mb.CommandID, 16), socketChannel,
                      strMessbody, data);
          //对于有效的报文处理后，退出本次数据的读取处理过程，如果还有数据需要处理会再次进入本函数处理
        }
      }
    }
    catch (Exception ex) {
    }
  }
*/
  protected void receiveData(SelectionKey key) throws Exception {
    //首先读取12个字符的报文头内容，分析报文头的有效性
    ByteBuffer[] buffer = new ByteBuffer[10];
    buffer[0] = ByteBuffer.allocate(12); //用于接收的缓冲
    SocketChannel socketChannel = (SocketChannel) key.channel();
    buffer[0].clear();
    long count = 0;
    try {
      count = socketChannel.read(buffer, 0, 1);
    }
    catch (IOException ex1) {
      //utils.PrintDebugMessage("readData Error,Error info " + ex1.toString(),
      //                        "D");
      count = -1;
    }
    if (count == -1) { //前置机的socket通道出现异常时，也需要处理链路队列，保证队列中的数据正确
      try {
        FStop = true;
        ConnectFlag = false;
        client.socket().close();
        client.close();
        socketChannel.socket().close();
        socketChannel.close();
      }
      catch (Exception ex8) {
      }
    }
    if (count == 12) {
      buffer[0].flip();
      byte data[] = new byte[10000];
      buffer[0].get(data, 0, buffer[0].limit());
      MessageBody mb = new MessageBody();
      String strMesshead = utils.bytes2str(data, buffer[0].limit());
      mb.TotalLength = strMesshead.substring(1, 8);
      mb.CommandID = strMesshead.substring(8, 16);
      mb.SeqID = strMesshead.substring(16, 24);
      //如果报文头有效，则继续读取后面的报文体内容，进行处理，否则丢失当前报文头，继续读取12个字符
      buffer[1] = ByteBuffer.allocate(Integer.parseInt(mb.TotalLength,
          16) - 12); //用于接收的缓冲
      count = socketChannel.read(buffer, 1, 1);
      if (count > 0) {
        buffer[1].flip();
        buffer[1].get(data, 12, buffer[1].limit());
        String strMessbody = utils.bytes2str(data,
                                             Integer.parseInt(mb.TotalLength,
            16));
        LastTime = Calendar.getInstance();
        processData(Integer.parseInt(mb.CommandID, 16), socketChannel,
                    strMessbody, data);
        //对于有效的报文处理后，退出本次数据的读取处理过程，如果还有数据需要处理会再次进入本函数处理
      }
    }
  }

  protected String AnalyzeCommandType(String Data) throws Exception {
    //分析得到命令类型，如果消息长度和收到报文内容长度不同，则丢弃该数据
    String sMessageLen = Data.substring(1, 8);
    if (Data.length() != utils.HexStrToInt(sMessageLen) * 2) {
      return "0";
    }
    else {
      String sCommandType = Data.substring(14, 16);
      return (sCommandType);
    }
  }

  protected void AckMessage(String CommandID, String sData,
                            SocketChannel channel) {
    //返回确认消息
    String ss = "0000000D08000" + CommandID + sData.substring(16, 24) + "0A";
    ByteBuffer bBuffer = ByteBuffer.allocate(5000);
    bBuffer.clear();
    bBuffer.put(utils.str2bytes(ss));
    bBuffer.flip();
    try {
      channel.write(bBuffer);
    }
    catch (IOException ex) {
    }
    bBuffer.clear();
  }

  private void UpdateCallInfoList(int AppID, String ZDLJDZ, int GYH, int SJLX,
                                  int SJCD, String SJNR, String GNM, String KZM) {
    for (int i = 0; i < RealTimeList.size(); i++) {
      RealTimeCommunicationInfo ci = (RealTimeCommunicationInfo) RealTimeList.
          get(i);
      //根据应用ID和终端逻辑地址判断该上送的召测数据是否是对应数据的返回
      if ( (ci.AppID == AppID) && (ci.TerminalAddr.trim().equals(ZDLJDZ)) &&
          (!ci.BackFlag)) {
        //后续命令都加如队列
        RealTimeCommunicationInfo rtcInfo = new RealTimeCommunicationInfo();
        rtcInfo.AppID = ci.AppID;
        rtcInfo.TerminalAddr = ci.TerminalAddr;
        rtcInfo.GYH = ci.GYH;
        rtcInfo.DataType = SJLX;
        rtcInfo.SJQCD = SJCD;
        rtcInfo.SJQNR = SJNR;
        rtcInfo.GNM = GNM;
        rtcInfo.KZM = KZM;
        rtcInfo.BackFlag = true; //命令返回标志
        rtcInfo.AddInTime = Calendar.getInstance();
        RealTimeList.set(i, rtcInfo);
        //utils.PrintDebugMessage("UpdateCallInfoList,APPID is "+AppID+",ZDLJDZ is " + ZDLJDZ +
        //        ",SJQNR is " + SJNR, "D");
        break;
      }
    }
  }

  protected void AnalyzeCommandContent(String sData, int CommandType,
                                       byte[] indata, SocketChannel channel) {
    String sMessageLen = sData.substring(1, 8);
    if (sData.length() != utils.HexStrToInt(sMessageLen) * 2) {
      return;
    }
    else {
      switch (CommandType) {
        case 0x00000007: { //召测数据的返回，对于返回内容的分析
          int iAppID = utils.HexStrToInt(sData.substring(24, 32)); //应用ID
          int iGYH = utils.HexStrToInt(sData.substring(72, 74)); //规约号
          String sZDLJDZ = "";

          byte[] Content = new byte[20];
          for (int i = 0; i < 20; i++) {
            Content[i] = indata[i + 16];
          }
          sZDLJDZ = new String(Content).trim(); //终端逻辑地址

          int iSJLX = utils.HexStrToInt(sData.substring(78, 80)); //数据类型
          int iSJQCD = utils.HexStrToInt(sData.substring(80, 84)); //数据区长度

          Content = new byte[iSJQCD];
          for (int i = 0; i < iSJQCD; i++) {
            Content[i] = indata[i + 42];
          }
          String sSJQNR = new String(Content).trim(); //数据区内容

          Content = new byte[2];
          for (int i = 0; i < 2; i++) {
            Content[i] = indata[i + 42 + iSJQCD];
          }
          String sGNM = new String(Content).trim(); //功能码

          Content = new byte[2];
          for (int i = 0; i < 2; i++) {
            Content[i] = indata[i + 44 + iSJQCD];
          }
          String sKZM = new String(Content).trim(); //控制码
          //utils.PrintDebugMessage("Receive Data Result,APPID is "+iAppID+",ZDLJDZ is " + sZDLJDZ +
          //                        ",SJQNR is " + sSJQNR, "D");
          UpdateCallInfoList(iAppID, sZDLJDZ, iGYH, iSJLX, iSJQCD, sSJQNR, sGNM,
                             sKZM); //根据应用ID和终端逻辑地址对应上层的调用
          break;
        }
        case 0x00000008: { //历史数据召测的返回
          int iAppID = utils.HexStrToInt(sData.substring(24, 32)); //应用ID
          int iGYH = utils.HexStrToInt(sData.substring(72, 74)); //规约号
          String sZDLJDZ = "";

          byte[] Content = new byte[20];
          for (int i = 0; i < 20; i++) {
            Content[i] = indata[i + 16];
          }
          sZDLJDZ = new String(Content).trim(); //终端逻辑地址

          int iSJQCD = utils.HexStrToInt(sData.substring(112, 116)); //数据区长度

          Content = new byte[iSJQCD];
          for (int i = 0; i < iSJQCD; i++) {
            Content[i] = indata[i + 58];
          }
          String sSJQNR = new String(Content).trim(); //数据区内容

          Content = new byte[2];
          for (int i = 0; i < 2; i++) {
            Content[i] = indata[i + 58 + iSJQCD];
          }
          String sGNM = new String(Content).trim(); //功能码

          Content = new byte[2];
          for (int i = 0; i < 2; i++) {
            Content[i] = indata[i + 60 + iSJQCD];
          }
          String sKZM = new String(Content).trim(); //控制码
          //utils.PrintDebugMessage("Receive Data Result,APPID is "+iAppID+",ZDLJDZ is " + sZDLJDZ +
          //                        ",SJQNR is " + sSJQNR, "D");
          UpdateCallInfoList(iAppID, sZDLJDZ, iGYH, 220, iSJQCD, sSJQNR, sGNM,
                             sKZM); //根据应用ID和终端逻辑地址对应上层的调用
          break;
        }
        case 0x00000009: { //异常召测数据的返回
          int iAppID = utils.HexStrToInt(sData.substring(24, 32)); //应用ID
          int iGYH = utils.HexStrToInt(sData.substring(72, 74)); //规约号
          String sZDLJDZ = "";

          byte[] Content = new byte[20];
          for (int i = 0; i < 20; i++) {
            Content[i] = indata[i + 16];
          }
          sZDLJDZ = new String(Content).trim(); //终端逻辑地址

          int iSJQCD = utils.HexStrToInt(sData.substring(128, 132)); //数据区长度

          Content = new byte[iSJQCD];
          for (int i = 0; i < iSJQCD; i++) {
            Content[i] = indata[i + 66];
          }
          String sSJQNR = new String(Content).trim(); //数据区内容

          Content = new byte[2];
          for (int i = 0; i < 2; i++) {
            Content[i] = indata[i + 66 + iSJQCD];
          }
          String sGNM = new String(Content).trim(); //功能码

          Content = new byte[2];
          for (int i = 0; i < 2; i++) {
            Content[i] = indata[i + 68 + iSJQCD];
          }
          String sKZM = new String(Content).trim(); //控制码
          UpdateCallInfoList(iAppID, sZDLJDZ, iGYH, 230, iSJQCD, sSJQNR, sGNM,
                             sKZM); //根据应用ID和终端逻辑地址对应上层的调用
          break;
        }
        case 0x00000016: {
          int iCount = utils.HexStrToInt(sData.substring(24, 28)); //返回的终端数量
          String sZDLJDZ = "";
          TerminalStatusList.clear();
          int iPos = 28;
          for (int i = 0; i < iCount; i++) {
            byte[] Content = new byte[20];
            for (int j = 0; j < 20; j++) {
              Content[j] = indata[j + iPos / 2];
            }
            sZDLJDZ = new String(Content).trim(); //终端逻辑地址
            iPos = iPos + 40;
            int iOnLineStatus = utils.HexStrToInt(sData.substring(iPos,
                iPos + 2)); //在线状态
            iPos = iPos + 2;
            TerminalOnLineStatus to = new TerminalOnLineStatus();
            to.TerminalAddress = sZDLJDZ;
            to.OnLineStatus = iOnLineStatus;
            TerminalStatusList.add(to);
          }
          GetTerminalOnLineStatusBack = true;
          break;
        }
        case 0x00000108:case 0x00000109: { //召测数据的返回，对于返回内容的分析
            int iAppID = utils.HexStrToInt(sData.substring(24, 32)); //应用ID
            int iGYH; //规约号
            if (CommandType==264){
            	iGYH = 1000;
            }
            else{
            	iGYH = 1001;
            }
            String sZDLJDZ = "";

            byte[] Content = new byte[20];
            for (int i = 0; i < 20; i++) {
              Content[i] = indata[i + 16];
            }
            sZDLJDZ = new String(Content).trim(); //终端逻辑地址

            int iSJQCD = utils.HexStrToInt(sData.substring(72, 76)); //数据区长度

            Content = new byte[iSJQCD];
            for (int i = 0; i < iSJQCD; i++) {
              Content[i] = indata[i + 38];
            }
            String sSJQNR = new String(Content).trim(); //数据区内容

            
            //utils.PrintDebugMessage("Receive Data Result,ZDLJDZ is " + sZDLJDZ +
            //                        ",SJQNR is " + sSJQNR, "D");
            UpdateCallInfoList(iAppID, sZDLJDZ, iGYH, 0, iSJQCD, sSJQNR, "",
                               ""); //根据应用ID和终端逻辑地址对应上层的调用
            break;
          }
        default: {
          break;
        }
      }
    }
  }

  private void processData(int CommandID, SocketChannel channel,
                           String ReceiveData, byte[] Data) {
    switch (CommandID) { //根据命令类型分别处理
      case 0x08000004: { //普通召测命令的确认
        AckBack = true;
        break;
      }
      case 0x08000005: { //历史数据读取的确认
        AckBack = true;
        break;
      }
      case 0x08000006: { //异常数据读取的确认
        AckBack = true;
        break;
      }
      case 0x00000007: { //从通讯服务提交的召测返回数据处理
        AckMessage(ReceiveData.substring(13, 16), ReceiveData, channel);
        AnalyzeCommandContent(ReceiveData, CommandID, Data, channel);
        break;
      }
      case 0x00000008: { //从通讯服务提交的历史数据处理
        AckMessage(ReceiveData.substring(13, 16), ReceiveData, channel);
        AnalyzeCommandContent(ReceiveData, CommandID, Data, channel);
        break;
      }
      case 0x00000009: { //从通讯服务提交的异常数据处理
        AckMessage(ReceiveData.substring(13, 16), ReceiveData, channel);
        AnalyzeCommandContent(ReceiveData, CommandID, Data, channel);
        break;
      }
      case 0x0800000A: { //下发自定义短信的确认
        AckBack = true;
        break;
      }
      case 0x0800000C: { //自定义命令的确认
        AckBack = true;
        break;
      }
      case 0x0800000D: { //确认消息，不做处理
        ConnectFlag = true;
        AckBack = true;
        //utils.PrintDebugMessage("MasterStation Connected", "D");
        break;
      }
      case 0x0800000E: { //确认消息，不做处理
        try {
          channel.socket().close();
          channel.close();
          client.socket().close();
          client.close();
          selector.close();
        }
        catch (IOException ex) {
        }
        AckBack = true;
        break;
      }
      case 0x08000015: { //下发终端逻辑地址的消息确认，不处理
        AckBack = true;
        break;
      }
      case 0x00000016: { //终端在线状态消息的返回
        AckMessage(ReceiveData.substring(13, 16), ReceiveData, channel);
        AnalyzeCommandContent(ReceiveData, CommandID, Data, channel);
        break;
      }
      case 0x0800001E: { //设置终端在线状态的消息确认，不处理
        AckBack = true;
        break;
      }
      case 0x00000108:case 0x00000109:{
    	  AckMessage(ReceiveData.substring(13, 16), ReceiveData, channel);
          AnalyzeCommandContent(ReceiveData, CommandID, Data, channel);
    	  break;
      }
      default: {
        break;
      }
    }
  }
/*
  private void processData(String sData, SocketChannel channel, byte[] InData) {
    String CommandID = null;
    try {
      String sMessageLen = sData.substring(1, 8);
      if (sData.length() != utils.HexStrToInt(sMessageLen) * 2) {
        sData = sData.substring(0, utils.HexStrToInt(sMessageLen) * 2);
      }
      CommandID = sData.substring(14, 16);
    }
    catch (Exception ex) {
      try {
        channel.close();
      }
      catch (IOException e) {
      }
      return ;
    }
    //System.out.println("CommandID is " + (CommandID));
    switch (Integer.parseInt(CommandID, 16)) { //根据命令类型分别处理
      case 4: { //普通召测命令的确认
        AckBack = true;
        break;
      }
      case 5: { //历史数据读取的确认
        AckBack = true;
        break;
      }
      case 6: { //异常数据读取的确认
        AckBack = true;
        break;
      }
      case 7: { //从通讯服务提交的召测返回数据处理
        //AckMessage(CommandID, sData, channel);
        AnalyzeCommandContent(sData, utils.ten2sixteen(CommandID), InData,
                              channel);
        break;
      }
      case 8: { //从通讯服务提交的历史数据处理
        //AckMessage(CommandID, sData, channel);
        AnalyzeCommandContent(sData, utils.ten2sixteen(CommandID), InData,
                              channel);
        break;
      }
      case 9: { //从通讯服务提交的异常数据处理
        //AckMessage(CommandID, sData, channel);
        AnalyzeCommandContent(sData, utils.ten2sixteen(CommandID), InData,
                              channel);
        break;
      }
      case 10: { //下发自定义短信的确认
        AckBack = true;
        break;
      }
      case 12: { //自定义命令的确认
        AckBack = true;
        break;
      }
      case 13: { //确认消息，不做处理
        ConnectFlag = true;
        AckBack = true;
        break;
      }
      case 14: { //确认消息，不做处理
        try {
          channel.socket().close();
          channel.close();
          client.socket().close();
          client.close();
          selector.close();
        }
        catch (IOException ex) {
        }
        AckBack = true;
        break;
      }
      case 21: { //下发终端逻辑地址的消息确认，不处理
        AckBack = true;
        break;
      }
      case 22: { //终端在线状态消息的返回
        AckMessage(CommandID, sData, channel);
        AnalyzeCommandContent(sData, utils.ten2sixteen(CommandID), InData,
                              channel);
        break;
      }
      case 30: { //设置终端在线状态的消息确认，不处理
        AckBack = true;
        break;
      }
      default: {
        break;
      }
    }
  }
*/
  class RealTimeCommunicationInfo { //实时通讯的对象结构
    public int AppID = 0; //应用ID
    public String TerminalAddr = ""; //终端逻辑地址
    public int GYH = 0; //规约号
    public int DataType = 0; //数据类型
    public int SJQCD = 0; //数据区长度
    public String SJQNR = ""; //数据区内容
    public String GNM = ""; //功能码
    public String KZM = ""; //控制码
    public boolean BackFlag = false; //消息是否返回标志
    public Calendar AddInTime; //加入队列的时间
  }

  class RealTimeCommunicationInfoListThread
      extends Thread {
    public void run() {
      while (true) {
        if (FStop) {
          fl.WriteLog("实时通讯监控线程退出！");
          return;
        }
        try {
          for (int i = 0; i < RealTimeList.size(); i++) {
            RealTimeCommunicationInfo ci = (RealTimeCommunicationInfo)
                RealTimeList.get(i);
            if (ci.BackFlag) { //已经返回的超时时间为60s
              ci.AddInTime.add(Calendar.MINUTE, 1);
              if (ci.AddInTime.before(Calendar.getInstance())) {
                RealTimeList.remove(i);
                break;
              }
              else {
                ci.AddInTime.add(Calendar.MINUTE, -1);
              }
            }
            else { //没有返回的超时时间为2分钟
              ci.AddInTime.add(Calendar.MINUTE, 2);
              if (ci.AddInTime.before(Calendar.getInstance())) {
                RealTimeList.remove(i);
                break;
              }
              else {
                ci.AddInTime.add(Calendar.MINUTE, -2);
              }
            }
            Thread.sleep(1);
          }
          if ( (RealTimeList.size() == 0)) {
            LastTime.add(Calendar.MINUTE, 2);
            if (LastTime.before(Calendar.getInstance())) {
              //Calendar c = Calendar.getInstance();
              //SimpleDateFormat formatter = new SimpleDateFormat(
              //    "yyyy-MM-dd HH:mm:ss");
              //System.out.println(formatter.format(c.getTime()) +
              //                   "实时通讯断开与通讯服务的空闲连接");
              DisConnect();
            }
            else {
              LastTime.add(Calendar.MINUTE, -2);
            }
          }
          Thread.sleep(1);
        }
        catch (Exception ex2) {
        }
      }

    }
  }

  class MonitorThread
      extends Thread {
    SendInfo si = null;
    public void run() {
      while (true) {
        if (FStop) {
          fl.WriteLog("定时发送数据线程退出！");
          return;
        }
        try {
          int n = 0;
          try {
            n = selector.selectNow(); //获得io准备就绪的channel数量
          }
          catch (Exception ex2) {
          }
          if (n == 0) { //判断当前是否有连接事件要处理
            try {
              Thread.sleep(1);
            }
            catch (InterruptedException ex1) {
            }
            continue ;
          }
          SendData();
          try {
            Thread.sleep(1);
          }
          catch (InterruptedException ex) {
          }
        }
        catch (Exception ex7) {
        }
      }
    }

    public void SendData() {
      Calendar SendTime = Calendar.getInstance(); //发送命令时间，用于判断命令超时的重发
      int ReSendTimes = 0; //命令重复发送次数
      try {
        Set readyKeys = selector.selectedKeys();
        Iterator i = readyKeys.iterator();
        while (i.hasNext()) {
          SelectionKey key = (SelectionKey) i.next(); //取得用于判断当前socket通道的状态对象
          i.remove();
          if (key.isConnectable()) { //说明socket可以准备连接
            SocketChannel keyChannel = (SocketChannel) key.channel();
            keyChannel.register(selector,
                                SelectionKey.OP_READ |
                                SelectionKey.OP_WRITE); //表明现在处于可收可发状态，一般就是继续处理发送时间
            if (keyChannel.isConnectionPending()) { //如果请求连接时间被挂，则主动请求中止
              keyChannel.finishConnect();
            }
            //发送登录命令
            try {
              BuildConnectAndSend(key);
            }
            catch (Exception ex6) {
            }
          }
          if (key.isReadable()) { //该事件是被动触发的
            try {
              receiveData(key); //处理接收到的数据
            }
            catch (Exception ex3) {
            }
          }
          else if (key.isWritable()) {
            int iSize = SendInfoList.size();
            if (GetTerminalOnLineStatus) {
              GetTerminalOnLineStatus = false;
              try {
                BuildTerminalInfoListAndSend(key, TerminalStatusList);
              }
              catch (Exception ex5) {
              }
            }
            else if (SetTerminalOnLineStatus) {
              SetTerminalOnLineStatus = false;
              BuildOffLineTerminalInfoListAndSend(key, TerminalStatusList);
            }
            else if (BuildCertainTerminalInfo) {
              BuildCertainTerminalInfo = false;
              BuildCertainTerminalInfoAndSend(key, TerminalAddr);
            }
            else {
              if (iSize > 0) {
                if (AckBack) {
                  try {
                    si = (SendInfo) SendInfoList.get(0);
                    SendInfoList.remove(0);
                  }
                  catch (Exception ex7) {
                  }
                  ReSendTimes = 0;
                  //AckBack = false;
                  try {
                    BuildSendFrameAndSend(si.AppID,
                                          si.TerminalAddress.toCharArray(),
                                          si.TerminalProtocol,
                                          si.TerminalCommType,
                                          si.TaskID,
                                          si.TaskCount, si.AlarmCount,
                                          si.FunctionCode.toCharArray(),
                                          si.ContentLength,
                                          si.DataContent.toCharArray(), key,
                                          si.SendShortMsg,
                                          si.SendSelfDefinedMsg, si.Priority,
                                          si.ArithmeticNo);
                  }
                  catch (Exception ex1) {
                    fl.WriteLog("发送实时通讯数据异常，异常内容:" + ex1.toString());
                  }
                  SendTime = Calendar.getInstance();
                  SendTime.add(Calendar.MILLISECOND, 100);
                  AckBack = true;
                }
                //屏蔽重发，因为通讯服务不再回确认
                else if (SendTime.before(Calendar.getInstance()) &&
                         (ReSendTimes < 2)) {
                  if (si != null) {
                    //System.err.println("ReSend");
                    fl.WriteLog("实时通讯重发命令，应用ID：" + si.AppID + "，终端逻辑地址：" +
                                si.TerminalAddress + "，数据区内容：" + si.DataContent);
                    ReSendTimes = ReSendTimes + 1;
                    try {
                      BuildSendFrameAndSend(si.AppID,
                                            si.TerminalAddress.toCharArray(),
                                            si.TerminalProtocol,
                                            si.TerminalCommType,
                                            si.TaskID,
                                            si.TaskCount, si.AlarmCount,
                                            si.FunctionCode.toCharArray(),
                                            si.ContentLength,
                                            si.DataContent.toCharArray(), key,
                                            si.SendShortMsg,
                                            si.SendSelfDefinedMsg, si.Priority,
                                            si.ArithmeticNo);
                    }
                    catch (Exception ex2) {
                      fl.WriteLog("发送实时通讯数据异常，异常内容:" + ex2.toString());
                    }
                    SendTime = Calendar.getInstance();
                    SendTime.add(Calendar.MILLISECOND, 100);
                  }
                  else {
                    AckBack = true;
                  }
                }
                else if (ReSendTimes >= 2) {
                  AckBack = true;
                }
                try {
                  Thread.sleep(10);
                }
                catch (InterruptedException ex) {
                }
              }
            }
          }
          try {
            Thread.sleep(1);
          }
          catch (InterruptedException ex) {
          }

        }
      }
      catch (Exception ex4) {
        fl.WriteLog("实时通讯链路异常，异常内容：" + ex4.toString());
        try {
          Thread.sleep(1000 * ReConnectTimes); //根据重连次数决定需要等待的时间
        }
        catch (Exception ex8) {
        }
      }
    }
  }

  public static void main(String[] args) {
    RealTimeCommunication rtc = new RealTimeCommunication("127.0.0.1", 3000);
    rtc.Connect();
    try {
		Thread.sleep(1500);
	} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    String s = "68BD08BD08684B33332222500F6D0000080C99C5EEC7990C5630303030303330383132313356565623280325051100000002B8390002EC390002EC390002EC390002F2390002EC390002F8390002FE390002043A0002EC390002EC390002EC3900020A3A0002103A0002163A00021C3A0002EC390002EC390002EC390002EC390002EC390002EC390002223A0002283A00022E3A0002343A00023A3A0002EC390002EC390002EC3900026245000254450002EC390002EC39000270450002EC390002EC390002EC390002EC390002EC390002EC390002EC390002EC390002EC390002EC390002EC390002EC390002EC390002EC390002EC390002EC390002EC390002EC390002A0450002EC390002EC390002EC390002A84900026E4B0002EC390002EC390002184A0002D84B0002EC390002EC390002EC390002EC390002EC390002EC390002EC390002EC390002BC520002EC390002EC390002EC390002EC390002EC3900028A4A00024E4C0002EC390002EC390002FC4A0002BC4C0002EC390002EC390002EC390002EC390002EC390002EC390002EC390002EC390002EC390002EC390002EC390002EC390002EC390002EC390002EC390002EC390002EC390002EC390002EC390002EC390002EC390002EC390002EC390002EC390002EC390002EC390002EC390002EC390002EC390002EC390002EC390002EC390002103D0002983C0002A63C0002103D0002B83C0002103D0002103D0002103D0002CA3C0002103D0002103D0002103D0002103D0002EE55554816";
    boolean flag = rtc.SendSelfDefinedMsg(1, "33331111".toCharArray(), 100, 50, 1, s.length(), s.toCharArray(), 1);
    //StructReturnMessage rm = rtc.GetNewMessage(0);
    if (flag) {
//     System.out.println("ok");
      while (true) {
        StructReturnMessage rm = rtc.GetNewMessage(516);
        if (rm != null) {
//          System.out.println("Get a New Message!");
          //System.out.println(rm.AppID + " " + rm.TerminalAddr + " " + rm.SJQNR);
          break;
        }
        try {
          Thread.sleep(2000);
        }
        catch (InterruptedException ex) {
        }
      }
    }
    else {
//      System.out.println("fail");
    }
  }

  class SendInfo { //需要下发的内容结构，用于保存到队列中
    public int AppID; //应用ID
    public String TerminalAddress; //终端逻辑地址或者是目标的手机号码
    public int TerminalProtocol; //终端规约号或者是短信发送的类型
    public int TerminalCommType; //终端通讯方式
    public String FunctionCode; //功能码
    public int ContentLength; //数据区长度
    public String DataContent; //数据区内容
    public int TaskID; //任务号
    public int TaskCount; //任务点数
    public int AlarmCount; //异常点数
    public int Priority; //优先级(针对230M通讯类型)
    public int ArithmeticNo; //算法编号(针对国网和天津模块表加密)
    public boolean SendShortMsg = false; //直接发送的标志，用于发送短信
    public boolean SendSelfDefinedMsg = false; //用于发送自定义命令
  }

  private void jbInit() throws Exception {
  }
}

class MessageBody {
  public String TotalLength;
  public String CommandID;
  public String SeqID;
}

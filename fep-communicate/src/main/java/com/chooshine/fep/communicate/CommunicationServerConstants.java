package com.chooshine.fep.communicate;

import java.io.*;
import java.util.*;

import com.chooshine.fep.ConstAndTypeDefine.Log4Fep;
import com.chooshine.fep.ConstAndTypeDefine.Trc4Fep;
import com.chooshine.fep.communicate.CommunicationScheduler.SwitchMPInfo;
import com.chooshine.fep.communicate.CommunicationScheduler.TerminalInfo;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */

public class CommunicationServerConstants {
	  public static int COMMSERVICE_COMMUNICATIONSCHEDULER_PORT; //通讯服务负责通讯调度的监听端口
	  public static int COMMSERVICE_COMMUNICATIONSCHEDULER_TIMEOUT; //通讯服务负责通讯调度的端口超时时间
	  public static int COMMSERVICE_COMMUNICATIONSCHEDULER_TIMEOUT230M; //通讯服务负责通讯调度的端口超时时间
	  public static int COMMSERVICE_COMMUNICATIONSCHEDULER_DATABASESAVEMINUTE; //通讯服务负责通讯调度的数据库回写时间
	  public static int COMMSERVICE_COMMUNICATIONSCHEDULER_MAXCOUNT; //通讯服务负责通讯调度的端口最大连接数量
	  public static int COMMSERVICE_COMMUNICATIONSCHEDULER_SLEEPINTERVAL; //通讯服务睡眠CDMA终端的时间间隔
	  public static int COMMSERVICE_MESSAGEEXCHANGE_PORT; //通讯服务负责处理报文处理的监听端口
	  public static int COMMSERVICE_MESSAGEEXCHANGE_MAXCOUNT; //通讯服务中报文处理socket服务的最大连接数
	  public static int COMMSERVICE_MESSAGEEXCHANGE_TIMEOUT; //通讯服务中报文处理socket链路的超时时间
	  public static int COMMSERVICE_MESSAGEEXCHANGE_WAITLISTMAXCOUNT; //每个实时通讯连接下行等待返回数量上限
	  public static int COMMSERVICE_MESSAGEEXCHANGE_SENDWAITTIMEOUT; //每个下行等待返回超时时间（秒）
	  public static boolean COMMSERVICE_COMMUNICATIONSCHEDULER_BatchSave; //原始通讯记录批量保存标志
	  public static int TERMINAL_HEARTINTERVAL_GPRS; //GPRS终端在线的心跳时间间隔，用于判断终端是否断线
	  public static int TERMINAL_HEARTINTERVAL_CDMA; //CDMA终端在线的心跳时间间隔，用于判断终端是否断线
	  public static String JDBC_CONNECTION_URL; //连接数据库字符串
	  public static String JDBC_CONNECTION_USERNAME; //连接数据库用户名
	  public static String JDBC_CONNECTION_PASSWORD; //连接数据库密码
	  public static String JMS_URL; //jms的RUL
	  public static String JMS_HISDATAQUEUE; //JMS的历史数据QUEUE的名称
	  public static String JMS_ALARMDATAQUEUE; //JMS的异常数据QUEUE的名称
	  public static int JMS_MAXCOUNT; //JMS队列最大数量
	  public static boolean IRANToGregorian;//ChangeIRANToGregorian
	  public static String UPDATEFRAME_UDPIP;//推送返回的目标UDP地址
	  public static int UPDATEFRAME_UDPPORT;//推送返回的目标UDP端口
	  
	  public static String COMMSERVICE_MESSAGEEXCHANGE_ENCRYPTORIP; //加密机IP
	  //public static String DAILY_F62FRAME; //设置F62的命令帧
	  //public static String DAILY_SLEEPDO; //每日自动睡眠、设置F62是否执行的标志
	  //public static String DAILY_SLEEPDO_TIME; //每日自动睡眠、设置F62执行时刻
	  //public static String DAILY_SLEEP_IPRULE; //需要睡眠的ip地址的规则

	  public static List<StructReceiveData> GlobalReceiveJMSList; //全局接收队列，从前置机接收的自动上送的任务和异常
	  public static List<StructReceiveData> GlobalReceiveCallBackList; //全局接收队列，从前置机接收的需要提交的召测返回数据
	  public static List<StructReceiveData> GlobalReceiveSMSList;//全局队列，从短信通道上送的短信数据
	  public static List<StructSendData> GlobalSendList; //全局发送队列，即从主站接收的需要发送的帧
	  public static List<StructDLMSSendData> GlobalDLMSSendList; //DLMS全局发送队列
	  public static List<StructSendData> Global230MSendList; //优先级为1的全局230M发送队列，即从主站接收的需要发送的帧
	  public static List<String> GlobalReceiveUpdateFrameList;//需要推送的升级返回帧
	  public static List<DLMSUpFrame> GlobalReceiveDLMSUpdateFrameList;//需要推送的DLMS升级返回帧
	  public static List<String> GlobalPreSailFrameList;//预售电的返回数据队列
	  //public static List GlobalUPRecordSaveList; //全局上行保存队列
	  //public static List GlobalDownRecordSaveList; //全局下行保存队列
	  public static String LogFileName; //记录错误日志的文件完整路径和文件名
	  public static String TrcFileName; //记录信息日志的文件完整路径和文件名
	  public static Hashtable <String,TerminalInfo>TerminalLocalList = null;
	  public static Hashtable <String,SwitchMPInfo>MPInfoList = null;
	  public static boolean BuildTerminalTag = false;
	  public static String TerminalAddr = "";
	  public static boolean BuildMPTag = false;
	  public static int CLDXH = -1;
	  public static Trc4Fep Trc1 = null;
	  public static Log4Fep Log1 = null;
	  static {
	    init();
	  }
	  
	  static void init() {

	    InputStream filecon = null;
	    try {
	      //创建公用队列
	    	GlobalPreSailFrameList = Collections.synchronizedList(new LinkedList<String>());
	      GlobalReceiveJMSList = Collections.synchronizedList(new LinkedList<StructReceiveData>());
	      GlobalReceiveCallBackList = Collections.synchronizedList(new LinkedList<StructReceiveData>());
	      GlobalSendList = Collections.synchronizedList(new LinkedList<StructSendData>());
	      Global230MSendList = Collections.synchronizedList(new LinkedList<StructSendData>());	
	      GlobalDLMSSendList = Collections.synchronizedList(new LinkedList<StructDLMSSendData>());	 
	      GlobalReceiveSMSList = Collections.synchronizedList(new LinkedList<StructReceiveData>());
	      GlobalReceiveUpdateFrameList = Collections.synchronizedList(new LinkedList<String>());
	      GlobalReceiveDLMSUpdateFrameList = Collections.synchronizedList(new LinkedList<DLMSUpFrame>());
	      Properties prop = new Properties();      

	      LogFileName = "CommService";  
	      TrcFileName = "CommService";         
	      Log1 = new Log4Fep(LogFileName);
	      Trc1 = new Trc4Fep(TrcFileName);
	      
	     // String file_name = "C:/hexing/CommService.config";
	      String file_name = "./CommService.config";
	      File file = new File(file_name);
	      if (!file.exists()) {
	        file_name = "/hexing/CommService.config";
	        file = new File(file_name);
	        if (!file.exists()) {
	          utils.PrintDebugMessage("Can not find CommService.config", "D");
	          System.exit(1);
	        }
	      }
	      
	      //    读取配置文件中的内容
	    //  Trc1.TraceLog("CommunicationServerConstants->file_name:" + file_name);
	      filecon = new FileInputStream(file_name); 
	      prop.load(filecon);
	      COMMSERVICE_MESSAGEEXCHANGE_PORT = Integer.parseInt(((String) prop.
	          getProperty("COMMSERVICE_MESSAGEEXCHANGE_PORT", "3000")).trim());
	      JMS_MAXCOUNT = Integer.parseInt(((String) prop.getProperty("JMS_MAXCOUNT",
	          "5000")).trim());
	      COMMSERVICE_COMMUNICATIONSCHEDULER_PORT = Integer.parseInt(((String) prop.
	          getProperty("COMMSERVICE_COMMUNICATIONSCHEDULER_PORT", "5000")).trim());
	      COMMSERVICE_MESSAGEEXCHANGE_MAXCOUNT = Integer.parseInt(((String) prop.
	          getProperty("COMMSERVICE_MESSAGEEXCHANGE_MAXCOUNT", "100")).trim());
	      COMMSERVICE_MESSAGEEXCHANGE_TIMEOUT = Integer.parseInt(((String) prop.
	          getProperty("COMMSERVICE_MESSAGEEXCHANGE_TIMEOUT", "10")).trim());
	      COMMSERVICE_COMMUNICATIONSCHEDULER_TIMEOUT230M = Integer.parseInt(((
	          String) prop.getProperty("COMMSERVICE_COMMUNICATIONSCHEDULER_TIMEOUT230M", "2")).trim());
	      COMMSERVICE_COMMUNICATIONSCHEDULER_DATABASESAVEMINUTE = Integer.parseInt(((
	          String) prop.getProperty("COMMSERVICE_COMMUNICATIONSCHEDULER_DATABASESAVEMINUTE","15")).trim());
	      COMMSERVICE_COMMUNICATIONSCHEDULER_SLEEPINTERVAL = Integer.parseInt(((
	          String) prop.getProperty("COMMSERVICE_COMMUNICATIONSCHEDULER_SLEEPINTERVAL", "60")).trim());
	      COMMSERVICE_COMMUNICATIONSCHEDULER_MAXCOUNT = Integer.parseInt(((String)
	          prop.getProperty("COMMSERVICE_COMMUNICATIONSCHEDULER_MAXCOUNT", "100")).trim());
	      COMMSERVICE_COMMUNICATIONSCHEDULER_TIMEOUT = Integer.parseInt(((String)
	          prop.getProperty("COMMSERVICE_COMMUNICATIONSCHEDULER_TIMEOUT", "10")).trim());
	      COMMSERVICE_MESSAGEEXCHANGE_WAITLISTMAXCOUNT = Integer.parseInt(((String)
	          prop.getProperty("COMMSERVICE_MESSAGEEXCHANGE_WAITLISTMAXCOUNT","500")).trim());
	      COMMSERVICE_MESSAGEEXCHANGE_SENDWAITTIMEOUT = Integer.parseInt(((String)
	          prop.getProperty("COMMSERVICE_MESSAGEEXCHANGE_SENDWAITTIMEOUT", "60")).trim());
	      COMMSERVICE_MESSAGEEXCHANGE_ENCRYPTORIP = ((String) prop.getProperty(
	              "COMMSERVICE_MESSAGEEXCHANGE_ENCRYPTORIP", "127.0.0.1")).trim();
	      TERMINAL_HEARTINTERVAL_GPRS = Integer.parseInt(((String) prop.getProperty(
	          "TERMINAL_HEARTINTERVAL_GPRS", "1800")).trim());
	      TERMINAL_HEARTINTERVAL_CDMA = Integer.parseInt(((String) prop.getProperty(
	          "TERMINAL_HEARTINTERVAL_CDMA", "120")).trim());
	      JDBC_CONNECTION_URL = ((String) prop.getProperty("JDBC_CONNECTION_URL",
	          "jdbc:oracle:thin:@127.0.0.1:1521:grv40")).trim();
	      JDBC_CONNECTION_USERNAME = ((String) prop.getProperty(
	          "JDBC_CONNECTION_USERNAME", "gd")).trim();
	      JDBC_CONNECTION_PASSWORD = ((String) prop.getProperty(
	          "JDBC_CONNECTION_PASSWORD", "gd")).trim();
	      JMS_URL =((String) prop.getProperty("JMS_URL", "t3://127.0.0.1:7001")).trim();
	      JMS_HISDATAQUEUE =((String) prop.getProperty("JMS_HISDATAQUEUE",
	          "HistoryDataQueue")).trim();
	      JMS_ALARMDATAQUEUE =((String) prop.getProperty("JMS_ALARMDATAQUEUE",
	          "AlarmDataQueue")).trim();
	      /*DAILY_F62FRAME =((String) prop.getProperty("DAILY_F62FRAME", "")).trim();
	      DAILY_SLEEPDO =((String) prop.getProperty("DAILY_SLEEPDO", "false").
	          toLowerCase()).trim();
	      DAILY_SLEEPDO_TIME =((String) prop.getProperty("DAILY_SLEEPDO_TIME",
	          "23:15:00").trim());
	      DAILY_SLEEP_IPRULE =((String) prop.getProperty("DAILY_SLEEP_IPRULE", "1.")).trim();*/
	      String sBatchSave =((String) prop.getProperty(
	          "COMMSERVICE_COMMUNICATIONSCHEDULER_BatchSave", "true")).trim();
	      UPDATEFRAME_UDPPORT = Integer.parseInt(((String)
	              prop.getProperty("UPDATEFRAME_UDPPORT", "9999")).trim());
	      UPDATEFRAME_UDPIP = ((String) prop.getProperty(
	              "UPDATEFRAME_UDPIP", "127.0.0.1")).trim();
	      if (sBatchSave.equals("true")) {
	        COMMSERVICE_COMMUNICATIONSCHEDULER_BatchSave = true;
	      }
	      else {
	        COMMSERVICE_COMMUNICATIONSCHEDULER_BatchSave = false;
	      }
	      String sBoolean = ((String) prop.getProperty(
	          "IRANToGregorian", "false")).trim();
	      if (sBoolean.equals("true")) {
	    	  IRANToGregorian = true;
	      }
	      else {
	    	  IRANToGregorian = false;
	      }
	    }
	    catch (Exception e) {
	    	System.out.print(e.toString());
	    }
	    finally {
	      try {
	        filecon.close();
	      }
	      catch (Exception e) {
	        return;
	      }
	    }
	  }  
	}
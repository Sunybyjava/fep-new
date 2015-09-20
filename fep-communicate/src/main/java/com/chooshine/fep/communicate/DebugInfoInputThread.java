package com.chooshine.fep.communicate;


import java.util.Calendar;
import java.io.*;
import java.text.SimpleDateFormat;

/**
 * <p>Title: 修改程序运行时的调试标志、记录日志的标志线程</p>
 *
 * <p>Description: 根据界面上输入的命令，实时修改程序的运行状态，决定是否要显示调试信息或者记录日志
 *    输入的命令有：Start Debug、Start LogWrite、Finish Debug、Finish LogWrite，这些命令不判断大小写，只要字母
 * 组成成为一条命令即可</p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: Hualong.cn</p>
 *
 * @author Jimmy
 * @version 1.0
 */
public class DebugInfoInputThread
    extends Thread {
  private MessageExchange fMx = null;
  private CommunicationScheduler fCs = null;
  private Calendar c = null;
  private SimpleDateFormat formatter = new SimpleDateFormat(
      "yyyy-MM-dd HH:mm:ss");
  public DebugInfoInputThread() {
  }

  public DebugInfoInputThread(MessageExchange mx, CommunicationScheduler cs) {
    fMx = mx;
    fCs = cs;
    c = Calendar.getInstance();
  }
  
  private String GetDateTime(){
	  Calendar cLogTime = Calendar.getInstance();
      String sDataTime = formatter.format(cLogTime.getTime());
      return sDataTime;
  }

  public void run() {
    while (true) {
      byte b[] = new byte[100];
      try {
        System.in.read(b);
      }
      catch (IOException ex1) {
      }
      String s = new String(b);
      s = s.trim();
      String sTemp = fMx.GetDebugFlag();
      if (s.toLowerCase().equals("open debug")) { //显示调试内容
        if (sTemp.indexOf("D") == -1) { //原来的调试标志无效，则增加调试显示标志，否则不改变原来的运行标志
          sTemp = sTemp.concat("D");
          fMx.ChangeDebugFlag(sTemp);
          fCs.ChangeDebugFlag(sTemp);
          System.out.println(" "+GetDateTime()+"Begin Show Debug Information.");
        }
      }
      else if (s.toLowerCase().equals("open log")) { //记录错误日志
        if (sTemp.indexOf("F") == -1) { //原来不记录错误日志，则增加标志，否则不修改原来的运行标志
          sTemp = sTemp.concat("F");
          fMx.ChangeDebugFlag(sTemp);
          fCs.ChangeDebugFlag(sTemp);
          System.out.println(" "+GetDateTime()+"Begin Write Log Information.");
        }
      }
      else if (s.toLowerCase().equals("stop debug")) { //停止显示调试信息
        if (sTemp.indexOf("D") != -1) { //原来有显示调试信息
          if (sTemp.indexOf("F") != -1) { //并且有记录错误日志，则继续可以记录错误日志，不再显示调试信息
            fMx.ChangeDebugFlag("F");
            fCs.ChangeDebugFlag("F");
          }
          else { //否则清空运行标志，即调试信息和日志都不需要
            fMx.ChangeDebugFlag("");
            fCs.ChangeDebugFlag("");
          }
          System.out.println(" "+GetDateTime()+"Stop Show Debug Information.");
        }
      }
      else if (s.toLowerCase().equals("stop log")) { //停止记录错误日志
        if (sTemp.indexOf("F") != -1) { //原来有记录错误日志
          if (sTemp.indexOf("D") != -1) { //并且有显示调试信息，则继续可以显示调试信息，不再记录错误日志
            fMx.ChangeDebugFlag("D");
            fCs.ChangeDebugFlag("D");
          }
          else { //否则清空运行标志，即调试信息和日志都不需要
            fMx.ChangeDebugFlag("");
            fCs.ChangeDebugFlag("");
          }
          System.out.println(" "+GetDateTime()+"Stop Write Log Information.");
        }
      }
      else if (s.toLowerCase().equals("fepinfo")) { //显示当前的通道信息
          fCs.ShowFepInfo();
      }
      else if (s.toLowerCase().equals("terminalinfo")) { //将当前的终端信息保存到文件中,便于检查终端运行情况
    	  fCs.DisplayTerminalInfo();
      }
      else if (s.toLowerCase().equals("onlinestatus")) { //手工更新终端在线状态
    	  fCs.OnLineStatusCheck();
      }      
      else if (s.toLowerCase().equals("terminalnotindb")) { //显示本次通讯服务启动后，收到的没有档案的终端信息
    	  fCs.RecordNewTerminalInfo();
      }
      else if (s.toLowerCase().equals("starttime")) {
    	  utils.PrintDebugMessage("Program StartTime is: " +
                                formatter.format(c.getTime()), "D");
      }
      else if (s.toLowerCase().equals("writebackterminalinfo")) {
        try {
          fCs.WriteTerminalInfotoDataBase();
        }
        catch (Exception ex2) {
        }
      }
      else if (s.toLowerCase().equals("version")) {
    	  utils.PrintDebugMessage("Program Version is 4.0.0.24", "D");
      }
//      else if (s.toLowerCase().equals("sleepdownonlineterminal")) {
//    	  fCs.SleepDownOnLineTerminal();
//      }
      else if (s.toLowerCase().equals("updateterminalinfo")) {
    	  fCs.ManualUpdateTerminalInfo(10, "");
      }
      /*else if (s.toLowerCase().equals("cdmaonlinesleep")) {
        fCs.DailySleep();
      }*/
      else if (s.toLowerCase().equals("showlistcount")) {
        System.out.println(
            "=============================================================");
        System.out.println("The length of JMS queue:" +
                           CommunicationServerConstants.GlobalReceiveJMSList.
                           size());
        System.out.println("The length of callback queue:" +
                           CommunicationServerConstants.GlobalReceiveCallBackList.
                           size());
        System.out.println("The length of non230-channel send queue:" +
                           CommunicationServerConstants.GlobalSendList.size());
        System.out.println("The length of 230-channel send queue:" +
                           CommunicationServerConstants.Global230MSendList.size());
        fCs.ShowFlowRecordListCount();
        System.out.println(
            "=============================================================");
      }
      else if (s.toLowerCase().equals("help")) {
        System.out.println(
            "=============================================================");
        System.out.println("open debug:Show Debug Info.");
        System.out.println("stop debug:Close Debug Info.");
        System.out.println("open log:Write Log Info.");
        System.out.println("stop log:Stop Write Log Info.");
        System.out.println("FepInfo:Show Valid FepChannel Info.");
        
        System.out.println("TerminalInfo:Write TerminalInfo to File.");
        System.out.println("OnLineStatus:Manual Update TerminalOnLineStatus.");
        
        System.out.println(
            "TerminalNotInDB:Show Terminals Info which are not in DataBase.");
        System.out.println(
            "WriteBackTerminalInfo:Manual Write TerminalInfo to DataBase.");
        System.out.println("StartTime:Show Progam Start Time.");
        System.out.println("Version:Show Progam Current Version.");
//        System.out.println("SleepDownOnLineTerminal:OnLine Terminal Sleep Down");
        System.out.println("UpdateTerminalInfo:Manual Update TerminalInfo.");
//        System.out.println("CDMAOnLineSleep:Current OnLine CDMA SleepDown");
        System.out.println("ShowListCount:Show Current List Count.");
        System.out.println(
            "=============================================================");
      }
      try {
        Thread.sleep(1);
      }
      catch (InterruptedException ex) {
      }
    }
  }
}

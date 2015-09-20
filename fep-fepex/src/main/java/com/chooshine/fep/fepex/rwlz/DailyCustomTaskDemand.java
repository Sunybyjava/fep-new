package com.chooshine.fep.fepex.rwlz;

import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.LinkedList;
import java.util.Hashtable;
import java.sql.ResultSet;

import com.chooshine.fep.FrameDataAreaExplain.SFE_ParamItem;
import com.chooshine.fep.FrameDataAreaExplain.SFE_QGSer_TimeLabel;
import com.chooshine.fep.communicate.utils;
import com.chooshine.fep.fas.realtimecom.RealTimeCommunication;
import com.chooshine.fep.fepex.common.CommonClass;
import com.chooshine.fep.fepex.common.DataAccess;
import com.chooshine.fep.fepex.common.TerminalInfo;
//import hexing.fep.hxv10.common.StructAmmeterInfo;
/**
 * <p>Title: CustomTaskAutomaticDemand</p>
 *
 * <p>Description: 自定义任务轮召</p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: Hualong Technology Hangzhou </p>
 *
 * @author jimmy
 * @version 1.0
 */
public class DailyCustomTaskDemand extends Thread {
    SFE_ParamItem[] ParamItem = null;
    String[] DataItem = null;
    SFE_QGSer_TimeLabel TimeLabel = null;
  //  int iCount = 0;
  //  int ParamCount = 0;
    DataAccess dataAccess = null;
    Calendar StartTime; //程序启动时间
    SimpleDateFormat formatter1 = new SimpleDateFormat("yyyyMMddHHmmss");
    SimpleDateFormat formatter2 = new SimpleDateFormat("yyyyMMdd");
    public DailyCustomTaskDemand() {
        try {
            dataAccess = new DataAccess(CommonClass.JDBC_DATABASETYPE,
                                        CommonClass.JDBC_CONNECTIONURL,
                                        CommonClass.JDBC_USERNAME,
                                        CommonClass.JDBC_PASSWORD);
        } catch (Exception ex) {
        }
    }
    
    private void TaskDemand() {
        //根据执行顺序，决定从开头执行还是结束执行
        StartTime = Calendar.getInstance(); //程序启动时间
        int AppID = 0;
        int ZDCount = 0; //存在漏点的终端列表数量
       // TerminalTaskInfo terminalInfo = new TerminalTaskInfo(); //终端信息
        List zdpz = new ArrayList();

        //调整每个任务的执行时间到程序启动时间以后
        try {
            ZDCount = applicationFunction.gTaskTerminalList.size();
        } catch (Exception ex) {
        }
        if (ZDCount == 0) {
            dataAccess.LogIn(0);
            try {
                GetTaskInfo gti = new GetTaskInfo(dataAccess);
                gti.GetTaskInforList();
            } finally {
                dataAccess.close();
                dataAccess.LogOut(0);
            }
            try {
                ZDCount = applicationFunction.gTaskTerminalList.size();
            } catch (Exception ex) {
            }
        }
        RealTimeCommunication rtc = new RealTimeCommunication(
                CommonClass.REALTIME_COMMUNICATION_IP,
                CommonClass.REALTIME_COMMUNICATION_PORT);
        rtc.Connect();
    	TimeLabel = new SFE_QGSer_TimeLabel();
        try {
            for (int j = 0; j < ZDCount; j++) {
                try {
                    TerminalTaskInfo tti = (TerminalTaskInfo) applicationFunction.gTaskTerminalList.get(j);
                    TerminalTaskInfo terInfo = new TerminalTaskInfo();
                    terInfo = (TerminalTaskInfo) tti;
                    terInfo.ZDLJDZ = terInfo.ZDLJDZ + "10";
                    zdpz.add(terInfo);
                } catch (Exception ex2) {
                }
            }
            while (zdpz.size() > 0){
                //1、判断终端在线状态
                List listTerminalInfo = new LinkedList();
                //100一次进行处理
                //for (int j = 0; j < ZDCount; j++) {
                TerminalTaskInfo t = (TerminalTaskInfo) zdpz.get(0);
                TerminalInfo terminal = new TerminalInfo();
                terminal.TerminalAddress = t.ZDLJDZ.toCharArray();
                terminal.TerminalCommType = t.TXFS;
                terminal.TerminalProtocol = t.GYH;
               // terminal.CldCount = 1;
                terminal.CldLx = 10;
                terminal.Cldxh = t.CLDXH;
                terminal.Clddz = t.CLDDZ;
                ParamItem = new SFE_ParamItem[t.DataCout];
                ParamItem[0] = new SFE_ParamItem();
                ParamItem[0].SetParamCaption(t.DataItemList[0]);
                listTerminalInfo.add(terminal);
                if (t.TaskType == 22) {//日冻结任务
	                if (listTerminalInfo != null) {
	                    StartTime = Calendar.getInstance();
	                    StartTime.add(Calendar.DATE, -1);
	                    String sTime = formatter1.format(StartTime.getTime());
	                    AppID = 1;
	                    applicationFunction.ReadHistoryParameter(rtc, AppID,
	                            listTerminalInfo.size(), listTerminalInfo,
	                            1, ParamItem, TimeLabel, 1, sTime.toCharArray(),
	                            1, 1, 10);
	                }
                }
                else if (t.TaskType == 24){//曲线任务
                	if (listTerminalInfo != null) {
	                    StartTime = Calendar.getInstance();
	                    StartTime.add(Calendar.DATE, -1);
	                    String sTime = formatter2.format(StartTime.getTime())+"000000";
	                    AppID = 1;
	                    applicationFunction.ReadHistoryParameter(rtc, AppID,
	                            listTerminalInfo.size(), listTerminalInfo,
	                            1, ParamItem, TimeLabel, 3, sTime.toCharArray(),
	                            3, 24, 10);
	                }
                }
                
                zdpz.remove(0);                    
            }
            try {
                Thread.sleep(500);
                rtc.DisConnect();
            } catch (InterruptedException ex6) {
            }
        } catch (Exception ex5) {
            utils.PrintDebugMessage("TaskDemand encounter Error " +
                                    ex5.toString(), "D");
        }
        rtc = null;
        try {
            Thread.sleep(1);
        } catch (InterruptedException ex1) {
        }
    }

    public void run() {
    	//ReadBalance();
        TaskDemand();
        for (int i = 1; i < CommonClass.REDOTIMES; i++) {
            try {
                Thread.sleep(CommonClass.REDO_INTERVAL * 3600000); //轮召后等待配置小时后重新召
            } catch (InterruptedException ex) {
            }
            //ReadBalance();
            dataAccess.LogIn(0);
            try {
                GetTaskInfo gti = new GetTaskInfo(dataAccess);
                gti.GetTaskInforList();
            } finally {
                dataAccess.close();
                dataAccess.LogOut(0);
            }
            utils.PrintDebugMessage(
                    "Start Today NextTime DailyTaskDemand.........", "D");
            TaskDemand();
        }
        utils.PrintDebugMessage("Finish Today DailyTaskDemand.", "D");
    }

//获取应用ID
    private int GetYYID() {
        int id = 0;
        dataAccess.LogIn(0);
        try {
            id = Integer.parseInt(dataAccess.GetAutoID("YYID"));
        } catch (Exception ex) {
        } finally {
            dataAccess.close();
            dataAccess.LogOut(0);
        }
        return id;
    }

    class CustonTaskMiss {
        public String TerminalAddr = "";
        public int ZZRWBH = 0;
    }

}

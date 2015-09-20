package com.chooshine.fep.fepex.rwlz;

import java.util.*;
import java.text.SimpleDateFormat;

import com.chooshine.fep.communicate.utils;
import com.chooshine.fep.fepex.common.CommonClass;
import com.chooshine.fep.fepex.common.DataAccess;

public class CustomTaskAutomaticDemand extends Thread {
    Calendar NowDateTime; //当前时间
    Calendar StartTime; //程序启动时间
    DataAccess dataAccess = null;
    private String StartNow = "";
    SimpleDateFormat formatter = null;
    SimpleDateFormat formatter1 = null;
    public CustomTaskAutomaticDemand() {
        try {
            dataAccess = new DataAccess(CommonClass.JDBC_DATABASETYPE,
                                        CommonClass.JDBC_CONNECTIONURL,
                                        CommonClass.JDBC_USERNAME,
                                        CommonClass.JDBC_PASSWORD);
        } catch (Exception ex) {
        }
    }

    public CustomTaskAutomaticDemand(String StartFlag) {
        StartNow = StartFlag;
        try {
            dataAccess = new DataAccess(CommonClass.JDBC_DATABASETYPE,
                                        CommonClass.JDBC_CONNECTIONURL,
                                        CommonClass.JDBC_USERNAME,
                                        CommonClass.JDBC_PASSWORD);
            //applicationFunction.gAmmeterInfoList = GetAmmeterInfoList();
        } catch (Exception ex) {
        }
    }
    
    public void run() {
        Calendar RedoTime = null;
        try {
            StartTime = Calendar.getInstance(); //程序启动时间
            NowDateTime = Calendar.getInstance();
            RedoTime = Calendar.getInstance();
        } catch (Exception ex3) {
        }
        //1、调整补召时间，为明天的0点15以后
        try {
            formatter = new SimpleDateFormat("yyyy-MM-dd");
            StartNow = "startnow";
            if (!StartNow.toLowerCase().equals("startnow")) {
                RedoTime.add(Calendar.DATE, 1);
            }
            String sDate = formatter.format(RedoTime.getTime()) + " " +
            				CommonClass.STARTTIME;
            formatter1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            try {
                java.util.Date d = formatter1.parse(sDate);
                RedoTime.setTime(d);
                utils.PrintDebugMessage("轮召开始时间为：" + sDate,
                "D");
            } catch (Exception ex) {
            }
        } catch (Exception ex2) {
        } while (true) {
            //2、判断时间是否到达
            Calendar c = Calendar.getInstance();
            if (c.after(RedoTime)) {
                GetTaskInfo gti = new GetTaskInfo(dataAccess);
                gti.GetTerminalInforList();
                gti.GetTaskInforList();
                DailyCustomTaskDemand dt = new DailyCustomTaskDemand();
                dt.start();
                utils.PrintDebugMessage("Start Today DailyTaskDemand.........",
                                        "D");
                formatter = new SimpleDateFormat("yyyy-MM-dd");
                RedoTime.add(Calendar.DATE, 1);
                String sDate = formatter.format(RedoTime.getTime()) + " " +
                               CommonClass.STARTTIME;
                formatter1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                try {
                    java.util.Date d = formatter1.parse(sDate);
                    RedoTime.setTime(d);
                } catch (Exception ex) {
                }
            }
            try {
                Thread.sleep(1);
            } catch (InterruptedException ex1) {
            }
        }
    }
    public static void main(String[] args) {
	    try {
	      String sDebug="";
	      if (args.length >0) {  
	    	  sDebug = args[0];
	      }
	      CustomTaskAutomaticDemand td = new CustomTaskAutomaticDemand("startnow");
	   //   TaskDataRedoZJ td = new TaskDataRedoZJ(sDebug);
	      td.start();
	    }
	    catch (Exception ex) {
	      StackTraceElement[] s = ex.getStackTrace();
	      utils.PrintDebugMessage("启动轮召出错，错误信息：" + ex.toString() +
	                              "，错误代码位置为：" + s[0].toString(), "D");
	    }
	    utils.PrintDebugMessage("轮召程序启动......", "D");
	  }

}

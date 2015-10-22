package com.chooshine.fep.fepex.rwlz;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chooshine.fep.communicate.utils;
import com.chooshine.fep.fepex.common.CommonClass;
import com.chooshine.fep.fepex.common.DataAccess;

public class CustomTaskAutomaticDemand {
    private static Logger log = LoggerFactory.getLogger(CustomTaskAutomaticDemand.class.getName());
    DataAccess dataAccess = null;
    private static ExecutorService execeutor = Executors.newFixedThreadPool(1);
    private static ScheduledThreadPoolExecutor schedule = new ScheduledThreadPoolExecutor(1);

    public CustomTaskAutomaticDemand() {
        try {
            dataAccess = new DataAccess(CommonClass.JDBC_DATABASETYPE,
                                        CommonClass.JDBC_CONNECTIONURL,
                                        CommonClass.JDBC_USERNAME,
                                        CommonClass.JDBC_PASSWORD);
            dataAccess.LogIn(0);
        } catch (Exception ex) {
        }
    }
    public void exit() {
        schedule.shutdown();
        execeutor.shutdown();
    }

    public void start() {
        log.info("轮召程序启动,开始时间:[{}]", new Date());
        schedule.scheduleAtFixedRate(new Worker(dataAccess), 60, 60, TimeUnit.SECONDS);
    }

    public static class Worker implements Runnable {
        private DataAccess dataAccess;

        public Worker(DataAccess dataAccess) {
            this.dataAccess = dataAccess;
        }

        @Override
        public void run() {
            GetTaskInfo gti = new GetTaskInfo(dataAccess);
            gti.GetTerminalInforList();
            execeutor.execute(new DailyCustomTaskDemand(dataAccess));
        }

    }

    public static void main(String[] args) {
	    try {
            CustomTaskAutomaticDemand td = new CustomTaskAutomaticDemand();
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

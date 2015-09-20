package com.chooshine.fep.fepex.rwlz;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;
import java.sql.ResultSet;
import java.util.Calendar;
import java.sql.*;
import java.util.Hashtable;

import com.chooshine.fep.communicate.utils;
import com.chooshine.fep.fepex.common.DataAccess;

public class GetTaskInfo {
    public List TerminalTaskInfoList = new ArrayList();
    public List TaskAndTerminalInfoList = new ArrayList();
    public DataAccess dataAccess = null;

    public GetTaskInfo() {
        try {
            jbInit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public GetTaskInfo(DataAccess das) {
        try {
            dataAccess = das;
            applicationFunction.gTaskTerminalList = new LinkedList<TerminalTaskInfo>();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    public void GetTerminalInforList(){
    	ResultSet rset = null;
    	TerminalTaskInfo TaskInfoList = null;
    	String sSQL = "SELECT ZDLJDZ,GYH,DQTXFS,GJMM FROM DA_ZDGZ WHERE GYH=200 AND LENGTH(ZDLJDZ)=4 ORDER BY ZDLJDZ";
        try {
            rset = dataAccess.executeQuery(sSQL);
            while (rset.next()) {                
                try {  
                	TaskInfoList = new TerminalTaskInfo();
                	TaskInfoList.ZDLJDZ = rset.getString("ZDLJDZ").trim();
                	TaskInfoList.CLDXH = 0;
                	TaskInfoList.TaskNo = 0;
                	TaskInfoList.CLDDZ = "";
                	TaskInfoList.DataCout = 1;
                	TaskInfoList.DataItemList = new String[1];
                	TaskInfoList.DataItemList[0] = "HX2067";
                	TaskInfoList.FinishTime = "";
                	TaskInfoList.FrequencyTime = 0;
                	TaskInfoList.GYH = Integer.parseInt(rset.getString("GYH").trim());
                	TaskInfoList.MM = rset.getString("GJMM").trim();
                	TaskInfoList.TXFS = Integer.parseInt(rset.getString("DQTXFS").trim());
                	applicationFunction.gTerminalList.add(TaskInfoList);
                }catch (Exception ex2) {
                }
            }
        }
        catch (Exception ex) {
        } finally {
            try {
                rset.close();
            } catch (Exception ex1) {
            }
        }  
    }
    //获取存在主站任务漏点的终端信息
    public void GetTaskInforList() {
        String sSQL = "SELECT C.ZDLJDZ,C.CLDXH,D.CLDDZ,A.ZZRWBH,E.GJMM,E.DQTXFS,A.GYH,A.ZZRWLX,A.DXLX,A.YXJ,A.ZXCS,A.ZXJG,A.ZXZQ,A.ZXKSSJ,A.ZXJSSJ,B.SJYXH,B.SJYBS"
        			+" FROM RW_ZZRWXX A,RW_ZZRWMX B,RW_ZDZZRWPZ C,DA_CLDXX D,DA_ZDGZ E"
        			+" WHERE A.ZZRWBH=B.ZZRWBH AND A.QDBZ=1 AND C.ZZRWBH=A.ZZRWBH AND C.GYH=A.GYH AND C.ZDLJDZ=E.ZDLJDZ"
        			+" AND C.CLDXH=D.CLDXH AND D.ZDJH=E.ZDJH AND A.GYH=106"
        			+" ORDER BY A.GYH,C.CLDXH,A.ZZRWBH,C.ZDLJDZ,B.SJYXH";
        utils.PrintDebugMessage("获取终端任务信息SQL:" + sSQL, "D");
        BuildTaskAndTerminalInfoList(sSQL);        
    }
    public void BuildTaskAndTerminalInfoList(String sSQL){
    	int iOldRWH = -1, iOldGYH = -1, iOldCLDXH = -1;
    	String sOLDZDLJDZ = "";
    	ResultSet rset = null;
    	TerminalTaskInfo TaskInfoList = null;
        try {
            rset = dataAccess.executeQuery(sSQL);
            while (rset.next()) {                
                try { //SELECT C.ZDLJDZ,C.CLDXH,A.ZZRWBH,A.GYH,A.ZZRWLX,A.DXLX,A.YXJ,A.ZXCS,A.ZXJG,A.ZXZQ,A.ZXKSSJ,A.ZXJSSJ,B.SJYXH,B.SJYBS          	
                    int iGYH = Integer.parseInt(rset.getString("GYH").trim());
                    int iRWH = Integer.parseInt(rset.getString("ZZRWBH").trim());
                    int iCLDXH = Integer.parseInt(rset.getString("CLDXH").trim());
                    String sZDLJDZ = rset.getString("ZDLJDZ").trim();
                    String sCommand = rset.getString("SJYBS").trim();
                    
                    if ((sZDLJDZ.equals(sOLDZDLJDZ)) && (iOldRWH == iRWH) && (iOldGYH == iGYH) && (iOldCLDXH == iCLDXH)){                	
                    	TaskInfoList.DataItemList[TaskInfoList.DataCout] = sCommand;
                    	TaskInfoList.DataCout = TaskInfoList.DataCout + 1;
                    } else {
                    	if (TaskInfoList != null){
                    		applicationFunction.gTaskTerminalList.add(TaskInfoList);
                    	}
                    	TaskInfoList = new TerminalTaskInfo();
                    	TaskInfoList.ZDLJDZ = sZDLJDZ;
                    	TaskInfoList.TaskNo = iRWH;
                    	TaskInfoList.GYH = iGYH;
                    	TaskInfoList.CLDXH = iCLDXH;
                    	TaskInfoList.TXFS = Integer.parseInt(rset.getString("DQTXFS").trim());
                    	TaskInfoList.CLDDZ = rset.getString("CLDDZ").trim();
                    	TaskInfoList.MM = rset.getString("GJMM");
                    	SimpleDateFormat formatter = new SimpleDateFormat("yyMMddHHmmss");
                    	Calendar TTime = Calendar.getInstance();
                    	java.util.Date sStartTime = rset.getDate("ZXKSSJ");
                    	java.util.Date sFinishTime = rset.getDate("ZXJSSJ");
                    	TTime.setTime(sStartTime);
                    	TaskInfoList.StartTime = formatter.format(TTime.getTime());
                    	TTime.setTime(sFinishTime);
                    	TaskInfoList.FinishTime = formatter.format(TTime.getTime());
                    	TaskInfoList.FrequencyTime = Integer.parseInt(rset.getString("ZXJG"));
                    	String NextTime = GetRecentCallTime(TaskInfoList.StartTime,TaskInfoList.FinishTime,TaskInfoList.FrequencyTime);
                    	TaskInfoList.TaskType = Integer.parseInt(rset.getString("ZZRWLX").trim());
                    	TaskInfoList.NextTime = NextTime;
                    	TaskInfoList.DataCout = 0;
                    	TaskInfoList.DataItemList = new String[TaskInfoList.DataCout+1];
                    	TaskInfoList.DataItemList[TaskInfoList.DataCout] = new String();
                    	TaskInfoList.DataItemList[TaskInfoList.DataCout] = sCommand;
                    	TaskInfoList.DataCout += 1;
                    }
                    iOldRWH = iRWH;
                    iOldGYH = iGYH;   
                    sOLDZDLJDZ = sZDLJDZ;
                    iOldCLDXH = iCLDXH;
                }catch (Exception ex2) {
                }                             
                
            }
            if (TaskInfoList != null){
            	applicationFunction.gTaskTerminalList.add(TaskInfoList);
        	}            
            utils.PrintDebugMessage("获取任务信息成功，任务总数:"+applicationFunction.gTaskTerminalList.size(), "D");
            for (int i = 0; i < applicationFunction.gTaskTerminalList.size(); i++){
            	TaskInfoList = (TerminalTaskInfo)applicationFunction.gTaskTerminalList.get(i);
            	String sMLLB = "";
            	for (int j = 0; j < TaskInfoList.DataCout; j++){
            		sMLLB = sMLLB + TaskInfoList.DataItemList[j] + ",";
            	}
            	utils.PrintDebugMessage("ZDLJDZ:" + TaskInfoList.ZDLJDZ +
            							" RWH:" + TaskInfoList.TaskNo +
            							" GYH:" + TaskInfoList.GYH +
            							" RWLX:" + TaskInfoList.TaskType +
            							" CLDXH:" + TaskInfoList.CLDXH +
            							" CLDDZ:" + TaskInfoList.CLDDZ +
            							" MLSL:" + TaskInfoList.DataCout +
            							" MLLB:" + sMLLB.substring(0, sMLLB.length()-1),"D");
            }
        } catch (Exception ex) {
        } finally {
            try {
                rset.close();
            } catch (Exception ex1) {
            }
        }    	
    }
    public String GetRecentCallTime(String StartTime, String FinishTime, int FrequencyTime) throws ParseException{
    	String NextTime = "";
    	Calendar NowTime = Calendar.getInstance();
    	Calendar TStartTime = Calendar.getInstance();;
    	SimpleDateFormat formatter = new SimpleDateFormat("yyMMddHHmmss");
    	NowTime = Calendar.getInstance(); //系统当前
    	String sDate = formatter.format(NowTime.getTime());
    	long iTemp;
    	java.util.Date d,n;
    	
		n= formatter.parse(sDate);
		d = formatter.parse(StartTime);
		TStartTime.setTime(d);
    	int iCallCount = 0;
    	if (FrequencyTime == 0){
    		FrequencyTime = 60;
    		TStartTime.add(Calendar.DATE, 100);
    	}
    	if (FrequencyTime == 30 * 24 * 60){//执行间隔为一月一次，此时不能支持按30天一个月计算
    		iTemp = (n.getTime() - d.getTime()) * 24 * 60;
    		iCallCount = (int)iTemp / (31*24*60);//一个月最多31天，此处按最小执行次数计划，已执行次数，下次执行时间
    		TStartTime.add(Calendar.MONTH, iCallCount);
    		String sTDate = formatter.format(TStartTime.getTime());
    		java.util.Date tm = formatter.parse(sTDate);
    		if ( n.getTime() - tm.getTime() > 2){//一月一次的任务超过两天即不执行
    			TStartTime.add(Calendar.MONTH, 1);
    		}
    		NextTime = formatter.format(TStartTime.getTime());
    	}else if (FrequencyTime == 24 * 60){//执行间隔为一天一次
    		iTemp = (n.getTime() - d.getTime()) /( 24 *  60 * 60 * 1000);
    		iCallCount = (int)iTemp / (24*60);
    		TStartTime.add(Calendar.DATE, iCallCount);
    		String sTDate = formatter.format(TStartTime.getTime());
    		java.util.Date tm = formatter.parse(sTDate);
    		if ( n.getTime() - tm.getTime() > 3/(24*60*60*1000)){//一天一次的任务超过三小时即不执行
    			TStartTime.add(Calendar.DATE, 1);
    		}
    		NextTime = formatter.format(TStartTime.getTime());
    	}else if ((FrequencyTime < 24 * 60) && (FrequencyTime >= 60)){//执行间隔为小时
    		iTemp = (n.getTime() - d.getTime()) * 24 * 60;
    		iCallCount = (int)iTemp / FrequencyTime;
    		TStartTime.add(Calendar.HOUR, iCallCount);
    		String sTDate = formatter.format(TStartTime.getTime());
    		java.util.Date tm = formatter.parse(sTDate);
    		if ( n.getTime() - tm.getTime() > 30/(24*60)){//几小时一次的任务超过30分钟即不执行
    			TStartTime.add(Calendar.HOUR, FrequencyTime / 60);
    		}
    		NextTime = formatter.format(TStartTime.getTime());
    	} else if (FrequencyTime < 60){//执行间隔为分钟
    		iTemp = (n.getTime() - d.getTime()) * 24 * 60;
    		iCallCount = (int)iTemp / FrequencyTime;
    		TStartTime.add(Calendar.MINUTE, iCallCount);
    		String sTDate = formatter.format(TStartTime.getTime());
    		java.util.Date tm = formatter.parse(sTDate);
    		if ( n.getTime() - tm.getTime() > FrequencyTime/(24*60*2)){//几分钟一次的任务超过间隔的1/2时间间隔即不执行
    			TStartTime.add(Calendar.MINUTE, FrequencyTime);
    		}
    		NextTime = formatter.format(TStartTime.getTime());
    	}else {
    		TStartTime.add(Calendar.DATE, 100);
    		NextTime = formatter.format(TStartTime.getTime());
    	}
    	return NextTime;
    }
    private void jbInit() throws Exception {
    }

    class TerminalStruct {
        public String ZDLJDZ = "";
        public String ZDMM = "";
        public String ZDJH = "";
        public int JMSF = 0;
        public int DQTXFS = 0;
        public String ZDSX = "";
    }

}

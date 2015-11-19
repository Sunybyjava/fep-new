package com.chooshine.fep.ConstAndTypeDefine;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
//import java.net.MalformedURLException;
import java.net.URI;
//import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Properties;

import com.chooshine.fep.FrameDataAreaExplain.SFE_AlarmData;
import com.chooshine.fep.FrameDataAreaExplain.SFE_CommandInfo;
import com.chooshine.fep.FrameDataAreaExplain.SFE_DataItem;

//import jcifs.smb.SmbException;
//import jcifs.smb.SmbFile;
//import jcifs.smb.SmbFileInputStream;


import com.chooshine.fep.FrameDataAreaExplain.SFE_HistoryData;
import com.chooshine.fep.FrameDataAreaExplain.SFE_TaskInfo;
import com.chooshine.fep.FrameDataAreaExplain.SPE_CommandInfoList;
import com.chooshine.fep.FrameDataAreaExplain.SPE_TaskInfoList;

//import hexing.fep.communicate.utils;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */

public class Glu_DataAccess {
	private int FDataBaseType = 10; //10--Oracle,20--Sybase,30--mysql

	private String FConnectURL = "";

	private String FUserName = "";

	private String FPassWord = "";

	private String FFileName = "./CommService.config";

	private String FTableName = "RW_RWSJB";

	private int gFunctionType = 0;

	private Connection conn;

	private Statement stmt = null;

	private ResultSet rs = null;

	private PreparedStatement PpstmtOfHistory, PpstmtOfDeleteMissPoint,PHistoryTemp;

	private PreparedStatement PpstmtOfAlarmData, PpstmtOfAlarmDataList;

	private PreparedStatement PpstmtOfCommunicationUp,PpstmtOfCommunicationDown;
	
	private PreparedStatement PpstmtofTaskOne1,PpstmtofTaskOne2,PpstmtofTaskTwo,PpstmtofTaskLately;

	Log4Fep DataAccessLog = new Log4Fep("Glu_DataAccess");

	Trc4Fep DataAccessTrc = new Trc4Fep("Glu_DataAccess");

	public Glu_DataAccess(String path) {
		Properties prop = new Properties();
		InputStream filecon = null;
		if (path.length()==0)
			path = "./CommService.config";
			File file = new File(path);
			if (!file.exists()) {
				FFileName = "C:/hexing/CommService.config";
				file = new File(path);
				if (!file.exists()) {
					DataAccessLog.WriteLog("获取数据库配置信息出错!");
				}
			}

			try {
				filecon = new FileInputStream(path);
			} catch (FileNotFoundException ex) {
				DataAccessLog.WriteLog("获取数据库配置信息出错FileInputStream,错误类型:"+ ex.toString());
			}
//		}
		
		try {
			prop.load(filecon);
		} catch (IOException ex1) {
			DataAccessLog.WriteLog("获取数据库配置信息出错prop.load,错误类型:"+ ex1.toString());
		}

		FDataBaseType = Integer.parseInt((String) prop.get("JDBC_CONNECTION_DBTYPE"));
		FConnectURL = (String) prop.get("JDBC_CONNECTION_URL");
		FUserName = (String) prop.get("JDBC_CONNECTION_USERNAME");
		FPassWord = (String) prop.get("JDBC_CONNECTION_PASSWORD");
        DataAccessTrc.TraceLog("Glu_DataAccess->FConnectURL:" + FConnectURL + " FUserName:" + FUserName);
		try {
			jbInit();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public Glu_DataAccess(URI ur) {
		Properties prop = new Properties();
		File file = new File(ur);
		if (!file.exists()) {			
			file = new File(FFileName);
			if (!file.exists()) {
				DataAccessLog.WriteLog("获取数据库配置信息出错!");
			}
		}

		InputStream filecon = null;
		try {
			filecon = new FileInputStream(file);			
		} catch (FileNotFoundException ex) {
			DataAccessLog.WriteLog("获取数据库配置信息出错FileInputStream,错误类型:"+ ex.toString());
		}
		try {
			prop.load(filecon);
		} catch (IOException ex1) {
			DataAccessLog.WriteLog("获取数据库配置信息出错prop.load,错误类型:"+ ex1.toString());
		}

		FDataBaseType = Integer.parseInt((String) prop.get("JDBC_CONNECTION_DBTYPE"));
		FConnectURL = (String) prop.get("JDBC_CONNECTION_URL");
		FUserName = (String) prop.get("JDBC_CONNECTION_USERNAME");
		FPassWord = (String) prop.get("JDBC_CONNECTION_PASSWORD");
		FTableName = (String) prop.getProperty("JDBC_TASKTABLENAME", "RW_RWSJB");
		DataAccessTrc.TraceLog("Glu_DataAccess->FConnectURL:" + FConnectURL + " FUserName:"+FUserName);
		try {
			jbInit();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	//建立数据库持久连接
		//FunctionType:
		/*
		 0:只建立链接,不与具体操作绑定
		 10:历史数据保存
		 20:异常数据保存
		 30:通讯记录保存
		 */
	public boolean LogIn(int FunctionType) {
		gFunctionType = FunctionType;
		//oracle
		if (FDataBaseType == 10) {
			try {
				Class.forName("oracle.jdbc.driver.OracleDriver");
			} catch (ClassNotFoundException ex) {
				DataAccessLog.WriteLog("链接数据库Class.forName出错,错误信息:"	+ ex.toString());
				System.out.println("链接数据库Class.forName出错,错误信息:" + ex.toString());
				return false;
			}
			try {
				conn = DriverManager.getConnection(FConnectURL, FUserName,
						FPassWord);
			} catch (SQLException ex1) {
				DataAccessLog.WriteLog("链接数据库LogIn出错,错误信息:" + ex1.toString()
						+ "FConnectURL:" + FConnectURL + " FUserName:"
						+ FUserName + " FPassWord:" + FPassWord);
				System.out.println("链接数据库LogIn出错,错误信息:" + ex1.toString()
						+ "FConnectURL:" + FConnectURL + " FUserName:"
						+ FUserName + " FPassWord:" + FPassWord);
				return false;
			}

			if (FunctionType == 10) {
				return LogInOfHistoryDataStor();
			} else if (FunctionType == 20) {
				return LogInOfAlarmDataStor();
			} else if (FunctionType == 30) {
				return LogInOfCommunicationDataStore();
			} else {
				return true;
			}
		}

		else if (FDataBaseType == 20) {
			return false;
		}
		else if (FDataBaseType == 30) {//Mysql
			try {
				Class.forName("com.mysql.jdbc.Driver");
			} catch (ClassNotFoundException ex) {
				DataAccessLog.WriteLog("链接数据库Class.forName出错,错误信息:"	+ ex.toString());
				System.out.println("链接数据库Class.forName出错,错误信息:" + ex.toString());
				return false;
			}
			try {
				conn = DriverManager.getConnection(FConnectURL, FUserName,
						FPassWord);
			} catch (SQLException ex1) {
				System.out.println("链接数据库LogIn出错,错误信息:" + ex1.toString()
				+ "FConnectURL:" + FConnectURL + " FUserName:"
				+ FUserName + " FPassWord:" + FPassWord);
				return false;
			}
			if (FunctionType == 30) {
				return LogInOfCommunicationDataStore();
			} else if (FunctionType == 40)
			{
				LogInOfSaveTask1();
				LogInOfSaveTask2();
				LogInOfSaveLATELY();
				return true;
			}
			else {
				return true;
			}
		}
		return false;
	}
	
	public boolean SaveTaskOne(int CLDBH,int CT,int PT,HashMap<String,String> dataItemMap)
	{
		String P_ACT_TOTAL = dataItemMap.get("P_ACT_TOTAL");
		if (P_ACT_TOTAL!=null)
		{
			try {
				//数据时间可能需要根据调试情况进行调整，后续保存的数据由于数据库格式和保存处理的格式不同也需要进行调试
				//保存不成功的话，需要在这里进行类型转换
				PpstmtofTaskOne1.setInt(1, CLDBH);
				String DATA_TIME = dataItemMap.get("DATA_TIME");
				if (DATA_TIME==null)
					return false;
				PpstmtofTaskOne1.setString(2, DATA_TIME);
				PpstmtofTaskOne1.setString(3, DATA_TIME.substring(0, 8));
				PpstmtofTaskOne1.setInt(4, CT);
				PpstmtofTaskOne1.setInt(5, PT);
				PpstmtofTaskOne1.setString(6, P_ACT_TOTAL);
				PpstmtofTaskOne1.executeUpdate();
			} catch (SQLException e) {
				DataAccessLog.WriteLog("SaveTaskOne出错,错误信息:"	+ e.toString());
				ReConnect();
				return false;
			}
		}
		if (dataItemMap.size()>=11)
		{
			try {
				PpstmtofTaskOne2.setInt(1, CLDBH);
				String DATA_TIME = dataItemMap.get("DATA_TIME");
				if (DATA_TIME==null)
					return false;
				PpstmtofTaskOne2.setString(2, DATA_TIME);
				PpstmtofTaskOne2.setString(3, DATA_TIME.substring(0, 8));
				PpstmtofTaskOne2.setInt(4, CT);
				PpstmtofTaskOne2.setInt(5, PT);
				String ACT_POWER = dataItemMap.get("ACT_POWER");
				if (ACT_POWER==null)
					return false;
				PpstmtofTaskOne2.setString(6, ACT_POWER);
				String REACT_POWER = dataItemMap.get("REACT_POWER");
				if (REACT_POWER==null)
					return false;
				PpstmtofTaskOne2.setString(7, REACT_POWER);
				String CUR_A = dataItemMap.get("CUR_A");
				if (CUR_A==null)
					return false;
				PpstmtofTaskOne2.setString(8, CUR_A);
				String CUR_B = dataItemMap.get("CUR_B");
				if (CUR_B==null)
					return false;
				PpstmtofTaskOne2.setString(9, CUR_B);
				String CUR_C = dataItemMap.get("CUR_C");
				if (CUR_C==null)
					return false;
				PpstmtofTaskOne2.setString(10, CUR_C);
				String VOLT_A = dataItemMap.get("VOLT_A");
				if (VOLT_A==null)
					return false;
				PpstmtofTaskOne2.setString(11, VOLT_A);
				String VOLT_B = dataItemMap.get("VOLT_B");
				if (VOLT_B==null)
					return false;
				PpstmtofTaskOne2.setString(12, VOLT_B);
				String VOLT_C = dataItemMap.get("VOLT_C");
				if (VOLT_C==null)
					return false;
				PpstmtofTaskOne2.setString(13, VOLT_C);
				String POWER_FACTOR = dataItemMap.get("POWER_FACTOR");
				if (POWER_FACTOR==null)
					return false;
				PpstmtofTaskOne2.setString(14, POWER_FACTOR);
				PpstmtofTaskOne2.executeUpdate();
			} catch (Exception e) {
				DataAccessLog.WriteLog("SaveTaskOne出错,错误信息:"	+ e.toString());
				ReConnect();
				return false;
			}
		}
		return true;
	}
	
	public boolean SaveTaskTwo(int CLDBH,int CT,int PT,HashMap<String,String> dataItemMap)
	{
		if (dataItemMap.size()>=10)
		{
			try {
				PpstmtofTaskTwo.setInt(1, CLDBH);
				String DATA_TIME = dataItemMap.get("DATA_TIME");
				if (DATA_TIME==null)
					return false;
				PpstmtofTaskTwo.setString(2, DATA_TIME);
				PpstmtofTaskTwo.setString(3, DATA_TIME.substring(0, 8));
				PpstmtofTaskTwo.setInt(4, CT);
				PpstmtofTaskTwo.setInt(5, PT);
				String P_ACT_TOTAL = dataItemMap.get("P_ACT_TOTAL");
				if (P_ACT_TOTAL==null)
					return false;
				PpstmtofTaskTwo.setString(6, P_ACT_TOTAL);
				String P_ACT_SHARP = dataItemMap.get("P_ACT_SHARP");
				if (P_ACT_SHARP==null)
					return false;
				PpstmtofTaskTwo.setString(7, P_ACT_SHARP);
				String P_ACT_PEAK = dataItemMap.get("P_ACT_PEAK");
				if (P_ACT_PEAK==null)
					return false;
				PpstmtofTaskTwo.setString(8, P_ACT_PEAK);
				String P_ACT_LEVEL = dataItemMap.get("P_ACT_LEVEL");
				if (P_ACT_LEVEL==null)
					return false;
				PpstmtofTaskTwo.setString(9, P_ACT_LEVEL);
				String P_ACT_VALLEY = dataItemMap.get("P_ACT_VALLEY");
				if (P_ACT_VALLEY==null)
					return false;
				PpstmtofTaskTwo.setString(10, P_ACT_VALLEY);
				String I_ACT_TOTAL = dataItemMap.get("I_ACT_TOTAL");
				if (I_ACT_TOTAL==null)
					return false;
				PpstmtofTaskTwo.setString(11, I_ACT_TOTAL);
				String P_REACT_TOTAL = dataItemMap.get("P_REACT_TOTAL");
				if (P_REACT_TOTAL==null)
					return false;
				PpstmtofTaskTwo.setString(12, P_REACT_TOTAL);
				String P_ACT_MAX_DEMAND = dataItemMap.get("P_ACT_MAX_DEMAND");
				if (P_ACT_MAX_DEMAND==null)
					return false;
				PpstmtofTaskTwo.setString(13, P_ACT_MAX_DEMAND);
				String P_ACT_MAX_DEMAND_TIME = dataItemMap.get("P_ACT_MAX_DEMAND_TIME");
				if (P_ACT_MAX_DEMAND_TIME==null)
					return false;
				int iYear = Calendar.getInstance().get(Calendar.YEAR);
                P_ACT_MAX_DEMAND_TIME = Integer.toString(iYear)+"-"+P_ACT_MAX_DEMAND_TIME+":00";
				PpstmtofTaskTwo.setString(14, P_ACT_MAX_DEMAND_TIME);
				PpstmtofTaskTwo.executeUpdate();
			} catch (Exception e) {
				DataAccessLog.WriteLog("SaveTaskTwo出错,错误信息:"	+ e.toString());
				ReConnect();
				return false;
			}
		}
		return true;
	}
	
	public boolean SaveTaskLately(int CLDBH,int CT,int PT,HashMap<String,String> dataItemMap)
	{
		if (dataItemMap.size()>=3)
		{
			try {
				PpstmtofTaskLately.setInt(1, CLDBH);
				String DATA_TIME = dataItemMap.get("DATA_TIME");
				if (DATA_TIME==null)
					return false;
				DATA_TIME = "20"+DATA_TIME+":00";
				PpstmtofTaskLately.setString(2, DATA_TIME);
				PpstmtofTaskLately.setInt(3, CT);
				PpstmtofTaskLately.setInt(4, PT);
				String P_ACT_MAX_DEMAND = dataItemMap.get("P_ACT_MAX_DEMAND");
				if (P_ACT_MAX_DEMAND==null)
					return false;
				PpstmtofTaskLately.setString(5, P_ACT_MAX_DEMAND);
				String P_ACT_MAX_DEMAND_TIME = dataItemMap.get("P_ACT_MAX_DEMAND_TIME");
				if (P_ACT_MAX_DEMAND_TIME==null)
					return false;
				int iYear = Calendar.getInstance().get(Calendar.YEAR);
				P_ACT_MAX_DEMAND_TIME = Integer.toString(iYear)+"-"+P_ACT_MAX_DEMAND_TIME+":00";
				PpstmtofTaskLately.setString(6, P_ACT_MAX_DEMAND_TIME);
				PpstmtofTaskLately.executeUpdate();
			} catch (SQLException e) {
				DataAccessLog.WriteLog("SaveTaskLately出错,错误信息:"+ e.toString());
				ReConnect();
				return false;
			}
		}
		return true;
	}
	
	private boolean LogInOfSaveTask1()//任务1采用insert into的方式
	{
		try
		{
			String sSql = "INSERT INTO ent_d_eq_reading(MP_ID,DATA_TIME,RECEIVE_TIME,DDATE,CT_RATIO,PT_RATIO,P_ACT_TOTAL,DATA_FLAG) "
					+ "VALUES(?,?,SYSDATE(),?,?,?,?,1)";
			PpstmtofTaskOne1 = conn.prepareStatement(sSql);
			String sSql1 = "INSERT INTO ent_d_power(MP_ID,DATA_TIME,RECEIVE_TIME,DDATE,CT_RATIO,PT_RATIO,"
					+ "ACT_POWER,REACT_POWER,CUR_A,CUR_B,CUR_C,VOLT_A,VOLT_B,VOLT_C,POWER_FACTOR,DATA_FLAG) "
					+ "VALUES(?,?,SYSDATE(),?,?,?,?,?,?,?,?,?,?,?,?,1)";
			PpstmtofTaskOne2 = conn.prepareStatement(sSql1);
			return true;
		}catch (Exception e)
		{
			return false;
		}
	}
	
	private boolean LogInOfSaveTask2()//任务2采用replace into的方式
	{
		try
		{
			String sSql = "REPLACE INTO ent_d_eq_reading(MP_ID,DATA_TIME,RECEIVE_TIME,DDATE,CT_RATIO,PT_RATIO,"
					+ "P_ACT_TOTAL,P_ACT_SHARP,P_ACT_PEAK,P_ACT_LEVEL,P_ACT_VALLEY,I_ACT_TOTAL,P_REACT_TOTAL,P_ACT_MAX_DEMAND,P_ACT_MAX_DEMAND_TIME,DATA_FLAG) "
					+ "VALUES(?,?,SYSDATE(),?,?,?,?,?,?,?,?,?,?,?,?,1)";
			PpstmtofTaskTwo = conn.prepareStatement(sSql);
			return true;
		}catch (Exception e)
		{
			return false;
		}
	}
	
	private boolean LogInOfSaveLATELY()//轮招任务保存
	{
		try
		{
			String sSql = "REPLACE INTO ent_d_eq_reading_lately(MP_ID,DATA_TIME,RECEIVE_TIME,CT_RATIO,PT_RATIO,"
					+ "P_ACT_MAX_DEMAND,P_ACT_MAX_DEMAND_TIME,DATA_FLAG) "
					+ "VALUES(?,?,SYSDATE(),?,?,?,?,1)";
			PpstmtofTaskLately = conn.prepareStatement(sSql);
			return true;
		}catch (Exception e)
		{
			return false;
		}
	}

	private boolean LogInOfCommunicationDataStore() {
		if (FDataBaseType == 10) { //Oracle
			try {
				conn.setAutoCommit(false);
				String strSQL = "insert into run_sxtxjl(LYDZ,MBDZ,TXFS,TXSJ,TXNR,ZDLJDZ) values(?,?,?,sysdate,?,?)";
				PpstmtOfCommunicationUp = conn.prepareStatement(strSQL);
				strSQL = "insert into run_xxtxjl(LYDZ,MBDZ,TXFS,TXSJ,TXNR,ZDLJDZ) values(?,?,?,sysdate,?,?)";
				PpstmtOfCommunicationDown = conn.prepareStatement(strSQL);
				return true;
			} catch (SQLException ex2) {
				DataAccessLog.WriteLog("通讯记录保存绑定SQL出错,错误信息:" + ex2.toString());
				return false;
			}
		} else if (FDataBaseType == 20) { //Sybase
			return false;
		} else if (FDataBaseType == 30) {//Mysql
			try {
				conn.setAutoCommit(false);
				String strSQL = "insert into run_sxtxjl(LYDZ,MBDZ,TXFS,TXSJ,TXNR,ZDLJDZ) values(?,?,?,NOW(),?,?)";
				PpstmtOfCommunicationUp = conn.prepareStatement(strSQL);
				strSQL = "insert into run_xxtxjl(LYDZ,MBDZ,TXFS,TXSJ,TXNR,ZDLJDZ) values(?,?,?,NOW(),?,?)";
				PpstmtOfCommunicationDown = conn.prepareStatement(strSQL);
				return true;
			} catch (SQLException ex2) {
				DataAccessLog.WriteLog("通讯记录保存绑定SQL出错,错误信息:" + ex2.toString());
				return false;
			}
		}
		return false;

	}

	private boolean LogInOfHistoryDataStor() {
		if (FDataBaseType == 10) { //Oracle  ��ݿ�
			try {
				conn.setAutoCommit(false); //���������ύ
				String strSQL = "INSERT INTO "
						+ FTableName
						+ " (RWH,CLDXH,CLDXZ,SJXDM,SJZ,SJSJ,JSSJ,CLZT,ZDLJDZ) VALUES(?,?,?,?,?,to_date(?,'yyyymmddhh24miss'),sysdate,?,?)";
				PpstmtOfHistory = conn.prepareStatement(strSQL);
				strSQL = "INSERT INTO RW_RWSJBTEMP"
					+ " (RWH,CLDXH,CLDXZ,SJXDM,SJZ,SJSJ,JSSJ,CLZT,ZDLJDZ) VALUES(?,?,?,?,?,to_date(?,'yyyymmddhh24miss'),sysdate,?,?)";
				PHistoryTemp = conn.prepareStatement(strSQL);
				strSQL = "DELETE FROM RW_LDSJ WHERE ZDLJDZ=? AND CLDXH=? AND CLDXZ=? "
						+ "AND RWH=? AND SJSJ=TO_DATE(?,'yyyymmddhh24miss')";
				PpstmtOfDeleteMissPoint = conn.prepareStatement(strSQL);
				return true;
			} catch (SQLException ex2) {
				DataAccessLog.WriteLog("��ʷ��ݱ����SQL����,������Ϣ:" + ex2.toString());
				return false;
			}
		} else if (FDataBaseType == 20) { //Sybase
			return false;
		}
		return false;
	}

	private boolean LogInOfAlarmDataStor() {
		if (FDataBaseType == 10) { //Oracle
			try {
				conn.setAutoCommit(false);
				String strSQL = "insert into YCCL_YCSJ (YCSJBH,ZDLJDZ,CLDXH,CLDXZ,YCBM,YCFSSJ,YCJSSJ,ZT,DWDM)"
						+ " values(?,?,?,?,?,to_date(?,'yyyymmddhh24miss'),to_date(?,'yyyymmddhh24miss'),?,?)";
				PpstmtOfAlarmData = conn.prepareStatement(strSQL);
				strSQL = "insert into YCCL_YCSJMX (YCSJBH,SJXBS,SJZ,DWDM) values(?,?,?,?)";
				PpstmtOfAlarmDataList = conn.prepareStatement(strSQL);
				return true;
			} catch (SQLException ex3) {
				DataAccessLog.WriteLog("�쳣��ݱ����SQL����,������Ϣ:" + ex3.toString());
				return false;
			}
		} else if (FDataBaseType == 20) { //Sybase
			return false;
		} else if (FDataBaseType == 30) {//Mysql
			return false;
		}
		return false;
	}

	public boolean LogOut(int FunctionType) {
		//Oracle
		if ((FDataBaseType == 10)||(FDataBaseType == 30)) {
			if (FunctionType == 10) {
				try {
					PpstmtOfHistory.close();
					PHistoryTemp.close();
				} catch (SQLException ex) {
				}
			} else if (FunctionType == 20) {
				try {
					PpstmtOfAlarmData.close();
					PpstmtOfAlarmDataList.close();
				} catch (SQLException ex1) {
				}
			} else if (FunctionType == 30) {
				try {
					PpstmtOfCommunicationUp.close();
					PpstmtOfCommunicationDown.close();
				} catch (SQLException ex3) {
				}
			}
			try {
				conn.close();
				return true;
			} catch (SQLException ex2) {
			}
		}

		else if (FDataBaseType == 20) {
			return false;
		}
		return false;
	}

	private boolean Commit() {
		try {
			conn.commit();
			return true;
		} catch (SQLException ex) {
			DataAccessLog.WriteLog("Commit Failed,E:" + ex.toString());
			ReConnect();
			return false;
		}

	}


	private boolean ReConnect() {
		try {
			LogOut(gFunctionType);
			return LogIn(gFunctionType);
		} catch (Exception ex) {
			DataAccessLog.WriteLog("ReConnect Failed,E:" + ex.toString());
			return false;
		}

	}

	public boolean DeleteMissingPoint(char ZDLJDZ[],
			SFE_HistoryData HistoryList[], int HistoryCount) {
		try {
			String sZDLJDZ = new String(ZDLJDZ).trim();
			for (int i = 0; i < HistoryCount; i++) {
				try {
					int CLDXH = HistoryList[i].GetMeasuredPointNo();
					int CLDXZ = HistoryList[i].GetMeasuredPointType();
					String RWSJ = new String(HistoryList[i].GetTaskDateTime())
							.trim();
					int RWH = HistoryList[i].GetTaskNo();
					if (FDataBaseType == 10) { //Oracle  ��ݿ�
						DeleteMPByOracle(sZDLJDZ, CLDXH, CLDXZ, RWH, RWSJ);
					} else if (FDataBaseType == 20) { //Sybase  ��ݿ�
						DeleteMPBySybase(sZDLJDZ, CLDXH, CLDXZ, RWH, RWSJ);
					}
				} catch (Exception ex) {
				}
			}
			return Commit();
		} catch (Exception ex) {
			DataAccessLog.WriteLog("SaveHistoryData Failed,E:" + ex.toString());
			return false;
		}
	}

	public boolean SaveHistoryData(char ZDLJDZ[], int GYH,
			SFE_HistoryData HiltoryList[], int HistoryCount) {
		try {
			String sZDLJDZ = new String(ZDLJDZ).trim();
			String sCLDDZ = new String(HiltoryList[0].GetMeasuredAdd());

			for (int i = 0; i < HistoryCount; i++) {
				int CLDXH = HiltoryList[i].GetMeasuredPointNo();
				///sZDLJDZ = GetMAddress(CLDXH,sZDLJDZ);
				if (GYH == 200 && CLDXH == 0) {
//					Statement st_DWDM = conn.createStatement();
					String SQL = "select a.CLDXH as CLDXH from da_cldxx a,da_zdgz b where a.zdjh=b.zdjh and substr('000000000000',0,12-length(a.clddz))||a.clddz='"
							+ sCLDDZ + "' and b.zdljdz='" + sZDLJDZ + "'";
					Statement stmtemp = null;
					ResultSet rset = null;
					try{
						stmtemp = conn.createStatement();
						rset = stmtemp.executeQuery(SQL);
						//  ResultSet rset = executeQuery(SQL); //st_DWDM.executeQuery(SQL); 
						while (rset.next()) {							
							//String sCLDXH= rset.getString("CLDXH");
							//CLDXH = Integer.parseInt(sCLDXH);
							CLDXH = rset.getInt("CLDXH");
							DataAccessTrc.TraceLog("SaveHistoryData ZDLJDZ="
									+ new String(ZDLJDZ) + " CLDDZ=" + sCLDDZ
									+ " CLDXH=" + CLDXH);
						
						}
					}catch (Exception ex) {
						DataAccessLog
						.WriteLog("SaveHistoryData Getting CLDXH error:"
								+ ex.toString());
					}
					finally{
						rset.close();
						stmtemp.close();
					}
					
				}
				int CLDLX = HiltoryList[i].GetMeasuredPointType();
				String LSSJSJ = new String(HiltoryList[i].GetTaskDateTime());
				int LSSJH = HiltoryList[i].GetTaskNo();
				if (!HiltoryList[i].DataItemList.isEmpty()) {
					SFE_DataItem DataItem; //= new SFE_DataItem();
					for (int j = 0; j < HiltoryList[i].DataItemList.size(); j++) {
						DataItem = (SFE_DataItem) HiltoryList[i].DataItemList.get(j);
						String SJXBS = new String(DataItem.GetDataCaption()).trim();
						String SJXNR = new String(DataItem.GetDataContent()).trim();
						if (FDataBaseType == 10) { //Oracle
							StorHistoryDataByOracle(LSSJH, sZDLJDZ, CLDLX,CLDXH, LSSJSJ, SJXBS, SJXNR);
						} else if (FDataBaseType == 20) { //Sybase
							StorHistoryDataBySybase(LSSJH, sZDLJDZ, CLDLX,CLDXH, LSSJSJ, SJXBS, SJXNR);
						}
					}
				}
			} //for (int i = 0; i < iCount; i++)
			return Commit();
			//return true;
		} catch (Exception ex) {
			DataAccessLog.WriteLog("SaveHistoryData����:" + ex.toString());
			return false;
		}
	}

	@SuppressWarnings("rawtypes")
	public boolean SaveUpFlowRecordToDB(ArrayList CommRecordList) {
		if ((FDataBaseType == 10)||(FDataBaseType == 30)) { //Oracle,Mysql
			try {
				int icount = CommRecordList.size();
				Struct_CommRecordItem CommRecord = null; // new Struct_CommRecordItem();
				for (int i = 0; i < icount; i++) {
					CommRecord = (Struct_CommRecordItem) CommRecordList.get(i);
					String LYDZ = new String(CommRecord.GetSourceAddress()).trim();
					String MBDZ = new String(CommRecord.GetTargetAddress()).trim();
					String TerminalAddress = new String(CommRecord.GetTerminalAddress()).trim();
					String Content = new String(CommRecord.GetMessageContent()).trim();
					int iChannelType = CommRecord.GetChannelType();
					StoreCommunicationUpFlowDataByOracle(LYDZ, MBDZ,
							iChannelType, Content, TerminalAddress);
				}
				return Commit();
			} catch (Exception ex) {
				DataAccessLog.WriteLog("SaveUpFlowRecordToOracleDB :"+ ex.toString());
				return false;
			}

		}

		else if (FDataBaseType == 20) { //Sybase
			try {
				return false;
			} catch (Exception ex) {
				DataAccessLog.WriteLog("SaveUpFlowRecordToSybaseDB :" + ex.toString());
				return false;
			}
		} else {
			return false;
		}
	}

	@SuppressWarnings("rawtypes")
	public boolean SaveDownFlowRecordToDB(ArrayList CommRecordList) {
		if ((FDataBaseType == 10)||(FDataBaseType == 30)) { //Oracle
			try {
				int icount = CommRecordList.size();
				Struct_CommRecordItem CommRecord = null; // new Struct_CommRecordItem();
				for (int i = 0; i < icount; i++) {
					CommRecord = (Struct_CommRecordItem) CommRecordList.get(i);
					String LYDZ = new String(CommRecord.GetSourceAddress()).trim();
					String MBDZ = new String(CommRecord.GetTargetAddress()).trim();
					String TerminalAddress = new String(CommRecord.GetTerminalAddress()).trim();
					String Content = new String(CommRecord.GetMessageContent()).trim();
					int iChannelType = CommRecord.GetChannelType();
					StoreCommunicationDownFlowDataByOracle(LYDZ, MBDZ,iChannelType, Content, TerminalAddress);
				}
				return Commit();

			} catch (Exception ex) {
				DataAccessLog.WriteLog("SaveDownFlowRecordToDB����:"
						+ ex.toString());
				return false;
			}
		} else if (FDataBaseType == 20) { //Sybase
			try {
				return false;
			} catch (Exception ex) {
				DataAccessLog.WriteLog("SaveDownFlowRecordToSybaseDB����:"
						+ ex.toString());
				return false;
			}

		} else {
			return false;
		}
	}

	//�����쳣���
	public boolean SaveAlarmData(char ZDLJDZ[], int DWDM,
			SFE_AlarmData AlarmList[], int AlarmDataCount) {
		try {
			int iYCSJBH = 0;
			int iSaveResult = -1;
			String sZDLJDZ = new String(ZDLJDZ).trim();
			for (int i = 0; i < AlarmDataCount; i++) {
				int CLDXH = AlarmList[i].GetMeasuredPointNo();
				int CLDLX = AlarmList[i].GetMeasuredPointType();
				String YCDM = new String(AlarmList[i].GetAlarmCode()).trim();
				String YCFSSJ = new String(AlarmList[i].GetAlarmDateTime())
						.trim();

				//�쳣��ݱ���
				if (FDataBaseType == 10) { //Oracle  ��ݿ�
					String YCSJBH = GetSequence("ycsjbh"); //��ȡ�쳣��ݱ���
					iYCSJBH = Integer.parseInt(YCSJBH);
					DataAccessTrc.TraceLog("SaveAlarmData iYCSJBH:"+iYCSJBH+" ZDLJDZ:"+sZDLJDZ+" YCDM:"+YCDM);
					iSaveResult = StorAlarmDataByOracle(iYCSJBH, sZDLJDZ,
							CLDXH, CLDLX, YCDM, YCFSSJ, DWDM);
				} else if (FDataBaseType == 20) { //Sybase  ��ݿ�
					iYCSJBH = 0; //��ȡ�쳣��ݱ���
					iSaveResult = StorAlarmDataBySybase(iYCSJBH, sZDLJDZ,
							CLDXH, CLDLX, YCDM, YCFSSJ);
				}
				//�쳣������
				if (iSaveResult == 0) {
					if (AlarmList[i].GetDataItemCount()>0) {
						SFE_DataItem DataItem = new SFE_DataItem();
						for (int j = 0; j < AlarmList[i].DataItemList.size(); j++) {
							DataItem = (SFE_DataItem) AlarmList[i].DataItemList
									.get(j);
							String sDataCaption = new String(DataItem
									.GetDataCaption());
							String sDataContent = new String(DataItem
									.GetDataContent());
							if (FDataBaseType == 10) { //Oracle  ��ݿ�
								StorAlarmDataItemByOracle(iYCSJBH,
										sDataCaption, sDataContent, DWDM);
							} else if (FDataBaseType == 20) { //Sybase  ��ݿ�
								StorAlarmDataItemBySybase(iYCSJBH,
										sDataCaption, sDataContent);
							}
						}
					}
				}
			}
			//�ύ��ݿ�
			return Commit();
		} catch (NumberFormatException ex) {
			DataAccessLog.WriteLog("SaveAlarmData����:" + ex.toString());
			return false;
		}
	}

	//ȡ��λ����
	public int GetDWDM() {
		int iDWDM = 0;
		if (FDataBaseType == 10) { //oracle��ݿ�
			try {
				Statement st_DWDM = conn.createStatement();
				ResultSet rset = st_DWDM
						.executeQuery("select DWDM from XT_DWDM");
				if (rset.next()) {
					iDWDM = rset.getInt("DWDM");
				}
				st_DWDM.close();
			} catch (SQLException ex) {
				DataAccessLog.WriteLog("GetDWDM���� " + ex.toString());
			}
			return iDWDM;
		} else {
			return 0;
		}
	}

	public SPE_CommandInfoList GetCommandInfoList(int ProtocolType) { //��ʼ��������Ϣ����
		SPE_CommandInfoList CommandInfoList = new SPE_CommandInfoList();
		ResultSet rset = null;
		Statement stmttemp = null;
		int iCommandCount = 0;
		try {
			try {
				/*String sSQL1 = "";
				String sSQL2 = " AND a.CSLX=10 ORDER BY a.SJYBS,a.CSLX,a.SJXH";
				String sSqlStr = "";
				if (ProtocolType >= 11 && ProtocolType < 80) { //����Լ
					sSQL1 = "SELECT a.SJYBS,a.SJXBS,a.SJXH,a.CSLX,a.SJLX,a.SJGS,a.SJCD,a.CCSX,a.CCLX FROM DATA_ITEM_DB a WHERE a.GYH=";
				} else { //�ն˹�Լ
					sSQL1 = "SELECT a.SJYBS,a.SJXBS,a.SJXH,a.CSLX,a.SJLX,a.SJGS,a.SJCD,a.CCSX,a.CCLX FROM DATA_ITEM_ZD a WHERE a.GYH=";
				}
				sSqlStr = sSQL1 + ("" + ProtocolType) + sSQL2;
				//System.out.println("========"+sSqlStr);
				*/
				String sSQL1 = "";
                String sSQL2 = " ORDER BY SJYBS,CSLX,SJXH";
                String sSqlStr = "";
                String sSQL3 = "";
                if (ProtocolType >= 11 && ProtocolType < 80) { //ammeter
                    sSQL1 = "SELECT a.SJYBS,a.SJXBS,a.SJXH,a.CSLX,a.SJLX,a.SJGS,a.SJCD,a.CCSX,a.CCLX FROM DATA_ITEM_DB a WHERE a.CSLX=10 AND a.GYH=";
                } else { //terminal
                    sSQL1 = "SELECT a.SJYBS,a.SJXBS,a.SJXH,a.CSLX,a.SJLX,a.SJGS,a.SJCD,a.CCSX,a.CCLX FROM DATA_ITEM_ZD a WHERE a.CSLX=10 AND a.GYH=";
                }
                if (ProtocolType == Glu_ConstDefine.GY_ZD_DLMS){//Profile data of dlms                  	
                	sSQL3 = " UNION "
                		+ " SELECT DISTINCT'000700'||SUBSTR(TO_CHAR(A.RWH, '0XX'), 3, 2)||'180300FF02' AS SJYBS,"
                		+ " B.SJXBS,1 AS SJXH,B.CSLX,B.SJLX,B.SJGS,B.SJCD,B.CCSX,B.CCLX"
                		+ " FROM RW_RWXX A,DATA_ITEM_ZD B"
                		+ " WHERE B.SJXBS='HD0000' AND A.GYH="
                		+ ("" + Glu_ConstDefine.GY_ZD_DLMS)
                		+ " UNION"
                		+ " SELECT '000700'||SUBSTR(TO_CHAR(A.RWH, '0XX'), 3, 2)||'180300FF02' AS SJYBS,"
                		+ " B.SJXBS,A.SJYXH + b.sjxh AS SJXH,B.CSLX,B.SJLX,B.SJGS,B.SJCD,B.CCSX,B.CCLX"
                		+ " FROM RW_RWXX A,DATA_ITEM_ZD B"
                		+ " WHERE A.SJYBS= B.SJYBS AND A.GYH="
                		+ ("" + Glu_ConstDefine.GY_ZD_DLMS);
                }else if (ProtocolType == Glu_ConstDefine.GY_DB_DLMS){//Profile data of dlms                  	
                	sSQL3 = " UNION "
                		+ " SELECT DISTINCT'000700'||SUBSTR(TO_CHAR(A.RWH, '0XX'), 3, 2)||'180300FF02' AS SJYBS,"
                		+ " B.SJXBS,1 AS SJXH,B.CSLX,B.SJLX,B.SJGS,B.SJCD,B.CCSX,B.CCLX"
                		+ " FROM RW_RWXX A,DATA_ITEM_DB B"
                		+ " WHERE B.SJXBS='HD0000' AND A.GYH="
                		+ ("" + Glu_ConstDefine.GY_ZD_DLMS)
                		+ " UNION"
                		+ " SELECT '000700'||SUBSTR(TO_CHAR(A.RWH, '0XX'), 3, 2)||'180300FF02' AS SJYBS,"
                		+ " B.SJXBS,A.SJYXH + b.sjxh AS SJXH,B.CSLX,B.SJLX,B.SJGS,B.SJCD,B.CCSX,B.CCLX"
                		+ " FROM RW_RWXX A,DATA_ITEM_DB B"
                		+ " WHERE A.SJYBS= B.SJYBS AND A.GYH="
                		+ ("" + Glu_ConstDefine.GY_ZD_DLMS);
                }
                sSqlStr = sSQL1 + ("" + ProtocolType) + sSQL3 + sSQL2;

				SFE_CommandInfo CommandInfo = new SFE_CommandInfo();
				stmttemp = conn.createStatement(
						ResultSet.TYPE_SCROLL_INSENSITIVE,
						ResultSet.CONCUR_READ_ONLY); //����һ�����Թ�����ֻ����SQL������
				rset = stmttemp.executeQuery(sSqlStr);

				String sOldDataCaption = "";

				String sDataCaption = ""; //������ʶ
				int iDataType = 0; //�������
				int iDataLen = 0; //��ݳ���
				String sDataFormat = ""; //�������ݸ�ʽ
				int iStorageType = 0; //�洢��ʽ��10 BCD;20 HEX;30 ASCII��
				int iStorageOrder = 0; //�洢˳��10˳��  20����
				if(rset.next()){
					while (!rset.isAfterLast()) {
						if (rset.isFirst()) {
							sOldDataCaption = rset.getString("SJYBS");
						}
						if (sOldDataCaption.equals(rset.getString("SJYBS"))) {
							CommandInfo.CommandInfoAdd(rset.getString("SJYBS"),
									ProtocolType);
							sDataCaption = rset.getString("SJXBS");
							iDataType = Integer.parseInt(rset.getString("SJLX"));
							iDataLen = Integer.parseInt(rset.getString("SJCD"));
							sDataFormat = rset.getString("SJGS");
							iStorageType = Integer.parseInt(rset.getString("CCLX"));
							iStorageOrder = Integer
									.parseInt(rset.getString("CCSX"));
							CommandInfo.DataItemInfoAdd(sDataCaption, iDataType,
									iDataLen, sDataFormat, iStorageType,
									iStorageOrder);
							if (rset.isLast()) {
								CommandInfoList.CommandInfoList.add(CommandInfo);
								iCommandCount = iCommandCount + 1;
							}
							rset.next();
						} else {
							// CommandInfoList.CommandInfoList.ensureCapacity(1000000);
							CommandInfoList.CommandInfoList.add(CommandInfo);
							CommandInfo = new SFE_CommandInfo();
							iCommandCount = iCommandCount + 1;
							sOldDataCaption = rset.getString("SJYBS");
						}
					}
					// System.out.println("========iCommandCount:"+iCommandCount);				
					CommandInfoList.SetFCommandCount(iCommandCount);
					// conn.close();
				}
				
			} catch (Exception e) {
				DataAccessLog.WriteLog("GetCommandInfoList����: " + e.toString() + " ProtocolType=" + ProtocolType + " CommandCount="+iCommandCount);
				CommandInfoList.SetFCommandCount(iCommandCount);
			}

			if (rset != null) {
				try {
					rset.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (stmttemp != null) {
				try {
					stmttemp.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		} finally {
		}
		return CommandInfoList;

	}

	@SuppressWarnings("rawtypes")
	public static void main(String[] args) {
		Glu_DataAccess da = new Glu_DataAccess("");
		da.LogIn(30);
		Hashtable lTerminalList = da.GetTerminalList();
		String a = "12345678";
		TerminalInfo ti = (TerminalInfo) lTerminalList.get(a);
		if (ti != null) {
			System.out.println("ok");
		}
		//SPE_TaskInfoList t = new SPE_TaskInfoList();
		//t = da.GetTaskInfoList(105);
		da.GetTaskInfoList(105);
	}

	@SuppressWarnings("rawtypes")
	public Hashtable GetTerminalList() {
		Hashtable<String, TerminalInfo> lTerminalList = new Hashtable<String, TerminalInfo>();
		try {
			Statement stmt = conn.createStatement();
			ResultSet rset = stmt
					.executeQuery("select A.ZDLJDZ as ZDLJDZ,A.SIM as SIMID,A.DQTXFS as TXFS,B.ZDWLDZ as WLDZ,B.TDIP as TDIP from DA_ZDGZ A,RUN_ZDDZXX B where A.ZDLJDZ=B.ZDLJDZ and A.DQTXFS=B.ZDTXFS");
			while (rset.next()) {
				TerminalInfo ti = new TerminalInfo();
				ti.TerminalAddress = rset.getString("ZDLJDZ"); //�ն��߼���ַ
				ti.TerminalSIMID = rset.getString("SIMID"); //�ն�SIM������
				ti.SIMLocalAddress = "";
				ti.UDPLocalAddress = "";
				ti.TCPLocalAddress = "";
				ti.CommandIndex = 0;
				ti.COMSourceSIMID = "1380013800"; //���ڶ�Ӧ��SIM�����룬һ��û���ô�
				ti.COMLocalAddress = "1";
				int i = rset.getInt("TXFS");
				switch (i) { //����ն˵�ǰͨѶ��ʽд��GPRS�����ַ
				case 10: {
					ti.COMCommunicationIP = rset.getString("TDIP");
					break;
				}
				case 30: {
					ti.SIMCommunicationIP = rset.getString("TDIP");
					break;
				}
				case 40: {
					ti.UDPCommunicationIp = rset.getString("TDIP");
					ti.TerminalUDPAddress = rset.getString("WLDZ"); //UDPͨѶ�������ַ
					break;
				}
				case 50: {
					ti.TCPCommunicationIp = rset.getString("TDIP");
					ti.TerminalTCPAddress = rset.getString("WLDZ"); //TCPͨѶ�������ַ
					break;
				}
				default: {
					break;
				}
				}
				lTerminalList.put(ti.TerminalAddress, ti);
			}
			stmt.close();
		} catch (SQLException ex) {
			DataAccessLog.WriteLog("GetTerminalList����:" + ex.toString());
			return null;
		}
		return lTerminalList;
	}

	@SuppressWarnings("finally")
	public SPE_TaskInfoList GetTaskInfoList(int ProtocolType) { // ��ʼ��������Ϣ����
		SPE_TaskInfoList TaskInfoList = new SPE_TaskInfoList();
		Statement stmttemp = null;
		ResultSet rset = null;
		int iTaskCount = 0;
		try {
			try {
				String sSQL1 = "SELECT RWH,RWLX,SJYBS,CLDXH,GYH FROM RW_RWXX WHERE (RWLX=1 OR RWLX=2) AND GYH=";
				String sSQL2 = " ORDER BY RWH,SJYXH";
				String sSqlStr = sSQL1 + ("" + ProtocolType) + sSQL2;

				SFE_TaskInfo TaskInfo = new SFE_TaskInfo();
				stmttemp = conn.createStatement(
						ResultSet.TYPE_SCROLL_INSENSITIVE,
						ResultSet.CONCUR_READ_ONLY); // ����һ�����Թ�����ֻ����SQL������
				rset = stmttemp.executeQuery(sSqlStr);
				String sOldTaskNo = "";

				if (rset.next()){// ����ݼ�
					while (!rset.isAfterLast()) {
						if (rset.isFirst()) {
							sOldTaskNo = rset.getString("RWH");
						}
						if (sOldTaskNo.equals(rset.getString("RWH"))) {
							TaskInfo.TaskInfoAdd(Integer.parseInt(sOldTaskNo),
									Integer.parseInt(rset.getString("RWLX")),
									Integer.parseInt(rset.getString("GYH")),
									Integer.parseInt(rset.getString("CLDXH")));
							TaskInfo.CommandAdd(rset.getString("SJYBS"));
							if (rset.isLast()) {
								TaskInfoList.TaskInfoList.add(TaskInfo);
								iTaskCount = iTaskCount + 1;
							}
							rset.next();
						} else {
							TaskInfoList.TaskInfoList.add(TaskInfo);
							TaskInfo = new SFE_TaskInfo();
							iTaskCount = iTaskCount + 1;
							sOldTaskNo = rset.getString("RWH");
						}
					}
					TaskInfoList.SetFTaskCount(iTaskCount);
				}				
				
				if (rset != null) {
					rset.close();
				}
				if (stmttemp != null) {
					stmttemp.close();
				}
			} catch (Exception e) {
				 DataAccessLog.WriteLog("GetTaskInfoList����:" + e.toString()+" ProtocolType:"+ProtocolType + " TaskCount:"+ iTaskCount);
			}
			try {
				String sSqlStr = "select A.ZZRWBH AS ZZRWBH,A.SJYBS AS SJYBS,B.ZZRWLX AS ZZRWLX from RW_ZZRWMX A,RW_ZZRWXX B WHERE A.ZZRWBH=B.ZZRWBH AND B.GYH="
						+ ProtocolType + " order by ZZRWBH";
				SFE_TaskInfo TaskInfo = new SFE_TaskInfo();
				stmttemp = conn.createStatement(
						ResultSet.TYPE_SCROLL_INSENSITIVE,
						ResultSet.CONCUR_READ_ONLY); // ����һ�����Թ�����ֻ����SQL������
				rset = stmttemp.executeQuery(sSqlStr);
				String sOldTaskNo = "";
				if (rset.next()){// ����ݼ�
					while (!rset.isAfterLast()) {
						if (rset.isFirst()) {
							sOldTaskNo = rset.getString("ZZRWBH");
						}
						if (sOldTaskNo.equals(rset.getString("ZZRWBH"))) {
							TaskInfo.TaskInfoAdd(Integer.parseInt(sOldTaskNo), rset
									.getInt("ZZRWLX"), ProtocolType, 0);
							TaskInfo.CommandAdd(rset.getString("SJYBS"));
							if (rset.isLast()) {
								TaskInfoList.TaskInfoList.add(TaskInfo);
								iTaskCount = iTaskCount + 1;
							}
							rset.next();
						} else {
							TaskInfoList.TaskInfoList.add(TaskInfo);
							TaskInfo = new SFE_TaskInfo();
							iTaskCount = iTaskCount + 1;
							sOldTaskNo = rset.getString("zzrwbh");
						}
					}
					TaskInfoList.SetFTaskCount(iTaskCount);
				}
				
			} catch (Exception e) {
				 DataAccessLog.WriteLog("GetTaskInfoList����:" + e.toString()+" ProtocolType:"+ProtocolType + " TaskCount:"+ iTaskCount);
			} finally {
				if (rset != null) {
					rset.close();
				}
				if (stmttemp != null) {
					stmttemp.close();
				}
			}
		} finally {
			return TaskInfoList;
		}
	}

	public String GetAutoID(String SequenceName) {
		String sID = new String("0");
		if (FDataBaseType == 10) { // Oracle ��ݿ�
			sID = GetSequence(SequenceName);
		} else if (FDataBaseType == 20) { // Sybase ��ݿ�

		}
		return sID;
	}

	@SuppressWarnings("unused")
	private String GetCurrentDateTime() {
		Calendar cLogTime = Calendar.getInstance();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		return formatter.format(cLogTime.getTime());
	}

	// Oracleȡ������ˮ��
	private String GetSequence(String SequenceName) {

		String sID = new String("0");
		ResultSet rset = null;
		try {
			Statement st_YCSJLSH = conn.createStatement();
			rset = st_YCSJLSH.executeQuery("select " + SequenceName
					+ ".nextval from dual");
			if (rset.next()) {
				sID = rset.getString(1);
			}
			st_YCSJLSH.close();
		} catch (SQLException ex) {
			DataAccessLog.WriteLog("GetSequence����,SequenceName:" + SequenceName
					+ " ������Ϣ��" + ex.toString());
			ReConnect();
		} finally {
			try {
				rset.close();
			} catch (SQLException ex1) {
			}
		}
		return sID;
	}

	private int StoreCommunicationUpFlowDataByOracle(String TargetAddress,
			String SourceAddress, int SourceChannel, String MessageContent,
			String ZDLJDZ) {
		try {
	//		DataAccessLog.WriteLog("StoreCommunicationUpFlowDataByOracle");
			if (PpstmtOfCommunicationUp == null) {
				LogIn(30);
			}
			PpstmtOfCommunicationUp.setString(1, SourceAddress);
			PpstmtOfCommunicationUp.setString(2, TargetAddress);
			PpstmtOfCommunicationUp.setInt(3, SourceChannel);
			PpstmtOfCommunicationUp.setString(4, MessageContent);
			PpstmtOfCommunicationUp.setString(5, ZDLJDZ);
			PpstmtOfCommunicationUp.executeUpdate();
			return 0;
		} catch (SQLException ex) {
			DataAccessLog.WriteLog("StoreCommunicationUpFlowDataByOracle:"
					+ ex.toString());
			return -1;
		}
	}

	//Sybase��ݿⱣ������ͨѶ��¼
	/*
	 private int StoreCommunicationUpFlowDataBySybase(String TargetAddress,
	 String SourceAddress,
	 int SendChannel,
	 String MessageContent,
	 String ZDLJDZ) {
	 return -1;
	 }*/

	//Oracle��ݿⱣ������ͨѶ��¼
	private int StoreCommunicationDownFlowDataByOracle(String TargetAddress,
			String SourceAddress, int SendChannel, String MessageContent,
			String ZDLJDZ) {
		try {
			if (PpstmtOfCommunicationDown == null) { //��ݿ�û�����ӳɹ�������
				LogIn(30);
			}
			PpstmtOfCommunicationDown.setString(1, SourceAddress);
			PpstmtOfCommunicationDown.setString(2, TargetAddress);
			PpstmtOfCommunicationDown.setInt(3, SendChannel);
			PpstmtOfCommunicationDown.setString(4, MessageContent);
			PpstmtOfCommunicationDown.setString(5, ZDLJDZ);
			PpstmtOfCommunicationDown.executeUpdate();
			return 0;
		} catch (SQLException ex) {
			DataAccessLog.WriteLog("StoreCommunicationDownFlowDataByOracle����:"
					+ ex.toString());
			return -1;
		}
	}

	//Sybase��ݿⱣ������ͨѶ��¼
	/*  private int StoreCommunicationDownFlowDataBySybase(String MsgDst,
	 int SendChannel, String MessageContent, String ZDLJDZ) {
	 return -1;
	 }
	 */
	//Oracle��ݿ��쳣��ݱ���
	private int StorAlarmDataByOracle(int YCSJBH, String ZDLJDZ, int CLDXH,
			int CLDXZ, String YCBM, String YCFSSJ, int DWDM) {
		String YCJSSJ;// = GetCurrentDateTime();
		Calendar cLogTime = Calendar.getInstance();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		YCJSSJ = formatter.format(cLogTime.getTime());
		String sSQL = "select ZDLJDZ from YCCL_YCSJ where ZDLJDZ='" + ZDLJDZ
				+ "' and CLDXH=" + CLDXH + " and CLDXZ=" + CLDXZ
				+ " and YCBM ='" + YCBM + "' and YCFSSJ =to_date('" + YCFSSJ
				+ "','yyyymmddhh24miss')";
		ResultSet rstemp = null;
		try {
			rstemp = executeQuery(sSQL);
		} catch (Exception ex1) {
		}
		try {
			if (!rstemp.next()) {
				PpstmtOfAlarmData.setInt(1, YCSJBH); //�쳣��ݱ��
				PpstmtOfAlarmData.setString(2, ZDLJDZ); //�ն��߼���ַ
				PpstmtOfAlarmData.setInt(3, CLDXH); //���������
				PpstmtOfAlarmData.setInt(4, CLDXZ); //���������ʵ�λ����
				PpstmtOfAlarmData.setString(5, YCBM.trim()); //�쳣����
				PpstmtOfAlarmData.setString(6, YCFSSJ.trim()); //�쳣ʱ��
				PpstmtOfAlarmData.setString(7, YCJSSJ.trim()); //�쳣�ӽ���ʱ��
				PpstmtOfAlarmData.setInt(8, 10); //����״
				PpstmtOfAlarmData.setInt(9, DWDM); //����״
				PpstmtOfAlarmData.executeUpdate(); //�ύ
				return 0;
			} else {
				return -1;
			}
		} catch (SQLException ex) {
			DataAccessLog.WriteLog("StorAlarmDataByOracle����:" + ex.toString());
			return -1;
		} finally {
			close();
		}
	}

	//Oracle��ݿ��쳣�����ϸ����
	private int StorAlarmDataItemByOracle(int YCSJBH, String DataCaption,
			String DataContent, int DWDM) {
		try {
			PpstmtOfAlarmDataList.setInt(1, YCSJBH);
			PpstmtOfAlarmDataList.setString(2, DataCaption);
			PpstmtOfAlarmDataList.setString(3, DataContent);
			PpstmtOfAlarmDataList.setInt(4, DWDM);
			PpstmtOfAlarmDataList.executeUpdate();
			return 0;
		} catch (SQLException ex) {
			DataAccessLog.WriteLog("StorAlarmDataItemByOracle����:"
					+ ex.toString());
			return -1;
		}
	}

	//Sybase��ݿ��쳣��ݱ���
	private int StorAlarmDataBySybase(int YCSJBH, String ZDLJDZ, int CLDXH,
			int CLDXZ, String YCBM, String YCFSSJ) {
		return -1;
	}

	//Sybase��ݿ��쳣�����ϸ����
	private int StorAlarmDataItemBySybase(int YCSJBH, String DataCaption,
			String DataContent) {
		return -1;
	}

	//Oracle��ݿ�ɾ��©����Ϣ
	private void DeleteMPByOracle(String ZDLJDZ, int CLDXH, int CLDXZ, int RWH,
			String SJSJ) {
		try {
			PpstmtOfDeleteMissPoint.setString(1, ZDLJDZ);
			PpstmtOfDeleteMissPoint.setInt(2, CLDXH);
			PpstmtOfDeleteMissPoint.setInt(3, CLDXZ);
			PpstmtOfDeleteMissPoint.setInt(4, RWH);
			PpstmtOfDeleteMissPoint.setString(5, SJSJ);
			PpstmtOfDeleteMissPoint.executeUpdate();
		} catch (Exception ex) {
			DataAccessLog.WriteLog("DeleteMPByOracle����:" + ex.toString());
		}
	}

	//Sybase��ݿ�ɾ��©����Ϣ
	private void DeleteMPBySybase(String ZDLJDZ, int CLDXH, int CLDXZ, int RWH,
			String SJSJ) {
		try {

		} catch (Exception ex) {
			DataAccessLog.WriteLog("DeleteMPBySybase����:" + ex.toString());
		}
	}

	//Oracle��ݿ���ʷ��ݱ���
	private int StorHistoryDataByOracle(int TaskDataNo, String ZDLJDZ,
			int CLDLB, int CLDH, String SJSJ, String SJXDM, String SJZ) {
		//String JSSJ = GetCurrentDateTime();
		
		try {
			PHistoryTemp.setInt(1, TaskDataNo);
			PHistoryTemp.setInt(2, CLDH);
			PHistoryTemp.setInt(3, CLDLB);
			PHistoryTemp.setString(4, SJXDM);
			PHistoryTemp.setString(5, SJZ);
			PHistoryTemp.setString(6, SJSJ);
			//      PpstmtOfHistory.setString(7, JSSJ);
			PHistoryTemp.setInt(7, 10);
			PHistoryTemp.setString(8, ZDLJDZ);
			PHistoryTemp.executeUpdate();
		} catch (SQLException ex) {
			DataAccessLog
					.WriteLog("StorHistoryDataByOracle����:" + ex.toString());
			return -1;
		}
		try {
			PpstmtOfHistory.setInt(1, TaskDataNo);
			PpstmtOfHistory.setInt(2, CLDH);
			PpstmtOfHistory.setInt(3, CLDLB);
			PpstmtOfHistory.setString(4, SJXDM);
			PpstmtOfHistory.setString(5, SJZ);
			PpstmtOfHistory.setString(6, SJSJ);
			//      PpstmtOfHistory.setString(7, JSSJ);
			PpstmtOfHistory.setInt(7, 10);
			PpstmtOfHistory.setString(8, ZDLJDZ);
			PpstmtOfHistory.executeUpdate(); //�ύ
			return 0;
		} catch (SQLException ex) {
			DataAccessLog
					.WriteLog("StorHistoryDataByOracle����:" + ex.toString());
			return -1;
		}
	}

	//Sybase��ݿ���ʷ��ݱ���
	private int StorHistoryDataBySybase(int TaskDataNo, String ZDLJDZ,
			int CLDLB, int CLDH, String SJSJ, String SJXDM, String SJZ) {
		return -1;
	}

	//��ѯ����
	public ResultSet executeQuery(String sql) throws Exception {
		try {
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			sql = new String(sql.getBytes("GBK"), "ISO8859_1");
			rs = stmt.executeQuery(sql);
		} catch (SQLException ex) {
			System.out.println("sql.executeQuery:" + ex.getMessage());
			DataAccessLog.WriteLog("sql.executeQuery:" + ex.getMessage());
			if (ex.getMessage().indexOf("Connection reset by peer") != -1) {
				ReConnect();
			}
		} catch (Exception ex1) {
			return null;
		}
		//    stmt.close();
		return rs;
	}

	public void close() {
		try {
			if (rs != null) {
				rs.close();
			}
			if (stmt != null) {
				stmt.close();
			}
			/*if (conn != null) {
			 conn.close();
			 }*/
		} catch (SQLException ex1) {

		}
	}

	public int executeUpdate(String sql) throws Exception {
		int iResult = -1;
		try {
			Statement stmt = conn.createStatement();
			sql = new String(sql.getBytes("GBK"), "ISO8859_1");
			iResult = stmt.executeUpdate(sql);
			conn.commit();
			stmt.close();
		} catch (SQLException ex) {
			System.out.println("sql.executeUpdate:" + ex.getMessage());
			DataAccessLog.WriteLog("sql.executeUpdate:" + ex.getMessage());
			if (ex.getMessage().indexOf("Connection reset by peer") != -1) {
				ReConnect();
			}
		}
		return iResult;

	}

	private void jbInit() throws Exception {
	}

	class TerminalInfo { //终端逻辑地址和物理地址的对应关系结构
		public String TerminalAddress = ""; //终端逻辑地址

		public int CommandIndex = 0; //命令序号

		public String TerminalSIMID = ""; //终端SIM卡号码

		public String SIMCommunicationIP = ""; //短信前置机的IP

		public String SIMLocalAddress = ""; //短信来源的主叫号码

		public String COMCommunicationIP = ""; //串口通讯的前置机IP

		public String COMSourceSIMID = ""; //串口通讯的对应的SIM卡号码

		public String COMLocalAddress = ""; //串口来源的主叫号码

		public String TerminalUDPAddress = ""; //终端UDP的物理地址

		public String UDPCommunicationIp = ""; //UDP前置机的IP

		public String UDPLocalAddress = ""; //UDP来源的通道地址

		public String TerminalTCPAddress = ""; //终端TCP的物理地址

		public String TCPCommunicationIp = ""; //TCP前置机的IP

		public String TCPLocalAddress = ""; //TCP来源的通道地址
	}
}

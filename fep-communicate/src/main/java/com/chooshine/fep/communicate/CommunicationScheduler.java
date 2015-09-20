package com.chooshine.fep.communicate;


import java.nio.ByteBuffer;
import java.util.Calendar;
import java.util.Iterator;
import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Hashtable;
import java.nio.channels.Selector;
import java.util.*;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;

import com.chooshine.fep.ConstAndTypeDefine.Glu_ConstDefine;
import com.chooshine.fep.ConstAndTypeDefine.Glu_DataAccess;
import com.chooshine.fep.ConstAndTypeDefine.Struct_CommRecordItem;
import com.chooshine.fep.ConstAndTypeDefine.Trc4Fep;
import com.chooshine.fep.FrameDataAreaExplain.GetFrameInfo;
import com.chooshine.fep.FrameExplain.FE_FrameExplain;
import com.chooshine.fep.FrameExplain.Struct_FrameInfo;

//import java.sql.*;

/**
 * <p>Title: 通讯调度类</p>
 *
 * <p>Description: 负责和前置机通讯，进行报文下发和接收工作；同时对于异常确认，更新终端GPRS通讯的物理地址</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class CommunicationScheduler extends Thread {
	private int Port = 0; //通讯调度模块本地监听端口号

	private int MaxCount = 0; //本地监听最大连接数（包括前置机链路与预付费链路列表总和）

	//private int TimeOut = 0; //本地连接超时时间，单位为分钟
	private SocketCommunicationBase sc = null; //socket服务对象

	private List<FepLinkList> FepCommList; //前置机链路列表

	private List<PreLinkList> PreCommList; //预付费加密、写卡等链路列表

	private int Sequence = 0; //流水号

	private String Debug = ""; //是否显示调试信息的标志

	private int RecvCount = 0;

	private Glu_DataAccess da_SaveRecord = null;

	private Glu_DataAccess da = null;

	private boolean AckBack = true; //确认信号是否返回的标志

	private ArrayList<Struct_CommRecordItem> UpFlowRecordSaveList; //上行通讯记录保存队列

	private ArrayList<Struct_CommRecordItem> DownFlowRecordSaveList; //下行通讯记录保存队列

	private int BatchSaveNum = 1; //批量保存数量

	private List<TerminalInfo> NewTerminalList = null; //新增加的终端队列

	public List<Struct_CommRecordItem> GlobalDownRecordSaveList = Collections
			.synchronizedList(new LinkedList<Struct_CommRecordItem>());

	public List<Struct_CommRecordItem> GlobalUPRecordSaveList = Collections
			.synchronizedList(new LinkedList<Struct_CommRecordItem>());

	SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");

	SimpleDateFormat formatter1 = null;

	public CommunicationScheduler() {
		try {
			jbInit();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void main(String[] args) {
		CommunicationScheduler a = new CommunicationScheduler(
				CommunicationServerConstants.COMMSERVICE_COMMUNICATIONSCHEDULER_PORT,
				CommunicationServerConstants.COMMSERVICE_COMMUNICATIONSCHEDULER_MAXCOUNT,
				CommunicationServerConstants.COMMSERVICE_COMMUNICATIONSCHEDULER_TIMEOUT,
				CommunicationServerConstants.COMMSERVICE_COMMUNICATIONSCHEDULER_TIMEOUT230M,
				CommunicationServerConstants.COMMSERVICE_COMMUNICATIONSCHEDULER_BatchSave,
				"-D");
		a.start();
	}

	public CommunicationScheduler(int ListenPort, int MaxConnCount,
			int ConnTimeOut, int WaitTimeOut230M, boolean BatchSave,
			String Debug_Flag) {
		try {
			GlobalDownRecordSaveList = Collections
					.synchronizedList(new LinkedList<Struct_CommRecordItem>());
			GlobalUPRecordSaveList = Collections
					.synchronizedList(new LinkedList<Struct_CommRecordItem>());
			NewTerminalList = new LinkedList<TerminalInfo>();
			Port = ListenPort;
			MaxCount = MaxConnCount;
			//TimeOut = ConnTimeOut;
			if (BatchSave) { //批量保存标志有效
				BatchSaveNum = 50; //批量保存的数量
			}
			Debug = Debug_Flag;
			sc = new SocketCommunicationBase();

			try {
				da_SaveRecord = new Glu_DataAccess("");
				da_SaveRecord.LogIn(30);
			} catch (Exception ex3) {
				CommunicationServerConstants.Log1
						.WriteLog("Establish Database connection failed(Save the raw data)!");
			}

			try {
				da = new Glu_DataAccess("");
				da.LogIn(30);
			} catch (Exception ex3) {
				CommunicationServerConstants.Log1
						.WriteLog("Establish Database connection failed！");
			}
			FepCommList = new ArrayList<FepLinkList>(); //初始化前置机通道连接队列
			PreCommList = new ArrayList<PreLinkList>(); //初始化预付费链路连接队列
			UpFlowRecordSaveList = new ArrayList<Struct_CommRecordItem>();
			DownFlowRecordSaveList = new ArrayList<Struct_CommRecordItem>();
			CommunicationServerConstants.TerminalLocalList = new Hashtable<String, TerminalInfo>(
					50000);
			CommunicationServerConstants.MPInfoList = new Hashtable<String, SwitchMPInfo>(
					50000);
			InitTerminalLocalList(); //初始化终端本地信息
			InitSwitchMeasurePointList();//initialize measere point list which switched concentrator
			try {
				sc.init(Port); //Socket服务端初始化
			} catch (Exception ex) {
				CommunicationServerConstants.Log1
						.WriteLog("CommunicationScheduler:Init Socket Server Port error,error is "
								+ ex.toString());
				//服务初始化失败，说明端口被占用，关闭应用
				CommunicationServerConstants.Trc1.TraceLog("Init Port Failed,Application Exit!");
				System.exit(1);
			}
		} catch (Exception ex1) {
		}
	}

	public void ShowFlowRecordListCount() {
		System.out.println("The length of up frames queue："
				+ GlobalUPRecordSaveList.size());
		System.out.println("The length of down frames queue："
				+ GlobalDownRecordSaveList.size());
	}

	public void ShowFepInfo() {
		for (int i = 0; i < FepCommList.size(); i++) {
			FepLinkList FepLink = (FepLinkList) FepCommList.get(i);
			try {
				if ((FepLink.GetSocketDownChannel() != null)
						&& (FepLink.GetSocketUpChannel() != null)) {
					System.out.println("Channel Type: " + FepLink.GetFepType());
					System.out.println("Channel Ip  : "
							+ FepLink.GetSocketDownChannel().socket()
									.getInetAddress().getHostAddress());
				}
			} catch (Exception ex) {
			}
		}
	}

	public String GetDebugFlag() { //获取当前的运行标志
		return this.Debug;
	}

	public void ChangeDebugFlag(String DebugFlag) { //修改运行标志
		this.Debug = DebugFlag;
	}

	//记录新加入队列的终端信息
	public void RecordNewTerminalInfo() {
		Trc4Fep fl = new Trc4Fep("NewTerminalInfo");
		for (int i = 0; i < NewTerminalList.size(); i++) {
			TerminalInfo ti = (TerminalInfo) NewTerminalList.get(i);
			try {
				fl
						.TraceLog("=========================================================");
				fl.TraceLog("TerminalAddress is " + ti.TerminalAddress);
				fl.TraceLog("TerminalProtocol is " + ti.TerminalProtocol);
				fl.TraceLog("TerminalCurrent is " + ti.TerminalCurrent);
				fl.TraceLog("TerminalSIMID is " + ti.TerminalSIMID);
				fl.TraceLog("SIMCommunicationIP is " + ti.SIMCommunicationIP);
				fl.TraceLog("SIMLocalAddress is " + ti.SIMLocalAddress);
				fl.TraceLog("TerminalCOMAddress is " + ti.TerminalCOMAddress);
				fl.TraceLog("TerminalUDPAddress is " + ti.TerminalUDPAddress);
				fl.TraceLog("UDPCommunicationIp is " + ti.UDPCommunicationIp);
				fl.TraceLog("UDPLocalAddress is " + ti.UDPLocalAddress);
				fl.TraceLog("TerminalTCPAddress is " + ti.TerminalTCPAddress);
				fl.TraceLog("TCPCommunicationIp is " + ti.TCPCommunicationIp);
				fl.TraceLog("TCPLocalAddress is " + ti.TCPLocalAddress);
				fl.TraceLog("OnLineStatus is " + ti.OnLineStatus);
				fl
						.TraceLog("=========================================================");
			} catch (Exception ex) {
			}
		}
	}

	//将变更的终端物理地址信息写回数据库
	public void WriteTerminalInfotoDataBase() {
		for (Enumeration e = CommunicationServerConstants.TerminalLocalList
				.elements(); e.hasMoreElements();) {
			TerminalInfo ti = (TerminalInfo) e.nextElement();
			String sSQL = "";
			if (ti.CommuniParamChanged) {
				if (ti.TerminalCurrent == 10) {
					sSQL = "UPDATE RUN_ZDDZXX SET ZDWLDZ='"
							+ ti.TerminalCOMAddress + "',TDIP='"
							+ ti.COMCommunicationIP
							+ "',ZHTBSJ=sysdate WHERE ZDLJDZ='"
							+ ti.TerminalAddress + "' and ZDTXFS=10";
					try {
						if (da.executeUpdate(sSQL) == 0) { //update 不成功，说明数据库没有初始记录，需要插入先
							sSQL = "INSERT INTO RUN_ZDDZXX(ZDLJDZ,ZDTXFS,ZDWLDZ,TDIP,ZHTBSJ,TDDZ) VALUES('"
									+ ti.TerminalAddress
									+ "',10,'1','"
									+ ti.COMCommunicationIP + "',sysdate,'1')";
							da.executeUpdate(sSQL);
						}
					} catch (Exception ex) {
					}
				} else if (ti.TerminalCurrent == 20) {
					sSQL = "UPDATE RUN_ZDDZXX SET ZDWLDZ='"
							+ ti.TerminalCOMAddress + "',TDIP='"
							+ ti.RadioCommunicationIP
							+ "',ZHTBSJ=sysdate WHERE ZDLJDZ='"
							+ ti.TerminalAddress + "' and ZDTXFS=20";
					try {
						if (da.executeUpdate(sSQL) == 0) { //update 不成功，说明数据库没有初始记录，需要插入先
							sSQL = "INSERT INTO RUN_ZDDZXX(ZDLJDZ,ZDTXFS,ZDWLDZ,TDIP,ZHTBSJ,TDDZ) VALUES('"
									+ ti.TerminalAddress
									+ "',20,'1','"
									+ ti.RadioCommunicationIP
									+ "',sysdate,'1')";
							da.executeUpdate(sSQL);
						}
					} catch (Exception ex) {
					}
				} else if (ti.TerminalCurrent == 40) {
					sSQL = "UPDATE RUN_ZDDZXX SET ZDWLDZ='"
							+ ti.TerminalUDPAddress + "',TDIP='"
							+ ti.UDPCommunicationIp + "',TDDZ='"
							+ ti.UDPLocalAddress
							+ "',ZHTBSJ=sysdate WHERE ZDLJDZ='"
							+ ti.TerminalAddress + "' and ZDTXFS=40";
					try {
						if (da.executeUpdate(sSQL) == 0) { //update 不成功，说明数据库没有初始记录，需要插入先
							sSQL = "INSERT INTO RUN_ZDDZXX(ZDLJDZ,ZDTXFS,ZDWLDZ,TDIP,ZHTBSJ,TDDZ) VALUES('"
									+ ti.TerminalAddress
									+ "',40,'"
									+ ti.TerminalUDPAddress
									+ "','"
									+ ti.UDPCommunicationIp
									+ "',sysdate,'"
									+ ti.UDPLocalAddress + "')";
							da.executeUpdate(sSQL);
						}
					} catch (Exception ex) {
					}
				} else if ((ti.TerminalCurrent == 50)
						|| (ti.TerminalCurrent == 51)
						|| (ti.TerminalCurrent == 52)) {
					sSQL = "UPDATE RUN_ZDDZXX SET ZDWLDZ='"
							+ ti.TerminalTCPAddress + "',TDIP='"
							+ ti.TCPCommunicationIp + "',TDDZ='"
							+ ti.TCPLocalAddress
							+ "',ZHTBSJ=sysdate WHERE ZDLJDZ='"
							+ ti.TerminalAddress + "' and ZDTXFS="
							+ ti.TerminalCurrent;
					try {
						if (da.executeUpdate(sSQL) == 0) { //update 不成功，说明数据库没有初始记录，需要插入先
							sSQL = "INSERT INTO RUN_ZDDZXX(ZDLJDZ,ZDTXFS,ZDWLDZ,TDIP,ZHTBSJ,TDDZ) VALUES('"
									+ ti.TerminalAddress
									+ "',"
									+ ti.TerminalCurrent
									+ ",'"
									+ ti.TerminalTCPAddress
									+ "','"
									+ ti.TCPCommunicationIp
									+ "',sysdate,'"
									+ ti.TCPLocalAddress + "')";
							da.executeUpdate(sSQL);
						}
					} catch (Exception ex) {
					}
				}
				if (ti.TerminalCurrent != 0) {
					ti.CommuniParamChanged = false;
					CommunicationServerConstants.TerminalLocalList
							.remove(ti.TerminalAddress);
					CommunicationServerConstants.TerminalLocalList.put(
							ti.TerminalAddress, ti);
				}
			}
		}
	}

	public void ManualUpdateSwitchMPInfo(int Updatetype, String TerminalAddr, int CLDXH) {
		try {
			CommunicationServerConstants.Trc1
					.TraceLog("UpdateSwitchMPInfo Start");
			SwitchMPInfo mpi = new SwitchMPInfo();
			ResultSet rset;
			if (Updatetype == 10) { //all
				rset = da
						.executeQuery("SELECT B.CLDXH,B.CLDDZ,B.CLDBH,A.ZDLJDZ AS MAddrESS,C.ZDLJDZ AS BADDRESS FROM DA_ZDGZ A,DA_CLDXX B,DA_ZDGZ C"
								+ " WHERE B.BYBZ=1 AND A.ZDJH=B.ZDJH AND B.BYZDJH=C.ZDJH AND A.GYH=C.GYH");
			} else {//specified MP
				rset = da
						.executeQuery("SELECT B.CLDXH,B.CLDDZ,B.CLDBH,A.ZDLJDZ AS MAddrESS,C.ZDLJDZ AS BADDRESS FROM DA_ZDGZ A,DA_CLDXX B,DA_ZDGZ C"
								+ " WHERE B.BYBZ=1 AND A.ZDJH=B.ZDJH AND B.BYZDJH=C.ZDJH AND  B.CLDXH='"
								+ CLDXH
								+ "' AND C.ZDLJDZ='"
								+ TerminalAddr
								+ "'");
			}
			if (!rset.next()) {//have no this record in db,then remove from list
				CommunicationServerConstants.MPInfoList.remove("" + CLDXH + "#"
						+ TerminalAddr);
				return;
			}
			while (!rset.isAfterLast()) {
				try {
					mpi = (SwitchMPInfo) CommunicationServerConstants.MPInfoList
							.get("" + CLDXH + "#"+ TerminalAddr);
					if (mpi != null) {
						mpi.CLDXH = rset.getInt("CLDXH");
						mpi.CLDDZ = (rset.getString("CLDDZ") != null ? rset
								.getString("CLDDZ") : "");
						mpi.CLDBH = (rset.getString("CLDBH") != null ? rset
								.getString("CLDBH") : "");
						mpi.MAddress = (rset.getString("MAddress") != null ? rset
								.getString("MAddress") : "");
						mpi.BAddress = (rset.getString("BAddress") != null ? rset
								.getString("BAddress") : "");
						CommunicationServerConstants.MPInfoList.remove("" + CLDXH
								+ "#" + TerminalAddr);
						CommunicationServerConstants.MPInfoList.put("" + mpi.CLDXH
								+ "#" + mpi.BAddress, mpi);
					}
					rset.next();
					
				} catch (Exception ex) {
					CommunicationServerConstants.Log1
							.WriteLog("InitSwitchMeasurePointList Error,Terminal is "
									+ rset.getString("ZDLJDZ"));
				}
			}
			rset.close();
			CommunicationServerConstants.Trc1
					.TraceLog("UpdateSwitchMPInfo Finished.");
			rset.close();
		} catch (Exception e) {
			CommunicationServerConstants.Log1
					.WriteLog("CommunicationScheduler:ManualUpdateSwitchMPInfo error,error is "
							+ e.toString());
		} finally {
			da.close();
		}
	}

	// 手工刷新终端信息，避免更新终端SIM卡信息后发送短信的错误,Updatetype:10,更新数据库所有终端；20更新指定的单个终端
	public void ManualUpdateTerminalInfo(int Updatetype, String TerminalAddr) {
		try {
			CommunicationServerConstants.Trc1
					.TraceLog("UpdateTerminalInfo Start");
			String sOldZDLJDZ = "";
			TerminalInfo ti = new TerminalInfo();
			ResultSet rset;
			if (Updatetype == 10) { //更新数据库所有终端
				rset = da
						.executeQuery("SELECT A.ZDLJDZ as ZDLJDZ,A.GYH as GYH,NVL(A.GJMM,'8888') AS ZDMM,NVL(A.JMSF,0) as JMSF,NVL(A.ZDSX,'01') as ZDSX,A.SIM as SIMID,NVL(A.DQTXFS,10) as DQTXFS,NVL(A.Yxzt,'3')as YXZT,NVL(B.ZDTXFS,0) as TXFS,B.ZDWLDZ as WLDZ,B.TDIP as TDIP,B.TDDZ as TDDZ FROM DA_ZDGZ A,RUN_ZDDZXX B WHERE A.ZDLJDZ=B.ZDLJDZ AND rownum<=50000 ");
			} else { //更新指定的单个终端
				rset = da
						.executeQuery("SELECT A.ZDLJDZ as ZDLJDZ,A.GYH as GYH,NVL(A.GJMM,'8888') AS ZDMM,NVL(A.JMSF,0) as JMSF,NVL(A.ZDSX,'01') as ZDSX,A.SIM as SIMID,NVL(A.DQTXFS,10) as DQTXFS,NVL(A.Yxzt,'3')as YXZT,NVL(B.ZDTXFS,0) as TXFS,B.ZDWLDZ as WLDZ,B.TDIP as TDIP,B.TDDZ as TDDZ FROM DA_ZDGZ A,RUN_ZDDZXX B WHERE A.ZDLJDZ=B.ZDLJDZ AND A.ZDLJDZ='"
								+ TerminalAddr + "'");
			}
			while (rset.next()) {
				sOldZDLJDZ = (rset.getString("ZDLJDZ") != null ? rset
						.getString("ZDLJDZ") : "");
				if (sOldZDLJDZ.length() == 0) {
					continue;
				}
				String SIM = (rset.getString("SIMID") != null ? rset
						.getString("SIMID") : sOldZDLJDZ);
				int ZDSX = 0;
				try {
					ZDSX = Integer
							.parseInt((rset.getString("ZDSX") != null ? rset
									.getString("ZDSX") : "01"));
				} catch (Exception ex1) {
				}
				String ZDMM = (rset.getString("ZDMM") != null ? rset
						.getString("ZDMM") : "8888");
				int JMSF = rset.getInt("JMSF");
				int GYH = rset.getInt("GYH");
				// utils.PrintDebugMessage("sOldZDLJDZ="+sOldZDLJDZ+" GYH="+GYH, "D");
				ti = (TerminalInfo) CommunicationServerConstants.TerminalLocalList
						.get(sOldZDLJDZ);
				if (ti != null) {
					try {
						if ((ti.TerminalSIMID.length() == 0 || !ti.TerminalSIMID
								.equals(SIM))
								|| ti.TerminalProperty != ZDSX
								|| (ti.TerminalPassWord.length() == 0 || !ti.TerminalPassWord
										.equals(ZDMM))
								|| ti.TerminalCodingNo != JMSF
								|| ti.TerminalProtocol != GYH) {
							ti.TerminalSIMID = SIM;
							ti.TerminalPassWord = ZDMM; //终端密码
							ti.TerminalCodingNo = JMSF; //终端密码算法
							ti.TerminalProtocol = GYH;
							try {
								ti.TerminalProperty = ZDSX;
							} catch (Exception ex2) {
							}
							ti.TerminalCodingNo = JMSF;
							CommunicationServerConstants.TerminalLocalList
									.remove(ti.TerminalAddress);
							CommunicationServerConstants.TerminalLocalList.put(
									ti.TerminalAddress, ti);
						}
						//2006-12-8 调整刷新通道IP的处理为针对所有的通讯方式，并且该方式只是在单个终端处理时才有效
						if (Updatetype == 20) {
							int i = rset.getInt("TXFS");
							switch (i) { //根据终端当前通讯方式写入物理地址
							case 10: {
								ti.COMCommunicationIP = (rset.getString("TDIP") != null ? rset
										.getString("TDIP")
										: "");
								break;
							}
							case 20: {
								ti.RadioCommunicationIP = (rset
										.getString("TDIP") != null ? rset
										.getString("TDIP") : "");
								break;
							}
							case 31: //cmpp
							case 32: //sgip
							case 30: {
								ti.SIMCommunicationIP = (rset.getString("TDIP") != null ? rset
										.getString("TDIP")
										: "");
								break;
							}
							case 40: {
								ti.UDPCommunicationIp = (rset.getString("TDIP") != null ? rset
										.getString("TDIP")
										: "");
								break;
							}
							case 50:
							case 51:
							case 52: {
								ti.TCPCommunicationIp = (rset.getString("TDIP") != null ? rset
										.getString("TDIP")
										: "");
								break;
							}
							default: {
								break;
							}
							}
							CommunicationServerConstants.TerminalLocalList
									.remove(ti.TerminalAddress);
							CommunicationServerConstants.TerminalLocalList.put(
									ti.TerminalAddress, ti);

						}
					} catch (Exception ex) {
						CommunicationServerConstants.Log1
								.WriteLog("UpdateTerminal " + sOldZDLJDZ
										+ " Error,Error is " + ex.toString());
					}
				} else {
					//	utils.PrintDebugMessage("add ZDLJDZ="+rset.getString("ZDLJDZ")+" GYH="+rset.getInt("GYH"), "D");
					ti = new TerminalInfo();
					ti.TerminalAddress = (rset.getString("ZDLJDZ") != null ? rset
							.getString("ZDLJDZ")
							: ""); //终端逻辑地址
					ti.TerminalSIMID = (rset.getString("SIMID") != null ? rset
							.getString("SIMID") : ti.TerminalAddress); //终端SIM卡号码
					ti.TerminalPassWord = (rset.getString("ZDMM") != null ? rset
							.getString("ZDMM")
							: "8888"); //终端密码
					ti.TerminalCodingNo = rset.getInt("JMSF"); //终端密码算法
					ti.TerminalProtocol = rset.getInt("GYH");
					ti.TerminalProperty = Integer.parseInt((rset
							.getString("ZDSX") != null ? rset.getString("ZDSX")
							: "01"));
					ti.CommandIndex = 0;
					if (ti.TerminalProperty == 1 || ti.TerminalProperty == 4) { //CDMA终端
						ti.HeartInterval = CommunicationServerConstants.TERMINAL_HEARTINTERVAL_CDMA;
					} else {
						ti.HeartInterval = CommunicationServerConstants.TERMINAL_HEARTINTERVAL_GPRS;
					}
					ti.LastLinkTime = Calendar.getInstance();
					ti.TerminalCurrent = rset.getInt("DQTXFS");

					int iYXZT = 20; //默认不在线
					String sYXZT = rset.getString("YXZT");
					if (sYXZT.equals("1")) {
						iYXZT = 10;
					}
					ti.OnLineStatus = iYXZT;//20; //档案初始化默认终端不在线
					int i = rset.getInt("DQTXFS");
					switch (i) { //根据终端当前通讯方式写入物理地址
					case 10: {
						ti.COMCommunicationIP = (rset.getString("TDIP") != null ? rset
								.getString("TDIP")
								: "");
						ti.TerminalCOMAddress = (rset.getString("WLDZ") != null ? rset
								.getString("WLDZ")
								: "1");
						ti.COMLocalAddress = (rset.getString("TDDZ") != null ? rset
								.getString("TDDZ")
								: "1");
						break;
					}
					case 20: {
						ti.RadioCommunicationIP = (rset.getString("TDIP") != null ? rset
								.getString("TDIP")
								: "");
						break;
					}
					case 31: //cmpp
					case 32: //sgip
					case 30: {
						ti.SIMCommunicationIP = (rset.getString("TDIP") != null ? rset
								.getString("TDIP")
								: "");
						ti.SIMLocalAddress = (rset.getString("TDDZ") != null ? rset
								.getString("TDDZ")
								: "");
						break;
					}
					case 40: {
						ti.UDPCommunicationIp = (rset.getString("TDIP") != null ? rset
								.getString("TDIP")
								: "");
						ti.TerminalUDPAddress = (rset.getString("WLDZ") != null ? rset
								.getString("WLDZ")
								: ""); //UDP通讯的物理地址
						ti.UDPLocalAddress = (rset.getString("TDDZ") != null ? rset
								.getString("TDDZ")
								: "");
						break;
					}
					case 50:
					case 51:
					case 52: {
						ti.TCPCommunicationIp = (rset.getString("TDIP") != null ? rset
								.getString("TDIP")
								: "");
						ti.TerminalTCPAddress = (rset.getString("WLDZ") != null ? rset
								.getString("WLDZ")
								: ""); //TCP通讯的物理地址
						ti.TCPLocalAddress = (rset.getString("TDDZ") != null ? rset
								.getString("TDDZ")
								: "");
						break;
					}
					}
					CommunicationServerConstants.TerminalLocalList.put(
							ti.TerminalAddress, ti);
				}
			}
			CommunicationServerConstants.Trc1
					.TraceLog("UpdateTerminalInfo Finished.");
			rset.close();
		} catch (Exception e) {
			CommunicationServerConstants.Log1
					.WriteLog("CommunicationScheduler:ManualUpdateTerminalInfo error,error is "
							+ e.toString());
		} finally {
			da.close();
		}
	}

	//将长期没有运行的网络终端睡眠掉
	public void SleepDownOnLineTerminal() {
		for (Enumeration e = CommunicationServerConstants.TerminalLocalList
				.elements(); e.hasMoreElements();) {
			TerminalInfo ti = (TerminalInfo) e.nextElement();
			if (((ti.TerminalCurrent == 40) || (ti.TerminalCurrent == 50))
					&& (ti.OnLineStatus == 20)
					&& (ti.TerminalAddress.length() >= 8)) { //睡眠认为掉线的CDMA
				//判断的条件为：UDP或者TCP在线的终端＋超过多少时间没有通讯，下发睡眠命令后修改终端在线状态和在线状态标志，保证尽快更新
				//其中判断需要睡眠的时间就通过配置参数中的心跳周期判断，如果在心跳周期内没有上送过就需要睡眠
				try {
					int iTxfs = 0;
					String sZDLJDZ = ti.TerminalAddress;
					String sXXBW = "683D003D00684A" + sZDLJDZ.substring(2, 4)
							+ sZDLJDZ.substring(0, 2) + sZDLJDZ.substring(6, 8)
							+ sZDLJDZ.substring(4, 6) + "1004700000080A";
					if (ti.TerminalProperty == 1 || ti.TerminalProperty == 4) { //CDMA终端、模块表
						sXXBW = sXXBW + "04"; //切换到CDMA睡眠方式
						iTxfs = 50;
					} else if (ti.TerminalProperty == 2
							|| ti.TerminalProperty == 5) { //GPRS终端、模块表
						sXXBW = sXXBW + "03"; //切换到GPRS睡眠方式
						iTxfs = 31;
						continue; //对于GPRS的终端暂时不做睡眠的处理
					} else {
						continue;
					}
					sXXBW = sXXBW + ti.TerminalPassWord.substring(2, 4)
							+ ti.TerminalPassWord.substring(0, 2) + "0016"; //睡眠帧组成完成，进行校验后就加入发送队列即可
					sXXBW = GetFrameInfo.gGetParityByteOfQuanGuo(sXXBW);
					if (sXXBW.length() > 0) {
						StructSendData sd = new StructSendData();
						sd.TerminalAddress = ti.TerminalAddress;
						sd.TerminalCommunication = iTxfs;
						sd.SendDirect = 0;
						sd.CommandSeq = 0;
						sd.ArithmeticNo = ti.TerminalCodingNo;
						sd.MessageContent = sXXBW.trim();
						sd.MessageLength = sXXBW.length();
						CommunicationServerConstants.GlobalSendList.add(sd);
						//队列长度超过5000条，就删除头上的记录，控制队列长度
						if (CommunicationServerConstants.GlobalSendList.size() > CommunicationServerConstants.JMS_MAXCOUNT) {
							CommunicationServerConstants.GlobalSendList
									.remove(0);
						}
					}
				} catch (Exception ex) {
				}
			}
		}
	}

	//记录当前队列中的终端信息
	public void DisplayTerminalInfo() {
		Trc4Fep fl = new Trc4Fep("TerminalInfo");
		for (Enumeration e = CommunicationServerConstants.TerminalLocalList
				.elements(); e.hasMoreElements();) {
			TerminalInfo ti = (TerminalInfo) e.nextElement();
			try {
				fl
						.TraceLog("=========================================================");
				fl.TraceLog("TerminalAddress is " + ti.TerminalAddress);
				fl.TraceLog("TerminalProcotol is " + ti.TerminalProtocol);
				fl.TraceLog("TerminalCurrent is " + ti.TerminalCurrent);
				fl.TraceLog("TerminalSIMID is " + ti.TerminalSIMID);
				fl.TraceLog("TerminalProperty is " + ti.TerminalProperty);
				if (ti.TerminalCurrent == 20) {
					fl.TraceLog("230MCommunicationIP is "
							+ ti.RadioCommunicationIP);
				}
				if (ti.TerminalCurrent == 30 || ti.TerminalCurrent == 31
						|| ti.TerminalCurrent == 32) {
					fl.TraceLog("SIMCommunicationIP is "
							+ ti.SIMCommunicationIP);
					fl.TraceLog("SIMLocalAddress is " + ti.SIMLocalAddress);
				}
				if (ti.TerminalCurrent == 10) {
					fl.TraceLog("TerminalCOMAddress is "
							+ ti.TerminalCOMAddress);
					fl.TraceLog("TerminalCOMIp is " + ti.COMCommunicationIP);
				}
				if (ti.TerminalCurrent == 40) {
					fl.TraceLog("TerminalUDPAddress is "
							+ ti.TerminalUDPAddress);
					fl.TraceLog("UDPCommunicationIp is "
							+ ti.UDPCommunicationIp);
					fl.TraceLog("UDPLocalAddress is " + ti.UDPLocalAddress);
				}
				if ((ti.TerminalCurrent == 50) || (ti.TerminalCurrent == 51)
						|| (ti.TerminalCurrent == 52)) {
					fl.TraceLog("TerminalTCPAddress is "
							+ ti.TerminalTCPAddress);
					fl.TraceLog("TCPCommunicationIp is "
							+ ti.TCPCommunicationIp);
					fl.TraceLog("TCPLocalAddress is " + ti.TCPLocalAddress);
				}
				if (ti.TerminalCurrent == 40 || ti.TerminalCurrent == 50) {
					fl.TraceLog("LastDataTime is "
							+ formatter.format(ti.LastLinkTime.getTime()));
					fl.TraceLog("OnLineStatus is " + ti.OnLineStatus);
				}
				fl
						.TraceLog("=========================================================");
			} catch (Exception ex) {
			}
		}
	}

	//终端在线情况的检查、更新
	public void OnLineStatusCheck() {
		for (Enumeration e = CommunicationServerConstants.TerminalLocalList
				.elements(); e.hasMoreElements();) {
			try {
				TerminalInfo ti = (TerminalInfo) e.nextElement();
				if ((ti.TerminalCurrent == 40) || (ti.TerminalCurrent == 50)) {
					if (ti.OnLineStatus == 10) { //判断在心跳间隔内是否有通讯记录，否则认为掉线
						Calendar c = (Calendar) ti.LastLinkTime.clone();
						c.add(Calendar.SECOND, ti.HeartInterval);
						if (c.before(Calendar.getInstance())) {
							utils.PrintDebugMessage("Terminal "
									+ ti.TerminalAddress + " is OffLine Now",
									Debug);
							ti.OnLineStatus = 20;
							ti.OnLineChanged = true;
						}
					}
					if (ti.OnLineChanged) { //终端在线状态改变，需要同步数据库信息
						String sSQL = "";
						if (ti.OnLineStatus == 20) {
							sSQL = "UPDATE DA_ZDGZ SET YXZT=3 WHERE ZDLJDZ='"
									+ ti.TerminalAddress + "'";
						} else {
							sSQL = "UPDATE DA_ZDGZ SET YXZT=1 WHERE ZDLJDZ='"
									+ ti.TerminalAddress + "'";
						}
						try {
							da.executeUpdate(sSQL);
						} catch (Exception ex) {
							CommunicationServerConstants.Log1
									.WriteLog("Update TerminalOnLineStatue Fail,Error is "
											+ ex.toString());
						}
						String sZDLJDZ = ti.TerminalAddress;
						ti.OnLineChanged = false;
						CommunicationServerConstants.TerminalLocalList
								.remove(sZDLJDZ);
						CommunicationServerConstants.TerminalLocalList.put(
								sZDLJDZ, ti);
					}
				}
			} catch (Exception ex) {
				CommunicationServerConstants.Log1
						.WriteLog("OnLineStatusCheck error, message is "
								+ e.toString());
			}
		}
	}

	private void InitSwitchMeasurePointList() {
		try {
			SwitchMPInfo mpi = null;
			ResultSet rset = da
					.executeQuery("SELECT B.CLDXH,B.CLDDZ,B.CLDBH,A.ZDLJDZ AS MAddrESS,C.ZDLJDZ AS BADDRESS FROM DA_ZDGZ A,DA_CLDXX B,DA_ZDGZ C WHERE B.BYBZ=1 AND A.ZDJH=B.ZDJH AND B.BYZDJH=C.ZDJH AND A.GYH=C.GYH");
			if (!rset.next()) {
				return;
			}
			while (!rset.isAfterLast()) {
				try {
					mpi = new SwitchMPInfo();
					mpi.CLDXH = rset.getInt("CLDXH");
					mpi.CLDBH = (rset.getString("CLDBH") != null ? rset
							.getString("CLDBH") : "");
					mpi.CLDDZ = (rset.getString("CLDDZ") != null ? rset
							.getString("CLDDZ") : "");
					mpi.MAddress = (rset.getString("MAddrESS") != null ? rset
							.getString("MAddrESS") : "");
					mpi.BAddress = (rset.getString("BAddress") != null ? rset
							.getString("BAddress") : "");
					String skey = ""+mpi.CLDXH + "#" + mpi.BAddress;
					CommunicationServerConstants.MPInfoList.put(skey, mpi);
					rset.next();
				} catch (Exception ex) {
					CommunicationServerConstants.Log1
							.WriteLog("InitSwitchMeasurePointList Error,Terminal is "
									+ rset.getString("ZDLJDZ"));
				}
			}
			rset.close();
		} catch (Exception e) {
			CommunicationServerConstants.Log1
					.WriteLog("CommunicationScheduler:InitSwitchMeasurePointList Port error,error is "
							+ e.toString());
		} finally {
			da.close();
		}
	}

	private void InitTerminalLocalList() {
		try {
			String sOldZDLJDZ = "";
			TerminalInfo ti = new TerminalInfo();
			//ResultSet rset = da.executeQuery("SELECT A.ZDLJDZ as ZDLJDZ,A.GYH as GYH,NVL(A.GJMM,'8888') AS ZDMM,NVL(A.JMSF,0) as JMSF,NVL(A.ZDSX,'01') as ZDSX,A.SIM as SIMID,NVL(A.DQTXFS,10) as DQTXFS,NVL(B.ZDTXFS,0) as TXFS,B.ZDWLDZ as WLDZ,B.TDIP as TDIP,B.TDDZ as TDDZ FROM DA_ZDGZ A,RUN_ZDDZXX B WHERE A.ZDLJDZ=B.ZDLJDZ AND LENGTH(A.ZDLJDZ)>=8 AND rownum<=50000");
			ResultSet rset = da
					.executeQuery("SELECT A.ZDLJDZ as ZDLJDZ,A.GYH as GYH,NVL(A.GJMM,'8888') AS ZDMM,NVL(A.JMSF,0) as JMSF,NVL(A.ZDSX,'01') as ZDSX,A.SIM as SIMID,NVL(A.DQTXFS,10) as DQTXFS,NVL(A.Yxzt,'3')as YXZT,NVL(B.ZDTXFS,0) as TXFS,B.ZDWLDZ as WLDZ,B.TDIP as TDIP,B.TDDZ as TDDZ FROM DA_ZDGZ A,RUN_ZDDZXX B WHERE A.ZDLJDZ=B.ZDLJDZ AND rownum<=50000");
			if (!rset.next()) {
				return;
			}
			while (!rset.isAfterLast()) {
				try {
					if (rset.isFirst()) {
						sOldZDLJDZ = (rset.getString("ZDLJDZ") != null ? rset
								.getString("ZDLJDZ") : "");
						if (sOldZDLJDZ.length() == 0) {
							continue;
						}
						ti.TerminalAddress = sOldZDLJDZ; //终端逻辑地址
						ti.TerminalProtocol = rset.getInt("GYH");
						ti.TerminalPassWord = (rset.getString("ZDMM") != null ? rset
								.getString("ZDMM")
								: "8888"); //终端密码
						ti.TerminalCodingNo = rset.getInt("JMSF"); //终端密码算法
						ti.TerminalProperty = Integer.parseInt((rset
								.getString("ZDSX") != null ? rset
								.getString("ZDSX") : "01")); //终端属性，01 CDMA终端 02GPRS 03230M
						ti.TerminalSIMID = (rset.getString("SIMID") != null ? rset
								.getString("SIMID")
								: sOldZDLJDZ); //终端SIM卡号码
						ti.CommandIndex = 0;
						if (ti.TerminalProperty == 1
								|| ti.TerminalProperty == 4) { //CDMA终端
							ti.HeartInterval = CommunicationServerConstants.TERMINAL_HEARTINTERVAL_CDMA;
						} else {
							ti.HeartInterval = CommunicationServerConstants.TERMINAL_HEARTINTERVAL_GPRS;
						}
						ti.LastLinkTime = Calendar.getInstance();
						ti.TerminalCurrent = rset.getInt("DQTXFS");
						ti.OnLineChanged = false;
					}

					int iYXZT = 20; //默认不在线
					String sYXZT = rset.getString("YXZT");
					if (sYXZT.equals("1")) {
						iYXZT = 10;
					}

					if (sOldZDLJDZ
							.equals((rset.getString("ZDLJDZ") != null ? rset
									.getString("ZDLJDZ") : ""))) {
						int i = rset.getInt("TXFS");
						switch (i) { //根据终端当前通讯方式写入物理地址
						case 10: {
							ti.COMCommunicationIP = (rset.getString("TDIP") != null ? rset
									.getString("TDIP")
									: "");
							ti.TerminalCOMAddress = (rset.getString("WLDZ") != null ? rset
									.getString("WLDZ")
									: "1");
							ti.COMLocalAddress = (rset.getString("TDDZ") != null ? rset
									.getString("TDDZ")
									: "1");
							break;
						}
						case 20: {
							ti.RadioCommunicationIP = (rset.getString("TDIP") != null ? rset
									.getString("TDIP")
									: "");
							break;
						}
						case 31: //cmpp
						case 32: //sgip
						case 30: {
							ti.SIMCommunicationIP = (rset.getString("TDIP") != null ? rset
									.getString("TDIP")
									: "");
							ti.SIMLocalAddress = (rset.getString("TDDZ") != null ? rset
									.getString("TDDZ")
									: "");
							break;
						}
						case 40: {
							ti.UDPCommunicationIp = (rset.getString("TDIP") != null ? rset
									.getString("TDIP")
									: "");
							ti.TerminalUDPAddress = (rset.getString("WLDZ") != null ? rset
									.getString("TDDZ")
									: ""); //UDP通讯的物理地址
							ti.UDPLocalAddress = (rset.getString("TDDZ") != null ? rset
									.getString("TDDZ")
									: "");
							ti.OnLineStatus = iYXZT; //20; //档案初始化默认终端不在线
							break;
						}
						case 50:
						case 51:
						case 52: {
							ti.TCPCommunicationIp = (rset.getString("TDIP") != null ? rset
									.getString("TDIP")
									: "");
							ti.TerminalTCPAddress = (rset.getString("WLDZ") != null ? rset
									.getString("TDDZ")
									: ""); //TCP通讯的物理地址
							ti.TCPLocalAddress = (rset.getString("TDDZ") != null ? rset
									.getString("TDDZ")
									: "");
							ti.OnLineStatus = iYXZT; //20;
							break;
						}
						default: {
							break;
						}
						}
						if (rset.isLast()) {
							CommunicationServerConstants.TerminalLocalList.put(
									ti.TerminalAddress, ti);
						}
						rset.next();
					} else {
						CommunicationServerConstants.TerminalLocalList.put(
								ti.TerminalAddress, ti);
						ti = new TerminalInfo();
						sOldZDLJDZ = (rset.getString("ZDLJDZ") != null ? rset
								.getString("ZDLJDZ") : "");
						if (sOldZDLJDZ.length() == 0) {
							continue;
						}
						ti.TerminalAddress = sOldZDLJDZ; //终端逻辑地址
						ti.TerminalProtocol = rset.getInt("GYH");
						ti.TerminalSIMID = (rset.getString("SIMID") != null ? rset
								.getString("SIMID")
								: sOldZDLJDZ); //终端SIM卡号码
						ti.CommandIndex = 0;
						ti.TerminalProperty = Integer.parseInt((rset
								.getString("ZDSX") != null ? rset
								.getString("ZDSX") : "01")); //终端属性，决定是GPRS、CDMA终端
						if (ti.TerminalProperty == 1
								|| ti.TerminalProperty == 4) { //CDMA终端
							ti.HeartInterval = CommunicationServerConstants.TERMINAL_HEARTINTERVAL_CDMA;
						} else {
							ti.HeartInterval = CommunicationServerConstants.TERMINAL_HEARTINTERVAL_GPRS;
						}
						ti.TerminalPassWord = (rset.getString("ZDMM") != null ? rset
								.getString("ZDMM")
								: "8888"); //终端密码
						ti.TerminalCodingNo = rset.getInt("JMSF"); //终端密码算法

						ti.LastLinkTime = Calendar.getInstance();
						ti.TerminalCurrent = rset.getInt("DQTXFS");
						ti.OnLineStatus = iYXZT; //20; //档案初始化默认终端不在线
						ti.OnLineChanged = false;
					}
				} catch (Exception ex) {
					CommunicationServerConstants.Log1
							.WriteLog("InitTerminalLocalList Error,Terminal is "
									+ rset.getString("ZDLJDZ"));
				}
			}
			rset.close();
		} catch (Exception e) {
			CommunicationServerConstants.Log1
					.WriteLog("CommunicationScheduler:InitTerminalLocalList Port error,error is "
							+ e.toString());
		} finally {
			da.close();
		}
	}

	//对于连接进行注册
	private void registerChannel(Selector selector, SocketChannel channel,
			int ops) {
		try {
			if (channel == null) {
				return;
			}
			if (FepCommList.size() + PreCommList.size() > MaxCount) { //如果队列中的连接数量超过最大连接数，则主动断开本次的连接
				channel.close();
				return;
			}
			channel.configureBlocking(false); //配置socket的阻塞情况
			channel.register(selector, ops);
		} catch (IOException ex) {
			CommunicationServerConstants.Log1
					.WriteLog("CommunicationScheduler:registerChannel() error,error is "
							+ ex.toString());
		}
	}

	//将需要下发的短信组成命令下发到前置机通道上
	private void BuildShortMessageAndSend(SocketChannel channel,
			int ContentLength, String SendContent, String MsgTarget, int MsgType) {
		ByteBuffer buffer = ByteBuffer.allocateDirect(5000); //用于发送的缓冲
		try {
			byte[] Msg = null;
			CommunicationServerConstants.Trc1
					.TraceLog("35、Begin to Form up Frame");
			try {
				int iLen = 12 + 2 + 50 + ContentLength;
				Msg = new byte[iLen];
				byte[] MsgLength = new byte[4];
				MsgLength = utils.int2byte(iLen);
				Msg[0] = MsgLength[0];
				Msg[1] = MsgLength[1];
				Msg[2] = MsgLength[2];
				Msg[3] = MsgLength[3];
				//消息总长度
				Msg[4] = 0;
				Msg[5] = 0;
				Msg[6] = 0;
				Msg[7] = 3;
				//命令类型
				byte[] MsgSequence = new byte[4];
				MsgSequence = utils.int2byte(Sequence);
				Msg[8] = MsgSequence[0];
				Msg[9] = MsgSequence[1];
				Msg[10] = MsgSequence[2];
				Msg[11] = MsgSequence[3];
				//消息流水号
				byte[] bContentLength = new byte[2];
				byte[] data = new byte[2];
				if (MsgType == 10) {
					//ContentLength = ContentLength / 2;
				}
				if (ContentLength < 15) { //只有小于15的数据需要特殊处理，其他都可以由统一函数实现
					data[1] = 0;
					data[0] = (byte) ContentLength;
				} else {
					data = utils.str2bytes(Integer.toHexString(ContentLength));
				}
				for (int i = 0; i < data.length; i++) {
					bContentLength[i] = data[i];
				}
				if (ContentLength < 255) {
					Msg[12] = bContentLength[1];
					Msg[13] = bContentLength[0];
				} else {
					Msg[12] = bContentLength[0];
					Msg[13] = bContentLength[1];
				}
				//内容总长度
				byte[] bContent = new byte[ContentLength];
				bContent = SendContent.getBytes();
				for (int i = 0; i < bContent.length; i++) {
					Msg[14 + i] = bContent[i];
				}
				MsgTarget = MsgTarget + "#" + MsgType; //如果是短信则需要制定发送消息的类型，即使用PDU格式发送
				int iMsgTarget = MsgTarget.trim().length();
				byte[] bMsgTarget = new byte[iMsgTarget];
				bMsgTarget = MsgTarget.getBytes();
				for (int i = 0; i < iMsgTarget; i++) {
					Msg[14 + i + ContentLength] = bMsgTarget[i];
				}
				CommunicationServerConstants.Trc1
						.TraceLog("36、Frame Build Complete");
			} catch (Exception ex2) {
				CommunicationServerConstants.Log1
						.WriteLog("CommunicationScheduler:GenerateSendMsg() Error,error is "
								+ ex2.toString());
			}

			//消息内容
			try {
				buffer.clear();
				buffer.put(Msg);
				buffer.flip();
			} catch (Exception ex3) {
				CommunicationServerConstants.Log1
						.WriteLog("CommunicationScheduler:PutByteToBuffer() Error,error is "
								+ ex3.toString());
			}
			try {
				channel.write(buffer);
				AckBack = false; //返回标志
				buffer.clear();
				CommunicationServerConstants.Trc1
						.TraceLog("37、Write to FepChannel Success " + channel);
			} catch (IOException ex1) {
				buffer.clear();
				//buffer.allocate(5000);
				buffer = ByteBuffer.allocate(5000);
				CommunicationServerConstants.Log1
						.WriteLog("CommunicationScheduler:Fep Link Error,error is "
								+ ex1.toString());
			} finally {
				Sequence = Sequence + 1;
			}
		} catch (Exception ex) {
			CommunicationServerConstants.Log1
					.WriteLog("CommunicationScheduler:BuildShortMessageAndSend() Error,error is "
							+ ex.toString());
		}
	}

	//将前置机收到的数据组成命令下发到前置机通道上
	private void BuildUpFepReceiveFrameAndSend(SocketChannel channel,
			int ContentLength, String SendContent, String MsgTarget,
			String LocalAddr, int FepType, int YXJ) {
		ByteBuffer buffer = ByteBuffer.allocateDirect(5000); //用于发送的缓冲
		try {
			byte[] Msg = null;
		//	CommunicationServerConstants.Trc1
		//			.TraceLog("35、Begin to Form up Frame");
			try {
				String s = SendContent.trim();
				int iLen = 12 + 2 + 50 + s.trim().length();
				Msg = new byte[iLen];
				byte[] MsgLength = new byte[4];
				MsgLength = utils.int2byte(iLen);
				Msg[0] = MsgLength[0];
				Msg[1] = MsgLength[1];
				Msg[2] = MsgLength[2];
				Msg[3] = MsgLength[3];
				//消息总长度
				Msg[4] = 0;
				Msg[5] = 0;
				Msg[6] = 0;
				Msg[7] = 3;
				//命令类型
				byte[] MsgSequence = new byte[4];
				MsgSequence = utils.int2byte(Sequence);
				Msg[8] = MsgSequence[0];
				Msg[9] = MsgSequence[1];
				Msg[10] = MsgSequence[2];
				Msg[11] = MsgSequence[3];
				//消息流水号
				byte[] bContentLength = new byte[2];
				byte[] data = new byte[2];
				if (ContentLength < 15) { //只有小于15的数据需要特殊处理，其他都可以由统一函数实现
					data[1] = 0;
					data[0] = (byte) ContentLength;
				} else {
					data = utils.str2bytes(Integer.toHexString(ContentLength));
				}
				for (int i = 0; i < data.length; i++) {
					bContentLength[i] = data[i];
				}
				if (ContentLength < 256) {
					Msg[12] = bContentLength[1];
					Msg[13] = bContentLength[0];
				} else {
					Msg[12] = bContentLength[0];
					Msg[13] = bContentLength[1];
				}
				//内容总长度
				byte[] bContent = new byte[ContentLength];
				bContent = SendContent.getBytes();
				for (int i = 0; i < ContentLength; i++) {
					Msg[14 + i] = bContent[i];
				}
				if ((FepType == 30) || (FepType == 31) || (FepType == 32)) {
					MsgTarget = MsgTarget + "#30"; //如果是短信则需要制定发送消息的类型，即使用PDU格式发送
				}
				int iMsgTarget = MsgTarget.trim().length();
				byte[] bMsgTarget = new byte[iMsgTarget];
				bMsgTarget = MsgTarget.getBytes();
				for (int i = 0; i < iMsgTarget; i++) {
					Msg[14 + i + ContentLength] = bMsgTarget[i];
				}
				int iLocalAddr;
				byte[] bLocalAddr;
				if (FepType == 20) { //230M特殊处理需要传优先级给通道
					LocalAddr = "" + YXJ; //发送通道物理地址在230M通讯中用来传优先级
					iLocalAddr = LocalAddr.trim().length();
					bLocalAddr = new byte[iLocalAddr];
					bLocalAddr = LocalAddr.getBytes();
				} else {
					iLocalAddr = LocalAddr.trim().length();
					bLocalAddr = new byte[iLocalAddr];
					bLocalAddr = LocalAddr.getBytes();
				}
				for (int i = 0; i < iLocalAddr; i++) {
					Msg[39 + i + ContentLength] = bLocalAddr[i];
				}
		//		CommunicationServerConstants.Trc1
		//				.TraceLog("36、Frame Build Complete");
			} catch (Exception ex2) {
				CommunicationServerConstants.Log1
						.WriteLog("CommunicationScheduler:GenerateSendMsg() Error,error is "
								+ ex2.toString());
			}
			//消息内容
			try {
				buffer.clear();
				buffer.put(Msg);
				buffer.flip();
			} catch (Exception ex3) {
				CommunicationServerConstants.Log1
						.WriteLog("CommunicationScheduler:PutByteToBuffer() Error,error is "
								+ ex3.toString());
			}
			try {
				AckBack = false; //返回标志
				channel.write(buffer);
		//		CommunicationServerConstants.Trc1
		//				.TraceLog("37、Write to FepChannel Success " + channel);
				buffer.clear();
			} catch (IOException ex1) {
				buffer.clear();
				//buffer.allocate(5000);
				buffer = ByteBuffer.allocate(5000);
				CommunicationServerConstants.Log1
						.WriteLog("CommunicationScheduler:Fep Link Error,error is "
								+ ex1.toString());
				for (int j = 0; j < FepCommList.size(); j++) {
					FepLinkList l = (FepLinkList) FepCommList.get(j);
					if (l.GetSocketDownChannel() == channel) {
						l.ConnectFlag = false;
						FepCommList.set(j, l);
					}
				}
			} finally {
				Sequence = Sequence + 1;
			}
		} catch (Exception ex) {
			CommunicationServerConstants.Log1
					.WriteLog("CommunicationScheduler:BuildUpFepReceiveFrameAndSend() Error,error is "
							+ ex.toString());
		}
	}

	//将前置机收到的数据组成命令下发到前置机通道上
	private void BuildUpPreReceiveFrameAndSend(SocketChannel channel,
			String SendContent, String UserNo, int CommandSeq, int iFepType) {
		ByteBuffer buffer = ByteBuffer.allocateDirect(5000); //用于发送的缓冲
		try {
			byte[] Msg = null;
	//		CommunicationServerConstants.Trc1
	//				.TraceLog("35、Begin to Form up Frame");
			try {
				String s = SendContent.trim();
				int ContentLength = (s.length());

				//消息总长度
				int iLen = 12 + 2 + ContentLength + 20 + 4;
				Msg = new byte[iLen];

				byte[] MsgLength = new byte[4];
				MsgLength = utils.int2byte(iLen);
				Msg[0] = MsgLength[0];
				Msg[1] = MsgLength[1];
				Msg[2] = MsgLength[2];
				Msg[3] = MsgLength[3];

				//命令类型
				int iCommandType = 0;
				if (iFepType == 100 || iFepType == 110) {
					iCommandType = 0x00000103; //调用加密机     	
				} else if (iFepType == 120) {
					iCommandType = 0x00000104; //调用读卡器
				}

				byte[] bCommandType = new byte[4];
				bCommandType = utils.int2byte(iCommandType);
				Msg[4] = bCommandType[0];
				Msg[5] = bCommandType[1];
				Msg[6] = bCommandType[2];
				Msg[7] = bCommandType[3];

				//消息流水号
				byte[] MsgSequence = new byte[4];
				MsgSequence = utils.int2byte(Sequence);
				Msg[8] = MsgSequence[0];
				Msg[9] = MsgSequence[1];
				Msg[10] = MsgSequence[2];
				Msg[11] = MsgSequence[3];

				//内容总长度
				byte[] bContentLength = new byte[2];
				bContentLength = utils.int2byte(ContentLength);
				Msg[12] = bContentLength[2];
				Msg[13] = bContentLength[3];

				//消息内容
				byte[] bContent = new byte[ContentLength];
				bContent = SendContent.getBytes();
				for (int i = 0; i < ContentLength; i++) {
					Msg[14 + i] = bContent[i];
				}

				//用户户号
				String sUserNo = UserNo.trim();
				byte[] bUserNo = new byte[20];
				bUserNo = sUserNo.getBytes();
				for (int i = 0; i < sUserNo.length(); i++) {
					Msg[14 + ContentLength + i] = bUserNo[i];
				}

				//命令序号
				byte[] bCommandSeq = new byte[4];
				bCommandSeq = utils.int2byte(CommandSeq);
				Msg[14 + ContentLength + 20 + 0] = bCommandSeq[0];
				Msg[14 + ContentLength + 20 + 1] = bCommandSeq[1];
				Msg[14 + ContentLength + 20 + 2] = bCommandSeq[2];
				Msg[14 + ContentLength + 20 + 3] = bCommandSeq[3];

		//		CommunicationServerConstants.Trc1
		//				.TraceLog("36、Frame Build Complete");
			} catch (Exception ex2) {
				CommunicationServerConstants.Log1
						.WriteLog("CommunicationScheduler:GenerateSendMsg() Error,error is "
								+ ex2.toString());
			}
			//消息内容
			try {
				buffer.clear();
				buffer.put(Msg);
				buffer.flip();
			} catch (Exception ex3) {
				CommunicationServerConstants.Log1
						.WriteLog("CommunicationScheduler:PutByteToBuffer() Error,error is "
								+ ex3.toString());
			}

			try {
				AckBack = false; //返回标志
				channel.write(buffer);
		//		CommunicationServerConstants.Trc1
		//				.TraceLog("37、Write to FepChannel Success " + channel);
				buffer.clear();
			} catch (IOException ex1) {
				buffer.clear();
				//buffer.allocate(5000);
				buffer = ByteBuffer.allocate(5000);
				CommunicationServerConstants.Log1
						.WriteLog("CommunicationScheduler:Fep Link Error,error is "
								+ ex1.toString());
				for (int j = 0; j < FepCommList.size(); j++) {
					FepLinkList l = (FepLinkList) FepCommList.get(j);
					if (l.GetSocketDownChannel() == channel) {
						l.ConnectFlag = false;
						FepCommList.set(j, l);
					}
				}
			} finally {
				Sequence = Sequence + 1;
			}
		} catch (Exception ex) {
			CommunicationServerConstants.Log1
					.WriteLog("CommunicationScheduler:BuildUpFepReceiveFrameAndSend() Error,error is "
							+ ex.toString());
		}
	}

	private String ReBuildFrameOfEncrypt(String Frame, String Ip,
			String TerminalAddr, int ArithmeticNo) {
		String sResult = "";
		;
		try {
			if (Frame.substring(0, 2).equals("68")) {
				String sGNM = Frame.substring(24, 26); //功能码
				if ((ArithmeticNo == 128 || ArithmeticNo == 129)
						&& (sGNM.equals("04") || sGNM.equals("05") || sGNM
								.equals("10"))) { //设置、控制、中继
					int iSEQ = Integer.parseInt(Frame.substring(26, 28), 16); //帧序列号
					int iTpv = iSEQ & 128; //时间标签有效标志
					String sMM = "";
					int iPos = Ip.indexOf(":", 0);
					Ip = Ip.substring(0, iPos);
					if (iTpv == 128) { //带时间标签
						sMM = Frame.substring(Frame.length() - 20, Frame
								.length() - 16); //密码
						sMM = sMM.substring(2, 4) + sMM.substring(0, 2); //帧中的密码为真实密码的倒置
						sMM = Encode.Encode1(ArithmeticNo, iSEQ, Ip,
								TerminalAddr, sMM);
						Frame = Frame.substring(0, Frame.length() - 20)
								+ sMM
								+ Frame.substring(Frame.length() - 16, Frame
										.length());
					} else {
						sMM = Frame.substring(Frame.length() - 8, Frame
								.length() - 4); //密码
						sMM = sMM.substring(2, 4) + sMM.substring(0, 2); //帧中的密码为真实密码的倒置
						sMM = Encode.Encode1(ArithmeticNo, iSEQ, Ip,
								TerminalAddr, sMM);
						Frame = Frame.substring(0, Frame.length() - 8)
								+ sMM
								+ Frame.substring(Frame.length() - 4, Frame
										.length());
					}
					sResult = GetFrameInfo.gGetParityByteOfQuanGuo(Frame);
				} else {
					sResult = Frame;
				}
			} else {
				sResult = Frame;
			}

		} finally {
		}
		return sResult;
	}

	//发送下行数据，选择对应的前置机通道链路，判断通讯方式
	private void SendDownFlowToFep(StructSendData sd) {
		try {
			if (sd.SendDirect == 1) { //发送普通短信的处理
				FepLinkList l = null;
				for (int j = FepCommList.size() - 1; j >= 0; j--) {
					l = (FepLinkList) FepCommList.get(j); //查找是否有可用的短信、230M通道
					if (l.GetFepType() == sd.TerminalCommunication) {
						//加密---固定为C0,A8,01,D9(对所有通信方式有效)   192.168.1.217
						if (l.GetFepType() == 20) { //230M通道的发送
							//考虑到可能采用多个230M通道，这里修改发送处理模式：首先判断当前终端的230M通道IP是否存在，如果存在就使用该通道下发
							//否则，就对于当前存在的230M通道全部发送一次，这样只要终端能正常通讯，下次通讯的时候就会使用正确的通道下发了
							TerminalInfo ti = null;
							ti = (TerminalInfo) CommunicationServerConstants.TerminalLocalList
									.get(sd.TerminalAddress);
							if (ti != null) {
								if (ti.RadioCommunicationIP.length() != 0) {
									if (l.GetSocketDownChannel().socket()
											.getInetAddress().getHostAddress()
											.equals(ti.RadioCommunicationIP)) {
										sd.MessageContent = ReBuildFrameOfEncrypt(
												sd.MessageContent,
												"192.168.1.217:0000",
												sd.TerminalAddress,
												sd.ArithmeticNo);
										sd.MessageLength = sd.MessageContent
												.length();
										BuildUpFepReceiveFrameAndSend(l
												.GetSocketDownChannel(),
												sd.MessageLength,
												sd.MessageContent, sd.MobileNo,
												"", sd.TerminalCommunication,
												sd.YXJ); //组成命令并下发
										break;
									}
								} else {
									sd.MessageContent = ReBuildFrameOfEncrypt(
											sd.MessageContent,
											"192.168.1.217:0000",
											sd.TerminalAddress, sd.ArithmeticNo);
									sd.MessageLength = sd.MessageContent
											.length();
									BuildUpFepReceiveFrameAndSend(l
											.GetSocketDownChannel(),
											sd.MessageLength,
											sd.MessageContent, sd.MobileNo, "",
											sd.TerminalCommunication, sd.YXJ); //组成命令并下发
									break;
								}
							} else {
								sd.MessageContent = ReBuildFrameOfEncrypt(
										sd.MessageContent,
										"192.168.1.217:0000",
										sd.TerminalAddress, sd.ArithmeticNo);
								sd.MessageLength = sd.MessageContent.length();
								BuildUpFepReceiveFrameAndSend(l
										.GetSocketDownChannel(),
										sd.MessageLength, sd.MessageContent,
										sd.MobileNo, "",
										sd.TerminalCommunication, sd.YXJ); //组成命令并下发
								break;
							}
						} else { //短信的下发
							BuildShortMessageAndSend(l.GetSocketDownChannel(),
									sd.MessageLength, sd.MessageContent,
									sd.MobileNo, sd.MessageType);
							break;
						}
					}
				}
				if (l == null) {
			//		CommunicationServerConstants.Trc1
			//				.TraceLog("34、No Correspond FepLink To Send!");
					AckBack = true;
				} else {
					SaveDownFlowRecordToDB(sd.MobileNo,
							sd.TerminalCommunication, sd.MessageContent,
							sd.TerminalAddress, l.GetSocketDownChannel());
				}
			} else if (sd.SendDirect == 10 || sd.SendDirect == 20
					|| sd.SendDirect == 30) //调用加密机或读卡器
			{
				int iFepType = 0;
				if (sd.SendDirect == 10)
					iFepType = 110;//国网加密机
				else if (sd.SendDirect == 20)
					iFepType = 120; //国网读卡器   
				else if (sd.SendDirect == 30)
					iFepType = 100; //海兴加密机   
				PreLinkList l = null;
				for (int j = PreCommList.size() - 1; j >= 0; j--) {
					l = (PreLinkList) PreCommList.get(j); //根据终端通讯方式查找对应的通道
				//	CommunicationServerConstants.Trc1
				//			.TraceLog("31、Pre Info LinkFepType"
				//					+ l.GetFepType() + " ,FepType " + iFepType);
					if ((l.GetSocketUpChannel() != null)
							&& (l.GetFepType() == iFepType)) {
						if (l.GetSocketUpChannel().socket().getInetAddress()
								.getHostAddress().equals(sd.MobileNo)) {
							//通道地址与终端指定的发送地址一致
				//			CommunicationServerConstants.Trc1
				//					.TraceLog("32、Find Pre Channel 110!");
							break;
						}
					} else {
						l = null;
					}
				}
				if (l != null) { //只有找到对应的链路信息后继续处理，否则显示错误信息
				//	CommunicationServerConstants.Trc1
				//			.TraceLog("33、Send Msg With Pre ");
					BuildUpPreReceiveFrameAndSend(l.GetSocketUpChannel(),
							sd.MessageContent, sd.TerminalAddress,
							sd.CommandSeq, iFepType); //组成命令并下发
					SaveDownFlowRecordToDB("", iFepType, sd.MessageContent,
							sd.TerminalAddress, l.GetSocketUpChannel()); //保存通讯记录
				} else {
				//	CommunicationServerConstants.Trc1
				//			.TraceLog("34、No Correspond FepLink To Send!");
				}
			} else { //发送主站下发报文，需要根据终端逻辑地址找到对应的物理地址和对应的通讯链路
				TerminalInfo ti = null;
				ti = (TerminalInfo) CommunicationServerConstants.TerminalLocalList
						.get(sd.TerminalAddress);
			//	CommunicationServerConstants.Trc1
			//			.TraceLog("30、FepCommList Size is "
			//					+ FepCommList.size());
				if (ti != null) { //找到对应的终端信息后继续，否则提示出错
					FepLinkList l = null;
					for (int j = FepCommList.size() - 1; j >= 0; j--) {
						l = (FepLinkList) FepCommList.get(j); //根据终端通讯方式查找对应的通道
			//			CommunicationServerConstants.Trc1
			//					.TraceLog("31、Fep Info " + l.GetFepType()
			//							+ " ,TerminalCommunication "
			//							+ sd.TerminalCommunication + " ");
						//2006-12-8 对于所有的通道都增加链路IP和档案中配置的是否一致的判断
						if ((l.GetSocketDownChannel() != null)
								&& (l.GetFepType() == sd.TerminalCommunication)) {
							//寻找对应的串口通道
							if (sd.TerminalCommunication == 10
									&& (l.GetSocketDownChannel().socket()
											.getInetAddress().getHostAddress()
											.equals(ti.COMCommunicationIP))) {
					//			CommunicationServerConstants.Trc1
					//					.TraceLog("32、Find Fep Channel 10!");
								break;
							} else if (sd.TerminalCommunication == 30) {
					//			CommunicationServerConstants.Trc1
					//					.TraceLog("32、Find Fep Channel 30!");
								break;
							} else if (sd.TerminalCommunication == 31) {
					//			CommunicationServerConstants.Trc1
					//					.TraceLog("32、Find Fep Channel 31!");
								break;
							} else if (sd.TerminalCommunication == 32) {
					//			CommunicationServerConstants.Trc1
					//					.TraceLog("32、Find Fep Channel 32!");
								break;
							} else if ((sd.TerminalCommunication == 40)
									&& (l.GetSocketDownChannel().socket()
											.getInetAddress().getHostAddress()
											.equals(ti.UDPCommunicationIp))) {
					//			CommunicationServerConstants.Trc1
					//					.TraceLog("32、Find Fep Channel 40!");
								break;
							} else if ((sd.TerminalCommunication == 50)
									&& (l.GetSocketDownChannel().socket()
											.getInetAddress().getHostAddress()
											.equals(ti.TCPCommunicationIp))) {
					//			CommunicationServerConstants.Trc1
					//					.TraceLog("32、Find Fep Channel 50!");
								break;
							} else if ((sd.TerminalCommunication == 51)
									&& (l.GetSocketDownChannel().socket()
											.getInetAddress().getHostAddress()
											.equals(ti.TCPCommunicationIp))) {
					//			CommunicationServerConstants.Trc1
					//					.TraceLog("32、Find Fep Channel 51!");
								break;
							} else if ((sd.TerminalCommunication == 52)
									&& (l.GetSocketDownChannel().socket()
											.getInetAddress().getHostAddress()
											.equals(ti.TCPCommunicationIp))) {
					//			CommunicationServerConstants.Trc1
					//					.TraceLog("32、Find Fep Channel 52!");
								break;
							} else {
								l = null;
							}
						} else {
							l = null;
						}
					}
					if (l != null) { //只有找到对应的链路信息后继续处理，否则显示错误信息
						switch (sd.TerminalCommunication) { //根据下发的终端的通讯方式查找对应链路的信息和终端物理地址
						case 10: {
							//加密
							sd.MessageContent = ReBuildFrameOfEncrypt(
									sd.MessageContent, "192.168.1.217:0000",
									sd.TerminalAddress, sd.ArithmeticNo);
							sd.MessageLength = sd.MessageContent.length();
							//判断要下发的链路IP是否和终端最近从串口有上送数据的IP相同
					//		CommunicationServerConstants.Trc1
					//				.TraceLog("33、Send Msg With Com, ComLocalAddress is "
					//						+ ti.COMLocalAddress);
							BuildUpFepReceiveFrameAndSend(l
									.GetSocketDownChannel(), sd.MessageLength,
									sd.MessageContent, ti.TerminalCOMAddress,
									ti.COMLocalAddress,
									sd.TerminalCommunication, sd.YXJ); //组成命令并下发
							SaveDownFlowRecordToDB(ti.TerminalCOMAddress,
									sd.TerminalCommunication,
									sd.MessageContent, ti.TerminalAddress, l
											.GetSocketDownChannel()); //保存通讯记录
							break;

						} //串口判断有两种条件，而短信也需要有两种条件
						case 31:
						case 32:
						case 30: {
							//加密
							sd.MessageContent = ReBuildFrameOfEncrypt(
									sd.MessageContent, "192.168.1.217:0000",
									sd.TerminalAddress, sd.ArithmeticNo);
							sd.MessageLength = sd.MessageContent.length();
							//判断要下发的链路IP是否和终端最近从短信有上送数据的IP相同
							BuildUpFepReceiveFrameAndSend(l
									.GetSocketDownChannel(), sd.MessageLength,
									sd.MessageContent, ti.TerminalSIMID,
									ti.SIMLocalAddress,
									sd.TerminalCommunication, sd.YXJ);

							SaveDownFlowRecordToDB(ti.TerminalSIMID,
									sd.TerminalCommunication,
									sd.MessageContent, ti.TerminalAddress, l
											.GetSocketDownChannel());

							break;
						}
							//UDP只能等待终端上送数据后再下发或者从数据库得到保留的IP地址
						case 40: {
							//加密
							sd.MessageContent = ReBuildFrameOfEncrypt(
									sd.MessageContent, "192.168.1.217:0000",
									sd.TerminalAddress, sd.ArithmeticNo);
							sd.MessageLength = sd.MessageContent.length();

							BuildUpFepReceiveFrameAndSend(l
									.GetSocketDownChannel(), sd.MessageLength,
									sd.MessageContent, ti.TerminalUDPAddress,
									ti.UDPLocalAddress,
									sd.TerminalCommunication, sd.YXJ);
							SaveDownFlowRecordToDB(ti.TerminalUDPAddress,
									sd.TerminalCommunication,
									sd.MessageContent, ti.TerminalAddress, l
											.GetSocketDownChannel());
							break;
						}
							//TCP只能等待终端上送数据后再下发或者从数据库得到保留的IP地址
						case 50:
						case 51:
						case 52: {
							//加密
							sd.MessageContent = ReBuildFrameOfEncrypt(
									sd.MessageContent, "192.168.1.217:0000",
									sd.TerminalAddress, sd.ArithmeticNo);
							sd.MessageLength = sd.MessageContent.length();
				//			CommunicationServerConstants.Trc1
				//					.TraceLog("33、Send Msg With TCP, TCPLocalAddress is "
				//							+ ti.TCPLocalAddress
				//							+ " TerminalTCPAddress is "
				//							+ ti.TerminalTCPAddress);
							BuildUpFepReceiveFrameAndSend(l
									.GetSocketDownChannel(), sd.MessageLength,
									sd.MessageContent, ti.TerminalTCPAddress,
									ti.TCPLocalAddress,
									sd.TerminalCommunication, sd.YXJ);
							SaveDownFlowRecordToDB(ti.TerminalTCPAddress,
									sd.TerminalCommunication,
									sd.MessageContent, ti.TerminalAddress, l
											.GetSocketDownChannel());
							break;
						}
						}
					} else {
				//		CommunicationServerConstants.Trc1
				//				.TraceLog("34、No Correspond FepLink To Send!");
					}
				} else { //找不到档案信息则认为是测试主站调用（不存在档案信息）
				//	CommunicationServerConstants.Trc1
				//			.TraceLog("35、No Correspond Terminal Info!");
					AckBack = true;
				}
			}
		} catch (Exception ex) {
			StackTraceElement[] s = ex.getStackTrace();
			CommunicationServerConstants.Log1
					.WriteLog("CommunicationScheduler:SendDownFlowToFep() Error,SendObject Info is "
							+ sd.TerminalAddress
							+ ","
							+ sd.TerminalCommunication
							+ " CodeLineNo is "
							+ s[0].toString()
							+ " "
							+ s[s.length - 1].toString());
		}
	}

	public void run() {
		ListenThread ls = new ListenThread();
		ls.start();
		SaveUpAndDownRecordThread SaveUpAndDownRecord = new SaveUpAndDownRecordThread();
		SaveUpAndDownRecord.start();
		SendDLMSThread SendDLMS = new SendDLMSThread();
		SendDLMS.start();
		/*
		 * 20090924 此版本暂时不需要支持230M通道
		 Send230MThread Send230M = new Send230MThread();
		 Send230M.start();
		 */

		Calendar detectDateTime = null;
		Calendar TerminalInfoSyncTime = null; //终端档案同步时间
		//Calendar DailySleep = null;
		Calendar UpdateDB = null; //终端状态信息回写数据库时间 
		Calendar SleepCDMA = null;
		int DBSaveMinute = CommunicationServerConstants.COMMSERVICE_COMMUNICATIONSCHEDULER_DATABASESAVEMINUTE; //数据库回写间隔
		int CDMASleepMinute = CommunicationServerConstants.COMMSERVICE_COMMUNICATIONSCHEDULER_SLEEPINTERVAL; //睡眠CDMA终端的时间间隔
		try {
			detectDateTime = Calendar.getInstance();
			detectDateTime.add(Calendar.MINUTE, 5); //每隔5分钟检查一次链路情况
			TerminalInfoSyncTime = Calendar.getInstance();
			TerminalInfoSyncTime.add(Calendar.HOUR, 6); //每隔6小时同步一次档案
			// TerminalInfoSyncTime.add(Calendar.MINUTE, 30); //每隔30分钟同步一次档案
			UpdateDB = Calendar.getInstance();
			UpdateDB.add(Calendar.MINUTE, DBSaveMinute); //每隔半个小时同回写一数据库
			//DailySleep = Calendar.getInstance();
			SleepCDMA = Calendar.getInstance();
			SleepCDMA.add(Calendar.MINUTE, CDMASleepMinute); //睡眠CDMA终端的时间间隔默认60分钟

			/*
			 formatter = new SimpleDateFormat("yyyy-MM-dd");
			 String sDate = formatter.format(DailySleep.getTime()) +
			 " " + CommunicationServerConstants.DAILY_SLEEPDO_TIME;
			 formatter1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			 try {
			 java.util.Date d = formatter1.parse(sDate);
			 DailySleep.setTime(d);
			 }
			 catch (Exception ex) {
			 }*/
		} catch (Exception ex3) {
		}
		while (true) {
			//1.维护和前置机通道的链路
			try {
				try {
					/*if (CommunicationServerConstants.DAILY_SLEEPDO.toLowerCase().equals(
					 "true")) {
					 Calendar NowDateTime = Calendar.getInstance();
					 if (NowDateTime.after(DailySleep)) {
					 DailySleep();
					 formatter = new SimpleDateFormat("yyyy-MM-dd");
					 DailySleep.add(Calendar.DATE, 1);
					 String sDate = formatter.format(DailySleep.getTime()) +
					 " " + CommunicationServerConstants.DAILY_SLEEPDO_TIME;
					 formatter1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					 try {
					 java.util.Date d = formatter1.parse(sDate);
					 DailySleep.setTime(d);
					 }
					 catch (Exception ex) {
					 }
					 }
					 }*/

					if (TerminalInfoSyncTime.before(Calendar.getInstance())) {
						ManualUpdateTerminalInfo(10, "");
						ManualUpdateSwitchMPInfo(10, "", 0);
						TerminalInfoSyncTime = Calendar.getInstance();
						//    TerminalInfoSyncTime.add(Calendar.MINUTE, 30); //每隔30分钟同步一次档案
						TerminalInfoSyncTime.add(Calendar.HOUR, 6); //每隔6小时同步一次档案
					} else if (CommunicationServerConstants.BuildTerminalTag
							&& !CommunicationServerConstants.TerminalAddr
									.equals("")) {
						ManualUpdateTerminalInfo(20,
								CommunicationServerConstants.TerminalAddr); //更新指定终端档案
						CommunicationServerConstants.BuildTerminalTag = false;
						CommunicationServerConstants.TerminalAddr = "";
					} else if (CommunicationServerConstants.BuildMPTag
							&& !CommunicationServerConstants.TerminalAddr
									.equals("")
							&& CommunicationServerConstants.CLDXH != -1) {
						ManualUpdateSwitchMPInfo(20,
								CommunicationServerConstants.TerminalAddr,
								CommunicationServerConstants.CLDXH); //更新指定终端档案
						CommunicationServerConstants.BuildMPTag = false;
						CommunicationServerConstants.TerminalAddr = "";
						CommunicationServerConstants.CLDXH = -1;
					}

					if (UpdateDB.before(Calendar.getInstance())) {
						WriteTerminalInfotoDataBase();
						UpdateDB = Calendar.getInstance(); //更新本次更新时间
						UpdateDB.add(Calendar.MINUTE, DBSaveMinute); //默认每隔半个小时回写一次数据库
					}

					/*
					 *20090924 此版本暂时不需要支持CDMA唤醒功能            
					 if (SleepCDMA.before(Calendar.getInstance())) { //默认每隔1个小时睡眠不在线CDMA终端
					 SleepDownOnLineTerminal();
					 SleepCDMA = Calendar.getInstance(); //更新本次更新时间
					 SleepCDMA.add(Calendar.MINUTE, CDMASleepMinute);
					 }
					 */

					if (detectDateTime.before(Calendar.getInstance())) {
						try {
							OnLineStatusCheck();
							detectDateTime = Calendar.getInstance(); //更新本次更新时间
							detectDateTime.add(Calendar.MINUTE, 5); //每隔5分钟检查一次终端在线状态、写回终端物理地址
						} catch (Exception ex5) {
						}
						/*for (int i = 0; i < FepCommList.size(); i++) {
						 try {
						 FepLinkList FepLink = (FepLinkList) FepCommList.get(i);
						 Calendar c = FepLink.GetLastCommDate();
						 //暂时屏蔽对于通道链路超时的判断
						 c.add(Calendar.MINUTE, TimeOut); //判断链路是否超时
						 if (FepLink.GetSocketDownChannel() != null &&
						 (c.before(Calendar.getInstance()) || (!FepLink.ConnectFlag))) {
						 try {
						 FepLink.GetSocketDownChannel().close();
						 FepLink.GetSocketDownChannel().socket().close();
						 //断开下行链路，但是对于上行链路则不断开；上行链路只要有数据，就会主动重连，关键是对于下行链路的控制
						 FepLink.SetSocketDownChannel(null);
						 }
						 catch (Exception ex1) {
						 utils.PrintDebugMessage("Close Fep UpChannel Error " +
						 ex1.toString(), Debug);
						 fl.WriteLog(
						 "CommunicationScheduler:Close Fep UpChannel Error,error message is " +
						 ex1.toString());
						 }
						 FepCommList.set(i, FepLink);
						 }
						 }
						 catch (Exception ex2) {
						 }
						 }*/
					}
				} catch (Exception ex1) {
					CommunicationServerConstants.Log1
							.WriteLog("TimingJob Exception " + ex1.toString());
				}
				SendData();
			} catch (Exception ex4) {
				CommunicationServerConstants.Log1
						.WriteLog("CommunicationScheduler:FepCommList TimeOut Process Error,error is "
								+ ex4.toString());
			}
			try {
				Thread.sleep(1);
			} catch (Exception ex) {
			}
		}
	}

	private void SendData() {
		//2.取发送队列数据发送
		try {
			int iSize = 0, ReSendTimes = 0;
			iSize = CommunicationServerConstants.GlobalSendList.size();
			Calendar SendTime = Calendar.getInstance();
			StructSendData sd = null;
			while (iSize > 0) {
				if (AckBack) {
					ReSendTimes = 0;
					sd = (StructSendData) CommunicationServerConstants.GlobalSendList
							.get(0);
					CommunicationServerConstants.GlobalSendList.remove(0);
					iSize = CommunicationServerConstants.GlobalSendList.size();
				//	CommunicationServerConstants.Trc1
				//			.TraceLog("07、DownFlow Content is TXLX:"
				//					+ sd.TerminalCommunication + " SJCD:"
				//					+ sd.MessageLength + " ZDLJDZ:"
				//					+ sd.TerminalAddress);
					AckBack = false;
					SendDownFlowToFep(sd);
					SendTime = Calendar.getInstance();
					SendTime.add(Calendar.MILLISECOND, 100);
				} else if (SendTime.before(Calendar.getInstance())
						&& (ReSendTimes < 1)) {
					if (sd != null) {
						ReSendTimes = ReSendTimes + 1;
						SendDownFlowToFep(sd);
						SendTime = Calendar.getInstance();
						SendTime.add(Calendar.MILLISECOND, 50);
					//	CommunicationServerConstants.Trc1
					//			.TraceLog("Resend frame:" + sd.MessageContent
					//					+ " TerminalInfo " + sd.TerminalAddress
					//					+ " " + sd.TerminalCommunication);
					} else {
						AckBack = true;
						iSize = CommunicationServerConstants.GlobalSendList
								.size();
					}
				} else if (SendTime.before(Calendar.getInstance())
						&& ReSendTimes >= 1) {
					AckBack = true;
					iSize = CommunicationServerConstants.GlobalSendList.size();
				}
				try {
					Thread.sleep(1);
				} catch (InterruptedException ex) {
				}
			}
		} catch (Exception ex2) {
			CommunicationServerConstants.Log1
					.WriteLog("CommunicationScheduler:Process GlobalSendList Error,error is "
							+ ex2.toString());
		}
	}

	private void UpdateLastCommDate(SocketChannel channel) {
		try {
			for (int i = 0; i < FepCommList.size(); i++) {
				FepLinkList l = new FepLinkList();
				l = (FepLinkList) FepCommList.get(i);
				if (l.GetSocketUpChannel() == channel) {
					l.SetLastCommDate(Calendar.getInstance());
					break;
				} else if (l.GetSocketDownChannel() == channel) {
					l.SetLastCommDate(Calendar.getInstance());
					break;
				}
			}
		} catch (Exception ex) {
			CommunicationServerConstants.Log1
					.WriteLog("CommunicationScheduler:UpdateLastCommDate() Error,error is "
							+ ex.toString());
		}
	}

	private void UpdateLastCommDateOfPre(SocketChannel channel) {
		try {
			for (int i = 0; i < PreCommList.size(); i++) {
				PreLinkList l = new PreLinkList();
				l = (PreLinkList) PreCommList.get(i);
				if (l.GetSocketUpChannel() == channel) {
					l.SetLastCommDate(Calendar.getInstance());
					break;
				}
			}
		} catch (Exception ex) {
			CommunicationServerConstants.Log1
					.WriteLog("CommunicationScheduler:UpdateLastCommDateOfPre() Error,error is "
							+ ex.toString());
		}
	}

	private void AckMessage(String CommandID, String sData,
			SocketChannel channel) {
		ByteBuffer buffer = ByteBuffer.allocateDirect(5000); //用于发送的缓冲
		try {
			String ss = "0000000D08" + CommandID + sData.substring(16, 24)
					+ "0A";
			buffer.clear();
			buffer.put(utils.str2bytes(ss));
			buffer.flip();
			try {
				channel.write(buffer);
			} catch (IOException ex) {
			}
			buffer.clear();
		} catch (Exception ex1) {
			CommunicationServerConstants.Log1
					.WriteLog("CommunicationScheduler:AckMessage() Error,error is "
							+ ex1.toString());
		}
	}

	private void SaveDownFlowRecordToDB(String MsgDst, int ChannelType,
			String MessageContent, String ZDLJDZ, SocketChannel channel) {
		try {
			if (GlobalDownRecordSaveList.size() < CommunicationServerConstants.JMS_MAXCOUNT) {
				Struct_CommRecordItem CommRecordItem = new Struct_CommRecordItem();
				CommRecordItem.SetSourceAddress(MsgDst);
				CommRecordItem.SetTargetAddress(channel.socket()
						.getInetAddress().getHostAddress());
				CommRecordItem.SetChannelType(ChannelType);
				CommRecordItem.SetTerminalAddress(ZDLJDZ);
				CommRecordItem.SetMessageContent(MessageContent);
				GlobalDownRecordSaveList.add(CommRecordItem);
			}
		} catch (Exception ex) {
			CommunicationServerConstants.Log1
					.WriteLog("CommunicationScheduler:SaveDownFlowRecordToDB() Error,error is "
							+ ex.toString());
		}
	}

	//对于国网的异常的确认
	private void ResponseToQuanGuoAlarm(int SourceChannel, int MessageLength,
			String MessageContent) {
		try {
			//687500750068C4081204030011EA1513290506092FC000007F0000E0042A20151329023416
			String sQrbw = "";
			sQrbw = "68310031006800" + MessageContent.substring(14, 24) + "006";
			sQrbw = sQrbw + MessageContent.substring(27, 28) + "00000100";
			sQrbw = sQrbw + "0016"; //至此已经将报警报文的前面组装好了
			sQrbw = GetFrameInfo.gGetParityByteOfQuanGuo(sQrbw); //计算校验码
			StructSendData sd = new StructSendData();
			sd.MessageContent = sQrbw.trim();
			sd.MessageLength = sQrbw.length();
			sd.MessageType = 30;
			sd.MobileNo = "";
			sd.TerminalAddress = MessageContent.substring(16, 18)
					+ MessageContent.substring(14, 16)
					+ MessageContent.substring(20, 22)
					+ MessageContent.substring(18, 20);
			sd.TerminalCommunication = SourceChannel;
			CommunicationServerConstants.GlobalSendList.add(sd);
			//队列长度超过5000条，就删除头上的记录，控制队列长度
			if (CommunicationServerConstants.GlobalSendList.size() > CommunicationServerConstants.JMS_MAXCOUNT) {
				CommunicationServerConstants.GlobalSendList.remove(0);
			}
		} catch (Exception ex) {
			CommunicationServerConstants.Log1
					.WriteLog("CommunicationScheduler:ResponseToQuanGuoAlarm() Error,error is "
							+ ex.toString());
		}
	}

	//对于浙江规范的异常的确认
	private void ResponseToZhejiangAlarm(int SourceChannel, int MessageLength,
			String MessageContent) {
		try {
			String sQrzt, sQrbw = "";
			String sBjbm = MessageContent.substring(36, 40);
			sQrzt = MessageContent.substring(0, 16); //Copy(MessageContent, 1, 16);
			sQrbw = sQrbw + sQrzt;
			sQrbw = sQrbw + "0A0300"; //加上报警确认控制码,对一个报警确认来说，报警数据区只有两个字节的数据要回
			String sBjfscldh = MessageContent.substring(26, 28); //从报警报文中得到第一个报警的发生测量点号
			sQrbw = sQrbw + sBjfscldh;
			sQrbw = sQrbw + sBjbm + "0016"; //至此已经将报警报文的前面组装好了
			sQrbw = GetFrameInfo.gGetParityByteOfZheJiang(sQrbw); //计算校验码
			StructSendData sd = new StructSendData();
			sd.MessageContent = sQrbw.trim();
			sd.MessageLength = sQrbw.length();
			sd.MessageType = 30;
			sd.MobileNo = "";
			sd.TerminalAddress = MessageContent.substring(2, 6)
					+ MessageContent.substring(8, 10)
					+ MessageContent.substring(6, 8);
			sd.TerminalCommunication = SourceChannel;
			CommunicationServerConstants.GlobalSendList.add(sd);
			//队列长度超过5000条，就删除头上的记录，控制队列长度
			if (CommunicationServerConstants.GlobalSendList.size() > CommunicationServerConstants.JMS_MAXCOUNT) {
				CommunicationServerConstants.GlobalSendList.remove(0);
			}
		} catch (Exception ex) {
			CommunicationServerConstants.Log1
					.WriteLog("CommunicationScheduler:ResponseToZhejiangAlarm() Error,error is "
							+ ex.toString());
		}
	}

	private String SearchTerminalAddress(int SourceChannel,
			SocketChannel channel, String MsgSrc, String LocalAddr) {
		String sZDLJDZ = "";
		try {
			for (Enumeration e = CommunicationServerConstants.TerminalLocalList
					.elements(); e.hasMoreElements();) {
				TerminalInfo ti = (TerminalInfo) e.nextElement();
				if (ti.TerminalCurrent == SourceChannel) {
					if (SourceChannel == 10) {
						if (ti.TerminalCOMAddress.equals(MsgSrc)
								&& ti.COMLocalAddress.equals(LocalAddr)
								&& ti.COMCommunicationIP.equals(channel
										.socket().getInetAddress()
										.getHostAddress())) {
							sZDLJDZ = ti.TerminalAddress;
							break;
						}
					} else if (SourceChannel == 30 || SourceChannel == 31
							|| SourceChannel == 32) {
						if (ti.TerminalSIMID.equals(MsgSrc)
								&& ti.SIMLocalAddress.equals(LocalAddr)
								&& ti.SIMCommunicationIP.equals(channel
										.socket().getInetAddress()
										.getHostAddress())) {
							sZDLJDZ = ti.TerminalAddress;
							break;
						}
					} else if (SourceChannel == 40) {
						if (ti.TerminalUDPAddress.equals(MsgSrc)
								&& ti.UDPLocalAddress.equals(LocalAddr)
								&& ti.UDPCommunicationIp.equals(channel
										.socket().getInetAddress()
										.getHostAddress())) {
							sZDLJDZ = ti.TerminalAddress;
							break;
						}
					} else if (SourceChannel == 50 || SourceChannel == 51
							|| SourceChannel == 52) {
						if (ti.TerminalTCPAddress.equals(MsgSrc)
								&& ti.TCPLocalAddress.equals(LocalAddr)
								&& ti.TCPCommunicationIp.equals(channel
										.socket().getInetAddress()
										.getHostAddress())) {
							sZDLJDZ = ti.TerminalAddress;
							break;
						}
					}
				}
			}
		} catch (Exception ex) {
			CommunicationServerConstants.Log1
					.WriteLog("CommunicationScheduler:UpdateTerminalLocalList() Error,error is "
							+ ex.toString());
		}
		return sZDLJDZ;
	}

	private void UpdateTerminalLocalList(String sZDLJDZ, int ZDGY,
			SocketChannel channel, int SourceChannel, String MessageContent,
			String MsgSrc, String LocalAddr) {
		try {
			TerminalInfo ti = null;
			ti = (TerminalInfo) CommunicationServerConstants.TerminalLocalList
					.get(sZDLJDZ);
			if (ti != null) {
				switch (SourceChannel) {
				case 10: {
					if (!ti.COMCommunicationIP.equals(channel.socket()
							.getInetAddress().getHostAddress())) {
						ti.CommuniParamChanged = true;
					}
					ti.COMCommunicationIP = channel.socket().getInetAddress()
							.getHostAddress();
					ti.TerminalCOMAddress = MsgSrc;
					ti.COMLocalAddress = LocalAddr;
					ti.TerminalCurrent = SourceChannel;
					CommunicationServerConstants.Trc1
							.TraceLog("15、UpdateTerminalLocalList ZDLJDZ:"
									+ sZDLJDZ + " Channel:" + SourceChannel
									+ " Address:" + MsgSrc);
					CommunicationServerConstants.TerminalLocalList
							.remove(sZDLJDZ);
					CommunicationServerConstants.TerminalLocalList.put(sZDLJDZ,
							ti);
					break;
				}
				case 31:
				case 32:
				case 30: {
					if (!ti.SIMCommunicationIP.equals(channel.socket()
							.getInetAddress().getHostAddress())) {
						ti.CommuniParamChanged = true;
					}
					ti.SIMCommunicationIP = channel.socket().getInetAddress()
							.getHostAddress();
					ti.TerminalSIMID = MsgSrc;
					ti.SIMLocalAddress = LocalAddr;
					ti.TerminalCurrent = SourceChannel;
					CommunicationServerConstants.Trc1
							.TraceLog("15、UpdateTerminalLocalList ZDLJDZ:"
									+ sZDLJDZ + " Channel:" + SourceChannel
									+ " Address:" + MsgSrc);
					CommunicationServerConstants.TerminalLocalList
							.remove(sZDLJDZ);
					CommunicationServerConstants.TerminalLocalList.put(sZDLJDZ,
							ti);
					break;
				}
				case 40: {
					ti.UDPCommunicationIp = channel.socket().getInetAddress()
							.getHostAddress();
					if (!ti.TerminalUDPAddress.equals(MsgSrc)) {
						ti.CommuniParamChanged = true;
					}
					ti.TerminalUDPAddress = MsgSrc;
					ti.UDPLocalAddress = LocalAddr;
					ti.TerminalCurrent = SourceChannel;
					ti.LastLinkTime = Calendar.getInstance();
					if (ti.OnLineStatus == 20) {
						ti.OnLineStatus = 10;
						ti.OnLineChanged = true;
						CommunicationServerConstants.Trc1
								.TraceLog("19、UpdateTerminalLocalList ZDLJDZ:"
										+ sZDLJDZ + " Channel:" + SourceChannel
										+ " onlien now");
					}
					CommunicationServerConstants.Trc1
							.TraceLog("15、UpdateTerminalLocalList ZDLJDZ:"
									+ sZDLJDZ + " Channel:" + SourceChannel);
					CommunicationServerConstants.TerminalLocalList
							.remove(sZDLJDZ);
					CommunicationServerConstants.TerminalLocalList.put(sZDLJDZ,
							ti);
					break;
				}
				case 50:
				case 51:
				case 52: {
					ti.TCPCommunicationIp = channel.socket().getInetAddress()
							.getHostAddress();
					if (!ti.TerminalTCPAddress.equals(MsgSrc)) {
						ti.CommuniParamChanged = true;
					}
					ti.TerminalProtocol = ti.TerminalProtocol;
					ti.TerminalTCPAddress = MsgSrc;
					ti.TCPLocalAddress = LocalAddr;
					ti.TerminalCurrent = SourceChannel;
					ti.LastLinkTime = Calendar.getInstance();
					if (ti.OnLineStatus == 20) {
						ti.OnLineStatus = 10;
						ti.OnLineChanged = true;
						CommunicationServerConstants.Trc1
								.TraceLog("19、UpdateTerminalLocalList ZDLJDZ:"
										+ sZDLJDZ + " Channel:" + SourceChannel
										+ " onlien now");
					}
					CommunicationServerConstants.Trc1
							.TraceLog("15、UpdateTerminalLocalList ZDLJDZ:"
									+ sZDLJDZ + " Channel:" + SourceChannel
									+ " TerminalProtocol:"
									+ ti.TerminalProtocol);
					CommunicationServerConstants.TerminalLocalList
							.remove(sZDLJDZ);
					CommunicationServerConstants.TerminalLocalList.put(sZDLJDZ,
							ti);
					break;
				}
				}
			} else { //队列里没有的新终端
				TerminalInfo tiNew = new TerminalInfo();
				tiNew.TerminalAddress = sZDLJDZ;
				tiNew.TerminalCurrent = SourceChannel;
				tiNew.TerminalPassWord = "8888";
				tiNew.TerminalProtocol = ZDGY;
				if (tiNew.TerminalProperty == 1 || tiNew.TerminalProperty == 4) { //CDMA终端
					tiNew.HeartInterval = CommunicationServerConstants.TERMINAL_HEARTINTERVAL_CDMA;
				} else {
					tiNew.HeartInterval = CommunicationServerConstants.TERMINAL_HEARTINTERVAL_GPRS;
				}
				switch (SourceChannel) {
				case 10: {
					tiNew.COMCommunicationIP = channel.socket()
							.getInetAddress().getHostAddress();
					tiNew.TerminalCOMAddress = MsgSrc;
					tiNew.CommuniParamChanged = true;
					tiNew.COMLocalAddress = LocalAddr;
					CommunicationServerConstants.TerminalLocalList.put(sZDLJDZ,
							tiNew);
					NewTerminalList.add(tiNew);
					break;
				}
				case 20: {
					tiNew.RadioCommunicationIP = channel.socket()
							.getInetAddress().getHostAddress();
					tiNew.TerminalCOMAddress = MsgSrc;
					tiNew.CommuniParamChanged = true;
					tiNew.COMLocalAddress = LocalAddr;
					CommunicationServerConstants.TerminalLocalList.put(sZDLJDZ,
							tiNew);
					NewTerminalList.add(tiNew);
					break;
				}
				case 31:
				case 32:
				case 30: {
					tiNew.SIMCommunicationIP = channel.socket()
							.getInetAddress().getHostAddress();
					tiNew.TerminalSIMID = MsgSrc;
					tiNew.CommuniParamChanged = true;
					tiNew.SIMLocalAddress = LocalAddr;
					CommunicationServerConstants.TerminalLocalList.put(sZDLJDZ,
							tiNew);
					NewTerminalList.add(tiNew);
					break;
				}
				case 40: {
					tiNew.UDPCommunicationIp = channel.socket()
							.getInetAddress().getHostAddress();
					tiNew.TerminalUDPAddress = MsgSrc;
					tiNew.UDPLocalAddress = LocalAddr;
					tiNew.CommuniParamChanged = true;
					tiNew.LastLinkTime = Calendar.getInstance();
					tiNew.OnLineStatus = 10; //新增的终端，其在线状态必定在线
					CommunicationServerConstants.TerminalLocalList.put(sZDLJDZ,
							tiNew);
					NewTerminalList.add(tiNew);
					break;
				}
				case 50: {
					tiNew.TCPCommunicationIp = channel.socket()
							.getInetAddress().getHostAddress();
					tiNew.TerminalTCPAddress = MsgSrc;
					tiNew.TCPLocalAddress = LocalAddr;
					tiNew.OnLineStatus = 10;
					tiNew.CommuniParamChanged = true;
					tiNew.LastLinkTime = Calendar.getInstance();
					CommunicationServerConstants.TerminalLocalList.put(sZDLJDZ,
							tiNew);
					NewTerminalList.add(tiNew);
					break;
				}
				}
				CommunicationServerConstants.Trc1
						.TraceLog("15、NewTerminalAddToLocalList ZDLJDZ:"
								+ sZDLJDZ + " Channel:" + SourceChannel
								+ " Address" + MsgSrc);
			}
		} catch (Exception ex) {
			CommunicationServerConstants.Log1
					.WriteLog("CommunicationScheduler:UpdateTerminalLocalList() Error,error is "
							+ ex.toString());
		}
	}

	private void DealWithUpFrame(int SourceChannel, int MessageLength,
			String MessageContent, String MsgSrc, String LocalAddr,
			SocketChannel channel) {
		try {
			FE_FrameExplain fe = new FE_FrameExplain();
			Struct_FrameInfo sf = new Struct_FrameInfo();
			sf = fe.IFE_FrameExplain(0, MessageContent.toCharArray()); //提交规约解释分析帧类型等
			StructReceiveData rd = new StructReceiveData();
			String sZDLJDZ = "";
			if (sf != null) { //解释成功后，根据数据类型不同处理
				sZDLJDZ = new String(sf.TerminalLogicAdd);
				String sControlCode = new String(sf.ControlCode);
				String sFunctionCode = new String(sf.FunctionCode);
				int iDataType = sf.DataType;
				int iGYH = sf.TermialProtocolNo;
				
				try{
					if ((sFunctionCode.equals("8F"))||(sFunctionCode.equals("0F"))){
						CommunicationServerConstants.GlobalReceiveUpdateFrameList.add(MessageContent.trim());
					}
				}catch (Exception e){
					
				}
				
				//这里根据功能码将预售电的返回数据，提交到预售电返回数据队列中，由单独的线程处理返回结果
				try{
					if (sFunctionCode.equalsIgnoreCase("10"))
					{
						
					}
				}catch(Exception e)
				{
					
				}
				
				if ((iGYH == Glu_ConstDefine.GY_ZD_QUANGUO)
						|| (iGYH == Glu_ConstDefine.GY_ZD_ZHEJIANG)
						|| (iGYH == Glu_ConstDefine.GY_ZD_IHD)) {
					sZDLJDZ = sZDLJDZ.substring(0, 8);
					TerminalInfo ti = (TerminalInfo) CommunicationServerConstants.TerminalLocalList
							.get(new String(sZDLJDZ).trim());
					if (ti != null) {
						iGYH = ti.TerminalProtocol;
					}
				} else if (iGYH == Glu_ConstDefine.GY_ZD_DLMS) {
					if ((iDataType == Glu_ConstDefine.SJLX_ZDDL || iDataType == Glu_ConstDefine.SJLX_ZXXT)
							&& (SourceChannel > 10)) {//心跳帧带表地址，其他帧没有
						//0001000100010012DD1000000000303030303039313230303937 最后8个字节为表地址
						sZDLJDZ = MessageContent.substring(36, 52);
						sZDLJDZ = Encode.HexASCIIToString(sZDLJDZ);
					} else {
						sZDLJDZ = SearchTerminalAddress(SourceChannel, channel,
								MsgSrc, LocalAddr);
					}
				}
				int iMLXH = sf.CommandSeq;
				rd.StationNo = sf.StationNo;
				rd.TerminalProtocal = iGYH;
				rd.TerminalLogicAdd = sZDLJDZ;
				rd.CommandSeq = iMLXH;
				rd.ControlCode = sControlCode;
				rd.FunctionCode = sFunctionCode;
				rd.DataType = iDataType;
				rd.FrameLength = new String(sf.FrameData).trim().length();
				rd.FrameData = new String(sf.FrameData).trim();
				boolean bCallBack = false;
				
				try{
					if (iGYH == Glu_ConstDefine.GY_ZD_DLMS) {//处理DLMS的升级返回帧
						//由于无法从帧格式区分升级返回帧，这里目前只能将DLMS所有的上行帧全部推送处理
						DLMSUpFrame uf = new DLMSUpFrame();
						uf.DeviceAddr = sZDLJDZ.trim().toUpperCase();
						uf.Frame = MessageContent.trim().toUpperCase();
						CommunicationServerConstants.GlobalReceiveDLMSUpdateFrameList.add(uf);
					}
				}catch (Exception e){
					
				}
				
				if (iGYH == Glu_ConstDefine.GY_ZD_DLMS) {
					for (int i = 0; i < CommunicationServerConstants.GlobalDLMSSendList
							.size(); i++) {
						StructDLMSSendData sa = new StructDLMSSendData();
						sa = CommunicationServerConstants.GlobalDLMSSendList
								.get(i);
						if (sa.TerminalAddress.equals(sZDLJDZ)) {
							if (rd.DataType == Glu_ConstDefine.SJLX_DLMSAARE) {
								int iIndex = MessageContent
										.indexOf("A109060760857405080101A2");
								if (iIndex > 0) {
									if (MessageContent.substring(iIndex + 30,
											iIndex + 32).equals("00")) {//accepted (0),rejected-permanent (1),rejected-transient (2)
										if (sa.SecurityLevel == 2
												&& sa.DealStep == 11) {
											sa.DealStep = 12;//StoC
											iIndex = MessageContent
													.indexOf("890760857405080202AA");
											if (iIndex > 0) {
												String sRandom = MessageContent
														.substring(iIndex + 26,
																iIndex + 58);
												sa.Random = sRandom;
											}
										} else {
											sa.DealStep = 20;
											sa.DataBlock = 0;
										}
									} else {
										if (sa.SecurityLevel < 2) {
											sa.SecurityLevel += 1;
											sa.DealStep = 10;//AARE
										} else {
											sa.DealStep = 30;//finish
										}
									}
								}
							} else if (rd.DataType == Glu_ConstDefine.SJLX_PTSJHJ) {
								sa.DataBlock = Integer.parseInt(MessageContent
										.substring(24, 32), 16);
								if (MessageContent.substring(22, 24).equals(
										"00")) {
									sa.DealStep = 22;//have next
								} else if (MessageContent.substring(22, 24)
										.equals("FF")) {
									sa.DealStep = 30;//finish
									bCallBack = true;
								}
							} else if ((rd.DataType == Glu_ConstDefine.SJLX_PTSJ || rd.DataType == Glu_ConstDefine.SJLX_SZFH)
									&& sa.DealStep == 21) {
								sa.DealStep = 30;//finish
								bCallBack = true;
							} else if (rd.DataType == Glu_ConstDefine.SJLX_SZFH
									&& sa.DealStep == 13
									&& sa.SecurityLevel == 2) {
								if (MessageContent.substring(22, 24).equals(
										"00")) {
									sa.DealStep = 20;
									sa.DataBlock = 0;
								}
							}
							break;
						}
					}
				}
				if ((rd.DataType != Glu_ConstDefine.SJLX_ZDTC)
						&& (rd.DataType != Glu_ConstDefine.SJLX_ZDDL)
						&& (rd.DataType != Glu_ConstDefine.SJLX_ZXXT)
						&& (rd.DataType != Glu_ConstDefine.SJLX_WDY)) {
					//提交到报文处理的数据类型:非终端心跳、登录、退出登录以及未定义的报文
					CommunicationServerConstants.Trc1
							.TraceLog("13、FrameExplained TerminalLogicAdd:"
									+ rd.TerminalLogicAdd + " CommandSeq:"
									+ sf.CommandSeq + " iDataType:" + iDataType);
					RecvCount = RecvCount + 1;
					if (rd.DataType == Glu_ConstDefine.SJLX_YCSJZD || //自动上送异常数据
							rd.DataType == Glu_ConstDefine.SJLX_LSSJZD || //自动上送历史数据
							rd.DataType == Glu_ConstDefine.SJLX_YCSJZC || //召测返回异常数据
							rd.DataType == Glu_ConstDefine.SJLX_LSSJZC) { //召测返回历史数据
						if (CommunicationServerConstants.GlobalReceiveJMSList
								.size() < CommunicationServerConstants.JMS_MAXCOUNT) {
							CommunicationServerConstants.GlobalReceiveJMSList
									.add(rd);
							CommunicationServerConstants.Trc1
									.TraceLog("14、AddDataToGlobalReceiveJMSList");
						}
					}
					if (rd.DataType != Glu_ConstDefine.SJLX_YCSJZD && //非主动上送数据
							rd.DataType != Glu_ConstDefine.SJLX_LSSJZD) {
						if (rd.TerminalProtocal == Glu_ConstDefine.GY_ZD_DLMS
								&& (bCallBack || rd.DataType == Glu_ConstDefine.SJLX_PTSJHJ)) {
							if (CommunicationServerConstants.GlobalReceiveCallBackList
									.size() < CommunicationServerConstants.JMS_MAXCOUNT) {
								CommunicationServerConstants.GlobalReceiveCallBackList
										.add(rd);
								CommunicationServerConstants.Trc1
										.TraceLog("14、AddDataToGlobalReceiveCallBackList");
							}
						} else if (rd.TerminalProtocal != Glu_ConstDefine.GY_ZD_DLMS) {
							if (CommunicationServerConstants.GlobalReceiveCallBackList
									.size() < CommunicationServerConstants.JMS_MAXCOUNT) {
								CommunicationServerConstants.GlobalReceiveCallBackList
										.add(rd);
								CommunicationServerConstants.Trc1
										.TraceLog("14、AddDataToGlobalReceiveCallBackList");
							}
						}

					}
				}
				if (rd.DataType == Glu_ConstDefine.SJLX_YCSJZD) { //对于自动上送的异常需要返回确认
					switch (rd.TerminalProtocal) { //根据规约类型不同返回对应的异常确认
					case Glu_ConstDefine.GY_ZD_ZHEJIANG:
					case Glu_ConstDefine.GY_ZD_ZJZB0404: {
						ResponseToZhejiangAlarm(SourceChannel, MessageLength,
								MessageContent);
						break;
					}
					case Glu_ConstDefine.GY_ZD_QUANGUO:
					case Glu_ConstDefine.GY_ZD_698: {
						ResponseToQuanGuoAlarm(SourceChannel, MessageLength,
								MessageContent);
						break;
					}
					default: {
						break;
					}
					}
				}
				UpdateTerminalLocalList(sZDLJDZ, iGYH, channel, SourceChannel,
						MessageContent, MsgSrc, LocalAddr);
			} else {
				CommunicationServerConstants.Trc1.TraceLog("13、Frame:"
						+ MessageContent + " Can not be Explained!");
			}
			if ( //(rd.DataType != Glu_ConstDefine.SJLX_ZDTC) &&
			//(rd.DataType != Glu_ConstDefine.SJLX_ZDDL) &&
			(rd.DataType != Glu_ConstDefine.SJLX_ZXXT)
					&& (rd.DataType != Glu_ConstDefine.SJLX_WDY)) {
				if (GlobalUPRecordSaveList.size() < CommunicationServerConstants.JMS_MAXCOUNT) {
					Struct_CommRecordItem CommRecordItem = new Struct_CommRecordItem();
					CommRecordItem.SetSourceAddress(LocalAddr);
					CommRecordItem.SetChannelType(SourceChannel);
					CommRecordItem.SetTerminalAddress(sZDLJDZ);
					CommRecordItem.SetMessageContent(MessageContent);
					CommRecordItem.SetTargetAddress(MsgSrc);
					GlobalUPRecordSaveList.add(CommRecordItem);
					//  CommunicationServerConstants.Log1.WriteLog("GlobalUPRecordSaveList.add");
					CommunicationServerConstants.Trc1
							.TraceLog("16、SaveUpFlowRecord");
				}
			}
		} catch (Exception ex) {
			CommunicationServerConstants.Log1
					.WriteLog("CommunicationScheduler:DealWithUpFrame() Error,error is "
							+ ex.toString()
							+ " UpFrame is "
							+ MessageContent
							+ " MsgSrc is " + MsgSrc);
		}
	}

	private void DealWithPreUpFrame(int SourceChannel, int MessageLength,
			String MessageContent, String MsgSrc, String LocalAddr,
			String sUserNo, int CommandSeq, SocketChannel channel,
			int CommandType) {
		try {
			//String sMLXH = MessageContent.substring(0,8);
			StructReceiveData rd = new StructReceiveData();
			int iMLXH = CommandSeq;
			rd.StationNo = 0;
			rd.TerminalProtocal = Glu_ConstDefine.GY_PrePayApp;
			rd.TerminalLogicAdd = sUserNo;
			rd.CommandSeq = iMLXH;
			rd.ControlCode = "";
			rd.FunctionCode = "";
			rd.DataType = CommandType;
			rd.FrameLength = MessageContent.length();
			rd.FrameData = MessageContent;
			if (CommunicationServerConstants.GlobalReceiveCallBackList.size() < CommunicationServerConstants.JMS_MAXCOUNT) {
				CommunicationServerConstants.GlobalReceiveCallBackList.add(rd);
				CommunicationServerConstants.Trc1
						.TraceLog("14、AddDataToGlobalReceiveCallBackList");
			}
			if (GlobalUPRecordSaveList.size() < CommunicationServerConstants.JMS_MAXCOUNT) {
				Struct_CommRecordItem CommRecordItem = new Struct_CommRecordItem();
				CommRecordItem.SetSourceAddress(LocalAddr);
				CommRecordItem.SetChannelType(SourceChannel);
				CommRecordItem.SetTerminalAddress(sUserNo);
				CommRecordItem.SetMessageContent(MessageContent);
				CommRecordItem.SetTargetAddress(MsgSrc);
				GlobalUPRecordSaveList.add(CommRecordItem);
				CommunicationServerConstants.Trc1
						.TraceLog("16、SaveUpFlowRecord");
			}
		} catch (Exception ex) {
			CommunicationServerConstants.Log1
					.WriteLog("CommunicationScheduler:DealWithUpFrame() Error,error is "
							+ ex.toString()
							+ " UpFrame is "
							+ MessageContent
							+ " MsgSrc is " + MsgSrc);
		}
	}

	//查询对应socket的前置机通道类型
	private int FindSourceChannel(SocketChannel channel) {
		try {
			for (int i = 0; i < FepCommList.size(); i++) {
				FepLinkList fl = (FepLinkList) FepCommList.get(i);
				if (fl.GetSocketUpChannel() == channel) { //只要比较上行链路的socket是否相同即可
					return fl.GetFepType();
				}
			}
			return 0;
		} catch (Exception ex) {
			CommunicationServerConstants.Log1
					.WriteLog("CommunicationScheduler:FindSourceChannel() Error,error is "
							+ ex.toString());
			return 0;
		}
	}

	private void ACDPosition(String sData) {
		String ACDPos = sData.substring(12, 13);
		int iACD = Integer.parseInt(ACDPos, 16) & 2;
		if (iACD == 2) { //如果请求访问位有效，则说明有异常需要获取
			String sEC = "";
			int iTPV = Integer.parseInt(sData.substring(26, 28), 16) & 128;
			if (iTPV == 128) {
				sEC = sData.substring(sData.length() - 20, sData.length() - 18);
			} else {
				sEC = sData.substring(sData.length() - 8, sData.length() - 6);
			}
			if (Integer.parseInt(sEC, 16) == 0) {
				sEC = "FF00";
			} else if (Integer.parseInt(sEC, 16) <= 16) {
				sEC = "0"
						+ Integer.toHexString(Integer.parseInt(sEC, 16) - 1)
								.toUpperCase() + sEC;
			} else {
				sEC = Integer.toHexString(Integer.parseInt(sEC, 16) - 1)
						.toUpperCase()
						+ sEC;
			}
			String sXXBW = "6839003900684B" + sData.substring(14, 24)
					+ "0E6000000100" + sEC + "0816";
			//目前只是读取了重要事件，而且读取每次都是从头开始读取，每次只读取一条事件
			//？？？？？这部分需要现场调试看是否可行；
			sXXBW = GetFrameInfo.gGetParityByteOfQuanGuo(sXXBW); //计算校验码
			StructSendData sd = new StructSendData();
			sd.YXJ = 2; //事件召测优先级为2
			sd.SendDirect = 1;
			sd.MessageContent = sXXBW.trim();
			sd.MessageLength = sXXBW.length();
			sd.MessageType = 30;
			sd.MobileNo = "";
			sd.TerminalAddress = sData.substring(16, 18)
					+ sData.substring(14, 16) + sData.substring(20, 22)
					+ sData.substring(18, 20);
			sd.TerminalCommunication = 20;
			CommunicationServerConstants.Global230MSendList.add(sd);
			//队列长度超过5000条，就删除头上的记录，控制队列长度
			if (CommunicationServerConstants.Global230MSendList.size() > CommunicationServerConstants.JMS_MAXCOUNT) {
				CommunicationServerConstants.Global230MSendList.remove(0);
			}
		}
	}

	private void AnalyzeCommandContent(String sData, int CommandType,
			byte[] data, SocketChannel channel) {
		try {
			String sMessageLen = "0";
			sMessageLen = sData.substring(1, 8);
			if (sData.length() != utils.HexStrToInt(sMessageLen) * 2) {
				return;
			} else {
				int SourceChannel = FindSourceChannel(channel); //来源通道类型
				switch (CommandType) { //根据消息类型分析消息具体内容，分别处理
				case 2: {
					try {
						int MessageLength = 0;
						String MessageContent = null;
						MessageLength = utils.HexStrToInt(sData.substring(24,
								28)); //实际有效内容的长度
						byte[] Content = new byte[MessageLength];
						for (int i = 0; i < MessageLength; i++) {
							Content[i] = data[i + 14];
						}
						MessageContent = new String(Content); //获取实际有效的内容
						byte[] Src = new byte[25];
						for (int i = 0; i < 25; i++) {
							Src[i] = data[i + 14 + MessageLength];
						}
						String MsgSrc = new String(Src).trim(); //消息来源物理地址
						String LocalAddr = "";
						byte[] From = new byte[25];
						for (int i = 0; i < 25; i++) {
							From[i] = data[i + 39 + MessageLength];
						}
						LocalAddr = new String(From).trim();

						CommunicationServerConstants.Trc1
								.TraceLog("12、AnalyzeCommandContent"
										+ " SoruceAddress:" + SourceChannel
										+ " TargerAddress:" + LocalAddr
										+ " MsgSrc:" + MsgSrc);
						DealWithUpFrame(SourceChannel, MessageLength,
								MessageContent, MsgSrc, LocalAddr, channel); //提交规约解释处理
						if (SourceChannel == 20) { //230M通讯类型
							ACDPosition(MessageContent); //对于230M的通道，需要判断请求访问位，然后下发异常主动读取的命令
						}
					} catch (Exception ex) {
						CommunicationServerConstants.Log1
								.WriteLog("CommunicationScheduler:Process command 2 Error,error is "
										+ ex.toString());
					}
					break;
				}
				case 3: { //对于前置机通道的确认信息，只要判断是否成功，然后决定是否重发或者继续下发
					AckBack = true;
					break;
				}
				case 17: { //批量提交的数据
					try {
						int MessageCount = 0;
						MessageCount = utils.HexStrToInt(sData
								.substring(24, 32)); //本次提交的消息数量
						int TotalLength = 0;
						for (int i = 0; i < MessageCount; i++) {
							int MessageLength = 0;
							String MessageContent = null;
							MessageLength = utils.HexStrToInt(sData.substring(
									32 + TotalLength, 36 + TotalLength)); //实际有效内容的长度
							byte[] Content = new byte[MessageLength];
							for (int j = 0; j < MessageLength; j++) {
								Content[j] = data[j + 18 + TotalLength / 2];
							}
							MessageContent = new String(Content); //获取实际有效的内容
							byte[] Src = new byte[25];
							for (int j = 0; j < 25; j++) {
								Src[j] = data[j + 18 + MessageLength
										+ TotalLength / 2];
							}
							String MsgSrc = new String(Src).trim(); //消息来源物理地址
							String LocalAddr = "";
							byte[] From = new byte[25];
							for (int j = 0; j < 25; j++) {
								From[j] = data[j + 43 + MessageLength
										+ TotalLength / 2];
							}
							LocalAddr = new String(From).trim();
							CommunicationServerConstants.Trc1
									.TraceLog("12、AnalyzeCommandContent"
											+ " SoruceAddress:" + SourceChannel
											+ " TargerAddress:" + LocalAddr
											+ " MsgSrc:" + MsgSrc
											+ " MsgContent:" + MessageContent);
							TotalLength = TotalLength + (MessageLength + 52)
									* 2;
							DealWithUpFrame(SourceChannel, MessageLength,
									MessageContent, MsgSrc, LocalAddr, channel); //提交规约解释处理
							if (SourceChannel == 20) { //230M通讯类型
								ACDPosition(MessageContent); //对于230M的通道，需要判断请求访问位，然后下发异常主动读取的命令
							}
						}
					} catch (Exception ex) {
						CommunicationServerConstants.Log1
								.WriteLog("CommunicationScheduler:Process command 17 Error,error is "
										+ ex.toString());
					}
					break;
				}
				default: {
					break;
				}
				}
			}
		} catch (Exception ex2) {
			CommunicationServerConstants.Log1
					.WriteLog("CommunicationScheduler:AnalyzeCommandContent() Error,error is "
							+ ex2.toString());
		}
	}

	private void AnalyzePreCommandContent(String sData, int CommandType,
			byte[] data, SocketChannel channel) {
		try {
			String sMessageLen = "0";
			sMessageLen = sData.substring(1, 8);
			if (sData.length() != utils.HexStrToInt(sMessageLen) * 2) {
				return;
			} else {
				int SourceChannel = FindSourceChannel(channel); //来源通道类型
				try {
					int MessageLength = 0;
					String MessageContent = null;
					MessageLength = utils.HexStrToInt(sData.substring(24, 28)); //实际有效内容的长度
					byte[] Content = new byte[MessageLength];
					for (int i = 0; i < MessageLength; i++) {
						Content[i] = data[i + 14];
					}
					MessageContent = new String(Content); //获取实际有效的内容
					byte[] Src = new byte[20];
					for (int i = 0; i < 20; i++) {
						Src[i] = data[i + 14 + MessageLength];
					}
					String sUserNo = new String(Src).trim(); //用户户号

					String MsgSrc = "127.0.0.1";
					String LocalAddr = "127.0.0.1";

					int CommandSeq = 0;
					CommandSeq = utils.HexStrToInt(sData.substring(
							28 + MessageLength * 2 + 40, 28 + MessageLength * 2
									+ 40 + 8)); //实际有效内容的长度

					byte[] From = new byte[25];
					for (int i = 0; i < 25; i++) {
						From[i] = data[i + 39 + MessageLength];
					}
					LocalAddr = new String(From).trim();

					CommunicationServerConstants.Trc1
							.TraceLog("12、AnalyzeCommandContent"
									+ " SoruceAddress:" + SourceChannel
									+ " TargerAddress:" + LocalAddr
									+ " sUserNo:" + sUserNo);
					DealWithPreUpFrame(SourceChannel, MessageLength,
							MessageContent, MsgSrc, LocalAddr, sUserNo,
							CommandSeq, channel, CommandType); //提交规约解释处理
				} catch (Exception ex) {
					CommunicationServerConstants.Log1
							.WriteLog("CommunicationScheduler:Process command 2 Error,error is "
									+ ex.toString());
				}
			}
		} catch (Exception ex2) {
			CommunicationServerConstants.Log1
					.WriteLog("CommunicationScheduler:AnalyzeCommandContent() Error,error is "
							+ ex2.toString());
		}

	}

	private void RegisterFepInfo(String Data, SocketChannel channel) {
		try {
			FepLinkList l = new FepLinkList();
			l.SetLastCommDate(Calendar.getInstance());
			l.SetRegFlag(true);
			int FepType = utils.HexStrToInt(Data.substring(24, 26));
			int LinkType = utils.HexStrToInt(Data.substring(26, 28));
			l.SetFepType(FepType);
			l.SetLinkType(LinkType);
			boolean newLink = true;
			for (int i = 0; i < FepCommList.size(); i++) {
				FepLinkList FepLink = (FepLinkList) FepCommList.get(i);
				if (LinkType == 10) { //上行通道注册
					if (FepLink.GetSocketDownChannel() == null) { //如果下行通道还没有对象，则只需要处理上行通道的更新或者新增
						if (FepLink.GetSocketUpChannel() != null) {
							if ((FepLink.GetSocketUpChannel().socket()
									.getInetAddress().getHostAddress()
									.equals(channel.socket().getInetAddress()
											.getHostAddress()))
									&& (FepLink.GetFepType() == FepType)) { //同一IP和通道类型，并且是上行通道的IP
								if (FepLink.GetSocketUpChannel() != null) {
									try {
										FepLink.GetSocketUpChannel().close(); //关闭上次的连接
										FepLink.GetSocketUpChannel().socket().close();
									} catch (IOException ex2) {
										CommunicationServerConstants.Log1
												.WriteLog("CommunicationScheduler:FepLink.GetSocketUpChannel().close IOError,error is "
														+ ex2.toString());
									}
								}
								FepLink.SetSocketUpChannel(channel); //将上行通道的链路修改为本次连接的链路
								FepCommList.set(i, FepLink);
								newLink = false;
								CommunicationServerConstants.Trc1
										.TraceLog("11、Update Fep Info :"
												+ FepType
												+ " LinkType:"
												+ LinkType
												+ " HostAddress:"
												+ channel.socket()
														.getInetAddress()
														.getHostAddress());
							}
						}
					} else {
						if ((FepLink.GetSocketDownChannel().socket()
								.getInetAddress().getHostAddress()
								.equals(channel.socket().getInetAddress()
										.getHostAddress()))
								&& (FepLink.GetFepType() == FepType)) { //下行通道的IP和本次链路的IP一致，并且是相同的通道类型
							if (FepLink.GetSocketUpChannel() != null) {
								try {
									FepLink.GetSocketUpChannel().close(); //关闭上次的连接
									FepLink.GetSocketUpChannel().socket().close();
								} catch (IOException ex2) {
									CommunicationServerConstants.Log1
											.WriteLog("CommunicationScheduler:FepLink.GetSocketUpChannel().close IOError,error is "
													+ ex2.toString());
								}
							}
							FepLink.SetSocketUpChannel(channel); //设置上行通道的链路
							FepCommList.set(i, FepLink);
							newLink = false;
							CommunicationServerConstants.Trc1
									.TraceLog("11、Update Fep Info :"
											+ FepType
											+ " LinkType:"
											+ LinkType
											+ " HostAddress:"
											+ channel.socket().getInetAddress()
													.getHostAddress());
						}
					}
				} else if (LinkType == 20) { //下行通道注册
					if (FepLink.GetSocketUpChannel() == null) { //如果上行通道还没有对象，则只需要处理下行通道的更新或者新增
						if (FepLink.GetSocketDownChannel() != null) {
							if ((FepLink.GetSocketDownChannel().socket()
									.getInetAddress().getHostAddress()
									.equals(channel.socket().getInetAddress()
											.getHostAddress()))
									&& (FepLink.GetFepType() == FepType)) { //同一IP和通道类型，并且是下行通道的IP
								try {									
									FepLink.GetSocketDownChannel().close(); //关闭上次的连接
									FepLink.GetSocketDownChannel().socket().close();
								} catch (IOException ex2) {
									CommunicationServerConstants.Log1
											.WriteLog("CommunicationScheduler:FepLink.GetSocketUpChannel().close IOError,error is "
													+ ex2.toString());
								}
								FepLink.SetSocketDownChannel(channel); //将下行通道的链路修改为本次连接的链路
								FepCommList.set(i, FepLink);
								newLink = false;
								CommunicationServerConstants.Trc1
										.TraceLog("11、Update Fep Info :"
												+ FepType
												+ " LinkType:"
												+ LinkType
												+ " HostAddress:"
												+ channel.socket()
														.getInetAddress()
														.getHostAddress());
							}
						}
					} else {
						if ((FepLink.GetSocketUpChannel().socket()
								.getInetAddress().getHostAddress()
								.equals(channel.socket().getInetAddress()
										.getHostAddress()))
								&& (FepLink.GetFepType() == FepType)) { //上行通道的IP和本次链路的IP一致，并且是相同的通道类型
							if (FepLink.GetSocketDownChannel() != null) {
								try {
									FepLink.GetSocketDownChannel().close(); //关闭上次的连接
									FepLink.GetSocketDownChannel().socket().close();
								} catch (IOException ex2) {
									CommunicationServerConstants.Log1
											.WriteLog("CommunicationScheduler:FepLink.GetSocketUpChannel().close IOError,error is "
													+ ex2.toString());
								}
							}
							FepLink.SetSocketDownChannel(channel); //设置下行通道的链路
							FepCommList.set(i, FepLink);
							newLink = false;
							CommunicationServerConstants.Trc1
									.TraceLog("11、Update Fep Info :"
											+ FepType
											+ " LinkType:"
											+ LinkType
											+ " HostAddress:"
											+ channel.socket().getInetAddress()
													.getHostAddress());
						}
					}
				}
			}
			if (newLink) { //没有找到相应的链路信息，即是新增的链路信息，根据链路类型修改对应的链路
				if (LinkType == 10) {
					l.SetSocketUpChannel(channel);
				} else if (LinkType == 20) {
					l.SetSocketDownChannel(channel);
				}
				FepCommList.add(l);
				CommunicationServerConstants.Trc1.TraceLog("11、Add Fep Info :"
						+ FepType + " LinkType:" + LinkType + " HostAddress:"
						+ channel.socket().getInetAddress().getHostAddress());
			}
			CommunicationServerConstants.Trc1
					.TraceLog("12、RegisterFepInfo FepType:"
							+ FepType
							+ " LinkType:"
							+ LinkType
							+ " HostAddress:"
							+ channel.socket().getInetAddress()
									.getHostAddress());
		} catch (Exception ex) {
			CommunicationServerConstants.Log1
					.WriteLog("CommunicationScheduler:RegisterFepInfo() Error,error is "
							+ ex.toString());
		}
	}

	private void RegisterPreInfo(String Data, SocketChannel channel) {
		try {
			PreLinkList l = new PreLinkList();
			l.SetLastCommDate(Calendar.getInstance());
			l.SetRegFlag(true);
			int FepType = utils.HexStrToInt(Data.substring(24, 26));
			l.SetFepType(FepType);
			boolean newLink = true;
			for (int i = 0; i < PreCommList.size(); i++) {
				PreLinkList FepLink = (PreLinkList) PreCommList.get(i);
				if (FepLink.GetSocketUpChannel() != null) {
					if ((FepLink.GetSocketUpChannel().socket().getInetAddress()
							.getHostAddress().equals(channel.socket()
							.getInetAddress().getHostAddress()))
							&& (FepLink.GetFepType() == FepType)) { //同一IP和通道类型，并且是上行通道的IP
						if (FepLink.GetSocketUpChannel() != null) {
							try {
								FepLink.GetSocketUpChannel().close(); //关闭上次的连接
							} catch (IOException ex2) {
								CommunicationServerConstants.Log1
										.WriteLog("CommunicationScheduler:PreLink.GetSocketUpChannel().close IOError,error is "
												+ ex2.toString());
							}
						}
						FepLink.SetSocketUpChannel(channel); //将上行通道的链路修改为本次连接的链路
						PreCommList.set(i, FepLink);
						newLink = false;
						CommunicationServerConstants.Trc1
								.TraceLog("11、Update Pre Info :"
										+ FepType
										+ " HostAddress:"
										+ channel.socket().getInetAddress()
												.getHostAddress());
					}
				}
			}
			if (newLink) { //没有找到相应的链路信息，即是新增的链路信息，根据链路类型修改对应的链路
				//if (LinkType == 10) {
				l.SetSocketUpChannel(channel);
				//}
				//else if (LinkType == 20) {
				//  l.SetSocketDownChannel(channel);
				//}
				PreCommList.add(l);
				CommunicationServerConstants.Trc1.TraceLog("11、Add Pre Info :"
						+ FepType + " HostAddress:"
						+ channel.socket().getInetAddress().getHostAddress());
			}
			CommunicationServerConstants.Trc1
					.TraceLog("12、RegisterPreInfo PreType:"
							+ FepType
							+ " HostAddress:"
							+ channel.socket().getInetAddress()
									.getHostAddress());
		} catch (Exception ex) {
			CommunicationServerConstants.Log1
					.WriteLog("CommunicationScheduler:RegisterPreInfo() Error,error is "
							+ ex.toString());
		}
	}

	private void processData(int CommandID, SocketChannel channel,
			String ReceiveData, byte[] Data) {
		switch (CommandID) { //根据命令类型不同分别处理
		case 1: { //前置机通道注册
			CommunicationServerConstants.Trc1.TraceLog("  RegisterFep "
					+ ReceiveData + channel.toString());
			RegisterFepInfo(ReceiveData, channel); //注册通道，加入队列
			CommunicationServerConstants.Trc1.TraceLog("  "
					+ FepCommList.size());
			UpdateLastCommDate(channel); //更新最近通讯时间
			AckMessage(ReceiveData.substring(10, 16), ReceiveData, channel); //返回确认消息
			CommunicationServerConstants.Trc1.TraceLog("  AckMessage "
					+ ReceiveData + channel.toString());
			break;
		}
		case 2: { //前置机提交的消息
			UpdateLastCommDate(channel);
			AckMessage(ReceiveData.substring(10, 16), ReceiveData, channel);
			AnalyzeCommandContent(ReceiveData, CommandID, Data, channel); //分析命令内容并处理
			break;
		}
		case 3: { //下发命令的返回确认
			UpdateLastCommDate(channel);
			AnalyzeCommandContent(ReceiveData, CommandID, Data, channel);
			break;
		}
		case 17: { //前置机批量提交的消息
			UpdateLastCommDate(channel);
			AckMessage(ReceiveData.substring(10, 16), ReceiveData, channel);
			AnalyzeCommandContent(ReceiveData, CommandID, Data, channel); //分析命令内容并处理
			break;
		}

		case 257: { //加密机、读卡器请求连接的消息0x00000101      
			CommunicationServerConstants.Trc1.TraceLog("  RegisterPre "
					+ ReceiveData + channel.toString());
			RegisterPreInfo(ReceiveData, channel); //注册通道，加入队列
			UpdateLastCommDateOfPre(channel); //更新最近通讯时间
			CommunicationServerConstants.Trc1.TraceLog("  "
					+ PreCommList.size());
			AckMessage(ReceiveData.substring(10, 16), ReceiveData, channel); //返回确认消息
			CommunicationServerConstants.Trc1.TraceLog("  AckMessage "
					+ ReceiveData + channel.toString());
			break;
		}
		case 258: //加密机提交的消息0x00000102
		case 261: { //读卡器提交的消息0x00000105
			UpdateLastCommDateOfPre(channel);
			AckMessage(ReceiveData.substring(10, 16), ReceiveData, channel);
			AnalyzePreCommandContent(ReceiveData, CommandID, Data, channel); //分析加密机、读卡器命令内容并处理
			break;
		}
		default: {
			break;
		}
		}
	}

	private void FepLinkDisconnect(SocketChannel socketChannel) {
		try {
			for (int j = 0; j < FepCommList.size(); j++) {
				FepLinkList l = (FepLinkList) FepCommList.get(j);
				if ((l.GetSocketUpChannel() != null)
						&& (l.GetSocketUpChannel() == socketChannel)) {
					socketChannel.close();
					socketChannel.socket().close();
					l.SetSocketUpChannel(null);
					CommunicationServerConstants.Trc1
							.TraceLog("17、Set Null Fep Info :"
									+ l.GetFepType()
									+ " LinkType:10 HostAddress:"
									+ socketChannel.socket().getInetAddress()
											.getHostAddress());
					if (l.GetSocketDownChannel() == null) {
						FepCommList.remove(l);
						CommunicationServerConstants.Trc1
								.TraceLog("FepChannel removed from FepLinkList,FepChannel Type is "
										+ l.GetFepType());
					}
					break;
				} else if ((l.GetSocketDownChannel() != null)
						&& (l.GetSocketDownChannel() == socketChannel)) {
					socketChannel.close();
					socketChannel.socket().close();
					l.SetSocketDownChannel(null);
					CommunicationServerConstants.Trc1
							.TraceLog("17、Set Null Fep Info :"
									+ l.GetFepType()
									+ " LinkType:20 HostAddress:"
									+ socketChannel.socket().getInetAddress()
											.getHostAddress());
					if (l.GetSocketUpChannel() == null) {
						FepCommList.remove(l);
						CommunicationServerConstants.Trc1
								.TraceLog("FepChannel removed from FepLinkList,FepChannel Type is "
										+ l.GetFepType());
					}
					break;
				}
			}
		} catch (Exception ex) {
		}
	}

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
			} catch (IOException ex1) {
				count = -1;
			}
			if (count == -1) { //前置机的socket通道出现异常时，也需要处理链路队列，保证队列中的数据正确
				FepLinkDisconnect(socketChannel);
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
				buffer[1] = ByteBuffer.allocateDirect(Integer.parseInt(
						mb.TotalLength, 16) - 12); //用于接收的缓冲
				count = socketChannel.read(buffer, 1, 1);
				if (count > 0) {
					buffer[1].flip();
					buffer[1].get(data, 12, buffer[1].limit());
					String strMessbody = utils.bytes2str(data, Integer
							.parseInt(mb.TotalLength, 16));
					processData(Integer.parseInt(mb.CommandID, 16),
							socketChannel, strMessbody, data);
					//对于有效的报文处理后，退出本次数据的读取处理过程，如果还有数据需要处理会再次进入本函数处理
				}
			}
			System.gc();
		} catch (Exception ex3) {
			CommunicationServerConstants.Log1
					.WriteLog("CommunicationScheduler:receiveData() IOerror,error is "
							+ ex3.toString());
		}
	}

	private void jbInit() throws Exception {
	}

	class SMSCommitThread extends Thread {
		public void run() {
			while (true) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	class ListenThread extends Thread { //监听线程，负责处理socket的监听和消息的接收
		public void run() {
			while (true) {
				try {
					int n = 0;
					try {
						n = sc.selector.selectNow();
					} catch (Exception ex) {
						CommunicationServerConstants.Log1
								.WriteLog("CommunicationScheduler:ListenThread sc.selector.select() IOerror,error is "
										+ ex.toString());
					}
					if (n == 0) {
						try {
							Thread.sleep(1);
						} catch (InterruptedException ex3) {
						}
						continue;
					} else if (n != 0) {
						Iterator it = sc.selector.selectedKeys().iterator();
						while (it.hasNext()) {
							try {
								SelectionKey key = (SelectionKey) it.next();
								if (key.isValid() && key.isAcceptable()) { //socket建立成功
									ServerSocketChannel server = (ServerSocketChannel) key
											.channel();
									SocketChannel channel = null;
									try {
										channel = server.accept();
									} catch (IOException ex1) {
										CommunicationServerConstants.Log1
												.WriteLog("CommunicationScheduler:ListenThread server.accept() IOerror,error is "
														+ ex1.toString());
									}
									//注册本次连接的通道信息
									registerChannel(sc.selector, channel,
											SelectionKey.OP_READ);
								}
								if (key.isValid() && key.isReadable()) { //Server端主要处理接收消息的事件，发送数据由接口部分出发或者定时发送
									//接收消息的事件
									receiveData(key);
								}

								it.remove();
								try {
									Thread.sleep(1);
								} catch (InterruptedException ex3) {
								}
							} catch (Exception ex5) {
								CommunicationServerConstants.Log1
										.WriteLog("CommunicationScheduler:ListenThreadRun() error,error is "
												+ ex5.toString());
							}
						}
					}
				} catch (Exception ex2) {
					CommunicationServerConstants.Log1
							.WriteLog("ListenThread Error:" + ex2.toString());
				}
			}
		}
	}

	class SaveUpAndDownRecordThread extends Thread { //上下行源始保存线程	
		public void run() {
			while (true) {
				try {
					if (GlobalUPRecordSaveList.size() >= BatchSaveNum) {
						for (int i = 0; i < BatchSaveNum; i++) {
							UpFlowRecordSaveList.add(GlobalUPRecordSaveList
									.get(i));
						}
						//   CommunicationServerConstants.Log1.WriteLog("SaveUpFlowRecordToDB");
						da_SaveRecord
								.SaveUpFlowRecordToDB(UpFlowRecordSaveList);
						UpFlowRecordSaveList.clear();
						for (int i = 0; i < BatchSaveNum; i++) {
							GlobalUPRecordSaveList.remove(0);
						}
					}
					if (GlobalDownRecordSaveList.size() >= BatchSaveNum) {
						for (int i = 0; i < BatchSaveNum; i++) {
							DownFlowRecordSaveList.add(GlobalDownRecordSaveList
									.get(i));
						}
						da_SaveRecord
								.SaveDownFlowRecordToDB(DownFlowRecordSaveList);
						DownFlowRecordSaveList.clear();
						for (int i = 0; i < BatchSaveNum; i++) {
							GlobalDownRecordSaveList.remove(0);
						}
					}
					try {
						Thread.sleep(1);
					} catch (InterruptedException ex3) {
					}
				} catch (Exception ex) {
					CommunicationServerConstants.Log1
							.WriteLog("CommunicationScheduler:SaveUpAndDownRecordThreadRun() error,error is "
									+ ex.toString());
				}
			}
		}
	}

	class SendDLMSThread extends Thread {//DLMS send thread
		public void run() {
			while (true) {
				try {
					SendDLMSData();
					try {
						Thread.sleep(1);
					} catch (InterruptedException ex3) {
					}
				} catch (Exception ex) {
					CommunicationServerConstants.Log1
							.WriteLog("CommunicationScheduler:SendDLMSThreadRun() error,error is "
									+ ex.toString());
				}
			}
		}

		private void SendDLMSData() {
			try {
				int iSize = 0;//, ReSendTimes = 0;
				iSize = CommunicationServerConstants.GlobalDLMSSendList.size();
				Calendar SendTime = Calendar.getInstance();
				StructDLMSSendData sdDLMS = null;
				StructSendData sd = new StructSendData();
				String MessageContent = "";
				while (iSize > 0) {
					//  if (DLMSAckBack) {
					//			          ReSendTimes = 0;
					sdDLMS = (StructDLMSSendData) CommunicationServerConstants.GlobalDLMSSendList
							.get(0);
					MessageContent = "";
					switch (sdDLMS.DealStep) {
					case 10://AARE
						if (sdDLMS.SecurityLevel == 0) {
							MessageContent = "000100100001001F601DA109060760857405080101BE0F040D01000000065F0400000019FFFF";
							sdDLMS.DealStep = 13;
						} else if (sdDLMS.SecurityLevel == 1) {
							String sData = "8002"
									+ Encode
											.StrStuff("0", 4, sdDLMS.ProVer, 10);
							sData = sData + "A109060760857405080101";
							sData = sData
									+ "8A02"
									+ Encode
											.StrStuff("0", 4, sdDLMS.ProVer, 10);
							sData = sData + "8B0760857405080201";
							sData = sData
									+ "AC0A8008"
									+ Encode.StringtoHexASCII(Encode.StrStuff(
											"0", 8, sdDLMS.Password, 10));
							sData = sData
									+ "BE0F040D01000000065F0400000019FFFF";
							sData = "60"
									+ Encode.IntToHex(
											"" + (sData.length() / 2), "00")
									+ sData;
							MessageContent = "000100100001"
									+ Encode.IntToHex(
											"" + (sData.length() / 2), "0000")
									+ sData;
							sdDLMS.DealStep = 13;
						} else if (sdDLMS.SecurityLevel == 2) {
							String sData = "8002"
									+ Encode
											.StrStuff("0", 4, sdDLMS.ProVer, 10);
							sData = sData + "A109060760857405080101";
							sData = sData
									+ "8A02"
									+ Encode
											.StrStuff("0", 4, sdDLMS.ProVer, 10);
							sData = sData + "8B0760857405080202";
							String sRandom = Encode.getRandom(32);
							sData = sData + "AC128010" + sRandom.toUpperCase();
							sData = sData
									+ "BE0F040D01000000065F0400000019FFFF";
							sData = "60"
									+ Encode.IntToHex(
											"" + (sData.length() / 2), "00")
									+ sData;
							MessageContent = "000100100001"
									+ Encode.IntToHex(
											"" + (sData.length() / 2), "0000")
									+ sData;
							sdDLMS.DealStep = 11;
						}
						sdDLMS.bHaveSend = false;
						break;
					case 12://StoC
						String enString = "";
						AES aes = new AES();
						String text = sdDLMS.Random;
						String key = Encode.StrStuff("0", 32, sdDLMS.Password,
								10);
						aes.setText(text.toLowerCase(), true);
						aes.setKeyLengthIndex(0);
						aes.setKey(key, true);
						enString = aes.Cipher().toUpperCase();
						MessageContent = "000100100001001F"
								+ "C30181000F0000280000FF01010910" + enString;
						sdDLMS.DealStep = 13;
						sdDLMS.bHaveSend = false;
						break;
					case 20://Send Data
						MessageContent = sdDLMS.MessageContent;
						sdDLMS.DealStep = 21;
						sdDLMS.bHaveSend = false;
						break;
					case 22://Have next
						MessageContent = "0001001000010007"
								+ "C00281"
								+ Encode.IntToHex("" + sdDLMS.DataBlock,
										"00000000");
						sdDLMS.DealStep = 21;
						sdDLMS.bHaveSend = false;
						break;
					case 30://Finish
						CommunicationServerConstants.GlobalDLMSSendList
								.remove(0);
						break;
					}
					if (sdDLMS.DealStep % 2 != 0 && !sdDLMS.bHaveSend) {
						sd.ArithmeticNo = sdDLMS.ArithmeticNo;
						sd.CommandSeq = sdDLMS.CommandSeq;
						sd.MessageContent = MessageContent;
						sd.MessageLength = MessageContent.length();
						sd.MessageType = sdDLMS.MessageType;
						sd.MobileNo = sdDLMS.MobileNo;
						sd.SendDirect = sdDLMS.SendDirect;
						sd.TerminalAddress = sdDLMS.TerminalAddress;
						sd.TerminalCommunication = sdDLMS.TerminalCommunication;
						sd.YXJ = sdDLMS.YXJ;
						CommunicationServerConstants.Trc1
								.TraceLog("07、DLMSDownFlow Content is TXLX:"
										+ sd.TerminalCommunication + " SJCD:"
										+ sd.MessageLength + " ZDLJDZ:"
										+ sd.TerminalAddress);
						SendDownFlowToFep(sd);
						sdDLMS.bHaveSend = true;
						SendTime = Calendar.getInstance();
						SendTime.add(Calendar.SECOND, 3);
					}
					/*  }else if (SendTime.before(Calendar.getInstance()) && (ReSendTimes < 1)) {					  
					 if (sd != null) {
					 ReSendTimes = ReSendTimes + 1;
					 SendDownFlowToFep(sd);
					 SendTime = Calendar.getInstance();
					 SendTime.add(Calendar.MILLISECOND, 50);
					 CommunicationServerConstants.Trc1.TraceLog("Resend frame:" + sd.MessageContent +
					 " TerminalInfo " + sd.TerminalAddress + " " +
					 sd.TerminalCommunication);
					 }else {
					 DLMSAckBack = true;
					 iSize = CommunicationServerConstants.GlobalDLMSSendList.size();
					 }
					 }else */if (SendTime.before(Calendar.getInstance())) {
						iSize = CommunicationServerConstants.GlobalDLMSSendList
								.size();
						CommunicationServerConstants.GlobalDLMSSendList
								.remove(0);
					}
					try {
						Thread.sleep(1);
					} catch (InterruptedException ex) {
					}
				}
			} catch (Exception ex2) {
				CommunicationServerConstants.Log1
						.WriteLog("CommunicationScheduler:Process GlobalDLMSSendList Error,error is "
								+ ex2.toString());
			}
		}
	}

	class Send230MThread extends Thread { //230M发送线程
		public void run() {
			while (true) {
				try {
					if (CommunicationServerConstants.Global230MSendList.size() > 0) {
						StructSendData sd = (StructSendData) CommunicationServerConstants.Global230MSendList
								.get(0);
						if (DataSend(sd)) { //数据返回或者等待超时
							CommunicationServerConstants.Global230MSendList
									.remove(sd);
						}
					}
					try {
						Thread.sleep(1);
					} catch (InterruptedException ex3) {
					}
				} catch (Exception ex) {
					CommunicationServerConstants.Log1
							.WriteLog("CommunicationScheduler:Send230MThreadRun() error,error is "
									+ ex.toString());
				}
			}
		}

		public boolean DataSend(StructSendData SendData) { //发送230M数据
			boolean result = true;
			try {
				try {
					CommunicationServerConstants.Trc1
							.TraceLog("44、SendDownFlowBy230M Content is "
									+ SendData.TerminalCommunication + " "
									+ SendData.MessageLength + " "
									+ SendData.TerminalAddress + " "
									+ SendData.MessageContent);
					SendDownFlowToFep(SendData); //此处不进行消息的等待或者超时处理，由230M通道处理消息的返回、超时等
				} catch (Exception ex) {
					CommunicationServerConstants.Log1
							.WriteLog("CommunicationScheduler:Send230MThreadDataSend() error,error is "
									+ ex.toString());
				}
			} finally {
			}
			return result;
		}
	}

	class SwitchMPInfo { //MP info which have backup connector 
		public String CLDBH = "";

		public int CLDXH = 0;
		
		public String CLDDZ = "";

		public String MAddress = "";//main connector address

		public String BAddress = "";//backup connector address
	}

	class TerminalInfo { //终端逻辑地址和物理地址的对应关系结构
		public String TerminalAddress = ""; //终端逻辑地址

		public int TerminalProtocol = 0; //终端规约号

		public String TerminalPassWord = "8888"; //终端密码

		public int TerminalCodingNo = 0; //终端密码算法

		public int TerminalCurrent = 0; //终端当前通讯方式

		public int TerminalProperty = 0; //终端属性，判断是01 CDMA终端 04 CDMA模块表 02 GPRS终端 05GPRS模块表 。。。。。

		public int CommandIndex = 0; //命令序号

		public String TerminalSIMID = ""; //终端SIM卡号码

		public String SIMCommunicationIP = ""; //短信前置机的IP

		public String SIMLocalAddress = ""; //前置机短信通道地址

		public String COMCommunicationIP = ""; //串口通讯的前置机IP

		public String TerminalCOMAddress = ""; //终端串口通讯地址

		public String COMLocalAddress = ""; //前置机串口通道地址

		public String RadioCommunicationIP = ""; //230通讯的前置机IP

		public String TerminalUDPAddress = ""; //终端UDP的物理地址

		public String UDPCommunicationIp = ""; //UDP前置机的IP

		public String UDPLocalAddress = ""; //前置机UDP通道地址

		public String TerminalTCPAddress = ""; //终端TCP的物理地址

		public String TCPCommunicationIp = ""; //TCP前置机的IP

		public String TCPLocalAddress = ""; //前置机TCP通道地址

		public int HeartInterval = 0; //终端心跳间隔，单位为s

		public Calendar LastLinkTime; //终端上次通讯时间，用于判断是否断线

		public int OnLineStatus = 20; //终端在线状态，10表示在线，20表示断线

		public boolean OnLineChanged = false; //终端在线状态是否更改

		public boolean CommuniParamChanged = false; //通讯参数修改标志
	}

	class TerminalOnLineStatus { //终端在线状态结构，用于返回到上层
		public String TerminalAddress; //终端逻辑地址

		public int OnLineStatus; //在线状态，10：在线，20：掉线
	}
}

class FepLinkList { //前置机链路信息
	private SocketChannel UpChannel = null; //上行的socket链路

	private SocketChannel DownChannel = null; //下行的socket链路

	private Calendar LastCommDate = null; //最后通讯时间

	private int FepType = 0; //前置机通道的类型

	private int LinkType = 0; //连接类型，仅在注册时用到

	private boolean RegFlag = false; //链路是否注册的标志

	public boolean ConnectFlag = true; //下行链路是否能够通讯的标志

	//下面就是对于以上参数的读取、设置的函数
	public void SetSocketUpChannel(SocketChannel Channel) {
		this.UpChannel = Channel;
	}

	public SocketChannel GetSocketUpChannel() {
		try {
			return this.UpChannel;
		} catch (Exception ex) {
			return null;
		}
	}

	public void SetSocketDownChannel(SocketChannel Channel) {
		this.DownChannel = Channel;
	}

	public SocketChannel GetSocketDownChannel() {
		try {
			return this.DownChannel;
		} catch (Exception ex) {
			return null;
		}
	}

	public void SetLastCommDate(Calendar c) {
		this.LastCommDate = c;
	}

	public Calendar GetLastCommDate() {
		return this.LastCommDate;
	}

	public void SetRegFlag(boolean Flag) {
		this.RegFlag = Flag;
	}

	public boolean GetRegFlag() {
		return this.RegFlag;
	}

	public void SetFepType(int FepType) {
		this.FepType = FepType;
	}

	public int GetFepType() {
		return this.FepType;
	}

	public void SetLinkType(int LinkType) {
		this.LinkType = LinkType;
	}

	public int GetLinkType() {
		return this.LinkType;
	}
}

class PreLinkList { //前置机链路信息
	private SocketChannel UpChannel = null; //上行的socket链路

	private Calendar LastCommDate = null; //最后通讯时间

	private int FepType = 0; //前置机通道的类型  100 海兴加密机110国网加密机 120读卡器

	private boolean RegFlag = false; //链路是否注册的标志

	public boolean ConnectFlag = true; //链路是否能够通讯的标志

	//下面就是对于以上参数的读取、设置的函数
	public void SetSocketUpChannel(SocketChannel Channel) {
		this.UpChannel = Channel;
	}

	public SocketChannel GetSocketUpChannel() {
		try {
			return this.UpChannel;
		} catch (Exception ex) {
			return null;
		}
	}

	public void SetLastCommDate(Calendar c) {
		this.LastCommDate = c;
	}

	public Calendar GetLastCommDate() {
		return this.LastCommDate;
	}

	public void SetRegFlag(boolean Flag) {
		this.RegFlag = Flag;
	}

	public boolean GetRegFlag() {
		return this.RegFlag;
	}

	public void SetFepType(int FepType) {
		this.FepType = FepType;
	}

	public int GetFepType() {
		return this.FepType;
	}
}

class MessageBody {
	public String TotalLength;

	public String CommandID;

	public String SeqID;
}

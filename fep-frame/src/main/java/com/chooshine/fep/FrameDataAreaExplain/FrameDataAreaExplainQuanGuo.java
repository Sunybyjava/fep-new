package com.chooshine.fep.FrameDataAreaExplain;

import java.util.ArrayList;
import java.util.Calendar;
import java.net.URI;
import java.text.SimpleDateFormat;
import com.chooshine.fep.ConstAndTypeDefine.*;

public class FrameDataAreaExplainQuanGuo { //国网数据区解帧类
	//存储方式
	private final int CCFS_BCD = 10; //BCD方式

	private final int CCFS_HEX = 20; //十六进制方式

	private final int CCFS_ASC = 30; //ASCII码方式

	//存储顺序
	private final int CCSX_SX = 10; //顺序存储

	private final int CCSX_NX = 20; //逆序存储

	//数据类型
	private final int c_Float = 10; //浮点型

	private final int c_DateTime = 20; //日期型

	private final int c_String = 30; //字符型

	//private final int c_NOLenStr = 40; //不定长字符型
	private final int c_Complex = 50; //复合数据类型

	private final int c_FloatEx = 60; //特殊浮点型（对应国网规范格式2,带幂部）

	private final int c_FloatFH = 70; //带正负号的浮点型 (0为正号或是上浮，1为负号或是下浮)

	//private final int c_DateTimeXQ = 80; //带星期的日期型2004-10-10 10：10：10#4
	private final int c_IntDWFH = 90; //带单位标志和正负号的整型值串

	//private final int c_IntZJDWFH = 91; //带追加,单位标志和正负号的整型值串 8月22号增加

	private final int MAX_LENGTH_FHCOUNT = 120; //负荷数据点数最大值

	SPE_CommandInfoList TermialQuanGuoCommandInfoList = new SPE_CommandInfoList(); //终端全国命令信息队列

	SPE_CommandInfoList TermialTianJinCommandInfoList = new SPE_CommandInfoList(); //终端天津模块表命令信息队列

	SPE_CommandInfoList Termial698CommandInfoList = new SPE_CommandInfoList(); //终端698命令信息队列

	SPE_TaskInfoList QuanGuoTaskInfoList = new SPE_TaskInfoList(); //国网任务信息队列

	SPE_TaskInfoList QuanGuo698TaskInfoList = new SPE_TaskInfoList(); //国网任务信息队列

	FrameDataAreaExplainGluMethod GluMethodQuanGuo = new FrameDataAreaExplainGluMethod(); //数据区解析公用类

	public FrameDataAreaExplainQuanGuo(String path) {
		try {
			InitialList(path,null);
		} catch (Exception e) {
			Glu_ConstDefine.Log1.WriteLog(e.toString());
		}
	}

	public FrameDataAreaExplainQuanGuo(URI ur) {
		try {
			InitialList("",ur);
		} catch (Exception e) {
			Glu_ConstDefine.Log1.WriteLog(e.toString());
		}
	}

	public boolean InitialList(String path,URI ur) {
		boolean Result = false;
		try {
			try {
				/*Calendar cLogTime=Calendar.getInstance();
				 SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				 String KSSJ="";
				 String JSSJ="";
				 cLogTime=Calendar.getInstance();
				 KSSJ=formatter.format(cLogTime.getTime());
				 System.out.println("开始时间："+KSSJ);*/
				Glu_DataAccess DataAccess;
				if (Glu_ConstDefine.TerminalQuanGuoSupport) {					
					if (ur == null) {
						DataAccess = new Glu_DataAccess(path);
					} else {
						DataAccess = new Glu_DataAccess(ur);
					}
					DataAccess.LogIn(0);
					TermialQuanGuoCommandInfoList = DataAccess
							.GetCommandInfoList(100); //初始化终端全国命令信息队列
					QuanGuoTaskInfoList = DataAccess.GetTaskInfoList(100); //初始化任务信息队列
					DataAccess.LogOut(0);
				}
				if (Glu_ConstDefine.TerminalTianJinSupport) {
					if (ur == null) {
						DataAccess = new Glu_DataAccess(path);
					} else {
						DataAccess = new Glu_DataAccess(ur);
					}
					DataAccess.LogIn(0);
					TermialTianJinCommandInfoList = DataAccess
							.GetCommandInfoList(105); //初始化终端全国命令信息队列
					DataAccess.LogOut(0);
				}
				if (Glu_ConstDefine.Terminal698Support) {
					if (ur == null) {
						DataAccess = new Glu_DataAccess(path);
					} else {
						DataAccess = new Glu_DataAccess(ur);
					}
					DataAccess.LogIn(0);
					Termial698CommandInfoList = DataAccess
							.GetCommandInfoList(Glu_ConstDefine.GY_ZD_698); //初始化终端698命令信息队列
					QuanGuo698TaskInfoList = DataAccess
							.GetTaskInfoList(Glu_ConstDefine.GY_ZD_698); //初始化任务信息队列
					DataAccess.LogOut(0);
				}

				/*cLogTime=Calendar.getInstance();
				 JSSJ=formatter.format(cLogTime.getTime());
				 System.out.println("结束时间："+JSSJ);*/
			} catch (Exception e) {
				Glu_ConstDefine.Log1
						.WriteLog("Func:FrameDataAreaExplainQuanGuo__InitialList();Error:"
								+ e.toString());
			}
		} finally {
		}
		return Result;
	}

	public SFE_DataListInfo ExplainNormalDataAreaQuanGuo( //普通数据区解释
			String FrameDataArea, int TermialProtocolType, String ControlCode) { //帧数据区,终端规约,功能码
		SFE_DataListInfo DataListInfo = new SFE_DataListInfo();
		ArrayList<SFE_NormalData> NormalDataList = new ArrayList<SFE_NormalData>();
		try {
			try {
				SFE_NormalData NormalData = new SFE_NormalData();
				SFE_NormalDataTemp NormalDataTemp = new SFE_NormalDataTemp();
				SPE_CommandInfoList CommandInfoList = new SPE_CommandInfoList();
				ArrayList CommandList = new ArrayList();
				int[] MeasuredSignList;
				boolean IsBreak = false;
				String sDataCaption = "", sDA = "", sDT = "";
				if (ControlCode.equals("0A") || ControlCode.equals("0C")
						|| ControlCode.equals("09") || ControlCode.equals("C2")) {
					if (TermialProtocolType == Glu_ConstDefine.GY_ZD_QUANGUO) { //终端全国
						CommandInfoList = TermialQuanGuoCommandInfoList;
					}/* else if (TermialProtocolType == Glu_ConstDefine.GY_ZD_TIANJIN) { //终端天津模块表
					 CommandInfoList = TermialTianJinCommandInfoList;
					 } */else if (TermialProtocolType == Glu_ConstDefine.GY_ZD_698) { //终端698规约
						CommandInfoList = Termial698CommandInfoList;
					}
					if (ControlCode.equals("0A")) { //参数查询
						ControlCode = "04";
					}
					while (FrameDataArea.length() >= 8 && IsBreak == false) { //无数据体时至少还4个字节(数据单元标识)
						sDA = FrameDataArea.substring(0, 4);
						sDT = FrameDataArea.substring(4, 8);
						MeasuredSignList = GluMethodQuanGuo
								.ExplainMeasuredPointList(sDA,
										TermialProtocolType); //解释信息点得到测量点列表
						CommandList = GluMethodQuanGuo.ExplainCommandList(sDT,
								ControlCode); //解释信息类得到命令列表
						FrameDataArea = FrameDataArea.substring(8,
								FrameDataArea.length()); //消去数据单元标识
						int iFn = Integer.parseInt(CommandList.get(0)
								.toString().substring(1, 4)); //用第一个命令的Fn来判断全部命令的数据类型
						int iMeasuredType = 10, iCount = 0;
						if (ControlCode.equals("0C")
								&& ((iFn >= 81 && iFn <= 84) || (iFn >= 17 && iFn <= 24))) {
							iMeasuredType = 40; //总加组
						}
						if (MeasuredSignList.length == 0) { //测量点没有解释出来
							DataListInfo.ExplainResult = 50020; //传入数据长度非法
							break;
						}
						for (int i = 0; i < MeasuredSignList.length; i++) {
							if (IsBreak) {
								break;
							}
							NormalData.SetMeasuredPointNo(MeasuredSignList[i]);
							NormalData.SetMeasuredPointType(iMeasuredType);

							for (int j = 0; j < CommandList.size(); j++) {
								sDataCaption = CommandList.get(j).toString();
								NormalDataTemp = GluMethodQuanGuo
										.CommandSearchAndExplain(FrameDataArea,
												sDataCaption, CommandInfoList,
												TermialProtocolType, iCount,
												iFn);
								if (NormalDataTemp.GetCommandLen() > 0
										|| sDataCaption.equals("000000")) { //000000表示无召测数据项
									FrameDataArea = FrameDataArea.substring(
											NormalDataTemp.GetCommandLen(),
											FrameDataArea.length());
								} else if (NormalDataTemp.GetCommandLen() == 0) { //找不到命令
									DataListInfo.ExplainResult = 50010; //数据项在规约里不能全部被支持
									IsBreak = true;
									break;
								} else {
									DataListInfo.ExplainResult = 50020; //传入数据长度非法
									IsBreak = true;
									break;
								}
								for (int k = 0; k < NormalDataTemp.DataItemList
										.size(); k++) { //把解释后的数据保存在中间对象里
									NormalData.DataItemList
											.add((SFE_DataItem) NormalDataTemp.DataItemList
													.get(k));
									NormalData.DataItemCountAdd();
								}
							}
							NormalDataList.add(NormalData);
							NormalData = new SFE_NormalData();
						}
						MeasuredSignList = null; //清空数组内容
						CommandList.clear(); //清空队列内容
					}
				}
			} catch (Exception e) {
				Glu_ConstDefine.Log1
						.WriteLog("Func:FrameDataAreaExplainQuanGuo__ExplainNormalDataAreaQuanGuo();Error:"
								+ e.toString());
			}
		} finally {
		}
		DataListInfo.DataType = 10;
		DataListInfo.DataList = NormalDataList;
		return DataListInfo;
	}

	public SFE_DataListInfo ExplainSetResultDataAreaQuanGuo( //国网设置返回区解释
			String FrameDataArea, int TermialProtocolType, String ControlCode) { //帧数据区,终端规约,控制码
		SFE_DataListInfo DataListInfo = new SFE_DataListInfo();
		ArrayList<SFE_SetResultData> SetResultDataList = new ArrayList<SFE_SetResultData>();
		try {
			try {
				SFE_SetResultData SetResultData = new SFE_SetResultData();
				ArrayList CommandList = new ArrayList();
				int[] MeasuredSignList;
				String sDataCaption = "", sDA = "", sDT = "";
				//int iMeasured = 0;
				int iSetResult = -1;
				int Fn = Integer.parseInt(FrameDataArea.substring(4, 6), 16) & 7; //1:全部确认;2:全部否认;4:部分确认部分否认
				FrameDataArea = FrameDataArea.substring(8, FrameDataArea
						.length()); //消去数据单元标识
				if (Fn == 1 || Fn == 2) {
					SetResultData.SetMeasuredPointNo(0);
					SetResultData.SetMeasuredPointType(10);
					sDataCaption = "000" + ("" + Fn) + "00"; //000100:全部确认;000200:全部否认
					SetResultData.DataItemAdd(sDataCaption, iSetResult);
					SetResultDataList.add(SetResultData);
				} else { //部分确认部分否认
					ControlCode = FrameDataArea.substring(0, 2); //功能码
					FrameDataArea = FrameDataArea.substring(2, FrameDataArea
							.length()); //消去功能码
					while (FrameDataArea.length() > 0) {
						sDA = FrameDataArea.substring(0, 4);
						sDT = FrameDataArea.substring(4, 8);
						iSetResult = Integer.parseInt(FrameDataArea.substring(
								8, 10), 16); //错误码
						MeasuredSignList = GluMethodQuanGuo
								.ExplainMeasuredPointList(sDA,
										TermialProtocolType); //解释信息点得到测量点列表
						CommandList = GluMethodQuanGuo.ExplainCommandList(sDT,
								ControlCode); //解释信息类得到命令列表
						FrameDataArea = FrameDataArea.substring(10,
								FrameDataArea.length()); //消去数据单元标识和错误码
						for (int i = 0; i < MeasuredSignList.length; i++) {
							SetResultData = new SFE_SetResultData();
							SetResultData
									.SetMeasuredPointNo(MeasuredSignList[i]);
							SetResultData.SetMeasuredPointType(10);
							for (int j = 0; j < CommandList.size(); j++) {
								SetResultData.DataItemAdd(CommandList.get(j)
										.toString(), iSetResult);
							}
							SetResultDataList.add(SetResultData);
						}
					}
				}

			} catch (Exception e) {
				Glu_ConstDefine.Log1
						.WriteLog("Func:FrameDataAreaExplainQuanGuo__ExplainSetResultDataAreaQuanGuo();Error:"
								+ e.toString());
			}
		} finally {
		}
		DataListInfo.ExplainResult = 0;
		DataListInfo.DataType = 40;
		DataListInfo.DataList = SetResultDataList;
		return DataListInfo;
	}

	public SFE_DataListInfo ExplainHistoryDataAreaQuanGuo(
			//历史数据区解释(一二类历史数据)
			String FrameDataArea, int TermialProtocolType, String ControlCode,
			int FrameType) { //帧数据区,终端规约,功能码,帧类型(主动上送还是召测返回)
		SFE_DataListInfo DataListInfo = new SFE_DataListInfo();
		ArrayList<SFE_HistoryData> HistoryDataList = new ArrayList<SFE_HistoryData>();
		try {
			try {
				SFE_NormalDataTemp NormalDataTemp = new SFE_NormalDataTemp();
				SPE_CommandInfoList CommandInfoList = new SPE_CommandInfoList();
				SFE_HistoryData[] HistoryDataListTemp = new SFE_HistoryData[MAX_LENGTH_FHCOUNT]; //数据点数最大值=24
				for (int i = 0; i < MAX_LENGTH_FHCOUNT; i++) {
					HistoryDataListTemp[i] = new SFE_HistoryData();
				}
				ArrayList CommandList = new ArrayList();
				ArrayList<String> CommandListTemp = new ArrayList<String>();
				int[] MeasuredSignList;
				String sDataCaption = "", sDA = "", sDT = "", sTaskDateTimeInfo = "", sTaskDateTime = "";
				int iFn = 0, iDataType = 0, iDataDensity = 0, iDataCount = 1, iMeasuredType = 10, iCount = 0;
				;
				if (TermialProtocolType == Glu_ConstDefine.GY_ZD_698) {
					CommandInfoList = Termial698CommandInfoList;
				} else {
					CommandInfoList = TermialQuanGuoCommandInfoList;
				}
				while (FrameDataArea.length() >= 8) { //无数据体时至少还4个字节(数据单元标识)
					sDA = FrameDataArea.substring(0, 4);
					sDT = FrameDataArea.substring(4, 8);
					if (TermialProtocolType == Glu_ConstDefine.GY_ZD_698) {
						iDataType = GluMethodQuanGuo.GetQuanGuo698DataType(sDT,
								ControlCode); //得到数据类型
					} else {
						iDataType = GluMethodQuanGuo.GetQuanGuoDataType(sDT,
								ControlCode); //得到数据类型
					}

					if (ControlCode.equals("C0")) { //对自定义命令特殊处理:除了消去数据单元还要把厂家代码'38303030'消去
						ControlCode = "0G";
						FrameDataArea = FrameDataArea.substring(16,
								FrameDataArea.length());
					} else {
						FrameDataArea = FrameDataArea.substring(8,
								FrameDataArea.length()); //消去数据单元标识
					}
					MeasuredSignList = GluMethodQuanGuo
							.ExplainMeasuredPointList(sDA, TermialProtocolType); //解释信息点得到测量点列表
					CommandList = GluMethodQuanGuo.ExplainCommandList(sDT,
							ControlCode); //解释信息类得到命令列表
					if (CommandList.get(0).toString().equals("000000")) { //无效数据
						continue;
					}
					iFn = Integer.parseInt(CommandList.get(0).toString()
							.substring(1, 4)); //用第一个命令的Fn来判断全部命令的数据类型
					if ((ControlCode.equals("0D") && ((iFn >= 73 && iFn <= 76) || (iFn >= 57 && iFn <= 66)))
							|| (ControlCode.equals("0C") && (iFn >= 81 && iFn <= 84))) {
						iMeasuredType = 40; //总加组
					}
					for (int i = 0; i < CommandList.size(); i++) { //把解释后命令列表保存起来,最后再匹配数据库保存的任务信息来确认任务号
						CommandListTemp.add(CommandList.get(i).toString());
					}					
					//解释任务数据
					for (int i = 0; i < MeasuredSignList.length; i++) { //测量点个数
						//处理数据时标
						if (iDataType == 21) { //一类数据的小时冻结数据
							String sLable;
							if (TermialProtocolType == Glu_ConstDefine.GY_ZD_698) {
								sLable = FrameDataArea.substring(0, 4);
							} else {
								sLable = FrameDataArea.substring(0, 2);
							}
							sTaskDateTimeInfo = GetTaskDateTimeInfo(sLable,
									iDataType, TermialProtocolType);
						} else if (iDataType == 22) { //曲线数据
							sTaskDateTimeInfo = GetTaskDateTimeInfo(FrameDataArea
									.substring(0, 14), iDataType,
									TermialProtocolType);
						} else if (iDataType == 23 || iDataType == 26) { //日冻结数据
							sTaskDateTimeInfo = GetTaskDateTimeInfo(FrameDataArea
									.substring(0, 6), iDataType,
									TermialProtocolType);
						} else if (iDataType == 24) { //月冻结数据
							sTaskDateTimeInfo = GetTaskDateTimeInfo(FrameDataArea
									.substring(0, 4), iDataType,
									TermialProtocolType);
						}
						if (sTaskDateTimeInfo.length() >= 17) { //数据时标解释成功
							sTaskDateTime = sTaskDateTimeInfo.substring(0, 14); //开始时间
							iDataDensity = Integer.parseInt(sTaskDateTimeInfo
									.substring(14, 16)); //时间间隔
							iDataCount = Integer.parseInt(sTaskDateTimeInfo
									.substring(16, sTaskDateTimeInfo.length())); //数据点数
						}
						for (int j = 0; j < CommandList.size(); j++) { //命令个数
							//每个命令都包含数据时标，假设上行任务数据同一单元标识里命令都是同一类数据，且时标都是一样的，不然是无法按任务结构解释的							
							switch (iDataType) {
							case 21:
								if (TermialProtocolType == Glu_ConstDefine.GY_ZD_698) {
									FrameDataArea = FrameDataArea.substring(4,
											FrameDataArea.length());
								} else {
									FrameDataArea = FrameDataArea.substring(2,
											FrameDataArea.length());
								}
								break;
							case 22:
								FrameDataArea = FrameDataArea.substring(14,
										FrameDataArea.length());
								break;
							case 23:case 26:
								FrameDataArea = FrameDataArea.substring(6,
										FrameDataArea.length());
								break;
							case 24:
								FrameDataArea = FrameDataArea.substring(4,
										FrameDataArea.length());
								break;
							}
							if (ControlCode.equals("0D")
									&& (iFn >= 121 && iFn <= 123)) { //特殊处理谐波次数
								iCount = Integer.parseInt(FrameDataArea
										.substring(0, 2), 16); //N次谐波
								FrameDataArea = FrameDataArea.substring(2,
										FrameDataArea.length());
							}
							if (ControlCode.equals("0C")
									&& (iFn >= 57 && iFn <= 58)) { //特殊处理谐波次数
								if (iFn == 57) {
									iCount = Integer.parseInt(FrameDataArea
											.substring(0, 2), 16) - 2; //N次谐波,没有总
								} else if (iFn == 58) {
									iCount = Integer.parseInt(FrameDataArea
											.substring(0, 2), 16) - 1; //N次谐波
								}
								FrameDataArea = FrameDataArea.substring(2,
										FrameDataArea.length());
							}
							for (int m = 0; m < iDataCount; m++) { //数据点数
								if(MeasuredSignList.length==1){
									HistoryDataListTemp[m].SetTaskNo(0);
									HistoryDataListTemp[m]
											.SetMeasuredPointNo(MeasuredSignList[i]);
									HistoryDataListTemp[m]
											.SetMeasuredPointType(iMeasuredType);
									if (iDataType == 23 || (iDataType == 26 && Glu_ConstDefine.BillingDateAddOneDay)) {
										HistoryDataListTemp[m]
												.SetTaskDateTime(DataSwitch
														.IncreaseDateTime(
																sTaskDateTime, 1, ////统一曲线的起始时间就是每隔小时的0分作为开始
																4));

									}else if (iDataType == 26 && !Glu_ConstDefine.BillingDateAddOneDay) {
										HistoryDataListTemp[m]
															.SetTaskDateTime(DataSwitch
																	.IncreaseDateTime(
																			sTaskDateTime, 1*m, ////统一曲线的起始时间就是每隔小时的0分作为开始
																			4));

									} else {
										HistoryDataListTemp[m]
												.SetTaskDateTime(DataSwitch
														.IncreaseDateTime(
																sTaskDateTime,
																iDataDensity * m, ////统一曲线的起始时间就是每隔小时的0分作为开始
																2));
									}
								}else{
									HistoryDataListTemp[i].SetTaskNo(0);
									HistoryDataListTemp[i]
											.SetMeasuredPointNo(MeasuredSignList[i]);
									HistoryDataListTemp[i]
											.SetMeasuredPointType(iMeasuredType);
									if (iDataType == 23|| (iDataType == 26 && Glu_ConstDefine.BillingDateAddOneDay)) {
										HistoryDataListTemp[i]
												.SetTaskDateTime(DataSwitch
														.IncreaseDateTime(
																sTaskDateTime, 1, ////统一曲线的起始时间就是每隔小时的0分作为开始
																4));

									}else if (iDataType == 26 && !Glu_ConstDefine.BillingDateAddOneDay) {
										HistoryDataListTemp[i]
															.SetTaskDateTime(DataSwitch
																	.IncreaseDateTime(
																			sTaskDateTime, 1*m, ////统一曲线的起始时间就是每隔小时的0分作为开始
																			4));

									} else {
										HistoryDataListTemp[i]
												.SetTaskDateTime(DataSwitch
														.IncreaseDateTime(
																sTaskDateTime,
																iDataDensity * m, ////统一曲线的起始时间就是每隔小时的0分作为开始
																2));
									}
								}
								
								sDataCaption = CommandList.get(j).toString();
								iFn = Integer.parseInt(CommandList.get(j)
										.toString().substring(1, 4));
								NormalDataTemp = GluMethodQuanGuo
										.CommandSearchAndExplain(FrameDataArea,
												sDataCaption, CommandInfoList,
												TermialProtocolType, iCount,
												iFn);
								FrameDataArea = FrameDataArea.substring(
										NormalDataTemp.GetCommandLen(),
										FrameDataArea.length());
								if(i==0){
									for (int k = 0; k < NormalDataTemp.DataItemList
											.size(); k++) { //把解释后的数据保存在中间对象里
										HistoryDataListTemp[m].DataItemList
												.add(NormalDataTemp.DataItemList
														.get(k));
										HistoryDataListTemp[m].DataItemCountAdd();
									}
								}else{								
									for (int k = 0; k < NormalDataTemp.DataItemList
											.size(); k++) { //把解释后的数据保存在中间对象里
										HistoryDataListTemp[i].DataItemList
												.add(NormalDataTemp.DataItemList
														.get(k));
										HistoryDataListTemp[i].DataItemCountAdd();
									}
								}
							}
						}
					}
					iDataCount = iDataCount * MeasuredSignList.length;
					MeasuredSignList = null; //清空数组内容
					CommandList.clear(); //清空队列内容
				}
				//从任务信息队列查找任务号和测量点类型,返回历史解析数据列表
				SPE_TaskInfoList TempTaskInfoList = new SPE_TaskInfoList();
				if (TermialProtocolType == Glu_ConstDefine.GY_ZD_698) {
					TempTaskInfoList = QuanGuo698TaskInfoList;
				} else {
					TempTaskInfoList = QuanGuoTaskInfoList;
				}
				if (FrameType == 11) { //主动上送任务
					HistoryDataList = GluMethodQuanGuo
							.SearchAndMatchingTaskInfoList(CommandListTemp,
									HistoryDataListTemp, iDataCount,
									TempTaskInfoList); //从数据库配置信息查找主站配置的任务号
					if (HistoryDataList.size() == 0) {
						DataListInfo.ExplainResult = 50018; //未找到任务配置信息
						for (int i = 0; i < iDataCount; i++) {
							HistoryDataList.add(HistoryDataListTemp[i]);
						}
					}
				} else {
					if (Glu_ConstDefine.TerminalAutoTaskAdjust) {
						HistoryDataList = GluMethodQuanGuo
								.SearchAndMatchingTaskInfoList(CommandListTemp,
										HistoryDataListTemp, iDataCount,
										TempTaskInfoList); //从数据库配置信息查找主站配置的任务号
						if (HistoryDataList.size() == 0) {
							for (int i = 0; i < iDataCount; i++) {
								String sCommand = (String) CommandListTemp
										.get(0);
								HistoryDataListTemp[i]
										.SetTaskNo(GluMethodQuanGuo
												.GetSelfDefineTaskNo(
														TermialProtocolType,
														sCommand,
														TempTaskInfoList));
								HistoryDataList.add(HistoryDataListTemp[i]);
							}
						}
					} else { //从主动上送任务列表中无法找到对应的任务号，则从自定义任务列表中查找
						for (int i = 0; i < iDataCount; i++) {
							String sCommand = (String) CommandListTemp.get(0);
							HistoryDataListTemp[i].SetTaskNo(GluMethodQuanGuo
									.GetSelfDefineTaskNo(TermialProtocolType,
											sCommand, TempTaskInfoList));
							HistoryDataList.add(HistoryDataListTemp[i]);
						}
					}
				}
			} catch (Exception e) {
				Glu_ConstDefine.Log1
						.WriteLog("Func:FrameDataAreaExplainQuanGuo__ExplainHistoryDataAreaQuanGuo();Error:"
								+ e.toString());
			}
		} finally {
		}
		DataListInfo.DataType = 20;
		DataListInfo.DataList = HistoryDataList;
		return DataListInfo;
	}

	public SFE_DataListInfo ExplainAlarmDataAreaQuanGuo( //终端全国告警数据区解释
			String FrameDataArea, int TermialProtocolType, String ControlCode) { //帧数据区,终端规约,控制码
		SFE_DataListInfo DataListInfo = new SFE_DataListInfo();
		ArrayList AlarmDataList = new ArrayList();
		try {
			try {
				//SFE_ExplainAlarmData ExplainAlarmData = new SFE_ExplainAlarmData();
				int iFn = Integer.parseInt(FrameDataArea.substring(4, 6)) & 3; //信息类元:1重要事件；2一般事件
				if (FrameDataArea.length() > 16) {
					int Pm = Integer.parseInt(FrameDataArea.substring(12, 14),
							16); //本帧报文传送的事件记录起始指针
					int Pn = Integer.parseInt(FrameDataArea.substring(14, 16),
							16); //本帧报文传送的事件记录结束指针
					int iAlarmDataCount = 0; //告警个数
					if (Pn >= Pm) {
						iAlarmDataCount = Pn - Pm;
					} else {
						iAlarmDataCount = 256 + Pn - Pm;
					}
					if (iAlarmDataCount > 0) {
						FrameDataArea = FrameDataArea.substring(16,
								FrameDataArea.length());
						AlarmDataList = ExplainAlarmParameterQuanGuo(
								FrameDataArea, iFn, iAlarmDataCount,
								TermialProtocolType);
					}
				}
			} catch (Exception e) {
				Glu_ConstDefine.Log1
						.WriteLog("Func:FrameDataAreaExplainQuanGuo__ExplainAlarmDataAreaQuanGuo();Error:"
								+ e.toString());
			}
		} finally {
		}
		DataListInfo.ExplainResult = 0;
		DataListInfo.DataType = 30;
		DataListInfo.DataList = AlarmDataList;
		return DataListInfo;
	}

	public ArrayList ExplainAlarmParameterQuanGuo(String sDateContent, int Fn,
			int AlarmDataCount, int GYH) {
		ArrayList<SFE_AlarmData> AlarmDataList = new ArrayList<SFE_AlarmData>();
		ArrayList<SFE_DataItemInfo> DataItemInfoList = new ArrayList<SFE_DataItemInfo>(); //数据项信息列表
		ArrayList<String> AlarmCodeList = new ArrayList<String>();
		SFE_AlarmData AlarmData;
		SFE_AlarmData AlarmDataTemp;
		SFE_DataItemInfo DataItemInfo;
		String sDataCaption = ""; //数据项标识
		int iDataType = 0; //数据类型
		int iDataLen = 0; //数据长度
		String sDataFormat = ""; //数据项数据格式
		int iStorageType = 0; //存储方式（10 BCD;20 HEX;30 ASCII）
		int iStorageOrder = 0; //存储顺序（10顺序  20逆序）
		String sLogicData = ""; //逻辑数据内容（如12.34）
		String sPhysicalData = ""; //物理数据内容(如BCD:3412,Hex:220C)
		String sMeasuredPointNo = "0"; //默认测量点号
		int iMeasuredPointType = 10; //默认测量点类型
		String sAlarmSign = "";
		int iAlarmType = 0;
		int AlarmMaxCount = 0;
		int iCount = 0;
		String sAlarmCode = "";
		try {
			try {
				for (int i = 0; i < AlarmDataCount; i++) {
					int iAlarmCode = Integer.parseInt(sDateContent.substring(0,
							2), 16); //告警编码
					int iAlarmlen = Integer.parseInt(sDateContent.substring(2,
							4), 16) + 2; //告警长度
					String sAlarmDateTime = "";
					if(Glu_ConstDefine.IRANToGregorian){
						sAlarmDateTime = DataSwitch.ReverseStringByByte(sDateContent
										.substring(4, 14)) + "00"; //告警发生时间	
						sAlarmDateTime = sAlarmDateTime.substring(0,6) + "01" + sAlarmDateTime.substring(6,12);
						sAlarmDateTime = Glu_ConstDefine.Cc1.TOU_IRANToGregorian(sAlarmDateTime);
						sAlarmDateTime = "20" + sAlarmDateTime.substring(0,6) + sAlarmDateTime.substring(8,14);
					}else{
						sAlarmDateTime = "20"
							+ DataSwitch.ReverseStringByByte(sDateContent
									.substring(4, 14)) + "00"; //告警发生时间						
					}
					sDateContent = sDateContent.substring(14, sDateContent
							.length());
					
					if (iAlarmCode == 1) { //ERC1:数据初始化和版本变更记录
						DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
						sDataCaption = "EC0101"; //事件标志
						iDataType = c_String; //字符型
						iDataLen = 1; //字节长度
						sDataFormat = "1"; //数据格式
						iStorageType = CCFS_HEX; //十六进制码
						iStorageOrder = CCSX_NX; //逆序
						sPhysicalData = sDateContent.substring(0, iDataLen * 2);
						sDateContent = sDateContent.substring(iDataLen * 2,
								sDateContent.length());
						DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
								iDataLen, sDataFormat, iStorageType,
								iStorageOrder);
						DataItemInfo.SetFPhysicalData(sPhysicalData);
						DataItemInfoList.add(DataItemInfo);
						sAlarmSign = sPhysicalData;

						DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
						sDataCaption = "EC0102"; //变更前软件版本号
						iDataType = c_String; //字符型
						iDataLen = 4; //物理数据的字符长度
						sDataFormat = "4"; //数据格式
						iStorageType = CCFS_ASC; //ASCII码
						iStorageOrder = CCSX_SX; //ASCII码顺序存储
						sPhysicalData = sDateContent.substring(0, iDataLen * 2);
						sDateContent = sDateContent.substring(iDataLen * 2,
								sDateContent.length());
						DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
								iDataLen, sDataFormat, iStorageType,
								iStorageOrder);
						DataItemInfo.SetFPhysicalData(sPhysicalData);
						DataItemInfoList.add(DataItemInfo);

						DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
						sDataCaption = "EC0103"; //变更后软件版本号
						iDataType = c_String; //字符型
						iDataLen = 4; //物理数据的字符长度
						sDataFormat = "4"; //数据格式
						iStorageType = CCFS_ASC; //ASCII码
						iStorageOrder = CCSX_SX; //ASCII码顺序存储
						sPhysicalData = sDateContent.substring(0, iDataLen * 2);
						sDateContent = sDateContent.substring(iDataLen * 2,
								sDateContent.length());
						DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
								iDataLen, sDataFormat, iStorageType,
								iStorageOrder);
						DataItemInfo.SetFPhysicalData(sPhysicalData);
						DataItemInfoList.add(DataItemInfo);

						if (!sAlarmSign.equals("")) {
							iAlarmType = 1; //告警类型:1发生；2恢复
							AlarmMaxCount = 3; //一个字节包含告警种类
							AlarmCodeList = ExplainAlarmCodeQuanGuo(iAlarmType,
									sAlarmSign, Integer.toString(iAlarmCode),
									AlarmMaxCount);
						}
					} else if (iAlarmCode == 2) { //ERC2:参数丢失记录
						DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
						sDataCaption = "EC0200"; //事件标志
						iDataType = c_String; //字符型
						iDataLen = 1; //物理数据的字符长度
						sDataFormat = "1"; //数据格式
						iStorageType = CCFS_HEX; //十六进制码
						iStorageOrder = CCSX_SX; //顺序存储
						sPhysicalData = sDateContent.substring(0, iDataLen * 2);
						sDateContent = sDateContent.substring(iDataLen * 2,
								sDateContent.length());
						DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
								iDataLen, sDataFormat, iStorageType,
								iStorageOrder);
						DataItemInfo.SetFPhysicalData(sPhysicalData);
						DataItemInfoList.add(DataItemInfo);
						sAlarmSign = sPhysicalData;

						if (!sAlarmSign.equals("")) {
							iAlarmType = 1; //告警类型:1发生；2恢复
							AlarmMaxCount = 2; //一个字节包含告警种类
							AlarmCodeList = ExplainAlarmCodeQuanGuo(iAlarmType,
									sAlarmSign, Integer.toString(iAlarmCode),
									AlarmMaxCount);
						}
					} else if (iAlarmCode == 3) { //ERC3:参数变更记录
						DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
						sDataCaption = "EC0301"; //启动站地址
						iDataType = c_String; //字符型
						iDataLen = 1; //物理数据的字符长度
						sDataFormat = "1"; //数据格式
						iStorageType = CCFS_HEX; //十六进制码
						iStorageOrder = CCSX_SX; //顺序存储
						sPhysicalData = sDateContent.substring(0, iDataLen * 2);
						sDateContent = sDateContent.substring(iDataLen * 2,
								sDateContent.length());
						DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
								iDataLen, sDataFormat, iStorageType,
								iStorageOrder);
						DataItemInfo.SetFPhysicalData(sPhysicalData);
						DataItemInfoList.add(DataItemInfo);

						DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
						sDataCaption = "EC0302"; //变更参数数据单元标识
						iDataType = c_Complex; //复合型
						iDataLen = (iAlarmlen - 8); //数据长度
						sDataFormat = ""; //数据格式
						iCount = iDataLen / 4; //计算变更参数数据单元标识的个数
						for (int j = 0; j < iCount; j++) {
							if (j == iCount - 1) { //最后一个
								sDataFormat = sDataFormat + "4,20,20";
							} else {
								sDataFormat = sDataFormat + "4,20,20" + "#";
							}
						}
						iStorageType = CCFS_HEX; //十六进制码
						iStorageOrder = CCSX_SX; //顺序存储
						sPhysicalData = sDateContent.substring(0, iDataLen * 2);
						sDateContent = sDateContent.substring(iDataLen * 2,
								sDateContent.length());
						DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
								iDataLen, sDataFormat, iStorageType,
								iStorageOrder);
						DataItemInfo.SetFPhysicalData(sPhysicalData);
						DataItemInfoList.add(DataItemInfo);

						AlarmCodeList.add("C030"); //参数变更记录
					} else if (iAlarmCode == 4) { //ERC4:状态量变位记录
						DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
						sDataCaption = "EC0401"; //状态变位
						iDataType = c_String; //字符型
						iDataLen = 1; //物理数据的字符长度
						sDataFormat = "1"; //数据格式
						iStorageType = CCFS_HEX; //十六进制码
						iStorageOrder = CCSX_SX; //顺序存储
						sPhysicalData = sDateContent.substring(0, iDataLen * 2);
						sDateContent = sDateContent.substring(iDataLen * 2,
								sDateContent.length());
						DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
								iDataLen, sDataFormat, iStorageType,
								iStorageOrder);
						DataItemInfo.SetFPhysicalData(sPhysicalData);
						DataItemInfoList.add(DataItemInfo);
						sAlarmSign = sPhysicalData;

						DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
						sDataCaption = "EC0402"; //变位后状态
						iDataType = c_String; //字符型
						iDataLen = 1; //物理数据的字符长度
						sDataFormat = "1"; //数据格式
						iStorageType = CCFS_HEX; //十六进制码
						iStorageOrder = CCSX_SX; //顺序存储
						sPhysicalData = sDateContent.substring(0, iDataLen * 2);
						sDateContent = sDateContent.substring(iDataLen * 2,
								sDateContent.length());
						DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
								iDataLen, sDataFormat, iStorageType,
								iStorageOrder);
						DataItemInfo.SetFPhysicalData(sPhysicalData);
						DataItemInfoList.add(DataItemInfo);
						sAlarmSign = sAlarmSign + sPhysicalData;

						if (!sAlarmSign.equals("")) {
							iAlarmType = 3; //告警类型:1发生;2恢复;状态变位
							AlarmMaxCount = 8; //一个字节包含告警种类
							AlarmCodeList = ExplainAlarmCodeQuanGuo(iAlarmType,
									sAlarmSign, Integer.toString(iAlarmCode),
									AlarmMaxCount);
						}
					} else if (iAlarmCode == 5) { //ERC5:遥控跳闸记录
						DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
						sDataCaption = "EC0501"; //跳闸轮次
						iDataType = c_String; //字符型
						iDataLen = 1; //物理数据的字符长度
						sDataFormat = "1"; //数据格式
						iStorageType = CCFS_HEX; //十六进制码
						iStorageOrder = CCSX_SX; //顺序存储
						sPhysicalData = sDateContent.substring(0, iDataLen * 2);
						sDateContent = sDateContent.substring(iDataLen * 2,
								sDateContent.length());
						DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
								iDataLen, sDataFormat, iStorageType,
								iStorageOrder);
						DataItemInfo.SetFPhysicalData(sPhysicalData);
						DataItemInfoList.add(DataItemInfo);

						DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
						sDataCaption = "EC0502"; //跳闸时功率
						iDataType = c_FloatEx; //特殊浮点型（对应国网规范格式2,带幂部）
						iDataLen = 2; //物理数据的字符长度
						sDataFormat = "A"; //数据格式
						iStorageType = CCFS_BCD; //BCD码
						iStorageOrder = CCSX_NX; //逆序存储
						sPhysicalData = sDateContent.substring(0, iDataLen * 2);
						sDateContent = sDateContent.substring(iDataLen * 2,
								sDateContent.length());
						DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
								iDataLen, sDataFormat, iStorageType,
								iStorageOrder);
						DataItemInfo.SetFPhysicalData(sPhysicalData);
						DataItemInfoList.add(DataItemInfo);

						DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
						sDataCaption = "EC0503"; //跳闸后2分钟的功率  数据格式跟上一个一样
						sPhysicalData = sDateContent.substring(0, iDataLen * 2);
						sDateContent = sDateContent.substring(iDataLen * 2,
								sDateContent.length());
						DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
								iDataLen, sDataFormat, iStorageType,
								iStorageOrder);
						DataItemInfo.SetFPhysicalData(sPhysicalData);
						DataItemInfoList.add(DataItemInfo);

						AlarmCodeList.add("C050"); //遥控跳闸
					} else if (iAlarmCode == 6) { //ERC6:功控跳闸记录
						DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
						sDataCaption = "EC0601"; //总加组号
						iDataType = c_Float; //字符型
						iDataLen = 1; //物理数据的字符长度
						sDataFormat = "00"; //数据格式
						iStorageType = CCFS_HEX; //十六进制码
						iStorageOrder = CCSX_SX; //顺序存储
						sPhysicalData = ""
								+ ((Integer.parseInt(sDateContent.substring(0,
										iDataLen * 2), 16) & 63)); //测量点号和总加组号加1
						sMeasuredPointNo = sPhysicalData;
						iMeasuredPointType = 20; //总加组类型
						sPhysicalData = DataSwitch
								.IntToHex(sPhysicalData, "00");
						sDateContent = sDateContent.substring(iDataLen * 2,
								sDateContent.length());
						DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
								iDataLen, sDataFormat, iStorageType,
								iStorageOrder);
						DataItemInfo.SetFPhysicalData(sPhysicalData);
						DataItemInfoList.add(DataItemInfo);

						DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
						sDataCaption = "EC0602"; //跳闸轮次
						iDataType = c_String; //字符型
						iDataLen = 1; //物理数据的字符长度
						sDataFormat = "1"; //数据格式
						iStorageType = CCFS_HEX; //十六进制码
						iStorageOrder = CCSX_SX; //顺序存储
						sPhysicalData = sDateContent.substring(0, iDataLen * 2);
						sDateContent = sDateContent.substring(iDataLen * 2,
								sDateContent.length());
						DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
								iDataLen, sDataFormat, iStorageType,
								iStorageOrder);
						DataItemInfo.SetFPhysicalData(sPhysicalData);
						DataItemInfoList.add(DataItemInfo);

						DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
						sDataCaption = "EC0603"; //功控类别 数据格式跟上一个一样
						sPhysicalData = sDateContent.substring(0, iDataLen * 2);
						sDateContent = sDateContent.substring(iDataLen * 2,
								sDateContent.length());
						DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
								iDataLen, sDataFormat, iStorageType,
								iStorageOrder);
						DataItemInfo.SetFPhysicalData(sPhysicalData);
						DataItemInfoList.add(DataItemInfo);

						DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
						sDataCaption = "EC0604"; //跳闸前功率
						iDataType = c_FloatEx; //特殊浮点型（对应国网规范格式2,带幂部）
						iDataLen = 2; //物理数据的字符长度
						sDataFormat = "A"; //数据格式
						iStorageType = CCFS_BCD; //BCD码
						iStorageOrder = CCSX_NX; //逆序存储
						sPhysicalData = sDateContent.substring(0, iDataLen * 2);
						sDateContent = sDateContent.substring(iDataLen * 2,
								sDateContent.length());
						DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
								iDataLen, sDataFormat, iStorageType,
								iStorageOrder);
						DataItemInfo.SetFPhysicalData(sPhysicalData);
						DataItemInfoList.add(DataItemInfo);

						DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
						sDataCaption = "EC0605"; //跳闸后2分钟的功率  数据格式跟上一个一样
						sPhysicalData = sDateContent.substring(0, iDataLen * 2);
						sDateContent = sDateContent.substring(iDataLen * 2,
								sDateContent.length());
						DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
								iDataLen, sDataFormat, iStorageType,
								iStorageOrder);
						DataItemInfo.SetFPhysicalData(sPhysicalData);
						DataItemInfoList.add(DataItemInfo);

						DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
						sDataCaption = "EC0606"; //跳闸时功率定值  数据格式跟上一个一样
						sPhysicalData = sDateContent.substring(0, iDataLen * 2);
						sDateContent = sDateContent.substring(iDataLen * 2,
								sDateContent.length());
						DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
								iDataLen, sDataFormat, iStorageType,
								iStorageOrder);
						DataItemInfo.SetFPhysicalData(sPhysicalData);
						DataItemInfoList.add(DataItemInfo);

						AlarmCodeList.add("C060"); //功控跳闸
					} else if (iAlarmCode == 7) { //ERC7:电控跳闸记录
						DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
						sDataCaption = "EC0701"; //总加组号
						iDataType = c_Float; //字符型
						iDataLen = 1; //物理数据的字符长度
						sDataFormat = "00"; //数据格式
						iStorageType = CCFS_HEX; //十六进制码
						iStorageOrder = CCSX_SX; //顺序存储
						sPhysicalData = ""
								+ ((Integer.parseInt(sDateContent.substring(0,
										iDataLen * 2), 16) & 63)); //测量点号和总加组号加1
						sMeasuredPointNo = sPhysicalData;
						iMeasuredPointType = 20; //总加组类型
						sPhysicalData = DataSwitch
								.IntToHex(sPhysicalData, "00");
						sDateContent = sDateContent.substring(iDataLen * 2,
								sDateContent.length());
						DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
								iDataLen, sDataFormat, iStorageType,
								iStorageOrder);
						DataItemInfo.SetFPhysicalData(sPhysicalData);
						DataItemInfoList.add(DataItemInfo);

						DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
						sDataCaption = "EC0702"; //跳闸轮次
						iDataType = c_String; //字符型
						iDataLen = 1; //物理数据的字符长度
						sDataFormat = "1"; //数据格式
						iStorageType = CCFS_HEX; //十六进制码
						iStorageOrder = CCSX_SX; //顺序存储
						sPhysicalData = sDateContent.substring(0, iDataLen * 2);
						sDateContent = sDateContent.substring(iDataLen * 2,
								sDateContent.length());
						DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
								iDataLen, sDataFormat, iStorageType,
								iStorageOrder);
						DataItemInfo.SetFPhysicalData(sPhysicalData);
						DataItemInfoList.add(DataItemInfo);

						DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
						sDataCaption = "EC0703"; //电控类别 数据格式跟上一个一样
						sPhysicalData = sDateContent.substring(0, iDataLen * 2);
						sDateContent = sDateContent.substring(iDataLen * 2,
								sDateContent.length());
						DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
								iDataLen, sDataFormat, iStorageType,
								iStorageOrder);
						DataItemInfo.SetFPhysicalData(sPhysicalData);
						DataItemInfoList.add(DataItemInfo);

						DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
						sDataCaption = "EC0704"; //跳闸时电能量
						iDataType = c_IntDWFH; //带单位标志和正负号的整型值串
						iDataLen = 4; //物理数据的字符长度
						sDataFormat = "E"; //数据格式
						iStorageType = CCFS_BCD; //BCD码
						iStorageOrder = CCSX_NX; //逆序存储
						sPhysicalData = sDateContent.substring(0, iDataLen * 2);
						sDateContent = sDateContent.substring(iDataLen * 2,
								sDateContent.length());
						DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
								iDataLen, sDataFormat, iStorageType,
								iStorageOrder);
						DataItemInfo.SetFPhysicalData(sPhysicalData);
						DataItemInfoList.add(DataItemInfo);

						DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
						sDataCaption = "EC0705"; //跳闸时电能量定值  数据格式跟上一个一样
						sPhysicalData = sDateContent.substring(0, iDataLen * 2);
						sDateContent = sDateContent.substring(iDataLen * 2,
								sDateContent.length());
						DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
								iDataLen, sDataFormat, iStorageType,
								iStorageOrder);
						DataItemInfo.SetFPhysicalData(sPhysicalData);
						DataItemInfoList.add(DataItemInfo);

						AlarmCodeList.add("C070"); //电控跳闸
					} else if (iAlarmCode == 8) { //ERC8:电能表参数变更
						DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
						sDataCaption = "EC0801"; //测量点号
						iDataType = c_Float; //字符型
						if (GYH == Glu_ConstDefine.GY_ZD_698) {
							iDataLen = 2; //物理数据的字符长度
							sDataFormat = "0000"; //数据格式
							iStorageType = CCFS_HEX; //十六进制码
							iStorageOrder = CCSX_SX; //顺序存储
							sPhysicalData = sDateContent.substring(0,
									iDataLen * 2);
							sPhysicalData = DataSwitch
									.ReverseStringByByte(sPhysicalData);
							sPhysicalData = ""
									+ ((Integer.parseInt(sPhysicalData, 16) & 4095)); //测量点号和总加组号加1
							sMeasuredPointNo = sPhysicalData;
							sPhysicalData = DataSwitch.IntToHex(sPhysicalData,
									"0000");
						} else {
							iDataLen = 1; //物理数据的字符长度
							sDataFormat = "00"; //数据格式
							iStorageType = CCFS_HEX; //十六进制码
							iStorageOrder = CCSX_SX; //顺序存储
							sPhysicalData = ""
									+ ((Integer.parseInt(sDateContent
											.substring(0, iDataLen * 2), 16) & 63)); //测量点号和总加组号加1
							sMeasuredPointNo = sPhysicalData;
							sPhysicalData = DataSwitch.IntToHex(sPhysicalData,
									"00");
						}
						sDateContent = sDateContent.substring(iDataLen * 2,
								sDateContent.length());
						DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
								iDataLen, sDataFormat, iStorageType,
								iStorageOrder);
						DataItemInfo.SetFPhysicalData(sPhysicalData);
						DataItemInfoList.add(DataItemInfo);

						DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
						sDataCaption = "EC0802"; //变更标志
						iDataType = c_String; //字符型
						iDataLen = 1; //物理数据的字符长度
						sDataFormat = "1"; //数据格式
						iStorageType = CCFS_HEX; //十六进制码
						iStorageOrder = CCSX_SX; //顺序存储
						sPhysicalData = sDateContent.substring(0, iDataLen * 2);
						sDateContent = sDateContent.substring(iDataLen * 2,
								sDateContent.length());
						DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
								iDataLen, sDataFormat, iStorageType,
								iStorageOrder);
						DataItemInfo.SetFPhysicalData(sPhysicalData);
						DataItemInfoList.add(DataItemInfo);
						sAlarmSign = sPhysicalData;

						if (!sAlarmSign.equals("")) {
							iAlarmType = 1; //告警类型:1发生;2恢复;状态变位
							AlarmMaxCount = 6; //一个字节包含告警种类
							AlarmCodeList = ExplainAlarmCodeQuanGuo(iAlarmType,
									sAlarmSign, Integer.toString(iAlarmCode),
									AlarmMaxCount);
						}

					} else if (iAlarmCode == 9 || iAlarmCode == 10) { //ERC9:电流回路异常,ERC10:电压回路异常
						sAlarmCode = DataSwitch.StrStuff("0", 2,
								("" + iAlarmCode), 10); //补足2两位
						DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
						sDataCaption = "EC" + sAlarmCode + "01"; //测量点号
						iDataType = c_Float; //字符型
						if (GYH == Glu_ConstDefine.GY_ZD_698) {
							iDataLen = 2; //物理数据的字符长度
							sDataFormat = "0000"; //数据格式
							iStorageType = CCFS_HEX; //十六进制码
							iStorageOrder = CCSX_SX; //顺序存储
							sPhysicalData = sDateContent.substring(0,
									iDataLen * 2);
							sPhysicalData = DataSwitch
									.ReverseStringByByte(sPhysicalData);
							if ((Integer.parseInt(sPhysicalData, 16) & 32768) == 32768) {
								iAlarmType = 10; //告警类型:10发生
							} else {
								iAlarmType = 20; //告警类型:20恢复
							}
							sPhysicalData = ""
									+ ((Integer.parseInt(sPhysicalData, 16) & 63)); //测量点号和总加组号加1
							sMeasuredPointNo = sPhysicalData;
							sPhysicalData = DataSwitch.IntToHex(sPhysicalData,
									"0000");
							
							sDateContent = sDateContent.substring(iDataLen * 2,
									sDateContent.length());
							DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
									iDataLen, sDataFormat, iStorageType,
									iStorageOrder);
							DataItemInfo.SetFPhysicalData(sPhysicalData);
							DataItemInfoList.add(DataItemInfo);

							DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
							sDataCaption = "EC" + sAlarmCode + "02"; //异常标志
							iDataType = c_String; //字符型
							iDataLen = 1; //物理数据的字符长度
							sDataFormat = "1"; //数据格式
							iStorageType = CCFS_HEX; //十六进制码
							iStorageOrder = CCSX_SX; //顺序存储
							sPhysicalData = sDateContent.substring(0, iDataLen * 2);
							sDateContent = sDateContent.substring(iDataLen * 2,
									sDateContent.length());
							DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
									iDataLen, sDataFormat, iStorageType,
									iStorageOrder);
							DataItemInfo.SetFPhysicalData(sPhysicalData);
							DataItemInfoList.add(DataItemInfo);
							sAlarmSign = sPhysicalData;

							DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
							sDataCaption = "EC" + sAlarmCode + "03"; //发生时的Ua/Uab
							iDataType = c_Float; //字符型
							iDataLen = 2; //物理数据的字符长度
							sDataFormat = "000.0"; //数据格式
							iStorageType = CCFS_BCD; //BCD码
							iStorageOrder = CCSX_NX; //逆序存储
							sPhysicalData = sDateContent.substring(0, iDataLen * 2);
							sDateContent = sDateContent.substring(iDataLen * 2,
									sDateContent.length());
							DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
									iDataLen, sDataFormat, iStorageType,
									iStorageOrder);
							DataItemInfo.SetFPhysicalData(sPhysicalData);
							DataItemInfoList.add(DataItemInfo);

							DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
							sDataCaption = "EC" + sAlarmCode + "04"; //发生时的Ub   数据格式跟上一个一样
							sPhysicalData = sDateContent.substring(0, iDataLen * 2);
							sDateContent = sDateContent.substring(iDataLen * 2,
									sDateContent.length());
							DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
									iDataLen, sDataFormat, iStorageType,
									iStorageOrder);
							DataItemInfo.SetFPhysicalData(sPhysicalData);
							DataItemInfoList.add(DataItemInfo);

							DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
							sDataCaption = "EC" + sAlarmCode + "05"; //发生时的Uc/Ucb  数据格式跟上一个一样
							sPhysicalData = sDateContent.substring(0, iDataLen * 2);
							sDateContent = sDateContent.substring(iDataLen * 2,
									sDateContent.length());
							DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
									iDataLen, sDataFormat, iStorageType,
									iStorageOrder);
							DataItemInfo.SetFPhysicalData(sPhysicalData);
							DataItemInfoList.add(DataItemInfo);

							DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
							sDataCaption = "EC" + sAlarmCode + "06"; //发生时的Ia
							iDataType = c_FloatFH; //字符型
							iDataLen = 3; //物理数据的字符长度
							sDataFormat = "CCC.CCC"; //数据格式
							iStorageType = CCFS_BCD; //BCD码
							iStorageOrder = CCSX_NX; //逆序存储
							sPhysicalData = sDateContent.substring(0, iDataLen * 2);
							sDateContent = sDateContent.substring(iDataLen * 2,
									sDateContent.length());
							DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
									iDataLen, sDataFormat, iStorageType,
									iStorageOrder);
							DataItemInfo.SetFPhysicalData(sPhysicalData);
							DataItemInfoList.add(DataItemInfo);

							DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
							sDataCaption = "EC" + sAlarmCode + "07"; //发生时的Ib     数据格式跟上一个一样
							sPhysicalData = sDateContent.substring(0, iDataLen * 2);
							sDateContent = sDateContent.substring(iDataLen * 2,
									sDateContent.length());
							DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
									iDataLen, sDataFormat, iStorageType,
									iStorageOrder);
							DataItemInfo.SetFPhysicalData(sPhysicalData);
							DataItemInfoList.add(DataItemInfo);

							DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
							sDataCaption = "EC" + sAlarmCode + "08"; //发生时的Ic    数据格式跟上一个一样
							sPhysicalData = sDateContent.substring(0, iDataLen * 2);
							sDateContent = sDateContent.substring(iDataLen * 2,
									sDateContent.length());
							DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
									iDataLen, sDataFormat, iStorageType,
									iStorageOrder);
							DataItemInfo.SetFPhysicalData(sPhysicalData);
							DataItemInfoList.add(DataItemInfo);

							DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
							sDataCaption = "EC" + sAlarmCode + "09"; //发生时电能表正向有功总电能示值
							iDataType = c_Float; //字符型
							iDataLen = 5; //物理数据的字符长度
							sDataFormat = "000000.0000"; //数据格式
							iStorageType = CCFS_BCD; //BCD码
							iStorageOrder = CCSX_NX; //逆序存储
							sPhysicalData = sDateContent.substring(0, iDataLen * 2);
							sDateContent = sDateContent.substring(iDataLen * 2,
									sDateContent.length());
							DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
									iDataLen, sDataFormat, iStorageType,
									iStorageOrder);
							DataItemInfo.SetFPhysicalData(sPhysicalData);
							DataItemInfoList.add(DataItemInfo);
						} else {
							iDataLen = 1; //物理数据的字符长度
							sDataFormat = "00"; //数据格式
							iStorageType = CCFS_HEX; //十六进制码
							iStorageOrder = CCSX_SX; //顺序存储
							sPhysicalData = sDateContent.substring(0,
									iDataLen * 2);
							if ((Integer.parseInt(sPhysicalData, 16) & 128) == 128) {
								iAlarmType = 10; //告警类型:10发生
							} else {
								iAlarmType = 20; //告警类型:20恢复
							}
							sPhysicalData = ""
									+ ((Integer.parseInt(sPhysicalData, 16) & 63)); //测量点号和总加组号加1
							sMeasuredPointNo = sPhysicalData;
							sPhysicalData = DataSwitch.IntToHex(sPhysicalData,
									"00");
							sDateContent = sDateContent.substring(iDataLen * 2,
									sDateContent.length());
							DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
									iDataLen, sDataFormat, iStorageType,
									iStorageOrder);
							DataItemInfo.SetFPhysicalData(sPhysicalData);
							DataItemInfoList.add(DataItemInfo);

							DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
							sDataCaption = "EC" + sAlarmCode + "02"; //异常标志
							iDataType = c_String; //字符型
							iDataLen = 1; //物理数据的字符长度
							sDataFormat = "1"; //数据格式
							iStorageType = CCFS_HEX; //十六进制码
							iStorageOrder = CCSX_SX; //顺序存储
							sPhysicalData = sDateContent.substring(0, iDataLen * 2);
							sDateContent = sDateContent.substring(iDataLen * 2,
									sDateContent.length());
							DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
									iDataLen, sDataFormat, iStorageType,
									iStorageOrder);
							DataItemInfo.SetFPhysicalData(sPhysicalData);
							DataItemInfoList.add(DataItemInfo);
							sAlarmSign = sPhysicalData;

							DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
							sDataCaption = "EC" + sAlarmCode + "03"; //发生时的Ua/Uab
							iDataType = c_Float; //字符型
							iDataLen = 2; //物理数据的字符长度
							sDataFormat = "000.0"; //数据格式
							iStorageType = CCFS_BCD; //BCD码
							iStorageOrder = CCSX_NX; //逆序存储
							sPhysicalData = sDateContent.substring(0, iDataLen * 2);
							sDateContent = sDateContent.substring(iDataLen * 2,
									sDateContent.length());
							DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
									iDataLen, sDataFormat, iStorageType,
									iStorageOrder);
							DataItemInfo.SetFPhysicalData(sPhysicalData);
							DataItemInfoList.add(DataItemInfo);

							DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
							sDataCaption = "EC" + sAlarmCode + "04"; //发生时的Ub   数据格式跟上一个一样
							sPhysicalData = sDateContent.substring(0, iDataLen * 2);
							sDateContent = sDateContent.substring(iDataLen * 2,
									sDateContent.length());
							DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
									iDataLen, sDataFormat, iStorageType,
									iStorageOrder);
							DataItemInfo.SetFPhysicalData(sPhysicalData);
							DataItemInfoList.add(DataItemInfo);

							DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
							sDataCaption = "EC" + sAlarmCode + "05"; //发生时的Uc/Ucb  数据格式跟上一个一样
							sPhysicalData = sDateContent.substring(0, iDataLen * 2);
							sDateContent = sDateContent.substring(iDataLen * 2,
									sDateContent.length());
							DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
									iDataLen, sDataFormat, iStorageType,
									iStorageOrder);
							DataItemInfo.SetFPhysicalData(sPhysicalData);
							DataItemInfoList.add(DataItemInfo);

							DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
							sDataCaption = "EC" + sAlarmCode + "06"; //发生时的Ia
							iDataType = c_FloatFH; //字符型
							iDataLen = 2; //物理数据的字符长度
							sDataFormat = "CC.CC"; //数据格式
							iStorageType = CCFS_BCD; //BCD码
							iStorageOrder = CCSX_NX; //逆序存储
							sPhysicalData = sDateContent.substring(0, iDataLen * 2);
							sDateContent = sDateContent.substring(iDataLen * 2,
									sDateContent.length());
							DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
									iDataLen, sDataFormat, iStorageType,
									iStorageOrder);
							DataItemInfo.SetFPhysicalData(sPhysicalData);
							DataItemInfoList.add(DataItemInfo);

							DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
							sDataCaption = "EC" + sAlarmCode + "07"; //发生时的Ib     数据格式跟上一个一样
							sPhysicalData = sDateContent.substring(0, iDataLen * 2);
							sDateContent = sDateContent.substring(iDataLen * 2,
									sDateContent.length());
							DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
									iDataLen, sDataFormat, iStorageType,
									iStorageOrder);
							DataItemInfo.SetFPhysicalData(sPhysicalData);
							DataItemInfoList.add(DataItemInfo);

							DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
							sDataCaption = "EC" + sAlarmCode + "08"; //发生时的Ic    数据格式跟上一个一样
							sPhysicalData = sDateContent.substring(0, iDataLen * 2);
							sDateContent = sDateContent.substring(iDataLen * 2,
									sDateContent.length());
							DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
									iDataLen, sDataFormat, iStorageType,
									iStorageOrder);
							DataItemInfo.SetFPhysicalData(sPhysicalData);
							DataItemInfoList.add(DataItemInfo);

							DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
							sDataCaption = "EC" + sAlarmCode + "09"; //发生时电能表正向有功总电能示值
							iDataType = c_Float; //字符型
							iDataLen = 5; //物理数据的字符长度
							sDataFormat = "000000.0000"; //数据格式
							iStorageType = CCFS_BCD; //BCD码
							iStorageOrder = CCSX_NX; //逆序存储
							sPhysicalData = sDateContent.substring(0, iDataLen * 2);
							sDateContent = sDateContent.substring(iDataLen * 2,
									sDateContent.length());
							DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
									iDataLen, sDataFormat, iStorageType,
									iStorageOrder);
							DataItemInfo.SetFPhysicalData(sPhysicalData);
							DataItemInfoList.add(DataItemInfo);
						}						

						if (!sAlarmSign.equals("")) {
							if (iAlarmCode == 9) {
								AlarmMaxCount = 3; //一个字节包含告警种类
							} else if (iAlarmCode == 10) {
								AlarmMaxCount = 2; //一个字节包含告警种类
							}
							AlarmCodeList = ExplainAlarmCodeQuanGuo(iAlarmType,
									sAlarmSign, Integer.toString(iAlarmCode),
									AlarmMaxCount);
						}
					} else if (iAlarmCode == 11 || iAlarmCode == 12) { //ERC11:相序异常,ERC12:电能表时间超差
						DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
						sDataCaption = "EC" + ("" + iAlarmCode) + "01"; //测量点号
						iDataType = c_Float; //字符型
						if (GYH == Glu_ConstDefine.GY_ZD_698) {
							iDataLen = 2; //物理数据的字符长度
							sDataFormat = "0000"; //数据格式
							iStorageType = CCFS_HEX; //十六进制码
							iStorageOrder = CCSX_SX; //顺序存储
							sPhysicalData = sDateContent.substring(0,
									iDataLen * 2);
							sPhysicalData = DataSwitch
									.ReverseStringByByte(sPhysicalData);
							if ((Integer.parseInt(sPhysicalData, 16) & 32768) == 32768) {
								sAlarmCode = "C" + ("" + iAlarmCode) + "0"; //发生
							} else {
								sAlarmCode = "R" + ("" + iAlarmCode) + "0"; //恢复
							}
							sPhysicalData = ""
									+ ((Integer.parseInt(sPhysicalData, 16) & 63)); //测量点号和总加组号加1
							sMeasuredPointNo = sPhysicalData;
							sPhysicalData = DataSwitch.IntToHex(sPhysicalData,
									"0000");
						} else {
							iDataLen = 1; //物理数据的字符长度
							sDataFormat = "00"; //数据格式
							iStorageType = CCFS_HEX; //十六进制码
							iStorageOrder = CCSX_SX; //顺序存储
							sPhysicalData = sDateContent.substring(0,
									iDataLen * 2);
							if ((Integer.parseInt(sPhysicalData, 16) & 128) == 128) {
								sAlarmCode = "C" + ("" + iAlarmCode) + "0"; //发生
							} else {
								sAlarmCode = "R" + ("" + iAlarmCode) + "0"; //恢复
							}
							sPhysicalData = ""
									+ ((Integer.parseInt(sPhysicalData, 16) & 63)); //测量点号和总加组号加1
							sMeasuredPointNo = sPhysicalData;
							sPhysicalData = DataSwitch.IntToHex(sPhysicalData,
									"00");
						}
						sDateContent = sDateContent.substring(iDataLen * 2,
								sDateContent.length());
						DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
								iDataLen, sDataFormat, iStorageType,
								iStorageOrder);
						DataItemInfo.SetFPhysicalData(sPhysicalData);
						DataItemInfoList.add(DataItemInfo);

						if (iAlarmCode == 11) {
							DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
							sDataCaption = "EC1102"; //∠Ua/Uab (单位：度)
							iDataType = c_FloatFH; //字符型
							iDataLen = 2; //物理数据的字符长度
							sDataFormat = "CCC.C"; //数据格式
							iStorageType = CCFS_BCD; //BCD码
							iStorageOrder = CCSX_NX; //逆序存储
							sPhysicalData = sDateContent.substring(0,
									iDataLen * 2);
							sDateContent = sDateContent.substring(iDataLen * 2,
									sDateContent.length());
							DataItemInfo.DataItemInfoAdd(sDataCaption,
									iDataType, iDataLen, sDataFormat,
									iStorageType, iStorageOrder);
							DataItemInfo.SetFPhysicalData(sPhysicalData);
							DataItemInfoList.add(DataItemInfo);

							DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
							sDataCaption = "EC1103"; //∠Ub (单位：度)
							sPhysicalData = sDateContent.substring(0,
									iDataLen * 2);
							sDateContent = sDateContent.substring(iDataLen * 2,
									sDateContent.length());
							DataItemInfo.DataItemInfoAdd(sDataCaption,
									iDataType, iDataLen, sDataFormat,
									iStorageType, iStorageOrder);
							DataItemInfo.SetFPhysicalData(sPhysicalData);
							DataItemInfoList.add(DataItemInfo);

							DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
							sDataCaption = "EC1104"; //∠Uc/Ucb (单位：度)
							sPhysicalData = sDateContent.substring(0,
									iDataLen * 2);
							sDateContent = sDateContent.substring(iDataLen * 2,
									sDateContent.length());
							DataItemInfo.DataItemInfoAdd(sDataCaption,
									iDataType, iDataLen, sDataFormat,
									iStorageType, iStorageOrder);
							DataItemInfo.SetFPhysicalData(sPhysicalData);
							DataItemInfoList.add(DataItemInfo);

							DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
							sDataCaption = "EC1105"; //∠Ia (单位：度)
							sPhysicalData = sDateContent.substring(0,
									iDataLen * 2);
							sDateContent = sDateContent.substring(iDataLen * 2,
									sDateContent.length());
							DataItemInfo.DataItemInfoAdd(sDataCaption,
									iDataType, iDataLen, sDataFormat,
									iStorageType, iStorageOrder);
							DataItemInfo.SetFPhysicalData(sPhysicalData);
							DataItemInfoList.add(DataItemInfo);

							DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
							sDataCaption = "EC1106"; //∠Ib (单位：度)
							sPhysicalData = sDateContent.substring(0,
									iDataLen * 2);
							sDateContent = sDateContent.substring(iDataLen * 2,
									sDateContent.length());
							DataItemInfo.DataItemInfoAdd(sDataCaption,
									iDataType, iDataLen, sDataFormat,
									iStorageType, iStorageOrder);
							DataItemInfo.SetFPhysicalData(sPhysicalData);
							DataItemInfoList.add(DataItemInfo);

							DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
							sDataCaption = "EC1107"; //∠Ic (单位：度)
							sPhysicalData = sDateContent.substring(0,
									iDataLen * 2);
							sDateContent = sDateContent.substring(iDataLen * 2,
									sDateContent.length());
							DataItemInfo.DataItemInfoAdd(sDataCaption,
									iDataType, iDataLen, sDataFormat,
									iStorageType, iStorageOrder);
							DataItemInfo.SetFPhysicalData(sPhysicalData);
							DataItemInfoList.add(DataItemInfo);

							DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
							sDataCaption = "EC1108"; //发生时电能表正向有功总电能示值
							iDataType = c_Float; //字符型
							iDataLen = 5; //物理数据的字符长度
							sDataFormat = "000000.0000"; //数据格式
							iStorageType = CCFS_BCD; //BCD码
							iStorageOrder = CCSX_NX; //逆序存储
							sPhysicalData = sDateContent.substring(0,
									iDataLen * 2);
							sDateContent = sDateContent.substring(iDataLen * 2,
									sDateContent.length());
							DataItemInfo.DataItemInfoAdd(sDataCaption,
									iDataType, iDataLen, sDataFormat,
									iStorageType, iStorageOrder);
							DataItemInfo.SetFPhysicalData(sPhysicalData);
							DataItemInfoList.add(DataItemInfo);
						}
						AlarmCodeList.add(sAlarmCode);
					} else if (iAlarmCode == 13) { //ERC13:电表故障信息
						DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
						sDataCaption = "EC1301"; //测量点号
						iDataType = c_Float; //字符型
						if (GYH == Glu_ConstDefine.GY_ZD_698) {
							iDataLen = 1; //物理数据的字符长度
							sDataFormat = "0000"; //数据格式
							iStorageType = CCFS_HEX; //十六进制码
							iStorageOrder = CCSX_SX; //顺序存储
							sPhysicalData = sDateContent.substring(0,
									iDataLen * 2);
							sPhysicalData = DataSwitch
									.ReverseStringByByte(sPhysicalData);
							sPhysicalData = ""
									+ ((Integer.parseInt(sPhysicalData, 16) & 63)); //测量点号和总加组号加1
							sMeasuredPointNo = sPhysicalData;
							sPhysicalData = DataSwitch.IntToHex(sPhysicalData,
									"0000");
						} else {
							iDataLen = 1; //物理数据的字符长度
							sDataFormat = "00"; //数据格式
							iStorageType = CCFS_HEX; //十六进制码
							iStorageOrder = CCSX_SX; //顺序存储
							sPhysicalData = sDateContent.substring(0,
									iDataLen * 2);
							sPhysicalData = ""
									+ ((Integer.parseInt(sPhysicalData, 16) & 63)); //测量点号和总加组号加1
							sMeasuredPointNo = sPhysicalData;
							sPhysicalData = DataSwitch.IntToHex(sPhysicalData,
									"00");
						}
						sDateContent = sDateContent.substring(iDataLen * 2,
								sDateContent.length());
						DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
								iDataLen, sDataFormat, iStorageType,
								iStorageOrder);
						DataItemInfo.SetFPhysicalData(sPhysicalData);
						DataItemInfoList.add(DataItemInfo);

						DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
						sDataCaption = "EC1302"; //异常标志
						iDataType = c_String; //字符型
						iDataLen = 1; //物理数据的字符长度
						sDataFormat = "1"; //数据格式
						iStorageType = CCFS_HEX; //十六进制码
						iStorageOrder = CCSX_SX; //顺序存储
						sPhysicalData = sDateContent.substring(0, iDataLen * 2);
						sDateContent = sDateContent.substring(iDataLen * 2,
								sDateContent.length());
						DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
								iDataLen, sDataFormat, iStorageType,
								iStorageOrder);
						DataItemInfo.SetFPhysicalData(sPhysicalData);
						DataItemInfoList.add(DataItemInfo);
						sAlarmSign = sPhysicalData;

						if (!sAlarmSign.equals("")) {
							iAlarmType = 1;
							AlarmMaxCount = 5; //一个字节包含告警种类
							AlarmCodeList = ExplainAlarmCodeQuanGuo(iAlarmType,
									sAlarmSign, Integer.toString(iAlarmCode),
									AlarmMaxCount);
						}
					} else if (iAlarmCode == 14) { //ERC14:终端停/上电事件
						DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
						sDataCaption = "EC1401"; //上电时间
						iDataType = c_DateTime; //字符型
						iDataLen = 5; //物理数据的字符长度
						sDataFormat = "YYMMDDHHNN"; //数据格式
						iStorageType = CCFS_BCD; //BCD码
						iStorageOrder = CCSX_NX; //逆序存储
						sPhysicalData = sDateContent.substring(0, iDataLen * 2);
						sDateContent = sDateContent.substring(iDataLen * 2,
								sDateContent.length());
						DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
								iDataLen, sDataFormat, iStorageType,
								iStorageOrder);
						DataItemInfo.SetFPhysicalData(sPhysicalData);
						DataItemInfoList.add(DataItemInfo);

						int iElectrifyTime = Integer.parseInt(DataSwitch
								.ReverseStringByByte(sPhysicalData)); //上电时间
						String sPowerCutTime = sAlarmDateTime; //停电时间
						int iAlarmDateTime = Integer.parseInt(sAlarmDateTime
								.substring(3, 12)); //异常发生时间
						if (iAlarmDateTime > iElectrifyTime) { //停电时间大于上电时间
							sAlarmCode = "C140"; //停电
						} else {
							sAlarmDateTime = "20"
									+ DataSwitch
											.ReverseStringByByte(sPhysicalData)
									+ "00"; //异常发生时间为上电时间,告警为上电告警
							sAlarmCode = "R140"; //上电
						}
						//特殊处理,增加一个额外的数据项:停电时间
						DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
						sDataCaption = "EC1402"; //停电时间
						iDataType = c_DateTime; //字符型
						iDataLen = 5; //物理数据的字符长度
						sDataFormat = "YYMMDDHHNN"; //数据格式
						iStorageType = CCFS_BCD; //BCD码
						iStorageOrder = CCSX_SX; //顺序存储
						sPhysicalData = sPowerCutTime.substring(2, 12);
						DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
								iDataLen, sDataFormat, iStorageType,
								iStorageOrder);
						DataItemInfo.SetFPhysicalData(sPhysicalData);
						DataItemInfoList.add(DataItemInfo);

						AlarmCodeList.add(sAlarmCode);
					} else if (iAlarmCode == 15) { //ERC15:谐波越限告警                    	
					} else if (iAlarmCode == 16) { //ERC16:直流模拟量越限记录
						DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
						sDataCaption = "EC1601"; //测量点号
						iDataType = c_Float; //字符型
						iDataLen = 1; //物理数据的字符长度
						sDataFormat = "00"; //数据格式
						iStorageType = CCFS_HEX; //十六进制码
						iStorageOrder = CCSX_SX; //顺序存储
						sPhysicalData = sDateContent.substring(0, iDataLen * 2);
						if ((Integer.parseInt(sPhysicalData, 16) & 128) == 128) {
							iAlarmType = 1; //发生
						} else {
							iAlarmType = 2; //恢复
						}
						sPhysicalData = ""
								+ ((Integer.parseInt(sPhysicalData, 16) & 63)); //测量点号和总加组号加1
						sMeasuredPointNo = sPhysicalData;
						sPhysicalData = DataSwitch
								.IntToHex(sPhysicalData, "00");
						sDateContent = sDateContent.substring(iDataLen * 2,
								sDateContent.length());
						DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
								iDataLen, sDataFormat, iStorageType,
								iStorageOrder);
						DataItemInfo.SetFPhysicalData(sPhysicalData);
						DataItemInfoList.add(DataItemInfo);

						DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
						sDataCaption = "EC1602"; //异常标志
						iDataType = c_String; //字符型
						iDataLen = 1; //物理数据的字符长度
						sDataFormat = "1"; //数据格式
						iStorageType = CCFS_HEX; //十六进制码
						iStorageOrder = CCSX_SX; //顺序存储
						sPhysicalData = sDateContent.substring(0, iDataLen * 2);
						sDateContent = sDateContent.substring(iDataLen * 2,
								sDateContent.length());
						DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
								iDataLen, sDataFormat, iStorageType,
								iStorageOrder);
						DataItemInfo.SetFPhysicalData(sPhysicalData);
						DataItemInfoList.add(DataItemInfo);
						sAlarmSign = sPhysicalData;

						DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
						sDataCaption = "EC1603"; //越限时直流模拟量数据
						iDataType = c_FloatEx; //特殊浮点型（对应国网规范格式2,带幂部）
						iDataLen = 2; //物理数据的字符长度
						sDataFormat = "A"; //数据格式
						iStorageType = CCFS_BCD; //BCD码
						iStorageOrder = CCSX_NX; //逆序存储
						sPhysicalData = sDateContent.substring(0, iDataLen * 2);
						sDateContent = sDateContent.substring(iDataLen * 2,
								sDateContent.length());
						DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
								iDataLen, sDataFormat, iStorageType,
								iStorageOrder);
						DataItemInfo.SetFPhysicalData(sPhysicalData);
						DataItemInfoList.add(DataItemInfo);

						if (!sAlarmSign.equals("")) {
							AlarmMaxCount = 2; //一个字节包含告警种类
							AlarmCodeList = ExplainAlarmCodeQuanGuo(iAlarmType,
									sAlarmSign, Integer.toString(iAlarmCode),
									AlarmMaxCount);
						}
					} else if (iAlarmCode == 17) { //ERC17:电压/电流不平衡度越限记录
						DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
						sDataCaption = "EC1701"; //测量点号
						iDataType = c_Float; //字符型
						if (GYH == Glu_ConstDefine.GY_ZD_698) {
							iDataLen = 2; //物理数据的字符长度
							sDataFormat = "0000"; //数据格式
							iStorageType = CCFS_HEX; //十六进制码
							iStorageOrder = CCSX_SX; //顺序存储
							sPhysicalData = sDateContent.substring(0,
									iDataLen * 2);
							sPhysicalData = DataSwitch
									.ReverseStringByByte(sPhysicalData);
							if ((Integer.parseInt(sPhysicalData, 16) & 32768) == 32768) {
								iAlarmType = 1; //发生
							} else {
								iAlarmType = 2; //恢复
							}
							sPhysicalData = ""
									+ ((Integer.parseInt(sPhysicalData, 16) & 63) ); //测量点号和总加组号加1
							sMeasuredPointNo = sPhysicalData;
							sPhysicalData = DataSwitch.IntToHex(sPhysicalData,
									"0000");
							
							sDateContent = sDateContent.substring(iDataLen * 2,
									sDateContent.length());
							DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
									iDataLen, sDataFormat, iStorageType,
									iStorageOrder);
							DataItemInfo.SetFPhysicalData(sPhysicalData);
							DataItemInfoList.add(DataItemInfo);

							DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
							sDataCaption = "EC1702"; //异常标志
							iDataType = c_String; //字符型
							iDataLen = 1; //物理数据的字符长度
							sDataFormat = "1"; //数据格式
							iStorageType = CCFS_HEX; //十六进制码
							iStorageOrder = CCSX_SX; //顺序存储
							sPhysicalData = sDateContent.substring(0, iDataLen * 2);
							sDateContent = sDateContent.substring(iDataLen * 2,
									sDateContent.length());
							DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
									iDataLen, sDataFormat, iStorageType,
									iStorageOrder);
							DataItemInfo.SetFPhysicalData(sPhysicalData);
							DataItemInfoList.add(DataItemInfo);
							sAlarmSign = sPhysicalData;

							DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
							sDataCaption = "EC1703"; //发生时的电压不平衡度
							iDataType = c_FloatFH; //带正负号的浮点型 (0为正号或是上浮，1为负号或是下浮)
							iDataLen = 2; //物理数据的字符长度
							sDataFormat = "CCC.C"; //数据格式
							iStorageType = CCFS_BCD; //BCD码
							iStorageOrder = CCSX_NX; //逆序存储
							sPhysicalData = sDateContent.substring(0, iDataLen * 2);
							sDateContent = sDateContent.substring(iDataLen * 2,
									sDateContent.length());
							DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
									iDataLen, sDataFormat, iStorageType,
									iStorageOrder);
							DataItemInfo.SetFPhysicalData(sPhysicalData);
							DataItemInfoList.add(DataItemInfo);

							DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
							sDataCaption = "EC1704"; //发生时的电流不平衡度  数据格式跟上一个一样
							sPhysicalData = sDateContent.substring(0, iDataLen * 2);
							sDateContent = sDateContent.substring(iDataLen * 2,
									sDateContent.length());
							DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
									iDataLen, sDataFormat, iStorageType,
									iStorageOrder);
							DataItemInfo.SetFPhysicalData(sPhysicalData);
							DataItemInfoList.add(DataItemInfo);

							DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
							sDataCaption = "EC1705"; //发生时的Ua/Uab
							iDataType = c_Float; //浮点型
							iDataLen = 2; //物理数据的字符长度
							sDataFormat = "000.0"; //数据格式
							iStorageType = CCFS_BCD; //BCD码
							iStorageOrder = CCSX_NX; //逆序存储
							sPhysicalData = sDateContent.substring(0, iDataLen * 2);
							sDateContent = sDateContent.substring(iDataLen * 2,
									sDateContent.length());
							DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
									iDataLen, sDataFormat, iStorageType,
									iStorageOrder);
							DataItemInfo.SetFPhysicalData(sPhysicalData);
							DataItemInfoList.add(DataItemInfo);

							DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
							sDataCaption = "EC1706"; //发生时的Ub    数据格式跟上一个一样
							sPhysicalData = sDateContent.substring(0, iDataLen * 2);
							sDateContent = sDateContent.substring(iDataLen * 2,
									sDateContent.length());
							DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
									iDataLen, sDataFormat, iStorageType,
									iStorageOrder);
							DataItemInfo.SetFPhysicalData(sPhysicalData);
							DataItemInfoList.add(DataItemInfo);

							DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
							sDataCaption = "EC1707"; //发生时的Uc/Ucb    数据格式跟上一个一样
							sPhysicalData = sDateContent.substring(0, iDataLen * 2);
							sDateContent = sDateContent.substring(iDataLen * 2,
									sDateContent.length());
							DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
									iDataLen, sDataFormat, iStorageType,
									iStorageOrder);
							DataItemInfo.SetFPhysicalData(sPhysicalData);
							DataItemInfoList.add(DataItemInfo);

							DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
							sDataCaption = "EC1708"; //发生时的Ia
							iDataType = c_FloatFH; //带正负号的浮点型 (0为正号或是上浮，1为负号或是下浮)
							iDataLen = 3; //物理数据的字符长度
							sDataFormat = "CCC.CCC"; //数据格式
							iStorageType = CCFS_BCD; //BCD码
							iStorageOrder = CCSX_NX; //逆序存储
							sPhysicalData = sDateContent.substring(0, iDataLen * 2);
							sDateContent = sDateContent.substring(iDataLen * 2,
									sDateContent.length());
							DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
									iDataLen, sDataFormat, iStorageType,
									iStorageOrder);
							DataItemInfo.SetFPhysicalData(sPhysicalData);
							DataItemInfoList.add(DataItemInfo);

							DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
							sDataCaption = "EC1709"; //发生时的Ib
							sPhysicalData = sDateContent.substring(0, iDataLen * 2);
							sDateContent = sDateContent.substring(iDataLen * 2,
									sDateContent.length());
							DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
									iDataLen, sDataFormat, iStorageType,
									iStorageOrder);
							DataItemInfo.SetFPhysicalData(sPhysicalData);
							DataItemInfoList.add(DataItemInfo);

							DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
							sDataCaption = "EC170A"; //发生时的Ic
							sPhysicalData = sDateContent.substring(0, iDataLen * 2);
							sDateContent = sDateContent.substring(iDataLen * 2,
									sDateContent.length());
							DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
									iDataLen, sDataFormat, iStorageType,
									iStorageOrder);
							DataItemInfo.SetFPhysicalData(sPhysicalData);
							DataItemInfoList.add(DataItemInfo);
						} else {
							iDataLen = 1; //物理数据的字符长度
							sDataFormat = "00"; //数据格式
							iStorageType = CCFS_HEX; //十六进制码
							iStorageOrder = CCSX_SX; //顺序存储
							sPhysicalData = sDateContent.substring(0,
									iDataLen * 2);
							if ((Integer.parseInt(sPhysicalData, 16) & 128) == 128) {
								iAlarmType = 1; //发生
							} else {
								iAlarmType = 2; //恢复
							}
							sPhysicalData = ""
									+ ((Integer.parseInt(sPhysicalData, 16) & 63)); //测量点号和总加组号加1
							sMeasuredPointNo = sPhysicalData;
							sPhysicalData = DataSwitch.IntToHex(sPhysicalData,
									"00");
							
							sDateContent = sDateContent.substring(iDataLen * 2,
									sDateContent.length());
							DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
									iDataLen, sDataFormat, iStorageType,
									iStorageOrder);
							DataItemInfo.SetFPhysicalData(sPhysicalData);
							DataItemInfoList.add(DataItemInfo);

							DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
							sDataCaption = "EC1702"; //异常标志
							iDataType = c_String; //字符型
							iDataLen = 1; //物理数据的字符长度
							sDataFormat = "1"; //数据格式
							iStorageType = CCFS_HEX; //十六进制码
							iStorageOrder = CCSX_SX; //顺序存储
							sPhysicalData = sDateContent.substring(0, iDataLen * 2);
							sDateContent = sDateContent.substring(iDataLen * 2,
									sDateContent.length());
							DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
									iDataLen, sDataFormat, iStorageType,
									iStorageOrder);
							DataItemInfo.SetFPhysicalData(sPhysicalData);
							DataItemInfoList.add(DataItemInfo);
							sAlarmSign = sPhysicalData;

							DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
							sDataCaption = "EC1703"; //发生时的电压不平衡度
							iDataType = c_FloatFH; //带正负号的浮点型 (0为正号或是上浮，1为负号或是下浮)
							iDataLen = 2; //物理数据的字符长度
							sDataFormat = "CCC.C"; //数据格式
							iStorageType = CCFS_BCD; //BCD码
							iStorageOrder = CCSX_NX; //逆序存储
							sPhysicalData = sDateContent.substring(0, iDataLen * 2);
							sDateContent = sDateContent.substring(iDataLen * 2,
									sDateContent.length());
							DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
									iDataLen, sDataFormat, iStorageType,
									iStorageOrder);
							DataItemInfo.SetFPhysicalData(sPhysicalData);
							DataItemInfoList.add(DataItemInfo);

							DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
							sDataCaption = "EC1704"; //发生时的电流不平衡度  数据格式跟上一个一样
							sPhysicalData = sDateContent.substring(0, iDataLen * 2);
							sDateContent = sDateContent.substring(iDataLen * 2,
									sDateContent.length());
							DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
									iDataLen, sDataFormat, iStorageType,
									iStorageOrder);
							DataItemInfo.SetFPhysicalData(sPhysicalData);
							DataItemInfoList.add(DataItemInfo);

							DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
							sDataCaption = "EC1705"; //发生时的Ua/Uab
							iDataType = c_Float; //浮点型
							iDataLen = 2; //物理数据的字符长度
							sDataFormat = "000.0"; //数据格式
							iStorageType = CCFS_BCD; //BCD码
							iStorageOrder = CCSX_NX; //逆序存储
							sPhysicalData = sDateContent.substring(0, iDataLen * 2);
							sDateContent = sDateContent.substring(iDataLen * 2,
									sDateContent.length());
							DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
									iDataLen, sDataFormat, iStorageType,
									iStorageOrder);
							DataItemInfo.SetFPhysicalData(sPhysicalData);
							DataItemInfoList.add(DataItemInfo);

							DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
							sDataCaption = "EC1706"; //发生时的Ub    数据格式跟上一个一样
							sPhysicalData = sDateContent.substring(0, iDataLen * 2);
							sDateContent = sDateContent.substring(iDataLen * 2,
									sDateContent.length());
							DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
									iDataLen, sDataFormat, iStorageType,
									iStorageOrder);
							DataItemInfo.SetFPhysicalData(sPhysicalData);
							DataItemInfoList.add(DataItemInfo);

							DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
							sDataCaption = "EC1707"; //发生时的Uc/Ucb    数据格式跟上一个一样
							sPhysicalData = sDateContent.substring(0, iDataLen * 2);
							sDateContent = sDateContent.substring(iDataLen * 2,
									sDateContent.length());
							DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
									iDataLen, sDataFormat, iStorageType,
									iStorageOrder);
							DataItemInfo.SetFPhysicalData(sPhysicalData);
							DataItemInfoList.add(DataItemInfo);

							DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
							sDataCaption = "EC1708"; //发生时的Ia
							iDataType = c_FloatFH; //带正负号的浮点型 (0为正号或是上浮，1为负号或是下浮)
							iDataLen = 2; //物理数据的字符长度
							sDataFormat = "CCC.C"; //数据格式
							iStorageType = CCFS_BCD; //BCD码
							iStorageOrder = CCSX_NX; //逆序存储
							sPhysicalData = sDateContent.substring(0, iDataLen * 2);
							sDateContent = sDateContent.substring(iDataLen * 2,
									sDateContent.length());
							DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
									iDataLen, sDataFormat, iStorageType,
									iStorageOrder);
							DataItemInfo.SetFPhysicalData(sPhysicalData);
							DataItemInfoList.add(DataItemInfo);

							DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
							sDataCaption = "EC1709"; //发生时的Ib
							sPhysicalData = sDateContent.substring(0, iDataLen * 2);
							sDateContent = sDateContent.substring(iDataLen * 2,
									sDateContent.length());
							DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
									iDataLen, sDataFormat, iStorageType,
									iStorageOrder);
							DataItemInfo.SetFPhysicalData(sPhysicalData);
							DataItemInfoList.add(DataItemInfo);

							DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
							sDataCaption = "EC170A"; //发生时的Ic
							sPhysicalData = sDateContent.substring(0, iDataLen * 2);
							sDateContent = sDateContent.substring(iDataLen * 2,
									sDateContent.length());
							DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
									iDataLen, sDataFormat, iStorageType,
									iStorageOrder);
							DataItemInfo.SetFPhysicalData(sPhysicalData);
							DataItemInfoList.add(DataItemInfo);
						}						

						if (!sAlarmSign.equals("")) {
							AlarmMaxCount = 2; //一个字节包含告警种类
							AlarmCodeList = ExplainAlarmCodeQuanGuo(iAlarmType,
									sAlarmSign, Integer.toString(iAlarmCode),
									AlarmMaxCount);
						}
					} else if (iAlarmCode == 18) { //ERC18:电容器投切自锁记录
						DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
						sDataCaption = "EC1801"; //测量点号
						iDataType = c_Float; //字符型
						if (GYH == Glu_ConstDefine.GY_ZD_698) {
							iDataLen = 2; //物理数据的字符长度
							sDataFormat = "0000"; //数据格式
							iStorageType = CCFS_HEX; //十六进制码
							iStorageOrder = CCSX_SX; //顺序存储
							sPhysicalData = sDateContent.substring(0,
									iDataLen * 2);
							sPhysicalData = DataSwitch
									.ReverseStringByByte(sPhysicalData);
							if ((Integer.parseInt(sPhysicalData, 16) & 32768) == 32768) {
								sAlarmCode = "C" + ("" + iAlarmCode) + "0"; //发生
							} else {
								sAlarmCode = "R" + ("" + iAlarmCode) + "0"; //恢复
							}
							sPhysicalData = ""
									+ ((Integer.parseInt(sPhysicalData, 16) & 4095)); //测量点号和总加组号加1
							sMeasuredPointNo = sPhysicalData;
							sPhysicalData = DataSwitch.IntToHex(sPhysicalData,
									"0000");
						} else {
							iDataLen = 1; //物理数据的字符长度
							sDataFormat = "00"; //数据格式
							iStorageType = CCFS_HEX; //十六进制码
							iStorageOrder = CCSX_SX; //顺序存储
							sPhysicalData = sDateContent.substring(0,
									iDataLen * 2);
							if ((Integer.parseInt(sPhysicalData, 16) & 128) == 128) {
								sAlarmCode = "C" + ("" + iAlarmCode) + "0"; //发生
							} else {
								sAlarmCode = "R" + ("" + iAlarmCode) + "0"; //恢复
							}
							sPhysicalData = ""
									+ ((Integer.parseInt(sPhysicalData, 16) & 63)); //测量点号和总加组号加1
							sMeasuredPointNo = sPhysicalData;
							sPhysicalData = DataSwitch.IntToHex(sPhysicalData,
									"00");
						}

						sDateContent = sDateContent.substring(iDataLen * 2,
								sDateContent.length());
						DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
								iDataLen, sDataFormat, iStorageType,
								iStorageOrder);
						DataItemInfo.SetFPhysicalData(sPhysicalData);
						DataItemInfoList.add(DataItemInfo);

						DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
						sDataCaption = "EC1802"; //异常标志
						iDataType = c_String; //字符型
						iDataLen = 1; //物理数据的字符长度
						sDataFormat = "1"; //数据格式
						iStorageType = CCFS_HEX; //十六进制码
						iStorageOrder = CCSX_SX; //顺序存储
						sPhysicalData = sDateContent.substring(0, iDataLen * 2);
						sDateContent = sDateContent.substring(iDataLen * 2,
								sDateContent.length());
						DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
								iDataLen, sDataFormat, iStorageType,
								iStorageOrder);
						DataItemInfo.SetFPhysicalData(sPhysicalData);
						DataItemInfoList.add(DataItemInfo);

						DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
						sDataCaption = "EC1803"; //电容器组标志
						iDataType = c_String; //字符型
						iDataLen = 2; //物理数据的字符长度
						sDataFormat = "2"; //数据格式
						iStorageType = CCFS_HEX; //十六进制码
						iStorageOrder = CCSX_SX; //顺序存储
						sPhysicalData = sDateContent.substring(0, iDataLen * 2);
						sDateContent = sDateContent.substring(iDataLen * 2,
								sDateContent.length());
						DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
								iDataLen, sDataFormat, iStorageType,
								iStorageOrder);
						DataItemInfo.SetFPhysicalData(sPhysicalData);
						DataItemInfoList.add(DataItemInfo);

						DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
						sDataCaption = "EC1804"; //越限发生时功率因数
						iDataType = c_FloatFH; //带正负号的浮点型 (0为正号或是上浮，1为负号或是下浮)
						iDataLen = 2; //物理数据的字符长度
						sDataFormat = "CCC.C"; //数据格式
						iStorageType = CCFS_BCD; //BCD码
						iStorageOrder = CCSX_NX; //逆序存储
						sPhysicalData = sDateContent.substring(0, iDataLen * 2);
						sDateContent = sDateContent.substring(iDataLen * 2,
								sDateContent.length());
						DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
								iDataLen, sDataFormat, iStorageType,
								iStorageOrder);
						DataItemInfo.SetFPhysicalData(sPhysicalData);
						DataItemInfoList.add(DataItemInfo);

						DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
						sDataCaption = "EC1805"; //越限发生时功率因数
						sPhysicalData = sDateContent.substring(0, iDataLen * 2);
						sDateContent = sDateContent.substring(iDataLen * 2,
								sDateContent.length());
						DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
								iDataLen, sDataFormat, iStorageType,
								iStorageOrder);
						DataItemInfo.SetFPhysicalData(sPhysicalData);
						DataItemInfoList.add(DataItemInfo);

						DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
						sDataCaption = "EC1806"; //越限发生时电压
						iDataType = c_Float; //浮点型
						iDataLen = 2; //物理数据的字符长度
						sDataFormat = "000.0"; //数据格式
						iStorageType = CCFS_BCD; //BCD码
						iStorageOrder = CCSX_NX; //逆序存储
						sPhysicalData = sDateContent.substring(0, iDataLen * 2);
						sDateContent = sDateContent.substring(iDataLen * 2,
								sDateContent.length());
						DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
								iDataLen, sDataFormat, iStorageType,
								iStorageOrder);
						DataItemInfo.SetFPhysicalData(sPhysicalData);
						DataItemInfoList.add(DataItemInfo);

						AlarmCodeList.add(sAlarmCode);
					} else if (iAlarmCode == 19) { //ERC19:购电参数设置记录
						DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
						sDataCaption = "EC1901"; //总加组号
						iDataType = c_Float; //字符型
						iDataLen = 1; //物理数据的字符长度
						sDataFormat = "00"; //数据格式
						iStorageType = CCFS_HEX; //十六进制码
						iStorageOrder = CCSX_SX; //顺序存储
						sPhysicalData = ""
								+ ((Integer.parseInt(sDateContent.substring(0,
										iDataLen * 2), 16) & 63) ); //测量点号和总加组号加1
						sMeasuredPointNo = sPhysicalData;
						iMeasuredPointType = 20; //总加组类型
						sPhysicalData = DataSwitch
								.IntToHex(sPhysicalData, "00");
						sDateContent = sDateContent.substring(iDataLen * 2,
								sDateContent.length());
						DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
								iDataLen, sDataFormat, iStorageType,
								iStorageOrder);
						DataItemInfo.SetFPhysicalData(sPhysicalData);
						DataItemInfoList.add(DataItemInfo);

						DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
						sDataCaption = "EC1902"; //购电单号
						iDataType = c_Float; //浮点型
						iDataLen = 4; //物理数据的字符长度
						sDataFormat = "00000000"; //数据格式
						iStorageType = CCFS_HEX; //十六进制码
						iStorageOrder = CCSX_NX; //逆序存储
						sPhysicalData = sDateContent.substring(0, iDataLen * 2);
						sDateContent = sDateContent.substring(iDataLen * 2,
								sDateContent.length());
						DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
								iDataLen, sDataFormat, iStorageType,
								iStorageOrder);
						DataItemInfo.SetFPhysicalData(sPhysicalData);
						DataItemInfoList.add(DataItemInfo);

						DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
						sDataCaption = "EC1903"; //追加/刷新标志
						iDataType = c_Float; //字符型
						iDataLen = 1; //物理数据的字符长度
						sDataFormat = "00"; //数据格式
						iStorageType = CCFS_HEX; //十六进制码
						iStorageOrder = CCSX_SX; //顺序存储
						sPhysicalData = sDateContent.substring(0, iDataLen * 2);
						sDateContent = sDateContent.substring(iDataLen * 2,
								sDateContent.length());
						DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
								iDataLen, sDataFormat, iStorageType,
								iStorageOrder);
						DataItemInfo.SetFPhysicalData(sPhysicalData);
						DataItemInfoList.add(DataItemInfo);

						DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
						sDataCaption = "EC1904"; //购电量值
						iDataType = c_IntDWFH; //带单位标志和正负号的整型值串
						iDataLen = 4; //物理数据的字符长度
						sDataFormat = "E"; //数据格式
						iStorageType = CCFS_BCD; //BCD码
						iStorageOrder = CCSX_NX; //逆序存储
						sPhysicalData = sDateContent.substring(0, iDataLen * 2);
						sDateContent = sDateContent.substring(iDataLen * 2,
								sDateContent.length());
						DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
								iDataLen, sDataFormat, iStorageType,
								iStorageOrder);
						DataItemInfo.SetFPhysicalData(sPhysicalData);
						DataItemInfoList.add(DataItemInfo);

						DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
						sDataCaption = "EC1905"; //报警门限
						sPhysicalData = sDateContent.substring(0, iDataLen * 2);
						sDateContent = sDateContent.substring(iDataLen * 2,
								sDateContent.length());
						DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
								iDataLen, sDataFormat, iStorageType,
								iStorageOrder);
						DataItemInfo.SetFPhysicalData(sPhysicalData);
						DataItemInfoList.add(DataItemInfo);

						DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
						sDataCaption = "EC1906"; //跳闸门限
						sPhysicalData = sDateContent.substring(0, iDataLen * 2);
						sDateContent = sDateContent.substring(iDataLen * 2,
								sDateContent.length());
						DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
								iDataLen, sDataFormat, iStorageType,
								iStorageOrder);
						DataItemInfo.SetFPhysicalData(sPhysicalData);
						DataItemInfoList.add(DataItemInfo);

						DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
						sDataCaption = "EC1907"; //本次购电前剩余电能量
						sPhysicalData = sDateContent.substring(0, iDataLen * 2);
						sDateContent = sDateContent.substring(iDataLen * 2,
								sDateContent.length());
						DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
								iDataLen, sDataFormat, iStorageType,
								iStorageOrder);
						DataItemInfo.SetFPhysicalData(sPhysicalData);
						DataItemInfoList.add(DataItemInfo);

						DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
						sDataCaption = "EC1908"; //本次购电后剩余电能量
						sPhysicalData = sDateContent.substring(0, iDataLen * 2);
						sDateContent = sDateContent.substring(iDataLen * 2,
								sDateContent.length());
						DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
								iDataLen, sDataFormat, iStorageType,
								iStorageOrder);
						DataItemInfo.SetFPhysicalData(sPhysicalData);
						DataItemInfoList.add(DataItemInfo);

						AlarmCodeList.add("C190");
					} else if (iAlarmCode == 20) { //ERC20:密码错误记录
						DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
						sDataCaption = "EC2001"; //错误密码
						iDataType = c_Float; //浮点型
						iDataLen = 2; //物理数据的字符长度
						sDataFormat = "0000"; //数据格式
						iStorageType = CCFS_HEX; //十六进制方式
						iStorageOrder = CCSX_NX; //逆序存储
						sPhysicalData = sDateContent.substring(0, iDataLen * 2);
						sDateContent = sDateContent.substring(iDataLen * 2,
								sDateContent.length());
						DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
								iDataLen, sDataFormat, iStorageType,
								iStorageOrder);
						DataItemInfo.SetFPhysicalData(sPhysicalData);
						DataItemInfoList.add(DataItemInfo);

						DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
						sDataCaption = "EC2002"; //启动站地址MSA
						iDataType = c_Float; //浮点型
						iDataLen = 1; //物理数据的字符长度
						sDataFormat = "00"; //数据格式
						iStorageType = CCFS_HEX; //十六进制方式
						iStorageOrder = CCSX_SX; //顺序存储
						sPhysicalData = sDateContent.substring(0, iDataLen * 2);
						sDateContent = sDateContent.substring(iDataLen * 2,
								sDateContent.length());
						DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
								iDataLen, sDataFormat, iStorageType,
								iStorageOrder);
						DataItemInfo.SetFPhysicalData(sPhysicalData);
						DataItemInfoList.add(DataItemInfo);

						AlarmCodeList.add("C200");
					} else if (iAlarmCode == 21) { //ERC21:终端故障记录
						DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
						sDataCaption = "EC2101"; //终端故障编码
						iDataType = c_Float; //浮点型
						iDataLen = 1; //物理数据的字符长度
						sDataFormat = "00"; //数据格式
						iStorageType = CCFS_HEX; //十六进制方式
						iStorageOrder = CCSX_SX; //顺序存储
						sPhysicalData = sDateContent.substring(0, iDataLen * 2);
						sDateContent = sDateContent.substring(iDataLen * 2,
								sDateContent.length());
						DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
								iDataLen, sDataFormat, iStorageType,
								iStorageOrder);
						DataItemInfo.SetFPhysicalData(sPhysicalData);
						DataItemInfoList.add(DataItemInfo);
						sAlarmSign = DataSwitch.HexToInt(sPhysicalData, "00");

						if (!sAlarmSign.equals("")) {
							if (Integer.parseInt(sAlarmSign) > 128) {
								sAlarmCode = "R21"
										+ Integer.toString(Integer
												.parseInt(sAlarmSign) & 7);
							} else {
								sAlarmCode = "C21"
										+ Integer.toString(Integer
												.parseInt(sAlarmSign) & 7);
							}
							AlarmCodeList.add(sAlarmCode);
						}
					} else if (iAlarmCode == 22) { //ERC22:有功总电能量差动越限事件记录
						DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
						sDataCaption = "EC2201"; //电能量差动组号
						iDataType = c_Float; //字符型
						iDataLen = 1; //物理数据的字符长度
						sDataFormat = "00"; //数据格式
						iStorageType = CCFS_HEX; //十六进制码
						iStorageOrder = CCSX_SX; //顺序存储
						sPhysicalData = sDateContent.substring(0, iDataLen * 2);
						if ((Integer.parseInt(sPhysicalData, 16) & 128) == 128) {
							sAlarmCode = "C" + ("" + iAlarmCode) + "0"; //发生
						} else {
							sAlarmCode = "R" + ("" + iAlarmCode) + "0"; //恢复
						}
						sPhysicalData = ""
								+ ((Integer.parseInt(sPhysicalData, 16) & 63)); //测量点号和总加组号加1   //暂时取消，保持和主站的一致
						sMeasuredPointNo = sPhysicalData;
						iMeasuredPointType = 20; //总加组类型
						sPhysicalData = DataSwitch
								.IntToHex(sPhysicalData, "00");
						sDateContent = sDateContent.substring(iDataLen * 2,
								sDateContent.length());
						DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
								iDataLen, sDataFormat, iStorageType,
								iStorageOrder);
						DataItemInfo.SetFPhysicalData(sPhysicalData);
						DataItemInfoList.add(DataItemInfo);

						DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
						sDataCaption = "EC2202"; //越限时对比总加组有功总电能量
						iDataType = c_IntDWFH; //浮点型
						iDataLen = 4; //物理数据的字符长度
						sDataFormat = "E"; //数据格式
						iStorageType = CCFS_BCD; //BCD码
						iStorageOrder = CCSX_NX; //逆序存储
						sPhysicalData = sDateContent.substring(0, iDataLen * 2);
						sDateContent = sDateContent.substring(iDataLen * 2,
								sDateContent.length());
						DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
								iDataLen, sDataFormat, iStorageType,
								iStorageOrder);
						DataItemInfo.SetFPhysicalData(sPhysicalData);
						DataItemInfoList.add(DataItemInfo);

						DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
						sDataCaption = "EC2203"; //越限时参照总加组有功总电能量
						iDataType = c_IntDWFH; //带单位标志和正负号的整型值串
						iDataLen = 4; //物理数据的字符长度
						sDataFormat = "E"; //数据格式
						iStorageType = CCFS_BCD; //BCD码
						iStorageOrder = CCSX_NX; //逆序存储
						sPhysicalData = sDateContent.substring(0, iDataLen * 2);
						sDateContent = sDateContent.substring(iDataLen * 2,
								sDateContent.length());
						DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
								iDataLen, sDataFormat, iStorageType,
								iStorageOrder);
						DataItemInfo.SetFPhysicalData(sPhysicalData);
						DataItemInfoList.add(DataItemInfo);

						DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
						sDataCaption = "EC2204"; //越限时差动越限相对偏差值
						iDataType = c_Float; //浮点型
						iDataLen = 1; //物理数据的字符长度
						sDataFormat = "00"; //数据格式
						iStorageType = CCFS_HEX; //十六进制码
						iStorageOrder = CCSX_NX; //逆序存储
						sPhysicalData = sDateContent.substring(0, iDataLen * 2);
						sDateContent = sDateContent.substring(iDataLen * 2,
								sDateContent.length());
						DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
								iDataLen, sDataFormat, iStorageType,
								iStorageOrder);
						DataItemInfo.SetFPhysicalData(sPhysicalData);
						DataItemInfoList.add(DataItemInfo);

						DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
						sDataCaption = "EC2205"; //越限时差动越限绝对偏差值
						iDataType = c_IntDWFH; //带单位标志和正负号的整型值串
						iDataLen = 4; //物理数据的字符长度
						sDataFormat = "E"; //数据格式
						iStorageType = CCFS_BCD; //BCD码
						iStorageOrder = CCSX_NX; //逆序存储
						sPhysicalData = sDateContent.substring(0, iDataLen * 2);
						sDateContent = sDateContent.substring(iDataLen * 2,
								sDateContent.length());
						DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
								iDataLen, sDataFormat, iStorageType,
								iStorageOrder);
						DataItemInfo.SetFPhysicalData(sPhysicalData);
						DataItemInfoList.add(DataItemInfo);

						DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
						sDataCaption = "EC2206"; //对比总加组测量点数量n
						iDataType = c_Float; //浮点型
						iDataLen = 1; //物理数据的字符长度
						sDataFormat = "00"; //数据格式
						iStorageType = CCFS_HEX; //BCD码
						iStorageOrder = CCSX_NX; //逆序存储
						sPhysicalData = sDateContent.substring(0, iDataLen * 2);
						sDateContent = sDateContent.substring(iDataLen * 2,
								sDateContent.length());
						DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
								iDataLen, sDataFormat, iStorageType,
								iStorageOrder);
						DataItemInfo.SetFPhysicalData(sPhysicalData);
						DataItemInfoList.add(DataItemInfo);
						iCount = Integer.parseInt(sPhysicalData, 16); //对比总加组测量点数量n

						DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
						sDataCaption = "EC2207"; //越限时对比总加组n个测量点有功总电能示值
						iDataType = c_Complex; //复合型
						iDataLen = iCount * 5; //数据字节长度
						sDataFormat = ""; //数据格式
						for (int j = 0; j < iCount; j++) {
							if (j == iCount - 1) { //最后一个
								sDataFormat = sDataFormat + "000000.0000,10,20";
							} else {
								sDataFormat = sDataFormat + "000000.0000,10,20"
										+ "#";
							}
						}
						iStorageType = CCFS_BCD; //十六进制码
						iStorageOrder = CCSX_SX; //顺序存储
						sPhysicalData = sDateContent.substring(0, iDataLen * 2);
						sDateContent = sDateContent.substring(iDataLen * 2,
								sDateContent.length());
						DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
								iDataLen, sDataFormat, iStorageType,
								iStorageOrder);
						DataItemInfo.SetFPhysicalData(sPhysicalData);
						DataItemInfoList.add(DataItemInfo);

						DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
						sDataCaption = "EC2208"; //参照总加组测量点数量m
						iDataType = c_Float; //浮点型
						iDataLen = 1; //物理数据的字符长度
						sDataFormat = "00"; //数据格式
						iStorageType = CCFS_HEX; //BCD码
						iStorageOrder = CCSX_NX; //逆序存储
						sPhysicalData = sDateContent.substring(0, iDataLen * 2);
						sDateContent = sDateContent.substring(iDataLen * 2,
								sDateContent.length());
						DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
								iDataLen, sDataFormat, iStorageType,
								iStorageOrder);
						DataItemInfo.SetFPhysicalData(sPhysicalData);
						DataItemInfoList.add(DataItemInfo);
						iCount = Integer.parseInt(sPhysicalData, 16); //参照总加组测量点数量m

						DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
						sDataCaption = "EC2209"; //越限时参照总加组m个测量点有功总电能示值
						iDataType = c_Complex; //复合型
						iDataLen = iCount * 5; //数据字节长度
						sDataFormat = ""; //数据格式
						for (int j = 0; j < iCount; j++) {
							if (j == iCount - 1) { //最后一个
								sDataFormat = sDataFormat + "000000.0000,10,20";
							} else {
								sDataFormat = sDataFormat + "000000.0000,10,20"
										+ "#";
							}
						}
						iStorageType = CCFS_BCD; //十六进制码
						iStorageOrder = CCSX_SX; //顺序存储
						sPhysicalData = sDateContent.substring(0, iDataLen * 2);
						sDateContent = sDateContent.substring(iDataLen * 2,
								sDateContent.length());
						DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
								iDataLen, sDataFormat, iStorageType,
								iStorageOrder);
						DataItemInfo.SetFPhysicalData(sPhysicalData);
						DataItemInfoList.add(DataItemInfo);

						AlarmCodeList.add(sAlarmCode);
					} else if (iAlarmCode == 24) { //ERC24:电压越限记录
						DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
						sDataCaption = "EC2401"; //测量点号
						iDataType = c_Float; //字符型
						if (GYH == Glu_ConstDefine.GY_ZD_698) {
							iDataLen = 2; //物理数据的字符长度
							sDataFormat = "0000"; //数据格式
							iStorageType = CCFS_HEX; //十六进制码
							iStorageOrder = CCSX_SX; //顺序存储
							sPhysicalData = sDateContent.substring(0,
									iDataLen * 2);
							sPhysicalData = DataSwitch
									.ReverseStringByByte(sPhysicalData);
							if ((Integer.parseInt(sPhysicalData, 16) & 32768) == 32768) {
								iAlarmType = 10; //发生
							} else {
								iAlarmType = 20; //恢复
							}
							sPhysicalData = ""
									+ ((Integer.parseInt(sPhysicalData, 16) & 4095)); //测量点号和总加组号加1
							sMeasuredPointNo = sPhysicalData;
							sPhysicalData = DataSwitch.IntToHex(sPhysicalData,
									"0000");
						} else {
							iDataLen = 1; //物理数据的字符长度
							sDataFormat = "00"; //数据格式
							iStorageType = CCFS_HEX; //十六进制码
							iStorageOrder = CCSX_SX; //顺序存储
							sPhysicalData = sDateContent.substring(0,
									iDataLen * 2);
							if ((Integer.parseInt(sPhysicalData, 16) & 128) == 128) {
								iAlarmType = 10; //发生
							} else {
								iAlarmType = 20; //恢复
							}
							sPhysicalData = ""
									+ ((Integer.parseInt(sPhysicalData, 16) & 63)); //测量点号和总加组号加1   //暂时取消，保持和主站的一致
							sMeasuredPointNo = sPhysicalData;
							sPhysicalData = DataSwitch.IntToHex(sPhysicalData,
									"00");
						}

						sDateContent = sDateContent.substring(iDataLen * 2,
								sDateContent.length());
						DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
								iDataLen, sDataFormat, iStorageType,
								iStorageOrder);
						DataItemInfo.SetFPhysicalData(sPhysicalData);
						DataItemInfoList.add(DataItemInfo);

						DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
						sDataCaption = "EC2402"; //越限标志
						iDataType = c_String; //字符型
						iDataLen = 1; //物理数据的字符长度
						sDataFormat = "1"; //数据格式
						iStorageType = CCFS_HEX; //十六进制
						iStorageOrder = CCSX_SX; //顺序存储
						sPhysicalData = sDateContent.substring(0, iDataLen * 2);
						sDateContent = sDateContent.substring(iDataLen * 2,
								sDateContent.length());
						DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
								iDataLen, sDataFormat, iStorageType,
								iStorageOrder);
						DataItemInfo.SetFPhysicalData(sPhysicalData);
						DataItemInfoList.add(DataItemInfo);
						sAlarmSign = sPhysicalData;

						DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
						sDataCaption = "EC2403"; //发生时的Ua/Uab
						iDataType = c_Float; //浮点型
						iDataLen = 2; //物理数据的字符长度
						sDataFormat = "000.0"; //数据格式
						iStorageType = CCFS_BCD; //BCD码
						iStorageOrder = CCSX_NX; //顺序存储
						sPhysicalData = sDateContent.substring(0, iDataLen * 2);
						sDateContent = sDateContent.substring(iDataLen * 2,
								sDateContent.length());
						DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
								iDataLen, sDataFormat, iStorageType,
								iStorageOrder);
						DataItemInfo.SetFPhysicalData(sPhysicalData);
						DataItemInfoList.add(DataItemInfo);

						DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
						sDataCaption = "EC2404"; //发生时的Ub
						sPhysicalData = sDateContent.substring(0, iDataLen * 2);
						sDateContent = sDateContent.substring(iDataLen * 2,
								sDateContent.length());
						DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
								iDataLen, sDataFormat, iStorageType,
								iStorageOrder);
						DataItemInfo.SetFPhysicalData(sPhysicalData);
						DataItemInfoList.add(DataItemInfo);

						DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
						sDataCaption = "EC2405"; //发生时的Uc/Ucb
						sPhysicalData = sDateContent.substring(0, iDataLen * 2);
						sDateContent = sDateContent.substring(iDataLen * 2,
								sDateContent.length());
						DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
								iDataLen, sDataFormat, iStorageType,
								iStorageOrder);
						DataItemInfo.SetFPhysicalData(sPhysicalData);
						DataItemInfoList.add(DataItemInfo);

						if (!sAlarmSign.equals("")) {
							AlarmMaxCount = 2; //一个字节包含告警种类
							AlarmCodeList = ExplainAlarmCodeQuanGuo(iAlarmType,
									sAlarmSign, Integer.toString(iAlarmCode),
									AlarmMaxCount);
						}
					} else if (iAlarmCode == 25) { //ERC25:电流越限记录
						DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
						sDataCaption = "EC2501"; //测量点号
						iDataType = c_Float; //字符型
						if (GYH == Glu_ConstDefine.GY_ZD_698) {
							iDataLen = 2; //物理数据的字符长度
							sDataFormat = "0000"; //数据格式
							iStorageType = CCFS_HEX; //十六进制码
							iStorageOrder = CCSX_SX; //顺序存储
							sPhysicalData = sDateContent.substring(0,
									iDataLen * 2);
							sPhysicalData = DataSwitch
									.ReverseStringByByte(sPhysicalData);
							if ((Integer.parseInt(sPhysicalData, 16) & 32768) == 32768) {
								iAlarmType = 10; //发生
							} else {
								iAlarmType = 20; //恢复
							}
							sPhysicalData = ""
									+ ((Integer.parseInt(sPhysicalData, 16) & 4095)); //测量点号和总加组号加1
							sMeasuredPointNo = sPhysicalData;
							sPhysicalData = DataSwitch.IntToHex(sPhysicalData,
									"0000");
							
							sDateContent = sDateContent.substring(iDataLen * 2,
									sDateContent.length());
							DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
									iDataLen, sDataFormat, iStorageType,
									iStorageOrder);
							DataItemInfo.SetFPhysicalData(sPhysicalData);
							DataItemInfoList.add(DataItemInfo);

							DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
							sDataCaption = "EC2502"; //越限标志
							iDataType = c_String; //字符型
							iDataLen = 1; //物理数据的字符长度
							sDataFormat = "1"; //数据格式
							iStorageType = CCFS_HEX; //十六进制
							iStorageOrder = CCSX_SX; //顺序存储
							sPhysicalData = sDateContent.substring(0, iDataLen * 2);
							sDateContent = sDateContent.substring(iDataLen * 2,
									sDateContent.length());
							DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
									iDataLen, sDataFormat, iStorageType,
									iStorageOrder);
							DataItemInfo.SetFPhysicalData(sPhysicalData);
							DataItemInfoList.add(DataItemInfo);
							sAlarmSign = sPhysicalData;

							DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
							sDataCaption = "EC2503"; //发生时的Ia
							iDataType = c_FloatFH; //浮点型
							iDataLen = 3; //物理数据的字符长度
							sDataFormat = "CCC.CCC"; //数据格式
							iStorageType = CCFS_BCD; //BCD码
							iStorageOrder = CCSX_NX; //逆序存储
							sPhysicalData = sDateContent.substring(0, iDataLen * 2);
							sDateContent = sDateContent.substring(iDataLen * 2,
									sDateContent.length());
							DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
									iDataLen, sDataFormat, iStorageType,
									iStorageOrder);
							DataItemInfo.SetFPhysicalData(sPhysicalData);
							DataItemInfoList.add(DataItemInfo);

							DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
							sDataCaption = "EC2504"; //发生时的Ib
							sPhysicalData = sDateContent.substring(0, iDataLen * 2);
							sDateContent = sDateContent.substring(iDataLen * 2,
									sDateContent.length());
							DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
									iDataLen, sDataFormat, iStorageType,
									iStorageOrder);
							DataItemInfo.SetFPhysicalData(sPhysicalData);
							DataItemInfoList.add(DataItemInfo);

							DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
							sDataCaption = "EC2405"; //发生时的Ic
							sPhysicalData = sDateContent.substring(0, iDataLen * 2);
							sDateContent = sDateContent.substring(iDataLen * 2,
									sDateContent.length());
							DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
									iDataLen, sDataFormat, iStorageType,
									iStorageOrder);
							DataItemInfo.SetFPhysicalData(sPhysicalData);
							DataItemInfoList.add(DataItemInfo);
						} else {
							iDataLen = 1; //物理数据的字符长度
							sDataFormat = "00"; //数据格式
							iStorageType = CCFS_HEX; //十六进制码
							iStorageOrder = CCSX_SX; //顺序存储
							sPhysicalData = sDateContent.substring(0,
									iDataLen * 2);
							if ((Integer.parseInt(sPhysicalData, 16) & 128) == 128) {
								iAlarmType = 10; //发生
							} else {
								iAlarmType = 20; //恢复
							}
							sPhysicalData = ""
									+ ((Integer.parseInt(sPhysicalData, 16) & 63)); //测量点号和总加组号加1   //暂时取消，保持和主站的一致
							sMeasuredPointNo = sPhysicalData;
							sPhysicalData = DataSwitch.IntToHex(sPhysicalData,
									"00");
							
							sDateContent = sDateContent.substring(iDataLen * 2,
									sDateContent.length());
							DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
									iDataLen, sDataFormat, iStorageType,
									iStorageOrder);
							DataItemInfo.SetFPhysicalData(sPhysicalData);
							DataItemInfoList.add(DataItemInfo);

							DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
							sDataCaption = "EC2502"; //越限标志
							iDataType = c_String; //字符型
							iDataLen = 1; //物理数据的字符长度
							sDataFormat = "1"; //数据格式
							iStorageType = CCFS_HEX; //十六进制
							iStorageOrder = CCSX_SX; //顺序存储
							sPhysicalData = sDateContent.substring(0, iDataLen * 2);
							sDateContent = sDateContent.substring(iDataLen * 2,
									sDateContent.length());
							DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
									iDataLen, sDataFormat, iStorageType,
									iStorageOrder);
							DataItemInfo.SetFPhysicalData(sPhysicalData);
							DataItemInfoList.add(DataItemInfo);
							sAlarmSign = sPhysicalData;

							DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
							sDataCaption = "EC2503"; //发生时的Ia
							iDataType = c_FloatFH; //浮点型
							iDataLen = 2; //物理数据的字符长度
							sDataFormat = "CC.CC"; //数据格式
							iStorageType = CCFS_BCD; //BCD码
							iStorageOrder = CCSX_NX; //逆序存储
							sPhysicalData = sDateContent.substring(0, iDataLen * 2);
							sDateContent = sDateContent.substring(iDataLen * 2,
									sDateContent.length());
							DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
									iDataLen, sDataFormat, iStorageType,
									iStorageOrder);
							DataItemInfo.SetFPhysicalData(sPhysicalData);
							DataItemInfoList.add(DataItemInfo);

							DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
							sDataCaption = "EC2504"; //发生时的Ib
							sPhysicalData = sDateContent.substring(0, iDataLen * 2);
							sDateContent = sDateContent.substring(iDataLen * 2,
									sDateContent.length());
							DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
									iDataLen, sDataFormat, iStorageType,
									iStorageOrder);
							DataItemInfo.SetFPhysicalData(sPhysicalData);
							DataItemInfoList.add(DataItemInfo);

							DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
							sDataCaption = "EC2405"; //发生时的Ic
							sPhysicalData = sDateContent.substring(0, iDataLen * 2);
							sDateContent = sDateContent.substring(iDataLen * 2,
									sDateContent.length());
							DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
									iDataLen, sDataFormat, iStorageType,
									iStorageOrder);
							DataItemInfo.SetFPhysicalData(sPhysicalData);
							DataItemInfoList.add(DataItemInfo);
						}

						if (!sAlarmSign.equals("")) {
							AlarmMaxCount = 2; //一个字节包含告警种类
							AlarmCodeList = ExplainAlarmCodeQuanGuo(iAlarmType,
									sAlarmSign, Integer.toString(iAlarmCode),
									AlarmMaxCount);
						}
					} else if (iAlarmCode == 26) { //ERC26:视在功率越限记录
						DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
						sDataCaption = "EC2601"; //测量点号
						iDataType = c_Float; //字符型
						if (GYH == Glu_ConstDefine.GY_ZD_698) {
							iDataLen = 2; //物理数据的字符长度
							sDataFormat = "0000"; //数据格式
							iStorageType = CCFS_HEX; //十六进制码
							iStorageOrder = CCSX_SX; //顺序存储
							sPhysicalData = sDateContent.substring(0,
									iDataLen * 2);
							sPhysicalData = DataSwitch
									.ReverseStringByByte(sPhysicalData);
							if ((Integer.parseInt(sPhysicalData, 16) & 32768) == 32768) {
								iAlarmType = 1; //发生
							} else {
								iAlarmType = 2; //恢复
							}
							sPhysicalData = ""
									+ ((Integer.parseInt(sPhysicalData, 16) & 4095)); //测量点号和总加组号加1
							sMeasuredPointNo = sPhysicalData;
							sPhysicalData = DataSwitch.IntToHex(sPhysicalData,
									"0000");
						} else {
							iDataLen = 1; //物理数据的字符长度
							sDataFormat = "00"; //数据格式
							iStorageType = CCFS_HEX; //十六进制码
							iStorageOrder = CCSX_SX; //顺序存储
							sPhysicalData = sDateContent.substring(0,
									iDataLen * 2);
							if ((Integer.parseInt(sPhysicalData, 16) & 128) == 128) {
								iAlarmType = 1; //发生
							} else {
								iAlarmType = 2; //恢复
							}
							sPhysicalData = ""
									+ ((Integer.parseInt(sPhysicalData, 16) & 63)); //测量点号和总加组号加1   //暂时取消，保持和主站的一致
							sMeasuredPointNo = sPhysicalData;
							sPhysicalData = DataSwitch.IntToHex(sPhysicalData,
									"00");
						}

						sDateContent = sDateContent.substring(iDataLen * 2,
								sDateContent.length());
						DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
								iDataLen, sDataFormat, iStorageType,
								iStorageOrder);
						DataItemInfo.SetFPhysicalData(sPhysicalData);
						DataItemInfoList.add(DataItemInfo);

						DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
						sDataCaption = "EC2602"; //越限标志
						iDataType = c_String; //字符型
						iDataLen = 1; //物理数据的字符长度
						sDataFormat = "1"; //数据格式
						iStorageType = CCFS_HEX; //十六进制
						iStorageOrder = CCSX_SX; //顺序存储
						sPhysicalData = sDateContent.substring(0, iDataLen * 2);
						sDateContent = sDateContent.substring(iDataLen * 2,
								sDateContent.length());
						DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
								iDataLen, sDataFormat, iStorageType,
								iStorageOrder);
						DataItemInfo.SetFPhysicalData(sPhysicalData);
						DataItemInfoList.add(DataItemInfo);
						sAlarmSign = sPhysicalData;

						DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
						sDataCaption = "EC2603"; //发生时的视在功率
						iDataType = c_FloatFH; //浮点型
						iDataLen = 3; //物理数据的字符长度
						sDataFormat = "CC.CCCC"; //数据格式
						iStorageType = CCFS_BCD; //BCD码
						iStorageOrder = CCSX_NX; //逆序存储
						sPhysicalData = sDateContent.substring(0, iDataLen * 2);
						sDateContent = sDateContent.substring(iDataLen * 2,
								sDateContent.length());
						DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
								iDataLen, sDataFormat, iStorageType,
								iStorageOrder);
						DataItemInfo.SetFPhysicalData(sPhysicalData);
						DataItemInfoList.add(DataItemInfo);

						DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
						sDataCaption = "EC2604"; //发生时的视在功率限值
						sPhysicalData = sDateContent.substring(0, iDataLen * 2);
						sDateContent = sDateContent.substring(iDataLen * 2,
								sDateContent.length());
						DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
								iDataLen, sDataFormat, iStorageType,
								iStorageOrder);
						DataItemInfo.SetFPhysicalData(sPhysicalData);
						DataItemInfoList.add(DataItemInfo);

						if (!sAlarmSign.equals("")) {
							if (Integer.parseInt(sAlarmSign, 16) == 64) { //发生越有功功率上上限
								if (iAlarmType == 1) { //发生
									sAlarmCode = "C261";
								} else {
									sAlarmCode = "R261";
								}
							} else if (Integer.parseInt(sAlarmSign, 16) == 128) { //发生越有功功率上限
								if (iAlarmType == 1) { //发生
									sAlarmCode = "C262";
								} else {
									sAlarmCode = "R262";
								}
							}
						}
						AlarmCodeList.add(sAlarmCode);
					} else if (iAlarmCode == 27 || iAlarmCode == 28
							|| iAlarmCode == 29 || iAlarmCode == 30) { //ERC27:电能表示度下降记录;ERC28:电能量超差记录;ERC29:电能表飞走记录；ERC30:电能表停走记录
						DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
						sDataCaption = "EC" + ("" + iAlarmCode) + "01"; //测量点号
						iDataType = c_Float; //字符型
						if (GYH == Glu_ConstDefine.GY_ZD_698) {
							iDataLen = 2; //物理数据的字符长度
							sDataFormat = "0000"; //数据格式
							iStorageType = CCFS_HEX; //十六进制码
							iStorageOrder = CCSX_SX; //顺序存储
							sPhysicalData = sDateContent.substring(0,
									iDataLen * 2);
							sPhysicalData = DataSwitch
									.ReverseStringByByte(sPhysicalData);
							sPhysicalData = ""
									+ ((Integer.parseInt(sPhysicalData, 16) & 4095)); //测量点号和总加组号加1
							sMeasuredPointNo = sPhysicalData;
							sPhysicalData = DataSwitch.IntToHex(sPhysicalData,
									"0000");
						} else {
							iDataLen = 1; //物理数据的字符长度
							sDataFormat = "00"; //数据格式
							iStorageType = CCFS_HEX; //十六进制码
							iStorageOrder = CCSX_SX; //顺序存储
							sPhysicalData = ""
									+ ((Integer.parseInt(sDateContent
											.substring(0, iDataLen * 2), 16) & 63)); //测量点号和总加组号加1   //暂时取消，保持和主站的一致
							sMeasuredPointNo = sPhysicalData;
							sPhysicalData = DataSwitch.IntToHex(sPhysicalData,
									"00");
						}

						sDateContent = sDateContent.substring(iDataLen * 2,
								sDateContent.length());
						DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
								iDataLen, sDataFormat, iStorageType,
								iStorageOrder);
						DataItemInfo.SetFPhysicalData(sPhysicalData);
						DataItemInfoList.add(DataItemInfo);

						DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
						sDataCaption = "EC" + ("" + iAlarmCode) + "02"; //下降前电能表正向有功总电能示值;电能量时超差发生时对应正向有功总电能示值;电能表飞走发生前正向有功总电能示值；电能表停走发生时正向有功总电能示值
						iDataType = c_Float; //字符型
						iDataLen = 5; //物理数据的字符长度
						sDataFormat = "000000.0000"; //数据格式
						iStorageType = CCFS_BCD; //BCD码
						iStorageOrder = CCSX_NX; //逆序存储
						sPhysicalData = sDateContent.substring(0, iDataLen * 2);
						sDateContent = sDateContent.substring(iDataLen * 2,
								sDateContent.length());
						DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
								iDataLen, sDataFormat, iStorageType,
								iStorageOrder);
						DataItemInfo.SetFPhysicalData(sPhysicalData);
						DataItemInfoList.add(DataItemInfo);

						if (iAlarmCode != 30) {
							DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
							sDataCaption = "EC" + ("" + iAlarmCode) + "03"; //下降后电能表正向有功总电能示值;电能量超差发生时正向有功总电能示值;电能表飞走发生后正向有功总电能示值
							sPhysicalData = sDateContent.substring(0,
									iDataLen * 2);
							sDateContent = sDateContent.substring(iDataLen * 2,
									sDateContent.length());
							DataItemInfo.DataItemInfoAdd(sDataCaption,
									iDataType, iDataLen, sDataFormat,
									iStorageType, iStorageOrder);
							DataItemInfo.SetFPhysicalData(sPhysicalData);
							DataItemInfoList.add(DataItemInfo);
						}

						if (iAlarmCode != 27) { //电能表飞走记录
							DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
							if (iAlarmCode == 30) {
								sDataCaption = "EC" + ("" + iAlarmCode) + "03"; //电能表飞走阈值
							} else {
								sDataCaption = "EC" + ("" + iAlarmCode) + "04"; //电能表飞走阈值
							}
							iDataType = c_Float; //浮点型
							iDataLen = 1; //物理数据的字符长度
							sDataFormat = "00"; //数据格式
							iStorageType = CCFS_HEX; //十六进制码
							iStorageOrder = CCSX_SX; //顺序存储
							sPhysicalData = sDateContent.substring(0,
									iDataLen * 2);
							sDateContent = sDateContent.substring(iDataLen * 2,
									sDateContent.length());
							DataItemInfo.DataItemInfoAdd(sDataCaption,
									iDataType, iDataLen, sDataFormat,
									iStorageType, iStorageOrder);
							DataItemInfo.SetFPhysicalData(sPhysicalData);
							DataItemInfoList.add(DataItemInfo);
						}
						sAlarmCode = "C" + ("" + iAlarmCode) + "0";
						AlarmCodeList.add(sAlarmCode);
					} else if (iAlarmCode == 36) { //ERC36:频率越限事件
						DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
						sDataCaption = "EC3601"; //测量点号
						iDataType = c_Float; //字符型
						iDataLen = 2; //物理数据的字符长度
						sDataFormat = "0000"; //数据格式
						iStorageType = CCFS_HEX; //十六进制码
						iStorageOrder = CCSX_SX; //顺序存储
						sPhysicalData = sDateContent.substring(0, iDataLen * 2);
						sPhysicalData = DataSwitch
								.ReverseStringByByte(sPhysicalData);
						if ((Integer.parseInt(sPhysicalData, 16) & 32768) == 32768) {
							iAlarmType = 1; //发生
						} else {
							iAlarmType = 2; //恢复
						}
						sPhysicalData = ""
								+ ((Integer.parseInt(sPhysicalData, 16) & 4095)); //测量点号和总加组号加1   //暂时取消，保持和主站的一致
						sMeasuredPointNo = sPhysicalData;
						sPhysicalData = DataSwitch.IntToHex(sPhysicalData,
								"0000");
						sDateContent = sDateContent.substring(iDataLen * 2,
								sDateContent.length());
						DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
								iDataLen, sDataFormat, iStorageType,
								iStorageOrder);
						DataItemInfo.SetFPhysicalData(sPhysicalData);
						DataItemInfoList.add(DataItemInfo);

						DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
						sDataCaption = "EC3602"; //发生时的频率
						iDataType = c_FloatFH; //字符型
						iDataLen = 2; //物理数据的字符长度
						sDataFormat = "CC.CC"; //数据格式
						iStorageType = CCFS_BCD; //BCD码
						iStorageOrder = CCSX_NX; //逆序存储
						sPhysicalData = sDateContent.substring(0, iDataLen * 2);
						sDateContent = sDateContent.substring(iDataLen * 2,
								sDateContent.length());
						DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
								iDataLen, sDataFormat, iStorageType,
								iStorageOrder);
						DataItemInfo.SetFPhysicalData(sPhysicalData);
						DataItemInfoList.add(DataItemInfo);

						DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
						sDataCaption = "EC3603"; //发生时的频率限值                        
						sPhysicalData = sDateContent.substring(0, iDataLen * 2);
						sDateContent = sDateContent.substring(iDataLen * 2,
								sDateContent.length());
						DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
								iDataLen, sDataFormat, iStorageType,
								iStorageOrder);
						DataItemInfo.SetFPhysicalData(sPhysicalData);
						DataItemInfoList.add(DataItemInfo);

						sAlarmCode = "C" + ("" + iAlarmCode) + "0";
						AlarmCodeList.add(sAlarmCode);
					} else if (iAlarmCode == 37) { //ERC37:电压谐波越限事件
						DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
						sDataCaption = "EC3701"; //测量点号
						iDataType = c_Float; //字符型
						iDataLen = 2; //物理数据的字符长度
						sDataFormat = "0000"; //数据格式
						iStorageType = CCFS_HEX; //十六进制码
						iStorageOrder = CCSX_SX; //顺序存储
						sPhysicalData = sDateContent.substring(0, iDataLen * 2);
						sPhysicalData = DataSwitch
								.ReverseStringByByte(sPhysicalData);
						sAlarmSign = ""
								+ (Integer.parseInt(sPhysicalData, 16) & 28672);//0x7000
						if ((Integer.parseInt(sPhysicalData, 16) & 32768) == 32768) {
							iAlarmType = 1; //发生
						} else {
							iAlarmType = 2; //恢复
						}
						sPhysicalData = ""
								+ ((Integer.parseInt(sPhysicalData, 16) & 4095)); //测量点号和总加组号加1   //暂时取消，保持和主站的一致
						sMeasuredPointNo = sPhysicalData;
						sPhysicalData = DataSwitch.IntToHex(sPhysicalData,
								"0000");
						sDateContent = sDateContent.substring(iDataLen * 2,
								sDateContent.length());
						DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
								iDataLen, sDataFormat, iStorageType,
								iStorageOrder);
						DataItemInfo.SetFPhysicalData(sPhysicalData);
						DataItemInfoList.add(DataItemInfo);

						DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
						sDataCaption = "EC3702"; //谐波次数
						iDataType = c_Float; //字符型
						iDataLen = 1; //物理数据的字符长度
						sDataFormat = "00"; //数据格式
						iStorageType = CCFS_HEX; //HEX码
						iStorageOrder = CCSX_NX; //逆序存储
						sPhysicalData = sDateContent.substring(0, iDataLen * 2);
						sDateContent = sDateContent.substring(iDataLen * 2,
								sDateContent.length());
						DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
								iDataLen, sDataFormat, iStorageType,
								iStorageOrder);
						DataItemInfo.SetFPhysicalData(sPhysicalData);
						DataItemInfoList.add(DataItemInfo);

						DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
						sDataCaption = "EC3703"; //发生时的电压谐波值       
						iDataType = c_Float; //字符型
						iDataLen = 2; //物理数据的字符长度
						sDataFormat = "000.0"; //数据格式
						iStorageType = CCFS_BCD; //BCD码
						iStorageOrder = CCSX_NX; //逆序存储
						sPhysicalData = sDateContent.substring(0, iDataLen * 2);
						sDateContent = sDateContent.substring(iDataLen * 2,
								sDateContent.length());
						DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
								iDataLen, sDataFormat, iStorageType,
								iStorageOrder);
						DataItemInfo.SetFPhysicalData(sPhysicalData);
						DataItemInfoList.add(DataItemInfo);

						DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
						sDataCaption = "EC3704"; //发生时的电压谐波限值       
						iDataType = c_Float; //字符型
						iDataLen = 2; //物理数据的字符长度
						sDataFormat = "000.0"; //数据格式
						iStorageType = CCFS_BCD; //BCD码
						iStorageOrder = CCSX_NX; //逆序存储
						sPhysicalData = sDateContent.substring(0, iDataLen * 2);
						sDateContent = sDateContent.substring(iDataLen * 2,
								sDateContent.length());
						DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
								iDataLen, sDataFormat, iStorageType,
								iStorageOrder);
						DataItemInfo.SetFPhysicalData(sPhysicalData);
						DataItemInfoList.add(DataItemInfo);

						if (sAlarmSign.equals("1")) { //A相电压                      
							if (iAlarmType == 1) { //发生
								sAlarmCode = "C371";
							} else {
								sAlarmCode = "R371";
							}
						} else if (sAlarmSign.equals("2")) { //B相电压                       
							if (iAlarmType == 1) { //发生
								sAlarmCode = "C372";
							} else {
								sAlarmCode = "R372";
							}
						} else if (sAlarmSign.equals("3")) { //C相电压                     
							if (iAlarmType == 1) { //发生
								sAlarmCode = "C373";
							} else {
								sAlarmCode = "R373";
							}
						}
						AlarmCodeList.add(sAlarmCode);
					} else if (iAlarmCode == 38) { //ERC38:电流谐波越限事件
						DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
						sDataCaption = "EC3801"; //测量点号
						iDataType = c_Float; //字符型
						iDataLen = 2; //物理数据的字符长度
						sDataFormat = "0000"; //数据格式
						iStorageType = CCFS_HEX; //十六进制码
						iStorageOrder = CCSX_SX; //顺序存储
						sPhysicalData = sDateContent.substring(0, iDataLen * 2);
						sPhysicalData = DataSwitch
								.ReverseStringByByte(sPhysicalData);
						sAlarmSign = ""
								+ (Integer.parseInt(sPhysicalData, 16) & 28672);//0x7000
						if ((Integer.parseInt(sPhysicalData, 16) & 32768) == 32768) {
							iAlarmType = 1; //发生
						} else {
							iAlarmType = 2; //恢复
						}
						sPhysicalData = ""
								+ ((Integer.parseInt(sPhysicalData, 16) & 4095)); //测量点号和总加组号加1   //暂时取消，保持和主站的一致
						sMeasuredPointNo = sPhysicalData;
						sPhysicalData = DataSwitch.IntToHex(sPhysicalData,
								"0000");
						sDateContent = sDateContent.substring(iDataLen * 2,
								sDateContent.length());
						DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
								iDataLen, sDataFormat, iStorageType,
								iStorageOrder);
						DataItemInfo.SetFPhysicalData(sPhysicalData);
						DataItemInfoList.add(DataItemInfo);

						DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
						sDataCaption = "EC3802"; //谐波次数
						iDataType = c_Float; //字符型
						iDataLen = 1; //物理数据的字符长度
						sDataFormat = "00"; //数据格式
						iStorageType = CCFS_HEX; //HEX码
						iStorageOrder = CCSX_NX; //逆序存储
						sPhysicalData = sDateContent.substring(0, iDataLen * 2);
						sDateContent = sDateContent.substring(iDataLen * 2,
								sDateContent.length());
						DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
								iDataLen, sDataFormat, iStorageType,
								iStorageOrder);
						DataItemInfo.SetFPhysicalData(sPhysicalData);
						DataItemInfoList.add(DataItemInfo);

						DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
						sDataCaption = "EC3803"; //发生时的电流谐波值       
						iDataType = c_Float; //字符型
						iDataLen = 2; //物理数据的字符长度
						sDataFormat = "000.0"; //数据格式
						iStorageType = CCFS_BCD; //BCD码
						iStorageOrder = CCSX_NX; //逆序存储
						sPhysicalData = sDateContent.substring(0, iDataLen * 2);
						sDateContent = sDateContent.substring(iDataLen * 2,
								sDateContent.length());
						DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
								iDataLen, sDataFormat, iStorageType,
								iStorageOrder);
						DataItemInfo.SetFPhysicalData(sPhysicalData);
						DataItemInfoList.add(DataItemInfo);

						DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
						sDataCaption = "EC3804"; //发生时的电流谐波限值
						sPhysicalData = sDateContent.substring(0, iDataLen * 2);
						sDateContent = sDateContent.substring(iDataLen * 2,
								sDateContent.length());
						DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
								iDataLen, sDataFormat, iStorageType,
								iStorageOrder);
						DataItemInfo.SetFPhysicalData(sPhysicalData);
						DataItemInfoList.add(DataItemInfo);

						if (sAlarmSign.equals("1")) { //A相电流                      
							if (iAlarmType == 1) { //发生
								sAlarmCode = "C381";
							} else {
								sAlarmCode = "R381";
							}
						} else if (sAlarmSign.equals("2")) { //B相电流                       
							if (iAlarmType == 1) { //发生
								sAlarmCode = "C382";
							} else {
								sAlarmCode = "R382";
							}
						} else if (sAlarmSign.equals("3")) { //C相电流                     
							if (iAlarmType == 1) { //发生
								sAlarmCode = "C383";
							} else {
								sAlarmCode = "R383";
							}
						}
						AlarmCodeList.add(sAlarmCode);
					} else if (iAlarmCode == 39) { //ERC39:DLMS相关带事件代码的事件
						DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
						sDataCaption = "EC3901"; //测量点号
						iDataType = c_Float; //字符型
						iDataLen = 2; //物理数据的字符长度
						sDataFormat = "0000"; //数据格式
						iStorageType = CCFS_HEX; //十六进制码
						iStorageOrder = CCSX_SX; //顺序存储
						sPhysicalData = sDateContent.substring(0, iDataLen * 2);
						sPhysicalData = DataSwitch
								.ReverseStringByByte(sPhysicalData);

						sPhysicalData = ""
								+ ((Integer.parseInt(sPhysicalData, 16) & 4095)); //测量点号和总加组号加1   //暂时取消，保持和主站的一致
						sMeasuredPointNo = sPhysicalData;
						sPhysicalData = DataSwitch.IntToHex(sPhysicalData,
								"0000");
						sDateContent = sDateContent.substring(iDataLen * 2,
								sDateContent.length());
						DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
								iDataLen, sDataFormat, iStorageType,
								iStorageOrder);
						DataItemInfo.SetFPhysicalData(sPhysicalData);
						DataItemInfoList.add(DataItemInfo);

						DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
						sDataCaption = "EC3902"; //事件代码
						iDataType = c_Float; //字符型
						iDataLen = 1; //物理数据的字符长度
						sDataFormat = "00"; //数据格式
						iStorageType = CCFS_HEX; //HEX码
						iStorageOrder = CCSX_NX; //逆序存储
						sPhysicalData = sDateContent.substring(0, iDataLen * 2);
						sDateContent = sDateContent.substring(iDataLen * 2,
								sDateContent.length());
						DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
								iDataLen, sDataFormat, iStorageType,
								iStorageOrder);
						iAlarmType = Integer.parseInt(sPhysicalData,16);
						DataItemInfo.SetFPhysicalData(sPhysicalData);
						DataItemInfoList.add(DataItemInfo);

						if (iAlarmType >= 60 && iAlarmType <= 65) {
							DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
							sDataCaption = "EC3903"; //控制阀值       
							iDataType = c_Float; //字符型
							iDataLen = 2; //物理数据的字符长度
							sDataFormat = "0000"; //数据格式
							iStorageType = CCFS_HEX; //HEX码
							iStorageOrder = CCSX_SX; //逆序存储
							sPhysicalData = sDateContent.substring(0,
									iDataLen * 2);
							sDateContent = sDateContent.substring(iDataLen * 2,
									sDateContent.length());
							DataItemInfo.DataItemInfoAdd(sDataCaption,
									iDataType, iDataLen, sDataFormat,
									iStorageType, iStorageOrder);
							DataItemInfo.SetFPhysicalData(sPhysicalData);
							DataItemInfoList.add(DataItemInfo);
						}
						switch(iAlarmType){
						  case 3:case 4:case 6:case 7:case 10:case 11:case 12:case 13:case 14:case 15:
						  case 16:case 17:case 19:case 20:case 22:case 40:case 42:case 44:case 46:case 47:
						  case 49:case 60:case 62:case 64:case 67:case 75:case 78:case 86:case 94:
						  case 100:case 102:case 103:case 104:case 110:case 112:case 113:case 114:case 120:
						  case 122:case 123:case 124:case 130:case 132:case 133:case 134:case 160:case 162:
						  case 164:case 170:case 172:case 174:case 73:case 80:case 82:case 88:case 90:
						  case 96:case 98:case 255:{
							  sAlarmCode = "C0" + ("" + iAlarmCode) + DataSwitch.IntToHex(""+iAlarmType, "00");
							  break;
						  }
						  default: {
							  sAlarmCode = "R0" + ("" + iAlarmCode); 
							  sAlarmCode = sAlarmCode + DataSwitch.IntToHex(""+(iAlarmType-1), "00");
							  break;
						  }
						}
						AlarmCodeList.add(sAlarmCode);
					} else if (iAlarmCode == 40) { //ERC40:DLMS相关不带事件代码的事件
						DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
						sDataCaption = "EC4001"; //测量点号
						iDataType = c_Float; //字符型
						iDataLen = 2; //物理数据的字符长度
						sDataFormat = "0000"; //数据格式
						iStorageType = CCFS_HEX; //十六进制码
						iStorageOrder = CCSX_SX; //顺序存储
						sPhysicalData = sDateContent.substring(0, iDataLen * 2);
						sPhysicalData = DataSwitch
								.ReverseStringByByte(sPhysicalData);

						sPhysicalData = ""
								+ ((Integer.parseInt(sPhysicalData, 16) & 4095)); //测量点号和总加组号加1   //暂时取消，保持和主站的一致
						sMeasuredPointNo = sPhysicalData;
						sPhysicalData = DataSwitch.IntToHex(sPhysicalData,
								"0000");
						sDateContent = sDateContent.substring(iDataLen * 2,
								sDateContent.length());
						DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
								iDataLen, sDataFormat, iStorageType,
								iStorageOrder);
						DataItemInfo.SetFPhysicalData(sPhysicalData);
						DataItemInfoList.add(DataItemInfo);

						DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
						sDataCaption = "EC4002"; //事件代码
						iDataType = c_Float; //字符型
						iDataLen = 1; //物理数据的字符长度
						sDataFormat = "00"; //数据格式
						iStorageType = CCFS_HEX; //HEX码
						iStorageOrder = CCSX_NX; //逆序存储
						sPhysicalData = sDateContent.substring(0, iDataLen * 2);
						sDateContent = sDateContent.substring(iDataLen * 2,
								sDateContent.length());
						DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
								iDataLen, sDataFormat, iStorageType,
								iStorageOrder);
						iAlarmType = Integer.parseInt(sPhysicalData);
						DataItemInfo.SetFPhysicalData(sPhysicalData);
						DataItemInfoList.add(DataItemInfo);

						DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
						sDataCaption = "EC4003"; //掉电持续时间/正向有功总电量       
						iDataType = c_Float; //字符型
						iDataLen = 4; //物理数据的字符长度
						sDataFormat = "00000000"; //数据格式
						iStorageType = CCFS_HEX; //HEX码
						iStorageOrder = CCSX_SX; //逆序存储
						sPhysicalData = sDateContent.substring(0, iDataLen * 2);
						sDateContent = sDateContent.substring(iDataLen * 2,
								sDateContent.length());
						DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
								iDataLen, sDataFormat, iStorageType,
								iStorageOrder);
						DataItemInfo.SetFPhysicalData(sPhysicalData);
						DataItemInfoList.add(DataItemInfo);
						sAlarmCode = "C0" + ("" + iAlarmCode) + DataSwitch.StrStuff("0", 2, ""+iAlarmType, 10);
						AlarmCodeList.add(sAlarmCode);
					}
					AlarmDataTemp = new SFE_AlarmData();
					for (int j = 0; j < DataItemInfoList.size(); j++) { //物理数据转换成逻辑数据
						((SFE_DataItemInfo) (DataItemInfoList.get(j)))
								.PhysicalDataToLogicData(); //物理数据转换成逻辑数据
						sDataCaption = ((SFE_DataItemInfo) (DataItemInfoList
								.get(j))).GetFDataCaption(); //数据项标识
						sLogicData = ((SFE_DataItemInfo) (DataItemInfoList
								.get(j))).GetFLogicData(); //逻辑数据
						iDataLen = sLogicData.length(); //逻辑数据长度
						AlarmDataTemp.DataItemAdd(sDataCaption, iDataLen,
								sLogicData);
					}
					for (int j = 0; j < AlarmCodeList.size(); j++) { //一个告警分解成多个告警
						AlarmData = new SFE_AlarmData();
						AlarmData.SetAlarmCode(AlarmCodeList.get(j).toString());
						AlarmData.SetAlarmType(Fn);
						AlarmData.SetAlarmDateTime(sAlarmDateTime);
						AlarmData.SetMeasuredPointNo(Integer
								.parseInt(sMeasuredPointNo));
						AlarmData.SetMeasuredPointType(iMeasuredPointType);
						AlarmData.SetDataItemCount(AlarmDataTemp
								.GetDataItemCount());
						AlarmData.DataItemList = AlarmDataTemp.DataItemList;
						AlarmDataList.add(AlarmData);
					}
					DataItemInfoList.clear();
					AlarmCodeList.clear();
				}
			} catch (Exception e) {
				Glu_ConstDefine.Log1
						.WriteLog("Func:FrameDataAreaExplainQuanGuo__ExplainAlarmParameterQuanGuo();Func::"
								+ e.toString());
			}
		} finally {
		}
		return AlarmDataList;
	}

	//----------------------------国网数据区解析所用方法-----------------------------
	public String GetTaskDateTimeInfo(String sDateTimeLabel, int DateType,
			int TermialProtocolType) { //返回值：开始时间(YYYYMMDDHHNNSS)+时间间隔+数据点数
		String sResult = "";
		try {
			try {
				String sDateTime = "", sNowDateTime = "", sDataDensity = "", sDataCount = "";
				int iDataDensity = 0; //时间间隔、数据点数
				if (DateType == 21) { //小时冻结数据时标
					if (TermialProtocolType == Glu_ConstDefine.GY_ZD_698) {
						sDateTime = Integer.toString((Integer.parseInt(
								sDateTimeLabel.substring(0, 1), 16) & 3))
								+ sDateTimeLabel.substring(1, 2); //小时
						iDataDensity = (Integer.parseInt(sDateTimeLabel
								.substring(2, 4), 16));
					} else {
						sDateTime = Integer.toString((Integer.parseInt(
								sDateTimeLabel.substring(0, 1), 16) & 3))
								+ sDateTimeLabel.substring(1, 2); //小时
						iDataDensity = (Integer.parseInt(sDateTimeLabel
								.substring(0, 1), 16) & 12) / 4;
					}
					//获取当前时间
					Calendar cLogTime = Calendar.getInstance();
					SimpleDateFormat formatter = new SimpleDateFormat(
							"yyyyMMddHHmmss");
					sNowDateTime = formatter.format(cLogTime.getTime());					
					//判断上送数据小时大于当前小时则为上一天数据
					if (Integer.parseInt(sDateTime) > Integer
							.parseInt(sNowDateTime.substring(8, 10))) {
						sNowDateTime = DataSwitch.IncreaseDateTime(
								sNowDateTime, -1, 4);
					}
					if (iDataDensity == 2) { //小时冻结密度为30分钟，则冻结时间为30，0
						sDateTime = sNowDateTime.substring(0, 8) + sDateTime
								+ "3000"; //取系统时间填充年月日分秒,但是无效的,只有时分是有效数据
					} else if (iDataDensity == 3) { //小时冻结密度为60分钟，则冻结时间为0
						sDateTime = sNowDateTime.substring(0, 8) + sDateTime
								+ "0000"; //取系统时间填充年月日分秒,但是无效的,只有时分是有效数据
						sDateTime = DataSwitch
								.IncreaseDateTime(sDateTime, 1, 3);
					} else { //小时冻结默认密度为15分钟，则冻结时间为15，30，45，0
						sDateTime = sNowDateTime.substring(0, 8) + sDateTime
								+ "1500"; //取系统时间填充年月日分秒,但是无效的,只有时分是有效数据
					}

				} else if (DateType == 22) { //曲线数据时标
					if (Glu_ConstDefine.IRANToGregorian){
						sDateTime = DataSwitch.ReverseStringByByte(sDateTimeLabel
										.substring(0, 10)) + "00"; //年月日时分						
						//Change Iran time to Gregorian						
						String sTime = sDateTime.substring(0,6) + "01" + sDateTime.substring(6,12);
						Glu_ConstDefine.Cc1.TOU_IRANToGregorian(sTime);						
						sNowDateTime = "20" + sNowDateTime + sTime.substring(0,6) + sTime.substring(8,14);
					}else{
						sDateTime = "20"
							+ DataSwitch.ReverseStringByByte(sDateTimeLabel
									.substring(0, 10)) + "00"; //年月日时分
					}					
					iDataDensity = Integer.parseInt(sDateTimeLabel.substring(
							10, 12), 16); //数据密度
					sDataCount = DataSwitch.HexToInt(sDateTimeLabel.substring(
							12, 14), "00"); //数据点数
					//当数据点数为个位数时需要补足为1个字节
					//sDataCount= DataSwitch.StrStuff("0",2,sDataCount,10);
				} else if (DateType == 23 || DateType == 26) { //日数据冻结时标
					if (Glu_ConstDefine.IRANToGregorian){
						sDateTime = DataSwitch.ReverseStringByByte(sDateTimeLabel
										.substring(0, 6)) + "000000"; //年月日						
						//Change Iran time to Gregorian						
						String sTime = sDateTime.substring(0,6) + "01" + sDateTime.substring(6,12);
						Glu_ConstDefine.Cc1.TOU_IRANToGregorian(sTime);						
						sNowDateTime = "20" + sNowDateTime + sTime.substring(0,6) + sTime.substring(8,14);
					}else{
						sDateTime = "20"
							+ DataSwitch.ReverseStringByByte(sDateTimeLabel
									.substring(0, 6)) + "000000"; //年月日
					}					
				} else if (DateType == 24) { //月数据冻结时标
					if (Glu_ConstDefine.IRANToGregorian){
						sDateTime = DataSwitch.ReverseStringByByte(sDateTimeLabel
										.substring(0, 4)) + "01000000"; //年月
						//Change Iran time to Gregorian						
						String sTime = sDateTime.substring(0,6) + "01" + sDateTime.substring(6,12);
						Glu_ConstDefine.Cc1.TOU_IRANToGregorian(sTime);						
						sNowDateTime = "20" + sNowDateTime + sTime.substring(0,6) + sTime.substring(8,14);
					}else{
						sDateTime = "20"
							+ DataSwitch.ReverseStringByByte(sDateTimeLabel
									.substring(0, 4)) + "01000000"; //年月
					}					
				}
				switch (iDataDensity) {
				case 1: {
					sDataDensity = "15";
					if (DateType == 21) {
						sDataCount = "04";
					}
					break;
				}
				case 2: {
					sDataDensity = "30";
					if (DateType == 21) {
						sDataCount = "02";
					}
					break;
				}
				case 3: {
					sDataDensity = "60";
					if (DateType == 21) {
						sDataCount = "01";
					}
					break;
				}
				default: { //日月冻结数据
					sDataDensity = "00";
					sDataCount = "01";
				}
				}
				sResult = sDateTime + sDataDensity + sDataCount;
			} catch (Exception e) {
				Glu_ConstDefine.Log1
						.WriteLog("Func:FrameDataAreaExplainQuanGuo__GetTaskDateTimeInfo();Error:"
								+ e.toString());
			}
		} finally {
		}
		return sResult;
	}

	public ArrayList<String> ExplainAlarmCodeQuanGuo(int AlarmType,
			String AlarmSign, String AlarmCode, int AlarmMaxCount) { //异常类型,异常事件标志,异常代码,当前异常最大分解数目
		ArrayList<String> AlarmCodeList = new ArrayList<String>();
		try {
			try {
				if (!AlarmSign.equals("")) {
					int iAlarmSign = Integer.parseInt(
							AlarmSign.substring(0, 2), 16);
					int iPower2 = 0;
					AlarmCode = DataSwitch.StrStuff("0", 2, AlarmCode, 10); //补足2位
					String sAlarmCode = "";
					if (AlarmType == 1 || AlarmType == 2 || AlarmType == 3) { //变更标志:1发生;2恢复;3状态变位
						for (int i = 0; i < AlarmMaxCount; i++) {
							iPower2 = DataSwitch.Power2(i);
							if ((iAlarmSign & iPower2) == iPower2) { //取单个字节每位的值
								if (AlarmType == 1) {
									sAlarmCode = "C" + AlarmCode
											+ Integer.toString(i + 1); //异常发生
								} else if (AlarmType == 2) {
									sAlarmCode = "R" + AlarmCode
											+ Integer.toString(i + 1); //异常恢复
								} else { //状态变位
									int iTemp = Integer.parseInt(AlarmSign
											.substring(2, 4), 16); //变位后状态
									if ((iTemp & iPower2) == iPower2) { //取单个字节每位的值
										sAlarmCode = "C" + AlarmCode
												+ Integer.toString(i + 1); //都属于变位异常,由于变位后状态不同而产生不同的异常编码，以便保存时信息不丢失
									} else {
										sAlarmCode = "R" + AlarmCode
												+ Integer.toString(i + 1); //都属于变位异常,由于变位后状态不同而产生不同的异常编码，以便保存时信息不丢失
									}
								}
								AlarmCodeList.add(sAlarmCode);
							}
						}
					} else if (AlarmType == 10 || AlarmType == 20) { //异常标志
						if ((iAlarmSign & 1) == 1) { //A相
							if ((iAlarmSign & 192) == 192 && AlarmMaxCount == 3) { //有3种异常
								if (AlarmType == 10) { //异常发生
									sAlarmCode = "C" + AlarmCode + "3";
								} else { //异常恢复
									sAlarmCode = "R" + AlarmCode + "3";
								}
							} else if ((iAlarmSign & 64) == 64) {
								if (AlarmType == 10) { //异常发生
									sAlarmCode = "C" + AlarmCode + "1";
								} else { //异常恢复
									sAlarmCode = "R" + AlarmCode + "1";
								}
							} else if ((iAlarmSign & 128) == 128) {
								if (AlarmType == 10) { //异常发生
									sAlarmCode = "C" + AlarmCode + "2";
								} else { //异常恢复
									sAlarmCode = "R" + AlarmCode + "2";
								}
							}
							AlarmCodeList.add(sAlarmCode);
						} else if ((iAlarmSign & 2) == 2) { //B相
							if ((iAlarmSign & 192) == 192 && AlarmMaxCount == 3) { //有3种异常
								if (AlarmType == 10) { //异常发生
									sAlarmCode = "C" + AlarmCode + "6";
								} else { //异常恢复
									sAlarmCode = "R" + AlarmCode + "6";
								}
							} else if ((iAlarmSign & 64) == 64) {
								if (AlarmType == 10) { //异常发生
									sAlarmCode = "C" + AlarmCode + "4";
								} else { //异常恢复
									sAlarmCode = "R" + AlarmCode + "4";
								}
							} else if ((iAlarmSign & 128) == 128) {
								if (AlarmType == 10) { //异常发生
									sAlarmCode = "C" + AlarmCode + "5";
								} else { //异常恢复
									sAlarmCode = "R" + AlarmCode + "5";
								}
							}
							AlarmCodeList.add(sAlarmCode);
						} else if ((iAlarmSign & 4) == 4) { //C相
							if ((iAlarmSign & 192) == 192 && AlarmMaxCount == 3) { //有3种异常
								if (AlarmType == 10) { //异常发生
									sAlarmCode = "C" + AlarmCode + "9";
								} else { //异常恢复
									sAlarmCode = "R" + AlarmCode + "9";
								}
							} else if ((iAlarmSign & 64) == 64) {
								if (AlarmType == 10) { //异常发生
									sAlarmCode = "C" + AlarmCode + "7";
								} else { //异常恢复
									sAlarmCode = "R" + AlarmCode + "7";
								}
							} else if ((iAlarmSign & 128) == 128) {
								if (AlarmType == 10) { //异常发生
									sAlarmCode = "C" + AlarmCode + "8";
								} else { //异常恢复
									sAlarmCode = "R" + AlarmCode + "8";
								}
							}
							AlarmCodeList.add(sAlarmCode);
						}
					}
				}
			} catch (Exception e) {
				Glu_ConstDefine.Log1
						.WriteLog("Func:FrameDataAreaExplainQuanGuo__ExplainAlarmCodeQuanGuo();Error:"
								+ e.toString());
			}
		} finally {
		}
		return AlarmCodeList;
	}
}

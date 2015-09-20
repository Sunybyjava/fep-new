package com.chooshine.fep.fepex.common;

import java.util.Hashtable;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import com.chooshine.fep.FrameDataAreaExplain.SFE_AlarmData;
import com.chooshine.fep.FrameDataAreaExplain.SFE_DataItem;
import com.chooshine.fep.FrameDataAreaExplain.SFE_DataListInfo;
import com.chooshine.fep.FrameDataAreaExplain.SFE_HistoryData;
import com.chooshine.fep.FrameDataAreaExplain.SFE_NormalData;
import com.chooshine.fep.FrameDataAreaExplain.SFE_SetResultData;
import com.chooshine.fep.FrameDataAreaExplain.SFE_SetResultItem;


public class CommResultConver {
	public CommResultConver() {
	}

	/**
	 *
	 * @param listResult List
	 * @return Hashtable
	 */
	public static Hashtable getHashtableResult(List listResult) {
		return getHashtableResultInside(listResult, 0);
	}
	
	public static Hashtable getHashtableResultHx(List listResult) {
		return getHashtableResultInsideHX(listResult, 0);
	}
	
	private static Hashtable getHashtableResultInsideHX(List listResult, int iZjFlag) {
		Hashtable htResult = new Hashtable();
		
		
		String sKey = null;

		ReturnExplainInfo returnExplainInfo = new ReturnExplainInfo();
		String sZdljdz = null;
		SFE_DataListInfo sfeDataListInfo = new SFE_DataListInfo();
		int iExplainResult = -1;
		int iDataType = -1;
		ArrayList alDataList = null;

		SFE_NormalData sfeNormalData = new SFE_NormalData();
		SFE_HistoryData sfeHistoryData = new SFE_HistoryData();
		SFE_AlarmData sfeAlarmData = new SFE_AlarmData();
		SFE_SetResultData sfeSetResultData = new SFE_SetResultData();
		int iCldlx = -1;
		int iCldxh = -1;
		String sClddz = null;
		ArrayList alDataItemList = null;
		int iTaskNo = -1;
		String sTaskDateTime = null;
		String sAlarmCode = null;
		int iAlarmType = -1;
		String sAlarmDateTime = null;
		ArrayList alSetResultDataList = null;

		SFE_DataItem sfeDataItem = new SFE_DataItem();
		SFE_SetResultItem sfeSetResultItem = null;
		String sDataCaption = null;
		String sDataContent = null;
		int iSetResult = -1;

		char[] cSpace = { 32 }; //空格

		if (listResult != null) {
			Iterator itReturnExplainInfo = listResult.iterator();
			while (itReturnExplainInfo != null && itReturnExplainInfo.hasNext()) {
				returnExplainInfo = (ReturnExplainInfo) itReturnExplainInfo.next();
				sZdljdz = new String((returnExplainInfo.TerminalAddress != null ? returnExplainInfo.TerminalAddress
						: cSpace));
				sZdljdz = (sZdljdz != null && sZdljdz.trim().length() > 8 ? sZdljdz.trim().substring(0, 8)
						: (sZdljdz != null ? sZdljdz.trim() : ""));
				sfeDataListInfo = returnExplainInfo.retrunDataInfo;
				if (sfeDataListInfo != null) {
					iExplainResult = sfeDataListInfo.ExplainResult;
					iDataType = sfeDataListInfo.DataType;
					alDataList = sfeDataListInfo.DataList;
				} else {
					iExplainResult = -1;
					iDataType = -1;
					alDataList = null;
				}

				//System.out.println("&&&&&sZdljdz&&&&& : " + sZdljdz);
				//System.out.println("&&&&&iExplainResult&&&&& : " + iExplainResult);
				//System.out.println("&&&&&iDataType&&&&& : " + iDataType);
				//System.out.println("&&&&&alDataList&&&&& : " + alDataList);

				if (iExplainResult == 0) { //解释成功
					if (iDataType == 10) { //普通数据
						if (alDataList != null) {
							Iterator itSfeNormalData = alDataList.iterator();
							while (itSfeNormalData != null && itSfeNormalData.hasNext()) {
								sfeNormalData = (SFE_NormalData) itSfeNormalData.next();
								iCldlx = sfeNormalData.GetMeasuredPointType();
								iCldxh = sfeNormalData.GetMeasuredPointNo();
								//System.out.println("sfeNormalData.GetMeasuredAdd() : " + sfeNormalData.GetMeasuredAdd());
								sClddz = new String((sfeNormalData.GetMeasuredAdd() != null ? sfeNormalData
										.GetMeasuredAdd() : cSpace));
								//System.out.println("sClddz : " + sClddz);
								alDataItemList = sfeNormalData.DataItemList;

								//System.out.println("&&&&&iCldlx&&&&& : " + iCldlx);
								//System.out.println("&&&&&iCldxh&&&&& : " + iCldxh);
								//System.out.println("&&&&&sClddz&&&&& : " + sClddz);
								//System.out.println("&&&&&alDataItemList&&&&& : " + alDataItemList);

								if (alDataItemList != null) {
									Iterator itSfeDataItem = alDataItemList.iterator();
									while (itSfeDataItem != null && itSfeDataItem.hasNext()) {
										sfeDataItem = (SFE_DataItem) itSfeDataItem.next();
										sDataCaption = new String((sfeDataItem.GetDataCaption() != null ? sfeDataItem
												.GetDataCaption() : cSpace));
										sDataContent = new String((sfeDataItem.GetDataContent() != null ? sfeDataItem
												.GetDataContent() : cSpace));

										//System.out.println("&&&&&sDataCaption&&&&& : " + sDataCaption);
										//System.out.println("&&&&&sDataContent&&&&& : " + sDataContent);

										if (iZjFlag == 1) { //中继
											//key : （终端逻辑地址）#（测量点类型）#（测量点地址）#（数据标识）
											sKey = "10@" + (sZdljdz != null ? sZdljdz.trim() : "") + "#" + iCldlx + "#"
													+ sClddz.trim() + "#"
													+ (sDataCaption != null ? sDataCaption.trim() : "");
										} else { //非中继
											//key : （终端逻辑地址）#（测量点类型）#（测量点序号）#（数据标识）
											sKey = "10@" + (sZdljdz != null ? sZdljdz.trim() : "") + "#" + iCldlx + "#"
													+ iCldxh + "#" + (sDataCaption != null ? sDataCaption.trim() : "");
										}
										htResult.put(sKey, (sDataContent != null ? sDataContent.trim() : ""));
									}
								}
							}
						}
					} else if (iDataType == 20) { //历史数据
						if (alDataList != null) {
							Iterator itSfeHistoryData = alDataList.iterator();
							while (itSfeHistoryData != null && itSfeHistoryData.hasNext()) {
								sfeHistoryData = (SFE_HistoryData) itSfeHistoryData.next();
								iTaskNo = sfeHistoryData.GetTaskNo();
								sTaskDateTime = new String((sfeHistoryData.GetTaskDateTime() != null ? sfeHistoryData
										.GetTaskDateTime() : cSpace));
								iCldlx = sfeHistoryData.GetMeasuredPointType();
								iCldxh = sfeHistoryData.GetMeasuredPointNo();
								sClddz = new String(sfeHistoryData.GetMeasuredAdd());
								alDataItemList = sfeHistoryData.DataItemList;

								if (alDataItemList != null) {
									Iterator itSfeDataItem = alDataItemList.iterator();
									while (itSfeDataItem != null && itSfeDataItem.hasNext()) {
										sfeDataItem = (SFE_DataItem) itSfeDataItem.next();
										sDataCaption = new String((sfeDataItem.GetDataCaption() != null ? sfeDataItem
												.GetDataCaption() : cSpace));
										sDataContent = new String((sfeDataItem.GetDataContent() != null ? sfeDataItem
												.GetDataContent() : cSpace));

										//key : （终端逻辑地址）#（测量点类型）#（测量点序号）#（时标）#（数据标识）
										sKey = "20@" + (sZdljdz != null ? sZdljdz.trim() : "") + "#" + iCldlx + "#"
												+ sClddz
												+ "#" + (sDataCaption != null ? sDataCaption.trim() : "");										
										htResult.put(sKey, (sDataContent != null ? sDataContent.trim() : ""));
									}
								}
							}
						}
					} else if (iDataType == 30) { //告警数据
						if (alDataList != null) {
							Iterator itSfeAlarmData = alDataList.iterator();
							while (itSfeAlarmData != null && itSfeAlarmData.hasNext()) {
								sfeAlarmData = (SFE_AlarmData) itSfeAlarmData.next();
								sAlarmCode = new String((sfeAlarmData.GetAlarmCode() != null ? sfeAlarmData
										.GetAlarmCode() : cSpace));
								iAlarmType = sfeAlarmData.GetAlarmType();
								sAlarmDateTime = new String((sfeAlarmData.GetAlarmDateTime() != null ? sfeAlarmData
										.GetAlarmDateTime() : cSpace));
								iCldlx = sfeAlarmData.GetMeasuredPointType();
								iCldxh = sfeAlarmData.GetMeasuredPointNo();
								alDataItemList = sfeAlarmData.DataItemList;

								//key : （终端逻辑地址）#（测量点类型）#（测量点序号）#（异常代码）#（异常类型）#（异常时间）
								sKey = "30@" + (sZdljdz != null ? sZdljdz.trim() : "") + "#" + iCldlx + "#" + iCldxh
										+ "#" + (sAlarmCode != null ? sAlarmCode.trim() : "") + "#" + iAlarmType + "#"
										+ (sAlarmDateTime != null ? sAlarmDateTime.trim() : "");
								//System.out.println();
								//System.out.println("key : " + sKey);
								//System.out.println("result : " + alDataItemList);
								//System.out.println();
								//LogFile.log("@@@@@@@" + sKey + " : " + alDataItemList);
								htResult.put(sKey, alDataItemList);

								/*if(alDataItemList != null) {
								    Iterator itSfeDataItem = alDataItemList.iterator();
								    while(itSfeDataItem != null && itSfeDataItem.hasNext()) {
								        sfeDataItem = (SFE_DataItem) itSfeDataItem.next();
								        sDataCaption = new String((sfeDataItem.GetDataCaption() != null ? sfeDataItem.GetDataCaption() : cSpace));
								        sDataContent = new String((sfeDataItem.GetDataContent() != null ? sfeDataItem.GetDataContent() : cSpace));

								        //key : （终端逻辑地址）#（测量点类型）#（测量点序号）#（异常代码）#（异常类型）#（异常时间）#（数据标识）
								        sKey = "30@" + (sZdljdz != null ? sZdljdz.trim() : "")
								               + "#" + iCldlx
								               + "#" + iCldxh
								               + "#" + (sAlarmCode != null ? sAlarmCode.trim() : "")
								               + "#" + iAlarmType
								               + "#" + (sAlarmDateTime != null ? sAlarmDateTime.trim() : "")
								               + "#" + (sDataCaption != null ? sDataCaption.trim() : "");
								        //System.out.println();
								        //System.out.println("key : " + sKey);
								        //System.out.println("result : " + (sDataContent != null ? sDataContent.trim() : ""));
								        //System.out.println();
								        LogFile.log("@@@@@@@" + sKey + " : " + (sDataContent != null ? sDataContent.trim() : ""));
								        htResult.put(sKey, (sDataContent != null ? sDataContent.trim() : ""));
								    }
								}*/
							}
						}
					} else if (iDataType == 40) { //设置返回数据
						if (alDataList != null) {
							Iterator itSfeSetResultData = alDataList.iterator();
							while (itSfeSetResultData != null && itSfeSetResultData.hasNext()) {
								sfeSetResultData = (SFE_SetResultData) itSfeSetResultData.next();
								iCldlx = sfeSetResultData.GetMeasuredPointType();
								iCldxh = sfeSetResultData.GetMeasuredPointNo();
								sClddz = new String((sfeSetResultData.GetMeasuredAdd() != null ? sfeSetResultData
										.GetMeasuredAdd() : cSpace));
								alSetResultDataList = sfeSetResultData.SetResultDataList;

								if (alSetResultDataList != null) {
									Iterator itSfeSetResultItem = alSetResultDataList.iterator();
									while (itSfeSetResultItem != null && itSfeSetResultItem.hasNext()) {
										sfeSetResultItem = (SFE_SetResultItem) itSfeSetResultItem.next();
										sDataCaption = new String(
												(sfeSetResultItem.GetDataCaption() != null ? sfeSetResultItem
														.GetDataCaption() : cSpace));
										iSetResult = sfeSetResultItem.GetSetResut();

										//key : （终端逻辑地址）#（测量点类型）#（测量点序号）#（数据标识）
										sKey = "40@" + (sZdljdz != null ? sZdljdz.trim() : "") + "#" + iCldlx + "#"
												+ iCldxh + "#" + (sDataCaption != null ? sDataCaption.trim() : "");
										
										htResult.put(sKey, "" + iSetResult);
									}
								}
							}
						}
					}
				} else { //解释失败
					//key : （终端逻辑地址）
					sKey = "-1@" + (sZdljdz != null ? sZdljdz.trim() : "");
					//System.out.println();
					//System.out.println("key : " + sKey);
					//System.out.println("result : " + (sDataContent != null ? sDataContent.trim() : ""));
					//System.out.println();
					//LogFile.log("@@@@@@@" + sKey + " : " + iExplainResult);
					htResult.put(sKey, "" + iExplainResult);
				}
			}
		} else {
			htResult = null;
		}

		return htResult;
	}

	/**
	 *
	 * @param listResult List
	 * @param iZjFlag int : 中继标记 0-非中继；1-中继
	 * @return Hashtable
	 */
	public static Hashtable getHashtableResult(List listResult, int iZjFlag) {
		return getHashtableResultInside(listResult, iZjFlag);
	}

	/**
	 *
	 * @param listResult List
	 * @param iZjFlag int : 中继标记 0-非中继；1-中继
	 * @return Hashtable
	 */
	private static Hashtable getHashtableResultInside(List listResult, int iZjFlag) {
		//System.out.println("&&&&&listResult&&&&& : " + listResult);

		Hashtable htResult = new Hashtable();

		/**
		 * 普通数据     ：     key = 10@（终端逻辑地址）#（测量点类型）#（测量点序号）#（数据标识）          value = 数据值（String）
		 *             (中继) key = 10@（终端逻辑地址）#（测量点类型）#（测量点地址）#（数据标识）          value = 数据值（String）
		 * 历史数据     ：     key = 20@（终端逻辑地址）#（测量点类型）#（测量点序号）#（时标）#（数据标识）   value = 数据值（String）
		 * 告警数据     ：     key = 30@（终端逻辑地址）#（测量点类型）#（测量点序号）#（异常代码）#（异常类型）#（异常时间）   value = 数据列表（DataItemList）
		 * 设置返回数据  ：     key = 40@（终端逻辑地址）#（测量点类型）#（测量点序号）#（数据标识）          value = 数据值（String）
		 * 解释失败     ：     key = -1@（终端逻辑地址）
		 */
		String sKey = null;

		ReturnExplainInfo returnExplainInfo = new ReturnExplainInfo();
		String sZdljdz = null;
		SFE_DataListInfo sfeDataListInfo = new SFE_DataListInfo();
		int iExplainResult = -1;
		int iDataType = -1;
		ArrayList alDataList = null;

		SFE_NormalData sfeNormalData = new SFE_NormalData();
		SFE_HistoryData sfeHistoryData = new SFE_HistoryData();
		SFE_AlarmData sfeAlarmData = new SFE_AlarmData();
		SFE_SetResultData sfeSetResultData = new SFE_SetResultData();
		int iCldlx = -1;
		int iCldxh = -1;
		String sClddz = null;
		ArrayList alDataItemList = null;
		int iTaskNo = -1;
		String sTaskDateTime = null;
		String sAlarmCode = null;
		int iAlarmType = -1;
		String sAlarmDateTime = null;
		ArrayList alSetResultDataList = null;

		SFE_DataItem sfeDataItem = new SFE_DataItem();
		SFE_SetResultItem sfeSetResultItem = null;
		String sDataCaption = null;
		String sDataContent = null;
		int iSetResult = -1;

		char[] cSpace = { 32 }; //空格

		if (listResult != null) {
			Iterator itReturnExplainInfo = listResult.iterator();
			while (itReturnExplainInfo != null && itReturnExplainInfo.hasNext()) {
				returnExplainInfo = (ReturnExplainInfo) itReturnExplainInfo.next();
				sZdljdz = new String((returnExplainInfo.TerminalAddress != null ? returnExplainInfo.TerminalAddress
						: cSpace));
				sZdljdz = (sZdljdz != null && sZdljdz.trim().length() > 8 ? sZdljdz.trim().substring(0, 8)
						: (sZdljdz != null ? sZdljdz.trim() : ""));
				sfeDataListInfo = returnExplainInfo.retrunDataInfo;
				if (sfeDataListInfo != null) {
					iExplainResult = sfeDataListInfo.ExplainResult;
					iDataType = sfeDataListInfo.DataType;
					alDataList = sfeDataListInfo.DataList;
				} else {
					iExplainResult = -1;
					iDataType = -1;
					alDataList = null;
				}

				//System.out.println("&&&&&sZdljdz&&&&& : " + sZdljdz);
				//System.out.println("&&&&&iExplainResult&&&&& : " + iExplainResult);
				//System.out.println("&&&&&iDataType&&&&& : " + iDataType);
				//System.out.println("&&&&&alDataList&&&&& : " + alDataList);

				if (iExplainResult == 0) { //解释成功
					if (iDataType == 10) { //普通数据
						if (alDataList != null) {
							Iterator itSfeNormalData = alDataList.iterator();
							while (itSfeNormalData != null && itSfeNormalData.hasNext()) {
								sfeNormalData = (SFE_NormalData) itSfeNormalData.next();
								iCldlx = sfeNormalData.GetMeasuredPointType();
								iCldxh = sfeNormalData.GetMeasuredPointNo();
								//System.out.println("sfeNormalData.GetMeasuredAdd() : " + sfeNormalData.GetMeasuredAdd());
								sClddz = new String((sfeNormalData.GetMeasuredAdd() != null ? sfeNormalData
										.GetMeasuredAdd() : cSpace));
								//System.out.println("sClddz : " + sClddz);
								alDataItemList = sfeNormalData.DataItemList;

								//System.out.println("&&&&&iCldlx&&&&& : " + iCldlx);
								//System.out.println("&&&&&iCldxh&&&&& : " + iCldxh);
								//System.out.println("&&&&&sClddz&&&&& : " + sClddz);
								//System.out.println("&&&&&alDataItemList&&&&& : " + alDataItemList);

								if (alDataItemList != null) {
									Iterator itSfeDataItem = alDataItemList.iterator();
									while (itSfeDataItem != null && itSfeDataItem.hasNext()) {
										sfeDataItem = (SFE_DataItem) itSfeDataItem.next();
										sDataCaption = new String((sfeDataItem.GetDataCaption() != null ? sfeDataItem
												.GetDataCaption() : cSpace));
										sDataContent = new String((sfeDataItem.GetDataContent() != null ? sfeDataItem
												.GetDataContent() : cSpace));

										//System.out.println("&&&&&sDataCaption&&&&& : " + sDataCaption);
										//System.out.println("&&&&&sDataContent&&&&& : " + sDataContent);

										if (iZjFlag == 1) { //中继
											//key : （终端逻辑地址）#（测量点类型）#（测量点地址）#（数据标识）
											sKey = "10@" + (sZdljdz != null ? sZdljdz.trim() : "") + "#" + iCldlx + "#"
													+ sClddz.trim() + "#"
													+ (sDataCaption != null ? sDataCaption.trim() : "");
										} else { //非中继
											//key : （终端逻辑地址）#（测量点类型）#（测量点序号）#（数据标识）
											sKey = "10@" + (sZdljdz != null ? sZdljdz.trim() : "") + "#" + iCldlx + "#"
													+ iCldxh + "#" + (sDataCaption != null ? sDataCaption.trim() : "");
										}
										htResult.put(sKey, (sDataContent != null ? sDataContent.trim() : ""));
									}
								}
							}
						}
					} else if (iDataType == 20) { //历史数据
						if (alDataList != null) {
							Iterator itSfeHistoryData = alDataList.iterator();
							while (itSfeHistoryData != null && itSfeHistoryData.hasNext()) {
								sfeHistoryData = (SFE_HistoryData) itSfeHistoryData.next();
								iTaskNo = sfeHistoryData.GetTaskNo();
								sTaskDateTime = new String((sfeHistoryData.GetTaskDateTime() != null ? sfeHistoryData
										.GetTaskDateTime() : cSpace));
								iCldlx = sfeHistoryData.GetMeasuredPointType();
								iCldxh = sfeHistoryData.GetMeasuredPointNo();
								alDataItemList = sfeHistoryData.DataItemList;

								if (alDataItemList != null) {
									Iterator itSfeDataItem = alDataItemList.iterator();
									while (itSfeDataItem != null && itSfeDataItem.hasNext()) {
										sfeDataItem = (SFE_DataItem) itSfeDataItem.next();
										sDataCaption = new String((sfeDataItem.GetDataCaption() != null ? sfeDataItem
												.GetDataCaption() : cSpace));
										sDataContent = new String((sfeDataItem.GetDataContent() != null ? sfeDataItem
												.GetDataContent() : cSpace));

										//key : （终端逻辑地址）#（测量点类型）#（测量点序号）#（时标）#（数据标识）
										sKey = "20@" + (sZdljdz != null ? sZdljdz.trim() : "") + "#" + iCldlx + "#"
												+ iCldxh + "#" + (sTaskDateTime != null ? sTaskDateTime.trim() : "0")
												+ "#" + (sDataCaption != null ? sDataCaption.trim() : "");
										
										htResult.put(sKey, (sDataContent != null ? sDataContent.trim() : ""));
									}
								}
							}
						}
					} else if (iDataType == 30) { //告警数据
						if (alDataList != null) {
							Iterator itSfeAlarmData = alDataList.iterator();
							while (itSfeAlarmData != null && itSfeAlarmData.hasNext()) {
								sfeAlarmData = (SFE_AlarmData) itSfeAlarmData.next();
								sAlarmCode = new String((sfeAlarmData.GetAlarmCode() != null ? sfeAlarmData
										.GetAlarmCode() : cSpace));
								iAlarmType = sfeAlarmData.GetAlarmType();
								sAlarmDateTime = new String((sfeAlarmData.GetAlarmDateTime() != null ? sfeAlarmData
										.GetAlarmDateTime() : cSpace));
								iCldlx = sfeAlarmData.GetMeasuredPointType();
								iCldxh = sfeAlarmData.GetMeasuredPointNo();
								alDataItemList = sfeAlarmData.DataItemList;

								//key : （终端逻辑地址）#（测量点类型）#（测量点序号）#（异常代码）#（异常类型）#（异常时间）
								sKey = "30@" + (sZdljdz != null ? sZdljdz.trim() : "") + "#" + iCldlx + "#" + iCldxh
										+ "#" + (sAlarmCode != null ? sAlarmCode.trim() : "") + "#" + iAlarmType + "#"
										+ (sAlarmDateTime != null ? sAlarmDateTime.trim() : "");
								htResult.put(sKey, alDataItemList);							
							}
						}
					} else if (iDataType == 40) { //设置返回数据
						if (alDataList != null) {
							Iterator itSfeSetResultData = alDataList.iterator();
							while (itSfeSetResultData != null && itSfeSetResultData.hasNext()) {
								sfeSetResultData = (SFE_SetResultData) itSfeSetResultData.next();
								iCldlx = sfeSetResultData.GetMeasuredPointType();
								iCldxh = sfeSetResultData.GetMeasuredPointNo();
								sClddz = new String((sfeSetResultData.GetMeasuredAdd() != null ? sfeSetResultData
										.GetMeasuredAdd() : cSpace));
								alSetResultDataList = sfeSetResultData.SetResultDataList;

								if (alSetResultDataList != null) {
									Iterator itSfeSetResultItem = alSetResultDataList.iterator();
									while (itSfeSetResultItem != null && itSfeSetResultItem.hasNext()) {
										sfeSetResultItem = (SFE_SetResultItem) itSfeSetResultItem.next();
										sDataCaption = new String(
												(sfeSetResultItem.GetDataCaption() != null ? sfeSetResultItem
														.GetDataCaption() : cSpace));
										iSetResult = sfeSetResultItem.GetSetResut();

										//key : （终端逻辑地址）#（测量点类型）#（测量点序号）#（数据标识）
										sKey = "40@" + (sZdljdz != null ? sZdljdz.trim() : "") + "#" + iCldlx + "#"
												+ iCldxh + "#" + (sDataCaption != null ? sDataCaption.trim() : "");
									
										htResult.put(sKey, "" + iSetResult);
									}
								}
							}
						}
					}
				} else { //解释失败
					//key : （终端逻辑地址）
					sKey = "-1@" + (sZdljdz != null ? sZdljdz.trim() : "");
					htResult.put(sKey, "" + iExplainResult);
				}
			}
		} else {
			htResult = null;
		}

		return htResult;
	}
}

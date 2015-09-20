package com.chooshine.fep.FrameDataAreaExplain;

import com.chooshine.fep.ConstAndTypeDefine.*;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

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
public class FrameDataAreaExplainGuYuan {
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
    //private final int c_FloatEx = 60; //特殊浮点型（对应固原集抄规范格式2,带幂部）
    private final int c_FloatFH = 70; //带正负号的浮点型 (0为正号或是上浮，1为负号或是下浮)
    //private final int c_DateTimeXQ = 80; //带星期的日期型2004-10-10 10：10：10#4
    //private final int c_IntDWFH = 90; //带单位标志和正负号的整型值串
    //private final int c_IntZJDWFH = 91; //带追加,单位标志和正负号的整型值串 8月22号增加

    private final int MAX_LENGTH_FHCOUNT = 120; //负荷数据点数最大值

    

    SPE_CommandInfoList TermialGuYuanCommandInfoList = new SPE_CommandInfoList(); //终端固原命令信息队列
    SPE_TaskInfoList GuYuanTaskInfoList = new SPE_TaskInfoList(); //固原集抄任务信息队列

    FrameDataAreaExplainGluMethod GluMethodGuYuan = new
            FrameDataAreaExplainGluMethod(); //数据区解析公用类
    
    public FrameDataAreaExplainGuYuan(String path) {
        try {
            InitialList(path,null);
        } catch (Exception e) {
        	Glu_ConstDefine.Log1.WriteLog("FrameDataAreaExplainGuYuan() error"+e.toString());
            System.out.println(e.toString());
        }
    }
    public FrameDataAreaExplainGuYuan(URI ur) {
        try {
            InitialList("",ur);
        } catch (Exception e) {
        	Glu_ConstDefine.Log1.WriteLog("FrameDataAreaExplainGuYuan() error"+e.toString());
            System.out.println(e.toString());
        }
    }

    public boolean InitialList(String path,URI ur) {
        boolean Result = false;
        try {
            try {
                if (Glu_ConstDefine.TerminalGuYuanSupport) {
                	Glu_DataAccess DataAccess;
                	if (ur == null) {
						DataAccess = new Glu_DataAccess(path);
					} else {
						DataAccess = new Glu_DataAccess(ur);
					}
                    DataAccess.LogIn(0);
                    TermialGuYuanCommandInfoList = DataAccess.
                            GetCommandInfoList(150); //初始化终端固原命令信息队列
                    GuYuanTaskInfoList = DataAccess.GetTaskInfoList(150); //初始化任务信息队列
                    DataAccess.LogOut(0);
                }
            } catch (Exception e) {
                Glu_ConstDefine.Log1.WriteLog(
                        "Func:FrameDataAreaExplainGuYuan__InitialList();Error:" +
                        e.toString());
            }
        } finally {
        }
        return Result;
    }

    public SFE_DataListInfo ExplainNormalDataAreaGuYuan( //普通数据区解释
            String FrameDataArea, int TermialProtocolType, String ControlCode) { //帧数据区,终端规约,功能码
        SFE_DataListInfo DataListInfo = new SFE_DataListInfo();
        ArrayList <SFE_NormalData>NormalDataList = new ArrayList<SFE_NormalData>();
        try {
            try {
                SFE_NormalData NormalData = new SFE_NormalData();
                SFE_NormalDataTemp NormalDataTemp = new SFE_NormalDataTemp();
                SPE_CommandInfoList CommandInfoList = new SPE_CommandInfoList();
                ArrayList CommandList = new ArrayList();
                int[] MeasuredSignList;
                boolean IsBreak = false;
                String sDataCaption = "", sDA = "", sDT = "";
                if (ControlCode.equals("0A") || ControlCode.equals("0C")) {
                    if (TermialProtocolType == 150) { //终端固原
                        CommandInfoList = TermialGuYuanCommandInfoList;
                    }
                    if (ControlCode.equals("0A")) { //参数查询
                        ControlCode = "04";
                    } while (FrameDataArea.length() >= 8 && IsBreak == false) { //无数据体时至少还4个字节(数据单元标识)
                        sDA = FrameDataArea.substring(0, 4);
                        sDT = FrameDataArea.substring(4, 8);
                        MeasuredSignList = GluMethodGuYuan.
                                           ExplainMeasuredPointList(sDA,
                                TermialProtocolType); //解释信息点得到测量点列表
                        CommandList = GluMethodGuYuan.ExplainCommandList(sDT,
                                ControlCode); //解释信息类得到命令列表
                        FrameDataArea = FrameDataArea.substring(8,
                                FrameDataArea.length()); //消去数据单元标识
                        int iFn = Integer.parseInt(CommandList.get(0).toString().
                                substring(1, 4)); //用第一个命令的Fn来判断全部命令的数据类型
                        int iMeasuredType = 10, iCount = 0;
                        if (ControlCode.equals("0C") &&
                            ((iFn >= 81 && iFn <= 84) ||
                             (iFn >= 17 && iFn <= 24))) {
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
                                NormalDataTemp = GluMethodGuYuan.
                                                 CommandSearchAndExplain(
                                        FrameDataArea, sDataCaption,
                                        CommandInfoList, TermialProtocolType,
                                        iCount, iFn);
                                if (NormalDataTemp.GetCommandLen() > 0 ||
                                    sDataCaption.equals("000000")) { //000000表示无召测数据项
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
                                for (int k = 0; k < NormalDataTemp.DataItemList.
                                             size(); k++) { //把解释后的数据保存在中间对象里
                                    NormalData.DataItemList.add((SFE_DataItem)NormalDataTemp.
                                            DataItemList.get(k));
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
                Glu_ConstDefine.Log1.WriteLog(
                        "Func:FrameDataAreaExplainGuYuan__ExplainNormalDataAreaGuYuan();Error:" +
                        e.toString());
            }
        } finally {
            DataListInfo.DataType = 10;
            DataListInfo.DataList = NormalDataList;
        }
        return DataListInfo;
    }

    public SFE_DataListInfo ExplainSetResultDataAreaGuYuan( //固原集抄设置返回区解释
            String FrameDataArea, int TermialProtocolType, String ControlCode) { //帧数据区,终端规约,控制码
        SFE_DataListInfo DataListInfo = new SFE_DataListInfo();
        ArrayList <SFE_SetResultData>SetResultDataList = new ArrayList<SFE_SetResultData>();
        try {
            try {
                SFE_SetResultData SetResultData = new SFE_SetResultData();
                ArrayList CommandList = new ArrayList();
                int[] MeasuredSignList;
                String sDataCaption = "",  sDA = "", sDT = "";
                //int iMeasured = 0;
                int iSetResult = -1;
                int Fn = Integer.parseInt(FrameDataArea.substring(4, 6), 16) &
                         7; //1:全部确认;2:全部否认;4:部分确认部分否认
                FrameDataArea = FrameDataArea.substring(8, FrameDataArea.length()); //消去数据单元标识
                if (Fn == 1 || Fn == 2) {
                    SetResultData.SetMeasuredPointNo(0);
                    SetResultData.SetMeasuredPointType(10);
                    sDataCaption = "000" + ("" + Fn) + "00"; //000100:全部确认;000200:全部否认
                    SetResultData.DataItemAdd(sDataCaption, iSetResult);
                    SetResultDataList.add(SetResultData);
                } else { //部分确认部分否认
                    ControlCode = FrameDataArea.substring(0, 2); //功能码
                    FrameDataArea = FrameDataArea.substring(2,
                            FrameDataArea.length()); //消去功能码
                    while (FrameDataArea.length() > 0) {
                        sDA = FrameDataArea.substring(0, 4);
                        sDT = FrameDataArea.substring(4, 8);
                        iSetResult = Integer.parseInt(FrameDataArea.substring(8,
                                10), 16); //错误码
                        MeasuredSignList = GluMethodGuYuan.
                                           ExplainMeasuredPointList(sDA,
                                TermialProtocolType); //解释信息点得到测量点列表
                        CommandList = GluMethodGuYuan.ExplainCommandList(sDT,
                                ControlCode); //解释信息类得到命令列表
                        FrameDataArea = FrameDataArea.substring(10,
                                FrameDataArea.length()); //消去数据单元标识和错误码
                        for (int i = 0; i < MeasuredSignList.length; i++) {
                            SetResultData = new SFE_SetResultData();
                            SetResultData.SetMeasuredPointNo(MeasuredSignList[i]);
                            SetResultData.SetMeasuredPointType(10);
                            for (int j = 0; j < CommandList.size(); j++) {
                                SetResultData.DataItemAdd(CommandList.get(j).
                                        toString(), iSetResult);
                            }
                            SetResultDataList.add(SetResultData);
                        }
                    }
                }

            } catch (Exception e) {
                Glu_ConstDefine.Log1.WriteLog(
                        "Func:FrameDataAreaExplainGuYuan__ExplainSetResultDataAreaGuYuan();Error:" +
                        e.toString());
            }
        } finally {
            DataListInfo.ExplainResult = 0;
            DataListInfo.DataType = 40;
            DataListInfo.DataList = SetResultDataList;
            
        }return DataListInfo;
    }

    public SFE_DataListInfo ExplainHistoryDataAreaGuYuan( //历史数据区解释(一二类历史数据)
            String FrameDataArea, int TermialProtocolType, String ControlCode,
            int FrameType) { //帧数据区,终端规约,功能码,帧类型(主动上送还是召测返回)
        SFE_DataListInfo DataListInfo = new SFE_DataListInfo();
        ArrayList <SFE_HistoryData>HistoryDataList = new ArrayList<SFE_HistoryData>();
        try {
            try {
                SFE_NormalDataTemp NormalDataTemp = new SFE_NormalDataTemp();
                SPE_CommandInfoList CommandInfoList = new SPE_CommandInfoList();
                SFE_HistoryData[] HistoryDataListTemp = new SFE_HistoryData[
                        MAX_LENGTH_FHCOUNT]; //数据点数最大值=120
                for (int i = 0; i < MAX_LENGTH_FHCOUNT; i++) {
                    HistoryDataListTemp[i] = new SFE_HistoryData();
                }
                ArrayList CommandList = new ArrayList();
                ArrayList <String>CommandListTemp = new ArrayList<String>();
                int[] MeasuredSignList;
                String sDataCaption = "", sDA = "", sDT = "",
                        sTaskDateTimeInfo = "", sTaskDateTime = "";
                int iFn = 0, iDataType = 0, iDataDensity = 0, iDataCount = 1,
                        iMeasuredType = 10, iCount = 0;
                CommandInfoList = TermialGuYuanCommandInfoList;
                int iTaskCount = -1;
                while (FrameDataArea.length() >= 8) { //无数据体时至少还4个字节(数据单元标识)
                    iTaskCount++;
                    sDA = FrameDataArea.substring(0, 4);
                    sDT = FrameDataArea.substring(4, 8);
                    iDataType = GluMethodGuYuan.GetGuYuanDataType(sDT,
                            ControlCode); //得到数据类型
                    if (ControlCode.equals("C0")) { //对自定义命令特殊处理:除了消去数据单元还要把厂家代码'38303030'消去
                        ControlCode = "0G";
                        FrameDataArea = FrameDataArea.substring(16,
                                FrameDataArea.length());
                    } else {
                        FrameDataArea = FrameDataArea.substring(8,
                                FrameDataArea.length()); //消去数据单元标识
                    }
                    MeasuredSignList = GluMethodGuYuan.
                                       ExplainMeasuredPointList(sDA,
                            TermialProtocolType); //解释信息点得到测量点列表
                    CommandList = GluMethodGuYuan.ExplainCommandList(sDT,
                            ControlCode); //解释信息类得到命令列表
                    if (CommandList.get(0).toString().equals("000000")) { //无效数据
                        continue;
                    }
                    for (int i = 0; i < CommandList.size(); i++) { //把解释后命令列表保存起来,最后再匹配数据库保存的任务信息来确认任务号
                        CommandListTemp.add(CommandList.get(i).toString());
                    }
                    //处理数据时标
                    if (iDataType == 21) { //一类数据的小时冻结数据
                        sTaskDateTimeInfo = GetTaskDateTimeInfo(FrameDataArea.
                                substring(0, 2), iDataType);
                    }
                    if (iDataType == 22) { //曲线数据
                        sTaskDateTimeInfo = GetTaskDateTimeInfo(FrameDataArea.
                                substring(0, 14), iDataType);
                    } else if (iDataType == 23) { //日冻结数据
                        sTaskDateTimeInfo = GetTaskDateTimeInfo(FrameDataArea.
                                substring(0, 6), iDataType);
                    } else if (iDataType == 24) { //月冻结数据
                        sTaskDateTimeInfo = GetTaskDateTimeInfo(FrameDataArea.
                                substring(0, 4), iDataType);
                    }
                    if (sTaskDateTimeInfo.length() >= 17) { //数据时标解释成功
                        sTaskDateTime = sTaskDateTimeInfo.substring(0, 14); //开始时间
                        iDataDensity = Integer.parseInt(sTaskDateTimeInfo.
                                substring(14, 16)); //时间间隔
                        iDataCount = Integer.parseInt(sTaskDateTimeInfo.
                                substring(16, sTaskDateTimeInfo.length())); //数据点数
                    }
                    //解释任务数据
                    for (int i = 0; i < MeasuredSignList.length; i++) { //测量点个数
                        for (int j = 0; j < CommandList.size(); j++) { //命令个数
                            //每个命令都包含数据时标，假设上行任务数据同一单元标识里命令都是同一类数据，且时标都是一样的，不然是无法按任务结构解释的
                            switch (iDataType) {
                            case 21:
                                FrameDataArea = FrameDataArea.substring(2,
                                        FrameDataArea.length());
                                break;
                            case 22:
                                FrameDataArea = FrameDataArea.substring(14,
                                        FrameDataArea.length());
                                break;
                            case 23:
                                FrameDataArea = FrameDataArea.substring(6,
                                        FrameDataArea.length());
                                break;
                            case 24:
                                FrameDataArea = FrameDataArea.substring(4,
                                        FrameDataArea.length());
                                break;
                            }
                            for (int m = 0; m < iDataCount; m++) { //数据点数
                                HistoryDataListTemp[m+iTaskCount].SetTaskNo(0);
                                HistoryDataListTemp[m+iTaskCount].SetMeasuredPointNo(
                                        MeasuredSignList[i]);
                                HistoryDataListTemp[m+iTaskCount].SetMeasuredPointType(
                                        iMeasuredType);
                                if (iDataType == 23) {
                                    HistoryDataListTemp[m+iTaskCount].SetTaskDateTime(
                                            DataSwitch.IncreaseDateTime(
                                            sTaskDateTime, 0, ////统一曲线的起始时间就是每隔小时的0分作为开始
                                            4));

                                } else {
                                    HistoryDataListTemp[m+iTaskCount].SetTaskDateTime(
                                            DataSwitch.IncreaseDateTime(
                                            sTaskDateTime, iDataDensity * m, ////统一曲线的起始时间就是每隔小时的0分作为开始
                                            2));
                                }
                                sDataCaption = CommandList.get(j).toString();
                                NormalDataTemp = GluMethodGuYuan.
                                                 CommandSearchAndExplain(
                                        FrameDataArea, sDataCaption,
                                        CommandInfoList, TermialProtocolType,
                                        iCount, iFn);
                                FrameDataArea = FrameDataArea.substring(
                                        NormalDataTemp.GetCommandLen(),
                                        FrameDataArea.length());
                                for (int k = 0; k < NormalDataTemp.DataItemList.
                                             size(); k++) { //把解释后的数据保存在中间对象里
                                    HistoryDataListTemp[m+iTaskCount].DataItemList.add(
                                            NormalDataTemp.DataItemList.get(k));
                                    HistoryDataListTemp[m+iTaskCount].DataItemCountAdd();
                                }
                            }
                            iTaskCount = iTaskCount + iDataCount;
                        }
                    }
                    MeasuredSignList = null; //清空数组内容
                    CommandList.clear(); //清空队列内容
                }
                //从任务信息队列查找任务号和测量点类型,返回历史解析数据列表
                if (FrameType == 11) { //主动上送任务
                    HistoryDataList = GluMethodGuYuan.
                                      SearchAndMatchingTaskInfoList(
                                              CommandListTemp,
                                              HistoryDataListTemp, iTaskCount,
                                              GuYuanTaskInfoList); //从数据库配置信息查找主站配置的任务号
                    if (HistoryDataList.size() == 0) {
                        DataListInfo.ExplainResult = 50018; //未找到任务配置信息
                        for (int i = 0; i < iTaskCount; i++) {
                            HistoryDataList.add(HistoryDataListTemp[i]);
                        }
                    }
                } else {
                    for (int i = 0; i < iTaskCount; i++) {
                        String sCommand = (String) CommandListTemp.get(0);
                        HistoryDataListTemp[i].SetTaskNo(GluMethodGuYuan.
                                GetSelfDefineTaskNo(150, sCommand,
                                GuYuanTaskInfoList));
                        HistoryDataList.add(HistoryDataListTemp[i]);
                    }
                }
            } catch (Exception e) {
                Glu_ConstDefine.Log1.WriteLog(
                        "Func:FrameDataAreaExplainGuYuan__ExplainHistoryDataAreaGuYuan();Error:" +
                        e.toString());
            }
        } finally {
            DataListInfo.DataType = 20;
            DataListInfo.DataList = HistoryDataList;
            
        }
        return DataListInfo;
    }

    public SFE_DataListInfo ExplainAlarmDataAreaGuYuan( //终端固原告警数据区解释
            String FrameDataArea, int TermialProtocolType, String ControlCode) { //帧数据区,终端规约,控制码
        SFE_DataListInfo DataListInfo = new SFE_DataListInfo();
        ArrayList AlarmDataList = new ArrayList();
        try {
            try {
                //SFE_ExplainAlarmData ExplainAlarmData = new  SFE_ExplainAlarmData();
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
                        AlarmDataList = ExplainAlarmParameterGuYuan(
                                FrameDataArea, iFn, iAlarmDataCount);
                    }
                }
            } catch (Exception e) {
                Glu_ConstDefine.Log1.WriteLog(
                        "Func:FrameDataAreaExplainGuYuan__ExplainAlarmDataAreaGuYuan();Error:" +
                        e.toString());
            }
        } finally {            
        }
        DataListInfo.ExplainResult = 0;
        DataListInfo.DataType = 30;
        DataListInfo.DataList = AlarmDataList;
        return DataListInfo;
    }

    public ArrayList ExplainAlarmParameterGuYuan(String sDataContent, int Fn,
                                                 int AlarmDataCount) {
        ArrayList <SFE_AlarmData>AlarmDataList = new ArrayList<SFE_AlarmData>();
        ArrayList <SFE_DataItemInfo>DataItemInfoList = new ArrayList<SFE_DataItemInfo>(); //数据项信息列表
        ArrayList <String>AlarmCodeList = new ArrayList<String>();
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
                    int iAlarmCode = Integer.parseInt(sDataContent.substring(0,
                            2), 16); //告警编码
                    int iAlarmlen = Integer.parseInt(sDataContent.substring(2,
                            4), 16) + 2; //告警长度
                    String sAlarmDateTime = "20" +
                                            DataSwitch.ReverseStringByByte(
                            sDataContent.substring(4, 14)) + "00"; //告警发生时间
                    sDataContent = sDataContent.substring(14,
                            sDataContent.length());
                    //所有异常都有告警编码、告警长度、告警发生时间固定字段
                    if (iAlarmCode == 1) { //ERC1:数据初始化和版本变更记录
                        DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
                        sDataCaption = "EC0101"; //事件标志
                        iDataType = c_String; //字符型
                        iDataLen = 1; //字节长度
                        sDataFormat = "1"; //数据格式
                        iStorageType = CCFS_HEX; //十六进制码
                        iStorageOrder = CCSX_NX; //逆序
                        sPhysicalData = sDataContent.substring(0, iDataLen * 2);
                        sDataContent = sDataContent.substring(iDataLen * 2,
                                sDataContent.length());
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
                        sPhysicalData = sDataContent.substring(0, iDataLen * 2);
                        sDataContent = sDataContent.substring(iDataLen * 2,
                                sDataContent.length());
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
                        sPhysicalData = sDataContent.substring(0, iDataLen * 2);
                        sDataContent = sDataContent.substring(iDataLen * 2,
                                sDataContent.length());
                        DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
                                iDataLen, sDataFormat, iStorageType,
                                iStorageOrder);
                        DataItemInfo.SetFPhysicalData(sPhysicalData);
                        DataItemInfoList.add(DataItemInfo);

                        if (!sAlarmSign.equals("")) {
                            iAlarmType = 1; //告警类型:1发生；2恢复
                            AlarmMaxCount = 3; //一个字节包含告警种类
                            AlarmCodeList = ExplainAlarmCodeGuYuan(iAlarmType,
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
                        sPhysicalData = sDataContent.substring(0, iDataLen * 2);
                        sDataContent = sDataContent.substring(iDataLen * 2,
                                sDataContent.length());
                        DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
                                iDataLen, sDataFormat, iStorageType,
                                iStorageOrder);
                        DataItemInfo.SetFPhysicalData(sPhysicalData);
                        DataItemInfoList.add(DataItemInfo);
                        sAlarmSign = sPhysicalData;

                        if (!sAlarmSign.equals("")) {
                            iAlarmType = 1; //告警类型:1发生；2恢复
                            AlarmMaxCount = 2; //一个字节包含告警种类
                            AlarmCodeList = ExplainAlarmCodeGuYuan(iAlarmType,
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
                        sPhysicalData = sDataContent.substring(0, iDataLen * 2);
                        sDataContent = sDataContent.substring(iDataLen * 2,
                                sDataContent.length());
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
                        sPhysicalData = sDataContent.substring(0, iDataLen * 2);
                        sDataContent = sDataContent.substring(iDataLen * 2,
                                sDataContent.length());
                        DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
                                iDataLen, sDataFormat, iStorageType,
                                iStorageOrder);
                        DataItemInfo.SetFPhysicalData(sPhysicalData);
                        DataItemInfoList.add(DataItemInfo);
                        AlarmCodeList.add("C030"); //参数变更记录
                    } else if (iAlarmCode == 8) { //ERC8:电能表参数变更
                        DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
                        sDataCaption = "EC0801"; //测量点号
                        iDataType = c_Float; //字符型
                        iDataLen = 2; //物理数据的字符长度
                        sDataFormat = "0000"; //数据格式
                        iStorageType = CCFS_HEX; //十六进制码
                        iStorageOrder = CCSX_SX; //顺序存储
                        sPhysicalData = "" +
                                        ((Integer.parseInt(DataSwitch.
                                ReverseStringByByte(sDataContent.
                                substring(0, iDataLen * 2)), 16) & 4095));
                        sMeasuredPointNo = sPhysicalData;
                        sPhysicalData = DataSwitch.IntToHex(sPhysicalData,
                                "0000");
                        sDataContent = sDataContent.substring(iDataLen * 2,
                                sDataContent.length());
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
                        sPhysicalData = sDataContent.substring(0, iDataLen * 2);
                        sDataContent = sDataContent.substring(iDataLen * 2,
                                sDataContent.length());
                        DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
                                iDataLen, sDataFormat, iStorageType,
                                iStorageOrder);
                        DataItemInfo.SetFPhysicalData(sPhysicalData);
                        DataItemInfoList.add(DataItemInfo);
                        sAlarmSign = sPhysicalData;

                        if (!sAlarmSign.equals("")) {
                            iAlarmType = 1; //告警类型:1发生;2恢复;状态变位
                            AlarmMaxCount = 6; //一个字节包含告警种类
                            AlarmCodeList = ExplainAlarmCodeGuYuan(iAlarmType,
                                    sAlarmSign, Integer.toString(iAlarmCode),
                                    AlarmMaxCount);
                        }

                    } else if (iAlarmCode == 9 || iAlarmCode == 10) { //ERC9:电流回路异常,ERC10:电压回路异常
                        sAlarmCode = DataSwitch.StrStuff("0", 2,
                                ("" + iAlarmCode), 10); //补足2两位
                        DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
                        sDataCaption = "EC" + sAlarmCode + "01"; //测量点号
                        iDataType = c_Float; //字符型
                        iDataLen = 2; //物理数据的字符长度
                        sDataFormat = "0000"; //数据格式
                        iStorageType = CCFS_HEX; //十六进制码
                        iStorageOrder = CCSX_SX; //顺序存储
                        sPhysicalData = sDataContent.substring(0, iDataLen * 2);
                        if ((Integer.parseInt(sPhysicalData, 16) & 128) == 128) {
                            iAlarmType = 10; //告警类型:10发生
                        } else {
                            iAlarmType = 20; //告警类型:20恢复
                        }

                        sPhysicalData = "" +
                                        ((Integer.parseInt(DataSwitch.
                                ReverseStringByByte(sPhysicalData), 16) &
                                          4095));
                        sMeasuredPointNo = sPhysicalData;
                        sPhysicalData = DataSwitch.IntToHex(sPhysicalData,
                                "0000");
                        sDataContent = sDataContent.substring(iDataLen * 2,
                                sDataContent.length());
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
                        sPhysicalData = sDataContent.substring(0, iDataLen * 2);
                        sDataContent = sDataContent.substring(iDataLen * 2,
                                sDataContent.length());
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
                        sPhysicalData = sDataContent.substring(0, iDataLen * 2);
                        sDataContent = sDataContent.substring(iDataLen * 2,
                                sDataContent.length());
                        DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
                                iDataLen, sDataFormat, iStorageType,
                                iStorageOrder);
                        DataItemInfo.SetFPhysicalData(sPhysicalData);
                        DataItemInfoList.add(DataItemInfo);

                        DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
                        sDataCaption = "EC" + sAlarmCode + "04"; //发生时的Ub   数据格式跟上一个一样
                        sPhysicalData = sDataContent.substring(0, iDataLen * 2);
                        sDataContent = sDataContent.substring(iDataLen * 2,
                                sDataContent.length());
                        DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
                                iDataLen, sDataFormat, iStorageType,
                                iStorageOrder);
                        DataItemInfo.SetFPhysicalData(sPhysicalData);
                        DataItemInfoList.add(DataItemInfo);

                        DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
                        sDataCaption = "EC" + sAlarmCode + "05"; //发生时的Uc/Ucb  数据格式跟上一个一样
                        sPhysicalData = sDataContent.substring(0, iDataLen * 2);
                        sDataContent = sDataContent.substring(iDataLen * 2,
                                sDataContent.length());
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
                        sPhysicalData = sDataContent.substring(0, iDataLen * 2);
                        sDataContent = sDataContent.substring(iDataLen * 2,
                                sDataContent.length());
                        DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
                                iDataLen, sDataFormat, iStorageType,
                                iStorageOrder);
                        DataItemInfo.SetFPhysicalData(sPhysicalData);
                        DataItemInfoList.add(DataItemInfo);

                        DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
                        sDataCaption = "EC" + sAlarmCode + "07"; //发生时的Ib     数据格式跟上一个一样
                        sPhysicalData = sDataContent.substring(0, iDataLen * 2);
                        sDataContent = sDataContent.substring(iDataLen * 2,
                                sDataContent.length());
                        DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
                                iDataLen, sDataFormat, iStorageType,
                                iStorageOrder);
                        DataItemInfo.SetFPhysicalData(sPhysicalData);
                        DataItemInfoList.add(DataItemInfo);

                        DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
                        sDataCaption = "EC" + sAlarmCode + "08"; //发生时的Ic    数据格式跟上一个一样
                        sPhysicalData = sDataContent.substring(0, iDataLen * 2);
                        sDataContent = sDataContent.substring(iDataLen * 2,
                                sDataContent.length());
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
                        sPhysicalData = sDataContent.substring(0, iDataLen * 2);
                        sDataContent = sDataContent.substring(iDataLen * 2,
                                sDataContent.length());
                        DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
                                iDataLen, sDataFormat, iStorageType,
                                iStorageOrder);
                        DataItemInfo.SetFPhysicalData(sPhysicalData);
                        DataItemInfoList.add(DataItemInfo);

                        if (!sAlarmSign.equals("")) {
                            if (iAlarmCode == 9) {
                                AlarmMaxCount = 3; //一个字节包含告警种类
                            } else if (iAlarmCode == 10) {
                                AlarmMaxCount = 2; //一个字节包含告警种类
                            }
                            AlarmCodeList = ExplainAlarmCodeGuYuan(iAlarmType,
                                    sAlarmSign, Integer.toString(iAlarmCode),
                                    AlarmMaxCount);
                        }
                    } else if (iAlarmCode == 12) { //ERC11:相序异常,ERC12:电能表时间超差
                        DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
                        sDataCaption = "EC" + ("" + iAlarmCode) + "01"; //测量点号
                        iDataType = c_Float; //字符型
                        iDataLen = 2; //物理数据的字符长度
                        sDataFormat = "0000"; //数据格式
                        iStorageType = CCFS_HEX; //十六进制码
                        iStorageOrder = CCSX_SX; //顺序存储
                        sPhysicalData = sDataContent.substring(0, iDataLen * 2);
                        if ((Integer.parseInt(sPhysicalData, 16) & 128) == 128) {
                            sAlarmCode = "C" + ("" + iAlarmCode) + "0"; //发生
                        } else {
                            sAlarmCode = "R" + ("" + iAlarmCode) + "0"; //恢复
                        }
                        sPhysicalData = "" +
                                        ((Integer.parseInt(DataSwitch.
                                ReverseStringByByte(sPhysicalData), 16) &
                                          4095));
                        sMeasuredPointNo = sPhysicalData;
                        sPhysicalData = DataSwitch.IntToHex(sPhysicalData,
                                "0000");
                        sDataContent = sDataContent.substring(iDataLen * 2,
                                sDataContent.length());
                        DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
                                iDataLen, sDataFormat, iStorageType,
                                iStorageOrder);
                        DataItemInfo.SetFPhysicalData(sPhysicalData);
                        DataItemInfoList.add(DataItemInfo);
                        AlarmCodeList.add(sAlarmCode);
                    } else if (iAlarmCode == 13) { //ERC13:电表故障信息
                        DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
                        sDataCaption = "EC1301"; //测量点号
                        iDataType = c_Float; //字符型
                        iDataLen = 2; //物理数据的字符长度
                        sDataFormat = "0000"; //数据格式
                        iStorageType = CCFS_HEX; //十六进制码
                        iStorageOrder = CCSX_SX; //顺序存储
                        sPhysicalData = sDataContent.substring(0, iDataLen * 2);
                        sPhysicalData = "" +
                                        (Integer.parseInt(DataSwitch.
                                ReverseStringByByte(sPhysicalData), 16) &
                                          4095);
                        sMeasuredPointNo = sPhysicalData;
                        sPhysicalData = DataSwitch.IntToHex(sPhysicalData,
                                "0000");
                        sDataContent = sDataContent.substring(iDataLen * 2,
                                sDataContent.length());
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
                        sPhysicalData = sDataContent.substring(0, iDataLen * 2);
                        sDataContent = sDataContent.substring(iDataLen * 2,
                                sDataContent.length());
                        DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
                                iDataLen, sDataFormat, iStorageType,
                                iStorageOrder);
                        DataItemInfo.SetFPhysicalData(sPhysicalData);
                        DataItemInfoList.add(DataItemInfo);
                        sAlarmSign = sPhysicalData;

                        if (!sAlarmSign.equals("")) {
                            iAlarmType = 1;
                            AlarmMaxCount = 5; //一个字节包含告警种类
                            AlarmCodeList = ExplainAlarmCodeGuYuan(iAlarmType,
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
                        sPhysicalData = sDataContent.substring(0, iDataLen * 2);
                        sDataContent = sDataContent.substring(iDataLen * 2,
                                sDataContent.length());
                        DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
                                iDataLen, sDataFormat, iStorageType,
                                iStorageOrder);
                        DataItemInfo.SetFPhysicalData(sPhysicalData);
                        DataItemInfoList.add(DataItemInfo);

                        int iElectrifyTime = Integer.parseInt(DataSwitch.
                                ReverseStringByByte(sPhysicalData)); //上电时间
                        String sPowerCutTime = sAlarmDateTime; //停电时间
                        int iAlarmDateTime = Integer.parseInt(sAlarmDateTime.
                                substring(3, 12)); //异常发生时间
                        if (iAlarmDateTime > iElectrifyTime) { //停电时间大于上电时间
                            sAlarmCode = "C140"; //停电
                        } else {
                            sAlarmDateTime = "20" +
                                             DataSwitch.
                                             ReverseStringByByte(sPhysicalData) +
                                             "00"; //异常发生时间为上电时间,告警为上电告警
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
                    } else if (iAlarmCode == 20) { //ERC20:密码错误记录
                        DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
                        sDataCaption = "EC2001"; //错误密码
                        iDataType = c_Float; //浮点型
                        iDataLen = 2; //物理数据的字符长度
                        sDataFormat = "0000"; //数据格式
                        iStorageType = CCFS_HEX; //十六进制方式
                        iStorageOrder = CCSX_NX; //逆序存储
                        sPhysicalData = sDataContent.substring(0, iDataLen * 2);
                        sDataContent = sDataContent.substring(iDataLen * 2,
                                sDataContent.length());
                        DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
                                iDataLen, sDataFormat, iStorageType,
                                iStorageOrder);
                        DataItemInfo.SetFPhysicalData(sPhysicalData);
                        DataItemInfoList.add(DataItemInfo);

                        DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
                        sDataCaption = "EC2002"; //启动站地址MSA
                        iDataType = c_Float; //浮点型
                        iDataLen = 1; //物理数据的字符长度
                        sDataFormat = "0000"; //数据格式
                        iStorageType = CCFS_HEX; //十六进制方式
                        iStorageOrder = CCSX_SX; //顺序存储
                        sPhysicalData = sDataContent.substring(0, iDataLen * 2);
                        sDataContent = sDataContent.substring(iDataLen * 2,
                                sDataContent.length());
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
                        sDataFormat = "0000"; //数据格式
                        iStorageType = CCFS_HEX; //十六进制方式
                        iStorageOrder = CCSX_SX; //顺序存储
                        sPhysicalData = sDataContent.substring(0, iDataLen * 2);
                        sDataContent = sDataContent.substring(iDataLen * 2,
                                sDataContent.length());
                        DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
                                iDataLen, sDataFormat, iStorageType,
                                iStorageOrder);
                        DataItemInfo.SetFPhysicalData(sPhysicalData);
                        DataItemInfoList.add(DataItemInfo);
                        sAlarmSign = DataSwitch.HexToInt(sPhysicalData, "00");

                        if (!sAlarmSign.equals("")) {
                            if (Integer.parseInt(sAlarmSign) > 128) {
                                sAlarmCode = "R21" +
                                             Integer.toString(Integer.
                                        parseInt(sAlarmSign) & 7);
                            } else {
                                sAlarmCode = "C21" +
                                             Integer.toString(Integer.
                                        parseInt(sAlarmSign) & 7);
                            }
                            AlarmCodeList.add(sAlarmCode);
                        }
                    } else if (iAlarmCode == 27) { //ERC27:电能表示度下降记录
                        DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
                        sDataCaption = "EC" + ("" + iAlarmCode) + "01"; //测量点号
                        iDataType = c_Float; //字符型
                        iDataLen = 2; //物理数据的字符长度
                        sDataFormat = "0000"; //数据格式
                        iStorageType = CCFS_HEX; //十六进制码
                        iStorageOrder = CCSX_SX; //顺序存储

                        sPhysicalData = "" +
                                        (Integer.parseInt(DataSwitch.
                                ReverseStringByByte(sDataContent.
                                substring(0, iDataLen * 2)), 16) & 4095);
                        sMeasuredPointNo = sPhysicalData;
                        sPhysicalData = DataSwitch.IntToHex(sPhysicalData,
                                "0000");
                        sDataContent = sDataContent.substring(iDataLen * 2,
                                sDataContent.length());
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
                        sPhysicalData = sDataContent.substring(0, iDataLen * 2);
                        sDataContent = sDataContent.substring(iDataLen * 2,
                                sDataContent.length());
                        DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
                                iDataLen, sDataFormat, iStorageType,
                                iStorageOrder);
                        DataItemInfo.SetFPhysicalData(sPhysicalData);
                        DataItemInfoList.add(DataItemInfo);

                        if (iAlarmCode != 30) {
                            DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
                            sDataCaption = "EC" + ("" + iAlarmCode) + "03"; //下降后电能表正向有功总电能示值;电能量超差发生时正向有功总电能示值;电能表飞走发生后正向有功总电能示值
                            sPhysicalData = sDataContent.substring(0,
                                    iDataLen * 2);
                            sDataContent = sDataContent.substring(iDataLen * 2,
                                    sDataContent.length());
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
                            sPhysicalData = sDataContent.substring(0,
                                    iDataLen * 2);
                            sDataContent = sDataContent.substring(iDataLen * 2,
                                    sDataContent.length());
                            DataItemInfo.DataItemInfoAdd(sDataCaption,
                                    iDataType, iDataLen, sDataFormat,
                                    iStorageType, iStorageOrder);
                            DataItemInfo.SetFPhysicalData(sPhysicalData);
                            DataItemInfoList.add(DataItemInfo);
                        }
                        sAlarmCode = "C" + ("" + iAlarmCode) + "0";
                        AlarmCodeList.add(sAlarmCode);
                    } else if (iAlarmCode == 31) { //ERC31：终端抄表失败记录
                        DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
                        sDataCaption = "EC3101"; //测量点号
                        iDataType = c_Float; //字符型
                        iDataLen = 2; //物理数据的字符长度
                        sDataFormat = "0000"; //数据格式
                        iStorageType = CCFS_HEX; //十六进制码
                        iStorageOrder = CCSX_SX; //顺序存储
                        sPhysicalData = "" +
                                        (Integer.parseInt(DataSwitch.
                                ReverseStringByByte(sDataContent.
                                substring(0, iDataLen * 2)), 16) & 4095);
                        sMeasuredPointNo = sPhysicalData;
                        sPhysicalData = DataSwitch.IntToHex(sPhysicalData,
                                "0000");
                        sDataContent = sDataContent.substring(iDataLen * 2,
                                sDataContent.length());
                        DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
                                iDataLen, sDataFormat, iStorageType,
                                iStorageOrder);
                        DataItemInfo.SetFPhysicalData(sPhysicalData);
                        DataItemInfoList.add(DataItemInfo);

                        DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
                        sDataCaption = "EC3102"; //变更标志
                        iDataType = c_String; //字符型
                        iDataLen = 1; //物理数据的字符长度
                        sDataFormat = "1"; //数据格式
                        iStorageType = CCFS_HEX; //十六进制码
                        iStorageOrder = CCSX_SX; //顺序存储
                        sPhysicalData = "" +
                                        sDataContent.substring(0, iDataLen * 2);
                        sDataContent = sDataContent.substring(iDataLen * 2,
                                sDataContent.length());
                        DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
                                iDataLen, sDataFormat, iStorageType,
                                iStorageOrder);
                        DataItemInfo.SetFPhysicalData(sPhysicalData);
                        DataItemInfoList.add(DataItemInfo);

                        DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
                        sDataCaption = "EC3103"; //上一次抄表失败的时间
                        iDataType = c_DateTime; //字符型
                        iDataLen = 5; //物理数据的字符长度
                        sDataFormat = "YYMMDDHHNN"; //数据格式
                        iStorageType = CCFS_BCD; //BCD码
                        iStorageOrder = CCSX_NX; //逆序存储
                        sPhysicalData = "" +
                                        sDataContent.substring(0, iDataLen * 2);
                        sDataContent = sDataContent.substring(iDataLen * 2,
                                sDataContent.length());
                        DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
                                iDataLen, sDataFormat, iStorageType,
                                iStorageOrder);
                        DataItemInfo.SetFPhysicalData(sPhysicalData);
                        DataItemInfoList.add(DataItemInfo);

                        DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
                        sDataCaption = "EC3104"; //最近一次抄表成功的时间
                        iDataType = c_DateTime; //字符型
                        iDataLen = 5; //物理数据的字符长度
                        sDataFormat = "YYMMDDHHNN"; //数据格式
                        iStorageType = CCFS_BCD; //BCD码
                        iStorageOrder = CCSX_NX; //逆序存储
                        sPhysicalData = "" +
                                        sDataContent.substring(0, iDataLen * 2);
                        sDataContent = sDataContent.substring(iDataLen * 2,
                                sDataContent.length());
                        DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
                                iDataLen, sDataFormat, iStorageType,
                                iStorageOrder);
                        DataItemInfo.SetFPhysicalData(sPhysicalData);
                        DataItemInfoList.add(DataItemInfo);

                        sAlarmCode = "C" + ("" + iAlarmCode) + "0";
                        AlarmCodeList.add(sAlarmCode);
                    } else if (iAlarmCode == 32) { //ERC32：电能表过零用电记录
                        DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
                        sDataCaption = "EC3201"; //测量点号
                        iDataType = c_Float; //字符型
                        iDataLen = 2; //物理数据的字符长度
                        sDataFormat = "0000"; //数据格式
                        iStorageType = CCFS_HEX; //十六进制码
                        iStorageOrder = CCSX_SX; //顺序存储
                        sPhysicalData = "" +
                                        (Integer.parseInt(DataSwitch.
                                ReverseStringByByte(sDataContent.
                                substring(0, iDataLen * 2)), 16) & 4095);
                        sMeasuredPointNo = sPhysicalData;
                        sPhysicalData = DataSwitch.IntToHex(sPhysicalData,
                                "0000");
                        sDataContent = sDataContent.substring(iDataLen * 2,
                                sDataContent.length());
                        DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
                                iDataLen, sDataFormat, iStorageType,
                                iStorageOrder);
                        DataItemInfo.SetFPhysicalData(sPhysicalData);
                        DataItemInfoList.add(DataItemInfo);

                        DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
                        sDataCaption = "EC3202"; //当前电表的剩余电量示值
                        iDataType = c_FloatFH; //字符型
                        iDataLen = 5; //物理数据的字符长度
                        sDataFormat = "SS000000.00"; //数据格式
                        iStorageType = CCFS_BCD; //十六进制码
                        iStorageOrder = CCSX_NX; //顺序存储
                        sPhysicalData = "" + (sDataContent.
                                              substring(0, iDataLen * 2));
                        sDataContent = sDataContent.substring(iDataLen * 2,
                                sDataContent.length());
                        DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
                                iDataLen, sDataFormat, iStorageType,
                                iStorageOrder);
                        DataItemInfo.SetFPhysicalData(sPhysicalData);
                        DataItemInfoList.add(DataItemInfo);
                        sAlarmCode = "C" + ("" + iAlarmCode) + "0";
                        AlarmCodeList.add(sAlarmCode);
                    } else if (iAlarmCode == 33) { //ERC33：电能表购电量参数下发电表成功记录
                        DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
                        sDataCaption = "EC3301"; //测量点号
                        iDataType = c_Float; //字符型
                        iDataLen = 2; //物理数据的字符长度
                        sDataFormat = "0000"; //数据格式
                        iStorageType = CCFS_HEX; //十六进制码
                        iStorageOrder = CCSX_SX; //顺序存储
                        sPhysicalData = "" +
                                        (Integer.parseInt(DataSwitch.
                                ReverseStringByByte(sDataContent.
                                substring(0, iDataLen * 2)), 16) & 4095);
                        sMeasuredPointNo = sPhysicalData;
                        sPhysicalData = DataSwitch.IntToHex(sPhysicalData,
                                "0000");
                        sDataContent = sDataContent.substring(iDataLen * 2,
                                sDataContent.length());
                        DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
                                iDataLen, sDataFormat, iStorageType,
                                iStorageOrder);
                        DataItemInfo.SetFPhysicalData(sPhysicalData);
                        DataItemInfoList.add(DataItemInfo);

                        DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
                        sDataCaption = "EC3302"; //购电单号
                        iDataType = c_Float; //字符型
                        iDataLen = 4; //物理数据的字符长度
                        sDataFormat = "00000000"; //数据格式
                        iStorageType = CCFS_HEX; //十六进制码
                        iStorageOrder = CCSX_SX; //顺序存储
                        sPhysicalData = "" +
                                        Integer.parseInt(DataSwitch.
                                ReverseStringByByte(sDataContent.
                                substring(0, iDataLen * 2)), 16);
                        sPhysicalData = DataSwitch.IntToHex(sPhysicalData,
                                "00000000");
                        sDataContent = sDataContent.substring(iDataLen * 2,
                                sDataContent.length());
                        DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
                                iDataLen, sDataFormat, iStorageType,
                                iStorageOrder);
                        DataItemInfo.SetFPhysicalData(sPhysicalData);
                        DataItemInfoList.add(DataItemInfo);

                        DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
                        sDataCaption = "EC3303"; //下发后表内的购电次数
                        iDataType = c_Float; //字符型
                        iDataLen = 2; //物理数据的字符长度
                        sDataFormat = "0000"; //数据格式
                        iStorageType = CCFS_HEX; //十六进制码
                        iStorageOrder = CCSX_SX; //顺序存储
                        sPhysicalData = "" +
                                        Integer.parseInt(DataSwitch.
                                ReverseStringByByte(sDataContent.
                                substring(0, iDataLen * 2)), 16);
                        sPhysicalData = DataSwitch.IntToHex(sPhysicalData,
                                "0000");
                        sDataContent = sDataContent.substring(iDataLen * 2,
                                sDataContent.length());
                        DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
                                iDataLen, sDataFormat, iStorageType,
                                iStorageOrder);
                        DataItemInfo.SetFPhysicalData(sPhysicalData);
                        DataItemInfoList.add(DataItemInfo);

                        DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
                        sDataCaption = "EC3304"; //下发后表内的当前累计购电量
                        iDataType = c_Float; //浮点型
                        iDataLen = 4; //物理数据的字符长度
                        sDataFormat = "000000.00"; //数据格式
                        iStorageType = CCFS_BCD; //BCD码
                        iStorageOrder = CCSX_NX; //逆序存储
                        sPhysicalData = sDataContent.substring(0, iDataLen * 2);
                        sDataContent = sDataContent.substring(iDataLen * 2,
                                sDataContent.length());
                        DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
                                iDataLen, sDataFormat, iStorageType,
                                iStorageOrder);
                        DataItemInfo.SetFPhysicalData(sPhysicalData);
                        DataItemInfoList.add(DataItemInfo);

                        DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
                        sDataCaption = "EC3305"; //下发后表内的剩余电量示值
                        iDataType = c_FloatFH; //字符型
                        iDataLen = 5; //物理数据的字符长度
                        sDataFormat = "SS000000.00"; //数据格式
                        iStorageType = CCFS_BCD; //十六进制码
                        iStorageOrder = CCSX_NX; //顺序存储
                        sPhysicalData = "" + (sDataContent.
                                              substring(0, iDataLen * 2));
                        sDataContent = sDataContent.substring(iDataLen * 2,
                                sDataContent.length());
                        DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
                                iDataLen, sDataFormat, iStorageType,
                                iStorageOrder);
                        DataItemInfo.SetFPhysicalData(sPhysicalData);
                        DataItemInfoList.add(DataItemInfo);

                        DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
                        sDataCaption = "EC3306"; //下发后表内的正向有功电能示值
                        iDataType = c_Float; //浮点型
                        iDataLen = 4; //物理数据的字符长度
                        sDataFormat = "000000.00"; //数据格式
                        iStorageType = CCFS_BCD; //BCD码
                        iStorageOrder = CCSX_NX; //逆序存储
                        sPhysicalData = sDataContent.substring(0, iDataLen * 2);
                        sDataContent = sDataContent.substring(iDataLen * 2,
                                sDataContent.length());
                        DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
                                iDataLen, sDataFormat, iStorageType,
                                iStorageOrder);
                        DataItemInfo.SetFPhysicalData(sPhysicalData);
                        DataItemInfoList.add(DataItemInfo);

                        sAlarmCode = "C" + ("" + iAlarmCode) + "0";
                        AlarmCodeList.add(sAlarmCode);
                    } else if (iAlarmCode == 34) { //ERC34：电能表报警电量参数下发电表成功记录
                        DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
                        sDataCaption = "EC3401"; //测量点号
                        iDataType = c_Float; //字符型
                        iDataLen = 2; //物理数据的字符长度
                        sDataFormat = "0000"; //数据格式
                        iStorageType = CCFS_HEX; //十六进制码
                        iStorageOrder = CCSX_SX; //顺序存储
                        sPhysicalData = "" +
                                        (Integer.parseInt(DataSwitch.
                                ReverseStringByByte(sDataContent.
                                substring(0, iDataLen * 2)), 16) & 4095);
                        sMeasuredPointNo = sPhysicalData;
                        sPhysicalData = DataSwitch.IntToHex(sPhysicalData,
                                "0000");
                        sDataContent = sDataContent.substring(iDataLen * 2,
                                sDataContent.length());
                        DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
                                iDataLen, sDataFormat, iStorageType,
                                iStorageOrder);
                        DataItemInfo.SetFPhysicalData(sPhysicalData);
                        DataItemInfoList.add(DataItemInfo);

                        DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
                        sDataCaption = "EC3402"; //下发后表内的一次报警电量：KWh
                        iDataType = c_Float; //浮点型
                        iDataLen = 4; //物理数据的字符长度
                        sDataFormat = "000000.00"; //数据格式
                        iStorageType = CCFS_BCD; //BCD码
                        iStorageOrder = CCSX_NX; //逆序存储
                        sPhysicalData = sDataContent.substring(0, iDataLen * 2);
                        sDataContent = sDataContent.substring(iDataLen * 2,
                                sDataContent.length());
                        DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
                                iDataLen, sDataFormat, iStorageType,
                                iStorageOrder);
                        DataItemInfo.SetFPhysicalData(sPhysicalData);
                        DataItemInfoList.add(DataItemInfo);

                        DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
                        sDataCaption = "EC3403"; //下发后表内的二次报警电量：KWh
                        iDataType = c_Float; //浮点型
                        iDataLen = 4; //物理数据的字符长度
                        sDataFormat = "000000.00"; //数据格式
                        iStorageType = CCFS_BCD; //BCD码
                        iStorageOrder = CCSX_NX; //逆序存储
                        sPhysicalData = sDataContent.substring(0, iDataLen * 2);
                        sDataContent = sDataContent.substring(iDataLen * 2,
                                sDataContent.length());
                        DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
                                iDataLen, sDataFormat, iStorageType,
                                iStorageOrder);
                        DataItemInfo.SetFPhysicalData(sPhysicalData);
                        DataItemInfoList.add(DataItemInfo);

                        DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
                        sDataCaption = "EC3404"; //下发后表内的授信额度电量：KWh
                        iDataType = c_Float; //浮点型
                        iDataLen = 4; //物理数据的字符长度
                        sDataFormat = "000000.00"; //数据格式
                        iStorageType = CCFS_BCD; //BCD码
                        iStorageOrder = CCSX_NX; //逆序存储
                        sPhysicalData = sDataContent.substring(0, iDataLen * 2);
                        sDataContent = sDataContent.substring(iDataLen * 2,
                                sDataContent.length());
                        DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
                                iDataLen, sDataFormat, iStorageType,
                                iStorageOrder);
                        DataItemInfo.SetFPhysicalData(sPhysicalData);
                        DataItemInfoList.add(DataItemInfo);

                        sAlarmCode = "C" + ("" + iAlarmCode) + "0";
                        AlarmCodeList.add(sAlarmCode);
                    } else if (iAlarmCode == 35) { //ERC35：电能表负荷限制参数下发电表成功记录
                        DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
                        sDataCaption = "EC3501"; //测量点号
                        iDataType = c_Float; //字符型
                        iDataLen = 2; //物理数据的字符长度
                        sDataFormat = "0000"; //数据格式
                        iStorageType = CCFS_HEX; //十六进制码
                        iStorageOrder = CCSX_SX; //顺序存储
                        sPhysicalData = "" +
                                        (Integer.parseInt(DataSwitch.
                                ReverseStringByByte(sDataContent.
                                substring(0, iDataLen * 2)), 16) & 4095);
                        sMeasuredPointNo = sPhysicalData;
                        sPhysicalData = DataSwitch.IntToHex(sPhysicalData,
                                "0000");
                        sDataContent = sDataContent.substring(iDataLen * 2,
                                sDataContent.length());
                        DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
                                iDataLen, sDataFormat, iStorageType,
                                iStorageOrder);
                        DataItemInfo.SetFPhysicalData(sPhysicalData);
                        DataItemInfoList.add(DataItemInfo);

                        DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
                        sDataCaption = "EC3502"; //下发后表内的负荷限制：W
                        iDataType = c_Float; //浮点型
                        iDataLen = 4; //物理数据的字符长度
                        sDataFormat = "000000.00"; //数据格式
                        iStorageType = CCFS_BCD; //BCD码
                        iStorageOrder = CCSX_NX; //逆序存储
                        sPhysicalData = sDataContent.substring(0, iDataLen * 2);
                        sDataContent = sDataContent.substring(iDataLen * 2,
                                sDataContent.length());
                        DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
                                iDataLen, sDataFormat, iStorageType,
                                iStorageOrder);
                        DataItemInfo.SetFPhysicalData(sPhysicalData);
                        DataItemInfoList.add(DataItemInfo);

                        sAlarmCode = "C" + ("" + iAlarmCode) + "0";
                        AlarmCodeList.add(sAlarmCode);
                    } else if (iAlarmCode == 36) { //ERC36：电能表囤积电量参数下发电表成功记录
                        DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
                        sDataCaption = "EC3601"; //测量点号
                        iDataType = c_Float; //字符型
                        iDataLen = 2; //物理数据的字符长度
                        sDataFormat = "0000"; //数据格式
                        iStorageType = CCFS_HEX; //十六进制码
                        iStorageOrder = CCSX_SX; //顺序存储
                        sPhysicalData = "" +
                                        (Integer.parseInt(DataSwitch.
                                ReverseStringByByte(sDataContent.
                                substring(0, iDataLen * 2)), 16) & 4095);
                        sMeasuredPointNo = sPhysicalData;
                        sPhysicalData = DataSwitch.IntToHex(sPhysicalData,
                                "0000");
                        sDataContent = sDataContent.substring(iDataLen * 2,
                                sDataContent.length());
                        DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
                                iDataLen, sDataFormat, iStorageType,
                                iStorageOrder);
                        DataItemInfo.SetFPhysicalData(sPhysicalData);
                        DataItemInfoList.add(DataItemInfo);

                        DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
                        sDataCaption = "EC3602"; //下发后表内的囤积电量：KWh
                        iDataType = c_Float; //浮点型
                        iDataLen = 4; //物理数据的字符长度
                        sDataFormat = "000000.00"; //数据格式
                        iStorageType = CCFS_BCD; //BCD码
                        iStorageOrder = CCSX_NX; //逆序存储
                        sPhysicalData = sDataContent.substring(0, iDataLen * 2);
                        sDataContent = sDataContent.substring(iDataLen * 2,
                                sDataContent.length());
                        DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
                                iDataLen, sDataFormat, iStorageType,
                                iStorageOrder);
                        DataItemInfo.SetFPhysicalData(sPhysicalData);
                        DataItemInfoList.add(DataItemInfo);

                        sAlarmCode = "C" + ("" + iAlarmCode) + "0";
                        AlarmCodeList.add(sAlarmCode);
                    } else if (iAlarmCode == 37) { //ERC37：电能表切合闸参数下发电表成功记录
                        DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
                        sDataCaption = "EC3701"; //测量点号
                        iDataType = c_Float; //字符型
                        iDataLen = 2; //物理数据的字符长度
                        sDataFormat = "0000"; //数据格式
                        iStorageType = CCFS_HEX; //十六进制码
                        iStorageOrder = CCSX_SX; //顺序存储
                        sPhysicalData = "" +
                                        (Integer.parseInt(DataSwitch.
                                ReverseStringByByte(sDataContent.
                                substring(0, iDataLen * 2)), 16) & 4095);
                        sMeasuredPointNo = sPhysicalData;
                        sPhysicalData = DataSwitch.IntToHex(sPhysicalData,
                                "0000");
                        sDataContent = sDataContent.substring(iDataLen * 2,
                                sDataContent.length());
                        DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
                                iDataLen, sDataFormat, iStorageType,
                                iStorageOrder);
                        DataItemInfo.SetFPhysicalData(sPhysicalData);
                        DataItemInfoList.add(DataItemInfo);

                        DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
                        sDataCaption = "EC3702"; //下发后表内的继电器状态
                        iDataType = c_Float; //字符型
                        iDataLen = 1; //物理数据的字符长度
                        sDataFormat = "00"; //数据格式
                        iStorageType = CCFS_HEX; //十六进制码
                        iStorageOrder = CCSX_SX; //顺序存储
                        sPhysicalData = DataSwitch.IntToHex(sPhysicalData,
                                "00");
                        sDataContent = sDataContent.substring(iDataLen * 2,
                                sDataContent.length());
                        DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
                                iDataLen, sDataFormat, iStorageType,
                                iStorageOrder);
                        DataItemInfo.SetFPhysicalData(sPhysicalData);
                        DataItemInfoList.add(DataItemInfo);

                        sAlarmCode = "C" + ("" + iAlarmCode) + "0";
                        AlarmCodeList.add(sAlarmCode);
                    } else if (iAlarmCode == 38) { //ERC38：电能表保电时限参数下发电表成功记录
                        DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
                        sDataCaption = "EC3801"; //测量点号
                        iDataType = c_Float; //字符型
                        iDataLen = 2; //物理数据的字符长度
                        sDataFormat = "0000"; //数据格式
                        iStorageType = CCFS_HEX; //十六进制码
                        iStorageOrder = CCSX_SX; //顺序存储
                        sPhysicalData = "" +
                                        (Integer.parseInt(DataSwitch.
                                ReverseStringByByte(sDataContent.
                                substring(0, iDataLen * 2)), 16) & 4095);
                        sMeasuredPointNo = sPhysicalData;
                        sPhysicalData = DataSwitch.IntToHex(sPhysicalData,
                                "0000");
                        sDataContent = sDataContent.substring(iDataLen * 2,
                                sDataContent.length());
                        DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
                                iDataLen, sDataFormat, iStorageType,
                                iStorageOrder);
                        DataItemInfo.SetFPhysicalData(sPhysicalData);
                        DataItemInfoList.add(DataItemInfo);

                        DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
                        sDataCaption = "EC3802"; //下发后表内的保电时限
                        iDataType = c_Float; //字符型
                        iDataLen = 1; //物理数据的字符长度
                        sDataFormat = "00"; //数据格式
                        iStorageType = CCFS_HEX; //十六进制码
                        iStorageOrder = CCSX_SX; //顺序存储
                        sPhysicalData = DataSwitch.IntToHex(sPhysicalData,
                                "00");
                        sDataContent = sDataContent.substring(iDataLen * 2,
                                sDataContent.length());
                        DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
                                iDataLen, sDataFormat, iStorageType,
                                iStorageOrder);
                        DataItemInfo.SetFPhysicalData(sPhysicalData);
                        DataItemInfoList.add(DataItemInfo);

                        sAlarmCode = "C" + ("" + iAlarmCode) + "0";
                        AlarmCodeList.add(sAlarmCode);
                    } else if (iAlarmCode == 39) { //ERC39：电能表密码参数下发电表修改成功记录
                        DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
                        sDataCaption = "EC3901"; //测量点号
                        iDataType = c_Float; //字符型
                        iDataLen = 2; //物理数据的字符长度
                        sDataFormat = "0000"; //数据格式
                        iStorageType = CCFS_HEX; //十六进制码
                        iStorageOrder = CCSX_SX; //顺序存储
                        sPhysicalData = "" +
                                        (Integer.parseInt(DataSwitch.
                                ReverseStringByByte(sDataContent.
                                substring(0, iDataLen * 2)), 16) & 4095);
                        sMeasuredPointNo = sPhysicalData;
                        sPhysicalData = DataSwitch.IntToHex(sPhysicalData,
                                "0000");
                        sDataContent = sDataContent.substring(iDataLen * 2,
                                sDataContent.length());
                        DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
                                iDataLen, sDataFormat, iStorageType,
                                iStorageOrder);
                        DataItemInfo.SetFPhysicalData(sPhysicalData);
                        DataItemInfoList.add(DataItemInfo);

                        DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
                        sDataCaption = "EC3902"; //下发后表内的新密码权限及密码
                        iDataType = c_Float; //字符型
                        iDataLen = 4; //物理数据的字符长度
                        sDataFormat = "00000000"; //数据格式
                        iStorageType = CCFS_HEX; //十六进制码
                        iStorageOrder = CCSX_SX; //顺序存储
                        sPhysicalData = DataSwitch.IntToHex(sPhysicalData,
                                "00000000");
                        sDataContent = sDataContent.substring(iDataLen * 2,
                                sDataContent.length());
                        DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
                                iDataLen, sDataFormat, iStorageType,
                                iStorageOrder);
                        DataItemInfo.SetFPhysicalData(sPhysicalData);
                        DataItemInfoList.add(DataItemInfo);

                        sAlarmCode = "C" + ("" + iAlarmCode) + "0";
                        AlarmCodeList.add(sAlarmCode);
                    } else if (iAlarmCode == 40) { //ERC40：电能表购电量参数下发电表失败记录
                        DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
                        sDataCaption = "EC4001"; //测量点号
                        iDataType = c_Float; //字符型
                        iDataLen = 2; //物理数据的字符长度
                        sDataFormat = "0000"; //数据格式
                        iStorageType = CCFS_HEX; //十六进制码
                        iStorageOrder = CCSX_SX; //顺序存储
                        sPhysicalData = "" +
                                        (Integer.parseInt(DataSwitch.
                                ReverseStringByByte(sDataContent.
                                substring(0, iDataLen * 2)), 16) & 4095);
                        sMeasuredPointNo = sPhysicalData;
                        sPhysicalData = DataSwitch.IntToHex(sPhysicalData,
                                "0000");
                        sDataContent = sDataContent.substring(iDataLen * 2,
                                sDataContent.length());
                        DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
                                iDataLen, sDataFormat, iStorageType,
                                iStorageOrder);
                        DataItemInfo.SetFPhysicalData(sPhysicalData);
                        DataItemInfoList.add(DataItemInfo);

                        DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
                        sDataCaption = "EC4002"; //购电单号
                        iDataType = c_Float; //字符型
                        iDataLen = 2; //物理数据的字符长度
                        sDataFormat = "0000"; //数据格式
                        iStorageType = CCFS_HEX; //十六进制码
                        iStorageOrder = CCSX_SX; //顺序存储
                        sPhysicalData = "" +
                                        sDataContent.substring(0, iDataLen * 2);
                        sDataContent = sDataContent.substring(iDataLen * 2,
                                sDataContent.length());
                        DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
                                iDataLen, sDataFormat, iStorageType,
                                iStorageOrder);
                        DataItemInfo.SetFPhysicalData(sPhysicalData);
                        DataItemInfoList.add(DataItemInfo);

                        DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
                        sDataCaption = "EC4003"; //失败原因代码
                        iDataType = c_Float; //字符型
                        iDataLen = 1; //物理数据的字符长度
                        sDataFormat = "0000"; //数据格式
                        iStorageType = CCFS_HEX; //十六进制码
                        iStorageOrder = CCSX_SX; //顺序存储
                        sPhysicalData = "" +
                                        sDataContent.substring(0, iDataLen * 2);
                        sDataContent = sDataContent.substring(iDataLen * 2,
                                sDataContent.length());
                        DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
                                iDataLen, sDataFormat, iStorageType,
                                iStorageOrder);
                        DataItemInfo.SetFPhysicalData(sPhysicalData);
                        DataItemInfoList.add(DataItemInfo);
                        sAlarmCode = "C" + ("" + iAlarmCode) + "0";
                        AlarmCodeList.add(sAlarmCode);
                    } else if (iAlarmCode == 41 || iAlarmCode == 42 || //ERC41：电能表报警电量参数下发电表失败记录ERC42：电能表负荷限制参数下发电表失败记录
                               iAlarmCode == 43 || iAlarmCode == 44 || //ERC43：电能表囤积电量参数下发电表失败记录ERC44：电能表切合闸参数下发电表失败记录
                               iAlarmCode == 45 || iAlarmCode == 46) { //ERC45：电能表保电时限参数下发电表失败记录ERC46：电能表密码参数修改失败记录
                        DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
                        sDataCaption = "EC" + ("" + iAlarmCode) + "01"; //测量点号
                        iDataType = c_Float; //字符型
                        iDataLen = 2; //物理数据的字符长度
                        sDataFormat = "0000"; //数据格式
                        iStorageType = CCFS_HEX; //十六进制码
                        iStorageOrder = CCSX_SX; //顺序存储
                        sPhysicalData = "" +
                                        (Integer.parseInt(DataSwitch.
                                ReverseStringByByte(sDataContent.
                                substring(0, iDataLen * 2)), 16) & 4095);
                        sMeasuredPointNo = sPhysicalData;
                        sPhysicalData = DataSwitch.IntToHex(sPhysicalData,
                                "0000");
                        sDataContent = sDataContent.substring(iDataLen * 2,
                                sDataContent.length());
                        DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
                                iDataLen, sDataFormat, iStorageType,
                                iStorageOrder);
                        DataItemInfo.SetFPhysicalData(sPhysicalData);
                        DataItemInfoList.add(DataItemInfo);

                        DataItemInfo = new SFE_DataItemInfo(); //ArrayList里的元素每次ADD都要初始化
                        sDataCaption = "EC" + ("" + iAlarmCode) + "02"; //失败原因代码
                        iDataType = c_Float; //字符型
                        iDataLen = 1; //物理数据的字符长度
                        sDataFormat = "00"; //数据格式
                        iStorageType = CCFS_HEX; //十六进制码
                        iStorageOrder = CCSX_SX; //顺序存储
                        sPhysicalData = "" +
                                        sDataContent.substring(0, iDataLen * 2);
                        sDataContent = sDataContent.substring(iDataLen * 2,
                                sDataContent.length());
                        DataItemInfo.DataItemInfoAdd(sDataCaption, iDataType,
                                iDataLen, sDataFormat, iStorageType,
                                iStorageOrder);
                        DataItemInfo.SetFPhysicalData(sPhysicalData);
                        DataItemInfoList.add(DataItemInfo);
                        sAlarmCode = "C" + ("" + iAlarmCode) + "0";
                        AlarmCodeList.add(sAlarmCode);
                    }
                    AlarmDataTemp = new SFE_AlarmData();
                    for (int j = 0; j < DataItemInfoList.size(); j++) { //物理数据转换成逻辑数据
                        ((SFE_DataItemInfo) (DataItemInfoList.get(j))).
                                PhysicalDataToLogicData(); //物理数据转换成逻辑数据
                        sDataCaption = ((SFE_DataItemInfo) (DataItemInfoList.
                                get(j))).GetFDataCaption(); //数据项标识
                        sLogicData = ((SFE_DataItemInfo) (DataItemInfoList.get(
                                j))).GetFLogicData(); //逻辑数据
                        iDataLen = sLogicData.length(); //逻辑数据长度
                        AlarmDataTemp.DataItemAdd(sDataCaption, iDataLen,
                                                  sLogicData);
                    }
                    for (int j = 0; j < AlarmCodeList.size(); j++) { //一个告警分解成多个告警
                        AlarmData = new SFE_AlarmData();
                        AlarmData.SetAlarmCode(AlarmCodeList.get(j).toString());
                        AlarmData.SetAlarmType(Fn);
                        AlarmData.SetAlarmDateTime(sAlarmDateTime);
                        AlarmData.SetMeasuredPointNo(Integer.parseInt(
                                sMeasuredPointNo));
                        AlarmData.SetMeasuredPointType(iMeasuredPointType);
                        AlarmData.SetDataItemCount(AlarmDataTemp.
                                GetDataItemCount());
                        AlarmData.DataItemList = AlarmDataTemp.DataItemList;
                        AlarmDataList.add(AlarmData);
                    }
                    DataItemInfoList.clear();
                    AlarmCodeList.clear();
                }
            } catch (Exception e) {
                Glu_ConstDefine.Log1.WriteLog(
                        "Func:FrameDataAreaExplainGuYuan__ExplainAlarmParameterGuYuan();Error:" +
                        e.toString());
            }
        } finally {
        }
        return AlarmDataList;
    }

    //----------------------------固原集抄数据区解析所用方法-----------------------------
    public String GetTaskDateTimeInfo(String sDateTimeLabel, int DateType) { //返回值：开始时间(YYYYMMDDHHNNSS)+时间间隔+数据点数
        String sResult = "";
        try {
            try {
                String sDateTime = "", sNowDateTime = "", sDataDensity = "",
                        sDataCount = "";
                int iDataDensity = 0; //时间间隔、数据点数
                if (DateType == 21) { //小时冻结数据时标
                    sDateTime = Integer.toString(Integer.parseInt(
                            sDateTimeLabel.substring(0, 1), 16) & 3) +
                                sDateTimeLabel.substring(1, 2); //小时
                    iDataDensity = (Integer.parseInt(sDateTimeLabel.substring(0,
                            1), 16) & 12) / 4;
                    //获取当前时间
                    Calendar cLogTime = Calendar.getInstance();
                    SimpleDateFormat formatter = new SimpleDateFormat(
                            "yyyyMMddHHmmss");
                    sNowDateTime = formatter.format(cLogTime.getTime());
                    //判断上送数据小时大于当前小时则为上一天数据
                    if (Integer.parseInt(sDateTime) >
                        Integer.parseInt(sNowDateTime.substring(8, 10))) {
                        sNowDateTime = DataSwitch.IncreaseDateTime(sNowDateTime,
                                -1, 4);
                    }
                    if (iDataDensity == 2) { //小时冻结密度为30分钟，则冻结时间为30，0
                        sDateTime = sNowDateTime.substring(0, 8) + sDateTime +
                                    "3000"; //取系统时间填充年月日分秒,但是无效的,只有时分是有效数据
                    } else if (iDataDensity == 3) { //小时冻结密度为60分钟，则冻结时间为0
                        sDateTime = sNowDateTime.substring(0, 8) + sDateTime +
                                    "0000"; //取系统时间填充年月日分秒,但是无效的,只有时分是有效数据
                        sDateTime = DataSwitch.IncreaseDateTime(sDateTime,
                                1, 3);
                    } else { //小时冻结默认密度为15分钟，则冻结时间为15，30，45，0
                        sDateTime = sNowDateTime.substring(0, 8) + sDateTime +
                                    "1500"; //取系统时间填充年月日分秒,但是无效的,只有时分是有效数据
                    }

                } else if (DateType == 22) { //曲线数据时标
                    sDateTime = "20" +
                                DataSwitch.ReverseStringByByte(sDateTimeLabel.
                            substring(0, 10)) + "00"; //年月日时分
                    iDataDensity = Integer.parseInt(sDateTimeLabel.substring(10,
                            12), 16); //数据密度
                    sDataCount = DataSwitch.HexToInt(sDateTimeLabel.substring(
                            12, 14), "00"); //数据点数
                    //当数据点数为个位数时需要补足为1个字节
                    //sDataCount= DataSwitch.StrStuff("0",2,sDataCount,10);
                } else if (DateType == 23) { //日数据冻结时标
                    sDateTime = "20" +
                                DataSwitch.ReverseStringByByte(sDateTimeLabel.
                            substring(0, 6)) + "000000"; //年月日
                } else if (DateType == 24) { //月数据冻结时标
                    sDateTime = "20" +
                                DataSwitch.ReverseStringByByte(sDateTimeLabel.
                            substring(0, 4)) + "01000000"; //年月
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
                Glu_ConstDefine.Log1.WriteLog(
                        "Func:FrameDataAreaExplainGuYuan__GetTaskDateTimeInfo();Error:" +
                        e.toString());
            }
        } finally {
        }
        return sResult;
    }

    public ArrayList<String> ExplainAlarmCodeGuYuan(int AlarmType, String AlarmSign,
                                            String AlarmCode,
                                            int AlarmMaxCount) { //异常类型,异常事件标志,异常代码,当前异常最大分解数目
        ArrayList <String>AlarmCodeList = new ArrayList<String>();
        try {
            try {
                if (!AlarmSign.equals("")) {
                    int iAlarmSign = Integer.parseInt(AlarmSign.substring(0, 2),
                            16);
                    int iPower2 = 0;
                    AlarmCode = DataSwitch.StrStuff("0", 2, AlarmCode, 10); //补足2位
                    String sAlarmCode = "";
                    if (AlarmType == 1 || AlarmType == 2 || AlarmType == 3) { //变更标志:1发生;2恢复;3状态变位
                        for (int i = 0; i < AlarmMaxCount; i++) {
                            iPower2 = DataSwitch.Power2(i);
                            if ((iAlarmSign & iPower2) == iPower2) { //取单个字节每位的值
                                if (AlarmType == 1) {
                                    sAlarmCode = "C" + AlarmCode +
                                                 Integer.toString(i + 1); //异常发生
                                } else if (AlarmType == 2) {
                                    sAlarmCode = "R" + AlarmCode +
                                                 Integer.toString(i + 1); //异常恢复
                                } else { //状态变位
                                    int iTemp = Integer.parseInt(AlarmSign.
                                            substring(2, 4), 16); //变位后状态
                                    if ((iTemp & iPower2) == iPower2) { //取单个字节每位的值
                                        sAlarmCode = "C" + AlarmCode +
                                                Integer.toString(i + 1); //都属于变位异常,由于变位后状态不同而产生不同的异常编码，以便保存时信息不丢失
                                    } else {
                                        sAlarmCode = "R" + AlarmCode +
                                                Integer.toString(i + 1); //都属于变位异常,由于变位后状态不同而产生不同的异常编码，以便保存时信息不丢失
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
                Glu_ConstDefine.Log1.WriteLog(
                        "Func:FrameDataAreaExplainGuYuan__ExplainAlarmCodeGuYuan();Error:" +
                        e.toString());
            }
        } finally {            
        }
        return AlarmCodeList;
    }
}

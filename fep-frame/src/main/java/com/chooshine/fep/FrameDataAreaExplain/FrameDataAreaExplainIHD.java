package com.chooshine.fep.FrameDataAreaExplain;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import com.chooshine.fep.ConstAndTypeDefine.*;

public class FrameDataAreaExplainIHD {
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

    SPE_CommandInfoList TermialIHDCommandInfoList = new SPE_CommandInfoList(); //终端IHD命令信息队列
    
    FrameDataAreaExplainGluMethod GluMethodIHD = new
            FrameDataAreaExplainGluMethod(); //数据区解析公用类
    
    public FrameDataAreaExplainIHD(String path) {
        try {
            InitialList(path,null);
        } catch (Exception e) {
            Glu_ConstDefine.Log1.WriteLog(e.toString());
        }
    }
    public FrameDataAreaExplainIHD(URI ur) {
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

                if (Glu_ConstDefine.TerminalIHDSupport) {
                    Glu_DataAccess DataAccess ;
                    if (ur == null) {
						DataAccess = new Glu_DataAccess(path);
					} else {
						DataAccess = new Glu_DataAccess(ur);
					}
                    DataAccess.LogIn(0);
                    TermialIHDCommandInfoList = DataAccess.
                            GetCommandInfoList(101); //初始化终端IHD命令信息队列                    
                    DataAccess.LogOut(0);
                }

                /*cLogTime=Calendar.getInstance();
                         JSSJ=formatter.format(cLogTime.getTime());
                         System.out.println("结束时间："+JSSJ);*/
            } catch (Exception e) {
                Glu_ConstDefine.Log1.WriteLog(
                        "Func:FrameDataAreaExplainIHD__InitialList();Error:" +
                        e.toString());
            }
        } finally {
        }
        return Result;
    }

    public SFE_DataListInfo ExplainNormalDataAreaIHD( //普通数据区解释
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
                    CommandInfoList = TermialIHDCommandInfoList;
                    
                    if (ControlCode.equals("0A")) { //参数查询
                        ControlCode = "04";
                    } 
                    while (FrameDataArea.length() >= 8 && IsBreak == false) { //无数据体时至少还4个字节(数据单元标识)
                        sDA = FrameDataArea.substring(0, 4);
                        sDT = FrameDataArea.substring(4, 8);
                        MeasuredSignList = GluMethodIHD.
                                           ExplainMeasuredPointList(sDA,
                                TermialProtocolType); //解释信息点得到测量点列表
                        CommandList = GluMethodIHD.ExplainCommandList(sDT,
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
                                NormalDataTemp = GluMethodIHD.
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
                                for (int k = 0;
                                             k < NormalDataTemp.DataItemList.
                                             size();
                                             k++) { //把解释后的数据保存在中间对象里
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
                        "Func:FrameDataAreaExplainIHD__ExplainNormalDataAreaIHD();Error:" +
                        e.toString());
            }
        } finally {                      
        }
        DataListInfo.DataType = 10;
        DataListInfo.DataList = NormalDataList;  
        return DataListInfo;
    }

    public SFE_DataListInfo ExplainSetResultDataAreaIHD( //国网设置返回区解释
            String FrameDataArea, int TermialProtocolType, String ControlCode) { //帧数据区,终端规约,控制码
        SFE_DataListInfo DataListInfo = new SFE_DataListInfo();
        ArrayList <SFE_SetResultData>SetResultDataList = new ArrayList<SFE_SetResultData>();
        try {
            try {
                SFE_SetResultData SetResultData = new SFE_SetResultData();
                ArrayList CommandList = new ArrayList();
                int[] MeasuredSignList;
                String sDataCaption = "", sDA = "", sDT = "";
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
                        MeasuredSignList = GluMethodIHD.
                                           ExplainMeasuredPointList(sDA,
                                TermialProtocolType); //解释信息点得到测量点列表
                        CommandList = GluMethodIHD.ExplainCommandList(sDT,
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
                        "Func:FrameDataAreaExplainIHD__ExplainSetResultDataAreaIHD();Error:" +
                        e.toString());
            }
        } finally {
        }
        DataListInfo.ExplainResult = 0;
        DataListInfo.DataType = 40;
        DataListInfo.DataList = SetResultDataList;
        return DataListInfo;
    }   

    public SFE_DataListInfo ExplainHistoryDataAreaIHD( //历史数据区解释(二类历史数据)
            String FrameDataArea, int TermialProtocolType, String ControlCode,
            int FrameType) { //帧数据区,终端规约,功能码,帧类型(主动上送还是召测返回)
        SFE_DataListInfo DataListInfo = new SFE_DataListInfo();
        ArrayList <SFE_HistoryData>HistoryDataList = new ArrayList<SFE_HistoryData>();
        try {
            try {
                SFE_NormalDataTemp NormalDataTemp = new SFE_NormalDataTemp();
                SPE_CommandInfoList CommandInfoList = new SPE_CommandInfoList();
                SFE_HistoryData[] HistoryDataListTemp = new SFE_HistoryData[
                        MAX_LENGTH_FHCOUNT]; //数据点数最大值=24
                for (int i = 0; i < MAX_LENGTH_FHCOUNT; i++) {
                    HistoryDataListTemp[i] = new SFE_HistoryData();
                }
                ArrayList CommandList = new ArrayList();
                ArrayList <String>CommandListTemp = new ArrayList<String>();
                int[] MeasuredSignList;
                String sDataCaption = "", sDA = "", sDT = "",
                        sTaskDateTimeInfo = "", sTaskDateTime = "";
                int iFn = 0, iDataType = 0, iDataDensity = 0, iDataCount = 0,
                        iMeasuredType = 10;
                
                CommandInfoList = TermialIHDCommandInfoList;
                               
                while (FrameDataArea.length() >= 8) { //无数据体时至少还4个字节(数据单元标识)
                    sDA = FrameDataArea.substring(0, 4);
                    sDT = FrameDataArea.substring(4, 8);
                    iDataType = GluMethodIHD.GetIHDDataType(sDT,
                            ControlCode); //得到数据类型23日冻结数据;24月冻结数据;26周冻结数据;27年冻结数据
                    FrameDataArea = FrameDataArea.substring(8,
                                FrameDataArea.length()); //消去数据单元标识
                   
                    MeasuredSignList = GluMethodIHD.
                                       ExplainMeasuredPointList(sDA,
                            TermialProtocolType); //解释信息点得到测量点列表
                    CommandList = GluMethodIHD.ExplainCommandList(sDT,
                            ControlCode); //解释信息类得到命令列表
                    if (CommandList.get(0).toString().equals("000000")) { //无效数据
                        continue;
                    }
                    iFn = Integer.parseInt(CommandList.get(0).toString().
                                           substring(1, 4)); //用第一个命令的Fn来判断全部命令的数据类型
                    
                    for (int i = 0; i < CommandList.size(); i++) { //把解释后命令列表保存起来,最后再匹配数据库保存的任务信息来确认任务号
                        CommandListTemp.add(CommandList.get(i).toString());
                    }
                    //解释任务数据
                    Calendar cNowTime = Calendar.getInstance();
                    SimpleDateFormat formatter = new SimpleDateFormat(
                    							"yyyyMMddHHmmss");
                    sTaskDateTime = formatter.format(cNowTime.getTime());
                    int iWeek = cNowTime.get(Calendar.DAY_OF_WEEK);
                    for (int i = 0; i < MeasuredSignList.length; i++) { //测量点个数
                        for (int j = 0; j < CommandList.size(); j++) { //命令个数
                            //每个命令都包含数据时标，假设上行任务数据同一单元标识里命令都是同一类数据，且时标都是一样的，不然是无法按任务结构解释的                           
                        	while (FrameDataArea.length() > 0) { //处理数据内容
                                HistoryDataListTemp[iDataCount].SetTaskNo(0);
                                HistoryDataListTemp[iDataCount].SetMeasuredPointNo(
                                        MeasuredSignList[i]);
                                HistoryDataListTemp[iDataCount].SetMeasuredPointType(
                                        iMeasuredType);
                                //******************处理数据时间******************                                
                                if (iDataType == 21) {//日数据
                                	if (iDataCount == 0){
                                		sTaskDateTime = sTaskDateTime.substring(0, 8) + "000000";
                                	}
                                	else{
                                		sTaskDateTime = DataSwitch.IncreaseDateTime(
                                                sTaskDateTime,
                                                1, ////统一曲线的起始时间就是每隔小时的0分作为开始
                                                3);//累加分、小时、日、月,相应IncreaseType：2,3,4,5
                                	}                                	
                                    HistoryDataListTemp[iDataCount].SetTaskDateTime(sTaskDateTime);
                                } else if (iDataType == 23) {//月数据
                                	if (iDataCount == 0){
                                		sTaskDateTime = sTaskDateTime.substring(0, 6) + "01000000";
                                	}
                                	else{
                                		sTaskDateTime = DataSwitch.IncreaseDateTime(
                                                sTaskDateTime,
                                                1, ////统一曲线的起始时间就是每隔小时的0分作为开始
                                                4);//累加分、小时、日、月,相应IncreaseType：2,3,4,5
                                	}    
                                    HistoryDataListTemp[iDataCount].SetTaskDateTime(sTaskDateTime);
                                } else if (iDataType == 26) {//周冻结数据                                	
                                	if (iDataCount == 0){
                                		iWeek = iWeek - 2;
                                    	if (iWeek == -1){
                                    		iWeek = 6;
                                    	}
                                    	cNowTime.add(Calendar.DATE, (-1)*iWeek);
                                    	sTaskDateTime = formatter.format(cNowTime.getTime());
                                		sTaskDateTime = sTaskDateTime.substring(0, 8) + "000000";
                                	}
                                	else{
                                		sTaskDateTime = DataSwitch.IncreaseDateTime(
                                                sTaskDateTime,
                                                1, ////统一曲线的起始时间就是每隔小时的0分作为开始
                                                4);//累加分、小时、日、月,相应IncreaseType：2,3,4,5
                                	}    
                                    HistoryDataListTemp[iDataCount].SetTaskDateTime(sTaskDateTime);
                                } else if (iDataType == 24) {//年数据
                                	if (iDataCount == 0){
                                		sTaskDateTime = sTaskDateTime.substring(0, 4) + "0101000000";
                                	}
                                	else{
                                		sTaskDateTime = DataSwitch.IncreaseDateTime(
                                                sTaskDateTime,
                                                1, ////统一曲线的起始时间就是每隔小时的0分作为开始
                                                5);//累加分、小时、日、月,相应IncreaseType：2,3,4,5
                                	}    
                                    HistoryDataListTemp[iDataCount].SetTaskDateTime(sTaskDateTime);
                                }
                                sDataCaption = CommandList.get(j).toString();
                                NormalDataTemp = GluMethodIHD.
                                                 CommandSearchAndExplain(
                                        FrameDataArea, sDataCaption,
                                        CommandInfoList, TermialProtocolType,
                                        1, iFn);
                                FrameDataArea = FrameDataArea.substring(
                                        NormalDataTemp.GetCommandLen(),
                                        FrameDataArea.length());
                                for (int k = 0;
                                             k < NormalDataTemp.DataItemList.
                                             size();
                                             k++) { //把解释后的数据保存在中间对象里
                                    HistoryDataListTemp[iDataCount].DataItemList.add(
                                            NormalDataTemp.DataItemList.get(k));
                                    HistoryDataListTemp[iDataCount].DataItemCountAdd();
                                }
                                iDataCount = iDataCount + 1;
                            }
                        }
                    }
                    MeasuredSignList = null; //清空数组内容
                    CommandList.clear(); //清空队列内容
                }
                for (int i = 0; i < iDataCount; i++) {
                    HistoryDataList.add(HistoryDataListTemp[i]);
                }
            } catch (Exception e) {
                Glu_ConstDefine.Log1.WriteLog(
                        "Func:FrameDataAreaExplainIHD__ExplainHistoryDataAreaIHD();Error:" +
                        e.toString());
            }
        } finally {
        }
        DataListInfo.DataType = 20;
        DataListInfo.DataList = HistoryDataList;
        return DataListInfo;
    }
   

}

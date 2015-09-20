package com.chooshine.fep.FrameDataAreaExplain;

import java.net.URI;
import java.util.ArrayList;
import com.chooshine.fep.ConstAndTypeDefine.*;

public class FrameDataAreaExplainHeXing {//海兴集中器规约数据区解帧类
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

    SPE_CommandInfoList TermialHeXingCommandInfoList = new SPE_CommandInfoList(); //终端海兴命令信息队列
    SPE_TaskInfoList HeXingTaskInfoList = new SPE_TaskInfoList(); //国网任务信息队列

    FrameDataAreaExplainGluMethod GluMethodHeXing = new FrameDataAreaExplainGluMethod(); //数据区解析公用类
    
    public FrameDataAreaExplainHeXing(String path) {
        try {
            InitialList(path,null);
        } catch (Exception e) {
        	Glu_ConstDefine.Log1.WriteLog(e.toString());
        }
    }
    public FrameDataAreaExplainHeXing(URI ur) {
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
                if (Glu_ConstDefine.TerminalHeXingSupport) {
                    Glu_DataAccess DataAccess ;
                    if (ur == null) {
						DataAccess = new Glu_DataAccess(path);
					} else {
						DataAccess = new Glu_DataAccess(ur);
					}
                    DataAccess.LogIn(0);
                    TermialHeXingCommandInfoList = DataAccess.
                            GetCommandInfoList(200); //初始化终端海兴命令信息队列
                    HeXingTaskInfoList = DataAccess.GetTaskInfoList(200); //初始化任务信息队列
                    DataAccess.LogOut(0);
                }                
            } catch (Exception e) {
                Glu_ConstDefine.Log1.WriteLog(
                        "Func:FrameDataAreaExplainHeXing__InitialList();Error:" +
                        e.toString());
            }
        } finally {
        }
        return Result;
    }
    public SFE_DataListInfo ExplainSetResultDataAreaHeXing( //设置否认返回解释
            String FrameDataArea, int TermialProtocolType, String FunctionCode,String ControlCode) { //帧数据区,终端规约,控制码
        SFE_DataListInfo DataListInfo = new SFE_DataListInfo();
        ArrayList <SFE_NormalData>NormalDataList = new ArrayList<SFE_NormalData>();
        ArrayList <SFE_SetResultData>SetResultDataList = new ArrayList<SFE_SetResultData>();
        int iExplainResult=0;
        try {
            try {
            	SFE_SetResultData SetResultData = new SFE_SetResultData();
            	
            	SFE_NormalData NormalData = new SFE_NormalData();
                SPE_CommandInfoList CommandInfoList = new SPE_CommandInfoList();
                boolean bIsSearch = false;
                String sDataCaption = "", sCommand="", sMeasureAdd="";
                int iResult=0;
                CommandInfoList = TermialHeXingCommandInfoList;
                sDataCaption = "HX" + FrameDataArea.substring(0, 4);
                int iCommandCount=CommandInfoList.GetFCommandCount();
                for (int j=0;j<iCommandCount;j++){
                	sCommand=((SFE_CommandInfo)(CommandInfoList.CommandInfoList.get(j))).GetFProtocolCommand();
                	if (sDataCaption.equals(sCommand)) {//查找是否存在当前命令
                		bIsSearch=true;
                		break;
                	}
                }                
                if (bIsSearch) {
                	iExplainResult = 0;
                	sDataCaption = FrameDataArea.substring(0, 4);//数据项标识   
                	sDataCaption = "HX" + sDataCaption;   
                	FrameDataArea = FrameDataArea.substring(4, FrameDataArea.length());
                    SetResultData.SetMeasuredPointNo(0);
                    SetResultData.SetMeasuredPointType(10);
                    if ((sDataCaption.equals("HX1011") || sDataCaption.equals("HX1012") || sDataCaption.equals("HX1013")
                   	  || sDataCaption.equals("HX1014") || sDataCaption.equals("HX1041") || sDataCaption.equals("HX2001")
                   	  || sDataCaption.equals("HX2041") || sDataCaption.equals("HX2002") || sDataCaption.equals("HX2042")
                   	  || sDataCaption.equals("HX2040") || sDataCaption.equals("HX2052")
                   	  || (sDataCaption.equals("HX1051") && !ControlCode.substring(1,2).equals("1"))) 
                   	  && (FrameDataArea.equals(""))){//设置成功
                    	SetResultData.SetMeasuredAdd("");
                        SetResultData.DataItemAdd(sDataCaption,0);
                    }else if (sDataCaption.equals("HX1051") && ControlCode.substring(1,2).equals("1")){
                    	SetResultData.SetMeasuredAdd("");
                        SetResultData.DataItemAdd(sDataCaption,-255);
                    }else if (sDataCaption.equals("HX4001")){
                    	sMeasureAdd = FrameDataArea.substring(4,16);
                    	iResult = Integer.parseInt(FrameDataArea.substring(16,FrameDataArea.length()),16);
                    	SetResultData.SetMeasuredAdd(sMeasureAdd);
                        SetResultData.DataItemAdd(sDataCaption,iResult);
                    }else if (sDataCaption.equals("HX1011") && FrameDataArea.length() >= 10){
                    	while (FrameDataArea.length() >= 10) {
                    		sMeasureAdd = FrameDataArea.substring(0,8);
                        	iResult = (-1) * Integer.parseInt(FrameDataArea.substring(8,10),16);
                        	FrameDataArea = FrameDataArea.substring(10,FrameDataArea.length());
                        	SetResultData.SetMeasuredAdd(sMeasureAdd);
                            SetResultData.DataItemAdd(sDataCaption,iResult);
                    	}
                    }else if ((sDataCaption.equals("HX1013") || sDataCaption.equals("HX1014")
                    	    || sDataCaption.equals("HX2001") || sDataCaption.equals("HX2041")
                    	    || sDataCaption.equals("HX2002") || sDataCaption.equals("HX2042")
                    	    || sDataCaption.equals("HX2003") || sDataCaption.equals("HX2043"))
                    	    &&(FrameDataArea.length() >= 10)){
                    	while (FrameDataArea.length() >= 10) {
                    		sMeasureAdd = FrameDataArea.substring(0,12);
                        	iResult = (-1) * Integer.parseInt(FrameDataArea.substring(12,14),16);
                        	FrameDataArea = FrameDataArea.substring(14,FrameDataArea.length());
                        	SetResultData.SetMeasuredAdd(sMeasureAdd);
                            SetResultData.DataItemAdd(sDataCaption,iResult);
                    	}
                    }else if (sDataCaption.equals("HX1015")){
                    	iResult = -1;
                    	SetResultData.SetMeasuredAdd(sMeasureAdd);
                        SetResultData.DataItemAdd(sDataCaption,iResult);
                    }else {
                    	if ((sDataCaption.equals("HX2020")||sDataCaption.equals("HX204B")
                    			|| sDataCaption.equals("HX2021")||sDataCaption.equals("HX204C")
                    			|| sDataCaption.equals("HX2019")||sDataCaption.equals("HX204A")
                    			|| sDataCaption.equals("HX202A")||sDataCaption.equals("HX202B"))
                    	  && ("".equals(FrameDataArea))){
                    		iResult = -255;
                    	}else {
                    		iResult = (-1) * Integer.parseInt(FrameDataArea.substring(0,2),16);
                    	}                    	
                    	SetResultData.SetMeasuredAdd(sMeasureAdd);
                        SetResultData.DataItemAdd(sDataCaption,iResult);
                    }
                    
                    SetResultDataList.add(SetResultData);  
                }
                else {
                	iExplainResult = 50010;//数据项在规约里未支持
                	NormalData.SetMeasuredPointNo(0);
                    NormalData.SetMeasuredPointType(10);
                    NormalData.DataItemAdd("",FrameDataArea.substring(0,FrameDataArea.length()));
                    NormalDataList.add(NormalData); 
                }

            } catch (Exception e) {
                Glu_ConstDefine.Log1.WriteLog(
                        "Func:FrameDataAreaExplainHeXing__ExplainSetResultDataAreaQuanGuo();Error:" +
                        e.toString());
            }
        } finally {
        }
        DataListInfo.ExplainResult = iExplainResult;
        DataListInfo.DataType = 40;
        DataListInfo.DataList = SetResultDataList;
        return DataListInfo;
    }
    
    public SFE_DataListInfo ExplainNormalDataAreaHeXing( //普通数据区解释
            String FrameDataArea, int TermialProtocolType, String ControlCode) { //帧数据区,终端规约,功能码
        SFE_DataListInfo DataListInfo = new SFE_DataListInfo();
        ArrayList <SFE_NormalData>NormalDataList = new ArrayList<SFE_NormalData>();
        int iExplainResult=0;
        try {
            try {
            	SFE_NormalData NormalData = new SFE_NormalData();
                SPE_CommandInfoList CommandInfoList = new SPE_CommandInfoList();
                boolean IsBreak = false, bIsSearch = false;
                String sDataCaption = "", sCommand="";
                int iCommandLen=0;
                int iCommandCount=0;
                CommandInfoList = TermialHeXingCommandInfoList;
                SFE_NormalData[] NormalDataListTemp = new SFE_NormalData[1];
                NormalDataListTemp[0] = new SFE_NormalData();
                NormalDataListTemp[0].SetMeasuredPointNo(0);
                NormalDataListTemp[0].SetMeasuredPointType(10);
                while (FrameDataArea.length() >= 4 && IsBreak == false) { //无数据体时至少还4个字节(数据单元标识)
                	sDataCaption = FrameDataArea.substring(0, 4);//数据项标识  
                	sDataCaption = "HX" + sDataCaption;
                	FrameDataArea = FrameDataArea.substring(4,FrameDataArea.length());
                	iCommandCount = CommandInfoList.GetFCommandCount();
                	if(sDataCaption.equals("1013") || sDataCaption.equals("1014") || sDataCaption.equals("1041")){
                		
                	}
                    for (int j = 0; j < iCommandCount; j++){
                    	sCommand = ((SFE_CommandInfo)(CommandInfoList.CommandInfoList.get(j))).GetFProtocolCommand();
                    	if (sDataCaption.equals(sCommand)) {//查找是否存在当前命令
                            iCommandLen = 2*((SFE_CommandInfo)(CommandInfoList.CommandInfoList.get(j))).GetFCommandLen();//得到此命令的数据区字符长度=字节长度*2
                            if (iCommandLen == 0){//不定长类型长度为0
                            	iCommandLen = FrameDataArea.length();
                            }
                            if (iCommandLen <= FrameDataArea.length()){//如果数据长度合法
                            	bIsSearch = true;
                            	NormalData = GluMethodHeXing.ExplainNormalPhysicalData(FrameDataArea.substring(0,iCommandLen),j,CommandInfoList,TermialProtocolType,0);//解释该命令的物理数据
                                FrameDataArea = FrameDataArea.substring(iCommandLen,FrameDataArea.length());//消去已经解释的命令数据长度
                                for (int k = 0; k <NormalData.DataItemList.size(); k++){//把解释后的数据保存在对象数组里
                                	NormalDataListTemp[0].DataItemList.add(NormalData.DataItemList.get(k));
                                	NormalDataListTemp[0].DataItemCountAdd();
                                }
                             
                            }else if (FrameDataArea.equals("00")){
                            	bIsSearch = true;
                            	NormalData.DataItemAdd(sDataCaption, "-1");
                            	NormalDataListTemp[0].DataItemList.add(NormalData.DataItemList.get(0));
                            	NormalDataListTemp[0].DataItemCountAdd();
                            	FrameDataArea = "";
                            }else{//传入长度不够
                                iExplainResult = 50020;//传入数据长度非法
                            }
                            break;
                    	}
                    }        
                    if (!bIsSearch) {
                    	if (iExplainResult == 0){
                    		iExplainResult = 50010;//数据项在规约里未支持
                    	}                    	
                        NormalDataListTemp[0].DataItemAdd("",FrameDataArea.substring(0,FrameDataArea.length()));                        
                        break;
                    }
                }
                NormalDataList.add(NormalDataListTemp[0]);
            } catch (Exception e) {
                Glu_ConstDefine.Log1.WriteLog(
                        "Func:FrameDataAreaExplainHeXing__ExplainNormalDataAreaHeXing();Error:" +
                        e.toString());
            }
        } finally {                      
        }
        DataListInfo.ExplainResult = iExplainResult;
        DataListInfo.DataType = 10;
        DataListInfo.DataList = NormalDataList;  
        return DataListInfo;
    }
    public SFE_DataListInfo ExplainHistoryDataAreaHeXing( //历史数据区解释(日月冻结及曲线数据)
            String FrameDataArea, int TermialProtocolType, String ControlCode,
            int DataType, int FrameType) { //帧数据区,终端规约,功能码,数据类型,帧类型(主动上送还是召测返回)
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
                ArrayList <String>CommandListTemp = new ArrayList<String>();
                String sDataCaption = "", sTaskDateTime = "", sHour = "";
                String sMeasureAdd="";
                int iMeasuredType = 10, iCount = 0;
                
                CommandInfoList = TermialHeXingCommandInfoList;

            	sDataCaption = FrameDataArea.substring(0, 4);
            	sDataCaption = "HX" + sDataCaption;        
            	CommandListTemp.add(sDataCaption);
                FrameDataArea = FrameDataArea.substring(4, FrameDataArea.length()); //消去数据单元标识        
                while (FrameDataArea.length() >= 4) { //无数据体时至少还2个字节(数据单元标识)
                    
                    //处理每块表计的数据，6字节表号+数据内容+5字节抄到时间
                    sMeasureAdd = FrameDataArea.substring(0, 12); //取表地址
                    FrameDataArea = FrameDataArea.substring(12, FrameDataArea.length());
                    
                    if (sDataCaption.equals("HX1015")){//重点用户数据带一个字节的小时数
                    	sHour = FrameDataArea.substring(0, 2); //取小时数
                        FrameDataArea = FrameDataArea.substring(2, FrameDataArea.length());
                    }
                    if (sDataCaption.equals("HX2009")){//单相表和三相表返回的内容不同
                    	if (FrameDataArea.length() == 52){//单相表
                    		sDataCaption = sDataCaption + "01";
                    	} else {
                    		sDataCaption = sDataCaption + "02";
                    	}
                    } 
                    //解释数据
                    NormalDataTemp = GluMethodHeXing.
		                    CommandSearchAndExplain(
								FrameDataArea, sDataCaption,
								CommandInfoList, TermialProtocolType,
								0, 0);
                    FrameDataArea = FrameDataArea.substring(
                            NormalDataTemp.GetCommandLen(),
                            FrameDataArea.length());
                    sDataCaption = sDataCaption.substring(0, 6);
                    //取抄表时间
                    if (sDataCaption.equals("HX3002") || sDataCaption.equals("HX3003") 
                     || sDataCaption.equals("HX3005") || sDataCaption.equals("HX2018")
                     || sDataCaption.equals("HX2048") || sDataCaption.equals("HX2009") 
                     || sDataCaption.equals("HX2019") || sDataCaption.equals("HX204A") ){ //不带时间
                    	if (sDataCaption.equals("HX2009")){//1个字节的保留字节
                    		FrameDataArea = FrameDataArea.substring(2,FrameDataArea.length());
                    	}
                    }else if (sDataCaption.equals("HX3004")){//3个字节YYMMDD
                    	sTaskDateTime = FrameDataArea.substring(0,6);
                    	sTaskDateTime = "20" + sTaskDateTime.substring(0, 6) + "000000";
                    	FrameDataArea = FrameDataArea.substring(6,FrameDataArea.length());
                    }else {//5个字节的YYMMDDHHNN	                    
	                    sTaskDateTime = FrameDataArea.substring(0,10);
	                    if (DataType == 22 || sDataCaption.equals("HX3001")){//曲线数据
	                    	sTaskDateTime = "20" + sTaskDateTime + "00";
	                    } else if (DataType == 23){//日冻结数据
	                    	sTaskDateTime = "20" + sTaskDateTime.substring(0, 6) + "000000";
	                    } else if (DataType == 24){//月冻结数据
	                    	sTaskDateTime = "20" + sTaskDateTime.substring(0, 4) + "01000000";
	                    } else if (sDataCaption.equals("HX1015")){
	                    	sTaskDateTime = "20" + sTaskDateTime.substring(0, 6) + sHour + "0000";
	                    }
	                    FrameDataArea = FrameDataArea.substring(10,FrameDataArea.length());
                    }    
                    //把解释后的数据保存在中间对象里
                    for (int k = 0; k < NormalDataTemp.DataItemList.size();	k++) {
                    	HistoryDataListTemp[iCount].DataItemList.add(
				                   NormalDataTemp.DataItemList.get(k));
                    	SFE_DataItem DataItem = NormalDataTemp.DataItemList.get(k);
                    	String sCommandID = new String(DataItem.GetDataCaption());
                		String sConnent = new String(DataItem.GetDataContent());
                		if (sCommandID.equals("HX0006")){
                			sTaskDateTime = "20" + sConnent.substring(0, 2) + sConnent.substring(3, 5) 
                				+ sConnent.substring(6, 8) + sConnent.substring(9, 11) + "0000";
                		}
                    
                    }
                    for (int k = 0; k < NormalDataTemp.DataItemList.size();	k++) {
                    	HistoryDataListTemp[iCount].DataItemList.add(
				                   NormalDataTemp.DataItemList.get(k));
                    	HistoryDataListTemp[iCount].DataItemCountAdd();
                    	HistoryDataListTemp[iCount].SetMeasuredAdd(sMeasureAdd);
                    	HistoryDataListTemp[iCount].SetMeasuredPointNo(0);
                    	HistoryDataListTemp[iCount].SetMeasuredPointType(iMeasuredType);
                    	HistoryDataListTemp[iCount].SetTaskDateTime(sTaskDateTime);
                    }
                    
                    iCount += 1;
                }
                //从任务信息队列查找任务号和测量点类型,返回历史解析数据列表
                SPE_TaskInfoList TempTaskInfoList = new SPE_TaskInfoList();
                TempTaskInfoList = HeXingTaskInfoList;                
                if (FrameType == 11) { //主动上送任务
                    HistoryDataList = GluMethodHeXing.
                                      SearchAndMatchingTaskInfoList(
                                              CommandListTemp,
                                              HistoryDataListTemp, iCount,
                                              TempTaskInfoList); //从数据库配置信息查找主站配置的任务号
                    if (HistoryDataList.size() == 0) {
                        DataListInfo.ExplainResult = 50018; //未找到任务配置信息
                        for (int i = 0; i < iCount; i++) {
                            HistoryDataList.add(HistoryDataListTemp[i]);
                        }
                    }
                } else {
                    if (Glu_ConstDefine.TerminalAutoTaskAdjust) {
                        HistoryDataList = GluMethodHeXing.
                                          SearchAndMatchingTaskInfoList(
                                                  CommandListTemp,
                                                  HistoryDataListTemp,
                                                  iCount,
                                                  TempTaskInfoList); //从数据库配置信息查找主站配置的任务号
                        if (HistoryDataList.size() == 0) {
                            for (int i = 0; i < iCount; i++) {
                                String sCommand = (String) CommandListTemp.get(
                                        0);
                                HistoryDataListTemp[i].SetTaskNo(
                                		GluMethodHeXing.
                                        GetSelfDefineTaskNo(TermialProtocolType, sCommand,
                                        		TempTaskInfoList));
                                HistoryDataList.add(HistoryDataListTemp[i]);
                            }
                        }
                    } else { //从主动上送任务列表中无法找到对应的任务号，则从自定义任务列表中查找
                        for (int i = 0; i < iCount; i++) {
                            String sCommand = (String) CommandListTemp.get(0);
                            HistoryDataListTemp[i].SetTaskNo(GluMethodHeXing.
                                    GetSelfDefineTaskNo(TermialProtocolType, sCommand,
                                    		TempTaskInfoList));
                            HistoryDataList.add(HistoryDataListTemp[i]);
                        }
                    }
                }
            } catch (Exception e) {
                Glu_ConstDefine.Log1.WriteLog(
                        "Func:FrameDataAreaExplainHeXing__ExplainHistoryDataAreaHeXing();Error:" +
                        e.toString());
            }
        } finally {
        }
        DataListInfo.DataType = 20;
        DataListInfo.DataList = HistoryDataList;
        return DataListInfo;
    }
   /* 
    public SFE_DataListInfo ExplainAlarmDataAreaHeXing( //告警数据区解释
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
                        AlarmDataList = ExplainAlarmParameterHeXing(
                                FrameDataArea, iFn, iAlarmDataCount);
                    }
                }
            } catch (Exception e) {
                Glu_ConstDefine.Log1.WriteLog(
                        "Func:FrameDataAreaExplainHeXing__ExplainAlarmDataAreaHeXing();Error:" +
                        e.toString());
            }
        } finally {
        }
        DataListInfo.ExplainResult = 0;
        DataListInfo.DataType = 30;
        DataListInfo.DataList = AlarmDataList;
        return DataListInfo;
    }*/
 /*   
    public ArrayList ExplainAlarmParameterHeXing(String sDateContent, int Fn,
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
					int iAlarmCode = Integer.parseInt(sDateContent.substring(0, 2), 16); //告警编码
					int iAlarmlen = Integer.parseInt(sDateContent.substring(2, 4), 16) + 2; //告警长度
					String sAlarmDateTime = "20" + DataSwitch.
					      ReverseStringByByte(sDateContent.substring(4, 14)) + "00"; //告警发生时间
					sDateContent = sDateContent.substring(14, sDateContent.length());
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
						//	AlarmCodeList = ExplainAlarmCodeQuanGuo(iAlarmType,
						//			sAlarmSign, Integer.toString(iAlarmCode),
						//			AlarmMaxCount);
						}
					} 
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
				//	AlarmData.SetAlarmDateTime(sAlarmDateTime);
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

			} catch (Exception e) {
				Glu_ConstDefine.Log1.WriteLog(
						"Func:FrameDataAreaExplainQuanGuo__ExplainAlarmParameterQuanGuo();Error:" +
						e.toString());
			}
		} finally {            
		}
		return AlarmDataList;
    }*/
}

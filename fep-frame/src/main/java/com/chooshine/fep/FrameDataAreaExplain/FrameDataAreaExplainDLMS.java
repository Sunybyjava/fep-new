package com.chooshine.fep.FrameDataAreaExplain;

import java.net.URI;
import java.util.ArrayList;
import com.chooshine.fep.ConstAndTypeDefine.*;

public class FrameDataAreaExplainDLMS {//DLMS规约数据区解帧类
    //存储方式
    private final int CCFS_BCD = 10; //BCD方式
    private final int CCFS_HEX = 20; //十六进制方式
    private final int CCFS_ASC = 30; //ASCII码方式

    //存储顺序
    private final int CCSX_SX = 10; //顺序存储
    private final int CCSX_NX = 20; //逆序存储

    //数据类型
    private final int c_Float = 10; //浮点型
    private final int c_FloatHex = 11; //Hex浮点型
    private final int c_FloatLen = 12; //带长度的Hex浮点型
    private final int c_FloatNoLx = 13; //不带类型的Hex浮点型
    private final int c_FloatBit = 14; //浮点数（DLMS用）
    private final int c_DateTime = 20; //日期型
    private final int c_DateTimeHex = 21; //Hex日期型
    private final int c_DateTimeNoLx = 22; //不带类型Hex日期型
    private final int c_String = 30; //字符型
    private final int c_StringNoLen = 31; //不带长度字符型
    private final int c_NOLenStr = 40; //不定长字符型
    private final int c_Complex = 50; //复合数据类型
    private final int c_complexEX =51;//带类型的复合数据类型（结构数组里面的结构数组元素）
    private final int c_FloatEx = 60; //特殊浮点型（对应国网规范格式2,带幂部）
    private final int c_FloatFH = 70; //带正负号的浮点型 (0为正号或是上浮，1为负号或是下浮)
    private final int c_DateTimeXQ = 80; //带星期的日期型2004-10-10 10：10：10#4
    private final int c_IntDWFH = 90; //带单位标志和正负号的整型值串
    private final int c_IntZJDWFH = 91; //带追加,单位标志和正负号的整型值串 8月22号增加

    private final int MAX_LENGTH_FHCOUNT = 120; //负荷数据点数最大值

    SPE_CommandInfoList TermialDLMSCommandInfoList = new SPE_CommandInfoList(); //终端DLMS命令信息队列
   
    FrameDataAreaExplainGluMethod GluMethodDLMS = new FrameDataAreaExplainGluMethod(); //数据区解析公用类
    
    public FrameDataAreaExplainDLMS(String path) {
        try {
            InitialList(path,null);
        } catch (Exception e) {
        	Glu_ConstDefine.Log1.WriteLog(e.toString());
        }
    }
    public FrameDataAreaExplainDLMS(URI ur) {
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
                if (Glu_ConstDefine.TerminalDLMSSupport) {
                    Glu_DataAccess DataAccess ;
                    if (ur == null) {
						DataAccess = new Glu_DataAccess(path);
					} else {
						DataAccess = new Glu_DataAccess(ur);
					}
                    DataAccess.LogIn(0);
                    TermialDLMSCommandInfoList = DataAccess.GetCommandInfoList(Glu_ConstDefine.GY_ZD_DLMS);//初始化终端DLMS命令信息队列
                    DataAccess.LogOut(0);
                }                
            } catch (Exception e) {
                Glu_ConstDefine.Log1.WriteLog(
                        "Func:FrameDataAreaExplainDLMS__InitialList();Error:" +
                        e.toString());
            }
        } finally {
        }
        return Result;
    }
    public SFE_DataListInfo ExplainSetResultDataAreaDLMS( //设置返回解释
            String FrameDataArea, int TermialProtocolType) { //帧数据区,终端规约
        SFE_DataListInfo DataListInfo = new SFE_DataListInfo();
        ArrayList <SFE_SetResultData>SetResultDataList = new ArrayList<SFE_SetResultData>();
        int iExplainResult=0;
        try {
            try {
            	SFE_SetResultData SetResultData = new SFE_SetResultData();            	
                int iSetResult = 0;
                String sDataCaption = "", sCommand = "", sResponseSeq = "";
                sDataCaption = FrameDataArea.substring(0, 4);//set_response
                sResponseSeq = FrameDataArea.substring(2, 4);
                if (sDataCaption.substring(0, 2).equals("C5")){
                	if (sResponseSeq.equals("01") || sResponseSeq.equals("03")){
                    	if (sResponseSeq.equals("01")){
                    		sDataCaption = sDataCaption + "00000000";
                    	} 
                    	sCommand = FrameDataArea.substring(6, 8);//result
                        if (sCommand.equals("00")){
                        	iSetResult = 0;
                        	if (sResponseSeq.equals("03")){
                        		sDataCaption = sDataCaption + FrameDataArea.substring(6, 14);
                        	}
                        }else{
                        	iSetResult = Integer.parseInt(FrameDataArea.substring(8, 10));
                        	if (sResponseSeq.equals("03")){
                        		sDataCaption = sDataCaption + FrameDataArea.substring(10, 18);
                        	}
                        }                
                    } else if (sResponseSeq.equals("02")){
                    	sDataCaption = sDataCaption + FrameDataArea.substring(6, 14);
                    	iSetResult = 0;
                    }
                }else if (sDataCaption.substring(0, 2).equals("C7")){
                	if (sResponseSeq.equals("01")){
                		sCommand = FrameDataArea.substring(6, 8);//result
                    	sDataCaption = sDataCaption + "00000000";
                        if (sCommand.equals("00")){
                        	iSetResult = 0;
                        }else{
                        	iSetResult = Integer.parseInt(FrameDataArea.substring(8, 10));
                        }
                	}
                }               
                
                SetResultData.SetMeasuredPointNo(0);
                SetResultData.SetMeasuredPointType(10);
                SetResultData.DataItemAdd(sDataCaption, iSetResult);
                SetResultDataList.add(SetResultData);   

            } catch (Exception e) {
                Glu_ConstDefine.Log1.WriteLog(
                        "Func:FrameDataAreaExplainDLMS__ExplainSetResultDataAreaDLMS();Error:" +
                        e.toString());
            }
        } finally {
        }
        DataListInfo.ExplainResult = iExplainResult;
        DataListInfo.DataType = 40;
        DataListInfo.DataList = SetResultDataList;
        return DataListInfo;
    }
    
    public SFE_DataListInfo ExplainNormalDataAreaDLMS( //普通数据区解释
            String FrameDataArea, int TermialProtocolType, String CommandID) { //帧数据区,终端规约,命令ID
        SFE_DataListInfo DataListInfo = new SFE_DataListInfo();
        ArrayList <SFE_NormalData>NormalDataList = new ArrayList<SFE_NormalData>();
        int iExplainResult=0;
        try {
            try {
            	SFE_NormalDataTemp NormalDataTemp = new SFE_NormalDataTemp();
                SPE_CommandInfoList CommandInfoList = new SPE_CommandInfoList();                
                int iCommandLen=0;
                SFE_NormalData NormalData;
        		String sDataCaption = ""; //数据项标识
        		NormalData = new SFE_NormalData();
        		NormalData.SetMeasuredPointNo(0);
        		NormalData.SetMeasuredPointType(10);
        		String sGetResponse = FrameDataArea.substring(2, 4);//数据标识 01:normal 02:datablock
        		CommandInfoList = TermialDLMSCommandInfoList;
                if (sGetResponse.equals("01") && !FrameDataArea.substring(6,8).equals("00")){
                	iExplainResult = 55000;//数据项返回错误
                	NormalData.DataItemAdd(CommandID,FrameDataArea.substring(8,10));
                    NormalDataList.add(NormalData);
                }else{// 取数据内容
                    if (sGetResponse.equals("01")){//单个帧
                    	FrameDataArea = FrameDataArea.substring(8, FrameDataArea.length());
                    	if (FrameDataArea.substring(0, 2).equals("01") || FrameDataArea.substring(0, 2).equals("02")){
                			FrameDataArea = FrameDataArea.substring(4, FrameDataArea.length());
                		}
                    }else if (sGetResponse.equals("02")){//多帧  
                    	int iFrameNo = Integer.parseInt(FrameDataArea.substring(8, 16),16);//帧序号                        
                    	int iLenSign = Integer.parseInt(FrameDataArea.substring(18, 20),16);
                    	if (iLenSign < 128){
                    		iCommandLen = Integer.parseInt(DataSwitch.HexToInt(FrameDataArea.substring(18, 20),"00"));
                    		FrameDataArea = FrameDataArea.substring(20, 20 + iCommandLen*2);
                    	}else if (iLenSign >= 128 && iLenSign < 256){//长度大于等于128时，长度前加81，比如200-->81 C8
                    		iCommandLen = Integer.parseInt(DataSwitch.HexToInt(FrameDataArea.substring(20, 22),"00"));
                    		FrameDataArea = FrameDataArea.substring(22, 22 + iCommandLen*2);
                    	}else if (iLenSign >= 256){//长度大于等于256时，长度前加82，比如600-->82 02 58
                    		iCommandLen = Integer.parseInt(DataSwitch.HexToInt(FrameDataArea.substring(20, 24),"00"));
                    		FrameDataArea = FrameDataArea.substring(24, 24 + iCommandLen*2);
                    	}
                    	if (iFrameNo == 1) { //第一个数据块会带数据标志，后续数据块不带                   		
                    		if (FrameDataArea.substring(0, 2).equals("01") || FrameDataArea.substring(0, 2).equals("02")){
                    			FrameDataArea = FrameDataArea.substring(4, FrameDataArea.length());
                    		}
                    	}
                    }   
                    if (FrameDataArea.equals("00")){
                    	iExplainResult = 50020; //传入数据长度非法
                    	NormalData.DataItemAdd(CommandID,"");
                        NormalDataList.add(NormalData);
                    }else {
                    	while (FrameDataArea.length()>2){                		
                    		NormalDataTemp = GluMethodDLMS.DLMSCommandSearchAndExplain(
    				               FrameDataArea, CommandID,
    				               CommandInfoList, TermialProtocolType);
    				       if (NormalDataTemp.GetCommandLen() > 0 ||
    				           sDataCaption.equals("000000")) { //000000表示无召测数据项
    				           FrameDataArea = FrameDataArea.substring(
    				                   NormalDataTemp.GetCommandLen(),
    				                   FrameDataArea.length());
    				       } else if (NormalDataTemp.GetCommandLen() == 0) { //找不到命令
    				    	   iExplainResult = 50010; //数据项在规约里不能全部被支持
    				           break;
    				       } else {
    				    	   iExplainResult = 50020; //传入数据长度非法
    				           break;
    				       }
    				       for (int k = 0; k < NormalDataTemp.DataItemList.size(); k++) { //把解释后的数据保存在中间对象里
    				    	   NormalData.DataItemList.add((SFE_DataItem)NormalDataTemp.
    				                      DataItemList.get(k));
    				           NormalData.DataItemCountAdd();
    				       }			              
                    	}                   	 
                    	NormalDataList.add(NormalData);
                        NormalData = new SFE_NormalData();
                    }
                }
            } catch (Exception e) {
                Glu_ConstDefine.Log1.WriteLog(
                        "Func:FrameDataAreaExplainDLMS__ExplainNormalDataAreaDLMS();Error:" +
                        e.toString());
            }
        } finally {                      
        }//000700 01 180300FF02
        if (CommandID.substring(0,6).equals("000700") && CommandID.substring(8,18).equals("180300FF02")){
        	ArrayList <SFE_HistoryData>HistoryDataList = new ArrayList<SFE_HistoryData>();
        	SFE_NormalData NormalData = null;
        	SFE_HistoryData[] HistoryDataListTemp = new SFE_HistoryData[MAX_LENGTH_FHCOUNT]; //数据点数最大值=24
            for (int i = 0; i < MAX_LENGTH_FHCOUNT; i++) {
                HistoryDataListTemp[i] = new SFE_HistoryData();
                HistoryDataListTemp[i].SetMeasuredPointType(10);
            }
        	SFE_DataItem DataItem = null;
        	int iTaskCount = -1;
        	for (int i = 0; i < NormalDataList.size(); i++){
        		NormalData = (SFE_NormalData)NormalDataList.get(i);
        		for (int j = 0;	 j < NormalData.DataItemList.size();j++) { //把解释后的数据保存在中间对象里
        			DataItem = NormalData.DataItemList.get(j);
        			String DataCaption = new String(DataItem.GetDataCaption());
        			if (DataCaption.equals("HD0000")){  
        				iTaskCount = iTaskCount + 1;
        				HistoryDataListTemp[iTaskCount].SetTaskDateTime(new String(DataItem.GetDataContent()));
        			}else{
        				HistoryDataListTemp[iTaskCount].DataItemList.add(NormalData.DataItemList.get(j));
            			HistoryDataListTemp[iTaskCount].DataItemCountAdd();
        			}        			
        		}
        		
        	}
        	for (int i = 0; i < iTaskCount+1; i++) {
                HistoryDataList.add(HistoryDataListTemp[i]);
            }
            DataListInfo.DataType = 20;
            DataListInfo.DataList = HistoryDataList; 
        }else{
        	DataListInfo.ExplainResult = iExplainResult;
            DataListInfo.DataType = 10;
            DataListInfo.DataList = NormalDataList;  
        }
        
        return DataListInfo;
    }

    public String PhysicalDataToLogicData(String sPhysicalData,int iDataType,int iDataLen,
    		String sDataFormat, int iStorageType,int iStorageOrder){
    	String LogicData = "-1";
        try{
          try{
            if (iStorageOrder==CCSX_NX){//存储顺序如果是逆序
            	sPhysicalData=DataSwitch.ReverseStringByByte(sPhysicalData);
            }
            if (PhysicalDataLegalCheck(sPhysicalData,iDataType,iDataLen,iStorageType)||(sDataFormat.toUpperCase().equals("X"))){//合法性检验
              if (iStorageType==CCFS_BCD){//BCD码
                switch (iDataType) {
                  case c_Float:LogicData=DataSwitch.BCDToFloat(sPhysicalData,sDataFormat);
                    break;
                  default:LogicData="-1";
                }
              }
              else if (iStorageType==CCFS_HEX){//HEX码
                switch (iDataType) {
                  case c_Float:LogicData=DataSwitch.HexToIntDot(sPhysicalData,sDataFormat);
                    break;
                  case c_String:LogicData=DataSwitch.HexToString(sPhysicalData,sDataFormat);
                    break;
                  case c_DateTime:LogicData=DataSwitch.HexToDateTime(sPhysicalData,sDataFormat);
                    break;
                  default:LogicData="-1";
                }
              }
              else if (iStorageType==CCFS_ASC){//HEX码
            	  LogicData=DataSwitch.HexASCIIToString(sPhysicalData);
              }
            }
          }
          catch(Exception e){
            Glu_ConstDefine.Log1.WriteLog("Func:SFE_DataItemInfo__PhysicalDataToLogicData();Error:"+e.toString());            
          }
        }
        finally{
        }
    	return LogicData;
    }
    public boolean PhysicalDataLegalCheck(String PhysicalData,int DataType,int DataLen,int StorageType){//物理数据合法性检验
        boolean Result=true;
        int iCheck=0;
        try{
          try{
            if((PhysicalData.length()==DataLen*2)){//数据长度校验
              for (int i=0;i<PhysicalData.length();i++){
                iCheck=Integer.parseInt(PhysicalData.substring(i,i+1),16);
                if ((StorageType==CCFS_BCD)&&((iCheck<0)||(iCheck>9))){//存储方式等于BCD码
                  Result=false;
                  break;
                }
                else if (((StorageType==CCFS_HEX)||(StorageType==CCFS_ASC))&&((iCheck<0)||(iCheck>15))){//存储方式等于Hex码或ASCII
                  break;
                }
              }
            }
          }
          catch(Exception e){
        	  Glu_ConstDefine.Log1.WriteLog("Func:SFE_DataItemInfo__PhysicalDataLegalCheck();Error:"+e.toString());
            Result=false;
          }
        }
        finally{
        }
        return Result;
      }
    public SFE_DataListInfo ExplainAlarmDataAreaDLMS( //告警数据区解释
            String FrameDataArea, int TermialProtocolType) { //帧数据区,终端规约
        SFE_DataListInfo DataListInfo = new SFE_DataListInfo();
        ArrayList<SFE_AlarmData> AlarmDataList = new ArrayList<SFE_AlarmData>();
        ArrayList <String>AlarmCodeList = new ArrayList<String>();
        SFE_AlarmData AlarmData;
        try {
            try {//000100010001001C C201 07DA0B0B040A122100000000 00010000616200FF02 0600200100
            	String sGetResponse = FrameDataArea.substring(2, 4);//数据标识 01:发生时间 02:恢复时间
            	String AlarmCode = "";
                if (sGetResponse.equals("01")){
                	AlarmCode = "S";
                }else{
                	AlarmCode = "E";
                }
            	FrameDataArea = FrameDataArea.substring(4, FrameDataArea.length());                    
                if (FrameDataArea.length() >= 52) {//事件上报26个字节
                    String sAlarmDateTime = FrameDataArea.substring(0, 24);
                    sAlarmDateTime = DataSwitch.HexToDateTime(sAlarmDateTime, "YYYYMMDDWWHHNNSS");
                    sAlarmDateTime = sAlarmDateTime.substring(0,4)+sAlarmDateTime.substring(5,7)+sAlarmDateTime.substring(8,10)
                    				+sAlarmDateTime.substring(11,13)+sAlarmDateTime.substring(14,16)+sAlarmDateTime.substring(17,19);
                    if (FrameDataArea.substring(24, 42).equals("00010000616200FF02")){//classID+obis+attribute+datatype
                    	FrameDataArea = FrameDataArea.substring(44, 52);//4个字节的状态字
                    	String BinOfContent = DataSwitch.Fun2HexTo8Bin(FrameDataArea);
                    	for(int i = 0; i < 32; i++){
                    		String sTemp = BinOfContent.substring(i, i+1);
                    		if (sTemp.equals("1")){
                    		//	AlarmCode = AlarmCode + DataSwitch.StrStuff(""+(32-i), 3, "0", 10);
                    		//	AlarmCodeList.add(AlarmCode);
                    			switch (i){
	                    			case 4: AlarmCode = "C03914";break;
	                    			case 6: AlarmCode = "C03907";break;
	                    			case 7: AlarmCode = "C03906";break;
	                    			case 10: AlarmCode = "C0392C";break;
	                    			case 11: AlarmCode = "C0390F";break;
	                    			case 12: AlarmCode = "C03910";break;
	                    			case 13: AlarmCode = "C0390E";break;
	                    			case 14: AlarmCode = "C0390D";break;
	                    			case 15: AlarmCode = "C0390C";break;
	                    			case 16: AlarmCode = "C03964";break;
	                    			case 17: AlarmCode = "C0396E";break;
	                    			case 18: AlarmCode = "C03978";break;
	                    			case 19: AlarmCode = "C03982";break;
	                    			case 20: AlarmCode = "C03967";break;
	                    			case 21: AlarmCode = "C03971";break;
	                    			case 22: AlarmCode = "C0397B";break;
	                    			case 23: AlarmCode = "C03985";break;                   			
                    			}
                    			AlarmCodeList.add(AlarmCode);
                    		}
                    	}
                    }
                    for (int j = 0; j < AlarmCodeList.size(); j++) { //一个告警分解成多个告警
                        AlarmData = new SFE_AlarmData();
                        AlarmData.SetAlarmCode(AlarmCodeList.get(j).toString());
                        AlarmData.SetAlarmType(1);
                        AlarmData.SetAlarmDateTime(sAlarmDateTime);
                        AlarmData.SetMeasuredPointNo(0);
                        AlarmData.SetMeasuredPointType(10);
                        AlarmData.SetDataItemCount(0);
                        AlarmData.DataItemList = null;
                        AlarmDataList.add(AlarmData);
                    }
                }
            } catch (Exception e) {
            	Glu_ConstDefine.Log1.WriteLog(
                        "Func:FrameDataAreaExplainDLMS__ExplainAlarmDataAreaDLMS();Error:" +
                        e.toString());
            }
        } finally {
        }
        DataListInfo.ExplainResult = 0;
        DataListInfo.DataType = 30;
        DataListInfo.DataList = AlarmDataList;
        return DataListInfo;
    }
 
}

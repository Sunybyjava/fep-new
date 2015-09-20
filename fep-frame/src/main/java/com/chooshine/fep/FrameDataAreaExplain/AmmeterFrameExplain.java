package com.chooshine.fep.FrameDataAreaExplain;

import com.chooshine.fep.ConstAndTypeDefine.Glu_ConstDefine;
import com.chooshine.fep.ConstAndTypeDefine.Glu_DataAccess;
import java.util.ArrayList;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AmmeterFrameExplain { //电表帧解释类
	//public  static boolean AmmeterQuanGuoSupport = true; //电表全国规约
 //   public  boolean AmmeterQuanGuoSupport = true; //电表全国规约
 //   public  boolean AmmeterZheJiangSupport = true; //电表浙江规约
 //   public  boolean AmmeterTianJinSupport = true; //电表天津规约
 //   public  boolean AmmeterQuanGuo2007Support = true; //2007版电表全国规约

    SPE_CommandInfoList AmmeterQuGuoCommandInfoListDX = new SPE_CommandInfoList(); //电表全国大项命令信息队列
    SPE_CommandInfoList AmmeterQuGuoCommandInfoListXX = new SPE_CommandInfoList(); //电表全国小项命令信息队列
    SPE_CommandInfoList AmmeterTianJinCommandInfoListDX = new
            SPE_CommandInfoList(); //电表天津645大项命令信息队列
    SPE_CommandInfoList AmmeterTianJinCommandInfoListXX = new
            SPE_CommandInfoList(); //电表天津645小项命令信息队列
    SPE_CommandInfoList AmmeterZheJiangCommandInfoListDX = new
            SPE_CommandInfoList(); //电表浙江大项命令信息队列
    SPE_CommandInfoList AmmeterZheJiangCommandInfoListXX = new
            SPE_CommandInfoList(); //电表浙江小项命令信息队列
    SPE_CommandInfoList AmmeterQuanGuo2007CommandInfoListDX = new SPE_CommandInfoList(); //电表全国2007大项命令信息队列
    SPE_CommandInfoList AmmeterQuanGuo2007CommandInfoListXX = new SPE_CommandInfoList(); //电表全国2007小项命令信息队列
    SPE_CommandInfoList WaterAmmeterCommandInfoListXX = new SPE_CommandInfoList(); //水表小项命令信息队列
    SPE_CommandInfoList WaterDLMSCommandInfoListXX = new SPE_CommandInfoList(); //DLMS表小项命令信息队列
    
    SPE_TaskInfoList TianJinTaskInfoList = new SPE_TaskInfoList(); //模块表任务信息队列

    FrameDataAreaExplainGluMethod GluMethodAmmeter = new
            FrameDataAreaExplainGluMethod(); //数据区解析公用类
    
    public AmmeterFrameExplain(String path) {
        try {
            InitialList(path,null);
        } catch (Exception e) {
            Glu_ConstDefine.Log1.WriteLog(e.toString());
        }
    }
    public AmmeterFrameExplain(URI ur) {
        try {
            InitialList("",ur);
        } catch (Exception e) {
            Glu_ConstDefine.Log1.WriteLog(e.toString());
        }
    }

    //--------------------初始化队列-------------------------------
    public boolean InitialList(String path,URI ur) {
        boolean Result = false;
        try {
            try {
                Glu_DataAccess DataAccess;
                if (ur == null) {
					DataAccess = new Glu_DataAccess(path);
				} else {
					DataAccess = new Glu_DataAccess(ur);
				}
                DataAccess.LogIn(0);
                if (Glu_ConstDefine.AmmeterQuanGuoSupport) {
                    AmmeterQuGuoCommandInfoListDX = DataAccess.
                            GetCommandInfoList(11); //初始化电表全国大项命令信息队列
                    AmmeterQuGuoCommandInfoListXX = DataAccess.
                            GetCommandInfoList(12); //初始化电表全国小项命令信息队列
                }
                if (Glu_ConstDefine.AmmeterZheJiangSupport) {
                    AmmeterZheJiangCommandInfoListDX = DataAccess.
                            GetCommandInfoList(21); //初始化电表浙江大项命令信息队列
                    AmmeterZheJiangCommandInfoListXX = DataAccess.
                            GetCommandInfoList(22); //初始化电表浙江小项命令信息队列
                }
                if (Glu_ConstDefine.AmmeterTianJinSupport) {
                    AmmeterTianJinCommandInfoListDX = DataAccess.
                            GetCommandInfoList(15); //初始化电表天津大项命令信息队列
                    AmmeterTianJinCommandInfoListXX = DataAccess.
                            GetCommandInfoList(14); //初始化电表天津小项命令信息队列
                    TianJinTaskInfoList = DataAccess.GetTaskInfoList(105); //初始化任务信息队列
                }
                if (Glu_ConstDefine.AmmeterQuanGuo2007Support) {
                    AmmeterQuanGuo2007CommandInfoListDX = DataAccess.
                            GetCommandInfoList(31); //初始化电表全国大项命令信息队列
                    AmmeterQuanGuo2007CommandInfoListXX = DataAccess.
                            GetCommandInfoList(32); //初始化电表全国小项命令信息队列
                }
                if (Glu_ConstDefine.WaterAmmeterSupport) {
                	WaterAmmeterCommandInfoListXX = DataAccess.
                            GetCommandInfoList(40); //初始化水表命令信息队列
                }
                if (Glu_ConstDefine.DLMSAmmeterSupport) {
                	WaterDLMSCommandInfoListXX = DataAccess.
                            GetCommandInfoList(50); //初始化DLMS表命令信息队列
                }
                DataAccess.LogOut(0);
            } catch (Exception e) {
                Glu_ConstDefine.Log1.WriteLog(
                        "ErrorFun:FrameDataAreaExplain@AmmeterFrameExplain__InitialList();Error:" +
                        e.toString());
            }
        } finally {
        }
        return Result;
    }

    public ArrayList ExplainAmmeterContent(String DataCaption,
                                           String DataContent) {
        String sCommand = "";
        SPE_CommandInfoList CommandInfoList = new SPE_CommandInfoList();
        boolean bIsFind = false;
        SFE_NormalData NormalData = new SFE_NormalData();
        ArrayList DataItemList = new ArrayList();
        CommandInfoList = AmmeterQuGuoCommandInfoListDX;
        for (int i = 0; i < CommandInfoList.GetFCommandCount();
                     i++) {
            sCommand = ((SFE_CommandInfo) (CommandInfoList.CommandInfoList.get(
                    i))).GetFProtocolCommand();
            if (DataCaption.equals(sCommand)) { //查找是否存在当前命令
                bIsFind = true;
                //解释该命令的物理数据
                NormalData = GluMethodAmmeter.
                             ExplainNormalPhysicalData(
                                     DataContent, i, CommandInfoList, 0, 0);
                break; //中继只支持单个命令
            }
        }
        if (bIsFind == false) {
            CommandInfoList = AmmeterQuGuoCommandInfoListXX;
        }
        for (int i = 0; i < CommandInfoList.GetFCommandCount(); i++) {
            sCommand = ((SFE_CommandInfo) (CommandInfoList.CommandInfoList.get(
                    i))).GetFProtocolCommand();
            if (DataCaption.equals(sCommand)) { //查找是否存在当前命令
                bIsFind = true;
                //解释该命令的物理数据
                NormalData = GluMethodAmmeter.ExplainNormalPhysicalData(
                        DataContent, i, CommandInfoList, 0, 0);
            }
        }
        DataItemList = NormalData.DataItemList;
        return DataItemList;
    }

    public SFE_DataListInfo ExplainPointAmmeterDataAreaGuYuan(String
            FrameDataArea) { //固原集抄中点抄数据区解释
        SFE_DataListInfo DataListInfo = new SFE_DataListInfo();
        DataListInfo.DataType = 50; //50：点抄电表的返回
        ArrayList <SFE_ExplainPointAmmeterReadData>PointAmmeterReadDataList = new ArrayList<SFE_ExplainPointAmmeterReadData>();
        SFE_ExplainPointAmmeterReadData PointAmmeterReadData = new
                SFE_ExplainPointAmmeterReadData();
        PointAmmeterReadData.MeasurePoint = Integer.parseInt(DataSwitch.
                ReverseStringByByte(FrameDataArea.substring(0, 4)), 16);
        FrameDataArea = FrameDataArea.substring(8);
        String sAmmeterAddress = DataSwitch.ReverseStringByByte(FrameDataArea.
                substring(0, 12));
        PointAmmeterReadData.AmmeterAddress = sAmmeterAddress;
        String sControlCode = FrameDataArea.substring(12, 14);
        sControlCode = DataSwitch.Fun2HexTo8Bin(sControlCode);
        String sErrorInfo = sControlCode.substring(1, 2);
        sControlCode = sControlCode.substring(3, 8);
        switch (Integer.parseInt(sControlCode)) {
        case 1: { //读数据--DataItemCount=1，即为下发读取的数据项返回结果或者返回的错误代码
            PointAmmeterReadData.ControlCode = 1;
            PointAmmeterReadData.DataItemCount = 1;
            if (sErrorInfo.equals("0")) { //从站正常应答
                ArrayList DataItemList = new ArrayList();
                String sDataCaption = DataSwitch.ReverseStringByByte(
                        FrameDataArea.substring(16, 20)); //数据标识
                String sDataContent = FrameDataArea.substring(20); //数据
                DataItemList = ExplainAmmeterContent(sDataCaption, sDataContent);
                PointAmmeterReadData.DataItemList = DataItemList;
            } else { //从站异常应答
                ArrayList <SFE_DataItem>DataItemList = new ArrayList<SFE_DataItem>();
                SFE_DataItem DataItem = new SFE_DataItem();
                DataItem.DataItemAdd("000200",
                                     DataSwitch.ReverseStringByByte(
                                             FrameDataArea.substring(14, 18))); //错误信息字
                DataItemList.add(DataItem);
                PointAmmeterReadData.DataItemList = DataItemList;
            }
            PointAmmeterReadDataList.add(PointAmmeterReadData);
            break;
        }
        case 100: { //写数据--DataItemCount=1，表示该命令是否成功
            PointAmmeterReadData.ControlCode = 2;
            PointAmmeterReadData.DataItemCount = 1;
            if (sErrorInfo.equals("0")) { //从站正常应答
                ArrayList <SFE_DataItem>DataItemList = new ArrayList<SFE_DataItem>();
                SFE_DataItem DataItem = new SFE_DataItem();
                DataItem.DataItemAdd("000100", ""); //从站正常应答：无数据体。
                DataItemList.add(DataItem);
                PointAmmeterReadData.DataItemList = DataItemList;
            } else { //从站异常应答
                ArrayList <SFE_DataItem>DataItemList = new ArrayList<SFE_DataItem>();
                SFE_DataItem DataItem = new SFE_DataItem();
                DataItem.DataItemAdd("000200",
                                     DataSwitch.ReverseStringByByte(
                                             FrameDataArea.substring(14, 18))); //错误信息字
                DataItemList.add(DataItem);
                PointAmmeterReadData.DataItemList = DataItemList;
            }
            PointAmmeterReadDataList.add(PointAmmeterReadData);
            break;
        }
        case 1111: { //修改密码--DataItemCount=1，正常返回为设置后新的密码权限
            PointAmmeterReadData.ControlCode = 4;
            PointAmmeterReadData.DataItemCount = 1;
            if (sErrorInfo.equals("0")) { //从站正常应答
                ArrayList <SFE_DataItem>DataItemList = new ArrayList<SFE_DataItem>();
                SFE_DataItem DataItem = new SFE_DataItem();
                DataItem.DataItemAdd("000100",
                                     DataSwitch.ReverseStringByByte(
                                             FrameDataArea.substring(14, 20))); //新编入的密码权限及密码
                DataItemList.add(DataItem);
                PointAmmeterReadData.DataItemList = DataItemList;
            }
            PointAmmeterReadDataList.add(PointAmmeterReadData);
            break;
        }
        }
        DataListInfo.ExplainResult = 0;
        DataListInfo.DataList = PointAmmeterReadDataList;
        return DataListInfo;
    }

    public SFE_NormalData ExplainDLMSAmmeterFrame(String AmmeterFrame,String sCommandID) { //电表帧解释
    	SFE_NormalData NormalData = new SFE_NormalData();
        try {
            try {         
            	String sDataArea = "";
                String sCommand = "";
                String sDataCaption = "";
                SPE_CommandInfoList CommandInfoList = new SPE_CommandInfoList();
            	AmmeterFrame = DataSwitch.SearchStr("7E", AmmeterFrame); //搜索以7E开头的电表帧,电表帧前有可能有间隔符FE                	       	
                int iSetResult = 0;
                String sResponseSeq = "";
                sDataArea = AmmeterFrame.substring(28, AmmeterFrame.length()-6);
                sDataCaption = sDataArea.substring(0, 4);//set_response
                sResponseSeq = sDataArea.substring(2, 4);
                if ((sDataCaption.substring(0, 2).equals("C4"))){
                	SFE_NormalDataTemp NormalDataTemp = new SFE_NormalDataTemp();               
                    int iCommandLen=0;
            		NormalData = new SFE_NormalData();
            		NormalData.SetMeasuredPointNo(0);
            		NormalData.SetMeasuredAdd("");
            		NormalData.SetMeasuredPointType(10);
            		String sGetResponse = sDataArea.substring(2, 4);//数据标识 01:normal 02:datablock
            		CommandInfoList = WaterDLMSCommandInfoListXX;
                    if (sGetResponse.equals("01") && !sDataArea.substring(6,8).equals("00")){
                    	NormalData.DataItemAdd(sDataCaption + "00000000",sDataArea.substring(8,10));
                    }else{// 取数据内容
                        if (sGetResponse.equals("01")){//单个帧
                        	sDataArea = sDataArea.substring(8, sDataArea.length());
                        	if (sDataArea.substring(0, 2).equals("01") || sDataArea.substring(0, 2).equals("02")){
                        		sDataArea = sDataArea.substring(4, sDataArea.length());
                    		}
                        }else if (sGetResponse.equals("02")){//多帧  
                        	int iFrameNo = Integer.parseInt(sDataArea.substring(8, 16),16);//帧序号                        
                        	int iLenSign = Integer.parseInt(sDataArea.substring(18, 20),16);
                        	if (iLenSign < 128){
                        		iCommandLen = Integer.parseInt(DataSwitch.HexToInt(sDataArea.substring(18, 20),"00"));
                        		sDataArea = sDataArea.substring(20, 20 + iCommandLen*2);
                        	}else if (iLenSign >= 128 && iLenSign < 256){//长度大于等于128时，长度前加81，比如200-->81 C8
                        		iCommandLen = Integer.parseInt(DataSwitch.HexToInt(sDataArea.substring(20, 22),"00"));
                        		sDataArea = sDataArea.substring(22, 22 + iCommandLen*2);
                        	}else if (iLenSign >= 256){//长度大于等于256时，长度前加82，比如600-->82 02 58
                        		iCommandLen = Integer.parseInt(DataSwitch.HexToInt(sDataArea.substring(20, 24),"00"));
                        		sDataArea = sDataArea.substring(24, 24 + iCommandLen*2);
                        	}
                        	if (iFrameNo == 1) { //第一个数据块会带数据标志，后续数据块不带                   		
                        		if (sDataArea.substring(0, 2).equals("01") || sDataArea.substring(0, 2).equals("02")){
                        			sDataArea = sDataArea.substring(4, sDataArea.length());
                        		}
                        	}
                        }   
                        if (sDataArea.equals("00")){
                        	NormalData.DataItemAdd(sCommandID,"");
                        }else {
                        	while (sDataArea.length()>2){                		
                        		NormalDataTemp = GluMethodAmmeter.DLMSCommandSearchAndExplain(
                        				sDataArea, sCommandID,
        				               CommandInfoList, Glu_ConstDefine.GY_DB_DLMS);
        				       if (NormalDataTemp.GetCommandLen() > 0 ||
        				           sDataCaption.equals("000000")) { //000000表示无召测数据项
        				    	   sDataArea = sDataArea.substring(
        				                   NormalDataTemp.GetCommandLen(),
        				                   sDataArea.length());
        				       }else {
        				           break;
        				       }
        				       for (int k = 0; k < NormalDataTemp.DataItemList.size(); k++) { //把解释后的数据保存在中间对象里
        				    	   NormalData.DataItemList.add((SFE_DataItem)NormalDataTemp.
        				                      DataItemList.get(k));
        				           NormalData.DataItemCountAdd();
        				       }			              
                        	}                   	 
                        }
                    }
                }
                else if ((sDataCaption.substring(0, 2).equals("C5")) || (sDataCaption.substring(0, 2).equals("C7"))){
                	//7EA0130200020403744CAEE6E700C501810036CF7E                    
                    if (sDataCaption.substring(0, 2).equals("C5")){
                    	if (sResponseSeq.equals("01") || sResponseSeq.equals("03")){
                        	if (sResponseSeq.equals("01")){
                        		sDataCaption = sDataCaption + "00000000";
                        	} 
                        	sCommand = sDataArea.substring(6, 8);//result
                            if (sCommand.equals("00")){
                            	iSetResult = 0;
                            	if (sResponseSeq.equals("03")){
                            		sDataCaption = sDataCaption + sDataArea.substring(6, 14);
                            	}
                            }else{
                            	iSetResult = Integer.parseInt(sDataArea.substring(8, 10));
                            	if (sResponseSeq.equals("03")){
                            		sDataCaption = sDataCaption + sDataArea.substring(10, 18);
                            	}
                            }                
                        } else if (sResponseSeq.equals("02")){
                        	sDataCaption = sDataCaption + sDataArea.substring(6, 14);
                        	iSetResult = 0;
                        }
                    }else if (sDataCaption.substring(0, 2).equals("C7")){
                    	if (sResponseSeq.equals("01")){
                    		sCommand = sDataArea.substring(6, 8);//result
                        	sDataCaption = sDataCaption + "00000000";
                            if (sCommand.equals("00")){
                            	iSetResult = 0;
                            }else{
                            	iSetResult = Integer.parseInt(sDataArea.substring(8, 10));
                            }
                    	}
                    }             
                    NormalData.DataItemAdd(sDataCaption, ""+iSetResult);
                    NormalData.SetMeasuredPointNo(0);
                    NormalData.SetMeasuredAdd("");
                    NormalData.SetMeasuredPointType(10);  
                }                                      
           
            }
            catch (Exception e) {
                Glu_ConstDefine.Log1.WriteLog(
                        "ErrorFun:FrameDataAreaExplain@AmmeterFrameExplain__ExplainDLMSAmmeterFrame();Error:" +
                        e.toString());
            }
        } finally {
        }
        return NormalData;   
    }
    public SFE_NormalData ExplainAmmeterFrame(String AmmeterFrame) { //电表帧解释
        SFE_NormalData NormalData = new SFE_NormalData();
        try {
            try {
                String sDataArea = "";
                String sCommand = "";
                String sDataCaption = "";
                String sMeasuredAdd = "";
                String WaterAmmeterFrame = "";
                int iLen = 0;
                SPE_CommandInfoList CommandInfoList = new SPE_CommandInfoList();
                boolean bIsFind = false;
                //首先搜索是否是水表帧  9B 00000001 06 04 00000001 FE64 9D
                WaterAmmeterFrame = DataSwitch.SearchStr("9B", AmmeterFrame); //搜索以B9开头的电表帧,电表帧前有可能有间隔符FE
                iLen = Integer.parseInt(WaterAmmeterFrame.substring(12, 14), 16) * 2; //数据区长度
                String cs = WaterAmmeterFrame.substring(16 + iLen, 18 + iLen)+WaterAmmeterFrame.substring(14 + iLen, 16 + iLen);
                String cstemp = DataSwitch.CRC16(WaterAmmeterFrame.substring(0, 14 + iLen));
                if (WaterAmmeterFrame.substring(18 + iLen, 20 + iLen).equals("9D") && cs.equals(cstemp)){
                	sMeasuredAdd = WaterAmmeterFrame.substring(2, 10); //水表地址
                    sMeasuredAdd = DataSwitch.StrFilter("0", true,
                            sMeasuredAdd); //过滤字符0
                    sDataCaption = "HS" + WaterAmmeterFrame.substring(10, 12) + "00";
                    sDataArea = WaterAmmeterFrame.substring(14, 14 + iLen);
                    CommandInfoList = WaterAmmeterCommandInfoListXX;
                    for (int i = 0; i < CommandInfoList.GetFCommandCount(); i++) {
                    	sCommand = ((SFE_CommandInfo) (CommandInfoList.
					           CommandInfoList.get(i))).
					              GetFProtocolCommand();
                    	if (sDataCaption.equals(sCommand)) { //查找是否存在当前命令
                    		bIsFind = true;
                    		//得到此命令的数据区字符长度=字节长度*2
                    		iLen = 2 * ((SFE_CommandInfo) (CommandInfoList.
                    				CommandInfoList.get(i))).GetFCommandLen();
					   	
                    		//解释该命令的物理数据
                    		if (iLen != sDataArea.length()){
                    			bIsFind = false;
                    			break;
                    		}
                    		NormalData = GluMethodAmmeter.ExplainNormalPhysicalData(
                    				sDataArea, i, CommandInfoList, 0, 0);                                
                    		break; //中继只支持单个命令
                    	}
                    }
                    NormalData.SetMeasuredPointNo(0);
                    NormalData.SetMeasuredAdd(sMeasuredAdd);
                    NormalData.SetMeasuredPointType(10);
                }
                
                if (!bIsFind){//不是水表帧，继续处理电表帧
                	AmmeterFrame = DataSwitch.SearchStr("68", AmmeterFrame); //搜索以68开头的电表帧,电表帧前有可能有间隔符FE
                    String sGNM = AmmeterFrame.substring(16,18);
                    if ((sGNM.equals("94")) || (sGNM.equals("9C"))){                	
                        sMeasuredAdd = DataSwitch.ReverseStringByByte(
                                 AmmeterFrame.substring(2, 14)); //电表地址
                        sMeasuredAdd = DataSwitch.StrFilter("A", true,
                                 sMeasuredAdd); //过滤字符A
                        sDataCaption = "000100";
                    	NormalData.DataItemAdd(sDataCaption, sDataArea);
                        NormalData.SetMeasuredPointNo(0);
                        NormalData.SetMeasuredAdd(sMeasuredAdd);
                        NormalData.SetMeasuredPointType(10);
                        return NormalData;
                    }else if ((sGNM.equals("C3")) || (sGNM.equals("C4")) ||
                    		  (sGNM.equals("D4")) || (sGNM.equals("DC"))){
                    	sMeasuredAdd = DataSwitch.ReverseStringByByte(
                                 AmmeterFrame.substring(2, 14)); //电表地址
                        sMeasuredAdd = DataSwitch.StrFilter("A", true,
                                 sMeasuredAdd); //过滤字符A
                    	sDataCaption = "000200";
                    	sDataArea = DataSwitch.ReverseStringByByte(AmmeterFrame.substring(20,AmmeterFrame.length()-4));
                    	sDataArea = AmmeterDataSwitch(sDataArea, "-"); //数据区减33H
                    	NormalData.DataItemAdd(sDataCaption, sDataArea);
                        NormalData.SetMeasuredPointNo(0);
                        NormalData.SetMeasuredAdd(sMeasuredAdd);
                        NormalData.SetMeasuredPointType(10);
                        return NormalData;
                    }                 
                    int iAmmeterProtocol = SearchAmmeterFrame(AmmeterFrame);
                    if (iAmmeterProtocol != 0) { //找到某种电表规约
                        if (iAmmeterProtocol == Glu_ConstDefine.GY_DB_QG97) { //部规
                            sMeasuredAdd = DataSwitch.ReverseStringByByte(
                                    AmmeterFrame.substring(2, 14)); //电表地址
                            sMeasuredAdd = DataSwitch.StrFilter("A", true,
                                    sMeasuredAdd); //过滤字符A
                            iLen = Integer.parseInt(AmmeterFrame.substring(18, 20),
                                                    16) * 2; //数据区长度
                            sDataArea = AmmeterFrame.substring(20, 20 + iLen);
                            sDataArea = AmmeterDataSwitch(sDataArea, "-"); //数据区减33H
                            if (sDataArea.length() >= 4) { //命令标识至少4位
                                sDataCaption = sDataArea.substring(2, 4) +
                                               sDataArea.substring(0, 2); //得到电表数据项标识
                                sDataArea = sDataArea.substring(4, sDataArea.length());
                                CommandInfoList = AmmeterQuGuoCommandInfoListDX;
                            }
                        }/*else if (iAmmeterProtocol == Glu_ConstDefine.GY_DB_ZHEJIANG) { //浙规
                            sDataArea = AmmeterFrame.substring(8,
                                    AmmeterFrame.length() - 4);
                            if (sDataArea.length() >= 6) { //数据区至少6位：测量点+命令标识
                                sMeasuredAdd = sDataArea.substring(0, 2); //电表地址
                                sDataCaption = sDataArea.substring(4, 6) +
                                               sDataArea.substring(2, 4); //得到电表数据项标识
                                sDataArea = sDataArea.substring(6, sDataArea.length());
                            }
                            CommandInfoList = AmmeterZheJiangCommandInfoListDX;
                        } else if (iAmmeterProtocol == Glu_ConstDefine.GY_DB_TJ) {
                            SFE_DataListInfo DataListInfo =
                                    ExplainTianJinAmmeterFrame(AmmeterFrame);
                            NormalData = (SFE_NormalData) DataListInfo.DataList.get(
                                    0);
                            return NormalData;
                        }*/
                        if (sDataCaption.length() == 4){
                            for (int i = 0; i < CommandInfoList.GetFCommandCount();
                                         i++) {
                                sCommand = ((SFE_CommandInfo) (CommandInfoList.
                                        CommandInfoList.get(i))).
                                           GetFProtocolCommand();
                                if (sDataCaption.equals(sCommand)) { //查找是否存在当前命令
                                    bIsFind = true;
                                    //得到此命令的数据区字符长度=字节长度*2
                                    iLen = 2 * ((SFE_CommandInfo) (CommandInfoList.
                                            CommandInfoList.get(i))).GetFCommandLen();
                                    	
                                    //解释该命令的物理数据
                                    if (iLen != sDataArea.length()){
                                    	bIsFind = false;
                                    	break;
                                    }
                                    NormalData = GluMethodAmmeter.ExplainNormalPhysicalData(
     		                               sDataArea, i, CommandInfoList, 0, 0);                                
                                    break; //中继只支持单个命令
                                }
                            }
                            if (bIsFind == false) {
                                if (iAmmeterProtocol == Glu_ConstDefine.GY_DB_QG97) {
                                    CommandInfoList = AmmeterQuGuoCommandInfoListXX;
                                } /*else if (iAmmeterProtocol == Glu_ConstDefine.GY_DB_ZHEJIANG) {
                                    CommandInfoList =
                                            AmmeterZheJiangCommandInfoListXX;
                                } */
                                for (int i = 0;
                                             i < CommandInfoList.GetFCommandCount();
                                             i++) {
                                    sCommand = ((SFE_CommandInfo) (CommandInfoList.
                                            CommandInfoList.get(i))).
                                               GetFProtocolCommand();                                
                                    if (sDataCaption.equals(sCommand)) { //查找是否存在当前命令
                                        bIsFind = true;
                                        if (sDataCaption.equals("EE20") && (sDataArea.length() >= 66)){//输入TOKEN码,STS解析结果(1)+ 电能底码数据块(20)+余额(4)+余额状态(1)+主继电器状态(1)+电表模式(1)+主继电器操作原因(1)+当前电表事件状态(4)
                                        	String stemp = "";
                                        	String sLogicData = "";
                                        	stemp = sDataArea.substring(0,2);
                                        	sLogicData = stemp + "#";
                                        	for (int j = 0; j <= 5; j++){
                                        		stemp = DataSwitch.ReverseStringByByte(sDataArea.substring(2 + j * 8, 2 + j * 8 + 8));
                                        		stemp = DataSwitch.HexToInt(stemp,"00000000");
                                            	sLogicData = sLogicData + stemp + "#";
                                        	}
                                        	stemp = sDataArea.substring(50,52);
                                        	sLogicData = sLogicData + stemp + "#";
                                        	stemp = sDataArea.substring(52,54);
                                        	sLogicData = sLogicData + stemp + "#";
                                        	stemp = sDataArea.substring(54,56);
                                        	sLogicData = sLogicData + stemp + "#";
                                        	stemp = sDataArea.substring(56,58);
                                        	sLogicData = sLogicData + stemp + "#";
                                        	stemp = sDataArea.substring(58,66);
                                        	sLogicData = sLogicData + DataSwitch.ReverseStringByByte(stemp);
                                        	NormalData.DataItemAdd(sDataCaption, sLogicData);
                                        	return NormalData;
                                        }else if (sDataCaption.equals("EE21") && (sDataArea.length() >= 86)){//输入注销码,STS解析结果(1)+ 电能底码数据块(20)+余额(4)+余额状态(1)+主继电器状态(1)+电表模式(1)+主继电器操作原因(1)+当前电表事件状态(4)+注销返回码(10)
                                        	String stemp = "";
                                        	String sLogicData = "";
                                        	stemp = sDataArea.substring(0,2);
                                        	sLogicData = stemp + "#";
                                        	for (int j = 0; j <= 5; j++){
                                        		stemp = DataSwitch.ReverseStringByByte(sDataArea.substring(2 + j * 8, 2 + j * 8 + 8));
                                        		stemp = DataSwitch.HexToInt(stemp,"00000000");
                                            	sLogicData = sLogicData + stemp + "#";
                                        	}
                                        	stemp = sDataArea.substring(50,52);
                                        	sLogicData = sLogicData + stemp + "#";
                                        	stemp = sDataArea.substring(52,54);
                                        	sLogicData = sLogicData + stemp + "#";
                                        	stemp = sDataArea.substring(54,56);
                                        	sLogicData = sLogicData + stemp + "#";
                                        	stemp = sDataArea.substring(56,58);
                                        	sLogicData = sLogicData + stemp + "#";
                                        	stemp = sDataArea.substring(58,66);
                                        	sLogicData = sLogicData + DataSwitch.ReverseStringByByte(stemp) + "#";
                                        	stemp = sDataArea.substring(66,86);
                                        	sLogicData = sLogicData + DataSwitch.ReverseStringByByte(stemp);
                                        	NormalData.DataItemAdd(sDataCaption, sLogicData);
                                        	return NormalData;
                                        }else if ((sDataCaption.equals("EE01") || sDataCaption.equals("EE02") ||
                                    		       sDataCaption.equals("EE03") || sDataCaption.equals("EE04") ||
                                    		       sDataCaption.equals("EE05") || sDataCaption.equals("EE06") ||
                                    		       sDataCaption.equals("EE07") || sDataCaption.equals("EE08") ||
                                    		       sDataCaption.equals("EE09") || sDataCaption.equals("EE10") ||
                                    		       sDataCaption.equals("EE011") || sDataCaption.equals("EE12") ||
                                    		       sDataCaption.equals("EE013") || sDataCaption.equals("EA20")) &&
                                    		      sDataArea.equals("")){                                    	
                                        	NormalData.DataItemAdd("001000", "");
                                        	return NormalData;
                                        }
                                        //得到此命令的数据区字符长度=字节长度*2
                                        iLen = 2 *
                                               ((SFE_CommandInfo) (CommandInfoList.
                                                CommandInfoList.get(i))).
                                               GetFCommandLen();
                                        //解释该命令的物理数据
                                        NormalData = GluMethodAmmeter.
                                                     ExplainNormalPhysicalData(
                                                sDataArea, i, CommandInfoList, 0, 0);
                                    }
                                }
                            }
                            NormalData.SetMeasuredPointNo(0);
                            NormalData.SetMeasuredAdd(sMeasuredAdd);
                            NormalData.SetMeasuredPointType(10);
                        }
                    }
                    //全国电表规约不符合，当做2007版全国电表规约来处理
                    if ((bIsFind == false) && (iAmmeterProtocol == Glu_ConstDefine.GY_DB_QG97)) {
                    	iAmmeterProtocol = Glu_ConstDefine.GY_DB_QG2007;
                    	sDataArea = AmmeterFrame.substring(20, 20 + iLen);
                        sDataArea = AmmeterDataSwitch(sDataArea, "-"); //数据区减33H
                        if (sDataArea.length() >= 8) { //命令标识至少8位
                            sDataCaption = DataSwitch.ReverseStringByByte(sDataArea.substring(0,8)); //得到电表数据项标识
                            sDataArea = sDataArea.substring(8, sDataArea.length());
                            CommandInfoList = AmmeterQuanGuo2007CommandInfoListDX;
                        }
                        if (sDataCaption.length() == 8){
                            for (int i = 0; i < CommandInfoList.GetFCommandCount();
                                         i++) {
                                sCommand = ((SFE_CommandInfo) (CommandInfoList.
                                        CommandInfoList.get(i))).
                                           GetFProtocolCommand();
                                if (sDataCaption.equals(sCommand)) { //查找是否存在当前命令
                                    bIsFind = true;                                
                                    //得到此命令的数据区字符长度=字节长度*2
                                    iLen = 2 * ((SFE_CommandInfo) (CommandInfoList.
                                            CommandInfoList.get(i))).GetFCommandLen();
                                    	
                                    //解释该命令的物理数据
                                    NormalData = GluMethodAmmeter.ExplainNormalPhysicalData(
     		                               sDataArea, i, CommandInfoList, 0, 0);                                
                                    break; //中继只支持单个命令
                                }
                            }
                            if (bIsFind == false) {
                                CommandInfoList =  AmmeterQuanGuo2007CommandInfoListXX;
                               
                                for (int i = 0;
                                             i < CommandInfoList.GetFCommandCount();
                                             i++) {
                                    sCommand = ((SFE_CommandInfo) (CommandInfoList.
                                            CommandInfoList.get(i))).
                                               GetFProtocolCommand();
                                    if (sDataCaption.equals(sCommand)) { //查找是否存在当前命令
                                        bIsFind = true;
                                        if (sDataCaption.equals("070000FF") && (sDataArea.length() >= 24)){//随机数2(4)+ESAM序列号(8)
                                        	String stemp="";
                                        	stemp = DataSwitch.ReverseStringByByte(sDataArea.substring(0,8))+"#"+DataSwitch.ReverseStringByByte(sDataArea.substring(8,24));
                                         	NormalData.DataItemAdd(sDataCaption, stemp);
                                        	return NormalData;
                                        }else if (sDataCaption.equals("070002FF") && (sDataArea.length() >= 36)){//客户编号(6)+剩余金额(ESAM内)(4)+购电次数（ESAM内）(4)+密钥信息(4)
                                        	String stemp="";
                                        	String sLogicData="";
                                        	stemp = DataSwitch.ReverseStringByByte(sDataArea.substring(0,12)) + "#";
                                        	sLogicData = stemp;
                                        	stemp = DataSwitch.ReverseStringByByte(sDataArea.substring(12,20));
                                        	stemp = DataSwitch.HexToInt(stemp,"00000000");
                                        	sLogicData = sLogicData + stemp + "#";
                                        	stemp = DataSwitch.ReverseStringByByte(sDataArea.substring(20,28));
                                        	stemp = DataSwitch.HexToInt(stemp,"00000000");
                                        	sLogicData = sLogicData + stemp + "#";
                                        	stemp = DataSwitch.ReverseStringByByte(sDataArea.substring(28,36));
                                        	sLogicData = sLogicData + stemp;
                                        	NormalData.DataItemAdd(sDataCaption, sLogicData);
                                        	return NormalData;
                                        }else if (sDataCaption.equals("070001FF") || sDataCaption.equals("070101FF") ||
                                        		  sDataCaption.equals("070102FF") || sDataCaption.equals("070201FF") ||
                                        		  sDataCaption.equals("070202FF")){
                                        	//预售电部分返回命令解析处理
                                        	NormalData.DataItemAdd("001000", "");
                                        	return NormalData;
                                        }
                                        //得到此命令的数据区字符长度=字节长度*2
                                        iLen = 2 *
                                               ((SFE_CommandInfo) (CommandInfoList.
                                                CommandInfoList.get(i))).
                                               GetFCommandLen();
                                        //解释该命令的物理数据
                                        NormalData = GluMethodAmmeter.
                                                     ExplainNormalPhysicalData(
                                                sDataArea, i, CommandInfoList, 0, 0);
                                    }
                                }
                            }
                            NormalData.SetMeasuredPointNo(0);
                            NormalData.SetMeasuredAdd(sMeasuredAdd);
                            NormalData.SetMeasuredPointType(10);
                        }
                    }
                }               
                
                if (bIsFind == false) {
                    NormalData.SetMeasuredPointNo(0);
                    NormalData.SetMeasuredPointType(10);
                    NormalData.DataItemAdd("HL03", AmmeterFrame);
                }
            } catch (Exception e) {
                Glu_ConstDefine.Log1.WriteLog(
                        "ErrorFun:FrameDataAreaExplain@AmmeterFrameExplain__ExplainAmmeterFrame();Error:" +
                        e.toString());
            }
        } finally {
        }
        return NormalData;
    }

 //   @SuppressWarnings(value="unchecked")  //本函数的DataListInfo.DataList数需要用到泛型，此处去掉相关警告
    public SFE_DataListInfo ExplainTianJinAmmeterCommand(String sCommand,
            String MeasuredAdd, String DataArea, int CommandOrder,
            SPE_CommandInfoList CommandInfoList,
            int FrameType) {
        SFE_CommandInfo CommandInfo = (SFE_CommandInfo) (CommandInfoList.
                CommandInfoList.get(CommandOrder));
        SFE_DataListInfo DataListInfo = new SFE_DataListInfo();
        SFE_NormalData NormalData = new SFE_NormalData();
        SFE_HistoryData HistoryData = new SFE_HistoryData();
        DataListInfo.DataType = 10; //默认普通数据
        int iCommandType = 0; //命令类型(10:大项命令,如901F(9010~901E);11:大项命令,C32F(C321~C31E);20:有编码规律的小项命令,如9010~901E,B330~B34F等;30:没有块命令且没有规律的小项命令等;40:曲线数据)
        try {
            try {
                int iCommand = Integer.parseInt(sCommand, 16); //需要解释的命令标识
                int iLen = CommandInfo.GetFCommandLen() * 2; //小项命令字符长度
                if (iLen == 0) { //不定长类型长度为0
                    iLen = DataArea.length();
                }
                int iCaptionCount = 0;
                String sLogicData = "", sDataCaption = "", sDateTime = "";
                NormalData.SetMeasuredAdd(MeasuredAdd);
                NormalData.SetMeasuredPointNo(0);
                NormalData.SetMeasuredPointType(10);
                boolean bIsHistoryData = false;
                //$6010..$602F,$6110..$6B6F,$9210..$9B6F  //前N次电量数据
                //$7010..$702F,$7110..$7B6F,$A210..$AB6F //前N次最大需量数据
                if ((iCommand >= Integer.parseInt("6010", 16) &&
                     iCommand <= Integer.parseInt("602F", 16)) ||
                    (iCommand >= Integer.parseInt("6110", 16) &&
                     iCommand <= Integer.parseInt("6B6F", 16)) ||
                    (iCommand >= Integer.parseInt("9210", 16) &&
                     iCommand <= Integer.parseInt("9B6F", 16)) ||
                    (iCommand >= Integer.parseInt("7010", 16) &&
                     iCommand <= Integer.parseInt("702F", 16)) ||
                    (iCommand >= Integer.parseInt("7110", 16) &&
                     iCommand <= Integer.parseInt("7B6F", 16)) ||
                    (iCommand >= Integer.parseInt("A210", 16) &&
                     iCommand <= Integer.parseInt("AB6F", 16))) {
                    sDateTime = DataArea.substring(0, 6); //数据冻结时标
                    DataArea = DataArea.substring(6, DataArea.length());
                    iCaptionCount = DataArea.length() / iLen; //数据项个数
                    if (sCommand.substring(3, 4).equals("F")) { //大项命令
                        iCommandType = 10;
                    } else {
                        iCommandType = 20;
                    }
                    bIsHistoryData = true;
                }
                //$9010..$902F,$9110..$916F //当前电能量数据
                //$A010..$A02F,$A110..$A16F //当前最大需量数据
                else if ((iCommand >= Integer.parseInt("9010", 16) &&
                          iCommand <= Integer.parseInt("902F", 16)) ||
                         (iCommand >= Integer.parseInt("9110", 16) &&
                          iCommand <= Integer.parseInt("916F", 16)) ||
                         (iCommand >= Integer.parseInt("A010", 16) &&
                          iCommand <= Integer.parseInt("A02F", 16)) ||
                         (iCommand >= Integer.parseInt("A110", 16) &&
                          iCommand <= Integer.parseInt("A16F", 16))) {
                    DataArea = DataArea.substring(6, DataArea.length()); //消去冻结时标：对当前电量数据冻结时标没意义
                    iCaptionCount = DataArea.length() / iLen; //数据项个数
                    if (sCommand.substring(3, 4).equals("F")) { //大项命令
                        iCommandType = 10;
                    } else {
                        iCommandType = 20;
                    }
                }
                //$8010..$802F,$8110..$8D6F,$B010..$B02F,$B110..$B16F,$B410..$B56F,$B810..$BD6F: //最大需量发生时间数据
                else if ((iCommand >= Integer.parseInt("8010", 16) &&
                          iCommand <= Integer.parseInt("802F", 16)) ||
                         (iCommand >= Integer.parseInt("8110", 16) &&
                          iCommand <= Integer.parseInt("8D6F", 16)) ||
                         (iCommand >= Integer.parseInt("B010", 16) &&
                          iCommand <= Integer.parseInt("B02F", 16)) ||
                         (iCommand >= Integer.parseInt("B110", 16) &&
                          iCommand <= Integer.parseInt("B16F", 16)) ||
                         (iCommand >= Integer.parseInt("B410", 16) &&
                          iCommand <= Integer.parseInt("B56F", 16)) ||
                         (iCommand >= Integer.parseInt("B810", 16) &&
                          iCommand <= Integer.parseInt("BD6F", 16))) {
                    iCaptionCount = DataArea.length() / iLen; //数据项个数
                    if (sCommand.substring(3, 4).equals("F")) { //大项命令
                        iCommandType = 10;
                    } else {
                        iCommandType = 20;
                    }
                    //当前数据
                    if ((iCommand >= Integer.parseInt("B010", 16) &&
                         iCommand <= Integer.parseInt("B02F", 16)) ||
                        (iCommand >= Integer.parseInt("B110", 16) &&
                         iCommand <= Integer.parseInt("B16F", 16))) {
                        bIsHistoryData = false;
                    } else { //前几次数据都要转换成历史数据
                        bIsHistoryData = true;
                    }
                }
                //$C321..C32F//区起始日期及日时段表号
                //$C331..C3AF//时段起始时间及费率号
                else if ((iCommand >= Integer.parseInt("C321", 16) &&
                          iCommand <= Integer.parseInt("C32F", 16)) ||
                         (iCommand >= Integer.parseInt("C331", 16) &&
                          iCommand <= Integer.parseInt("C3AF", 16))) {
                    iCaptionCount = DataArea.length() / iLen; //数据项个数
                    if (sCommand.substring(3, 4).equals("F")) { //大项命令
                        iCommandType = 11;
                    } else {
                        iCommandType = 20;
                    }
                }
                //断相、失压、失流、开盖、编程记录数据
                else if ((iCommand >= Integer.parseInt("B330", 16) &&
                          iCommand <= Integer.parseInt("B34F", 16)) ||
                         (iCommand >= Integer.parseInt("B370", 16) &&
                          iCommand <= Integer.parseInt("B38F", 16)) ||
                         (iCommand >= Integer.parseInt("B3B0", 16) &&
                          iCommand <= Integer.parseInt("B3CF", 16)) ||
                         (iCommand >= Integer.parseInt("B730", 16) &&
                          iCommand <= Integer.parseInt("B74F", 16)) ||
                         (iCommand >= Integer.parseInt("B750", 16) &&
                          iCommand <= Integer.parseInt("B77F", 16)) ||
                         (iCommand >= Integer.parseInt("E000", 16) &&
                          iCommand <= Integer.parseInt("E02F", 16)) ||
                         (iCommand >= Integer.parseInt("E100", 16) &&
                          iCommand <= Integer.parseInt("E12F", 16)) ||
                         (iCommand >= Integer.parseInt("E200", 16) &&
                          iCommand <= Integer.parseInt("E22F", 16))) {
                    iCaptionCount = 1; //数据项个数(没有块命令)
                    iCommandType = 20;
                } else if (sCommand.equals("C117")) { //冻结次数、冻结时间、冻结日期               2006-4-27 maoyihua
                    iCaptionCount = 1; //数据项个数(没有块命令)
                    iCommandType = 20;
                } else if (sCommand.substring(0, 3).equals("C02")) { //告警状态字
                    if (sCommand.substring(3, 4).equals("F")) { //大项命令一般是
                        iCaptionCount = 7;
                        iCommandType = 10;
                    } else { //小项命令
                        iCaptionCount = 1;
                        iCommandType = 20;
                    }
                    if (FrameType == 10) { //召测返回
                        DataListInfo.DataType = 10; //普通数据
                    } else { //主动上送的
                        DataListInfo.DataType = 30; //告警数据
                    }
                } else if (sCommand.substring(3, 4).equals("F") &&
                           CommandInfo.GetFDataItemCount() > 1) { //大项命令
                    iCommandType = 12;

                } else {
                    if (sCommand.substring(0, 1).equals("D")) { //曲线数据
                        iCommandType = 40;
                    } else { //不是曲线数据,且没有块命令的小项命令
                        iCaptionCount = 1; //数据项个数
                        iCommandType = 30;
                    }
                }
                if (iCommandType == 40) { //曲线负荷数据
                    sDateTime = DataSwitch.ReverseStringByByte(DataArea.
                            substring(0, 10));
                    sDateTime = "20" + sDateTime + "00"; //起始时间
                    int iIncreaseNo = Integer.parseInt(DataArea.substring(12,
                            14) + DataArea.substring(10, 12)); //时间间隔
                    iCaptionCount = Integer.parseInt(DataArea.substring(14, 16),
                            16); //数据点数
                    DataArea = DataArea.substring(16, DataArea.length());
                    for (int i = 0; i < iCaptionCount; i++) {
                        ((SFE_DataItemInfo) (CommandInfo.DataItemInfoList.get(0))).
                                SetFPhysicalData(DataArea.substring(0, iLen));
                        if (((SFE_DataItemInfo) (CommandInfo.DataItemInfoList.
                                                 get(0))).
                            PhysicalDataToLogicData()) {
                            sLogicData = ((SFE_DataItemInfo) (CommandInfo.
                                    DataItemInfoList.get(0))).GetFLogicData();
                            HistoryData.DataItemAdd(sCommand, sLogicData);
                            HistoryData.SetMeasuredPointNo(0);
                            HistoryData.SetMeasuredPointType(10);
                            HistoryData.SetTaskNo(GluMethodAmmeter.
                                                  GetSelfDefineTaskNo(105,
                                    sCommand, TianJinTaskInfoList)); //曲线数据任务号
                            HistoryData.SetTaskDateTime(DataSwitch.
                                    IncreaseDateTime(sDateTime, iIncreaseNo * i,
                                    2));
                            DataListInfo.DataList.add(HistoryData);
                            HistoryData = new SFE_HistoryData();
                        }
                        DataArea = DataArea.substring(iLen, DataArea.length());
                    }
                    if (DataArea.equals("")) {
                        DataListInfo.DataType = 20; //历史数据
                    } else {
                        DataListInfo.ExplainResult = 50020; //传入数据长度非法
                    }
                } else if (iCommandType == 12) { //不需要通配符匹配的大项命令
                    NormalData = GluMethodAmmeter.ExplainNormalPhysicalData(
                            DataArea, CommandOrder, CommandInfoList, 0, 0);
                    DataArea = DataArea.substring(iLen, DataArea.length());
                    if (DataArea.equals("") || DataArea.equals("AA")) { //对于组成数据集合的数据块间要求用分割符AAH进行分割（单个数据块传输结束后也要有AA）。
                        DataListInfo.DataList.add(NormalData);
                    } else {
                        DataListInfo.ExplainResult = 50020; //传入数据长度非法
                    }
                    iCaptionCount = CommandInfo.GetFDataItemCount();

                } else {
                    for (int i = 0; i < iCaptionCount; i++) {
                        if (bIsHistoryData) { //转换成历史数据
                            ((SFE_DataItemInfo) (CommandInfo.DataItemInfoList.
                                                 get(0))).SetFPhysicalData(
                                    DataArea.substring(0, iLen));
                            sDateTime = DataSwitch.ReverseStringByByte(
                                    sDateTime);
                            sDateTime = DataSwitch.StrStuff("0", 10, sDateTime,
                                    20);
                            SimpleDateFormat formatter = new SimpleDateFormat(
                                    "yyyy");
                            String sYear = formatter.format(Calendar.
                                    getInstance().getTime());
                            sDateTime = sYear + sDateTime;
                            HistoryData.SetTaskDateTime(sDateTime);
                            HistoryData.SetMeasuredPointNo(NormalData.
                                    GetMeasuredPointNo());
                            HistoryData.SetMeasuredPointType(NormalData.
                                    GetMeasuredPointType());
                            HistoryData.SetTaskNo(GluMethodAmmeter.
                                                  GetSelfDefineTaskNo(105,
                                    sCommand, TianJinTaskInfoList)); //曲线数据任务号
                        } else {
                            ((SFE_DataItemInfo) (CommandInfo.DataItemInfoList.
                                                 get(0))).SetFPhysicalData(
                                    sDateTime + DataArea.substring(0, iLen));
                        }

                        if (((SFE_DataItemInfo) (CommandInfo.DataItemInfoList.
                                                 get(0))).
                            PhysicalDataToLogicData()) {
                            sLogicData = ((SFE_DataItemInfo) (CommandInfo.
                                    DataItemInfoList.get(0))).GetFLogicData();
                            if (iCommandType == 10) { //大项命令(0~E)
                                sDataCaption = ((SFE_DataItemInfo) (CommandInfo.
                                        DataItemInfoList.get(0))).
                                               GetFDataCaption().substring(0, 3) +
                                               Integer.toString(i, 16);
                            } else if (iCommandType == 11) { //大项命令(1~E)
                                sDataCaption = ((SFE_DataItemInfo) (CommandInfo.
                                        DataItemInfoList.get(0))).
                                               GetFDataCaption().substring(0, 3) +
                                               Integer.toString(i + 1, 16);
                            } else if (iCommandType == 20) { //有编码规律的小项命令
                                sDataCaption = ((SFE_DataItemInfo) (CommandInfo.
                                        DataItemInfoList.get(0))).
                                               GetFDataCaption().substring(0, 3) +
                                               sCommand.substring(3, 4);
                            } else if (iCommandType == 30) { //没有编码规律的小项命令
                                sDataCaption = ((SFE_DataItemInfo) (CommandInfo.
                                        DataItemInfoList.get(0))).
                                               GetFDataCaption();
                            }
                            if (bIsHistoryData) { //转换成历史数据
                                HistoryData.DataItemAdd(sDataCaption,
                                        sLogicData);
                            } else {
                                NormalData.DataItemAdd(sDataCaption, sLogicData);
                            }
                        }
                        DataArea = DataArea.substring(iLen, DataArea.length());
                    }
                    if (DataArea.equals("") || DataArea.equals("AA")) { //对于组成数据集合的数据块间要求用分割符AAH进行分割（单个数据块传输结束后也要有AA）。
                        if (bIsHistoryData) { //历史数据
                            DataListInfo.DataType = 20; //历史数据
                            DataListInfo.DataList.add(HistoryData);
                        } else {
                            DataListInfo.DataList.add(NormalData);
                        }
                    } else {
                        DataListInfo.ExplainResult = 50020; //传入数据长度非法
                    }
                }
                if (iCaptionCount == 0) { //命令在规约里不支持
                    DataListInfo.ExplainResult = 50016; //命令在规约里不支持
                }
            } catch (Exception e) {
                DataListInfo.ExplainResult = 50020; //传入数据长度非法
                if (iCommandType == 40) {
                    DataListInfo.DataList.add(HistoryData);
                } else {
                    DataListInfo.DataList.add(NormalData);
                }
                Glu_ConstDefine.Log1.WriteLog(
                        "ErrorFun:AmmeterFrameExplain__ExlpainTianJinAmmeterCommand();Error:" +
                        e.toString());
            }
        } finally {
        }
        return DataListInfo;
    }

 //   @SuppressWarnings(value="unchecked")  //本函数的DataListInfo.DataList数需要用到泛型，此处去掉相关警告
    public SFE_DataListInfo ExplainTianJinAmmeterFrame(String AmmeterFrame) { //天津645电表帧解释
        SFE_DataListInfo DataListInfo = new SFE_DataListInfo();
        SFE_NormalData NormalData = new SFE_NormalData();
        SFE_SetResultData SetResultData = new SFE_SetResultData();
        try {
            try {
                String sDataArea = "";
                String sTemp = "";
                String sCommand = "";
                String sDataCaption = "";
                String sMeasuredAdd = "";
                String sKZM = "";
                int iLen = 0;
                SPE_CommandInfoList CommandInfoList = new SPE_CommandInfoList();
                boolean bIsFind = false;
                int iAmmeterProtocol = SearchAmmeterFrame(AmmeterFrame);
                if (iAmmeterProtocol != 0) { //找到某种电表规约
                    if (iAmmeterProtocol == 13) { //天津645规约
                        AmmeterFrame = DataSwitch.SearchStr("68", AmmeterFrame); //搜索以68开头的电表帧,电表帧前有可能有间隔符FE
                        sMeasuredAdd = DataSwitch.ReverseStringByByte(
                                AmmeterFrame.substring(2, 12)); //电表地址
                        sMeasuredAdd = DataSwitch.StrFilter("A", true,
                                sMeasuredAdd); //过滤字符A
                        sKZM = AmmeterFrame.substring(16, 18); //控制码
                        if (sKZM.substring(0, 1).equals("9") ||
                            sKZM.substring(0, 1).equals("C") ||
                            sKZM.substring(0, 1).equals("D") ||
                            sKZM.equals("84") || sKZM.equals("8A")) { //异常应答或设置返回
                            SetResultData.SetMeasuredPointNo(0);
                            SetResultData.SetMeasuredPointType(10);
                            SetResultData.SetMeasuredAdd(sMeasuredAdd);
                            if (sKZM.equals("84") || sKZM.equals("8A") ||
                                sKZM.substring(0, 1).equals("9")) { //设置成功
                                SetResultData.DataItemAdd("000100", 0);
                                DataListInfo.DataType = 40;
                                DataListInfo.DataList.add(SetResultData);
                            } else { //异常返回
                                sTemp = AmmeterDataSwitch(AmmeterFrame.
                                        substring(20, 22), "-"); //错误代码
                                if (sKZM.equals("C1")) { //读取异常返回
                                    NormalData.SetMeasuredPointNo(0);
                                    NormalData.SetMeasuredPointType(10);
                                    NormalData.SetMeasuredAdd(sMeasuredAdd);
                                    NormalData.DataItemAdd("H021", sTemp);
                                    DataListInfo.DataType = 10;
                                    DataListInfo.DataList.add(NormalData);
                                } else {
                                    SetResultData.DataItemAdd("H021",
                                            Integer.parseInt(sTemp, 16));
                                    DataListInfo.DataType = 40;
                                    DataListInfo.DataList.add(SetResultData);
                                }
                            }
                            DataListInfo.ExplainResult = 0;
                        } else { //读取返回
                            iLen = Integer.parseInt(AmmeterFrame.substring(18,
                                    20), 16) * 2; //数据区长度
                            sDataArea = AmmeterFrame.substring(20, 20 + iLen);
                            sDataArea = AmmeterDataSwitch(sDataArea, "-"); //数据区减33H
                            if (sDataArea.length() > 4) { //命令标识至少4位
                                sDataCaption = sDataArea.substring(2, 4) +
                                               sDataArea.substring(0, 2); //得到电表数据项标识
                                sDataArea = sDataArea.substring(4,
                                        sDataArea.length());
                                CommandInfoList =
                                        AmmeterTianJinCommandInfoListDX; //先匹配大项命令
                                for (int i = 0; i < CommandInfoList.
                                             GetFCommandCount(); i++) {
                                    sCommand = ((SFE_CommandInfo) (
                                            CommandInfoList.CommandInfoList.get(
                                            i))).GetFProtocolCommand();
                                    if (sDataCaption.equals(sCommand)) { //查找是否存在当前命令
                                        bIsFind = true;
                                        DataListInfo =
                                                ExplainTianJinAmmeterCommand(
                                                sDataCaption, sMeasuredAdd,
                                                sDataArea, i, CommandInfoList,
                                                10);
                                        break;
                                    }
                                }
                                if (bIsFind == false) {
                                    CommandInfoList =
                                            AmmeterTianJinCommandInfoListXX; //再匹配小项命令
                                    for (int i = 0;
                                                 i < CommandInfoList.
                                                 GetFCommandCount(); i++) {
                                        sCommand = ((SFE_CommandInfo) (
                                                CommandInfoList.CommandInfoList.
                                                get(i))).GetFProtocolCommand();
                                        //小项命令匹配有两种:全部匹配;前三位匹配且数据库录入的命令是以X结尾的(用901X来表示9010~901E)
                                        if (sDataCaption.equals(sCommand) ||
                                            (sDataCaption.substring(0, 3).
                                             equals(sCommand.substring(0, 3)) &&
                                             sCommand.substring(3,
                                                4).equals("X"))) { //查找是否存在当前命令
                                            bIsFind = true;
                                            DataListInfo =
                                                    ExplainTianJinAmmeterCommand(
                                                    sDataCaption, sMeasuredAdd,
                                                    sDataArea, i,
                                                    CommandInfoList, 10);
                                            break;
                                        }
                                    }
                                }
                            }
                            if (bIsFind == false) { //无法辨别的电表帧或者命令不支持
                                NormalData.SetMeasuredPointNo(0);
                                NormalData.SetMeasuredPointType(10);
                                NormalData.DataItemAdd("HL03", AmmeterFrame);
                                DataListInfo.DataList.add(NormalData);
                                DataListInfo.DataType = 10;
                                DataListInfo.ExplainResult = 50010; //数据项在规约里不能全部被支持
                            }
                        }
                    }
                }
            } catch (Exception e) {
                DataListInfo.ExplainResult = 50020; //传入数据长度非法
                Glu_ConstDefine.Log1.WriteLog(
                        "ErrorFun:AmmeterFrameExplain__ExplainTianJinAmmeterFrame();Error:" +
                        e.toString());
            }
        } finally {
        }
        return DataListInfo;
    }

 //   @SuppressWarnings(value="unchecked")  //本函数的DataListInfo.DataList数需要用到泛型，此处去掉相关警告
    public SFE_DataListInfo ExplainTianJinHistoryDataArea( //天津模块表任务主动上送数据
            String FrameDataArea, int TermialProtocolType, String ControlCode,
            int FrameType) { //帧数据区,终端规约,功能码,帧类型(主动上送还是召测返回)
        SFE_DataListInfo DataListInfo = new SFE_DataListInfo();
       // String sError = FrameDataArea;
        try {
            try {
                SFE_DataListInfo DataListInfoTemp = new SFE_DataListInfo();
                SFE_HistoryData[] HistoryDataListTemp = new SFE_HistoryData[1];
                HistoryDataListTemp[0] = new SFE_HistoryData();
                ArrayList HistoryDataList = new ArrayList();
                SPE_CommandInfoList CommandInfoList = new SPE_CommandInfoList();
                ArrayList <String>CommandList = new ArrayList<String>();
                SFE_AlarmData AlarmData = new SFE_AlarmData();
                String sDataCaption = "", sCommand = "", sDataArea = "";
                String sDateTime = DataSwitch.ReverseStringByByte(FrameDataArea.
                        substring(0, 10));
                sDateTime = "20" + sDateTime + "00"; //起始时间
                FrameDataArea = FrameDataArea.substring(10,
                        FrameDataArea.length());
                int iLen = 0;
                while (FrameDataArea.length() > 0) {
                    boolean bIsFind = false;
                    iLen = Integer.parseInt(FrameDataArea.substring(0, 2), 16) *
                           2; //数据长度
                    FrameDataArea = FrameDataArea.substring(2,
                            FrameDataArea.length());
                    sDataCaption = FrameDataArea.substring(2, 4) +
                                   FrameDataArea.substring(0, 2); //数据项标识
                    CommandList.add(sDataCaption); //上送帧的命令列表，用来匹配数据库的任务配置信息
                    CommandInfoList = AmmeterTianJinCommandInfoListDX; //先匹配大项命令
                    for (int i = 0; i < CommandInfoList.GetFCommandCount(); i++) {
                        sCommand = ((SFE_CommandInfo) (CommandInfoList.
                                CommandInfoList.get(i))).GetFProtocolCommand();
                        if (sDataCaption.equals(sCommand)) { //查找是否存在当前命令
                            bIsFind = true;
                            sDataArea = FrameDataArea.substring(4, iLen);
                            //大项命令带有一个字节的间隔符(AA),由ExplainTianJinAmmeterCommand中判断
                            //电能表告警事件上送没有间隔符(AA)的
                            FrameDataArea = FrameDataArea.substring(iLen,
                                    FrameDataArea.length());
                            DataListInfoTemp = ExplainTianJinAmmeterCommand(
                                    sDataCaption, "", sDataArea, i,
                                    CommandInfoList, FrameType);
                            break;
                        }
                    }
                    if (bIsFind == false) {
                        CommandInfoList = AmmeterTianJinCommandInfoListXX; //再匹配小项命令
                        for (int i = 0; i < CommandInfoList.GetFCommandCount();
                                     i++) {
                            sCommand = ((SFE_CommandInfo) (CommandInfoList.
                                    CommandInfoList.get(i))).
                                       GetFProtocolCommand();
                            int iLenTemp = 4 + 2 *
                                           ((SFE_CommandInfo) (CommandInfoList.
                                    CommandInfoList.get(i))).GetFCommandLen(); //取命令长度+命令标识自身的2个字节
                            //小项命令匹配有两种:全部匹配;前三位匹配且数据库录入的命令是以X结尾的(用901X来表示9010~901E)
                            if (sDataCaption.equals(sCommand) ||
                                (sDataCaption.substring(0,
                                    3).equals(sCommand.substring(0, 3)) &&
                                 sCommand.substring(3, 4).equals("X"))) { //查找是否存在当前命令
                                bIsFind = true;
                                if (iLen < iLenTemp) { //华隆的天津模块表上送任务帧的数据块长度有可能会有错，比如需量就少个字节，所以需要特殊处理
                                    iLen = iLenTemp;
                                }
                                sDataArea = FrameDataArea.substring(4, iLen);
                                FrameDataArea = FrameDataArea.substring(iLen,
                                        FrameDataArea.length());
                                DataListInfoTemp = ExplainTianJinAmmeterCommand(
                                        sDataCaption, "", sDataArea, i,
                                        CommandInfoList, FrameType);
                                break;
                            }
                        }
                    }
                    if (bIsFind) { //找到命令
                        DataListInfo.ExplainResult = DataListInfoTemp.
                                ExplainResult;
                        if (DataListInfoTemp.DataType == 10) { //普通数据要转换为任务数据
                            DataListInfo.DataType = 20;
                            for (int i = 0; i < DataListInfoTemp.DataList.size();
                                         i++) {
                                HistoryDataListTemp[0].SetMeasuredPointNo(0);
                                HistoryDataListTemp[0].SetMeasuredPointType(10);
                                HistoryDataListTemp[0].SetTaskDateTime(
                                        sDateTime);
                                HistoryDataListTemp[0].SetTaskNo(0);
                                for (int j = 0; j <
                                             ((SFE_NormalData) DataListInfoTemp.
                                              DataList.get(i)).DataItemList.
                                             size(); j++) {
                                    HistoryDataListTemp[0].DataItemList.add(((
                                            SFE_NormalData) DataListInfoTemp.
                                            DataList.get(i)).DataItemList.get(j));
                                    HistoryDataListTemp[0].DataItemCountAdd();
                                }
                            }
                        } else if (DataListInfoTemp.DataType == 30) { //告警数据
                            for (int i = 0; i < DataListInfoTemp.DataList.size();
                                         i++) {
                                AlarmData.SetMeasuredPointNo(0);
                                AlarmData.SetMeasuredPointType(10);
                                AlarmData.SetAlarmDateTime(sDateTime);
                                AlarmData.SetAlarmCode("状态字");
                                for (int j = 0; j <
                                             ((SFE_NormalData) DataListInfoTemp.
                                              DataList.get(i)).DataItemList.
                                             size(); j++) {
                                    AlarmData.DataItemList.add(((SFE_NormalData)
                                            DataListInfoTemp.DataList.get(i)).
                                            DataItemList.get(j));
                                    AlarmData.DataItemCountAdd();
                                }
                            }
                            DataListInfo.DataList.add(AlarmData);
                            DataListInfo.DataType = 30;
                            break;
                        }
                        if (DataListInfoTemp.ExplainResult != 0) { //如果解释出错就退出
                            break;
                        }
                    } else {
                        DataListInfo.ExplainResult = 50010; //数据项在规约里不能全部被支持
                        break;
                    }
                }
                if (DataListInfo.DataType == 20) { //任务数据要按数据库配置信息来匹配任务
                    HistoryDataList = GluMethodAmmeter.
                                      SearchAndMatchingTaskInfoList(CommandList,
                            HistoryDataListTemp, 1, TianJinTaskInfoList);
                    if (HistoryDataList.size() == 0) { //没找到相匹配的任务信息
                        DataListInfo.ExplainResult = 50018; //未找到任务配置信息
                        HistoryDataList.add(HistoryDataListTemp[0]);
                    }
                    DataListInfo.DataList = HistoryDataList;
                    DataListInfo.DataType = 20;
                }
            } catch (Exception e) {
                Glu_ConstDefine.Log1.WriteLog(
                        "ErrorFun:AmmeterFrameExplain__ExplainTianJinHistoryDataArea();Error:" +
                        e.toString());
            }
        } finally {
        }
        return DataListInfo;
    }

    public int SearchAmmeterFrame(String sAmmeterFrame) { //检验电表帧是浙规还部规
        int iResult = 0;
        try {
            try {
                String sTemp = "";
                String sCheck = "";
                int iCheck = 0;
                sAmmeterFrame = DataSwitch.SearchStr("68", sAmmeterFrame); //搜索以68开头的电表帧,电表帧前有可能有间隔符FE
                int iLen = (sAmmeterFrame.trim()).length();
                if ((sAmmeterFrame.length() % 2 == 0) && (iLen > 12)) {
                    if (iLen >= 14) {
                        if ((sAmmeterFrame.substring(0, 2).equals("68")) &&
                            (sAmmeterFrame.substring(6, 8).equals("68")) &&
                            (sAmmeterFrame.substring(iLen - 2,
                                iLen).equals("0D"))) { //浙规
                            sTemp = sAmmeterFrame.substring(8, iLen - 4); //校验区
                            for (int i = 0; i < sTemp.length() / 2; i++) {
                                iCheck = iCheck +
                                         Integer.parseInt(sTemp.
                                        substring(i * 2, i * 2 + 2), 16);
                            }
                            iCheck = iCheck % 256; //重新计算得到的校验码
                            sCheck = (Integer.toString(iCheck, 16)).toUpperCase();
                            if (sCheck.equals(sAmmeterFrame.substring(iLen - 4,
                                    iLen - 2))) {
                                iResult = 20; //浙规
                            }
                        }
                        if ((iLen >= 24) && (iResult != 20)) {
                            if ((sAmmeterFrame.substring(0, 2).equals("68")) &&
                                (sAmmeterFrame.substring(14, 16).equals("68"))) { //部规
                                int iContentLen = Integer.parseInt(
                                        sAmmeterFrame.substring(18, 20), 16); //数据区长度
                                sAmmeterFrame = sAmmeterFrame.substring(0, 20) +
                                                sAmmeterFrame.substring(20,
                                        iContentLen * 2 + 24); //取完整的电表帧，可以消去多余字节
                                if (sAmmeterFrame.substring(sAmmeterFrame.
                                        length() - 2, sAmmeterFrame.length()).
                                    equals("16")) {
                                    sTemp = sAmmeterFrame.substring(0,
                                            sAmmeterFrame.length() - 4); //校验区
                                    for (int i = 0; i < sTemp.length() / 2; i++) {
                                        iCheck = iCheck +
                                                 Integer.parseInt(sTemp.
                                                substring(i * 2, i * 2 + 2), 16);
                                    }
                                    iCheck = iCheck % 256; //重新计算得到的校验码
                                    sCheck = DataSwitch.IntToHex("" + iCheck,
                                            "00");
                                    if (sCheck.equals(sAmmeterFrame.substring(
                                            sAmmeterFrame.length() - 4,
                                            sAmmeterFrame.length() - 2))) {
                                        iResult = Glu_ConstDefine.GY_DB_QG97; //部规
                                        if (sAmmeterFrame.substring(12,
                                                14).equals("F1")) { //天津645电表规约标志位
                                            iResult = 13; //天津645电表规约
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                Glu_ConstDefine.Log1.WriteLog(
                        "ErrorFun:AmmeterFrameExplain__SearchAmmeterFrame();Error:" +
                        e.toString());
            }
        } finally {
        }
        return iResult;
    }

    public String AmmeterDataSwitch(String sStr, String sSign) {
        String sResult = "";
        try {
            try {
                String sTemp = "";
                int iTemp = 0;
                for (int i = 0; i < sStr.length() / 2; i++) {
                    if (sSign.equals("-")) { //减33H
                        iTemp = (Integer.parseInt(sStr.substring(2 * i,
                                2 * i + 2), 16) - 51);
                        if (iTemp < 0) {
                            iTemp = 256 + iTemp;
                        }
                    } else { //加33H
                        iTemp = (Integer.parseInt(sStr.substring(2 * i,
                                2 * i + 2), 16) + 51) % 256;
                    }
                    sTemp = DataSwitch.IntToHex(Integer.toString(iTemp), "00");
                    sResult = sResult + sTemp;
                }
            } catch (Exception e) {
                Glu_ConstDefine.Log1.WriteLog(
                        "ErrorFun:AmmeterFrameExplain__AmmeterDataSwitch();Error:" +
                        e.toString());
            }
        } finally {
        }
        return sResult;
    }
}

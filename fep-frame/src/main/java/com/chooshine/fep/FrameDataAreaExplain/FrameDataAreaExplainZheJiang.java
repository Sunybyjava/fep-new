package com.chooshine.fep.FrameDataAreaExplain;

import java.net.URI;
import java.util.ArrayList;
import com.chooshine.fep.ConstAndTypeDefine.*;

public class FrameDataAreaExplainZheJiang {//浙规数据区解帧类
	  //存储方式
	  private final int CCFS_BCD=10 ; //BCD方式
	  private final int CCFS_HEX=20 ; //十六进制方式
	  //private final int CCFS_ASC=30 ; //ASCII码方式

	  //存储顺序
	  //private final int CCSX_SX=10;  //顺序存储
	  private final int CCSX_NX=20;  //逆序存储

	  //数据类型
	  private final int c_Float     =10;//浮点型
	  private final int c_DateTime  =20;//日期型
	  private final int c_String    =30;//字符型
	  //private final int c_NOLenStr  =40;//不定长字符型
	  //private final int c_Complex   =50;//复合数据类型
	  //private final int c_FloatEx   =60;//特殊浮点型（对应国网规范格式2,带幂部）
	  //private final int c_FloatFH   =70;//带正负号的浮点型 (0为正号或是上浮，1为负号或是下浮)
	  //private final int c_DateTimeXQ=80;//带星期的日期型2004-10-10 10：10：10#4
	  //private final int c_IntDWFH   =90;//带单位标志和正负号的整型值串
	  //private final int c_IntZJDWFH =91;//带追加,单位标志和正负号的整型值串

	  SPE_CommandInfoList TermialZheJiangCommandInfoList=new SPE_CommandInfoList();             //终端浙江命令信息队列
	  SPE_CommandInfoList TermialZheJiangExCommandInfoList=new SPE_CommandInfoList();           //终端浙江增补命令信息队列
	  SPE_CommandInfoList TermialZheJiangManufacturerCommandInfoList=new SPE_CommandInfoList(); //终端浙江厂家自定义命令信息队列
	  SPE_CommandInfoList TermialZheJiangBDZCommandInfoList=new SPE_CommandInfoList();          //终端浙江变电站命令信息队列
	  SPE_CommandInfoList TermialZheJiangMKBCommandInfoList=new SPE_CommandInfoList();          //终端浙江模块表命令信息队列
	  SPE_CommandInfoList TermialJiangXiPBCommandInfoList=new SPE_CommandInfoList();            //终端江西配变命令信息队列
	  SPE_CommandInfoList TermialZheJiangHLJC2CommandInfoList=new SPE_CommandInfoList();        //终端华隆集抄(两层,采集器层透明)命令信息队列
	  SPE_CommandInfoList TermialGuangDongCommandInfoList=new SPE_CommandInfoList();            //终端广东命令信息队列
	  SPE_CommandInfoList TermialHuaLongJCCommandInfoList=new SPE_CommandInfoList();            //终端华隆集抄命令信息队列

	  SPE_TaskInfoList ZheJiangTaskInfoList=new SPE_TaskInfoList();      //浙规任务信息队列
	  SPE_TaskInfoList ZheJiangExTaskInfoList=new SPE_TaskInfoList();    //浙规增补任务信息队列
	  SPE_TaskInfoList ZheJiangBDZTaskInfoList=new SPE_TaskInfoList();   //浙规变电站任务信息队列
	  SPE_TaskInfoList ZheJiangMKBTaskInfoList=new SPE_TaskInfoList();   //浙规模块表任务信息队列
	  SPE_TaskInfoList JiangXiPBTaskInfoList=new SPE_TaskInfoList();     //江西配变任务信息队列
	  SPE_TaskInfoList ZheJiangHLJC2TaskInfoList=new SPE_TaskInfoList(); //华隆集抄(两层,采集器层透明)信息队列
	  SPE_TaskInfoList GuangDongTaskInfoList=new SPE_TaskInfoList();     //广东任务信息队列
	  SPE_TaskInfoList HuaLongJCTaskInfoList=new SPE_TaskInfoList();     //华隆集抄任务信息队列

	  FrameDataAreaExplainGluMethod GluMethodZheJiang=new FrameDataAreaExplainGluMethod();//数据区解析公用类
	  
	  public FrameDataAreaExplainZheJiang(String path) {
	    try {
	      InitialList(path,null);
	    }
	    catch (Exception e) {
	    	Glu_ConstDefine.Log1.WriteLog(e.toString());
	    }
	  }
	  public FrameDataAreaExplainZheJiang(URI ur) {
		    try {
		      InitialList(null,ur);
		    }
		    catch (Exception e) {
		    	Glu_ConstDefine.Log1.WriteLog(e.toString());
		    }
		  }
	  public boolean InitialList(String path,URI ur){
	    boolean Result=false;
	    try {
	      try {
	    	  Glu_DataAccess DataAccess;
	    	  if (ur == null){
	    		  DataAccess=new Glu_DataAccess(path);
	    	  }
	    	  else{
	    		  DataAccess=new Glu_DataAccess(ur);
	    	  }
	        DataAccess.LogIn(0);
	        if (Glu_ConstDefine.TerminalZheJiangSupport){
	          TermialZheJiangCommandInfoList=DataAccess.GetCommandInfoList(Glu_ConstDefine.GY_ZD_ZHEJIANG);  //初始化终端浙江命令信息队列
	          TermialZheJiangExCommandInfoList=DataAccess.GetCommandInfoList(Glu_ConstDefine.GY_ZD_ZJZB0404);  //初始化终端浙江增补命令信息队列
	      //    TermialZheJiangManufacturerCommandInfoList=DataAccess.GetCommandInfoList(Glu_ConstDefine.GY_ZD_ZJYXYDZB);  //初始化终端浙江自定义命令信息队列
	          ZheJiangTaskInfoList=DataAccess.GetTaskInfoList(Glu_ConstDefine.GY_ZD_ZHEJIANG);    //初始化任务信息队列
	          ZheJiangExTaskInfoList=DataAccess.GetTaskInfoList(Glu_ConstDefine.GY_ZD_ZJZB0404);    //初始化任务信息队列
	        }
	      /*  if (Glu_ConstDefine.TerminalGuangDongSupport){
	          TermialGuangDongCommandInfoList=DataAccess.GetCommandInfoList(Glu_ConstDefine.GY_ZD_GUANGDONG);  //初始化终端广东命令信息队列
	          GuangDongTaskInfoList=DataAccess.GetTaskInfoList(Glu_ConstDefine.GY_ZD_GUANGDONG);    //初始化任务信息队列
	          if (!Glu_ConstDefine.TerminalZheJiangSupport){//广东规约相当于浙江规约的增补规约
	            TermialZheJiangCommandInfoList=DataAccess.GetCommandInfoList(Glu_ConstDefine.GY_ZD_ZHEJIANG);              //初始化终端浙江命令信息队列
	            TermialZheJiangManufacturerCommandInfoList=DataAccess.GetCommandInfoList(Glu_ConstDefine.GY_ZD_ZJYXYDZB); //初始化终端浙江自定义命令信息队列
	          }
	        }
	        
	        if (Glu_ConstDefine.TerminalZheJiangBDZSupport){
	          TermialZheJiangBDZCommandInfoList=DataAccess.GetCommandInfoList(Glu_ConstDefine.GY_ZD_ZJBDZ);  //初始化终端变电站命令信息队列
	          ZheJiangBDZTaskInfoList=DataAccess.GetTaskInfoList(Glu_ConstDefine.GY_ZD_ZJBDZ);    //初始化任务信息队列
	          if (!Glu_ConstDefine.TerminalZheJiangSupport){//变电站相当于浙江规约的增补规约
	            TermialZheJiangCommandInfoList=DataAccess.GetCommandInfoList(Glu_ConstDefine.GY_ZD_ZHEJIANG);              //初始化终端浙江命令信息队列
	            TermialZheJiangManufacturerCommandInfoList=DataAccess.GetCommandInfoList(Glu_ConstDefine.GY_ZD_ZJYXYDZB); //初始化终端浙江自定义命令信息队列
	          }
	        }
	       if (Glu_ConstDefine.TerminalZheJiangMKBSupport){
	          TermialZheJiangMKBCommandInfoList=DataAccess.GetCommandInfoList(87);  //初始化终端模块表命令信息队列
	          ZheJiangMKBTaskInfoList=DataAccess.GetTaskInfoList(87);    //初始化任务信息队列
	          if (!Glu_ConstDefine.TerminalZheJiangSupport){//模块表相当于浙江规约的增补规约
	            TermialZheJiangCommandInfoList=DataAccess.GetCommandInfoList(80);              //初始化终端浙江命令信息队列
	            TermialZheJiangManufacturerCommandInfoList=DataAccess.GetCommandInfoList(81); //初始化终端浙江自定义命令信息队列
	          }
	        }
	        if (Glu_ConstDefine.TerminalJiangXiPBSupport){
	          TermialJiangXiPBCommandInfoList=DataAccess.GetCommandInfoList(88);  //初始化终端江西配变命令信息队列
	          JiangXiPBTaskInfoList=DataAccess.GetTaskInfoList(88);    //初始化任务信息队列
	          if (!Glu_ConstDefine.TerminalZheJiangSupport){//江西配变相当于浙江规约的增补规约
	            TermialZheJiangCommandInfoList=DataAccess.GetCommandInfoList(80);              //初始化终端浙江命令信息队列
	            TermialZheJiangManufacturerCommandInfoList=DataAccess.GetCommandInfoList(81); //初始化终端浙江自定义命令信息队列
	          }
	        }
	        if (Glu_ConstDefine.TerminalZheJiangHLJC2Support){
	          TermialZheJiangHLJC2CommandInfoList=DataAccess.GetCommandInfoList(111);  //初始化终端华隆集抄(两层,采集器层透明)命令信息队列
	          ZheJiangHLJC2TaskInfoList=DataAccess.GetTaskInfoList(111);              //初始化任务信息队列
	          if (!Glu_ConstDefine.TerminalZheJiangSupport){//华隆集抄(两层,采集器层透明)相当于浙江规约的增补规约
	            TermialZheJiangCommandInfoList=DataAccess.GetCommandInfoList(80);              //初始化终端浙江命令信息队列
	            TermialZheJiangManufacturerCommandInfoList=DataAccess.GetCommandInfoList(81); //初始化终端浙江自定义命令信息队列
	          }
	        }
	        if (Glu_ConstDefine.TerminalHuaLongJCSupport){
	          TermialHuaLongJCCommandInfoList=DataAccess.GetCommandInfoList(110);  //初始化终端华隆集抄命令信息队列
	          HuaLongJCTaskInfoList=DataAccess.GetTaskInfoList(110);    //初始化任务信息队列
	        }*/
	        DataAccess.LogOut(0);
	      }
	      catch (Exception e) {
	        Glu_ConstDefine.Log1.WriteLog("Func:FrameDataAreaExplainZheJiang__InitialList();Error:"+e.toString());
	      }
	    }
	    finally{
	    }
	    return Result;
	  }

	  public SFE_DataListInfo ExplainNormalDataAreaZheJiang(//普通数据区解释
	          String FrameDataArea,int TermialProtocolType,String ControlCode){//帧数据区,终端规约,控制码
	    SFE_DataListInfo DataListInfo=new SFE_DataListInfo();
	    ArrayList <SFE_NormalData>NormalDataList=new ArrayList<SFE_NormalData>();
	    int iNormalDataCount=0;
	    int iExplainResult=0;
	    try{
	      try{
	        SFE_NormalData NormalData=new SFE_NormalData();
	        SPE_CommandInfoList CommandInfoList=new SPE_CommandInfoList();
	        int[] MeasuredSignList;
	        if (ControlCode.equals("81")){//确认
	          if(TermialProtocolType==110){//华隆集抄
	            MeasuredSignList=GluMethodZheJiang.ExplainMeasuredPointList(FrameDataArea,TermialProtocolType);//得到测量点列表
	            if(MeasuredSignList.length==1&&MeasuredSignList[0]==0){//如果是终端参数对象标志只有两个字节
	              FrameDataArea=FrameDataArea.substring(4,FrameDataArea.length());
	            }
	            else {
	              FrameDataArea=FrameDataArea.substring(4+6*MeasuredSignList.length,FrameDataArea.length());
	            }
	          }
	          else{//浙规系列
	            MeasuredSignList=GluMethodZheJiang.ExplainMeasuredPointList(FrameDataArea.substring(0,16),TermialProtocolType);//得到测量点列表
	            FrameDataArea=FrameDataArea.substring(16,FrameDataArea.length());
	          }
	          iNormalDataCount=MeasuredSignList.length;//普通数据结构数取决于测量点数
	          SFE_NormalData[] NormalDataListTemp=new SFE_NormalData[iNormalDataCount];
	          for (int i=0;i<iNormalDataCount;i++){
	            NormalDataListTemp[i]=new SFE_NormalData();
	            NormalDataListTemp[i].SetMeasuredPointNo(MeasuredSignList[i]);
	            NormalDataListTemp[i].SetMeasuredPointType(10);
	          }
	          String sDataCaption="";
	          String sCommand="";
	          int iCommandLen=0;
	          int iCommandCount=0;
	          while(FrameDataArea.length()>0){
	            sDataCaption=FrameDataArea.substring(2,4)+FrameDataArea.substring(0,2);//命令标识
	            FrameDataArea=FrameDataArea.substring(4,FrameDataArea.length());
	            if (TermialProtocolType==110&&sDataCaption.equals("4021")){//华隆台区表中继抄表
	              NormalData.SetMeasuredPointNo(MeasuredSignList[0]);
	              NormalData.SetMeasuredPointType(10);
	              FrameDataArea=DataSwitch.StrStuff("0",2,Integer.toString(MeasuredSignList[0]),10)+FrameDataArea.substring(6,FrameDataArea.length());//测量点号+电表帧
	              NormalData.DataItemAdd("4021",FrameDataArea);
	              NormalDataList.add(NormalData);
	              break;
	            }
	            boolean bIsSearch=false;
	            if (TermialProtocolType==Glu_ConstDefine.GY_ZD_ZJZB0404
	            /*	||TermialProtocolType==Glu_ConstDefine.GY_ZD_ZJBDZ||
	            	TermialProtocolType==Glu_ConstDefine.GY_ZD_GUANGDONG||
	            	TermialProtocolType==Glu_ConstDefine.GY_ZD_ZJMKB||
	            	TermialProtocolType==88||TermialProtocolType==110||
	            	TermialProtocolType==111*/){
	            //  if (TermialProtocolType==Glu_ConstDefine.GY_ZD_ZJZB0404){//浙规增补
	                CommandInfoList=TermialZheJiangExCommandInfoList;
	               /* }
	              else if(TermialProtocolType==83) {//变电站规约
	                CommandInfoList=TermialZheJiangBDZCommandInfoList;
	              }
	              else if(TermialProtocolType==84) {//广东规约
	                CommandInfoList=TermialGuangDongCommandInfoList;
	              }
	              else if(TermialProtocolType==87) {//模块表规约
	                CommandInfoList=TermialZheJiangMKBCommandInfoList;
	              }
	              else if(TermialProtocolType==88) {//江西配变
	                CommandInfoList=TermialJiangXiPBCommandInfoList;
	              }
	              else if(TermialProtocolType==110){//华隆集抄
	                CommandInfoList=TermialHuaLongJCCommandInfoList;
	              }
	              else if(TermialProtocolType==111) {//华隆集抄(两层,采集器层透明)
	                CommandInfoList=TermialZheJiangHLJC2CommandInfoList;
	              }*/
	              iCommandCount=CommandInfoList.GetFCommandCount();
	              for (int j=0;j<iCommandCount;j++){
	                sCommand=((SFE_CommandInfo)(CommandInfoList.CommandInfoList.get(j))).GetFProtocolCommand();
	                if (sDataCaption.equals(sCommand)) {//查找是否存在当前命令
	                  iCommandLen=2*((SFE_CommandInfo)(CommandInfoList.CommandInfoList.get(j))).GetFCommandLen();//得到此命令的数据区字符长度=字节长度*2
	                  if (iCommandLen==0){//不定长类型长度为0
	                    iCommandLen=FrameDataArea.length();
	                  }
	                  if (iCommandLen<=FrameDataArea.length()){//如果数据长度合法
	                    bIsSearch=true;
	                    for (int i=0;i<iNormalDataCount;i++){
	                      NormalData=GluMethodZheJiang.ExplainNormalPhysicalData(FrameDataArea.substring(0,iCommandLen),j,CommandInfoList,TermialProtocolType,0);//解释该命令的物理数据
	                      FrameDataArea=FrameDataArea.substring(iCommandLen,FrameDataArea.length());//消去已经解释的命令数据长度
	                      for (int k=0;k<NormalData.DataItemList.size();k++){//把解释后的数据保存在对象数组里
	                        NormalDataListTemp[i].DataItemList.add(NormalData.DataItemList.get(k));
	                        NormalDataListTemp[i].DataItemCountAdd();
	                      }
	                    }
	                  }
	                  break;
	                }
	              }
	            }
	            if (bIsSearch==false&&TermialProtocolType!=110){//如果浙规增补或者广东没找到再用浙规解释;华隆集抄命令列表是独立的
	              CommandInfoList=TermialZheJiangCommandInfoList;
	              iCommandCount=CommandInfoList.GetFCommandCount();
	              for (int j=0;j<iCommandCount;j++){
	                sCommand=((SFE_CommandInfo)(CommandInfoList.CommandInfoList.get(j))).GetFProtocolCommand();
	                if (sDataCaption.equals(sCommand)) {//查找是否存在当前命令
	                  bIsSearch=true;
	                  iCommandLen=2*((SFE_CommandInfo)(CommandInfoList.CommandInfoList.get(j))).GetFCommandLen();//得到此命令的数据区字符长度=字节长度*2
	                  if (iCommandLen==0){//不定长类型长度为0
	                    iCommandLen=FrameDataArea.length();
	                  }
	                  if (iCommandLen<=FrameDataArea.length()){//如果数据长度合法
	                    for (int i=0;i<iNormalDataCount;i++){
	                      NormalData=GluMethodZheJiang.ExplainNormalPhysicalData(FrameDataArea.substring(0,iCommandLen),j,CommandInfoList,TermialProtocolType,0);//解释该命令的物理数据
	                      FrameDataArea=FrameDataArea.substring(iCommandLen,FrameDataArea.length());//消去已经解释的命令数据长度
	                      for (int k=0;k<NormalData.DataItemList.size();k++){//把解释后的数据保存在对象数组里
	                        NormalDataListTemp[i].DataItemList.add(NormalData.DataItemList.get(k));
	                        NormalDataListTemp[i].DataItemCountAdd();
	                      }
	                    }
	                  }
	                  else{//传入长度不够
	                    iExplainResult=50020;//传入数据长度非法
	                  }
	                  break;
	                }
	              }
	            }
	          }
	          for(int i=0;i<iNormalDataCount;i++){
	            NormalDataList.add(NormalDataListTemp[i]);
	          }
	        }
	        else if (ControlCode.substring(1,2).equals("F")){//厂家自定义命令
	          CommandInfoList=TermialZheJiangManufacturerCommandInfoList;
	          NormalData=ExplainManufacturerFrame(FrameDataArea);
	          NormalDataList.add(NormalData);
	        }
	        else if (ControlCode.equals("C1")){//否认
	          NormalData.SetMeasuredPointNo(0);
	          NormalData.SetMeasuredPointType(10);
	          NormalData.DataItemAdd("H020",FrameDataArea.substring(0,2));
	          NormalDataList.add(NormalData);
	        }
	        else{
	          iExplainResult=50017;//控制字在规约未定义
	        }
	      }
	      catch (Exception e) {
	        Glu_ConstDefine.Log1.WriteLog("Func:FrameDataAreaExplainZheJiang__ExplainNormalDataAreaZheJiang();Error:"+e.toString());
	      }
	    }
	    finally{
	    }
	    DataListInfo.DataType=10;
	    DataListInfo.ExplainResult=iExplainResult;
	    DataListInfo.DataList=NormalDataList;
	    return DataListInfo;
	  }
	  public SFE_DataListInfo ExplainHistoryDataAreaZheJiang(//历史数据区解释
	          String FrameDataArea,String TerminalAddress,int TermialProtocolType,String ControlCode){//帧数据区,终端规约,控制码
	    SFE_DataListInfo DataListInfo=new SFE_DataListInfo();
	    ArrayList <SFE_HistoryData>HistoryDataList=new ArrayList<SFE_HistoryData>();
	    String sError=FrameDataArea;
	    int iExplainResult=0;
	    //System.out.println("====++++++++++进入ExplainHistoryDataAreaZheJiang");
	    try{
	      try{
	        SFE_HistoryData HistoryData=new SFE_HistoryData();
	        int TaskNo=0,iMeasuredPointNo=0,iTaskIndex=0,
	            iTaskDataItemCount=0,iTaskType=0,
	            iTaskDataCount=0,iTaskFeqUnit=0,iTaskFeqCount=0;
	        String sDataCaption="",sLogicData="",sTaskDatetime="";
	        //SFE_NormalData NormalData=new SFE_NormalData();
	        SFE_NormalDataTemp NormalDataTemp=new SFE_NormalDataTemp();
	        SPE_CommandInfoList CommandInfoList=new SPE_CommandInfoList();
	        SPE_TaskInfoList TaskInfoList=new SPE_TaskInfoList();
	        int[] MeasuredSignList=new int[1];
	        if (TermialProtocolType==Glu_ConstDefine.GY_ZD_ZHEJIANG){
	          TaskInfoList=ZheJiangTaskInfoList;
	        }
	        else if (TermialProtocolType==Glu_ConstDefine.GY_ZD_ZJZB0404) {
	        	TaskInfoList=ZheJiangExTaskInfoList;
	        }
	        /*else if (TermialProtocolType==Glu_ConstDefine.GY_ZD_ZJBDZ){
	          TaskInfoList=ZheJiangBDZTaskInfoList;
	        }
	        else if (TermialProtocolType==Glu_ConstDefine.GY_ZD_GUANGDONG){
	          TaskInfoList=GuangDongTaskInfoList;
	        }
	        else if (TermialProtocolType==Glu_ConstDefine.GY_ZD_ZJMKB){
	          TaskInfoList=ZheJiangMKBTaskInfoList;
	        }
	        else if (TermialProtocolType==88){
	          TaskInfoList=JiangXiPBTaskInfoList;
	        }
	        else if (TermialProtocolType==110){
	          TaskInfoList=HuaLongJCTaskInfoList;
	        }
	        else if (TermialProtocolType==111){   
	          TaskInfoList=ZheJiangHLJC2TaskInfoList;
	        }*/
	        int iTaskNo=Integer.parseInt(FrameDataArea.substring(0,2),16);//任务号
	        //System.out.println("====++++++++++数据帧任务号: "+ Integer.toString(iTaskNo));
	        //System.out.println("====++++++++++队列里规约号: "+ Integer.toString(TermialProtocolType));
	        if(TermialProtocolType==110){
	          sTaskDatetime="20"+FrameDataArea.substring(2,10)+"0000";
	          iTaskDataCount=Integer.parseInt(FrameDataArea.substring(10,12),16); //任务点数
	          MeasuredSignList=GluMethodZheJiang.ExplainMeasuredPointList(FrameDataArea.substring(12,FrameDataArea.length()),TermialProtocolType);//得到测量点列表
	          FrameDataArea=FrameDataArea.substring(4+6*MeasuredSignList.length+12,FrameDataArea.length());
	          iTaskFeqUnit=Integer.parseInt(FrameDataArea.substring(0,2),16);//任务间隔单位
	          iTaskFeqCount=Integer.parseInt(FrameDataArea.substring(2,4),16);//任务间隔值
	          FrameDataArea=FrameDataArea.substring(4,FrameDataArea.length());//取任务数据区
	        }
	        else {
	          iTaskDataCount=Integer.parseInt(FrameDataArea.substring(12,14),16); //任务点数
	          iTaskFeqUnit=Integer.parseInt(FrameDataArea.substring(14,16),16);//任务间隔单位
	          iTaskFeqCount=Integer.parseInt(FrameDataArea.substring(16,18),16);//任务间隔值
	          sTaskDatetime="20"+FrameDataArea.substring(2,12)+"00";
	          FrameDataArea=FrameDataArea.substring(18,FrameDataArea.length());//取任务数据区
	        }
	        //System.out.println("====++++++++++任务队列总数: "+ Integer.toString(TaskInfoList.GetFTaskCount()));
	        for (int i=0;i<TaskInfoList.GetFTaskCount();i++){
	          TaskNo=((SFE_TaskInfo)(TaskInfoList.TaskInfoList.get(i))).GetFTaskNo();
	          if (iTaskNo==TaskNo) {//匹配任务号
	            iTaskType=((SFE_TaskInfo)(TaskInfoList.TaskInfoList.get(i))).GetFTaskType();
	            iMeasuredPointNo=((SFE_TaskInfo)(TaskInfoList.TaskInfoList.get(i))).GetFMeasuredPointNo();
	            iTaskIndex=i;
	            break;
	          }
	        }
	        //得到此任务包含命令个数
	        if (iTaskType==1){//普通任务
	          for(int k=0;k<MeasuredSignList.length;k++){
	            if(TermialProtocolType==110){ //集抄有多个任务
	              iMeasuredPointNo=MeasuredSignList[k];
	            }
	            iTaskDataItemCount=((SFE_TaskInfo)(TaskInfoList.TaskInfoList.get(iTaskIndex))).GetFDataItemCount();
	            for (int i=0;i<iTaskDataCount;i++){//数据区的任务点数
	              HistoryData.SetTaskNo(iTaskNo);
	              HistoryData.SetMeasuredPointNo(iMeasuredPointNo);
	              HistoryData.SetMeasuredPointType(10);
	              //得到每点任务数据时间
	              HistoryData.SetTaskDateTime(DataSwitch.IncreaseDateTime(sTaskDatetime,iTaskFeqCount*i,iTaskFeqUnit));
	              for (int j=0;j<iTaskDataItemCount;j++){//任务包含命令个数
	                sDataCaption=((String)((SFE_TaskInfo)(TaskInfoList.TaskInfoList.get(iTaskIndex))).CommandList.get(j));
	                boolean IsSearch=false;
	                if (TermialProtocolType==Glu_ConstDefine.GY_ZD_ZJZB0404 /*||
	                	TermialProtocolType==Glu_ConstDefine.GY_ZD_GUANGDONG ||
	                	TermialProtocolType==110*/){//浙规增补、广东、华隆集抄
	                 // if (TermialProtocolType==Glu_ConstDefine.GY_ZD_ZJZB0404){
	                    CommandInfoList=TermialZheJiangExCommandInfoList;
	                /*  }
	                  else if(TermialProtocolType==84) {
	                    CommandInfoList=TermialGuangDongCommandInfoList;
	                  }
	                  else if(TermialProtocolType==110){
	                    CommandInfoList=TermialHuaLongJCCommandInfoList;
	                  }*/
	                  NormalDataTemp=GluMethodZheJiang.CommandSearchAndExplain(FrameDataArea,sDataCaption,CommandInfoList,TermialProtocolType,0,0);
	                  if (NormalDataTemp.GetCommandLen()>0){//是否找到该命令
	                    IsSearch=true;
	                    FrameDataArea=FrameDataArea.substring(NormalDataTemp.GetCommandLen(),FrameDataArea.length());
	                    for (int n=0;n<NormalDataTemp.GetDataItemCount();n++){
	                      sDataCaption=new String(((SFE_DataItem)(NormalDataTemp.DataItemList.get(n))).GetDataCaption()).trim();
	                      sLogicData=new String(((SFE_DataItem)(NormalDataTemp.DataItemList.get(n))).GetDataContent()).trim();
	                      HistoryData.DataItemAdd(sDataCaption,sLogicData);
	                    }
	                  }
	                }
	                if (IsSearch==false&&TermialProtocolType!=110){//如果浙规增补或广东找不到再遍历浙规,华隆集抄命令列表是独立的
	                  CommandInfoList=TermialZheJiangCommandInfoList;
	                  NormalDataTemp=GluMethodZheJiang.CommandSearchAndExplain(FrameDataArea,sDataCaption,CommandInfoList,TermialProtocolType,0,0);
	                  if (NormalDataTemp.GetCommandLen()>0){//是否找到该命令
	                    FrameDataArea=FrameDataArea.substring(NormalDataTemp.GetCommandLen(),FrameDataArea.length());
	                    for (int n=0;n<NormalDataTemp.GetDataItemCount();n++){
	                      sDataCaption=new String(((SFE_DataItem)(NormalDataTemp.DataItemList.get(n))).GetDataCaption()).trim();
	                      sLogicData=new String(((SFE_DataItem)(NormalDataTemp.DataItemList.get(n))).GetDataContent()).trim();
	                      HistoryData.DataItemAdd(sDataCaption,sLogicData);
	                    }
	                  }
	                  else if(NormalDataTemp.GetCommandLen()==-1&&Glu_ConstDefine.TaskLengthCheckSupport){//如果任务长度需要检验且任务数据长度不够
	                    iExplainResult=50024;//任务数据长度非法
	                  }
	                }
	              }
	              HistoryDataList.add(HistoryData);
	              HistoryData=new SFE_HistoryData();
	            }
	          }
	          if(!FrameDataArea.equals("")&&Glu_ConstDefine.TaskLengthCheckSupport){//如果任务长度需要检验且任务数据长度过长
	            iExplainResult=50024;//任务数据长度非法
	          }
	        }
	        else if (iTaskType==2){//中继任务
	          sDataCaption="ZJRW";
	          sLogicData=FrameDataArea;
	          HistoryData.SetTaskNo(iTaskNo);
	          HistoryData.SetTaskDateTime(sTaskDatetime);
	          HistoryData.SetMeasuredPointNo(iMeasuredPointNo);
	          HistoryData.SetMeasuredPointType(10);
	          HistoryData.DataItemAdd(sDataCaption,sLogicData);
	          HistoryDataList.add(HistoryData);
	        }
	        else {
	          iExplainResult=50018;//未找到任务配置信息
	          Glu_ConstDefine.Log1.WriteLog("DataAreaExplain:TerminalAddress:"+TerminalAddress+",the task "+iTaskNo+" had not configured"+sError);
	        }
	      }
	      catch (Exception e) {
	        Glu_ConstDefine.Log1.WriteLog("Func:FrameDataAreaExplainZheJiang__ExplainHistoryDataAreaZheJiang();Error:"+e.toString());
	      }
	    }
	    finally{
	    }
	    DataListInfo.ExplainResult=iExplainResult;
	    DataListInfo.DataType=20;
	    DataListInfo.DataList=HistoryDataList;
	    return DataListInfo;
	  }
	  public SFE_DataListInfo ExplainAlarmDataAreaZheJiang(//告警数据区解释
	          String FrameDataArea,int TermialProtocolType,String ControlCode){//帧数据区,终端规约,控制码
	    SFE_DataListInfo DataListInfo=new SFE_DataListInfo();
	    ArrayList <SFE_AlarmData>AlarmDataList=new ArrayList<SFE_AlarmData>();
	    int iExplainResult=0;
	    try{
	      try{
	        SFE_ExplainAlarmData ExplainAlarmData=new SFE_ExplainAlarmData();
	        String sAlarmCode="";
	        int iAlarmDataCount=0;
	        String sAlarmDateTime="";
	        int iMeasuredPointNo=0;
	        if(TermialProtocolType==88){//江西配变
	          sAlarmCode=FrameDataArea.substring(0,2);//记录号01:事件记录;02电容器投切记录
	          FrameDataArea=FrameDataArea.substring(8,FrameDataArea.length());//消去记录号和记录时间4个字节
	          if(sAlarmCode.equals("01")){//每个事件记录有7个字节
	            iAlarmDataCount=FrameDataArea.length()/14;
	          }
	          else{//每个电容器投切记录有14个字节
	            iAlarmDataCount=FrameDataArea.length()/28;
	          }
	        }
	        else{//浙规系列其他规约
	          iAlarmDataCount=Integer.parseInt(FrameDataArea.substring(0,2),16);//告警个数
	          FrameDataArea=FrameDataArea.substring(2,FrameDataArea.length());
	        }
	        for (int ii=0;ii<iAlarmDataCount;ii++){
	          if(TermialProtocolType==88){//江西配变
	            sAlarmDateTime="20"+FrameDataArea.substring(0,10)+"00";                   //告警发生时间
	            FrameDataArea=FrameDataArea.substring(10,FrameDataArea.length());
	          }
	          else{//浙规系列其他规约
	            iMeasuredPointNo=Integer.parseInt(FrameDataArea.substring(0,2));          //测量点号
	            sAlarmDateTime="20"+FrameDataArea.substring(2,12)+"00";                   //告警发生时间
	            sAlarmCode=FrameDataArea.substring(14,16)+FrameDataArea.substring(12,14); //告警编码
	            FrameDataArea=FrameDataArea.substring(16,FrameDataArea.length());
	          }
	          ExplainAlarmData=ExplainAlarmParameterZheJing(FrameDataArea,sAlarmCode,TermialProtocolType,iMeasuredPointNo,sAlarmDateTime);
	          if(ExplainAlarmData.GetDataLen()==-1) {
	            iExplainResult=50020;//传入数据长度非法
	          }
	          else if(ExplainAlarmData.GetDataLen()<=FrameDataArea.length()){
	            FrameDataArea=FrameDataArea.substring(ExplainAlarmData.GetDataLen(),FrameDataArea.length());
	            AlarmDataList.add(ExplainAlarmData.AlarmData);
	          }
	        }
	      }
	      catch(Exception e){
	        Glu_ConstDefine.Log1.WriteLog("Func:FrameDataAreaExplainZheJiang__ExplainAlarmDataAreaZheJiang();Error:"+e.toString());
	      }
	    }
	    finally{
	    }
	    DataListInfo.DataType=30;
	    DataListInfo.ExplainResult=iExplainResult;
	    DataListInfo.DataList=AlarmDataList;
	    return DataListInfo;
	  }
	  public SFE_ExplainAlarmData ExplainAlarmParameterZheJing(String sDateContent,String sAlarmCode,int iTermialProtocolType,int iMeasuredPointNo,String sAlarmDateTime){
	    SFE_ExplainAlarmData ExplainAlarmData=new SFE_ExplainAlarmData();
	    ArrayList <SFE_DataItemInfo>DataItemInfoList=new ArrayList<SFE_DataItemInfo>(); //数据项信息列表
	    SFE_DataItemInfo DataItemInfo=new SFE_DataItemInfo();
	    int iDataItemCount=0;
	    String sDataCaption="";//数据项标识
	    int iDataType=0;   //数据类型
	    int iDataLen=0;  //数据长度
	    String sDataFormat=""; //数据项数据格式
	    int iStorageType=0;//存储方式（10 BCD;20 HEX;30 ASCII）
	    int iStorageOrder=0;//存储顺序（10顺序  20逆序）
	    String sLogicData="";//逻辑数据内容（如12.34）
	    String sPhysicalData="";//物理数据内容(如BCD:3412,Hex:220C)
	    int ilen=0;
	    int iStringLen=0;
	    try{
	      try{
	        if((sAlarmCode.equals("0102"))||(sAlarmCode.equals("0182"))||(sAlarmCode.equals("0109"))||(sAlarmCode.equals("0189"))||(sAlarmCode.equals("010A"))||(sAlarmCode.equals("018A"))||
	          (sAlarmCode.equals("0111"))||(sAlarmCode.equals("0112"))||(sAlarmCode.equals("0113"))||(sAlarmCode.equals("0114"))||(sAlarmCode.equals("0115"))||(sAlarmCode.equals("0116"))||
	          (sAlarmCode.equals("0117"))||(sAlarmCode.equals("0118"))||(sAlarmCode.equals("0119"))||(sAlarmCode.equals("011A"))||(sAlarmCode.equals("011B"))||(sAlarmCode.equals("011C"))||
	          (sAlarmCode.equals("0191"))||(sAlarmCode.equals("0192"))||(sAlarmCode.equals("0193"))||(sAlarmCode.equals("0194"))||(sAlarmCode.equals("0195"))||(sAlarmCode.equals("0196"))||
	          (sAlarmCode.equals("0197"))||(sAlarmCode.equals("0198"))||(sAlarmCode.equals("0199"))||(sAlarmCode.equals("019A"))||(sAlarmCode.equals("019B"))||(sAlarmCode.equals("019C"))||
	          (sAlarmCode.equals("0149"))||(sAlarmCode.equals("014A"))||(sAlarmCode.equals("014B"))||(sAlarmCode.equals("01C9"))||(sAlarmCode.equals("01CA"))||(sAlarmCode.equals("01CB"))||
	          (sAlarmCode.equals("0141"))||(sAlarmCode.equals("01C1"))||(sAlarmCode.equals("0136"))||
	          (sAlarmCode.equals("0153"))||(sAlarmCode.equals("01D3"))||//这两个比其他告警多B62F其他一样，比较特殊
	          (((sAlarmCode.equals("01B6"))||(sAlarmCode.equals("0152"))||(sAlarmCode.equals("01D2"))||(sAlarmCode.equals("0163")))&&
	        		  (iTermialProtocolType==Glu_ConstDefine.GY_ZD_ZJZB0404 || iTermialProtocolType==83))||
	          (((sAlarmCode.equals("EE01"))||(sAlarmCode.equals("EE81")))&&(iTermialProtocolType==84))){//85版终端才支持自定义的告警
	          //--------------901F,902F,9130,9140,9150,9160(4个字节)----------------
	          iDataType=c_Float;
	          iDataLen=4;
	          iStringLen=iDataLen*2;//物理数据的字符长度
	          sDataFormat="000000.00";
	          iStorageType=CCFS_BCD;
	          iStorageOrder=CCSX_NX;
	          for(int i=0;i<14;i++){
	            DataItemInfo=new SFE_DataItemInfo();  //ArrayList里的元素每次ADD都要初始化
	            switch (i) {
	              case 0:sDataCaption="0000";//正向有功总
	                break;
	              case 1:sDataCaption="0001";//正向有功尖
	                break;
	              case 2:sDataCaption="0002";//正向有功峰
	                break;
	              case 3:sDataCaption="0003";//正向有功平
	                break;
	              case 4:sDataCaption="0004";//正向有功谷
	                break;
	              case 5:sDataCaption="0100";//反向有功谷
	                break;
	              case 6:sDataCaption="0101";//反向有功谷
	                break;
	              case 7:sDataCaption="0102";//反向有功谷
	                break;
	              case 8:sDataCaption="0103";//反向有功谷
	                break;
	              case 9:sDataCaption="0104";//反向有功谷
	                break;
	              case 10:sDataCaption="0400";//一象限无功总
	                break;
	              case 11:sDataCaption="0700";//四象限无功总
	                break;
	              case 12:sDataCaption="0500";//二象限无功总
	                break;
	              case 13:sDataCaption="0600";//三象限无功总
	                break;
	            }
	            sPhysicalData=sDateContent.substring(i*iStringLen,i*iStringLen+iStringLen);
	            DataItemInfo.DataItemInfoAdd(sDataCaption,iDataType,iDataLen,sDataFormat,iStorageType,iStorageOrder);
	            DataItemInfo.SetFPhysicalData(sPhysicalData);
	            DataItemInfoList.add(DataItemInfo);
	            iDataItemCount=iDataItemCount+1;
	          }
	          ilen=ilen+14*iStringLen;//已处理的字符长度
	          sDateContent=sDateContent.substring(14*iStringLen,sDateContent.length());
	          //--------------A010,A020(3个字节)----------------
	          iDataType=c_Float;
	          iDataLen=3;
	          iStringLen=iDataLen*2;//物理数据的字符长度
	          sDataFormat="00.0000";
	          for(int i=0;i<2;i++){
	            DataItemInfo=new SFE_DataItemInfo();  //ArrayList里的元素每次ADD都要初始化
	            switch (i) {
	              case 0:sDataCaption="1000";//正向有功总最大需量
	                break;
	              case 1:sDataCaption="1100";//反向有功总最大需量
	                break;
	            }
	            sPhysicalData=sDateContent.substring(i*iStringLen,i*iStringLen+iStringLen);
	            DataItemInfo.DataItemInfoAdd(sDataCaption,iDataType,iDataLen,sDataFormat,iStorageType,iStorageOrder);
	            DataItemInfo.SetFPhysicalData(sPhysicalData);
	            DataItemInfoList.add(DataItemInfo);
	            iDataItemCount=iDataItemCount+1;
	          }
	          ilen=ilen+2*iStringLen;//已处理的字符长度
	          sDateContent=sDateContent.substring(2*iStringLen,sDateContent.length());
	          //--------------B010,B020(4个字节)----------------
	          iDataType=c_DateTime;
	          iDataLen=3;
	          iStringLen=iDataLen*2;//物理数据的字符长度
	          sDataFormat="MMDDHHNN";
	          for(int i=0;i<2;i++){
	            DataItemInfo=new SFE_DataItemInfo();  //ArrayList里的元素每次ADD都要初始化
	            switch (i) {
	              case 0:sDataCaption="2000";//正向有功总最大需量发生时间
	                break;
	              case 1:sDataCaption="2100";//反向有功总最大需量发生时间
	                break;
	            }
	            sPhysicalData=sDateContent.substring(i*iStringLen,i*iStringLen+iStringLen);
	            DataItemInfo.DataItemInfoAdd(sDataCaption,iDataType,iDataLen,sDataFormat,iStorageType,iStorageOrder);
	            DataItemInfo.SetFPhysicalData(sPhysicalData);
	            DataItemInfoList.add(DataItemInfo);
	            iDataItemCount=iDataItemCount+1;
	          }
	          ilen=ilen+2*iStringLen;//已处理的字符长度
	          sDateContent=sDateContent.substring(2*iStringLen,sDateContent.length());
	          if ((sAlarmCode.equals("0153"))||(sAlarmCode.equals("01D3"))){
	            //--------------B62F(6)----------------
	            iDataType=c_Float;
	            iDataLen=2;
	            iStringLen=iDataLen*2;//物理数据的字符长度
	            sDataFormat="00.00";
	            for(int i=0;i<3;i++){
	              DataItemInfo=new SFE_DataItemInfo();  //ArrayList里的元素每次ADD都要初始化
	              switch (i) {
	                case 0:sDataCaption="3420";//A相电流
	                  break;
	                case 1:sDataCaption="3421";//B相电流
	                  break;
	                case 2:sDataCaption="3422";//C相电流
	                  break;
	              }
	              sPhysicalData=sDateContent.substring(i*iStringLen,i*iStringLen+iStringLen);
	              DataItemInfo.DataItemInfoAdd(sDataCaption,iDataType,iDataLen,sDataFormat,iStorageType,iStorageOrder);
	              DataItemInfo.SetFPhysicalData(sPhysicalData);
	              DataItemInfoList.add(DataItemInfo);
	              iDataItemCount=iDataItemCount+1;
	            }
	            ilen=ilen+3*iStringLen;//已处理的字符长度
	            sDateContent=sDateContent.substring(3*iStringLen,sDateContent.length());
	          }
	        }
	        else if (sAlarmCode.equals("0122")){
	          //--------------901F,8F901F,9020,8F9020,9130,8F9130,9140,8F9140(4个字节)----------------
	          iDataType=c_Float;
	          iDataLen=4;
	          iStringLen=iDataLen*2;//物理数据的字符长度
	          sDataFormat="000000.00";
	          iStorageType=CCFS_BCD;
	          iStorageOrder=CCSX_NX;
	          for(int i=0;i<16;i++){
	            DataItemInfo=new SFE_DataItemInfo();  //ArrayList里的元素每次ADD都要初始化
	            switch (i) {
	              case 0:sDataCaption="0000";//正向有功总
	                break;
	              case 1:sDataCaption="0001";//正向有功尖
	                break;
	              case 2:sDataCaption="0002";//正向有功峰
	                break;
	              case 3:sDataCaption="0003";//正向有功平
	                break;
	              case 4:sDataCaption="0004";//正向有功谷
	                break;
	              case 5:sDataCaption="8F0000";//正向有功总(异常发生前)
	                break;
	              case 6:sDataCaption="8F0001";//正向有功尖(异常发生前)
	                break;
	              case 7:sDataCaption="8F0002";//正向有功峰(异常发生前)
	                break;
	              case 8:sDataCaption="8F0003";//正向有功平(异常发生前)
	                break;
	              case 9:sDataCaption="8F0004";//正向有功谷(异常发生前)
	                break;
	              case 10:sDataCaption="0100";//反向有功总
	                break;
	              case 11:sDataCaption="8F0100";//反向有功总(异常发生前)
	                break;
	              case 12:sDataCaption="0400";//一象限无功总
	                break;
	              case 13:sDataCaption="8F0400";//一象限无功总(异常发生前)
	                break;
	              case 14:sDataCaption="0700";//四象限无功总
	                break;
	              case 15:sDataCaption="8F0700";//四象限无功总(异常发生前)
	                break;
	            }
	            sPhysicalData=sDateContent.substring(i*iStringLen,i*iStringLen+iStringLen);
	            DataItemInfo.DataItemInfoAdd(sDataCaption,iDataType,iDataLen,sDataFormat,iStorageType,iStorageOrder);
	            DataItemInfo.SetFPhysicalData(sPhysicalData);
	            DataItemInfoList.add(DataItemInfo);
	            iDataItemCount=iDataItemCount+1;
	          }
	          ilen=ilen+16*iStringLen;//已处理的字符长度
	          sDateContent=sDateContent.substring(16*iStringLen,sDateContent.length());
	          //--------------A010,8FA010(3个字节)----------------
	          iDataType=c_Float;
	          iDataLen=3;
	          iStringLen=iDataLen*2;//物理数据的字符长度
	          sDataFormat="00.0000";
	          for(int i=0;i<2;i++){
	            DataItemInfo=new SFE_DataItemInfo();  //ArrayList里的元素每次ADD都要初始化
	            switch (i) {
	              case 0:sDataCaption="1000";//正向有功总最大需量
	                break;
	              case 1:sDataCaption="8F1000";//正向有功总最大需量(异常发生前)
	                break;
	            }
	            sPhysicalData=sDateContent.substring(i*iStringLen,i*iStringLen+iStringLen);
	            DataItemInfo.DataItemInfoAdd(sDataCaption,iDataType,iDataLen,sDataFormat,iStorageType,iStorageOrder);
	            DataItemInfo.SetFPhysicalData(sPhysicalData);
	            DataItemInfoList.add(DataItemInfo);
	            iDataItemCount=iDataItemCount+1;
	          }
	          ilen=ilen+2*iStringLen;//已处理的字符长度
	          sDateContent=sDateContent.substring(2*iStringLen,sDateContent.length());
	          //--------------C33FH(24)----------------
	          iDataType=c_String;
	          iDataLen=3;
	          iStringLen=iDataLen*2;//物理数据的字符长度
	          sDataFormat="3";
	          for(int i=0;i<16;i++){
	            DataItemInfo=new SFE_DataItemInfo();  //ArrayList里的元素每次ADD都要初始化
	            switch (i) {
	              case 0 :sDataCaption="4D31";  //第一日时段表第1时段起始时间及费率号
	                break;
	              case 1 :sDataCaption="4D32";  //第一日时段表第2时段起始时间及费率号
	                break;
	              case 2 :sDataCaption="4D33";  //第一日时段表第3时段起始时间及费率号
	                break;
	              case 3 :sDataCaption="4D34";  //第一日时段表第4时段起始时间及费率号
	                break;
	              case 4 :sDataCaption="4D35";  //第一日时段表第5时段起始时间及费率号
	                break;
	              case 5 :sDataCaption="4D36";  //第一日时段表第6时段起始时间及费率号
	                break;
	              case 6 :sDataCaption="4D37";  //第一日时段表第7时段起始时间及费率号
	                break;
	              case 7 :sDataCaption="4D38";  //第一日时段表第8时段起始时间及费率号
	                break;
	              case 8 :sDataCaption="8F4D31";//第一日时段表第1时段起始时间及费率号(异常发生前)
	                break;
	              case 9 :sDataCaption="8F4D32";//第一日时段表第2时段起始时间及费率号(异常发生前)
	                break;
	              case 10:sDataCaption="8F4D33";//第一日时段表第3时段起始时间及费率号(异常发生前)
	                break;
	              case 11:sDataCaption="8F4D34";//第一日时段表第4时段起始时间及费率号(异常发生前)
	                break;
	              case 12:sDataCaption="8F4D35";//第一日时段表第5时段起始时间及费率号(异常发生前)
	                break;
	              case 13:sDataCaption="8F4D36";//第一日时段表第6时段起始时间及费率号(异常发生前)
	                break;
	              case 14:sDataCaption="8F4D37";//第一日时段表第7时段起始时间及费率号(异常发生前)
	                break;
	              case 15:sDataCaption="8F4D38";//第一日时段表第8时段起始时间及费率号(异常发生前)
	                break;
	            }
	            sPhysicalData=sDateContent.substring(i*iStringLen,i*iStringLen+iStringLen);
	            DataItemInfo.DataItemInfoAdd(sDataCaption,iDataType,iDataLen,sDataFormat,iStorageType,iStorageOrder);
	            DataItemInfo.SetFPhysicalData(sPhysicalData);
	            DataItemInfoList.add(DataItemInfo);
	            iDataItemCount=iDataItemCount+1;
	          }
	          ilen=ilen+16*iStringLen;//已处理的字符长度
	          sDateContent=sDateContent.substring(2*iStringLen,sDateContent.length());
	        }
	        else if (sAlarmCode.equals("0124")||(sAlarmCode.equals("0126"))){
	          //--------------901F,8F901F,9020,8F9020,9130,8F9130,9140,8F9140,9150,8F9150,9160,8F9160(4个字节)----------------
	          iDataType=c_Float;
	          iDataLen=4;
	          iStringLen=iDataLen*2;//物理数据的字符长度
	          sDataFormat="000000.00";
	          iStorageType=CCFS_BCD;
	          iStorageOrder=CCSX_NX;
	          for(int i=0;i<20;i++){
	            DataItemInfo=new SFE_DataItemInfo();  //ArrayList里的元素每次ADD都要初始化
	            switch (i) {
	              case 0:sDataCaption="0000";//正向有功总
	                break;
	              case 1:sDataCaption="0001";//正向有功尖
	                break;
	              case 2:sDataCaption="0002";//正向有功峰
	                break;
	              case 3:sDataCaption="0003";//正向有功平
	                break;
	              case 4:sDataCaption="0004";//正向有功谷
	                break;
	              case 5:sDataCaption="8F0000";//正向有功总(异常发生前)
	                break;
	              case 6:sDataCaption="8F0001";//正向有功尖(异常发生前)
	                break;
	              case 7:sDataCaption="8F0002";//正向有功峰(异常发生前)
	                break;
	              case 8:sDataCaption="8F0003";//正向有功平(异常发生前)
	                break;
	              case 9:sDataCaption="8F0004";//正向有功谷(异常发生前)
	                break;
	              case 10:sDataCaption="0100";//反向有功总
	                break;
	              case 11:sDataCaption="8F0100";//反向有功总(异常发生前)
	                break;
	              case 12:sDataCaption="0400";//一象限无功总
	                break;
	              case 13:sDataCaption="8F0400";//一象限无功总(异常发生前)
	                break;
	              case 14:sDataCaption="0700";//四象限无功总
	                break;
	              case 15:sDataCaption="8F0700";//四象限无功总(异常发生前)
	                break;
	              case 16:sDataCaption="0500";//二象限无功总
	                break;
	              case 17:sDataCaption="8F0500";//二象限无功总(异常发生前)
	                break;
	              case 18:sDataCaption="0600";//三象限无功总
	                break;
	              case 19:sDataCaption="8F0600";//三象限无功总(异常发生前)
	                break;
	            }
	            sPhysicalData=sDateContent.substring(i*iStringLen,i*iStringLen+iStringLen);
	            DataItemInfo.DataItemInfoAdd(sDataCaption,iDataType,iDataLen,sDataFormat,iStorageType,iStorageOrder);
	            DataItemInfo.SetFPhysicalData(sPhysicalData);
	            DataItemInfoList.add(DataItemInfo);
	            iDataItemCount=iDataItemCount+1;
	          }
	          ilen=ilen+20*iStringLen;//已处理的字符长度
	          sDateContent=sDateContent.substring(20*iStringLen,sDateContent.length());
	          //--------------A010,8FA010(3个字节)----------------
	          iDataType=c_Float;
	          iDataLen=3;
	          iStringLen=iDataLen*2;//物理数据的字符长度
	          sDataFormat="00.0000";
	          for(int i=0;i<2;i++){
	            DataItemInfo=new SFE_DataItemInfo();  //ArrayList里的元素每次ADD都要初始化
	            switch (i) {
	              case 0:sDataCaption="1000";//正向有功总最大需量
	                break;
	              case 1:sDataCaption="8F1000";//正向有功总最大需量(异常发生前)
	                break;
	            }
	            sPhysicalData=sDateContent.substring(i*iStringLen,i*iStringLen+iStringLen);
	            DataItemInfo.DataItemInfoAdd(sDataCaption,iDataType,iDataLen,sDataFormat,iStorageType,iStorageOrder);
	            DataItemInfo.SetFPhysicalData(sPhysicalData);
	            DataItemInfoList.add(DataItemInfo);
	            iDataItemCount=iDataItemCount+1;
	          }
	          ilen=ilen+2*iStringLen;//已处理的字符长度
	          sDateContent=sDateContent.substring(2*iStringLen,sDateContent.length());
	          if (sAlarmCode.equals("0124")){
	            //--------------C030,8FC030,C031,8FC031(3个字节)----------------
	            iDataType=c_Float;
	            iDataLen=3;
	            iStringLen=iDataLen*2;//物理数据的字符长度
	            sDataFormat="000000";
	            for(int i=0;i<4;i++){
	              DataItemInfo=new SFE_DataItemInfo();  //ArrayList里的元素每次ADD都要初始化
	              switch (i) {
	                case 0:sDataCaption="4A30";//电表常数(有功)
	                  break;
	                case 1:sDataCaption="8F4A30";//电表常数(有功)(异常发生前)
	                  break;
	                case 2:sDataCaption="4A31";//电表常数(无功)
	                  break;
	                case 3:sDataCaption="8F4A31";//电表常数(无功)(异常发生前)
	                  break;
	              }
	              sPhysicalData=sDateContent.substring(i*iStringLen,i*iStringLen+iStringLen);
	              DataItemInfo.DataItemInfoAdd(sDataCaption,iDataType,iDataLen,sDataFormat,iStorageType,iStorageOrder);
	              DataItemInfo.SetFPhysicalData(sPhysicalData);
	              DataItemInfoList.add(DataItemInfo);
	              iDataItemCount=iDataItemCount+1;
	            }
	            ilen=ilen+4*iStringLen;//已处理的字符长度
	            sDateContent=sDateContent.substring(4*iStringLen,sDateContent.length());
	          }
	          else if (sAlarmCode.equals("0126")){
	            //--------------8911,8F8911,8912,8F8912(3个字节)----------------
	            iDataType=c_Float;
	            iDataLen=2;
	            iStringLen=iDataLen*2;//物理数据的字符长度
	            sDataFormat="0000";
	            for(int i=0;i<4;i++){
	              DataItemInfo=new SFE_DataItemInfo();  //ArrayList里的元素每次ADD都要初始化
	              switch (i) {
	                case 0:sDataCaption="8911";//CT变比
	                  break;
	                case 1:sDataCaption="8F8911";//CT变比(异常发生前)
	                  break;
	                case 2:sDataCaption="8912";//PT变比
	                  break;
	                case 3:sDataCaption="8F8912";//PT变比(异常发生前)
	                  break;
	              }
	              sPhysicalData=sDateContent.substring(i*iStringLen,i*iStringLen+iStringLen);
	              DataItemInfo.DataItemInfoAdd(sDataCaption,iDataType,iDataLen,sDataFormat,iStorageType,iStorageOrder);
	              DataItemInfo.SetFPhysicalData(sPhysicalData);
	              DataItemInfoList.add(DataItemInfo);
	              iDataItemCount=iDataItemCount+1;
	            }
	            ilen=ilen+4*iStringLen;//已处理的字符长度
	            sDateContent=sDateContent.substring(4*iStringLen,sDateContent.length());
	          }
	        }
	        else if ((sAlarmCode.equals("0121"))|| (sAlarmCode.equals("0132")) || (sAlarmCode.equals("0133"))|| (sAlarmCode.equals("0134"))|| (sAlarmCode.equals("0135"))){
	          //----901F,8F901F,902F,8F902F,9130,8F9130,9140,8F9140,9150,8F9150,9160,8F9160(4个字节)-----
	          iDataType=c_Float;
	          iDataLen=4;
	          iStringLen=iDataLen*2;//物理数据的字符长度
	          sDataFormat="000000.00";
	          iStorageType=CCFS_BCD;
	          iStorageOrder=CCSX_NX;
	          for(int i=0;i<28;i++){
	            DataItemInfo=new SFE_DataItemInfo();  //ArrayList里的元素每次ADD都要初始化
	            switch (i) {
	              case 0:sDataCaption="0000";//正向有功总
	                break;
	              case 1:sDataCaption="0001";//正向有功尖
	                break;
	              case 2:sDataCaption="0002";//正向有功峰
	                break;
	              case 3:sDataCaption="0003";//正向有功平
	                break;
	              case 4:sDataCaption="0004";//正向有功
	                break;
	              case 5:sDataCaption="8F0000";//正向有功总(异常发生前)
	                break;
	              case 6:sDataCaption="8F0001";//正向有功尖(异常发生前)
	                break;
	              case 7:sDataCaption="8F0002";//正向有功峰(异常发生前)
	                break;
	              case 8:sDataCaption="8F0003";//正向有功平(异常发生前)
	                break;
	              case 9:sDataCaption="8F0004";//正向有功谷(异常发生前)
	                break;
	              case 10:sDataCaption="0100";//反向有功总
	                break;
	              case 11:sDataCaption="0101";//反向有功尖
	                break;
	              case 12:sDataCaption="0102";//反向有功峰
	                break;
	              case 13:sDataCaption="0103";//反向有功平
	                break;
	              case 14:sDataCaption="0104";//反向有功谷
	                break;
	              case 15:sDataCaption="8F0100";//反向有功总(异常发生前)
	                break;
	              case 16:sDataCaption="8F0101";//反向有功尖(异常发生前)
	                break;
	              case 17:sDataCaption="8F0102";//反向有功峰(异常发生前)
	                break;
	              case 18:sDataCaption="8F0103";//反向有功平(异常发生前)
	                break;
	              case 19:sDataCaption="8F0104";//反向有功谷(异常发生前)
	                break;
	              case 20:sDataCaption="0400";//一象限无功总
	                break;
	              case 21:sDataCaption="8F0400";//一象限无功总(异常发生前)
	                break;
	              case 22:sDataCaption="0700";//四象限无功总
	                break;
	              case 23:sDataCaption="8F0700";//四象限无功总(异常发生前)
	                break;
	              case 24:sDataCaption="0500";//二象限无功总
	                break;
	              case 25:sDataCaption="8F0500";//二象限无功总(异常发生前)
	                break;
	              case 26:sDataCaption="0600";//三象限无功总
	                break;
	              case 27:sDataCaption="8F0600";//三象限无功总(异常发生前)
	                break;
	            }
	            sPhysicalData=sDateContent.substring(i*iStringLen,i*iStringLen+iStringLen);
	            DataItemInfo.DataItemInfoAdd(sDataCaption,iDataType,iDataLen,sDataFormat,iStorageType,iStorageOrder);
	            DataItemInfo.SetFPhysicalData(sPhysicalData);
	            DataItemInfoList.add(DataItemInfo);
	            iDataItemCount=iDataItemCount+1;
	          }
	          ilen=ilen+28*iStringLen;//已处理的字符长度
	          sDateContent=sDateContent.substring(28*iStringLen,sDateContent.length());
	          //--------------A010,8FA010(3个字节)----------------
	          iDataType=c_Float;
	          iDataLen=3;
	          iStringLen=iDataLen*2;//物理数据的字符长度
	          sDataFormat="00.0000";
	          for(int i=0;i<2;i++){
	            DataItemInfo=new SFE_DataItemInfo();  //ArrayList里的元素每次ADD都要初始化
	            switch (i) {
	              case 0:sDataCaption="1000";//正向有功总最大需量
	                break;
	              case 1:sDataCaption="8F1000";//正向有功总最大需量(异常发生前)
	                break;
	            }
	            sPhysicalData=sDateContent.substring(i*iStringLen,i*iStringLen+iStringLen);
	            DataItemInfo.DataItemInfoAdd(sDataCaption,iDataType,iDataLen,sDataFormat,iStorageType,iStorageOrder);
	            DataItemInfo.SetFPhysicalData(sPhysicalData);
	            DataItemInfoList.add(DataItemInfo);
	            iDataItemCount=iDataItemCount+1;
	          }
	          ilen=ilen+2*iStringLen;//已处理的字符长度
	          sDateContent=sDateContent.substring(2*iStringLen,sDateContent.length());
	        }
	        else if((sAlarmCode.equals("0168"))||(sAlarmCode.equals("01E8"))||(sAlarmCode.equals("0178"))||(sAlarmCode.equals("01F8"))||(sAlarmCode.equals("0179"))||
	                (sAlarmCode.equals("01F9"))||(sAlarmCode.equals("01E9"))||(sAlarmCode.equals("01EA"))||(sAlarmCode.equals("0169"))||(sAlarmCode.equals("016A"))||
	                (sAlarmCode.equals("EE42"))||(sAlarmCode.equals("EEC2"))){
	          //--------------8E30(4个字节)----------------
	          iDataType=c_Float;
	          iDataLen=4;
	          iStringLen=iDataLen*2;
	          sDataFormat="0000000.0";
	          iStorageType=CCFS_BCD;
	          iStorageOrder=CCSX_NX;
	          sDataCaption="8E30";//瞬时有功功率(一次侧)
	          sPhysicalData=sDateContent.substring(0,iStringLen);
	          DataItemInfo.DataItemInfoAdd(sDataCaption,iDataType,iDataLen,sDataFormat,iStorageType,iStorageOrder);
	          DataItemInfo.SetFPhysicalData(sPhysicalData);
	          DataItemInfoList.add(DataItemInfo);
	          iDataItemCount=iDataItemCount+1;

	          ilen=ilen+iStringLen;//已处理的字符长度
	          sDateContent=sDateContent.substring(iStringLen,sDateContent.length());
	        }
	        //浙规增补才支持的告警
	        else if (((sAlarmCode.equals("0142"))||(sAlarmCode.equals("0143"))||(sAlarmCode.equals("0144"))||(sAlarmCode.equals("0145"))||
	                 (sAlarmCode.equals("01C2"))||(sAlarmCode.equals("01C3"))||(sAlarmCode.equals("01C4"))||(sAlarmCode.equals("01C5")))
	                 &&(iTermialProtocolType == Glu_ConstDefine.GY_ZD_ZJZB0404)){
	          //--------------B62F(6)----------------
	          iDataType=c_Float;
	          iDataLen=2;
	          iStringLen=iDataLen*2;//物理数据的字符长度
	          sDataFormat="00.00";
	          iStorageType=CCFS_BCD;
	          iStorageOrder=CCSX_NX;
	          for(int i=0;i<3;i++){
	            DataItemInfo=new SFE_DataItemInfo();  //ArrayList里的元素每次ADD都要初始化
	            switch (i) {
	              case 0:sDataCaption="3420";//A相电流
	                break;
	              case 1:sDataCaption="3421";//B相电流
	                break;
	              case 2:sDataCaption="3422";//C相电流
	                break;
	            }
	            sPhysicalData=sDateContent.substring(i*iStringLen,i*iStringLen+iStringLen);
	            DataItemInfo.DataItemInfoAdd(sDataCaption,iDataType,iDataLen,sDataFormat,iStorageType,iStorageOrder);
	            DataItemInfo.SetFPhysicalData(sPhysicalData);
	            DataItemInfoList.add(DataItemInfo);
	            iDataItemCount=iDataItemCount+1;
	          }
	          ilen=ilen+3*iStringLen;//已处理的字符长度
	          sDateContent=sDateContent.substring(3*iStringLen,sDateContent.length());
	        }
	        //浙规增补才支持的告警
	        else if (((sAlarmCode.equals("0167"))||(sAlarmCode.equals("01E7"))||(sAlarmCode.equals("0166")))
	        		&&(iTermialProtocolType == Glu_ConstDefine.GY_ZD_ZJZB0404)){
	          //--------------901F,902F,9130,9140,9150,9160(4个字节)----------------
	          iDataType=c_Float;
	          iDataLen=4;
	          iStringLen=iDataLen*2;//物理数据的字符长度
	          sDataFormat="000000.00";
	          iStorageType=CCFS_BCD;
	          iStorageOrder=CCSX_NX;
	          for(int i=0;i<14;i++){
	            DataItemInfo=new SFE_DataItemInfo();  //ArrayList里的元素每次ADD都要初始化
	            switch (i) {
	              case 0:sDataCaption="0000";//正向有功总
	                break;
	              case 1:sDataCaption="0001";//正向有功尖
	                break;
	              case 2:sDataCaption="0002";//正向有功峰
	                break;
	              case 3:sDataCaption="0003";//正向有功平
	                break;
	              case 4:sDataCaption="0004";//正向有功谷
	                break;
	              case 5:sDataCaption="0100";//反向有功谷
	                break;
	              case 6:sDataCaption="0101";//反向有功谷
	                break;
	              case 7:sDataCaption="0102";//反向有功谷
	                break;
	              case 8:sDataCaption="0103";//反向有功谷
	                break;
	              case 9:sDataCaption="0104";//反向有功谷
	                break;
	              case 10:sDataCaption="0400";//一象限无功总
	                break;
	              case 11:sDataCaption="0700";//四象限无功总
	                break;
	              case 12:sDataCaption="0500";//二象限无功总
	                break;
	              case 13:sDataCaption="0600";//三象限无功总
	                break;
	            }
	            sPhysicalData=sDateContent.substring(i*iStringLen,i*iStringLen+iStringLen);
	            DataItemInfo.DataItemInfoAdd(sDataCaption,iDataType,iDataLen,sDataFormat,iStorageType,iStorageOrder);
	            DataItemInfo.SetFPhysicalData(sPhysicalData);
	            DataItemInfoList.add(DataItemInfo);
	            iDataItemCount=iDataItemCount+1;
	          }
	          ilen=ilen+14*iStringLen;//已处理的字符长度
	          sDateContent=sDateContent.substring(14*iStringLen,sDateContent.length());
	          if (sAlarmCode.equals("0166")){
	            //--------------8063(4个字节)----------------
	            iDataType=c_Float;
	            iDataLen=5;
	            iStringLen=iDataLen*2;
	            sDataFormat="000000000.0";
	            sDataCaption="8063";//剩余电量(一次值)
	            sPhysicalData=sDateContent.substring(0,iStringLen);
	            DataItemInfo.DataItemInfoAdd(sDataCaption,iDataType,iDataLen,sDataFormat,iStorageType,iStorageOrder);
	            DataItemInfo.SetFPhysicalData(sPhysicalData);
	            DataItemInfoList.add(DataItemInfo);
	            iDataItemCount=iDataItemCount+1;

	            ilen=ilen+iStringLen;
	            sDateContent=sDateContent.substring(iStringLen,sDateContent.length());
	          }
	          else {
	            //--------------8E30(4个字节)----------------
	            iDataType=c_Float;
	            iDataLen=4;
	            iStringLen=iDataLen*2;
	            sDataFormat="0000000.0";
	            sDataCaption="8E30";//瞬时有功功率(一次侧)
	            sPhysicalData=sDateContent.substring(0,iStringLen);
	            DataItemInfo.DataItemInfoAdd(sDataCaption,iDataType,iDataLen,sDataFormat,iStorageType,iStorageOrder);
	            DataItemInfo.SetFPhysicalData(sPhysicalData);
	            DataItemInfoList.add(DataItemInfo);
	            iDataItemCount=iDataItemCount+1;

	            ilen=ilen+iStringLen;
	            sDateContent=sDateContent.substring(iStringLen,sDateContent.length());
	          }
	        }
	        //江西配变支持的告警
	        else if (((sAlarmCode.equals("01"))||(sAlarmCode.equals("02")))&&(iTermialProtocolType==88)){
	          if (sAlarmCode.equals("01")){
	            //异常代码=事件编号+域值
	            sAlarmCode=DataSwitch.HexToInt(sDateContent.substring(0,2),"00")+DataSwitch.HexToInt(sDateContent.substring(2,4),"00");
	          }
	          else{
	            //异常代码=2+投切指示+0+动作原因
	            int itemp=Integer.parseInt(sDateContent.substring(1,2),16);
	            sAlarmCode="2"+Integer.toString((itemp>>3)&1)+"0"+Integer.toString(itemp&7);

	            iDataType=c_Float;
	            iDataLen=1;
	            iStringLen=iDataLen*2;//物理数据的字符长度
	            iStorageType=CCFS_HEX;
	            iStorageOrder=CCSX_NX;
	            sDataCaption="Z00000"; //动作组号
	            sDataFormat="00";
	            sPhysicalData="0"+sDateContent.substring(0,1);
	            DataItemInfo.DataItemInfoAdd(sDataCaption,iDataType,iDataLen,sDataFormat,iStorageType,iStorageOrder);
	            DataItemInfo.SetFPhysicalData(sPhysicalData);
	            DataItemInfoList.add(DataItemInfo);
	            iDataItemCount=iDataItemCount+1;
	            sDateContent=sDateContent.substring(iStringLen,sDateContent.length());
	            ilen=ilen+iStringLen;

	            iDataType=c_Float;
	            iDataLen=2;
	            iStringLen=iDataLen*2;//物理数据的字符长度
	            iStorageType=CCFS_BCD;
	            iStorageOrder=CCSX_NX;
	            for(int i=0;i<4;i++){
	              DataItemInfo=new SFE_DataItemInfo();  //ArrayList里的元素每次ADD都要初始化
	              switch (i) {
	                case 0:{
	                  sDataCaption="F0B640"; //动作前无功
	                  sDataFormat="00.00";
	                  break;
	                }
	                case 1:{
	                  sDataCaption="B0B640";//动作后无功
	                  sDataFormat="00.00";
	                  break;
	                }
	                case 2:{
	                  sDataCaption="F0B610";//动作后电压
	                  sDataFormat="0000";
	                  break;
	                }
	                case 3:{
	                  sDataCaption="B0B610";//动作后电压
	                  sDataFormat="0000";
	                  break;
	                }
	              }
	              sPhysicalData=sDateContent.substring(i*iStringLen,i*iStringLen+iStringLen);
	              DataItemInfo.DataItemInfoAdd(sDataCaption,iDataType,iDataLen,sDataFormat,iStorageType,iStorageOrder);
	              DataItemInfo.SetFPhysicalData(sPhysicalData);
	              DataItemInfoList.add(DataItemInfo);
	              iDataItemCount=iDataItemCount+1;
	            }
	            ilen=ilen+4*iStringLen;//已处理的字符长度
	            sDateContent=sDateContent.substring(4*iStringLen,sDateContent.length());
	          }
	        }

	        for (int i=0;i<iDataItemCount;i++){
	          ((SFE_DataItemInfo)(DataItemInfoList.get(i))).PhysicalDataToLogicData();//物理数据转换成逻辑数据
	          sDataCaption=((SFE_DataItemInfo)(DataItemInfoList.get(i))).GetFDataCaption();//数据项标识
	          sLogicData=((SFE_DataItemInfo)(DataItemInfoList.get(i))).GetFLogicData();//逻辑数据
	          iDataLen=sLogicData.length();//逻辑数据长度
	          ExplainAlarmData.AlarmData.DataItemAdd(sDataCaption,iDataLen,sLogicData);
	        }
	        ExplainAlarmData.AlarmData.SetAlarmCode(sAlarmCode);
	        ExplainAlarmData.AlarmData.SetAlarmDateTime(sAlarmDateTime);
	        ExplainAlarmData.AlarmData.SetMeasuredPointNo(iMeasuredPointNo);
	        ExplainAlarmData.AlarmData.SetMeasuredPointType(10);
	        ExplainAlarmData.SetDataLen(ilen);//已经处理的数据区长度

	      }
	      catch(Exception e){
	        if (ilen>0){//出错且处理了部分长度,很可能是数据长度不够导致
	          ilen=-1;
	          ExplainAlarmData.SetDataLen(ilen);
	        }
	        Glu_ConstDefine.Log1.WriteLog("Func:FrameDataAreaExplainZheJiang__ExplainAlarmParameterZheJing();Error:"+e.toString());
	      }
	    }
	    finally{
	    }
	    return ExplainAlarmData;
	  }
	  public SFE_DataListInfo ExplainSetResultDataAreaZheJiang(//设置返回区解释
	          String FrameDataArea,int TermialProtocolType,String ControlCode){//帧数据区,终端规约,控制码
	    SFE_DataListInfo DataListInfo=new SFE_DataListInfo();
	    ArrayList <SFE_SetResultData>SetResultDataList=new ArrayList<SFE_SetResultData>();
	    try{
	      try{
	        SFE_SetResultData SetResultData=new SFE_SetResultData();
	        String sDataCaption="";
	        int iMeasured=0;
	        int iSetResult=-1;
	        if(TermialProtocolType==110){//华隆集抄目前只处理设置单个对象
	          int[] MeasuredSignList;
	          MeasuredSignList=GluMethodZheJiang.ExplainMeasuredPointList(FrameDataArea,TermialProtocolType);//得到测量点列表
	          if(MeasuredSignList.length==1&&MeasuredSignList[0]==0){//如果是终端参数对象标志只有两个字节
	            FrameDataArea=FrameDataArea.substring(4,FrameDataArea.length());
	          }
	          else {
	            FrameDataArea=FrameDataArea.substring(4+6*MeasuredSignList.length,FrameDataArea.length());
	          }
	          iMeasured=MeasuredSignList[0];
	        }
	        else{
	          iMeasured=Integer.parseInt(FrameDataArea.substring(0,2));
	          FrameDataArea=FrameDataArea.substring(2,FrameDataArea.length());
	        }
	        SetResultData.SetMeasuredPointNo(iMeasured);
	        SetResultData.SetMeasuredPointType(10);
	        while (FrameDataArea.length()>0){
	          sDataCaption=FrameDataArea.substring(2,4)+FrameDataArea.substring(0,2);
	          iSetResult=Integer.parseInt(FrameDataArea.substring(4,6));
	          SetResultData.DataItemAdd(sDataCaption,iSetResult);
	          FrameDataArea=FrameDataArea.substring(6,FrameDataArea.length());
	        }
	        SetResultDataList.add(SetResultData);
	      }
	      catch(Exception e){
	        Glu_ConstDefine.Log1.WriteLog("Func:FrameDataAreaExplainZheJiang__ExplainSetResultDataAreaZheJiang();Error:"+e.toString());
	      }
	    }
	    finally{
	    }
	    DataListInfo.ExplainResult=0;
	    DataListInfo.DataType=40;
	    DataListInfo.DataList=SetResultDataList;
	    return DataListInfo;
	  }
	  public SFE_NormalData ExplainManufacturerFrame(String DataArea){
	    SFE_NormalData NormalData=new SFE_NormalData();
	    try{
	      try{
	        String sCommand=DataArea.substring(4,6)+DataArea.substring(2,4);
	        SPE_CommandInfoList CommandInfoList=new SPE_CommandInfoList();
	        CommandInfoList=TermialZheJiangManufacturerCommandInfoList;
	        String Command="";
	        String sControlCode="";
	        String sDataContent="";
	        int iCommandLen=0;
	        if (DataArea.length()>=8){//是否存在自定义命令的控制码
	          sControlCode=DataArea.substring(6,8);
	        }
	        if (sCommand.equals("EEB1") || sCommand.equals("EEB2")|| sCommand.equals("EEB3")|| sCommand.equals("EEB5")|| sCommand.equals("EEB6")||
	           sCommand.equals("FD00") || sCommand.equals("FD01")|| sCommand.equals("FD02")|| sCommand.equals("FD03")|| sCommand.equals("FD04")||
	           sCommand.equals("FE01") || sCommand.equals("FE03")|| sCommand.equals("FE06")|| sCommand.equals("FE08")|| sCommand.equals("FE0C")||
	           sCommand.equals("FE0E") || sCommand.equals("FE14")|| sCommand.equals("FE16")|| sCommand.equals("FE18")||
	           sCommand.equals("FE26") || sCommand.equals("FE28")|| sCommand.equals("FE2A")||
	           sCommand.equals("FE1A") || sCommand.equals("FE1C")||sControlCode.equals("88")||sControlCode.equals("C8")){ //广东规约自定义命令:FD
	           sDataContent=DataArea.substring(6,DataArea.length());
	           if (sDataContent.length()==2||sControlCode.equals("88")||(sDataContent.length()==0&&(sCommand.equals("EEB5")||sCommand.equals("EEB6")))){
	             NormalData.DataItemAdd(sCommand,"88");  //设置成功(需要跟主站约定)
	           }
	           else{
	             NormalData.DataItemAdd(sCommand,"C8");  //设置失败(需要跟主站约定)
	           }
	        }
	        else{
	          if(sCommand.equals("EEA0")|| sCommand.equals("EEA1")|| sCommand.equals("EEA2")|| sCommand.equals("EEA3")||    //有序用电数据读取返回
	            sCommand.equals("EEA4")|| sCommand.equals("EEA5")|| sCommand.equals("EEA6")|| sCommand.equals("0001")||
	            sCommand.equals("FE00")|| sCommand.equals("FE02")|| sCommand.equals("FE04")|| sCommand.equals("FE05")||
	            sCommand.equals("FE07")|| sCommand.equals("FE0A")|| sCommand.equals("FE0B")|| sCommand.equals("FE0D")||
	            sCommand.equals("FE15")|| sCommand.equals("FE17")|| sCommand.equals("FE19")|| sCommand.equals("FE1B")||
	            sCommand.equals("FE27")|| sCommand.equals("FE29")|| sCommand.equals("FE2B")||
	            sCommand.equals("FD05")|| sCommand.equals("FD06")){
	            sDataContent=DataArea.substring(6,DataArea.length());
	          }
	          else{
	            sDataContent=DataArea.substring(8,DataArea.length());
	          }
	          int iCommandCount=CommandInfoList.GetFCommandCount();
	          for (int i=0;i<iCommandCount;i++){
	            Command=((SFE_CommandInfo)(CommandInfoList.CommandInfoList.get(i))).GetFProtocolCommand();
	            if (Command.equals(sCommand)) {//查找是否存在当前命令
	              iCommandLen=2*((SFE_CommandInfo)(CommandInfoList.CommandInfoList.get(i))).GetFCommandLen();//得到此命令的数据区字符长度=字节长度*2
	              if (iCommandLen==0){//不定长类型长度为0
	                iCommandLen=sDataContent.length();
	              }
	              NormalData=GluMethodZheJiang.ExplainNormalPhysicalData(sDataContent.substring(0,iCommandLen),i,CommandInfoList,0,0);//解释该命令的物理数据
	              break;
	            }
	          }
	        }
	        NormalData.SetMeasuredPointNo(0);//自定义命令测量点号默认为0
	        NormalData.SetMeasuredPointType(10);
	      }
	      catch(Exception e){
	        Glu_ConstDefine.Log1.WriteLog("Func:FrameDataAreaExplainZheJiang__ExplainManufacturerFrame();Error:"+e.toString());
	      }
	    }
	    finally{
	    }
	    return NormalData;
	  }


	}

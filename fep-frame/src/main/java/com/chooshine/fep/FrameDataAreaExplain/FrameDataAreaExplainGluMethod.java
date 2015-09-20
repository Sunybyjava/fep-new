package com.chooshine.fep.FrameDataAreaExplain;

import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.Connection;
import java.util.ArrayList;
import com.chooshine.fep.ConstAndTypeDefine.Glu_ConstDefine;

public class FrameDataAreaExplainGluMethod { //数据区解析公用函数类
	
    public FrameDataAreaExplainGluMethod() {
        try {
            jbInit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public SPE_CommandInfoList InitialCommandInfoList(int ProtocolType) { //初始化命令信息队列
        SPE_CommandInfoList CommandInfoList = new SPE_CommandInfoList();
        try {
            try {
              /*  String sSQL1 = "";
                String sSQL2 = " AND a.CSLX=10 ORDER BY a.SJYBS,a.CSLX,a.SJXH";
                String sSqlStr = "";
                if (ProtocolType >= 11 && ProtocolType <= 22) { //电表规约
                    sSQL1 = "SELECT a.SJYBS,a.SJXBS,a.SJXH,a.CSLX,a.SJLX,a.SJGS,a.SJCD,a.CCSX,a.CCLX FROM DATA_ITEM_DB a WHERE a.GYH=";
                } else { //终端规约
                    sSQL1 = "SELECT a.SJYBS,a.SJXBS,a.SJXH,a.CSLX,a.SJLX,a.SJGS,a.SJCD,a.CCSX,a.CCLX FROM DATA_ITEM_ZD a WHERE a.GYH=";
                }
                sSqlStr = sSQL1 + ("" + ProtocolType) + sSQL2;
                */
                String sSQL1 = "";
                String sSQL2 = " ORDER BY SJYBS,CSLX,SJXH";
                String sSqlStr = "";
                String sSQL3 = "";
                if (ProtocolType >= 11 && ProtocolType <= 22) { //ammeter
                    sSQL1 = "SELECT a.SJYBS,a.SJXBS,a.SJXH,a.CSLX,a.SJLX,a.SJGS,a.SJCD,a.CCSX,a.CCLX FROM DATA_ITEM_DB a WHERE a.CSLX=10 AND a.GYH=";
                } else { //terminal
                    sSQL1 = "SELECT a.SJYBS,a.SJXBS,a.SJXH,a.CSLX,a.SJLX,a.SJGS,a.SJCD,a.CCSX,a.CCLX FROM DATA_ITEM_ZD a WHERE a.CSLX=10 AND a.GYH=";
                }
                if (ProtocolType == Glu_ConstDefine.GY_ZD_DLMS){//Profile data of dlms
                	sSQL3 = " UNION "
                		+ " SELECT '000700'||SUBSTR(TO_CHAR(A.RWH,'00'),2,2)||'180300FF02' AS SJYBS,"
                		+ " B.SJXBS,A.SJYXH as sjxh,B.CSLX,B.SJLX,B.SJGS,B.SJCD,B.CCSX,B.CCLX"
                		+ " FROM RW_RWXX A,DATA_ITEM_ZD B"
                		+ " WHERE A.SJYBS= B.SJYBS AND A.GYH="
                		+ ("" + ProtocolType);
                }
                sSqlStr = sSQL1 + ("" + ProtocolType) + sSQL3 + sSQL2;
                
                SFE_CommandInfo CommandInfo = new SFE_CommandInfo();
                Connection conn = DriverManager.getConnection("", "gd", "gd");
                Statement stmt = conn.createStatement(ResultSet.
                        TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY); //创建一个可以滚动的只读的SQL语句对象
                ResultSet rset = stmt.executeQuery(sSqlStr);

                String sOldDataCaption = "";
                int iCommandCount = 0;
                String sDataCaption = ""; //数据项标识
                int iDataType = 0; //数据类型
                int iDataLen = 0; //数据长度
                String sDataFormat = ""; //数据项数据格式
                int iStorageType = 0; //存储方式（10 BCD;20 HEX;30 ASCII）
                int iStorageOrder = 0; //存储顺序（10顺序  20逆序）

                rset.next();
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
                        iStorageOrder = Integer.parseInt(rset.getString("CCSX"));
                        CommandInfo.DataItemInfoAdd(sDataCaption, iDataType,
                                iDataLen, sDataFormat, iStorageType,
                                iStorageOrder);
                        if (rset.isLast()) {
                            CommandInfoList.CommandInfoList.add(CommandInfo);
                            iCommandCount = iCommandCount + 1;
                        }
                        rset.next();
                    } else {
                        //CommandInfoList.CommandInfoList.ensureCapacity(1000000);
                        CommandInfoList.CommandInfoList.add(CommandInfo);
                        CommandInfo = new SFE_CommandInfo();
                        iCommandCount = iCommandCount + 1;
                        sOldDataCaption = rset.getString("SJYBS");
                    }
                }
                CommandInfoList.SetFCommandCount(iCommandCount);
                rset.close();
                stmt.close();
                conn.close();
            } catch (Exception e) {
                Glu_ConstDefine.Log1.WriteLog(
                        "Func:FrameDataAreaExplainGluMethod__InitialCommandInfoList();Error:" +
                        e.toString());
            }
        } finally {
        }
        return CommandInfoList;
    }

    /*public SPE_TaskInfoList InitialTaskInfoList(int ProtocolType){//初始化任务信息队列
      SPE_TaskInfoList TaskInfoList=new SPE_TaskInfoList();
      try{
        try {
          String sSQL1="SELECT RWH,RWLX,SJYBS,CLDXH,GYH FROM RW_RWXX WHERE RWLX=1 OR RWLX=2 AND GYH=";
          String sSQL2=" ORDER BY RWH,SJYXH";
          String sSqlStr=sSQL1+(""+ProtocolType)+sSQL2;

          SFE_TaskInfo TaskInfo=new SFE_TaskInfo();
          Connection conn = DriverManager.getConnection(IFE_FrameDataAreaExplain.FConnectString,"gd","gd");
          Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);//创建一个可以滚动的只读的SQL语句对象
          ResultSet rset = stmt.executeQuery(sSqlStr);
          String sOldTaskNo="";
          int iTaskCount=0;
          rset.next();//打开数据集
          while (!rset.isAfterLast()) {
            if (rset.isFirst()){
              sOldTaskNo=rset.getString("RWH");
            }
            if (sOldTaskNo.equals(rset.getString("RWH"))){
              TaskInfo.TaskInfoAdd(Integer.parseInt(sOldTaskNo),Integer.parseInt(rset.getString("RWLX")),Integer.parseInt(rset.getString("GYH")),Integer.parseInt(rset.getString("CLDXH")));
              TaskInfo.CommandAdd(rset.getString("SJYBS"));
              if (rset.isLast()){
                TaskInfoList.TaskInfoList.add(TaskInfo);
                iTaskCount=iTaskCount+1;
              }
              rset.next();
            }
            else {
              TaskInfoList.TaskInfoList.add(TaskInfo);
              TaskInfo=new SFE_TaskInfo();
              iTaskCount=iTaskCount+1;
              sOldTaskNo=rset.getString("RWH");
            }
          }
          TaskInfoList.SetFTaskCount(iTaskCount);
          stmt.close();
          conn.close();
        }
        catch (Exception e) {
          System.out.println("数据区解析InitialTaskInfoList出错:"+e.toString());
          Glu_ConstDefine.Log1.WriteLog("报错函数:数据区解析FrameDataAreaExplainGluMethod__InitialTaskInfoList();报错原因:"+e.toString());
        }
      }
      finally{
        return TaskInfoList;
      }
       }*/
    public SFE_NormalData ExplainNormalPhysicalData(String CommandData,
            int CommandOrder, SPE_CommandInfoList CommandInfoList, int GYH,
            int Count) { //解释普通数据区的物理数据
        SFE_NormalData NormalData = new SFE_NormalData();
        try {
            try {
                int iDataItemCount = ((SFE_CommandInfo) (CommandInfoList.
                        CommandInfoList.get(CommandOrder))).GetFDataItemCount();
                int iDataItemLen = 0;
                //String sDataItemData = "";
                String sLogicData = "";
                String sDataCaption = "";
                String sDataFormat = "";
                for (int i = 0; i < iDataItemCount; i++) {
                    sDataFormat = ((SFE_DataItemInfo) ((SFE_CommandInfo) (
                            CommandInfoList.CommandInfoList.get(CommandOrder))).
                                   DataItemInfoList.get(i)).GetFDataFormat().
                                  toUpperCase();
                    if (sDataFormat.equals("X")) { //不定长数据类型
                        iDataItemLen = CommandData.length();
                    } else {
                        //得到数据项字符长度=字节长度*2
                        iDataItemLen = 2 *
                                       ((SFE_DataItemInfo) ((SFE_CommandInfo) (
                                               CommandInfoList.CommandInfoList.
                                               get(CommandOrder))).
                                        DataItemInfoList.get(i)).GetFDataLen();
                    }
                    //得到数据项标识
                    sDataCaption = ((SFE_DataItemInfo) ((SFE_CommandInfo) (
                            CommandInfoList.CommandInfoList.get(
                                    CommandOrder))).DataItemInfoList.get(i)).
                                   GetFDataCaption();
                    //设置物理数据
                    if (iDataItemLen <= CommandData.length()) { //数据长度是否合法
                        ((SFE_DataItemInfo) ((SFE_CommandInfo) (
                                CommandInfoList.CommandInfoList.get(
                                        CommandOrder))).DataItemInfoList.get(i)).
                                SetFPhysicalData(CommandData.substring(0,
                                iDataItemLen));
                        //物理数据转换成逻辑数据
                        if (((SFE_DataItemInfo) ((SFE_CommandInfo) (
                                CommandInfoList.CommandInfoList.get(
                                        CommandOrder))).DataItemInfoList.
                             get(i)).PhysicalDataToLogicData()) {
                            //得到逻辑数据
                            sLogicData = ((SFE_DataItemInfo) ((
                                    SFE_CommandInfo) (
                                            CommandInfoList.CommandInfoList.
                                            get(CommandOrder))).
                                          DataItemInfoList.get(i)).
                                         GetFLogicData();
                        } else {
                            sLogicData = "-1";
                        }
                        //消去已经解释的数据项长度
                        CommandData = CommandData.substring(iDataItemLen,
                                CommandData.length());
                    } else {
                        sLogicData = "-1";
                    }
                    NormalData.DataItemAdd(sDataCaption, sLogicData);
                }
            } catch (Exception e) {
                Glu_ConstDefine.Log1.WriteLog(
                        "Func:FrameDataAreaExplainGluMethod__ExplainNormalPhysicalData();Error:" +
                        e.toString());
            }
        } finally {
        }
        return NormalData;
    }

    public int[] ExplainMeasuredPointList(String MeasuredSign, int ProtocolType) { //解释测量点信息得到测量点列表
        int iCount = 0;
        int[] iMeasuredSignList = new int[64];
        try {
            try {
                if (ProtocolType >= Glu_ConstDefine.GY_ZD_ZHEJIANG && ProtocolType <= 89) { //浙规系列
                    String sMeasured = "";
                    for (int i = 0; i < 8; i++) {
                        sMeasured = DataSwitch.Fun2HexTo8Bin(MeasuredSign.
                                substring(i * 2, i * 2 + 2));
                        for (int j = 7; j >= 0; j--) {
                            if (sMeasured.substring(j, j + 1).equals("1")) {
                                iMeasuredSignList[iCount] = i * 8 + 7 - j;
                                iCount = iCount + 1;
                            }
                        }
                    }
                } else if (ProtocolType == Glu_ConstDefine.GY_ZD_QUANGUO || ProtocolType == Glu_ConstDefine.GY_ZD_IHD) { //全国规约、天津模块表
                    String sDA1 = MeasuredSign.substring(0, 2); //信息点元
                    String sDA2 = MeasuredSign.substring(2, 4); //信息点组
                    if (sDA1.equals("00")) { //测量点号为0
                        iCount = 1;
                        iMeasuredSignList[0] = 0;
                    } else {
                        char[] cDA2, cDA1;
                        int iDA2 = 0;
                        cDA2 = (DataSwitch.Fun2HexTo8Bin(sDA2)).toCharArray();
                        cDA1 = (DataSwitch.Fun2HexTo8Bin(sDA1)).toCharArray();
                        for (int i = 0; i < 8; i++) {
                            if (cDA2[i] == '1') {
                                iDA2 = 8 - i; //信息点组号
                                break;
                            }
                        }
                        for (int i = 0; i < 8; i++) {
                            if (cDA1[i] == '1') {
                                iMeasuredSignList[iCount] = (iDA2 - 1) * 8 + 8 -
                                        i; //信息点序号
                                iCount = iCount + 1;
                            }
                        }
                    }
                } else if (ProtocolType == Glu_ConstDefine.GY_ZD_698 ) { //698规约   
                    String sDA1 = MeasuredSign.substring(0, 2); //信息点元
                    int iDA2 = Integer.parseInt(MeasuredSign.substring(2,4), 16); //信息点组
                    if (sDA1.equals("00")) { //测量点号为0
                        iCount = 1;
                        iMeasuredSignList[0] = 0;
                    } else {
                        char[] cDA1;
                        cDA1 = (DataSwitch.Fun2HexTo8Bin(sDA1)).toCharArray();
                        for (int i = 7; i >= 0; i--) {
                            if (cDA1[i] == '1') {
                                iMeasuredSignList[iCount] = (iDA2 - 1) * 8 + 8 -
                                        i; //信息点序号
                                iCount = iCount + 1;
                            }
                        }
                    }
                } else if (ProtocolType == 110) { //华隆集抄
                    int iObjectType = Integer.parseInt(MeasuredSign.substring(0,
                            2), 16) & 7; //低3位表示对象类型
                    iCount = Integer.parseInt(MeasuredSign.substring(2, 4), 16); //对象个数
                    MeasuredSign = MeasuredSign.substring(4,
                            MeasuredSign.length());
                    if (iCount == 0) { //终端参数
                        iMeasuredSignList[0] = 0;
                        iCount = 1;
                    } else {
                        if (iObjectType == 0 || iObjectType == 1 ||
                            iObjectType == 2 || iObjectType == 4) {
                            for (int i = 0; i < iCount; i++) {
                                if (iObjectType == 1) { //台区表
                                    iMeasuredSignList[i] = Integer.parseInt(
                                            MeasuredSign.substring(4, 6), 16);
                                } else { //终端,采集器和电表
                                    iMeasuredSignList[i] = Integer.parseInt(
                                            MeasuredSign.substring(4, 6), 16) *
                                            1000 +
                                            Integer.parseInt(MeasuredSign.
                                            substring(0, 2), 16);
                                }
                                MeasuredSign = MeasuredSign.substring(6,
                                        MeasuredSign.length());
                            }
                        }
                    }
                }/* else if (ProtocolType == Glu_ConstDefine.GY_ZD_GUYUAN) {
                    MeasuredSign = DataSwitch.ReverseStringByByte(MeasuredSign);
                    if (MeasuredSign.equals("0000")) {
                        iCount = 1;
                        iMeasuredSignList[0] = 0;
                    } else {
                        iCount = 1;
                        iMeasuredSignList[0] = Integer.parseInt(MeasuredSign,
                                16);
                    }
                }*/
            } catch (Exception e) {
                Glu_ConstDefine.Log1.WriteLog(
                        "Func:FrameDataAreaExplainGluMethod__GetMeasuredPointList();Error:" +
                        e.toString());
            }
        } finally {            
        }
        int[] MeasuredSignList;
        MeasuredSignList = new int[iCount];
        for (int i = 0; i < iCount; i++) {
            MeasuredSignList[i] = iMeasuredSignList[i];
        }  
        return MeasuredSignList;        
    }

    public SFE_NormalDataTemp CommandSearchAndExplain(String FrameDataArea,
            String DataCaption, SPE_CommandInfoList CommandInfoList, int GYH,
            int Count, int Fn) { //查找命令并解释，返回处理数据长度
        SFE_NormalDataTemp NormalDataTemp = new SFE_NormalDataTemp();
        try {
            try {
                SFE_NormalData NormalData = new SFE_NormalData();
                //String sDataFormat = "";
                int iCommandLen = 0;
                int iCommandCount = CommandInfoList.GetFCommandCount();
                String Command = "";
                for (int j = 0; j < iCommandCount; j++) {
                    Command = ((SFE_CommandInfo) (CommandInfoList.
                                                  CommandInfoList.get(j))).
                              GetFProtocolCommand();
                    if (DataCaption.equals(Command)) { //查找是否存在当前命令
                        iCommandLen = 2 *
                                      ((SFE_CommandInfo) (CommandInfoList.
                                CommandInfoList.
                                get(j))).GetFCommandLen(); //得到此命令的数据区字符长度=字节长度*2
                        if (iCommandLen == 0 && !Command.equals("000000")) { //不定长类型长度为0,000000表示无召测数据项
                            iCommandLen = FrameDataArea.length();
                        } else if (GYH == Glu_ConstDefine.GY_ZD_QUANGUO && Count > 0) { //谐波
                            if (Fn == 58) { //一类数据的F58很特殊，前3个数据项电压有总，后3个数据项电流没有总
                                iCommandLen = 3 * Count * 4 +
                                              3 * (Count - 1) * 4;
                            } else {
                                iCommandLen = Count * iCommandLen;
                            }
                        }
                        if (iCommandLen <= FrameDataArea.length()) { //如果任务数据长度合法
                            NormalData = ExplainNormalPhysicalData(
                                    FrameDataArea.substring(0, iCommandLen), j,
                                    CommandInfoList, GYH, Count); //解释该命令的物理数据
                            NormalDataTemp.SetDataItemCount(NormalData.
                                    GetDataItemCount());
                            NormalDataTemp.DataItemList = NormalData.
                                    DataItemList;
                        } else { //数据长度不够
                            iCommandLen = -1;
                        }
                        NormalDataTemp.SetCommandLen(iCommandLen); //命令实际长度
                        break;
                    }
                }
            } catch (Exception e) {                
                Glu_ConstDefine.Log1.WriteLog(
                        "Func:FrameDataAreaExplainGluMethod__CommandSearchAndExplain();Error:" +
                        e.toString());
            }
        } finally {
        }
        return NormalDataTemp;
    }
    public SFE_NormalDataTemp DLMSCommandSearchAndExplain(String FrameDataArea,
            String DataCaption, SPE_CommandInfoList CommandInfoList, int GYH) { //查找命令并解释，返回处理数据长度
        SFE_NormalDataTemp NormalDataTemp = new SFE_NormalDataTemp();
        try {
            try {
                SFE_NormalData NormalData = new SFE_NormalData();
                //String sDataFormat = "";
                int iCommandLen = 0;
                int iFrameLen = FrameDataArea.length();
                int iCommandCount = CommandInfoList.GetFCommandCount();
                String CommandData = FrameDataArea;
                String Command = "";
                for (int j = 0; j < iCommandCount; j++) {
                    Command = ((SFE_CommandInfo) (CommandInfoList.CommandInfoList.get(j))).
                              GetFProtocolCommand();
                    if (DataCaption.equals(Command)) { //查找是否存在当前命令                    
                        try {
                            int iDataItemCount = ((SFE_CommandInfo) (CommandInfoList.
                                    CommandInfoList.get(j))).GetFDataItemCount();
                            if (iDataItemCount > 1 && CommandData.substring(0,2).equals("02")){//消去结构体类型及结构元素数
                            	CommandData = CommandData.substring(4,CommandData.length());
                            	iCommandLen = iCommandLen + 4;
                            }
                            int iDataItemLen = 0;
                            String sLogicData = "";
                            String sDataCaption = "";
                            for (int i = 0; i < iDataItemCount; i++) {                                
                                //得到数据项字符长度                               
                                int iDataType = ((SFE_DataItemInfo) ((SFE_CommandInfo) (
	                                                CommandInfoList.CommandInfoList.get(j))).
	                                                DataItemInfoList.get(i)).GetFDataType();
                                switch (iDataType){
                                    case 11: case 14: case 31: //delete datatype
                                    	CommandData = CommandData.substring(2,CommandData.length());
                                    	iDataItemLen = 2 * ((SFE_DataItemInfo) ((SFE_CommandInfo) (
                                    			CommandInfoList.CommandInfoList.get(j))).
                                    			DataItemInfoList.get(i)).GetFDataLen();
                                    	iCommandLen = iCommandLen + 2 + iDataItemLen;
                                    	break;
                                    case 12: case 21: case 30: //delete datatype and length
                                    	if(CommandData.substring(0, 4).equals("0D08")){
                                    		iDataItemLen = 6;
                                    	}else{
                                    		iDataItemLen = 2 * Integer.parseInt(CommandData.substring(2, 4), 16);
                                    	}                                    	
                                        CommandData = CommandData.substring(4,CommandData.length());
                                        iCommandLen = iCommandLen + 4 + iDataItemLen;
                                        break;
                                    case 13: case 22:  //no datatype
                                    	CommandData = CommandData.substring(0,CommandData.length());
                                    	iDataItemLen = 2 * ((SFE_DataItemInfo) ((SFE_CommandInfo) (
                                    			CommandInfoList.CommandInfoList.get(j))).
                                    			DataItemInfoList.get(i)).GetFDataLen();
                                    	iCommandLen = iCommandLen + iDataItemLen;
                                    	break;
                                    case 40:
                                    	iDataItemLen = CommandData.length();
                                    	iCommandLen = iCommandLen + iDataItemLen;
                                        break;
                                    case 51:
                                    	iDataItemLen = CommandData.length();
                                    	iCommandLen = iCommandLen + iDataItemLen;
                                        break;
                                }                          
                                //得到数据项标识
                                sDataCaption = ((SFE_DataItemInfo) ((SFE_CommandInfo) (
                                        CommandInfoList.CommandInfoList.get(
                                                j))).DataItemInfoList.get(i)).
                                               GetFDataCaption();
                                if (iDataItemLen == 0){
                                	sLogicData = "-2";
                                	NormalData.DataItemAdd(sDataCaption, sLogicData);
                                    NormalDataTemp.SetDataItemCount(NormalData.
                                            GetDataItemCount());
                                    NormalDataTemp.DataItemList = NormalData.
                                            DataItemList;
                                }else {
                                	if (iDataType == 51){//structure
                                    	int iCount = Integer.parseInt(CommandData.substring(2,4),16);
                                    	if (DataCaption.equals("001600000F0001FF04")){
                                    		iCount = 1;
                                    	}
                                    	CommandData = CommandData.substring(4,CommandData.length());
                                    	for (int k = 0; k < iCount; k++){
                                    		((SFE_DataItemInfo) ((SFE_CommandInfo) (
                                                    CommandInfoList.CommandInfoList.get(
                                                            j))).DataItemInfoList.get(i)).
                                                    SetFPhysicalData(CommandData);
                                            //物理数据转换成逻辑数据
                                            if (((SFE_DataItemInfo) ((SFE_CommandInfo) (
                                                    CommandInfoList.CommandInfoList.get(
                                                            j))).DataItemInfoList.
                                                 get(i)).PhysicalDataToLogicData()) {
                                                //得到逻辑数据
                                                sLogicData = ((SFE_DataItemInfo) ((
                                                        SFE_CommandInfo) (CommandInfoList.CommandInfoList.
                                                                get(j))).DataItemInfoList.get(i)).
                                                                GetFLogicData();
                                            } else {
                                                sLogicData = "-1";
                                            }
                                            CommandData = ((SFE_DataItemInfo) ((SFE_CommandInfo) (
                                                            CommandInfoList.CommandInfoList.
                                                            get(j))).DataItemInfoList.get(i)).GetFPhysicalData();
                                            NormalData.DataItemAdd(sDataCaption, sLogicData);
                                            NormalDataTemp.SetDataItemCount(NormalData.
                                                    GetDataItemCount());
                                            NormalDataTemp.DataItemList = NormalData.
                                                    DataItemList;
                                    	}
                                    	iCommandLen = iFrameLen - CommandData.length(); //命令实际长度
                                    }else {
                                    	//设置物理数据
                                        if (iDataItemLen <= CommandData.length()) { //数据长度是否合法
                                            ((SFE_DataItemInfo) ((SFE_CommandInfo) (
                                                    CommandInfoList.CommandInfoList.get(
                                                            j))).DataItemInfoList.get(i)).
                                                    SetFPhysicalData(CommandData.substring(0,
                                                    iDataItemLen));
                                            //物理数据转换成逻辑数据
                                            if (((SFE_DataItemInfo) ((SFE_CommandInfo) (
                                                    CommandInfoList.CommandInfoList.get(
                                                            j))).DataItemInfoList.
                                                 get(i)).PhysicalDataToLogicData()) {
                                                //得到逻辑数据
                                                sLogicData = ((SFE_DataItemInfo) ((
                                                        SFE_CommandInfo) (
                                                                CommandInfoList.CommandInfoList.
                                                                get(j))).
                                                              DataItemInfoList.get(i)).
                                                             GetFLogicData();
                                            } else {
                                                sLogicData = "-1";
                                            }
                                            //消去已经解释的数据项长度
                                            CommandData = CommandData.substring(iDataItemLen,
                                                    CommandData.length());
                                        } else {
                                            sLogicData = "-1";
                                        }

                                        NormalData.DataItemAdd(sDataCaption, sLogicData);
                                        NormalDataTemp.SetDataItemCount(NormalData.
                                                GetDataItemCount());
                                        NormalDataTemp.DataItemList = NormalData.
                                                DataItemList;
                                    } 
                                }                                                               
                            }
                        } catch (Exception e) {
                            Glu_ConstDefine.Log1.WriteLog(
                                    "Func:FrameDataAreaExplainGluMethod__ExplainNormalPhysicalData();Error:" +
                                    e.toString());
                        }
                  
                        NormalDataTemp.SetCommandLen(iCommandLen); //命令实际长度
                        break;
                    }
                }
            } catch (Exception e) {
                Glu_ConstDefine.Log1.WriteLog(
                        "Func:FrameDataAreaExplainGluMethod__DLMSCommandSearchAndExplain();Error:" +
                        e.toString());
            }
        } finally {
        }
        return NormalDataTemp;
    }
    public ArrayList<String> ExplainCommandList(String sDT, String sGNM) { //解释国网命令列表
        ArrayList <String>sCommandList = new ArrayList<String>();
        try {
            try {
                String sDT1 = sDT.substring(0, 2); //信息类元
                int iDT2 = Integer.parseInt(sDT.substring(2, 4), 16); //信息类组
                char[] cDT1 = (DataSwitch.Fun2HexTo8Bin(sDT1)).toCharArray();
                int iFn = 0;
                if (sDT1.equals("00") && iDT2 == 0) { //无效数据
                    sCommandList.add("000000");
                } else {
                    for (int i = 7; i >= 0; i--) {
                        if (cDT1[i] == '1') {
                            iFn = iDT2 * 8 + 8 - i; //Fn
                            if (sGNM.equals("C1")||sGNM.equals("C0")){
                            	sDT = "G" + DataSwitch.StrStuff("0", 3,
                                		Integer.toString(iFn), 10) + "00"; //得到命令
                            } else if (sGNM.equals("C2")){
                            	sDT = "H" + DataSwitch.StrStuff("0", 3,
                                		Integer.toString(iFn), 10) + "00"; //得到命令
                            }else {
                            	sDT = sGNM.substring(1, 2) +
                                	DataSwitch.StrStuff("0", 3,
                                		Integer.toString(iFn), 10) + "00"; //得到命令
                            }
                            
                            sCommandList.add(sDT);
                        }
                    }
                }
            } catch (Exception e) {
                Glu_ConstDefine.Log1.WriteLog(
                        "Func:FrameDataAreaExplainGluMethod__ExplainCommandList();Error:" +
                        e.toString());
            }
        } finally {
        }
        return sCommandList;
    }

    public int GetGuYuanDataType(String DT, String FunctionCode) { //得到数据类型:10普通数据;11中继数据(普通数据结构);21小时冻结数据;22曲线数据;23日冻结数据;24月冻结数据;25天津模块表主动上送数据;30告警数据;40设置返回数据
        int iResult = 0;
        try {
            try {
                if (FunctionCode.equals("00")) { //确认/否认
                    iResult = 40;
                } else if (FunctionCode.equals("0A")) { //读取终端、测量点参数
                    iResult = 10;
                } else if (FunctionCode.equals("0E")) { //三类数据(告警数据)
                    iResult = 30;
                } else if (FunctionCode.equals("10")) { //中继数据
                    if (DT.equals("0119")){
                        iResult = 15;
                    }else{
                        iResult = 11;
                    }
                } else if (FunctionCode.equals("11")) { //天津模块表主动上送数据
                    iResult = 25;
                } else if (FunctionCode.equals("0C") ||
                           FunctionCode.equals("0D") ||
                           FunctionCode.equals("C0")) { //一类数据(当前数据、小时冻结数据),二类数据(历史数据),自定义的一、二类数据
                    int iFn = 0;
                    ArrayList CommandList = new ArrayList();
                    CommandList = ExplainCommandList(DT, FunctionCode);
                    iFn = Integer.parseInt(CommandList.get(0).toString().
                                           substring(1, 4)); //用第一个命令的Fn来判断全部命令的数据类型
                    if (FunctionCode.equals("0C")) { //一类数据
                        if (iFn >= 109 && iFn <= 112) { //小时冻结数据
                            iResult = 21;
                        } else { //普通数据(当前数据)
                            iResult = 10;
                        }
                    } else if (FunctionCode.equals("0D")) { //二类数据的曲线数据、
                        if ((iFn >= 101 && iFn <= 104) || iFn == 234) {
                            iResult = 22; //曲线数据
                        } else if (((iFn == 49) ||
                                    (iFn >= 201 && iFn <= 219) ||
                                    (iFn == 235))) {
                            iResult = 23; //日冻结数据
                        } else {
                            iResult = 24; //月冻结数据
                        }
                    } else if (FunctionCode.equals("C0")) { //自定义数据
                        if (iFn == 105 || iFn == 106) { //日冻结数据
                            iResult = 23;
                        } else { //普通数据
                            iResult = 10;
                        }
                    }
                }
            } catch (Exception e) {
                Glu_ConstDefine.Log1.WriteLog(
                        "Func:FrameDataAreaExplainGluMethod__GetQuanGuoDataType();Error:" +
                        e.toString());
            }
        } finally {
        }
        return iResult;
    }
    
    public int GetHeXingDataType(String CommandID,String Content, String FunctionCode, String ControlCode) { //得到数据类型:10普通数据;11中继数据(普通数据结构);21小时冻结数据;22曲线数据;23日冻结数据;24月冻结数据;25天津模块表主动上送数据;30告警数据;40设置返回数据
    	int iResult = 0;
        try {
        	if (ControlCode.substring(1,2).equals("1") && FunctionCode.equals("81")) { //否认
                iResult = 40;
            } else if ((CommandID.equals("1011") || CommandID.equals("1012") || CommandID.equals("1013")
            		 || CommandID.equals("1014") || CommandID.equals("1041") || CommandID.equals("2001")
            		 || CommandID.equals("2041") || CommandID.equals("2002") || CommandID.equals("2042")
            		 || CommandID.equals("2040") || CommandID.equals("2052") || CommandID.equals("1051")) 
            		&& (Content.equals(""))){//设置成功
            	iResult = 40;
            } else if ((CommandID.equals("2061") || CommandID.equals("2062"))) { //单、三相表事件
            	if (FunctionCode.equals("82")){  //主动上送
            		iResult = 30;
            	}else if (FunctionCode.equals("81")){  //事件读取
            		iResult = 31;
            	}                
            } else if (FunctionCode.equals("82")) { //主动上报非告警
            	iResult = 20;
            } else if (CommandID.equals("3001")){
            	iResult = 22; //曲线数据
            } else if (CommandID.equals("2004") || CommandID.equals("2044") || CommandID.equals("2020")
            		|| CommandID.equals("204B") || CommandID.equals("200A") || CommandID.equals("202A")){
            	iResult = 23; //日冻结数据
            } else if (CommandID.equals("2005") || CommandID.equals("2045") || CommandID.equals("2021")
            		|| CommandID.equals("204C") || CommandID.equals("200B") || CommandID.equals("202B")){
            	iResult = 24; //月冻结数据
            } else if (CommandID.equals("1015") || CommandID.equals("3002") || CommandID.equals("3003") 
            		|| CommandID.equals("3004") || CommandID.equals("3005") || CommandID.equals("2018") 
            		|| CommandID.equals("2048") || CommandID.equals("2009") || CommandID.equals("2019") 
            		|| CommandID.equals("204A") ){
            	iResult = 25;
            }else { //读取终端、测量点参数,测量点数据
                iResult = 10;
            } 
        	
        } catch (Exception e) {
            Glu_ConstDefine.Log1.WriteLog(
                    "Func:FrameDataAreaExplainGluMethod__GetHeXingDataType();Error:" +
                    e.toString());
        }
        return iResult;
    }

    public int GetQuanGuo698DataType(String DT, String FunctionCode) { //得到数据类型:10普通数据;11中继数据(普通数据结构);21小时冻结数据;22曲线数据;23日冻结数据;24月冻结数据;25天津模块表主动上送数据;30告警数据;40设置返回数据
        int iResult = 0;
        try {
            try {
                if (FunctionCode.equals("00")) { //确认/否认
                    iResult = 40;
                } else if ((FunctionCode.equals("0A"))||(FunctionCode.equals("03"))||
                		   (FunctionCode.equals("09")) ){ //读取终端、测量点参数
                	iResult = 10;                    
                } else if (FunctionCode.equals("0E")) { //三类数据(告警数据)
                    iResult = 30;
                } else if (FunctionCode.equals("10")) { //中继数据
                    iResult = 11;
                } else if (FunctionCode.equals("0C") || //一类数据(当前数据、小时冻结数据)
                           FunctionCode.equals("0D") || //二类数据(历史数据)
                           FunctionCode.equals("C0") ) { //自定义命令
                    int iFn = 0;
                    ArrayList CommandList = new ArrayList();
                    CommandList = ExplainCommandList(DT, FunctionCode);
                    iFn = Integer.parseInt(CommandList.get(0).toString().
                                           substring(1, 4)); //用第一个命令的Fn来判断全部命令的数据类型
                    if (FunctionCode.equals("0C")) { //一类数据
                        if ((iFn >= 81 && iFn <= 121) || (iFn >= 457 && iFn <= 520)) { //小时冻结数据
                            iResult = 21;
                        } else { //普通数据(当前数据)
                            iResult = 10;
                        }
                    } else if (FunctionCode.equals("0D")) { //二类数据的曲线数据、
                        if ((iFn >= 73 && iFn <= 110) || 
                        	(iFn >= 138 && iFn <= 148)|| 
                        	(iFn >= 217 && iFn <= 218)|| 
                        	(iFn >= 401 && iFn <= 716)||
                        	(iFn == 393) ||
                        	(iFn >= 396 && iFn <= 399)) {
                            iResult = 22; //曲线数据
                        } else if ((iFn >= 1    && iFn <= 8) ||
                                    (iFn >= 25  && iFn <= 32) ||
                                    (iFn >= 41  && iFn <= 43) ||
                                    (iFn >= 49  && iFn <= 50) ||
                                    (iFn == 53)               ||
                                    (iFn >= 57  && iFn <= 59) ||
                                    (iFn >= 113 && iFn <= 129)||
                                    (iFn >= 153 && iFn <= 156)||
                                    (iFn >= 161 && iFn <= 168)||
                                    (iFn >= 185 && iFn <= 188)||
                                    (iFn == 209 )) {
                            iResult = 23; //日冻结数据
                        } else if ((iFn >= 9 && iFn <= 12) ||
                                (iFn >= 169 && iFn <= 176)||
                                (iFn >= 189 && iFn <= 192)||
                                (iFn == 394)||(iFn == 395)) {
                        	iResult = 26; //抄表日冻结数据
                        } else {
                            iResult = 24; //月冻结数据
                        }
                    } else if (FunctionCode.equals("C0")) { //自定义数据
                        if (iFn == 105 || iFn == 106) { //日冻结数据
                            iResult = 23;
                        } else { //普通数据
                            iResult = 10;
                        }
                    }
                }
            } catch (Exception e) {
                Glu_ConstDefine.Log1.WriteLog(
                        "Func:FrameDataAreaExplainGluMethod__GetQuanGuo698DataType();Error:" +
                        e.toString());
            }
        } finally {
        }
        return iResult;
    }


    public int GetQuanGuoDataType(String DT, String FunctionCode) { //得到数据类型:10普通数据;11中继数据(普通数据结构);21小时冻结数据;22曲线数据;23日冻结数据;24月冻结数据;25天津模块表主动上送数据;30告警数据;40设置返回数据
        int iResult = 0;
        try {
            try {
                if (FunctionCode.equals("00")) { //确认/否认
                    iResult = 40;
                } else if ((FunctionCode.equals("0A"))||(FunctionCode.equals("03"))||(FunctionCode.equals("09")) ){ //读取终端、测量点参数
                    iResult = 10;
                } else if (FunctionCode.equals("0E")) { //三类数据(告警数据)
                    iResult = 30;
                } else if (FunctionCode.equals("10")) { //中继数据
                    int iFn = 0;
                    ArrayList CommandList = new ArrayList();
                    CommandList = ExplainCommandList(DT, FunctionCode);
                    iFn = Integer.parseInt(CommandList.get(0).toString().
                                           substring(1, 4)); //用第一个命令的Fn来判断全部命令的数据类型
                    if (iFn == 201) {
                        iResult = 15; //宁夏集抄的点抄返回
                    } else {
                        iResult = 11;
                    }
                } else if (FunctionCode.equals("11")) { //天津模块表主动上送数据
                    iResult = 25;
                } else if (FunctionCode.equals("0C") ||
                           FunctionCode.equals("0D") ||
                           FunctionCode.equals("C0")) { //一类数据(当前数据、小时冻结数据),二类数据(历史数据),自定义的一、二类数据
                    int iFn = 0;
                    ArrayList CommandList = new ArrayList();
                    CommandList = ExplainCommandList(DT, FunctionCode);
                    iFn = Integer.parseInt(CommandList.get(0).toString().
                                           substring(1, 4)); //用第一个命令的Fn来判断全部命令的数据类型
                    if (FunctionCode.equals("0C")) { //一类数据
                        if (iFn >= 81 && iFn <= 121) { //小时冻结数据
                            iResult = 21;
                        } else { //普通数据(当前数据)
                            iResult = 10;
                        }
                    } else if (FunctionCode.equals("0D")) { //二类数据的曲线数据、
                        if ((iFn >= 73 && iFn <= 108) || iFn == 138) {
                            iResult = 22; //曲线数据
                        } else if (((iFn >= 1 && iFn <= 12) ||
                                    (iFn >= 25 && iFn <= 31) ||
                                    (iFn >= 41) && (iFn <= 43) ||
                                    (iFn >= 49 && iFn <= 50) ||
                                    (iFn >= 57 && iFn <= 59) ||
                                    (iFn >= 113 && iFn <= 130))) {
                            iResult = 23; //日冻结数据
                        } else {
                            iResult = 24; //月冻结数据
                        }
                    } else if (FunctionCode.equals("C0")) { //自定义数据
                        if (iFn == 105 || iFn == 106) { //日冻结数据
                            iResult = 23;
                        } else { //普通数据
                            iResult = 10;
                        }
                    }
                }
            } catch (Exception e) {
                Glu_ConstDefine.Log1.WriteLog(
                        "Func:FrameDataAreaExplainGluMethod__GetQuanGuoDataType();Error:" +
                        e.toString());
            }
        } finally {
        }
        return iResult;
    }
    public int GetIHDDataType(String DT, String FunctionCode) { //得到数据类型:10普通数据;11中继数据(普通数据结构);21小时冻结数据;22曲线数据;23日冻结数据;24月冻结数据;25天津模块表主动上送数据;26周冻结数据;30告警数据;40设置返回数据
        int iResult = 0;
        try {
            try {
                if (FunctionCode.equals("00")) { //确认/否认
                    iResult = 40;
                } else if (FunctionCode.equals("0A") || FunctionCode.equals("0C")){ //读取终端、测量点参数、一类当前数据
                    iResult = 10;
                } else if (FunctionCode.equals("0D")) { //二类数据(历史数据)
                    int iFn = 0;
                    ArrayList CommandList = new ArrayList();
                    CommandList = ExplainCommandList(DT, FunctionCode);
                    iFn = Integer.parseInt(CommandList.get(0).toString().
                                           substring(1, 4)); //用第一个命令的Fn来判断全部命令的数据类型
                
                    if (iFn == 1) {
                        iResult = 21; //日冻结数据
                    } else if (iFn == 2) {
                        iResult = 26; //周冻结数据
                    } else if (iFn == 3){
                        iResult = 23; //月冻结数据
                    } else if (iFn == 4){
                        iResult = 24; //年冻结数据
                    }                    
                }
            } catch (Exception e) {
                Glu_ConstDefine.Log1.WriteLog(
                        "Func:FrameDataAreaExplainGluMethod__GetIHDDataType();Error:" +
                        e.toString());
            }
        } finally {
        }
        return iResult;
    }

    public int GetSelfDefineTaskNo(int GYH, String sCommand,
                                   SPE_TaskInfoList TaskInfoList) {
        int iTaskNo = 0;
        for (int i = 0; i < TaskInfoList.TaskInfoList.size(); i++) {
            int iTaskType = ((SFE_TaskInfo) (TaskInfoList.TaskInfoList.get(
                    i))).GetFTaskType();
            int iCommandFindCount = 0;
            if (iTaskType > 1) {
                String sDataCaption = ((String) ((SFE_TaskInfo) (TaskInfoList.
                        TaskInfoList.get(i))).CommandList.get(0));
                int TaskType = ((SFE_TaskInfo) (TaskInfoList.TaskInfoList.get(i))).
                               GetFTaskType();
                if (GYH != Glu_ConstDefine.GY_ZD_HEXING){
                	int iCommand = Integer.parseInt(sCommand.substring(1, sCommand.length()), 16);
                    int iCaption = Integer.parseInt(sDataCaption.substring(1, sDataCaption.length()), 16);
                    //曲线当前数据需要完全匹配数据项，区别前几日曲线数据
                  /*  if ((GYH == Glu_ConstDefine.GY_ZD_TIANJIN) && (sCommand.substring(0, 1).equals("D")) &&
                        (sCommand.substring(3, 4).equals("0"))) {
                        if (sDataCaption.equals(sCommand)) {
                            iCommandFindCount = 1;
                        } else {
                            iCommandFindCount = 0;
                        }
                    } else if ((GYH == Glu_ConstDefine.GY_ZD_TIANJIN) && (sDataCaption.substring(0,
                            3).equals(sCommand.substring(0, 3)))) {
                        iCommandFindCount = 1;
                    } else if ((GYH == Glu_ConstDefine.GY_ZD_GUYUAN) && (sDataCaption.equals(sCommand))) { //跟数据库命令匹配就累加1
                        iCommandFindCount = 1;
                    } else */if ((GYH == Glu_ConstDefine.GY_ZD_QUANGUO) && (sDataCaption.equals(sCommand))) { //跟数据库命令匹配就累加1
                        iCommandFindCount = 1;
                    } else if ((TaskType == 1301) && (GYH == 105) &&
                               (sCommand.
                                substring(0, 1).equals(sDataCaption.substring(0, 1))) &&
                               ((iCaption - iCommand) % 512 == 0)) {
                        iCommandFindCount = 1;
                    } else {
                        iCommandFindCount = 0;
                    }
                }else {
                	if (sDataCaption.equals(sCommand)){
                		iCommandFindCount = 1;
                	} else {
                        iCommandFindCount = 0;
                    }
                }

                //前几日曲线数据任务号都一致，只需匹配前三位
                if (iCommandFindCount > 0) {
                    iTaskNo = ((SFE_TaskInfo) (TaskInfoList.TaskInfoList.get(i))).
                              GetFTaskNo();
                    break;
                }
            }
        }
        return iTaskNo;
    }


    public ArrayList<SFE_HistoryData> SearchAndMatchingTaskInfoList(ArrayList CommandListTemp,
            SFE_HistoryData[] HistoryDataListTemp, int HistoryDataCount,
            SPE_TaskInfoList TaskInfoList) {
        ArrayList <SFE_HistoryData>HistoryDataList = new ArrayList<SFE_HistoryData>();
        try {
            try {
                //从任务信息队列查找任务号和测量点类型
                int iTaskDataItemCount = 0, iTaskNo = 0, iCommandFindCount = 0,
                        iTaskType = 0, iMeasuredPointNo = 0;
                String sCommand = "", sDataCaption = "", sTaskDateTime = "";
                SFE_HistoryData HistoryData = new SFE_HistoryData();
                for (int i = 0; i < TaskInfoList.TaskInfoList.size(); i++) {
                    iTaskType = ((SFE_TaskInfo) (TaskInfoList.TaskInfoList.get(
                            i))).GetFTaskType();
                    iMeasuredPointNo = ((SFE_TaskInfo) (TaskInfoList.
                            TaskInfoList.get(i))).GetFMeasuredPointNo();
                    if (iTaskType != 10) {
                        iTaskDataItemCount = ((SFE_TaskInfo) (TaskInfoList.
                                TaskInfoList.get(i))).GetFDataItemCount();
                        if (iTaskDataItemCount == CommandListTemp.size()) { //找到跟解释出来的命令数目一致的任务
                            iCommandFindCount = 0;
                            for (int j = 0; j < iTaskDataItemCount; j++) {
                                sDataCaption = ((String) ((SFE_TaskInfo) (
                                        TaskInfoList.TaskInfoList.
                                        get(i))).CommandList.get(j));
                                for (int k = 0; k < CommandListTemp.size(); k++) {
                                    sCommand = CommandListTemp.get(k).toString();
                                    if (sDataCaption.equals(sCommand)) { //跟数据库命令匹配就累加1
                                        iCommandFindCount = iCommandFindCount +
                                                1;
                                    }
                                }
                            }
                            if (iTaskDataItemCount == iCommandFindCount &&
                                (HistoryDataListTemp[0].GetMeasuredPointNo() ==
                                iMeasuredPointNo || iMeasuredPointNo == -1)) { //如果数据库配置的命令跟解释后的命令都匹配
                                iTaskNo = ((SFE_TaskInfo) (TaskInfoList.
                                        TaskInfoList.get(i))).GetFTaskNo();
                                for (int j = 0; j < HistoryDataCount; j++) {
                                    HistoryData.SetTaskNo(iTaskNo); //任务号取自数据库配置
                                    sTaskDateTime = new String(
                                            HistoryDataListTemp[j].
                                            GetTaskDateTime());
                                    HistoryData.SetTaskDateTime(sTaskDateTime);
                                    HistoryData.SetMeasuredPointNo(
                                            HistoryDataListTemp[j].
                                            GetMeasuredPointNo()); //测量点号取自解释出来的测量点号
                                    HistoryData.SetMeasuredAdd(
                                            new String (HistoryDataListTemp[j].GetMeasuredAdd())); //测量点号取自解释出来的测量点地址
                                    HistoryData.SetMeasuredPointType(
                                            HistoryDataListTemp[j].
                                            GetMeasuredPointType());
                                    for (int k = 0;
                                                 k < HistoryDataListTemp[j].
                                                 GetDataItemCount(); k++) {
                                        HistoryData.DataItemList.add(
                                                HistoryDataListTemp[j].
                                                DataItemList.get(k));
                                        HistoryData.DataItemCountAdd();
                                    }
                                    HistoryDataList.add(HistoryData);
                                    HistoryData = new SFE_HistoryData();
                                }
                                break;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                Glu_ConstDefine.Log1.WriteLog("Func:FrameDataAreaExplainQuanGuo__SearchQuanGuoTaskInfoAndReturnHistoryDataList();Error:" +
                                    e.toString());
            }
        } finally {
        }
        return HistoryDataList;
    }

    private void jbInit() throws Exception {
    }
    //----------------------------数据区解析接口调用函数-----------------------------

}

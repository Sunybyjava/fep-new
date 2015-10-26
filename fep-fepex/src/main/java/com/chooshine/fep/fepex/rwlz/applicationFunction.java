package com.chooshine.fep.fepex.rwlz;

import java.util.LinkedList;
import java.util.List;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.chooshine.fep.ConstAndTypeDefine.Glu_DataAccess;
import com.chooshine.fep.FrameDataAreaExplain.IFE_FrameDataAreaExplain;
import com.chooshine.fep.FrameDataAreaExplain.SFE_ParamItem;
import com.chooshine.fep.FrameDataAreaExplain.SFE_QGSer_TimeLabel;
import com.chooshine.fep.communicate.utils;
import com.chooshine.fep.fas.realtimecom.DataContentStruct;
import com.chooshine.fep.fas.realtimecom.RealTimeCommunication;
import com.chooshine.fep.fas.realtimecom.TerminalInfoStruct;
import com.chooshine.fep.fepex.common.CommonClass;
import com.chooshine.fep.fepex.common.TerminalInfo;

/*应用功能类  */
public class applicationFunction {
    public static IFE_FrameDataAreaExplain FrameDataAreaExplain; //数据区解释
    public static Glu_DataAccess dataAccess = null;
    public static List<TerminalTaskInfo> gTaskTerminalList; //任务数据信息列表
    public static List<TerminalTaskInfo> gTerminalList; //终端信息列表

    static {
        init();
    }
    static void init() {
        try {
            Resource result = new ClassPathResource("CommService.config");
            FrameDataAreaExplain = new IFE_FrameDataAreaExplain(result.getURL().getPath());
            dataAccess = new Glu_DataAccess(result.getURL().getPath());
            gTaskTerminalList = new LinkedList<TerminalTaskInfo>();
            gTerminalList = new LinkedList<TerminalTaskInfo>();
        } catch (Exception e) {
        } finally {
        }
    }

    public applicationFunction() {
        try {
            jbInit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static TerminalInfoStruct CopyInfo_TerminalInfo(TerminalInfo ti) {
        TerminalInfoStruct tis = new TerminalInfoStruct();
        String sZDLJDZ = new String(ti.TerminalAddress) ;
        tis.TerminalAddress = sZDLJDZ.toCharArray();
        tis.TerminalProtocol = 100;
        tis.TerminalCommType = 50;
        tis.ArithmeticNo = 0;
        return tis;
    }

    private static DataContentStruct CopyInfo_DataContentStruct(char[] areadata) {
        DataContentStruct dcs = new DataContentStruct();
        dcs.DataContent = areadata;
        dcs.DataContentLength = areadata.length;
        return dcs;
    }

    public static void RealTimeReconnection(RealTimeCommunication rtc) {
       try {
           rtc.DisConnect();
           Thread.sleep(1000);
           rtc.Connect();
           Thread.sleep(1000);
       } catch (Exception ex) {
            System.out.println("ReConnected Error:" + ex.toString());
       }
   }
    
    /**
     * 新增固定功率报文的数据区生成处理
     */
    public static boolean ReadCurrentData(RealTimeCommunication rtc,int TerminalCount,
            List<TerminalInfo> TerminalInfoList)
    {
    	boolean bResult = false; //调用结果
    	String sGnm = "0C";
    	int GYH = 100; //规约号
    	char[] DataArea = null; //数据区
    	SFE_ParamItem[] DataItem = new SFE_ParamItem[1];
    	DataItem[0] = new SFE_ParamItem();
    	DataItem[0].SetParamCaption("C03500");
    	SFE_QGSer_TimeLabel TimeLabel = new SFE_QGSer_TimeLabel();
    	List<DataContentStruct> DataContentInfo = new LinkedList<DataContentStruct>(); //数据区内容列表
    	List<TerminalInfoStruct> terminalInfoStructList = new LinkedList<TerminalInfoStruct>(); //调用前置机接口用的终端信息列表
    	for (int i = 0; i < TerminalCount; i++) {
    		TerminalInfo t = TerminalInfoList.get(i);
    		terminalInfoStructList.add(CopyInfo_TerminalInfo(t));
    		int[] PnList = new int[t.Cldxh.size()];
    		for (int j=0;j<t.Cldxh.size();j++)
    			PnList[j] = t.Cldxh.get(j);
    		DataArea = FrameDataAreaExplain.IFE_QGSer_CurrentDataQuery(GYH, PnList.length, PnList, 1, DataItem, TimeLabel);
    		String stemp = new String(DataArea);
            utils.PrintDebugMessage("数据区：" + stemp + " length:" + stemp.length(),"D");
            DataContentInfo.add(CopyInfo_DataContentStruct(DataArea));
    	}
    	try {
            bResult = rtc.SendBatchToFep(1, TerminalCount,terminalInfoStructList, DataContentInfo, sGnm.toCharArray(),
                    0, 0, 0, 3);
            
            int iRealTime = 0;
            while (!bResult && iRealTime < 3) {
                RealTimeReconnection(rtc);
                bResult = rtc.SendBatchToFep(1,TerminalCount, terminalInfoStructList, DataContentInfo,
                        sGnm.toCharArray(), 1, 0, 0, 3);
                iRealTime = iRealTime + 1;
                Thread.sleep(1000);
            }
            Thread.sleep(1000);
        } catch (Exception ex) {
            System.out.println("数据发送异常！" + ex.toString());
        }
    	return true;
    }

    /*历史数据查询：国网二类数据/230M历史日、月数据查询--发送*/
    /*入参:实时通讯对象,应用ID,终端信息,参数数目,参数列表,时间标签,查询数据类型,数据起始时间,数据密度,数据点数,组合类型*/
    /*查询类型:1-历史日数据时标;2-历史月数据时标*/
    /*返回值:发送成功标志*/
    public static boolean ReadHistoryParameter(RealTimeCommunication
                                               RealTimeCommunication,
                                               int AppID, int TerminalCount,
                                               List<TerminalInfo> TerminalInfoList,
                                               int DataItemCount,
                                               SFE_ParamItem[] DataItem,
                                               SFE_QGSer_TimeLabel TimeLabel,
                                               int QueryDataType,
                                               char[] StartTime,
                                               int DataDensity,
                                               int DataCount, int BuildSign) {
        boolean bResult = false; //调用结果
        int GYH = 0; //规约号
        char[] DataArea = null; //数据区
        String sGnm = "0D";
        int Priority = 3; //读取优先级设置为3
        List<DataContentStruct> DataContentInfo = new LinkedList<DataContentStruct>(); //数据区内容列表
        List<TerminalInfoStruct> terminalInfoStructList = new LinkedList<TerminalInfoStruct>(); //调用前置机接口用的终端信息列表
        for (int i = 0; i < TerminalCount; i++) {
            TerminalInfo strTerminalInfo = (TerminalInfo) TerminalInfoList.get(
                    i);
            terminalInfoStructList.add(CopyInfo_TerminalInfo(strTerminalInfo));
            GYH = strTerminalInfo.TerminalProtocol;
            //测量点信息
            int[] PnList = new int[strTerminalInfo.Cldxh.size()];
            PnList[0] = strTerminalInfo.Cldxh.getFirst();
            //组数据区
            DataArea = FrameDataAreaExplain.IFE_QGSer_HistoryDataQuery(GYH,
                    1, PnList, DataItemCount, DataItem, TimeLabel,
                    QueryDataType, StartTime, DataDensity, DataCount);
            String stemp = new String(DataArea);
            utils.PrintDebugMessage("数据区：" + stemp + " length:" + stemp.length() + " AppID:" + AppID,"D");
            DataContentInfo.add(CopyInfo_DataContentStruct(DataArea));
        }
        try {
            bResult = RealTimeCommunication.SendBatchToFep(AppID, TerminalCount,
                    terminalInfoStructList, DataContentInfo, sGnm.toCharArray(),
                    1, DataCount, 0, Priority);
            int iRealTime = 0;
            while (!bResult && iRealTime < 3) {
                RealTimeReconnection(RealTimeCommunication);
                bResult = RealTimeCommunication.SendBatchToFep(AppID,
                        TerminalCount, terminalInfoStructList, DataContentInfo,
                        sGnm.toCharArray(), 1, DataCount, 0, Priority);
                iRealTime = iRealTime + 1;
                Thread.sleep(CommonClass.FRAME_INTERVAL *1000);
            }
            Thread.sleep(CommonClass.FRAME_INTERVAL *1000);
        } catch (Exception ex) {
            System.out.println("数据发送异常！" + ex.toString());
        }
        return bResult;
    }
    
    private void jbInit() throws Exception {
    }

    public static void main(String[] args) {
        new applicationFunction();
    }
}

package com.chooshine.fep.fepex.rwlz;

import java.util.*;

import com.chooshine.fep.fas.realtimecom.RealTimeCommunication;
import com.chooshine.fep.FrameDataAreaExplain.IFE_FrameDataAreaExplain;
import com.chooshine.fep.FrameDataAreaExplain.SFE_ParamItem;
import com.chooshine.fep.ConstAndTypeDefine.Glu_DataAccess;
import com.chooshine.fep.FrameDataAreaExplain.SFE_QGSer_TimeLabel;
import com.chooshine.fep.communicate.utils;
import com.chooshine.fep.fepex.common.CommonClass;
import com.chooshine.fep.fepex.common.TerminalInfo;
import com.chooshine.fep.fas.realtimecom.TerminalInfoStruct;
import com.chooshine.fep.fas.realtimecom.DataContentStruct;

/*应用功能类  */
public class applicationFunction {
    public static IFE_FrameDataAreaExplain FrameDataAreaExplain; //数据区解释
    public static Glu_DataAccess dataAccess = null;
    public static List gTaskTerminalList; //任务数据信息列表
    public static List gTerminalList; //终端信息列表

    static {
        init();
    }
    static void init() {
        try {
            FrameDataAreaExplain = new IFE_FrameDataAreaExplain();
            dataAccess = new Glu_DataAccess("");
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
        tis.TerminalProtocol = ti.TerminalProtocol;
        tis.TerminalCommType = ti.TerminalCommType;
        tis.ArithmeticNo = ti.Sfbh;
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

    /*历史数据查询：国网二类数据/230M历史日、月数据查询--发送*/
    /*入参:实时通讯对象,应用ID,终端信息,参数数目,参数列表,时间标签,查询数据类型,数据起始时间,数据密度,数据点数,组合类型*/
    /*查询类型:1-历史日数据时标;2-历史月数据时标*/
    /*返回值:发送成功标志*/
    public static boolean ReadHistoryParameter(RealTimeCommunication
                                               RealTimeCommunication,
                                               int AppID, int TerminalCount,
                                               List TerminalInfoList,
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
        List DataContentInfo = new LinkedList(); //数据区内容列表
        List terminalInfoStructList = new LinkedList(); //调用前置机接口用的终端信息列表
        for (int i = 0; i < TerminalCount; i++) {
            TerminalInfo strTerminalInfo = (TerminalInfo) TerminalInfoList.get(
                    i);
            terminalInfoStructList.add(CopyInfo_TerminalInfo(strTerminalInfo));
            GYH = strTerminalInfo.TerminalProtocol;
            //测量点信息
            int[] PnList = new int[64];
            PnList[0] = strTerminalInfo.Cldxh;
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
    
}

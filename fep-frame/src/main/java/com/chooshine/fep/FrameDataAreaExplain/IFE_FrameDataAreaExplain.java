package com.chooshine.fep.FrameDataAreaExplain;

import java.util.ArrayList;
import com.chooshine.fep.ConstAndTypeDefine.*;

import java.util.Properties;
import javax.swing.JOptionPane;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.io.File;
import java.net.URI;

public class IFE_FrameDataAreaExplain { //规约数据区解析实现类
    public static String FFullFileName =
            "./IFE_FrameDataAreaExplainErrorLogger"; //日志文件名

    FrameDataAreaExplainZheJiang FrameDataExplainZheJiang; //浙规数据区解帧类
    FrameDataAreaExplainQuanGuo FrameDataExplainQuanGuo; //国网数据区解帧类
    FrameDataAreaExplainGuYuan FrameDataExplainGuYuan; //固原数据区解帧类
    FrameDataAreaExplainHeXing FrameDataExplainHeXing; //海兴集中器规约数据区解帧类
    FrameDataAreaExplainDLMS FrameDataExplainDLMS; //DLMS规约数据区解帧类
    FrameDataAreaExplainIHD FrameDataExplainIHD; //IHD规约数据区解帧类
    FrameDataAreaExplainGluMethod GluMethod; //数据区解析公用类
    AmmeterFrameExplain AmmeterDataFrameExplain;
    
    //private Object DateFormat;
    //static {InitialList();}
    public static void main(String[] args) {
        IFE_FrameDataAreaExplain FrameDataAreaExplain = new
                IFE_FrameDataAreaExplain("");
        String sFrameDataArea = "00000402E2";
        String sControlCode = "88";
        String sFunctionCode = "0A";
        String sTerminalAddress = "33331111";
        SFE_DataListInfo DataListInfo = new SFE_DataListInfo();
        DataListInfo = FrameDataAreaExplain.IFE_ExplainDataArea(sFrameDataArea.
                toCharArray(), sTerminalAddress.toCharArray(),100, sControlCode.toCharArray(),
                sFunctionCode.toCharArray());
        Glu_ConstDefine.Log1.WriteLog("IFE_ExplainDataArea" + "" +
                           DataListInfo.ExplainResult);
        ArrayList FrameList = new ArrayList();
        char[] cAdd = {'0', '1'};
        char[][] cDataCaption = new char[1][];
        String stemp = "D010";
        cDataCaption[0] = stemp.toCharArray();
        char[] cDate = {'2', '0', '0', '6', '0', '4', '0', '3', '1', '0', '2',
                       '0', '0', '0'};
        FrameList = FrameDataAreaExplain.IFE_TJAmmeterSer_ReadAmmeterData(cAdd,
                13, 20, 1, cDataCaption, cDate, 10);
        Glu_ConstDefine.Log1.WriteLog("IFE_TJAmmeterSer_ReadAmmeterData" + " " +
                           FrameList.get(0).toString());
    }
    public IFE_FrameDataAreaExplain() {
        try {
            InitialList("");
        } catch (Exception e) {
        	Glu_ConstDefine.Log1.WriteLog(e.toString());
            System.out.println("IFE_FrameDataAreaExplain() error:" + e.toString());
        }
        try {
            jbInit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    public IFE_FrameDataAreaExplain(String path) {
        try {
            InitialList(path);
        } catch (Exception e) {
        	Glu_ConstDefine.Log1.WriteLog("IFE_FrameDataAreaExplain() error:" + e.toString());
            System.out.println(e.toString());
        }
        try {
            jbInit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    public IFE_FrameDataAreaExplain(URI ur) {
        try {
            InitialList(ur);
        } catch (Exception e) {
        	Glu_ConstDefine.Log1.WriteLog("IFE_FrameDataAreaExplain() error:" + e.toString());
            System.out.println(e.toString());
        }
        try {
            jbInit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    public void InitialList(String path) {
        InputStream filecon = null;
       // String file_name = "C://hexing//CommService.config";
        Glu_ConstDefine.Trc1.TraceLog("path:" + path);
        if (!path.equals("") && path.substring(0,4).equals("smb:")){
        	SmbFile file = null;
    		try {
    			file = new SmbFile(path);
    		} catch (MalformedURLException e) {
    			Glu_ConstDefine.Log1.WriteLog("new SmbFile()MalformedURLException:"+e.getMessage());
    		}
    		try {
    			filecon = new SmbFileInputStream(file);
    		} catch (SmbException e) {
    			Glu_ConstDefine.Log1.WriteLog("new SmbFileInputStream() SmbException:"+e.getMessage());
    		} catch (MalformedURLException e) {
    			Glu_ConstDefine.Log1.WriteLog("new SmbFileInputStream() MalformedURLException:"+e.getMessage());
    		} catch (UnknownHostException e) {
    			Glu_ConstDefine.Log1.WriteLog("new SmbFileInputStream() UnknownHostException:"+e.getMessage());
    		} 
    		LoadFile(filecon,path,null);
        }else{
        	String file_name="";
            if (path.equals("")){
            	file_name = "./CommService.config";
            }else{
            	file_name = path;
            }
        //    Glu_ConstDefine.Trc1.TraceLog("IFE_FrameDataAreaExplain__InitialList->file_name:"+file_name);
            File file = new File(file_name);
            try {            
                if (!file.exists()) {
                    file_name = file.getAbsolutePath().replace('\\', '/');
                    file = new File(file_name);
                }
                if (!file.exists()) {
                    file_name = "C:/hexing/CommService.config";
                    file = new File(file_name);
                }
                if (!file.exists()) {
                    JOptionPane.showMessageDialog(null, "Error！Can't find the profile！",
                                                  "Collector config",
                                                  JOptionPane.ERROR_MESSAGE);
                    Glu_ConstDefine.Log1.WriteLog(
                            "Func:IFE_FrameDataAreaExplain__InitialList();Error:" +
                            "Can't find the profile!");
                    //System.exit(1);
                }
                try {
    				filecon = new FileInputStream(file);
    			} catch (FileNotFoundException e) {
    				// TODO Auto-generated catch block
    				Glu_ConstDefine.Log1.WriteLog("new FileInputStream() FileNotFoundException:"+e.getMessage());
    				e.printStackTrace();
    			} //读取配置文件中的内容
                LoadFile(filecon,path,null);
            } finally {            
            }
        }
        
    }
    public void InitialList(URI ur){
        File file = new File(ur);
        InputStream filecon = null;
        try {                    
            if (!file.exists()) {
                JOptionPane.showMessageDialog(null, "Error！Can't find the profile！",
                                              "Collector config",
                                              JOptionPane.ERROR_MESSAGE);
                Glu_ConstDefine.Log1.WriteLog(
                        "Func:IFE_FrameDataAreaExplain__InitialList();Error:" +
                        "Can't find the profile!");
                //System.exit(1);
            }
            try {
				filecon = new FileInputStream(file);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} //读取配置文件中的内容
            LoadFile(filecon,"",ur);

        }catch (Exception e) {
        	Glu_ConstDefine.Log1.WriteLog(
                    "Func:IFE_FrameDataAreaExplain__InitialList();Error:" +
                    e.toString());
        }    
    }
    
    public void LoadFile(InputStream filecon,String path,URI ur){
    //	FileInputStream filecon = null; 
    //	InputStream filecon = null; 
    	try {
    		try{
    			Properties prop = new Properties();
              //  filecon = new FileInputStream(file); //读取配置文件中的内容
                prop.load(filecon);
                if (filecon != null) {
                    filecon.close();
                }

                String sBoolean = (String) prop.get("FRAME_EXPLAIN_RWSJCDHFX");
                if (sBoolean != null) {
                    if (sBoolean.equals("true")) {
                    	Glu_ConstDefine.TaskLengthCheckSupport = true;
                    } else {
                    	Glu_ConstDefine.TaskLengthCheckSupport = false;
                    }
                } else {
                	Glu_ConstDefine.Log1.WriteLog(
                            "Func:IFE_FrameDataAreaExplain__InitialList:The profile is short of FRAME_EXPLAIN_RWSJCDHFX");
                }
                sBoolean = (String) prop.get("TERMINAL_TYPE_ZJ");
                if (sBoolean != null) {
                    if (sBoolean.equals("true")) {
                    	Glu_ConstDefine.TerminalZheJiangSupport = true;
                    //	Glu_ConstDefine.TerminalZheJiangBDZSupport = true;
                    } else {
                    	Glu_ConstDefine.TerminalZheJiangSupport = false;
                    //	Glu_ConstDefine.TerminalZheJiangBDZSupport = false;
                    }
                } else {
                	Glu_ConstDefine.Log1.WriteLog(
                            "Func:IFE_FrameDataAreaExplain__InitialList:The profile is short of TERMINAL_TYPE_ZJ");
                }
                sBoolean = (String) prop.get("TERMINAL_TYPE_DLMS");
                if (sBoolean != null) {
                    if (sBoolean.equals("true")) {
                    	Glu_ConstDefine.TerminalDLMSSupport = true;
                    //	Glu_ConstDefine.TerminalZheJiangBDZSupport = true;
                    } else {
                    	Glu_ConstDefine.TerminalDLMSSupport = false;
                    //	Glu_ConstDefine.TerminalZheJiangBDZSupport = false;
                    }
                } else {
                	Glu_ConstDefine.Log1.WriteLog(
                            "Func:IFE_FrameDataAreaExplain__InitialList:The profile is short of TERMINAL_TYPE_DLMS");
                }
                /*
                sBoolean = (String) prop.get("TERMINAL_TYPE_HLJC");
                if (sBoolean != null) {
                    if (sBoolean.equals("true")) {
                    	Glu_ConstDefine.TerminalHuaLongJCSupport = true;
                    } else {
                    	Glu_ConstDefine.TerminalHuaLongJCSupport = false;
                    }
                } else {
                    System.out.println(
                            "数据区解析InitialList提示:配置文件缺少TERMINAL_TYPE_HLJC");
                }*/
                sBoolean = (String) prop.get("TERMINAL_TYPE_GD");
                if (sBoolean != null) {
                    if (sBoolean.equals("true")) {
                    	Glu_ConstDefine.TerminalGuangDongSupport = true;
                    } else {
                    	Glu_ConstDefine.TerminalGuangDongSupport = false;
                    }
                } else {
                	Glu_ConstDefine.Log1.WriteLog(
                            "Func:IFE_FrameDataAreaExplain__InitialList:The profile is short of TERMINAL_TYPE_GD");
                }
                sBoolean = (String) prop.get("TERMINAL_TYPE_QG");
                if (sBoolean != null) {
                    if (sBoolean.equals("true")) {
                    	Glu_ConstDefine.TerminalQuanGuoSupport = true;
                    } else {
                    	Glu_ConstDefine.TerminalQuanGuoSupport = false;
                    }
                } else {
                	Glu_ConstDefine.Log1.WriteLog(
                            "Func:IFE_FrameDataAreaExplain__InitialList:The profile is short of TERMINAL_TYPE_QG");
                }
                sBoolean = (String) prop.get("TERMINAL_TYPE_IHD");
                if (sBoolean != null) {
                    if (sBoolean.equals("true")) {
                    	Glu_ConstDefine.TerminalIHDSupport = true;
                    } else {
                    	Glu_ConstDefine.TerminalIHDSupport = false;
                    }
                } else {
                	Glu_ConstDefine.Log1.WriteLog(
                            "Func:IFE_FrameDataAreaExplain__InitialList:The profile is short of TERMINAL_TYPE_IHD");
                }
                sBoolean = (String) prop.get("TERMINAL_TYPE_TJ");
                if (sBoolean != null) {
                    if (sBoolean.equals("true")) {
                    	Glu_ConstDefine.TerminalTianJinSupport = true;
                    } else {
                    	Glu_ConstDefine.TerminalTianJinSupport = false;
                    }
                } else {
                	Glu_ConstDefine.Log1.WriteLog(
                            "Func:IFE_FrameDataAreaExplain__InitialList:The profile is short of TERMINAL_TYPE_TJ");
                }
                sBoolean = (String) prop.get("TERMINAL_TYPE_698");
                if (sBoolean != null) {
                    if (sBoolean.equals("true")) {
                    	Glu_ConstDefine.Terminal698Support = true;
                    } else {
                    	Glu_ConstDefine.Terminal698Support = false;
                    }
                } else {
                	Glu_ConstDefine.Log1.WriteLog(
                            "Func:IFE_FrameDataAreaExplain__InitialList:The profile is short of TERMINAL_TYPE_698");
                }
                sBoolean = (String) prop.get("TERMINAL_TYPE_GY");
                if (sBoolean != null) {
                    if (sBoolean.equals("true")) {
                    	Glu_ConstDefine.TerminalGuYuanSupport = true;
                    } else {
                    	Glu_ConstDefine.TerminalGuYuanSupport = false;
                    }
                } else {
                	Glu_ConstDefine.Log1.WriteLog(
                            "Func:IFE_FrameDataAreaExplain__InitialList:The profile is short of TERMINAL_TYPE_GY");
                }
                sBoolean = (String) prop.get("TERMINAL_TYPE_HX");
                if (sBoolean != null) {
                    if (sBoolean.equals("true")) {
                    	Glu_ConstDefine.TerminalHeXingSupport = true;
                    } else {
                    	Glu_ConstDefine.TerminalHeXingSupport = false;
                    }
                } else {
                	Glu_ConstDefine.Log1.WriteLog(
                            "Func:IFE_FrameDataAreaExplain__InitialList:The profile is short of TERMINAL_TYPE_HX");
                }
                sBoolean = (String) prop.get("AMMETER_TYPE_QG");
                if (sBoolean != null) {
                    if (sBoolean.equals("true")) {
                    	Glu_ConstDefine.AmmeterQuanGuoSupport = true;
                    } else {
                    	Glu_ConstDefine.AmmeterQuanGuoSupport = false;
                    }
                } else {
                	Glu_ConstDefine.Log1.WriteLog(
                            "Func:IFE_FrameDataAreaExplain__InitialList:The profile is short of AMMETER_TYPE_QG");
                }
                sBoolean = (String) prop.get("AMMETER_TYPE_ZJ");
                if (sBoolean != null) {
                    if (sBoolean.equals("true")) {
                    	Glu_ConstDefine.AmmeterZheJiangSupport = true;
                    } else {
                    	Glu_ConstDefine.AmmeterZheJiangSupport = false;
                    }
                } else {
                	Glu_ConstDefine.Log1.WriteLog(
                            "Func:IFE_FrameDataAreaExplain__InitialList:The profile is short of AMMETER_TYPE_ZJ");
                }
                sBoolean = (String) prop.get("AMMETER_TYPE_TJ");
                if (sBoolean != null) {
                    if (sBoolean.equals("true")) {
                    	Glu_ConstDefine.AmmeterTianJinSupport = true;
                    } else {
                    	Glu_ConstDefine.AmmeterTianJinSupport = false;
                    }
                } else {
                	Glu_ConstDefine.Log1.WriteLog(
                            "Func:IFE_FrameDataAreaExplain__InitialList:The profile is short of AMMETER_TYPE_TJ");
                }
                sBoolean = (String) prop.get("AMMETER_TYPE_QG2007");
                if (sBoolean != null) {
                    if (sBoolean.equals("true")) {
                    	Glu_ConstDefine.AmmeterQuanGuo2007Support = true;
                    } else {
                    	Glu_ConstDefine.AmmeterQuanGuo2007Support = false;
                    }
                } else {
                	Glu_ConstDefine.Log1.WriteLog(
                            "Func:IFE_FrameDataAreaExplain__InitialList:The profile is short of AMMETER_TYPE_QG2007");
                }
                sBoolean = (String) prop.get("AMMETER_TYPE_WATER");
                if (sBoolean != null) {
                    if (sBoolean.equals("true")) {
                    	Glu_ConstDefine.WaterAmmeterSupport = true;
                    } else {
                    	Glu_ConstDefine.WaterAmmeterSupport = false;
                    }
                } else {
                	Glu_ConstDefine.Log1.WriteLog(
                            "Func:IFE_FrameDataAreaExplain__InitialList:The profile is short of AMMETER_TYPE_WATER");
                }
                sBoolean = (String) prop.get("AMMETER_TYPE_DLMS");
                if (sBoolean != null) {
                    if (sBoolean.equals("true")) {
                    	Glu_ConstDefine.DLMSAmmeterSupport = true;
                    } else {
                    	Glu_ConstDefine.DLMSAmmeterSupport = false;
                    }
                } else {
                	Glu_ConstDefine.Log1.WriteLog(
                            "Func:IFE_FrameDataAreaExplain__InitialList:The profile is short of AMMETER_TYPE_DLMS");
                }
                sBoolean = (String) prop.get("TERMINAL_AUTOTASK_ADJUST");
                if (sBoolean != null) {
                    if (sBoolean.equals("true")) {
                    	Glu_ConstDefine.TerminalAutoTaskAdjust = true;
                    } else {
                    	Glu_ConstDefine.TerminalAutoTaskAdjust = false;
                    }
                } else {
                	Glu_ConstDefine.Log1.WriteLog(
                            "Func:IFE_FrameDataAreaExplain__InitialList:The profile is short of TERMINAL_AUTOTASK_ADJUST");
                }
                sBoolean = ((String) prop.getProperty(
                        "IRANToGregorian", "false")).trim();
                if (sBoolean.equals("true")) {
                	Glu_ConstDefine.IRANToGregorian = true;
                }
                else {
                	Glu_ConstDefine.IRANToGregorian = false;
                }
                sBoolean = ((String) prop.getProperty(
                        "BillingDateAddOneDay", "false")).trim();
                if (sBoolean.equals("true")) {
                	Glu_ConstDefine.BillingDateAddOneDay = true;
                }
                else {
                	Glu_ConstDefine.BillingDateAddOneDay = false;
                }
                if (ur == null){
                	FrameDataExplainZheJiang = new FrameDataAreaExplainZheJiang(path);
                    FrameDataExplainQuanGuo = new FrameDataAreaExplainQuanGuo(path);
                    FrameDataExplainGuYuan = new FrameDataAreaExplainGuYuan(path);
                    FrameDataExplainHeXing = new FrameDataAreaExplainHeXing(path);
                    FrameDataExplainDLMS = new FrameDataAreaExplainDLMS(path);
                    FrameDataExplainIHD = new FrameDataAreaExplainIHD(path);
                    AmmeterDataFrameExplain = new AmmeterFrameExplain(path);
                    GluMethod = new FrameDataAreaExplainGluMethod();
                }else{
                	FrameDataExplainZheJiang = new FrameDataAreaExplainZheJiang(ur);
                    FrameDataExplainQuanGuo = new FrameDataAreaExplainQuanGuo(ur);
                    FrameDataExplainGuYuan = new FrameDataAreaExplainGuYuan(ur);
                    FrameDataExplainHeXing = new FrameDataAreaExplainHeXing(ur);
                    FrameDataExplainDLMS = new FrameDataAreaExplainDLMS(ur);
                    FrameDataExplainIHD = new FrameDataAreaExplainIHD(ur);
                    AmmeterDataFrameExplain = new AmmeterFrameExplain(ur);
                    GluMethod = new FrameDataAreaExplainGluMethod();
                }
                
    		}
    		catch (Exception e) {
            	Glu_ConstDefine.Log1.WriteLog(
                        "Func:IFE_FrameDataAreaExplain__LoadFile();Error:" +
                        e.toString());
            }
    		
    	} 
    	finally {
            try {
                filecon.close();
            } catch (Exception e) {
            	Glu_ConstDefine.Log1.WriteLog(
                        "Func:IFE_FrameDataAreaExplain__InitialList();Error:" +
                        "Close the profile error!");
            }
        }   	
    }
    
    //---------------------------组数据区接口--------------------------------------
    //------------------通用接口-----------------------
    public char[] IFE_ReadData( //读取测量点参数、终端参数
            int ProtocolType, SFE_MeasuredPointInfor MeasuredPointInfo, //规约号,测量点信息
            int CommandCount, SFE_ParamItem[] ParamList, int BuildSign) { //命令数目,命令列表,组合标志(10交叉组合；20点对点组合)
        String sDataArea = "";
        try {
            try {
                String[] sCommandList = new String[CommandCount];
                for (int row = 0; row < CommandCount; row++) {
                    sCommandList[row] = new String(ParamList[row].GetParamCaption()).trim();
                }
                if ((ProtocolType == Glu_ConstDefine.GY_ZD_ZHEJIANG) || 
                	  (ProtocolType == Glu_ConstDefine.GY_ZD_ZJZB0404) 
                /*    (ProtocolType == Glu_ConstDefine.GY_ZD_ZJBDZ) || 
                    (ProtocolType == Glu_ConstDefine.GY_ZD_GUANGDONG) ||
                    (ProtocolType == Glu_ConstDefine.GY_ZD_GUANGDONG_SC) || 
                    (ProtocolType == Glu_ConstDefine.GY_ZD_ZJMKB) ||
                    (ProtocolType == 88) || (ProtocolType == 110) ||
                    (ProtocolType == 111)*/) { //终端浙江系列
                    sDataArea = GetMeasuredPointSign(MeasuredPointInfo,
                            ProtocolType); //得到测量点标志
                    for (int i = 0; i < sCommandList.length; i++) {
                        sDataArea = sDataArea +
                                    DataSwitch.ReverseStringByByte(sCommandList[
                                i]);
                    }
                } else if ((ProtocolType == Glu_ConstDefine.GY_ZD_QUANGUO) || 
                		 //  (ProtocolType == Glu_ConstDefine.GY_ZD_TIANJIN) ||
                         //  (ProtocolType == Glu_ConstDefine.GY_ZD_GUYUAN) || 
                           (ProtocolType == Glu_ConstDefine.GY_ZD_698)) { //国网,天津模块表(这个接口只支持国网的终端参数和一类数据的终端状态数据,且这两类数据要分开组)
                    String sSEQ = "60"; //帧序列域SEQ:时间标签有效标志为0,首帧标志FIR、末帧标志FIN置1,请求确认标志位CON为0,帧序号填0(组帧时重组)
                    String sParamContent = "";
                    String[] sParamItemList = new String[CommandCount];                    
                    String sTemp = "";
                    if (BuildSign == 20 &&
                        MeasuredPointInfo.MeasuredPointCount == CommandCount) { //20测量点列表和命令列表点对点组合
                        for (int i = 0; i < CommandCount; i++) {
                        	sTemp = GetDataDellID(ProtocolType,
                                    MeasuredPointInfo.
                                    MeasuredPointList[i], sCommandList[i]);
                        	if (MeasuredPointInfo.MeasuredPointListType == 30){
                        		sTemp = "FFFF" + sTemp.substring(4,8);
                        	}
                            sDataArea = sDataArea + sTemp;
                        	sTemp = sCommandList[i].substring(0, 1);
                            if ((sTemp.equals("4") || sTemp.equals("B") || sTemp.equals("H") ) && (ProtocolType == Glu_ConstDefine.GY_ZD_698)){//698规约0A,C2命令下部分项有数据单元
                            	sParamContent = new String(ParamList[i].GetParamContent());
                                sDataArea = sDataArea + sParamContent;
                            }
                        }
                    } else { //测量点列表和命令列表交叉组合
                    	sTemp = sCommandList[0].substring(0, 1);
                        if ((sTemp.equals("4") || sTemp.equals("B") || sTemp.equals("H")) && (ProtocolType == Glu_ConstDefine.GY_ZD_698)){//698规约0A命令下部分项有数据单元
                        	for (int i = 0; i < CommandCount; i++) {
                        		for (int j = 0; j < MeasuredPointInfo.
                        				MeasuredPointCount; j++) {
                        			sTemp = GetDataDellID(ProtocolType,
    		                                MeasuredPointInfo.MeasuredPointList[j], sCommandList[i]);
                        			if (MeasuredPointInfo.MeasuredPointListType == 30){
                                		sTemp = "FFFF" + sTemp.substring(4,8);
                                	}
                        			sParamItemList[i] = sDataArea + sTemp;
                        		}                        		
                                sParamContent = new String(ParamList[i].GetParamContent());
                                sDataArea = sDataArea + sParamItemList[i] + sParamContent;                               
                            }
                        }
                        else {
	                        ArrayList sDADTList = new ArrayList();
	                        sDADTList = GetIncorporateDataDellIDList(ProtocolType,
	                                MeasuredPointInfo.MeasuredPointList,
	                                MeasuredPointInfo.MeasuredPointCount,
	                                sCommandList);
	                        for (int i = 0; i < sDADTList.size(); i++) {
	                        	sTemp = sDADTList.get(i).toString();
	                        	if (MeasuredPointInfo.MeasuredPointListType == 30){
                            		sTemp = "FFFF" + sTemp.substring(4,8);
                            	}
	                            sDataArea = sDataArea + sTemp;
	                        }
                        }
                    }
                    sDataArea = sSEQ + sDataArea;
                } else if (ProtocolType == Glu_ConstDefine.GY_ZD_HEXING)  { //终端海兴集抄规约
                	String sParamItem = new String(ParamList[0].
	                            GetParamCaption()).trim(); //命令标识
                	sParamItem = sParamItem.substring(2, 6);
                	String sParamContent = new String(ParamList[0].GetParamContent());
	                sDataArea =  sParamItem + sParamContent;                         
                }else if (ProtocolType == Glu_ConstDefine.GY_ZD_DLMS)  { //终端DLMS规约
                	String sParamItem = new String(ParamList[0].
                            GetParamCaption()).trim(); //命令标识           
	                if (sParamItem.length() == 18){
	                	sParamItem = sParamItem + "00";
	                }
	            	String sParamContent = new String(ParamList[0].GetParamContent());
	                sDataArea =  sParamItem + sParamContent;                         
	            } else if (ProtocolType == Glu_ConstDefine.GY_ZD_IHD) { //IHD(这个接口只支持国网的终端参数和一类数据的终端状态数据,且这两类数据要分开组)
                   String sSEQ = "60"; //帧序列域SEQ:时间标签有效标志为0,首帧标志FIR、末帧标志FIN置1,请求确认标志位CON为0,帧序号填0(组帧时重组)
                   if (BuildSign == 20 &&
                       MeasuredPointInfo.MeasuredPointCount == CommandCount) { //20测量点列表和命令列表点对点组合
                       for (int i = 0; i < CommandCount; i++) {
                           sDataArea = sDataArea +
                                       GetDataDellID(ProtocolType,
                                   MeasuredPointInfo.
                                   MeasuredPointList[i], sCommandList[i]);                  
                       }
                   } else { //测量点列表和命令列表交叉组合                	    
                        ArrayList sDADTList = new ArrayList();
                        sDADTList = GetIncorporateDataDellIDList(ProtocolType,
                                MeasuredPointInfo.MeasuredPointList,
                                MeasuredPointInfo.MeasuredPointCount,
                                sCommandList);
                        for (int i = 0; i < sDADTList.size(); i++) {
                            sDataArea = sDataArea + sDADTList.get(i).toString();
                        }
                  
                   }

                   sDataArea = sSEQ + sDataArea;
               }
            } catch (Exception e) {
            	Glu_ConstDefine.Log1.WriteLog(
                        "Func:IFE_FrameDataAreaExplain__IFE_ReadData();Error:" +
                        e.toString());
            }
        } finally {
        }
        char[] DataArea = sDataArea.toCharArray();
        return DataArea;
    }

    public char[] IFE_SetData( //设置参数
            int ProtocolType, SFE_MeasuredPointInfor MeasuredPointInfo, //规约号,测量点信息
            int ParamCount, SFE_ParamItem[] ParamList, char[] PasswordLevel, //参数数目,参数列表,密码权限
            char[] Password, char[] ExecuteTime, int EffectTime, int BuildSign) { //密码值,有效时间(分),组合标志(10交叉组合；20点对点组合)
        String sPasswordLevel = new String(PasswordLevel).trim();
        String sPassword = new String(Password).trim();
        String sExecuteTime = new String(ExecuteTime).trim();
        String sDataArea = "";
        try {
            try {
                String[] sParamItemList = new String[ParamCount];
                String sParamContent = "";
                String sMeasuredPoint = "";
                sPassword = DataSwitch.ReverseStringByByte(sPassword);
                if ((ProtocolType == Glu_ConstDefine.GY_ZD_ZHEJIANG) || 
                	(ProtocolType == Glu_ConstDefine.GY_ZD_ZJZB0404)/* ||
                    (ProtocolType == Glu_ConstDefine.GY_ZD_ZJBDZ) || 
                    (ProtocolType == Glu_ConstDefine.GY_ZD_GUANGDONG) ||
                    (ProtocolType == Glu_ConstDefine.GY_ZD_GUANGDONG_SC) || 
                    (ProtocolType == Glu_ConstDefine.GY_ZD_ZJMKB) ||
                    (ProtocolType == 88) || (ProtocolType == 110) ||
                    (ProtocolType == 111)*/) { //终端浙江系列
                	sPasswordLevel = sPasswordLevel.substring(0,2);
                    if (ProtocolType == 110) { //华隆集抄是对象标志
                        sMeasuredPoint = GetMeasuredPointSign(MeasuredPointInfo,
                                ProtocolType); //得到对象标志
                    } else {
                        sMeasuredPoint = DataSwitch.IntToHex(Integer.toString(
                                MeasuredPointInfo.MeasuredPointList[0]), "00");
                    }
                    for (int i = 0; i < ParamCount; i++) {
                        sParamItemList[i] = new String(ParamList[i].
                                GetParamCaption()).trim(); //命令标识
                        sParamItemList[i] = DataSwitch.ReverseStringByByte(
                                sParamItemList[i]); //命令标识按字节倒置
                        sParamContent = new String(ParamList[i].GetParamContent());
                        sParamContent = DataSwitch.ReverseStringByByte(
                                sParamContent); //命令内容按字节倒置(为了统一处理(配置任务时也要倒置)
                        sDataArea = sDataArea + sParamItemList[i] +
                                    sParamContent;
                        sParamContent = "";
                    }
                    //String sKZM="";
                    if (EffectTime > 0) { //实时写对象参数
                        sExecuteTime = DataSwitch.DateTimeToBCD(sExecuteTime,
                                "yymmddhhnn");
                        String sEffectTime = ("" + EffectTime).substring(0, 2);
                        if (sEffectTime.length() < 2) {
                            sEffectTime = "0" + sEffectTime;
                        }
                        sDataArea = sMeasuredPoint + sPasswordLevel + sPassword +
                                    sExecuteTime + sEffectTime + sDataArea;
                        //sKZM="07";
                    } else {
                        sDataArea = sMeasuredPoint + sPasswordLevel + sPassword +
                                    sDataArea;
                        //sKZM="08";
                    }
                } else if ((ProtocolType == Glu_ConstDefine.GY_ZD_QUANGUO) || 
                		 //  (ProtocolType == Glu_ConstDefine.GY_ZD_TIANJIN) ||
                         //  (ProtocolType == Glu_ConstDefine.GY_ZD_GUYUAN)  || 
                           (ProtocolType == Glu_ConstDefine.GY_ZD_698)) { //国网,天津模块表,698
                	sPasswordLevel = sPasswordLevel.substring(0,2);
                    String sSEQ = "70"; //帧序列域SEQ:时间标签有效标志TpV=0,FIR=1,FIN=1,请求确认标志位CON=1,帧序号填0(组帧时重组)
                    for (int i = 0; i < ParamCount; i++) {
                        sParamItemList[i] = new String(ParamList[i].
                                GetParamCaption()).trim(); //命令标识
                      /*  if (ProtocolType == Glu_ConstDefine.GY_ZD_GUYUAN) {
                            sParamItemList[i] = GetDataCellID(
                                    ProtocolType,
                                    MeasuredPointInfo.MeasuredPointList,
                                    MeasuredPointInfo.MeasuredPointCount,
                                    sParamItemList[i]);
                        } else */{
                            if (BuildSign == 20 &&
                                MeasuredPointInfo.MeasuredPointCount ==
                                ParamCount) { //20测量点列表和命令列表点对点组合
                                sParamItemList[i] = GetDataDellID(ProtocolType,
                                        MeasuredPointInfo.
                                        MeasuredPointList[i],
                                        sParamItemList[i]);
                            } else { //测量点列表和命令列表交叉组合
                                for (int j = 0;
                                             j < MeasuredPointInfo.
                                             MeasuredPointCount;
                                             j++) {
                                    sParamItemList[i] = GetDataDellID(
                                            ProtocolType,
                                            MeasuredPointInfo.
                                            MeasuredPointList[j],
                                            sParamItemList[i]);
                                }
                            }
                            if (MeasuredPointInfo.MeasuredPointListType == 30){
                            	sParamItemList[i] = "FFFF" + sParamItemList[i].substring(4,8);
                            }
                        }
                        sParamContent = new String(ParamList[i].GetParamContent());
                        sDataArea = sDataArea + sParamItemList[i] +
                                    sParamContent;
                        sParamContent = "";
                    }
                    sDataArea = sDataArea + sPassword;
                    if (EffectTime > 0) { //带时间标签Tp
                        sSEQ = "F0"; //帧序列域SEQ:时间标签有效标志TpV=1,FIR=1,FIN=1,请求确认标志位CON=1,帧序号填0(组帧时重组)
                        sExecuteTime = DataSwitch.DateTimeToBCD(sExecuteTime,
                                "ddhhnnss");
                        String sEffectTime = DataSwitch.IntToHex(("" +
                                EffectTime), "00");
                        sDataArea = sDataArea + "00" + sExecuteTime +
                                    sEffectTime; //命令序号暂时填0,在组帧时再修改
                    }
                    sDataArea = sSEQ + sDataArea;
                } else if (ProtocolType == Glu_ConstDefine.GY_ZD_HEXING) { //终端海兴集抄规约                        
                        sParamItemList[0] = new String(ParamList[0].
                                    GetParamCaption()).trim(); //命令标识
                        sParamItemList[0] = sParamItemList[0].substring(2, 6);
                        sParamContent = new String(ParamList[0].GetParamContent());
                        sDataArea =  sParamItemList[0] + sPassword + sParamContent;      
                } else if (ProtocolType == Glu_ConstDefine.GY_ZD_DLMS) { //终端DLMS规约                        
                    sParamItemList[0] = new String(ParamList[0].
                            GetParamCaption()).trim(); //命令标识
                    if (sParamItemList[0].length() == 18){
                    	sParamItemList[0] = sParamItemList[0] + "00";
	                }
	                sParamContent = new String(ParamList[0].GetParamContent());
	                sDataArea =  sParamItemList[0] + sParamContent;      
                } else if (ProtocolType == Glu_ConstDefine.GY_ZD_IHD) { //IHD
                	sPasswordLevel = sPasswordLevel.substring(0,2);
                	String sSEQ = "70"; //帧序列域SEQ:时间标签有效标志TpV=0,FIR=1,FIN=1,请求确认标志位CON=1,帧序号填0(组帧时重组)
                	for (int i = 0; i < ParamCount; i++) {
                		sParamItemList[i] = new String(ParamList[i].
                				GetParamCaption()).trim(); //命令标识
                		if (BuildSign == 20 &&
                				MeasuredPointInfo.MeasuredPointCount ==
                					ParamCount) { //20测量点列表和命令列表点对点组合
                			sParamItemList[i] = GetDataDellID(ProtocolType,
                					MeasuredPointInfo.
                					MeasuredPointList[i],
                					sParamItemList[i]);
                		} else { //测量点列表和命令列表交叉组合
                			for (int j = 0; j < MeasuredPointInfo.MeasuredPointCount; j++) {
                				sParamItemList[i] = GetDataDellID(
                						ProtocolType,
                						MeasuredPointInfo.
                						MeasuredPointList[j],
                						sParamItemList[i]);
                			}
                		}
                		sParamContent = new String(ParamList[i].GetParamContent());
                		sDataArea = sDataArea + sParamItemList[i] +
                                   sParamContent;
                		sParamContent = "";
                	}
                	sDataArea = sDataArea + sPassword;
                	if (EffectTime > 0) { //带时间标签Tp
                        sSEQ = "F0"; //帧序列域SEQ:时间标签有效标志TpV=1,FIR=1,FIN=1,请求确认标志位CON=1,帧序号填0(组帧时重组)
                        sExecuteTime = DataSwitch.DateTimeToBCD(sExecuteTime,
                                "ddhhnnss");
                        String sEffectTime = DataSwitch.IntToHex(("" +
                                EffectTime), "00");
                        sDataArea = sDataArea + "00" + sExecuteTime +
                                    sEffectTime; //命令序号暂时填0,在组帧时再修改
                    }
                    sDataArea = sSEQ + sDataArea;
                } 
            } catch (Exception e) {
            	Glu_ConstDefine.Log1.WriteLog(
                        "Func:IFE_FrameDataAreaExplain__IFE_SetData();Error:" +
                        e.toString());
            }
        } finally {
        }
        char[] DataArea = sDataArea.toCharArray();
        return DataArea;
    }

    public char[] IFE_ReadMeasuredData( //读取电表数据—当前
            int ProtocolType, SFE_MeasuredPointInfor MeasuredPointInfo, //规约号,测量点信息
            int CommandSign, int DataCaptionCount, SFE_ParamItem[] DataCaptionList,
            int BuildSign) { //组大小项命令标志(10大项；20小项),数据项数目,数据项列表,组合标志(10交叉组合；20点对点组合)
        String sDataArea = "";
        try {
            try {
                SPE_CommandInfoList TerminalCommandList = new
                        SPE_CommandInfoList();
                String[] sDataItemList = new String[DataCaptionCount];
                String[] sContentList = new String[DataCaptionCount];
                for (int row = 0; row < DataCaptionCount; row++) {
                    sDataItemList[row] = new String(DataCaptionList[row].GetParamCaption()).trim();
                    sContentList[row] = new String(DataCaptionList[row].GetParamContent()).trim();
                }
                ArrayList CommandList = new ArrayList();
                if ((ProtocolType == Glu_ConstDefine.GY_ZD_ZHEJIANG) || 
                	(ProtocolType == Glu_ConstDefine.GY_ZD_ZJZB0404)/* ||
                    (ProtocolType == Glu_ConstDefine.GY_ZD_ZJBDZ) || 
                    (ProtocolType == Glu_ConstDefine.GY_ZD_GUANGDONG) ||
                    (ProtocolType == Glu_ConstDefine.GY_ZD_GUANGDONG_SC) || 
                    (ProtocolType == Glu_ConstDefine.GY_ZD_ZJMKB) ||
                    (ProtocolType == 88) || (ProtocolType == 110) ||
                    (ProtocolType == 111)*/) { //终端浙江系列
                    sDataArea = GetMeasuredPointSign(MeasuredPointInfo,
                            ProtocolType); //得到测量点标志
                    if (ProtocolType == 110) { //华隆集抄命令集和浙规系列是分开的
                        TerminalCommandList = FrameDataExplainZheJiang.
                                              TermialHuaLongJCCommandInfoList;
                        CommandList = SearchCommandList(ProtocolType,
                                sDataItemList, TerminalCommandList, CommandSign); //通过数据项列表查找队列中对应的命令标识列表
                    } else { //浙规系列
                        CommandList = SearchZheJiangCommandList(ProtocolType,
                                sDataItemList, CommandSign); //通过数据项列表查找队列中对应的命令标识列表
                    }
                    for (int i = 0; i < CommandList.size(); i++) {
                        sDataArea = sDataArea +
                                    DataSwitch.ReverseStringByByte(CommandList.
                                get(i).toString());
                    }
                } else if (ProtocolType == Glu_ConstDefine.GY_ZD_QUANGUO || 
                		//   ProtocolType == Glu_ConstDefine.GY_ZD_GUYUAN || 
                		   ProtocolType == Glu_ConstDefine.GY_ZD_698) { //国网(这个接口只支持一类数据中的当前电表数据)
                    String sSEQ = "60"; //帧序列域SEQ:时间标签有效标志为0,首帧标志FIR、末帧标志FIN置1,请求确认标志位CON为0,帧序号填0(组帧时重组)
                  /*  if (ProtocolType == Glu_ConstDefine.GY_ZD_GUYUAN) {
                        TerminalCommandList = FrameDataExplainGuYuan.
                                              TermialGuYuanCommandInfoList;
                    } else */if (ProtocolType == Glu_ConstDefine.GY_ZD_698) {
                        TerminalCommandList = FrameDataExplainQuanGuo.
                                              Termial698CommandInfoList;
                    } else {
                        TerminalCommandList = FrameDataExplainQuanGuo.
                        TermialQuanGuoCommandInfoList;
                    }
                    CommandList = SearchCommandList(ProtocolType, sDataItemList,
                            TerminalCommandList, CommandSign); //通过数据项列表查找队列中对应的命令标识列表
                    String[] sCommandList = new String[CommandList.size()];
                    for (int i = 0; i < sCommandList.length; i++) {
                        sCommandList[i] = CommandList.get(i).toString();
                    }
                    String sTemp = "";
                    if (BuildSign == 20 &&
                        MeasuredPointInfo.MeasuredPointCount ==
                        sCommandList.length) { //20测量点列表和命令列表点对点组合
                        for (int i = 0; i < sCommandList.length; i++) {
                        	sTemp = GetDataDellID(ProtocolType,
                                        MeasuredPointInfo.
                                        MeasuredPointList[i], sCommandList[i]);
                        	if (MeasuredPointInfo.MeasuredPointListType == 30){
                        		sTemp = "FFFF" + sTemp.substring(4,8);
                        	}                        	
                            sDataArea = sDataArea + sTemp;
                        }
                    } else { //测量点列表和命令列表交叉组合
                        ArrayList sDADTList = new ArrayList();
                        sDADTList = GetIncorporateDataDellIDList(ProtocolType,
                                MeasuredPointInfo.MeasuredPointList,
                                MeasuredPointInfo.MeasuredPointCount,
                                sCommandList);
                        for (int i = 0; i < sDADTList.size(); i++) {
                        	sTemp = sDADTList.get(i).toString();
                        	if (MeasuredPointInfo.MeasuredPointListType == 30){
                        		sTemp = "FFFF" + sTemp.substring(4,8);
                        	}
                            sDataArea = sDataArea + sTemp;
                        }
                    }
                    sDataArea = sSEQ + sDataArea;
                } else if (ProtocolType == Glu_ConstDefine.GY_ZD_HEXING)  { //终端海兴集抄
                	TerminalCommandList = FrameDataExplainHeXing.
                    			TermialHeXingCommandInfoList;                
                	CommandList = SearchCommandList(ProtocolType, sDataItemList,
                            TerminalCommandList, CommandSign); //通过数据项列表查找队列中对应的命令标识列表
                	sDataArea =  sDataItemList[0].substring(2, 6) + sContentList[0];                 	
                        
                } else if (ProtocolType == Glu_ConstDefine.GY_ZD_IHD) { //IHD(这个接口只支持一类数据中的当前电表数据)
                	String sSEQ = "60"; //帧序列域SEQ:时间标签有效标志为0,首帧标志FIR、末帧标志FIN置1,请求确认标志位CON为0,帧序号填0(组帧时重组)
                	TerminalCommandList = FrameDataExplainIHD.
                     			TermialIHDCommandInfoList;                 
                	CommandList = SearchCommandList(ProtocolType, sDataItemList,
                			TerminalCommandList, CommandSign); //通过数据项列表查找队列中对应的命令标识列表
                	String[] sCommandList = new String[CommandList.size()];
                	for (int i = 0; i < sCommandList.length; i++) {
                		sCommandList[i] = CommandList.get(i).toString();
                	}
                	if (BuildSign == 20 &&
                			MeasuredPointInfo.MeasuredPointCount ==
                				sCommandList.length) { //20测量点列表和命令列表点对点组合
                		for (int i = 0; i < sCommandList.length; i++) {
                			sDataArea = sDataArea +
                                     GetDataDellID(ProtocolType,
                                    		 MeasuredPointInfo.
                                    		 MeasuredPointList[i], sCommandList[i]);
                		}
                	} else { //测量点列表和命令列表交叉组合
                		ArrayList sDADTList = new ArrayList();
                		sDADTList = GetIncorporateDataDellIDList(ProtocolType,
                				MeasuredPointInfo.MeasuredPointList,
                				MeasuredPointInfo.MeasuredPointCount,
                				sCommandList);
                		for (int i = 0; i < sDADTList.size(); i++) {
                			sDataArea = sDataArea + sDADTList.get(i).toString();
                		}
                	}
                	sDataArea = sSEQ + sDataArea;
                }  else if (ProtocolType == Glu_ConstDefine.GY_ZD_DLMS) { //终端DLMS规约  
                	TerminalCommandList = FrameDataExplainDLMS.
			        			TermialDLMSCommandInfoList;                
			    	CommandList = SearchCommandList(ProtocolType, sDataItemList,
			                TerminalCommandList, CommandSign); //通过数据项列表查找队列中对应的命令标识列表
			    	
			    	sDataArea =  sDataItemList[0];
			    	if (sDataArea.length() == 18){
			    		sDataArea = sDataArea + "00";
	                }
			    	sDataArea = sDataArea + sContentList[0];  			    	   
                } 
            } catch (Exception e) {
            	Glu_ConstDefine.Log1.WriteLog(
                        "Func:IFE_FrameDataAreaExplain__IFE_ReadMeasuredData();Error:" +
                        e.toString());
            }
        } finally {
        }
        char[] DataArea = sDataArea.toCharArray();
        return DataArea;
    }

    public char[] IFE_ReadAmmeterData( //读取电表数据——中继
            int ProtocolType, int PortNO, int Overtime, int GetFrom, //规约号,端口号(浙规)/转发通信端口号(国网)/集抄码(华隆集抄),超时时间(秒) (浙规),截取开始(浙规)
            int GetLength, char[] CharacterChar, int WaitByteTime, //截取长度(浙规),特征字节/转发通信控制字(浙规/国网),转发接收等待字节超时时间(10ms)(国网)
            int WaitFrameTime, char[] PassWord, int RelayFrameLen, //转发接收等待报文超时时间(10ms) (国网),密码(国网),转发内容长度n
            char[] AmmeterFrame) { //转发内容（中继命令帧）
        String sDataArea = "";
        try {
            try {
                String sPortNO = DataSwitch.IntToHex(("" + PortNO), "00"); //端口号(浙规/国网)
                String sCharacterChar = new String(CharacterChar).substring(0,
                        2); //特征字节(浙规/国网)
                String sAmmeterFrame = new String(AmmeterFrame);
                if ((ProtocolType == Glu_ConstDefine.GY_ZD_ZHEJIANG) || 
                	(ProtocolType == Glu_ConstDefine.GY_ZD_ZJZB0404) /*||
                    (ProtocolType == Glu_ConstDefine.GY_ZD_ZJBDZ) || 
                    (ProtocolType == Glu_ConstDefine.GY_ZD_GUANGDONG) ||
                    (ProtocolType == Glu_ConstDefine.GY_ZD_GUANGDONG_SC) || 
                    (ProtocolType == Glu_ConstDefine.GY_ZD_ZJMKB) ||
                    (ProtocolType == 88) || 
                    (ProtocolType == 110) ||
                    (ProtocolType == 111)*/) { //终端浙江系列
                    String sOvertime = DataSwitch.IntToHex(("" + Overtime),
                            "00"); //超时时间(秒)
                    String sGetFrom = DataSwitch.IntToHex(("" + GetFrom),
                            "0000"); //截取开始
                    sGetFrom = DataSwitch.ReverseStringByByte(sGetFrom);
                    String sGetLength = DataSwitch.IntToHex(("" + GetLength),
                            "0000"); //截取长度(浙规/国网)
                    sGetLength = DataSwitch.ReverseStringByByte(sGetLength);
                    sDataArea = sPortNO + sOvertime + sCharacterChar + sGetFrom +
                                sGetLength + sAmmeterFrame;
                } else if ((ProtocolType == Glu_ConstDefine.GY_ZD_QUANGUO) /*|| 
                		   (ProtocolType == Glu_ConstDefine.GY_ZD_TIANJIN) ||
                           (ProtocolType == Glu_ConstDefine.GY_ZD_GUYUAN) */) { //国网,天津模块表
                    String sSEQ = "60"; //帧序列域SEQ:时间标签有效标志为0,首帧标志FIR、末帧标志FIN置1,请求确认标志位CON为0,帧序号填0(组帧时重组)
                    String sWaitByteTime = DataSwitch.IntToHex(("" +
                            WaitByteTime), "00"); //转发接收等待字节超时时间(10ms)
                    String sWaitFrameTime = DataSwitch.IntToHex(("" +
                            WaitFrameTime), "00"); //转发接收等待报文超时时间(10ms)
                    String sRelayFrameLen = DataSwitch.IntToHex(("" +
                            RelayFrameLen), "00"); //转发内容长度n
                    String sPassWord = new String(PassWord);
                    sPassWord = DataSwitch.ReverseStringByByte(sPassWord); //密码
                    sDataArea = sSEQ + "00000100" + sPortNO + sCharacterChar +
                                sWaitByteTime + sWaitFrameTime + sRelayFrameLen +
                                sAmmeterFrame + sPassWord;
                } else if (ProtocolType == 110) { //华隆集抄
                    SFE_MeasuredPointInfor MeasuredPointInfo = new
                            SFE_MeasuredPointInfor();
                    MeasuredPointInfo.MeasuredPointCount = 1;
                    MeasuredPointInfo.MeasuredPointListType = 80; //台区表
                    MeasuredPointInfo.MeasuredPointList[0] = PortNO;
                    sDataArea = GetMeasuredPointSign(MeasuredPointInfo,
                            ProtocolType); //得到测量点标志
                    sDataArea = sDataArea + "2140FEFEFE" + sAmmeterFrame; //台区表中继抄表数据项4021
                }else if (ProtocolType == Glu_ConstDefine.GY_ZD_698)  { //国网698
                    String sSEQ = "60"; //帧序列域SEQ:时间标签有效标志为0,首帧标志FIR、末帧标志FIN置1,请求确认标志位CON为0,帧序号填0(组帧时重组)
                    String sWaitByteTime = DataSwitch.IntToHex(("" +
                            WaitByteTime), "00"); //转发接收等待字节超时时间(10ms)
                    String sWaitFrameTime = DataSwitch.IntToHex(("" +
                            WaitFrameTime), "00"); //转发接收等待报文超时时间(10ms)
                    String sRelayFrameLen = DataSwitch.ReverseStringByByte(DataSwitch.IntToHex(("" +
                            RelayFrameLen), "0000")); //转发内容长度n
                    String sPassWord = new String(PassWord);
                    sPassWord = DataSwitch.ReverseStringByByte(sPassWord); //密码
                    sDataArea = sSEQ + "00000100" + sPortNO + sCharacterChar +
                                sWaitByteTime + sWaitFrameTime + sRelayFrameLen +
                                sAmmeterFrame + sPassWord;
                }
            } catch (Exception e) {
            	Glu_ConstDefine.Log1.WriteLog(
                        "Func:IFE_FrameDataAreaExplain__IFE_ReadAmmeterData();Error:" +
                        e.toString());
            }
        } finally {
        }
        char[] DataArea = sDataArea.toCharArray();
        return DataArea;
    }

    //固原集抄中点抄电表组数据区
    public ArrayList IFE_GYAmmeterSer_PointReadAmmeterData(int PN,
            char[] AmmeterAdd, //地址域(A)
            int DataItemCount, char[][] DataItemList, int RelayCount, //中继级数  m级中继表地址
            char[][] RelayAddrList, int RelayFlag, int RelaySource, //中继使能标志  中继参数源
            int ControlCode) { //功能码
        ArrayList<String> FrameList = new ArrayList<String>();
        String sFrame = "";
        String sAmmeterAdd = new String(AmmeterAdd).trim();
        sAmmeterAdd = DataSwitch.StrStuff("A", 12, sAmmeterAdd, 10); //12位的电表地址左补足A
        sAmmeterAdd = DataSwitch.ReverseStringByByte(sAmmeterAdd); //按字节倒置
        String sControlCode = "0";
        String sRelayList = "";
        if ((RelayFlag == 1) && (RelaySource == 1)) { //当控制码D6D5位不为11时，无中继级数及中继表地址项。
            sRelayList = sRelayList + RelayCount;
            for (int i = 0; i < RelayCount; i++) {
                String sAmmAdd = new String(RelayAddrList[i]).trim();
                sAmmAdd = DataSwitch.StrStuff("A", 12, sAmmeterAdd, 10); //12位的电表地址左补足A
                sAmmAdd = DataSwitch.ReverseStringByByte(sAmmeterAdd); //按字节倒置
                sRelayList = sRelayList + sAmmAdd;
            }
        }
        String sDA = "";
        if (PN == 0) {
            sDA = "0000";
        } else {
            sDA = DataSwitch.ReverseStringByByte(DataSwitch.IntToHex(Integer.
                    toString(PN), "0000"));
        }

        switch (ControlCode) {
        case 1: { //读数据--DataItemCount=1，只有一个读取的数据标示
            try {
                sControlCode = sControlCode + RelayFlag + RelaySource + "00001";
                sControlCode = DataSwitch.Fun8BinTo2Hex(sControlCode);
                sFrame = sFrame + sAmmeterAdd + sControlCode;
                String sDataCaption = DataSwitch.ReverseStringByByte(new String(
                        DataItemList[0]).trim());
                String sDataArea = sRelayList + sDataCaption;
                String sDataAreaLength = DataSwitch.IntToHex(Integer.toString(
                        sDataArea.length() / 2), "00");
                sFrame = sFrame + sDataAreaLength + sDataArea;
            } catch (Exception ex3) {
            	Glu_ConstDefine.Log1.WriteLog(
                        "Func:IFE_GYAmmeterSer_PointReadAmmeterData---ControlCode=1 Error:" +
                        ex3.toString());
                sFrame = "";
            }
            sFrame = "10" + sDA + "0119" + sFrame;
            FrameList.add(sFrame);
            break;
        }
        case 2: { //写数据--DataItemCount=3，分别表示密码权限及密码、数据标识、数据
            try {
                sControlCode = sControlCode + RelayFlag + RelaySource + "00100";
                sControlCode = DataSwitch.Fun8BinTo2Hex(sControlCode);
                sFrame = sFrame + sAmmeterAdd + sControlCode;
                String sDataCaption = DataSwitch.ReverseStringByByte(new String(
                        DataItemList[0]).trim()) +
                                      DataSwitch.ReverseStringByByte(new String(
                                              DataItemList[1]).trim()) +
                                      DataSwitch.ReverseStringByByte(new String(
                                              DataItemList[2]).trim());
                String sDataArea = sRelayList + sDataCaption;
                String sDataAreaLength = DataSwitch.IntToHex(Integer.toString(
                        sDataArea.length() / 2), "00");
                sFrame = sFrame + sDataAreaLength + sDataArea;
            } catch (Exception ex2) {
            	Glu_ConstDefine.Log1.WriteLog(
                        "Func:IFE_GYAmmeterSer_PointReadAmmeterData---ControlCode=2 Error:" +
                        ex2.toString());
                sFrame = "";
            }
            sFrame = "10" + sDA + "0119" + sFrame;
            FrameList.add(sFrame);
            break;
        }
        case 3: { //广播校时--DataItemCount=1，只有一个主站时间
            try {
                sControlCode = sControlCode + RelayFlag + RelaySource + "01000";
                sControlCode = DataSwitch.Fun8BinTo2Hex(sControlCode);
                sFrame = sFrame + sAmmeterAdd + sControlCode;
                String sDataCaption = DataSwitch.ReverseStringByByte(new String(
                        DataItemList[0]).trim());
                String sDataArea = sRelayList + sDataCaption;
                String sDataAreaLength = DataSwitch.IntToHex(Integer.toString(
                        sDataArea.length() / 2), "00");
                sFrame = sFrame + sDataAreaLength + sDataArea;
            } catch (Exception ex1) {
            	Glu_ConstDefine.Log1.WriteLog(
                        "Func:IFE_GYAmmeterSer_PointReadAmmeterData---ControlCode=3 Error:" +
                        ex1.toString());
                sFrame = "";
            }
            sFrame = "10" + sDA + "0119" + sFrame;
            FrameList.add(sFrame);
            break;
        }
        case 4: { //修改密码--DataItemCount=2，分别表示原密码权限及密码、新密码权限及密码
            try {
                sControlCode = sControlCode + RelayFlag + RelaySource + "01111";
                sControlCode = DataSwitch.Fun8BinTo2Hex(sControlCode);
                sFrame = sFrame + sAmmeterAdd + sControlCode;
                String sDataCaption = DataSwitch.ReverseStringByByte(new String(
                        DataItemList[0]).trim()) +
                                      DataSwitch.ReverseStringByByte(new String(
                                              DataItemList[1]).trim());
                String sDataArea = sRelayList + sDataCaption;
                String sDataAreaLength = DataSwitch.IntToHex(Integer.toString(
                        sDataArea.length() / 2), "00");
                sFrame = sFrame + sDataAreaLength + sDataArea;
            } catch (Exception ex) {
            	Glu_ConstDefine.Log1.WriteLog(
                        "Func:IFE_GYAmmeterSer_PointReadAmmeterData---ControlCode=4 Error:" +
                        ex.toString());
                sFrame = "";
            }
            sFrame = "10" + sDA + "0119" + sFrame;
            FrameList.add(sFrame);
            break;
        }
        }
        return FrameList;
    }

    public ArrayList IFE_QGAmmeterSer_ReadAmmeterData(
            char[] AmmeterAdd, int ProtocolType, int CommandSign, //电表地址,电表规约号,大小项标志(10大项;20小项)
            int DataItemCount, char[][] DataItemList) { //命令字数目,命令字列表
        ArrayList <String>FrameList = new ArrayList<String>();
        String sAmmeterAdd = new String(AmmeterAdd).trim();
        try {
            try {
                SPE_CommandInfoList AmmeterCommandList = new
                        SPE_CommandInfoList();
                if (CommandSign == 10) { //大项
                    AmmeterCommandList = AmmeterDataFrameExplain.
                                         AmmeterQuGuoCommandInfoListDX;
                } else { //小项
                    AmmeterCommandList = AmmeterDataFrameExplain.
                                         AmmeterQuGuoCommandInfoListXX;
                }
                String[] sDataItemList = new String[DataItemCount];
                for (int row = 0; row < DataItemCount; row++) {
                    sDataItemList[row] = new String(DataItemList[row]).trim();
                }
                ArrayList CommandList = new ArrayList();
                CommandList = SearchCommandList(ProtocolType, sDataItemList,
                                                AmmeterCommandList, 0); //通过数据项列表查找队列中对应的命令标识列表,对于组电表帧在这个方法不能再传大小项标志只对测量点数据有用
                String sCommand = "";
                String sFrame = "";
                int iCheck = 0;
                if (sAmmeterAdd.length() % 2 != 0) {
                    sAmmeterAdd = "0" + sAmmeterAdd; //电表地址为奇数时补0
                }
                sAmmeterAdd = DataSwitch.StrStuff("A", 12, sAmmeterAdd, 10); //12位的电表地址左补足A
                sAmmeterAdd = DataSwitch.ReverseStringByByte(sAmmeterAdd); //按字节倒置
                for (int i = 0; i < CommandList.size(); i++) {
                    sCommand = CommandList.get(i).toString();
                    sCommand = AmmeterDataFrameExplain.AmmeterDataSwitch(
                            sCommand, "+"); //转换电表命令(+33H)
                    sCommand = DataSwitch.ReverseStringByByte(sCommand); //倒置
                    sFrame = "68" + sAmmeterAdd + "68" + "0102" + sCommand;
                    iCheck = 0;
                    for (int j = 0; j < sFrame.length() / 2; j++) {
                        iCheck = iCheck +
                                 Integer.parseInt(sFrame.substring(j * 2,
                                j * 2 + 2), 16);
                    }
                    iCheck = iCheck % 256; //重新计算得到的校验码
                    sFrame = sFrame + DataSwitch.IntToHex(("" + iCheck), "00") +
                             "16";
                    FrameList.add(sFrame);
                }
            } catch (Exception e) {
            	Glu_ConstDefine.Log1.WriteLog(
                        "Func:IFE_FrameDataAreaExplain__IFE_QGAmmeterSer_ReadAmmeterData();Error:" +
                        e.toString());
            }
        } finally {
        }
        return FrameList;
    }
    public ArrayList IFE_DLMSAmmeterSer_ReadAmmeterData(
            char[] AmmeterAdd, int ProtocolType, int CommandSign, //电表地址,电表规约号,大小项标志(10大项;20小项)
            int DataItemCount, char[][] DataItemList) { //命令字数目,命令字列表
        ArrayList <String>FrameList = new ArrayList<String>();
        try {
            try {
                String sDataArea =  new String(DataItemList[0]);
                if (sDataArea.length() == 18){
		    		sDataArea = sDataArea + "00";
                }
                SFE_FrameInfo FrameInfo = new SFE_FrameInfo();
				FrameInfo = BuildDBDLMSFrame(AmmeterAdd, 11, 12, "C0".toCharArray(),
						sDataArea.length(), sDataArea.toCharArray());
				String sFrame = new String(FrameInfo.GetCommandFrame());
				FrameList.add(sFrame);
            } catch (Exception e) {
            	Glu_ConstDefine.Log1.WriteLog(
                        "Func:IFE_FrameDataAreaExplain__IFE_DLMSAmmeterSer_ReadAmmeterData();Error:" +
                        e.toString());
            }
        } finally {
        }
        return FrameList;
    }
    public ArrayList IFE_DLMSAmmeterSer_ReadHistoryData(
            char[] AmmeterAdd, int ProtocolType, //电表地址,电表规约号
            int DataItemCount, char[][] DataItemList,  //命令字数目,命令字列表
            char[] DataStartTime, char[] DataEndTime) {//任务起始时间、结束时间 
        ArrayList <String>FrameList = new ArrayList<String>();
        String sDataStartTime = new String(DataStartTime).substring(0, 14).trim();     
        String sDataEndTime = new String(DataEndTime).substring(0, 14).trim();
        String sDataArea = "";
        try {
            try {
                sDataStartTime = DataSwitch.DateTimeToHEX(sDataStartTime,
                        "YYYYMMDDWWHHNNSS");
                sDataEndTime = DataSwitch.DateTimeToHEX(sDataEndTime,
                		"YYYYMMDDWWHHNNSS");
                String sParamItem =  new String(DataItemList[0]); //命令标识   
                sDataArea =  sParamItem + "01020412000809060000010000FF0F0212000019" + sDataStartTime + "19"+ sDataEndTime;  
                SFE_FrameInfo FrameInfo = new SFE_FrameInfo();
                FrameInfo = BuildDBDLMSFrame(AmmeterAdd, 11, 12, "C0".toCharArray(),
						sDataArea.length(), sDataArea.toCharArray());
				String sFrame = new String(FrameInfo.GetCommandFrame());
				FrameList.add(sFrame);
            } catch (Exception e) {
            	Glu_ConstDefine.Log1.WriteLog(
                        "Func:IFE_FrameDataAreaExplain__IFE_DLMSAmmeterSer_ReadHistoryData();Error:" +
                        e.toString());
            }
            
        } finally {
        }         
        return FrameList;
    }
    public ArrayList IFE_DLMSAmmeterSer_SetAmmeterData( //组DLMS电表设置帧
            char[] AmmeterAdd, int ProtocolType, int CommandSign, //电表地址,电表规约号,大小项标志(10大项;20小项)
            int ParamCount, SFE_ParamItem[] ParamList, //参数数目,参数列表
            char[] PasswordLevel, char[] Password) { //密码权限,密码
        ArrayList <String>FrameList = new ArrayList<String>();
        try {
            try {            	
                String sDataArea =  new String(ParamList[0].
                        GetParamCaption()).trim(); //命令标识
                if (sDataArea.length() == 18){
		    		sDataArea = sDataArea + "00";
                }
                String sParamContent = new String(ParamList[0].GetParamContent());
                sDataArea =  sDataArea + sParamContent; 
                SFE_FrameInfo FrameInfo = new SFE_FrameInfo();
				FrameInfo = BuildDBDLMSFrame(AmmeterAdd, 11, 12, "C1".toCharArray(),
						sDataArea.length(), sDataArea.toCharArray());
				String sFrame = new String(FrameInfo.GetCommandFrame());
				FrameList.add(sFrame);
            } catch (Exception e) {
            	Glu_ConstDefine.Log1.WriteLog(
                        "Func:IFE_FrameDataAreaExplain__IFE_DLMSAmmeterSer_SetAmmeterData();Error:" +
                        e.toString());
            }
        } finally {
        }
        return FrameList;
    }
    public ArrayList IFE_DLMSAmmeterSer_ControlAmmeterData( //组DLMS电表控制帧
            char[] AmmeterAdd, int ProtocolType, int CommandSign, //电表地址,电表规约号,大小项标志(10大项;20小项)
            int ParamCount, SFE_ParamItem[] ParamList, //参数数目,参数列表
            char[] PasswordLevel, char[] Password) { //密码权限,密码
        ArrayList <String>FrameList = new ArrayList<String>();
        try {
            try {
            	String sDataArea =  new String(ParamList[0].
                        GetParamCaption()).trim(); //命令标识
                if (sDataArea.length() == 18){
		    		sDataArea = sDataArea + "00";
                }
                String sParamContent = new String(ParamList[0].GetParamContent());
                sDataArea =  sDataArea + sParamContent; 
                SFE_FrameInfo FrameInfo = new SFE_FrameInfo();
				FrameInfo = BuildDBDLMSFrame(AmmeterAdd, 11, 12, "C3".toCharArray(),
						sDataArea.length(), sDataArea.toCharArray());
				String sFrame = new String(FrameInfo.GetCommandFrame());
				FrameList.add(sFrame);
            } catch (Exception e) {
            	Glu_ConstDefine.Log1.WriteLog(
                        "Func:IFE_FrameDataAreaExplain__IFE_DLMSAmmeterSer_ControlAmmeterData;Error:" +
                        e.toString());
            }
        } finally {
        }
        return FrameList;
    }
    public ArrayList IFE_QGAmmeterSer_SetAmmeterData( //组QG电表设置帧
            char[] AmmeterAdd, int ProtocolType, int CommandSign, //电表地址,电表规约号,大小项标志(10大项;20小项)
            int ParamCount, SFE_ParamItem[] ParamList, //参数数目,参数列表
            char[] PasswordLevel, char[] Password) { //密码权限,密码
    	ArrayList <String>FrameList = new ArrayList<String>();
        String sAmmeterAdd = new String(AmmeterAdd).trim();
        String sPasswordLevel = new String(PasswordLevel).trim();
        String sPassword = new String(Password).trim();
        try {
            try {
                SPE_CommandInfoList AmmeterCommandList = new
                        SPE_CommandInfoList();
                if (CommandSign == 10) { //大项
                    AmmeterCommandList = AmmeterDataFrameExplain.
                                         AmmeterQuGuoCommandInfoListDX;
                } else { //小项
                    AmmeterCommandList = AmmeterDataFrameExplain.
                                         AmmeterQuGuoCommandInfoListXX;
                }
                if (sAmmeterAdd.length() % 2 != 0) {
                    sAmmeterAdd = "0" + sAmmeterAdd; //电表地址为奇数时补0
                }
                sAmmeterAdd = DataSwitch.StrStuff("A", 12, sAmmeterAdd, 10); //10位的电表地址左补足A
                sAmmeterAdd = DataSwitch.ReverseStringByByte(sAmmeterAdd); //按字节倒置

                SFE_ParamItem sParamItemList = new SFE_ParamItem();
                ArrayList CommandList = new ArrayList();
                String sParamContent = "";
                String sCommand = "";
                String sFrame = "";
                String sContent = "";
                String sKzm = "";
                int iCheck = 0;
                
                CommandList = SearchQGDB2007CommandList(ProtocolType,ParamCount,ParamList,
                        AmmeterCommandList, 0); //通过数据项列表查找队列中对应的命令标识列表,对于组电表帧在这个方法不能再传大小项标志只对测量点数据有用
                
                for (int i = 0; i < CommandList.size(); i++) {
                	sParamItemList = (SFE_ParamItem)CommandList.get(i); //命令标识
                	sCommand = new String(sParamItemList.GetParamCaption()).trim();
                    sKzm = "04";                    
                    sCommand = DataSwitch.ReverseStringByByte(sCommand); //倒置
                    sParamContent = new String(sParamItemList.GetParamContent());
                    sContent = sCommand + sPasswordLevel + DataSwitch.ReverseStringByByte(sPassword) +
                    		   sParamContent;
                    sContent = AmmeterDataFrameExplain.AmmeterDataSwitch(
                            sContent, "+"); //转换电表命令(+33H)
                    sFrame = "68" + sAmmeterAdd + "68" + sKzm +
                             DataSwitch.
                             IntToHex(("" + sContent.length() / 2), "00") +
                             sContent;
                    iCheck = 0;
                    for (int j = 0; j < sFrame.length() / 2; j++) {
                        iCheck = iCheck +
                                 Integer.parseInt(sFrame.substring(j * 2,
                                j * 2 + 2), 16);
                    }
                    iCheck = iCheck % 256; //重新计算得到的校验码
                    sFrame = sFrame + DataSwitch.IntToHex(("" + iCheck), "00") +
                             "16";
                    FrameList.add(sFrame);
                }
            } catch (Exception e) {
            	Glu_ConstDefine.Log1.WriteLog(
                        "Func:IFE_FrameDataAreaExplain__IFE_QGAmmeterSer_SetAmmeterData();Error:" +
                        e.toString());
            }
        } finally {
        }
        return FrameList;
    }
    public ArrayList IFE_QG2007AmmeterSer_ReadAmmeterData(
            char[] AmmeterAdd, int ProtocolType, int CommandSign, //电表地址,电表规约号,大小项标志(10大项;20小项)
            int DataItemCount, SFE_ParamItem[] ParamList) { //命令字数目,命令字列表
        ArrayList <String>FrameList = new ArrayList<String>();
        String sAmmeterAdd = new String(AmmeterAdd).trim();
        try {
            try {
                SPE_CommandInfoList AmmeterCommandList = new
                        SPE_CommandInfoList();
                if (CommandSign == 10) { //大项
                    AmmeterCommandList = AmmeterDataFrameExplain.
                                         AmmeterQuanGuo2007CommandInfoListDX;
                } else { //小项
                    AmmeterCommandList = AmmeterDataFrameExplain.
                                         AmmeterQuanGuo2007CommandInfoListXX;
                }
                String[] sDataItemList = new String[DataItemCount];
                for (int row = 0; row < DataItemCount; row++) {
                	sDataItemList[row] = new String(ParamList[row].GetParamCaption()).trim();
                }
                SFE_ParamItem sParamItemList = new SFE_ParamItem();
                ArrayList CommandList = new ArrayList();
                CommandList = SearchQGDB2007CommandList(ProtocolType, DataItemCount,ParamList,
                                                AmmeterCommandList, 0); //通过数据项列表查找队列中对应的命令标识列表,对于组电表帧在这个方法不能再传大小项标志只对测量点数据有用
                String sCommand = "";
                String sFrame = "";
                String sParamContent = "";
                int iCheck = 0;
                if (sAmmeterAdd.length() % 2 != 0) {
                    sAmmeterAdd = "0" + sAmmeterAdd; //电表地址为奇数时补0
                }
                sAmmeterAdd = DataSwitch.StrStuff("A", 12, sAmmeterAdd, 10); //12位的电表地址左补足A
                sAmmeterAdd = DataSwitch.ReverseStringByByte(sAmmeterAdd); //按字节倒置
                for (int i = 0; i < CommandList.size(); i++) {
                	sParamItemList = (SFE_ParamItem)CommandList.get(i); //命令标识
                	sCommand = new String(sParamItemList.GetParamCaption()).trim();
                	sParamContent = new String(sParamItemList.GetParamContent());
                    sCommand = AmmeterDataFrameExplain.AmmeterDataSwitch(
                            sCommand, "+"); //转换电表命令(+33H)
                    sCommand = DataSwitch.ReverseStringByByte(sCommand); //倒置
                    sParamContent = AmmeterDataFrameExplain.AmmeterDataSwitch(
                    		sParamContent, "+"); //转换电表命令(+33H)
                    sFrame = "68" + sAmmeterAdd + "68" + "11" +
                             DataSwitch.IntToHex(("" + (sCommand.length() + sParamContent.length())/ 2), "00") +
                             sCommand + sParamContent; //读数据控制码为11
                    iCheck = 0;
                    for (int j = 0; j < sFrame.length() / 2; j++) {
                        iCheck = iCheck +
                                 Integer.parseInt(sFrame.substring(j * 2,
                                j * 2 + 2), 16);
                    }
                    iCheck = iCheck % 256; //重新计算得到的校验码
                    sFrame = sFrame + DataSwitch.IntToHex(("" + iCheck), "00") +
                             "16";
                    FrameList.add(sFrame);
                }
            } catch (Exception e) {
            	Glu_ConstDefine.Log1.WriteLog(
                        "Func:IFE_FrameDataAreaExplain__IFE_QGAmmeterSer_ReadAmmeterData();Error:" +
                        e.toString());
            }
        } finally {
        }
        return FrameList;
    }

    public ArrayList IFE_QG2007AmmeterSer_SetAmmeterData( //组QG2007电表设置帧
            char[] AmmeterAdd, int ProtocolType, int CommandSign, //电表地址,电表规约号,大小项标志(10大项;20小项)
            int ParamCount, SFE_ParamItem[] ParamList, //参数数目,参数列表
            char[] PasswordLevel, char[] Password, char[] OperateCode) { //密码权限,密码,操作者代码
    	ArrayList <String>FrameList = new ArrayList<String>();
        String sAmmeterAdd = new String(AmmeterAdd).trim();
        String sPasswordLevel = new String(PasswordLevel).trim();
        String sPassword = new String(Password).trim();
        String sOperateCode = new String(OperateCode).trim();
        try {
            try {
                SPE_CommandInfoList AmmeterCommandList = new
                        SPE_CommandInfoList();
                if (CommandSign == 10) { //大项
                    AmmeterCommandList = AmmeterDataFrameExplain.
                                         AmmeterQuanGuo2007CommandInfoListDX;
                } else { //小项
                    AmmeterCommandList = AmmeterDataFrameExplain.
                                         AmmeterQuanGuo2007CommandInfoListXX;
                }
                if (sAmmeterAdd.length() % 2 != 0) {
                    sAmmeterAdd = "0" + sAmmeterAdd; //电表地址为奇数时补0
                }
                sAmmeterAdd = DataSwitch.StrStuff("A", 12, sAmmeterAdd, 10); //10位的电表地址左补足A
                sAmmeterAdd = DataSwitch.ReverseStringByByte(sAmmeterAdd); //按字节倒置

                SFE_ParamItem sParamItemList = new SFE_ParamItem();
                ArrayList CommandList = new ArrayList();
                String sParamContent = "";
                String sCommand = "";
                String sFrame = "";
                String sContent = "";
                String sKzm = "";
                int iCheck = 0;

                CommandList = SearchQGDB2007CommandList(ProtocolType,ParamCount,ParamList,
                        AmmeterCommandList, 0); //通过数据项列表查找队列中对应的命令标识列表,对于组电表帧在这个方法不能再传大小项标志只对测量点数据有用
                for (int i = 0; i < CommandList.size(); i++) {
                	sParamItemList = (SFE_ParamItem)CommandList.get(i); //命令标识
                	sCommand = new String(sParamItemList.GetParamCaption()).trim();
                    if (sCommand.substring(0, 2).equals("FE")) { //特殊电表命令:标识=FE+控制码,目前有跳合闸、报警、保电命令
                        sKzm = sCommand.substring(2, 4);
                        sCommand = "";
                    } else if (sCommand.substring(0, 2).equals("07")) { //安全认证相关命令
                        sKzm = "03";
                        sPasswordLevel = "";
                        sPassword = "";
                    } else {
                        sKzm = "14";
                    } //写命令的控制码
                    sCommand = DataSwitch.ReverseStringByByte(sCommand); //倒置
                    sParamContent = new String(sParamItemList.GetParamContent());
                    sContent = sCommand + sPasswordLevel + DataSwitch.ReverseStringByByte(sPassword) +
                    		   DataSwitch.ReverseStringByByte(sOperateCode) +
                               sParamContent;
                    sContent = AmmeterDataFrameExplain.AmmeterDataSwitch(
                            sContent, "+"); //转换电表命令(+33H)
                    sFrame = "68" + sAmmeterAdd + "68" + sKzm +
                             DataSwitch.
                             IntToHex(("" + sContent.length() / 2), "00") +
                             sContent;
                    iCheck = 0;
                    for (int j = 0; j < sFrame.length() / 2; j++) {
                        iCheck = iCheck +
                                 Integer.parseInt(sFrame.substring(j * 2,
                                j * 2 + 2), 16);
                    }
                    iCheck = iCheck % 256; //重新计算得到的校验码
                    sFrame = sFrame + DataSwitch.IntToHex(("" + iCheck), "00") +
                             "16";
                    FrameList.add(sFrame);
                }
            } catch (Exception e) {
            	Glu_ConstDefine.Log1.WriteLog(
                        "Func:IFE_FrameDataAreaExplain__IFE_QG2007AmmeterSer_SetAmmeterData();Error:" +
                        e.toString());
            }
        } finally {
        }
        return FrameList;
    	
    }
  
    public ArrayList IFE_WaterAmmeterSer_ReadAmmeterData(//组水表读取帧
            char[] AmmeterAdd, int ProtocolType, int CommandSign, //电表地址,电表规约号,大小项标志(10大项;20小项)
            int DataItemCount, SFE_ParamItem[] ParamList) { //命令字数目,命令字列表
        ArrayList <String>FrameList = new ArrayList<String>();
        String sAmmeterAdd = new String(AmmeterAdd).trim();
        try {
            try {
                SPE_CommandInfoList AmmeterCommandList = new
                        SPE_CommandInfoList();
                if (CommandSign == 10) { //大项
                    AmmeterCommandList = AmmeterDataFrameExplain.
                                         WaterAmmeterCommandInfoListXX;
                } else { //小项
                    AmmeterCommandList = AmmeterDataFrameExplain.
                    					 WaterAmmeterCommandInfoListXX;
                }
                String[] sDataItemList = new String[DataItemCount];
                for (int row = 0; row < DataItemCount; row++) {
                	sDataItemList[row] = new String(ParamList[row].GetParamCaption()).trim();
                }
                SFE_ParamItem sParamItemList = new SFE_ParamItem();
                ArrayList CommandList = new ArrayList();
                CommandList = SearchQGDB2007CommandList(ProtocolType, DataItemCount,ParamList,
                                                AmmeterCommandList, 0); //通过数据项列表查找队列中对应的命令标识列表,对于组电表帧在这个方法不能再传大小项标志只对测量点数据有用
                String sCommand = "";
                String sFrame = "";
                String sKzm = "";
                if (sAmmeterAdd.length() % 2 != 0) {
                    sAmmeterAdd = "0" + sAmmeterAdd; //电表地址为奇数时补0
                }
                for (int i = 0; i < CommandList.size(); i++) {
                	sParamItemList = (SFE_ParamItem)CommandList.get(i); //命令标识
                	sCommand = new String(sParamItemList.GetParamCaption()).trim();
                	sCommand = new String(sParamItemList.GetParamCaption()).trim();
                    if (sCommand.substring(2, 4).equals("05")) { //读取表号，初始地址为00000000，功能码为05
                    	sAmmeterAdd = "00000000";
                    	sKzm = sCommand.substring(2, 4);
                    } else { //读户号、信息、数据功能码为12
                        sKzm = sCommand.substring(2, 4);
                    } 
                    sFrame = "B9" + sAmmeterAdd + sKzm + "00"; 
                    //CRC-16校验方式
                    String cs = DataSwitch.CRC16(sFrame);                    
                    sFrame = sFrame + DataSwitch.ReverseStringByByte(cs) +
                             "9D";
                    FrameList.add(sFrame);
                }
            } catch (Exception e) {
            	Glu_ConstDefine.Log1.WriteLog(
                        "Func:IFE_FrameDataAreaExplain__IFE_WaterAmmeterSer_ReadAmmeterData();Error:" +
                        e.toString());
            }
        } finally {
        }
        return FrameList;
    }

    public ArrayList IFE_WaterAmmeterSer_SetAmmeterData(//组水表设置帧
    		char[] AmmeterAdd, int ProtocolType, int CommandSign, //电表地址,电表规约号,大小项标志(10大项;20小项)
            int ParamCount, SFE_ParamItem[] ParamList)/*, //参数数目,参数列表
            char[] PasswordLevel, char[] Password, char[] OperateCode) */{ //密码权限,密码,操作者代码
    	ArrayList <String>FrameList = new ArrayList<String>();
        String sAmmeterAdd = new String(AmmeterAdd).trim();
      //  String sPasswordLevel = new String(PasswordLevel).trim();
      //  String sPassword = new String(Password).trim();
      //  String sOperateCode = new String(OperateCode).trim();
        try {
            try {
                SPE_CommandInfoList AmmeterCommandList = new
                        SPE_CommandInfoList();
                if (CommandSign == 10) { //大项
                    AmmeterCommandList = AmmeterDataFrameExplain.
                    					 WaterAmmeterCommandInfoListXX;
                } else { //小项
                    AmmeterCommandList = AmmeterDataFrameExplain.
                    					 WaterAmmeterCommandInfoListXX;
                }
                if (sAmmeterAdd.length() % 2 != 0) {
                    sAmmeterAdd = "0" + sAmmeterAdd; //电表地址为奇数时补0
                }
                SFE_ParamItem sParamItemList = new SFE_ParamItem();
                ArrayList CommandList = new ArrayList();
                String sParamContent = "";
                String sCommand = "";
                String sFrame = "";
                String sContent = "";
                String sKzm = "";
                
                CommandList = SearchQGDB2007CommandList(ProtocolType,ParamCount,ParamList,
                        AmmeterCommandList, 0); //通过数据项列表查找队列中对应的命令标识列表,对于组电表帧在这个方法不能再传大小项标志只对测量点数据有用
                for (int i = 0; i < CommandList.size(); i++) {
                	sParamItemList = (SFE_ParamItem)CommandList.get(i); //命令标识
                	sCommand = new String(sParamItemList.GetParamCaption()).trim();
                    if (sCommand.substring(2, 4).equals("06")) { //设置表号，初始地址为00000000，功能码为06
                    	sAmmeterAdd = "00000000";
                    	sKzm = sCommand.substring(2, 4);
                    } else { //设户号功能码为13，设信息功能码为14
                        sKzm = sCommand.substring(2, 4);
                    } 
                    sParamContent = new String(sParamItemList.GetParamContent());
                    sContent = sParamContent;
                    sFrame = "B9" + sAmmeterAdd + sKzm +
                             DataSwitch.
                             IntToHex(("" + sContent.length() / 2), "00") +
                             sContent;
                    //CRC-16校验方式
                    String cs = DataSwitch.CRC16(sFrame);                    
                    sFrame = sFrame + DataSwitch.ReverseStringByByte(cs) +
                             "9D";
                    FrameList.add(sFrame);
                }
            } catch (Exception e) {
            	Glu_ConstDefine.Log1.WriteLog(
                        "Func:IFE_FrameDataAreaExplain__IFE_WaterAmmeterSer_SetAmmeterData();Error:" +
                        e.toString());
            }
        } finally {
        }
        return FrameList;
    	
    }
    public ArrayList IFE_TJAmmeterSer_ReadAmmeterData( //组天津645电表读取帧
            char[] AmmeterAdd, int ProtocolType, int CommandSign, //电表地址,电表规约号,大小项标志(10大项;20小项)
            int DataItemCount, char[][] DataItemList, char[] StartTime,
            int DataCount) { //命令字数目,命令字列表,起始时间(YYYYMMDDHHNNSS),数据点数
        ArrayList <String>FrameList = new ArrayList<String>();
        String sAmmeterAdd = new String(AmmeterAdd).trim();
        try {
            try {
                SPE_CommandInfoList AmmeterCommandList = new
                        SPE_CommandInfoList();
                if (CommandSign == 10) { //大项
                    AmmeterCommandList = AmmeterDataFrameExplain.
                                         AmmeterTianJinCommandInfoListDX;
                } else { //小项
                    AmmeterCommandList = AmmeterDataFrameExplain.
                                         AmmeterTianJinCommandInfoListXX;
                }
                String[] sDataItemList = new String[DataItemCount];
                for (int row = 0; row < DataItemCount; row++) {
                    sDataItemList[row] = new String(DataItemList[row]).trim();
                }
                ArrayList CommandList = new ArrayList();
                CommandList = SearchCommandList(ProtocolType, sDataItemList,
                                                AmmeterCommandList, CommandSign);
                String sCommand = "", sFrame = "", sContent = "";
                int iCheck = 0;
                if (sAmmeterAdd.length() % 2 != 0) {
                    sAmmeterAdd = "0" + sAmmeterAdd; //电表地址为奇数时补0
                }
                sAmmeterAdd = DataSwitch.StrStuff("A", 10, sAmmeterAdd, 10); //10位的电表地址左补足A
                sAmmeterAdd = DataSwitch.ReverseStringByByte(sAmmeterAdd); //按字节倒置
                sAmmeterAdd = sAmmeterAdd + "F1"; //天津645电表规约特征字节
                for (int i = 0; i < CommandList.size(); i++) {
                    sContent = "";
                    sCommand = CommandList.get(i).toString();
                    if (sCommand.substring(0, 1).equals("D")) { //读取负荷曲线数据:带起始时间和数据点数
                        String sStartTime = new String(StartTime).trim();
                        sContent = DataSwitch.ReverseStringByByte(DataSwitch.
                                DateTimeToBCD(sStartTime, "yymmddhhnn"));
                        sContent = sContent +
                                   DataSwitch.IntToHex("" + DataCount, "00");
                    }
                    sContent = DataSwitch.ReverseStringByByte(sCommand) +
                               sContent;
                    sContent = AmmeterDataFrameExplain.AmmeterDataSwitch(
                            sContent, "+"); //转换电表命令(+33H)
                    sFrame = "68" + sAmmeterAdd + "68" + "01" +
                             DataSwitch.
                             IntToHex(("" + sContent.length() / 2), "00") +
                             sContent;
                    iCheck = 0;
                    for (int j = 0; j < sFrame.length() / 2; j++) {
                        iCheck = iCheck +
                                 Integer.parseInt(sFrame.substring(j * 2,
                                j * 2 + 2), 16);
                    }
                    iCheck = iCheck % 256; //重新计算得到的校验码
                    sFrame = sFrame + DataSwitch.IntToHex(("" + iCheck), "00") +
                             "16";
                    FrameList.add(sFrame);
                }
            } catch (Exception e) {
            	Glu_ConstDefine.Log1.WriteLog(
                        "Func:IFE_FrameDataAreaExplain__IFE_TJAmmeterSer_ReadAmmeterData();Error:" +
                        e.toString());
            }
        } finally {
        }
        return FrameList;
    }

    public ArrayList IFE_TJAmmeterSer_SetAmmeterData( //组天津645电表设置帧
            char[] AmmeterAdd, int ProtocolType, int CommandSign, //电表地址,电表规约号,大小项标志(10大项;20小项)
            int ParamCount, SFE_ParamItem[] ParamList, //参数数目,参数列表
            char[] PasswordLevel, char[] Password) { //密码权限,密码
        ArrayList <String>FrameList = new ArrayList<String>();
        String sAmmeterAdd = new String(AmmeterAdd).trim();
        String sPasswordLevel = new String(PasswordLevel).trim();
        String sPassword = new String(Password).trim();
        try {
            try {
                SPE_CommandInfoList AmmeterCommandList = new
                        SPE_CommandInfoList();
                if (CommandSign == 10) { //大项
                    AmmeterCommandList = AmmeterDataFrameExplain.
                                         AmmeterTianJinCommandInfoListDX;
                } else { //小项
                    AmmeterCommandList = AmmeterDataFrameExplain.
                                         AmmeterTianJinCommandInfoListXX;
                }
                if (sAmmeterAdd.length() % 2 != 0) {
                    sAmmeterAdd = "0" + sAmmeterAdd; //电表地址为奇数时补0
                }
                sAmmeterAdd = DataSwitch.StrStuff("A", 10, sAmmeterAdd, 10); //10位的电表地址左补足A
                sAmmeterAdd = DataSwitch.ReverseStringByByte(sAmmeterAdd); //按字节倒置
                sAmmeterAdd = sAmmeterAdd + "F1"; //天津645电表规约特征字节

                String[] sParamItemList = new String[ParamCount];
                ArrayList CommandList = new ArrayList();
                String sParamContent = "";
                String sCommand = "";
                String sFrame = "";
                String sContent = "";
                String sKzm = "";
                int iCheck = 0;

                for (int i = 0; i < ParamCount; i++) {
                    sParamItemList[0] = new String(ParamList[i].GetParamCaption()).
                                        trim(); //命令标识
                    CommandList = SearchCommandList(ProtocolType,
                            sParamItemList, AmmeterCommandList, 0); //通过数据项列表查找队列中对应的命令标识列表,对于组电表帧在这个方法不能再传大小项标志只对测量点数据有用
                    sCommand = CommandList.get(0).toString();
                    if (sCommand.substring(0, 2).equals("FE")) { //特殊电表命令:标识=FE+控制码
                        sKzm = sCommand.substring(2, 4);
                        sCommand = "";
                    } else {
                        sKzm = "04";
                    } //写命令的控制码
                    sCommand = DataSwitch.ReverseStringByByte(sCommand); //倒置
                    sParamContent = new String(ParamList[i].GetParamContent());
                    sParamContent = DataSwitch.ReverseStringByByte(
                            sParamContent); //命令内容按字节倒置
                    sContent = sCommand + sPasswordLevel + sPassword +
                               sParamContent;
                    sContent = AmmeterDataFrameExplain.AmmeterDataSwitch(
                            sContent, "+"); //转换电表命令(+33H)
                    sFrame = "68" + sAmmeterAdd + "68" + sKzm +
                             DataSwitch.
                             IntToHex(("" + sContent.length() / 2), "00") +
                             sContent;
                    iCheck = 0;
                    for (int j = 0; j < sFrame.length() / 2; j++) {
                        iCheck = iCheck +
                                 Integer.parseInt(sFrame.substring(j * 2,
                                j * 2 + 2), 16);
                    }
                    iCheck = iCheck % 256; //重新计算得到的校验码
                    sFrame = sFrame + DataSwitch.IntToHex(("" + iCheck), "00") +
                             "16";
                    FrameList.add(sFrame);
                }
            } catch (Exception e) {
            	Glu_ConstDefine.Log1.WriteLog(
                        "Func:IFE_FrameDataAreaExplain__IFE_TJAmmeterSer_SetAmmeterData();Error:" +
                        e.toString());
            }
        } finally {
        }
        return FrameList;
    }

    public ArrayList IFE_ZJAmmeterSer_ReadAmmeterData(
            char[] AmmeterAdd, int ProtocolType, int CommandSign, //电表地址,电表规约号,大小项标志(10大项;20小项)
            int DataItemCount, char[][] DataItemList) { //命令字数目,命令字列表
        ArrayList <String>FrameList = new ArrayList<String>();
        String sAmmeterAdd = new String(AmmeterAdd).trim();
        try {
            try {
                SPE_CommandInfoList AmmeterCommandList = new
                        SPE_CommandInfoList();
                if (CommandSign == 10) { //大项
                    AmmeterCommandList = AmmeterDataFrameExplain.
                                         AmmeterZheJiangCommandInfoListDX;
                } else { //小项
                    AmmeterCommandList = AmmeterDataFrameExplain.
                                         AmmeterZheJiangCommandInfoListXX;
                }
                String[] sDataItemList = new String[DataItemCount];
                for (int row = 0; row < DataItemCount; row++) { //转换数据项列表类型
                    sDataItemList[row] = new String(DataItemList[row]).trim();
                }
                ArrayList CommandList = new ArrayList();
                CommandList = SearchCommandList(ProtocolType, sDataItemList,
                                                AmmeterCommandList, 0); //通过数据项列表查找队列中对应的命令标识列表，对于组电表帧在这个方法不能再传大小项标志只对测量点数据有用
                String sCommand = "";
                String sFrame = "";
                int iCheck = 0;
                sAmmeterAdd = DataSwitch.StrStuff("0", 2, sAmmeterAdd, 10); //2位的电表地址左补足0
                for (int i = 0; i < CommandList.size(); i++) {
                    sCommand = CommandList.get(i).toString();
                    sCommand = DataSwitch.ReverseStringByByte(sCommand); //倒置
                    sFrame = sAmmeterAdd + sCommand;
                    iCheck = 0;
                    for (int j = 0; j < sFrame.length() / 2; j++) {
                        iCheck = iCheck +
                                 Integer.parseInt(sFrame.substring(j * 2,
                                j * 2 + 2), 16);
                    }
                    iCheck = iCheck % 256; //重新计算得到的校验码
                    sFrame = "68030368" + sFrame +
                             DataSwitch.IntToHex(("" + iCheck), "00") + "0D";
                    FrameList.add(sFrame);
                }
            } catch (Exception e) {
            	Glu_ConstDefine.Log1.WriteLog(
                        "Func:IFE_FrameDataAreaExplain__IFE_ZJAmmeterSer_ReadAmmeterData();Error:" +
                        e.toString());
            }
        } finally {
        }
        return FrameList;
    }

    //-----------------------------浙规专用接口------------------------------------
    public char[] IFE_ZJSer_ReadTaskData( //读任务数据
            int ProtocolType, int TaskNO, char[] DataStartTime, //规约号,任务号,任务起始时间
            int TaskDataCount, int DataFeqN) { //召测点数,上送间隔倍率
        String sDataStartTime = new String(DataStartTime).substring(0, 14).trim();
        String sDataArea = "";
        try {
            try {
                sDataStartTime = DataSwitch.DateTimeToBCD(sDataStartTime,
                        "yymmddhhnn");
                String sTaskNO = "" + TaskNO;
                sTaskNO = DataSwitch.StrStuff("0", 2, sTaskNO, 10);
                String sTaskDataCount = "" + TaskDataCount;
                sTaskDataCount = DataSwitch.IntToHex(sTaskDataCount, "00");
                String sDataFeqN = "" + DataFeqN;
                sDataFeqN = DataSwitch.IntToHex(sDataFeqN, "00");
                sDataArea = sTaskNO + sDataStartTime + sTaskDataCount +
                            sDataFeqN;
            } catch (Exception e) {
            	Glu_ConstDefine.Log1.WriteLog(
                        "Func:IFE_FrameDataAreaExplain__IFE_ZJSer_ReadTaskData();Error:" +
                        e.toString());
            }
        } finally {
        }
        char[] DataArea = sDataArea.toCharArray();
        return DataArea;
    }
    
    public char[] IFE_ZJSer_ReadProgrammeLog( //读编程日志
            int ProtocolType, int TargetCode, //规约号,测量点信息
            char[] StartTime, int DataCount) { //召测起始时间,召测数据点数
        String sStartTime = new String(StartTime).substring(0, 14).trim();
        String sDataArea = "";
        try {
            try {
                sStartTime = DataSwitch.DateTimeToBCD(sStartTime, "yymmddhhnn");
                String sTargetCodet = "" + TargetCode;
                sTargetCodet = DataSwitch.IntToHex(sTargetCodet, "00");
                String sDataCount = "" + DataCount;
                sDataCount = DataSwitch.IntToHex(sDataCount, "00");
                sDataArea = sTargetCodet + sStartTime + sDataCount;
            } catch (Exception e) {
            	Glu_ConstDefine.Log1.WriteLog(
                        "Func:IFE_FrameDataAreaExplain__IFE_ZJSer_ReadProgrammeLog();Error:" +
                        e.toString());
            }
        } finally {
        }
        char[] DataArea = sDataArea.toCharArray();
        return DataArea;
    }

    public char[] IFE_ZJSer_ReadAlarmData( //读异常日志
            int ProtocolType, int TargetCode, char[] AlarmCode, //规约号,测量点号,报警编码
            char[] StartTime, int DataCount) { //测量点号,召测起始时间,召测数据点数
        String sStartTime = new String(StartTime).substring(0, 14).trim();
        String sAlarmCode = new String(AlarmCode).trim();
        String sDataArea = "";
        try {
            try {
                if (ProtocolType == 88) { //江西配变事件读取格式特殊
                    sDataArea = sAlarmCode.substring(0, 2) +
                                DataSwitch.DateTimeToBCD(sStartTime, "yymmdd");
                } else { //浙规系列其他规约
                    String sTargetCodet = "" + TargetCode;
                    sTargetCodet = DataSwitch.IntToHex(sTargetCodet, "00");
                    sAlarmCode = sAlarmCode.substring(2, 4) +
                                 sAlarmCode.substring(0, 2);
                    sStartTime = DataSwitch.DateTimeToBCD(sStartTime,
                            "yymmddhhnn");
                    String sDataCount = "" + DataCount;
                    sDataCount = DataSwitch.IntToHex(sDataCount, "00");
                    sDataArea = sTargetCodet + sAlarmCode + sStartTime +
                                sDataCount; //数据区
                }
            } catch (Exception e) {
            	Glu_ConstDefine.Log1.WriteLog(
                        "Func:IFE_FrameDataAreaExplain__IFE_ZJSer_ReadAlarmData();Error:" +
                        e.toString());
            }
        } finally {
        }
        char[] DataArea = sDataArea.toCharArray();
        return DataArea;
    }

    //----------------------------组国网帧专用接口----------------------------------
    public char[] IFE_QGSer_CurrentDataQuery( //当前数据查询：一类数据查询、查询终端参数/230M终端参数、实时数据查询、状态数据查询、远方读表
            int ProtocolType, int PnCount, int[] PnList, int ParamCount, //规约号,信息点数目,信息点列表,参数数目
            SFE_ParamItem[] ParamList, SFE_QGSer_TimeLabel TimeLabel) { //参数列表,时间标签
        String sDataArea = "";
        try {
            try {
                SPE_CommandInfoList TerminalCommandList = new
                        SPE_CommandInfoList();
                String[] sDataItemList = new String[ParamCount];
                ArrayList CommandList = new ArrayList();
                for (int row = 0; row < ParamCount; row++) {
                    sDataItemList[row] = new String(ParamList[row].
                            GetParamCaption());
                }
              /*  if (ProtocolType == Glu_ConstDefine.GY_ZD_GUYUAN) {
                    TerminalCommandList = FrameDataExplainGuYuan.
                                          TermialGuYuanCommandInfoList;
                } else */if (ProtocolType == Glu_ConstDefine.GY_ZD_698){
                    TerminalCommandList = FrameDataExplainQuanGuo.
                                          Termial698CommandInfoList;
                } else {
                    TerminalCommandList = FrameDataExplainQuanGuo.
                    TermialQuanGuoCommandInfoList;
                }
                CommandList = SearchCommandList(ProtocolType, sDataItemList,
                                                TerminalCommandList, 20); //通过数据项列表查找队列中对应的命令标识列表
                String[] sCommandList = new String[CommandList.size()];
                for (int i = 0; i < sCommandList.length; i++) {
                    sCommandList[i] = CommandList.get(i).toString();
                }
                ArrayList sDADTList = new ArrayList();
                sDADTList = GetIncorporateDataDellIDList(ProtocolType, PnList,
                        PnCount, sCommandList); //合并数据单元
                for (int i = 0; i < sDADTList.size(); i++) {
                    sDataArea = sDataArea + sDADTList.get(i).toString();
                }
                String sSEQ = "60"; //帧序列域SEQ:时间标签有效标志为0,首帧标志FIR、末帧标志FIN置1,请求确认标志位CON为0,帧序号填0(组帧时重组)
                if (!TimeLabel.GetTp().equals("")) {
                    sSEQ = "E0"; //帧序列域SEQ:时间标签有效标志为1,首帧标志FIR、末帧标志FIN置1,请求确认标志位CON为0,帧序号填0(组帧时重组)
                }
                sDataArea = sSEQ + sDataArea + TimeLabel.GetTp();
            } catch (Exception e) {
            	Glu_ConstDefine.Log1.WriteLog(
                        "Func:IFE_FrameDataAreaExplain__IFE_QGSer_CurrentDataQuery();Error:" +
                        e.toString());
            }
        } finally {
        }
        char[] DataArea = sDataArea.toCharArray();
        return DataArea;
    }

    public char[] IFE_QGSer_HistoryDataQuery( //历史数据查询：国网二类数据/230M历史日、月数据查询
            int ProtocolType, int PnCount, int[] PnList, int ParamCount, //规约号,信息点数目,信息点列表,参数数目
            SFE_ParamItem[] ParamList, SFE_QGSer_TimeLabel TimeLabel, //参数列表,时间标签
            int QueryDataType, char[] StartTime, int DataDensity, int DataCount) { //查询数据类型,数据起始时间,数据密度,数据点数
        String sDataArea = "";
        try {
            try {
                SPE_CommandInfoList TerminalCommandList = new
                        SPE_CommandInfoList();
                String[] sDataItemList = new String[ParamCount];
                ArrayList CommandList = new ArrayList();
                for (int row = 0; row < ParamCount; row++) {
                    sDataItemList[row] = new String(ParamList[row].
                            GetParamCaption());
                }
             /*   if (ProtocolType == Glu_ConstDefine.GY_ZD_GUYUAN) {
                    TerminalCommandList = FrameDataExplainGuYuan.
                                          TermialGuYuanCommandInfoList;
                } else */if (ProtocolType == Glu_ConstDefine.GY_ZD_698){
                    TerminalCommandList = FrameDataExplainQuanGuo.
                    Termial698CommandInfoList;
                }else {
                    TerminalCommandList = FrameDataExplainQuanGuo.
                                          TermialQuanGuoCommandInfoList;
                }
                CommandList = SearchCommandList(ProtocolType, sDataItemList,
                                                TerminalCommandList, 10); //通过数据项列表查找队列中对应的命令标识列表
                String[] sCommandList = new String[CommandList.size()];
                for (int i = 0; i < sCommandList.length; i++) {
                    sCommandList[i] = CommandList.get(i).toString();
                }
                ArrayList sDADTList = new ArrayList();
                sDADTList = GetIncorporateDataDellIDList(ProtocolType, PnList,
                        PnCount, sCommandList); //合并数据单元
                String sDateSign = new String(StartTime);
                if (QueryDataType == 1) { //历史日数据时标
                    sDateSign = DataSwitch.DateTimeToBCD(sDateSign, "yymmdd");
                    sDateSign = DataSwitch.ReverseStringByByte(sDateSign);
                } else if (QueryDataType == 2) { //历史月数据时标
                    sDateSign = DataSwitch.DateTimeToBCD(sDateSign, "yymm");
                    sDateSign = DataSwitch.ReverseStringByByte(sDateSign);
                } else { //曲线数据
                    sDateSign = DataSwitch.DateTimeToBCD(sDateSign,
                            "yymmddhhnn");
                    sDateSign = DataSwitch.ReverseStringByByte(sDateSign);
                    String sDataDensity = DataSwitch.IntToHex(("" + DataDensity),
                            "00");
                    String sDataCount = DataSwitch.IntToHex(("" + DataCount),
                            "00");
                    sDateSign = sDateSign + sDataDensity + sDataCount;
                }
                for (int i = 0; i < sDADTList.size(); i++) {
                    sDataArea = sDataArea + sDADTList.get(i).toString();
                    for (int j = 0; j < sCommandList.length; j++){
                    	sDataArea = sDataArea + sDateSign;
                    }
                }
                String sSEQ = "60"; //帧序列域SEQ:时间标签有效标志为0,首帧标志FIR、末帧标志FIN置1,请求确认标志位CON为0,帧序号填0(组帧时重组)
                if (!TimeLabel.GetTp().equals("")) {
                    sSEQ = "E0"; //帧序列域SEQ:时间标签有效标志为1,首帧标志FIR、末帧标志FIN置1,请求确认标志位CON为0,帧序号填0(组帧时重组)
                }
                sDataArea = sSEQ + sDataArea + TimeLabel.GetTp();                

            } catch (Exception e) {
            	Glu_ConstDefine.Log1.WriteLog(
                        "Func:IFE_FrameDataAreaExplain__IFE_QGSer_HistoryDataQuery();Error:" +
                        e.toString());
            }
        } finally {
        }
        char[] DataArea = sDataArea.toCharArray();
        return DataArea;
    }
    public char[] IFE_IHDSer_HistoryDataQuery( //历史数据查询：二类数据日、周、月、年数据查询
            int ProtocolType, int PnCount, int[] PnList, int ParamCount, //规约号,信息点数目,信息点列表,参数数目
            SFE_ParamItem[] ParamList, SFE_QGSer_TimeLabel TimeLabel, //参数列表,时间标签
            int QueryDataType, char[] StartTime, int DataDensity, int DataCount) { //查询数据类型,数据起始时间,数据密度,数据点数
        String sDataArea = "";
        try {
            try {
                SPE_CommandInfoList TerminalCommandList = new
                        SPE_CommandInfoList();
                String[] sDataItemList = new String[ParamCount];
                ArrayList CommandList = new ArrayList();
                for (int row = 0; row < ParamCount; row++) {
                    sDataItemList[row] = new String(ParamList[row].
                            GetParamCaption());
                }
                TerminalCommandList = FrameDataExplainIHD.
                                      TermialIHDCommandInfoList;
                
                CommandList = SearchCommandList(ProtocolType, sDataItemList,
                                                TerminalCommandList, 10); //通过数据项列表查找队列中对应的命令标识列表
                String[] sCommandList = new String[CommandList.size()];
                for (int i = 0; i < sCommandList.length; i++) {
                    sCommandList[i] = CommandList.get(i).toString();
                }
                ArrayList sDADTList = new ArrayList();
                sDADTList = GetIncorporateDataDellIDList(ProtocolType, PnList,
                        PnCount, sCommandList); //合并数据单元                
                for (int i = 0; i < sDADTList.size(); i++) {
                    sDataArea = sDataArea + sDADTList.get(i).toString();
                }
                String sSEQ = "60"; //帧序列域SEQ:时间标签有效标志为0,首帧标志FIR、末帧标志FIN置1,请求确认标志位CON为0,帧序号填0(组帧时重组)
                if (!TimeLabel.GetTp().equals("")) {
                    sSEQ = "E0"; //帧序列域SEQ:时间标签有效标志为1,首帧标志FIR、末帧标志FIN置1,请求确认标志位CON为0,帧序号填0(组帧时重组)
                }
                sDataArea = sSEQ + sDataArea + TimeLabel.GetTp();

            } catch (Exception e) {
            	Glu_ConstDefine.Log1.WriteLog(
                        "Func:IFE_FrameDataAreaExplain__IFE_QGSer_HistoryDataQuery();Error:" +
                        e.toString());
            }
        } finally {
        }
        char[] DataArea = sDataArea.toCharArray();
        return DataArea;
    }

    public char[] IFE_QGSer_AlarmDataQuery( //当前数据查询：一类数据查询、查询终端参数/230M终端参数、实时数据查询、状态数据查询、远方读表
            int QueryDataType, int StrartPoint, //查询数据类型(重要10/一般20),请求事件记录起始指针Pm
            int DataCount, SFE_QGSer_TimeLabel TimeLabel) { //数据点数,时间标签
        String sDataArea = "";
        try {
            try {
                String sDADT = ""; //数据单元标识
                if (QueryDataType == 10) {
                    sDADT = "00000100";
                } else {
                    sDADT = "00000200";
                }
                String sSEQ = "60"; //帧序列域SEQ:时间标签有效标志为0,首帧标志FIR、末帧标志FIN置1,请求确认标志位CON为0,帧序号填0(组帧时重组)
                if (!TimeLabel.GetTp().equals("")) {
                    sSEQ = "E0"; //帧序列域SEQ:时间标签有效标志为1,首帧标志FIR、末帧标志FIN置1,请求确认标志位CON为0,帧序号填0(组帧时重组)
                }
                String sStrartPoint = DataSwitch.IntToHex(("" + StrartPoint),
                        "00");
                String sEndPoint = DataSwitch.IntToHex(("" +
                        (StrartPoint + DataCount) % 256), "00"); //请求事件记录结束指针Pn=(Pm+DataCount)%256

                sDataArea = sSEQ + sDADT + sStrartPoint + sEndPoint +
                            TimeLabel.GetTp();
            } catch (Exception e) {
            	Glu_ConstDefine.Log1.WriteLog(
                        "Func:IFE_FrameDataAreaExplain__IFE_QGSer_AlarmDataQuery();Error:" +
                        e.toString());
            }
        } finally {
        }
        char[] DataArea = sDataArea.toCharArray();
        return DataArea;
    }
    public char[] IFE_DLMSSer_ReadHistoryData( //读历史数据
            int ProtocolType, int CommandCount, SFE_ParamItem[] ParamList,//规约号,数据项数目，数据项列表
            char[] DataStartTime, char[] DataEndTime) {//任务起始时间、结束时间             
        String sDataStartTime = new String(DataStartTime).substring(0, 14).trim();     
        String sDataEndTime = new String(DataEndTime).substring(0, 14).trim();
        String sDataArea = "";
        try {
            try {
                sDataStartTime = DataSwitch.DateTimeToHEX(sDataStartTime,
                        "YYYYMMDDWWHHNNSS");
                sDataEndTime = DataSwitch.DateTimeToHEX(sDataEndTime,
                		"YYYYMMDDWWHHNNSS");
                String sParamItem = new String(ParamList[0].
                        GetParamCaption()).trim(); //命令标识   
                sDataArea =  sParamItem + "01020412000809060000010000FF0F0212000019" + sDataStartTime + "19"+ sDataEndTime;     
            } catch (Exception e) {
            	Glu_ConstDefine.Log1.WriteLog(
                        "Func:IFE_FrameDataAreaExplain__IFE_ZJSer_ReadTaskData();Error:" +
                        e.toString());
            }
        } finally {
        }
        char[] DataArea = sDataArea.toCharArray();
        return DataArea;
    }
    //-----------------------------------组帧函数----------------------------------
    public SFE_FrameInfo BuildZheJiangFrame( //终端浙江规约拼帧函数
            char[] TerminalLogicAdd, int StationAdd, //终端逻辑地址,主站地址
            int CommandSeq, char[] KZM, int DataAreaLen, char[] DataArea) { //命令序号,控制码,数据区
        SFE_FrameInfo FrameInfo = new SFE_FrameInfo();
        String sFrame = "";
        String sTerminalLogicAdd = new String(TerminalLogicAdd).substring(0, 8).
                                   trim();
        String sDataArea = new String(DataArea).substring(0, DataAreaLen).trim();
        String sKZM = new String(KZM).substring(0, 2).trim();
        try {
            try {
                sTerminalLogicAdd = sTerminalLogicAdd.substring(0, 4) +
                                    sTerminalLogicAdd.substring(6, 8) +
                                    sTerminalLogicAdd.substring(4, 6);
                String sZzdzAndMlxh = GetStationAddrAndCommandSeq(StationAdd,
                        CommandSeq); //得到主站地址和命令序号
                String sLen = "" + (sDataArea.length() / 2);
                sLen = DataSwitch.IntToHex(sLen, "0000");
                sLen = sLen.substring(2, 4) + sLen.substring(0, 2); //数据区长度
                sFrame = "68" + sTerminalLogicAdd + sZzdzAndMlxh + "68" + sKZM +
                         sLen + sDataArea + "0016";
                sFrame = GetFrameInfo.gGetParityByteOfZheJiang(sFrame);
                FrameInfo.SetCommandFrame(sFrame);
                FrameInfo.SetCommandSeq(CommandSeq);
            } catch (Exception e) {
            	Glu_ConstDefine.Log1.WriteLog(
                        "Func:IFE_FrameDataAreaExplain__BuildZheJiangFrame();Error:" +
                        e.toString());
            }
        } finally {
        }
        return FrameInfo;
    }

    public SFE_FrameInfo BuildHeXingFrame( //终端海兴集抄规约拼帧函数
            char[] TerminalLogicAdd, int StationAdd, //终端逻辑地址,主站地址
            int CommandSeq, char[] KZM, int DataAreaLen, char[] DataArea) { //命令序号,控制码,数据区
        SFE_FrameInfo FrameInfo = new SFE_FrameInfo();
        String sFrame = "";
        String sTerminalLogicAdd = new String(TerminalLogicAdd).substring(0, 4).
                                   trim();
        String sDataArea = new String(DataArea).substring(0, DataAreaLen).trim();
        String sKZM = new String(KZM).substring(0, 2).trim();
        try {
            try {
                sTerminalLogicAdd = DataSwitch.ReverseStringByByte(sTerminalLogicAdd);                
                String sLen = "" + ((sDataArea.length() / 2) + 12);
                sLen = DataSwitch.IntToHex(sLen, "00");
                sFrame = "0564" + sLen + "D4" + sTerminalLogicAdd + "0000";
                String CRC1 = DataSwitch.MultiCRC(sFrame, "3D65");
                sDataArea = "C000" + sKZM + sDataArea;
                String CRC2 = DataSwitch.MultiCRC(sDataArea, "3D65");
                sFrame = sFrame + DataSwitch.ReverseStringByByte(CRC1)+ sDataArea + 
                		 DataSwitch.ReverseStringByByte(CRC2);
                FrameInfo.SetCommandFrame(sFrame);
                FrameInfo.SetCommandSeq(CommandSeq);
            } catch (Exception e) {
            	Glu_ConstDefine.Log1.WriteLog(
                        "Func:IFE_FrameDataAreaExplain__BuildHeXingFrame();Error:" +
                        e.toString());
            }
        } finally {
        }
        return FrameInfo;
    }
    public SFE_FrameInfo BuildDLMSFrame( //终端DLMS规约拼帧函数
            char[] TerminalLogicAdd, int StationAdd, //终端逻辑地址,主站地址
            int CommandSeq, char[] KZM, int DataAreaLen, char[] DataArea) { //命令序号,控制码,数据区
        SFE_FrameInfo FrameInfo = new SFE_FrameInfo();
        String sFrame = "";
        String sDataArea = new String(DataArea).substring(0, DataAreaLen).trim();
        String sKZM = new String(KZM).substring(0, 2).trim();
        try {
            try {
                String sLen = "" + ((sDataArea.length() / 2) + 3);
                sLen = DataSwitch.IntToHex(sLen, "0000");
                sFrame = "000100100001" + sLen + sKZM + "0181" + sDataArea;
                FrameInfo.SetCommandFrame(sFrame);
                FrameInfo.SetCommandSeq(CommandSeq);
            } catch (Exception e) {
            	Glu_ConstDefine.Log1.WriteLog(
                        "Func:IFE_FrameDataAreaExplain__BuildHeXingFrame();Error:" +
                        e.toString());
            }
        } finally {
        }
        return FrameInfo;
    }
    public SFE_FrameInfo BuildDBDLMSFrame( //电表DLMS规约拼帧函数
            char[] TerminalLogicAdd, int StationAdd, //终端逻辑地址,主站地址
            int CommandSeq, char[] KZM, int DataAreaLen, char[] DataArea) { //命令序号,控制码,数据区
        SFE_FrameInfo FrameInfo = new SFE_FrameInfo();
        try {//1234--4869
        	String sAddress = new String(TerminalLogicAdd);
        	sAddress = sAddress.substring(sAddress.length()-8, sAddress.length());
        	/*
        	String sHigher = sAddress.substring(sAddress.length()-4, sAddress.length()-2);
        	String sLower = sAddress.substring(sAddress.length()-2, sAddress.length());
        	int iHigher = Integer.parseInt(DataSwitch.HexToInt(sHigher, "00"));
        	int iLower = Integer.parseInt(DataSwitch.HexToInt(sLower, "00"));
        	iHigher = iHigher << 2;
        	if ((iLower & 128) == 128){
        		iHigher = iHigher | 2;
        	}        	        	
        	sHigher = DataSwitch.IntToHex(""+iHigher,"00");
        	iLower = iLower << 1;
        	iLower = iLower | 1;        	
        	sLower = DataSwitch.IntToHex(""+iLower,"00");
        	sAddress = "0002" + sHigher + sLower;
        	*/
        	String sFrame = "";
            String sDataArea = new String(DataArea).substring(0, DataAreaLen).trim();
            String sKZM = new String(KZM).substring(0, 2).trim();
            sDataArea = "0310" + "0000" + "E6E600" + sKZM + "0181" + sDataArea;
            String sLen = "" + ((sDataArea.length() / 2) + 8);
            sLen = DataSwitch.IntToHex(sLen, "0000");
            sFrame = "A" + sLen.substring(1,4) + sAddress + sDataArea;
            String sCRC = DataSwitch.FCS(sFrame.substring(0, 16));
            sFrame = sFrame.substring(0, 16) + DataSwitch.ReverseStringByByte(sCRC) + sFrame.substring(20, sFrame.length());
            sCRC = DataSwitch.FCS(sFrame);
            sFrame = "7E" + sFrame + DataSwitch.ReverseStringByByte(sCRC) + "7E";
            FrameInfo.SetCommandFrame(sFrame);
            FrameInfo.SetCommandSeq(CommandSeq);
        }catch (Exception e) {
        	Glu_ConstDefine.Log1.WriteLog(
                    "Func:IFE_FrameDataAreaExplain__BuildDBDLMSFrame();Error:" +
                    e.toString());
        }     
        return FrameInfo;
    }
    public SFE_FrameInfo BuildIHDFrame( //终端IHD规约拼帧函数
            char[] TerminalLogicAdd, int Gyh, int StationAdd, //终端逻辑地址,主站地址
            int CommandSeq, char[] GNM, int DataAreaLen, char[] DataArea) { //命令序号,功能码,数据区
        SFE_FrameInfo FrameInfo = new SFE_FrameInfo();
        String sFrame = "";
        String sTerminalLogicAdd = new String(TerminalLogicAdd).substring(0, 8).
                                   trim();
        String sDataArea = new String(DataArea).substring(0, DataAreaLen).trim();
        String sGNM = new String(GNM).substring(0, 2).trim();
        try {
            try {
                String sKZM = "";
                if (sGNM.equals("0A") || sGNM.equals("0C") ) { //一类
                    sKZM = "4B";
                } else if (sGNM.equals("04") || sGNM.equals("05")) { //终端参数设置和控制命令
                    sKZM = "4A";
                }
                String sPFC = DataSwitch.IntToHex(("" + CommandSeq), "00"); //帧计数器PFC
                CommandSeq = CommandSeq & 15; //取帧计数器PFC低4位作为帧序号,且不能大于15
                String sSEQ = sDataArea.substring(0, 1); //帧序列域高4位(组数据区已组了高4位,还需要组低4位的帧序号)
                if ((Integer.parseInt(sSEQ, 16) & 8) == 8) { //带时间标签
                    sDataArea = sDataArea.substring(0, sDataArea.length() - 12) + sPFC +
                                sDataArea.substring(sDataArea.length() - 10,
                                		sDataArea.length()); //给时间标签加上帧计数器PFC
                }
                sSEQ = sSEQ + Integer.toString(CommandSeq, 16).toUpperCase(); //得到帧序列域
                sDataArea = sDataArea.substring(2, sDataArea.length()); //消去传入的帧序列域
                sTerminalLogicAdd = sTerminalLogicAdd.substring(2, 4) +
                                    sTerminalLogicAdd.substring(0, 2) +
                                    sTerminalLogicAdd.substring(6, 8) +
                                    sTerminalLogicAdd.substring(4, 6) + "10"; //主站地址和组地址标志目前默认为10
                sDataArea = sKZM + sTerminalLogicAdd + sGNM + sSEQ +
                                sDataArea;
                
                int iLen = sDataArea.length() / 2;
                iLen = (iLen << 2) | 2;                
                String sLen = DataSwitch.IntToHex(("" + iLen), "0000"); //用户数据长度
                sLen = DataSwitch.ReverseStringByByte(sLen);
                sFrame = "68" + sLen + "68" + sDataArea + "0016";
                sFrame = GetFrameInfo.gGetParityByteOfIHD(sFrame);
                FrameInfo.SetCommandSeq(CommandSeq);
                FrameInfo.SetCommandFrame(sFrame);
            } catch (Exception e) {
            	Glu_ConstDefine.Log1.WriteLog(
                        "Func:IFE_FrameDataAreaExplain__BuildIHDFrame();Error:" +
                        e.toString());
            }
        } finally {
        }
        return FrameInfo;
    }
    public SFE_FrameInfo BuildQuanGuoFrame( //终端全国规约拼帧函数
            char[] TerminalLogicAdd, int Gyh, int StationAdd, //终端逻辑地址,主站地址
            int CommandSeq, char[] GNM, int DataAreaLen, char[] DataArea) { //命令序号,功能码,数据区
        SFE_FrameInfo FrameInfo = new SFE_FrameInfo();
        String sFrame = "";
        String sTerminalLogicAdd = new String(TerminalLogicAdd).substring(0, 8).
                                   trim();
        String sDataArea = new String(DataArea).substring(0, DataAreaLen).trim();
        String sGNM = new String(GNM).substring(0, 2).trim();
        try {
            try {
                String sKZM = "";
                if (sGNM.equals("0A") || sGNM.equals("0C") || sGNM.equals("0D") ||
                    sGNM.equals("0E") || sGNM.equals("10") || sGNM.equals("C1") ||
                    sGNM.equals("09") || sGNM.equals("0B") || sGNM.equals("03") ||
                    sGNM.equals("C2") ) { //一类、二类、三类数据读取、中继抄表、自定义命令
                    sKZM = "4B";
                } else if (sGNM.equals("04") || sGNM.equals("05") || sGNM.equals("C0")) { //终端参数设置和控制命令
                    sKZM = "4A";
                } else if (sGNM.equals("01")) { //复位命令
                    sKZM = "41";
                }
                String sPFC = DataSwitch.IntToHex(("" + CommandSeq), "00"); //帧计数器PFC
                CommandSeq = CommandSeq & 15; //取帧计数器PFC低4位作为帧序号,且不能大于15
                String sSEQ = sDataArea.substring(0, 1); //帧序列域高4位(组数据区已组了高4位,还需要组低4位的帧序号)
                if ((Integer.parseInt(sSEQ, 16) & 8) == 8) { //带时间标签
                    sDataArea = sDataArea.substring(0, sDataArea.length() - 12) +
                                sPFC +
                                sDataArea.substring(sDataArea.length() - 10,
                            sDataArea.length()); //给时间标签加上帧计数器PFC
                }
                sSEQ = sSEQ + Integer.toString(CommandSeq, 16).toUpperCase(); //得到帧序列域
                sDataArea = sDataArea.substring(2, sDataArea.length()); //消去传入的帧序列域
                sTerminalLogicAdd = sTerminalLogicAdd.substring(2, 4) +
                                    sTerminalLogicAdd.substring(0, 2) +
                                    sTerminalLogicAdd.substring(6, 8) +
                                    sTerminalLogicAdd.substring(4, 6) + "10"; //主站地址和组地址标志目前默认为10
                if (sGNM.equals("C0")) { //自定义命令特殊处理
                    sDataArea = sKZM + sTerminalLogicAdd + sGNM + sSEQ +
                                sDataArea.substring(0, 8) + "38303030" +
                                sDataArea.substring(8, sDataArea.length());
                } else {
                    sDataArea = sKZM + sTerminalLogicAdd + sGNM + sSEQ +
                                sDataArea;
                }
                int iLen = sDataArea.length() / 2;
                if (Gyh == Glu_ConstDefine.GY_ZD_698){
                	iLen = (iLen << 2) | 2;
                }else {
                	iLen = (iLen << 2) | 1;
                }
                String sLen = DataSwitch.IntToHex(("" + iLen), "0000"); //用户数据长度
                sLen = DataSwitch.ReverseStringByByte(sLen);
                sFrame = "68" + sLen + sLen + "68" + sDataArea + "0016";
                sFrame = GetFrameInfo.gGetParityByteOfQuanGuo(sFrame);
                FrameInfo.SetCommandSeq(CommandSeq);
                FrameInfo.SetCommandFrame(sFrame);
            } catch (Exception e) {
            	Glu_ConstDefine.Log1.WriteLog(
                        "Func:IFE_FrameDataAreaExplain__BuildQuanGuoFrame();Error:" +
                        e.toString());
            }
        } finally {
        }
        return FrameInfo;
    }

    //-----------------------------解数据区----------------------------------------
    public SFE_DataListInfo IFE_ExplainDataArea( //数据区解释接口
            char[] FrameDataArea,char[] TerminalAddress, int TermialProtocolType, //帧数据区,终端逻辑地址,终端规约
            char[] ControlCode, char[] FunctionCode) { //控制码,功能码
        SFE_DataListInfo DataListInfo = new SFE_DataListInfo();
        String sFrameDataArea = new String(FrameDataArea).trim();
        String sControlCode = new String(ControlCode);
        String sFunctionCode = new String(FunctionCode);
        String sTerminalAddress = new String(TerminalAddress);
        try {
            try {
                if (TermialProtocolType == Glu_ConstDefine.GY_ZD_ZHEJIANG || 
                	TermialProtocolType == Glu_ConstDefine.GY_ZD_ZJZB0404 /*||
                    TermialProtocolType == Glu_ConstDefine.GY_ZD_ZJBDZ || 
                    TermialProtocolType == Glu_ConstDefine.GY_ZD_GUANGDONG ||
                    TermialProtocolType == Glu_ConstDefine.GY_ZD_GUANGDONG_SC || 
                    TermialProtocolType == Glu_ConstDefine.GY_ZD_ZJMKB ||
                    TermialProtocolType == 88 || 
                    TermialProtocolType == 110 ||
                    TermialProtocolType == 111*/) { //浙规规约系列
                    if (sControlCode.equals("81") || sControlCode.equals("8F") ||
                        sControlCode.equals("C1")) { //普通数据区解释
                        DataListInfo = FrameDataExplainZheJiang.
                                       ExplainNormalDataAreaZheJiang(
                                               sFrameDataArea,
                                               TermialProtocolType,
                                               sControlCode);
                        if (TermialProtocolType == 110) { //对华隆集抄台区表中继抄表特殊处理
                            String sCaption = new String(((SFE_DataItem) (((
                                    SFE_NormalData) DataListInfo.DataList.get(0)).
                                    DataItemList.get(0))).GetDataCaption());
                            if (sCaption.equals("4021")) {
                                String sAmmeterFrame = new String(((
                                        SFE_DataItem) (((SFE_NormalData)
                                        DataListInfo.DataList.get(0)).
                                        DataItemList.get(0))).GetDataContent());
                                DataListInfo = ExplainAmmeterData(sAmmeterFrame,
                                        TermialProtocolType);
                            }
                        }
                    } else if ((sControlCode.equals("87")) ||
                               (sControlCode.equals("88"))) { //设置返回数据区解释
                        DataListInfo = FrameDataExplainZheJiang.
                                       ExplainSetResultDataAreaZheJiang(
                                               sFrameDataArea,
                                               TermialProtocolType,
                                               sControlCode);
                    } else if (sControlCode.equals("82")) { //历史数据区解释
                        DataListInfo = FrameDataExplainZheJiang.
                                       ExplainHistoryDataAreaZheJiang(
                                               sFrameDataArea,
                                               sTerminalAddress,
                                               TermialProtocolType,
                                               sControlCode);
                        String sCaption = new String(((SFE_DataItem) (((
                                SFE_HistoryData) DataListInfo.DataList.get(0)).
                                DataItemList.get(0))).GetDataCaption());
                        if (sCaption.equals("ZJRW")) { //中继任务解释
                            DataListInfo = ExplainAmmeterTaskData((
                                    SFE_HistoryData) DataListInfo.DataList.get(
                                            0));
                        }
                    } else if (sControlCode.equals("89") ||
                               sControlCode.equals("83")) { //告警数据区解释(江西配变控制码位83)
                        DataListInfo = FrameDataExplainZheJiang.
                                       ExplainAlarmDataAreaZheJiang(
                                               sFrameDataArea,
                                               TermialProtocolType,
                                               sControlCode);
                    } else if (sControlCode.equals("80")) { //中继数据
                        DataListInfo = ExplainAmmeterData(sFrameDataArea,
                                TermialProtocolType);
                    } else {
                        DataListInfo.ExplainResult = 50017; //控制字在规约未定义
                    }
                } 
                else if(TermialProtocolType == Glu_ConstDefine.GY_ZD_HS){
                	SFE_NormalData NormalData = new SFE_NormalData();
                	ArrayList <SFE_NormalData>NormalDataList = new ArrayList<SFE_NormalData>();
                	NormalData = AmmeterDataFrameExplain.ExplainAmmeterFrame(sFrameDataArea);
                	NormalDataList.add(NormalData);
                	DataListInfo.ExplainResult = 0;
                    DataListInfo.DataType = 10;
                    DataListInfo.DataList = NormalDataList;
                	
                } else if (TermialProtocolType == Glu_ConstDefine.GY_ZD_QUANGUO ||
                        //   TermialProtocolType == Glu_ConstDefine.GY_ZD_TIANJIN ||
                         //  TermialProtocolType == Glu_ConstDefine.GY_ZD_GUYUAN ||
                           TermialProtocolType == Glu_ConstDefine.GY_ZD_698) { //国网,天津模块表
                    String sDT = sFrameDataArea.substring(4, 8); //信息类
                    if ((Integer.parseInt(sControlCode.substring(0, 1), 16) & 2) ==
                        1) { //消去2个字节的事件计数器
                        sFrameDataArea = sFrameDataArea.substring(0,
                                sFrameDataArea.length() - 4);
                    }
                    int iDataType = 0;
                 /*   if (TermialProtocolType == Glu_ConstDefine.GY_ZD_GUYUAN) {
                        iDataType = GluMethod.GetGuYuanDataType(sDT,
                                sFunctionCode);
                    } else */if (TermialProtocolType == Glu_ConstDefine.GY_ZD_698){
                        iDataType = GluMethod.GetQuanGuo698DataType(sDT,
                                sFunctionCode); //数据类型:10普通数据;21小时冻结数据;22曲线数据;23日冻结数据;24月冻结数据;30告警数据;40设置返回数据
                    }else {
                        iDataType = GluMethod.GetQuanGuoDataType(sDT,
                                sFunctionCode); //数据类型:10普通数据;21小时冻结数据;22曲线数据;23日冻结数据;24月冻结数据;30告警数据;40设置返回数据
                    }
                    int iFrameType = Integer.parseInt(sControlCode.substring(0,
                            1), 16);
                    if ((iFrameType & 8) == 8 && (iFrameType & 4) == 4) { //DIR=1：表示上行报文;PRM =1：表示来自启动站
                        iFrameType = 11; //表示终端主动上送
                    } else {
                        iFrameType = 10; //表示终端召测返回
                    }
                  /*  if (TermialProtocolType == Glu_ConstDefine.GY_ZD_GUYUAN) {
                        if (iDataType == 40) { //确认/否认
                            DataListInfo = FrameDataExplainGuYuan.
                                           ExplainSetResultDataAreaGuYuan(
                                    sFrameDataArea, TermialProtocolType,
                                    sFunctionCode);
                        } else if (iDataType == 10) { //读取终端、测量点参数
                            DataListInfo = FrameDataExplainGuYuan.
                                           ExplainNormalDataAreaGuYuan(
                                    sFrameDataArea, TermialProtocolType,
                                    sFunctionCode);
                        } else if (iDataType >= 21 && iDataType <= 24) { //一类小时冻结、二类数据(历史数据)
                            DataListInfo = FrameDataExplainGuYuan.
                                           ExplainHistoryDataAreaGuYuan(
                                    sFrameDataArea, TermialProtocolType,
                                    sFunctionCode, iFrameType);
                        } else if (iDataType == 30) { //三类数据(告警数据)
                            DataListInfo = FrameDataExplainGuYuan.
                                           ExplainAlarmDataAreaGuYuan(
                                    sFrameDataArea, TermialProtocolType,
                                    sFunctionCode);
                        } else if (iDataType == 11) { //中继数据(485转发)
                            DataListInfo = ExplainAmmeterData(sFrameDataArea,
                                    TermialProtocolType);
                        } else if (iDataType == 15) { //点抄电表命令（载波）
                            DataListInfo = AmmeterDataFrameExplain.
                                           ExplainPointAmmeterDataAreaGuYuan(
                                    sFrameDataArea);
                        } else {
                            DataListInfo.ExplainResult = 50016; //命令在规约里不支持
                        }
                    } else */{
                        if (iDataType == 40) { //确认/否认
                            DataListInfo = FrameDataExplainQuanGuo.
                                           ExplainSetResultDataAreaQuanGuo(
                                    sFrameDataArea, TermialProtocolType,
                                    sFunctionCode);
                        } else if (iDataType == 10) { //读取终端、测量点参数
                            DataListInfo = FrameDataExplainQuanGuo.
                                           ExplainNormalDataAreaQuanGuo(
                                    sFrameDataArea, TermialProtocolType,
                                    sFunctionCode);
                        } else if (iDataType >= 21 && iDataType <= 24 || iDataType == 26) { //一类小时冻结、二类数据(历史数据)
                            DataListInfo = FrameDataExplainQuanGuo.
                                           ExplainHistoryDataAreaQuanGuo(
                                    sFrameDataArea, TermialProtocolType,
                                    sFunctionCode, iFrameType);
                        } else if (iDataType == 30) { //三类数据(告警数据)
                            DataListInfo = FrameDataExplainQuanGuo.
                                           ExplainAlarmDataAreaQuanGuo(
                                    sFrameDataArea, TermialProtocolType,
                                    sFunctionCode);
                        } else if (iDataType == 25) { //天津模块表主动上送数据
                            DataListInfo = AmmeterDataFrameExplain.
                                           ExplainTianJinHistoryDataArea(
                                    sFrameDataArea, TermialProtocolType,
                                    sFunctionCode, iFrameType);
                        } else if (iDataType == 11) { //中继数据
                            DataListInfo = ExplainAmmeterData(sFrameDataArea,
                                    TermialProtocolType);
                        } else {
                            DataListInfo.ExplainResult = 50016; //命令在规约里不支持
                        }
                    }
                }else if (TermialProtocolType == Glu_ConstDefine.GY_ZD_HEXING ) { //海兴集中器规约
                   String sCommandID = sFrameDataArea.substring(0, 4); //应用层命令ID
                   String sContent = sFrameDataArea.substring(4, sFrameDataArea.length());
                   int iDataType = 0;
                   iDataType = GluMethod.GetHeXingDataType(sCommandID,sContent,
                               sFunctionCode,sControlCode); //数据类型:10读取返回;20非事件主动上送;30告警数据主动上送;30告警数据读取返回;40设置否认返回
                   
                   int iFrameType = Integer.parseInt(sControlCode.substring(0,
                           2), 16);
                   if (iFrameType == 130) { //sControlCode=130：表示上行报文;sControlCode =129：表示来自启动站
                       iFrameType = 11; //表示终端主动上送
                   } else {
                       iFrameType = 10; //表示终端召测返回
                   }
                  
                   if (iDataType == 40) { //确认/否认
                       DataListInfo = FrameDataExplainHeXing.
                                      ExplainSetResultDataAreaHeXing(
                               sFrameDataArea, TermialProtocolType,
                               sFunctionCode,sControlCode);
                   } else if (iDataType == 10 || iDataType == 20) { //读取终端、测量点参数,非告警主动上送
                       DataListInfo = FrameDataExplainHeXing.
                                      ExplainNormalDataAreaHeXing(
                               sFrameDataArea, TermialProtocolType,
                               sFunctionCode);
                   } else if (iDataType >= 21 && iDataType <= 25) { //一类小时冻结、二类数据(历史数据)
                       DataListInfo = FrameDataExplainHeXing.
                                      ExplainHistoryDataAreaHeXing(
                               sFrameDataArea, TermialProtocolType,
                               sFunctionCode, iDataType, iFrameType);
                   } /*else if (iDataType == 30) { //三类数据(告警数据)
                       DataListInfo = FrameDataExplainHeXing.
                       				  ExplainAlarmDataAreaHeXing(
                               sFrameDataArea, TermialProtocolType,
                               sFunctionCode);
                   } */else {
                       DataListInfo.ExplainResult = 50016; //命令在规约里不支持
                   }
              
                } else if (TermialProtocolType == Glu_ConstDefine.GY_ZD_IHD) { //IHD
                   String sDT = sFrameDataArea.substring(4, 8); //信息类
                   if ((Integer.parseInt(sControlCode.substring(0, 1), 16) & 2) ==
                       1) { //消去2个字节的事件计数器
                       sFrameDataArea = sFrameDataArea.substring(0,
                               sFrameDataArea.length() - 4);
                   }
                   int iDataType = 0;
                   iDataType = GluMethod.GetIHDDataType(sDT,
                               sFunctionCode); //数据类型:10普通数据;11中继数据(普通数据结构);21小时冻结数据;22曲线数据;23日冻结数据;24月冻结数据;25天津模块表主动上送数据;26周冻结数据;27年冻结数据;30告警数据;40设置返回数据
                   
                   int iFrameType = Integer.parseInt(sControlCode.substring(0,
                           1), 16);
                   if ((iFrameType & 8) == 8 && (iFrameType & 4) == 4) { //DIR=1：表示上行报文;PRM =1：表示来自启动站
                       iFrameType = 11; //表示终端主动上送
                   } else {
                       iFrameType = 10; //表示终端召测返回
                   }                 
                   if (iDataType == 40) { //确认/否认
                       DataListInfo = FrameDataExplainIHD.
                                      ExplainSetResultDataAreaIHD(
                               sFrameDataArea, TermialProtocolType,
                               sFunctionCode);
                   } else if (iDataType == 10) { //读取终端、测量点参数
                       DataListInfo = FrameDataExplainIHD.
                                      ExplainNormalDataAreaIHD(
                               sFrameDataArea, TermialProtocolType,
                               sFunctionCode);
                   } else if (iDataType >= 21 && iDataType <= 27) { //二类数据(历史数据)
                       DataListInfo = FrameDataExplainIHD.
                       					ExplainHistoryDataAreaIHD(
				                sFrameDataArea, TermialProtocolType,
				                sFunctionCode, iFrameType);
                   }else {
                       DataListInfo.ExplainResult = 50016; //命令在规约里不支持
                   }               
                }
            } catch (Exception e) {
            	Glu_ConstDefine.Log1.WriteLog(
                        "Func:IFE_ExplainDataArea;Error:" +
                                    e.toString());
            }
        } finally {
        }
        return DataListInfo;
    }
    //DLMS规约数据区解析，命令标识作为参数
    public SFE_DataListInfo IFE_ExplainDataAreaDLMS( //数据区解释接口
    		char[] CommandID,//命令标识
            char[] FrameDataArea, int TermialProtocolType) { //帧数据区,终端规约           
        SFE_DataListInfo DataListInfo = new SFE_DataListInfo();
        String sCommandID = new String(CommandID).trim();
        String sFrameDataArea = new String(FrameDataArea).trim();
        try {
            try {
                if (TermialProtocolType == Glu_ConstDefine.GY_ZD_DLMS ){
                	String sIdentifier = sFrameDataArea.substring(0, 2); //应用层命令ID
                	if (sIdentifier.equals("C5") || sIdentifier.equals("C7")){//Set/Action
                		DataListInfo = FrameDataExplainDLMS.
		                        ExplainSetResultDataAreaDLMS(
		                        sFrameDataArea, TermialProtocolType);
                	}else if (sIdentifier.equals("C4")){//Get
                		DataListInfo = FrameDataExplainDLMS.
                				ExplainNormalDataAreaDLMS(
                                sFrameDataArea, TermialProtocolType,
                                sCommandID);
                	}else if (sIdentifier.equals("61")){//AARE
                		
                	}else if (sIdentifier.equals("C2")){//Alarm
                		DataListInfo = FrameDataExplainDLMS.
                				ExplainAlarmDataAreaDLMS(
                				sFrameDataArea, TermialProtocolType);
                	}
                }
                else if (TermialProtocolType == Glu_ConstDefine.GY_ZD_QUANGUO ||
                		 TermialProtocolType == Glu_ConstDefine.GY_ZD_698 ){         	
                    DataListInfo = ExplainDLMSAmmeterData(sFrameDataArea,
                                TermialProtocolType,sCommandID);
                    
                }
            } catch (Exception e) {
            	Glu_ConstDefine.Log1.WriteLog(
                        "Func:IFE_ExplainDataArea;Error:" +
                                    e.toString());
            }
        } finally {
        }
        return DataListInfo;
    }

    //------------------------------数据区解析接口调用函数------------------------------
    public SFE_DataListInfo ExplainAmmeterTaskData( //解释中继任务数据
            SFE_HistoryData HistoryData) { //任务数据
        ArrayList <SFE_HistoryData>HistoryDataList = new ArrayList<SFE_HistoryData>();
        SFE_DataListInfo DataListInfo = new SFE_DataListInfo();
        try {
            try {
                SFE_NormalData NormalData = new SFE_NormalData();
                String AmmeterFrame = new String(((SFE_DataItem) HistoryData.
                                                  DataItemList.get(0)).
                                                 GetDataContent());
                NormalData = AmmeterDataFrameExplain.ExplainAmmeterFrame(
                        AmmeterFrame);
                HistoryData.DataItemList = NormalData.DataItemList;
                HistoryDataList.add(HistoryData);
            } catch (Exception e) {
            	Glu_ConstDefine.Log1.WriteLog(
                        "Func:IFE_FrameDataAreaExplain__ExplainAmmeterTaskData();Error:" +
                        e.toString());
            }
        } finally {
        }
        DataListInfo.ExplainResult = 0;
        DataListInfo.DataType = 20;
        DataListInfo.DataList = HistoryDataList;
        return DataListInfo;
    }

    public SFE_DataListInfo ExplainAmmeterData( //解释中继数据
            String FrameDataArea, int TermialProtocolType) { //数据区,终端规约
        ArrayList <SFE_NormalData>NormalDataList = new ArrayList<SFE_NormalData>();
        SFE_DataListInfo DataListInfo = new SFE_DataListInfo();
        try {
            try {
                SFE_NormalData NormalData = new SFE_NormalData();
                if ((TermialProtocolType >= Glu_ConstDefine.GY_ZD_ZHEJIANG &&
                     TermialProtocolType <= 86) ||
                     TermialProtocolType == 110) { //浙规系列
                    int iMeasuredPoint = Integer.parseInt(FrameDataArea.
                            substring(0, 2));
                    NormalData = AmmeterDataFrameExplain.ExplainAmmeterFrame(
                            FrameDataArea.substring(2, FrameDataArea.length()));
                    NormalData.SetMeasuredPointNo(iMeasuredPoint);
                    NormalData.SetMeasuredPointType(10);
                    NormalDataList.add(NormalData);
                    DataListInfo.ExplainResult = 0;
                    DataListInfo.DataType = 10;
                    DataListInfo.DataList = NormalDataList;
                } else if (TermialProtocolType == Glu_ConstDefine.GY_ZD_QUANGUO || 
                          // TermialProtocolType == Glu_ConstDefine.GY_ZD_GUYUAN ||
                		   TermialProtocolType == Glu_ConstDefine.GY_ZD_698 ) { //国网
                	int iLen = 0;
                	if (TermialProtocolType == Glu_ConstDefine.GY_ZD_698){//698规约电表帧长度为2个字节
                		iLen = Integer.parseInt(FrameDataArea.substring(12, 14)+FrameDataArea.substring(10, 12),
					                                16) * 2; //电表帧字符长度
					    FrameDataArea = FrameDataArea.substring(14,
					            FrameDataArea.length());
                	}else {
                		iLen = Integer.parseInt(FrameDataArea.substring(8, 10),
					                                16) * 2; //电表帧字符长度
					    FrameDataArea = FrameDataArea.substring(10,
					            FrameDataArea.length());
                	}
                	
                    NormalData = AmmeterDataFrameExplain.ExplainAmmeterFrame(
                            FrameDataArea.substring(0, iLen));
                    NormalData.SetMeasuredPointNo(0);
                    NormalData.SetMeasuredPointType(10);
                    NormalDataList.add(NormalData);
                    DataListInfo.ExplainResult = 0;
                    DataListInfo.DataType = 10;
                    DataListInfo.DataList = NormalDataList;
                }/* else if (TermialProtocolType == Glu_ConstDefine.GY_ZD_TIANJIN) { //天津模块表
                    int iLen = Integer.parseInt(FrameDataArea.substring(8, 10),
                                                16) * 2; //电表帧字符长度
                    FrameDataArea = FrameDataArea.substring(10,
                            FrameDataArea.length());
                    DataListInfo = AmmeterDataFrameExplain.
                                   ExplainTianJinAmmeterFrame(FrameDataArea.
                            substring(0, iLen));
                }*/
            } catch (Exception e) {
            	Glu_ConstDefine.Log1.WriteLog(
                        "Func:IFE_FrameDataAreaExplain__ExplainAmmeterData();Error:" +
                        e.toString());
            }
        } finally {
        }
        return DataListInfo;
    }
    public SFE_DataListInfo ExplainDLMSAmmeterData( //解释DLMS中继数据
            String FrameDataArea, int TermialProtocolType, String CommandID ) { //数据区,终端规约,命令标识
        ArrayList <SFE_NormalData>NormalDataList = new ArrayList<SFE_NormalData>();
        SFE_DataListInfo DataListInfo = new SFE_DataListInfo();        
        try {
            try {
                SFE_NormalData NormalData = new SFE_NormalData();
                if ((TermialProtocolType >= Glu_ConstDefine.GY_ZD_ZHEJIANG &&
                     TermialProtocolType <= 86) ||
                     TermialProtocolType == 110) { //浙规系列
                    int iMeasuredPoint = Integer.parseInt(FrameDataArea.
                            substring(0, 2));
                    NormalData = AmmeterDataFrameExplain.ExplainDLMSAmmeterFrame(
                            FrameDataArea.substring(2, FrameDataArea.length()),CommandID);
                    NormalData.SetMeasuredPointNo(iMeasuredPoint);
                    NormalData.SetMeasuredPointType(10);
                    NormalDataList.add(NormalData);
                    DataListInfo.ExplainResult = 0;
                    DataListInfo.DataType = 10;
                    DataListInfo.DataList = NormalDataList;
                } else if (TermialProtocolType == Glu_ConstDefine.GY_ZD_QUANGUO || 
                          // TermialProtocolType == Glu_ConstDefine.GY_ZD_GUYUAN ||
                		   TermialProtocolType == Glu_ConstDefine.GY_ZD_698 ) { //国网
                	int iLen = 0;
                	if (TermialProtocolType == Glu_ConstDefine.GY_ZD_698){//698规约电表帧长度为2个字节
                		iLen = Integer.parseInt(FrameDataArea.substring(12, 14)+FrameDataArea.substring(10, 12),
					                                16) * 2; //电表帧字符长度
					    FrameDataArea = FrameDataArea.substring(14,
					            FrameDataArea.length());
                	}else {
                		iLen = Integer.parseInt(FrameDataArea.substring(8, 10),
					                                16) * 2; //电表帧字符长度
					    FrameDataArea = FrameDataArea.substring(10,
					            FrameDataArea.length());
                	}
                	
                    NormalData = AmmeterDataFrameExplain.ExplainDLMSAmmeterFrame(
                            FrameDataArea.substring(0, iLen),CommandID);                    
                    NormalData.SetMeasuredPointNo(0);
                    NormalData.SetMeasuredPointType(10);
                    NormalDataList.add(NormalData);   
                    if (CommandID.substring(0,6).equals("000700") && CommandID.substring(8,18).equals("180300FF02")){
                    	ArrayList <SFE_HistoryData>HistoryDataList = new ArrayList<SFE_HistoryData>();
                    	SFE_NormalData NormalDataTemp = null;
                    	SFE_HistoryData[] HistoryDataListTemp = new SFE_HistoryData[24]; //数据点数最大值=24
                        for (int i = 0; i < 24; i++) {
                            HistoryDataListTemp[i] = new SFE_HistoryData();
                        }
                    	SFE_DataItem DataItem = null;
                    	int iTaskCount = -1;
                    	for (int i = 0; i < NormalDataList.size(); i++){
                    		NormalDataTemp = (SFE_NormalData)NormalDataList.get(i);
                    		for (int j = 0;	 j < NormalDataTemp.DataItemList.size();j++) { //把解释后的数据保存在中间对象里
                    			DataItem = NormalDataTemp.DataItemList.get(j);
                    			String DataCaption = new String(DataItem.GetDataCaption());
                    			if (DataCaption.equals("HD0000")){  
                    				iTaskCount = iTaskCount + 1;
                    				HistoryDataListTemp[iTaskCount].SetTaskDateTime(new String(DataItem.GetDataContent()));
                    			}else{
                    				HistoryDataListTemp[iTaskCount].DataItemList.add(NormalDataTemp.DataItemList.get(j));
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
                        DataListInfo.DataType = 10;
                        DataListInfo.DataList = NormalDataList;  
                    }
                }/* else if (TermialProtocolType == Glu_ConstDefine.GY_ZD_TIANJIN) { //天津模块表
                    int iLen = Integer.parseInt(FrameDataArea.substring(8, 10),
                                                16) * 2; //电表帧字符长度
                    FrameDataArea = FrameDataArea.substring(10,
                            FrameDataArea.length());
                    DataListInfo = AmmeterDataFrameExplain.
                                   ExplainTianJinAmmeterFrame(FrameDataArea.
                            substring(0, iLen));
                }*/
            } catch (Exception e) {
            	Glu_ConstDefine.Log1.WriteLog(
                        "Func:IFE_FrameDataAreaExplain__ExplainDLMSAmmeterData();Error:" +
                        e.toString());
            }
        } finally {
        }
        return DataListInfo;
    }

    public ArrayList SearchCommandList(int ProtocolType, String[] sDataItemList,
                                       SPE_CommandInfoList AmmeterCommandList,
                                       int CommandSign) { //通过数据项列表查找队列中对应的命令标识列表,对于组电表帧在这个方法不能再传大小项标志只对测量点数据有用
        ArrayList <String>ResultList = new ArrayList<String>();
        try {
            try {
                String sCommand = ""; //命令标识
                String sCommandNext = "";
                String sCommandNow = "";
                String sDataCaption = ""; //数据项
                int iDataItemCount = 0; //命令的数据项个数
                int iCommandCount = 0;
                if (ProtocolType == Glu_ConstDefine.GY_ZD_QUANGUO || 
                //	ProtocolType == Glu_ConstDefine.GY_ZD_GUYUAN || 
                	ProtocolType == Glu_ConstDefine.GY_ZD_698 ||
                	ProtocolType == Glu_ConstDefine.GY_ZD_HEXING||
                	ProtocolType == Glu_ConstDefine.GY_ZD_DLMS) { //国网不存在大小项,避免跟浙规处理冲突,大小项标志置20小项
                    CommandSign = 20;
                }
                for (int k = 0; k < sDataItemList.length; k++) { //组帧数据项数目
                    boolean bIsGet = false;
                    for (int i = 0; i < AmmeterCommandList.GetFCommandCount();
                                 i++) { //队列命令数目
                        if (bIsGet) {
                            break;
                        }
                        iDataItemCount = ((SFE_CommandInfo) (AmmeterCommandList.
                                CommandInfoList.get(i))).GetFDataItemCount();
                        sCommand = ((SFE_CommandInfo) (AmmeterCommandList.
                                CommandInfoList.get(i))).GetFProtocolCommand(); //得到该命令
                        //命令匹配有两种:全部匹配;前三位匹配且数据库录入的命令是以X结尾的(用901X来表示9010~901F)
                        if (sDataItemList[k].equals(sCommand) ||
                            (sDataItemList[k].substring(0,
                                3).equals(sCommand.substring(0, 3)) &&
                             sCommand.substring(3, 4).equals("X"))) { //先匹配命令
                            if (CommandSign == 10) { //组大项
                                sCommandNow = sCommand.substring(0, 3) + "F";
                            } else {
                                sCommandNow = sDataItemList[k];
                            }
                            break;
                        } else { //如果找不到再匹配数据项
                            for (int j = 0; j < iDataItemCount; j++) { //命令的数据项数目
                                sDataCaption = ((SFE_DataItemInfo) (((
                                        SFE_CommandInfo) (AmmeterCommandList.
                                        CommandInfoList.get(i))).
                                        DataItemInfoList.get(j))).
                                               GetFDataCaption();
                                //数据项匹配有三种:全部匹配;前三位匹配且数据库录入的命令是以X结尾的(用901X来表示9010~901F)；前两位匹配，第4位是F或者Y，并且是国网规约，主要用于谐波
                                if (sDataItemList[k].equals(sDataCaption) ||
                                    (sDataItemList[k].substring(0,
                                        3).equals(sDataCaption.substring(0, 3)) &&
                                     (sDataCaption.substring(3, 4).equals("X"))) ||
                                    (sDataItemList[k].substring(0,
                                        2).equals(sDataCaption.substring(0, 2)) &&
                                     (sDataCaption.substring(3, 4).equals("F") ||
                                      sDataCaption.substring(3, 4).equals("Y")) &&
                                     ProtocolType == 100)) {
                                    if (CommandSign == 10) { //组大项
                                        sCommandNow = sCommand.substring(0, 3) +
                                                "F";
                                    } else {
                                        if (sDataItemList[k].substring(0, 3).
                                            equals(sDataCaption.substring(0, 3)) &&
                                            sDataCaption.substring(3,
                                                4).equals("X")) { //模糊匹配:901X表示9010~901E
                                            sCommandNow = sCommand.substring(0,
                                                    3) + sDataItemList[k].
                                                    substring(3, 4); //数据库录数据域901X、数据项000X,主站传入0000,则发送命令为901+0;
                                        } else {
                                            sCommandNow = sCommand;
                                        }
                                    }
                                    bIsGet = true;
                                    break;
                                }
                            }
                        }
                    }
                    if (sCommandNow.length() != 0) { //找到
                        if (iCommandCount == 0) {
                            ResultList.add(sCommandNow);
                            iCommandCount = iCommandCount + 1;
                        } else {
                            boolean bIsEqual = false;
                            for (int j = 0; j < iCommandCount; j++) {
                                sCommandNext = ResultList.get(j).toString();
                                if ((sCommandNext).equals(sCommandNow)) { //找到重复命令
                                    bIsEqual = true;
                                }
                            }
                            if (bIsEqual == false) { //如果是新命令
                                ResultList.add(sCommandNow);
                                iCommandCount = iCommandCount + 1;
                            }
                        }
                    }
                }
            } catch (Exception e) {
            	Glu_ConstDefine.Log1.WriteLog(
                        "Func:IFE_FrameDataAreaExplain__SearchCommandList();Error:" +
                        e.toString());
            }
        } finally {
        }
        return ResultList;
    }

    public ArrayList SearchQGDB2007CommandList(int ProtocolType, int DataItemCount, SFE_ParamItem[] sDataItemList,
            SPE_CommandInfoList AmmeterCommandList,
            int CommandSign) { //通过数据项列表查找队列中对应的命令标识列表,对于组电表帧在这个方法不能再传大小项标志只对测量点数据有用
    	ArrayList <SFE_ParamItem>ResultList = new ArrayList<SFE_ParamItem>();
        try {
            try {
                String sCommand = ""; //命令标识
                String sCommandNext = "";
                String sCommandNow = "";
                String sDataCaption = ""; //数据项
                String sParamContent ="";
                SFE_ParamItem sParamItemNow = new SFE_ParamItem();
                int iDataItemCount = 0; //命令的数据项个数
                int iCommandCount = 0;
                for (int k = 0; k < DataItemCount; k++) { //组帧数据项数目
                    boolean bIsGet = false;
                    for (int i = 0; i < AmmeterCommandList.GetFCommandCount();
                                 i++) { //队列命令数目
                        if (bIsGet) {
                            break;
                        }
                        iDataItemCount = ((SFE_CommandInfo) (AmmeterCommandList.
                                CommandInfoList.get(i))).GetFDataItemCount();
                        sCommand = ((SFE_CommandInfo) (AmmeterCommandList.
                                CommandInfoList.get(i))).GetFProtocolCommand(); //得到该命令
                        String sDataItem = new String(sDataItemList[k].GetParamCaption()).trim();
                        sParamContent = new String(sDataItemList[k].GetParamContent()).trim();
                        //命令匹配有两种:全部匹配;前四位匹配且五六位为XX的(用0000XX00来表示00000000~0000FE00)
                        if (sDataItem.equals(sCommand) ||
                            (sDataItem.substring(0,4).equals(sCommand.substring(0, 4)) &&
                             sCommand.substring(4, 6).equals("XX"))) { //先匹配命令
                            if (CommandSign == 10) { //组大项
                                sCommandNow = sCommand.substring(0, 4) + "FF" + sCommand.substring(6, sCommand.length());
                            } else {
                                sCommandNow = sDataItem;
                            }
                            sParamItemNow.SetParamCaption(sCommandNow);
                       //     sParamContent = DataSwitch.ReverseStringByByte(sParamContent);
                            sParamItemNow.SetcParamContent(sParamContent);
                            break;
                        } else { //如果找不到再匹配数据项
                            for (int j = 0; j < iDataItemCount; j++) { //命令的数据项数目
                                sDataCaption = ((SFE_DataItemInfo) (((
                                        SFE_CommandInfo) (AmmeterCommandList.
                                        CommandInfoList.get(i))).
                                        DataItemInfoList.get(j))).
                                               GetFDataCaption();
                                //数据项匹配有两种:全部匹配;前两位匹配且三四位为XX的(用00XX来表示0000~00FE)
                                if ( sDataItem.equals(sDataCaption) ||
                                    (sDataItem.length() == sDataCaption.length() &&
                                     sDataItem.substring(0,2).equals(sDataCaption.substring(0, 2)) &&
                                     (sDataCaption.substring(2, 4).equals("XX"))) ||
                                    (sDataItem.length() == sDataCaption.length() &&
                                     sDataItem.substring(0,2).equals(sDataCaption.substring(0, 2)) &&
                                     sDataCaption.substring(2, 4).equals("XX") &&
                                     sDataCaption.substring(4, 6).equals("YY"))) {
                                    if (CommandSign == 10) { //组大项
                                        sCommandNow = sCommand.substring(0, 4) +
                                                "FF" + sCommand.substring(6, sCommand.length());
                                    } else {
                                    	if (sDataItem.length() == sDataCaption.length() &&
                                    		sDataItem.substring(0,2).equals(sDataCaption.substring(0, 2)) &&
                                            sDataCaption.substring(2, 4).equals("XX") &&
                                            sDataCaption.substring(4, 6).equals("YY")){
                                    		sCommandNow = sCommand.substring(0,
                                                    4) + sDataItem. substring(4, 8); //数据库录数据域0000XXYY、数据项0CXXYY,主站传入0C0001,则发送命令为0000+00+01;
                                    	} else if (sDataItem.length() == sDataCaption.length() &&
                                    			   sDataItem.substring(0, 2).equals(sDataCaption.substring(0, 2)) &&
                                                   (sDataCaption.substring(2,4).equals("XX") ||
                                                	sDataCaption.substring(2,4).equals("YY")) ) { 
                                            sCommandNow = sCommand.substring(0,
                                                    6) + sDataItem.substring(2, 4); //数据库录数据域040300XX、数据项4EXX,主站传入4E01,则发送命令为040300+01;
                                        } else {
                                            sCommandNow = sCommand;
                                        }
                                    }
                                    bIsGet = true;
                                    sParamItemNow.SetParamCaption(sCommandNow);
                               //     sParamContent = DataSwitch.ReverseStringByByte(sParamContent);
                                    sParamItemNow.SetcParamContent(sParamContent);
                                    break;
                                }
                            }
                        }
                    }
                    if (sCommandNow.length() != 0) { //找到
                        if (iCommandCount == 0) {
                            ResultList.add(sParamItemNow);
                            iCommandCount = iCommandCount + 1;
                        } else {
                            boolean bIsEqual = false;
                            for (int j = 0; j < iCommandCount; j++) {
                                sCommandNext = new String(ResultList.get(j).GetParamCaption()).trim();
                                sParamContent = new String(ResultList.get(j).GetParamContent()).trim();
                                sParamContent = sParamContent +  new String(sParamItemNow.GetParamContent()).trim();
                                if ((sCommandNext).equals(sCommandNow)) { //找到重复命令
                                	ResultList.get(j).SetcParamContent(sParamContent);
                                    bIsEqual = true;
                                }
                            }
                            if (bIsEqual == false) { //如果是新命令
                                ResultList.add(sParamItemNow);
                                iCommandCount = iCommandCount + 1;
                            }
                        }
                    }
                }
            } catch (Exception e) {
            	Glu_ConstDefine.Log1.WriteLog(
                        "Func:IFE_FrameDataAreaExplain__SearchQGDB2007CommandList();Error:" +
                        e.toString());
            }
        } finally {
        }
        return ResultList;
    }

    public String SearchCommand(String DataCaption,
                                SPE_CommandInfoList TerminalCommandList,
                                int CommandSign) { //通过数据项查找队列中对应的命令标识,主要用于终端浙规系列
        String sResult = "";
        try {
            try {
                boolean bIsGet = false;
                String sCommand = "";
                String sDataCaption = "";
                int iDataItemCount = 0; //命令的数据项个数
                for (int i = 0; i < TerminalCommandList.GetFCommandCount(); i++) { //队列命令数目
                    if (bIsGet) {
                        break;
                    }
                    iDataItemCount = ((SFE_CommandInfo) (TerminalCommandList.
                            CommandInfoList.get(i))).GetFDataItemCount();
                    sCommand = ((SFE_CommandInfo) (TerminalCommandList.
                            CommandInfoList.get(i))).GetFProtocolCommand(); //得到该命令
                    if (DataCaption.equals(sCommand)) { //先匹配命令
                        if (CommandSign == 10) { //组大项
                            sResult = sCommand.substring(0, 3) + "F";
                        } else {
                            sResult = sCommand;
                        }
                        break;
                    } else { //如果找不到再匹配数据项
                        for (int j = 0; j < iDataItemCount; j++) { //命令的数据项数目
                            sDataCaption = ((SFE_DataItemInfo) (((
                                    SFE_CommandInfo) (TerminalCommandList.
                                    CommandInfoList.get(i))).DataItemInfoList.
                                    get(j))).GetFDataCaption();
                            if (DataCaption.equals(sDataCaption)) {
                                if (CommandSign == 10) { //组大项
                                    sResult = sCommand.substring(0, 3) + "F";
                                } else {
                                    sResult = sCommand;
                                }
                                bIsGet = true;
                                break;
                            }
                        }
                    }
                }
            } catch (Exception e) {
            	Glu_ConstDefine.Log1.WriteLog(
                        "Func:IFE_FrameDataAreaExplain__SearchCommand();Error:" +
                        e.toString());
            }
        } finally {
        }
        return sResult;
    }

    public ArrayList SearchZheJiangCommandList(int ProtocolType,
                                               String[] sDataItemList,
                                               int CommandSign) { //通过数据项列表查找队列中对应的命令标识列表,主要用于终端浙规系列
        ArrayList <String>ResultList = new ArrayList<String>();
        try {
            try {
                String sCommandNext = "";
                String sCommandNow = "";
                int iCommandCount = 0;

                SPE_CommandInfoList TerminalCommandList = new
                        SPE_CommandInfoList();
                for (int k = 0; k < sDataItemList.length; k++) { //组帧数据项数目
                    if (ProtocolType == Glu_ConstDefine.GY_ZD_ZJZB0404) { //浙江增补
                        TerminalCommandList = FrameDataExplainZheJiang.
                                              TermialZheJiangExCommandInfoList;
                    }/* else if (ProtocolType == Glu_ConstDefine.GY_ZD_ZJBDZ) { //变电站规约
                        TerminalCommandList = FrameDataExplainZheJiang.
                                              TermialZheJiangBDZCommandInfoList;
                    } else if (ProtocolType == Glu_ConstDefine.GY_ZD_GUANGDONG) { //广东规约
                        TerminalCommandList = FrameDataExplainZheJiang.
                                              TermialGuangDongCommandInfoList;
                    } else if (ProtocolType == Glu_ConstDefine.GY_ZD_ZJMKB) { //模块表规约
                        TerminalCommandList = FrameDataExplainZheJiang.
                                              TermialZheJiangMKBCommandInfoList;
                    } else if (ProtocolType == 88) { //江西配变
                        TerminalCommandList = FrameDataExplainZheJiang.
                                              TermialJiangXiPBCommandInfoList;
                    } else if (ProtocolType == 111) { //华隆集抄(两层,采集器层透明)
                        TerminalCommandList = FrameDataExplainZheJiang.
                                              TermialZheJiangHLJC2CommandInfoList;
                    }*/
                    sCommandNow = SearchCommand(sDataItemList[k],
                                                TerminalCommandList,
                                                CommandSign);
                    if (sCommandNow.length() != 0) { //找到
                        if (iCommandCount == 0) {
                            ResultList.add(sCommandNow);
                            iCommandCount = iCommandCount + 1;
                        } else {
                            boolean bIsEqual = false;
                            for (int j = 0; j < iCommandCount; j++) {
                                sCommandNext = ResultList.get(j).toString();
                                if ((sCommandNext).equals(sCommandNow)) { //找到重复命令
                                    bIsEqual = true;
                                }
                            }
                            if (bIsEqual == false) { //如果是新命令
                                ResultList.add(sCommandNow);
                                iCommandCount = iCommandCount + 1;
                            }
                        }
                    } else { //如果特殊部分找不到再到公用的浙规队列查找
                        TerminalCommandList = FrameDataExplainZheJiang.
                                              TermialZheJiangCommandInfoList;
                        sCommandNow = SearchCommand(sDataItemList[k],
                                TerminalCommandList, CommandSign);
                        if (sCommandNow.length() != 0) { //找到
                            if (iCommandCount == 0) {
                                ResultList.add(sCommandNow);
                                iCommandCount = iCommandCount + 1;
                            } else {
                                boolean bIsEqual = false;
                                for (int j = 0; j < iCommandCount; j++) {
                                    sCommandNext = ResultList.get(j).toString();
                                    if ((sCommandNext).equals(sCommandNow)) { //找到重复命令
                                        bIsEqual = true;
                                    }
                                }
                                if (bIsEqual == false) { //如果是新命令
                                    ResultList.add(sCommandNow);
                                    iCommandCount = iCommandCount + 1;
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
            	Glu_ConstDefine.Log1.WriteLog(
                        "Func:IFE_FrameDataAreaExplain__SearchCommand();Error:" +
                        e.toString());
            }
        } finally {
        }
        return ResultList;
    }

    public String GetMeasuredPointSign(SFE_MeasuredPointInfor MeasuredPointInfo,
                                       int ProtocolType) { //计算浙规测量点标志
        String sResult = "";
        try {
            try {
                if ((ProtocolType == Glu_ConstDefine.GY_ZD_ZHEJIANG) ||
                	(ProtocolType == Glu_ConstDefine.GY_ZD_ZJZB0404) /*||
                    (ProtocolType == Glu_ConstDefine.GY_ZD_ZJBDZ) || 
                    (ProtocolType == Glu_ConstDefine.GY_ZD_GUANGDONG) ||
                    (ProtocolType == Glu_ConstDefine.GY_ZD_GUANGDONG_SC) || 
                    (ProtocolType == Glu_ConstDefine.GY_ZD_ZJMKB) ||
                    (ProtocolType == 88) || 
                    (ProtocolType == 110) ||
                    (ProtocolType == 111)*/) { //终端浙江系列
                    if (MeasuredPointInfo.MeasuredPointListType == 30) { //所有测量点
                        sResult = "FE";
                    } else {
                        char[] MeasuredPointList = new char[64];
                        for (int i = 0; i < 64; i++) {
                            MeasuredPointList[i] = '0';
                        }
                        for (int i = 0;
                                     i < MeasuredPointInfo.MeasuredPointCount;
                                     i++) {
                            if ((MeasuredPointInfo.MeasuredPointList[i] <= 63) &&
                                (MeasuredPointInfo.MeasuredPointList[i] >= 0)) {
                                MeasuredPointList[63 - MeasuredPointInfo.
                                        MeasuredPointList[i]] = '1';
                            }
                        }
                        String sMeasuredPointSign = new String(
                                MeasuredPointList).trim();
                        for (int i = 0; i < 8; i++) {
                            sResult = sResult +
                                      DataSwitch.Fun8BinTo2Hex(
                                              sMeasuredPointSign.
                                              substring(i * 8, i * 8 + 8));
                        }
                    }
                    sResult = DataSwitch.ReverseStringByByte(sResult);
                }/* else if (ProtocolType == 110) { //华隆集抄(主站直接传集抄码下来,集抄码后三位表示表号,三位前的数表示采集器号)
                    String sJCM = "", sCJQBH = "", sDBBH = "", sDXLX = ""; //集抄码、采集器编号、电表编号、对象类型
                    for (int i = 0; i < MeasuredPointInfo.MeasuredPointCount; i++) {
                        sCJQBH = DataSwitch.IntToHex("" +
                                MeasuredPointInfo.MeasuredPointList[i] / 1000,
                                "00"); //采集器编号
                        sDBBH = Integer.toString(MeasuredPointInfo.
                                                 MeasuredPointList[i]);
                        sDBBH = DataSwitch.IntToHex(sDBBH.substring(sDBBH.
                                length() - 3, sDBBH.length()), "00"); //电表编号
                        if (MeasuredPointInfo.MeasuredPointListType == 30) { //抄采集器下所有电表
                            sJCM = sJCM + "FF" + sCJQBH + sCJQBH;
                        } else {
                            sJCM = sJCM + sDBBH + sCJQBH + sCJQBH;
                        }
                    }
                    if (MeasuredPointInfo.MeasuredPointListType == 10 ||
                        MeasuredPointInfo.MeasuredPointListType == 30) { //对象是电表
                        sDXLX = "04";
                    } else if (MeasuredPointInfo.MeasuredPointListType == 70) { //对象是采集器
                        sDXLX = "02";
                    } else if (MeasuredPointInfo.MeasuredPointListType == 80) { //对象是台区表
                        sDXLX = "01";
                    } else if (MeasuredPointInfo.MeasuredPointListType == 60) { //对象是集中器
                        sDXLX = "00";
                    }
                    if (MeasuredPointInfo.MeasuredPointCount == 0) { //读取或则设置集中器参数没有集抄码
                        sResult = "0000";
                    } else {
                        sResult = sDXLX +
                                  DataSwitch.IntToHex("" +
                                MeasuredPointInfo.MeasuredPointCount, "00") +
                                  sJCM;
                    }
                }*/
            } catch (Exception e) {
            	Glu_ConstDefine.Log1.WriteLog(
                        "Func:IFE_FrameDataAreaExplain__GetMeasuredPointSign();Error:" +
                        e.toString());
            }
        } finally {
        }
        return sResult;
    }

    public String GetStationAddrAndCommandSeq(int StationAdd, int CommandSeq) { //计算主站地址和命令序号
        String sResult = "";
        try {
            try {
                StationAdd = StationAdd & 63; //得到主站地址
                int CommandSeqL = CommandSeq & 3; //得到命令序号的低2位
                int CommandSeqH = (CommandSeq & 124) >> 2; //得到命令序号的高5位
                int iZZDZandMLXHL = StationAdd ^ (CommandSeqL << 6); //得到主站地址和命令序号的低字节
                int iZZDZandMLXHH = CommandSeqH; //得到主站地址和命令序号的高字节
                String sZZDZandMLXL = "" + iZZDZandMLXHL;
                sZZDZandMLXL = DataSwitch.IntToHex(sZZDZandMLXL, "00");
                String sZZDZandMLXH = "" + iZZDZandMLXHH;
                sZZDZandMLXH = DataSwitch.IntToHex(sZZDZandMLXH, "00");
                sResult = sZZDZandMLXL + sZZDZandMLXH;
            } catch (Exception e) {
            	Glu_ConstDefine.Log1.WriteLog(
                        "Func:IFE_FrameDataAreaExplain__GetStationAddrAndCommandSeq();Error:" +
                        e.toString());
            }
        } finally {
        }
        return sResult;
    }

    public ArrayList GetIncorporateDataDellIDList(int ProtocolType,
                                                  int[] MeasuredPointList,
                                                  int MeasuredPointCount,
                                                  String[] sCommandList) { //得到合并过的国网数据单元标识队列
        ArrayList <String>sDADTList = new ArrayList<String>();
        try {
            try {
                char[] cMeasuredPointList = new char[2040];
                char[] cDT1 = {'0', '0', '0', '0', '0', '0', '0', '0'};
                ArrayList <String>sDAList = new ArrayList<String>();
                ArrayList <String>sDTList = new ArrayList<String>();
                boolean IsEqualZero = false;
                String sDT1 = "", sDT2 = "", sDA1 = "", sDA2 = "", sDT = "",
                        sDA = ""; ; //信息类元、信息类组、信息点元、信息点组,信息类,信息点
                //处理信息点
            /*    if (ProtocolType == Glu_ConstDefine.GY_ZD_GUYUAN) {
                    if (MeasuredPointList[0] == 0) {
                        sDAList.add("0000");
                    } else {
                        for (int i = 0; i < MeasuredPointCount; i++) {
                            sDAList.add(DataSwitch.ReverseStringByByte(
                                    DataSwitch.IntToHex(Integer.toString(
                                            MeasuredPointList[i]), "0000")));
                        }
                    }
                } 
                else */if (ProtocolType == Glu_ConstDefine.GY_ZD_698){//698规约测量点最多2040
                    for (int i = 0; i < 2040; i++) {
                        cMeasuredPointList[i] = '0';
                    }
                    for (int i = 0; i < MeasuredPointCount; i++) {
                        if (MeasuredPointList[i] == 0 && IsEqualZero == false) { //保证只有一个测量点0
                            sDAList.add("0000");
                            IsEqualZero = true;
                        } 
                        else if ((MeasuredPointList[i] <= 2040) &&
                                   (MeasuredPointList[i] > 0)) {
                            cMeasuredPointList[2040 - MeasuredPointList[i]] = '1';
                        }
                    }
                    String sMeasuredPointSign = new String(cMeasuredPointList).
                                                trim();
                    for (int i = 0; i < 255; i++) {
                        sDA1 = DataSwitch.Fun8BinTo2Hex(sMeasuredPointSign.
                                substring(i * 8, i * 8 + 8)); //信息点元
                        if (!sDA1.equals("00")) {
                            sDA2 = "" + (255 - i);
                            sDA2 = DataSwitch.IntToHex(sDA2, "00"); //信息点组
                            sDAList.add(sDA1 + sDA2); //元在前组在后
                        }
                    }
                } 
                else {//国网规约测量点最多64
                    for (int i = 0; i < 64; i++) {
                        cMeasuredPointList[i] = '0';
                    }
                    for (int i = 0; i < MeasuredPointCount; i++) {
                        if (MeasuredPointList[i] == 0 && IsEqualZero == false) { //保证只有一个测量点0
                            sDAList.add("0000");
                            IsEqualZero = true;
                        } else if ((MeasuredPointList[i] <= 64) &&
                                   (MeasuredPointList[i] > 0)) {
                            cMeasuredPointList[64 - MeasuredPointList[i]] = '1';
                        }
                    }
                    String sMeasuredPointSign = new String(cMeasuredPointList).
                                                trim();
                    for (int i = 0; i < 8; i++) {
                        sDA1 = DataSwitch.Fun8BinTo2Hex(sMeasuredPointSign.
                                substring(i * 8, i * 8 + 8)); //信息点元
                        if (!sDA1.equals("00")) {
                            sDA2 = "" + (int) (128 / Math.pow(2, i));
                            sDA2 = DataSwitch.IntToHex(sDA2, "00"); //信息点组
                            sDAList.add(sDA1 + sDA2); //元在前组在后
                        }
                    }
                }
                //处理信息类
                int iTemp = 0;
                for (int i = 0; i < sCommandList.length; i++) {
                    iTemp = Integer.parseInt(sCommandList[i].substring(1, 4));
                    sDT1 = "" + (int) (Math.pow(2, (iTemp - 1) % 8));
                    sDT1 = DataSwitch.IntToHex(sDT1, "00"); //信息类元
                    sDT2 = "" + (iTemp - 1) / 8;
                    sDT2 = DataSwitch.IntToHex(sDT2, "00"); //信息类组
                    sDTList.add(sDT1 + sDT2); //元在前组在后
                }
                for (int i = 0; i < sDTList.size(); i++) {
                    cDT1 = "00000000".toCharArray();
                    //合并信息类
                    sDT1 = (sDTList.get(i).toString()).substring(0, 4); //当前的信息类
                    if (!sDT1.equals("FFFF")) { //判断信息类是否已经处理过
                        cDT1 = Set1ByBit(cDT1, sDT1.substring(0, 2)); //当前的信息类元
                        for (int j = i + 1; j < sDTList.size(); j++) {
                            sDT2 = (sDTList.get(j).toString()).substring(0, 4); //下一个信息类
                            if (sDT1.substring(2, 4).equals(sDT2.substring(2, 4))) { //信息类组相同
                                cDT1 = Set1ByBit(cDT1, sDT2.substring(0, 2));
                                sDTList.set(j, "FFFF"); //同一组处理过的信息类用FFFF特殊标识
                            }
                        }
                        sDT = new String(cDT1);
                        sDT = DataSwitch.Fun8BinTo2Hex(sDT) +
                              sDT1.substring(2, 4); //得到合并过的信息类
                        //得到数据单元标识=信息点+信息元
                        for (int k = 0; k < sDAList.size(); k++) {
                            sDA = sDAList.get(k).toString();
                            sDADTList.add(sDA + sDT);
                        }
                    }
                }
            } catch (Exception e) {
            	Glu_ConstDefine.Log1.WriteLog(
                        "Func:IFE_FrameDataAreaExplain__GetIncorporateDataDellIDList();Error:" +
                        e.toString());
            }
        } finally {
        }
        return sDADTList;
    }

    public char[] Set1ByBit(char[] cList, String sPower2) { //对一个字节按位置1
        try {
            try {
                int iTemp = Integer.parseInt(sPower2, 16);
                if (iTemp == 1) { //如果不等于F1
                    cList[7] = '1';
                } else if (iTemp == 2) {
                    cList[6] = '1';
                } else if (iTemp == 4) {
                    cList[5] = '1';
                } else if (iTemp == 8) {
                    cList[4] = '1';
                } else if (iTemp == 16) {
                    cList[3] = '1';
                } else if (iTemp == 32) {
                    cList[2] = '1';
                } else if (iTemp == 64) {
                    cList[1] = '1';
                } else if (iTemp == 128) {
                    cList[0] = '1';
                }
            } catch (Exception e) {
            	Glu_ConstDefine.Log1.WriteLog(
                        "Func:IFE_FrameDataAreaExplain__Set1ByBit();Error:" +
                        e.toString());
            }
        } finally {
        }
        return cList;
    }

    public String GetDataCellID(int ProtocolType, int[] MeasuredPointList,
                                int MeasuredPoint,
                                String Command) {
        String sDADT = new String();
        try {
            try {
                String sDT1 = "", sDT2 = "", sDT = "",
                        sDA = ""; ; //信息类元、信息类组、信息点元、信息点组,信息类,信息点
                //处理信息点
             /*   if (ProtocolType == Glu_ConstDefine.GY_ZD_GUYUAN) {
                    if (MeasuredPointList[MeasuredPoint - 1] == 0) {
                        sDA = "0000";
                    } else {
                        sDA = DataSwitch.ReverseStringByByte(DataSwitch.
                                IntToHex(Integer.toString(MeasuredPointList[
                                MeasuredPoint - 1]), "0000"));
                    }
                } else {

                }*/
                //处理信息类
                int iTemp = 0;
                iTemp = Integer.parseInt(Command.substring(1, 4));
                sDT1 = "" + (int) (Math.pow(2, (iTemp - 1) % 8));
                sDT1 = DataSwitch.IntToHex(sDT1, "00"); //信息类元
                sDT2 = "" + (iTemp - 1) / 8;
                sDT2 = DataSwitch.IntToHex(sDT2, "00"); //信息类组
                sDT = sDT1 + sDT2; //元在前组在后
                sDADT = sDA + sDT;
            } catch (Exception e) {
            	Glu_ConstDefine.Log1.WriteLog(
                        "Func:IFE_FrameDataAreaExplain__GetDataDellID();Error:" +
                        e.toString());
            }
        } finally {
        }
        return sDADT;
    }

    public String GetDataDellID(int ProtocolType, int MeasuredPoint,
                                String Command) { //得到国网数据单元标识(即单个测量点单个命令)
        String sDADT = new String();
        try {
            try {
                char[] MeasuredPointList = new char[2040];
                String sDT1 = "", sDT2 = "", sDA1 = "", sDA2 = "", sDT = "",
                        sDA = ""; ; //信息类元、信息类组、信息点元、信息点组,信息类,信息点
                //处理信息点
             /*   if (ProtocolType == Glu_ConstDefine.GY_ZD_GUYUAN) {
                    if (MeasuredPointList[MeasuredPoint] == 0) {
                        sDA = "0000";
                    } else {
                        sDA = DataSwitch.ReverseStringByByte(DataSwitch.
                                IntToHex(Integer.toString(MeasuredPointList[
                                MeasuredPoint]), "0000"));
                    }
                } else */if (ProtocolType == Glu_ConstDefine.GY_ZD_698) {//698规约
                    for (int i = 0; i < 2040; i++) {
                        MeasuredPointList[i] = '0';
                    }
                    if (MeasuredPoint == 0) { //保证只有一个测量点0
                        sDA = "0000";
                    } else if ((MeasuredPoint <= 2040) && (MeasuredPoint > 0)) {
                        MeasuredPointList[2040 - MeasuredPoint] = '1';
                        String sMeasuredPointSign = new String(
                                MeasuredPointList).
                                trim();
                        for (int j = 0; j < 255; j++) {
                            sDA1 = DataSwitch.Fun8BinTo2Hex(sMeasuredPointSign.
                                    substring(j * 8, j * 8 + 8)); //信息点元
                            if (!sDA1.equals("00")) {
                                sDA2 = "" + (int) (255-j);
                                sDA2 = DataSwitch.IntToHex(sDA2, "00"); //信息点组
                                sDA = sDA1 + sDA2; //元在前组在后
                            }
                        }
                    }
                } else {
                    for (int i = 0; i < 64; i++) {
                        MeasuredPointList[i] = '0';
                    }
                    if (MeasuredPoint == 0) { //保证只有一个测量点0
                        sDA = "0000";
                    } else if ((MeasuredPoint <= 64) && (MeasuredPoint > 0)) {
                        MeasuredPointList[64 - MeasuredPoint] = '1';
                        String sMeasuredPointSign = new String(
                                MeasuredPointList).
                                trim();
                        for (int j = 0; j < 8; j++) {
                            sDA1 = DataSwitch.Fun8BinTo2Hex(sMeasuredPointSign.
                                    substring(j * 8, j * 8 + 8)); //信息点元
                            if (!sDA1.equals("00")) {
                                sDA2 = "" + (int) (128 / Math.pow(2, j));
                                sDA2 = DataSwitch.IntToHex(sDA2, "00"); //信息点组
                                sDA = sDA1 + sDA2; //元在前组在后
                            }
                        }
                    }
                }
                //处理信息类
                int iTemp = 0;
                iTemp = Integer.parseInt(Command.substring(1, 4));
                sDT1 = "" + (int) (Math.pow(2, (iTemp - 1) % 8));
                sDT1 = DataSwitch.IntToHex(sDT1, "00"); //信息类元
                sDT2 = "" + (iTemp - 1) / 8;
                sDT2 = DataSwitch.IntToHex(sDT2, "00"); //信息类组
                sDT = sDT1 + sDT2; //元在前组在后
                sDADT = sDA + sDT;
            } catch (Exception e) {
            	Glu_ConstDefine.Log1.WriteLog(
                        "Func:IFE_FrameDataAreaExplain__GetDataDellID();Error:" +
                        e.toString());
            }
        } finally {
        }
        return sDADT;
    }

    private void jbInit() throws Exception {
    }
}

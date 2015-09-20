package com.chooshine.fep.FrameExplain;

import com.chooshine.fep.FrameExplain.Struct_FrameInfo;
import com.chooshine.fep.FrameDataAreaExplain.DataSwitch;
import com.chooshine.fep.FrameDataAreaExplain.SFE_FrameInfo;
import com.chooshine.fep.ConstAndTypeDefine.*;
import java.util.ArrayList;

//import Glu_ConstAndTypeDefine.Glu_TypeDefine;

public class FE_FrameExplain {
	  public FE_FrameExplain() {
	  }

	 // private void jbInit() throws Exception {
		  
	 // }

	  //组命令帧
	  //TerminalLogicAdd	In	char(20)	终端逻辑地址
	  //TermialProtocolNo	In	int	终端规约号
	  //StationNo	In	int	主站序号
	  //CommandSeq	In	int	命令序号
	  //ControlCode	In	Char(5)	控制码
	  //DataContent	In	Char()	数据区内容
	  //FrmeData	Out	Char()	命令帧
	  public char[] IFE_BuildFrame(char[] TerminalLogicAdd, int TermialProtocolNo,
	                               int StationNo, int CommandSeq,
	                               char[] ControlCode, char[] DataContent) {
	    try {
	      switch (TermialProtocolNo) {
	        case Glu_ConstDefine.GY_ZD_ZHEJIANG:
	        case Glu_ConstDefine.GY_ZD_ZJZB0404:
	        /* case Glu_ConstDefine.GY_ZD_ZJBDZ:
	        case Glu_ConstDefine.GY_ZD_GUANGDONG:
	        case Glu_ConstDefine.GY_ZD_GUANGDONG_SC:*/ { //浙江规约系列组帧
	          return IFE_BuildFrameOfZheJiang(TerminalLogicAdd, StationNo,
	                                          CommandSeq, ControlCode,
	                                          DataContent);
	        }
	        case Glu_ConstDefine.GY_ZD_QUANGUO:
	      //  case Glu_ConstDefine.GY_ZD_GUYUAN:
	      //  case Glu_ConstDefine.GY_ZD_TIANJIN: 
	        case Glu_ConstDefine.GY_ZD_698:{ //全国规约系列组帧
	          return IFE_BuildFrameOfQuanGuo(TerminalLogicAdd,Glu_ConstDefine.GY_ZD_698, StationNo,
	                                         CommandSeq, ControlCode,
	                                         DataContent);
	        }
	        case Glu_ConstDefine.GY_ZD_HS:{
	        	return DataContent;
	        }        	
	        case Glu_ConstDefine.GY_ZD_HEXING:{
	        	return IFE_BuildFrameOfHeXing(TerminalLogicAdd, StationNo,
	                    CommandSeq, ControlCode,
	                    DataContent);
	        }        	
	        case Glu_ConstDefine.GY_ZD_DLMS:{
	        	return IFE_BuildFrameOfDLMS(TerminalLogicAdd, StationNo,
	                    CommandSeq, ControlCode,
	                    DataContent);
	        }        	
	        case Glu_ConstDefine.GY_ZD_IHD:{
	        	return IFE_BuildFrameOfIHD(TerminalLogicAdd, StationNo,
	                    CommandSeq, ControlCode,
	                    DataContent);
	        }
	        default: {
	          return null;
	        }

	      }

	    }
	    catch (NumberFormatException ex) {
	      return null;
	    }
	  }

	  private int SearchAmmeterFrame(String sAmmeterFrame) { //检验电表帧是浙规还部规
	    int iResult = 0;
	    try {
	      try {
	        String sTemp = "";
	        String sCheck = "";
	        int iCheck = 0;
	        sAmmeterFrame = DataSwitch.SearchStr("68", sAmmeterFrame); //搜索以68开头的电表帧,电表帧前有可能有间隔符FE
	        int iLen = (sAmmeterFrame.trim()).length();
	        if ( (sAmmeterFrame.length() % 2 == 0) && (iLen > 12)) {
	          if (iLen >= 14) {
	            if ( (sAmmeterFrame.substring(0, 2).equals("68")) &&
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
	            if ( (iLen >= 24) && (iResult != 20)) {
	              if ( (sAmmeterFrame.substring(0, 2).equals("68")) &&
	                  (sAmmeterFrame.substring(14, 16).equals("68"))) { //部规
	                int iContentLen = Integer.parseInt(
	                    sAmmeterFrame.substring(18, 20), 16); //数据区长度
	                sAmmeterFrame = sAmmeterFrame.substring(0, 20) +
	                    sAmmeterFrame.substring(20,
	                                            iContentLen * 2 + 24); //取完整的电表帧，可以消去多余字节
	                if (sAmmeterFrame.substring(sAmmeterFrame.
	                                            length() - 2,
	                                            sAmmeterFrame.length()).equals("16")) {
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
	                    iResult = 10; //部规
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
	      }
	      catch (Exception e) {
	      }
	    }
	    finally {
	    }
	    return iResult;
	  }

	  private String AmmeterDataSwitch(String sStr, String sSign) {
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
	          }
	          else { //加33H
	            iTemp = (Integer.parseInt(sStr.substring(2 * i,
	                2 * i + 2), 16) + 51) % 256;
	          }
	          sTemp = DataSwitch.IntToHex(Integer.toString(iTemp), "00");
	          sResult = sResult + sTemp;
	        }
	      }
	      catch (Exception e) {
	      }
	    }
	    finally {
	    }
	    return sResult;
	  }

	//组浙江规约系列命令帧
	//TerminalLogicAdd	In	char(20)	终端逻辑地址
	//StationNo	In	int	主站序号
	//CommandSeq	In	int	命令序号
	//ControlCode	In	Char(5)	控制码
	//DataContent	In	Char()	数据区内容
	//FrmeData	Out	Char()	命令帧
	  private char[] IFE_BuildFrameOfZheJiang(char[] TerminalLogicAdd,
	                                          int StationNo, int CommandSeq,
	                                          char[] ControlCode,
	                                          char[] DataContent) {
	    try {
	      //取输入值,校验合法性
	      String sZDLJDZ = new String(TerminalLogicAdd);
	      String sKZM = new String(ControlCode);
	      String sDATA = new String(DataContent);
	      if ( (sZDLJDZ.length() != 8) || (sKZM.length() != 2) ||
	          (sDATA.length() <= 0) || (sDATA.length() % 2 != 0)) {
	        return null;
	      }
	      //计算主站地址及命令序号段,组帧头
	      String sZZDZAndMLXH = BuildZZDZAndMLXH(StationNo, CommandSeq, 0);
	      String sHead = "68" + sZDLJDZ.substring(0, 4) +
	          sZDLJDZ.substring(6, 8) + sZDLJDZ.substring(4, 6) +
	          sZZDZAndMLXH + "68";
	      //计算数据区长度
	      int iDataLen = sDATA.length() / 2;
	      String sDataLen = Integer.toHexString(iDataLen);
	      if (sDataLen.length() > 4) {
	        sDataLen = sDataLen.substring(0, 4);
	      }
	      sDataLen = "0000".substring(0, 4 - sDataLen.length()) + sDataLen;
	      sDataLen = sDataLen.substring(2, 4) + sDataLen.substring(0, 2);

	      String sBuf = sHead + sKZM + sDataLen + sDATA;
	      //计算校并添加校验码
	      int iChecked = 0;
	      for (int i = 0; i < sBuf.length() / 2; i++) { //通过内容计算新的校验码
	        iChecked +=
	            Integer.parseInt(sBuf.substring(2 * i,
	                                            2 * i + 2), 16);
	        //System.out.println(sBuf.substring(2 * i,2 * i + 2)+"_"+iChecked);
	      }
	      int iJYM = iChecked % 256;
	      String sJYM = Integer.toHexString(iJYM);
	      if (sJYM.length() > 2) {
	        sJYM = sJYM.substring(0, 2);
	      }
	      sJYM = "00".substring(0, 2 - sJYM.length()) + sJYM;
	      sBuf = sBuf + sJYM + "16";
	      sBuf = sBuf.toUpperCase();
	      return sBuf.toCharArray();
	    }
	    catch (NumberFormatException ex) {
	      return null;
	    }
	  }
	//组海兴集抄命令帧
	//TerminalLogicAdd	In	char(20)	终端逻辑地址
	//StationNo	In	int	主站序号
	//CommandSeq	In	int	命令序号
	//ControlCode	In	Char(5)	控制码
	//DataContent	In	Char()	数据区内容
	//FrmeData	Out	Char()	命令帧
	  private char[] IFE_BuildFrameOfHeXing(char[] TerminalLogicAdd,
	                                          int StationNo, int CommandSeq,
	                                          char[] ControlCode,
	                                          char[] DataContent) {
	    try {
	      //取输入值,校验合法性
	      String sZDLJDZ = new String(TerminalLogicAdd);
	      String sKZM = new String(ControlCode);
	      String sDATA = new String(DataContent);
	      if ( (sZDLJDZ.length() != 4) || (sKZM.length() != 2) ||
	          (sDATA.length() <= 0) || (sDATA.length() % 2 != 0)) {
	        return null;
	      }
	                    
	      String sLen = "" + ((sDATA.length() / 2) + 12);
	      sLen = DataSwitch.IntToHex(sLen, "00");
	      String sFrame = "0564" + sLen + "D4" + DataSwitch.ReverseStringByByte(sZDLJDZ) + "0000";
	      String CRC1 = DataSwitch.MultiCRC(sFrame, "3D65");
	      String sDataArea = "C000" + sKZM + sDATA;
	      String CRC2 = DataSwitch.MultiCRC(sDataArea, "3D65");
	      sFrame = sFrame + DataSwitch.ReverseStringByByte(CRC1)+ sDataArea + 
	      		 DataSwitch.ReverseStringByByte(CRC2);            
	     
	      return sFrame.toCharArray();
	    }
	    catch (NumberFormatException ex) {
	      return null;
	    }
	  }
	  
	//Build DLMS frame
	//TerminalLogicAdd	In	char(20)	
	//StationNo	In	int	
	//CommandSeq	In	int	
	//ControlCode	In	Char(5)	
	//DataContent	In	Char()	
	//FrmeData	Out	Char()	
	  private char[] IFE_BuildFrameOfDLMS(char[] TerminalLogicAdd,
			  int StationNo, int CommandSeq,
			  char[] ControlCode, char[] DataContent) {
	    try {
	      String sKZM = new String(ControlCode);
	      String sDATA = new String(DataContent);
	      if ( sKZM.length() != 2 || sDATA.length() <= 0 || sDATA.length() % 2 != 0) {
	        return null;
	      }
	                    
	      String sLen = "" + ((sDATA.length() / 2) + 3);
	      sLen = DataSwitch.IntToHex(sLen, "0000");
	      String sFrame = "000100100001" + sLen + sKZM + "0181" + sDATA;     
	      return sFrame.toCharArray();
	    }
	    catch (NumberFormatException ex) {
	      return null;
	    }
	  }
	  //通过主站序号命令序号及帧类序号组主站地址命令序号段
	  private String BuildZZDZAndMLXH(int ZZXH, int MLXH, int ZNXH) {
	    try {
	      int iZZXH = ZZXH & 63;
	      int iZXHH = ( (MLXH & 124) >> 2); //命令序号高位
	      int iZXHL = MLXH & 3; //命令序号低位
	      int iZNXH = ZNXH & 7;
	      int iiZZDZandMLXHL = iZZXH | (iZXHL << 6); //主站地址及命令序号段低位
	      int iiZZDZandMLXHH = iZXHH | (iZNXH << 5); //主站地址及命令序号段高位
	      String sZZDZandMLXH = Integer.toHexString(iiZZDZandMLXHL);
	      if (sZZDZandMLXH.length() > 2) {
	        sZZDZandMLXH = sZZDZandMLXH.substring(0, 2);
	      }
	      sZZDZandMLXH = "00".substring(0, 2 - sZZDZandMLXH.length()) +
	          sZZDZandMLXH;

	      String sTemp = Integer.toHexString(iiZZDZandMLXHH);
	      if (sTemp.length() > 2) {
	        sTemp = sTemp.substring(0, 2);
	      }
	      sTemp = "00".substring(0, 2 - sTemp.length()) + sTemp;

	      sZZDZandMLXH += sTemp;
	      return sZZDZandMLXH;
	    }
	    catch (Exception ex) {
	      return null;
	    }
	  }

	  //全国规约系列组帧,DataContent内容为规约里应用层内容除功能码AFN之外的所有数据,ControlCode传入的值为功能码AFN
	  //另外由于SEQ及AUX里的帧序号在组数据区时不能获取填为0,所以此处要从CommandSeq取值进行更新
	  private char[] IFE_BuildFrameOfQuanGuo(char[] TerminalLogicAdd, int Gyh,
	                                         int StationNo, int CommandSeq,
	                                         char[] ControlCode,
	                                         char[] DataContent) {
	    try {
	      //取输入值,校验合法性
	      String sZDLJDZ = new String(TerminalLogicAdd);
	      String sGNM = new String(ControlCode);
	      String sDATA = new String(DataContent);
	      if ( (sZDLJDZ.length() != 10) || (sGNM.length() != 2) ||
	          (sDATA.length() <= 0) || (sDATA.length() % 2 != 0)) {
	        return null;
	      }

	      //通过功能码获取控制码
	      String sKZM = "";
	      if (sGNM.equals("0A") || sGNM.equals("0C") || sGNM.equals("0D") ||
	          sGNM.equals("0E") || sGNM.equals("10") || sGNM.equals("C1") ||
	          sGNM.equals("0B") || sGNM.equals("03") || sGNM.equals("09")){ //一类、二类、三类数据读取、中继抄表、自定义读取命令
	        sKZM = "4B";
	      }
	      else if (sGNM.equals("04") || sGNM.equals("05")|| sGNM.equals("C0")) { //终端参数设置和控制命令、自定义设置命令
	        sKZM = "4A";
	      }
	      else if (sGNM.equals("01")) { //复位命令
	        sKZM = "41";
	      }

	      //计算并更机关报启动帧序号PSEQ
	      int iPSEQ = CommandSeq & 15;
	      String sPSEQ = Integer.toString(iPSEQ, 16);
	      sDATA = sDATA.substring(0, 1) + sPSEQ +
	          sDATA.substring(2, sDATA.length());

	      //计算并更新帧序号计数器
	      String sPFC = ""; //启动帧帧序号计数器
	      String sSEQ = sDATA.substring(0, 1); //帧序列域高4位(组数据区已组了高4位,还需要组低4位的帧序号)
	      if ( (Integer.parseInt(sSEQ, 16) & 8) == 8) { //带时间标签
	        sPFC = Integer.toString(CommandSeq, 16);
	        if (sPFC.length() > 2) {
	          sPFC = sPFC.substring(sPFC.length() - 2, sPFC.length());
	        }
	        sPFC = "00".substring(0, 2 - sPFC.length()) + sPFC;
	        sDATA = sDATA.substring(0, sDATA.length() - 12) + sPFC +
	            sDATA.subSequence(sDATA.length() - 10, sDATA.length());
	      }

	      /*if (sGNM.equals("C0")) { //自定义命令特殊处理
	           sDataArea = sKZM + sTerminalLogicAdd + sGNM + sSEQ +
	                       sDataArea.substring(0, 8) + "38303030" +
	                       sDataArea.substring(8, sDataArea.length());
	       } else {
	       sDataArea = sKZM + sTerminalLogicAdd + sGNM + sSEQ + sDataArea;
	       }
	       int iLen = sDataArea.length() / 2;
	       iLen = (iLen << 2) | 1;
	       String sLen = DataSwitch.IntToHex(("" + iLen), "0000"); //用户数据长度
	       sLen = DataSwitch.ReverseStringByByte(sLen);
	       sFrame = "68" + sLen + sLen + "68" + sDataArea + "0016";
	       sFrame = GetFrameInfo.gGetParityByteOfQuanGuo(sFrame);
	       FrameInfo.SetCommandSeq(CommandSeq);
	       FrameInfo.SetCommandFrame(sFrame);
	       */
	      //计算数据区长度
	      int iDataLen = sDATA.length() / 2 + 7;
	      if (Gyh == Glu_ConstDefine.GY_ZD_698){
	    	  iDataLen = (iDataLen << 2) | 2;
	      } else {
	    	  iDataLen = (iDataLen << 2) | 1;
	      }      
	      String sDataLen = Integer.toHexString(iDataLen);
	      if (sDataLen.length() > 4) {
	        sDataLen = sDataLen.substring(0, 4);
	      }
	      sDataLen = "0000".substring(0, 4 - sDataLen.length()) + sDataLen;
	      sDataLen = sDataLen.substring(2, 4) + sDataLen.substring(0, 2);

	      String sBuf = sKZM + sZDLJDZ.substring(2, 4) +
	          sZDLJDZ.substring(0, 2) + sZDLJDZ.substring(6, 8) +
	          sZDLJDZ.substring(4, 6) + sZDLJDZ.substring(8, 10) +
	          sGNM + sDATA;

	      //计算校并添加校验码
	      int iChecked = 0;
	      for (int i = 0; i < sBuf.length() / 2; i++) { //通过内容计算新的校验码
	        iChecked +=
	            Integer.parseInt(sBuf.substring(2 * i,
	                                            2 * i + 2), 16);
	        //System.out.println(sBuf.substring(2 * i,2 * i + 2)+"_"+iChecked);
	      }
	      int iJYM = iChecked % 256;
	      String sJYM = Integer.toHexString(iJYM);
	      if (sJYM.length() > 2) {
	        sJYM = sJYM.substring(0, 2);
	      }
	      sJYM = "00".substring(0, 2 - sJYM.length()) + sJYM;
	      sBuf = "68" + sDataLen + sDataLen + "68" + sBuf + sJYM + "16";
	      sBuf = sBuf.toUpperCase();
	      return sBuf.toCharArray();

	    }
	    catch (Exception ex) {
	      return null;
	    }

	  }
	//全国规约系列组帧,DataContent内容为规约里应用层内容除功能码AFN之外的所有数据,ControlCode传入的值为功能码AFN
	  //另外由于SEQ及AUX里的帧序号在组数据区时不能获取填为0,所以此处要从CommandSeq取值进行更新
	  private char[] IFE_BuildFrameOfIHD(char[] TerminalLogicAdd,
	                                         int StationNo, int CommandSeq,
	                                         char[] ControlCode,
	                                         char[] DataContent) {
	    try {
	      //取输入值,校验合法性
	      String sZDLJDZ = new String(TerminalLogicAdd);
	      String sGNM = new String(ControlCode);
	      String sDATA = new String(DataContent);
	      if ( (sZDLJDZ.length() != 10) || (sGNM.length() != 2) ||
	          (sDATA.length() <= 0) || (sDATA.length() % 2 != 0)) {
	        return null;
	      }

	      //通过功能码获取控制码
	      String sKZM = "";
	      if (sGNM.equals("0A") || sGNM.equals("0C")){ //一类
	        sKZM = "4B";
	      }
	      else if (sGNM.equals("04") || sGNM.equals("05")) { //终端参数设置和控制命令
	        sKZM = "4A";
	      }

	      //计算并更机关报启动帧序号PSEQ
	      int iPSEQ = CommandSeq & 15;
	      String sPSEQ = Integer.toString(iPSEQ, 16);
	      sDATA = sDATA.substring(0, 1) + sPSEQ +
	          sDATA.substring(2, sDATA.length());

	      //计算并更新帧序号计数器
	      String sPFC = ""; //启动帧帧序号计数器
	      String sSEQ = sDATA.substring(0, 1); //帧序列域高4位(组数据区已组了高4位,还需要组低4位的帧序号)
	      if ( (Integer.parseInt(sSEQ, 16) & 8) == 8) { //带时间标签
	        sPFC = Integer.toString(CommandSeq, 16);
	        if (sPFC.length() > 2) {
	          sPFC = sPFC.substring(sPFC.length() - 2, sPFC.length());
	        }
	        sPFC = "00".substring(0, 2 - sPFC.length()) + sPFC;
	        sDATA = sDATA.substring(0, sDATA.length() - 12) + sPFC +
	            sDATA.subSequence(sDATA.length() - 10, sDATA.length());
	      }

	      //计算数据区长度
	      int iDataLen = sDATA.length() / 2 + 7;
	      iDataLen = (iDataLen << 2) | 2;           
	      String sDataLen = Integer.toHexString(iDataLen);
	      if (sDataLen.length() > 4) {
	        sDataLen = sDataLen.substring(0, 4);
	      }
	      sDataLen = "0000".substring(0, 4 - sDataLen.length()) + sDataLen;
	      sDataLen = sDataLen.substring(2, 4) + sDataLen.substring(0, 2);

	      String sBuf = sKZM + sZDLJDZ.substring(2, 4) +
	          sZDLJDZ.substring(0, 2) + sZDLJDZ.substring(6, 8) +
	          sZDLJDZ.substring(4, 6) + sZDLJDZ.substring(8, 10) +
	          sGNM + sDATA;

	      //计算校并添加校验码
	      int iChecked = 0;
	      for (int i = 0; i < sBuf.length() / 2; i++) { //通过内容计算新的校验码
	        iChecked +=
	            Integer.parseInt(sBuf.substring(2 * i,
	                                            2 * i + 2), 16);
	        //System.out.println(sBuf.substring(2 * i,2 * i + 2)+"_"+iChecked);
	      }
	      int iJYM = iChecked % 256;
	      String sJYM = Integer.toHexString(iJYM);
	      if (sJYM.length() > 2) {
	        sJYM = sJYM.substring(0, 2);
	      }
	      sJYM = "00".substring(0, 2 - sJYM.length()) + sJYM;
	      sBuf = "68" + sDataLen + "68" + sBuf + sJYM + "16";
	      sBuf = sBuf.toUpperCase();
	      return sBuf.toCharArray();

	    }
	    catch (Exception ex) {
	      return null;
	    }

	  }
	//规约帧解帧接口函数
	  public Struct_FrameInfo IFE_FrameExplain(int TermialProtocolNo,
	                                           char[] FrmeData) {
	    String sSourceData = new String(FrmeData);
	    int FrameType = FrameCheck(sSourceData); //校验帧格式是否合法
	    switch (FrameType) {
	      //浙江规范系列
	      case Glu_ConstDefine.GY_ZD_ZHEJIANG:{
	        switch (TermialProtocolNo) {
	          case Glu_ConstDefine.GY_ZD_ZHEJIANG:{ //标准浙规
	            return FrameExplainOfZheJiang(sSourceData, FrameType); //浙江规范系统解帧

	          }
	          default: { //未指定规约号则用标准浙规解释
	            return FrameExplainOfZheJiang(sSourceData, FrameType); //浙江规范系统解帧
	          }
	        }
	      }

	      //国网系列
	      case Glu_ConstDefine.GY_ZD_QUANGUO: {
	        switch (TermialProtocolNo) {
	          case Glu_ConstDefine.GY_ZD_QUANGUO:
	        //  case Glu_ConstDefine.GY_ZD_GUYUAN:
	        //  case Glu_ConstDefine.GY_ZD_TIANJIN:
	          case Glu_ConstDefine.GY_ZD_698: { //标准国网规约
	            return FrameExplainOfQuanGuo(sSourceData, FrameType);
	          }
	          default: { //未指定规约号则用标准国网规约解释
	            return FrameExplainOfQuanGuo(sSourceData, FrameType);
	          }
	        }
	      }
	      //杭州水表规约
	      case Glu_ConstDefine.GY_ZD_HS: {//杭州水表规约
	        return FrameExplainOfHS(sSourceData, FrameType);        
	      }
	      //海兴集抄规约
	      case Glu_ConstDefine.GY_ZD_HEXING: {
	        return FrameExplainOfHeXing(sSourceData, FrameType);        
	      }
	      //DLMS规约
	      case Glu_ConstDefine.GY_ZD_DLMS: {
	        return FrameExplainOfDLMS(sSourceData, FrameType);        
	      }
	      //IHD规约
	      case Glu_ConstDefine.GY_ZD_IHD: {
	        return FrameExplainOfIHD(sSourceData, FrameType);        
	      }
	      default:
	        return null;
	    } //switch (FrameType)
	    //return null;
	  }

	//判断规约型
	  private int FrameCheck(String FrameData) {
	    if (FrameCheckOfZheJiang(FrameData)) {
	    	return Glu_ConstDefine.GY_ZD_ZHEJIANG; //浙江规范系统帧
	    }
	    else if (FrameCheckOfQuanGuo(FrameData)) {
	    	return Glu_ConstDefine.GY_ZD_QUANGUO;
	    }
	    else if (FrameCheckOfHS(FrameData)) {
	        return Glu_ConstDefine.GY_ZD_HS;
	    }
	    else if (FrameCheckOfHeXing(FrameData)) {
	        return Glu_ConstDefine.GY_ZD_HEXING;
	    }
	    else if (FrameCheckOfDLMS(FrameData)) {
	        return Glu_ConstDefine.GY_ZD_DLMS;
	    }
	    else if (FrameCheckOfIHD(FrameData)) {
	        return Glu_ConstDefine.GY_ZD_IHD;
	    }
	    return Glu_ConstDefine.GY_UnDefine; //未定义帧
	  }

	//浙江规范系列帧判断
	  private boolean FrameCheckOfHS(String FrameData) {
	    try {
	      int iFrameLen = FrameData.length();
	      if (iFrameLen >= 20) { //帧长度大于最小帧长度
	        //System.out.println(FrameData);
	        if ( (FrameData.substring(0, 2).equals("9B")) ||
	            (FrameData.substring(0, 2).equals("B9"))) { //大至判断帧格式
	          int iLen = Integer.parseInt(FrameData.substring(12, 14), 16);
	          iLen = iLen * 2 + 20;
	          if (FrameData.substring(iLen - 2, iLen).equals("9D")) {
	            String sOldJYM = FrameData.substring(iLen - 4, iLen - 2) +
	            				 FrameData.substring(iLen - 6, iLen - 4);
	            String cs = DataSwitch.CRC16(FrameData.substring(0,iLen-6));  
	           
	            if (sOldJYM.equals(cs)) {
	              return true;
	            }
	          }
	        }
	      }
	    }
	    catch (Exception e) {
	      return false;
	    }
	    return false;
	  }
	  
	  
	//浙江规范系列帧判断
	  private boolean FrameCheckOfZheJiang(String FrameData) {
	    try {
	      int iFrameLen = FrameData.length();
	      if (iFrameLen >= 20) { //帧长度大于最小帧长度
	        //System.out.println(FrameData);
	        if ( (FrameData.substring(0, 2).equals("68")) &&
	            (FrameData.substring(14, 16).equals("68"))) { //大至判断帧格式
	          int iLen = Integer.parseInt(FrameData.substring(20, 22) +
	                                      FrameData.substring(18, 20), 16);
	          iLen = iLen * 2 + 26;
	          if (FrameData.substring(iLen - 2, iLen).equals("16")) {
	            String sOldJYM = FrameData.substring(iLen - 4, iLen - 2);
	            int iChecked = 0;
	            for (int i = 0; i < iLen / 2 - 2; i++) { //通过内容计算新的校验码
	              iChecked +=
	                  Integer.parseInt(FrameData.substring(2 * i,
	                  2 * i + 2), 16);
	              //System.out.println(FrameData.substring(2 * i,2 * i + 2));
	            }
	            int iJYM = iChecked % 256;
	            if (iJYM == Integer.parseInt(sOldJYM, 16)) {
	              return true;
	            }
	          }
	        }
	      }
	    }
	    catch (Exception e) {
	      return false;
	    }
	    return false;
	  }
	  
	  //海兴集抄帧判断
	  private boolean FrameCheckOfHeXing(String FrameData) {
	    try {
	    	FrameData = FrameData.trim();//消空格
	        int iLenFrame = FrameData.length();
	        if ((iLenFrame>=34)&&(iLenFrame%2==0)){//最小帧长度
	        	if (FrameData.substring(0,4).equals("0564")){//检查起始字0x0564
	        		String sLenData = FrameData.substring(4,6);  
					int iLenData=Integer.parseInt(sLenData,16);//数据区字节长度	
					if (iLenData*2 == (FrameData.length()-6)){//检查数据区长度是否一致		
						String sData = FrameData.substring(0,16);//报头数据区
						String CRC1 = DataSwitch.MultiCRC(sData,"3D65");
						CRC1 = DataSwitch.ReverseStringByByte(CRC1);
						if (CRC1.equals(FrameData.substring(16, 20))){//检查CRC校验码1
							sData=FrameData.substring(20,iLenData * 2 + 2);//主体数据区
							String CRC2 = DataSwitch.MultiCRC(sData,"3D65");
							CRC2 = DataSwitch.ReverseStringByByte(CRC2);
							if (CRC2.equals(FrameData.substring(FrameData.length() - 4, FrameData.length()))){//检查CRC校验码2
								return true;
							}
						}						
					}
	        	}
	        }     
	        return false;
	    }
	    catch (Exception e) {
	      return false;
	    }
	  }
	  //DLMS帧判断
	  private boolean FrameCheckOfDLMS(String FrameData) {
	    try {
	    	boolean result=false;
	    	FrameData = FrameData.trim();//消空格
	        int iLenFrame = FrameData.length();
	        if ((iLenFrame>=16)&&(iLenFrame%2==0)){//最小帧长度
	        	if (FrameData.substring(0,12).equals("000100010010")){//检查起始字000100010010
	        		String sLenData = FrameData.substring(12,16);  
					int iLenData=Integer.parseInt(sLenData,16);//数据区字节长度	
					if (iLenData*2 == (FrameData.length()-16)){//检查数据区长度是否一致		
						result = true;		
					}
	        	}
	        }     
	        return result;
	    }
	    catch (Exception e) {
	      return false;
	    }
	  }
	//国网规范系列帧判断
	  private boolean FrameCheckOfQuanGuo(String FrameData) {
	    try {
	      int iFrameLen = FrameData.length();
	      if (iFrameLen >= 40) { //帧长度大于最小帧长度
	        if ( (FrameData.substring(0, 2).equals("68")) &&
	            (FrameData.substring(10, 12).equals("68"))) { //大至判断帧格式
	          if (FrameData.substring(2, 6).equals(FrameData.substring(6, 10))) {
	            //if ( (Integer.parseInt(FrameData.substring(2, 4), 16) & 1) == 1) {
	              int iLen = Integer.parseInt(FrameData.substring(4, 6) +
	                                          FrameData.substring(2, 4), 16);
	              iLen = iLen >> 2;
	              if (iLen * 2 == iFrameLen - 16) {
	                if (FrameData.substring(iFrameLen - 2, iFrameLen).equals("16")) {
	                  String sOldJYM = FrameData.substring(iFrameLen - 4,
	                      iFrameLen - 2);

	                  //String ByCheckCode = FrameData.substring(12, iFrameLen - 4);
	                  int iChecked = 0;
	                  for (int i = 6; i < iFrameLen / 2 - 2; i++) { //通过内容计算新的校验码
	                    iChecked +=
	                        Integer.parseInt(FrameData.substring(2 * i, 2 * i + 2),
	                                         16);
	                  }
	                  int iJYM = iChecked % 256;
	                  if (iJYM == Integer.parseInt(sOldJYM, 16)) {
	                    return true;
	                  }
	                }
	              }
	            //}
	          }
	        }
	      }
	      return false;
	    }
	    catch (NumberFormatException ex) {
	      return false;
	    }
	  }
	  //IHD规范系列帧判断
	  private boolean FrameCheckOfIHD(String FrameData) {
		  try {
			  int iFrameLen = FrameData.length();
		      if (iFrameLen >= 36) { //帧长度大于最小帧长度
		    	  if ( (FrameData.substring(0, 2).equals("68")) && (FrameData.substring(6, 8).equals("68"))) { //大至判断帧格式
		    		  int iLen = Integer.parseInt(FrameData.substring(4, 6) +
		                                          FrameData.substring(2, 4), 16);
		    		  iLen = iLen >> 2;
		              if (iLen * 2 == iFrameLen - 12) {
		            	  if (FrameData.substring(iFrameLen - 2, iFrameLen).equals("16")) {
		            		  String sOldJYM = FrameData.substring(iFrameLen - 4, iFrameLen - 2);
		            		  int iChecked = 0;
		            		  for (int i = 4; i < iFrameLen / 2 - 2; i++) { //通过内容计算新的校验码
		            			  iChecked +=
		            				  Integer.parseInt(FrameData.substring(2 * i, 2 * i + 2), 16);
		            		  }
		            		  int iJYM = iChecked % 256;
		            		  if (iJYM == Integer.parseInt(sOldJYM, 16)) {
		            			  return true;
		            		  }
		            	  }
		              }	          
		    	  }
		      }
		      return false;
		  }
		  catch (NumberFormatException ex) {
			  return false;
		  }
	  }

	//浙江规范规约帧解析
	  private Struct_FrameInfo FrameExplainOfZheJiang(String sSourceData,
	                                                  int iGYLX) {
	    try {
	      String sZDLJDZ = sSourceData.substring(2, 10);
	      sZDLJDZ = sZDLJDZ.substring(0, 4) + sZDLJDZ.substring(6, 8) +
	          sZDLJDZ.substring(4, 6);
	      String sZZDZandMLXH = sSourceData.substring(10, 14);
	      String sKZM = sSourceData.substring(16, 18);
	      //取主站编号
	      int iTemp = Integer.parseInt(sZZDZandMLXH.substring(0, 2), 16); //取主站地址和命令序号段的低字并转为数值
	      int iZZBH = iTemp & 63; //取低字节的后六位
	      //取帧序号(命令序号)
	      int iZXH = iTemp & 192; //取低字节的高六位
	      iZXH = iZXH >> 6; //右移六位
	      iTemp = Integer.parseInt(sZZDZandMLXH.substring(2, 4), 16); //取主站地址和命令序号段的高字并转为数值
	      int iZXHHeight = iTemp & 31;
	      iZXHHeight = iZXHHeight << 2;
	      iZXH = iZXH | iZXHHeight;
	      //取帧内序号
	      int iZNXH = iTemp & 224;
	      iZNXH = iZNXH >> 5;
	      //解析控制码
	      iTemp = Integer.parseInt(sKZM, 16);
	      int iCSFX = iTemp & 128; //传送方向
	      int iYCBZ = iTemp & 64; //异常标志
	      int iGNM = iTemp & 63; //功能码
	      String sGNM = Integer.toHexString(iGNM);
	      //解析数据类型
	      int iFrameType = Glu_ConstDefine.SJLX_WDY; //未定义类型
	      if (iCSFX == 0) {
	        if (iGNM == 36) { //心跳帧，控制码为24或者A4都认为是心跳帧
	          iFrameType = Glu_ConstDefine.SJLX_ZXXT; //终端在线心跳帧
	        }
	        else {
	          iFrameType = Glu_ConstDefine.SJLX_XXBW;
	        }
	      }
	      else {
	        if (iYCBZ == 0) {
	          switch (iGNM) {
	            case 0:
	              ; //中继
	            case 1:
	              ; //当前数据
	            case 4:
	              ; //编程日志
	            case 15: { //用户自定义数据
	              iFrameType = Glu_ConstDefine.SJLX_PTSJ;
	              break;
	            }
	            case 2: { //任务数据
	              if (iZZBH == 0) {
	                iFrameType = Glu_ConstDefine.SJLX_LSSJZD;
	              }
	              else {
	                iFrameType = Glu_ConstDefine.SJLX_LSSJZC;
	              }
	              break;
	            }
	            case 7:
	              ; //设置返回
	            case 8: {
	              iFrameType = Glu_ConstDefine.SJLX_SZFH;
	              break;
	            }
	            case 9: { //异常数据
	              if (iZZBH == 0) {
	                iFrameType = Glu_ConstDefine.SJLX_YCSJZD;
	              }
	              else {
	                iFrameType = Glu_ConstDefine.SJLX_YCSJZC;
	              }
	              break;
	            }
	            case 33: {
	              iFrameType = Glu_ConstDefine.SJLX_ZDDL; //终端登录
	              break;
	            }
	            case 34: {
	              iFrameType = Glu_ConstDefine.SJLX_ZDTC; //终端登录退出
	              break;
	            }
	            case 36: {
	              iFrameType = Glu_ConstDefine.SJLX_ZXXT; //终端在线心跳帧
	              break;
	            }
	          } //switch
	        }
	      }
	      //取数据区内容
	      int iLen = Integer.parseInt(sSourceData.substring(20, 22) +
	                                  sSourceData.substring(18, 20), 16);
	      iLen *= 2;
	      String sData = sSourceData.substring(22, 22 + iLen);
	      //赋返回值
	      Struct_FrameInfo FrameInfo = new Struct_FrameInfo();
	      FrameInfo.TerminalLogicAdd = sZDLJDZ.toCharArray();
	      FrameInfo.StationNo = iZZBH;
	      FrameInfo.FrameSeq = iZNXH;
	      FrameInfo.CommandSeq = iZXH;
	      FrameInfo.ControlCode = sKZM.toCharArray();
	      FrameInfo.FunctionCode = sGNM.toCharArray();
	      FrameInfo.DataType = iFrameType;
	      FrameInfo.SetDataContent(sData);
	      FrameInfo.TermialProtocolNo = iGYLX;
	      return FrameInfo;
	    }
	    catch (NumberFormatException ex) {
	      return null;
	    }
	  }
	//海兴集抄规约帧解析
	  private Struct_FrameInfo FrameExplainOfHeXing(String sSourceData,
	                                                  int iGYLX) {
	    try {
	      String sZDLJDZ = sSourceData.substring(12, 16);
	      sZDLJDZ = sZDLJDZ.substring(2, 4) + sZDLJDZ.substring(0, 2);
	      String sGNM = sSourceData.substring(24, 26);//应用层功能码
	      String sKZM = sSourceData.substring(6, 8);
	      String sCommandID = sSourceData.substring(26, 30);
	      //取主站编号
	      int iTemp = Integer.parseInt(sSourceData.substring(20, 24), 16); //取传输报文头
	      //取帧内序号
	      int iZNXH = iTemp & 16383; //取低字节的后14位      
	      int iZXH = 0; //帧序号默认为0
	      
	      //解析控制码
	      iTemp = Integer.parseInt(sKZM, 16);
	      //解析数据类型
	      int iFrameType = Glu_ConstDefine.SJLX_WDY; //未定义类型
	      
	      if (sGNM.equals("82")){//主动上送
	    	  if (sCommandID.equals("1050")){
	        	  iFrameType = Glu_ConstDefine.SJLX_ZXXT; //终端心跳帧
	          } else if (sCommandID.equals("2061") || sCommandID.equals("2062")){
	        	  iFrameType = Glu_ConstDefine.SJLX_YCSJZD; //异常数据主动上送
	          } else {
	        	  iFrameType = Glu_ConstDefine.SJLX_LSSJZD; //非异常主动上送
	          }
	      } else if (sGNM.equals("81")){//召测返回
	    	  if (sCommandID.equals("2061") || sCommandID.equals("2062")){
	        	  iFrameType = Glu_ConstDefine.SJLX_YCSJZC; //异常数据召测
	          } else if (sCommandID.equals("1015") || //重点用户数据
	        		     sCommandID.equals("3001") || sCommandID.equals("3002") ||
	        		     sCommandID.equals("3003") || sCommandID.equals("3004") ||
	        		     sCommandID.equals("3005") || //公变总表
	        		     sCommandID.equals("2004") || sCommandID.equals("2044") ||
	        		     sCommandID.equals("2005") || sCommandID.equals("2045") ||
	        		     sCommandID.equals("2018") || sCommandID.equals("2048") ||
	        		     sCommandID.equals("2009") || sCommandID.equals("2020") ||
	        		     sCommandID.equals("204B") || sCommandID.equals("2021") ||
	        		     sCommandID.equals("204C") || sCommandID.equals("2019") ||
	        		     sCommandID.equals("204A") || sCommandID.equals("200A") ||
	        		     sCommandID.equals("200B") || sCommandID.equals("202A") ||
	        		     sCommandID.equals("202B") ){//单相表及三相表数据
	        	  iFrameType = Glu_ConstDefine.SJLX_LSSJZC; //任务数据召测
	          } else {
	        	  iFrameType = Glu_ConstDefine.SJLX_PTSJ;
	          }
	    	  
	      }
	      //取数据区内容
	      String sData = sSourceData.substring(26, sSourceData.length() - 4);
	      //赋返回值
	      Struct_FrameInfo FrameInfo = new Struct_FrameInfo();
	      FrameInfo.TerminalLogicAdd = sZDLJDZ.toCharArray();
	      FrameInfo.StationNo = 0;
	      FrameInfo.FrameSeq = iZNXH;
	      FrameInfo.CommandSeq = iZXH;
	      FrameInfo.ControlCode = sKZM.toCharArray();
	      FrameInfo.FunctionCode = sGNM.toCharArray();
	      FrameInfo.DataType = iFrameType;
	      FrameInfo.SetDataContent(sData);
	      FrameInfo.TermialProtocolNo = iGYLX;
	      return FrameInfo;
	    }
	    catch (NumberFormatException ex) {
	      return null;
	    }
	  }
	  //DLMS规约帧解析
	  private Struct_FrameInfo FrameExplainOfDLMS(String sSourceData,
	                                                  int iGYLX) {
	    try {
	      //解析数据类型
	      int iFrameType = Glu_ConstDefine.SJLX_WDY; //未定义类型
	      String sCommandID = sSourceData.substring(16,18);
	      if (sCommandID.equals("DD")){
	    	  iFrameType = Glu_ConstDefine.SJLX_ZXXT; //终端心跳帧
	      }else if (sCommandID.equals("61")){//AARE
	    	  iFrameType = Glu_ConstDefine.SJLX_DLMSAARE; //DLMS规约AARE
	      }else if (sCommandID.equals("C2")){//Alarm
	    	  iFrameType = Glu_ConstDefine.SJLX_YCSJZD; //异常数据主动上送          
	      }else if (sCommandID.equals("C4")){//Get
	    	  if (sSourceData.substring(18, 20).equals("02")){
	    		  iFrameType = Glu_ConstDefine.SJLX_PTSJHJ;
	    	  }else {
	    		  iFrameType = Glu_ConstDefine.SJLX_PTSJ; //普通数据 
	    	  }
	      }else if (sCommandID.equals("C5") || sCommandID.equals("C7")){//Set/Action
	    	  iFrameType = Glu_ConstDefine.SJLX_SZFH; //设置返回数据 
	      }
	      //取数据区内容
	      String sData = sSourceData.substring(16, sSourceData.length());
	      //赋返回值
	      Struct_FrameInfo FrameInfo = new Struct_FrameInfo();
	      FrameInfo.TerminalLogicAdd = "".toCharArray();
	      FrameInfo.StationNo = 0;
	      FrameInfo.FrameSeq = 0;
	      FrameInfo.CommandSeq = 0;
	      FrameInfo.ControlCode = "".toCharArray();
	      FrameInfo.FunctionCode = "".toCharArray();
	      FrameInfo.DataType = iFrameType;
	      FrameInfo.SetDataContent(sData);
	      FrameInfo.TermialProtocolNo = iGYLX;
	      return FrameInfo;
	    }
	    catch (NumberFormatException ex) {
	      return null;
	    }
	  }
	  private ArrayList ExplainCommandList(String sDT, String sGNM) { //解释国网命令列表
	    ArrayList <String>sCommandList = new ArrayList<String>();
	    try {
	      try {
	        String sDT1 = sDT.substring(0, 2); //信息类元
	        int iDT2 = Integer.parseInt(sDT.substring(2, 4), 16); //信息类组
	        char[] cDT1 = (DataSwitch.Fun2HexTo8Bin(sDT1)).toCharArray();
	        int iFn = 0;
	        if (sDT1.equals("00") && iDT2 == 0) { //无效数据
	          sCommandList.add("000000");
	        }
	        else {
	          for (int i = 7; i >= 0; i--) {
	            if (cDT1[i] == '1') {
	              iFn = iDT2 * 8 + 8 - i; //Fn
	              sDT = sGNM.substring(1, 2) +
	                  DataSwitch.StrStuff("0", 3, Integer.toString(iFn), 10) + "00"; //得到命令
	              sCommandList.add(sDT);
	            }
	          }
	        }
	      }
	      catch (Exception e) {
	      }
	    }
	    finally {      
	    }
	    return sCommandList;
	  }

	  private int GetQuanGuoDataType(String DT, String FunctionCode) { //得到数据类型:10普通数据;11中继数据(普通数据结构);21小时冻结数据;22曲线数据;23日冻结数据;24月冻结数据;25天津模块表主动上送数据;30告警数据;40设置返回数据
	    int iResult = 0;
	    try {
	      try {
	        if (FunctionCode.equals("00")) { //确认/否认
	          iResult = 40;
	        }
	        else if (FunctionCode.equals("0A")) { //读取终端、测量点参数
	          iResult = 10;
	        }
	        else if (FunctionCode.equals("0E")) { //三类数据(告警数据)
	          iResult = 30;
	        }
	        else if (FunctionCode.equals("10")) { //中继数据
	          iResult = 11;
	        }
	        else if (FunctionCode.equals("11")) { //天津模块表主动上送数据
	          iResult = 25;
	        }
	        else if (FunctionCode.equals("0C") ||
	                 FunctionCode.equals("0D") ||
	                 FunctionCode.equals("0B") ||
	                 FunctionCode.equals("C0")) { //一类数据(当前数据、小时冻结数据),二类数据(历史数据),自定义的一、二类数据
	          int iFn = 0;
	          ArrayList CommandList = new ArrayList();
	          CommandList = ExplainCommandList(DT, FunctionCode);
	          iFn = Integer.parseInt(CommandList.get(0).toString().
	                                 substring(1, 4)); //用第一个命令的Fn来判断全部命令的数据类型
	          if (FunctionCode.equals("0C")) { //一类数据
	            if (iFn >= 81 && iFn <= 121) { //小时冻结数据
	              iResult = 21;
	            }
	            else { //普通数据(当前数据)
	              iResult = 10;
	            }
	          }
	          else if (FunctionCode.equals("0D")) { //二类数据的曲线数据、
	            if ( (iFn >= 73 && iFn <= 108) || iFn == 138) {
	              iResult = 22; //曲线数据
	            }
	            else if ( ( (iFn >= 1 && iFn <= 12) ||
	                       (iFn >= 25 && iFn <= 31) ||
	                       (iFn >= 41) && (iFn <= 43) ||
	                       (iFn >= 49 && iFn <= 50) ||
	                       (iFn >= 57 && iFn <= 59) ||
	                       (iFn >= 113 && iFn <= 130))) {
	              iResult = 23; //日冻结数据
	            }
	            else {
	              iResult = 24; //月冻结数据
	            }
	          }
	          else if (FunctionCode.equals("C0")) { //自定义数据
	            if (iFn == 105 || iFn == 106) { //日冻结数据
	              iResult = 23;
	            }
	            else { //普通数据
	              iResult = 10;
	            }
	          }
	        }
	      }
	      catch (Exception e) {
	      }
	    }
	    finally {
	    }
	    return iResult;
	  }
	//杭水规约帧解析
	  private Struct_FrameInfo FrameExplainOfHS(String sSourceData,
	                                                 int iGYLX) {
	    try {
	    	String sZDLJDZ = sSourceData.substring(2, 10); //取终端逻辑地址
	    	String sKZM = sSourceData.substring(10, 12); //控制码
	    	//赋返回值
	        Struct_FrameInfo FrameInfo = new Struct_FrameInfo();
	        FrameInfo.TerminalLogicAdd = sZDLJDZ.toCharArray();
	        FrameInfo.StationNo = 0;
	        FrameInfo.FrameSeq = 0;
	        FrameInfo.CommandSeq = 0;
	        FrameInfo.ControlCode = sKZM.toCharArray();
	        FrameInfo.FunctionCode = sKZM.toCharArray();
	        FrameInfo.DataType = Glu_ConstDefine.SJLX_PTSJ;
	        FrameInfo.SetDataContent(sSourceData);
	        FrameInfo.TermialProtocolNo = iGYLX;
	        return FrameInfo;
	      }
	      catch (Exception ex) {
	        return null;
	      }
	  }
	//国网规范规约帧解析
	  private Struct_FrameInfo FrameExplainOfQuanGuo(String sSourceData,
	                                                 int iGYLX) {
	    try {
	      String sZDLJDZ = sSourceData.substring(14, 24); //取终端逻辑地址
	      sZDLJDZ = sZDLJDZ.substring(2, 4) + sZDLJDZ.substring(0, 2) +
	          sZDLJDZ.substring(6, 8) + sZDLJDZ.substring(4, 6) +
	          sZDLJDZ.substring(8, 10);
	      int iZZBH = (Integer.parseInt(sSourceData.substring(22, 24), 16) >>
	                   1); ////取主站编号
	      String sKZM = sSourceData.substring(12, 14); //控制码

	      //取命令序号
	      String stemp = sSourceData.substring(26, 28); //取序列域
	      int iTpV = Integer.parseInt(stemp, 16) & 128; //时间标签有效位
	      int iZXH = Integer.parseInt(stemp, 16) & 15;
	      int iZNXH = 0;

	      //取功能码AFN
	      String sGNM = sSourceData.substring(24, 26);
	      int iTemp = Integer.parseInt(sKZM, 16);
	      int iCSFX = iTemp & 128; //传送方向DIR
	      int iQDBZ = iTemp & 64; //启动标志位PRM

	      //取数据项标识,用来判断登陆/心跳/退出信息
	      String sLLXX = sSourceData.substring(32, 34);
	      //天津模块表,用来判断电能表报警事件,和任务区分开
	      String sAlarm = "";
	      if (sSourceData.length() > 44) {
	        sAlarm = sSourceData.substring(40, 44); //电能表报警事件主动上传数据单元标识1规定为“0xC02F”，
	      }

	      //取数据区内容
	      int iLen = Integer.parseInt(sSourceData.substring(4,
	          6) + sSourceData.substring(2, 4), 16);
	      iLen = iLen >> 2;
	      iLen *= 2;
	      String sData = "";

	      if (iTpV == 128) { //附加信息域中带有时间标签Tp
	        sData = sData + sSourceData.substring(28, 12 + iLen - 12); //数据区只取链路数据
	      }
	      else {
	        sData = sData + sSourceData.substring(28, 12 + iLen); //数据区只取链路数据
	      }
	      if ( (Integer.parseInt(sKZM.substring(0, 1), 16) & 2) == 2) { //消去2个字节的事件计数器
	        sData = sData.substring(0, sData.length() - 4);
	      }

	      //对于国网而言，只能召测两类数据，普通数据召测（一类，二类）和事件召测（三类）,没有任务补召
	      //所以启动标志为0的有两类数据：任务自动上送或是事件自动上送；启动标志为1的也只有两类：普通数据召测或是事件数据召测
	      //事件召测（三类数据）的功能码只为一个‘0E’所以利用这两个参数可以判断三种类型的数据）
	      int iFrameType = Glu_ConstDefine.SJLX_WDY; //未定义类型
	      if (iCSFX == 0) {
	        iFrameType = Glu_ConstDefine.SJLX_XXBW;
	      }
	      else {
	        if (iQDBZ == 0) {
	          if (sGNM.equals("0E")) { //肯定是异常数据召测
	            iFrameType = Glu_ConstDefine.SJLX_YCSJZC;
	          }
	          else {
	            //对于曲线数据需要判断到数据项来决定是否需要修改数据类型为任务数据
	            if (sData.length() > 10) {
	              if (//SearchAmmeterFrame(sData) == Glu_ConstDefine.GY_DB_TJ ||
	                  SearchAmmeterFrame(sData) == Glu_ConstDefine.GY_DB_QG97) { //要先检查数据区中是否为电表帧的返回，然后再判断数据项
	                String AmmeterFrame = DataSwitch.SearchStr("68",
	                    sData); //搜索以68开头的电表帧,电表帧前有可能有间隔符FE
	                int iDataAreaLen = Integer.parseInt(
	                    AmmeterFrame.substring(18, 20), 16) * 2; //数据区长度
	                if (iDataAreaLen != 0) {
	                  String sDataArea = AmmeterFrame.substring(20,
	                      20 + iDataAreaLen);
	                  sDataArea = AmmeterDataSwitch(sDataArea, "-"); //数据区减33H
	                  if (sDataArea.length() > 4) {
	                    String sDataCaption = sDataArea.substring(2, 4) +
	                        sDataArea.substring(0, 2); //得到电表数据项标识
	                    int iCommand = Integer.parseInt(sDataCaption, 16); //需要解释的命令标识
	                    //$6010..$602F,$6110..$6B6F,$9210..$9B6F  //前N次电量数据
	                    //$7010..$702F,$7110..$7B6F,$A210..$AB6F //前N次最大需量数据
	                    //$A010..$A02F,$A110..$A16F //当前最大需量数据
	                    //$8010..$802F,$8110..$8D6F,$B010..$B02F,$B110..$B16F,$B410..$B56F,$B810..$BD6F: //最大需量发生时间数据
	                    if ( (iCommand >= Integer.parseInt("6010", 16) &&
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
	                         iCommand <= Integer.parseInt("AB6F", 16)) ||
	                        (iCommand >= Integer.parseInt("8010", 16) &&
	                         iCommand <= Integer.parseInt("802F", 16)) ||
	                        (iCommand >= Integer.parseInt("8110", 16) &&
	                         iCommand <= Integer.parseInt("8D6F", 16)) ||
	                        (iCommand >= Integer.parseInt("B410", 16) &&
	                         iCommand <= Integer.parseInt("B56F", 16)) ||
	                        (iCommand >= Integer.parseInt("B810", 16) &&
	                         iCommand <= Integer.parseInt("BD6F", 16)) ||
	                        (sDataCaption.substring(0, 1).equals("D"))) { //曲线数据
	                      iFrameType = Glu_ConstDefine.SJLX_LSSJZC;
	                      iGYLX = 105;
	                    }
	                    else {
	                      iFrameType = Glu_ConstDefine.SJLX_PTSJ; //普通数据
	                    }
	                  }
	                  else {
	                    iFrameType = Glu_ConstDefine.SJLX_PTSJ; //普通数据
	                  }
	                }
	                else {
	                  iFrameType = Glu_ConstDefine.SJLX_PTSJ;
	                }
	              }
	              else {
	                iFrameType = Glu_ConstDefine.SJLX_PTSJ; //普通数据
	              }
	            }
	            else {
	              iFrameType = Glu_ConstDefine.SJLX_PTSJ; //普通数据
	            }
	          }
	        }
	        else {
	          if ( (sGNM.equals("0E")) ||
	              ( (sGNM.equals("11")) && (sAlarm.equals("2FC0")))) { //肯定是异常数据自动上送//天津模块表电能表告警
	            iFrameType = Glu_ConstDefine.SJLX_YCSJZD;
	          }
	          else if ( (sGNM.equals("02")) && (sLLXX.equals("01"))) { //登陆
	            iFrameType = Glu_ConstDefine.SJLX_ZDDL;
	          }
	          else if ( (sGNM.equals("02")) && (sLLXX.equals("02"))) { //退出登陆
	            iFrameType = Glu_ConstDefine.SJLX_ZDTC;
	          }
	          else if ( (sGNM.equals("02")) && (sLLXX.equals("04"))) { //心跳
	            iFrameType = Glu_ConstDefine.SJLX_ZXXT;
	          }
	          else { //任务数据自动（对于国网任务数据只有自动，没有召测）
	            iFrameType = Glu_ConstDefine.SJLX_LSSJZD;
	          }
	        }
	      }

	      String sDT = sData.substring(4, 8); //信息类
	      int iDataType = GetQuanGuoDataType(sDT, sGNM); //数据类型:10普通数据;21小时冻结数据;22曲线数据;23日冻结数据;24月冻结数据;30告警数据;40设置返回数据
	      if (iDataType >= 21 && iDataType <= 24) { //一类小时冻结、二类数据(历史数据)
	        iFrameType = Glu_ConstDefine.SJLX_LSSJZC;
	      }

	      //赋返回值
	      Struct_FrameInfo FrameInfo = new Struct_FrameInfo();
	      FrameInfo.TerminalLogicAdd = sZDLJDZ.toCharArray();
	      FrameInfo.StationNo = iZZBH;
	      FrameInfo.FrameSeq = iZNXH;
	      FrameInfo.CommandSeq = iZXH;
	      FrameInfo.ControlCode = sKZM.toCharArray();
	      FrameInfo.FunctionCode = sGNM.toCharArray();
	      FrameInfo.DataType = iFrameType;
	      FrameInfo.SetDataContent(sData);
	      FrameInfo.TermialProtocolNo = iGYLX;
	      return FrameInfo;
	    }
	    catch (Exception ex) {
	      return null;
	    }

	  }
	//国网规范规约帧解析
	  private Struct_FrameInfo FrameExplainOfIHD(String sSourceData,
	                                                 int iGYLX) {
	    try {
	      String sZDLJDZ = sSourceData.substring(10, 20); //取终端逻辑地址
	      sZDLJDZ = sZDLJDZ.substring(2, 4) + sZDLJDZ.substring(0, 2) +
	          sZDLJDZ.substring(6, 8) + sZDLJDZ.substring(4, 6) +
	          sZDLJDZ.substring(8, 10);
	      int iZZBH = (Integer.parseInt(sSourceData.substring(18, 20), 16) >>
	                   1); ////取主站编号
	      String sKZM = sSourceData.substring(8, 10); //控制码

	      //取命令序号
	      String stemp = sSourceData.substring(22, 24); //取序列域
	      int iTpV = Integer.parseInt(stemp, 16) & 128; //时间标签有效位
	      int iZXH = Integer.parseInt(stemp, 16) & 15;
	      int iZNXH = 0;

	      //取功能码AFN
	      String sGNM = sSourceData.substring(20, 22);
	      int iTemp = Integer.parseInt(sKZM, 16);
	      int iCSFX = iTemp & 128; //传送方向DIR
	      int iQDBZ = iTemp & 64; //启动标志位PRM

	      //取数据项标识,用来判断登陆/心跳/退出信息
	      String sLLXX = sSourceData.substring(28, 30);
	      //天津模块表,用来判断电能表报警事件,和任务区分开
	      String sAlarm = "";
	      if (sSourceData.length() > 44) {
	    	  sAlarm = sSourceData.substring(40, 44); //电能表报警事件主动上传数据单元标识1规定为“0xC02F”，
	      }

	      //取数据区内容
	      int iLen = Integer.parseInt(sSourceData.substring(4,
	          6) + sSourceData.substring(2, 4), 16);
	      iLen = iLen >> 2;
	      iLen *= 2;
	      String sData = "";

	      if (iTpV == 128) { //附加信息域中带有时间标签Tp
	    	  sData = sData + sSourceData.substring(24, 8 + iLen - 12); //数据区只取链路数据
	      }
	      else {
	    	  sData = sData + sSourceData.substring(24, 8 + iLen); //数据区只取链路数据
	      }
	      if ( (Integer.parseInt(sKZM.substring(0, 1), 16) & 2) == 2) { //消去2个字节的事件计数器
	    	  sData = sData.substring(0, sData.length() - 4);
	      }

	      //对于国网而言，只能召测两类数据，普通数据召测（一类，二类）和事件召测（三类）,没有任务补召
	      //所以启动标志为0的有两类数据：任务自动上送或是事件自动上送；启动标志为1的也只有两类：普通数据召测或是事件数据召测
	      //事件召测（三类数据）的功能码只为一个‘0E’所以利用这两个参数可以判断三种类型的数据）
	      int iFrameType = Glu_ConstDefine.SJLX_WDY; //未定义类型
	      if (iCSFX == 0) {
	    	  iFrameType = Glu_ConstDefine.SJLX_XXBW;
	      }
	      else {
	    	  if (iQDBZ == 0) {        
	    		  //对于曲线数据需要判断到数据项来决定是否需要修改数据类型为任务数据
	              iFrameType = Glu_ConstDefine.SJLX_PTSJ; //普通数据          
	    	  }
	    	  else {
		          if ( (sGNM.equals("0E")) ||
		              ( (sGNM.equals("11")) && (sAlarm.equals("2FC0")))) { //肯定是异常数据自动上送//天津模块表电能表告警
		        	  iFrameType = Glu_ConstDefine.SJLX_YCSJZD;
		          }
		          else if ( (sGNM.equals("02")) && (sLLXX.equals("01"))) { //登陆
		        	  iFrameType = Glu_ConstDefine.SJLX_ZDDL;
		          }
		          else if ( (sGNM.equals("02")) && (sLLXX.equals("02"))) { //退出登陆
		        	  iFrameType = Glu_ConstDefine.SJLX_ZDTC;
		          }
		          else if ( (sGNM.equals("02")) && (sLLXX.equals("04"))) { //心跳
		        	  iFrameType = Glu_ConstDefine.SJLX_ZXXT;
		          }
		          else { //任务数据自动（对于国网任务数据只有自动，没有召测）
		        	  iFrameType = Glu_ConstDefine.SJLX_LSSJZD;
		          }
	    	  }
	      }

	      String sDT = sData.substring(4, 8); //信息类
	      int iDataType = GetQuanGuoDataType(sDT, sGNM); //数据类型:10普通数据;21小时冻结数据;22曲线数据;23日冻结数据;24月冻结数据;30告警数据;40设置返回数据
	      if (iDataType >= 21 && iDataType <= 24) { //一类小时冻结、二类数据(历史数据)
	    	  iFrameType = Glu_ConstDefine.SJLX_LSSJZC;
	      }

	      //赋返回值
	      Struct_FrameInfo FrameInfo = new Struct_FrameInfo();
	      FrameInfo.TerminalLogicAdd = sZDLJDZ.toCharArray();
	      FrameInfo.StationNo = iZZBH;
	      FrameInfo.FrameSeq = iZNXH;
	      FrameInfo.CommandSeq = iZXH;
	      FrameInfo.ControlCode = sKZM.toCharArray();
	      FrameInfo.FunctionCode = sGNM.toCharArray();
	      FrameInfo.DataType = iFrameType;
	      FrameInfo.SetDataContent(sData);
	      FrameInfo.TermialProtocolNo = iGYLX;
	      return FrameInfo;
	    }
	    catch (Exception ex) {
	      return null;
	    }

	  }
	}

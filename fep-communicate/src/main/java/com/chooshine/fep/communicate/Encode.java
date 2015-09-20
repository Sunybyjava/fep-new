package com.chooshine.fep.communicate;


import javax.crypto.*;
import javax.crypto.spec.DESKeySpec;

import com.chooshine.fep.ConstAndTypeDefine.Glu_ConstDefine;
import com.chooshine.fep.FrameDataAreaExplain.DataSwitch;

import java.security.*;
import java.util.Random;

public class Encode {
	  //public Encode() {
	  //}

	  public static void main(String[] args) {
	    System.out.println(Encode1(129, 33, "127.0.0.1", "12660000", "164D"));
	  }

	  private static String Encode129(int Seq, String Ip, String TerminalAddr,
	                                  String Key) {
	    try {
	      String sTemp = "";
	      String sHexIp = ChangeIPtoHexString(Ip);
	      int[] iTempBuff = new int[8];
	      if (sHexIp.length() == 8) {
	        String sParam1 = "" + sHexIp + "3393" + Key;
	        if (sParam1.length() != 16) {
	          return "";
	        }
	        String sParam2 = "" + TerminalAddr + "9333" +
	            IntToHex(Integer.toString(Seq), "00") + "81";
	        if (sParam2.length() != 16) {
	          return "";
	        }
	        int[] iTemp1 = new int[8];
	        int[] iTemp2 = new int[8];
	        for (int i = 0; i < 8; i++) {
	          iTemp1[i] = Integer.parseInt(sParam1.substring(i * 2, i * 2 + 2), 16);
	          iTemp2[i] = Integer.parseInt(sParam2.substring(i * 2, i * 2 + 2), 16);
	        }
	        // DES算法要求有一个可信任的随机数源
	        SecureRandom sr = new SecureRandom();
	        byte rawKeyData[] = new byte[8];
	        for (int i = 0; i < 8; i++) {
	          rawKeyData[i] = (byte) iTemp1[i];
	        }
	        // 从原始密匙数据创建DESKeySpec对象
	        DESKeySpec dks = new DESKeySpec(rawKeyData);

	        // 创建一个密匙工厂，然后用它把DESKeySpec转换成
	        // 一个SecretKey对象
	        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
	        SecretKey key = keyFactory.generateSecret(dks);

	        // Cipher对象实际完成加密操作
	        Cipher cipher = Cipher.getInstance("DES");

	        // 用密匙初始化Cipher对象
	        cipher.init(Cipher.ENCRYPT_MODE, key, sr);

	        // 现在，获取数据并加密
	        byte data[] = new byte[8];
	        for (int i = 0; i < 8; i++) {
	          data[i] = (byte) iTemp2[i];
	        }
	        // 正式执行加密操作
	        byte encryptedData[] = cipher.doFinal(data);
	        for (int i = 0; i < 8; i++) {
	          iTempBuff[i] = encryptedData[i];
	        }
	        iTempBuff = Regular(iTempBuff, 8);
	        sTemp = GetCode(iTempBuff);
	      }
	      return sTemp;
	    }
	    catch (Exception ex) {
	      return "";
	    }
	  }

	  private static String Encode128(int Seq, String Ip, String TerminalAddr,
	                                  String Key) {
	    try {
	      String sTemp = "";
	      String sHexIp = ChangeIPtoHexString(Ip);
	      int[] iTempBuff = new int[8];
	      if (sHexIp.length() == 8) {
	        String sParam1 = "" + sHexIp + "3393" + Key;
	        if (sParam1.length() != 16) {
	          return "";
	        }
	        String sParam2 = "" + TerminalAddr + "9333" +
	            IntToHex(Integer.toString(Seq), "00") + "80";
	        if (sParam2.length() != 16) {
	          return "";
	        }
	        for (int i = 0; i < 8; i++) {
	          int iTemp1 = Integer.parseInt(sParam1.substring(i * 2, i * 2 + 2), 16);
	          int iTemp2 = Integer.parseInt(sParam2.substring(i * 2, i * 2 + 2), 16);
	          iTempBuff[i] = iTemp1 ^ iTemp2;
	        }
	        iTempBuff = Regular(iTempBuff, 8);
	        sTemp = GetCode(iTempBuff);
	      }
	      return sTemp;
	    }
	    catch (Exception ex) {
	      return "";
	    }
	  }

	  private static String ChangeIPtoHexString(String Ip) {
	    String sOutput = "";
	    int iPos = -1;
	    try {
	      for (int i = 0; i < 4; i++) {
	        iPos = Ip.indexOf(".");
	        if ( (iPos == -1) && (i != 3)) { //前面三次都需要处理间隔符
	          return sOutput;
	        }
	        if (i == 3) { //最后一位不需要有间隔符，所以位置就是剩余的长度
	          iPos = Ip.length();
	        }
	        if (iPos != -1) {
	          String sTemp = Ip.substring(0, iPos);
	          sOutput = sOutput + IntToHex(sTemp, "00");
	          Ip = Ip.substring(iPos + 1);
	        }
	      }
	    }
	    finally {      
	    }
	    return sOutput;
	  }

	  private static String GetCode(int[] Code) {
	    /*密码算子	50	42	34	26	18	10	2	  52
	     密码	    1	  2	  3	  4	  5	  6	  7	  8
	     密码算子	44	36	28	20	12	4	  54	46
	     密码	    9	  10	11	12	13	14	15	16 */
	    String Result = "";
	    int iTemp = Code[6];
	    int[] iResult = new int[1];
	    iResult[0] = (iTemp >> 6) & 1;
	    iResult[0] = iResult[0] << 1;
	    iTemp = Code[5];
	    iResult[0] = iResult[0] + ( (iTemp >> 6) & 1);
	    iResult[0] = iResult[0] << 1;
	    iTemp = Code[4];
	    iResult[0] = iResult[0] + ( (iTemp >> 6) & 1);
	    iResult[0] = iResult[0] << 1;
	    iTemp = Code[3];
	    iResult[0] = iResult[0] + ( (iTemp >> 6) & 1);
	    iResult[0] = iResult[0] << 1;
	    iTemp = Code[2];
	    iResult[0] = iResult[0] + ( (iTemp >> 6) & 1);
	    iResult[0] = iResult[0] << 1;
	    iTemp = Code[1];
	    iResult[0] = iResult[0] + ( (iTemp >> 6) & 1);
	    iResult[0] = iResult[0] << 1;
	    iTemp = Code[0];
	    iResult[0] = iResult[0] + ( (iTemp >> 6) & 1);
	    iResult[0] = iResult[0] << 1;
	    iTemp = Code[6];
	    iResult[0] = iResult[0] + ( (iTemp >> 4) & 1);
	    iResult = Regular(iResult, 1);
	    Result = IntToHex(Integer.toString(iResult[0]), "00");
	    iTemp = Code[5];
	    iResult[0] = (iTemp >> 4) & 1;
	    iResult[0] = iResult[0] << 1;
	    iTemp = Code[4];
	    iResult[0] = iResult[0] + ( (iTemp >> 4) & 1);
	    iResult[0] = iResult[0] << 1;
	    iTemp = Code[3];
	    iResult[0] = iResult[0] + ( (iTemp >> 4) & 1);
	    iResult[0] = iResult[0] << 1;
	    iTemp = Code[2];
	    iResult[0] = iResult[0] + ( (iTemp >> 4) & 1);
	    iResult[0] = iResult[0] << 1;
	    iTemp = Code[1];
	    iResult[0] = iResult[0] + ( (iTemp >> 4) & 1);
	    iResult[0] = iResult[0] << 1;
	    iTemp = Code[0];
	    iResult[0] = iResult[0] + ( (iTemp >> 4) & 1);
	    iResult[0] = iResult[0] << 1;
	    iTemp = Code[6];
	    iResult[0] = iResult[0] + ( (iTemp >> 2) & 1);
	    iResult[0] = iResult[0] << 1;
	    iTemp = Code[5];
	    iResult[0] = iResult[0] + ( (iTemp >> 2) & 1);
	    iResult = Regular(iResult, 1);
	    Result = Result + IntToHex(Integer.toString(iResult[0]), "00");
	    return Result;
	  }

	  public static String StrStuff(String str, int iLen, String sInput, int iSign) { //字符补足匹配:补足字符；要求长度；输入字符串；补足方向
	    String sOutput = "";
	    try {
	      try {
	        int iLenStr = sInput.length();
	        if (iLen > iLenStr) { //输入字符需要补足
	          for (int i = 0; i < (iLen - iLenStr); i++) {
	            if (iSign == 10) { //左补足
	              sInput = str + sInput;
	            }
	            else { //右补足
	              sInput = sInput + str;
	            }
	          }
	        }
	        else if (iLen < iLenStr) { //输入字符过长需要消去
	          if (iSign == 10) { //消去左部
	            sInput = sInput.substring(iLenStr - iLen, iLenStr);
	          }
	          else { //消去右部
	            sInput = sInput.substring(0, iLen);
	          }
	        }
	        sOutput = sInput;
	      }
	      catch (Exception e) {
	      }
	    }
	    finally {
	    }
	    return sOutput;
	  }
	  public static String getRandom(int Lenth) {
	  	StringBuffer buffer = new StringBuffer(
	  			"0123456789abcdef");
	  	StringBuffer sb = new StringBuffer();
	  	Random r = new Random();
	  	int range = buffer.length();
	  	for (int i = 0; i < Lenth; i++) {
	  		//生成指定范围类的随机数0—字符串长度(包括0、不包括字符串长度)
	  	    sb.append(buffer.charAt(r.nextInt(range)));
	  	}
	  	return sb.toString();
	  }

	  public static String IntToHex(String sInt, String sSJGS) { //十进制转换成十六进制
	    String sDataContent = "";
	    try {
	      sInt = (Integer.toString(Integer.parseInt(sInt), 16)).toUpperCase(); //Integer.toString(int,16):十进制整型转换成十六进制(小写字母)
	      sDataContent = StrStuff("0", sSJGS.length(), sInt, 10);
	    }
	    finally {
	    }
	    return sDataContent;
	  }
	  public static String HexASCIIToString(String sHex) { //把十六进制的ASCII码转换成相应的字符
	      String sDataContent = "";
	      try {
	          try {
	              if ((sHex.length() % 2) == 0) {
	                  int sByteLen = sHex.length() / 2; //字节长度
	                  char[] chrList = new char[sByteLen];
	                  for (int i = 0; i < sByteLen; i++) {
	                      chrList[i] = (char) (Integer.parseInt(DataSwitch.
	                              HexToInt(sHex.substring(2 * i, 2 * i + 2), "00")));
	                  }
	                  sDataContent = (new String(chrList)).trim();
	              }
	          } catch (Exception e) {
	          	Glu_ConstDefine.Log1.WriteLog("Fun(HexASCIIToString) Error:" + e.toString());
	          }
	      } finally {
	      }
	      return sDataContent;
	  }
	  private static int[] Regular(int[] CodeBuff, int Count) {
	    int[] iArrBuff = new int[Count];
	    int iBuff = 0;
	    for (int i = 0; i < Count; i++) {
	      iBuff = CodeBuff[i];
	      int iTemp = 0;
	      for (int j = 0; j < 8; j++) {
	        iTemp = ( (iTemp << 1) + ( (iBuff >> j) & 1));
	      }
	      iArrBuff[i] = iTemp;
	    }
	    return iArrBuff;
	  }

	  /*
	        //--------加密函数---------
	        //     EncodeType  加密类型 :  128,129
	        //     Seq         帧序列号 ： 0...255
	        //     IP           IP地址 : '127.0.0.1'
	        //     ZDLJDZ  终端逻辑地址 : '65D391F3'
	        //     Key         加密密钥 : '7E6A'
	        //------------------------------------------
	   */
	  public static String Encode1(int EncodeType, int Seq, String Ip,
	                              String TerminalAddr,
	                              String Key) {
	    if (EncodeType == 128) {
	      return Encode128(Seq, Ip, TerminalAddr, Key);
	    }
	    else if (EncodeType == 129) {
	      return Encode129(Seq, Ip, TerminalAddr, Key);
	    }
	    else {
	      return Key;
	    }
	  }
	  public static String StringtoHexASCII(String sStr) { //把字符转换成相应的ASCII码的十六进制
	      String sDataContent = "";
	      try {
	          byte[] bt = sStr.getBytes();
	          for (int i = 0; i < bt.length; i++) {
	              sDataContent = sDataContent + Integer.toHexString(bt[i]);
	          }
	      } finally {
	      }
	      return sDataContent;
	  }
	}

package com.chooshine.fep.FrameDataAreaExplain;

import java.util.Date;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import com.chooshine.fep.ConstAndTypeDefine.Glu_ConstDefine;
//import java.text.ParsePosition;

//import com.fep.FrameExplain.PublicFunctions;
public class DataSwitch { //数据转换类
	public DataSwitch() {    	
    }

    /*public static void main(String[] args){
      String sSHex="",sSSJGS="",sPutout="";
      DataSwitch dataSW=new DataSwitch();
      //物理数据转换成逻辑数据
      sSHex="FFFFFFFF";
      sSSJGS="00000000";
      sPutout=dataSW.HexToInt(sSHex,sSSJGS);
      System.out.println((sPutout));
      sSHex="20050926101213";
      sSSJGS="yyyymmddhhnnss";
      sPutout=dataSW.BCDToDateTime(sSHex,sSSJGS);
      System.out.println((sPutout));
      sSHex="1234";
      sSSJGS="00.00";
      sPutout=dataSW.BCDToFloat(sSHex,sSSJGS);
      System.out.println((sPutout));
      sSHex="1234";
      sSSJGS="2";
      sPutout=dataSW.HexToString(sSHex,sSSJGS);
      System.out.println((sPutout));
      //逻辑数据转换成物理数据
      sSHex="1234";
      sSSJGS="0000";
      sPutout=dataSW.IntToHex(sSHex,sSSJGS);
      System.out.println((sPutout));
      sSHex="20050907110112";
      sSSJGS="yymmddhhnnss";
      sPutout=dataSW.DateTimeToBCD(sSHex,sSSJGS);
      System.out.println((sPutout));
      sSHex="112111";
      sSSJGS="0000.00";
      sPutout=dataSW.FloatToBCD(sSHex,sSSJGS);
      System.out.println((sPutout));
      sSHex="1234";
      sSSJGS="1";
      sPutout=dataSW.StringToHex(sSHex,sSSJGS);
      System.out.println((sPutout));
      sSHex="1bcd";
      System.out.println(sPutout);
      sSHex="112233445566";
      sPutout=dataSW.ReverseStringByByte(sSHex);
      System.out.println(sPutout);
      sSHex="Aabcdef123";
      sPutout=dataSW.StringtoHexASCII(sSHex);
      System.out.println(sPutout);
      sSHex="41616263646566313233";
      sPutout=dataSW.HexASCIIToString(sSHex);
      System.out.println(sPutout);

      sSHex="00FEFEFEFE6800FE16";
      sPutout=dataSW.StrFilter("FE",false,sSHex);
      System.out.println(sPutout);


      sSHex="20051114151200";
      sPutout=dataSW.IncreaseDateTime(sSHex,2,5);
      System.out.println(sPutout);



      String s1 = "2006-01-05 10:26:00";
      String s2 = "2003-08-15 17:15:30";
      try {
     SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
          ParsePosition pos = new ParsePosition(0);
          ParsePosition pos1 = new ParsePosition(0);
          Date dt1 = formatter.parse(s1, pos);
          Date dt2 = formatter.parse(s2, pos1);
          Date dt3 =new Date();
          System.out.println("dt1=" + dt1);
          //System.out.println("dt2=" + dt2);
          System.out.println("dt3=" + dt3);
          double l = (dt3.getTime() - dt1.getTime());
          l=l/3600000;
          int iTemp=(int)(l);
          System.out.println("Hello World!=" + iTemp);


      } catch (Exception e) {
          System.out.println("exception" + e.toString());
      }
     }*/

    public static String CRC16(String str) { //CRC16校验方式
    	String cs = "";
    	try {
    		int iCrcData = Integer.parseInt(HexToInt("FFFF","0000"));
	        int iLen = str.length() / 2;
	        int iTemp = 0;
	        int iMSBInfo = 0;
	        for( int i = 0 ; i < iLen; i++ ) {
	        	iTemp = Integer.parseInt(HexToInt(str.substring(i*2, i*2+2),"00"));
	        	iCrcData = iCrcData ^ iTemp;
	        	for (int j = 0; j < 8; j++){
	        		iMSBInfo = iCrcData & 1;
	        		iCrcData = iCrcData >> 1;
	        		if (iMSBInfo != 0){
	        			iCrcData = iCrcData ^ Integer.parseInt(HexToInt("A001","0000"));
	        		}

	        	}
	        }
	        cs = IntToHex(""+iCrcData,"0000");
    	}
    	catch (Exception e) {
        }      
    	return cs;
    }
    public static String FCS(String str) { //DLMS校验方式
    	String cs = "";
    	int[] CRC16Tab = {
    		0x0000, 0x1189, 0x2312, 0x329b, 0x4624, 0x57ad, 0x6536, 0x74bf,
    		0x8c48, 0x9dc1, 0xaf5a, 0xbed3, 0xca6c, 0xdbe5, 0xe97e, 0xf8f7,
    		0x1081, 0x0108, 0x3393, 0x221a, 0x56a5, 0x472c, 0x75b7, 0x643e,
    		0x9cc9, 0x8d40, 0xbfdb, 0xae52, 0xdaed, 0xcb64, 0xf9ff, 0xe876,
    		0x2102, 0x308b, 0x0210, 0x1399, 0x6726, 0x76af, 0x4434, 0x55bd,
    		0xad4a, 0xbcc3, 0x8e58, 0x9fd1, 0xeb6e, 0xfae7, 0xc87c, 0xd9f5,
    		0x3183, 0x200a, 0x1291, 0x0318, 0x77a7, 0x662e, 0x54b5, 0x453c,
    		0xbdcb, 0xac42, 0x9ed9, 0x8f50, 0xfbef, 0xea66, 0xd8fd, 0xc974,
    		0x4204, 0x538d, 0x6116, 0x709f, 0x0420, 0x15a9, 0x2732, 0x36bb,
    		0xce4c, 0xdfc5, 0xed5e, 0xfcd7, 0x8868, 0x99e1, 0xab7a, 0xbaf3,
    		0x5285, 0x430c, 0x7197, 0x601e, 0x14a1, 0x0528, 0x37b3, 0x263a,
    		0xdecd, 0xcf44, 0xfddf, 0xec56, 0x98e9, 0x8960, 0xbbfb, 0xaa72,
    		0x6306, 0x728f, 0x4014, 0x519d, 0x2522, 0x34ab, 0x0630, 0x17b9,
    		0xef4e, 0xfec7, 0xcc5c, 0xddd5, 0xa96a, 0xb8e3, 0x8a78, 0x9bf1,
    		0x7387, 0x620e, 0x5095, 0x411c, 0x35a3, 0x242a, 0x16b1, 0x0738,
    		0xffcf, 0xee46, 0xdcdd, 0xcd54, 0xb9eb, 0xa862, 0x9af9, 0x8b70,
    		0x8408, 0x9581, 0xa71a, 0xb693, 0xc22c, 0xd3a5, 0xe13e, 0xf0b7,
    		0x0840, 0x19c9, 0x2b52, 0x3adb, 0x4e64, 0x5fed, 0x6d76, 0x7cff,
    		0x9489, 0x8500, 0xb79b, 0xa612, 0xd2ad, 0xc324, 0xf1bf, 0xe036,
    		0x18c1, 0x0948, 0x3bd3, 0x2a5a, 0x5ee5, 0x4f6c, 0x7df7, 0x6c7e,
    		0xa50a, 0xb483, 0x8618, 0x9791, 0xe32e, 0xf2a7, 0xc03c, 0xd1b5,
    		0x2942, 0x38cb, 0x0a50, 0x1bd9, 0x6f66, 0x7eef, 0x4c74, 0x5dfd,
    		0xb58b, 0xa402, 0x9699, 0x8710, 0xf3af, 0xe226, 0xd0bd, 0xc134,
    		0x39c3, 0x284a, 0x1ad1, 0x0b58, 0x7fe7, 0x6e6e, 0x5cf5, 0x4d7c,
    		0xc60c, 0xd785, 0xe51e, 0xf497, 0x8028, 0x91a1, 0xa33a, 0xb2b3,
    		0x4a44, 0x5bcd, 0x6956, 0x78df, 0x0c60, 0x1de9, 0x2f72, 0x3efb,
    		0xd68d, 0xc704, 0xf59f, 0xe416, 0x90a9, 0x8120, 0xb3bb, 0xa232,
    		0x5ac5, 0x4b4c, 0x79d7, 0x685e, 0x1ce1, 0x0d68, 0x3ff3, 0x2e7a,
    		0xe70e, 0xf687, 0xc41c, 0xd595, 0xa12a, 0xb0a3, 0x8238, 0x93b1,
    		0x6b46, 0x7acf, 0x4854, 0x59dd, 0x2d62, 0x3ceb, 0x0e70, 0x1ff9,
    		0xf78f, 0xe606, 0xd49d, 0xc514, 0xb1ab, 0xa022, 0x92b9, 0x8330,
    		0x7bc7, 0x6a4e, 0x58d5, 0x495c, 0x3de3, 0x2c6a, 0x1ef1, 0x0f78
    	};
    	 
    	try {
    		int iLen = str.length() / 2;
    		int CRCval = 0xFFFF;
    		int itemp = 0; 
    		int[] bSJNR = new int[iLen];
    		for (int i = 0; i < iLen; i++){
    			String hex =HexToInt(str.substring(i*2, i*2+2),"00"); 
    			bSJNR[i] = Integer.parseInt(hex);
    		}
    		for (int i = 0; i < iLen; i++){
    			itemp = CRCval & 0x00ff;
    			CRCval >>= 8;
    			itemp = itemp ^ bSJNR[i];    		
    			CRCval = CRC16Tab[itemp] ^ CRCval;
    		}
    		CRCval = CRCval ^ 0xFFFF;
    		int itemp1 = CRCval & 0x00FF;
    		int itemp2 = CRCval & 0xFF00;
    		CRCval = itemp1 + itemp2;
    		cs = IntToHex(""+CRCval,"0000");
    	}
    	catch (Exception e) {
        }      
    	return cs;
    }
    
    public static String MultiCRC(String str, String Key){
    	String[] crc_ta;
    	crc_ta = new String[256];
    	String[] crc_3D65 = {// CRC 余式表:生成多项式0x3d65       			
    	        "0000", "3D65", "7ACA", "47AF", "F594", "C8F1", "8F5E", "B23B", 
    	        "D64D", "EB28", "AC87", "91E2", "23D9", "1EBC", "5913", "6476", 
                "91FF", "AC9A", "EB35", "D650", "646B", "590E", "1EA1", "23C4", 
    	        "47B2", "7AD7", "3D78", "001D", "B226", "8F43", "C8EC", "F589", 
    	        "1E9B", "23FE", "6451", "5934", "EB0F", "D66A", "91C5", "ACA0", 
    	        "C8D6", "F5B3", "B21C", "8F79", "3D42", "0027", "4788", "7AED", 
    	        "8F64", "B201", "F5AE", "C8CB", "7AF0", "4795", "003A", "3D5F", 
    	        "5929", "644C", "23E3", "1E86", "ACBD", "91D8", "D677", "EB12", 
    	        "3D36", "0053", "47FC", "7A99", "C8A2", "F5C7", "B268", "8F0D", 
    	        "EB7B", "D61E", "91B1", "ACD4", "1EEF", "238A", "6425", "5940", 
    	        "ACC9", "91AC", "D603", "EB66", "595D", "6438", "2397", "1EF2", 
    	        "7A84", "47E1", "004E", "3D2B", "8F10", "B275", "F5DA", "C8BF", 
    	        "23AD", "1EC8", "5967", "6402", "D639", "EB5C", "ACF3", "9196", 
    	        "F5E0", "C885", "8F2A", "B24F", "0074", "3D11", "7ABE", "47DB", 
    	        "B252", "8F37", "C898", "F5FD", "47C6", "7AA3", "3D0C", "0069", 
    	        "641F", "597A", "1ED5", "23B0", "918B", "ACEE", "EB41", "D624", 
    	        "7A6C", "4709", "00A6", "3DC3", "8FF8", "B29D", "F532", "C857", 
    	        "AC21", "9144", "D6EB", "EB8E", "59B5", "64D0", "237F", "1E1A", 
    	        "EB93", "D6F6", "9159", "AC3C", "1E07", "2362", "64CD", "59A8", 
    	        "3DDE", "00BB", "4714", "7A71", "C84A", "F52F", "B280", "8FE5", 
    	        "64F7", "5992", "1E3D", "2358", "9163", "AC06", "EBA9", "D6CC", 
    	        "B2BA", "8FDF", "C870", "F515", "472E", "7A4B", "3DE4", "0081", 
    	        "F508", "C86D", "8FC2", "B2A7", "009C", "3DF9", "7A56", "4733", 
    	        "2345", "1E20", "598F", "64EA", "D6D1", "EBB4", "AC1B", "917E", 
    	        "475A", "7A3F", "3D90", "00F5", "B2CE", "8FAB", "C804", "F561", 
    	        "9117", "AC72", "EBDD", "D6B8", "6483", "59E6", "1E49", "232C", 
    	        "D6A5", "EBC0", "AC6F", "910A", "2331", "1E54", "59FB", "649E", 
    	        "00E8", "3D8D", "7A22", "4747", "F57C", "C819", "8FB6", "B2D3", 
    	        "59C1", "64A4", "230B", "1E6E", "AC55", "9130", "D69F", "EBFA", 
    	        "8F8C", "B2E9", "F546", "C823", "7A18", "477D", "00D2", "3DB7", 
    	        "C83E", "F55B", "B2F4", "8F91", "3DAA", "00CF", "4760", "7A05", 
    	        "1E73", "2316", "64B9", "59DC", "EBE7", "D682", "912D", "AC48" 
    	};
    	String[] crc_1021 = {// CRC 余式表:生成多项式0x1021       			
    			"0000", "1021", "2042", "3063", "4084", "50a5", "60c6", "70e7",
    	        "8108", "9129", "a14a", "b16b", "c18c", "d1ad", "e1ce", "f1ef",
    	        "1231", "0210", "3273", "2252", "52b5", "4294", "72f7", "62d6",
    	        "9339", "8318", "b37b", "a35a", "d3bd", "c39c", "f3ff", "e3de",
    	        "2462", "3443", "0420", "1401", "64e6", "74c7", "44a4", "5485",
    	        "a56a", "b54b", "8528", "9509", "e5ee", "f5cf", "c5ac", "d58d",
    	        "3653", "2672", "1611", "0630", "76d7", "66f6", "5695", "46b4",
    	        "b75b", "a77a", "9719", "8738", "f7df", "e7fe", "d79d", "c7bc",
    	        "48c4", "58e5", "6886", "78a7", "0840", "1861", "2802", "3823",
    	        "c9cc", "d9ed", "e98e", "f9af", "8948", "9969", "a90a", "b92b",
    	        "5af5", "4ad4", "7ab7", "6a96", "1a71", "0a50", "3a33", "2a12",
    	        "dbfd", "cbdc", "fbbf", "eb9e", "9b79", "8b58", "bb3b", "ab1a",
    	        "6ca6", "7c87", "4ce4", "5cc5", "2c22", "3c03", "0c60", "1c41",
    	        "edae", "fd8f", "cdec", "ddcd", "ad2a", "bd0b", "8d68", "9d49",
    	        "7e97", "6eb6", "5ed5", "4ef4", "3e13", "2e32", "1e51", "0e70",
    	        "ff9f", "efbe", "dfdd", "cffc", "bf1b", "af3a", "9f59", "8f78",
    	        "9188", "81a9", "b1ca", "a1eb", "d10c", "c12d", "f14e", "e16f",
    	        "1080", "00a1", "30c2", "20e3", "5004", "4025", "7046", "6067",
    	        "83b9", "9398", "a3fb", "b3da", "c33d", "d31c", "e37f", "f35e",
    	        "02b1", "1290", "22f3", "32d2", "4235", "5214", "6277", "7256",
    	        "b5ea", "a5cb", "95a8", "8589", "f56e", "e54f", "d52c", "c50d",
    	        "34e2", "24c3", "14a0", "0481", "7466", "6447", "5424", "4405",
    	        "a7db", "b7fa", "8799", "97b8", "e75f", "f77e", "c71d", "d73c",
    	        "26d3", "36f2", "0691", "16b0", "6657", "7676", "4615", "5634",
    	        "d94c", "c96d", "f90e", "e92f", "99c8", "89e9", "b98a", "a9ab",
    	        "5844", "4865", "7806", "6827", "18c0", "08e1", "3882", "28a3",
    	        "cb7d", "db5c", "eb3f", "fb1e", "8bf9", "9bd8", "abbb", "bb9a",
    	        "4a75", "5a54", "6a37", "7a16", "0af1", "1ad0", "2ab3", "3a92",
    	        "fd2e", "ed0f", "dd6c", "cd4d", "bdaa", "ad8b", "9de8", "8dc9",
    	        "7c26", "6c07", "5c64", "4c45", "3ca2", "2c83", "1ce0", "0cc1",
    	        "ef1f", "ff3e", "cf5d", "df7c", "af9b", "bfba", "8fd9", "9ff8",
    	        "6e17", "7e36", "4e55", "5e74", "2e93", "3eb2", "0ed1", "1ef0" 
    	};
    	if (Key.equals("1021")){
    		crc_ta = crc_1021;
    	}else if (Key.equals("3D65")){
    		crc_ta = crc_3D65;
    	}    	
    	String cs = "";
    	try {
    		int iCrcData = 0;
    		int iLen = str.length() / 2;
	        int iTemp = 0;
	        int ida = 0;
	        String Hex = "";
	        for( int i = 0 ; i < iLen; i++ ) {
	        	ida = iCrcData / 256;  //暂存CRC的高8位
	        	iCrcData <<= 8;        //左移8位，相当于CRC的低8位乘以28 	
	        	Hex = IntToHex(""+iCrcData,"00000000");
	        	iCrcData = Integer.parseInt(HexToInt(Hex.substring(4, 8),"0000"));
	        	iTemp = Integer.parseInt(HexToInt(str.substring(i*2, i*2+2),"00"));
	        	iTemp = (ida ^ iTemp);
	        	iTemp = Integer.parseInt(HexToInt(crc_ta[iTemp],"0000"));
	        	iCrcData = iCrcData ^ iTemp;	        	
	        }
	        cs = IntToHex(""+iCrcData,"0000");
    	}
    	catch (Exception e) {
        }   
    	return cs;
    }
    public static int Power2(int Mi) { //2的幂运算0~7
        int iResult = 0;
        try {
            try {
                if (Mi == 0) {
                    iResult = 1;
                } else if (Mi == 1) {
                    iResult = 2;
                } else if (Mi == 2) {
                    iResult = 4;
                } else if (Mi == 3) {
                    iResult = 8;
                } else if (Mi == 4) {
                    iResult = 16;
                } else if (Mi == 5) {
                    iResult = 32;
                } else if (Mi == 6) {
                    iResult = 64;
                } else if (Mi == 7) {
                    iResult = 128;
                }
            } catch (Exception e) {
            	Glu_ConstDefine.Log1.WriteLog("Fun(Power2) Error:" + e.toString());
            }
        } finally {
        }
        return iResult;

    }

    public static String ReverseStringByByte(String str) { //按字节倒置
        String sOutput = "";
        try {
            try {
                if (str.length() % 2 == 0) {
                    for (int i = 0; i < str.length() / 2; i++) {
                        sOutput = sOutput +
                                  str.substring((str.length() - (i + 1) * 2),
                                                (str.length() - i * 2));
                    }
                } else {
                	Glu_ConstDefine.Log1.WriteLog(
                            "Fun(ReverseStringByByte) Error:String Length must can be 2 divided!");
                }
            } catch (Exception e) {
            	Glu_ConstDefine.Log1.WriteLog("Fun(ReverseStringByByte) Error:" + e.toString());
            }
        } finally {
        }
        return sOutput;
    }

    public static String StrFilter(String str, boolean IsSeries, String sInput) { //字符过滤函数：过滤字符;过滤字符是否要连续才过滤;需要过滤的字符串
        String sOutput = "";
        try {
            try {
                int iPos1 = sInput.indexOf(str, 0);
                while (iPos1 != -1) {
                    sInput = sInput.substring(0, iPos1) +
                             sInput.substring(iPos1 + str.length(),
                                              sInput.length());
                    int iPos2 = sInput.indexOf(str, 0);
                    if (IsSeries == false || (iPos2 == iPos1)) { //如果过滤字符不需要连续的或者下一个字符是连续的
                        iPos1 = iPos2;
                    } else {
                        break;
                    }
                }
                sOutput = sInput;
            } catch (Exception e) {
            	Glu_ConstDefine.Log1.WriteLog("Fun(StrFilter) Error:" + e.toString());
            }
        } finally {
        }
        return sOutput;
    }

    public static String SearchStr(String str, String sInput) { //搜索某字符开头的字符串：匹配字符;需要搜索的字符串
        String sOutput = "";
        try {
            try {
                int iPos1 = sInput.indexOf(str, 0);
                if (iPos1 != -1) {
                    sOutput = sInput.substring(iPos1, sInput.length());
                } else {
                    sOutput = sInput;
                }
            } catch (Exception e) {
            	Glu_ConstDefine.Log1.WriteLog("Fun(SearchStr) Error:" + e.toString());
            }
        } finally {
        }
        return sOutput;
    }

    public static String StrStuff(String str, int iLen, String sInput,
                                  int iSign) { //字符补足匹配函数:补足字符；要求长度；输入字符串；补足方向
        String sOutput = "";
        try {
            try {
                int iLenStr = sInput.length();
                if (iLen > iLenStr) { //输入字符需要补足
                    for (int i = 0; i < (iLen - iLenStr); i++) {
                        if (iSign == 10) { //左补足
                            sInput = str + sInput;
                        } else { //右补足
                            sInput = sInput + str;
                        }
                    }
                } else if (iLen < iLenStr) { //输入字符过长需要消去
                    if (iSign == 10) { //消去左部
                        sInput = sInput.substring(iLenStr - iLen, iLenStr);
                    } else { //消去右部
                        sInput = sInput.substring(0, iLen);
                    }
                }
                sOutput = sInput;
            } catch (Exception e) {
            	Glu_ConstDefine.Log1.WriteLog("Fun(StrStuff) Error:" + e.toString());
            }
        } finally {
        }
        return sOutput;
    }

    /*-----------------------------逻辑数据转换成物理数据-------------------------------------------------*/
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

    public static String DateTimeToBCD(String sDateTime, String sSJGS) { //输入字符串固定格式YYYYMMDDHHNNSS转换成按格式要求的BCD码
        String sDataContent = "";
        String sGSTemp = "";
        try {
            try {
                if (sDateTime.length() == 14) {
                    //int iDateTime = sDateTime.length();
                    int iLenSJGS = sSJGS.length();
                    for (int i = 0; i < iLenSJGS / 2; i++) {
                        sGSTemp = sSJGS.substring(i * 2, i * 2 + 2);
                        if ((sGSTemp.toUpperCase()).equals("YY")) {
                            if (sDataContent.equals("")) { //第一个YY
                                sDataContent = sDataContent +
                                               sDateTime.substring(2, 4);
                            } else { //第二个YY
                                sDataContent = sDateTime.substring(0, 2) +
                                               sDataContent;
                            }
                        } else if ((sGSTemp.toUpperCase()).equals("MM")) { //月
                            sDataContent = sDataContent +
                                           sDateTime.substring(4, 6);
                        } else if ((sGSTemp.toUpperCase()).equals("DD")) { //日
                            sDataContent = sDataContent +
                                           sDateTime.substring(6, 8);
                        } else if ((sGSTemp.toUpperCase()).equals("HH")) { //时
                            sDataContent = sDataContent +
                                           sDateTime.substring(8, 10);
                        } else if ((sGSTemp.toUpperCase()).equals("NN")) { //分
                            sDataContent = sDataContent +
                                           sDateTime.substring(10, 12);
                        } else if ((sGSTemp.toUpperCase()).equals("SS")) { //秒
                            sDataContent = sDataContent +
                                           sDateTime.substring(12, 14);
                        } else if ((sGSTemp.toUpperCase()).equals("WW")) { //周
                        	Calendar DateTime = Calendar.getInstance();
                        	SimpleDateFormat formater = new SimpleDateFormat("yyyyMMdd");
                        	java.util.Date dST = formater.parse(sDateTime.substring(0, 8));
                        	DateTime.setTime(dST);
                        	int iWeek = DateTime.get(Calendar.DAY_OF_WEEK); 
                        	iWeek = iWeek - 1;
                        	if (iWeek == 0){
                        		iWeek = 7;
                        	}
                        	sDataContent = sDataContent + StrStuff("0", 2, ""+iWeek, 10);
                        }
                    }
                }
            } catch (Exception e) {
            	Glu_ConstDefine.Log1.WriteLog("Fun(DateTimeToBCD) Error:" + e.toString());
            }
        } finally {
        }
        return sDataContent;
    }
    public static String DateTimeToHEX(String sDateTime, String sSJGS) { //输入字符串固定格式YYYYMMDDHHNNSS转换成按格式要求的HEX码
        String sDataContent = "";
        String gDateTime = sDateTime;
        String sGSTemp = "", sDataTemp = "";
        try {
            try {
                if (sDateTime.length() >= 14) {                	
                    while (sSJGS.length() > 0){
                        sDataTemp = sDateTime.substring(0, 2);
                        sGSTemp = sSJGS.substring(0, 2);
                        if ((sGSTemp.toUpperCase()).equals("YY")) { //年
                        	sDataTemp = sDateTime.substring(0, 4);
                        	sDataTemp = IntToHex(sDataTemp, "0000");
                        	sDataContent = sDataContent + sDataTemp;
                        	sDateTime = sDateTime.substring(4, sDateTime.length());
                            sSJGS = sSJGS.substring(4, sSJGS.length());
                        } else if ((sGSTemp.toUpperCase()).equals("MM")) { //月
                        	sDataTemp = IntToHex(sDataTemp, "00");
                        	sDataContent = sDataContent + sDataTemp;
                            sDateTime = sDateTime.substring(2, sDateTime.length());
                            sSJGS = sSJGS.substring(2, sSJGS.length());
                        } else if ((sGSTemp.toUpperCase()).equals("DD")) { //日
                        	sDataTemp = IntToHex(sDataTemp, "00");
                        	sDataContent = sDataContent + sDataTemp;                  
                            sDateTime = sDateTime.substring(2, sDateTime.length());
                            sSJGS = sSJGS.substring(2, sSJGS.length());
                        }else if ((sGSTemp.toUpperCase()).equals("WW")) { //week
                        	Calendar DateTime = Calendar.getInstance();
                        	SimpleDateFormat formater = new SimpleDateFormat("yyyyMMdd");
                        	java.util.Date dST = formater.parse(gDateTime.substring(0,8));
                        	DateTime.setTime(dST);
                        	int iWeek = DateTime.get(Calendar.DAY_OF_WEEK); 
                        	if (iWeek == 1) {
                        		iWeek = 7;
                        	}else{
                        		iWeek = iWeek -1;
                        	}
                        	sDataContent = sDataContent + StrStuff("0", 2, ""+iWeek, 10);
                        	if (sDateTime.length() > 6){
                        		sDateTime = sDateTime.substring(2, sDateTime.length());
                        	}
                            sSJGS = sSJGS.substring(2, sSJGS.length());
                        } else if ((sGSTemp.toUpperCase()).equals("HH")) { //时
                        	sDataTemp = IntToHex(sDataTemp, "00");
                        	sDataContent = sDataContent + sDataTemp;
                            sDateTime = sDateTime.substring(2, sDateTime.length());
                            sSJGS = sSJGS.substring(2, sSJGS.length());
                        } else if ((sGSTemp.toUpperCase()).equals("NN")) { //分
                        	sDataTemp = IntToHex(sDataTemp, "00");
                        	sDataContent = sDataContent + sDataTemp;
                            sDateTime = sDateTime.substring(2, sDateTime.length());
                            sSJGS = sSJGS.substring(2, sSJGS.length());
                        } else if ((sGSTemp.toUpperCase()).equals("SS")) { //秒
                        	sDataTemp = IntToHex(sDataTemp, "00");
                        	sDataContent = sDataContent + sDataTemp + "00";
                            sDateTime = sDateTime.substring(2, sDateTime.length());
                            sSJGS = sSJGS.substring(2, sSJGS.length());
                        }
                    
                	}
                    if (sDataContent.length() >= 18){
                    	sDataContent = sDataContent + "800000";
                    }
                }
            } catch (Exception e) {
            	Glu_ConstDefine.Log1.WriteLog("Fun(DateTimeToBCD) Error" + e.toString());
            }
        } finally {
        }
        return sDataContent;
    }
    public static String FloatExToBCDConvert(String FloatSjz) {
        Double d = new Double(FloatSjz);
        String sFH = "";
        String sZSSjz = "";
        String sMB = "";
        if (d.doubleValue() >= 0.0) { //判断是正数、还是负数
            sFH = "+";
            if (d.doubleValue() >= 9990000.0) {
                return "0999"; //最大值
            }
        } else if (d.doubleValue() < 0.0) {
            sFH = "-";
            if (d.doubleValue() > -0.0001) {
                return "8001"; //最小值
            }
            try {
                FloatSjz = d.toString().substring(1);
            } catch (Exception ex7) {
                return "0000"; //处理异常情况，返回0000
            }
            d = new Double(FloatSjz);
        }
        try {
            char[] cFloat = d.toString().toCharArray();
            for (int i = 0; i < cFloat.length; i++) {
                if (((cFloat[i] >= '0') && (cFloat[i] <= '9')) ||
                    (cFloat[i] == '.')) { //判断数字中是否存在非法字符
                    continue;
                } else {
                    return "0000";
                }
            }
        } catch (Exception ex6) {
            return "0000"; //处理异常情况，返回0000
        }
        int iDotPos = FloatSjz.indexOf(".");
        if (iDotPos == -1) { //处理没有小数点的值
            try {
                if (FloatSjz.length() >= 7) {
                    sZSSjz = FloatSjz.substring(0, 3); //数字有效个数最多为3个
                    sMB = "10000"; //幂指数最大的情况
                }
            } catch (Exception ex4) {
                return "0000"; //处理异常情况，返回0000
            }
            if ((FloatSjz.length() < 7) && (FloatSjz.length() > 3)) {
                try {
                    sZSSjz = FloatSjz.substring(0, 3); //取前面三位数字
                } catch (Exception ex5) {
                    return "0000"; //处理异常情况，返回0000
                }
                switch (FloatSjz.length() - 3) { //根据剩余长度计算幂指数
                case 1: {
                    sMB = "10";
                    break;
                }
                case 2: {
                    sMB = "100";
                    break;
                }
                case 3: {
                    sMB = "1000";
                    break;
                }
                }
            } else {
                while (FloatSjz.length() < 3) {
                    FloatSjz = "0" + FloatSjz;
                }
                sZSSjz = FloatSjz; //不足3个字符补0
                sMB = "1";
            }
        }
        try {
            if ((iDotPos == 1) && (FloatSjz.substring(0, 1).equals("0"))) { //处理如0.123 或0.12345
                sMB = "0.001"; //幂指数是固定的
                if ((FloatSjz.length() - iDotPos) < 4) { //有效数字的个数处理，这种情况需要在数字后面补充0
                    sZSSjz = FloatSjz.substring(iDotPos + 1, FloatSjz.length());
                    while (sZSSjz.length() < 3) {
                        sZSSjz = sZSSjz + "0";
                    } //处理成120或100或010
                } else { //否则就取前面3位数字即可
                    sZSSjz = FloatSjz.substring(iDotPos + 1, iDotPos + 4);
                }
            }
        } catch (Exception ex3) {
            return "0000"; //处理异常情况，返回0000
        }
        try {
            if ((iDotPos >= 0) && (iDotPos <= 2) &&
                (!FloatSjz.substring(0, 1).equals("0"))) { //处理如1.2345或12.345
                if (FloatSjz.length() < 4) { //如1.2
                    FloatSjz = FloatSjz + "0"; //补上0，保证有三位数字可以取到
                }
                FloatSjz = FloatSjz.substring(0, 4); //得到如1.23或 12.3或1.20
                sZSSjz = FloatSjz.substring(0, iDotPos) +
                         FloatSjz.substring(iDotPos + 1, 4); //去除小数点后的三位有效数字
                if ((FloatSjz.length() - iDotPos) == 2) { //计算幂指数
                    sMB = "0.1";
                } else {
                    sMB = "0.01";
                }
            }
        } catch (Exception ex2) {
            return "0000"; //处理异常情况，返回0000
        }
        if ((iDotPos >= 3) && (iDotPos < 7)) { //处理如12.3、123.4等情况
            try {
                FloatSjz = FloatSjz.substring(0, 3); //取前面三位的有效数字
            } catch (Exception ex1) {
                return "0000"; //处理异常情况，返回0000
            }
            switch (iDotPos + 1 - FloatSjz.length()) { //计算幂指数
            case 1: {
                sMB = "1";
                break;
            }
            case 2: {
                sMB = "10";
                break;
            }
            case 3: {
                sMB = "100";
                break;
            }
            case 4: {
                sMB = "1000";
                break;
            }
            }
            sZSSjz = FloatSjz;
        }
        if (iDotPos >= 7) { //小数点在很后面的情况，幂指数也是固定，数字也取前三位有效
            try {
                FloatSjz = FloatSjz.substring(0, 3);
            } catch (Exception ex) {
                return "0000"; //处理异常情况，返回0000
            }
            sMB = "10000";
            sZSSjz = FloatSjz;
        }
        if (sFH.equals("+")) { //正号和幂部，根据数字符号和幂指数生成头一个字节的值
            if (sMB.equals("10000")) {
                sMB = "0";
            } else if (sMB.equals("1000")) {
                sMB = "2";
            } else if (sMB.equals("100")) {
                sMB = "4";
            } else if (sMB.equals("10")) {
                sMB = "6";
            } else if (sMB.equals("1")) {
                sMB = "8";
            } else if (sMB.equals("0.1")) {
                sMB = "A";
            } else if (sMB.equals("0.01")) {
                sMB = "C";
            } else if (sMB.equals("0.001")) {
                sMB = "E";
            }
        } else { //负号和幂部
            if (sMB.equals("10000")) {
                sMB = "1";
            } else if (sMB.equals("1000")) {
                sMB = "3";
            } else if (sMB.equals("100")) {
                sMB = "5";
            } else if (sMB.equals("10")) {
                sMB = "7";
            } else if (sMB.equals("1")) {
                sMB = "9";
            } else if (sMB.equals("0.1")) {
                sMB = "B";
            } else if (sMB.equals("0.01")) {
                sMB = "D";
            } else if (sMB.equals("0.001")) {
                sMB = "F";
            }
        }
        return sMB + sZSSjz; //和取到的三位有效数字组合后就返回结果
    }

    public static String FloatToBCD(String sFloat, String sSJGS) {
        String sDataContent = "", sZS = "", sXS = "";
        int iLenZS = 0, iLenXS = 0;
        try {
            try {
                String sSign = "";
                if (sSJGS.substring(0, 1).equals("C")) { //带符号的浮点数:负数带负号,整数不需要带正号
                    if (sFloat.substring(0, 1).equals("-")) { //负数
                        sFloat = sFloat.substring(1, sFloat.length());
                        sSign = "-";
                    } else {
                        sSign = "+";
                    }
                } else if (sSJGS.equals("Q")) {
                    if (sFloat.substring(0, 1).equals("+")) { //正数
                        sSign = "00";
                    } else {
                        sSign = "55";
                    }
                    sSJGS = "000000.00";
                }
                int iPos = sSJGS.indexOf('.', 0);
                if (iPos != -1) { //格式带小数点
                    iLenZS = (sSJGS.substring(0, iPos)).length();
                    iLenXS = (sSJGS.substring(iPos + 1, sSJGS.length())).length();
                    iPos = sFloat.indexOf('.', 0);
                    if (iPos != -1) { //数据带小数点
                        sZS = StrStuff("0", iLenZS, sFloat.substring(0, iPos),
                                       10);
                        sXS = StrStuff("0", iLenXS,
                                       sFloat.substring(iPos + 1, sFloat.length()),
                                       20);
                        System.out.println((sZS));
                        System.out.println((sXS));
                    } else { //没按格式带小数点
                        sZS = StrStuff("0", iLenZS, sFloat, 10);
                        sXS = StrStuff("0", iLenXS, sXS, 20);
                    }
                    sDataContent = sZS + sXS;
                } else { //格式不带小数点
                    iPos = sFloat.indexOf('.', 0);
                    iLenZS = sSJGS.length();
                    if (iPos != -1) { //数据带小数点
                        sZS = StrStuff("0", iLenZS, sFloat.substring(0, iPos),
                                       10);
                    } else { //数据不带小数点
                        sZS = StrStuff("0", iLenZS, sFloat, 10);
                    }
                    sDataContent = sZS;
                }
                if (sSJGS.equals("000000.00")) {
                    sDataContent = sSign + sDataContent;
                } else {
                    if (sSign.equals("-")) { //第一个字节最高位置1
                        sSign = Integer.toString((Integer.parseInt(sDataContent.
                                substring(0, 1), 16) | 8));
                        sDataContent = sSign +
                                       sDataContent.substring(1,
                                sDataContent.length());
                    } else if (sSign.equals("+")) { //第一个字节最高位置0
                        sSign = Integer.toString((Integer.parseInt(sDataContent.
                                substring(0, 1), 16) & 7));
                        sDataContent = sSign +
                                       sDataContent.substring(1,
                                sDataContent.length());
                    }
                }
            } catch (Exception e) {
            	Glu_ConstDefine.Log1.WriteLog("Fun(FloatToBCD) Error" + e.toString());
            }
        } finally {
        }
        return sDataContent;
    }

    public static String IntToHex(String sInt, String sSJGS) { //十进制转换成十六进制
        String sDataContent = "";
        try {
            try {
                sInt = (Integer.toString(Integer.parseInt(sInt), 16)).
                       toUpperCase(); //Integer.toString(int,16):十进制整型转换成十六进制(小写字母)
                sDataContent = StrStuff("0", sSJGS.length(), sInt, 10);
            } catch (Exception e) {
            	Glu_ConstDefine.Log1.WriteLog("Fun(IntToHex) Error:" + e.toString());
            }
        } finally {
        }
        return sDataContent;
    }

    public static String StringToHex(String sStr, String sSJGS) { //字符串转换成十六进制
        String sDataContent = "";
        try {
            try {
                sDataContent = StrStuff("0", Integer.parseInt(sSJGS) * 2, sStr,
                                        20);
            } catch (Exception e) {
            	Glu_ConstDefine.Log1.WriteLog("Fun(StringToHex) Error:" + e.toString());
            }
        } finally {
        }
        return sDataContent;

    }

    /*--------------------------------物理数据转换成逻辑数据--------------------------------------------*/
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

    public static String HexToString(String sHex, String sSJGS) { //十六进制转换成字符串
        String sDataContent = "";
        try {
            try {
                int iLenHex = sHex.length();
                
                if (((Integer.parseInt(sSJGS) * 2) == iLenHex) &&
                    ((iLenHex % 2) == 0)) { //长度合法判断
                    sDataContent = sHex;
                } else if (sSJGS.equals("0")){
                	sDataContent = sHex;
                } else {
                    sDataContent = "-1"; //无效值
                }
            } catch (Exception e) {
            	Glu_ConstDefine.Log1.WriteLog("Fun(HexToString) Error:" + e.toString());
            }
        } finally {
        }
        return sDataContent;
    }

    public static String HexToInt(String sHex, String sSJGS) { //十六进制转换成十进制
        String sDataContent = "";
        long itemp = 0;
        try {
            try {
                int iLenHex = sHex.length();
                int iLenSJGS = sSJGS.length();
                if ((iLenHex == iLenSJGS) && ((iLenHex % 2) == 0)) {
                    for (int i = 0; i < iLenHex / 2; i++) {
                        itemp = itemp * 256 +
                                Integer.parseInt(sHex.substring(i * 2,
                                i * 2 + 2), 16); //Integer.parseInt("AB",16):一个字节的十六进制转换成十进制
                    }
                    sDataContent = "" + itemp;
                } else {
                    sDataContent = "-1"; //无效值
                }
            } catch (Exception e) {
            	Glu_ConstDefine.Log1.WriteLog("Fun(HexToInt) Error:" + e.toString());
            }
        } finally {
        }
        return sDataContent;
    }
    public static String HexToIntDot(String sHex, String sSJGS) { //十六进制转换成十进制，转换后加小数点
        String sDataContent = "";
        long itemp = 0;
        try {
            try {
                int iLenHex = sHex.length();
                int iLenSJGS = sSJGS.length();
                if (sSJGS.indexOf('.', 0) != -1){
                	iLenSJGS = iLenSJGS - 1;
                }
                if ((iLenHex == iLenSJGS) && ((iLenHex % 2) == 0)) {
                    for (int i = 0; i < iLenHex / 2; i++) {
                        itemp = itemp * 256 +
                                Integer.parseInt(sHex.substring(i * 2,
                                i * 2 + 2), 16); //Integer.parseInt("AB",16):一个字节的十六进制转换成十进制
                    }
                    sDataContent = "" + itemp;
                } else {
                    sDataContent = "-1"; //无效值
                }
                if (sSJGS.indexOf('.', 0)>0){
                	int iDotPos = sSJGS.indexOf('.', 0);
                	iDotPos = sSJGS.length() - iDotPos - 1;
                	if (sDataContent.length()<=iDotPos){
                		sDataContent = "0." + sDataContent.substring(sDataContent.length() - iDotPos, sDataContent.length());
                	}else {
                		sDataContent = sDataContent.substring(0, sDataContent.length() - iDotPos) + "." + sDataContent.substring(sDataContent.length() - iDotPos, sDataContent.length());
                	}
                }
                
            } catch (Exception e) {
            	Glu_ConstDefine.Log1.WriteLog("Fun(HexToInt) Error:" + e.toString());
            }
        } finally {
        }
        return sDataContent;
    }
    public static String HexToBitFloat(String sHex, String sSJGS) { //十六进制转换成浮点数
        String sDataContent = "";
        try {
            try {
                int iLenHex = sHex.length();
                int iLenSJGS = sSJGS.length();
                if (sSJGS.indexOf('.', 0) != -1){
                	iLenSJGS = iLenSJGS - 1;
                }
                if ((iLenHex == iLenSJGS) && ((iLenHex % 2) == 0)) {
                    int iNum = Integer.parseInt(sHex, 16);
                    Float fDataContent = Float.intBitsToFloat(iNum);   
                    if (sSJGS.indexOf('.', 0)>0){
                    	int iDotPos = sSJGS.indexOf('.', 0);
                    	iLenSJGS = sSJGS.length();
                    	sDataContent = String.format("%."+(iLenSJGS-iDotPos-1)+"f", fDataContent);
                    }
                } else {
                    sDataContent = "-1"; //无效值
                }   
            } catch (Exception e) {
            	Glu_ConstDefine.Log1.WriteLog("Fun(HexToFloatBit) Error:" + e.toString());
            }
        } finally {
        }
        return sDataContent;
    }
    public static String BCDToDateTime(String sBCD, String sSJGS) { //BCD码转换成时间,对应数据格式:20,80
        String sDataContent = "";
        String sDate = "", sTime = "", sGSTemp = "", sDataTemp = "";
        try {
            try {
                if (sSJGS.equals("B")) { //带星期的定长时间型,对应类型格式80
                    String sWeek = "" +
                                   (Integer.parseInt(sBCD.substring(2, 3), 16) >>
                                    1); //得到星期
                    sBCD = sBCD.substring(0, 2) +
                           ("" +
                            (Integer.parseInt(sBCD.substring(2, 3), 16) & 1)) +
                           sBCD.substring(3, sBCD.length());
                    sDataContent = "20" + sBCD.substring(0, 2) + "-" +
                                   sBCD.substring(2, 4) + "-" +
                                   sBCD.substring(4, 6) + " " +
                                   sBCD.substring(6, 8) + ":" +
                                   sBCD.substring(8, 10) + ":" +
                                   sBCD.substring(10, 12) + "#" + sWeek;
                } else { //普通时间型,对应类型格式20
                    int iLenBCD = sBCD.length();
                    int iLenSJGS = sSJGS.length();
                    if ((iLenBCD == iLenSJGS) && ((iLenBCD % 2) == 0)) { //长度合法判断
                        for (int i = 0; i < iLenBCD / 2; i++) {
                            sDataTemp = sBCD.substring(i * 2, i * 2 + 2);
                            sGSTemp = sSJGS.substring(i * 2, i * 2 + 2);
                            if ((sGSTemp.toUpperCase()).equals("YY")) { //年
                                sDate = sDate + sDataTemp;
                            } else if ((sGSTemp.toUpperCase()).equals("MM")) { //月
                                sDate = sDate + '-' + sDataTemp;
                            } else if ((sGSTemp.toUpperCase()).equals("DD")) { //日
                                sDate = sDate + '-' + sDataTemp;
                            } else if ((sGSTemp.toUpperCase()).equals("HH")) { //时
                                sTime = sTime + sDataTemp;
                            } else if ((sGSTemp.toUpperCase()).equals("NN")) { //分
                                sTime = sTime + ':' + sDataTemp;
                            } else if ((sGSTemp.toUpperCase()).equals("SS")) { //秒
                                sTime = sTime + ':' + sDataTemp;
                            }
                        }
                        if (sDate.length() > 0) {
                            if ((sDate.substring(0, 1).toUpperCase()).equals(
                                    "-")) {
                                sDate = sDate.substring(1, sDate.length()); //如果没有年消去‘-’
                            }
                        }
                        if (sTime.length() > 0) {
                            if ((sTime.substring(0, 1).toUpperCase()).equals(
                                    ":")) {
                                sTime = sTime.substring(1, sTime.length()); //如果没有小时消去‘:
                            }
                        }
                        sDataContent = (sDate + ' ' + sTime).trim();
                    } else {
                        sDataContent = "-1"; //无效值
                    }
                }
            } catch (Exception e) {
            	Glu_ConstDefine.Log1.WriteLog("Fun(BCDToDateTime) Error:" + e.toString());
            }
        } finally {
        }
        return sDataContent;
    }
    public static String HexToDateTime(String sSource, String sSJGS) { //Hex码转换成时间,对应数据格式:20
        String sDataContent = "";
        String sDate = "", sTime = "", sGSTemp = "", sDataTemp = "";
        try {
            try {       
                int iLenBCD = sSource.length();
                int iLenSJGS = sSJGS.length();
                if ((iLenBCD >= iLenSJGS) && ((iLenBCD % 2) == 0)) { //长度合法判断
                    while (sSJGS.length() > 0){
                        sDataTemp = sSource.substring(0, 2);
                        sGSTemp = sSJGS.substring(0, 2);
                        if ((sGSTemp.toUpperCase()).equals("YY")) { //年
                        	if ((sSJGS.substring(0, 4).toUpperCase()).equals("YYYY")){
                        		sDataTemp = "" + Integer.parseInt(sSource.substring(0, 4), 16);
                            	if (sDataTemp.equals("65535")){
                            		sDate = sDate + "FFFF";
                            	}else{
                            		sDate = sDate + StrStuff("0", 4, sDataTemp, 10);
                            	}
                                sSource = sSource.substring(4, sSource.length());
                                sSJGS = sSJGS.substring(4, sSJGS.length());
                        	}else{
                        		sDataTemp = "13" + Integer.parseInt(sSource.substring(0, 2), 16);
                            	if (sDataTemp.equals("65535")){
                            		sDate = sDate + "FFFF";
                            	}else{
                            		sDate = sDate + StrStuff("0", 4, sDataTemp, 10);
                            	}
                                sSource = sSource.substring(2, sSource.length());
                                sSJGS = sSJGS.substring(2, sSJGS.length());
                        	}
                        	
                        } else if ((sGSTemp.toUpperCase()).equals("MM")) { //月
                        	sDataTemp = "" + Integer.parseInt(sDataTemp, 16);
                        	if (sDataTemp.equals("255")){
                        		sDate = sDate + '-' + "FF";
                        	}else{
                        		sDate = sDate + '-' + StrStuff("0", 2, sDataTemp, 10);
                        	}
                            sSource = sSource.substring(2, sSource.length());
                            sSJGS = sSJGS.substring(2, sSJGS.length());
                        } else if ((sGSTemp.toUpperCase()).equals("DD")) { //日
                        	sDataTemp = "" + Integer.parseInt(sDataTemp, 16);
                        	if (sDataTemp.equals("255")){
                        		sDate = sDate + '-' + "FF";
                        	}else{
                        		sDate = sDate + '-' + StrStuff("0", 2, sDataTemp, 10);
                        	}
                            sSource = sSource.substring(2, sSource.length());
                            sSJGS = sSJGS.substring(2, sSJGS.length());
                        } else if ((sGSTemp.toUpperCase()).equals("HH")) { //时
                        	sDataTemp = "" + Integer.parseInt(sDataTemp, 16);
                        	if (sDataTemp.equals("255")){
                        		sTime = sTime + "FF";
                        	}else{
                        		sTime = sTime + StrStuff("0", 2, sDataTemp, 10);
                        	}
                            sSource = sSource.substring(2, sSource.length());
                            sSJGS = sSJGS.substring(2, sSJGS.length());
                        } else if ((sGSTemp.toUpperCase()).equals("NN")) { //分
                        	sDataTemp = "" + Integer.parseInt(sDataTemp, 16);
                        	if (sDataTemp.equals("255")){
                        		sTime = sTime + ':' + "FF";
                        	}else{
                        		sTime = sTime + ':' + StrStuff("0", 2, sDataTemp, 10);
                        	}
                            sSource = sSource.substring(2, sSource.length());
                            sSJGS = sSJGS.substring(2, sSJGS.length());
                        } else if ((sGSTemp.toUpperCase()).equals("SS")) { //秒
                        	sDataTemp = "" + Integer.parseInt(sDataTemp, 16);
                        	if (sDataTemp.equals("255")){
                        		sTime = sTime + ':' + "FF";
                        	}else{
                        		sTime = sTime + ':' + StrStuff("0", 2, sDataTemp, 10);
                        	}
                            sSource = sSource.substring(2, sSource.length());
                            sSJGS = sSJGS.substring(2, sSJGS.length());
                        } else {
                            sSource = sSource.substring(2, sSource.length());
                            sSJGS = sSJGS.substring(2, sSJGS.length());
                        }
                    }
                    if (sDate.length() > 0) {
                        if ((sDate.substring(0, 1).toUpperCase()).equals(
                                "-")) {
                            sDate = sDate.substring(1, sDate.length()); //如果没有年消去‘-’
                        }
                    }
                    if (sTime.length() > 0) {
                        if ((sTime.substring(0, 1).toUpperCase()).equals(
                                ":")) {
                            sTime = sTime.substring(1, sTime.length()); //如果没有小时消去‘:
                        }
                    }
                    sDataContent = (sDate + ' ' + sTime).trim();
                } else {
                    sDataContent = "-1"; //无效值
                }
         
            } catch (Exception e) {
            	Glu_ConstDefine.Log1.WriteLog("Fun(HexToDateTime) Error:" + e.toString());
            }
        } finally {
        }
        return sDataContent;
    }

    public static String BCDToFloat(String sBCD, String sSJGS) { //BCD码转换成浮点数,对应数据类型：10,60,70,90
        String sDataContent = "";
        String sZS = "";
        String sXS = "";
        //String sTemp = "";
        try {
            try {
            	for(int i = 0; i < sBCD.length(); i++){
            		int iBCD = Integer.parseInt(DataSwitch.HexToInt("0"+sBCD.substring(i,i+1),"00"));
            		if ( iBCD > 9 || iBCD < 0){
            			sDataContent = "-1";
            			return sDataContent;
            		}
            	}
                String sSign = "";
                if (sSJGS.substring(0, 1).equals("A")) { //带符号和幂部的定长浮点型,对应数据类型：10(国网格式2)
                    sSign = sBCD.substring(0, 1); //幂部、符号高半字节
                    int iMB = Integer.parseInt(sSign, 16) & 14; //高三位为幕部
                    if ((Integer.parseInt(sSign, 16) & 1) == 1) { //最低位等于1为负数
                        sSign = "-";
                    } else {
                        sSign = ""; //正数不带正号
                    }
                    float iBCD = Integer.parseInt(sBCD.substring(1, sBCD.length())); //取整数部分
                    switch (iMB) {
                    case 0: {
                        iBCD = iBCD * 10000;
                        break;
                    }
                    case 2: {
                        iBCD = iBCD * 1000;
                        break;
                    }
                    case 4: {
                        iBCD = iBCD * 100;
                        break;
                    }
                    case 6: {
                        iBCD = iBCD * 10;
                        break;
                    }
                    case 8: {
                        iBCD = iBCD * 1;
                        break;
                    }
                    case 10: {
                        iBCD = iBCD / 10;
                        break;
                    }
                    case 12: {
                        iBCD = iBCD / 100;
                        break;
                    }
                    case 14: {
                        iBCD = iBCD / 1000;
                        break;
                    }
                    }
                    sDataContent = sSign + "" + iBCD;
                } else if (sSJGS.substring(0, 1).equals("E")) { //带符号和单位的定长整型,对应数据类型:90(国网格式3)
                    sSign = sBCD.substring(0, 1); //单位、符号高半字节
                    int iUnit = (Integer.parseInt(sSign, 16) & 4) >> 2; //第三位为单位标志
                    if ((Integer.parseInt(sSign, 16) & 1) == 1) { //最低位等于1为负数
                        sSign = "-";
                    } else {
                        sSign = "+"; //正数不带正号
                    }
                    sDataContent = ("" + iUnit) + sSign + sBCD.substring(1, 8); //例如:0+1234567
                } else { //对应数据类型：10,70
                    if (sSJGS.substring(0, 1).equals("C")) { //带符号的浮点数:负数带负号,正数不需要带正号
                        sSign = sBCD.substring(0, 1); //符号高半字节
                        if ((Integer.parseInt(sSign, 16) & 8) == 8) { //最高位等于1为负数
                            sSign = Integer.toString((Integer.parseInt(sSign,
                                    16) & 7)); //消去符号位
                            sBCD = sSign + sBCD.substring(1, sBCD.length());
                            sSign = "-";
                        }
                    } else if (sSJGS.substring(0, 1).equals("S")) { //数据类型为10的带符号格式(主要出现在浙规)
                        if (sBCD.substring(0, 1).equals("1")) { //负号
                            sSign = "-1";
                            sBCD = "0" + sBCD.substring(1, sBCD.length()); //消去符号位
                        }
                    } else if (sSJGS.equals("Q")) {
                        if (sBCD.substring(0, 1).equals("0")) { //正数
                            sSign = "+";
                        } else {
                            sSign = "-";
                        }
                        sSJGS = "000000.00";
                        sBCD = sBCD.substring(2);
                    }

                    int iPos = sSJGS.indexOf('.', 0);
                    int iLenBCD = sBCD.length();
                    if (iPos != -1) { //数据格式带小数点
                        int iLenSJGS = sSJGS.length() - 1;
                        if ((iLenBCD == iLenSJGS) && ((iLenBCD % 2) == 0)) { //长度合法判断
                            sZS = sBCD.substring(0, iPos); //整数部分
                            if (iPos == 0) {
                                sZS = "0";
                            }
                            sXS = sBCD.substring(iPos, iLenBCD); //小数部分
                            sDataContent = sZS + "." + sXS;
                        } else {
                            sDataContent = "-1"; //无效值
                        }
                    } else { //数据格式为整数
                        int iLenSJGS = sSJGS.length();
                        if ((iLenBCD == iLenSJGS) && ((iLenBCD % 2) == 0)) { //长度合法判断
                            sDataContent = sBCD;
                        } else {
                            sDataContent = "-1"; //无效值
                        }
                    }
                    if (sSign.equals("-") && !sDataContent.equals("-1")) { //为负数且数据值有效
                        sDataContent = sSign + sDataContent;
                    }
                }

            } catch (Exception e) {
            	Glu_ConstDefine.Log1.WriteLog("Fun(BCDToFloat) Error:" + e.toString());
            }
        } finally {
        }
        return sDataContent;
    }

    public static String Fun8BinTo2Hex(String sBit8) { //8位2进制字符转化为1个字节的十六进制
        String sResult = "";
        String sTemp = "";
        try {
            try {
                for (int i = 0; i < 2; i++) {
                    sTemp = sBit8.substring(i * 4, i * 4 + 4);
                    if (sTemp.equals("0000")) {
                        sTemp = "0";
                    }
                    if (sTemp.equals("0001")) {
                        sTemp = "1";
                    }
                    if (sTemp.equals("0010")) {
                        sTemp = "2";
                    }
                    if (sTemp.equals("0011")) {
                        sTemp = "3";
                    }
                    if (sTemp.equals("0100")) {
                        sTemp = "4";
                    }
                    if (sTemp.equals("0101")) {
                        sTemp = "5";
                    }
                    if (sTemp.equals("0110")) {
                        sTemp = "6";
                    }
                    if (sTemp.equals("0111")) {
                        sTemp = "7";
                    }
                    if (sTemp.equals("1000")) {
                        sTemp = "8";
                    }
                    if (sTemp.equals("1001")) {
                        sTemp = "9";
                    }
                    if (sTemp.equals("1010")) {
                        sTemp = "A";
                    }
                    if (sTemp.equals("1011")) {
                        sTemp = "B";
                    }
                    if (sTemp.equals("1100")) {
                        sTemp = "C";
                    }
                    if (sTemp.equals("1101")) {
                        sTemp = "D";
                    }
                    if (sTemp.equals("1110")) {
                        sTemp = "E";
                    }
                    if (sTemp.equals("1111")) {
                        sTemp = "F";
                    }
                    sResult = sResult + sTemp;
                }
            } catch (Exception e) {
            	Glu_ConstDefine.Log1.WriteLog("Fun(Fun8BinTo2Hex) Error:" + e.toString());
            }
        } finally {
        }
        return sResult;
    }

    public static String Fun2HexTo8Bin(String sBit8) { //将一个字节16进制数转换成8位的二进制字符
        String sResult = "";
        String sTemp = "";
        try {
            try {
                for (int i = 0; i < sBit8.length(); i++) {
                    sTemp = sBit8.substring(i, 1 + i);
                    if (sTemp.toUpperCase().equals("0")) {
                        sTemp = "0000";
                    }
                    if (sTemp.toUpperCase().equals("1")) {
                        sTemp = "0001";
                    }
                    if (sTemp.toUpperCase().equals("2")) {
                        sTemp = "0010";
                    }
                    if (sTemp.toUpperCase().equals("3")) {
                        sTemp = "0011";
                    }
                    if (sTemp.toUpperCase().equals("4")) {
                        sTemp = "0100";
                    }
                    if (sTemp.toUpperCase().equals("5")) {
                        sTemp = "0101";
                    }
                    if (sTemp.toUpperCase().equals("6")) {
                        sTemp = "0110";
                    }
                    if (sTemp.toUpperCase().equals("7")) {
                        sTemp = "0111";
                    }
                    if (sTemp.toUpperCase().equals("8")) {
                        sTemp = "1000";
                    }
                    if (sTemp.toUpperCase().equals("9")) {
                        sTemp = "1001";
                    }
                    if (sTemp.toUpperCase().equals("A")) {
                        sTemp = "1010";
                    }
                    if (sTemp.toUpperCase().equals("B")) {
                        sTemp = "1011";
                    }
                    if (sTemp.toUpperCase().equals("C")) {
                        sTemp = "1100";
                    }
                    if (sTemp.toUpperCase().equals("D")) {
                        sTemp = "1101";
                    }
                    if (sTemp.toUpperCase().equals("E")) {
                        sTemp = "1110";
                    }
                    if (sTemp.toUpperCase().equals("F")) {
                        sTemp = "1111";
                    }
                    sResult = sResult + sTemp;
                }
            } catch (Exception e) {
            	Glu_ConstDefine.Log1.WriteLog("Fun(Fun2HexTo8Bin) Error:" + e.toString());
            }
        } finally {
        }
        return sResult;
    }

    public static String IncreaseDateTime(String sDateTime, int iIncreaseNo,
                                          int iIncreaseType) { //把输入的字符串型的时间累加分、小时、日、月,相应IncreaseType：2,3,4,5
        String sResult = "";
        try {
            if (iIncreaseNo != 0) {
                Calendar DateTime = Calendar.getInstance();
                //时间格式:YYYYMMDDHHNNSS
                DateTime.set(Integer.parseInt(sDateTime.substring(0, 4)),
                             Integer.parseInt(sDateTime.substring(4, 6)) - 1,
                             Integer.parseInt(sDateTime.substring(6, 8)),
                             Integer.parseInt(sDateTime.substring(8, 10)),
                             Integer.parseInt(sDateTime.substring(10, 12)),
                             Integer.parseInt(sDateTime.substring(12, 14)));
                SimpleDateFormat formatter = new SimpleDateFormat(
                        "yyyy-MM-dd HH:mm:ss");
                //DateFormat df= DateFormat.getDateTimeInstance();
                try {
                    //DateTime=df.parse(sDateTime);
                    switch (iIncreaseType) {
                    case 2:
                        DateTime.add(Calendar.MINUTE, iIncreaseNo);
                        //DateTime.setMinutes(DateTime.getMinutes()+iIncreaseNo);//累加分钟
                        break;
                    case 3:
                        DateTime.add(Calendar.HOUR, iIncreaseNo);

                        //DateTime.setHours(DateTime.getHours()+iIncreaseNo);    //累加小时
                        break;
                    case 4:
                        DateTime.add(Calendar.DATE, iIncreaseNo);

                        //DateTime.setDate(DateTime.getDate()+iIncreaseNo);      //累加日
                        break;
                    case 5:
                        DateTime.add(Calendar.MONTH, iIncreaseNo);

                        //DateTime.setMonth(DateTime.getMonth()+iIncreaseNo);    //累加月
                        break;
                    }
                    sResult = formatter.format(DateTime.getTime());
                    //sResult=df.format(DateTime);
                    //格式化时间:YYYYMMDDHHNNSS
                    sResult = sResult.substring(0, 4) + sResult.substring(5, 7) +
                              sResult.substring(8, 10) +
                              sResult.substring(11, 13) +
                              sResult.substring(14, 16) +
                              sResult.substring(17, 19);
                } catch (Exception e) {
                	Glu_ConstDefine.Log1.WriteLog("Fun(IncreaseDateTime) Error:" + e.toString());
                }
            } else {
                sResult = sDateTime;
            }
        } finally {
        }
        return sResult;
    }

    public int getCount(Date StartTime, int Frequency) { //理论次数=(当前时间-开始时间)/频率
        int sResult = 0;
        try {
            try {
                Date NowDate = new Date();
                double dTemp = NowDate.getTime() - StartTime.getTime(); //毫秒
                int iTemp = (int) (dTemp / 3600000);
                sResult = (int) (iTemp / Frequency);
            } catch (Exception e) {
            	Glu_ConstDefine.Log1.WriteLog("Fun(getCount) Error:" + e.toString());
            }
        } finally {
        }
        return sResult;
    }
}

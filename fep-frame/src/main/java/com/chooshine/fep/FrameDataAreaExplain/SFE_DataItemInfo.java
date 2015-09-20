package com.chooshine.fep.FrameDataAreaExplain;

import com.chooshine.fep.ConstAndTypeDefine.Glu_ConstDefine;

public class SFE_DataItemInfo {//数据项信息
    //存储方式
    private final int CCFS_BCD=10 ; //BCD方式
    private final int CCFS_HEX=20 ; //十六进制方式
    private final int CCFS_ASC=30 ; //ASCII码方式

    //存储顺序
    //private final int CCSX_SX=10;  //顺序存储
    private final int CCSX_NX=20;  //逆序存储

    //数据类型
    private final int c_Float=10;//浮点型
    private final int c_FloatHex = 11; //Hex浮点型
    private final int c_FloatLen = 12; //带长度的Hex浮点型
    private final int c_FloatNoLx = 13; //不带类型的Hex浮点型
    private final int c_FloatBit = 14; //浮点数（DLMS用）
    private final int c_DateTime  =20;//日期型
    private final int c_DateTimeHex = 21; //Hex日期型
    private final int c_DateTimeNoLx = 22; //不带类型Hex日期型
    private final int c_String    =30;//字符型
    private final int c_StringNoLen = 31; //不带长度字符型
    private final int c_NOLenStr  =40;//不定长字符型
    private final int c_complex   =50;//复合数据类型
    private final int c_complexEX =51;//带类型的复合数据类型（结构数组里面的结构数组元素）
    private final int c_FloatEx   =60;//特殊浮点型（对应国网规范格式2,带幂部）
    private final int c_FloatFH1  =61;//带正负号的浮点型 (00为正号或是上浮，55为负号或是下浮)
    private final int c_FloatFH   =70;//带正负号的浮点型 (0为正号或是上浮，1为负号或是下浮)
    private final int c_DateTimeXQ=80;//带星期的日期型2004-10-10 10：10：10#4
    private final int c_IntDWFH   =90;//带单位标志和正负号的整型值串
    private final int c_IntZJDWFH =91;//带追加,单位标志和正负号的整型值串 8月22号增加

    private String FDataCaption="";  //数据项标识
    private int FDataType=0;        //数据类型
    private int FDataLen=0;         //数据字节长度
    private String FDataFormat="";  //数据项数据格式
    private int FStorageType=0;     //存储方式（10 BCD;20 HEX;30 ASCII）
    private int FStorageOrder=0;    //存储顺序（10顺序  20逆序）
    private String FLogicData="";   //逻辑数据内容（如12.34）
    private String FPhysicalData="";//物理数据内容(如BCD:3412,Hex:220C)

    public SFE_DataItemInfo(){
    }
    public void DataItemInfoAdd(String sDataCaption,int iDataType,
            int iDataLen,String sDataFormat,int iStorageType,
            int iStorageOrder){
       FDataCaption=sDataCaption;
       FDataType=iDataType;
       FDataLen=iDataLen;
       FDataFormat=sDataFormat;
       FStorageType=iStorageType;
       FStorageOrder=iStorageOrder;
    }

    public String GetFDataCaption(){
      return this.FDataCaption;
    }
    public int GetFDataType(){
      return this.FDataType;
    }
    public int GetFDataLen(){
      return this.FDataLen;
    }
    public String GetFDataFormat(){
      return this.FDataFormat;
    }
    public int GetFStorageType(){
      return this.FStorageType;
    }
    public int GetFStorageOrder(){
      return this.FStorageOrder;
    }
    public String GetFLogicData(){
      return this.FLogicData;
    }
    public String GetFPhysicalData(){
      return this.FPhysicalData;
    }
    public void SetFLogicData(String sLogicData){
      FLogicData=sLogicData;
    }
    public void SetFPhysicalData(String sPhysicalData){
      FPhysicalData=sPhysicalData;
    }
    public boolean PhysicalDataToLogicData(){//物理数据转换成逻辑数据
      boolean Result=false;
      FLogicData="-1";
      try{
        try{
          if (FStorageOrder==CCSX_NX){//存储顺序如果是逆序
            FPhysicalData=DataSwitch.ReverseStringByByte(FPhysicalData);
          }
          if (PhysicalDataLegalCheck(FPhysicalData,FDataType,FDataLen,FStorageType)||(FDataFormat.toUpperCase().equals("X"))){//合法性检验
            if (FStorageType==CCFS_BCD){//BCD码
              switch (FDataType) {
                case c_Float:FLogicData=DataSwitch.BCDToFloat(FPhysicalData,FDataFormat);
                  break;
                case c_DateTime:FLogicData=DataSwitch.BCDToDateTime(FPhysicalData,FDataFormat);
                  break;
                case c_complex:FLogicData=ExplainComplexDataType(FPhysicalData,FDataFormat);
                  break;
                case c_String:FLogicData=DataSwitch.HexToString(FPhysicalData,FDataFormat);
                  break;
                case c_FloatEx:FLogicData=DataSwitch.BCDToFloat(FPhysicalData,FDataFormat);
                  break;
                case c_FloatFH1:
                case c_FloatFH:FLogicData=DataSwitch.BCDToFloat(FPhysicalData,FDataFormat);
                  break;
                case c_DateTimeXQ:FLogicData=DataSwitch.BCDToDateTime(FPhysicalData,FDataFormat);
                  break;
                case c_IntDWFH:FLogicData=DataSwitch.BCDToFloat(FPhysicalData,FDataFormat);
                  break;
                case c_NOLenStr:FLogicData=FPhysicalData;
                  break;
                default:FLogicData="-1";
              }
            }
            else if (FStorageType==CCFS_HEX){//HEX码
              switch (FDataType) {
                case c_Float:FLogicData=DataSwitch.HexToInt(FPhysicalData,FDataFormat);
                  break;
                case c_FloatHex:
                case c_FloatLen:
                case c_FloatNoLx:FLogicData=DataSwitch.HexToIntDot(FPhysicalData,FDataFormat); 
                  	break;
                case c_FloatBit: FLogicData=DataSwitch.HexToBitFloat(FPhysicalData,FDataFormat); 
                	break; 
                case c_DateTimeHex:
                case c_DateTimeNoLx:FLogicData=DataSwitch.HexToDateTime(FPhysicalData,FDataFormat);
                  	break;
                case c_String:FLogicData=DataSwitch.HexToString(FPhysicalData,FDataFormat);
                	break;
                case c_StringNoLen:FLogicData=DataSwitch.HexToString(FPhysicalData,FDataFormat);
                	break;
                case c_complex:FLogicData=ExplainComplexDataType(FPhysicalData,FDataFormat);
                  	break;
                case c_complexEX:FLogicData=ExplainComplexEXDataType(FPhysicalData,FDataFormat);
                	break; 
                case c_NOLenStr:FLogicData=FPhysicalData;
                  	break;
                default:FLogicData="-1";
              }
            }
            else if (FStorageType==CCFS_ASC){//HEX码
              FLogicData=DataSwitch.HexASCIIToString(FPhysicalData);
            }
          }
          Result=true;
        }
        catch(Exception e){
          System.out.println("数据区解析PhysicalDataToLogicData出错:"+e.toString());
          Glu_ConstDefine.Log1.WriteLog("报错函数:数据区解析SFE_DataItemInfo__PhysicalDataToLogicData();报错原因:"+e.toString());
          Result=false;
        }
      }
      finally{
      }
      return Result;
    }
    public String ExplainComplexDataType(String sData,String DataFormat){
      String sResult="";
      try{
        int iPos=0;
        String sSJGS="";
        String sTemp="";
        String sSJGSJ="";
        String sPhysicalData="";
        String sLogicData="";
        int iDataType=0;
        int iStorageType=0;
        int iStorageOrder=0;
        int iDataLen=0;
        while(DataFormat.length()>0){
          try{
            //分解复合数据格式得到：数据格式，存储类型，存储顺序，数据长度
            iPos=DataFormat.indexOf('#',0);
            if (iPos!=-1){
              sSJGSJ=DataFormat.substring(0,iPos);//取当前的数据格式集：数据格式，存储类型，存储顺序
              DataFormat=DataFormat.substring(iPos+1,DataFormat.length());
            }
            else{//最后一个
              sSJGSJ=DataFormat;
              DataFormat="";
            }
            iPos=sSJGSJ.indexOf(',',0);
            sSJGS=sSJGSJ.substring(0,iPos);//数据格式
            sSJGSJ=sSJGSJ.substring(iPos+1,sSJGSJ.length());
            iPos=sSJGSJ.indexOf(',',0);
            iStorageType=Integer.parseInt(sSJGSJ.substring(0,iPos));//存储类型
            sSJGSJ=sSJGSJ.substring(iPos+1,sSJGSJ.length());
            iStorageOrder=Integer.parseInt(sSJGSJ);//存储顺序
            sTemp=sSJGS.substring(0,1).toUpperCase();
            if (sTemp.equals("0")||sTemp.equals("C")){
              if (sTemp.equals("0")){
                iDataType=c_Float;}
              else {iDataType=c_FloatFH;}
              iPos=sSJGS.indexOf('.',0);
              if (iPos!=-1){//浮点数
                iDataLen=sSJGS.length()-1;
              }//整型
              else{iDataLen=sSJGS.length();}
            }
            else if (sTemp.equals("A")){
              iDataType=c_FloatEx;
              iDataLen=4;
            }
            else if (sTemp.equals("B")){
              iDataType=c_DateTimeXQ;
              iDataLen=12;
            }
            else if (sTemp.equals("E")){
              iDataType=c_IntDWFH;
              iDataLen=8;
            }
            else if (sTemp.equals("Q")){
              iDataType=c_FloatEx;
              iDataLen=10;
            }
            else if (sTemp.equals("Y")||sTemp.equals("M")||sTemp.equals("D")||
                     sTemp.equals("H")||sTemp.equals("N")||sTemp.equals("S")){
              iDataType=c_DateTime;
              iDataLen=sSJGS.length();
            }
            else {
              iDataType=c_String;
              iDataLen=Integer.parseInt(sSJGS)*2;
            }
            //得到转化数据
            sPhysicalData=sData.substring(0,iDataLen);
            if (sData.length()>=iDataLen){
              sData=sData.substring(iDataLen,sData.length());
            }
            else{sData="";}
            if (iStorageOrder==CCSX_NX){//存储顺序如果是逆序
              sPhysicalData=DataSwitch.ReverseStringByByte(sPhysicalData);
            }
            if (PhysicalDataLegalCheck(sPhysicalData,iDataType,iDataLen,iStorageType)){//合法性检验
              if (iStorageType==CCFS_BCD){//BCD码
                switch (iDataType) {
                  case c_Float:sLogicData=DataSwitch.BCDToFloat(sPhysicalData,sSJGS);
                    break;
                  case c_DateTime:sLogicData=DataSwitch.BCDToDateTime(sPhysicalData,sSJGS);
                    break;
                  case c_String:sLogicData=DataSwitch.HexToString(sPhysicalData,sSJGS);
                    break;
                  case c_FloatEx:sLogicData=DataSwitch.BCDToFloat(sPhysicalData,sSJGS);
                    break;
                  case c_FloatFH:sLogicData=DataSwitch.BCDToFloat(sPhysicalData,sSJGS);
                    break;
                  case c_DateTimeXQ:sLogicData=DataSwitch.BCDToDateTime(sPhysicalData,sSJGS);
                    break;
                  case c_IntDWFH:sLogicData=DataSwitch.BCDToFloat(sPhysicalData,sSJGS);
                    break;
                  default:sLogicData="-1";
                }
              }
              else if (iStorageType==CCFS_HEX){//HEX码
                switch (iDataType) {
                  case c_Float:sLogicData=DataSwitch.HexToInt(sPhysicalData,sSJGS);
                    break;
                  case c_String:sLogicData=DataSwitch.HexToString(sPhysicalData,sSJGS);
                    break;
                  default:sLogicData="-1";
                }
              }
              else if (iStorageType==CCFS_ASC){//HEX码
                sLogicData=DataSwitch.HexASCIIToString(sPhysicalData);
              }
            }
            if (sResult.equals("")){
              sResult=sLogicData.trim();
            }
            else{
              sResult=sResult+"#"+sLogicData.trim();
            }
          }
          catch(Exception e){
            System.out.println("数据区解析ExplainComplexDataType出错:"+e.toString());
            Glu_ConstDefine.Log1.WriteLog("报错函数:数据区解析SFE_DataItemInfo__ExplainComplexDataType();报错原因:"+e.toString());
            return sResult="-1";
          }
        }
      }
      finally{
      }
      return sResult;
    }
    public String ExplainComplexEXDataType(String sData,String DataFormat){
        String sResult="";
        try{
          int iPos=0;
          String sSJGS="";
          String sTemp="";
          String sSJGSJ="";
          String sPhysicalData="";
          String sLogicData="";
          int iDataType=0;
          int iStorageType=0;
          int iStorageOrder=0;
          int iDataLen=0;
          if (sData.substring(0,2).equals("02")){
        	  sData = sData.substring(4, sData.length());
          }
          while(DataFormat.length()>0){
            try{
              //分解复合数据格式得到：数据类型，数据格式，存储类型，存储顺序
              iPos=DataFormat.indexOf('#',0);
              if (iPos!=-1){
                sSJGSJ=DataFormat.substring(0,iPos);//取当前的数据格式集：数据类型，数据格式，存储类型，存储顺序
                DataFormat=DataFormat.substring(iPos+1,DataFormat.length());
              }
              else{//最后一个
                sSJGSJ=DataFormat;
                DataFormat="";
              }
              iPos=sSJGSJ.indexOf(',',0);
              iDataType = Integer.parseInt(sSJGSJ.substring(0, iPos));//数据类型
              sSJGSJ=sSJGSJ.substring(iPos+1,sSJGSJ.length());
              iPos=sSJGSJ.indexOf(',',0);
              sSJGS=sSJGSJ.substring(0,iPos);//数据格式
              sSJGSJ=sSJGSJ.substring(iPos+1,sSJGSJ.length());
              iPos=sSJGSJ.indexOf(',',0);
              iStorageType=Integer.parseInt(sSJGSJ.substring(0,iPos));//存储类型
              sSJGSJ=sSJGSJ.substring(iPos+1,sSJGSJ.length());
              iStorageOrder=Integer.parseInt(sSJGSJ);//存储顺序
              sTemp=sSJGS.substring(0,1).toUpperCase();
              if (iDataType == c_FloatHex || iDataType == c_FloatLen || iDataType == c_FloatNoLx || iDataType == c_FloatBit){
            	  iPos=sSJGS.indexOf('.',0);
            	  if (iPos!=-1){//浮点数
            		  iDataLen=sSJGS.length()-1;
            	  }//整型
            	  else{iDataLen=sSJGS.length();}
            	  if (iDataType == c_FloatNoLx){
            		  sData = sData.substring(0, sData.length()); 
            	  }else if (iDataType == c_FloatHex || iDataType == c_FloatBit){
            		  sData = sData.substring(2, sData.length()); 
            	  }else if (iDataType == c_FloatLen){
            		  sData = sData.substring(4, sData.length()); 
            	  }
              }else if (iDataType == c_DateTimeHex){
                  iDataLen=Integer.parseInt(sData.substring(2, 4),16)*2;
                  sData = sData.substring(4, sData.length());
              }
              else if (iDataType == c_String){
                iDataLen=Integer.parseInt(sData.substring(2, 4),16)*2;
                sData = sData.substring(4, sData.length());
              }
              //得到转化数据
              sPhysicalData=sData.substring(0,iDataLen);
              if (sData.length()>=iDataLen){
                sData=sData.substring(iDataLen,sData.length());
              }
              else{sData="";}
              if (iStorageOrder==CCSX_NX){//存储顺序如果是逆序
                sPhysicalData=DataSwitch.ReverseStringByByte(sPhysicalData);
              }
              if (PhysicalDataLegalCheck(sPhysicalData,iDataType,iDataLen,iStorageType)){//合法性检验
                if (iStorageType==CCFS_BCD){//BCD码
                  switch (iDataType) {
                    case c_Float:sLogicData=DataSwitch.BCDToFloat(sPhysicalData,sSJGS);
                      break;
                    case c_DateTime:sLogicData=DataSwitch.BCDToDateTime(sPhysicalData,sSJGS);
                      break;
                    case c_String:sLogicData=DataSwitch.HexToString(sPhysicalData,sSJGS);
                      break;
                    case c_FloatEx:sLogicData=DataSwitch.BCDToFloat(sPhysicalData,sSJGS);
                      break;
                    case c_FloatFH:sLogicData=DataSwitch.BCDToFloat(sPhysicalData,sSJGS);
                      break;
                    case c_DateTimeXQ:sLogicData=DataSwitch.BCDToDateTime(sPhysicalData,sSJGS);
                      break;
                    case c_IntDWFH:sLogicData=DataSwitch.BCDToFloat(sPhysicalData,sSJGS);
                      break;
                    default:sLogicData="-1";
                  }
                }
                else if (iStorageType==CCFS_HEX){//HEX码
                  switch (iDataType) {	
					  case c_Float:sLogicData=DataSwitch.HexToInt(sPhysicalData,sSJGS);
					    	break;
					  case c_FloatHex:
					  case c_FloatLen:
					  case c_FloatNoLx:
						  sLogicData=DataSwitch.HexToIntDot(sPhysicalData,sSJGS); 
						  break;
					  case c_FloatBit:
						  sLogicData=DataSwitch.HexToBitFloat(sPhysicalData,sSJGS); 
						  break;
					  case c_DateTimeHex:sLogicData=DataSwitch.HexToDateTime(sPhysicalData,sSJGS);
					    	break;
					  case c_String:sLogicData=DataSwitch.HexToString(sPhysicalData,sSJGS);
					    	break;
                      default:sLogicData="-1";
                  }
                }
                else if (iStorageType==CCFS_ASC){//HEX码
                  sLogicData=DataSwitch.HexASCIIToString(sPhysicalData);
                }
              }
              if (sResult.equals("")){
                sResult=sLogicData.trim();
              }
              else{
                sResult=sResult+"#"+sLogicData.trim();
              }
            }
            catch(Exception e){
              System.out.println("ExplainComplexEXDataType Error:"+e.toString());
              Glu_ConstDefine.Log1.WriteLog("ErrorFunc:SFE_DataItemInfo__ExplainComplexEXDataType();Error:"+e.toString());
              return sResult="-1";
            }
          }
          SetFPhysicalData(sData);
        }
        finally{
        }
        return sResult;
      }
    public boolean LogicDataToPhysicalData(){//逻辑数据转换成物理数据
      boolean Result=false;
      FPhysicalData="";
      try{
        try{
          if (LogicDataLegalCheck(FDataType,FStorageType,FPhysicalData)){//合法性检验
            if (FStorageType==CCFS_BCD){//BCD码
              switch (FDataType) {
                case c_Float: FPhysicalData=DataSwitch.FloatToBCD(FLogicData,FDataFormat);
                  break;
                case c_DateTime: FPhysicalData=DataSwitch.DateTimeToBCD(FLogicData,FDataFormat);
                  break;
                default: FPhysicalData="-1";
              }
            }
            else if (FStorageType==CCFS_HEX){//HEX码
              switch (FDataType) {
                case c_Float: FPhysicalData=DataSwitch.IntToHex(FLogicData,FDataFormat);
                  break;
                case c_String: FPhysicalData=DataSwitch.HexToString(FLogicData,FDataFormat);
                  break;
                case c_NOLenStr: FPhysicalData=FLogicData;
                  break;
                default: FPhysicalData="-1";
              }
            }
            else if (FStorageType==CCFS_ASC){//HEX码
            }
            if (FStorageOrder==CCSX_NX){//存储顺序如果是逆序
              FPhysicalData=DataSwitch.ReverseStringByByte(FPhysicalData);
            }
            if (FPhysicalData!=""){
              Result=true;
            }
          }
        }
        catch(Exception e){
          System.out.println("数据区解析LogicDataToPhysicalData出错:"+e.toString());
          Glu_ConstDefine.Log1.WriteLog("报错函数:数据区解析SFE_DataItemInfo__LogicDataToPhysicalData();报错原因:"+e.toString());
          Result=false;
        }
      }
      finally{
      }
      return Result;
    }

    public boolean PhysicalDataLegalCheck(String PhysicalData,int DataType,int DataLen,int StorageType){//物理数据合法性检验
      boolean Result=true;
      int iCheck=0;
      try{
        try{
          if((PhysicalData.length()==DataLen*2)||(DataType==c_NOLenStr)){//数据长度校验
            for (int i=0;i<PhysicalData.length();i++){
              iCheck=Integer.parseInt(PhysicalData.substring(i,i+1),16);
              if ((StorageType==CCFS_BCD)&&((iCheck<0)||(iCheck>9))){//存储方式等于BCD码
                if (((DataType==c_FloatEx)||(DataType==c_FloatFH)||(DataType==c_IntZJDWFH))&&(i!=0)) {//第一位是符号位，有可能不是BCD码
                  Result=false;
                  break;
                }
                else if ((DataType==c_DateTimeXQ)&&(i!=2)){//第三位是星期，有可能不是BCD码
                  Result=false;
                  break;
                }
              }
              else if (((StorageType==CCFS_HEX)||(StorageType==CCFS_ASC))&&((iCheck<0)||(iCheck>15))){//存储方式等于Hex码或ASCII
                break;
              }
            }
          }

        }
        catch(Exception e){
          System.out.println("数据区解析PhysicalDataLegalCheck出错:"+e.toString());
          Glu_ConstDefine.Log1.WriteLog("报错函数:数据区解析SFE_DataItemInfo__PhysicalDataLegalCheck();报错原因:"+e.toString());
          Result=false;
        }
      }
      finally{
      }
      return Result;
    }
    public boolean LogicDataLegalCheck(//逻辑数据合法性检验
            int iDataType,int iStorageType,String sLogicData){
      boolean Result=true;
      char cCheck=0;
      //String sCheck="";
      try{
        try{
          for (int i=0;i<sLogicData.length();i++){
            cCheck = sLogicData.toUpperCase().charAt(i);
            if (iDataType == c_Float) {
              if (((cCheck<'0')||(cCheck>'9'))&&(cCheck!='.')){
                return Result;
              }
            }
            else if ((iDataType==c_FloatFH)||(iDataType==c_FloatEx)||(iDataType==c_IntZJDWFH)){
              if (((cCheck<'0')||(cCheck>'9'))&&((cCheck!='.')||(cCheck!='+')||(cCheck!='-'))){
                return Result;
              }
            }
            else if ((iDataType==c_String)){
              if ((iStorageType!=CCFS_ASC)&&((cCheck<'0')||((cCheck>'9')&&(cCheck<'A'))||(cCheck>'F'))){
                return Result;
              }
            }
            else{//时间型和整型
              if ((cCheck<'0')||(cCheck>'9')){
                return Result;
              }
            }
          }
          Result=true;
        }
        catch(Exception e){
          System.out.println("数据区解析LogicDataLegalCheck出错:"+e.toString());
          Glu_ConstDefine.Log1.WriteLog("报错函数:数据区解析SFE_DataItemInfo__LogicDataLegalCheck();报错原因:"+e.toString());
          Result=false;
        }
      }
      finally{
      }
      return Result;
    }
}

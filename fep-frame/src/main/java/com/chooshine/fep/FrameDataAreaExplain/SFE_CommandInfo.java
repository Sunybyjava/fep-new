package com.chooshine.fep.FrameDataAreaExplain;

import java.util.ArrayList;
public class SFE_CommandInfo {//命令信息类
    private String FProtocolCommand="";//规约命令
    private int FProtocolType=0;      //规约号
    private int FDataItemCount=0;     //数据项数目
    private int FCommandLen=0;        //命令中的所有数据项的数据总长
    ArrayList <SFE_DataItemInfo>DataItemInfoList=new ArrayList<SFE_DataItemInfo>(); //数据项信息列表
    public SFE_CommandInfo() {
    }
    public void CommandInfoAdd(String sProtocolCommand,int iProtocolType) {
      FProtocolCommand=sProtocolCommand;
      FProtocolType=iProtocolType;
    }

    public void DataItemInfoAdd(String sDataCaption,int iDataType,
            int iDataLen,String sDataFormat,int iStorageType,
            int iStorageOrder){
      SFE_DataItemInfo DataItemInfo=new SFE_DataItemInfo();
      DataItemInfo.DataItemInfoAdd(sDataCaption,iDataType,iDataLen,sDataFormat,iStorageType,iStorageOrder);
      DataItemInfoList.add(DataItemInfo);
      FCommandLen=FCommandLen+iDataLen;
      FDataItemCount=FDataItemCount+1;
    }
    public String GetFProtocolCommand(){
      return this.FProtocolCommand;
    }
    public int GetFProtocolType(){
      return this.FProtocolType;
    }
    public int GetFDataItemCount(){
      return this.FDataItemCount;
    }
    public int GetFCommandLen(){
      return this.FCommandLen;
    }

    /*public static void main(String[] args){
      SFE_CommandInfo ProtocolCommandInfo=new SFE_CommandInfo();
      ProtocolCommandInfo.CommandInfoAdd("8010",80);
      String DataCaption="8010";
      int FDataType=10;
      int FDataLen=4;
      String FDataFormat="4";
      int FStorageType=10;
      int FStorageOrder=10;
      ProtocolCommandInfo.DataItemInfoAdd(DataCaption,FDataType,FDataLen,FDataFormat,FStorageType,FStorageOrder);
      System.out.println(((SFE_DataItemInfo)(ProtocolCommandInfo.DataItemInfoList.get(0))).GetFDataCaption());
    }*/
}

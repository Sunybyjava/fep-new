package com.chooshine.fep.FrameDataAreaExplain;

import java.util.ArrayList;

public class SFE_NormalDataTemp {
    private int MeasuredPointType=0;       //测量点类型
    private int MeasuredPointNo=0;         //测量点序号
    private char[] MeasuredAdd;            //测量点地址,这个字段专门给解释电表帧用的
    private int DataItemCount=0;           //数据项数目
    private int CommandLen=0;              //解释的命令长度
    public int ExplainResult=0;            //解释结果
    public ArrayList <SFE_DataItem>DataItemList=new ArrayList<SFE_DataItem>(); //数据项列表

    public int GetMeasuredPointType(){
      return this.MeasuredPointType;
    }
    public int GetMeasuredPointNo(){
      return this.MeasuredPointNo;
    }
    public int GetDataItemCount(){
      return this.DataItemCount;
    }
    public int GetCommandLen(){
      return this.CommandLen;
    }
    public char[] GetMeasuredAdd(){
      return this.MeasuredAdd;
    }
    public void SetMeasuredPointType(int iMeasuredPointType){
      MeasuredPointType=iMeasuredPointType;
    }
    public void SetMeasuredPointNo(int iMeasuredPointNo){
      MeasuredPointNo=iMeasuredPointNo;
    }
    public void SetDataItemCount(int iDataItemCount){
      DataItemCount=iDataItemCount;
    }
    public void SetCommandLen(int iCommandLen){
      CommandLen=iCommandLen;
    }
    public void SetMeasuredAdd(String sMeasuredAdd){
      MeasuredAdd=sMeasuredAdd.toCharArray();
    }
    public void DataItemAdd(String sDataCaption,String sDataContent){
      SFE_DataItem DataItem=new SFE_DataItem();
      DataItem.DataItemAdd(sDataCaption,sDataContent);
      DataItemList.add(DataItem);
      DataItemCount=DataItemCount+1;
    }
    public SFE_NormalDataTemp() {

    }
}

package com.chooshine.fep.FrameDataAreaExplain;

import java.util.ArrayList;

public class SFE_AlarmData {
    private char[] AlarmCode=new char[10];        //告警代码
    private int AlarmType=1;                      //告警类型:1重要;2一般
    private char[] AlarmDateTime=new char[14];    //告警数据时间
    private int MeasuredPointType=0;               //测量点类型
    private int MeasuredPointNo=0;                 //测量点序号
    private int DataItemCount=0;                   //数据项数目
    public ArrayList <SFE_DataItem>DataItemList=new ArrayList<SFE_DataItem>(); //数据项列表
    public SFE_AlarmData() {
    }
    public char[] GetAlarmCode(){
      return this.AlarmCode;
    }
    public int GetAlarmType(){
      return this.AlarmType;
    }
    public char[] GetAlarmDateTime(){
      return this.AlarmDateTime;
    }
    public int GetMeasuredPointType(){
      return this.MeasuredPointType;
    }
    public int GetMeasuredPointNo(){
      return this.MeasuredPointNo;
    }
    public int GetDataItemCount(){
      return this.DataItemCount;
    }
    public void SetAlarmCode(String sAlarmCode){
      AlarmCode=sAlarmCode.toCharArray();
    }
    public void SetAlarmType(int iAlarmType){
      AlarmType=iAlarmType;
    }
    public void SetAlarmDateTime(String sAlarmDateTime){
      AlarmDateTime=sAlarmDateTime.toCharArray();
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
    public void DataItemCountAdd(){
      DataItemCount=DataItemCount+1;
    }
    public void DataItemAdd(String sDataCaption,int iDataLength,String sDataContent){
      SFE_DataItem DataItem=new SFE_DataItem();
      DataItem.DataItemAdd(sDataCaption,sDataContent);
      DataItemList.add(DataItem);
      DataItemCount=DataItemCount+1;
    }
}

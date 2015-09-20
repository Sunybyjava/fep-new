package com.chooshine.fep.FrameDataAreaExplain;

import java.util.ArrayList;

public class SFE_HistoryData {
    private int TaskNo=0;                     //任务号
    private char[] TaskDateTime=new char[14];//任务数据时间
    private int MeasuredPointType=0;          //测量点类型
    private int MeasuredPointNo=0;            //测量点序号
    private char[] MeasuredAdd=new char[100];            //测量点地址,这个字段专门给解释电表帧用的
    private int DataItemCount=0;              //数据项数目
    public ArrayList <SFE_DataItem>DataItemList=new ArrayList<SFE_DataItem>();    //数据项列表
    public SFE_HistoryData() {

    }
    public int GetTaskNo(){
      return this.TaskNo;
    }
    public char[] GetTaskDateTime(){
      return this.TaskDateTime;
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
    public char[] GetMeasuredAdd(){
        return this.MeasuredAdd;
    }
    public void SetTaskNo(int iTaskNo){
      TaskNo=iTaskNo;
    }
    public void SetTaskDateTime(String sTaskDateTime){
      TaskDateTime=sTaskDateTime.toCharArray();
    }
    public void SetMeasuredPointType(int iMeasuredPointType){
      MeasuredPointType=iMeasuredPointType;
    }
    public void SetMeasuredPointNo(int iMeasuredPointNo){
      MeasuredPointNo=iMeasuredPointNo;
    }
    public void SetMeasuredAdd(String sMeasuredAdd){
        MeasuredAdd=sMeasuredAdd.toCharArray();
    }
    public void DataItemCountAdd(){
      DataItemCount=DataItemCount+1;
    }
    public void DataItemAdd(String sDataCaption,String sDataContent){
      SFE_DataItem DataItem=new SFE_DataItem();
      DataItem.DataItemAdd(sDataCaption,sDataContent);
      DataItemList.add(DataItem);
      DataItemCount=DataItemCount+1;
    }
}

package com.chooshine.fep.FrameDataAreaExplain;

import java.util.ArrayList;
public class SFE_SetResultData {
	  private int MeasuredPointType=0;            //测量点类型
	  private int MeasuredPointNo=0;              //测量点序号
	  private char[] MeasuredAdd = new char[100];                 //测量点地址,这个字段专门给解释电表帧用的
	  private int SetRusultCount=0;               //设置返回数据数目
	  public ArrayList <SFE_SetResultItem>SetResultDataList=new ArrayList<SFE_SetResultItem>(); //设置返回数据列表
	  public SFE_SetResultData() {
	  }
	  public int GetSetRusultCount(){
	    return this.SetRusultCount;
	  }
	  public int GetMeasuredPointType(){
	    return this.MeasuredPointType;
	  }
	  public int GetMeasuredPointNo(){
	    return this.MeasuredPointNo;
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
	  public void SetMeasuredAdd(String sMeasuredAdd){
	    MeasuredAdd=sMeasuredAdd.toCharArray();
	  }

	  public void DataItemAdd(String sDataCaption,int iSetResut){
	    SFE_SetResultItem SetResultItem=new SFE_SetResultItem(sDataCaption,iSetResut);
	    SetResultDataList.add(SetResultItem);
	    SetRusultCount=SetRusultCount+1;
	  }
	}

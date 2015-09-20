package com.chooshine.fep.FrameDataAreaExplain;

public class SFE_MeasuredPointInfor {//采集点信息
	  public int MeasuredPointCount=0;            //测量点数目
	  public int MeasuredPointListType=10;        //测量点列表类型(默认10:测量点列表形式;30:测量点所有对象;60:集中器列表形式;70:采集器列表形式)
	  public int[] MeasuredPointList=new int[2040];//测量点序号列表
	  public SFE_MeasuredPointInfor() {
	  }
	}

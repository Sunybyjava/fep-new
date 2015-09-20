package com.chooshine.fep.FrameDataAreaExplain;

import java.util.ArrayList;

public class SFE_TaskInfo {
	private int FTaskNo=0;            //任务号
    private int FTaskType=0;          //任务类型(1普通任务;2中继任务;10轮召任务)
    private int FProtocolType=0;      //规约号
    private int FMeasuredPointNo=0;   //测量点序号
    private int FCommandCount=0;      //数据项数目
    ArrayList <String>CommandList=new ArrayList<String>(); //数据项标识列表
    public SFE_TaskInfo() {
    }
    public void TaskInfoAdd(int iTaskNo,int iTaskType,int iProtocolType,int iMeasuredPointNo) {
      FTaskNo=iTaskNo;
      FTaskType=iTaskType;
      FProtocolType=iProtocolType;
      FMeasuredPointNo=iMeasuredPointNo;
    }

    public void CommandAdd(String sDataCaption){
      CommandList.add(sDataCaption);
      FCommandCount=FCommandCount+1;
    }
    public int GetFTaskNo(){
      return this.FTaskNo;
    }
    public int GetFTaskType(){
      return this.FTaskType;
    }
    public int GetFProtocolType(){
      return this.FProtocolType;
    }
    public int GetFMeasuredPointNo(){
      return this.FMeasuredPointNo;
    }
    public int GetFDataItemCount(){
      return this.FCommandCount;
    }
}

package com.chooshine.fep.FrameDataAreaExplain;

import java.util.ArrayList;

public class SPE_TaskInfoList {
    private int FTaskCount=0;//任务数目
    public ArrayList<SFE_TaskInfo> TaskInfoList=new ArrayList<SFE_TaskInfo>(); //任务信息列表

    public int GetFTaskCount(){
      return this.FTaskCount;
    }
    public void SetFTaskCount(int iTaskCount){
      FTaskCount=iTaskCount;
    }
    public SPE_TaskInfoList() {
    }
}


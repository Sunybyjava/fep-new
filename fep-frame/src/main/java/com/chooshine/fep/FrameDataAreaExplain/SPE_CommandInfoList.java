package com.chooshine.fep.FrameDataAreaExplain;

import java.util.ArrayList;

public class SPE_CommandInfoList {//命令信息列表
    private int FCommandCount=0;//命令数目
    public ArrayList <SFE_CommandInfo>CommandInfoList=new ArrayList<SFE_CommandInfo>(); //命令信息列表
    public SPE_CommandInfoList() {
    }

    public int GetFCommandCount(){
      return this.FCommandCount;
    }
    public void SetFCommandCount(int iCommandCount){
      FCommandCount=iCommandCount;
    }

}
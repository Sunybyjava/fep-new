package com.chooshine.fep.FrameDataAreaExplain;

public class SFE_ExplainAlarmData {
    SFE_AlarmData AlarmData=new SFE_AlarmData();
    private int DataLen=0;
    public SFE_ExplainAlarmData() {
    }
    public int GetDataLen(){
      return this.DataLen;
    }
    public void SetDataLen(int iDataLen){
      DataLen=iDataLen;
    }
}

package com.chooshine.fep.FrameDataAreaExplain;

public class SFE_QGSer_TimeLabel {//时间标签Tp
    private int PFC=0;         //启动帧帧序号计数器
    private char[] ExecuteTime;//启动帧发送时标
    private int EffectTime=0;  //允许发送传输延时时间
    public SFE_QGSer_TimeLabel() {
    }
    public int GetPFC(){
      return this.PFC;
    }
    public char[] GetExecuteTime(){
      return this.ExecuteTime;
    }
    public int GetEffectTime(){
      return this.EffectTime;
    }
    public String GetTp(){//得到处理后的时间标签
      String sTp="";
      try{
        try{
          if(ExecuteTime!=null){
            String sPFC=DataSwitch.IntToHex((""+PFC),"00");
            String sExecuteTime=new String(ExecuteTime);
            sExecuteTime=DataSwitch.DateTimeToBCD(sExecuteTime,"ddhhnnss");
            String sEffectTime=DataSwitch.IntToHex((""+EffectTime),"00");
            sTp=sPFC+sExecuteTime+sEffectTime;
          }
        }
        catch(Exception e){
          System.out.println(e.toString());
        }
      }
      finally{
      }
      return sTp;
    }
    public void SetPFC(int iPFC){
      PFC=iPFC;
    }
    public void SetExecuteTime(String sExecuteTime){
      ExecuteTime=sExecuteTime.toCharArray();
    }
    public void SetEffectTime(int iEffectTime){
      EffectTime=iEffectTime;
    }

}

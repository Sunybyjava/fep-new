package com.chooshine.fep.FrameDataAreaExplain;

public class SFE_SetResultItem {
	private char[] DataCaption;//数据标识
    private int SetResut=-1;   //参数设置结果

    public SFE_SetResultItem(String sDataCaption,int iSetResut) {
      DataCaption=sDataCaption.toCharArray();
      SetResut=iSetResut;
    }
    public char[] GetDataCaption(){
     return this.DataCaption;
    }
    public int GetSetResut(){
     return this.SetResut;
    }
    public void SetDataCaption(String sDataCaption){
      DataCaption=sDataCaption.toCharArray();
    }
    public void SetSetResut(int iSetResut){
      SetResut=iSetResut;
    }
}

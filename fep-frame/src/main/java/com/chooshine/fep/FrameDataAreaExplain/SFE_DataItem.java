package com.chooshine.fep.FrameDataAreaExplain;

public class SFE_DataItem {//数据项
    private char[] DataCaption;  //数据标识
    private int DataLength=0;    //数据长度
    private char[] DataContent;  //数据内容

    public SFE_DataItem() {
    }
    public void DataItemAdd(String sDataCaption,String sDataContent) {
      DataCaption=sDataCaption.toCharArray();
      DataLength=(sDataContent.trim()).length();
      DataContent=(sDataContent.trim()).toCharArray();
    }

    public char[] GetDataCaption(){
      return this.DataCaption;
    }
    public int GetDataLength(){
      return this.DataLength;
    }
    public char[] GetDataContent(){
      return this.DataContent;
    }
    public void SetDataCaption(String sDataCaption){
      DataCaption=sDataCaption.toCharArray();
    }
    public void SetDataContent(String sDataContent){
      DataLength=(sDataContent.trim()).length();
      DataContent=(sDataContent.trim()).toCharArray();
    }

}

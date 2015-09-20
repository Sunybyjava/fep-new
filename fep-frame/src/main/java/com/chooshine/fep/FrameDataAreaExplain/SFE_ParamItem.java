package com.chooshine.fep.FrameDataAreaExplain;

public class SFE_ParamItem {//参数项
	  private char[] ParamCaption; //命令标识
	  private int ParamLen=0;      //参数长度
	  private char[] ParamContent; //参数内容
	  public SFE_ParamItem() {
	  }
	  public char[] GetParamCaption(){
	    return this.ParamCaption;
	  }
	  public char[] GetParamContent(){
	    return this.ParamContent;
	  }
	  public int GetParamLen(){
	    return this.ParamLen;
	  }
	  public void SetParamCaption(String sParamCaption){
	    ParamCaption=sParamCaption.toCharArray();
	  }
	  public void SetcParamContent(String sParamContent){
	    ParamLen=(sParamContent.trim()).length();
	    ParamContent=sParamContent.toCharArray();
	  }

	}

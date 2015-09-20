package com.chooshine.fep.FrameDataAreaExplain;

public class SFE_FrameInfo {//命令帧信息
	  private int  CommandSeq=0;       //命令序号
	  private int CommandFrameLen=0;   //命令帧长度
	  private char[] CommandFrame;     //返回帧内容
	  public SFE_FrameInfo() {
	  }
	  public int GetCommandSeq(){
	    return this.CommandSeq;
	  }
	  public int GetCommandFrameLen(){
	    return this.CommandFrameLen;
	  }
	  public char[] GetCommandFrame(){
	    return this.CommandFrame;
	  }

	  public void SetCommandFrame(String sCommandFrame){
	    CommandFrameLen=sCommandFrame.length();
	    CommandFrame=sCommandFrame.toCharArray();
	  }
	  public void SetCommandSeq(int iCommandSeq){
	    CommandSeq=iCommandSeq;
	  }



	}

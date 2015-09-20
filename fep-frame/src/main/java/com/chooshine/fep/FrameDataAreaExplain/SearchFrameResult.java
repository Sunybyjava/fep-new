package com.chooshine.fep.FrameDataAreaExplain;

public class SearchFrameResult {
	private boolean SearchFlag;
	private int StartPos;
	private int EndPos;

	public boolean getSearchFlag(){
		return SearchFlag;
	}

	public int getStartPos(){
		return StartPos;
	}

	public int getEndPos(){
		return EndPos;
	}

	public void setSearchFlag(boolean Flag){
		this.SearchFlag=Flag;
	}

	public void setStartPos(int iStartPos){
		this.StartPos=iStartPos;
	}

	public void setEndPos(int iEndPos){
		this.EndPos=iEndPos;
	}
}

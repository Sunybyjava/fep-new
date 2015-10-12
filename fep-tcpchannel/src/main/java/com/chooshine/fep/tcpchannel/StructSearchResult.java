package com.chooshine.fep.tcpchannel;

public class StructSearchResult {
	public boolean bFrameIsFull = false;
	public boolean bFrameIsRight = false;
	public int iStartPosition = 0;
	public int iEndPos = 0;
	public int FrameHeadPort = 0;
	public int iProtocolTag = 0;// 规约系列标记 10 国网;20 浙规;30 海兴;40 DLMS

	public StructSearchResult() {
	}
}

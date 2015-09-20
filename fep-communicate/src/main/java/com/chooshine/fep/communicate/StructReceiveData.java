package com.chooshine.fep.communicate;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class StructReceiveData {
	  public String TerminalLogicAdd = ""; //终端逻辑地址
	  public int TerminalProtocal = 0;//终端规约号
	  public int StationNo = 0; //主站序号
	  public int CommandSeq = 0; //命令序号
	  public int FrameSeq = 0; //帧序号
	  public String ControlCode = ""; //控制码
	  public String FunctionCode = ""; //功能码
	  public String ManufacturerCode = ""; //厂商编码
	  public int DataType = 0; //数据类型
	  public int FrameLength = 0; //数据区长度
	  public String FrameData = ""; //数据区内容
	  public StructReceiveData() {
	  }
	}
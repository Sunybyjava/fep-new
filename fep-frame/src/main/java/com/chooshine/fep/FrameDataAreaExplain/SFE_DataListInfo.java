package com.chooshine.fep.FrameDataAreaExplain;

import java.util.ArrayList;

public class SFE_DataListInfo {//解释接口返回数据列表信息
	  public int ExplainResult=0;                //解释结果
	  public int DataType=0;                     //数据类型：10普通数据；20历史数据；30告警数据；40；设置返回数据；50：点抄电表的返回
	  public ArrayList DataList=new ArrayList(); //返回数据列表
	  public SFE_DataListInfo() {
	  }
	}


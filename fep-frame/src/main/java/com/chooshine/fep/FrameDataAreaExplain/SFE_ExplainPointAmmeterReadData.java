package com.chooshine.fep.FrameDataAreaExplain;

import java.util.ArrayList;

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
public class SFE_ExplainPointAmmeterReadData {
    public int MeasurePoint = 0;
    public String AmmeterAddress = "";
    public int ControlCode = 0;
    public int DataItemCount = 0; //数据项数目
    public ArrayList DataItemList = new ArrayList(); //数据项列表
    public SFE_ExplainPointAmmeterReadData() {
    }
}

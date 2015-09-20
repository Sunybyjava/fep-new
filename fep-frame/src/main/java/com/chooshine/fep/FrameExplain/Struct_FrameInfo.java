package com.chooshine.fep.FrameExplain;

public class Struct_FrameInfo {

    public char[] TerminalLogicAdd; //终端逻辑地址
    public int TermialProtocolNo; //终端规约号
    public int StationNo;//主站序号
    public int CommandSeq;//命令序号
    public int FrameSeq;//帧类序号
    public char[] ControlCode;//控制码
    public char[] FunctionCode;//功能码
    public char[] ManufacturerCode;//厂商编码
    public int DataType;//数据类型
    public char[] FrameData; //规约数据区内容

    public Struct_FrameInfo() {
        TerminalLogicAdd = new char[20];
        ControlCode = new char[5];
        FunctionCode = new char[5];
        ManufacturerCode = new char[5];
        TermialProtocolNo = 0;
        CommandSeq = 0;
        CommandSeq = 0;
        FrameSeq = 0;
        try {
            jbInit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void SetDataContent(String sData) {
        FrameData = new char[sData.length()];
        FrameData=sData.toCharArray();
        //for (int i = 0; i < cData.length; i++) {
            //FrameData[i] = cData[i];
        //}
        //System.out.println(FrameData);
    }

    private void jbInit() throws Exception {
    }
}

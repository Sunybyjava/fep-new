package com.chooshine.fep.fepex.common;

public class TerminalInfo {
	public char[] TerminalAddress; //终端逻辑地址
    public int TerminalProtocol; //终端规约号
    public int TerminalCommType; //终端通讯方式
    public String Popedom; //密码级别
    public char[] TerminalPassword; //终端密码
    public char[] ExecuteTime; //计划执行时间  （全0时表示接收到命令立即执行，并且不进行时效性判断）
    //格式：yyyymmddhhnnss
    public int EffectTime; //有效时间（分）
   // public int CldCount; //测量点数目
    public int CldLx; //测量点类型
    public int Cldxh; //测量点序号
    public String Clddz; //测量点地址
    public int Dxxbz; //大小项标志
    public int Sfbh = 0; //密码算法编号

    public TerminalInfo() {
    }
}

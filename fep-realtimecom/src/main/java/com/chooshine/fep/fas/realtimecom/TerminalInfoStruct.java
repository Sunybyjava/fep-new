package com.chooshine.fep.fas.realtimecom;

public class TerminalInfoStruct { //终端信息结构
  public char[] TerminalAddress; //终端逻辑地址
  public int TerminalProtocol; //终端规约号
  public int TerminalCommType; //终端通讯方式
  public int ArithmeticNo=0;//算法编号
  public TerminalInfoStruct() {
  }
}

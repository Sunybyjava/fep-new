package com.chooshine.fep.ConstAndTypeDefine;

public class Struct_CommRecordItem { //ԭʼͨѶ��¼�� (�������к�����ԭʼͨѶ��¼)
  private char[] TargetAddress; //Ŀ���ַ(���������ǽ��յ�ַ,�����������ն�ͨѶ��ַ)
  private char[] SourceAddress; //Դ��ַ(�����������ն�ͨѶ��ַ,����������ǰ�û���ͨ���ĵ�ַ)
  private int ChannelType; //ͨѶ��ʽ
  private char[] MessageContent; //ͨѶ����
  private char[] TerminalAddress; //�ն��߼���ַ

  public Struct_CommRecordItem() {}

  public char[] GetTargetAddress() {
    return this.TargetAddress;
  }

  public char[] GetSourceAddress() {
    return this.SourceAddress;
  }

  public char[] GetMessageContent() {
    return this.MessageContent;
  }

  public char[] GetTerminalAddress() {
    return this.TerminalAddress;
  }

  public int GetChannelType() {
    return this.ChannelType;
  }

  public void SetTargetAddress(String sTargetAddress) {
    TargetAddress = sTargetAddress.toCharArray();
  }

  public void SetSourceAddress(String sSourceAddress) {
    SourceAddress = sSourceAddress.toCharArray();
  }

  public void SetMessageContent(String sMessageContent) {
    MessageContent = sMessageContent.toCharArray();
  }

  public void SetTerminalAddress(String sTerminalAddress) {
    TerminalAddress = sTerminalAddress.toCharArray();
  }

  public void SetChannelType(int iChannelType) {
    ChannelType = iChannelType;
  }
}

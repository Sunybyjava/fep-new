package com.chooshine.fep.fas.realtimecom;

import java.awt.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.util.LinkedList;
//import hexing.fep.communicate.utils;
import java.util.List;

public class RealTimeTestFrame
    extends JFrame {
  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
JPanel contentPane;
  JButton jButton1 = new JButton();
  JButton jButton2 = new JButton();
  JLabel jLabel1 = new JLabel();
  JLabel jLabel2 = new JLabel();
  JTextField jTextField1 = new JTextField();
  JTextField txt_port = new JTextField();
  RealTimeCommunication rtc = null;
  JLabel jLabel3 = new JLabel();
  JLabel jLabel4 = new JLabel();
  JLabel jLabel5 = new JLabel();
  JLabel jLabel6 = new JLabel();
  JLabel jLabel7 = new JLabel();
  JTextField txt_AppID = new JTextField();
  JTextField txt_zdljdz = new JTextField();
  JTextField txt_gyh = new JTextField();
  JTextField txt_txfs = new JTextField();
  JTextField jTextField7 = new JTextField();
  JTextField txt_ip = new JTextField();
  JTextField txt_content = new JTextField();
  JButton jButton3 = new JButton();
  JButton jButton4 = new JButton();
  JLabel lab_yxj = new JLabel();
  JTextField txt_gnm = new JTextField();
  JLabel jLabel9 = new JLabel();
  JTextField txt_sjhm = new JTextField();
  JLabel jLabel10 = new JLabel();
  JTextField txt_mlxh = new JTextField();
  JButton jButton5 = new JButton();
  JButton jButton6 = new JButton();
  JLabel jLabel11 = new JLabel();
  JTextField txt_listterminal = new JTextField();
  JButton jButton8 = new JButton();
  JButton jButton9 = new JButton();
  JTextArea memo1 = new JTextArea();
  private java.util.LinkedList <TerminalOnLineStatus>TerminalInfoList = new LinkedList<TerminalOnLineStatus>();
  JButton jButton7 = new JButton();
  JButton jButton10 = new JButton();
  JLabel jLabel12 = new JLabel();
  JTextField txt_yxj = new JTextField();
  JButton jButton11 = new JButton();
  JButton jButton12 = new JButton();
  public RealTimeTestFrame() {
    try {
      setDefaultCloseOperation(EXIT_ON_CLOSE);
      jbInit();
    }
    catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  /**
   * Component initialization.
   *
   * @throws java.lang.Exception
   */
  private void jbInit() throws Exception {

    contentPane = (JPanel) getContentPane();
    contentPane.setLayout(null);

    this.setFont(new java.awt.Font("宋体", Font.PLAIN, 12));
    setSize(new Dimension(400, 621));
    setTitle("实时通讯模块测试程序");
    jButton1.setBounds(new Rectangle(11, 412, 82, 26));
    jButton1.setFont(new java.awt.Font("宋体", Font.PLAIN, 12));
    jButton1.setText("召测接口");
    jButton1.addActionListener(new RealTimeTestFrame_jButton1_actionAdapter(this));
    jButton2.setBounds(new Rectangle(10, 366, 83, 26));
    jButton2.setFont(new java.awt.Font("宋体", Font.PLAIN, 12));
    jButton2.setText("创建对象");
    jButton2.addActionListener(new RealTimeTestFrame_jButton2_actionAdapter(this));
    jLabel1.setFont(new java.awt.Font("宋体", Font.PLAIN, 12));
    jLabel1.setText("接入IP：");
    jLabel1.setBounds(new Rectangle(23, 22, 63, 16));
    jLabel2.setFont(new java.awt.Font("宋体", Font.PLAIN, 12));
    jLabel2.setText("接入端口：");
    jLabel2.setBounds(new Rectangle(23, 67, 63, 16));
    jTextField1.setFont(new java.awt.Font("宋体", Font.PLAIN, 12));
    jTextField1.setText("172.19.74.13");
    txt_port.setFont(new java.awt.Font("宋体", Font.PLAIN, 12));
    txt_port.setText("3000");
    txt_port.setBounds(new Rectangle(84, 64, 88, 22));
    jLabel3.setFont(new java.awt.Font("宋体", Font.PLAIN, 12));
    jLabel3.setText("AppID：");
    jLabel3.setBounds(new Rectangle(23, 113, 63, 16));
    contentPane.setFont(new java.awt.Font("宋体", Font.PLAIN, 12));
    jLabel4.setFont(new java.awt.Font("宋体", Font.PLAIN, 12));
    jLabel4.setText("ZDLJDZ：");
    jLabel4.setBounds(new Rectangle(23, 158, 63, 16));
    jLabel5.setFont(new java.awt.Font("宋体", Font.PLAIN, 12));
    jLabel5.setText("规约号：");
    jLabel5.setBounds(new Rectangle(23, 186, 63, 16));
    jLabel6.setFont(new java.awt.Font("宋体", Font.PLAIN, 12));
    jLabel6.setText("发送内容：");
    jLabel6.setBounds(new Rectangle(23, 333, 63, 16));
    jLabel7.setFont(new java.awt.Font("宋体", Font.PLAIN, 12));
    jLabel7.setText("通讯方式：");
    jLabel7.setBounds(new Rectangle(21, 219, 63, 16));
    txt_AppID.setFont(new java.awt.Font("宋体", Font.PLAIN, 12));
    txt_AppID.setText("25");
    txt_AppID.setBounds(new Rectangle(84, 110, 83, 22));
    txt_zdljdz.setFont(new java.awt.Font("宋体", Font.PLAIN, 12));
    txt_zdljdz.setText("91010022");
    txt_zdljdz.setBounds(new Rectangle(84, 155, 88, 22));
    txt_gyh.setFont(new java.awt.Font("宋体", Font.PLAIN, 12));
    txt_gyh.setText("100");
    txt_gyh.setBounds(new Rectangle(84, 183, 63, 22));
    txt_txfs.setFont(new java.awt.Font("宋体", Font.PLAIN, 12));
    txt_txfs.setText("40");
    txt_txfs.setBounds(new Rectangle(84, 218, 63, 22));
    jTextField7.setFont(new java.awt.Font("宋体", Font.PLAIN, 12));
    txt_ip.setFont(new java.awt.Font("宋体", Font.PLAIN, 12));
    InetAddress localhost = java.net.InetAddress.getLocalHost();
    String hostIP = localhost.getHostAddress();
    txt_ip.setText(hostIP);
    txt_ip.setBounds(new Rectangle(84, 19, 159, 22));
    txt_content.setFont(new java.awt.Font("宋体", Font.PLAIN, 12));
    txt_content.setText("6000000100111107");
    txt_content.setBounds(new Rectangle(84, 330, 298, 22));
    txt_content.addActionListener(new
                                  RealTimeTestFrame_txt_content_actionAdapter(this));
    jButton3.setBounds(new Rectangle(200, 412, 89, 26));
    jButton3.setFont(new java.awt.Font("宋体", Font.PLAIN, 12));
    jButton3.setText("发送短信");
    jButton3.addActionListener(new RealTimeTestFrame_jButton3_actionAdapter(this));
    jButton4.setBounds(new Rectangle(295, 366, 96, 26));
    jButton4.setFont(new java.awt.Font("宋体", Font.PLAIN, 12));
    jButton4.setText("自定义命令");
    jButton4.addActionListener(new RealTimeTestFrame_jButton4_actionAdapter(this));
    lab_yxj.setFont(new java.awt.Font("宋体", Font.PLAIN, 12));
    lab_yxj.setText("优先级：");
    lab_yxj.setBounds(new Rectangle(23, 287, 61, 16));
    txt_gnm.setFont(new java.awt.Font("宋体", Font.PLAIN, 12));
    txt_gnm.setText("0D");
    txt_gnm.setBounds(new Rectangle(84, 251, 63, 22));
    jLabel9.setFont(new java.awt.Font("宋体", Font.PLAIN, 12));
    jLabel9.setText("手机号码：");
    jLabel9.setBounds(new Rectangle(190, 67, 67, 16));
    txt_sjhm.setFont(new java.awt.Font("宋体", Font.PLAIN, 12));
    txt_sjhm.setText("13800571505");
    txt_sjhm.setBounds(new Rectangle(250, 64, 111, 22));
    jLabel10.setFont(new java.awt.Font("宋体", Font.PLAIN, 12));
    jLabel10.setText("命令序号：");
    jLabel10.setBounds(new Rectangle(190, 112, 73, 16));
    txt_mlxh.setFont(new java.awt.Font("宋体", Font.PLAIN, 12));
    txt_mlxh.setText("1");
    txt_mlxh.setBounds(new Rectangle(250, 109, 110, 22));
    jButton5.setBounds(new Rectangle(105, 366, 83, 26));
    jButton5.setFont(new java.awt.Font("宋体", Font.PLAIN, 12));
    jButton5.setText("连接");
    jButton5.addActionListener(new RealTimeTestFrame_jButton5_actionAdapter(this));
    jButton6.setBounds(new Rectangle(200, 366, 83, 26));
    jButton6.setFont(new java.awt.Font("宋体", Font.PLAIN, 12));
    jButton6.setText("断开连接");
    jButton6.addActionListener(new RealTimeTestFrame_jButton6_actionAdapter(this));
    jLabel11.setFont(new java.awt.Font("宋体", Font.PLAIN, 12));
    jLabel11.setText("终端逻辑地址：");
    jLabel11.setBounds(new Rectangle(164, 199, 87, 16));
    txt_listterminal.setText("12041234");
    txt_listterminal.setBounds(new Rectangle(252, 196, 130, 22));
    jButton8.setBounds(new Rectangle(170, 248, 90, 26));
    jButton8.setFont(new java.awt.Font("宋体", Font.PLAIN, 12));
    jButton8.setText("加入队列");
    jButton8.addActionListener(new RealTimeTestFrame_jButton8_actionAdapter(this));
    jButton9.setBounds(new Rectangle(283, 248, 85, 26));
    jButton9.setFont(new java.awt.Font("宋体", Font.PLAIN, 12));
    jButton9.setText("在线状态");
    jButton9.addActionListener(new RealTimeTestFrame_jButton9_actionAdapter(this));
    memo1.setFont(new java.awt.Font("宋体", Font.PLAIN, 12));
    memo1.setToolTipText("");
    memo1.setEditable(false);
    memo1.setLineWrap(true);
    memo1.setWrapStyleWord(true);
    memo1.setBounds(new Rectangle(17, 451, 361, 154));
    jButton7.setBounds(new Rectangle(226, 289, 92, 26));
    jButton7.setFont(new java.awt.Font("宋体", Font.PLAIN, 12));
    jButton7.setText("设置掉线");
    jButton7.addActionListener(new RealTimeTestFrame_jButton7_actionAdapter(this));
    jButton10.setBounds(new Rectangle(192, 155, 90, 23));
    jButton10.setFont(new java.awt.Font("宋体", Font.PLAIN, 12));
    jButton10.setText("手工建档");
    jButton10.addActionListener(new RealTimeTestFrame_jButton10_actionAdapter(this));
    jLabel12.setFont(new java.awt.Font("宋体", Font.PLAIN, 12));
    jLabel12.setText("功能码：");
    jLabel12.setBounds(new Rectangle(23, 254, 61, 16));
    txt_yxj.setFont(new java.awt.Font("宋体", Font.PLAIN, 12));
    txt_yxj.setText("08");
    txt_yxj.setBounds(new Rectangle(84, 283, 63, 22));
    jButton11.setBounds(new Rectangle(105, 412, 82, 26));
    jButton11.setFont(new java.awt.Font("宋体", Font.PLAIN, 12));
    jButton11.setActionCommand("");
    jButton11.setText("获取结果");
    jButton11.addActionListener(new RealTimeTestFrame_jButton11_actionAdapter(this));
    jButton12.setBounds(new Rectangle(301, 412, 87, 26));
    jButton12.setFont(new java.awt.Font("宋体", Font.PLAIN, 12));
    jButton12.setText("批量测试");
    jButton12.addActionListener(new RealTimeTestFrame_jButton12_actionAdapter(this));
    contentPane.add(jLabel1);
    contentPane.add(jLabel2);
    contentPane.add(txt_port);
    contentPane.add(jLabel3);
    contentPane.add(jLabel4);
    contentPane.add(jTextField1);
    contentPane.add(txt_AppID);
    contentPane.add(txt_zdljdz);
    contentPane.add(jTextField7);
    contentPane.add(txt_ip);
    contentPane.add(txt_content);
    contentPane.add(jLabel6);
    contentPane.add(jLabel9);
    contentPane.add(txt_sjhm);
    contentPane.add(jLabel10);
    contentPane.add(txt_mlxh);
    contentPane.add(txt_listterminal);
    contentPane.add(jLabel11);
    contentPane.add(jButton8);
    contentPane.add(jButton9);
    contentPane.add(memo1);
    contentPane.add(jButton7);
    contentPane.add(jButton10);
    contentPane.add(txt_gnm);
    contentPane.add(txt_txfs);
    contentPane.add(txt_gyh);
    contentPane.add(jLabel5);
    contentPane.add(jLabel7);
    contentPane.add(jLabel12);
    contentPane.add(lab_yxj);
    contentPane.add(txt_yxj);
    contentPane.add(jButton2);
    contentPane.add(jButton5);
    contentPane.add(jButton6);
    contentPane.add(jButton4);
    contentPane.add(jButton1);
    contentPane.add(jButton11);
    contentPane.add(jButton3);
    contentPane.add(jButton12);
  }

  public void jButton2_actionPerformed(ActionEvent e) {
    String hostName = txt_ip.getText();
    int port = Integer.parseInt(txt_port.getText());
    rtc = new RealTimeCommunication(hostName, port);
  }

  public void jButton1_actionPerformed(ActionEvent e) {
    if (rtc != null) {
//      if (!rtc.SendToFep(Integer.parseInt(txt_AppID.getText()),
//                         txt_zdljdz.getText().toCharArray(),
//                         Integer.parseInt(txt_gyh.getText()),
//                         Integer.parseInt(txt_txfs.getText()), 0, 0, 0,
//                         txt_gnm.getText().toCharArray(),
//                         txt_content.getText().length(),
//                         txt_content.getText().toCharArray(),
//                         Integer.parseInt(txt_yxj.getText()), 128)) {
//        System.out.println("error");
//      }
      List <TerminalInfoStruct>TerminalInfo = new LinkedList<TerminalInfoStruct>();
      List <DataContentStruct>DataContentInfo = new LinkedList<DataContentStruct>();

        TerminalInfoStruct tis = new TerminalInfoStruct();
        tis.TerminalAddress = txt_zdljdz.getText().toCharArray(); //("" + (12080101 + i) + "10").toCharArray();
        tis.TerminalCommType = Integer.parseInt(txt_txfs.getText());
        tis.TerminalProtocol = Integer.parseInt(txt_gyh.getText());
        TerminalInfo.add(tis);
       /* tis = new TerminalInfoStruct();
        tis.TerminalAddress = "1208010710".toCharArray(); //("" + (12080101 + i) + "10").toCharArray();
        tis.TerminalCommType = 50;
        tis.TerminalProtocol = 100;
        TerminalInfo.add(tis);
       */
        DataContentStruct dcs = new DataContentStruct();
        dcs.DataContentLength = txt_content.getText().length();
        dcs.DataContent = txt_content.getText().toCharArray();
        DataContentInfo.add(dcs);
        dcs = new DataContentStruct();
        dcs.DataContentLength = txt_content.getText().length();
        dcs.DataContent = txt_content.getText().toCharArray();
        DataContentInfo.add(dcs);

      /*for (int i = 0; i < 10; i++) {
               TerminalInfoStruct tis = new TerminalInfoStruct();
       tis.TerminalAddress = ("1208004" + ("" + i) + "10").toCharArray();
               tis.TerminalCommType = 40;
               tis.TerminalProtocol = 105;
               TerminalInfo.add(tis);
               DataContentStruct dcs = new DataContentStruct();
               dcs.DataContentLength = txt_content.getText().length();
               dcs.DataContent = txt_content.getText().toCharArray();
               DataContentInfo.add(dcs);
           }
       */
      rtc.SendBatchToFep(Integer.parseInt(txt_AppID.getText()),
                         TerminalInfo.size(),
                         TerminalInfo, DataContentInfo,
                         txt_gnm.getText().toCharArray(), 0, 0, 0, 0);
    }
    else {
      System.out.println("no object,create it first");
    }

  }

  public void jButton4_actionPerformed(ActionEvent e) {
    if (rtc != null) {
      boolean b = rtc.SendSelfDefinedMsg(Integer.parseInt(txt_AppID.getText()),
                                         txt_zdljdz.getText().toCharArray(),
                                         Integer.parseInt(txt_gyh.getText()),
                                         Integer.parseInt(txt_txfs.getText()),
                                         Integer.parseInt(txt_mlxh.getText()),
                                         txt_content.getText().length(),
                                         txt_content.getText().toCharArray(), 0);
      try {
        Thread.sleep(1000);
      }
      catch (InterruptedException ex) {
      }
      rtc.SendSelfDefinedMsg(Integer.parseInt(txt_AppID.getText()) + 100,
                             txt_zdljdz.getText().toCharArray(),
                             Integer.parseInt(txt_gyh.getText()),
                             Integer.parseInt(txt_txfs.getText()),
                             Integer.parseInt(txt_mlxh.getText()),
                             txt_content.getText().length(),
                             txt_content.getText().toCharArray(), 0);

      if (b) {
        System.out.println("send success!");
      }
      else {
        System.out.println("send fail!");
      }
    }
    else {
      System.out.println("no object,create it first");
    }
  }

  public void jButton3_actionPerformed(ActionEvent e) {
    if (rtc != null) {
      rtc.SendShortMsg(txt_content.getText().toCharArray(),
                                   txt_content.getText().length(),
                                   txt_sjhm.getText().toCharArray(), 10, 30);
    }
    else {
      System.out.println("no object,create it first");
    }
  }

  public void jButton5_actionPerformed(ActionEvent e) {
    if (rtc != null) {
      rtc.Connect();
    }
    else {
      System.out.println("no object,create it first");
    }
  }

  public void jButton6_actionPerformed(ActionEvent e) {
    if (rtc != null) {
      rtc.DisConnect();
    }
    else {
      System.out.println("no object,create it first");
    }
  }

  public void jButton7_actionPerformed(ActionEvent e) {
    if (rtc != null) {
      if (rtc.SetTerminalOnLineStatus(TerminalInfoList)) {
        memo1.append("Terminal OffLine Command Send Success!" + "\r\n");
      }
    }
    else {
      System.out.println("no object,create it first");
    }
  }

  public void jButton8_actionPerformed(ActionEvent e) {
    TerminalOnLineStatus to = new TerminalOnLineStatus();
    to.TerminalAddress = txt_listterminal.getText();
    to.OnLineStatus = 20;
    TerminalInfoList.add(to);
  }

  public void jButton9_actionPerformed(ActionEvent e) {
    java.util.List OutList = new LinkedList();
    if (rtc != null) {
      OutList = rtc.GetTerminalOnLineStatus(TerminalInfoList);
    }
    else {
      System.out.println("no object,create it first");
    }
    if (OutList != null) {
      for (int i = 0; i < OutList.size(); i++) {
        TerminalOnLineStatus to = (TerminalOnLineStatus) OutList.get(i);
        memo1.append(to.TerminalAddress + " OnLineStatus is " +
                     to.OnLineStatus + "\r\n");
      }
    }
  }

  public void jButton10_actionPerformed(ActionEvent actionEvent) {
    if (rtc != null) {
      rtc.BuildCertainTerminalInfo(txt_zdljdz.getText());
    }
  }

  public void jButton11_actionPerformed(ActionEvent actionEvent) {
    /*StructReturnMessage ci = rtc.GetNewMessage(Integer.parseInt(txt_AppID.
        getText()));
         if (ci != null) {
      memo1.append("AppID:" + ci.AppID + "\r\n");
      String FunctionCode = new String(ci.FunctionCode).trim();
      memo1.append("FunctionCode:" + FunctionCode + "\r\n");
      String ControlConde = new String(ci.ControlConde).trim();
      memo1.append("ControlConde:" + ControlConde + "\r\n");
      String SJQNR = new String(ci.SJQNR).trim();
      memo1.append("SJQNR:" + SJQNR + "\r\n");
         }*/
    List strReturnMessageList = rtc.GetNewListMessage(Integer.parseInt(
        txt_AppID.getText()));
    if (strReturnMessageList != null) {
      memo1.setEditable(true);
      memo1.append("AppID:" + txt_AppID.getText() + "\r\n");
      memo1.append("GetCount:" + strReturnMessageList.size() + "\r\n");
    }

  }

  public void txt_content_actionPerformed(ActionEvent actionEvent) {

  }

  public void jButton12_actionPerformed(ActionEvent e) {
    List <TerminalInfoStruct>TerminalInfo = new LinkedList<TerminalInfoStruct>();
    List <DataContentStruct>DataContentInfo = new LinkedList<DataContentStruct>();
    for (int i = 0; i < 50; i++) {
      TerminalInfo.clear();
      DataContentInfo.clear();
      TerminalInfoStruct tis = new TerminalInfoStruct();
      int l = 91010001 + i;
      String s = Integer.toString(l);
      tis.TerminalAddress = ("" + s.substring(0, 4) + s.substring(6, 8) +
                             s.substring(4, 6)).toCharArray();
      tis.TerminalCommType = 40;
      tis.TerminalProtocol = 80;
      TerminalInfo.add(tis);
      DataContentStruct dcs = new DataContentStruct();
      dcs.DataContentLength = txt_content.getText().length();
      dcs.DataContent = txt_content.getText().toCharArray();
      DataContentInfo.add(dcs);
      rtc.SendBatchToFep(Integer.parseInt(txt_AppID.getText()),
                         TerminalInfo.size(),
                         TerminalInfo, DataContentInfo,
                         txt_gnm.getText().toCharArray(), 0, 0, 0, 0);
      try {
        Thread.sleep(1000);
      }
      catch (InterruptedException ex) {
      }
    }

  }
}

class RealTimeTestFrame_jButton12_actionAdapter
    implements ActionListener {
  private RealTimeTestFrame adaptee;
  RealTimeTestFrame_jButton12_actionAdapter(RealTimeTestFrame adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {

    adaptee.jButton12_actionPerformed(e);
  }
}

class RealTimeTestFrame_txt_content_actionAdapter
    implements ActionListener {
  private RealTimeTestFrame adaptee;
  RealTimeTestFrame_txt_content_actionAdapter(RealTimeTestFrame adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent actionEvent) {
    adaptee.txt_content_actionPerformed(actionEvent);
  }
}

class RealTimeTestFrame_jButton11_actionAdapter
    implements ActionListener {
  private RealTimeTestFrame adaptee;
  RealTimeTestFrame_jButton11_actionAdapter(RealTimeTestFrame adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent actionEvent) {
    adaptee.jButton11_actionPerformed(actionEvent);
  }
}

class RealTimeTestFrame_jButton10_actionAdapter
    implements ActionListener {
  private RealTimeTestFrame adaptee;
  RealTimeTestFrame_jButton10_actionAdapter(RealTimeTestFrame adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent actionEvent) {

    adaptee.jButton10_actionPerformed(actionEvent);
  }
}

class RealTimeTestFrame_jButton7_actionAdapter
    implements ActionListener {
  private RealTimeTestFrame adaptee;
  RealTimeTestFrame_jButton7_actionAdapter(RealTimeTestFrame adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.jButton7_actionPerformed(e);
  }
}

class RealTimeTestFrame_jButton9_actionAdapter
    implements ActionListener {
  private RealTimeTestFrame adaptee;
  RealTimeTestFrame_jButton9_actionAdapter(RealTimeTestFrame adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.jButton9_actionPerformed(e);
  }
}

class RealTimeTestFrame_jButton8_actionAdapter
    implements ActionListener {
  private RealTimeTestFrame adaptee;
  RealTimeTestFrame_jButton8_actionAdapter(RealTimeTestFrame adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.jButton8_actionPerformed(e);
  }
}

class RealTimeTestFrame_jButton6_actionAdapter
    implements ActionListener {
  private RealTimeTestFrame adaptee;
  RealTimeTestFrame_jButton6_actionAdapter(RealTimeTestFrame adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.jButton6_actionPerformed(e);
  }
}

class RealTimeTestFrame_jButton5_actionAdapter
    implements ActionListener {
  private RealTimeTestFrame adaptee;
  RealTimeTestFrame_jButton5_actionAdapter(RealTimeTestFrame adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.jButton5_actionPerformed(e);
  }
}

class RealTimeTestFrame_jButton3_actionAdapter
    implements ActionListener {
  private RealTimeTestFrame adaptee;
  RealTimeTestFrame_jButton3_actionAdapter(RealTimeTestFrame adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.jButton3_actionPerformed(e);
  }
}

class RealTimeTestFrame_jButton4_actionAdapter
    implements ActionListener {
  private RealTimeTestFrame adaptee;
  RealTimeTestFrame_jButton4_actionAdapter(RealTimeTestFrame adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {

    adaptee.jButton4_actionPerformed(e);
  }
}

class RealTimeTestFrame_jButton1_actionAdapter
    implements ActionListener {
  private RealTimeTestFrame adaptee;
  RealTimeTestFrame_jButton1_actionAdapter(RealTimeTestFrame adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {

    adaptee.jButton1_actionPerformed(e);
  }
}

class RealTimeTestFrame_jButton2_actionAdapter
    implements ActionListener {
  private RealTimeTestFrame adaptee;
  RealTimeTestFrame_jButton2_actionAdapter(RealTimeTestFrame adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.jButton2_actionPerformed(e);
  }
}

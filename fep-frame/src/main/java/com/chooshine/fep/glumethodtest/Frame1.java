package com.chooshine.fep.glumethodtest;

import com.chooshine.fep.ConstAndTypeDefine.FileLogger;
import com.chooshine.fep.ConstAndTypeDefine.Glu_DataAccess;
import com.chooshine.fep.ConstAndTypeDefine.Struct_CommRecordItem;

import java.util.Calendar;
import java.text.SimpleDateFormat;

import java.awt.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import com.chooshine.fep.FrameDataAreaExplain.SFE_HistoryData;
import com.chooshine.fep.FrameDataAreaExplain.SPE_CommandInfoList;
import java.util.ArrayList;
//import hexing.fep.FrameDataAreaExplain.SPE_TaskInfoList;

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
public class Frame1
    extends JFrame {
  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
JPanel contentPane;
  JButton jButton1 = new JButton();
  JTextField jTF_Directory = new JTextField();
  JTextField jTF_FileName = new JTextField();
  JButton jButton2 = new JButton();
  JLabel jLabel1 = new JLabel();
  JLabel jLabel2 = new JLabel();
  JButton jButton3 = new JButton();
  JButton jButton4 = new JButton();

  private Glu_DataAccess gDataAccess;
  JButton jButton5 = new JButton();
  JButton jButton6 = new JButton();
  JButton jButton7 = new JButton();
  JButton jButton8 = new JButton();
  JButton jButton9 = new JButton();
  JButton jButton10 = new JButton();
  JButton jButton11 = new JButton();

  public Frame1() {
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
    setSize(new Dimension(400, 300));
    setTitle("Frame Title");
    jButton1.setBounds(new Rectangle(58, 60, 104, 25));
    jButton1.setText("д��־�ļ�");
    jButton1.addActionListener(new Frame1_jButton1_actionAdapter(this));
    jTF_Directory.setText("c:\\MyLog");
    jTF_Directory.setBounds(new Rectangle(67, 12, 106, 21));
    jTF_FileName.setText("DataProcess");
    jTF_FileName.setBounds(new Rectangle(237, 12, 105, 21));
    jButton2.setBounds(new Rectangle(215, 61, 91, 25));
    jButton2.setText("��ǰʱ��");
    jButton2.addActionListener(new Frame1_jButton2_actionAdapter(this));
    jLabel1.setText("Ŀ¼");
    jLabel1.setBounds(new Rectangle(24, 14, 34, 16));
    jLabel2.setToolTipText("");
    jLabel2.setText("�ļ���");
    jLabel2.setBounds(new Rectangle(187, 13, 34, 16));
    jButton3.setBounds(new Rectangle(52, 107, 90, 25));
    jButton3.setText("������ݿ�");
    jButton3.addActionListener(new Frame1_jButton3_actionAdapter(this));
    jButton3.addActionListener(new Frame1_jButton3_actionAdapter(this));
    jButton1.addActionListener(new Frame1_jButton1_actionAdapter(this));
    jButton4.setBounds(new Rectangle(259, 107, 95, 25));
    jButton4.setText("�����������");
    jButton4.addActionListener(new Frame1_jButton4_actionAdapter(this));
    jButton3.addActionListener(new Frame1_jButton3_actionAdapter(this));
    jButton5.setBounds(new Rectangle(156, 108, 93, 25));
    jButton5.setText("�Ͽ���ݿ�");
    jButton5.addActionListener(new Frame1_jButton5_actionAdapter(this));
    jButton4.addActionListener(new Frame1_jButton4_actionAdapter(this));
    jButton6.setBounds(new Rectangle(47, 243, 175, 25));
    jButton6.setText("��Լ�������г�ʼ������");
    jButton6.addActionListener(new Frame1_jButton6_actionAdapter(this));
    jButton7.setBounds(new Rectangle(247, 167, 123, 25));
    jButton7.setText("��������ͨѶ��¼");
    jButton7.addActionListener(new Frame1_jButton7_actionAdapter(this));
    jButton8.setBounds(new Rectangle(48, 183, 91, 25));
    jButton8.setText("������ݿ�");
    jButton8.addActionListener(new Frame1_jButton8_actionAdapter(this));
    jButton9.setBounds(new Rectangle(144, 184, 95, 25));
    jButton9.setText("�Ͽ���ݿ�");
    jButton9.addActionListener(new Frame1_jButton9_actionAdapter(this));
    jButton4.addActionListener(new Frame1_jButton4_actionAdapter(this));
    jButton7.addActionListener(new Frame1_jButton7_actionAdapter(this));
    jButton10.setBounds(new Rectangle(248, 204, 123, 25));
    jButton10.setText("��������ͨѶ��¼");
    jButton10.addActionListener(new Frame1_jButton10_actionAdapter(this));
    jButton11.setBounds(new Rectangle(230, 241, 155, 25));
    jButton11.setText("��������ȡ�����б�");
    jButton11.addActionListener(new Frame1_jButton11_actionAdapter(this));
    contentPane.add(jTF_Directory);
    contentPane.add(jLabel1);
    contentPane.add(jTF_FileName);
    contentPane.add(jLabel2);
    contentPane.add(jButton1);
    contentPane.add(jButton2);
    contentPane.add(jButton6);
    contentPane.add(jButton8);
    contentPane.add(jButton9);
    contentPane.add(jButton7);
    contentPane.add(jButton3);
    contentPane.add(jButton5);
    contentPane.add(jButton4);
    contentPane.add(jButton10);
    contentPane.add(jButton11);
  }

  public void jButton1_actionPerformed(ActionEvent e) {
    //String sDirectory = jTF_Directory.getText().trim();
    String sFileName = jTF_FileName.getText().trim();
    //String sFullName=sDirectory+"\\"+sFileName;
    FileLogger MyLog = new FileLogger(sFileName);
    MyLog.WriteLog("��¡���Ӽ������޹�˾��");
    MyLog.WriteLog("DataProcessErr!");
  }

  public void jButton2_actionPerformed(ActionEvent e) {
//
    Calendar NowDateTime = Calendar.getInstance();
    System.out.println(NowDateTime.getTime());
    //SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    SimpleDateFormat formatter = new SimpleDateFormat(
        "yyyyMMddHHmmss" + "___ G E D F w W a E F");
    String sDateTime = formatter.format(NowDateTime.getTime());
    System.out.println(sDateTime);

    // =Calendar.getInstance() ;
  }

  public void jButton3_actionPerformed(ActionEvent e) {
    gDataAccess = new Glu_DataAccess("");
    if (gDataAccess.LogIn(10) == false) {
      System.out.println("������ݿ�ʧ�ܣ�");
    }
  }

  public void jButton4_actionPerformed(ActionEvent e) {
    SFE_HistoryData HiltoryList[] = new SFE_HistoryData[2];

    Calendar cLogTime = Calendar.getInstance();
    System.out.println("pzh" + cLogTime.getTime());
    for (int k = 0; k < 1; k++) {
      HiltoryList[0] = new SFE_HistoryData();
      HiltoryList[0].SetMeasuredPointNo(1);
      HiltoryList[0].SetMeasuredPointType(10);
      HiltoryList[0].SetTaskNo(1);
      HiltoryList[0].SetTaskDateTime("20060111110800");

      HiltoryList[0].DataItemCountAdd();
      for (int i = 0; i < 5; i++) {
        HiltoryList[0].DataItemAdd(Integer.toString(9010 + i), "00000000");
      }

      HiltoryList[1] = new SFE_HistoryData();
      HiltoryList[1].SetMeasuredPointNo(1);
      HiltoryList[1].SetMeasuredPointType(10);
      HiltoryList[1].SetTaskNo(1);
      HiltoryList[1].SetTaskDateTime("20060111110800");
      HiltoryList[1].DataItemCountAdd();
      for (int i = 0; i < 5; i++) {
        HiltoryList[1].DataItemAdd(Integer.toString(9020 + i), "00000000");
      }
      gDataAccess.SaveHistoryData("99999999".toCharArray(),100, HiltoryList, 2);
      //FileLogger.WriteLog("c:\\Log","fjdkal;fjdkalsdka;");
    }
    cLogTime = Calendar.getInstance();
    System.out.println("pzh11" + cLogTime.getTime());
  }

  public void jButton5_actionPerformed(ActionEvent e) {
    gDataAccess.LogOut(10);
  }

  public void jButton6_actionPerformed(ActionEvent e) {
    Glu_DataAccess DataAccess = new Glu_DataAccess("");
    //SPE_TaskInfoList TermialZheJiangCommandInfoList = new SPE_TaskInfoList();
    DataAccess.LogIn(0);
    //TermialZheJiangCommandInfoList = DataAccess.GetTaskInfoList(105);
    //DataAccess.close();
    DataAccess.LogOut(0);
  }

  public void jButton7_actionPerformed(ActionEvent e) {

    Calendar cLogTime = Calendar.getInstance();
    System.out.println(cLogTime.getTime());
    ArrayList <Struct_CommRecordItem>CommRecordList = new ArrayList<Struct_CommRecordItem>();

    for (int i = 0; i < 100; i++) {
      CommRecordList.clear();
      for (int j = 0; j < 50; j++) {
        Struct_CommRecordItem CommRecord = new Struct_CommRecordItem();
        CommRecord.SetChannelType(41);
        CommRecord.SetMessageContent(
            "111111111111111111111111111111111111111111111111111111111111");
        CommRecord.SetSourceAddress("127.0.0.1:1025");
        CommRecord.SetTargetAddress("127.0.0.1:1024");
        CommRecord.SetTerminalAddress("99999999");
        CommRecordList.add(CommRecord);
      }
      gDataAccess.SaveUpFlowRecordToDB(CommRecordList);
    }
    cLogTime = Calendar.getInstance();
    System.out.println(cLogTime.getTime());
  }

  public void jButton8_actionPerformed(ActionEvent e) {
    gDataAccess = new Glu_DataAccess("");
    if (gDataAccess.LogIn(30) == false) {
      System.out.println("������ݿ�ʧ�ܣ�");
    }
  }

  public void jButton9_actionPerformed(ActionEvent e) {
    gDataAccess.LogOut(30);
  }

  public void jButton10_actionPerformed(ActionEvent e) {
    Calendar cLogTime = Calendar.getInstance();
    System.out.println(cLogTime.getTime());
    ArrayList <Struct_CommRecordItem>CommRecordList = new ArrayList<Struct_CommRecordItem>();

    for (int i = 0; i < 100; i++) {
      CommRecordList.clear();
      for (int j = 0; j < 50; j++) {
        Struct_CommRecordItem CommRecord = new Struct_CommRecordItem();
        CommRecord.SetChannelType(41);
        CommRecord.SetMessageContent(
            "111111111111111111111111111111111111111111111111111111111111");
        CommRecord.SetSourceAddress("127.0.0.1:1025");
        CommRecord.SetTargetAddress("127.0.0.1:1024");
        CommRecord.SetTerminalAddress("99999999");
        CommRecordList.add(CommRecord);
      }
      gDataAccess.SaveDownFlowRecordToDB(CommRecordList);
    }
    cLogTime = Calendar.getInstance();
    System.out.println(cLogTime.getTime());
  }

  public void jButton11_actionPerformed(ActionEvent e) {
    SPE_CommandInfoList TermialZheJiangCommandInfoList = new
        SPE_CommandInfoList();
    Glu_DataAccess DataAccess = new Glu_DataAccess("");
    DataAccess.LogIn(0);
    TermialZheJiangCommandInfoList = DataAccess.GetCommandInfoList(80); //��ʼ���ն��㽭������Ϣ����
    DataAccess.LogOut(0);
    System.out.println(TermialZheJiangCommandInfoList.GetFCommandCount());

  }

  class Frame1_jButton11_actionAdapter
      implements ActionListener {
    private Frame1 adaptee;
    Frame1_jButton11_actionAdapter(Frame1 adaptee) {
      this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
      adaptee.jButton11_actionPerformed(e);
    }
  }

  class Frame1_jButton10_actionAdapter
      implements ActionListener {
    private Frame1 adaptee;
    Frame1_jButton10_actionAdapter(Frame1 adaptee) {
      this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {

      adaptee.jButton10_actionPerformed(e);
    }
  }

  class Frame1_jButton9_actionAdapter
      implements ActionListener {
    private Frame1 adaptee;
    Frame1_jButton9_actionAdapter(Frame1 adaptee) {
      this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
      adaptee.jButton9_actionPerformed(e);
      //adaptee.jButton9_actionPerformed(e);
    }
  }

  class Frame1_jButton8_actionAdapter
      implements ActionListener {
    private Frame1 adaptee;
    Frame1_jButton8_actionAdapter(Frame1 adaptee) {
      this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
      adaptee.jButton8_actionPerformed(e);
      //adaptee.jButton8_actionPerformed(e);
    }
  }

  class Frame1_jButton7_actionAdapter
      implements ActionListener {
    private Frame1 adaptee;
    Frame1_jButton7_actionAdapter(Frame1 adaptee) {
      this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {

      adaptee.jButton7_actionPerformed(e); //adaptee.jButton7_actionPerformed(e);
    }
  }

  class Frame1_jButton6_actionAdapter
      implements ActionListener {
    private Frame1 adaptee;
    Frame1_jButton6_actionAdapter(Frame1 adaptee) {
      this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
      adaptee.jButton6_actionPerformed(e);
      //adaptee.jButton6_actionPerformed(e);
    }
  }

  class Frame1_jButton5_actionAdapter
      implements ActionListener {
    //private Frame1 adaptee;
    Frame1_jButton5_actionAdapter(Frame1 adaptee) {
      //this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
      //adaptee.jButton5_actionPerformed(e);
    }
  }

  class Frame1_jButton4_actionAdapter
      implements ActionListener {
    private Frame1 adaptee;
    Frame1_jButton4_actionAdapter(Frame1 adaptee) {
      this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {

      adaptee.jButton4_actionPerformed(e); //adaptee.jButton4_actionPerformed(e);
    }
  }

  class Frame1_jButton3_actionAdapter
      implements ActionListener {
    private Frame1 adaptee;
    Frame1_jButton3_actionAdapter(Frame1 adaptee) {
      this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
      adaptee.jButton3_actionPerformed(e);

      //adaptee.jButton3_actionPerformed(e);
    }
  }

  class Frame1_jButton2_actionAdapter
      implements ActionListener {
    private Frame1 adaptee;
    Frame1_jButton2_actionAdapter(Frame1 adaptee) {
      this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
      adaptee.jButton2_actionPerformed(e);
      //adaptee.jButton2_actionPerformed(e);
    }
  }

  class Frame1_jButton1_actionAdapter
      implements ActionListener {
    private Frame1 adaptee;
    Frame1_jButton1_actionAdapter(Frame1 adaptee) {
      this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
      adaptee.jButton1_actionPerformed(e);

      //adaptee.jButton1_actionPerformed(e);
    }
  }
}

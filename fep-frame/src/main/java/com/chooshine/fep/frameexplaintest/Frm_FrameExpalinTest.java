package com.chooshine.fep.frameexplaintest;

import com.chooshine.fep.FrameExplain.FE_FrameExplain;
import com.chooshine.fep.FrameExplain.Struct_FrameInfo;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import java.awt.*;
//import com.borland.jbcl.layout.XYLayout;
//import com.borland.jbcl.layout.*;

public class Frm_FrameExpalinTest extends JFrame {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JPanel contentPane;
    JMenuBar jMenuBar1 = new JMenuBar();
    JMenu jMenuFile = new JMenu();
    JMenuItem jMenuFileExit = new JMenuItem();
    JButton jButton_BuildFrame = new JButton();
    JTextField jTF_ZDLJDZ = new JTextField();
    JTextField jTF_ZZXH = new JTextField();
    JTextField jTF_MLXH = new JTextField();
    JTextField jTF_KZM = new JTextField();
    JTextField jTF_SourcdData = new JTextField();
    JLabel jLabel1 = new JLabel();
    JLabel jLabel2 = new JLabel();
    JLabel jLabel3 = new JLabel();
    JComboBox jCB_GYH = new JComboBox();
    JLabel jLabel4 = new JLabel();
    JLabel jLabel6 = new JLabel();
    JLabel jLabel7 = new JLabel();
    JButton jButton_FrameExplain = new JButton();
    JPanel jPanel1 = new JPanel();
    JPanel jPanel2 = new JPanel();
    JScrollPane jScrollPane2 = new JScrollPane();
    BorderLayout borderLayout1 = new BorderLayout();
    JTextArea jTextArea_Log = new JTextArea();
    public Frm_FrameExpalinTest() {
        try {
            setDefaultCloseOperation(EXIT_ON_CLOSE);
            jbInit();
        } catch (Exception exception) {
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
        setSize(new Dimension(442, 329));
        setTitle("Frame Title");
        jMenuFile.setText("File");
        jMenuFileExit.setText("Exit");
        jMenuFileExit.addActionListener(new
                                        Frm_FrameExpalinTest_jMenuFileExit_ActionAdapter(this));
        jButton_BuildFrame.setBounds(new Rectangle(361, 12, 69, 23));
        jButton_BuildFrame.setFont(new java.awt.Font("����", Font.PLAIN, 11));
        jButton_BuildFrame.setText("��֡");
        jButton_BuildFrame.addActionListener(new
                                             Frm_FrameExpalinTest_jButton_BuildFrame_actionAdapter(this));
        jTF_ZDLJDZ.setFont(new java.awt.Font("����", Font.PLAIN, 11));
        jTF_ZDLJDZ.setText("91010001");
        jTF_ZDLJDZ.setBounds(new Rectangle(82, 35, 104, 20));
        jTF_ZZXH.setFont(new java.awt.Font("����", Font.PLAIN, 11));
        jTF_ZZXH.setText("1");
        jTF_ZZXH.setBounds(new Rectangle(269, 33, 74, 20));
        jTF_MLXH.setFont(new java.awt.Font("����", Font.PLAIN, 11));
        jTF_MLXH.setText("1");
        jTF_MLXH.setBounds(new Rectangle(268, 7, 74, 20));
        jTF_KZM.setFont(new java.awt.Font("����", Font.PLAIN, 11));
        jTF_KZM.setText("01");
        jTF_KZM.setBounds(new Rectangle(82, 59, 103, 20));
        jTF_SourcdData.setFont(new java.awt.Font("����", Font.PLAIN, 11));
        jTF_SourcdData.setText(
                "6891010000810468811000010000000000000030800027112306048E16");
        jTF_SourcdData.setBounds(new Rectangle(83, 85, 267, 20));
        jLabel1.setFont(new java.awt.Font("����", Font.PLAIN, 11));
        jLabel1.setText("��Լ�ţ�");
        jLabel1.setBounds(new Rectangle(10, 11, 44, 14));
        jLabel2.setFont(new java.awt.Font("����", Font.PLAIN, 11));
        jLabel2.setText("�ն��߼���ַ��");
        jLabel2.setBounds(new Rectangle(8, 35, 77, 14));
        jLabel3.setFont(new java.awt.Font("����", Font.PLAIN, 11));
        jLabel3.setText("��վ��ţ�");
        jLabel3.setBounds(new Rectangle(207, 30, 55, 14));
        jLabel4.setFont(new java.awt.Font("����", Font.PLAIN, 11));
        jLabel4.setText("������ţ�");
        jLabel4.setBounds(new Rectangle(208, 10, 55, 14));
        jLabel6.setFont(new java.awt.Font("����", Font.PLAIN, 11));
        jLabel6.setText("�����룺");
        jLabel6.setBounds(new Rectangle(10, 60, 44, 14));
        jLabel7.setFont(new java.awt.Font("����", Font.PLAIN, 11));
        jLabel7.setText("���֡/�����ݣ�");
        jLabel7.setBounds(new Rectangle(7, 86, 84, 14));
        jButton_FrameExplain.setBounds(new Rectangle(362, 55, 67, 23));
        jButton_FrameExplain.setFont(new java.awt.Font("����", Font.PLAIN, 11));
        jButton_FrameExplain.setText("��֡");
        jButton_FrameExplain.addActionListener(new
                                               Frm_FrameExpalinTest_jButton_FrameExplain_actionAdapter(this));
        contentPane.setFont(new java.awt.Font("����", Font.PLAIN, 11));
        contentPane.setMinimumSize(new Dimension(1, 2));
        jPanel1.setBackground(new Color(212, 208, 200));
        jPanel1.setBorder(BorderFactory.createRaisedBevelBorder());
        jPanel1.setToolTipText("");
        jPanel1.setBounds(new Rectangle( -2, 0, 435, 120));
        jPanel1.setLayout(null);
        jPanel2.setBackground(SystemColor.inactiveCaptionBorder);
        jPanel2.setBorder(BorderFactory.createRaisedBevelBorder());
        jPanel2.setBounds(new Rectangle(0, 116, 433, 199));
        jPanel2.setLayout(borderLayout1);
        jScrollPane2.getViewport().setBackground(new Color(152, 208, 200));
        jScrollPane2.setToolTipText("");
        jTextArea_Log.setText("");
        jCB_GYH.setToolTipText("");
        jCB_GYH.setBounds(new Rectangle(82, 10, 106, 22));
        jCB_GYH.addActionListener(new
                                  Frm_FrameExpalinTest_jCB_GYH_actionAdapter(this));
        jMenuBar1.add(jMenuFile);
        jMenuFile.add(jMenuFileExit);
        jPanel1.add(jLabel1, null);
        jPanel1.add(jLabel7, null);
        jPanel1.add(jButton_FrameExplain, null);
        jPanel1.add(jButton_BuildFrame, null);
        jPanel1.add(jTF_ZZXH, null);
        jPanel1.add(jLabel4, null);
        jPanel1.add(jLabel3, null);
        jPanel1.add(jTF_MLXH, null);
        jPanel1.add(jLabel2, null);
        jPanel1.add(jTF_ZDLJDZ, null);
        jPanel1.add(jCB_GYH, null);
        jPanel1.add(jTF_SourcdData, null);
        jPanel1.add(jTF_KZM, null);
        jPanel1.add(jLabel6, null);
        contentPane.add(jPanel2, null);
        jPanel2.add(jScrollPane2, java.awt.BorderLayout.CENTER);
        contentPane.add(jPanel1, null);
        jScrollPane2.getViewport().add(jTextArea_Log);
        setJMenuBar(jMenuBar1);
        jCB_GYH.addItem("000-��ָ����Լ");
        jCB_GYH.addItem("080-��_�㽭��Լ");
        jCB_GYH.addItem("100-��_�����Լ");
        jCB_GYH.addItem("105-��_����Լ");
        jCB_GYH.addItem("106-��_698��Լ");
        jCB_GYH.addItem("130-��_230��Լ");
    }

    /**
     * File | Exit action performed.
     *
     * @param actionEvent ActionEvent
     */
    void jMenuFileExit_actionPerformed(ActionEvent actionEvent) {
        System.exit(0);
    }

    public void jButton_BuildFrame_actionPerformed(ActionEvent e) {

        String sZDLJDZ = jTF_ZDLJDZ.getText();
        String sKZM = jTF_KZM.getText();
        String sDataContent = jTF_SourcdData.getText();

        String sGYH = jCB_GYH.getSelectedItem().toString();
        sGYH = sGYH.substring(0, 3);
        int iGYH = Integer.parseInt(sGYH);
        int iZZXH = Integer.parseInt(jTF_ZZXH.getText());
        int iMLXH = Integer.parseInt(jTF_MLXH.getText());

        if ((iGYH==100)&& sZDLJDZ.length()!=10){
            jTextArea_Log.append("�ն��߼���ַӦΪ 10λ:"  + "\r\n");
        }

        FE_FrameExplain FrameExplain;
        FrameExplain = new FE_FrameExplain();
        char[] cGYML;
        cGYML = FrameExplain.IFE_BuildFrame(sZDLJDZ.toCharArray(), iGYH, iZZXH,
                                            iMLXH, sKZM.toCharArray(),
                                            sDataContent.toCharArray());
        if (cGYML == null) {
            jTextArea_Log.append("��֡ʧ��" + "\r\n");
        } else {
            String sGYML = new String(cGYML);
            jTextArea_Log.append("��֡�ɹ�:" + sGYML + "\r\n");
        }

    }

    public void jButton_FrameExplain_actionPerformed(ActionEvent e) {
        FE_FrameExplain FrameExplain;
        FrameExplain = new FE_FrameExplain();
        Struct_FrameInfo FrameInfo;
        FrameInfo = new Struct_FrameInfo();
        String sSourceData = jTF_SourcdData.getText();


        //String sGYH=jCB_GYH.getSelectedItem().toString();
        //int iGYH = Integer.parseInt(sGYH.substring(0, 3), 10);
        jTextArea_Log.append(sSourceData + "\r\n");
        FrameInfo = FrameExplain.IFE_FrameExplain(0, sSourceData.toCharArray());
        if (FrameInfo == null) {
            jTextArea_Log.append("��Լ֡����ʧ��!");
        } else {
            String sShow = new String(FrameInfo.TerminalLogicAdd); //�ն��߼���ַ
            jTextArea_Log.append("  �ն��߼���ַ:" + sShow);
            String sKZM = new String(FrameInfo.ControlCode); //������;
            jTextArea_Log.append("  ������:" + sKZM);
            String sGNM = new String(FrameInfo.FunctionCode); //������;
            jTextArea_Log.append("  ������:" + sGNM);
            String sCSBM = new String(FrameInfo.ManufacturerCode); //���̱���;
            jTextArea_Log.append("  ���̱���:" + sCSBM + "\r\n");
            sShow = Integer.toString(FrameInfo.TermialProtocolNo); //�ն˹�Լ��;
            jTextArea_Log.append("  �ն˹���:" + sShow);
            sShow = Integer.toString(FrameInfo.StationNo); //��վ���;
            jTextArea_Log.append("  ��վ���:" + sShow);
            sShow = Integer.toString(FrameInfo.CommandSeq); //�������;
            jTextArea_Log.append("  �������:" + sShow);
            sShow = Integer.toString(FrameInfo.FrameSeq); //֡�����;
            jTextArea_Log.append("  ֡�����:" + sShow + "\r\n");

            sShow = Integer.toString(FrameInfo.DataType); //�������;
            jTextArea_Log.append("  �������:" + sShow);
            String sFrameData = new String(FrameInfo.FrameData); //��Լ���������;
            jTextArea_Log.append("  ��Լ���������:" + sFrameData + "\r\n");
        }
    }

    public void jCB_GYH_actionPerformed(ActionEvent e) {

    }
}


class Frm_FrameExpalinTest_jCB_GYH_actionAdapter implements ActionListener {
    private Frm_FrameExpalinTest adaptee;
    Frm_FrameExpalinTest_jCB_GYH_actionAdapter(Frm_FrameExpalinTest adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.jCB_GYH_actionPerformed(e);
    }
}


class Frm_FrameExpalinTest_jButton_FrameExplain_actionAdapter implements
        ActionListener {
    private Frm_FrameExpalinTest adaptee;
    Frm_FrameExpalinTest_jButton_FrameExplain_actionAdapter(
            Frm_FrameExpalinTest adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.jButton_FrameExplain_actionPerformed(e);
    }
}


class Frm_FrameExpalinTest_jButton_BuildFrame_actionAdapter implements
        ActionListener {
    private Frm_FrameExpalinTest adaptee;
    Frm_FrameExpalinTest_jButton_BuildFrame_actionAdapter(Frm_FrameExpalinTest
            adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.jButton_BuildFrame_actionPerformed(e);
    }
}


class Frm_FrameExpalinTest_jMenuFileExit_ActionAdapter implements
        ActionListener {
    Frm_FrameExpalinTest adaptee;

    Frm_FrameExpalinTest_jMenuFileExit_ActionAdapter(Frm_FrameExpalinTest
            adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent actionEvent) {
        adaptee.jMenuFileExit_actionPerformed(actionEvent);
    }
}

package com.chooshine.fep.FrameDataAreaExplain;

import com.chooshine.fep.ConstAndTypeDefine.Glu_ConstDefine;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import java.awt.*;
import javax.swing.JButton;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JLabel;
import javax.swing.JEditorPane;
import java.util.ArrayList;
import javax.swing.JScrollPane;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.Connection;
import javax.swing.UIManager;
import javax.swing.JCheckBox;

//import oracle.*;
import java.io.File;
import java.util.Properties;
import javax.swing.JOptionPane;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream; //
import java.net.URI;

public class TestForm extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	JPanel contentPane;

	JTextArea jTextArea2 = new JTextArea();

	JLabel jLabel2 = new JLabel();

	JButton jButton2 = new JButton();

	JEditorPane jEditorPane2 = new JEditorPane();

	JButton jButton3 = new JButton();

	JTextArea jTextArea4 = new JTextArea();

	JTextArea jTextArea3 = new JTextArea();

	JButton jButton4 = new JButton();

	JLabel jLabel1 = new JLabel();

	JPanel jPanel1 = new JPanel();

	JTextArea TextArea_GYH = new JTextArea();

	JLabel jLabel4 = new JLabel();

	JLabel jLabel5 = new JLabel();

	JTextArea TextArea_TerAddr = new JTextArea();

	JButton jButton1 = new JButton();

	JTextArea TextArea_CLDH = new JTextArea();

	JButton jButton5 = new JButton();

	JTextArea TextArea_Command = new JTextArea();

	JLabel jLabel6 = new JLabel();

	JTextArea TextArea_DXXBZ = new JTextArea();

	// JLabel jLabel3 = new JLabel();
	JScrollPane jScrollPane1 = new JScrollPane();

	JTextArea jTextArea1 = new JTextArea();

	JButton jButton_BuildFrame = new JButton();

	JLabel jLabel7 = new JLabel();

	JTextArea jTextArea_DBGY = new JTextArea();

	JCheckBox jCheckBox_Set = new JCheckBox();

	JLabel jLabel8 = new JLabel();

	JTextArea jTextArea_ZCDS = new JTextArea();

	JTextArea jTextArea_SJMD = new JTextArea();

	JLabel jLabel9 = new JLabel();

	JLabel jLabel10 = new JLabel();

	JTextArea jTextArea_QSZZ = new JTextArea();

	JLabel jLabel11 = new JLabel();

	JLabel jLabel12 = new JLabel();

	JTextArea jTextArea_QSSJ = new JTextArea();

	JTextArea jTextArea_JSSJ = new JTextArea();

	JCheckBox jCheckBox_LSSJZC = new JCheckBox();

	JLabel jLabel1_YCSJZC = new JLabel();

	JLabel jLabel = new JLabel();

	JCheckBox jCheckBox_YCSJZC = new JCheckBox();

	JLabel jLabel13 = new JLabel();

	JTextArea jTextArea_SJLX = new JTextArea();

	JLabel jLabel14 = new JLabel();

	JLabel jLabel15 = new JLabel();

	JLabel jLabel16 = new JLabel();

	JLabel jLabel17 = new JLabel();

	JCheckBox jCheckBox_DQSJZC = new JCheckBox();

	JCheckBox jCheckBox_ACTION = new JCheckBox();

	JCheckBox jCheckBox_GET = new JCheckBox();

	GetFrameInfo GetF = new GetFrameInfo();

	FrameDataAreaExplainGluMethod GluMethod = new FrameDataAreaExplainGluMethod(); // ��������������

	private int FDataItemCount = 0;

	ArrayList<String> FDataItemList = new ArrayList<String>();

	SFE_MeasuredPointInfor FMeasuredPointInfor = new SFE_MeasuredPointInfor();

	ArrayList<SFE_ParamItem> FParamItemList = new ArrayList<SFE_ParamItem>();

	String FConnectString = "";

	String FUserName = "";

	String FPassWord = "";

	int FDBType = 10;
	IFE_FrameDataAreaExplain FrameDataAreaExplain = new IFE_FrameDataAreaExplain("");
  /*  
	static IFE_FrameDataAreaExplain FrameDataAreaExplain = null;

	static URI uri;
	static {
		try {
			uri = new URI("file:////172.16.241.15/fep/CommService.config");

			FrameDataAreaExplain = new IFE_FrameDataAreaExplain(uri);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}smb://172.16.241.15/fep/CommService.config
smb://administrator:hexing@172.16.251.239/g$/fep/CommService.config
	*/
	//static IFE_FrameDataAreaExplain FrameDataAreaExplain =  new IFE_FrameDataAreaExplain("smb://172.16.241.15/fep/CommService.config");

	
	public TestForm() {
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
		} catch (ClassNotFoundException ex) {
			System.out.println("Class Not Found");
		}
		InitialList();
		try {
			setDefaultCloseOperation(EXIT_ON_CLOSE);
			jbInit();
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	public static void main(String[] args) {
		TestForm TestFrm = new TestForm();
		TestFrm.show();
	}

	/**
	 * Component initialization.
	 * 
	 * @throws java.lang.Exception
	 */
	private void jbInit() throws Exception {
		contentPane = (JPanel) getContentPane();
		contentPane.setLayout(null);
		setSize(new Dimension(500, 585));
		setTitle("Frame Title");
		jTextArea2.setBounds(new Rectangle(9, 129, 58, 19));
		jLabel2.setText("Raw Frame:");
		jLabel2.setBounds(new Rectangle(13, 80, 76, 16));
		jButton2.setBounds(new Rectangle(9, 103, 79, 20));
		jButton2.setText("Check");
		jButton2.addActionListener(new TestForm_jButton2_actionAdapter(this));
		jEditorPane2
				.setText("689101444480046882310004051024150001020F3538290188891700740562007425380099161100413636000474060052170"
						+ "30052271500991611009516");
		jEditorPane2.setBounds(new Rectangle(98, 81, 377, 67));
		jButton3.setBounds(new Rectangle(10, 198, 127, 22));
		jButton3.setActionCommand("Explain");
		jButton3.setText("Cycle Explain");
		jButton3.addActionListener(new TestForm_jButton3_actionAdapter(this));
		jTextArea4.setText("1");
		jTextArea4.setBounds(new Rectangle(231, 200, 52, 19));
		contentPane.setBackground(UIManager.getColor("TabbedPane.light"));
		contentPane.setFont(new java.awt.Font("����", Font.PLAIN, 11));
		contentPane.setPreferredSize(new Dimension(900, 929));
		contentPane.setToolTipText("");
		jTextArea3.setToolTipText("");
		jTextArea3.setText("select * from run_sxtxjl where rownum<=10000");
		jTextArea3.setBounds(new Rectangle(151, 162, 325, 22));
		jButton4.setBounds(new Rectangle(10, 163, 130, 22));
		jButton4.setActionCommand("");
		jButton4.setSelectedIcon(null);
		jButton4.setText("Explain dbframe");
		jButton4.addActionListener(new TestForm_jButton4_actionAdapter(this));
		jLabel1.setText("Cycle times:");
		jLabel1.setBounds(new Rectangle(151, 201, 76, 17));
		jPanel1.setBackground(UIManager
				.getColor("TextField.inactiveBackground"));
		jPanel1.setBounds(new Rectangle(10, 1, 495, 71));
		jPanel1.setLayout(null);
		TextArea_GYH.setToolTipText("");
		TextArea_GYH.setText("100");
		TextArea_GYH.setBounds(new Rectangle(63, 9, 28, 16));
		jLabel4.setText("Protocol:");
		jLabel4.setBounds(new Rectangle(2, 9, 58, 16));
		jLabel5.setText("TermianlAddr:");
		jLabel5.setBounds(new Rectangle(246, 8, 89, 16));
		TextArea_TerAddr.setText("91010001");
		TextArea_TerAddr.setBounds(new Rectangle(330, 9, 58, 16));
		jButton1.setBounds(new Rectangle(71, 37, 138, 20));
		jButton1.setText("Add MeasurePoint");
		jButton1.addActionListener(new TestForm_jButton1_actionAdapter(this));
		TextArea_CLDH.setText("1");
		TextArea_CLDH.setBounds(new Rectangle(5, 37, 57, 20));
		jButton5.setBounds(new Rectangle(328, 37, 118, 20));
		jButton5.setText("Add Command");
		jButton5.addActionListener(new TestForm_jButton5_actionAdapter(this));
		TextArea_Command.setBounds(new Rectangle(223, 37, 94, 20));
		jLabel6.setText("DXXBZ:");
		jLabel6.setBounds(new Rectangle(390, 8, 48, 16));
		TextArea_DXXBZ.setText("10");
		TextArea_DXXBZ.setBounds(new Rectangle(440, 8, 28, 16));
		// jLabel3.setText("Times");
		// jLabel3.setBounds(new Rectangle(265, 202, 49, 16));
		jScrollPane1.setBounds(new Rectangle(11, 231, 466, 225));
		jTextArea1.setText("");
		jButton_BuildFrame.setBounds(new Rectangle(330, 197, 64, 21));
		jButton_BuildFrame.setText("Build");
		jButton_BuildFrame
				.addActionListener(new TestForm_jButton6_actionAdapter(this));
		jLabel7.setText("Ammeter Protocol:");
		jLabel7.setBounds(new Rectangle(97, 8, 112, 16));
		jTextArea_DBGY.setToolTipText("");
		jTextArea_DBGY.setText("");
		jTextArea_DBGY.setBounds(new Rectangle(211, 9, 29, 16));
		jCheckBox_Set.setText("jCheckBox1");
		jCheckBox_Set.setBounds(new Rectangle(402, 201, 21, 12));
		jLabel8.setOpaque(true);
		jLabel8.setVerifyInputWhenFocusTarget(true);
		jLabel8.setText("Set");
		jLabel8.setBounds(new Rectangle(426, 199, 38, 16));
		jTextArea_ZCDS.setText("2");
		jTextArea_ZCDS.setBounds(new Rectangle(82, 488, 43, 19));
		jTextArea_SJMD.setText("1");
		jTextArea_SJMD.setBounds(new Rectangle(219, 487, 29, 19));
		jLabel9.setText("Call Count:");
		jLabel9.setBounds(new Rectangle(12, 488, 69, 19));
		jLabel10.setText("Datatype:");
		jLabel10.setBounds(new Rectangle(253, 487, 64, 19));
		jTextArea_QSZZ.setText("1");
		jTextArea_QSZZ.setBounds(new Rectangle(82, 465, 42, 19));
		jLabel11.setText("Start Point:");
		jLabel11.setBounds(new Rectangle(13, 465, 64, 19));
		jLabel12.setText("Start Time:");
		jLabel12.setBounds(new Rectangle(131, 465, 64, 19));
		jTextArea_QSSJ.setText("20051222111111");
		jTextArea_QSSJ.setBounds(new Rectangle(219, 464, 119, 19));
		jLabel15.setText("End Time:");
		jLabel15.setBounds(new Rectangle(12, 511, 64, 19));
		jTextArea_JSSJ.setText("20051222111111");
		jTextArea_JSSJ.setBounds(new Rectangle(82, 511, 129, 19));
		jCheckBox_LSSJZC.setText("jCheckBox1");
		jCheckBox_LSSJZC.setBounds(new Rectangle(342, 495, 21, 12));
		jLabel1_YCSJZC.setText("GW Alarm");
		jLabel1_YCSJZC.setBounds(new Rectangle(365, 463, 112, 13));
		jLabel.setText("GW Second Data");
		jLabel.setBounds(new Rectangle(365, 492, 113, 16));
		jCheckBox_YCSJZC.setText("jCheckBox1");
		jCheckBox_YCSJZC.setBounds(new Rectangle(342, 464, 21, 12));
		jLabel13.setText("Data Density:");
		jLabel13.setBounds(new Rectangle(131, 488, 74, 19));
		jTextArea_SJLX.setText("1");
		jTextArea_SJLX.setBounds(new Rectangle(308, 487, 29, 19));
		jLabel14.setText("GW First Data");
		jLabel14.setBounds(new Rectangle(365, 477, 113, 16));
		jCheckBox_DQSJZC.setText("jCheckBox1");
		jCheckBox_DQSJZC.setBounds(new Rectangle(342, 480, 21, 12));
		jLabel16.setText("DLMS-ACTION");
		jLabel16.setBounds(new Rectangle(365, 511, 113, 16));
		jCheckBox_ACTION.setText("jCheckBox1");
		jCheckBox_ACTION.setBounds(new Rectangle(342, 511, 21, 12));
		jLabel17.setText("DLMS-With time read");
		jLabel17.setBounds(new Rectangle(365, 531, 123, 16));
		jCheckBox_GET.setText("jCheckBox1");
		jCheckBox_GET.setBounds(new Rectangle(342, 531, 21, 12));
		jPanel1.add(TextArea_CLDH);
		jPanel1.add(jButton1);
		jPanel1.add(jButton5);
		jPanel1.add(jLabel4);
		jPanel1.add(jLabel7);
		jPanel1.add(jLabel6);
		jPanel1.add(jLabel5);
		jPanel1.add(jTextArea_DBGY);
		jPanel1.add(TextArea_GYH);
		jPanel1.add(TextArea_TerAddr);
		jPanel1.add(TextArea_DXXBZ);
		jPanel1.add(TextArea_Command);
		contentPane.add(jPanel1);
		contentPane.add(jEditorPane2);
		contentPane.add(jTextArea2);
		contentPane.add(jTextArea3);
		contentPane.add(jScrollPane1);
		contentPane.add(jButton2);
		contentPane.add(jButton3);
		contentPane.add(jLabel1);
		contentPane.add(jLabel2);
		contentPane.add(jTextArea4);
		// contentPane.add(jLabel3);
		contentPane.add(jButton4);
		contentPane.add(jButton_BuildFrame);
		contentPane.add(jLabel11);
		contentPane.add(jTextArea_QSZZ);
		contentPane.add(jLabel9);
		contentPane.add(jTextArea_ZCDS);
		contentPane.add(jTextArea_QSSJ);
		contentPane.add(jLabel15);
		contentPane.add(jTextArea_JSSJ);
		contentPane.add(jLabel8);
		contentPane.add(jCheckBox_Set);
		contentPane.add(jLabel12);
		contentPane.add(jLabel13);
		contentPane.add(jLabel10);
		contentPane.add(jTextArea_SJLX);
		contentPane.add(jTextArea_SJMD);
		contentPane.add(jLabel1_YCSJZC);
		contentPane.add(jCheckBox_YCSJZC);
		contentPane.add(jLabel14);
		contentPane.add(jCheckBox_DQSJZC);
		contentPane.add(jCheckBox_LSSJZC);
		contentPane.add(jLabel16);
		contentPane.add(jCheckBox_ACTION);
		contentPane.add(jLabel17);
		contentPane.add(jCheckBox_GET);
		contentPane.add(jLabel);
		jScrollPane1.getViewport().add(jTextArea1);
	}

	public void jButton2_actionPerformed(ActionEvent e) {
		String sFrame = "";
		sFrame = jEditorPane2.getText();
		if (GetFrameInfo.gTerminalProtocolCheckOfZheJiang(sFrame)) {
			jTextArea2.setText("ZheJiang");
			jEditorPane2.setText(GetFrameInfo.gGetParityByteOfZheJiang(sFrame));
		} else if (GetFrameInfo.gTerminalProtocolCheckOfHuaLong(sFrame)) {
			jTextArea2.setText("Hualong");
			jEditorPane2.setText(GetFrameInfo.gGetParityByteOfHuaLong(sFrame));
		} else if (GetFrameInfo.gTerminalProtocolCheckOfQuanGuo(sFrame)) {
			jTextArea2.setText("Quanguo");
			jEditorPane2.setText(GetFrameInfo.gGetParityByteOfQuanGuo(sFrame));
		} else {
			jTextArea2.setText("Undefined");
		}
	}

	public void jButton3_actionPerformed(ActionEvent e) {
		String sFrame = jEditorPane2.getText().trim();
		if (sFrame.equals("")) {
			jTextArea1.append("Please input raw frame" + "\r\n");
			return;
		}
		int iLen = 0;
		String sKZM = "";
		String sGNM = "";
		String sFrameArea = "";
		String sSEQ = "";
		String sCommandID = "";
		String sTerminalAddress = "";
		int iGYH = 0;
		int iTpV = 0;
		if (GetFrameInfo.gTerminalProtocolCheckOfZheJiang(sFrame)) {
			if (!TextArea_GYH.getText().trim().equals("")) {
				iGYH = Integer.parseInt(TextArea_GYH.getText().trim());
			} else {
				iGYH = 80;
			}
			sTerminalAddress = sFrame.substring(2, 6) + sFrame.substring(8, 10)
					+ sFrame.substring(6, 8);
			sKZM = sFrame.substring(16, 18);
			iLen = Integer.parseInt((sFrame.substring(20, 22) + sFrame
					.substring(18, 20)), 16) * 2; // ������ֽڳ���
			sFrameArea = sFrame.substring(22, 22 + iLen);
		} else if (GetFrameInfo.gTerminalProtocolCheckOfQuanGuo(sFrame)) {
			if (!TextArea_GYH.getText().trim().equals("")) {
				iGYH = Integer.parseInt(TextArea_GYH.getText().trim());
			} else {
				iGYH = 100;
			}
			sTerminalAddress = sFrame.substring(16, 18)
					+ sFrame.substring(14, 16) + sFrame.substring(20, 22)
					+ sFrame.substring(18, 20);
			sKZM = sFrame.substring(12, 14);
			sGNM = sFrame.substring(24, 26);
			sSEQ = sFrame.substring(26, 28); // ȡ������
			iTpV = Integer.parseInt(sSEQ, 16) & 128; // ʱ���ǩ��Чλ
			iLen = (Integer.parseInt((sFrame.substring(4, 6) + sFrame
					.substring(2, 4)), 16) >> 2) * 2;
			if (iTpV == 128) { // ������Ϣ���д���ʱ���ǩTp
				sFrameArea = sFrame.substring(28, 12 + iLen - 12); // �����ֻȡ��·���
			} else {
				sFrameArea = sFrame.substring(28, 12 + iLen); // �����ֻȡ��·���
			}
			if ((Integer.parseInt(sKZM.substring(0, 1), 16) & 2) == 2) { // ��ȥ2���ֽڵ��¼�������
				sFrameArea = sFrameArea.substring(0, sFrameArea.length() - 4);
			}

		} else if (GetFrameInfo.gTerminalProtocolCheckOfHeXing(sFrame)) {
			if (!TextArea_GYH.getText().trim().equals("")) {
				iGYH = Integer.parseInt(TextArea_GYH.getText().trim());
			} else {
				iGYH = 200;
			}
			sTerminalAddress = sFrame.substring(14, 16)
					+ sFrame.substring(12, 14);
			sKZM = sFrame.substring(6, 8);
			sGNM = sFrame.substring(24, 26);
			iLen = Integer.parseInt(sFrame.substring(4, 6), 16) * 2;
			sFrameArea = sFrame.substring(26, 6 + iLen - 4); // �����ֻȡ��·���
		} else if (GetFrameInfo.gTerminalProtocolCheckOfDLMS(sFrame)) {
			if (!TextArea_GYH.getText().trim().equals("")) {
				iGYH = Integer.parseInt(TextArea_GYH.getText().trim());
			} else {
				iGYH = 300;
			}
			sCommandID = TextArea_Command.getText().trim();
			if (sCommandID.equals("")) {
				jTextArea1.append("Please input CommandID" + "\r\n");
				return;
			}
			iLen = Integer.parseInt(sFrame.substring(14, 16), 16) * 2;
			sFrameArea = sFrame.substring(16, 16 + iLen); // �����ֻȡ��·���
		} else if (GetFrameInfo.gTerminalProtocolCheckOfIHD(sFrame)) {
			if (!TextArea_GYH.getText().trim().equals("")) {
				iGYH = Integer.parseInt(TextArea_GYH.getText().trim());
			} else {
				iGYH = 101;
			}
			sTerminalAddress = sFrame.substring(12, 14)
					+ sFrame.substring(10, 12) + sFrame.substring(16, 18)
					+ sFrame.substring(14, 16);
			sKZM = sFrame.substring(8, 10);
			sGNM = sFrame.substring(20, 22);
			sSEQ = sFrame.substring(22, 24); // ȡ������
			iTpV = Integer.parseInt(sSEQ, 16) & 128; // ʱ���ǩ��Чλ
			iLen = (Integer.parseInt((sFrame.substring(4, 6) + sFrame
					.substring(2, 4)), 16) >> 2) * 2;
			if (iTpV == 128) { // ������Ϣ���д���ʱ���ǩTp
				sFrameArea = sFrame.substring(24, 8 + iLen - 12); // �����ֻȡ��·���
			} else {
				sFrameArea = sFrame.substring(24, 8 + iLen); // �����ֻȡ��·���
			}
			if ((Integer.parseInt(sKZM.substring(0, 1), 16) & 2) == 2) { // ��ȥ2���ֽڵ��¼�������
				sFrameArea = sFrameArea.substring(0, sFrameArea.length() - 4);
			}

		}

		ArrayList DataList = new ArrayList();
		SFE_DataListInfo DataListInfo = new SFE_DataListInfo();
		String sDataCaption = "";
		String sDataContent = "";
		String sDateTime;
		String sMeasureAdd;
		int iDataItemCount = 0;
		String JSSJ = "";
		String KSSJ = "";
		jTextArea1.setText("");
		Calendar cLogTime = Calendar.getInstance();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		KSSJ = formatter.format(cLogTime.getTime());

		int Count = 0;
		int SuccessCount = 0;
		if ((jTextArea4.getText().trim()).length() != 0) {
			Count = Integer.parseInt(jTextArea4.getText().trim());
		}
		int iDatatype = 0;
		for (int iCount = 0; iCount < Count; iCount++) {
			// jTextArea1.append("��ǰ����"+Integer.toString(iCount+1)+"\r\n");
			if (iGYH == 80 || iGYH == 82 || iGYH == 83 || iGYH == 84
					|| iGYH == 87 || iGYH == 88 || iGYH == 110 || iGYH == 111) {
				DataListInfo = FrameDataAreaExplain.IFE_ExplainDataArea(
						sFrameArea.toCharArray(), sTerminalAddress
								.toCharArray(), iGYH, sKZM.toCharArray(), sKZM
								.toCharArray()); // //��ͨ��������:֡�����,�ն˹�Լ,������
			} else if (iGYH == Glu_ConstDefine.GY_ZD_QUANGUO || iGYH == 105
					|| iGYH == Glu_ConstDefine.GY_ZD_698 || iGYH == 150) {
				int iDBGYH=0;
				if (!jTextArea_DBGY.getText().equals("")) {
					iDBGYH = Integer.parseInt(jTextArea_DBGY.getText());
				}
				if (iDBGYH==50){
					sCommandID = TextArea_Command.getText().trim();
					if (sCommandID.equals("")) {
						jTextArea1.append("Please input CommandID" + "\r\n");
						return;
					}
					DataListInfo = FrameDataAreaExplain.IFE_ExplainDataAreaDLMS(
							sCommandID.toCharArray(), sFrameArea.toCharArray(),
							iGYH); // //��ͨ��������:�����ʶ��֡�����,�ն˹�Լ
				}else{
					DataListInfo = FrameDataAreaExplain.IFE_ExplainDataArea(
							sFrameArea.toCharArray(), sTerminalAddress
									.toCharArray(), iGYH, sKZM.toCharArray(), sGNM
									.toCharArray());
				}
				
				String sDT = sFrameArea.substring(4, 8); // ��Ϣ��
				if (iGYH == Glu_ConstDefine.GY_ZD_698) {
					iDatatype = GluMethod.GetQuanGuo698DataType(sDT, sGNM); // �������:10��ͨ���;21Сʱ�������;22�������;23�ն������;24�¶������;30�澯���;40���÷������
				} else {
					iDatatype = GluMethod.GetQuanGuoDataType(sDT, sGNM); // �������:10��ͨ���;21Сʱ�������;22�������;23�ն������;24�¶������;30�澯���;40���÷������
				}
				if (iDatatype >= 21 && iDatatype <= 24) {
					iDatatype = 20;
				} else if (iDatatype == 11) {
					iDatatype = 10;
				}
			} else if (iGYH == Glu_ConstDefine.GY_ZD_HEXING) {
				DataListInfo = FrameDataAreaExplain.IFE_ExplainDataArea(
						sFrameArea.toCharArray(), sTerminalAddress
								.toCharArray(), iGYH, sKZM.toCharArray(), sGNM
								.toCharArray()); // //��ͨ��������:֡�����,�ն˹�Լ,������
				if (iDatatype == 31) {
					iDatatype = 30;
				}
			} else if (iGYH == Glu_ConstDefine.GY_ZD_DLMS) {
				DataListInfo = FrameDataAreaExplain.IFE_ExplainDataAreaDLMS(
						sCommandID.toCharArray(), sFrameArea.toCharArray(),
						iGYH); // //��ͨ��������:�����ʶ��֡�����,�ն˹�Լ
			} else if (iGYH == Glu_ConstDefine.GY_ZD_IHD) {
				DataListInfo = FrameDataAreaExplain.IFE_ExplainDataArea(
						sFrameArea.toCharArray(), sTerminalAddress
								.toCharArray(), iGYH, sKZM.toCharArray(), sGNM
								.toCharArray());
				String sDT = sFrameArea.substring(4, 8); // ��Ϣ��
				iDatatype = GluMethod.GetIHDDataType(sDT, sGNM); // �������:10��ͨ���;21Сʱ�������;22�������;23�ն������;24�¶������;30�澯���;40���÷������
				if (iDatatype >= 21 && iDatatype <= 27) {
					iDatatype = 20;
				} else if (iDatatype == 11) {
					iDatatype = 10;
				}
			}
			DataList = DataListInfo.DataList;
			if (DataList.size() > 0) {
				switch (DataListInfo.DataType) {
				case 10: { // ��ͨ���
					for (int i = 0; i < DataList.size(); i++) {
						iDataItemCount = ((SFE_NormalData) (DataList.get(i)))
								.GetDataItemCount();
						if (Count == 1) {
							jTextArea1.append("Terminal Address�� "
									+ sTerminalAddress + ";\r\n");
							jTextArea1.append("Measure Point No�� "
									+ ((SFE_NormalData) (DataList.get(i)))
											.GetMeasuredPointNo() + ";\r\n");
							jTextArea1.setLineWrap(true);
						}
						for (int j = 0; j < iDataItemCount; j++) {
							sDataCaption = new String(
									((SFE_DataItem) (((SFE_NormalData) (DataList
											.get(i))).DataItemList.get(j)))
											.GetDataCaption());
							sDataContent = new String(
									((SFE_DataItem) (((SFE_NormalData) (DataList
											.get(i))).DataItemList.get(j)))
											.GetDataContent());
							if (Count == 1) {
								jTextArea1.append("DataCaption " + sDataCaption
										+ ":" + sDataContent + ";\r\n");
								jTextArea1.setLineWrap(true);
							}
						}
					}
					break;
				}
				case 20: { // �������
					for (int i = 0; i < DataList.size(); i++) {
						iDataItemCount = ((SFE_HistoryData) (DataList.get(i)))
								.GetDataItemCount();
						sDateTime = new String(((SFE_HistoryData) (DataList
								.get(i))).GetTaskDateTime()).trim();
						sMeasureAdd = new String(((SFE_HistoryData) (DataList
								.get(i))).GetMeasuredAdd()).trim();
						if (Count == 1) {
							jTextArea1.append("TerminalAddress: "
									+ sTerminalAddress + ";\r\n");
							jTextArea1
									.append("Task No:"
											+ Integer
													.toString(((SFE_HistoryData) (DataList
															.get(i)))
															.GetTaskNo())
											+ ";\r\n");
							jTextArea1
									.append("MP No:"
											+ Integer
													.toString(((SFE_HistoryData) (DataList
															.get(i)))
															.GetMeasuredPointNo())
											+ ";\r\n");
							jTextArea1.append("MP Address:" + sMeasureAdd
									+ ";\r\n");
							jTextArea1.append("Task Time:" + sDateTime
									+ ";\r\n");
						}
						for (int j = 0; j < iDataItemCount; j++) {
							sDataCaption = new String(
									((SFE_DataItem) (((SFE_HistoryData) (DataList
											.get(i))).DataItemList.get(j)))
											.GetDataCaption());
							sDataContent = new String(
									((SFE_DataItem) (((SFE_HistoryData) (DataList
											.get(i))).DataItemList.get(j)))
											.GetDataContent());
							if (Count == 1) {
								jTextArea1.append("DataCaption " + sDataCaption
										+ ": " + sDataContent + ";\r\n");
								jTextArea1.setLineWrap(true);
							}
						}
					}
					break;
				}
				case 30: { // �澯���
					String sAlarmCode;
					for (int i = 0; i < DataList.size(); i++) {
						iDataItemCount = ((SFE_AlarmData) (DataList.get(i)))
								.GetDataItemCount();
						sAlarmCode = new String(((SFE_AlarmData) (DataList
								.get(i))).GetAlarmCode()).trim();
						sDateTime = new String(((SFE_AlarmData) (DataList
								.get(i))).GetAlarmDateTime()).trim();
						if (Count == 1) {
							jTextArea1.append("TerminalAddress "
									+ sTerminalAddress + ";\r\n");
							jTextArea1.append("Alarm Code:" + sAlarmCode
									+ ";\r\n");
							jTextArea1
									.append("MP No:"
											+ Integer
													.toString(((SFE_AlarmData) (DataList
															.get(i)))
															.GetMeasuredPointNo())
											+ ";\r\n");
							jTextArea1.append("Alarm Time:" + sDateTime
									+ ";\r\n");
						}
						for (int j = 0; j < iDataItemCount; j++) {
							sDataCaption = new String(
									((SFE_DataItem) (((SFE_AlarmData) (DataList
											.get(i))).DataItemList.get(j)))
											.GetDataCaption());
							sDataContent = new String(
									((SFE_DataItem) (((SFE_AlarmData) (DataList
											.get(i))).DataItemList.get(j)))
											.GetDataContent());
							if (Count == 1) {
								jTextArea1.append("DataCaption " + sDataCaption
										+ ":" + sDataContent + ";\r\n");
								jTextArea1.setLineWrap(true);
							}
						}
					}
					break;
				}
				case 40: { // ���÷������
					for (int i = 0; i < DataList.size(); i++) {
						iDataItemCount = ((SFE_SetResultData) (DataList.get(i)))
								.GetSetRusultCount();
						for (int j = 0; j < iDataItemCount; j++) {
							sDataCaption = new String(
									((SFE_SetResultItem) (((SFE_SetResultData) (DataList
											.get(i))).SetResultDataList.get(j)))
											.GetDataCaption());
							sDataContent = Integer
									.toString(((SFE_SetResultItem) (((SFE_SetResultData) (DataList
											.get(i))).SetResultDataList.get(j)))
											.GetSetResut());
							if (Count == 1) {
								jTextArea1.append("TerminalAddress:"
										+ sTerminalAddress + ";\r\n");
								jTextArea1.append("DataCaption:" + sDataCaption
										+ ":" + sDataContent + ";\r\n");
								jTextArea1.setLineWrap(true);
							}
						}
					}
					break;
				}
				case 50: { // �㳭��ݷ���
					for (int i = 0; i < DataList.size(); i++) {
						SFE_ExplainPointAmmeterReadData ammeterData = (SFE_ExplainPointAmmeterReadData) DataList
								.get(i);
						iDataItemCount = ammeterData.DataItemCount;
						int CLDXH = ammeterData.MeasurePoint;
						String sAmmeterAddress = ammeterData.AmmeterAddress;
						jTextArea1.append("TerminalAddress: "
								+ sTerminalAddress + ";\r\n");
						jTextArea1.append("MP No:" + CLDXH + ";\r\n");
						jTextArea1.append("MP Address:" + sAmmeterAddress
								+ ";\r\n");
						for (int j = 0; j < iDataItemCount; j++) {
							sDataCaption = new String(
									((SFE_DataItem) (ammeterData.DataItemList)
											.get(j)).GetDataCaption());
							sDataContent = new String(
									((SFE_DataItem) (ammeterData.DataItemList)
											.get(j)).GetDataContent());
							jTextArea1.append("DataCaption:" + sDataCaption
									+ ":" + sDataContent + ";\r\n");
							jTextArea1.setLineWrap(true);
						}
					}
					break;
				}
				}
				SuccessCount = SuccessCount + 1;
			}
		}
		cLogTime = Calendar.getInstance();
		JSSJ = formatter.format(cLogTime.getTime());
		jTextArea1.append("Result: " + ("" + DataListInfo.ExplainResult)
				+ ";\r\n");
		jTextArea1.append("Start Time:" + KSSJ + "\r\n");
		jTextArea1.append("Finish Time" + JSSJ + "\r\n");
		jTextArea1.append("Success Count:" + Integer.toString(SuccessCount));
	}

	public boolean InitialList() {
		boolean Result = false;
		InputStream filecon = null;
		String file_name = "./CommService.config";
		File file = new File(file_name);
		try {
			try {
				Class.forName("oracle.jdbc.driver.OracleDriver");
			} catch (ClassNotFoundException ex) {
				System.out.println("Class Not Found");

			}
			// FConnectString="jdbc:oracle:thin:@172.19.74.205:1521:GRV40";
			if (!file.exists()) {
				file_name = "C:/hexing/CommService.config";
			}
			file = new File(file_name);
			if (!file.exists()) {
				JOptionPane.showMessageDialog(null,
						"Error��Can't find the profile��", "Collector Config",
						JOptionPane.ERROR_MESSAGE);
				System.exit(1);
			}
			Properties prop = new Properties();
			try {
				filecon = new FileInputStream(file_name);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} // ��ȡ�����ļ��е�����
			try {
				prop.load(filecon);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			FConnectString = (String) prop.get("JDBC_CONNECTION_URL");
			FUserName = (String) prop.get("JDBC_CONNECTION_USERNAME");
			FPassWord = (String) prop.get("JDBC_CONNECTION_PASSWORD");
			FDBType = Integer.parseInt((String) prop
					.get("JDBC_CONNECTION_DBTYPE"));
		} finally {
		}
		try {
			filecon.close();
		} catch (Exception e) {
		}
		return Result;
	}

	public void jButton4_actionPerformed(ActionEvent e) {
		try {
			String sSqlStr = jTextArea3.getText().trim();
			String sFrame = "";
			Connection conn = DriverManager.getConnection(FConnectString,
					FUserName, FPassWord);
			Statement stmt = conn.createStatement(
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY); // ����һ�����Թ�����ֻ����SQL������
			ResultSet rset = stmt.executeQuery(sSqlStr);
			Calendar cLogTime = Calendar.getInstance();
			SimpleDateFormat formatter = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			String KSSJ = "";
			String JSSJ = "";

			ArrayList DataList = new ArrayList();
			SFE_DataListInfo DataListInfo = new SFE_DataListInfo();
			String sDataCaption = "";
			String sDataContent = "";
			String sDateTime;
			int iDataItemCount = 0;
			int iTotalCount = 0;
			int iSuccessCount = 0;
			// int iFaultCount = 0;
			int iZheJiangFrameCount = 0;
			int Count = 0;
			while (rset.next()) {
				try {
					iTotalCount = iTotalCount + 1;
					if (iTotalCount > 150000) {
						if (iTotalCount % 1000 == 0) {
							System.out.println(iTotalCount);
						}
					}
					sFrame = rset.getString("TXNR");
					if (sFrame.substring(14, 16).equals("68")
							& sFrame.substring(0, 2).equals("68")) { // ���
						iZheJiangFrameCount = iZheJiangFrameCount + 1;
						String sKZM = sFrame.substring(16, 18);
						int iLen = Integer.parseInt(
								(sFrame.substring(20, 22) + sFrame.substring(
										18, 20)), 16) * 2; // ������ֽڳ���
						String sFrameArea = sFrame.substring(22, 22 + iLen);
						if (rset.isFirst()) {
							cLogTime = Calendar.getInstance();
							KSSJ = formatter.format(cLogTime.getTime());
							// jTextArea1.append("��ʼʱ�䣺"+KSSJ+"\r\n");
						}
						String sTerminalAddress = sFrame.substring(2, 6)
								+ sFrame.substring(8, 10)
								+ sFrame.substring(6, 8);
						DataListInfo = FrameDataAreaExplain
								.IFE_ExplainDataArea(sFrameArea.toCharArray(),
										sTerminalAddress.toCharArray(),
										Glu_ConstDefine.GY_ZD_ZJZB0404, sKZM
												.toCharArray(), sKZM
												.toCharArray()); // ��ͨ��������:֡�����,�ն˹�Լ,������
						DataList = DataListInfo.DataList;
						if (DataList.size() > 0) {
							switch (DataListInfo.DataType) {
							case 10: { // ��ͨ���
								for (int i = 0; i < DataList.size(); i++) {
									iDataItemCount = ((SFE_NormalData) (DataList
											.get(i))).GetDataItemCount();
									for (int j = 0; j < iDataItemCount; j++) {
										sDataCaption = new String(
												((SFE_DataItem) (((SFE_NormalData) (DataList
														.get(i))).DataItemList
														.get(j)))
														.GetDataCaption());
										sDataContent = new String(
												((SFE_DataItem) (((SFE_NormalData) (DataList
														.get(i))).DataItemList
														.get(j)))
														.GetDataContent());
										if (Count == 1) {
											jTextArea1.append("DataCaption:"
													+ sDataCaption + ":"
													+ sDataContent + ";\r\n");
											jTextArea1.setLineWrap(true);
										}
									}
								}
								break;
							}
							case 20: { // �������
								for (int i = 0; i < DataList.size(); i++) {
									iDataItemCount = ((SFE_HistoryData) (DataList
											.get(i))).GetDataItemCount();
									sDateTime = new String(
											((SFE_HistoryData) (DataList.get(i)))
													.GetTaskDateTime()).trim();
									if (Count == 1) {
										jTextArea1
												.append("Task No:"
														+ Integer
																.toString(((SFE_HistoryData) (DataList
																		.get(i)))
																		.GetTaskNo())
														+ ";\r\n");
										jTextArea1.append("Task Time:"
												+ sDateTime + ";\r\n");
									}
									for (int j = 0; j < iDataItemCount; j++) {
										sDataCaption = new String(
												((SFE_DataItem) (((SFE_HistoryData) (DataList
														.get(i))).DataItemList
														.get(j)))
														.GetDataCaption());
										sDataContent = new String(
												((SFE_DataItem) (((SFE_HistoryData) (DataList
														.get(i))).DataItemList
														.get(j)))
														.GetDataContent());
										if (Count == 1) {
											jTextArea1.append("DataCaption:"
													+ sDataCaption + ": "
													+ sDataContent + ";\r\n");
											jTextArea1.setLineWrap(true);
										}
									}
								}
								break;
							}
							case 30: { // �澯���
								String sAlarmCode;
								for (int i = 0; i < DataList.size(); i++) {
									iDataItemCount = ((SFE_AlarmData) (DataList
											.get(i))).GetDataItemCount();
									sAlarmCode = new String(
											((SFE_AlarmData) (DataList.get(i)))
													.GetAlarmCode()).trim();
									sDateTime = new String(
											((SFE_AlarmData) (DataList.get(i)))
													.GetAlarmDateTime()).trim();
									if (Count == 1) {
										jTextArea1.append("Alarm Code:"
												+ sAlarmCode + ";\r\n");
										jTextArea1.append("Alarm Time:"
												+ sDateTime + ";\r\n");
									}
									for (int j = 0; j < iDataItemCount; j++) {
										sDataCaption = new String(
												((SFE_DataItem) (((SFE_AlarmData) (DataList
														.get(i))).DataItemList
														.get(j)))
														.GetDataCaption());
										sDataContent = new String(
												((SFE_DataItem) (((SFE_AlarmData) (DataList
														.get(i))).DataItemList
														.get(j)))
														.GetDataContent());
										if (Count == 1) {
											jTextArea1.append("DataCaption:"
													+ sDataCaption + ":"
													+ sDataContent + ";\r\n");
											jTextArea1.setLineWrap(true);
										}
									}
								}
								break;
							}
							case 40: { // ���÷������
								for (int i = 0; i < DataList.size(); i++) {
									iDataItemCount = ((SFE_SetResultData) (DataList
											.get(i))).GetSetRusultCount();
									for (int j = 0; j < iDataItemCount; j++) {
										sDataCaption = new String(
												((SFE_SetResultItem) (((SFE_SetResultData) (DataList
														.get(i))).SetResultDataList
														.get(j)))
														.GetDataCaption());
										sDataContent = Integer
												.toString(((SFE_SetResultItem) (((SFE_SetResultData) (DataList
														.get(i))).SetResultDataList
														.get(j))).GetSetResut());
										if (Count == 1) {
											jTextArea1.append("DataCaption:"
													+ sDataCaption + ":"
													+ sDataContent + ";\r\n");
											jTextArea1.setLineWrap(true);
										}
									}
								}
								break;
							}
							}
							iSuccessCount = iSuccessCount + 1;
							// jTextArea1.append(""+iSuccessCount+"\r\n");
						}
					}
				} catch (Exception e2) {
					System.out.println("Call error��" + e2.toString());
					System.out.println("Error frame:" + sFrame);
				}
			}
			rset.close();
			cLogTime = Calendar.getInstance();
			JSSJ = formatter.format(cLogTime.getTime());
			jTextArea1.setText("");
			jTextArea1.append("Start Time:" + KSSJ + "\r\n");
			jTextArea1.append("Finish Time:" + JSSJ + "\r\n");
			jTextArea1.append("Explain Count:" + Integer.toString(iTotalCount)
					+ "\r\n");
			jTextArea1.append("The count of ZJFrames:"
					+ Integer.toString(iZheJiangFrameCount) + "\r\n");
			jTextArea1.append("Failed count:"
					+ Integer.toString(iZheJiangFrameCount - iSuccessCount)
					+ "\r\n");
			jTextArea1.append("Success count:"
					+ Integer.toString(iSuccessCount) + "\r\n");
		} catch (Exception e2) {
			System.out.println(e2.toString());
		}
	}

	public void jButton1_actionPerformed(ActionEvent e) {
		FMeasuredPointInfor.MeasuredPointList[FMeasuredPointInfor.MeasuredPointCount] = Integer
				.parseInt(TextArea_CLDH.getText());
		FMeasuredPointInfor.MeasuredPointCount = FMeasuredPointInfor.MeasuredPointCount + 1;
		jTextArea1.append("Add MP No:" + TextArea_CLDH.getText() + "\r\n");
	}

	public void jButton5_actionPerformed(ActionEvent e) {
		String sCommand = TextArea_Command.getText().trim();
		FDataItemList.add(sCommand);
		SFE_ParamItem ParamItem = new SFE_ParamItem();
		ParamItem.SetParamCaption(sCommand);
		String sContent = jEditorPane2.getText().trim();
		ParamItem.SetcParamContent(sContent);
		FParamItemList.add(ParamItem);
		FDataItemCount = FDataItemCount + 1;
		jTextArea1.append("Add CommandID" + sCommand + "\r\n");
		if (sCommand.substring(0, 1).equals("4")) {
			jTextArea1.append("Add DataContent:" + sContent + "\r\n");
		}
	}

	public void jButton6_actionPerformed(ActionEvent e) {
		try {
			ArrayList FrameList = new ArrayList();
			SFE_FrameInfo FrameInfo = new SFE_FrameInfo();
			String sCommand = "";
			String sContent = "";
			SFE_ParamItem ParamItem = new SFE_ParamItem();
			SFE_ParamItem[] ParamItemList;
			if (FMeasuredPointInfor.MeasuredPointCount == 0) {
				FMeasuredPointInfor.MeasuredPointList[FMeasuredPointInfor.MeasuredPointCount] = Integer
						.parseInt(TextArea_CLDH.getText());
				FMeasuredPointInfor.MeasuredPointCount = FMeasuredPointInfor.MeasuredPointCount + 1;
			}
			boolean IsYCSJZC = jCheckBox_YCSJZC.isSelected();
			boolean IsLSSJZC = jCheckBox_LSSJZC.isSelected();
			boolean IsDQSJZC = jCheckBox_DQSJZC.isSelected();
			boolean IsDLMSACTION = jCheckBox_ACTION.isSelected();
			boolean IsDLMSGETDT = jCheckBox_GET.isSelected();
			if (FDataItemCount == 0 && !IsYCSJZC) {
				sCommand = TextArea_Command.getText().trim();
				if (sCommand.equals("")) {
					jTextArea1.append("Please input CommandID!" + "\r\n");
					return;
				}
				FDataItemList.add(sCommand);
				FDataItemCount = FDataItemCount + 1;
				ParamItem.SetParamCaption(sCommand);
				sContent = jEditorPane2.getText().trim();
				ParamItem.SetcParamContent(sContent);
				FParamItemList.add(ParamItem);
			}
			char[] KZM = { '0', '1' };
			char[][] ccDataItemList = new char[FDataItemList.size()][];
			for (int i = 0; i < FDataItemCount; i++) {
				ccDataItemList[i] = ((String) (FDataItemList.get(i)))
						.toCharArray();
			}
			char[] cTerminalLogicAdd = TextArea_TerAddr.getText().toCharArray();
			int iDXXBZ = Integer.parseInt(TextArea_DXXBZ.getText());
			char[] DataArea;
			int iDBGYH = 0;
			int iZDGYH = 80;
			if (!TextArea_GYH.getText().equals("")) {
				iZDGYH = Integer.parseInt(TextArea_GYH.getText());
			}
			if (!jTextArea_DBGY.getText().equals("")) {
				iDBGYH = Integer.parseInt(jTextArea_DBGY.getText());
			}
			int Count = 0;
			if ((jTextArea4.getText().trim()).length() != 0) {
				Count = Integer.parseInt(jTextArea4.getText().trim());
			}
			String JSSJ = "";
			String KSSJ = "";
			Calendar cLogTime = Calendar.getInstance();
			SimpleDateFormat formatter = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			KSSJ = formatter.format(cLogTime.getTime());

			char[] TZZJ = { 'C', 'B' };
			char[] MM = { '5', '5', '5', '5' };
			String sMM = "";
			if (iZDGYH == 106) {
				sMM = "11111111111111111111111111111111";
			} else {
				sMM = "5555";
			}
			char[] DBZ;
			char[] BDZ = { '0', '1' };
			String sFrame;
			int iPortNo = 1;
			for (int k = 0; k < Count; k++) {
				if (iDBGYH == 10) { // ���ȫ��
					char[] cStartTime = jTextArea_QSSJ.getText().trim()
							.toCharArray();
					int iCount = Integer.parseInt(jTextArea_ZCDS.getText()
							.trim());
					boolean IsCheck = jCheckBox_Set.isSelected();
					ParamItemList = new SFE_ParamItem[FParamItemList.size()];
					for (int i = 0; i < FParamItemList.size(); i++) {
						ParamItemList[i] = new SFE_ParamItem();
						sCommand = new String(((SFE_ParamItem) FParamItemList
								.get(i)).GetParamCaption());
						sContent = new String(((SFE_ParamItem) FParamItemList
								.get(i)).GetParamContent());
						ParamItemList[i].SetParamCaption(sCommand);
						ParamItemList[i].SetcParamContent(sContent);
					}
					if (IsCheck) {
						jTextArea1
								.append("Call IFE_QG2AmmeterSer_SetAmmeterData:"
										+ "\r\n");

						char[] PasswordLevel = { '0', '1' };
						char[] Password = { '0', '0', '0', '0', '0', '0' };
						FrameList = FrameDataAreaExplain
								.IFE_QGAmmeterSer_SetAmmeterData(BDZ, iDBGYH,
										iDXXBZ, FDataItemCount, ParamItemList,
										PasswordLevel, Password);
					} else {
						jTextArea1
								.append("Call IFE_QGAmmeterSer_ReadAmmeterData:"
										+ "\r\n");
						FrameList = FrameDataAreaExplain
								.IFE_QGAmmeterSer_ReadAmmeterData(BDZ, iZDGYH,
										iDXXBZ, FDataItemCount, ccDataItemList);
					}
					if (Count == 1) {
						for (int i = 0; i < FrameList.size(); i++) {
							DBZ = (FrameList.get(i).toString()).toCharArray();
							if (iZDGYH == 110) { // ��¡�����˿�������Ϊ������
								iPortNo = Integer.parseInt(TextArea_CLDH
										.getText());
							}
							DataArea = FrameDataAreaExplain
									.IFE_ReadAmmeterData(iZDGYH, iPortNo, 1, 1,
											1, TZZJ, 1, 1, sMM.toCharArray(),
											DBZ.length / 2, DBZ);
							if ((iZDGYH >= 80 && iZDGYH <= 84) || iZDGYH == 110) {
								KZM[0] = '0';
								KZM[1] = '1';
								FrameInfo = FrameDataAreaExplain
										.BuildZheJiangFrame(cTerminalLogicAdd,
												11, 12, KZM, DataArea.length,
												DataArea);
							} else if ((iZDGYH == 100) || (iZDGYH == 106)) {
								KZM[0] = '1';
								KZM[1] = '0';
								FrameInfo = FrameDataAreaExplain
										.BuildQuanGuoFrame(cTerminalLogicAdd,
												iZDGYH, 11, 12, KZM,
												DataArea.length, DataArea);
							}
							sFrame = new String(FrameInfo.GetCommandFrame());
							jTextArea1.append(sFrame + "\r\n");
							jTextArea1.setLineWrap(true);
						}
					}
				}
				if (iDBGYH == 30) { // ���ȫ��2007
					char[] cStartTime = jTextArea_QSSJ.getText().trim()
							.toCharArray();
					int iCount = Integer.parseInt(jTextArea_ZCDS.getText()
							.trim());
					boolean IsCheck = jCheckBox_Set.isSelected();
					ParamItemList = new SFE_ParamItem[FParamItemList.size()];
					for (int i = 0; i < FParamItemList.size(); i++) {
						ParamItemList[i] = new SFE_ParamItem();
						sCommand = new String(((SFE_ParamItem) FParamItemList
								.get(i)).GetParamCaption());
						sContent = new String(((SFE_ParamItem) FParamItemList
								.get(i)).GetParamContent());
						ParamItemList[i].SetParamCaption(sCommand);
						ParamItemList[i].SetcParamContent(sContent);
					}
					if (IsCheck) {
						jTextArea1
								.append("Call IFE_QG2007AmmeterSer_SetAmmeterData:"
										+ "\r\n");

						char[] PasswordLevel = { '0', '1' };
						char[] Password = { '0', '0', '0', '0', '0', '0' };
						char[] OperateCode = { '0', '0', '0', '0', '0', '0',
								'0', '0' };
						FrameList = FrameDataAreaExplain
								.IFE_QG2007AmmeterSer_SetAmmeterData(BDZ,
										iDBGYH, iDXXBZ, FDataItemCount,
										ParamItemList, PasswordLevel, Password,
										OperateCode);
					} else {
						jTextArea1
								.append("Call IFE_QG2007AmmeterSer_ReadAmmeterData:"
										+ "\r\n");
						FrameList = FrameDataAreaExplain
								.IFE_QG2007AmmeterSer_ReadAmmeterData(BDZ,
										iZDGYH, iDXXBZ, FDataItemCount,
										ParamItemList);
					}
					if (Count == 1) {
						for (int i = 0; i < FrameList.size(); i++) {
							DBZ = (FrameList.get(i).toString()).toCharArray();
							if (iZDGYH == 110) { // ��¡�����˿�������Ϊ������
								iPortNo = Integer.parseInt(TextArea_CLDH
										.getText());
							}
							DataArea = FrameDataAreaExplain
									.IFE_ReadAmmeterData(iZDGYH, iPortNo, 1, 1,
											1, TZZJ, 1, 1, sMM.toCharArray(),
											DBZ.length / 2, DBZ);
							if ((iZDGYH >= 80 && iZDGYH <= 84) || iZDGYH == 110) {
								KZM[0] = '0';
								KZM[1] = '1';
								FrameInfo = FrameDataAreaExplain
										.BuildZheJiangFrame(cTerminalLogicAdd,
												11, 12, KZM, DataArea.length,
												DataArea);
							} else if ((iZDGYH == 100) || (iZDGYH == 106)) {
								KZM[0] = '1';
								KZM[1] = '0';
								FrameInfo = FrameDataAreaExplain
										.BuildQuanGuoFrame(cTerminalLogicAdd,
												iZDGYH, 11, 12, KZM,
												DataArea.length, DataArea);
							}
							sFrame = new String(FrameInfo.GetCommandFrame());
							jTextArea1.append(sFrame + "\r\n");
							jTextArea1.setLineWrap(true);
						}

					}
				} else if (iDBGYH == 20) {
					FrameList = FrameDataAreaExplain
							.IFE_ZJAmmeterSer_ReadAmmeterData(BDZ, iZDGYH,
									iDXXBZ, FDataItemCount, ccDataItemList);
					if (Count == 1) {
						jTextArea1
								.append("Call IFE_ZJAmmeterSer_ReadAmmeterData:"
										+ "\r\n");
						for (int i = 0; i < FrameList.size(); i++) {
							DBZ = (FrameList.get(i).toString()).toCharArray();
							DataArea = FrameDataAreaExplain
									.IFE_ReadAmmeterData(iZDGYH, 1, 1, 1, 1,
											TZZJ, 1, 1, sMM.toCharArray(),
											DBZ.length / 2, DBZ);
							if (iZDGYH == 80) {
								KZM[0] = '0';
								KZM[1] = '1';
								FrameInfo = FrameDataAreaExplain
										.BuildZheJiangFrame(cTerminalLogicAdd,
												11, 12, KZM, DataArea.length,
												DataArea);
							} else {
								KZM[0] = '1';
								KZM[1] = '0';
								FrameInfo = FrameDataAreaExplain
										.BuildQuanGuoFrame(cTerminalLogicAdd,
												iZDGYH, 11, 12, KZM,
												DataArea.length, DataArea);
							}
							sFrame = new String(FrameInfo.GetCommandFrame());
							jTextArea1.append(sFrame + "\r\n");
							jTextArea1.setLineWrap(true);
						}
					}
				} else if (iDBGYH == 13) { // ������
					char[] cStartTime = jTextArea_QSSJ.getText().trim()
							.toCharArray();
					int iCount = Integer.parseInt(jTextArea_ZCDS.getText()
							.trim());
					boolean IsCheck = jCheckBox_Set.isSelected();
					if (IsCheck) {
						jTextArea1
								.append("Call IFE_TJAmmeterSer_SetAmmeterData:"
										+ "\r\n");
						ParamItemList = new SFE_ParamItem[FParamItemList.size()];
						for (int i = 0; i < FParamItemList.size(); i++) {
							ParamItemList[i] = new SFE_ParamItem();
							sCommand = new String(
									((SFE_ParamItem) FParamItemList.get(i))
											.GetParamCaption());
							sContent = new String(
									((SFE_ParamItem) FParamItemList.get(i))
											.GetParamContent());
							ParamItemList[i].SetParamCaption(sCommand);
							ParamItemList[i].SetcParamContent(sContent);
						}
						char[] PasswordLevel = { '0', '1' };
						char[] Password = { '0', '0', '0', '0', '0', '0' };
						FrameList = FrameDataAreaExplain
								.IFE_TJAmmeterSer_SetAmmeterData(BDZ, iDBGYH,
										iDXXBZ, FDataItemCount, ParamItemList,
										PasswordLevel, Password);
					} else {
						jTextArea1
								.append("Call IFE_TJAmmeterSer_ReadAmmeterData:"
										+ "\r\n");
						FrameList = FrameDataAreaExplain
								.IFE_TJAmmeterSer_ReadAmmeterData(BDZ, iDBGYH,
										iDXXBZ, FDataItemCount, ccDataItemList,
										cStartTime, iCount);
					}
					if (Count == 1) {
						for (int i = 0; i < FrameList.size(); i++) {
							DBZ = (FrameList.get(i).toString()).toCharArray();
							DataArea = FrameDataAreaExplain
									.IFE_ReadAmmeterData(iZDGYH, iPortNo, 1, 1,
											1, TZZJ, 1, 1, sMM.toCharArray(),
											DBZ.length / 2, DBZ);
							KZM[0] = '1';
							KZM[1] = '0';
							FrameInfo = FrameDataAreaExplain.BuildQuanGuoFrame(
									cTerminalLogicAdd, iZDGYH, 11, 12, KZM,
									DataArea.length, DataArea);
							sFrame = new String(FrameInfo.GetCommandFrame());
							jTextArea1.append(sFrame + "\r\n");
							jTextArea1.setLineWrap(true);
						}
					}
				}else if (iDBGYH == 50) {//DLMS
					boolean IsCheck = jCheckBox_Set.isSelected();
					ParamItemList = new SFE_ParamItem[FParamItemList.size()];
					BDZ = TextArea_TerAddr.getText().toCharArray();
					for (int i = 0; i < FParamItemList.size(); i++) {
						ParamItemList[i] = new SFE_ParamItem();
						sCommand = new String(((SFE_ParamItem) FParamItemList
								.get(i)).GetParamCaption());
						sContent = new String(((SFE_ParamItem) FParamItemList
								.get(i)).GetParamContent());
						ParamItemList[i].SetParamCaption(sCommand);
						ParamItemList[i].SetcParamContent(sContent);
					}
					if (IsCheck) {
						jTextArea1
								.append("Call IFE_DLMSAmmeterSer_SetAmmeterData:"
										+ "\r\n");

						char[] PasswordLevel = { '0', '1' };
						char[] Password = { '0', '0', '0', '0', '0', '0' };
						FrameList = FrameDataAreaExplain
								.IFE_DLMSAmmeterSer_SetAmmeterData(BDZ, iDBGYH,
										iDXXBZ, FDataItemCount, ParamItemList,
										PasswordLevel, Password);
					}else if (IsDLMSACTION){
						jTextArea1
								.append("Call IFE_DLMSAmmeterSer_ControlAmmeterData:"
										+ "\r\n");
		
						char[] PasswordLevel = { '0', '1' };
						char[] Password = { '0', '0', '0', '0', '0', '0' };
						FrameList = FrameDataAreaExplain
								.IFE_DLMSAmmeterSer_ControlAmmeterData(BDZ, iDBGYH,
										iDXXBZ, FDataItemCount, ParamItemList,
										PasswordLevel, Password);
					} else {
						jTextArea1
								.append("Call IFE_DLMSAmmeterSer_ReadAmmeterData:"
										+ "\r\n");
						FrameList = FrameDataAreaExplain
								.IFE_DLMSAmmeterSer_ReadAmmeterData(BDZ, iZDGYH,
										iDXXBZ, FDataItemCount, ccDataItemList);
					}
					if (Count == 1) {
						for (int i = 0; i < FrameList.size(); i++) {
							if (iZDGYH == 110 || iZDGYH == 100 || iZDGYH == 106) { // ��¡�����˿�������Ϊ������
								iPortNo = Integer.parseInt(TextArea_CLDH
										.getText());
							}							
							DBZ = (DataSwitch.ReverseStringByByte(DataSwitch.IntToHex(""+iPortNo, "0000"))+FrameList.get(i).toString()).toCharArray();
							DataArea = FrameDataAreaExplain
									.IFE_ReadAmmeterData(iZDGYH, iPortNo, 1, 1,
											1, TZZJ, 184, 184, sMM.toCharArray(),
											DBZ.length / 2, DBZ);
							if ((iZDGYH >= 80 && iZDGYH <= 84) || iZDGYH == 110) {
								KZM[0] = '0';
								KZM[1] = '1';
								FrameInfo = FrameDataAreaExplain
										.BuildZheJiangFrame(cTerminalLogicAdd,
												11, 12, KZM, DataArea.length,
												DataArea);
							} else if ((iZDGYH == 100) || (iZDGYH == 106)) {
								KZM[0] = '1';
								KZM[1] = '0';
								FrameInfo = FrameDataAreaExplain
										.BuildQuanGuoFrame(cTerminalLogicAdd,
												iZDGYH, 11, 12, KZM,
												DataArea.length, DataArea);
							}
							sFrame = new String(FrameInfo.GetCommandFrame());
							jTextArea1.append(sFrame + "\r\n");
							jTextArea1.setLineWrap(true);
						}
					}				
				}  else {
					boolean IsCheck = jCheckBox_Set.isSelected();
					String sGNM = "";
					SFE_QGSer_TimeLabel TimeLabel = new SFE_QGSer_TimeLabel();
					ParamItemList = new SFE_ParamItem[FParamItemList.size()];
					for (int i = 0; i < FParamItemList.size(); i++) {
						ParamItemList[i] = new SFE_ParamItem();
						sCommand = new String(((SFE_ParamItem) FParamItemList
								.get(i)).GetParamCaption());
						sContent = new String(((SFE_ParamItem) FParamItemList
								.get(i)).GetParamContent());
						ParamItemList[i].SetParamCaption(sCommand);
						ParamItemList[i].SetcParamContent(sContent);
					}
					if (iZDGYH == Glu_ConstDefine.GY_ZD_ZHEJIANG
							|| iZDGYH == Glu_ConstDefine.GY_ZD_ZJZB0404
							|| iZDGYH == 83 || iZDGYH == 84 || iZDGYH == 86
							|| iZDGYH == 87 || iZDGYH == 110) {
						if (IsCheck) {
							sGNM = "08";
							KZM = sGNM.toCharArray();
							char[] PasswordLevel = { '1', '1' };
							char[] Password = { '0', '0', '0', '0', '0', '0' };
							char[] ExecuteTime = { '2', '0', '0', '5', '1',
									'0', '1', '8', '1', '0', '1', '1', '1', '2' };
							DataArea = FrameDataAreaExplain.IFE_SetData(iZDGYH,
									FMeasuredPointInfor, FDataItemCount,
									ParamItemList, PasswordLevel, Password,
									ExecuteTime, 0, 10);
							FrameInfo = FrameDataAreaExplain
									.BuildZheJiangFrame(cTerminalLogicAdd, 11,
											12, KZM, DataArea.length, DataArea);
							sFrame = new String(FrameInfo.GetCommandFrame());
							if (Count == 1) {
								jTextArea1.append("Call IFE_SetData:" + "\r\n");
								jTextArea1.append(sFrame + "\r\n");
								jTextArea1.setLineWrap(true);
							}
						} else {
							DataArea = FrameDataAreaExplain
									.IFE_ReadMeasuredData(iZDGYH,
											FMeasuredPointInfor, iDXXBZ,
											FDataItemCount, ParamItemList, 10);
							FrameInfo = FrameDataAreaExplain
									.BuildZheJiangFrame(cTerminalLogicAdd, 11,
											12, KZM, DataArea.length, DataArea);
							sFrame = new String(FrameInfo.GetCommandFrame());
							if (Count == 1) {
								jTextArea1.append("Call IFE_ReadMeasuredData:"
										+ "\r\n");
								jTextArea1.append(sFrame + "\r\n");
							}
						}
					} else if (iZDGYH == 100 || iZDGYH == 105 || iZDGYH == 150
							|| iZDGYH == Glu_ConstDefine.GY_ZD_698) {
						if (sCommand.length() == 6
								&& (sCommand.substring(0, 1).equals("4")
										|| sCommand.substring(0, 1).equals("5") || sCommand
										.substring(0, 1).equals("B"))) {
							if (IsCheck) {
								sGNM = "0" + sCommand.substring(0, 1);
								KZM = sGNM.toCharArray();
								char[] PasswordLevel = { '1', '1' };
								char[] ExecuteTime = { '2', '0', '0', '5', '1',
										'0', '1', '8', '1', '0', '1', '1', '1',
										'2' };
								DataArea = FrameDataAreaExplain.IFE_SetData(
										iZDGYH, FMeasuredPointInfor,
										FDataItemCount, ParamItemList,
										PasswordLevel, sMM.toCharArray(),
										ExecuteTime, 10, 20);
								FrameInfo = FrameDataAreaExplain
										.BuildQuanGuoFrame(cTerminalLogicAdd,
												iZDGYH, 11, 12, KZM,
												DataArea.length, DataArea);
								sFrame = new String(FrameInfo.GetCommandFrame());
								if (Count == 1) {
									jTextArea1.append("Call IFE_SetData:"
											+ "\r\n");
									jTextArea1.append(sFrame + "\r\n");
									jTextArea1.setLineWrap(true);
								}
							} else {
								if (sCommand.substring(0, 1).equals("B")) {
									sGNM = "0B";
								} else
									sGNM = "0A";
								KZM = sGNM.toCharArray();
								DataArea = FrameDataAreaExplain.IFE_ReadData(
										iZDGYH, FMeasuredPointInfor,
										FDataItemCount, ParamItemList, 10);
								FrameInfo = FrameDataAreaExplain
										.BuildQuanGuoFrame(cTerminalLogicAdd,
												iZDGYH, 11, 12, KZM,
												DataArea.length, DataArea);
								sFrame = new String(FrameInfo.GetCommandFrame());
								if (Count == 1) {
									jTextArea1.append("Call IFE_ReadData:"
											+ "\r\n");
									jTextArea1.append(sFrame + "\r\n");
									jTextArea1.setLineWrap(true);
								}
							}
						} else if (IsYCSJZC) {
							sGNM = "0E";
							KZM = sGNM.toCharArray();
							int iStart = Integer.parseInt(jTextArea_QSZZ
									.getText().trim());
							int iCount = Integer.parseInt(jTextArea_ZCDS
									.getText().trim());
							DataArea = FrameDataAreaExplain
									.IFE_QGSer_AlarmDataQuery(iZDGYH, iStart,
											iCount, TimeLabel);
							FrameInfo = FrameDataAreaExplain.BuildQuanGuoFrame(
									cTerminalLogicAdd, iZDGYH, 11, 12, KZM,
									DataArea.length, DataArea);
							sFrame = new String(FrameInfo.GetCommandFrame());
							if (Count == 1) {
								jTextArea1
										.append("Call IFE_QGSer_AlarmDataQuery:"
												+ "\r\n");
								jTextArea1.append(sFrame + "\r\n");
								jTextArea1.setLineWrap(true);
							}
						} else if (IsLSSJZC) {
							sGNM = "0D";
							KZM = sGNM.toCharArray();
							char[] cStartTime = jTextArea_QSSJ.getText().trim()
									.toCharArray();
							int iMD = Integer.parseInt(jTextArea_SJMD.getText()
									.trim());
							int iCount = Integer.parseInt(jTextArea_ZCDS
									.getText().trim());
							int iDataType = Integer.parseInt(jTextArea_SJLX
									.getText().trim());
							DataArea = FrameDataAreaExplain
									.IFE_QGSer_HistoryDataQuery(
											iZDGYH,
											FMeasuredPointInfor.MeasuredPointCount,
											FMeasuredPointInfor.MeasuredPointList,
											FDataItemCount, ParamItemList,
											TimeLabel, iDataType, cStartTime,
											iMD, iCount);
							FrameInfo = FrameDataAreaExplain.BuildQuanGuoFrame(
									cTerminalLogicAdd, iZDGYH, 11, 12, KZM,
									DataArea.length, DataArea);
							sFrame = new String(FrameInfo.GetCommandFrame());
							if (Count == 1) {
								jTextArea1
										.append("Call IFE_QGSer_HistoryDataQuery:"
												+ "\r\n");
								jTextArea1.append(sFrame + "\r\n");
								jTextArea1.setLineWrap(true);
							}
						} else if (IsDQSJZC) {
							sGNM = "0C";
							KZM = sGNM.toCharArray();
							DataArea = FrameDataAreaExplain
									.IFE_QGSer_CurrentDataQuery(
											iZDGYH,
											FMeasuredPointInfor.MeasuredPointCount,
											FMeasuredPointInfor.MeasuredPointList,
											FDataItemCount, ParamItemList,
											TimeLabel);
							FrameInfo = FrameDataAreaExplain.BuildQuanGuoFrame(
									cTerminalLogicAdd, iZDGYH, 11, 12, KZM,
									DataArea.length, DataArea);
							sFrame = new String(FrameInfo.GetCommandFrame());
							if (Count == 1) {
								jTextArea1
										.append("Call IFE_QGSer_CurrentDataQuery:"
												+ "\r\n");
								jTextArea1.append(sFrame + "\r\n");
								jTextArea1.setLineWrap(true);
							}
						} else {
							sGNM = "0C";
							KZM = sGNM.toCharArray();
							DataArea = FrameDataAreaExplain
									.IFE_ReadMeasuredData(iZDGYH,
											FMeasuredPointInfor, iDXXBZ,
											FDataItemCount, ParamItemList, 10);
							FrameInfo = FrameDataAreaExplain.BuildQuanGuoFrame(
									cTerminalLogicAdd, iZDGYH, 11, 12, KZM,
									DataArea.length, DataArea);
							sFrame = new String(FrameInfo.GetCommandFrame());
							if (Count == 1) {
								jTextArea1.append("Call IFE_ReadMeasuredData:"
										+ "\r\n");
								jTextArea1.append(sFrame + "\r\n");
								jTextArea1.setLineWrap(true);
							}
						}
					} else if (iZDGYH == Glu_ConstDefine.GY_ZD_HEXING) {
						if (IsCheck) {
							sGNM = "02";
							KZM = sGNM.toCharArray();
							char[] PasswordLevel = { '1', '1' };
							char[] Password = { '0', '0', '0', '0', '0', '0' };
							char[] ExecuteTime = { '2', '0', '0', '5', '1',
									'0', '1', '8', '1', '0', '1', '1', '1', '2' };
							DataArea = FrameDataAreaExplain.IFE_SetData(iZDGYH,
									FMeasuredPointInfor, FDataItemCount,
									ParamItemList, PasswordLevel, Password,
									ExecuteTime, 0, 10);
							FrameInfo = FrameDataAreaExplain.BuildHeXingFrame(
									cTerminalLogicAdd, 11, 12, KZM,
									DataArea.length, DataArea);
							sFrame = new String(FrameInfo.GetCommandFrame());
							if (Count == 1) {
								jTextArea1.append("Call IFE_SetData:" + "\r\n");
								jTextArea1.append(sFrame + "\r\n");
								jTextArea1.setLineWrap(true);
							}
						} else {
							DataArea = FrameDataAreaExplain
									.IFE_ReadMeasuredData(iZDGYH,
											FMeasuredPointInfor, iDXXBZ,
											FDataItemCount, ParamItemList, 10);
							FrameInfo = FrameDataAreaExplain.BuildHeXingFrame(
									cTerminalLogicAdd, 11, 12, KZM,
									DataArea.length, DataArea);
							sFrame = new String(FrameInfo.GetCommandFrame());
							if (Count == 1) {
								jTextArea1.append("Call IFE_ReadMeasuredData:"
										+ "\r\n");
								jTextArea1.append(sFrame + "\r\n");
							}
						}
					} else if (iZDGYH == Glu_ConstDefine.GY_ZD_DLMS) {
						if (IsCheck || IsDLMSACTION) {
							sGNM = "C1";
							if (IsDLMSACTION) {
								sGNM = "C3";
							}
							KZM = sGNM.toCharArray();
							char[] PasswordLevel = { '1', '1' };
							char[] Password = { '0', '0', '0', '0', '0', '0' };
							char[] ExecuteTime = { '2', '0', '0', '5', '1',
									'0', '1', '8', '1', '0', '1', '1', '1', '2' };
							DataArea = FrameDataAreaExplain.IFE_SetData(iZDGYH,
									FMeasuredPointInfor, FDataItemCount,
									ParamItemList, PasswordLevel, Password,
									ExecuteTime, 0, 10);
							FrameInfo = FrameDataAreaExplain.BuildDLMSFrame(
									cTerminalLogicAdd, 11, 12, KZM,
									DataArea.length, DataArea);
							sFrame = new String(FrameInfo.GetCommandFrame());
							if (Count == 1) {
								jTextArea1.append("Call IFE_SetData:" + "\r\n");
								jTextArea1.append(sFrame + "\r\n");
								jTextArea1.setLineWrap(true);
							}
						} else if (IsDLMSGETDT) {
							sGNM = "C0";
							KZM = sGNM.toCharArray();
							char[] cStartTime = jTextArea_QSSJ.getText().trim()
									.toCharArray();
							char[] cEndTime = jTextArea_QSSJ.getText().trim()
									.toCharArray();
							DataArea = FrameDataAreaExplain
									.IFE_DLMSSer_ReadHistoryData(iZDGYH,
											FDataItemCount, ParamItemList,
											cStartTime, cEndTime);
							FrameInfo = FrameDataAreaExplain.BuildDLMSFrame(
									cTerminalLogicAdd, 11, 12, KZM,
									DataArea.length, DataArea);
							sFrame = new String(FrameInfo.GetCommandFrame());
							if (Count == 1) {
								jTextArea1.append("Call IFE_ReadMeasuredData:"
										+ "\r\n");
								jTextArea1.append(sFrame + "\r\n");
							}
						} else {
							sGNM = "C0";
							KZM = sGNM.toCharArray();
							DataArea = FrameDataAreaExplain.IFE_ReadData(
									iZDGYH, FMeasuredPointInfor,
									FDataItemCount, ParamItemList, 10);
							FrameInfo = FrameDataAreaExplain.BuildDLMSFrame(
									cTerminalLogicAdd, 11, 12, KZM,
									DataArea.length, DataArea);
							sFrame = new String(FrameInfo.GetCommandFrame());
							if (Count == 1) {
								jTextArea1.append("Call IFE_ReadMeasuredData:"
										+ "\r\n");
								jTextArea1.append(sFrame + "\r\n");
							}
						}
					} else if (iZDGYH == Glu_ConstDefine.GY_ZD_IHD) {
						if (sCommand.length() == 6
								&& (sCommand.substring(0, 1).equals("4") || sCommand
										.substring(0, 1).equals("5"))) {
							if (IsCheck) {
								sGNM = "0" + sCommand.substring(0, 1);
								KZM = sGNM.toCharArray();
								char[] PasswordLevel = { '1', '1' };
								char[] ExecuteTime = { '2', '0', '0', '5', '1',
										'0', '1', '8', '1', '0', '1', '1', '1',
										'2' };
								DataArea = FrameDataAreaExplain.IFE_SetData(
										iZDGYH, FMeasuredPointInfor,
										FDataItemCount, ParamItemList,
										PasswordLevel, sMM.toCharArray(),
										ExecuteTime, 10, 20);
								FrameInfo = FrameDataAreaExplain.BuildIHDFrame(
										cTerminalLogicAdd, iZDGYH, 11, 12, KZM,
										DataArea.length, DataArea);
								sFrame = new String(FrameInfo.GetCommandFrame());
								if (Count == 1) {
									jTextArea1.append("Call IFE_SetData:"
											+ "\r\n");
									jTextArea1.append(sFrame + "\r\n");
									jTextArea1.setLineWrap(true);
								}
							} else {
								if (sCommand.substring(0, 1).equals("B")) {
									sGNM = "0B";
								} else
									sGNM = "0A";
								KZM = sGNM.toCharArray();
								DataArea = FrameDataAreaExplain.IFE_ReadData(
										iZDGYH, FMeasuredPointInfor,
										FDataItemCount, ParamItemList, 10);
								FrameInfo = FrameDataAreaExplain.BuildIHDFrame(
										cTerminalLogicAdd, iZDGYH, 11, 12, KZM,
										DataArea.length, DataArea);
								sFrame = new String(FrameInfo.GetCommandFrame());
								if (Count == 1) {
									jTextArea1.append("Call IFE_ReadData:"
											+ "\r\n");
									jTextArea1.append(sFrame + "\r\n");
									jTextArea1.setLineWrap(true);
								}
							}
						} else if (IsLSSJZC) {
							sGNM = "0D";
							KZM = sGNM.toCharArray();
							char[] cStartTime = jTextArea_QSSJ.getText().trim()
									.toCharArray();
							int iMD = Integer.parseInt(jTextArea_SJMD.getText()
									.trim());
							int iCount = Integer.parseInt(jTextArea_ZCDS
									.getText().trim());
							int iDataType = Integer.parseInt(jTextArea_SJLX
									.getText().trim());
							DataArea = FrameDataAreaExplain
									.IFE_IHDSer_HistoryDataQuery(
											iZDGYH,
											FMeasuredPointInfor.MeasuredPointCount,
											FMeasuredPointInfor.MeasuredPointList,
											FDataItemCount, ParamItemList,
											TimeLabel, iDataType, cStartTime,
											iMD, iCount);
							FrameInfo = FrameDataAreaExplain.BuildIHDFrame(
									cTerminalLogicAdd, iZDGYH, 11, 12, KZM,
									DataArea.length, DataArea);
							sFrame = new String(FrameInfo.GetCommandFrame());
							if (Count == 1) {
								jTextArea1
										.append("Call IFE_QGSer_HistoryDataQuery:"
												+ "\r\n");
								jTextArea1.append(sFrame + "\r\n");
								jTextArea1.setLineWrap(true);
							}
						} else {
							sGNM = "0C";
							KZM = sGNM.toCharArray();
							DataArea = FrameDataAreaExplain
									.IFE_ReadMeasuredData(iZDGYH,
											FMeasuredPointInfor, iDXXBZ,
											FDataItemCount, ParamItemList, 10);
							FrameInfo = FrameDataAreaExplain.BuildIHDFrame(
									cTerminalLogicAdd, iZDGYH, 11, 12, KZM,
									DataArea.length, DataArea);
							sFrame = new String(FrameInfo.GetCommandFrame());
							if (Count == 1) {
								jTextArea1.append("Call IFE_ReadMeasuredData:"
										+ "\r\n");
								jTextArea1.append(sFrame + "\r\n");
								jTextArea1.setLineWrap(true);
							}
						}
					} else {
						jTextArea1.append("Invalid Protocol No" + "\r\n");
					}
				}
			}
			cLogTime = Calendar.getInstance();
			JSSJ = formatter.format(cLogTime.getTime());
			jTextArea1.append("Start Time:" + KSSJ + "\r\n");
			jTextArea1.append("Finish Time:" + JSSJ + "\r\n");
			jTextArea1.append("Success Count:" + Integer.toString(Count)
					+ "\r\n");

		} finally {
			FDataItemCount = 0;
			FMeasuredPointInfor.MeasuredPointCount = 0;
			FDataItemList.clear();
			FParamItemList.clear();
		}
	}
}

class TestForm_jButton6_actionAdapter implements ActionListener {
	private TestForm adaptee;

	TestForm_jButton6_actionAdapter(TestForm adaptee) {
		this.adaptee = adaptee;
	}

	public void actionPerformed(ActionEvent e) {

		adaptee.jButton6_actionPerformed(e);
	}
}

class TestForm_jButton5_actionAdapter implements ActionListener {
	private TestForm adaptee;

	TestForm_jButton5_actionAdapter(TestForm adaptee) {
		this.adaptee = adaptee;
	}

	public void actionPerformed(ActionEvent e) {

		adaptee.jButton5_actionPerformed(e);
	}
}

class TestForm_jButton1_actionAdapter implements ActionListener {
	private TestForm adaptee;

	TestForm_jButton1_actionAdapter(TestForm adaptee) {
		this.adaptee = adaptee;
	}

	public void actionPerformed(ActionEvent e) {

		adaptee.jButton1_actionPerformed(e);
	}
}

class TestForm_jButton4_actionAdapter implements ActionListener {
	private TestForm adaptee;

	TestForm_jButton4_actionAdapter(TestForm adaptee) {
		this.adaptee = adaptee;
	}

	public void actionPerformed(ActionEvent e) {

		adaptee.jButton4_actionPerformed(e);
	}
}

class TestForm_jButton3_actionAdapter implements ActionListener {
	private TestForm adaptee;

	TestForm_jButton3_actionAdapter(TestForm adaptee) {
		this.adaptee = adaptee;
	}

	public void actionPerformed(ActionEvent e) {

		adaptee.jButton3_actionPerformed(e);
	}
}

class TestForm_jButton2_actionAdapter implements ActionListener {
	private TestForm adaptee;

	TestForm_jButton2_actionAdapter(TestForm adaptee) {
		this.adaptee = adaptee;
	}

	public void actionPerformed(ActionEvent e) {

		adaptee.jButton2_actionPerformed(e);
	}
}

package com.chooshine.fep.ConstAndTypeDefine;


import java.util.Hashtable;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright:</p>
 *
 * <p>Company: </p>
 *
 * @author 
 * @version 
 */
public class Glu_ConstDefine {
//�ն˹�Լ
  public final static int GY_DB_QG97 = 10; //ȫ���Լ1997���
  //public final static int GY_DB_TJ = 13; //���645��Լ
  //public final static int GY_DB_HLGB = 15; //��¡�������Լ
  //public final static int GY_DB_ZHEJIANG = 20; //�㽭��Լ
  //public final static int GY_DB_ZHEJIANGSXSX = 25; //�㽭��Լ(�������ߵĵ����͵�ѹ��������⴦��)
  public final static int GY_DB_QG2007 = 30; //ȫ���Լ2007���
  public final static int GY_ZD_HS = 40; //����ˮ���Լ
  public final static int GY_DB_DLMS = 50; //����DLMS���Լ
  //public final static int GY_ZD_HUALONG = 90; //��¡��Լ
  public final static int GY_ZD_ZHEJIANG = 80; //�㽭�淶
  //public final static int GY_ZD_ZJYXYDZB = 81; //�㽭�淶��¡�����õ�������Լ
  public final static int GY_ZD_ZJZB0404 = 82; //�㽭�淶2004��4��������Լ
  //public final static int GY_ZD_ZJBDZ = 83; //���ϵ�б��վ
  //public final static int GY_ZD_GUANGDONG = 84; //�ն˹㶫��Լ
  //public final static int GY_ZD_GUANGDONG_SC = 86; //�ն˹㶫�ڶ����Լ
  //public final static int GY_ZD_ZJMKB = 87; //���ϵ��ģ���
  public final static int GY_ZD_QUANGUO = 100; //�ն�ȫ���Լ
  public final static int GY_ZD_IHD = 101; //�ն�IHD��Լ
  //public final static int GY_ZD_TIANJIN = 105; //�ն����ģ����Լ
  public final static int GY_ZD_698 = 106; //�ն�698��Լ
  //public final static int GY_ZD_GUYUAN = 150; //�ն˹�ԭ������Լ
  //public final static int GY_ZD_FuKong = 130; //�ն˸��ع�Լ(230M)
  public final static int GY_ZD_HEXING = 200; //�ն˺��˼�������Լ
  public final static int GY_ZD_DLMS = 107; //�ն�DLMS��Լ
  public final static int GY_UnDefine = -100; //δ�����Լ
  public final static int GY_PrePayApp = 1000; //���ڼ��ܻ���������Լ�Ԥ����Զ�̷��͵�ҵ��������Լ��

  //�������
  public final static int SJLX_WDY = -100; //δ���屨��
  public final static int SJLX_XXBW = 100; //���б���

  public final static int SJLX_PTSJ = 210; //��ͨ���
  public final static int SJLX_PTSJHJ = 211; //��ͨ����к��֡
  public final static int SJLX_LSSJZC = 220; //��ʷ����ٲ�
  public final static int SJLX_LSSJZD = 221; //��ʷ����Զ�
  public final static int SJLX_YCSJZC = 230; //�쳣����ٲ�
  public final static int SJLX_YCSJZD = 231; //�쳣����Զ�
  public final static int SJLX_SZFH = 240; //���÷������

  public final static int SJLX_ZDDL = 310; //GPRSͨѶ(UDP��TCP)ʱ�ն˵�¼
  public final static int SJLX_ZXXT = 320; //GPRSͨѶ(UDP��TCP)ʱ�ն���������֡(������Ϣ��)
  public final static int SJLX_ZDTC = 330; //GPRSͨѶ(UDP��TCP)ʱ�ն˵�¼�˳�
  public final static int SJLX_DLMSAARE = 340; //DLMS��ԼAARE
  
  //��Լ֧�ֶ���
  public static boolean TerminalZheJiangSupport=false;  //�ն��㽭��Լ
  public static boolean TerminalGuangDongSupport=false; //�ն˹㶫��Լ
  public static boolean TerminalZheJiangBDZSupport=false; //�ն��㽭���վ��Լ
  public static boolean TerminalQuanGuoSupport = false; //�ն�ȫ���Լ
  public static boolean TerminalIHDSupport = false; //�ն�IHD��Լ
  public static boolean TerminalTianJinSupport = false; //�ն�����Լ
  public static boolean Terminal698Support = false; //�ն�698��Լ
  public static boolean TerminalGuYuanSupport = false; //�ն˹�ԭ��Լ
  public static boolean TerminalHeXingSupport = false; //�ն˺��˼�������Լ
  public static boolean TerminalDLMSSupport = false; //�ն�DLMS��Լ
  
  public static boolean TerminalAutoTaskAdjust = false; //�ն��Զ����������ж�����վ���  
  public static  boolean AmmeterQuanGuoSupport = false; //���ȫ���Լ
  public static  boolean AmmeterZheJiangSupport = false; //����㽭��Լ
  public static  boolean AmmeterTianJinSupport = false; //�������Լ
  public static  boolean AmmeterQuanGuo2007Support = false; //2007���ȫ���Լ
  public static  boolean WaterAmmeterSupport = false; //ˮ���Լ
  public static  boolean DLMSAmmeterSupport = false; //DLMS��Լ
  
  public static Trc4Fep Trc1 = new Trc4Fep("FrameDataAreaExplain");
  public static Log4Fep Log1 = new Log4Fep("FrameDataAreaExplain");
  
  public static boolean TaskLengthCheckSupport=false;  //����̶ȺϷ���У��	
  
  public static boolean IRANToGregorian;//ChangeIRANToGregorian
  public static boolean BillingDateAddOneDay;
  public static CalendarConvert Cc1= new CalendarConvert();
  
}

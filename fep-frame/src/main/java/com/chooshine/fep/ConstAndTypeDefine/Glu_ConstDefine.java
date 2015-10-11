package com.chooshine.fep.ConstAndTypeDefine;

//import java.util.Hashtable;

public class Glu_ConstDefine {
	// 终端规约
	public final static int GY_DB_QG97 = 10; // 全国规约1997年版
	// public final static int GY_DB_TJ = 13; //天津645规约
	// public final static int GY_DB_HLGB = 15; //华隆公变电表规约
	// public final static int GY_DB_ZHEJIANG = 20; //浙江规约
	// public final static int GY_DB_ZHEJIANGSXSX = 25;
	// //浙江规约(三相三线的电流和电压问题的特殊处理)
	public final static int GY_DB_QG2007 = 30; // 全国规约2007年版
	public final static int GY_ZD_HS = 40; // 杭州水表规约
	public final static int GY_DB_DLMS = 50; // 杭州DLMS表规约
	// public final static int GY_ZD_HUALONG = 90; //华隆规约
	public final static int GY_ZD_ZHEJIANG = 80; // 浙江规范
	// public final static int GY_ZD_ZJYXYDZB = 81; //浙江规范华隆有序用电增补规约
	public final static int GY_ZD_ZJZB0404 = 82; // 浙江规范2004年4月增补规约
	// public final static int GY_ZD_ZJBDZ = 83; //浙规系列变电站
	// public final static int GY_ZD_GUANGDONG = 84; //终端广东规约
	// public final static int GY_ZD_GUANGDONG_SC = 86; //终端广东第二版规约
	// public final static int GY_ZD_ZJMKB = 87; //浙规系列模块表
	public final static int GY_ZD_QUANGUO = 100; // 终端全国规约
	public final static int GY_ZD_IHD = 101; // 终端IHD规约
	// public final static int GY_ZD_TIANJIN = 105; //终端天津模块表规约
	public final static int GY_ZD_698 = 106; // 终端698规约
	// public final static int GY_ZD_GUYUAN = 150; //终端固原集抄规约
	// public final static int GY_ZD_FuKong = 130; //终端负控规约(230M)
	public final static int GY_ZD_HEXING = 200; // 终端海兴集中器规约
	public final static int GY_ZD_DLMS = 107; // 终端DLMS规约
	public final static int GY_UnDefine = -100; // 未定义规约
	public final static int GY_PrePayApp = 1000; // 用于加密机、读卡器、以及预付费远程发送等业务的特殊规约号

	// 数据类型
	public final static int SJLX_WDY = -100; // 未定义报文
	public final static int SJLX_XXBW = 100; // 下行报文

	public final static int SJLX_PTSJ = 210; // 普通数据
	public final static int SJLX_PTSJHJ = 211; // 普通数据有后继帧
	public final static int SJLX_LSSJZC = 220; // 历史数据召测
	public final static int SJLX_LSSJZD = 221; // 历史数据自动
	public final static int SJLX_YCSJZC = 230; // 异常数据召测
	public final static int SJLX_YCSJZD = 231; // 异常数据自动
	public final static int SJLX_SZFH = 240; // 设置返回数据

	public final static int SJLX_ZDDL = 310; // GPRS通讯(UDP或TCP)时终端登录
	public final static int SJLX_ZXXT = 320; // GPRS通讯(UDP或TCP)时终端在线心跳帧(握手信息号)
	public final static int SJLX_ZDTC = 330; // GPRS通讯(UDP或TCP)时终端登录退出
	public final static int SJLX_DLMSAARE = 340; // DLMS规约AARE

	// 规约支持定义
	public static boolean TerminalZheJiangSupport = false; // 终端浙江规约
	public static boolean TerminalGuangDongSupport = false; // 终端广东规约
	public static boolean TerminalZheJiangBDZSupport = false; // 终端浙江变电站规约
	public static boolean TerminalQuanGuoSupport = false; // 终端全国规约
	public static boolean TerminalIHDSupport = false; // 终端IHD规约
	public static boolean TerminalTianJinSupport = false; // 终端天津规约
	public static boolean Terminal698Support = false; // 终端698规约
	public static boolean TerminalGuYuanSupport = false; // 终端固原规约
	public static boolean TerminalHeXingSupport = false; // 终端海兴集中器规约
	public static boolean TerminalDLMSSupport = false; // 终端DLMS规约

	public static boolean TerminalAutoTaskAdjust = false; // 终端自动上送任务不判断启动站标记
	public static boolean AmmeterQuanGuoSupport = false; // 电表全国规约
	public static boolean AmmeterZheJiangSupport = false; // 电表浙江规约
	public static boolean AmmeterTianJinSupport = false; // 电表天津规约
	public static boolean AmmeterQuanGuo2007Support = false; // 2007电表全国规约
	public static boolean WaterAmmeterSupport = false; // 水表规约
	public static boolean DLMSAmmeterSupport = false; // DLMS规约

	public static Trc4Fep Trc1 = new Trc4Fep("FrameDataAreaExplain");
	public static Log4Fep Log1 = new Log4Fep("FrameDataAreaExplain");

	public static boolean TaskLengthCheckSupport = false; // 任务程度合法性校验

	public static boolean IRANToGregorian = false;// ChangeIRANToGregorian
	public static boolean BillingDateAddOneDay;
	public static CalendarConvert Cc1 = new CalendarConvert();

}

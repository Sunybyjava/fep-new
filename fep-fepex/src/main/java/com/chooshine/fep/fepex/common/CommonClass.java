package com.chooshine.fep.fepex.common;

import java.io.InputStream;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.File;

public class CommonClass {
    public static String REALTIME_COMMUNICATION_IP = "127.0.0.1";
    public static int REALTIME_COMMUNICATION_PORT = 3000;
    public static int JDBC_DATABASETYPE = 10;
    public static String JDBC_CONNECTIONURL =
            "jdbc:oracle:thin:@127.0.0.1:1521:hxv10";
    public static String JDBC_USERNAME = "gd";
    public static String JDBC_PASSWORD = "gd";
    public static int REDOTIMES = 2; //轮召次数
    public static String STARTTIME = "00:15:00"; //轮召开始时刻
    public static int REDO_INTERVAL = 15; //轮召间隔小时数
    public static int FRAME_INTERVAL = 15; //两条命令之间的间隔秒数
  //  public static int GROUP_COUNT = 100; //批次设备数量
 //   public static int READCOUNT = 1;//要召的数据项个数
 //   public static String READ_SJX1 = "D23500";//要召的第一个数据项
 //   public static String READ_SJX2 = "D20100";//要召的第二个数据项
 //   public static String READ_SJX3 = "D20300";//要召的第三个数据项
 //   public static String READ_SQL = "SELECT * FROM DA_ZDGZ A,DA_CLDXX B WHERE A.ZDJH=B.ZDJH "
 //                                   +"AND A.ZDLX=20 AND B.CLDLX ='20' AND NOT EXISTS("
 //                                   +"SELECT 1 FROM RW_JMSJ WHERE SJSJ=TRUNC(SYSDATE-1) AND CLDBH=B.CLDBH)";
 //   public static String[] SJXItem = null;

    static {
        init();
    }

    static void init() {
        InputStream filecon = null;
        try {
            File file = new File("CustomTaskDemand.config");
            String pfile = file.getAbsolutePath().replace('\\', '/');
            Properties prop = new Properties();
            filecon = new FileInputStream(pfile); //读取配置文件中的内容
            prop.load(filecon);
            REALTIME_COMMUNICATION_IP = prop.getProperty(
                    "REALTIME_COMMUNICATION_IP", "127.0.0.1");
            REALTIME_COMMUNICATION_PORT = Integer.parseInt(prop.getProperty(
                    "COMMSERVICE_MESSAGEEXCHANGE_PORT", "3000"));
            JDBC_DATABASETYPE = Integer.parseInt(prop.getProperty(
                    "JDBC_DATABASETYPE", "10"));
            JDBC_CONNECTIONURL = prop.getProperty("JDBC_CONNECTIONURL",
                                                  "jdbc:oracle:thin:@127.0.0.1:1521:grv40");
            JDBC_USERNAME = prop.getProperty("JDBC_USERNAME", "gd");
            JDBC_PASSWORD = prop.getProperty("JDBC_PASSWORD", "gd");
            REDOTIMES = Integer.parseInt(prop.getProperty("REDOTIMES", "2")); //轮召次数
            STARTTIME = prop.getProperty("STARTTIME", "00:15:00"); //轮召开始时刻
            REDO_INTERVAL = Integer.parseInt(prop.getProperty("REDO_INTERVAL",
                    "15")); //轮召间隔小时数
            FRAME_INTERVAL = Integer.parseInt(prop.getProperty("READ_INTERVAL",
                    "15")); //两条命令之间的间隔秒数
        } catch (Exception e) {
            System.out.println(e.toString());
        } finally {
            try {
                filecon.close();
            } catch (Exception e) {
                return;
            }
        }
    }
}

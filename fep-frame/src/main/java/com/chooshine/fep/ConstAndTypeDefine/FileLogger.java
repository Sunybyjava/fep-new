package com.chooshine.fep.ConstAndTypeDefine;

import java.io.*;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.Properties;
import java.net.URI;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */

public class FileLogger {
	  private String FFullLogFirName = "./"; //Ĭ��Ϊ��ǰĿ¼
	  public FileLogger(String LogFileName) {
	    try {
	      //ȡ��ݿ�������Ϣ
	      String FFileName = "./CommService.config"; //Ĭ��Ϊwindowϵͳ
	      Properties prop = new Properties();
	      File file = new File(FFileName);
	      if (!file.exists()) {
	        FFileName = "C:/hexing/CommService.config";
	        file = new File(FFileName);
	        if (!file.exists()) {
	        }
	      }
	      InputStream filecon = null;
	      try {
	        filecon = new FileInputStream(FFileName); //��ȡ�����ļ��е�����
	      }
	      catch (FileNotFoundException ex) {

	      }
	      try {
	        prop.load(filecon);
	      }
	      catch (IOException ex1) {
	      }
	      FFullLogFirName = (String) prop.get("FEP_LOGFILE_DIRECTORY");
	      filecon.close();

	      FFullLogFirName = FFullLogFirName + LogFileName;
	      jbInit();
	    }
	    catch (Exception ex) {
	      ex.printStackTrace();
	    }
	  }
	  public FileLogger(String logDit, String LogFileName) {
			FFullLogFirName = logDit;
			FFullLogFirName = FFullLogFirName + LogFileName;
		}

	  public FileLogger(URI ur,String LogFileName) {
		    try {
		      //ȡ��ݿ�������Ϣ
		      Properties prop = new Properties();
		      File file = new File(ur);	      
		      InputStream filecon = null;
		      try {
		        filecon = new FileInputStream(file); //��ȡ�����ļ��е�����
		      }
		      catch (FileNotFoundException ex) {

		      }
		      try {
		        prop.load(filecon);
		      }
		      catch (IOException ex1) {
		      }
		      FFullLogFirName = (String) prop.get("FEP_LOGFILE_DIRECTORY");
		      filecon.close();

		      FFullLogFirName = FFullLogFirName + LogFileName;
		      jbInit();
		    }
		    catch (Exception ex) {
		      ex.printStackTrace();
		    }
		  }
	  public void WriteLog(String info) {
	    try {

	      RandomAccessFile RandomFile = null;
	      String RandomAFileName = FFullLogFirName;

	      //�ж��ļ����ȣ�����򱸷������ļ����������µ���־�ļ�
	      //�ͱ��ֵ�ǰ��־�ͱ�����־�ļ������ֻ�������ļ�ͬʱ���ڣ�������־�ļ�������Ӳ�̿ռ䲻��
	      try {
	        RandomFile = new RandomAccessFile(RandomAFileName + "Now.log", "rw");
	      }
	      catch (Exception ex1) {
	      }
	      if (RandomFile.length() > 1024 * 1024 * 5) { //����ļ�����5M���򱸷���ǰ��ͬ���ļ�
	        RandomFile.close();
	        //����ԭ�ļ�
	        File logFile = new File(RandomAFileName + "Now.log");
	        File backFile = new File(RandomAFileName + "Back.log");
	        try {
	          if (backFile.exists()) {
	            backFile.delete();
	          }
	          logFile.renameTo(backFile);
	        }
	        catch (Exception ex2) {
	        }
	        //logFile.
	        //�������ļ�
	        try {
	          RandomFile = new RandomAccessFile(RandomAFileName + "Now.log", "rw");
	        }
	        catch (Exception ex1) {
	        }
	      }

	      RandomFile.seek(RandomFile.length()); //��ָ���ƶ����ļ�ĩβ
	      Calendar cLogTime = Calendar.getInstance();
	      SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	      String sLogTime = formatter.format(cLogTime.getTime());

	      byte[] b; //ת������ʾ����
	      b = (sLogTime + ": " + info + "\r\n").getBytes(); //"\r\n"����
	      RandomFile.write(b); //
	      RandomFile.close(); //�ر��ļ���
	    }
	    catch (Exception ex) {
	    }
	  }

	  private void jbInit() throws Exception {
	  }

	}

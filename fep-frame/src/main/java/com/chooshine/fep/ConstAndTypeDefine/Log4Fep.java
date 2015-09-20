package com.chooshine.fep.ConstAndTypeDefine;

import java.io.File;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Log4Fep {
private String FFullLogFirName = "./"; //Ĭ��Ϊ��ǰĿ¼
	
	//����һ�����ټ������ڲ�ͬ����µ���Ϣ��¼
	//private int LogLevel;

	public static void main(String args[]){
		Log4Fep log = new Log4Fep("test");
		log.WriteLog("test");
	}
	
	public Log4Fep(String LogFileName) {
		FFullLogFirName = LogFileName;
		File sLog = new File("./log/");
		if ((sLog.isDirectory()) && (sLog.exists())) {
			
		}else {
			sLog.mkdir();
		}
	}

	public void WriteLog(String info) {
		Calendar cLogTime = Calendar.getInstance();
		SimpleDateFormat formatter = new SimpleDateFormat("dd");
		String sLogTime = formatter.format(cLogTime.getTime());
		File sLog = new File("./log/"+sLogTime+"/");
		if ((sLog.isDirectory()) && (sLog.exists())) {
			
		}else {
			sLog.mkdir();
		}
		String sFullFilePath = "./log/"+sLogTime+"/"+this.FFullLogFirName;
		try {

			RandomAccessFile RandomFile = null;
			String RandomAFileName = sFullFilePath;

			//�ж��ļ����ȣ�����򱸷������ļ����������µ���־�ļ�
			//�ͱ��ֵ�ǰ��־�ͱ�����־�ļ������ֻ�������ļ�ͬʱ���ڣ�������־�ļ�������Ӳ�̿ռ䲻��
			try {
				RandomFile = new RandomAccessFile(RandomAFileName + ".log", "rw");
			}
			catch (Exception ex1) {
			}
			if (RandomFile.length() > 1024 * 1024 * 5) { //����ļ�����5M���򱸷���ǰ��ͬ���ļ�
				RandomFile.close();
				//����ԭ�ļ�
				File logFile = new File(RandomAFileName + ".log");
				SimpleDateFormat formatter1 = new SimpleDateFormat("yyyyMMddHHmmss");
				String sLogTime1 = formatter1.format(cLogTime.getTime());
				File backFile = new File(RandomAFileName + sLogTime1 + "Back.log");
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
					RandomFile = new RandomAccessFile(RandomAFileName + ".log", "rw");
				}
				catch (Exception ex1) {
				}
			}

			RandomFile.seek(RandomFile.length()); //��ָ���ƶ����ļ�ĩβ
			cLogTime = Calendar.getInstance();
			formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			sLogTime = formatter.format(cLogTime.getTime());

			byte[] b; //ת������ʾ����
			b = (sLogTime + ": " + info + "\r\n").getBytes(); //"\r\n"����
			RandomFile.write(b); //
			RandomFile.close(); //�ر��ļ���
		}
		catch (Exception ex) {
		}
	}
}

package com.chooshine.fep.ConstAndTypeDefine;

import java.io.File;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Trc4Fep {
	private String TraceFileName = "";
	
	public static void main(String args[]){
		Trc4Fep trc = new Trc4Fep("test");
		trc.TraceLog("Test");
	}
	
	public Trc4Fep(String TrcFileName) {
		//��־����Ŀ¼������顢��ʼ��
		this.TraceFileName = TrcFileName;
		File sTrc = new File("./trc/");
		if ((sTrc.isDirectory()) && (sTrc.exists())) {
			
		}else {
			sTrc.mkdir();
		}
	}
	
	//��д����־����ǰ����Ҫȷ�����ڣ����޸���־�ļ�·�����
	public void TraceLog(String Msg) {
		Calendar cLogTime = Calendar.getInstance();
		SimpleDateFormat formatter = new SimpleDateFormat("dd");
		String sLogTime = formatter.format(cLogTime.getTime());
		File sTrcDate = new File("./trc/"+sLogTime+"/");
		if ((sTrcDate.isDirectory()) && (sTrcDate.exists())) {

		} else {
			sTrcDate.mkdir();
		}
		String sFullFilePath = "./trc/"+sLogTime+"/"+this.TraceFileName;
		try {

			RandomAccessFile RandomFile = null;
			String RandomAFileName = sFullFilePath;

			//�ж��ļ����ȣ�����򱸷������ļ����������µ���־�ļ�
			//�ͱ��ֵ�ǰ��־�ͱ�����־�ļ������ֻ�������ļ�ͬʱ���ڣ�������־�ļ�������Ӳ�̿ռ䲻��
			try {
				RandomFile = new RandomAccessFile(RandomAFileName + ".trc", "rw");
			}
			catch (Exception ex1) {
			}
			if (RandomFile.length() > 1024 * 1024 * 5) { //����ļ�����5M���򱸷���ǰ��ͬ���ļ�
				RandomFile.close();
				//����ԭ�ļ�
				File logFile = new File(RandomAFileName + ".trc");
				SimpleDateFormat formatter1 = new SimpleDateFormat("yyyyMMddHHmmss");
				String sLogTime1 = formatter1.format(cLogTime.getTime());
				File backFile = new File(RandomAFileName + sLogTime1 + "Back.trc");
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
					RandomFile = new RandomAccessFile(RandomAFileName + ".trc", "rw");
				}
				catch (Exception ex1) {
				}
			}

			RandomFile.seek(RandomFile.length()); //��ָ���ƶ����ļ�ĩβ
			cLogTime = Calendar.getInstance();
			formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			sLogTime = formatter.format(cLogTime.getTime());

			byte[] b; //ת������ʾ����
			b = (sLogTime + ": " + Msg + "\r\n").getBytes(); //"\r\n"����
			RandomFile.write(b); //
			RandomFile.close(); //�ر��ļ���
		}
		catch (Exception ex) {
		}
	}
}

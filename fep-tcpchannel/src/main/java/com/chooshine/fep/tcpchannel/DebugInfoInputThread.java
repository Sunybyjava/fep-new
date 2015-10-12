package com.chooshine.fep.tcpchannel;

import java.io.IOException;

public class DebugInfoInputThread extends Thread {
	private CommunicateWithCommService fCc = null;
	private TCPPortList[] fTCP;
	private int PortCount = 0;

	public DebugInfoInputThread(CommunicateWithCommService cc, TCPPortList[] up, int TCPPortCount) {
		this.fCc = cc;
		this.PortCount = TCPPortCount;
		this.fTCP = up;
	}

	public void run() {
		while (true) {
			byte b[] = new byte[100];
			try {
				System.in.read(b);
			} catch (IOException ex1) {
			}
			String s = new String(b);
			s = s.trim();
			String sTemp = fCc.GetDebugFlag();
			if (s.equals("Start Debug")) { // 显示调试内容
				if (sTemp.indexOf("D") == -1) { // 原来的调试标志无效，则增加调试显示标志，否则不改变原来的运行标志
					sTemp = sTemp.concat("D");
					fCc.ChangeDebugFlag(sTemp);
					for (int i = 0; i < PortCount; i++) {
						fTCP[i].PortObject.ChangeDebugFlag(sTemp);
					}
				}
			} else if (s.equals("Start LogWrite")) { // 记录错误日志
				if (sTemp.indexOf("F") == -1) { // 原来不记录错误日志，则增加标志，否则不修改原来的运行标志
					sTemp = sTemp.concat("F");
					fCc.ChangeDebugFlag(sTemp);
					for (int i = 0; i < PortCount; i++) {
						fTCP[i].PortObject.ChangeDebugFlag(sTemp);
					}

				}
			} else if (s.equals("Finish Debug")) { // 停止显示调试信息
				if (sTemp.indexOf("D") != -1) { // 原来有显示调试信息
					if (sTemp.indexOf("F") != -1) { // 并且有记录错误日志，则继续可以记录错误日志，不再显示调试信息
						fCc.ChangeDebugFlag("F");
						for (int i = 0; i < PortCount; i++) {
							fTCP[i].PortObject.ChangeDebugFlag("F");
						}

					} else { // 否则清空运行标志，即调试信息和日志都不需要
						fCc.ChangeDebugFlag("");
						for (int i = 0; i < PortCount; i++) {
							fTCP[i].PortObject.ChangeDebugFlag("");
						}

					}
				}
			} else if (s.equals("Finish LogWrite")) { // 停止记录错误日志
				if (sTemp.indexOf("F") != -1) { // 原来有记录错误日志
					if (sTemp.indexOf("D") != -1) { // 并且有显示调试信息，则继续可以显示调试信息，不再记录错误日志
						fCc.ChangeDebugFlag("D");
						for (int i = 0; i < PortCount; i++) {
							fTCP[i].PortObject.ChangeDebugFlag("D");
						}

					} else { // 否则清空运行标志，即调试信息和日志都不需要
						fCc.ChangeDebugFlag("");
						for (int i = 0; i < PortCount; i++) {
							fTCP[i].PortObject.ChangeDebugFlag("");
						}
					}
				}
			} else if (s.equals("Current Count")) {
				for (int i = 0; i < PortCount; i++) {
					System.out.println("TCPPort" + i + " Current Connection Count is "
							+ fTCP[i].PortObject.GetCurrentConnectionCount());
				}
			}
			try {
				Thread.sleep(1);
			} catch (InterruptedException ex) {
			}
		}
	}
}

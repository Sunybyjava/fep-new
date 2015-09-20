package com.chooshine.fep.communicate;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UpdateFramePushThread extends Thread{
	public void run(){
		while (true){
			try{
				if (CommunicationServerConstants.GlobalReceiveUpdateFrameList.size()>0){
					String sFrame = (String)CommunicationServerConstants.GlobalReceiveUpdateFrameList.get(0);
					try {
						byte[] bData = utils.str2bytes(sFrame.trim());
						DatagramSocket UDPClientSocket = new DatagramSocket();
						InetAddress destAddress = InetAddress
								.getByName(CommunicationServerConstants.UPDATEFRAME_UDPIP);
						DatagramPacket sendPacket = new DatagramPacket(
								bData,bData.length,destAddress,CommunicationServerConstants.UPDATEFRAME_UDPPORT); // 组成要发送的数据包
						UDPClientSocket.send(sendPacket);
						CommunicationServerConstants.Trc1.TraceLog("UpdateFrame "+sFrame+" push to "+CommunicationServerConstants.UPDATEFRAME_UDPIP
								+" "+CommunicationServerConstants.UPDATEFRAME_UDPPORT);
					} catch (Exception e) {
						CommunicationServerConstants.Log1.WriteLog("PushUpdateFrame Error,Frame "+sFrame+" ,Err is "+e.toString());
					}
					CommunicationServerConstants.GlobalReceiveUpdateFrameList.remove(0);
				}
				if (CommunicationServerConstants.GlobalReceiveDLMSUpdateFrameList.size()>0){
					DLMSUpFrame up = (DLMSUpFrame)CommunicationServerConstants.GlobalReceiveDLMSUpdateFrameList.get(0);
					try{
						byte[] bData = utils.str2bytes(up.DeviceAddr.trim().concat(up.Frame.trim()).trim().toUpperCase());
						DatagramSocket UDPClientSocket = new DatagramSocket();
						InetAddress destAddress = InetAddress
								.getByName(CommunicationServerConstants.UPDATEFRAME_UDPIP);
						DatagramPacket sendPacket = new DatagramPacket(
								bData,bData.length,destAddress,CommunicationServerConstants.UPDATEFRAME_UDPPORT); // 组成要发送的数据包
						UDPClientSocket.send(sendPacket);
						CommunicationServerConstants.Trc1.TraceLog("UpdateFrame "+up.Frame+" push to "+CommunicationServerConstants.UPDATEFRAME_UDPIP
								+" "+CommunicationServerConstants.UPDATEFRAME_UDPPORT);
					}catch(Exception e){
						CommunicationServerConstants.Log1.WriteLog("PushDLMSUpdateFrame Error,Frame "+up.Frame+" ,Err is "+e.toString());
					}
					CommunicationServerConstants.GlobalReceiveDLMSUpdateFrameList.remove(0);
				}
			}catch(Exception e){
				
			}
			try{
				Thread.sleep(1);
			}catch(Exception e){
				
			}
		}
	}
}

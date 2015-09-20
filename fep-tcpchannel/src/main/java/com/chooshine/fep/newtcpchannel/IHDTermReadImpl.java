package com.chooshine.fep.newtcpchannel;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;


public class IHDTermReadImpl {
	SelectionKey	channelKey;
	
	String				termLogicAddr;
	
	long RecentCommTime;
	/**
	 * 默认缓冲区长度
	 */
	public static final int DEFAULT_BUFFER_SIZE = 256;
	
	/**
	 * 帧头长度
	 */
	public static final int FRAME_HEAD_LENGTH = 4;
	
	/**
	 * 帧起始字节
	 */
	public static final byte FRAME_START_BYTE = 0x68;
	
	/**
	 * 帧结束字节
	 */
	public static final byte FRAME_END_BYTE = 0x16;
	
	/**
	 * 帧尾长度
	 */
	public static final int FRAME_TAIL_LENGTH = 2;
	
	/**
	 * 接收数据缓冲
	 */
	private HXByteBuffer 	recvDataBuffer;
	
	/**
	 * 剩余未接收数据长度
	 */
	private int		   		leftRecvLength;
	
	/**
	 * 总接收数据量(Byte)
	 */
	private long	   		totalRecvBytes;	
	
	/**
	 * 是否正在接收头
	 */
	private boolean			isRecvingHead = true;
	
	private long			totalSendBytes;
		
	public IHDTermReadImpl() {
		recvDataBuffer = new HXByteBuffer(DEFAULT_BUFFER_SIZE);
		leftRecvLength = 0;
		totalRecvBytes = 0;
		totalSendBytes = 0;
//		this.logger = logger;
	}
	
	public void setRecentCommTime(long RecentCommTime) {
		this.RecentCommTime = RecentCommTime;
	}
	
	public long getRecentCommTime() {
		return this.RecentCommTime;
	}
	
	public void write(byte[] data) {
		try{
			HXByteBuffer buffer = new HXByteBuffer(DEFAULT_BUFFER_SIZE);
			SocketChannel chl = (SocketChannel) channelKey.channel();
			buffer.clear();
			buffer.append(data);
			chl.write(ByteBuffer.wrap(buffer.getBytes()));
			totalSendBytes += buffer.length();
		}catch(IOException e){
			TCPChannelConstants.log.WriteLog("Write Socket Data Error,"+e.getMessage());
		}
	}
	
	public String getTermLogicAddr() {
		return termLogicAddr;
	}

	public void setTermLogicAddr(String termLogicAddr) {
		this.termLogicAddr = termLogicAddr;
	}	
	
	private byte [] readLeft() throws Exception {
		ByteBuffer bb = ByteBuffer.allocate(leftRecvLength);
		try{
			int recvLen = ((SocketChannel) channelKey.channel()).read(bb);
			if (recvLen < 0) {
				throw new Exception("recvLen<0");
				//连接已关闭
			} else if (recvLen == 0) {
				return null;
			}
			bb.flip();
			if (recvLen > 0 && recvLen != leftRecvLength) {
				//还没有接收完整
				leftRecvLength -= recvLen;
				recvDataBuffer.append(bb.array(), 0, recvLen);
				TCPChannelConstants.log.WriteLog("recv not yet");
				return null;
			} else {
				recvDataBuffer.append(bb.array(), 0, recvLen);
				leftRecvLength = 0;
				if (recvDataBuffer.length() == FRAME_HEAD_LENGTH && isRecvingHead) {
					//接收到的是帧头，还需要接收后面的用户数据及帧尾
					isRecvingHead = false;
					try {
						return recvFrameBody(ByteBuffer.wrap(recvDataBuffer.getBytes()));
					} catch (Exception e) {
						return null;
					}
				} else if (isRecvingHead) {
					//接收到的帧头长度不对
					dealBadFrameHead(ByteBuffer.wrap(recvDataBuffer.getBytes()));//, TCPChannelConstants.log);
					return null;
				} else {
					if (bb.get(recvLen - 1) != FRAME_END_BYTE) {
						//用户帧未以0x16结束，放弃此帧,重新开始接收帧头
						recvDataBuffer.clear();
						leftRecvLength = 0;
						this.isRecvingHead = true;
//						logger.error(sm.getString(HiConstants.ERR_FRAMETAIL_END_FLAG, HiConvHelper.bcd2AscStr(bb.array())));
						return null;
					}
					//接收到了剩余的帧体和帧尾,需要进行校验及截去帧体
					byte[] rst = checkAndTrim(recvDataBuffer.getBytes());
					if (rst != null) {
						//数据帧接收完整并且通过了校验，准备开始接收下一帧
						isRecvingHead = true;
						recvDataBuffer.clear();
						leftRecvLength = 0;
					}
					return rst;
				}
			}
		}catch(IOException e){
			e.printStackTrace();
		}
		return null;
	}
	
	private byte[] recvFrameBody(ByteBuffer frameHead) throws Exception{
		ByteBuffer bb;
		int recvLen;
		
		if (frameHead.get(0) != FRAME_START_BYTE
				|| frameHead.get(FRAME_HEAD_LENGTH - 1) != FRAME_START_BYTE) {
			//接收到帧头的开始及结束字符有误，放弃此帧,重新开始接收帧头
//			logger.error(sm.getString(HiConstants.ERR_FRAMEHEAD_START_FLAG, cc.getTermIP() + "{" + HiConvHelper.bcd2AscStr(frameHead.array()) + "}"));
			dealBadFrameHead(frameHead);//, TCPChannelConstants.log);
			isRecvingHead = true;
			return null;
		} else {
			int userDataLen = getFrameLength(frameHead);
//			if ( logger.isDebugEnabled() ){
//				logger.debug("USER DATA LENGTH=[" + userDataLen + "]");
//			}
			if (userDataLen < 0) {
				//数据长度有误,重新开始接收帧头
//				logger.error("Invalid length.");
				recvDataBuffer.clear();
				leftRecvLength = 0;
				isRecvingHead = true;
				return null;
			}
			bb = ByteBuffer.allocate(userDataLen + FRAME_TAIL_LENGTH);
			try {
				recvLen = ((SocketChannel)channelKey.channel()).read(bb);
				if (recvLen < 0) {
					throw new Exception("recvLen<0");
				}
				bb.flip();
				totalRecvBytes += recvLen;
				if (recvLen != userDataLen + FRAME_TAIL_LENGTH) {
					//数据未接收完整,接着继续接收帧体
					recvDataBuffer.clear();
					recvDataBuffer.append(bb.array(), 0, recvLen);
					leftRecvLength = userDataLen + FRAME_TAIL_LENGTH - recvLen;
					this.isRecvingHead = false;
//					if ( logger.isDebugEnabled() ){
//						logger.debug("recv frame body not complete. recvLen=[" + recvLen + "] FRAME_TAIL_LENGTH=[" + FRAME_TAIL_LENGTH + "] leftRecvLength=[" + leftRecvLength + "]");
//					}
					return null;
				} else if (bb.get(recvLen - 1) != FRAME_END_BYTE) {
					//用户帧未以0x16结束，放弃此帧,重新开始接收帧头
					recvDataBuffer.clear();
					leftRecvLength = 0;
					this.isRecvingHead = true;
//					logger.error(sm.getString(HiConstants.ERR_FRAMETAIL_END_FLAG, HiConvHelper.bcd2AscStr(bb.array())));
					return null;
				} else {
					byte[] rst = checkAndTrim(bb.array());
					if ( rst != null ){
						//数据帧接收完整并且通过了校验，准备开始接收下一帧
						isRecvingHead = true;
						recvDataBuffer.clear();
						leftRecvLength = 0;
					}
					return rst;
				}
			}catch(IOException e){
				throw new Exception(e);
			}
		}
	}
	
	/**
	 * 如果帧头是非法帧头，则将遍历其中的数据，看其中是否存在0x68起始字符，将之作为帧头的起始位置
	 * @param bb
	 * @param logger
	 */
	private void dealBadFrameHead(ByteBuffer bb){
		int size = bb.position();
		boolean isStart = false;
		int length = 0;
		
		recvDataBuffer.clear();
		leftRecvLength = 0;
		for ( int i = 0; i < size; i++ ){
			byte b = bb.get(i);
			if ( b == FRAME_START_BYTE && i != 0 ){
				isStart = true;
			}
			if ( isStart ){
				this.recvDataBuffer.append(b);
				length++;
			}
		}
		if ( isStart ){
			this.leftRecvLength = FRAME_HEAD_LENGTH - length;
		}
	}
	
	/**
	 * 处理不完整帧头
	 * @param bb
	 * @param recvLen
	 */
	private void dealNotCompleteFrameHead(ByteBuffer bb, int recvLen){
		isRecvingHead = true;
		//接收到的报文未达到帧头长度即帧头不完整
		if ( bb.get(0) != FRAME_START_BYTE ){
			//起始字符不是0x68
//			logger.error(sm.getString(HiConstants.ERR_FRAMEHEAD_START_FLAG, HiConvHelper.bcd2AscStr(bb.array())));
			dealBadFrameHead(bb);//, TCPChannelConstants.log);
		}else{
			recvDataBuffer.append(bb.array(), 0, recvLen);
			leftRecvLength = FRAME_HEAD_LENGTH - recvLen;
//			if ( logger.isDebugEnabled()){
//				logger.debug("frame head is not recved complete. recv data=[" + HiConvHelper.bcd2AscStr(bb.array())+"]");
//			}
		}
	}
		
	private byte [] readNew() throws Exception {
		ByteBuffer bb = ByteBuffer.allocate(FRAME_HEAD_LENGTH);
		try{
			int recvLen = 0;
			recvLen = ((SocketChannel)channelKey.channel()).read(bb);
			if (recvLen < 0) {
				throw new Exception("recvLen<0");
			} else if (recvLen == 0) {
				return null;
			}
			bb.flip();
			totalRecvBytes += recvLen;
			if (recvLen < FRAME_HEAD_LENGTH) {
				dealNotCompleteFrameHead(bb, recvLen);
				return null;
			} else {
				isRecvingHead = false;
				return recvFrameBody(bb);
			}
		} catch (IOException e) {
			throw new Exception(e);
		}
	}
	
	private short byte2short(byte[] bp, int index)
	  {
	    return (short)(((bp[index] & 0xFF) << 8) + (bp[(index + 1)] & 0xFF));
	  }
	
	private int getFrameLength(ByteBuffer bb){
		byte[] len = new byte[4];
		len[0] = bb.get(2);
		len[1] = bb.get(1);

		short num = byte2short(len, 0);
		return (num >>> 2 & 0xffff);
	}
	
	private static byte calcCheckSum(byte[] data, int index, int length) {
		int i = index;
		int max = index + length;
		int result = 0;
		
		while (i < max) {
			result += (data[i] & 0xFF);
			i++;
		}
		return (byte) (result % 256);
	}
	
	private byte[] checkAndTrim(byte[] data){
		int userDataLen = data.length - FRAME_TAIL_LENGTH;
		//校验数据并截取用户数据区数据
		byte cs1 = calcCheckSum(data, 0, userDataLen);
		byte cs2 = data[userDataLen];

		if (cs1 == cs2) {
			byte[] userData;
			userData = new byte[data.length];
			System.arraycopy(data, 0, userData, 0, data.length);
			return userData;
		} else {
			//帧校验和不匹配,放弃此帧,重新开始接收帧头
			recvDataBuffer.clear();
			leftRecvLength = 0;
			this.isRecvingHead = true;
//			logger.error(sm.getString(HiConstants.ERR_FRAME_CHECKSUM));
			return null;
		}
	}
	
	public byte[] read() throws Exception {
		if (leftRecvLength > 0) {
			return readLeft();
		} else {
			return readNew();
		}		
	}

	public long getTotalRecvBytes() {
		return totalRecvBytes;
	}

	public void setChannel(SelectionKey s) {
		this.channelKey = s;
	}

	public void resetTotalRecvBytes() {
		totalRecvBytes = 0;
	}
}

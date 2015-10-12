package com.chooshine.fep.tcpchannel;

import com.chooshine.fep.FrameDataAreaExplain.DataSwitch;

/**
 * <p>
 * Title: 帧搜索类
 * </p>
 *
 * <p>
 * Description: 实现对于浙规、国网的帧搜索算法
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2005
 * </p>
 *
 * <p>
 * Company:
 * </p>
 *
 * @author
 * @version
 */
public class SearchFrame {
	public SearchFrame() {
	}

	public static void main(String[] args) {
		SearchFrame sf = new SearchFrame();

		String sTemp = "6809036802C001682103001111115E16";
		// "8110000100000000000000308055202112050476168110000100000000000000308055202112050476168110000100000000000000308055202112050476168110000100000000000000308055202112050476166899053806C11668811000010000000000000030805520211205047616";
		StructSearchResult ss = sf.SearchZJFrame(sTemp, sTemp.length());
		sTemp = "684900490068008899125500006100000400020000040000F316";
		// "6819096831003100684B34127856900A61010108006416";
		ss = sf.SearchQGFrame(sTemp, sTemp.length());
		if ((ss.bFrameIsFull == true) && (ss.bFrameIsRight = true)) {
		}
	}

	public StructSearchResult SearchRightFrame(String Buffer, int BufferLen) {
		StructSearchResult ss = new StructSearchResult();
		ss = SearchQGFrame(Buffer, BufferLen); // 首先处理浙规的帧搜索，如果浙规不符合再继续处理国网的搜索
		if ((ss.bFrameIsFull) && (ss.bFrameIsRight)) {
			return ss;
		} else {
			ss = SearchZJFrame(Buffer, BufferLen);
			if ((ss.bFrameIsFull) && (ss.bFrameIsRight)) {
				return ss;
			} else {
				ss = SearchHXFrame(Buffer, BufferLen);
				if ((ss.bFrameIsFull) && (ss.bFrameIsRight)) {
					return ss;
				} else {
					ss = SearchDLMSFrame(Buffer, BufferLen);
				}
			}
		}
		return ss;
	}

	private StructSearchResult SearchHXFrame(String Buffer, int BufferLen) {
		StructSearchResult ss = new StructSearchResult();
		String ptemp;
		int i, iFirstHead;
		boolean bFirstLB; // 每一次遍历到帧头；
		ptemp = Buffer.trim();
		bFirstLB = false;
		int iContentLen = 0;
		try {
			for (i = 0; i < BufferLen; i++) {
				iFirstHead = Buffer.indexOf("0564", i);
				if (iFirstHead != -1) {
					if (!bFirstLB) {
						bFirstLB = true;
						ss.FrameHeadPort = i;
					}

					iContentLen = Integer.parseInt(Buffer.substring(iFirstHead + 4, iFirstHead + 6), 16);
					iContentLen = iContentLen * 2;
					// System.out.println("Content Length is " + iContentLen);
					if (BufferLen >= iContentLen + 6 + iFirstHead) {
						ss.bFrameIsFull = true;
						ss.iStartPosition = iFirstHead;
						ss.iEndPos = iFirstHead + iContentLen + 6;
						break;
					} else {
						i = iFirstHead;
						continue;
					}

				} else { // 第一次查找0564就没有，则马上可以退出
					break;
				}
			}

			if (ss.bFrameIsFull) {
				ptemp = Buffer.substring(ss.iStartPosition, ss.iEndPos);
				String sData = ptemp.substring(0, 16);// 报头数据区
				String CRC1 = DataSwitch.MultiCRC(sData, "3D65");
				CRC1 = DataSwitch.ReverseStringByByte(CRC1);
				if (CRC1.equals(ptemp.substring(16, 20))) {// 检查CRC校验码1
					sData = ptemp.substring(20, ptemp.length() - 4);// 主体数据区
					String CRC2 = DataSwitch.MultiCRC(sData, "3D65");
					CRC2 = DataSwitch.ReverseStringByByte(CRC2);
					if (CRC2.equals(ptemp.substring(ptemp.length() - 4, ptemp.length()))) {// 检查CRC校验码2
						ss.bFrameIsRight = true;
						ss.iProtocolTag = 30;
					} else {
						ss.bFrameIsRight = false;
					}
				} else {
					ss.bFrameIsRight = false;
				}
			}
			return ss;
		} catch (Exception e) {
		}
		return ss;
	}

	private StructSearchResult SearchDLMSFrame(String Buffer, int BufferLen) {
		StructSearchResult ss = new StructSearchResult();
		String ptemp;
		int i, iFirstHead;
		boolean bFirstLB; // 每一次遍历到帧头；
		ptemp = Buffer.trim();
		bFirstLB = false;
		int iContentLen = 0;
		try {
			for (i = 0; i < BufferLen; i++) {
				iFirstHead = Buffer.indexOf("000100010010", i);
				if (iFirstHead != -1) {
					if (!bFirstLB) {
						bFirstLB = true;
						ss.FrameHeadPort = i;
					}

					iContentLen = Integer.parseInt(Buffer.substring(iFirstHead + 12, iFirstHead + 16), 16);
					iContentLen = iContentLen * 2;
					// System.out.println("Content Length is " + iContentLen);
					if (BufferLen >= iContentLen + 16 + iFirstHead) {
						ss.bFrameIsFull = true;
						ss.iStartPosition = iFirstHead;
						ss.iEndPos = iFirstHead + iContentLen + 16;
						break;
					} else {
						i = iFirstHead;
						continue;
					}

				} else { // 第一次查找000100010010就没有，查找是否为心跳帧0001000100010012DD1000000000303030303030303030303031
					iFirstHead = Buffer.indexOf("0001000100010012DD10", i);
					if (iFirstHead != -1) {
						if (!bFirstLB) {
							bFirstLB = true;
							ss.FrameHeadPort = i;
						}

						iContentLen = Integer.parseInt(Buffer.substring(iFirstHead + 12, iFirstHead + 16), 16);
						iContentLen = iContentLen * 2;
						// System.out.println("Content Length is " +
						// iContentLen);
						if (BufferLen >= iContentLen + 16 + iFirstHead) {
							ss.bFrameIsFull = true;
							ss.iStartPosition = iFirstHead;
							ss.iEndPos = iFirstHead + iContentLen + 16;
							break;
						} else {
							i = iFirstHead;
							continue;
						}
					} else {// 事件主动上报000100010001001CC20107DA0B0B040A12210000000000010000616200FF020600200100
						iFirstHead = Buffer.indexOf("000100010001001CC2", i);
						if (iFirstHead != -1) {
							if (!bFirstLB) {
								bFirstLB = true;
								ss.FrameHeadPort = i;
							}
							if (BufferLen >= 72 + iFirstHead) {
								ss.bFrameIsFull = true;
								ss.iStartPosition = iFirstHead;
								ss.iEndPos = iFirstHead + 72;
								break;
							} else {
								i = iFirstHead;
								continue;
							}
						}
					}
				}
			}

			if (ss.bFrameIsFull) {
				ptemp = Buffer.substring(ss.iStartPosition, ss.iEndPos);
				String sTag = ptemp.substring(16, 18);// 编码标签
				if (sTag.equals("61") || sTag.equals("C4") || sTag.equals("C5") || sTag.equals("C7")
						|| sTag.equals("C2") || sTag.equals("DD")) {
					ss.bFrameIsRight = true;
					ss.iProtocolTag = 40;
				}
			}
			return ss;
		} catch (Exception e) {
		}
		return ss;
	}

	private StructSearchResult SearchZJFrame(String Buffer, int BufferLen) {
		StructSearchResult ss = new StructSearchResult();
		String ptemp;
		int i, sum, checknum, iFirstHead;
		boolean bFirstLB; // 每一次遍历到帧头；
		ptemp = Buffer.trim();
		bFirstLB = false;
		int iContentLen = 0;
		try {
			for (i = 0; i < BufferLen; i++) {
				iFirstHead = Buffer.indexOf("68", i);
				if (iFirstHead != -1) {
					if (!bFirstLB) {
						bFirstLB = true;
						ss.FrameHeadPort = i;
					}
					String sNexHead = Buffer.substring(iFirstHead + 14, iFirstHead + 16);
					if (sNexHead.equals("68")) {
						iContentLen = Integer.parseInt(Buffer.substring(iFirstHead + 14 + 6, iFirstHead + 14 + 8)
								+ Buffer.substring(iFirstHead + 14 + 4, iFirstHead + 14 + 6), 16);
						iContentLen = iContentLen * 2;
						// System.out.println("Content Length is " +
						// iContentLen);
						if ((BufferLen >= iContentLen + 26 + iFirstHead)
								&& (Buffer.substring(iContentLen + 24 + iFirstHead, iContentLen + 26 + iFirstHead)
										.equals("16"))) {
							ss.bFrameIsFull = true;
							ss.iStartPosition = iFirstHead;
							ss.iEndPos = iFirstHead + iContentLen + 26;
							break;
						} else {
							i = iFirstHead;
							continue;
						}
					} else { // 只有一个68的位置，则也需要退出，不再继续处理
						continue;
					}
				} else { // 第一次查找68就没有，则马上可以退出
					break;
				}
			}

			if (ss.bFrameIsFull) {
				sum = 0;
				checknum = -1;
				ptemp = Buffer.substring(ss.iStartPosition, ss.iEndPos);
				checknum = Integer.parseInt(ptemp.substring(iContentLen + 22, iContentLen + 24), 16);
				for (i = 1; i < (iContentLen + 24) / 2; i++) {
					sum = sum + Integer.parseInt(ptemp.substring((i - 1) * 2, (i - 1) * 2 + 2), 16);
				}
				if ((sum % 256) == checknum) {
					ss.bFrameIsRight = true;
					ss.iProtocolTag = 20;
				} else {
					ss.bFrameIsRight = false;
				}
			}
			return ss;
		} catch (Exception e) {
		}
		return ss;
	}

	private StructSearchResult SearchQGFrame(String Buffer, int BufferLen) {
		StructSearchResult ss = new StructSearchResult();
		String ptemp;
		int i, sum, checknum, iFirstHead;
		boolean bFirstLB; // 每一次遍历到帧头；
		ptemp = Buffer.trim();
		bFirstLB = false;
		int iContentLen = 0;
		try {
			for (i = 0; i < BufferLen; i++) {
				iFirstHead = Buffer.indexOf("68", i);
				if (iFirstHead != -1) {
					if (!bFirstLB) {
						bFirstLB = true;
						ss.FrameHeadPort = i;
					}
					// iNextHead = Buffer.indexOf("68", iFirstHead + 2);
					String sNextHead = Buffer.substring(iFirstHead + 10, iFirstHead + 12);
					if (sNextHead.equals("68")) {
						/*
						 * iContentLen =
						 * Integer.parseInt(Buffer.substring(iFirstHead + 4,
						 * iFirstHead + 6), 16) +
						 * Integer.parseInt(Buffer.substring(iFirstHead + 2,
						 * iFirstHead + 4), 16);
						 */
						iContentLen = Integer.parseInt(Buffer.substring(iFirstHead + 4, iFirstHead + 6)
								+ Buffer.substring(iFirstHead + 2, iFirstHead + 4), 16);
						iContentLen = iContentLen >> 2;
						iContentLen = iContentLen * 2;
						// System.out.println("Content Length is " +
						// iContentLen);
						if ((BufferLen >= iContentLen + 16 + iFirstHead)
								&& (Buffer.substring(iContentLen + 14 + iFirstHead, iContentLen + 16 + iFirstHead)
										.equals("16"))) {
							ss.bFrameIsFull = true;
							ss.iStartPosition = iFirstHead;
							ss.iEndPos = iFirstHead + iContentLen + 16;
							break;
						} else {
							continue;
						}
					} else { // 只有一个68的位置，则也需要退出，不再继续处理
						continue;
					}
				} else { // 第一次查找68就没有，则马上可以退出
					break;
				}
			}

			if (ss.bFrameIsFull) {
				sum = 0;
				checknum = -1;
				ptemp = Buffer.substring(ss.iStartPosition, ss.iEndPos);
				checknum = Integer.parseInt(ptemp.substring(iContentLen + 12, iContentLen + 14), 16);
				for (i = 7; i < (iContentLen + 14) / 2; i++) {
					sum = sum + Integer.parseInt(ptemp.substring((i - 1) * 2, (i - 1) * 2 + 2), 16);
				}
				if ((sum % 256) == checknum) {
					ss.bFrameIsRight = true;
					ss.iProtocolTag = 10;
				} else {
					ss.bFrameIsRight = false;
				}
			}
			return ss;
		} catch (Exception e) {
		}

		return ss;
	}
}

package com.chooshine.fep.FrameDataAreaExplain;

/*import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import java.awt.*;
import javax.swing.JToggleButton;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;*/

public class GetFrameInfo {
    public GetFrameInfo() {
        try {
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        try {
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    /*public static void main(String[] args){
      String sFrame="";
      boolean Check=false;
      GetFrameInfo GetF=new GetFrameInfo();
      sFrame="6899053610C10768810E0002000000000000001090149905005F16";
      if (GetF.gTerminalProtocolCheckOfZheJiang(sFrame)){
        System.out.println("终端浙规规约");
      }
      sFrame="680000632B68892C0013715700010905005407260000000000550908007675110023220600552502004363000006560007201208EA16";
      if (GetF.gTerminalProtocolCheckOfHuaLong(sFrame)){
        System.out.println("终端华隆规约");
      }
      sFrame="684D004D00684B00000000000D610101020A101010100401101C16";
      if (GetF.gTerminalProtocolCheckOfQuanGuo(sFrame)){
        System.out.println("终端全国规约");
      }
    }*/

    public static boolean gTerminalProtocolCheckOfZheJiang(String sOriginalFrame){//校验是否为终端浙江规约
      try {
        boolean result=false;
        sOriginalFrame=sOriginalFrame.trim();//消空格
        int iLenFrame=sOriginalFrame.length();
        if ((iLenFrame>=20)&&(iLenFrame%2==0)){//最小帧长度
          if ((sOriginalFrame.substring(0,2).equals("68"))&&(sOriginalFrame.substring(14,16).equals("68"))){//检查两个68位子
            int iLenData=Integer.parseInt((sOriginalFrame.substring(20,22)+sOriginalFrame.substring(18,20)),16);//数据区字节长度
            String sData=sOriginalFrame.substring(22,sOriginalFrame.length()-4);//数据区
            int iLenJYQ=iLenData+11;//检验区字节长度
            if ((iLenData*2==sData.length())&&(sOriginalFrame.substring(iLenFrame-2,iLenFrame).equals("16"))){//检查数据区长度是否一致且是否以16结尾
              int iJYM=Integer.parseInt(sOriginalFrame.substring(iLenFrame-4,iLenFrame-2),16);//检验码
              String SJYQ=sOriginalFrame.substring(0,iLenFrame-4);//校验区
              int iCheck=0;
              for(int i=0;i<iLenJYQ;i++){
                iCheck=iCheck+Integer.parseInt(SJYQ.substring(i*2,i*2+2),16);
              }
              iCheck=iCheck%256;//重新计算得到的校验码
              if (iJYM==iCheck){
                result=true;
              }
            }
          }
        }
        return result;
      }
      catch(Exception e) {return false;}
    }
    
    public static boolean gTerminalProtocolCheckOfHeXing(String sOriginalFrame){//校验是否为海兴集中器规约
    	try {
            boolean result=false;
            sOriginalFrame=sOriginalFrame.trim();//消空格
            int iLenFrame=sOriginalFrame.length();
            if ((iLenFrame>=34)&&(iLenFrame%2==0)){//最小帧长度
            	if (sOriginalFrame.substring(0,4).equals("0564")){//检查起始字0x0564
            		String sLenData=sOriginalFrame.substring(4,6);  
					int iLenData=Integer.parseInt(sLenData,16);//数据区字节长度	
					if (iLenData*2 == (sOriginalFrame.length()-6)){//检查数据区长度是否一致		
						String sData=sOriginalFrame.substring(0,16);//报头数据区
						String CRC1 = DataSwitch.MultiCRC(sData,"3D65");
						CRC1 = DataSwitch.ReverseStringByByte(CRC1);
						if (CRC1.equals(sOriginalFrame.substring(16, 20))){//检查CRC校验码1
							sData=sOriginalFrame.substring(20,iLenData * 2 + 2);//主体数据区
							String CRC2 = DataSwitch.MultiCRC(sData,"3D65");
							CRC2 = DataSwitch.ReverseStringByByte(CRC2);
							if (CRC2.equals(sOriginalFrame.substring(sOriginalFrame.length() - 4, sOriginalFrame.length()))){//检查CRC校验码2
								result=true;
							}
						}						
					}
            	}
            }
            return result;
          }
          catch(Exception e) {return false;}
    }
    public static boolean gTerminalProtocolCheckOfDLMS(String sOriginalFrame){//校验是否为DLMS规约
    	try {
            boolean result=false;
            sOriginalFrame=sOriginalFrame.trim();//消空格
            int iLenFrame=sOriginalFrame.length();
            if ((iLenFrame>=16)&&(iLenFrame%2==0)){//最小帧长度
            	if (sOriginalFrame.substring(0,12).equals("000100010010")){//检查起始字000100010010
            		String sLenData=sOriginalFrame.substring(12,16);  
					int iLenData=Integer.parseInt(sLenData,16);//数据区字节长度	
					if (iLenData*2 == (sOriginalFrame.length()-16)){//检查数据区长度是否一致	,DLMS没有结束符	
						result=true;											
					}
            	}else {//000100010001001CC20107DA0B0B040A12210000000000010000616200FF020600200100
            		if (sOriginalFrame.substring(0,18).equals("000100010001001CC2")){//检查起始字000100010001001CC2
                		if (72 == sOriginalFrame.length()){//检查数据区长度是否一致	,DLMS没有结束符	
    						result=true;											
    					}
                	}
            	}
            }
            return result;
          }
          catch(Exception e) {return false;}
    }
    public static boolean gTerminalProtocolCheckOfQuanGuo(String sOriginalFrame){//校验是否为终端全国规约
      try {
        boolean result=false;
        sOriginalFrame=sOriginalFrame.trim();//消空格
        int iLenFrame=sOriginalFrame.length();
        if ((iLenFrame>=40)&&(iLenFrame%2==0)){//最小帧长度
          if ((sOriginalFrame.substring(0,2).equals("68"))&&(sOriginalFrame.substring(10,12).equals("68"))){//检查两个68位置
            if (sOriginalFrame.substring(2,6).equals(sOriginalFrame.substring(6,10))){//数据区长度是否一致
              String sLenData=sOriginalFrame.substring(4,6)+sOriginalFrame.substring(2,4);  //高低字节倒置
              int iLenData=Integer.parseInt(sLenData,16)/4;//数据区字节长度
              String sData=sOriginalFrame.substring(12,sOriginalFrame.length()-4);//数据区
              int iLenJYQ=iLenData;//国网检验区和数据区一致
              if ((iLenData*2==sData.length())&&(sOriginalFrame.substring(iLenFrame-2,iLenFrame).equals("16"))){//检查数据区长度是否一致且是否以16结尾
                int iJYM=Integer.parseInt(sOriginalFrame.substring(iLenFrame-4,iLenFrame-2),16);//检验码
                String SJYQ=sData;//校验区
                int iCheck=0;
                for(int i=0;i<iLenJYQ;i++){
                  iCheck=iCheck+Integer.parseInt(SJYQ.substring(i*2,i*2+2),16);
                }
                iCheck=iCheck%256;//重新计算得到的校验码
                if (iJYM==iCheck){
                  result=true;
                }
              }
            }
          }
        }
        return result;
      }
      catch(Exception e) {return false;}
    }
    public static boolean gTerminalProtocolCheckOfIHD(String sOriginalFrame){//校验是否为终端IHD规约
        try {
        	boolean result=false;
            sOriginalFrame=sOriginalFrame.trim();//消空格
            int iLenFrame=sOriginalFrame.length();
            if ((iLenFrame>=36)&&(iLenFrame%2==0)){//最小帧长度
            	if ((sOriginalFrame.substring(0,2).equals("68"))&&(sOriginalFrame.substring(6,8).equals("68"))){//检查两个68位置
            		String sLenData=sOriginalFrame.substring(4,6)+sOriginalFrame.substring(2,4);  //高低字节倒置
            		int iLenData=Integer.parseInt(sLenData,16)/4;//数据区字节长度
            		String sData=sOriginalFrame.substring(8,sOriginalFrame.length()-4);//数据区
            		int iLenJYQ=iLenData;//国网检验区和数据区一致
            		if ((iLenData*2==sData.length())&&(sOriginalFrame.substring(iLenFrame-2,iLenFrame).equals("16"))){//检查数据区长度是否一致且是否以16结尾
            			int iJYM=Integer.parseInt(sOriginalFrame.substring(iLenFrame-4,iLenFrame-2),16);//检验码
            			String SJYQ=sData;//校验区
            			int iCheck=0;
            			for(int i=0;i<iLenJYQ;i++){
            				iCheck=iCheck+Integer.parseInt(SJYQ.substring(i*2,i*2+2),16);
            			}
            			iCheck=iCheck%256;//重新计算得到的校验码
            			if (iJYM==iCheck){
            				result=true;
            			}
            		}
            	}             
            }
            return result;
        }
        catch(Exception e) {return false;}
    }
    public static boolean gTerminalProtocolCheckOfHS(String sOriginalFrame){//校验是否为杭州水表规约
    	try {
            boolean result=false;
            sOriginalFrame=sOriginalFrame.trim();//消空格
            int iLenFrame=sOriginalFrame.length();
            if ((iLenFrame>=20)&&(iLenFrame%2==0)){//最小帧长度
              if (sOriginalFrame.substring(0,2).equals("9B")){//检查两个9B位子
                int iLenData=Integer.parseInt(sOriginalFrame.substring(12,14),16) * 2;//数据区字节长度
                String cs = sOriginalFrame.substring(16 + iLenData, 18 + iLenData)+sOriginalFrame.substring(14 + iLenData, 16 + iLenData);
                String cstemp = DataSwitch.CRC16(sOriginalFrame.substring(0, 14 + iLenData));
                if (sOriginalFrame.substring(18 + iLenData, 20 + iLenData).equals("9D") && cs.equals(cstemp)){
                	result=true;
                }
              }
            }
            return result;
          }
          catch(Exception e) {return false;}
        }
    public static boolean gTerminalProtocolCheckOfHuaLong(String sOriginalFrame){//校验是否为终端华隆规约
      try {
        boolean result=false;
        sOriginalFrame=sOriginalFrame.trim();//消空格
        int iLenFrame=sOriginalFrame.length();
        if ((iLenFrame>=20)&&(iLenFrame%2==0)){//最小帧长度
          if ((sOriginalFrame.substring(0,2).equals("68"))&&(sOriginalFrame.substring(10,12).equals("68"))){//检查两个68位子
            int iLenData=Integer.parseInt(sOriginalFrame.substring(14,16),16);//数据区字节长度
            String sData=sOriginalFrame.substring(16,sOriginalFrame.length()-4);//数据区
            int iLenJYQ=iLenData+8;//检验区字节长度
            if ((iLenData*2==sData.length())&&(sOriginalFrame.substring(iLenFrame-2,iLenFrame).equals("16"))){//检查数据区长度是否一致且是否以16结尾
              int iJYM=Integer.parseInt(sOriginalFrame.substring(iLenFrame-4,iLenFrame-2),16);//检验码
              String SJYQ=sOriginalFrame.substring(0,iLenFrame-4);//校验区
              int iCheck=0;
              for(int i=0;i<iLenJYQ;i++){
                iCheck=iCheck+Integer.parseInt(SJYQ.substring(i*2,i*2+2),16);
              }
              iCheck=iCheck%256;//重新计算得到的校验码
              if (iJYM==iCheck){
                result=true;
              }
            }
          }
        }
        return result;
      }
      catch(Exception e) {return false;}
    }
    public static String gGetParityByteOfZheJiang(String sOriginalFrame){//计算终端浙江规约的校验码
      String result="";
      try {
        sOriginalFrame=sOriginalFrame.trim();//消空格
        int iLenFrame=sOriginalFrame.length();
        if ((iLenFrame>=20)&&(iLenFrame%2==0)){//最小帧长度
          if ((sOriginalFrame.substring(0,2).equals("68"))&&(sOriginalFrame.substring(14,16).equals("68"))){//检查两个68位子
            int iLenData=Integer.parseInt((sOriginalFrame.substring(20,22)+sOriginalFrame.substring(18,20)),16);//数据区字节长度
            String sData=sOriginalFrame.substring(22,sOriginalFrame.length()-4);//数据区
            int iLenJYQ=iLenData+11;//检验区字节长度
            if ((iLenData*2==sData.length())&&(sOriginalFrame.substring(iLenFrame-2,iLenFrame).equals("16"))){//检查数据区长度是否一致且是否以16结尾
              String SJYQ=sOriginalFrame.substring(0,iLenFrame-4);//校验区
              int iCheck=0;
              for(int i=0;i<iLenJYQ;i++){
                iCheck=iCheck+Integer.parseInt(SJYQ.substring(i*2,i*2+2),16);
              }
              iCheck=iCheck%256;//重新计算得到的校验码
              String sJYM=DataSwitch.IntToHex((""+iCheck),"00");
              result=sOriginalFrame.substring(0,iLenFrame-4)+sJYM+"16";
            }
          }
        }
        return result;
      }
      catch(Exception e) {return result;}
    }
    public static String gGetParityByteOfQuanGuo(String sOriginalFrame){////计算终端全国规约的校验码
      String result="";
      try {
        sOriginalFrame=sOriginalFrame.trim();//消空格
        int iLenFrame=sOriginalFrame.length();
        if ((iLenFrame>=40)&&(iLenFrame%2==0)){//最小帧长度
          if ((sOriginalFrame.substring(0,2).equals("68"))&&(sOriginalFrame.substring(10,12).equals("68"))){//检查两个68位置
            if (sOriginalFrame.substring(2,6).equals(sOriginalFrame.substring(6,10))){//数据区长度是否一致
              String sLenData=sOriginalFrame.substring(4,6)+sOriginalFrame.substring(2,4);  //高低字节倒置
              int iLenData=Integer.parseInt(sLenData,16)/4;//数据区字节长度
              String sData=sOriginalFrame.substring(12,sOriginalFrame.length()-4);//数据区
              int iLenJYQ=iLenData;//国网检验区和数据区一致
              if ((iLenData*2==sData.length())&&(sOriginalFrame.substring(iLenFrame-2,iLenFrame).equals("16"))){//检查数据区长度是否一致且是否以16结尾
                String SJYQ=sData;//校验区
                int iCheck=0;
                for(int i=0;i<iLenJYQ;i++){
                  iCheck=iCheck+Integer.parseInt(SJYQ.substring(i*2,i*2+2),16);
                }
                iCheck=iCheck%256;//重新计算得到的校验码
                String sJYM=DataSwitch.IntToHex((""+iCheck),"00");
                result=sOriginalFrame.substring(0,iLenFrame-4)+sJYM+"16";
              }
            }
          }
        }
        return result;
      }
      catch(Exception e) {return result;}
    }
    public static String gGetParityByteOfIHD(String sOriginalFrame){////计算终端IHD规约的校验码
        String result="";
        try {
        	sOriginalFrame=sOriginalFrame.trim();//消空格
        	int iLenFrame=sOriginalFrame.length();
        	if ((iLenFrame>=36)&&(iLenFrame%2==0)){//最小帧长度
        		if ((sOriginalFrame.substring(0,2).equals("68"))&&(sOriginalFrame.substring(6,8).equals("68"))){//检查两个68位置              
        			String sLenData=sOriginalFrame.substring(4,6)+sOriginalFrame.substring(2,4);  //高低字节倒置
        			int iLenData=Integer.parseInt(sLenData,16)/4;//数据区字节长度
        			String sData=sOriginalFrame.substring(8,sOriginalFrame.length()-4);//数据区
        			int iLenJYQ=iLenData;//国网检验区和数据区一致
        			if ((iLenData*2==sData.length())&&(sOriginalFrame.substring(iLenFrame-2,iLenFrame).equals("16"))){//检查数据区长度是否一致且是否以16结尾
        				String SJYQ=sData;//校验区
        				int iCheck=0;
        				for(int i=0;i<iLenJYQ;i++){
        					iCheck=iCheck+Integer.parseInt(SJYQ.substring(i*2,i*2+2),16);
        				}
        				iCheck=iCheck%256;//重新计算得到的校验码
        				String sJYM=DataSwitch.IntToHex((""+iCheck),"00");
        				result=sOriginalFrame.substring(0,iLenFrame-4)+sJYM+"16";
        			}              
        		}
        	}
        	return result;
        }
        catch(Exception e) {return result;}
      }

    public static String gGetParityByteOfHuaLong(String sOriginalFrame){//计算终端华隆规约的校验码
      String result="";
      try {
        sOriginalFrame=sOriginalFrame.trim();//消空格
        int iLenFrame=sOriginalFrame.length();
        if ((iLenFrame>=20)&&(iLenFrame%2==0)){//最小帧长度
          if ((sOriginalFrame.substring(0,2).equals("68"))&&(sOriginalFrame.substring(10,12).equals("68"))){//检查两个68位子
            int iLenData=Integer.parseInt(sOriginalFrame.substring(14,16),16);//数据区字节长度
            String sData=sOriginalFrame.substring(16,sOriginalFrame.length()-4);//数据区
            int iLenJYQ=iLenData+8;//检验区字节长度
            if ((iLenData*2==sData.length())&&(sOriginalFrame.substring(iLenFrame-2,iLenFrame).equals("16"))){//检查数据区长度是否一致且是否以16结尾
              String SJYQ=sOriginalFrame.substring(0,iLenFrame-4);//校验区
              int iCheck=0;
              for(int i=0;i<iLenJYQ;i++){
                iCheck=iCheck+Integer.parseInt(SJYQ.substring(i*2,i*2+2),16);
              }
              iCheck=iCheck%256;//重新计算得到的校验码
              String sJYM=DataSwitch.IntToHex((""+iCheck),"00");
              result=sOriginalFrame.substring(0,iLenFrame-4)+sJYM+"16";
            }
          }
        }
        return result;
      }
      catch(Exception e) {return result;}
    }
}






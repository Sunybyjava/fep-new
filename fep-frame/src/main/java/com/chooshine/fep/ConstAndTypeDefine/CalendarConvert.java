package com.chooshine.fep.ConstAndTypeDefine;
public class CalendarConvert {

	public static int[] IranDayTab = {0,0,31,62,93,124,155,186,216,246,276,306,336};
	public static int[] IranYearDay = {
			0, 0, 366, 731, 1096, 1461, 1827, 2192, 2557,
			2922, 3288, 3653, 4018, 4383, 4749, 5114, 5479, 5844, 6210, 6575,
			6940, 7305, 7671, 8036, 8401, 8766, 9132, 9497, 9862, 10227, 10592,
			10958, 11323, 11688, 12053, 12419, 12784, 13149, 13514, 13880,
			14245, 14610, 14975, 15341, 15706, 16071, 16436, 16802, 17167,
			17532, 17897, 18263, 18628, 18993, 19358, 19724, 20089, 20454,
			20819, 21185, 21550, 21915, 22280, 22645, 23011, 23376, 23741,
			24106, 24472, 24837, 25202, 25567, 25933, 26298, 26663, 27028,
			27394, 27759, 28124, 28489, 28855, 29220, 29585, 29950, 30316,
			30681, 31046, 31411, 31777, 32142, 32507, 32872, 33238, 33603,
			33968, 34333, 34698, 35064, 35429, 35794, 36159
	};
	public static int[] DayTab = {
		    0,0,31,59,90,120,151,181,212,243,273,304,334
	};

	public static int[] DayTabR = {
	    0,0,31,60,91,121,152,182,213,244,274,305,335
	};
//	---------------------------------------------------------------------------------------------------------------//
//	鍑芥暟鍔熻兘锛氭牴鎹棩鏈堝勾璁＄畻鍑轰紛鏈楀巻鐨勬棩鏁�
//	鍏ュ彛鍙傛暟锛氫紛鏈楁棩鍘嗘棩璧峰鍦板潃
//	---------------------------------------------------------------------------------------------------------------//
	public static int TOU_GetGmDay_IRAN(String IAddr ){
	    int Day;
	    int Mon,Year;
	    
	    Mon = Integer.parseInt(IAddr.substring(2,4));
		Year= Integer.parseInt(IAddr.substring(0,2));
		if ( Year < 79 ){
	        Year = 100 + Year;
		}
		Day = Integer.parseInt(IAddr.substring(4,6))-1 + IranDayTab[Mon] + IranYearDay[Year-79+1];
	   	return (Day);
	}
//	--------------------------------------------------------------------
//	寰楀埌褰撳墠鐨勬棩
//	--------------------------------------------------------------------
	public static int TOU_GetGmDay( String GAddr )
	{
	    int Day,Mon,Year;

	    Mon = Integer.parseInt(GAddr.substring(2,4));
		Year= Integer.parseInt(GAddr.substring(0,2));
		Day = Integer.parseInt(GAddr.substring(4,6))-1 + DayTab[Mon] + Year*365 + (Year>>2) + 1;
	   
	    if ( (Year&0x03)==0 && Mon<=2 ) Day--;
	    return Day;
	}
	/*

//---------------------------------------------------------------------------------------------------------------//
//鍑芥暟鍔熻兘锛氭牴鎹棩鏈堝勾璁＄畻鍑轰紛鏈楀巻鐨勫皬鏃舵暟
//鍏ュ彛鍙傛暟锛氫紛鏈楁棩鍘嗘椂璧峰鍦板潃,浣嗘槸闇�鎶婁腑闂寸殑鍛ㄥ幓鎺�
//---------------------------------------------------------------------------------------------------------------//
	public long TOU_GetGmHour_IRAN(char[] Addr)
	{
		return ( (long)BF_BCD_Byte(*(Addr)) + (unsigned long)TOU_GetGmDay_IRAN(Addr+1)*24);
	} 

//---------------------------------------------------------------------------------------------------------------//
//鍑芥暟鍔熻兘锛氭牴鎹棩鏈堝勾鏃跺垎璁＄畻鍑哄綋鍓嶇殑鍒嗘暟
//鍏ュ彛鍙傛暟锛氫紛鏈楁棩鍘嗗垎璧峰鍦板潃
//---------------------------------------------------------------------------------------------------------------//
	public long TOU_GetGmMin_IRAN(String IAddr)
	{
		return ( (unsigned long)BF_BCD_Byte(*(Addr)) + (unsigned long)BF_BCD_Byte(*(Addr+1))*60 +
			     (unsigned long)TOU_GetGmDay_IRAN(Addr+3)*24*60);
	} 
*/
//	---------------------------------------------------------------------------------------------------------------//
//	鍑芥暟鍔熻兘锛氭牴鎹叕鍘嗚绠楀嚭鏉ョ殑鏃ユ暟寰楀埌浼婃湕鏃ュ巻鐨勫勾鏈堟棩
//	---------------------------------------------------------------------------------------------------------------//
	public static String TOU_Days_Date_IRAN(int days ){
		String IAddr = "";
	    int  i,tmp;
	    //Year
	    for ( i=100; i>0; i-- ) 
	    {
	    	if ( days >= IranYearDay[i] )	 
	            break;	
	    }
	    tmp = 78+i;
	    if (tmp >= 100 )
		{
	        tmp -= 100;
		}
	    String stemp = "" +tmp;
	    stemp = "00".substring(0, 2 - stemp.length()) + stemp;
	    IAddr = stemp;
	    //Month
	    days -= IranYearDay[i];	    
	    for ( i=12; i>0; i-- ) 
	    {
	    	if ( days >= IranDayTab[i] )	   
	            break;
	    }
	    stemp = "00".substring(0, 2 - (""+i).length()) + (""+i);
	    IAddr = IAddr + stemp;
	    //Day
	    days -= IranDayTab[i];
	    stemp = "" + (days + 1);
	    stemp = "00".substring(0, 2 - stemp.length()) + stemp;
	    IAddr = IAddr + stemp;
	    
	    return IAddr;
	}
//	---------------------------------------------------------------------------------------------------------------//
//	鍑芥暟鍔熻兘锛氭牴鎹棩鏁板緱鍒板叕鍘嗙殑骞存湀鏃�
//	---------------------------------------------------------------------------------------------------------------//
	public static String TOU_Days_Date_G(int days)
	{
		String GAddr = "";
		int Tmp;
		int Y1,Y2;
		String stemp = "";

	    Tmp = days;
	    
	    Y1 = Tmp / (365*4+1);
	    Y2 = Tmp % (365*4+1);
	    if ( Y2 >= 366 )
	    {
	    	stemp = "" + (Y1*4 + (Y2-1)/365);       // Year
	       // *(Day+2) = Y1*4 + (Y2-1)/365;       // Year	    	
	        Y2 = (Y2-1)%365;
	    }
	    else 
	    	stemp = "" + (Y1*4);
	    GAddr = stemp;
	    
	    if ((Integer.parseInt(stemp)&0x03)!=0x00)
		{
		    for (Y1=12;Y1>0;Y1--)
		        if ( (Y2>=DayTab[Y1]) )
		            break;
			stemp = "" + Y1;                  	// Month			
		    stemp = "00".substring(0, 2 - stemp.length()) + stemp;
		    GAddr = GAddr + stemp;
			stemp = "" + (Y2 +1 - DayTab[Y1]);      // Day		
		    stemp = "00".substring(0, 2 - stemp.length()) + stemp;
			GAddr = GAddr + stemp;
		}
		else
		{
			for (Y1=12;Y1>0;Y1--)
		        if ( (Y2>=DayTabR[Y1]) )
		            break;
			stemp = "" + Y1;                  	// Month		
		    stemp = "00".substring(0, 2 - stemp.length()) + stemp;
		    GAddr = GAddr + stemp;
			stemp = "" + (Y2 +1 - DayTabR[Y1]);     // Day
		    stemp = "00".substring(0, 2 - stemp.length()) + stemp;
			GAddr = GAddr + stemp;
		}   
	    return GAddr;
	}

//	---------------------------------------------------------------------------------------------------------------//
//	鍑芥暟鍔熻兘锛氭妸鍏巻杞崲鎴愪紛鏈楀巻
//	鍙槸鎶婃棩鏈熻浆鎹簡涓�笅锛屾椂鍒嗕笉闇�杞崲
//	鍏ュ彛鍙傛暟锛欸Addr----鍏巻鏃堕棿鍦板潃锛涘甫鍏ョ殑鍦板潃涓虹锛�
//				IAddr----浼婃湕鍘嗘椂闂村湴鍧�紱甯﹀叆鐨勫湴鍧�负绉掞紱
//	---------------------------------------------------------------------------------------------------------------//
	public static String TOU_GregorianToIRAN(String GAddr)
	{
		String IAddr = "";
	    int  Days;	    
	    Days = TOU_GetGmDay(GAddr.substring(0,6));//璁＄畻寰楀埌褰撳墠鍏巻涓嬬殑鏃ユ暟锛屽墠鎻愭槸褰撳墠骞存�鏄湪2000骞翠箣鍚�								

	    if (( Days >=79 )&&(Days<36525))
	    {        
	        Days -= 79;
	         
	        IAddr = TOU_Days_Date_IRAN( Days );				//鏍规嵁褰撳墠鏃ユ暟杞崲鎴愪紛鏈楀巻
//	      鎶婂綋鍓嶅懆鏃跺垎绉掓惉鍒颁紛鏈楁棩鍘嗛噷鍘�
			if(Integer.parseInt(GAddr.substring(6,8)) == 0){
	        	IAddr = IAddr + "07";
	        }else {
	        	IAddr = IAddr + GAddr.substring(6,8);
	        }	        	
	        IAddr = IAddr + GAddr.substring(8,14);
	
	    }
		else								//濡傛灉鍏巻鐨勬棩鏈熷嚭閿欙紝鍒欒浆鎹箣鍚庣殑浼婃湕鏃ュ巻鐨勬棩鏈熶负鍏�
		{
//			鎶婂綋鍓嶆椂鍒嗙鎼埌浼婃湕鏃ュ巻閲屽幓
			IAddr = "00000001" +  GAddr.substring(8,14);	    	
		}
	    return IAddr;
	}

//	---------------------------------------------------------------------------------------------------------------//
//	鍑芥暟鍔熻兘锛氭妸浼婃湕鍘嗚浆鎹㈡垚鍏巻
//	鍏ュ彛鍙傛暟锛欸Addr----鍏巻鏃堕棿鍦板潃锛涘甫鍏ョ殑鍦板潃涓虹锛�
//				IAddr----浼婃湕鍘嗘椂闂村湴鍧�紱甯﹀叆鐨勫湴鍧�负绉掞紱
//	---------------------------------------------------------------------------------------------------------------//
	public static String TOU_IRANToGregorian( String IAddr)
	{
		String GAddr = "";	
	    int  Days;	    
	    Days = TOU_GetGmDay_IRAN(IAddr.substring(0,6))+79;
	    if (Days<36525)
	    {
	    	GAddr = TOU_Days_Date_G(Days );
	    	if(Integer.parseInt(IAddr.substring(6,8)) == 0){
	    		GAddr = GAddr + "07";
	        }else {
	        	GAddr = GAddr + IAddr.substring(6,8);
	        }	        	
	    	GAddr = GAddr + IAddr.substring(8,14);	   
	    }
		else									//if have error, then the Gregorian is 0
		{
			GAddr = "00000001" +  IAddr.substring(8,14);	
		}
	return GAddr;
	}
	public static void main(String[] args){
		String GAddr = "10112901135200";
		String IAddr = "89090801135200";
		String sDT = TOU_IRANToGregorian(IAddr);
		System.out.println("IAddr:" + IAddr +" GAddr:" + sDT);
		sDT = TOU_GregorianToIRAN(GAddr);
		System.out.println("GAddr:" + GAddr +" IAddr:" + sDT);
	}
}

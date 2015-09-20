/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.chooshine.fep.communicate;

/**
 *
 * 
 */
public class AES {
    private static short[]sbox={
         // populate the Sbox matrix
/*        0     1     2     3     4     5     6     7     8     9     a     b     c     d     e     f */
/*0*/  0x63, 0x7c, 0x77, 0x7b, 0xf2, 0x6b, 0x6f, 0xc5, 0x30, 0x01, 0x67, 0x2b, 0xfe, 0xd7, 0xab, 0x76,
/*1*/  0xca, 0x82, 0xc9, 0x7d, 0xfa, 0x59, 0x47, 0xf0, 0xad, 0xd4, 0xa2, 0xaf, 0x9c, 0xa4, 0x72, 0xc0,
/*2*/  0xb7, 0xfd, 0x93, 0x26, 0x36, 0x3f, 0xf7, 0xcc, 0x34, 0xa5, 0xe5, 0xf1, 0x71, 0xd8, 0x31, 0x15,
/*3*/  0x04, 0xc7, 0x23, 0xc3, 0x18, 0x96, 0x05, 0x9a, 0x07, 0x12, 0x80, 0xe2, 0xeb, 0x27, 0xb2, 0x75,
/*4*/  0x09, 0x83, 0x2c, 0x1a, 0x1b, 0x6e, 0x5a, 0xa0, 0x52, 0x3b, 0xd6, 0xb3, 0x29, 0xe3, 0x2f, 0x84,
/*5*/  0x53, 0xd1, 0x00, 0xed, 0x20, 0xfc, 0xb1, 0x5b, 0x6a, 0xcb, 0xbe, 0x39, 0x4a, 0x4c, 0x58, 0xcf,
/*6*/  0xd0, 0xef, 0xaa, 0xfb, 0x43, 0x4d, 0x33, 0x85, 0x45, 0xf9, 0x02, 0x7f, 0x50, 0x3c, 0x9f, 0xa8,
/*7*/  0x51, 0xa3, 0x40, 0x8f, 0x92, 0x9d, 0x38, 0xf5, 0xbc, 0xb6, 0xda, 0x21, 0x10, 0xff, 0xf3, 0xd2,
/*8*/  0xcd, 0x0c, 0x13, 0xec, 0x5f, 0x97, 0x44, 0x17, 0xc4, 0xa7, 0x7e, 0x3d, 0x64, 0x5d, 0x19, 0x73,
/*9*/  0x60, 0x81, 0x4f, 0xdc, 0x22, 0x2a, 0x90, 0x88, 0x46, 0xee, 0xb8, 0x14, 0xde, 0x5e, 0x0b, 0xdb,
/*a*/  0xe0, 0x32, 0x3a, 0x0a, 0x49, 0x06, 0x24, 0x5c, 0xc2, 0xd3, 0xac, 0x62, 0x91, 0x95, 0xe4, 0x79,
/*b*/  0xe7, 0xc8, 0x37, 0x6d, 0x8d, 0xd5, 0x4e, 0xa9, 0x6c, 0x56, 0xf4, 0xea, 0x65, 0x7a, 0xae, 0x08,
/*c*/  0xba, 0x78, 0x25, 0x2e, 0x1c, 0xa6, 0xb4, 0xc6, 0xe8, 0xdd, 0x74, 0x1f, 0x4b, 0xbd, 0x8b, 0x8a,
/*d*/  0x70, 0x3e, 0xb5, 0x66, 0x48, 0x03, 0xf6, 0x0e, 0x61, 0x35, 0x57, 0xb9, 0x86, 0xc1, 0x1d, 0x9e,
/*e*/  0xe1, 0xf8, 0x98, 0x11, 0x69, 0xd9, 0x8e, 0x94, 0x9b, 0x1e, 0x87, 0xe9, 0xce, 0x55, 0x28, 0xdf,
/*f*/  0x8c, 0xa1, 0x89, 0x0d, 0xbf, 0xe6, 0x42, 0x68, 0x41, 0x99, 0x2d, 0x0f, 0xb0, 0x54, 0xbb, 0x16 
    };
    private short[]invbox;
    private static char[]hex={'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};
    private short log[];
    private short alog[];
    private short rc[];
    private static int nr[]={10,12,14};   
    private static int nb[]={ 4, 6, 8};       
    private short[]text;
    private short[]key;
    private short[][]word;
    private int INDEX=0;   
    private int Nb=4;
    private int Nr=10;

    public AES(){
        init();
    }

    private void init(){
  /* create table hexlog and hexalog start and create invbox */
        log=new short[256];
        alog=new short[256];
        invbox=new short[256];
        alog[0]=1;
        for(int i=1;i<256;i++)
        {
            alog[i]=(short) ( (alog[i - 1] << 1)^ alog[i - 1] ); 
            if(alog[i]>0xff) alog[i]^=0x11b;                     // if over 255,then ^ 0x11b.
            log[alog[i]]=(short) i;
            invbox[sbox[i]]=(short) i;
        }
        invbox[sbox[0]]=0;
        log[1]=0;
  /* create table hexlog and hexalog end*/
        rc=new short[14];
        rc[0]=0x01;
        for(int i=1;i<14;i++) rc[i]=FFmul(0x02,rc[i-1]);
//        for(int i=0;i<rc.length;i++) System.out.print(rc[i]+" ");System.out.println("");//check the rc[]
    }

    public String Cipher(){
        String ans="";
        short state[]=new short[16];
        short codetext[]=new short[text.length];
        KeyExpansion();                
        int nb4=Nb*4;
        for(int i=0;i<text.length;i+=nb4)
        {
            for(int col=0,p=0;col<Nb;col++)          //text to state
                for(int row=0;row<4;row++)
                {
                    state[row*4+col]=text[i+p];
                    p++;
                }
            AddRoundKey(state,0);
            for(int j=0;j<Nr-1;j++)
            {
                SubBytes(state);
                ShiftRow(state);
                MixColumns(state);
                AddRoundKey(state,j+1);
            }
            SubBytes(state);              //the last time not do MixColumns.
            ShiftRow(state);
            AddRoundKey(state,Nr);
            for(int col=0,p=0;col<Nb;col++)         //state to codetext
                for(int row=0;row<4;row++)
                {
                    codetext[i+p]=state[row*4+col];
                    p++;
                }
        }
        ans=Hex2String(codetext);
        return ans;
    }

    public String InvCipher(String hexcode){
        short state[]=new short[16];
        text=HexString2Short(hexcode);
        short[]dtext=new short[text.length];
        String ans="";
        KeyExpansion();               
        int nb4=Nb*4;
        for(int i=0;i<text.length;i+=nb4)
        {
            for(int col=0,p=0;col<Nb;col++)          //text to state
                for(int row=0;row<4;row++)
                {
                    state[row*4+col]=text[i+p];
                    p++;
                }
            AddRoundKey(state,Nr);
            for(int j=0;j<Nr-1;j++)
            {
                InvShiftRow(state);
                InvSubBytes(state);
                AddRoundKey(state,Nr-j-1);
                InvMixColumns(state); 
            }
            InvShiftRow(state);
            InvSubBytes(state);              //the last time not do MixColumns.
            AddRoundKey(state,0);
            for(int col=0,p=0;col<Nb;col++)         //state to codetext
                for(int row=0;row<4;row++)
                {
                    dtext[i+p]=state[row*4+col];
                    p++;
                }
        }
        ans=Hex2String(dtext);
        return ans;
    }
    
    private void KeyExpansion(){
        Nr=nr[INDEX];              
        Nb=nb[INDEX];              
        int len=Nb*(Nr+1);
        short []temp=new short[4];
        word=new short[len][4];
        for(int i=0;i<Nb;i++)
            for(int j=0;j<4;j++) word[i][j]=key[i * 4 + j];
        for(int i=Nb;i<len;i++)
        {
            for(int j=0;j<4;j++) temp[j]=word[i-1][j];
            if(i%Nb==0)
            {
                RotWord(temp);
                SubWord(temp);
                temp[0]^=rc[i/Nb-1];
            }
            for(int j=0;j<4;j++) word[i][j]=(short)(temp[j] ^ word[i-Nb][j]);
        }
    }

    private void RotWord(short[]a){
        short t=a[0];
        for(int i=0;i<3;i++) a[i]=a[i+1];
        a[3]=t;
    }

    private void SubWord(short[]a){
        for(int i=0;i<4;i++) a[i]=sbox[a[i]];
    }

    private void SubBytes(short[]state){
        int nb4=Nb*4;
        for(int i=0;i<nb4;i++) state[i]=sbox[state[i]];
//        
    }

    private void InvSubBytes(short[]state){
        int nb4=Nb*4;
        for(int i=0;i<nb4;i++) state[i]=invbox[state[i]];
//        
    }

    private void ShiftRow(short[]state){
        short a[]=new short[Nb];
        for(int i=1;i<4;i++)
        {
            int k=i*Nb;
            for(int j=0;j<Nb;j++) a[j]=state[k+(j+i)%Nb];
            for(int j=0;j<Nb;j++) state[k+j]=a[j];
        }
//        System.out.println("shiftrow:");printhex(state);
    }

    private void InvShiftRow(short[]state)
    {
        short a[]=new short[Nb];
        for(int i=1;i<4;i++)
        {
            int k=i*Nb;
            for(int j=0;j<Nb;j++) a[j]=state[k+(j+4-i)%Nb];
            for(int j=0;j<Nb;j++) state[k+j]=a[j];
        }
//        System.out.println("invshiftrow:");printhex(state);
    }

    private void MixColumns(short[]state){
        short a[]=new short[4];
        for(int c=0;c<Nb;c++)
        {
            for(int r=0;r<4;r++) a[r]=state[r*4+c];
            for(int r=0;r<4;r++) state[r*4+c]=(short) ( FFmul(0x02,a[r]) ^ FFmul(0x03,a[(r+1)%4]) ^ a[ (r+2)%4 ] ^ a[ (r+3)%4 ] );
        }
//        System.out.println("mixcolumn:");printhex(state);
    }

    private void InvMixColumns(short[]state){
        short a[]=new short[4];
        for(int c=0;c<Nb;c++)
        {
            for(int r=0;r<4;r++) a[r]=state[r*4+c];
            for(int r=0;r<4;r++) state[r*4+c]=(short) ( FFmul(0x0e,a[r]) ^ FFmul(0x0b,a[(r+1)%4]) ^ FFmul(0x0d,a[(r+2)%4]) ^ FFmul(0x09,a[(r+3)%4]) );
        }
 //       System.out.println("Invmixcolumn:");printhex(state);
    }

    private short FFmul(int a,short b){             
        if(b==0) return 0;         
        return alog[(log[a]+log[b])%255];
    }

    private void AddRoundKey(short[]state,int round){
        int rn=round*Nb;
        for(int c=0;c<Nb;c++)
            for(int r=0;r<4;r++) state[r*4+c]^=word[rn+c][r];
//        System.out.println("AddRoundkey:");printhex(state);
    }

    private String Hex2String(short[]codetext){
        StringBuffer s=new StringBuffer();
//        s.setLength(codetext.length*2);
        for(int i=0;i<codetext.length;i++)
        {
            s.append(hex[codetext[i]>>4]);
            s.append(hex[codetext[i]&0xf]);
        }
        return s.toString();
    }
    
    private short[] HexString2Short(String s){
        int len=s.length();
        short[] pp=new short[len/2];
        for(int i=0;i<len;i+=2)
        {
            char c=s.charAt(i);
            int sum=0;
            if(c>='0'&&c<='9') sum+=(c-'0')*16;
            else sum+=(c-'a'+10)*16;
            c=s.charAt(i+1);
            if(c>='0'&&c<='9') sum+=(c-'0');
            else sum+=(c-'a'+10);
            pp[i/2]=    (short) sum;
        }
        return pp;
    }

//    private void printhex(short[]a){
//        String s=Hex2String(a);
//         for(int i=0;i<s.length();i+=2)
//        {
//            String tw=""+s.charAt(i);
//            tw+=s.charAt(i+1);
//            tw=tw+" ";
//            System.out.print(tw);
//            if(i%8==6) System.out.println("");
//            if(i%32==30) System.out.println("");
//        }
//    }

    public void setKey(String key,boolean tag){           //key is hex or not
        if(tag==false)
        {
            byte []a=key.getBytes();
            this.key=new short[a.length];
            for(int i=0;i<a.length;i++) this.key[i]=a[i];
        }
        else
        {
            this.key=new short[key.length()/2];
            for(int i=0;i<this.key.length;i++)
            {
                char c=key.charAt(i*2);
                int sum=0;
                if(c>='0'&&c<='9') sum+=(c-'0')<<4;
                else sum+=(c-'a'+10)<<4;
                c=key.charAt(i*2+1);
                if(c>='0'&&c<='9') sum+=c-'0';
                else sum+=c-'a'+10;
                this.key[i]=  (short) sum;
            }
        }
    }

    public void setKeyLengthIndex(int index){
        INDEX=index;
        Nr=nr[INDEX];            
        Nb=nb[INDEX];            
    }
    
    public void setText(String text,boolean isHex){
        int nb4=Nb*4;
        if(isHex==false)
        {
            byte[]a=text.getBytes();
            if(a.length%nb4!=0)
            {
                this.text=new short[a.length+nb4-a.length%nb4];
                for(int i=0;i<a.length;i++) this.text[i]=(short) ((a[i] + 256)&0xff);
                for(int i=a.length;i<this.text.length;i++) this.text[i]=0;
            }
            else
            {
                this.text=new short[a.length];
                for(int i=0;i<a.length;i++) this.text[i]=a[i];
            }
        }
        else
        {
            short[]a=HexString2Short(text);
            if(a.length%nb4!=0)
            {
                this.text=new short[a.length+nb4-a.length%nb4];
                for(int i=0;i<a.length;i++) this.text[i]=a[i];
                for(int i=a.length;i<this.text.length;i++) this.text[i]=0;
            }
            else
            {
                this.text=new short[a.length];
                for(int i=0;i<a.length;i++) this.text[i]=a[i];
            }
        }
    }

    public void setText(byte[]text){
        int nb4=Nb*4;
        if(text.length%nb4!=0)
        {
            this.text=new short[text.length+nb4-text.length%nb4];
            for(int i=0;i<text.length;i++) this.text[i]=(short) ((text[i] + 256) & 0xff);
            for(int i=text.length;i<this.text.length;i++) this.text[i]=0;
        }
        else 
        {
            this.text=new short[text.length];
            for(int i=0;i<text.length;i++) this.text[i]=text[i];
        }
    }

    public void setText(short[]text){
        int nb4=Nb*4;
        if(text.length%nb4!=0)
        {
            this.text=new short[text.length+nb4-text.length%nb4];
            for(int i=0;i<text.length;i++) this.text[i]=text[i];
            for(int i=text.length;i<this.text.length;i++) this.text[i]=0;
        }
        else 
        {
            this.text=new short[text.length];
            for(int i=0;i<text.length;i++) this.text[i]=text[i];
        }

//        printhex(this.text);
    }

    public String getWordHex(){
        StringBuffer s=new StringBuffer();
        for(int i=0;i<word.length;i++)
        {
            for(int j=0;j<4;j++)
            {
                s.append(hex[word[i][j]>>4]);
                s.append(hex[word[i][j]&0xf]);
            }
        }
        return s.toString();
    }

    public String getcText(String ctext){
        String s=InvCipher(ctext);
        short[]a=HexString2Short(s);
        byte[]b=new byte[a.length];
        for(int i=0;i<a.length;i++) b[i]=(byte) a[i];
        return new String(b);
    }    

    public static void main(String args[]){
    	AES aes = new AES();
    	//String text ="931E45B30B8A26086C708ED3AB2972ED";
    	String text = Encode.getRandom(32);
    	String key = "00000000000000000000000000000000";
    	aes.setText(text.toLowerCase(), true);
    	aes.setKeyLengthIndex(0);
        aes.setKey(key,true);
        
        System.out.println(aes.Cipher().toUpperCase());
    }
}

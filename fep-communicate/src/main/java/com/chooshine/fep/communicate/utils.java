package com.chooshine.fep.communicate;

import java.nio.*;
import java.util.Calendar;
import java.text.SimpleDateFormat;

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
public class utils {

  public utils() {
  }

  public static String bytebuf2str(ByteBuffer bb) {
    bb.rewind();
    int i_len = bb.remaining();
    byte[] bbs = new byte[i_len];
    bb.get(bbs, 0, i_len);
    return bytes2str(bbs);
  }

  public static byte[] str2bytes(String s) {
    int i_len = s.length();
    if (i_len % 2 != 0) {
      i_len = i_len + 1;
      s = "0" + s;
    }
    char[] cc = s.toCharArray();
    byte[] bb = new byte[i_len / 2];
    int j = 0;

    for (int i = 0; i < i_len; i = i + 2) {
      bb[j] = (byte) ( (Character.digit(cc[i], 16) << 4) +
                      Character.digit(cc[i + 1], 16));
      j++;
    }

    return bb;
  }

  public static String bytes2str(byte[] by,int len) {
    byte[] tmp = new byte[4];
    String str = "";
    for (int i = 0; i < len; i++) {
      tmp[0] = by[i];
      if ( (i % 10 == 0) && (i > 0)) {
        str = str + bytetostr(tmp).substring(4, 6);
      }
      else {
        str = str + bytetostr(tmp).substring(4, 6);
      }

    }
    return str;
  }

  public static String bytes2str(byte[] by) {
    int len = by.length;
    byte[] tmp = new byte[4];
    String str = "";
    for (int i = 0; i < len; i++) {
      tmp[0] = by[i];
      if ( (i % 10 == 0) && (i > 0)) {
        str = str + bytetostr(tmp).substring(4, 6);
      }
      else {
        str = str + bytetostr(tmp).substring(4, 6);
      }

    }
    return str;
  }

  public static String bytetostr(byte[] bt) {
    int i = byte2int(bt, 0);
    String str = short2str(i, 16);
    return str;
  }

  public static int byte2int(byte[] bt, int offset) {
    int st = 0;
    int tmp;
    int mod = 0xff;
    for (int i = offset; i < offset + 4; i++) {
      tmp = bt[i];
      st = st + ( (tmp & mod) << (8 * (i - offset)));
    }
    return st;
  }

  public static String short2str(int st, int DoH) {
    String rstr = "";
    int rint = st;
    if (DoH == 10) {
      rstr = String.valueOf(st);
    }
    if (DoH == 16) {
      for (int i = 0; i < 4; i++) {
        if (rint == 0) {
          rstr = "0" + rstr;
        }
        else if (rint < 16) {
          rstr = sixteen2ten(rint) + rstr;
          rint = 0;
        }
        else {
          rstr = sixteen2ten(rint % 16) + rstr;
          rint = (rint - (rint % 16)) / 16;
        }
      }
      rstr = "0x" + rstr;
    }
    return rstr;
  }

  public static int ten2sixteen(String s) {
    if (s.toUpperCase().equals("0A")) {
      return 10;
    }
    else if (s.toUpperCase().equals("0B")) {
      return 11;
    }
    else if (s.toUpperCase().equals("0C")) {
      return 12;
    }
    else if (s.toUpperCase().equals("0D")) {
      return 13;
    }
    else if (s.toUpperCase().equals("0E")) {
      return 14;
    }
    else if (s.toUpperCase().equals("0F")) {
      return 15;
    }
    else {
      return Integer.parseInt(s);
    }
  }

  public static int HexStrToInt(String s) {
    return Integer.parseInt(s, 16);
  }

  public static byte[] int2byte(int st) {
    byte[] bt = new byte[4];
    int tmp = st;
    int mod = 0xff;
    for (int i = 3; i > 0; i--, tmp >>= 8) {
      bt[i] = (byte) (tmp & mod);
    }
    return bt;
  }

  public static String sixteen2ten(int st) {
    String Rs = "";
    switch (st) {
      case 10:
        Rs = "A";
        break;
      case 11:
        Rs = "B";
        break;
      case 12:
        Rs = "C";
        break;
      case 13:
        Rs = "D";
        break;
      case 14:
        Rs = "E";
        break;
      case 15:
        Rs = "F";
        break;
      default:
        Rs = String.valueOf(st);
    }
    return Rs;
  }

  public static void PrintDebugMessage(String Msg, String DebugFlag) {
    if (DebugFlag.indexOf("D") != -1) {
      Calendar c = Calendar.getInstance();
      SimpleDateFormat formatter = new SimpleDateFormat(
          "yyyy-MM-dd HH:mm:ss");
      System.out.println(formatter.format(c.getTime()) + " " + Msg);
    }
  }
}

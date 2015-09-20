package com.chooshine.fep.fepex.common;

import java.sql.*;

public class DataAccess {
    private int FDataBaseType = 10; //默认为Oracle数据库
    private String FConnectURL = "";
    private String FUserName = "";
    private String FPassWord = "";

    private int gFunctionType = 0; //应用类型
    //Oracle数据库参数
    private Connection conn;
    private Statement stmt = null;
    private ResultSet rs = null;
    public DataAccess() {
    }

    //数据库参数
    public DataAccess(int DataBaseType, String ConnectionURL, String UseName,
                      String PassWord) {
        FDataBaseType = DataBaseType;
        FConnectURL = ConnectionURL;
        FUserName = UseName;
        FPassWord = PassWord;
        try {
            jbInit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //获取自动编码/ID
    public String GetAutoID(String SequenceName) {
        String sID = new String("0");
        if (FDataBaseType == 10) { //Oracle  数据库
            sID = GetSequence(SequenceName);
        } else if (FDataBaseType == 20) { //Sybase  数据库
            //可以采用存储过程方式调用
        }
        return sID;
    }

//Oracle取各类流水号
    private String GetSequence(String SequenceName) {
        String sID = new String("0");
        ResultSet rset = null;
        try {
            Statement st_YCSJLSH = conn.createStatement();
            rset = st_YCSJLSH.executeQuery("select " + SequenceName +
                                           ".nextval from dual");
            if (rset.next()) {
                sID = rset.getString(1);
            }
            st_YCSJLSH.close();
        } catch (SQLException ex) {
            ReConnect();
        } finally {
            try {
                rset.close();
            } catch (SQLException ex1) {
            }
        }
        return sID;
    }

    //建立数据库持久连接
    //FunctionType:
    /*
     0:只建立链接,不与具体操作绑定
     */
    public boolean LogIn(int FunctionType) {
        gFunctionType = FunctionType;
        //oracle 数据库
        if (FDataBaseType == 10) { //Oracle  数据库
            //数据库连接
            try {
                Class.forName("oracle.jdbc.driver.OracleDriver");
            } catch (ClassNotFoundException ex) {
            }
            try {
                conn = DriverManager.getConnection(FConnectURL, FUserName,
                        FPassWord);
            } catch (SQLException ex1) {
                return false;
            }
            return true; //没有绑定操作只连接数据库
        } else if (FDataBaseType == 20) { //Sybase 数据库
            return false;
        }
        return false;
    }

//断开数据库链接
    public boolean LogOut(int FunctionType) {
        //Oracle  数据库
        if (FDataBaseType == 10) {
            try {
                conn.close();
                return true;
            } catch (SQLException ex2) {
            }
        }
        //Sybase 数据库
        else if (FDataBaseType == 20) {
            return false;
        }
        return false;
    }

    //数据库断开重连
    private boolean ReConnect() {
        //以后此函数可以再做扩展,当数据库断开后并不每次都重连,可以控制重连的频度,以减少开销
        try {
            return LogIn(gFunctionType);
        } catch (Exception ex) {
            return false;
        }

    }

//查询功能
    public ResultSet executeQuery(String sql) throws Exception {
        try {
            stmt = conn.createStatement();
            sql = new String(sql.getBytes("GBK"), "ISO8859_1");
            rs = stmt.executeQuery(sql);
        } catch (Exception ex) {
            System.out.println("sql.executeQuery:" + ex.getMessage());
        }
        return rs;
    }

    public void close() {
        try {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
        } catch (SQLException ex1) {
        }
    }

    public int executeUpdate(String sql) throws Exception {
        int iResult = -1;
        Statement stmt = conn.createStatement();
        try {
            sql = new String(sql.getBytes("GBK"), "ISO8859_1");
            iResult = stmt.executeUpdate(sql);
            conn.commit();
        } catch (SQLException ex) {
            System.out.println("sql.executeUpdate:" + ex.getMessage());
        } finally {
            try {
                stmt.close();
            } catch (Exception e) {}
        }
        return iResult;
    }
    public boolean SaveBalanceToDB(String sZDLJDZ,String sDBDZ,String sBalance,String sYEZT){    	
    	try {    		
    		String sSQL = "UPDATE RW_YEXX SET YE='" + sBalance +
	            "',YEZT='" + sYEZT +"' WHERE DBDZ='" + sDBDZ + "' AND ZDLJDZ='" + sZDLJDZ +"'";
	        try {
	          if (executeUpdate(sSQL) == 0) { //update 不成功，说明数据库没有初始记录，需要插入先
	        	  sSQL = "INSERT INTO RW_YEXX(ZDLJDZ,DBDZ,YE,YEZT) VALUES('" +
	        	  		sZDLJDZ + "','" + sDBDZ + "','" + sBalance + "','"+ sYEZT +"')";
	        	  executeUpdate(sSQL);
	          }
	        }
	        catch (Exception ex) {
	        }
    	    return true;
    	}
	    catch (Exception ex) {
	      //DataAccessLog.WriteLog("StorHistoryDataByOracle出错:" + ex.toString());	
	      return false;
	    }
    }
    private void jbInit() throws Exception {
    }
}

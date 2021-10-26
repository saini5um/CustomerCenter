/*
 * NRICValidation.java
 *
 * Created on October 18, 2005, 4:26 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.maxis.util;

import java.util.Calendar;
import java.sql.*;
import com.csgsystems.fx.security.remote.RemoteDBConnection;

import java.util.GregorianCalendar;
import java.text.SimpleDateFormat;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.HashMap;
import com.maxis.util.*;
import com.csgsystems.fx.security.remote.SQL;
import com.csgsystems.fx.security.util.FxException;

/**
 *
 * @author Pankaj Saini
 */
public class NRICValidation {
        
    /** Creates a new instance of NRICValidation */
    public NRICValidation() {
    }
    
  public static boolean oldNricValidation(String oldNric) {
    if(oldNric.indexOf(" ")>=0) {
      return false;
    }
    return true;
  }

  public static boolean checkNum(String str) {
      boolean is_numeric = true;
      for ( int i = 0; i < str.length(); i++ )
          if( !Character.isDigit(str.charAt(i)) ) is_numeric = false;
      
      return is_numeric;
  }
  
  public static ArrayList newNricValidation(String nric) {
    HashMap map = new HashMap();
    nric = nric.trim();
    if(nric.length()==12) {
      if(nric.indexOf(" ")<0) {
        if( !checkNum(nric) ) {
            map.put("All Characters for new NRIC must be numeric", new String());
        } else {
            if(!checkCal(nric.substring(2,4), Calendar.MONTH)) {
              map.put("Characters 3 and 4 should represent Month", new String());
            }
            if(!checkCal(nric.substring(4,6), Calendar.DATE)) {
              map.put("Characters 5 and 6 should represent Day", new String());
            }
            if(!isValidDate(nric.substring(0, 8))) {
              map.put("Date is not valid", new String());
            }
            if(!isValidState(nric.substring(6,8))) {
              map.put("Characters 7 and 8 for New NRIC are incorrect", new String());
            }
        }
      } else {
        map.put("No spaces are allowed in new NRIC", new String());
      }
    } else {
      map.put("New NRIC length should be 12 characters in length", new String());
    }
    String[] collTemp = (String[])map.keySet().toArray(new String[map.size()]);
    ArrayList list = new ArrayList();
    int errLength = collTemp.length;
    for (int i = 0;i< errLength;i++) {
      list.add(collTemp[i]);
    }
    return list;
  }
  
  private static boolean isValidDate(String date) { // format yymmdd
      boolean valid = true;
      SimpleDateFormat icDateFormat = new SimpleDateFormat("yyMMdd");
      ParsePosition pos = new ParsePosition(0);
      icDateFormat.setCalendar(new GregorianCalendar());
      icDateFormat.setLenient(false);
      java.util.Date d = null;
      
      try {
          d = icDateFormat.parse(date.substring(0, 6), pos);
      } catch (Exception e) {
          e.printStackTrace();
          valid = false;
      }
      if ( d == null || pos.getErrorIndex() != -1 ) valid = false;
      
      return valid;
  }
  
  private static boolean checkCal(String dayMonth, int dayOrMonth) {
    if(dayOrMonth == Calendar.DATE) {
      //means date
      int dayOfMonth = Integer.parseInt(dayMonth);
      if(dayOfMonth>0&&dayOfMonth<=31) {
        return true;
      }
    }else if(dayOrMonth == Calendar.MONTH) {
      int month = Integer.parseInt(dayMonth);
      if(month>0&&month<=12) {
        return true;
      }
    }
    return false;
  }
     ///////////////////////////////////////////////////////////////////////////
     //Method to validate the state field in Database table ////////////////
     // If the database connection is not instantiated then it would create   //
     // an instance for the connection and send to SQL to execute. If the     //
     // connection has already established then it will just pass the sql cmd //
     ///////////////////////////////////////////////////////////////////////////
  public static boolean isValidState(String stateNo){
    boolean returnVal = false;
    int isRegistered = 0;
    RemoteDBConnection dbConn = tuxJDBCManager.getInstance().getCurrentConnection("ADMIN");
    
    if (dbConn == null) {
      //System.out.println("No connection to database");
    }
    else{
      try{
        //String userId = com.csgsystems.domain.framework.security.SecurityManager.getInstance().getLoggedInUserId();
        CallableStatement cstmt = dbConn.prepareCall(new SQL("get_mxs_state","proc"));
        cstmt.setString(1, stateNo);
        //System.out.println("*** Sending in = "+stateNo);
        cstmt.registerOutParameter(2, Types.NUMERIC);
        
        ((com.csgsystems.fx.security.remote.SQLParameter)((com.csgsystems.fx.security.remote.impl.CallableStatementImpl)cstmt).getParms().get(1)).registerAsOutParameter();
        ((com.csgsystems.fx.security.remote.SQLParameter)((com.csgsystems.fx.security.remote.impl.CallableStatementImpl)cstmt).getParms().get(1)).setNullParm(false);
        ((com.csgsystems.fx.security.remote.SQLParameter)((com.csgsystems.fx.security.remote.impl.CallableStatementImpl)cstmt).getParms().get(1)).setValue("eeee");
        
        int result = cstmt.executeUpdate();
        isRegistered = cstmt.getInt(2);
        if(isRegistered>0) {
          returnVal = true;
        }
      }
      catch (java.sql.SQLException ex) {
        ex.printStackTrace();
      }
      catch (FxException fx) {
        fx.printStackTrace();
      }
      catch (Exception fx) {
        fx.printStackTrace();
      }
    }
    return returnVal;
  }

}

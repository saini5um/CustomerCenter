/*
 * SqlDate.java
 *
 * Created on June 13, 2005, 12:20 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.maxis.util;

import java.util.*;

/**
 *
 * @author Pankaj Saini
 */
public class SqlDate {
    
    /** Creates a new instance of SqlDate */
    public SqlDate() {
    }
    
    public static java.sql.Date Today() {
        java.util.Date t = new java.util.Date();
        java.sql.Date today = new java.sql.Date(t.getTime());
        return today;
    }

    public static java.sql.Timestamp Now() {
        java.util.Date t = new java.util.Date();
        java.sql.Timestamp now = new java.sql.Timestamp(t.getTime());
        return now;
    }
    
    public static String Format(Date d) {
        Calendar c = GregorianCalendar.getInstance();
        c.setTime(d);
        
        String month = Integer.toString(c.get(Calendar.MONTH)+1);
        String day = Integer.toString(c.get(Calendar.DATE));
        
        if (month.length() == 1)
            month = "0" + month;
        
        if (day.length() == 1)
            day = "0" + day;
        
        return day + "-" + month + "-" + c.get(Calendar.YEAR);
    }
}

/**
 *  Project Name: Maxis R&B
 *  Developer Name: Ming Hon
 *  Module Name: Customer Center - Integrator
 *  Date Created: 20050525
 *  Description:
 *  Date Modified: 20050525
 *  Version #: v01
 *
 *
 */
package com.maxis.util;

import com.csgsystems.bopt.*;
import com.csgsystems.domain.framework.businessobject.IPersistentObject;
import com.csgsystems.domain.framework.attribute.*;

/**
 *
 *
 * @author Ming Hon
 */
public class CommonUtil{
  
    public static int getCurrentServerId(){
        try{
            BoptSet bopt = null;
            int nRowIndex = -1;
            try {
                bopt = BoptFactory.getBoptSet("Server");
            }
            catch (BoptException ex) {
                ex.printStackTrace();
            }
            nRowIndex = bopt.getRowIndex("ServerType", "3");
            String temp = bopt.getDataValue(nRowIndex, "ServerId");
            int as = new Integer(temp).intValue();
            return as;
        } catch(Exception ex){
            ex.printStackTrace();
            return -1;
        }
    }
  
    public static void objectToString(IPersistentObject tObj) {
        if(tObj!=null){
            java.util.Collection col = tObj.getAttributes();
            Object o[] = col.toArray();
            System.out.println("*** objectToString ("+tObj.getObjectDescription()+")");
            int spaces = getSpaces(o);
            for(int i=0; i<o.length; i++){
                Attribute a = (Attribute)o[i];
                if(a instanceof IntegerEnumeratedAttribute) {
                    System.out.println(i+") Field=["+a.getName()+"]"+
                    getPlaceholder(spaces,a.getName())+"Value=["+a.getDataAsInteger()+"]");
                }
                System.out.println(i+") Field=["+a.getName()+"]"+
                getPlaceholder(spaces,a.getName())+"Value=["+a.getDataAsString()+"]");
            }
            System.out.println("*********************************");
        }
    }
  
    private static String getPlaceholder(int spaces, String vals) {
        if(spaces<=0) {
            spaces = 30;
        }
        StringBuffer sb = null;
            if(spaces>vals.length()) {
                int sbLen = spaces-vals.length();
                sb = new StringBuffer();
                for (int i = 0;i<= sbLen;i++) {
                    sb.append(" ");
                }
            }
        if(sb==null) {
            return null;
        }
        return sb.toString();
    }
  
    private static int getSpaces(Object[] o) {
        int len = 0;
        int i=0;
        for (int ix = 0;ix< o.length;ix++) {
            Attribute a = (Attribute)o[ix];
            if(a.getName().toString().length()>len){
                len = a.getName().toString().length()+2;
            }
        }
        return len;
    }
  
    //Added by Joell - Check if value contains a number
    public static boolean hasNumber( String s ) {
        for (int j = 0; j < s.length(); j++) {
            if ( ! Character.isDigit( s.charAt(j) ) ) {
                return false;
            }
        }

        return true;
    }
    
    public static String encodeAsXML(String str) {
        String saXMLEquivalent[] = {"&amp;", "&apos;", "&quot;", "&lt;", "&gt;"};
        String saSpecialChars[] = {"&", "\'", "\"", "<", ">"};
        String sFind;
        String sReplace;
        boolean bFound;
        int iPos = -1;
        int i = 0;

        while (i < saXMLEquivalent.length) {
            String newStr = "";

            //Search for special chars in ASCII and replace with XML safe chars
            sFind = saSpecialChars[i];
            sReplace = saXMLEquivalent[i];
            do {
                iPos = str.indexOf(sFind, ++iPos);
                if (iPos > -1) {
                    newStr = newStr + str.substring(0, iPos) + sReplace + str.substring(iPos+sFind.length(),str.length());
                    str = newStr;
                    newStr = "";
                    bFound = true;
                } else {
                    bFound = false;
                }
            } while ( bFound );
            i++;
        }
        return(str);
    }  
    
    //Added by Joell - Check if value is a number
    public boolean isNumber( String test_string )
	{
	  try
	  {
	    Integer.parseInt(test_string);
	    return true;
	  }
	  catch ( Exception e )
	  {
	    return false;
	  }
	}
}

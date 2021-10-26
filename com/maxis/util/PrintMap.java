package com.maxis.util;
import java.util.*;
import java.io.*;
import com.csgsystems.aruba.connection.*;
import com.csgsystems.aruba.connection.ServiceException;

public class PrintMap{
    static PrintStream out = System.out;
    public static boolean printObjId = false;
    
    //===================================================
    // Utility method for printing HashMaps
    //===================================================
    public static void printHashMap(Map map) {
        out.println(toMapString(map, 2));
        if(true) return;
        /*try {
            // Create a BufferedWriter
            BufferedWriter bw = new BufferedWriter(new PrintWriter(out));
            // Pass the map to the ServiceException object's print() method
            ServiceException.printMap( bw, map, 2 );
            
            // Flush the BufferedWriter
            bw.flush();
        }
        // Handle input/output exceptions
        catch (IOException ioe) {
            ioe.printStackTrace();
        }*/
    }
    
    
    public static boolean EnableMapString = true;
    
    /**
     * Format the contents of a hash map into a string
     * @param the hash map to be formated to a string
     * @param the indention of the string to be formated. normally a value of 2 is used
     */
    public static String toMapString(Map map, int indent){
        if(!EnableMapString) return null;
        StringBuffer str = new StringBuffer();
        if(map == null)
            return null;
        java.util.Map.Entry entry;
        for(Iterator iter = map.entrySet().iterator(); iter.hasNext(); ) {
            entry = (java.util.Map.Entry)iter.next();
            for(int i = 0; i < indent; i++)
                str.append((char)32);
            
            if( printObjId ) str.append( entry.getValue().getClass().getName() + '@' + Integer.toHexString(entry.getValue().hashCode()));
            str.append( entry.getKey());
            str.append(" = ");
            str.append( printValue(entry.getValue(), indent));
            
        }
        return str.toString();
    }
    
    /**
     * Print the value of the hash map based on the object type that is contain within the hashmap
     */
    private static String printValue(Object val, int indent){
        StringBuffer str = new StringBuffer();
        if(val == null)
            return "null";
        else if(val instanceof Map){
            str.append((char)123);
            str.append("\r\n");
            str.append(toMapString((Map)val, indent + 2));
            for(int i = 0; i < indent; i++)
                str.append((char)32);
            str.append((char)125);
        }
        else if(val.getClass().isArray() && !(val instanceof byte[])) {
            str.append((char)91);
            str.append("\r\n");
            Object objs[] = (Object[])val;
            for(int i = 0; i < objs.length; i++)
                str.append(printValue(objs[i], indent + 2));
            
            str.append((char)93);
        }
        else if(val instanceof List){
            str.append((char)91);
            str.append("\r\n");
            for(Iterator iter = ((List)val).iterator(); iter.hasNext(); ){
                str.append(printValue(iter.next(), indent + 2));
            }
            str.append((char)93);
        }
        else{
            str.append("'" + val + "'");
        }
        str.append("\r\n");
        return str.toString();
    }
}
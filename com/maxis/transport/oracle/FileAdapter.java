/*
 * FileAdapter.java
 *
 * Created on November 13, 2005, 3:43 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.maxis.transport.oracle;

import com.csgsystems.marshal.python.EncodeArguments;
import java.io.*;

/**
 *
 * @author Pankaj Saini
 */
public class FileAdapter {
    String serviceName = null;
    String path = "C:\\Maxis\\Tuxedo\\Response\\FileAdapter\\";
    
    /** Creates a new instance of FileAdapter */
    public FileAdapter(String service) {
        serviceName = service;
    }
    
    public byte[] getResponse() {
        System.out.println("file adapter get response called...");
        byte resp[] = null; //= new byte[100000];
        String fileName = serviceName + ".Encoded";
        try {
            File file = new File(path + fileName);
            FileInputStream is = new FileInputStream(file);
            DataInputStream ds = new DataInputStream(is);
            BufferedReader br = new BufferedReader(new InputStreamReader(ds));
            
            int len = (int)file.length();
            resp = new byte[len];
            ds.readFully(resp);
//            ds.readFully(resp);
            ds.close();
            is.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return resp;
    }
    
}

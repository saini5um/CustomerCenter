/**
 * AccountValidator.java
 *
 * Based on a sample class of the same name written by Dave Sieh.
 *
 * Copyright (C) 2004 CSG Systems
 * Created 2004/05/14
 */
package com.maxis.xvalidation;

import com.csgsystems.domain.framework.IFrameworkObject;
import com.csgsystems.domain.framework.attribute.IAttribute;
import com.csgsystems.domain.framework.businessobject.IPersistentObject;
import com.csgsystems.validator.ValidatorBase;
import java.util.StringTokenizer;
import java.io.*;
import java.lang.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.util.StringTokenizer;
import javax.swing.*;

/**
 * This is a sample implementation of the IValidator interface,
 * which lets you validate objects and their attributes
 * before they are changed or stored. This class can validate
 * both application domain objects (such as Accounts) and
 * attributes (such as First Name).
 *
 * @author Alan Alyn (<a href="mailto:Alan_alyn@csgsystems.com">
 * Alan_alyn@csgsystems.com</a>)
 */
public class AccountValidator extends ValidatorBase {
    
    /**
     * Required default, no-argument constructor
     */
    public AccountValidator() {
        super();
    }
    
    /**
     * Override this method to provide validation logic for changes
     * to an attribute. The attribute (IAttribute) is passed in as
     * the framework object, and the proposed new value for the
     * attribute is passed in as the clientData parameter.
     *
     * If true is returned, the attribute change is allowed.
     * If false is returned, the attribute change is vetoed.
     *
     * @param fwkObject The object whose value is being validated.
     * @param clientData The value to be validated.
     */
    public boolean isValid(IFrameworkObject fwkObject, Object clientData) {
        //System.out.println("In isValid, 2-argument version.");
        
        boolean isValid = true;
        
      /*
       * If fwkObject is not an attribute, pass it to
       * the parent class for validation and return the result.
       */
        if (!(fwkObject instanceof IAttribute)) {
            return super.isValid(fwkObject, clientData);
        }
        
      /*
       * fwkObject is an attribute, so cast it to IAttribute
       * and get its name. If the name is "BillFname," it's the
       * FirstName on some object (presumably an Account) and
       * should consequently be validated.
       */
        IAttribute attr = (IAttribute) fwkObject;
        String attrName = attr.getName();
        
        //if (attrName.equals("FavoriteColor")) {
        //   System.out.println("In isValid(), attribute " + attrName + " = " + attr.getDataAsString());
        //}
        
        //adding validation for the new attribute "Callercountry"
        if (attrName.equals("Callercountry")) {
            String country = (String) clientData;
            //checkCountry(country);
        }
        
        if (attrName.equals("BillFname")) {
            
            //Get the value to be tested (clientData).
            String firstName = (String) clientData;
            System.out.println("First name submitted: " + firstName);
            
            //If the value to be tested matches "Alan", the change
            // is invalid, so set isValid to false.
            if (firstName.equals("Alan")) {
                isValid = false;
            }
            
         /*
          * If the change is invalid, setError on the attribute
          * to indicate this fact. Parameters are as follows:
          *
          * 1) String containing error key (should this have a
          *    corresponding entry in the resource bundle?).
          *
          * 2) Array of objects to use as insertables for the
          *    error messages (can be null).
          */
            if (!isValid) {
                attr.setError("We don't allow customers named Alan.", new Object[0]);
                System.out.println("We don't allow customers named Alan.");
            }
            
            
        } else{
            
      /*
       * This is some other attribute; pass it to the parent
       * class for validation.
       */
            isValid = super.isValid(fwkObject, clientData);
        }
        
        return isValid;
    }
    
   /*
   //Adding new method
   boolean checkCountry(String countryName)
   {
         DataInputStream dis = null;
         String dbRecord = null;
         int flag = 1;
    
         try {
            File f = new File("customer.txt");
            FileInputStream fis = new FileInputStream(f);
    
            BufferedInputStream bis = new BufferedInputStream(fis);
            dis = new DataInputStream(bis);
    
            String countryname = null;
            String countrycode = null;
    
            // read the records from the text file
            while ( (dbRecord = dis.readLine()) != null) {
               StringTokenizer st = new StringTokenizer(dbRecord, ":");
               countryname = st. nextToken();
               countrycode = st.nextToken();
    
               if (countrycode.compareTo(countryName)== 0) {
                  flag = 0;
                  break;
               }
            } //end while
            if (flag == 0)
               JOptionPane.showMessageDialog(null,"This customer's frequent-calling-country-code is: " + countrycode + ".","Country Code Check Results",JOptionPane.ERROR_MESSAGE );
            else
               JOptionPane.showMessageDialog(null,"This customer's frequent-calling-country-code does not exist.","Country Code Check Results", JOptionPane.ERROR_MESSAGE );
    
         } //end try
         catch (IOException e) {
            // catch io errors from FileInputStream or readLine()
            System.out.println("msg1, got an IOException error tring to read this file:");
    
         } finally {
            // if the file opened okay, make sure we close it
            if (dis != null) {
               try {
                  dis.close();
               } catch (IOException ioe) {
                  System.out.println("msg2, go an IOException error trying to close the file: " );
               }
            } // end if
    
         } // end finally
         if(flag==0) return true;
         else return false;
   }
    */
    
    /**
     * Override this method to provide additional validation
     * logic for the application domain or an attribute on the
     * application domain.
     */
    public boolean isValid(IFrameworkObject fwkObject) {
        
        boolean isValid = true;
        
        // Verify that this code is being executed
        //System.out.println("In isValid, single argument version.");
        
      /*
       * If fwkObject is an attribute, evaluate it the same
       * way we do above.
       */
        if (fwkObject instanceof IAttribute) {
            IAttribute attr = (IAttribute) fwkObject;
            String attrName = attr.getName();
            if (attrName.equals("BillFname")) {
                String firstName = attr.getDataAsString();
                System.out.println("First name submitted: " + firstName);
                if (firstName.equals("Alan")) {
                    isValid = false;
                }
                if (!isValid) {
                    attr.setError("We don't allow customers named Alan.", new Object[0]);
                    System.out.println("We don't allow customers named Alan.");
                }
            } else {
                isValid = super.isValid(fwkObject);
            }
            
      /*
       * If fwkObject is a domain object (IPersistentObject),
       * you can evaluate it here. From this object, you have access
       * to any of its descendents.
       *
       * HOW DO YOU GET RELATED OBJECTS
       * FROM THE DATABASE IF THEY ARE NOT DESCENDENTS?
       */
        } else if (fwkObject instanceof IPersistentObject) {
            IPersistentObject obj = (IPersistentObject) fwkObject;
            
         /*
          * You could include some validation code here that looked
          * at fwkObject and its attributes and descendents, and
          * set isValid = false if there's something wrong.
          */
            
            if (!isValid) {
                obj.setError("Domain object invalid.", new Object[0]);
                System.out.println("Domain object invalid.");
            }
        } else {
         /*
          * fwkObject is neither a domain object nor an attribute;
          * pass it to the parent class for validation.
          */
            isValid = super.isValid(fwkObject);
        }
        
        return isValid;
    }
    
}
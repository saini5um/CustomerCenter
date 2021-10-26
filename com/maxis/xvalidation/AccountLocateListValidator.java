/**
 * AccountValidator.java
 *
 * Based on a sample class of the same name written by Dave Sieh.
 *
 * Copyright (C) 2004 CSG Systems
 * Created 2004/05/14
 * Date         By          PN#                      Resolution          
 * 20050824     kinyip      KenanFX–CC–General-91    Add in checking for account type selection. 
                                                     Value will only be defaulted if there is no value for the account type drop down.
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
public class AccountLocateListValidator extends ValidatorBase {
  
  /**
   * Required default, no-argument constructor
   */
  public AccountLocateListValidator() {
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
    boolean isValid = true;
    //System.out.println("isValid(IFrameworkObject fwkObject, Object clientData)");
    checkFields((IAttribute)fwkObject);
    return isValid;
  }
  
  
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
      if (!attrName.equals("AccountExternalIdType")
      && !attrName.equals("ServiceExternalIdType")) {
        String attrVal = attr.getDataAsString();
        if (attrVal != null && attrVal.trim().matches("^%+")) {
          attr.setDataAsString(null);
        }
      }
      
      if (!isValid) {
        //attr.setError("Invalid New NRIC", new Object[0]);
        //System.out.println("**** hhaahahahahahah *** ");
      } else {
        isValid = super.isValid(fwkObject);
      }
      
      /*
       * If fwkObject is a domain object (IPersistentObject),
       * you can evalua te it here. From this object, you have access
       * to any of its descendents.
       *
       * HOW DO YOU GET RELATED OBJECTS
       * FROM THE DATABASE IF THEY ARE NOT DESCENDENTS?
       */
    } else if (fwkObject instanceof IPersistentObject) {
      IPersistentObject obj = (IPersistentObject) fwkObject;
      //System.out.println("AccountLocateListValidator: isIPersistentObject");
         /*
          * You could include some validation code here that looked
          * at fwkObject and its attributes and descendents, and
          * set isValid = false if there's something wrong.
          */
      
      if (!isValid) {
        obj.setError("Domain object invalid.", new Object[0]);
        //System.out.println("Domain object invalid.");
      }
    } else {
         /*
          * fwkObject is neither a domain object nor an attribute;
          * pass it to the parent class for validation.
          */
      isValid = super.isValid(fwkObject);
    }
    //System.out.println("isValid(IFrameworkObject fwkObject)");
    checkFields((IAttribute)fwkObject);
    return isValid;
  }
  
  private void checkFields(IAttribute attr) {
    // 20050727 LKY: FIX for KenanFX–CC–General-5, Data Migration-5, Data Migration-16
    //System.out.println("checkFields called for attribute " + attr.getName());
    //System.out.println("attribute value = " + attr.getDataAsString());
    //System.out.println("AccountExternalIdType="+attr.getParent().getAttribute("AccountExternalIdType"));
    //System.out.println("AccountExternalId="+attr.getParent().getAttribute("AccountExternalId"));
    //System.out.println("-------------------------------------------------------");
    if(attr.getName().equals("AccountExternalId") && attr.getDataAsString() == null){
      // 20050824 LKY: FIX for KenanFX–CC–General-91
      //attr.getParent().getAttribute("AccountExternalIdType").setDataAsString("1");
      if(attr.getParent().getAttribute("AccountExternalIdType").getDataAsString() == null ||
         attr.getParent().getAttribute("ServiceExternalIdType").getDataAsString() != null){
        attr.getParent().getAttribute("AccountExternalIdType").setDataAsString("1");
        //System.out.println("ext id type changed to 1 as external id was null");
      }
    }
    else if(attr.getName().equals("AccountExternalId") && attr.getDataAsString() != null){
      if(attr.getParent().getAttribute("AccountExternalIdType").getDataAsString() == null){
        attr.getParent().getAttribute("AccountExternalIdType").setDataAsString("1");
        //System.out.println("ext id type changed to 1 as external id was not null");
      }
    }
  }
}


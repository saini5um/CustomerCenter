/**
 * MyContextValidator.java
 *
 * Based on a sample class of the same name written by Dave Sieh.
 *
 * Copyright (C) 2004 CSG Systems
 * Created 2004/05/14
 */
package com.maxis.xvalidation;

import com.csgsystems.domain.framework.IFrameworkObject;
import com.csgsystems.domain.framework.context.IContext;
import com.csgsystems.validator.ValidatorBase;
import com.csgsystems.domain.framework.businessobject.IPersistentObject;

import java.text.DateFormat;
import java.util.Date;

/**
 * This is a sample implementation of the IValidator
 * interface. It extends the ValidatorBase class, which
 * provides stub implementations for its methods.
 * Its purpose is to verify the validity of a context.
 * Since a context doesn't have attributes, only
 * the framework object validation is provided.
 *
 * @author Trevor Alyn (<a href="mailto:trevor_alyn@csgsystems.com">
 * trevor_alyn@csgsystems.com</a>)
 */
public class MyContextValidator extends ValidatorBase {
   
   /**
    * Required default no-argument constructor.
    */
   public MyContextValidator() {
      super();
   }
   
    private void setBirthDate(IPersistentObject acct, String icNumber) {
        DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);
        Date birthDay = null;
        try {
            birthDay = df.parse(icNumber.substring(4, 6) + "/" + 
                    icNumber.substring(2, 4) + "/" + icNumber.substring(0, 2));            
            acct.setAttributeDataAsDate("Birth Date_3", birthDay);
        } catch (Exception e) {
            System.out.println("parse err:" + e.getMessage());
        }
    }
    
   /**
    * Checks the context for validity. Override this
    * method to verify the context.
    */
   public boolean isValid(IFrameworkObject fwkObject) {
      
      if (!(fwkObject instanceof IContext)) {
         return super.isValid(fwkObject);
      }
      
      IContext context = (IContext) fwkObject;
      boolean isValid = true;
      
      /*
       * Implement any custom validation logic here,
       * and if the object is invalid, set isValid to false.
       */
      IPersistentObject acct = context.getObject("Account");
      IPersistentObject genericDomain = context.getObject("GenericDomain");
      if ( acct == null ) {
          System.out.println("account not found!");
          return false;
      } 
      if ( genericDomain == null ) {
          System.out.println("generic domain not found");
          return false;
      }
      
      if ( acct.getAttributeData("AccountCategory") != null ) {
          if ( genericDomain.getAttributeDataAsInteger("Type") == 9 ) // New IC
              setBirthDate(acct, genericDomain.getAttributeDataAsString("Value"));
      } else System.out.println("AccountCategory is not set");
      
      if (!isValid) {
         
         /*
          * The context is invalid, so an error should
          * be set on the context to indicate the nature
          * of the problem.
          */
         context.setError("Context is screwed up.", new Object[0]);
      }
      return isValid;
   }
   
}

/**
 * SampleValidatorFactory.java
 *
 * Based on a sample class of the same name written by Dave Sieh.
 *
 * Copyright (C) 2004 CSG Systems
 * Created 2004/05/14
 */
package com.maxis.xvalidation;

import java.util.HashMap;
import java.util.Map;
import com.csgsystems.domain.framework.attribute.IAttribute;
import com.csgsystems.domain.framework.businessobject.IPersistentObject;
import com.csgsystems.domain.framework.context.IContext;
import com.csgsystems.validator.IValidator;
import com.csgsystems.validator.ValidatorFactoryBase;

/**
 * This is a sample implementation of the IValidatorFactory
 * interface. It extends the ValidatorBase class, which
 * provides stub implementations for its methods.
 *
 * This implementation uses a java.util.Map to identify the
 * implementations of specific IValidator objects.
 *
 * @author Trevor Alyn (<a href="mailto:trevor_alyn@csgsystems.com">
 * trevor_alyn@csgsystems.com</a>)
 */
public class SampleValidatorFactory extends ValidatorFactoryBase {
   
   /**
    * A mapping of domain object names to the corresponding
    * implementations of the IValidator interface (that is,
    * to the IValidator objects that validate those domain
    * objects).<P>
    * <UL>
    * <LI>Key: Unqualified class name for domain object.</LI>
    * <LI>Value: The corresponding implementation of IValidator.</LI>
    * </UL>
    */
   protected Map mapValidators;
   
   /**
    * Required default no-argument constructor. Populates the
    * mapValidators map with our two validation classes.
    */
   public SampleValidatorFactory() {
      super();
      System.out.println("In SampleValidatorFactory constructor.");
      mapValidators = new HashMap();
      mapValidators.put("Account", new MXSAccountValidator());
      mapValidators.put("Service", new MXSServiceValidator());
      mapValidators.put("AccountLocateList", new AccountLocateListValidator());
      mapValidators.put("CustomerIdEquipMap", new CustomerIdEquipMapValidator());
      mapValidators.put("NewCustAcqAccount", new MyContextValidator());
   }
   
   /**
    * Returns a validator appropriate for the supplied
    * context (IContext).
    * @param context The context passed for validation.
    */
   public IValidator createContextValidator(IContext context) {
      
      // Find and return the appropriate IValidator (if any)
      String contextName = getUnqualifiedClassName(context);
      IValidator val = (IValidator) mapValidators.get(contextName);
      if (val == null) {
         
         /*
          * If there is no matching IValidator object,
          * return the parent (stub) implementation.
          */
         val = super.createContextValidator(context);
      }
      return val;
   }
   
   /**
    * Returns a validator appropriate for the supplied
    * domain object (IPersistentObject).
    * @param obj The domain object passed for validation.
    */
   public IValidator createDomainValidator(IPersistentObject obj) {
      
      /*
       * Find and return the appropriate IValidator (if any)
       */
      String contextName = getUnqualifiedClassName(obj);
      IValidator val = (IValidator) mapValidators.get(contextName);
      if (val == null) {
         
         /*
          * If there is no matching IValidator object,
          * return the parent (stub) implementation.
          */
         val = super.createDomainValidator(obj);
      }
      return val;
   }
   
   /**
    * Returns a validator appropriate for the supplied
    * attribute (IAttribute).
    * @param attr The attribute passed for validation.
    */
   public IValidator createAttributeValidator(IAttribute attr){
      
      /*
       * If the object passed is an attribute (that is,
       * if the object's parent is an IPersistentObject),
       * find and return the appropriate IValidator (if any)
       */
      IPersistentObject obj = attr.getParent();
      if (obj != null) {
         String parentObject = getUnqualifiedClassName(obj);
         IValidator val = (IValidator) mapValidators.get(parentObject);
         if (val == null) {
            
            /*
             * If there is no matching IValidator object,
             * return the parent (stub) implementation.
             */
            val = super.createAttributeValidator(attr);
         }
         return val;
      } else {
         
         /*
          * If the object passed has no parent, return
          * the parent (stub) implementation.
          */
         return super.createAttributeValidator(attr);
      }
   }
   
   /**
    * Utility method to get the unqualified method of the
    * supplied object.
    *
    * @param obj The object to get the class name for.
    */
   protected static String getUnqualifiedClassName(Object obj) {
      String className = "(null)";
      if (obj !=null) {
         className = obj.getClass().getName();
         int pos = className.lastIndexOf(".");
         if (pos >= 0) {
            className = className.substring(pos+1);
         }
      }
      return className;
   }
   
}

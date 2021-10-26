/*
 * MXSServiceValidator.java
 *
 * Created on January 12, 2006, 7:42 PM
 * 
 */

package com.maxis.xvalidation;

import java.util.*;
import java.sql.*;

import com.maxis.util.tuxJDBCManager;

import com.csgsystems.validator.ValidatorBase;
import com.csgsystems.domain.framework.IFrameworkObject;
import com.csgsystems.domain.framework.attribute.IAttribute;
import com.csgsystems.domain.framework.businessobject.IPersistentObject;
import com.csgsystems.fx.security.remote.RemoteDBConnection;
import com.csgsystems.fx.security.remote.SQL;
import com.csgsystems.fx.security.util.FxException;
/**
 *
 * @author  Pankaj Saini
 */
public class MXSServiceValidator extends ValidatorBase{
  
  /** Creates a new instance of MXSAccountValidation */
  public MXSServiceValidator() {
    super();
  }
  
  
  public boolean isValid(IFrameworkObject fwkObject, Object clientData) {
    boolean isValid = true;
    if(fwkObject instanceof IAttribute&&fwkObject!=null) {
      IAttribute iAtt = (IAttribute)fwkObject;
      if(iAtt!=null&&iAtt.getName().equals("ServiceZip")&&clientData!=null) {
        if(iAtt.getParent().getAttribute("ServiceCountryCode").getDataAsInteger()==458) {
          String newPostcode = clientData.toString();
          String oldPostcode = "";
          try {
              oldPostcode = iAtt.getParent().getAttribute("ReferencePostcode").getDataAsString();
          } catch (IllegalArgumentException e) {
              System.out.println("ReferencePostcode not found!");
          }
          if(!newPostcode.equals(oldPostcode)) {
            ArrayList err = checkAttribute(newPostcode, iAtt);
            if(err.size()>0) {
              fwkObject.setError("Invalid Data", err.toArray());
              isValid= false;
            }
          }
        }
      }
      else if(iAtt!=null&&iAtt.getName().equals("BServiceZip")&&clientData!=null) {
        if(iAtt.getParent().getAttribute("BServiceCountryCode").getDataAsInteger()==458) {
          String newPostCode = clientData.toString();
          String oldPostCode = iAtt.getParent().getAttributeDataAsString("BServiceZip");
          System.out.println("*** alternate new postCode = "+newPostCode);
          System.out.println("*** alternate old postcode = "+oldPostCode);
          HashMap map = isValidZip(newPostCode);
          System.out.println("*** Map.size()"+map.size());
          if(map.size()>1){
            System.out.println("*** Setting attribs");
            setAttributesInObject(iAtt, "BServiceCity",  (String)map.get("city"));
            setAttributesInObject(iAtt, "BServiceState",  (String)map.get("state"));
          }
        }
      } else if(iAtt!=null&&iAtt.getName().equals("ServiceCountryCode")&&clientData!=null) {
          if (iAtt.getDataAsInteger() != 458 && "458".equals(clientData.toString())) {
              System.out.println("changed from non malaysia to malaysia");
              if ( iAtt.getParent().getAttribute("ServiceZip").isEmpty() == false ) {
                  System.out.println("checking bill zip now");
                ArrayList err = checkAttribute(iAtt.getParent().getAttributeDataAsString("ServiceZip"), iAtt);
                if(err.size()>0) {
                  fwkObject.setError("Invalid Data", err.toArray());
                  isValid= false;
                }
              }
          }
      } else if(iAtt!=null&&iAtt.getName().equals("BServiceCountryCode")&&clientData!=null) {
          if (iAtt.getDataAsInteger() != 458 && "458".equals(clientData.toString())) {
              if ( iAtt.getParent().getAttribute("BServiceZip").isEmpty() == false ) {
                  HashMap map = isValidZip(iAtt.getParent().getAttributeDataAsString("BServiceZip"));
                  System.out.println("*** Map.size()"+map.size());
                  if(map.size()>1){
                    System.out.println("*** Setting attribs");
                    setAttributesInObject(iAtt, "BServiceCity",  (String)map.get("city"));
                    setAttributesInObject(iAtt, "BServiceState",  (String)map.get("state"));
                  }
              }
          }
      }
    } 
    
    return isValid;
  }
    
  private ArrayList checkAttribute(String postCode, IAttribute attrib) {
    ArrayList errList = new ArrayList();
    String city = null;
    String state = null;
    //String oldPostCode = iAtt.getParent().getAttribute("BillZip").getDataAsString();
    //String postCode = iAtt.getDataAsString();
    //String postCode = iAtt.getDataAsString();
    if(postCode!=null&&!(postCode.equals(""))) {
      
      //RemoteDBConnection conn =null;
      /*
      try{
        
        conn = tuxJDBCManager.getInstance().getCurrentConnection("ADMIN");
        String strQuery ="SELECT POSTOFFICE, STATE FROM MALAYSIA_POSTCODE WHERE POSTCODE = ?";
        PreparedStatement cstat = conn.prepareStatement(new SQL(strQuery,"select"));
        cstat.setString(1, iAtt);
        ResultSet rs = cstat.executeQuery();
        while(rs.next()){
          city = rs.getString(1);
          state = rs.getString(2);
        }
        rs.close();
        cstat.close();
       */
        HashMap map = isValidZip(postCode);
        city =  (String)map.get("city");
        state = (String)map.get("state");
        if(state==null&&city==null) {
          errList.add("Invalid Postcode entered. Please Try Again");
        }else {
          setAttributesInObject(attrib,  "ServiceCity",  city);
          setAttributesInObject(attrib,  "ServiceState",  state);
//          setAttributesInObject(attrib,  "ReferencePostcode", postCode);
        }
      
     /*
      catch(SQLException ex){
        ex.printStackTrace();
      }
     
      catch (FxException fx) {
        fx.printStackTrace();
      } */
    }else {
        //KenanFX – CC – General-24
        errList.add("Postcode is required, but no value has been set");
      //errList.add("PostCode must not be empty");
    }
    return errList;
  }
  
  private HashMap isValidZip(String postCode) {
     RemoteDBConnection conn =null;
     PreparedStatement cstat=null;
     ResultSet rs=null;
     HashMap map=null;
      try{
        conn = tuxJDBCManager.getInstance().getCurrentConnection("ADMIN");
        String strQuery ="SELECT POSTOFFICE, STATE FROM MALAYSIA_POSTCODE WHERE POSTCODE = ?";
        cstat = conn.prepareStatement(new SQL(strQuery,"select"));
        cstat.setString(1, postCode);
        rs = cstat.executeQuery();
        map = new HashMap();
        while(rs.next()){
          map.put("city",  rs.getString(1));
          map.put("state",  rs.getString(2));
        }
        
        rs.close();
        cstat.close();
      }
      catch(SQLException ex){
        ex.printStackTrace();
      }
      catch (FxException fx) {
        fx.printStackTrace();
      } 
      return map;
  }
  
  private void setAttributesInObject(IAttribute iAtt, String name, String value) {
    IPersistentObject obj = iAtt.getParent();
    obj.setAttributeData(name, value );
  }

    
}


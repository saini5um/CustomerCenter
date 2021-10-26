/**
 *  Project Name: Maxis R&B
 *  Developer Name: Ming Hon
 *  Module Name: Customer Center
 *  Date Created: 20050525
 *  Description:
 *  Date Modified: 20050525
 *  Version #: v01
 *
 *
 */

package com.maxis.businessobject;

import com.csgsystems.domain.framework.*;
import com.csgsystems.domain.framework.businessobject.*;
import com.csgsystems.domain.framework.attribute.*;
import com.csgsystems.fx.security.remote.SQL;
import com.csgsystems.fx.security.remote.RemoteDBConnection;
import com.csgsystems.fx.security.util.FxException;
import com.csgsystems.fxcommon.attribute.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
//added by teoh 30/06/2005
import java.util.HashMap;
import java.sql.*;

import com.csgsystems.transport.IModelAdapter;
import com.csgsystems.domain.arbor.order.*;
import java.util.Iterator;
import java.util.List;
import java.util.Calendar;
import java.util.Collection;
import com.csgsystems.error.*;
import com.maxis.util.*;
/**
 *
 * @author Ming Hon
 */
public class PrepaidIN extends Domain {

  protected IntegerEnumeratedAttribute IsLocked = null;
  protected BPCurrencyAttribute OnPeakAccountIDBalance = null;
  protected DateAttribute OnPeakAccountIDExpiryDate = null;
  protected IntegerAttribute CounterChangeTariffForFree = null;
  protected IntegerEnumeratedAttribute IsVoucherRechargingAllowed = null;
  protected IntegerAttribute CounterChangeFnFnoForFree = null;
  protected IntegerAttribute CounterChangeFAnoForFree = null;
  //protected StringAttribute LanguageID=null;
  protected StringAttribute Cgpa = null;
  protected StringAttribute ProviderID = null;
  protected BooleanAttribute IsFnFAllowed = null;
  protected BooleanAttribute IsFAAllowed = null;
  protected IntegerAttribute FnFCnt = null;
  protected IntegerAttribute FACnt = null;
  protected DateAttribute LastModifyDate = null;
  protected StringAttribute INFetchStatus = null; //[Success|Cache|CacheINFail|CacheINDown]
  protected StringAttribute SCPID = null;
  //added by teoh 30/06/2005
  protected IntegerEnumeratedAttribute languageID ;


  protected BooleanAttribute IsHZIActive                      = null;

  protected LongAttribute CounterHZIFreeAdministration        = null;
  protected LongAttribute BoltOnRegis                         = null;

  protected IntegerAttribute TariffModelNumber                = null;
  protected LongAttribute SMSBundleCounter                    = null;
  protected LongAttribute BoltOnFreeCounter                   = null;

  protected IntegerAttribute LastSelectedSMSBundle            = null;

  protected DateAttribute SpecialDayDate                      = null;
  protected LongAttribute SpecialDayFreeCounter               = null;

  protected IntegerAttribute homeZoneFreeCounter = null;
  protected IntegerAttribute homeZoneCount = null;

  protected IntegerAttribute specialDayCount = null;
  protected IntegerAttribute specialDayCounterCount = null;

  /*
  FnF and FA attributes are created on runtime via class PrepaidINList during runtime
  The number of FnF slot created can be referenced by FnFCnt
  The number of FA slot created can be referenced by FACnt
  The Attribute name for FnF are "FnFNumber[X]" where X is a number begining 0 to FnFCnt - 1
  The Attribute name for FA are "FnFDestination [X]" where X is a number begining 0 to FACnt - 1
   */

    private static Log log = null;
    static{
        try{
            log = LogFactory.getLog(com.maxis.businessobject.PrepaidIN.class);
        }
        catch(Exception ex) { }
    }

    /** Creates a new instance of PrepaidIN */
    public PrepaidIN() {
        //IsLocked = new BooleanAttribute("IsLocked", this, false);
        IsLocked = new IntegerEnumeratedAttribute("IsLocked", this, "GuiIndicator.FieldName=call_bar_sts", "IntegerValue", "DisplayValue", true);
        OnPeakAccountIDBalance = new BPCurrencyAttribute("OnPeakAccountIDBalance", this, false);
        OnPeakAccountIDBalance.setCurrencyCodeAccessors(null, null, "CurrencyCode");
        OnPeakAccountIDExpiryDate = new DateAttribute("OnPeakAccountIDExpiryDate", this, false);
        CounterChangeTariffForFree = new IntegerAttribute("CounterChangeTariffForFree", this, false);
        //IsVoucherRechargingAllowed = new BooleanAttribute("IsVoucherRechargingAllowed", this, false);
        IsVoucherRechargingAllowed = new IntegerEnumeratedAttribute("IsVoucherRechargingAllowed", this, "GuiIndicator.FieldName=voucher_bar_sts", "IntegerValue", "DisplayValue", true);
        CounterChangeFnFnoForFree = new IntegerAttribute("CounterChangeFnFnoForFree", this, false);
        CounterChangeFAnoForFree = new IntegerAttribute("CounterChangeFAnoForFree", this, false);
        //LanguageID = new StringAttribute("LanguageID", this , false);
        Cgpa = new StringAttribute("Cgpa", this, false);
        ProviderID = new StringAttribute("ProviderID", this, false);
        IsFnFAllowed = new BooleanAttribute("IsFnFAllowed", this, false);
        IsFAAllowed = new BooleanAttribute("IsFAAllowed", this, false);
        FnFCnt = new IntegerAttribute("FnFCnt", this, false);
        FACnt = new IntegerAttribute("FACnt", this, false);
        LastModifyDate = new DateAttribute("LastModifyDate", this, false);
        INFetchStatus = new StringAttribute("INFetchStatus", this, false);
        SCPID = new StringAttribute("SCPID", this, false);
        //added by teoh 30/06/2005
        languageID = new IntegerEnumeratedAttribute("LanguageID", this, "GenericEnumeration.EnumerationKey=maxis_vm_language", "Value", "DisplayValue", false);

        //IN Phase2/3 fields
        IsHZIActive                     = new BooleanAttribute("IsHZIActive", this,false);
        CounterHZIFreeAdministration    = new LongAttribute("CounterHZIFreeAdministration", this, false);
        BoltOnRegis                     = new LongAttribute("BoltOnRegis", this, false);
        SMSBundleCounter                = new LongAttribute("SMSBundleCounter", this, false);
        TariffModelNumber               = new IntegerAttribute("TariffModelNumber", this, false);
        BoltOnFreeCounter               = new LongAttribute("BoltOnFreeCounter", this, false);
        LastSelectedSMSBundle           = new IntegerAttribute("LastSelectedSMSBundle", this, false);
        SpecialDayDate                  = new DateAttribute("SpecialDayDate", this, false);
        SpecialDayFreeCounter           = new LongAttribute("SpecialDayFreeCounter", this, false);
        homeZoneCount                   = new IntegerAttribute("homeZoneCount", this, false);
        homeZoneFreeCounter             = new IntegerAttribute("homeZoneFreeCounter", this, false);
    }

    /* 7-Jul-2005 Pankaj Saini
     Implements basic updates. Extended as required */
    public boolean flush(IModelAdapter ma, IPersistentObject service) {

        if (service == null) {
            if (log != null) {log.error("No service found.");}
            return false;
        }

        //set the change data of the service record
        service.getAttribute("ChgWho").setDataAsString(com.csgsystems.domain.framework.security.SecurityManager.getInstance().getLoggedInUserId());
        service.getAttribute("ChgDt").setDataAsDate(new java.util.Date());

        // Set FnF numbers
        for ( int i = 0; i < getAttributeDataAsInteger("FnFCnt"); i++ ) {
            service.setAttributeData("FnF Slot " + (i + 1) + "_" + (PrepaidINList.FnF_Param_ID_Offset + i), getAttributeData("FnFNumber" + i));
        }
        // Set FA numbers
        for ( int i = 0; i < getAttributeDataAsInteger("FACnt"); i++ ) {
            service.setAttributeData("FA Slot " + (i + 1) + "_" + (PrepaidINList.FA_Param_ID_Offset + i), getAttributeData("FADestination" + i));
        }
        // Set HZ numbers
        for ( int i = 0; i < getAttributeDataAsInteger("homeZoneCount"); i++ ) {
            //service.getAttributeData("HZ Slot " + (i + 1) + "_" + (PrepaidINList.HomeZone_Param_ID_Offset + i)).setDataAs, getAttributeData("HomeZone" + i));
        }

        //other IN fields
        service.getAttribute("FnF Counter_2500").setDataAsInteger( getAttribute("CounterChangeFnFnoForFree").getDataAsInteger());
        service.getAttribute("FA Counter_2550").setDataAsInteger( getAttribute("CounterChangeFAnoForFree").getDataAsInteger());
        //service.getAttribute("Plan Counter").setDataAsString( getAttribute("").getDataAsString());
        service.getAttribute("Bolt On Tarriff Type_2430").setDataAsInteger( getAttribute("TariffModelNumber").getDataAsInteger());
        service.getAttribute("Bolt On Counter_2432").setDataAsLong( getAttribute("BoltOnFreeCounter").getDataAsLong());
        service.getAttribute("Bolt On SMS Bundle Type_2431").setDataAsLong( getAttribute("SMSBundleCounter").getDataAsLong());
        service.getAttribute("HZ Counter_2600").setDataAsInteger( getAttribute("homeZoneFreeCounter").getDataAsInteger());
        service.getAttribute("Special Day_2420").setDataAsDate( getAttribute("SpecialDayDate").getDataAsDate());
        service.getAttribute("Special Day Counter_2421").setDataAsLong( getAttribute("SpecialDayFreeCounter").getDataAsLong());


        service.flush(ma);

        return service.flush(ma);
    }
}

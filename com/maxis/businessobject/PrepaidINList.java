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

import com.csgsystems.domain.framework.attribute.*;
import com.csgsystems.fxcommon.attribute.*;
import com.csgsystems.domain.arbor.businessobject.Account;
import com.csgsystems.domain.framework.PersistentObjectFactory;
import com.csgsystems.domain.framework.businessobject.*;
import com.csgsystems.domain.arbor.businessobject.Service;
import com.csgsystems.util.service.ServiceFinder;
import com.csgsystems.fx.security.remote.RemoteDBConnection;
import com.csgsystems.fx.security.remote.SQL;
import com.csgsystems.fx.security.util.FxException;
import com.csgsystems.xlogic.IExtendedLogicFactory;
import com.csgsystems.domain.framework.security.SecurityManager;
import com.maxis.xlogic.*;
import com.maxis.util.*;
import com.csgsystems.cache.*;
import java.sql.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.util.Map;
import java.util.HashMap;
import com.maxis.cache.*;
import com.maxis.integ.*;
import java.util.ArrayList;

/**
 *
 * @author Ming Hon
 */
public class PrepaidINList extends DomainCollection {

    private IPersistentObject m_pService;
    private IPersistentObject m_pPrepaidService;
    private Cache prepaidProviderInfoCache = null;
    private int FnFCnt = 0;
    private int FACnt = 0;
    private int ProviderId = 0;
    private static String valid_in_access_subscriber_status[] = {"A", "N","D","W"};
    public static final int FnF_Param_ID_Offset = 2501;
    public static final int FA_Param_ID_Offset = 2551;

    //Added by Joell
    private int homeZoneCount = 0;
    public static final int HomeZone_Param_ID_Offset = 2601;

    private int specialDayCount = 0;
    private int specialDayCounter = 0;
    private ArrayList homeZoneArray = null;
    private ArrayList homeZoneResults = null;

    /** Creates a new instance of PrepaidINList */
    public PrepaidINList() {
        super(PrepaidINList.class);
    }

    private static Log log = null;
    static{
        try{
            log = LogFactory.getLog(com.maxis.businessobject.PrepaidINList.class);
        }
        catch(Exception ex) { }
    }

    protected boolean localAddAssociation(IPersistentObject relObject) {
        m_pService = relObject;
        return true;
    }

    protected int listPageFault(int nStart) {
        //log.debug("PrepaidINList.listPageFault()");


        try{
            //get the emf_config_id of the current service
            int emf_config_id = m_pService.getAttribute("EmfConfigId").getDataAsInteger();
            //Get the Prepaid provider info cache
            if (prepaidProviderInfoCache == null) {
                // The cache is stored in FXExtendedLogicFactory class

                IExtendedLogicFactory factory = (IExtendedLogicFactory)
                ServiceFinder.findServiceProvider(com.csgsystems.xlogic.IExtendedLogicFactory.class, null);

                // Only try to extract cache if we are using FXExtenedLogicFactory otherwise
                // no cache would be present
                if (factory!=null && factory instanceof FXExtendedLogicFactory) {
                    prepaidProviderInfoCache = ((FXExtendedLogicFactory)factory).getPrepaidProviderInfoCache();
                }
            }


            /*if ( prepaidHomeZoneCache == null )
            {
                IExtendedLogicFactory factory = (IExtendedLogicFactory)
                ServiceFinder.findServiceProvider(com.csgsystems.xlogic.IExtendedLogicFactory.class, null);
                log.info("Zero");

                if (factory!=null && factory instanceof FXExtendedLogicFactory)
                {
                    log.info("One");
                    prepaidProviderInfoCache = ((FXExtendedLogicFactory)factory).getPrepaidProviderInfoCache();
                }
            }*/

            //get the provider info related to the current service
            Map providerInfo = prepaidProviderInfoCache.getCache();
            com.maxis.cache.PrepaidProviderInfo prepaidProviderInfo
            = (com.maxis.cache.PrepaidProviderInfo)prepaidProviderInfoCache.get(new Long(emf_config_id));

            //Ming Hon: No need to catch, since do nothing
            //try{
                Map providerInfoItem = prepaidProviderInfo.getValue();
                Long FNF_SLOT = (Long)providerInfoItem.get("FNF_SLOT");
                Long FA_SLOT = (Long)providerInfoItem.get("FA_SLOT");
                Long PROVIDER_ID = (Long)providerInfoItem.get("PROVIDER_ID");

                //Added by Joell
                Long HZ_SLOT = (Long)providerInfoItem.get("HZ_SLOT");

                if(FNF_SLOT!=null) FnFCnt = FNF_SLOT.intValue();
                else log.warn("FNF_SLOT for EMF_CONFIG_ID="+emf_config_id+" is null");
                if(FA_SLOT!=null) FACnt = FA_SLOT.intValue();
                else log.warn("FA_SLOT for EMF_CONFIG_ID="+emf_config_id+" is null");
                if(PROVIDER_ID!=null) ProviderId = PROVIDER_ID.intValue();
                else log.warn("PROVIDER_ID for EMF_CONFIG_ID="+emf_config_id+" is null");

                //Added by Joell
                if(HZ_SLOT!=null) homeZoneCount = HZ_SLOT.intValue();
                else log.warn("HZ_SLOT for EMF_CONFIG_ID="+emf_config_id+" is null");

            /*}
            catch(Exception ex){
                ex.printStackTrace();
            }*/

            //get the PrepaidServiceListObject
            //table MXS_SERVICE_EXT
            m_pPrepaidService = m_pService.getObject("PrepaidServiceList","Service");

            //get the persistant object
            IPersistentObject PrepaidIN = (IPersistentObject)PersistentObjectFactory.getFactory().createNew(PrepaidIN.class, null);

            //set some readonly values
            PrepaidIN.getAttribute("FnFCnt").setDataAsInteger(FnFCnt);
            PrepaidIN.getAttribute("FACnt").setDataAsInteger(FACnt);

            //Added by Joell
            PrepaidIN.getAttribute("homeZoneCount").setDataAsInteger(homeZoneCount);

            //create the FnF and FA fields
            for(int i=0; i<FnFCnt; i++){
                StringAttribute FnFAtt = new StringAttribute("FnFNumber"+i, (Domain)PrepaidIN, false);
            }
            for(int i=0; i<FACnt; i++){
                StringAttribute FnFAtt = new StringAttribute("FADestination"+i, (Domain)PrepaidIN, false);
            }


            //Added by Joell
            for(int i=0; i<homeZoneCount; i++){
                log.info("HomeZone Counter -> " + homeZoneCount);
                StringAttribute homeZoneAtt = new StringAttribute("HomeZone"+i, (Domain)PrepaidIN, false);
            }


            boolean toFetchFromIN = false;
            if(m_pPrepaidService!=null){
                IPersistentCollection ipc = (IPersistentCollection)m_pPrepaidService;
                //find the status of the subscriber
                //and check IN subscriber status
                    if(ipc.getCount()>0){
                    IPersistentObject ipo = ipc.getAt(0);
                    String subscriberStatus = ipo.getAttribute("SubscriberStatus").getDataAsString();
                    for(int i=0; i<valid_in_access_subscriber_status.length; i++){
                        if(subscriberStatus.equals(valid_in_access_subscriber_status[i])){
                            toFetchFromIN = true;
                            i = valid_in_access_subscriber_status.length;
                        }
                    }

                    /* //display all fields on Prepaid Service object
                    log.info("3 ipc.getCount()="+ipc.getCount());
                    if(!ipc.isFaulted()){
                        ipc.reset();
                        log.info("reset");
                    }
                    log.info("4 ipc.getCount()="+ipc.getCount());
                    java.util.Collection col = m_pPrepaidService.getAttributes();
                    Object o[] = col.toArray();
                    for(int i=0; i<o.length; i++){
                        Attribute a = (Attribute)o[i];
                        log.info("field name="+a.getName());
                    }*/

                    //get the IN data from cache first
                    PrepaidIN.getAttribute("IsLocked").setDataAsInteger(ipo.getAttribute("CallBarStatus").getDataAsInteger());
                    PrepaidIN.getAttribute("OnPeakAccountIDBalance").setDataAsDouble(ipo.getAttribute("Balance1").getDataAsDouble());
                    PrepaidIN.getAttribute("OnPeakAccountIDExpiryDate").setDataAsDate(ipo.getAttribute("DeactivationDate").getDataAsDate());
                    PrepaidIN.getAttribute("CounterChangeTariffForFree").setDataAsInteger(m_pService.getAttribute("Plan Counter_2401").getDataAsInteger());
                    PrepaidIN.getAttribute("IsVoucherRechargingAllowed").setDataAsInteger(ipo.getAttribute("VoucherBarStatus").getDataAsInteger());
                    PrepaidIN.getAttribute("LanguageID").setDataAsInteger(ipo.getAttribute("IPLanguage").getDataAsInteger());
                    PrepaidIN.getAttribute("CounterChangeFnFnoForFree").setDataAsInteger(m_pService.getAttribute("FnF Counter_2500").getDataAsInteger());
                    PrepaidIN.getAttribute("CounterChangeFAnoForFree").setDataAsInteger(m_pService.getAttribute("FA Counter_2550").getDataAsInteger());
                    PrepaidIN.getAttribute("Cgpa").setDataAsString(m_pService.getAttribute("ServiceExternalId").getDataAsString());
                    PrepaidIN.getAttribute("ProviderID").setDataAsInteger(ProviderId);
                    PrepaidIN.getAttribute("SCPID").setDataAsString("N/A"); //just give a dummy SCPID first

                    if(FnFCnt>0)
                        PrepaidIN.getAttribute("IsFnFAllowed").setDataAsBoolean(true);
                    else
                        PrepaidIN.getAttribute("IsFnFAllowed").setDataAsBoolean(false);

                    if(FACnt>0)
                        PrepaidIN.getAttribute("IsFAAllowed").setDataAsBoolean(true);
                    else
                        PrepaidIN.getAttribute("IsFAAllowed").setDataAsBoolean(false);
                        PrepaidIN.getAttribute("LastModifyDate").setDataAsDate(ipo.getAttribute("LastUpdateDate").getDataAsDate());
                        PrepaidIN.getAttribute("INFetchStatus").setDataAsString("Cache");

                        for(int i=0; i<FnFCnt; i++)
                        {
                            PrepaidIN.getAttribute("FnFNumber"+i).setDataAsString(
                            m_pService.getAttribute("FnF Slot "+(i+1)+"_"+(FnF_Param_ID_Offset+i)).getDataAsString());
                            //log.info("FF --> " + PrepaidIN.getAttribute("FnFNumber"+i).getDataAsString());
                        }

                        for(int i=0; i<FACnt; i++)
                        {
                            PrepaidIN.getAttribute("FADestination"+i).setDataAsString(
                            m_pService.getAttribute("FA Slot "+(i+1)+"_"+(FA_Param_ID_Offset+i)).getDataAsString()
                            );
                        }

                    //Added by Joell
                    for(int i=0; i<homeZoneCount; i++)
                    {
                        //log.info("HZ");
                        PrepaidIN.getAttribute("HomeZone"+i).setDataAsString(String.valueOf(m_pService.getAttribute("HZ Slot "+(i+1)+"_"+(HomeZone_Param_ID_Offset+i)).getDataAsInteger()));
                        ///log.info("LAALA -> " + String.valueOf(m_pService.getAttribute("HZ Slot "+(i+1)+"_"+(HomeZone_Param_ID_Offset+i)).getDataAsString()));
                    }


                    //Added by Joell
                    PrepaidIN.getAttribute("CounterHZIFreeAdministration").setDataAsInteger(m_pService.getAttribute("HZ Counter_2600").getDataAsInteger());
                    PrepaidIN.getAttribute("SpecialDayDate").setDataAsDate(m_pService.getAttribute("Special Day_2420").getDataAsDate());
                    PrepaidIN.getAttribute("SpecialDayFreeCounter").setDataAsLong(0);

                    PrepaidIN.getAttribute("TariffModelNumber").setDataAsInteger(m_pService.getAttribute("Bolt On Tarriff Type_2430").getDataAsInteger());
                    PrepaidIN.getAttribute("SMSBundleCounter").setDataAsLong(m_pService.getAttribute("Bolt On SMS Bundle Type_2431").getDataAsLong());
                    PrepaidIN.getAttribute("BoltOnFreeCounter").setDataAsLong(0);



                    /* display all fields on service object
                    java.util.Collection col = m_pService.getAttributes();
                    Object o[] = col.toArray();
                    for(int i=0; i<o.length; i++){
                        Attribute a = (Attribute)o[i];
                        log.info("field name="+a.getName());
                    }
                    */



                    //get data from IN when conditions are valid
                    if(toFetchFromIN)
                    {
                        //get the SCPID from inventory
                        String sql = "SELECT ANNOTATION_2 FROM INVD_MAIN WHERE EXTERNAL_ID = ? ";
                        boolean scp_id_retrieve_success = false;
                        boolean scp_id_retrieve_error = false;
                        boolean scp_id_notfound = false;

                        RemoteDBConnection dbConn = tuxJDBCManager.getInstance().getCurrentConnection("CAT");

                        if (dbConn == null)
                        {
                            log.error("Error getting current Oracle Connection."+getClass().getName());
                            scp_id_retrieve_error = true;
                        }
                        else
                        {
                            PreparedStatement pstmt = null;
                            try
                            {
                                pstmt = dbConn.prepareStatement (new SQL(sql, "select"));
                                pstmt.setMaxRows(10);
                                pstmt.setString(1, m_pService.getAttribute("ServiceExternalId").getDataAsString());
                                ResultSet rset = pstmt.executeQuery();

                                if(rset.next())
                                {
                                    PrepaidIN.getAttribute("SCPID").setDataAsString(rset.getString(1));
                                    scp_id_retrieve_success = true;
                                }
                                else
                                {
                                    scp_id_notfound = true;
                                }
                                rset.close();
                                pstmt.close();

                            } catch (FxException ex) {
                                log.error("SQL Exception "+ex.getMessage(), ex);
                                scp_id_retrieve_error = true;
                            } catch (SQLException ex) {
                                log.error("SQL Exception "+ex.getMessage(), ex);
                                scp_id_retrieve_error = true;
                            }

                            if(!scp_id_retrieve_error)
                            {
                                //get the DATA from IN
                                Integrator integ = Integrator.getInstance();
                                HashMap input = new HashMap();
                                HashMap inputHeader = new HashMap();
                                HashMap inputBody = new HashMap();
                                inputHeader.put("KenanUserID", SecurityManager.getInstance().getLoggedInUserId());
                                inputHeader.put("AccountInternalId", new Integer(m_pService.getAttribute("ParentAccountInternalId").getDataAsInteger()));
                                inputHeader.put("ServiceInternalId", new Integer(m_pService.getAttribute("ServiceInternalId").getDataAsInteger()));
                                inputHeader.put("ServiceInternalIdResets", new Integer(m_pService.getAttribute("ServiceInternalIdResets").getDataAsInteger()));
                                inputHeader.put("ServerId", new Integer(CommonUtil.getCurrentServerId()));
                                inputHeader.put("MSISDN", m_pService.getAttribute("ServiceExternalId").getDataAsString());
                                inputHeader.put("TransType", "AccRetrieval");
                                inputBody.put("SCPID", PrepaidIN.getAttribute("SCPID").getDataAsString());
                                input.put("Header", inputHeader);
                                input.put("Body", inputBody);
                                //log.info("Input="+PrintMap.toMapString(input, 2));
                                HashMap output = integ.execute("AccRetrieval", input);
                                HashMap outputHeader = (HashMap)output.get("Header");
                                HashMap outputBody = (HashMap)output.get("Body");
                                //log.info("Output body --> " + outputBody);

                                //log.info("outputHashMap=");
                                //PrintMap.printHashMap(output);
                                int execCode = ((Integer)outputHeader.get("ExecCode")).intValue();
                                //execCode = 1 = success
                                //execCode > 1 = success with warning
                                if(execCode >= 1)
                                {
                                    if(((Boolean)outputBody.get("IsLocked")).booleanValue()) PrepaidIN.getAttribute("IsLocked").setDataAsInteger(1);
                                    else PrepaidIN.getAttribute("IsLocked").setDataAsInteger(2);
                                    //PrepaidIN.getAttribute("OnPeakAccountIDBalance").setDataAsInteger(((Integer)outputBody.get("OnPeakAccountID.Balance")).intValue());
                                    PrepaidIN.getAttribute("OnPeakAccountIDBalance").setDataAsDouble(Double.parseDouble(outputBody.get("OnPeakAccountID.Balance").toString()));
                                    PrepaidIN.getAttribute("OnPeakAccountIDExpiryDate").setDataAsDate((java.util.Date)outputBody.get("OnPeakAccountID.ExpiryDate"));
                                    PrepaidIN.getAttribute("CounterChangeTariffForFree").setDataAsInteger(((Integer)outputBody.get("CounterChangeTariffForFree")).intValue());
                                    if(((Boolean)outputBody.get("IsVoucherRechargingAllowed")).booleanValue()) PrepaidIN.getAttribute("IsVoucherRechargingAllowed").setDataAsInteger(1);
                                    else PrepaidIN.getAttribute("IsVoucherRechargingAllowed").setDataAsInteger(2);
                                    PrepaidIN.getAttribute("LanguageID").setDataAsInteger(((Integer)outputBody.get("LanguageID")).intValue());
                                    PrepaidIN.getAttribute("CounterChangeFnFnoForFree").setDataAsInteger(((Integer)outputBody.get("CounterChangeFnFnoForFree")).intValue());
                                    PrepaidIN.getAttribute("Cgpa").setDataAsString((String)outputBody.get("Cgpa"));
                                    PrepaidIN.getAttribute("ProviderID").setDataAsInteger(((Integer)outputBody.get("ProviderID")).intValue());
                                    PrepaidIN.getAttribute("IsFnFAllowed").setDataAsBoolean(((Boolean)outputBody.get("IsFnFAllowed")).booleanValue());
                                    PrepaidIN.getAttribute("IsFAAllowed").setDataAsBoolean(((Boolean)outputBody.get("IsFAAllowed")).booleanValue());
                                    PrepaidIN.getAttribute("LastModifyDate").setDataAsDate(new java.util.Date());

                                    // Added by Pankaj 06-Jul-2005
                                    PrepaidIN.getAttribute("CounterChangeFAnoForFree").setDataAsInteger(((Integer)outputBody.get("CounterChangeFAnoForFree")).intValue());

                                    //Added by Joell
                                    PrepaidIN.getAttribute("CounterHZIFreeAdministration").setDataAsInteger(((Integer)outputBody.get("CounterHZIFreeAdministration")).intValue());
                                    PrepaidIN.getAttribute("SpecialDayDate").setDataAsDate(((java.util.Date)outputBody.get("SpecialDayDate")));
                                    PrepaidIN.getAttribute("TariffModelNumber").setDataAsInteger(((Integer)outputBody.get("TariffModelNumber")).intValue());
                                    PrepaidIN.getAttribute("SMSBundleCounter").setDataAsInteger(((Integer)outputBody.get("SMSBundleCounter")).intValue());

                                    //log.info("TariffModelNumber --> " + PrepaidIN.getAttribute("TariffModelNumber").getDataAsInteger());
                                    ///log.info("SMSBundleCounter --> " + PrepaidIN.getAttribute("SMSBundleCounter").getDataAsInteger());

                                    for(int i=0; i<FnFCnt; i++)
                                    {
                                        PrepaidIN.getAttribute("FnFNumber"+i).setDataAsString(((String)outputBody.get("FnFNumber["+i+"]")));
                                        m_pService.getAttribute("FnF Slot "+(i+1)+"_"+(FnF_Param_ID_Offset+i)).setDataAsString((String)outputBody.get("FnFNumber["+i+"]"));
                                    }

                                    for(int i=0; i<FACnt; i++)
                                    {
                                        PrepaidIN.getAttribute("FADestination"+i).setDataAsString(((String)outputBody.get("FADestination["+i+"]")));
                                        m_pService.getAttribute("FA Slot "+(i+1)+"_"+(FA_Param_ID_Offset+i)).setDataAsString((String)outputBody.get("FADestination["+i+"]"));
                                    }

                                    //Added by Joell
                                    homeZoneArray = new ArrayList();
                                    homeZoneResults = new ArrayList();
                                    StringBuffer hzBuffer = new StringBuffer();
                                    String homeZone = null;

                                    for(int i=0; i<homeZoneCount; i++)
                                    {
                                        //log.info("@@@ --> " +homeZoneCount+ "i --> " +i );
                                        //PrepaidIN.getAttribute("HomeZone"+i).setDataAsString(((String)outputBody.get("HomeZone["+i+"]")));
                                        homeZoneArray.add(outputBody.get("HomeZone["+i+"]"));
                                        String a = String.valueOf(outputBody.get("HomeZone["+i+"]"));
                                        String hZones = this.getHomeZoneDescription( a );
                                        PrepaidIN.getAttribute("HomeZone"+i).setDataAsString(hZones);
                                        //log.info("(String)outputBody.get(HomeZone[+i+] " + hZones);
                                        //hzBuffer.append(String.valueOf(outputBody.get("HomeZone["+i+"]")) + ",");
                                   }

                                   /*if ( hzBuffer != null )
                                    {
                                        if (hzBuffer.length() != 0 && hzBuffer.charAt(hzBuffer.length() - 1) == ',')
                                        {
                                            log.info("*** " + hzBuffer.toString());
                                            hzBuffer.delete(hzBuffer.lastIndexOf(","), hzBuffer.length());
                                        }



                                        for ( int x = 0; x < homeZoneResults.size(); x++ )
                                        {
                                            PrepaidIN.getAttribute("HomeZone"+x).setDataAsString((String)homeZoneResults.get(x));
                                        }
                                    }*/

                                   // log.info("HomeZone size --->  " + homeZoneResults.size());
                                    for ( int x = 0; x < homeZoneResults.size(); x++ )
                                    {
                                        PrepaidIN.getAttribute("HomeZone"+x).setDataAsString((String)homeZoneResults.get(x));
                                        //log.info("HomeZone --->  " + (String)homeZoneResults.get(x));
                                    }


                                    //Ming Hon: added 11 July 2005
                                    ipo.getAttribute("DeactivationDate").setDataAsDate(PrepaidIN.getAttribute("OnPeakAccountIDExpiryDate").getDataAsDate());
                                    FXExtendedLogicFactory.chkExpDt(ipo,providerInfoItem);
                                    FXExtendedLogicFactory.chkBalForfeitedDt(ipo,providerInfoItem);


                                    PrepaidIN.getAttribute("INFetchStatus").setDataAsString("Success");
                                    //m_pService.flush(com.csgsystems.transport.ModelAdapterFactory.getModelAdapter());
                                    
                                    ((PrepaidIN)PrepaidIN).flush(com.csgsystems.transport.ModelAdapterFactory.getModelAdapter(),m_pService);
                                }
                                else
                                {
                                    PrepaidIN.getAttribute("INFetchStatus").setDataAsString("CacheINFail");
                                    javax.swing.JOptionPane.showMessageDialog(null,"IN is down. Fail to retrieve subscriber details from IN.","Customer center",javax.swing.JOptionPane.ERROR_MESSAGE);
                                }
                            }
                            else
                            {
                                log.debug("Error on retiving SCP_ID, not going fetch from IN.");
                            }
                        }
                    }
                    else
                    {
                        log.debug("No need to fetch from IN, the subscriber status does not represent a status to fetch from IN.");
                    }
                    add(PrepaidIN);
                }
                else{
                    log.error("PrepaidServiceList object is null. This may not be a prepaid service or the correspondend row on MXS_SERVICE_EXT does not exists.");
                }
            }
            else{
                log.error("PrepaidServiceList object is null. This may not be a prepaid service or the correspondend row on MXS_SERVICE_EXT does not exists.");
                //javax.swing.JOptionPane.showMessageDialog(null,"Subscriber Data not found in MXS_SERVICE_EXT.","Customer center",javax.swing.JOptionPane.ERROR_MESSAGE);
            }

            // PrepaidIN.flush();
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        return 0;
    }

    //Added by Joell
    public String getHomeZoneDescription( String homeZoneIDS )
    {
        RemoteDBConnection dbConn = tuxJDBCManager.getInstance().getCurrentConnection("ADMIN");
        //ResultSet result;
        ArrayList resultHomeZone = new ArrayList();
        String homeZoneDesc = null;

        if (dbConn == null) {
            log.info("No connection to database");
        }

        try
        {

            String strQuery = "SELECT HOMEZONE_DESCRIPTION FROM MXS_HOMEZONE WHERE HOMEZONE_ID = ( " + homeZoneIDS + " ) ORDER BY HOMEZONE_ID";
            log.info("Strquery --> " + strQuery);
            PreparedStatement sql = dbConn.prepareStatement(new SQL(strQuery, "select"));
            ResultSet result = sql.executeQuery();

            while ( result.next() )
            {
                log.info("HZ Result --> " + result.getString(1));
                //resultHomeZone.add(result.getString(1));
                homeZoneDesc =  result.getString(1);
            }

            result.close();

        }
        catch (java.sql.SQLException ex)
        {
            ex.printStackTrace();
            log.error("SQL Exception "+ex.getMessage());
        }
        catch (FxException fx)
        {
            fx.printStackTrace();
        }

        return homeZoneDesc;
    }

}

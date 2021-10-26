/*
 * MXSServiceOrderExtendedLogic.java
 *
 * Created on June 29, 2005, 5:31 PM
 * Date         By          PN#                      Resolution          
 * 
 */

package com.maxis.xlogic;

import com.csgsystems.domain.framework.businessobject.IPersistentObject;
import com.csgsystems.xlogic.ExtendedLogicBase;

import com.csgsystems.domain.framework.context.IContext;
import com.csgsystems.domain.framework.ObjectManager;
import com.csgsystems.domain.arbor.order.OrderManager;
import com.csgsystems.domain.framework.attribute.IDateTime;
import com.csgsystems.domain.framework.attribute.DateTimeAttribute;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
/**
 *
 * @author  maxis
 */
public class MXSServiceOrderExtendedLogic extends ExtendedLogicBase
{

    private static Log log;
    private static Date dt;
    
    static {
        try {
            log = LogFactory.getLog(com.maxis.xlogic.MXSServiceOrderExtendedLogic.class);
        }
        catch (Exception ex) {
        }
    }
    
    
    /** Creates a new instance of MXSServiceOrderExtendedLogic */
    public MXSServiceOrderExtendedLogic() 
    {
        super();
    }
    
    public void postUnmarshal(IPersistentObject target) {
        System.out.println("due date set to include time");
        DateTimeAttribute dueDate = (DateTimeAttribute)target.getAttribute("Due Date_120001");
        dueDate.setDateTimeInclude(IDateTime.DATETIME);
    }
    
    public void newObject(IPersistentObject target) 
    {
        log.info("newObject");
        System.out.println("due date set to include time in new object");
        DateTimeAttribute dueDate = (DateTimeAttribute)target.getAttribute("Due Date_120001");
        dueDate.setDateTimeInclude(IDateTime.DATETIME);
        //target.getAttribute("Due Date_120001").setReadOnly(true);
/*        if(target.getAttribute("Due Date_120001").getData() == null)
            target.setAttributeDataAsDate("Due Date_120001", MXSOrderExtendedLogic.effectiveDt);
        if(target.getAttribute("CustDesiredDate").getData() == null){
            IPersistentObject servicePlanObj = null;
            int industryType = -1;
            IContext icontext = (IContext)ObjectManager.getInstance().getContexts().get(0);
            IPersistentObject ipersistentobject2 = null;
            if(icontext != null)
                ipersistentobject2 = icontext.getObject("Service", null);
            if(ipersistentobject2 == null)
            {
                log.warn("Unable to get service");
            } else {
                servicePlanObj = ipersistentobject2.getObject("ServicePricingPlan","Service");
                if(servicePlanObj != null){
                    industryType = servicePlanObj.getAttribute("IndustryType").getDataAsInteger();
                    log.info("industryType="+industryType);
                    if(industryType != -1 && industryType != 0){
                        log.info("Setting CustDesiredDate for orders sent to either DCMS or APS");
                        target.setAttributeDataAsDate("CustDesiredDate", MXSOrderExtendedLogic.todayDt);
                    }
                } else{
                    log.warn("No ServicePlan Object !!");
                }
            }
        } */
   }
    
}
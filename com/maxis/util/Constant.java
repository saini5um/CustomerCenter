/*
 * Constant.java
 *
 * Created on July 21, 2005, 4:39 PM
 */

package com.maxis.util;

/**
 *
 * @author  maxis
 */
public class Constant {
    
    public static final String PYMT_MGR        = "ccpre_PaymentServicesManager";
    public static final String PYMT_SEN_EXEC   = "ccpre_PaymentServicesSenExec";
    public static final String PYMT_EXEC       = "ccpre_PaymentServicesExec";
    public static final String PYMT_NON_EXEC_1 = "ccpre_PaymentServicesNonExec1";
    
    public static final long ADJ_LIMIT_PYMT_MGR = 500000; //$5000.00
    public static final long ADJ_LIMIT_PYMT_SEN_EXEC = 50000; //$500.00
    public static final long ADJ_LIMIT_PYMT_EXEC = 10000; //$100.00
    public static final long ADJ_LIMIT_PYMT_NON_EXEC_1 = 6000; //$60.00
    
    public static final long MRELOAD_LIMIT_PYMT_MGR = 500000; //$5000.00
    public static final long MRELOAD_LIMIT_PYMT_SEN_EXEC = 50000; //$500.00
    public static final long MRELOAD_LIMIT_PYMT_EXEC = 10000; //$100.00
    public static final long MRELOAD_LIMIT_PYMT_NON_EXEC_1 = 6000; //$60.00

    public static final int MRELOAD_RPPT = 22;
    public static final String MRELOAD_RPPT_MSG_SEQ_ID_OVERRIDE = "020";

    public static final int VBarUnBarByFS = 14;
    public static final int CBarUnBarByFS = 17;
    
    public static final String SERVICE_SUSPEND = "SUSPEND";
    public static final String SERVICE_DISCONNECT = "DISCONNECT";
    public static final String SERVICE_TRANSFER = "TRANSFER";
    public static final String SERVICE_RESUME = "RESUME";
    
    //service type ranges constant
    public static final int SERVICE_TYPE_PREPAID_MOBILE_LOW = 1000;
    public static final int SERVICE_TYPE_PREPAID_MOBILE_HIGH = 1999;
    public static final int SERVICE_TYPE_POSTPAID_MOBILE_LOW = 4000;
    public static final int SERVICE_TYPE_POSTPAID_MOBILE_HIGH = 5999;
    public static final int SERVICE_TYPE_POSTPAID_FIXED_LOW = 2000;
    public static final int SERVICE_TYPE_POSTPAID_FIXED_HIGH = 3999;
    public static final int SERVICE_TYPE_POSTPAID_ISP_LOW = 0;
    public static final int SERVICE_TYPE_POSTPAID_ISP_HIGH = 999;

    public static final int SIMM_MISM_LINK = 33;
    public static final int SIMM_PRIMARY_INV_TYPE = 414;
    public static final int SIMM_PRIMARY_USIM_INV_TYPE = 421;
    public static final int SIMM_SECONDARY_INV_TYPE = 415;
    public static final int SIMM_SECONDARY_USIM_INV_TYPE = 422;
    
    public static final int POSTPAID_GSM_EMF_CONFIG_ID = 4001;
    public static final int INV_MEMBER_TYPE = 70;
    public static final int ADD_ACTION = 10;
    public static final int DISCONNECT_ACTION = 30;
    public static final int POSTPAID_REGULAR_INV_TYPE = 600;
    public static final int POSTPAID_SPECIAL_INV_TYPE = 603;
    public static final int POSTPAID_GOLDEN_INV_TYPE = 604;

    public static final int MSISDN_EXT_ID = 23;
    public static final String DATA_NUMBER_PREFIX = "6017";
    
    // NSA/NSB dependent industry type
    
    public static final int NSA_NSB_DEPENDENT_INDUSTRY_TYPE = 1000;
}

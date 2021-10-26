package com.maxis.action;

import com.csgsystems.localization.ResourceManager;
import com.maxis.util.MISMUtil;
import java.awt.event.ActionEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.csgsystems.domain.framework.businessobject.IPersistentObject;
import com.csgsystems.domain.framework.context.IContext;
import com.csgsystems.fx.security.remote.RemoteDBConnection;
import com.csgsystems.fx.security.remote.SQL;
import com.csgsystems.fx.security.util.FxException;
import com.csgsystems.workflow.BaseWorkflowAction;
import com.maxis.util.ErrorReporting;
import com.maxis.util.tuxJDBCManager;
import com.csgsystems.error.ErrorFactory;
import javax.swing.JOptionPane;

/**
 *
 * @author  SK Chua
 */
public class TransferSIAction extends BaseWorkflowAction {

	  private ErrorReporting err = ErrorReporting.getInstance();

	  public void execute(ActionEvent arg0, IContext context) {

           	 IPersistentObject accountServiceObject  = context.getObject("Service",null);
                 IPersistentObject account = context.getObject("Account");
                 System.out.println("is Primary = " + MISMUtil.isPrimary(accountServiceObject));
                 System.out.println("is Secondary = " + MISMUtil.isSecondary(accountServiceObject));
                 System.out.println("has Secondary = " + MISMUtil.hasSecondary(account));
                 System.out.println("has Primary = " + MISMUtil.hasPrimary(account));
		if (isNotAllowToTransferPostSI2Pre(context)){
			accountServiceObject.setError("Cannot transfer this Postpaid Service to a non-convergent Account",null);
            engine.getActionFrame().displayError(accountServiceObject.getError(), true);
            accountServiceObject.resetError();
            fireEvent("noPass");
		} else if (MISMUtil.isSecondary(accountServiceObject) && MISMUtil.hasPrimary(account)) {
                     engine.getActionFrame().displayError(ErrorFactory.createError(ResourceManager.getString("MISM.Secondary.Transfer.error"), 
                             null, TransferSIAction.class), true);
                     fireEvent("noPass");
                } else if (MISMUtil.isPrimary(accountServiceObject) && MISMUtil.hasSecondary(account)) {
                     // warning message
                     JOptionPane.showMessageDialog(null, ResourceManager.getString("MISM.Primary.Transfer.warning"), 
                             "Alert!", JOptionPane.WARNING_MESSAGE);
                     fireEvent("pass");
                }
		else{
			 fireEvent("pass");
		}
	}

	 private final boolean isNotAllowToTransferPostSI2Pre(IContext context) {
		boolean isPrepaid = false;
		IPersistentObject service = context.getObject("Service", null);
		IPersistentObject ServiceTransfer = context.getObject(
				"ServiceTransfer", null);
		if (service != null) {

			// check is PostPaid SI
			int tranferService = service.getAttributeDataAsInteger("EmfConfigId");
			if (tranferService >= 4001 && tranferService <= 6000) {
				// is postPaid Service
				// check TransferToAccount is Prepaid
				if (ServiceTransfer != null) {
					String tranferServiceAccount = ServiceTransfer
							.getAttributeDataAsString("TransferToAccountInternalId");
					// retrive DB isPrepaidAccount();

					isPrepaid = isPrepaidAccount(tranferServiceAccount);
					if (isPrepaid) {
						return true;
					}
				}
			}

		}
		return false;
	}


		public boolean isPrepaidAccount (String accountNo )
		 {
			 boolean isPrepaidAcct=false;
			 int acctSegId=0;
			try
		     {

		         StringBuffer query = new StringBuffer();

		         RemoteDBConnection dbConn = tuxJDBCManager.getInstance().getCurrentConnection("CUS");

		         query.append( " SELECT ACCT_SEG_ID from CMF WHERE ACCOUNT_NO="+accountNo );
		          if ( dbConn != null )
		         {
		             PreparedStatement pStmt = dbConn.prepareStatement( new SQL ( query.toString() , "select" ) );
		             ResultSet rs = null;
		             rs = pStmt.executeQuery();
		             int i = 0;

		             if ( rs.next() )
		             {
		            	 System.out.println("rs.next");
		            	 acctSegId= rs.getInt(1);
		            	 int  acctType=acctSegId;
		                System.out.println("acctType="+acctType);

		                	if(acctType==1)// 1== prepaid
		                	{
		                		isPrepaidAcct=true;
		                	}


					 }

		             rs.close();
		             pStmt.close();
		         }
		         System.out.println(" return isPrepaidAcct;="+ isPrepaidAcct);
		     }
		     catch ( java.sql.SQLException ex )
		     {
		         ex.printStackTrace();
//		         err.logErrorToFile("SQL Exception "+ex.getMessage(), GeneralPreCheck.class);

		     }
		     catch ( FxException ex )
		     {
		         ex.printStackTrace();
//		         err.logErrorToFile("FxException Exception "+ex.getMessage(), GeneralPreCheck.class);

		     }
		      catch ( Exception ex )
		     {
		         ex.printStackTrace();

		     }
		      return isPrepaidAcct;

		 }
}

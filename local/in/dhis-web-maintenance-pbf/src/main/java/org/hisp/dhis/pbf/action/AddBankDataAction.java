package org.hisp.dhis.pbf.action;

import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.pbf.api.BankDetails;
import org.hisp.dhis.pbf.api.BankDetailsService;

import com.opensymphony.xwork2.Action;

public class AddBankDataAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private BankDetailsService bankDetailsService;

    public void setBankDetailsService(BankDetailsService bankDetailsService) {
		this.bankDetailsService = bankDetailsService;
	}
    
    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }
    
    private DataSetService dataSetService;
    
    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }
    
    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private String orgUnitUid;

    public void setOrgUnitUid( String orgUnitUid )
    {
        this.orgUnitUid = orgUnitUid;
    }

    private String dataSetId;
    
    public void setDataSetId(String dataSetId) {
		this.dataSetId = dataSetId;
	}
    
    private String accountNumber;
    
    public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}
    
    private String accountName;
    
    public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
    
    private String bank;

	public void setBank(String bank) {
		this.bank = bank;
	}
	
	private String branchName;
	
	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}
	
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

	public String execute()
        throws Exception
    {        
        OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( orgUnitUid );
        
        DataSet dataSet = dataSetService.getDataSet(Integer.parseInt(dataSetId));
        
        BankDetails bankDetails = bankDetailsService.getBankDetails(organisationUnit, dataSet);
       
        if ( bankDetails == null )
        {
        	bankDetails = new BankDetails();
        	bankDetails.setDataSet(dataSet);
        	bankDetails.setOrganisationUnit(organisationUnit);
        	bankDetails.setAccountName(accountName);
        	bankDetails.setAccountNumber(accountNumber);
        	bankDetails.setBank(bank);
        	bankDetails.setBranchName(branchName);
        	bankDetailsService.addBankDetails(bankDetails);
        }
        else
        {
        	bankDetails.setAccountName(accountName);
        	bankDetails.setAccountNumber(accountNumber);
        	bankDetails.setBank(bank);
        	bankDetails.setBranchName(branchName);
            bankDetailsService.updateBankDetails(bankDetails);
        }

        return SUCCESS;
    }
}
package org.hisp.dhis.pbf.action;

import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.pbf.api.BankDetails;
import org.hisp.dhis.pbf.api.BankDetailsService;

import com.opensymphony.xwork2.Action;

public class DeleteBankDataAction
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

    private Integer organisationUnitId;
    
    private Integer dataSetId;

    public void setOrganisationUnitId( Integer organisationUnitId )
    {
        this.organisationUnitId = organisationUnitId;
    }
   
    public void setDataSetId( Integer dataSetId )
    {
        this.dataSetId = dataSetId;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {         
        DataSet dataSet = dataSetService.getDataSet( dataSetId );
        OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( organisationUnitId );

        BankDetails bankDetail = bankDetailsService.getBankDetails(organisationUnit, dataSet);

        bankDetailsService.deleteBankDetails(bankDetail);

        return SUCCESS;
    }
}
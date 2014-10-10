package org.hisp.dhis.pbf.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.pbf.api.BankDetails;
import org.hisp.dhis.pbf.api.BankDetailsService;
import org.hisp.dhis.pbf.api.Lookup;
import org.hisp.dhis.pbf.api.LookupService;

import com.opensymphony.xwork2.Action;

public class LoadBankDetailsAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
   
    private BankDetailsService bankDetailsService;

    public void setBankDetailsService( BankDetailsService bankDetailsService )
    {
        this.bankDetailsService = bankDetailsService;
    }
    
    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    private LookupService lookupService;
    
    public void setLookupService( LookupService lookupService )
    {
        this.lookupService = lookupService;
    }

    private DataSetService dataSetService;
    
    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }

    // -------------------------------------------------------------------------
    // Input / Output
    // -------------------------------------------------------------------------

    private String orgUnitUid;

    public void setOrgUnitUid( String orgUnitUid )
    {
        this.orgUnitUid = orgUnitUid;
    }
    
    private List<DataSet> dataSets = new ArrayList<DataSet>();
    
    public List<DataSet> getDataSets()
    {
        return dataSets;
    }    

	private List<String> banks = new ArrayList<String>();

    public List<String> getBanks()
    {
        return banks;
    }
    
    private List<BankDetails> bankDetailsList = new ArrayList<BankDetails>();
    
    public List<BankDetails> getBankDetailsList()
    {
        return bankDetailsList;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    public String execute()
    {
        OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( orgUnitUid );
        
        bankDetailsList.addAll( bankDetailsService.getBankDetails( organisationUnit ) );
        dataSets.clear();
        List<Lookup> lookups = new ArrayList<Lookup>( lookupService.getAllLookupsByType( Lookup.DS_PBF_TYPE ) );
        List<DataSet> bankDetailDataSets = new ArrayList<DataSet>();
        for( Lookup lookup : lookups )
        {
            Integer dataSetId = Integer.parseInt( lookup.getValue() );
            
            DataSet dataSet = dataSetService.getDataSet( dataSetId );
            dataSets.add(dataSet);
        }
        for(BankDetails bd : bankDetailsList)
        {
        	bankDetailDataSets.add( bd.getDataSet() );
        }
        //dataSets.removeAll(bankDetailDataSets);
        
        lookups = new ArrayList<Lookup>( lookupService.getAllLookupsByType( Lookup.BANK ) );
        for( Lookup lookup : lookups )
        {
            banks.add( lookup.getValue() );
        }
        
        Collections.sort(dataSets);
        /*for(DataSet ds : dataSets)
        {
        	System.out.println(ds.getName());
        }
        System.out.println(dataSets.size());
        */
        return SUCCESS;
    }
}

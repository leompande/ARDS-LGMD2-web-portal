package org.hisp.dhis.pbf.dataentry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.pbf.api.Lookup;
import org.hisp.dhis.pbf.api.LookupService;

import com.opensymphony.xwork2.Action;

public class GetOrganisationUnitAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

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
    // Input/output
    // -------------------------------------------------------------------------
    private String message;
    
    public String getMessage()
    {
        return message;
    }
    
    private String orgUnitId;
    
    public String getOrgUnitId()
    {
        return orgUnitId;
    }

    public void setOrgUnitId( String orgUnitId )
    {
        this.orgUnitId = orgUnitId;
    }

    private List<DataSet> dataSets = new ArrayList<DataSet>();
    
    public List<DataSet> getDataSets()
    {
        return dataSets;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute() throws Exception
    {
        OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( orgUnitId );
        
        dataSets = new ArrayList<DataSet>( organisationUnit.getDataSets() );
        
        List<Lookup> lookups = new ArrayList<Lookup>( lookupService.getAllLookupsByType( Lookup.DS_PBF_TYPE ) );
        
        List<DataSet> pbfDataSets = new ArrayList<DataSet>();
        
        for( Lookup lookup : lookups )
        {
            Integer dataSetId = Integer.parseInt( lookup.getValue() );
            
            DataSet dataSet = dataSetService.getDataSet( dataSetId );
            if( dataSet != null )
            {
                pbfDataSets.add(dataSet);
           
            }
        }
        
        dataSets.retainAll( pbfDataSets );
        Collections.sort(dataSets);
        
        /*
        for( DataSet dataSet : dataSets )
        {
            System.out.println(" dataSet ---" + dataSet.getId() +" -- " + dataSet.getName() );
        }
        */
        
        if ( dataSets.size() > 0 )
        {
            message = organisationUnit.getName();
            return SUCCESS;
            
        }
        else
        {
            message = organisationUnit.getName();
            
            return INPUT;
        }

    }

}

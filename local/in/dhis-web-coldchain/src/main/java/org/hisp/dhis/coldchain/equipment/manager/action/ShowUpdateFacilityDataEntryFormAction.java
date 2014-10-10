package org.hisp.dhis.coldchain.equipment.manager.action;

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 *
 * @version ShowUpdateFacilityDataEntryFormAction.javaOct 20, 2012 2:30:14 PM	
 */

public class ShowUpdateFacilityDataEntryFormAction implements Action
{

    // -------------------------------------------------------------------------
    // Dependency
    // -------------------------------------------------------------------------
    
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
    // Input & Output
    // -------------------------------------------------------------------------
    
    private Integer orgUnitId;
    
    public void setOrgUnitId( Integer orgUnitId )
    {
        this.orgUnitId = orgUnitId;
    }
    
    private OrganisationUnit organisationUnit;

    public OrganisationUnit getOrganisationUnit()
    {
        return organisationUnit;
    }
    
    private DataSet dataSet;
    
    public DataSet getDataSet()
    {
        return dataSet;
    }
    
    private Boolean isDataSetAssign;
    
    public Boolean getIsDataSetAssign()
    {
        return isDataSetAssign;
    }
    
    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------
    
    public String execute() throws Exception
    {
     // OrganisationUnit and its Attribute Information
        organisationUnit = organisationUnitService.getOrganisationUnit( orgUnitId );

        // Data set and sections Information
        List<DataSet> dataSets = new ArrayList<DataSet>( dataSetService.getDataSetByShortName( "FMD" ) ); 
        dataSet = dataSets.get( 0 );
        
        
        List<OrganisationUnit> dataSetSource = new ArrayList<OrganisationUnit>( dataSet.getSources() );
        
        if( dataSetSource != null && dataSetSource.size() > 0 )
        {
            if( dataSetSource.contains( organisationUnit) )
            {
                isDataSetAssign = true;
            }
        }
        
        return SUCCESS;
    }

}



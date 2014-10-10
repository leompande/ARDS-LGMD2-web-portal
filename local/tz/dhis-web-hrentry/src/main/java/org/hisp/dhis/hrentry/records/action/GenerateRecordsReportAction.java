package org.hisp.dhis.hrentry.records.action;

import static org.apache.commons.lang.StringUtils.isNotBlank;
import java.util.Collection;


import org.hisp.dhis.common.Grid;
import org.hisp.dhis.hr.Person;
import org.hisp.dhis.hr.PersonService;
import org.hisp.dhis.hr.HrDataSet;
import org.hisp.dhis.hr.HrDataSetService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.oust.manager.SelectionTreeManager;
import org.hisp.dhis.paging.ActionPagingSupport;

/**
* @author Wilfred Felix Senyoni
 * @version $Id$
 */

public class GenerateRecordsReportAction 
extends ActionPagingSupport<Person>
{
	// -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
        
    private PersonService personService;

    public void setPersonService( PersonService personService )
    {
        this.personService = personService;
    }
    
	private HrDataSetService hrDataSetService;

    public void setHrDataSetService( HrDataSetService hrDataSetService )
    {
        this.hrDataSetService = hrDataSetService;
    }
    
    private SelectionTreeManager selectionTreeManager;

    public void setSelectionTreeManager( SelectionTreeManager selectionTreeManager )
    {
        this.selectionTreeManager = selectionTreeManager;
    }
    
    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------      
    
    private Integer hrDataSetId;
    
    public void setHrDataSetId( Integer hrDataSetId )
    {
        this.hrDataSetId = hrDataSetId;
    }

    private boolean selectedUnitOnly;
    
    public void setSelectedUnitOnly( boolean selectedUnitOnly )
    {
        this.selectedUnitOnly = selectedUnitOnly;
    }
    
    private String key;    

    public void setKey( String key )
    {
        this.key = key;
    }
    
    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------      
    
    public String getKey()
    {
        return key;
    }
    
    public boolean getSelectedUnitOnly()
    {
        return selectedUnitOnly;
    }
    
    private HrDataSet hrDataSet;
    
    public HrDataSet getHrDataSet()
    {
    	return hrDataSet;    	
    }
        
    private OrganisationUnit unit;
    
    public OrganisationUnit getUnit()
    {
    	return unit;
    }
    
    private Grid grid;

    public Grid getGrid()
    {
        return grid;
    }     
    
    // -----------------------------------------------------------------------
    // Action implementation
    // -----------------------------------------------------------------------
    
    @Override
	public String execute()
    	throws Exception
    {        
    unit = selectionTreeManager.getSelectedOrganisationUnit(); 
    
    hrDataSet = hrDataSetService.getHrDataSet( hrDataSetId );
    
    String reportingUnit = "Tange Report for " + unit.getName();
    
    if(!selectedUnitOnly)reportingUnit = reportingUnit + " with lower Level";
    
    if ( unit != null && hrDataSet != null )
    {
    	if ( isNotBlank( key ) )
        {
	    	this.paging = createPaging( personService.getCountPersonByNameDatasetAndOrganisation(hrDataSet, unit, selectedUnitOnly, key ));  
	        
	        Collection<Person> persons = personService.getPersonByNameDatasetAndOrganisationBetween(hrDataSet, unit, selectedUnitOnly, paging.getStartPos(), paging.getPageSize(), key ); 
	        
	        grid = personService.getGrid( persons , reportingUnit , hrDataSet);
        }
    	else
        {
    		this.paging = createPaging( personService.getCountPersonByDatasetAndOrganisation(hrDataSet, unit, selectedUnitOnly )); 
    		
	        Collection<Person> persons = personService.getPersonByDatasetAndOrganisationBetween(hrDataSet, unit, selectedUnitOnly, paging.getStartPos(), paging.getPageSize() ); 
	        
	        grid = personService.getGrid( persons , reportingUnit , hrDataSet);
        }
        return SUCCESS;           	
    }
    
    return ERROR;
}
}

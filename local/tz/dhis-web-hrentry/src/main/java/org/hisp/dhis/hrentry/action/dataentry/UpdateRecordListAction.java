package org.hisp.dhis.hrentry.action.dataentry;

import static org.apache.commons.lang.StringUtils.isNotBlank;

import java.util.Collection;

import org.hisp.dhis.common.Grid;
import org.hisp.dhis.hr.HrDataSet;
import org.hisp.dhis.hr.HrDataSetService;
import org.hisp.dhis.hr.Person;
import org.hisp.dhis.hr.PersonService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.paging.ActionPagingSupport;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.User;

/**
* @author Wilfred Felix Senyoni
 * @version $Id$
 */

public class UpdateRecordListAction 
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
	    
	    private CurrentUserService currentUserService;

	    public void setCurrentUserService( CurrentUserService currentUserService )
	    {
	        this.currentUserService = currentUserService;
	    }
	    
	    private OrganisationUnitService organisationUnitService;

	    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
	    {
	        this.organisationUnitService = organisationUnitService;
	    }
	    
	    
	    // -------------------------------------------------------------------------
	    // Input
	    // -------------------------------------------------------------------------      
	    
	    private Integer hrDataSetId;
	    
	    public void setHrDataSetId( Integer hrDataSetId )
	    {
	        this.hrDataSetId = hrDataSetId;
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
	    
	    private boolean selectedUnitOnly;
	    
	    public boolean getSelectedUnitOnly()
	    {
	        return selectedUnitOnly;
	    }
	    
	    private String reportingUnit;
	    
	    public String getReportingUnit()
	    {
	    	return reportingUnit;
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
	    
	    private Collection<Person> person;
	    
	    public Collection<Person> getPerson()
	    {
	    	return person;
	    }
	    
	    // -----------------------------------------------------------------------
	    // Action implementation
	    // -----------------------------------------------------------------------
	    
	    @Override
		public String execute()
	    	throws Exception
	    {        
	    unit = currentUserService.getCurrentUser().getOrganisationUnit();
	    
	    if ( unit == null)
	    {
	    	//Collection<OrganisationUnit> organisationUnit = organisationUnitService.getRootOrganisationUnits( );
	    	
	    	for ( OrganisationUnit organisationUnit : organisationUnitService.getRootOrganisationUnits( ) )
	    	{
	    		unit = organisationUnit;
	    	}
	    	
	    }
	    
	    hrDataSet = hrDataSetService.getHrDataSet( hrDataSetId );
	    
	    selectedUnitOnly = false;
	    
	    reportingUnit = "Records Available for " + unit.getName();
        
        if(!selectedUnitOnly)reportingUnit = reportingUnit + " with lower Level";
	    
	    if ( unit != null && hrDataSet != null )
	    {
	    	if ( isNotBlank( key ) )
	        {
		    	this.paging = createPaging( personService.getCountPersonByNameDatasetAndOrganisation(hrDataSet, unit, selectedUnitOnly, key ));
		        
		        person = personService.getPersonByNameDatasetAndOrganisationBetween(hrDataSet, unit, selectedUnitOnly, paging.getStartPos(), paging.getPageSize(), key ); 
		        
		        //grid = personService.getGrid( persons , reportingUnit , hrDataSet);
	        }
	    	else
	        {
	    		this.paging = createPaging( personService.getCountPersonByDatasetAndOrganisation(hrDataSet, unit, selectedUnitOnly ));
		        
		        person = personService.getPersonByDatasetAndOrganisationBetween(hrDataSet, unit, selectedUnitOnly, paging.getStartPos(), paging.getPageSize() ); 
		        
		        //grid = personService.getGrid( persons , reportingUnit , hrDataSet);
	        }
	        return SUCCESS;           	
	    }
	    
	    return ERROR;
	}
	}

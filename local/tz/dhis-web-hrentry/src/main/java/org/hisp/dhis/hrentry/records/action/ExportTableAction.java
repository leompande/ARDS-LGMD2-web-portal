package org.hisp.dhis.hrentry.records.action;

import java.util.Collection;

import org.hisp.dhis.common.Grid;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.hr.HrDataSet;
import org.hisp.dhis.hr.HrDataSetService;
import org.hisp.dhis.hr.Person;
import org.hisp.dhis.hr.PersonService;

import com.opensymphony.xwork2.Action;

/**
* @author Wilfred Felix Senyoni
 * @version $Id$
 */

public class ExportTableAction 
implements Action
{
	
	private static final String DEFAULT_TYPE = "html";
	
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
    
    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }
    
    
    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------      
	private Integer unitId;
    
    public void setUnitId( Integer unitId )
    {
        this.unitId = unitId;
    }
    
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
    
    private Integer id;
    
    public void setId( Integer id )
    {
        this.id = id;
    }
    
    private String type;

    public void setType( String type )
    {
        this.type = type;
    }
    
    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------      
    
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
    	OrganisationUnit unit = organisationUnitService.getOrganisationUnit(unitId);
    	
    	HrDataSet hrDataSet = hrDataSetService.getHrDataSet( hrDataSetId );
    	
    	if ( unit != null && hrDataSet != null )
        {
    		Collection<Person> persons = personService.getPersonByDatasetAndOrganisation(hrDataSet, unit, selectedUnitOnly );
    		
    		String reportingUnit = "Tange Report for " + unit.getName();
            
            if(!selectedUnitOnly)reportingUnit = reportingUnit + " with lower Level";
    		
    		grid = personService.getGrid( persons , reportingUnit , hrDataSet);
        }
        
        return type != null ? type : DEFAULT_TYPE;
    }
}

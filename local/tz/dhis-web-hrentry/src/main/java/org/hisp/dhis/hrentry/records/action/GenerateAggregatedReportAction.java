package org.hisp.dhis.hrentry.records.action;

import static org.apache.commons.lang.StringUtils.isNotBlank;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hisp.dhis.common.Grid;
import org.hisp.dhis.common.GridHeader;
import org.hisp.dhis.hr.AggregatedReportService;
import org.hisp.dhis.hr.Attribute;
import org.hisp.dhis.hr.AttributeOptions;
import org.hisp.dhis.hr.AttributeService;
import org.hisp.dhis.hr.HrDataSet;
import org.hisp.dhis.hr.HrDataSetService;
import org.hisp.dhis.hr.Person;
import org.hisp.dhis.hr.PersonService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.oust.manager.SelectionTreeManager;
import org.jfree.chart.JFreeChart;

import com.opensymphony.xwork2.ActionContext;

import com.opensymphony.xwork2.Action;

/**
* @author Wilfred Felix Senyoni
 * @version $Id$
 */

public class GenerateAggregatedReportAction 
	implements Action
{
	// -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
      
	private HrDataSetService hrDataSetService;

    public void setHrDataSetService( HrDataSetService hrDataSetService )
    {
        this.hrDataSetService = hrDataSetService;
    }
    
    private AttributeService attributeService;

    public void setAttributeService( AttributeService attributeService )
    {
        this.attributeService = attributeService;
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
    
    private Integer attributeId;
    
    public void setAttributeId( Integer attributeId )
    {
        this.attributeId = attributeId;
    }

    private boolean selectedUnitOnly;
    
    public void setSelectedUnitOnly( boolean selectedUnitOnly )
    {
        this.selectedUnitOnly = selectedUnitOnly;
    }
    
    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------      
    
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
    
    private Attribute attribute;
    
    public Attribute getAttribute()
    {
    	return attribute;
    }
    
    private String reportingUnit;
    
    public String getReportingUnit()
    {
    	return reportingUnit;
    }
    
       
    
    // -----------------------------------------------------------------------
    // Action implementation
    // -----------------------------------------------------------------------
    
	public String execute()
    {        
		
		unit = selectionTreeManager.getSelectedOrganisationUnit(); 
	    
	    hrDataSet = hrDataSetService.getHrDataSet( hrDataSetId );
	    
	    attribute = attributeService.getAttribute(attributeId);
	    
	    reportingUnit = attribute.getCaption() + " Aggregate Report for " + unit.getName();
	    
	    if(!selectedUnitOnly)reportingUnit = reportingUnit + " with lower Level";    
	    
	    return SUCCESS;           	
    }

}


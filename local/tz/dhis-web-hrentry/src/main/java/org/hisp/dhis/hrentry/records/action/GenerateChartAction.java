package org.hisp.dhis.hrentry.records.action;

import org.hisp.dhis.hr.AggregatedReportService;
import org.hisp.dhis.hr.Attribute;
import org.hisp.dhis.hr.AttributeService;
import org.hisp.dhis.hr.HrDataSet;
import org.hisp.dhis.hr.HrDataSetService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.oust.manager.SelectionTreeManager;
import org.jfree.chart.JFreeChart;

import com.opensymphony.xwork2.Action;

/**
* @author Wilfred Felix Senyoni
 * @version $Id$
 */

public class GenerateChartAction 
implements Action
{
	// -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
        
    private AggregatedReportService aggregatedReportService;

    public void setAggregatedReportService( AggregatedReportService aggregatedReportService )
    {
        this.aggregatedReportService = aggregatedReportService;
    }
    
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
    
    private Integer attributeId;
    
    public void setAttributeId( Integer attributeId )
    {
        this.attributeId = attributeId;
    }
    
    private Integer unitId;
    
    public void setUnitId( Integer unitId )
    {
        this.unitId = unitId;
    }

    private boolean selectedUnitOnly;
    
    public void setSelectedUnitOnly( boolean selectedUnitOnly )
    {
        this.selectedUnitOnly = selectedUnitOnly;
    }
    
    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------      
    
    private JFreeChart chart;

    public JFreeChart getChart()
    {
        return chart;
    }
    
    private int width;

    public int getWidth()
    {
        return width;
    }

    private int height;

    public int getHeight()
    {
        return height;
    }
    
       
    
    // -----------------------------------------------------------------------
    // Action implementation
    // -----------------------------------------------------------------------
    
	public String execute()
    {        
		OrganisationUnit unit = organisationUnitService.getOrganisationUnit(unitId); 
	    
	    HrDataSet hrDataSet = hrDataSetService.getHrDataSet( hrDataSetId );
	    
	    Attribute attribute = attributeService.getAttribute( attributeId );
	 
		width =  700;
        
        height = 500;
        
		chart = aggregatedReportService.getJFreeChart( hrDataSet, attribute, unit, selectedUnitOnly );	    
	    
	    return SUCCESS;           	
    }

}
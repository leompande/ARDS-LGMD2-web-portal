package org.hisp.dhis.ll.action.llagg;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.linelisting.llaggregation.LinelistAggMapService;
import org.hisp.dhis.linelisting.llaggregation.LinelistAggregationMapping;

import com.opensymphony.xwork2.Action;

public class GetLinelistAggExpressionAction implements Action
{ 

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private LinelistAggMapService linelistAggMapService;

    public void setLinelistAggMapService( LinelistAggMapService linelistAggMapService )
    {
        this.linelistAggMapService = linelistAggMapService;
    }

    private DataElementCategoryService categoryService;

    public void setCategoryService( DataElementCategoryService categoryService )
    {
        this.categoryService = categoryService;
    }

    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------
    
    private String aggdeId;
    
    public void setAggdeId( String aggdeId )
    {
        this.aggdeId = aggdeId;
    }
    
    private String expression;
    
    public String getExpression()
    {
        return expression;
    }
        
    private String onchangeCB = "false";
    
    public String getOnchangeCB()
    {
        return onchangeCB;
    }
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {            
        DataElement dataElement = dataElementService.getDataElement(  Integer.parseInt( aggdeId.split( ":" )[0] ) );
        
        DataElementCategoryOptionCombo deCoc = categoryService.getDataElementCategoryOptionCombo( Integer.parseInt( aggdeId.split( ":" )[1] ) );
        
        LinelistAggregationMapping linelistAggMapping = linelistAggMapService.getLinelistAggregationMappingByOptionCombo( dataElement, deCoc );
        
        if( linelistAggMapping != null )
        {
            expression = linelistAggMapping.getExpression();
            
            if( linelistAggMapping.isOnchange() )
            {
                onchangeCB = "true";
            }
        }
        else
        {
            expression = " ";
        }
    
        
        return SUCCESS;
    }
    
}

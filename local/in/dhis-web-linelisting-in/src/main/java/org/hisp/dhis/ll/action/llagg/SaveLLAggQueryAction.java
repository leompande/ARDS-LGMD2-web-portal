package org.hisp.dhis.ll.action.llagg;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.linelisting.llaggregation.LinelistAggMapService;
import org.hisp.dhis.linelisting.llaggregation.LinelistAggregationMapping;

import com.opensymphony.xwork2.Action;

public class SaveLLAggQueryAction implements Action
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
    // Input
    // -------------------------------------------------------------------------

    private String aggde;

    public void setAggde( String aggde )
    {
        this.aggde = aggde;
    }

    private String expression;

    public void setExpression( String expression )
    {
        this.expression = expression;
    }

    private String onchangeCB;
    
    public void setOnchangeCB( String onchangeCB )
    {
        this.onchangeCB = onchangeCB;
    }


    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------
    
    private String statusMessage;

    public String getStatusMessage()
    {
        return statusMessage;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {

        boolean b = false;
        
        if( onchangeCB != null )
        {
            b = true;
        }
        
       DataElement de = dataElementService.getDataElement( Integer.parseInt( aggde.split( ":" )[0] ) );

        DataElementCategoryOptionCombo optCombo = categoryService.getDataElementCategoryOptionCombo( Integer
            .parseInt( aggde.split( ":" )[1] ) );

        LinelistAggregationMapping llAggMapping = linelistAggMapService.getLinelistAggregationMappingByOptionCombo( de,
            optCombo );

        if ( llAggMapping == null )
        {
               
            llAggMapping = new LinelistAggregationMapping( de, optCombo, expression, b );

            llAggMapping = new LinelistAggregationMapping( de, optCombo, expression );

            linelistAggMapService.addLineListAggregationMapping( llAggMapping );

            statusMessage = "Expression is Added";
        }
        else
        {
            llAggMapping.setExpression( expression );
            
            llAggMapping.setOnchange( b );

            linelistAggMapService.updateLinelistAggregationMapping( llAggMapping );

            statusMessage = "Expression is Updated";
        }
        
        System.out.println("status : "+ statusMessage);

        return SUCCESS;
    }

}

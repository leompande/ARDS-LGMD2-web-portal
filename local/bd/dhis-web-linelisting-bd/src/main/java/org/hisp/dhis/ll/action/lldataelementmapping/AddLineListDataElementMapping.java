package org.hisp.dhis.ll.action.lldataelementmapping;

import org.hisp.dhis.expression.Expression;
import org.hisp.dhis.expression.ExpressionService;
import org.hisp.dhis.linelisting.linelistdataelementmapping.LineListDataElementMapping;
import org.hisp.dhis.linelisting.linelistdataelementmapping.LineListDataElementMappingService;

import com.opensymphony.xwork2.Action;

public class AddLineListDataElementMapping
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    /*
     * private LineListService lineListService;
     * 
     * public void setLineListService( LineListService lineListService ) {
     * this.lineListService = lineListService; }
     */
    private ExpressionService expressionService;

    public void setExpressionService( ExpressionService expressionService )
    {
        this.expressionService = expressionService;
    }

    private LineListDataElementMappingService lineListDataElementMappingService;

    public void setLineListDataElementMappingService(
        LineListDataElementMappingService lineListDataElementMappingService )
    {
        this.lineListDataElementMappingService = lineListDataElementMappingService;
    }

    /*
     * private DataElementService dataElementService;
     * 
     * public void setDataElementService( DataElementService dataElementService
     * ) { this.dataElementService = dataElementService; }
     */
    // -------------------------------------------------------------------------
    // Input & output
    // -------------------------------------------------------------------------
    private String description;

    public void setDescription( String description )
    {
        this.description = description;
    }

    private String leftSideDescription;

    public void setLeftSideDescription( String leftSideDescription )
    {
        this.leftSideDescription = leftSideDescription;
    }

    private String leftSideDataElements;

    public void setLeftSideDataElements( String leftSideDataElements )
    {
        this.leftSideDataElements = leftSideDataElements;
    }

    private String leftSideExpression;

    public void setLeftSideExpression( String leftSideExpression )
    {
        this.leftSideExpression = leftSideExpression;
    }

    private String rightSideExpression;

    public void setRightSideExpression( String rightSideExpression )
    {
        this.rightSideExpression = rightSideExpression;
    }

    // -------------------------------------------------------------------------
    // Action
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {

        Expression dataElementAndCombo = new Expression();

        dataElementAndCombo.setExpression( leftSideExpression );
        dataElementAndCombo.setDescription( leftSideDescription );
        dataElementAndCombo.setDataElementsInExpression( expressionService
            .getDataElementsInExpression( leftSideExpression ) );

        expressionService.addExpression( dataElementAndCombo );

        LineListDataElementMapping lineListDataElementMapping = new LineListDataElementMapping();

        lineListDataElementMapping.setDataElementExpression( leftSideExpression );
        lineListDataElementMapping.setDescription( description );
        lineListDataElementMapping.setLineListExpression( rightSideExpression );

        lineListDataElementMappingService.addLineListDataElementMapping( lineListDataElementMapping );

        return SUCCESS;
    }

}

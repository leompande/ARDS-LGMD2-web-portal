package org.hisp.dhis.linelisting.llaggregation;

import java.io.Serializable;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;

@SuppressWarnings("serial")
public class LinelistAggregationMapping
    implements Serializable
{
    private DataElement dataElement;

    private DataElementCategoryOptionCombo optionCombo;

    private String expression;

    private boolean onchange = false;
    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public LinelistAggregationMapping()
    {

    }

    public LinelistAggregationMapping( DataElement dataElement, DataElementCategoryOptionCombo optionCombo,
        String expression )
    {
        this.dataElement = dataElement;

        this.optionCombo = optionCombo;

        this.expression = expression;
    }

    public LinelistAggregationMapping( DataElement dataElement, DataElementCategoryOptionCombo optionCombo,
        String expression, boolean onchange )
    {
        this.dataElement = dataElement;

        this.optionCombo = optionCombo;

        this.expression = expression;
        
        this.onchange = onchange;
    }
    
    // -------------------------------------------------------------------------
    // hashCode and equals
    // -------------------------------------------------------------------------

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;

        result = result * prime + optionCombo.hashCode();
        result = result * prime + dataElement.hashCode();

        return result;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( o == null )
        {
            return false;
        }

        if ( !(o instanceof LinelistAggregationMapping) )
        {
            return false;
        }

        final LinelistAggregationMapping other = (LinelistAggregationMapping) o;

        return dataElement.equals( other.getDataElement() ) && optionCombo.equals( other.getOptionCombo() );
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    public DataElement getDataElement()
    {
        return dataElement;
    }

    public void setDataElement( DataElement dataElement )
    {
        this.dataElement = dataElement;
    }

    public String getExpression()
    {
        return expression;
    }

    public void setExpression( String expression )
    {
        this.expression = expression;
    }

    public DataElementCategoryOptionCombo getOptionCombo()
    {
        return optionCombo;
    }

    public void setOptionCombo( DataElementCategoryOptionCombo optionCombo )
    {
        this.optionCombo = optionCombo;
    }

    public boolean isOnchange()
    {
        return onchange;
    }

    public void setOnchange( boolean onchange )
    {
        this.onchange = onchange;
    }

    
}

package org.hisp.dhis.hr;

/**
 * @author Wilfred Felix Senyoni
 * @version $Id$
 */

public class AggregateOperands 
	{
	
	private String attributeOptionsValue;
	
	private int total;
	
	// -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public AggregateOperands()
    {
    }
    
    public AggregateOperands(String attributeOptionsValue, int total)
    {
    	this.attributeOptionsValue = attributeOptionsValue;
    	this.total = total;
    	
    }
    
 // -------------------------------------------------------------------------
    // hashCode, equals and toString
    // -------------------------------------------------------------------------
   
        
    // -------------------------------------------------------------------------
    // Getters and setters
    // ----------------------------------------------------------------------    

    public int gettotal()
    {
        return total;
    }

    public void setTotal( int total )
    {
        this.total = total;
    }

    public String getAttributeOptionsValue()
    {
        return attributeOptionsValue;
    }

    public void setAttributeOptionsValue( String attributeOptionsValue )
    {
        this.attributeOptionsValue = attributeOptionsValue;
    } 
}

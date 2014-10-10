package org.hisp.dhis.validationrule.util;


/**
 * @author Abyot Asalefew
 * @version $Id: Operand.java 5730 2008-09-20 14:32:22Z brajesh $
 */
public class Operand {
	
	public static final String SEPARATOR = ".";
	
	private String id;
	
	private int dataElementId;
	
	private int optionComboId;	
	
	private String operandName;
	
	public Operand( int dataElementId, int optionComboId, String operandName )
	{
		this.id = dataElementId + SEPARATOR + optionComboId;
		
		this.operandName = operandName;
	}	
	
	public String getId()
	{
	    return id;
	}

	public void setId( String id )
	{
	    this.id = id;
	}
	
	public String getOperandName()
	{
	    return operandName;
	}

	public void setOperandName( String operandName )
	{
	    this.operandName = operandName;
	}
	
	public int getDataElementId()
    {
        return dataElementId;
    }

    public void setDataElementId( int dataElementId )
    {
        this.dataElementId = dataElementId;
    }

    public int getOptionComboId()
    {
        return optionComboId;
    }

    public void setOptionComboId( int optionComboId )
    {
        this.optionComboId = optionComboId;
    }
	
	 public boolean equals( Object object )
	 {
		 if( this == object )
	        {
	            return true;
	        }
	        
	        if( ( object == null) || ( object.getClass() != this.getClass() ) )
	        {
	            return false;
	        }
	        
	        Operand operand = (Operand) object;
	        
	        return ( id == operand.id && dataElementId == operand.dataElementId && 
	            optionComboId == operand.optionComboId );
		 
	 }

}


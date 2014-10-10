package org.hisp.dhis.hr.action.attribute;


import java.util.ArrayList;
import java.util.Collection;

import org.hisp.dhis.hr.Attribute;
import org.hisp.dhis.hr.AttributeService;
import org.hisp.dhis.hr.InputType;
import org.hisp.dhis.hr.InputTypeService;
import org.hisp.dhis.hr.DataType;
import org.hisp.dhis.hr.DataTypeService;

import com.opensymphony.xwork2.Action;

public class GetAttributeAction 
implements Action
{
	// -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private AttributeService attributeService;

    public void setAttributeService( AttributeService attributeService )
    {
        this.attributeService = attributeService;
    }
    
    private InputTypeService inputTypeService;

    public void setInputTypeService( InputTypeService inputTypeService )
    {
        this.inputTypeService = inputTypeService;
    }
    
    private DataTypeService dataTypeService;

    public void setDataTypeService( DataTypeService dataTypeService )
    {
        this.dataTypeService = dataTypeService;
    }

    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------

    private int id;

    public void setId( int id )
    {
        this.id = id;
    }

    private Attribute attributes;

    public Attribute getAttribute()
    {
        return attributes;
    }
        
    private Collection<InputType> inputTypes;

    public Collection<InputType> getInputType()
    {
        return inputTypes;
    }
    
    private Collection<DataType> dataTypes;

    public Collection<DataType> getDataType()
    {
        return dataTypes;
    }
    
    // -------------------------------------------------------------------------
    // Action
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        attributes = attributeService.getAttribute( id );
        
        inputTypes = new ArrayList<InputType> ( inputTypeService.getAllInputType() );
        
        dataTypes = new ArrayList<DataType> ( dataTypeService.getAllDataType() );

        return SUCCESS;
    }

}

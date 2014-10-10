package org.hisp.dhis.hr.action.attribute;

import java.util.ArrayList;
import java.util.Collection;

import org.hisp.dhis.hr.DataType;
import org.hisp.dhis.hr.DataTypeService;
import org.hisp.dhis.hr.InputType;
import org.hisp.dhis.hr.InputTypeService;

import com.opensymphony.xwork2.Action;

public class ShowAddAttributeFormAction 
implements Action
{
	
	// -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
   
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
   
    private Collection<InputType> inputTypes;

    public Collection<InputType> getInputType()
    {
        return inputTypes;
    }
    
    private Integer inputTypeId;

    public Integer getInputTypeId()
    {
        return inputTypeId;
    }

    public void setInputTypeId( Integer inputTypeId )
    {
        this.inputTypeId = inputTypeId;
    }
    
    private Collection<DataType> dataTypes;

    public Collection<DataType> getDataType()
    {
        return dataTypes;
    }

    private Integer dataTypeId;

    public Integer getDataTypeId()
    {
        return dataTypeId;
    }

    public void setDataTypeId( Integer dataTypeId )
    {
        this.dataTypeId = dataTypeId;
    }
    
    // -------------------------------------------------------------------------
    // Action
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        
        inputTypes = new ArrayList<InputType> ( inputTypeService.getAllInputType() );
        
        dataTypes = new ArrayList<DataType> ( dataTypeService.getAllDataType() );

        return SUCCESS;
    }
}

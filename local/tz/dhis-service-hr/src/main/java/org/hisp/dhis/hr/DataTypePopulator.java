package org.hisp.dhis.hr;

import org.hisp.dhis.hr.DataTypeService;
import org.hisp.dhis.system.startup.AbstractStartupRoutine;

public class DataTypePopulator 
extends AbstractStartupRoutine
{
	public static final String DATA_TYPE_STRING = "string";
    public static final String DATA_TYPE_INTERGER = "integer";
    public static final String DATA_TYPE_DOUBLE = "double";
    public static final String DATA_TYPE_DATE = "date";
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DataTypeService dataTypeService;
    
    public void setDataTypeService( DataTypeService dataTypeService )
    {
        this.dataTypeService = dataTypeService;
    }
    
 // -------------------------------------------------------------------------
    // AbstractStartupRoutine implementation
    // -------------------------------------------------------------------------

    public void execute()
        throws Exception
    {
    	DataType str = dataTypeService.getDataTypeByName( DATA_TYPE_STRING );
        
        if ( str == null )
        {
        	str = new DataType();
        	str.setName( "string" );
            
        	dataTypeService.saveDataType( str );
        }
        
        DataType integer = dataTypeService.getDataTypeByName( DATA_TYPE_INTERGER );
        
        if ( integer == null )
        {
        	integer = new DataType();
        	integer.setName( "integer" );
            
        	dataTypeService.saveDataType( integer );
        }
        
        DataType dbl = dataTypeService.getDataTypeByName( DATA_TYPE_DOUBLE );
        
        if ( dbl == null )
        {
        	dbl = new DataType();
        	dbl.setName( "double" );
            
        	dataTypeService.saveDataType( dbl );
        }
        
        DataType date = dataTypeService.getDataTypeByName( DATA_TYPE_DATE );
        
        if ( date == null )
        {
        	date = new DataType();
        	date.setName( "date" );
            
        	dataTypeService.saveDataType( date );
        }
    }
}



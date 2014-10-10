package org.hisp.dhis.hr;

import org.hisp.dhis.hr.InputTypeService;
import org.hisp.dhis.hr.DataTypeService;
import org.hisp.dhis.hr.AttributeService;
import org.hisp.dhis.hr.Attribute;
import org.hisp.dhis.system.startup.AbstractStartupRoutine;

public class AttributePopulator 
extends AbstractStartupRoutine
{
	public static final String ATTRIBUTE_FIRSTNAME = "firstname";
    public static final String ATTRIBUTE_MIDDLENAME = "middlename";
    public static final String ATTRIBUTE_LASTNAME = "lastname";
    public static final String ATTRIBUTE_BIRTHDATE = "birthdate";
    public static final String ATTRIBUTE_GENDER = "gender";
    public static final String ATTRIBUTE_NATIONALITY = "nationality";
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private AttributeService attributeService;
    
    public void setAttributeService( AttributeService attributeService )
    {
        this.attributeService = attributeService;
    }
    
    private DataTypeService dataTypeService;
    
    public void setDataTypeService( DataTypeService dataTypeService )
    {
        this.dataTypeService = dataTypeService;
    }
    
    private InputTypeService inputTypeService;
    
    public void setInputTypeService( InputTypeService inputTypeService )
    {
        this.inputTypeService = inputTypeService;
    }
    
    // -------------------------------------------------------------------------
    // AbstractStartupRoutine implementation
    // -------------------------------------------------------------------------

    public void execute()
        throws Exception
    {	
    	
    	Attribute firstname = attributeService.getAttributeByName( ATTRIBUTE_FIRSTNAME );
        
        if ( firstname == null )
        {
        	firstname = new Attribute();
        	firstname.setName( "firstname" );
        	firstname.setCaption("Firstname");
        	firstname.setDescription("The firstname of the Person");
        	firstname.setCompulsory(true);
        	firstname.setHistory(false);
        	firstname.setIsUnique(false);
        	firstname.setDataType(dataTypeService.getDataTypeByName("string"));
        	firstname.setInputType(inputTypeService.getInputTypeByName("text"));        	
            
        	attributeService.saveAttribute( firstname );
        }
        
        Attribute middlename = attributeService.getAttributeByName( ATTRIBUTE_MIDDLENAME );
        
        if ( middlename == null )
        {
        	middlename = new Attribute();
        	middlename.setName( "middlename" );
        	middlename.setCaption("Middlename");
        	middlename.setDescription("The middlename of the Person");
        	middlename.setCompulsory(false);
        	middlename.setHistory(false);
        	middlename.setIsUnique(false);
        	middlename.setDataType(dataTypeService.getDataTypeByName("string"));
        	middlename.setInputType(inputTypeService.getInputTypeByName("text"));        	
            
        	attributeService.saveAttribute( middlename );
        }
        
        Attribute lastname = attributeService.getAttributeByName( ATTRIBUTE_LASTNAME );
        
        if ( lastname == null )
        {
        	lastname = new Attribute();
        	lastname.setName( "lastname" );
        	lastname.setCaption("Lastname");
        	lastname.setDescription("The Lastname of the Person");
        	lastname.setCompulsory(true);
        	lastname.setHistory(true);
        	lastname.setIsUnique(false);
        	lastname.setDataType(dataTypeService.getDataTypeByName("string"));
        	lastname.setInputType(inputTypeService.getInputTypeByName("text"));        	
            
        	attributeService.saveAttribute( lastname );
        }
        
        Attribute birthdate = attributeService.getAttributeByName( ATTRIBUTE_BIRTHDATE );
        
        if ( birthdate == null )
        {
        	birthdate = new Attribute();
        	birthdate.setName( "birthdate" );
        	birthdate.setCaption("Birthdate");
        	birthdate.setDescription("The Date of Birth of the Person");
        	birthdate.setCompulsory(true);
        	birthdate.setHistory(false);
        	birthdate.setIsUnique(false);
        	birthdate.setDataType(dataTypeService.getDataTypeByName("date"));
        	birthdate.setInputType(inputTypeService.getInputTypeByName("date"));        	
            
        	attributeService.saveAttribute( birthdate );
        }
        
        Attribute gender = attributeService.getAttributeByName( ATTRIBUTE_GENDER );
        
        if ( gender == null )
        {
        	gender = new Attribute();
        	gender.setName( "gender" );
        	gender.setCaption("Gender");
        	gender.setDescription("The Gender of the Person");
        	gender.setCompulsory(true);
        	gender.setHistory(false);
        	gender.setIsUnique(false);
        	gender.setDataType(dataTypeService.getDataTypeByName("string"));
        	gender.setInputType(inputTypeService.getInputTypeByName("combo"));        	
            
        	attributeService.saveAttribute( gender );
        }
        
        Attribute nationality = attributeService.getAttributeByName( ATTRIBUTE_NATIONALITY );
        
        if ( nationality == null )
        {
        	nationality = new Attribute();
        	nationality.setName( "nationality" );
        	nationality.setCaption("Nationality");
        	nationality.setDescription("The Nationality of the Person");
        	nationality.setCompulsory(true);
        	nationality.setHistory(false);
        	nationality.setIsUnique(false);
        	nationality.setDataType(dataTypeService.getDataTypeByName("string"));
        	nationality.setInputType(inputTypeService.getInputTypeByName("combo"));        	
            
        	attributeService.saveAttribute( nationality );
        }
        
    }
}



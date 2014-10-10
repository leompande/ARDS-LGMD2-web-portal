package org.hisp.dhis.hr.action.attribute;

import org.hisp.dhis.hr.Attribute;
import org.hisp.dhis.hr.AttributeService;
import org.hisp.dhis.hr.DataType;
import org.hisp.dhis.hr.DataTypeService;
import org.hisp.dhis.hr.InputType;
import org.hisp.dhis.hr.InputTypeService;

import com.opensymphony.xwork2.Action;

public class AddAttributeAction implements Action
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

   private String nameField;

   public void setNameField( String nameField )
   {
       this.nameField = nameField;
   }
   
   private String caption;

   public void setCaption( String caption )
   {
       this.caption = caption;
   }
   
   private String description;

   public void setDescription( String description )
   {
       this.description = description;
   }

   private int inputTypeId;

   public void setInputTypeId( int inputTypeId )
   {
       this.inputTypeId = inputTypeId;
   }   
   
   private int dataTypeId;

   public void setDataTypeId( int dataTypeId )
   {
       this.dataTypeId = dataTypeId;
   }   
   
   private boolean compulsory;
   
   public void setCompulsory( boolean compulsory )
   {
       this.compulsory = compulsory;
   }
   
   private boolean unique;
   
   public void setUnique( boolean unique )
   {
       this.unique = unique;
   }
   
   private boolean history;
   
   public void setHistory( boolean history )
   {
       this.history = history;
   }
   

   // -------------------------------------------------------------------------
   // Action implementation
   // -------------------------------------------------------------------------

   public String execute()
       throws Exception
   {
	   Attribute attribute = new Attribute();       

       attribute.setName( nameField );
       attribute.setCaption( caption );
       attribute.setDescription( description );
       attribute.setHistory( history );
       attribute.setIsUnique( unique );
       attribute.setCompulsory( compulsory );
       
       DataType dataType = new DataType();
       dataType = dataTypeService.getDataType(dataTypeId);
       
       InputType inputType = new InputType();
       inputType = inputTypeService.getInputType(inputTypeId);
       
       attribute.setDataType(dataType);
       attribute.setInputType(inputType);
       
       attributeService.saveAttribute( attribute );
       
       
       return SUCCESS;
   }
}
package org.hisp.dhis.hr;

import org.hisp.dhis.hr.InputTypeService;
import org.hisp.dhis.system.startup.AbstractStartupRoutine;

public class InputTypePopulator 
extends AbstractStartupRoutine{

	public static final String INPUT_TYPE_TEXT = "text";
    public static final String INPUT_TYPE_COMBO = "combo";
    public static final String INPUT_TYPE_CHECK = "check";
    public static final String INPUT_TYPE_RADIO = "radio";
    public static final String INPUT_TYPE_DATE = "date";
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

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
    	InputType text = inputTypeService.getInputTypeByName( INPUT_TYPE_TEXT );
        
        if ( text == null )
        {
        	text = new InputType();
        	text.setName( "text" );
            
        	inputTypeService.saveInputType( text );
        }
        
        InputType combo = inputTypeService.getInputTypeByName( INPUT_TYPE_COMBO );
        
        if ( combo == null )
        {
        	combo = new InputType();
        	combo.setName( "combo" );
            
        	inputTypeService.saveInputType( combo );
        }
        
        InputType check = inputTypeService.getInputTypeByName( INPUT_TYPE_CHECK );
        
        if ( check == null )
        {
        	check = new InputType();
        	check.setName( "check" );
            
        	inputTypeService.saveInputType( check );
        }
        
        InputType radio = inputTypeService.getInputTypeByName( INPUT_TYPE_RADIO );
        
        if ( radio == null )
        {
        	radio = new InputType();
        	radio.setName( "radio" );
            
        	inputTypeService.saveInputType( radio );
        }
        
        InputType date = inputTypeService.getInputTypeByName( INPUT_TYPE_DATE );
        
        if ( date == null )
        {
        	date = new InputType();
        	date.setName( "date" );
            
        	inputTypeService.saveInputType( date );
        }
    }
}




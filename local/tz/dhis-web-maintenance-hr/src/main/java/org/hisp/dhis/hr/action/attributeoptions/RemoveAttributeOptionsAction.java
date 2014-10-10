package org.hisp.dhis.hr.action.attributeoptions;

import org.hisp.dhis.hr.AttributeOptions;
import org.hisp.dhis.hr.AttributeOptionsService;
import com.opensymphony.xwork2.Action;

public class RemoveAttributeOptionsAction 
implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private AttributeOptionsService attributeOptionsService;

    public void setAttributeOptionsService( AttributeOptionsService attributeOptionsService )
    {
        this.attributeOptionsService = attributeOptionsService;
    }

    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------

    private int id;

    public void setId( int id )
    {
        this.id = id;
    }
    
    private int attributeId;

    public void setAttributeId( int attributeId )
    {
        this.attributeId = attributeId;
    }

    public String execute()
        throws Exception
    {
        AttributeOptions attributeOptions = attributeOptionsService.getAttributeOptions( id );

        attributeOptionsService.deleteAttributeOptions( attributeOptions );

        return SUCCESS;
    }
}


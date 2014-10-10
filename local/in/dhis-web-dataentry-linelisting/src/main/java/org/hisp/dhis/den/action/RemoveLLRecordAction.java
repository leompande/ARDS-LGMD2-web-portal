package org.hisp.dhis.den.action;

import org.hisp.dhis.den.api.LLDataValueService;
import com.opensymphony.xwork2.Action;

public class RemoveLLRecordAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private LLDataValueService lldataValueService;
    
    public void setLldataValueService( LLDataValueService lldataValueService )
    {
        this.lldataValueService = lldataValueService;
    } 
    
    // -------------------------------------------------------------------------
    // Getters & setters
    // -------------------------------------------------------------------------
 
    private Integer recordId;
    
    public void setRecordId( Integer recordId )
    {
        this.recordId = recordId;
    }
        
    // -------------------------------------------------------------------------
    // Action
    // -------------------------------------------------------------------------

    public String execute() throws Exception
    {
        System.out.println("Inside remove ll record action");
        
        lldataValueService.removeLLRecord( recordId );

        return SUCCESS;
    }

}

package org.hisp.dhis.reports.action;

import org.hisp.dhis.mail.MailService;

import com.opensymphony.xwork2.Action;

public class SendMailAction implements Action
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private MailService mailService;
    
    public void setMailService( MailService mailService )
    {
        this.mailService = mailService;
    }
    
    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------
    
    private String resultMessage;
    
    public String getResultMessage()
    {
        return resultMessage;
    }
    
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        
        //resultMessage = mailService.sendEmail();
        
        resultMessage = mailService.sendEmailWithAttachment();
        
        return SUCCESS;
    }
    
}

package org.hisp.dhis.mobile.action;

import java.util.Date;

import org.hisp.dhis.mobile.api.MobileImportService;

import com.opensymphony.xwork2.Action;

public class TestSMSDataAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private MobileImportService mobileImportService;

    public void setMobileImportService( MobileImportService mobileImportService )
    {
        this.mobileImportService = mobileImportService;
    }
    
    // -------------------------------------------------------------------------
    // Parameters
    // -------------------------------------------------------------------------
    
    private String smsdata;
    
    public void setSmsdata( String smsdata )
    {
        this.smsdata = smsdata;
    }

    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------

    public String execute() throws Exception
    {
    	if (smsdata.startsWith("SN")){
        mobileImportService.registerPatientData( smsdata, "+9999888811", new Date() );
    	}
    	else{
    		mobileImportService.registerDataByUID(smsdata,  "+9999888811", new Date());
    	}
    	
    	
        return SUCCESS;
    }

}

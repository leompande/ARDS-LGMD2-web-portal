package org.hisp.dhis.scheduler;

import java.util.Calendar;
import java.util.Date;

import org.hisp.dhis.mail.MailService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class HelloJob implements Job
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private MailService mailService = new MailService();
    
    public void setMailService( MailService mailService )
    {
        this.mailService = mailService;
    }

    
    public void execute( JobExecutionContext arg0 ) throws JobExecutionException
    {
        System.out.println("Hello World Quartz Scheduler: " + new Date());
        Calendar cal = Calendar.getInstance();
        
        cal.setTime( new Date() );
        
        /*
        if( cal.get( Calendar.HOUR_OF_DAY ) == 16 && cal.get( Calendar.MINUTE ) == 30 )
        {
            mailService.sendEmail();
            System.out.println("Message may sent");
        }
        */
    }
}

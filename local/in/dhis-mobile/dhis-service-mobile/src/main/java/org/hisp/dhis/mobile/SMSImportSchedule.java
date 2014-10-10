package org.hisp.dhis.mobile;

import org.hisp.dhis.system.startup.AbstractStartupRoutine;

public class SMSImportSchedule extends AbstractStartupRoutine
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    
    @Override
    public void execute() throws Exception
    {
        System.out.println("*************Inside SMSImportShedule execute method");
        //specify your sceduler task details
        /*JobDetail job = new JobDetail();
        job.setName("SMSImport");
        job.setJobClass(SMSImportJob.class);
        

        //configure the scheduler time
        SimpleTrigger trigger = new SimpleTrigger();
        trigger.setName("SMSImportScheduler");
        trigger.setStartTime(new Date(System.currentTimeMillis() + 1000));
        trigger.setRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY);
        trigger.setRepeatInterval(30000);

        //schedule it
        Scheduler scheduler = new StdSchedulerFactory().getScheduler();
        
        scheduler.start();
        
        scheduler.scheduleJob(job, trigger);*/

        

    }
    
    //public SMSImportSchedule()throws Exception
    //{
        /*
        //specify your sceduler task details
        JobDetail job = new JobDetail();
        job.setName("SMSImport");
        job.setJobClass(SMSImportJob.class);

        //configure the scheduler time
        SimpleTrigger trigger = new SimpleTrigger();
        trigger.setName("SMSImportScheduler");
        trigger.setStartTime(new Date(System.currentTimeMillis() + 1000));
        trigger.setRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY);
        trigger.setRepeatInterval(30000);

        //schedule it
        Scheduler scheduler = new StdSchedulerFactory().getScheduler();
        scheduler.start();

        scheduler.scheduleJob(job, trigger);
        */
        
        /*
        SchedulerFactory sf = new StdSchedulerFactory();
        Scheduler sched = sf.getScheduler();
        
        JobDetail jd = new JobDetail("SMSImportJob","SMSImportGroup",SMSImportJob.class);
        
        CronTrigger ct=new CronTrigger("SMSImportTrigger","SMSImportTringerGroup","0 0 13 * * ?");
        sched.scheduleJob(jd,ct);
        //CronTrigger ct = new CronTrigger("SMSImportTrigger","SMSImportGroup", "SMSImportJob", "SMSImportGroup", "0 0 12 * * ?");
        //sched.addJob(jd, true);

        sched.start();
        */
        
    //}
}

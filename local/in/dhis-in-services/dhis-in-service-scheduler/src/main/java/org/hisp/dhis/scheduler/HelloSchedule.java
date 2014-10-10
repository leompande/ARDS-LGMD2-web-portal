package org.hisp.dhis.scheduler;

import java.util.Date;

import org.hisp.dhis.system.startup.AbstractStartupRoutine;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SimpleTrigger;
import org.quartz.impl.StdSchedulerFactory;

public class HelloSchedule extends AbstractStartupRoutine
{

    public void execute() throws Exception
    {
 
    }
    
    public HelloSchedule()throws Exception
    {
        //specify your sceduler task details
        JobDetail job = new JobDetail();
        job.setName("HelloJob");
        job.setJobClass(HelloJob.class);

        //configure the scheduler time
        SimpleTrigger trigger = new SimpleTrigger();
        trigger.setName("HelloJobTesting");
        trigger.setStartTime(new Date(System.currentTimeMillis() + 1000));
        trigger.setRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY);
        trigger.setRepeatInterval(30000);

        //schedule it
        Scheduler scheduler = new StdSchedulerFactory().getScheduler();
        scheduler.start();

        scheduler.scheduleJob(job, trigger);
    }

}

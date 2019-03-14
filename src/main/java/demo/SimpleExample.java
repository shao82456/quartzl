package demo;

import job.ShellJob;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.quartz.Trigger;
import java.util.Date;

import static org.quartz.DateBuilder.evenMinuteDate;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

public class SimpleExample {
    public void run() throws Exception{
        Logger log = LoggerFactory.getLogger(SimpleExample.class);

        log.info("------- Initializing ----------------------");

        // First we must get a reference to a scheduler
        SchedulerFactory sf = new StdSchedulerFactory();
        Scheduler sched = sf.getScheduler();

        sched.clear();

        log.info("------- Initialization Complete -----------");

        // computer a time that is on the next round minute
        Date runTime = evenMinuteDate(new Date());

        log.info("------- Scheduling Job  -------------------");

        // define the job and tie it to our HelloJob class
        JobDetail job = newJob(HelloJob.class).withIdentity("job1", "group1").build();
        JobDetail job2=newJob(WorldJob.class).usingJobData("message","hooooo ").withIdentity("job2","group1").build();
        JobDetail job3=newJob(ShellJob.class).withIdentity("job1", "group1")
                .usingJobData("scriptname","sh/a.sh")
                .build();

        // Trigger the job to run on the next round minute

        Trigger tr = newTrigger()
                .withIdentity("trigger", "group1")
                .withSchedule(simpleSchedule()
                        .withIntervalInSeconds(10)
                        .repeatForever())
                .startNow()
                .build();

        Trigger trigger2 = newTrigger()
                .withIdentity("trigger2", "group1")
                .startNow()
                .withSchedule(simpleSchedule()
                        .withIntervalInSeconds(60)
                        .repeatForever())
                .build();

        // Tell quartz to schedule the job using our trigger
        sched.scheduleJob(job3, trigger2);
        log.info(job.getKey() + " will run at: " + runTime);

        // Start up the scheduler (nothing can actually run until the
        // scheduler has been started)
        sched.start();

        log.info("------- Started Scheduler -----------------");

        // wait long enough so that the scheduler as an opportunity to
        // run the job!
        log.info("------- Waiting 65 seconds... -------------");
        try {
            // wait 65 seconds to show job
            Thread.sleep(1 * 1000L);
            // executing...
        } catch (Exception e) {
            //
        }
    }
    public static void main(String[] args) throws Exception {

        SimpleExample example = new SimpleExample();
        example.run();
    }
}

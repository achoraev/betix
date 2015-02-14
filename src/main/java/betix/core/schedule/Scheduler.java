package betix.core.schedule;

import betix.core.config.ConfigKey;
import betix.core.config.Configuration;
import betix.core.logger.Logger;
import betix.core.logger.LoggerFactory;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

public class Scheduler {

    private static final Logger logger = LoggerFactory.getLogger(Scheduler.class);
    protected static final Configuration config = Configuration.getDefaultConfig();
    public static org.quartz.Scheduler scheduler = null;

    static {
        try {
            scheduler = new StdSchedulerFactory().getScheduler();

            //Create JobDetail object specifying which Job you want to execute
            JobDetail jobDetail = JobBuilder.newJob(ScheduledJob.class)
                    .withIdentity("ScheduledJob", "scheduledGroup")
                    .build();

            Trigger trigger = TriggerBuilder
                    .newTrigger()
                    .withIdentity("scheduledTriggerName", "scheduledGroup")
                    .withSchedule(CronScheduleBuilder.cronSchedule(config.getConfigAsString(ConfigKey.cronExpression)))
                    .build();

            //Pass JobDetail and trigger dependencies to schedular
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
            logger.error("can't schedule the betting process!", e);
        }
    }

    public static void startSchedule() throws SchedulerException {
        scheduler.start();
    }

    public static void stopSchedule() throws SchedulerException {
        scheduler.shutdown();
    }

}

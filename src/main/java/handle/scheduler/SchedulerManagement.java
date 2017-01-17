package handle.scheduler;

import manament.log.LoggerWapper;
import org.apache.commons.lang3.time.DateUtils;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import util.Constant;
import util.PropertiesUtil;

import java.util.Date;

public class SchedulerManagement {
    public static final String API_KEY = "API_KEY_JOB";
    final static LoggerWapper logger = LoggerWapper.getLogger(SchedulerManagement.class);
    private static SchedulerManagement INSTANCE = new SchedulerManagement();
    private Scheduler scheduler;

    private SchedulerManagement() {
        try {
            scheduler = StdSchedulerFactory.getDefaultScheduler();
            startClearJob();
            scheduler.start();
        } catch (SchedulerException e) {
            logger.fastDebug("Cannot init scheduler", e, new Object());
        }
    }

    public static SchedulerManagement getInstance() {
        return INSTANCE;
    }

    public void startClearJob() {
        int intervalInHours = PropertiesUtil.getInt(Constant.CLEAN_CACHE_TIME, 24);
        JobDetail clearCache = JobBuilder.newJob(CleanCacheJob.class).withIdentity("CLEAN_CACHE", "API").build();
        Date triggerStartTime = DateUtils.addHours(new Date(), intervalInHours);
        Trigger trigger = TriggerBuilder.newTrigger().withIdentity("CLEAN_CACHE_TRIGGER", "API").startAt(triggerStartTime)
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInHours(intervalInHours).repeatForever()).build();
        try {
            scheduler.scheduleJob(clearCache, trigger);
            logger.fastInfo("started clear cache job");
        } catch (SchedulerException e) {
            logger.fastDebug("Cannot schedule job", e, new Object());
        }
    }


    public Scheduler getScheduler() {
        return scheduler;
    }

    public void schedule(APICacheJob clearJob) {
        int intervalInMinute = PropertiesUtil.getInt(Constant.CLEAN_DATA_CACHE_TIME, 1);
        JobDataMap jobdatamap = new JobDataMap();
        jobdatamap.put(API_KEY, clearJob);
        JobDetail clearCache = JobBuilder.newJob(JobWrapper.class).withIdentity(clearJob.getName(), "API_DATA").usingJobData(jobdatamap).build();
        Date triggerStartTime = DateUtils.addMinutes(new Date(), intervalInMinute);
        Trigger trigger = TriggerBuilder.newTrigger().withIdentity(clearJob.getName() +"-TRICGER", "API_DATA").startAt(triggerStartTime)
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInMinutes(intervalInMinute).repeatForever()).build();
        try {
            scheduler.scheduleJob(clearCache, trigger);
            logger.fastInfo("started clear data cache job");
        } catch (SchedulerException e) {
            logger.fastDebug("Cannot schedule clear data job", e, new Object());
        }
    }

   
}

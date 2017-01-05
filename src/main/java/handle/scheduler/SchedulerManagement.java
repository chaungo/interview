package handle.scheduler;

import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import manament.log.LoggerWapper;
import util.Constant;
import util.PropertiesUtil;

public class SchedulerManagement {
    final static LoggerWapper logger = LoggerWapper.getLogger(SchedulerManagement.class);
    private static SchedulerManagement INSTANCE = new SchedulerManagement();
    private Scheduler scheduler;
    
    public static SchedulerManagement getInstance(){
        return INSTANCE;
    }
    private SchedulerManagement() {
        try{
            scheduler = StdSchedulerFactory.getDefaultScheduler();
            startClearJob();
        } catch (SchedulerException e){
            logger.fastDebug("Cannot init scheduler", e, new Object());
        }
    }
    
    public void startClearJob(){
        int intervalInHours = PropertiesUtil.getInt(Constant.CLEAN_CACHE_TIME, 1);
        JobDetail clearCache = JobBuilder.newJob(ClearCacheJob.class).withIdentity("CLEAN_CACHE", "API").build();
        Date triggerStartTime = DateUtils.addHours(new Date(), intervalInHours);
        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("CLEAN_CACHE_TRIGGER", "API")
                .startAt(triggerStartTime)
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                    .withIntervalInHours(intervalInHours)
                    .repeatForever())            
                .build();
        try{
            scheduler.scheduleJob(clearCache, trigger);
            logger.fastInfo("started clear cache job");
        } catch (SchedulerException e){
            logger.fastDebug("Cannot schedule job", e, new Object());
        }
    }

}

package handle.scheduler;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class JobWrapper implements Job {
    private APICacheJob apiJob;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDataMap dataMap = context.getJobDetail().getJobDataMap();
        apiJob = (APICacheJob) dataMap.get(SchedulerManagement.API_KEY);
        if (apiJob != null) {
            apiJob.execute();
        }
    }

}

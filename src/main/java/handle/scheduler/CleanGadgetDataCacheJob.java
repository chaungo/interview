package handle.scheduler;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import manament.log.LoggerWapper;

public class CleanGadgetDataCacheJob implements Job {
    final static LoggerWapper logger = LoggerWapper.getLogger(CleanGadgetDataCacheJob.class);
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        GadgetCacheMap.getInstance().cleanup();
    }

}

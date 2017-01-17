package handle.scheduler;

import manament.log.LoggerWapper;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class CleanGadgetDataCacheJob implements Job {
    final static LoggerWapper logger = LoggerWapper.getLogger(CleanGadgetDataCacheJob.class);

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        GadgetCacheMap.getInstance().cleanup();
    }

}

package handle.scheduler;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import manament.log.LoggerWapper;
import util.gadget.GadgetUtility;

public class CleanCacheJob implements Job {
    final static LoggerWapper logger = LoggerWapper.getLogger(CleanCacheJob.class);

    @Override
    public void execute(JobExecutionContext arg0) throws JobExecutionException {
        logger.fastInfo("Begin clear cache");
        GadgetUtility.getInstance().clearCache();
    }


}

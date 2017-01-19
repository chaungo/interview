package handle.scheduler;

import manament.log.LoggerWapper;

public class APICacheJob {
    final static LoggerWapper logger = LoggerWapper.getLogger(APICacheJob.class);
    private GadgetCacheMap<?> cacheMap;
    private String name;

    public APICacheJob(GadgetCacheMap<?> cacheMap, String name) {
        this.cacheMap = cacheMap;
        this.name = name;
    }

    public void execute() {
        cacheMap.cleanup();
    }

    public String getName() {
        return name;
    }

}

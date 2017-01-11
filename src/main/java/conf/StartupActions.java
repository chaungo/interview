package conf;

import handle.scheduler.GadgetCacheMap;
import handle.scheduler.SchedulerManagement;

import javax.inject.Singleton;

@Singleton
public class StartupActions {
    public StartupActions() {
        SchedulerManagement.getInstance();
        GadgetCacheMap.getInstance();
    }
}

package conf;

import javax.inject.Singleton;

import handle.scheduler.SchedulerManagement;

@Singleton
public class StartupActions {
    public StartupActions() {
        SchedulerManagement.getInstance();
    }
}

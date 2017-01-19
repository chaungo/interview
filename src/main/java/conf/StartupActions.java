package conf;

import handle.scheduler.SchedulerManagement;

import javax.inject.Singleton;

@Singleton
public class StartupActions {
    public StartupActions() {
        SchedulerManagement.getInstance();
    }
}

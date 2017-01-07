package conf;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import handle.*;

@Singleton
public class Module extends AbstractModule {
    protected void configure() {
        bind(StartupActions.class);
        bind(EpicHandler.class).to(EpicHandlerImpl.class);
        bind(GadgetHandler.class).to(GadgetHandlerImpl.class);
        bind(AssigneeHandler.class).to(AssigneeHandlerImpl.class);
    }
}

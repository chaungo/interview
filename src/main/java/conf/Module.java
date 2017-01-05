package conf;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

import handle.AssigneeHandler;
import handle.AssigneeHandlerImpl;
import handle.EpicHandler;
import handle.EpicHandlerImpl;
import handle.GadgetHandler;
import handle.GadgetHandlerImpl;

@Singleton
public class Module extends AbstractModule {
    protected void configure() {
        bind(StartupActions.class);
        bind(EpicHandler.class).to(EpicHandlerImpl.class);
        bind(GadgetHandler.class).to(GadgetHandlerImpl.class);
        bind(AssigneeHandler.class).to(AssigneeHandlerImpl.class);
    }
}

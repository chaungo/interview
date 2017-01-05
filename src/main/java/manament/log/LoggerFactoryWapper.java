package manament.log;

import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggerFactory;

public class LoggerFactoryWapper implements LoggerFactory{

    @Override
    public Logger makeNewLoggerInstance(String name) {
        return new LoggerWapper(name);
    }

}

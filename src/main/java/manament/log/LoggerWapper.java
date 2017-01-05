package manament.log;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class LoggerWapper extends Logger {
    private static LoggerFactoryWapper factory = new LoggerFactoryWapper();
    private static final String FQCN = LoggerWapper.class.getName();

    protected LoggerWapper(String name) {
        super(name);
    }

    @SuppressWarnings("rawtypes")
    public static LoggerWapper getLogger(Class origin) {
        return getLogger(origin.getName());
    }

    public static LoggerWapper getLogger(String name) {
        return (LoggerWapper) getLogger(name, factory);
    }
    
    public void fastInfo(String message, Object... args) {
        if (isInfoEnabled()) {
            log(FQCN, Level.INFO, String.format(message, args), null);
        }
    }

    public void fastInfo(String message, Throwable t, Object... args) {
        if (isInfoEnabled()) {
            log(FQCN, Level.INFO, String.format(message, args), t);
        }
    }

    public void fastInfo(String message) {
        if (isInfoEnabled()) {
            log(FQCN, Level.INFO, message, null);
        }
    }
    

    public void fastDebug(String message, Object... args) {
        if (isDebugEnabled()) {
            log(FQCN, Level.DEBUG, String.format(message, args), null);
        }
    }

    public void fastDebug(String message, Throwable t, Object... args) {
        if (isDebugEnabled()) {
            log(FQCN, Level.DEBUG, String.format(message, args), t);
        }
    }

    public void fastDebug(String message) {
        if (isDebugEnabled()) {
            log(FQCN, Level.DEBUG, message, null);
        }
    }

    public void fasttrace(String message, Object... args) {
        if (isTraceEnabled()) {
            log(FQCN, Level.TRACE, String.format(message, args), null);
        }
    }

    public void fasttrace(String message, Throwable t, Object... args) {
        if (isTraceEnabled()) {
            log(FQCN, Level.TRACE, String.format(message, args), t);
        }
    }

    public void fasttrace(String message) {
        if (isTraceEnabled()) {
            log(FQCN, Level.TRACE, message, null);
        }
    }
}

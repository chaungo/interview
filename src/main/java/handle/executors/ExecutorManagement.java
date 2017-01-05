package handle.executors;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import manament.log.LoggerWapper;
import models.exception.APIException;
import util.Constant;
import util.PropertiesUtil;

public class ExecutorManagement {
    final static LoggerWapper logger = LoggerWapper.getLogger(ExecutorManagement.class);
    private static ExecutorManagement INSTANCE = new ExecutorManagement();

    private ExecutorService executor;

    private ExecutorManagement() {
        executor = Executors.newFixedThreadPool(PropertiesUtil.getInt(Constant.CONCURRENT_THREAD));
    }

    public static ExecutorManagement getInstance() {
        return INSTANCE;
    }

    public <T> List<Future<T>> invokeTask(List<? extends Callable<T>> tasks) throws APIException {
        try{
            return executor.invokeAll(tasks);
        } catch (InterruptedException e){
            logger.error("error during invoke", e);
            throw new APIException("error during invoke", e);
        }
    }

    public <T> List<T> getResult(List<Future<T>> results) throws APIException {
        List<T> returnData = new ArrayList<T>();
        if(results != null){
            try{
                for (Future<T> result : results){
                    if(result.isDone()){
                        returnData.add(result.get());
                    }
                }
            } catch (InterruptedException e){
                if(e.getCause() != null && e.getCause() instanceof APIException){
                    logger.error(e.getMessage(), e.getCause());
                    throw (APIException) e.getCause();
                }
                logger.error("error during invoke", e);
                throw new APIException("error during invoke", e);
            } catch (ExecutionException e){
                logger.fastDebug("erorrrrrrrrrrrrr", e);
                if(e.getCause() != null && e.getCause() instanceof APIException){
                    logger.error(e.getMessage(), e.getCause());
                    throw (APIException) e.getCause();
                }
                throw new APIException("error during invoke", e);
            }
        }
        return returnData;
    }
    public <T> List<T> invokeAndGet(List<? extends Callable<T>> tasks) throws APIException{
        return getResult(invokeTask(tasks));
    }
}

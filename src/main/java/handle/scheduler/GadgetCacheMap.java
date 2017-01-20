package handle.scheduler;

import manament.log.LoggerWapper;
import models.main.DataCacheVO;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class GadgetCacheMap<T> {
    final static LoggerWapper logger = LoggerWapper.getLogger(GadgetCacheMap.class);
    private long timeToLive;
    private ConcurrentMap<String, DataCacheVO<T>> cacheMap = new ConcurrentHashMap<>();

    /**
     * @param timeToLive : minute to live
     */
    public GadgetCacheMap(long timeToLive, String name) {
        this.timeToLive = timeToLive * 1000 * 60;
        APICacheJob clearJob = new APICacheJob(this, name);
        SchedulerManagement.getInstance().schedule(clearJob);
    }


    // PUT method
    public void put(String key, DataCacheVO<T> value) {

        cacheMap.put(key, value);
    }

    // GET method

    public DataCacheVO<T> get(String key) {
        if (key == null) {
            return null;
        }
        DataCacheVO<T> c = cacheMap.get(key);
        if (c == null)
            return null;
        else {
            // c.lastAccessed = System.currentTimeMillis();
            return c;
        }
    }

    // REMOVE method
    public void remove(String key) {
        logger.fasttrace("clean cache for gadgetId: %s", key);
        cacheMap.remove(key);
    }

    // CLEANUP method
    public void cleanup() {
        long now = System.currentTimeMillis();
        ArrayList<String> deleteKey = null;

        Iterator<Entry<String, DataCacheVO<T>>> itr = cacheMap.entrySet().iterator();

        deleteKey = new ArrayList<String>();
        DataCacheVO<T> c = null;


        while (itr.hasNext()) {
            Entry<String, DataCacheVO<T>> key = itr.next();
            c = key.getValue();
            if (c != null && (now > (timeToLive + c.getLastAccessed()))) {

                deleteKey.add(key.getKey());
            }
        }

        for (String key : deleteKey) {
            remove(key);
            Thread.yield();
        }
    }

    public void cleanAll() {
        cacheMap.clear();
    }
    
    public void cleanUserCache(String username) {
        Iterator<String> itr = cacheMap.keySet().iterator();
        ArrayList<String> deleteKey = new ArrayList<String>();
        while (itr.hasNext()){
            String key = itr.next();
            if(key.contains(username)){
                deleteKey.add(key);
            }
        }
        
        for (String key : deleteKey) {
            remove(key);
            Thread.yield();
        }
    }
}
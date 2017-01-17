package handle.scheduler;

import manament.log.LoggerWapper;
import models.main.GadgetDataCacheVO;
import util.Constant;
import util.PropertiesUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

<<<<<<< HEAD
public class GadgetCacheMap {
    final static LoggerWapper logger = LoggerWapper.getLogger(GadgetCacheMap.class);
    private static ConcurrentMap<String, GadgetDataCacheVO> cacheMap = new ConcurrentHashMap<String, GadgetDataCacheVO>();
    private static GadgetCacheMap INSTANCE = new GadgetCacheMap(PropertiesUtil.getInt(Constant.DATA_CACHE_TIME_TO_LIVE, 10));
    ;
    private long timeToLive;

=======
import manament.log.LoggerWapper;
import models.main.DataCacheVO;
public class GadgetCacheMap<T> {
    final static LoggerWapper logger = LoggerWapper.getLogger(GadgetCacheMap.class);
    private long timeToLive;
    private ConcurrentMap<String, DataCacheVO<T>> cacheMap = new ConcurrentHashMap<>();
>>>>>>> f7fff14a62165840b6a469df201cfe0c21353a0f
    /**
     * @param timeToLive : minute to live
     */
    public GadgetCacheMap(long timeToLive, String name) {
        this.timeToLive = timeToLive * 1000 * 60;
        APICacheJob clearJob = new APICacheJob(this, name);
        SchedulerManagement.getInstance().schedule(clearJob);
    }
<<<<<<< HEAD


    public static GadgetCacheMap getInstance() {
        return INSTANCE;
    }


    // PUT method
    public void put(String key, GadgetDataCacheVO value) {
=======
    
    // PUT method
    public void put(String key, DataCacheVO<T> value) {
>>>>>>> f7fff14a62165840b6a469df201cfe0c21353a0f
        cacheMap.put(key, value);
    }

    // GET method
<<<<<<< HEAD
    public GadgetDataCacheVO get(String key) {
        GadgetDataCacheVO c = cacheMap.get(key);
        if (c == null)
=======
    public DataCacheVO<T> get(String key) {
        DataCacheVO<T> c = cacheMap.get(key);
        if(c == null)
>>>>>>> f7fff14a62165840b6a469df201cfe0c21353a0f
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

<<<<<<< HEAD
        while (itr.hasNext()) {
            Entry<String, GadgetDataCacheVO> key = itr.next();
            c = (GadgetDataCacheVO) key.getValue();
            if (c != null && (now > (timeToLive + c.lastAccessed))) {
=======
        while (itr.hasNext()){
            Entry<String, DataCacheVO<T>> key = itr.next();
            c = key.getValue();
            if(c != null && (now > (timeToLive + c.lastAccessed))){
>>>>>>> f7fff14a62165840b6a469df201cfe0c21353a0f
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
}
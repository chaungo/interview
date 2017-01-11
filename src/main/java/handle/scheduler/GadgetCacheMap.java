package handle.scheduler;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import manament.log.LoggerWapper;
import models.main.GadgetDataCacheVO;
import util.Constant;
import util.PropertiesUtil;
public class GadgetCacheMap {
    final static LoggerWapper logger = LoggerWapper.getLogger(GadgetCacheMap.class);
    private long timeToLive;
    private static ConcurrentMap<String, GadgetDataCacheVO> cacheMap = new ConcurrentHashMap<String, GadgetDataCacheVO>();;
    private static GadgetCacheMap INSTANCE = new GadgetCacheMap(PropertiesUtil.getInt(Constant.DATA_CACHE_TIME_TO_LIVE, 10));
    
    /**
     * 
     * @param timeToLive : minute to live
     */
    private GadgetCacheMap(long timeToLive) {
        this.timeToLive = timeToLive * 1000 * 60;
    }
    
    
    public static GadgetCacheMap getInstance() {
        return INSTANCE;
    }


    // PUT method
    public void put(String key, GadgetDataCacheVO value) {
            cacheMap.put(key, value);
    }
    
    // GET method
    public GadgetDataCacheVO get(String key) {
        GadgetDataCacheVO c = cacheMap.get(key);
        if(c == null)
            return null;
        else{
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

        Iterator<Entry<String, GadgetDataCacheVO>> itr = cacheMap.entrySet().iterator();

        deleteKey = new ArrayList<String>();
        GadgetDataCacheVO c = null;

        while (itr.hasNext()){
            Entry<String, GadgetDataCacheVO> key = itr.next();
            c = (GadgetDataCacheVO) key.getValue();
            if(c != null && (now > (timeToLive + c.lastAccessed))){
                deleteKey.add(key.getKey());
            }
        }

        for (String key : deleteKey){
            remove(key);
            Thread.yield();
        }
    }
    public void cleanAll(){
        cacheMap.clear();
    }
}
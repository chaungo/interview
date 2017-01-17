package models.main;

<<<<<<< HEAD:src/main/java/models/main/GadgetDataCacheVO.java
import java.util.Map;

public class GadgetDataCacheVO {
    public long lastAccessed = System.currentTimeMillis();
    private Map<String, GadgetDataWapper> gadgetsData;
=======
public class DataCacheVO<T> {
    private T data;
>>>>>>> f7fff14a62165840b6a469df201cfe0c21353a0f:src/main/java/models/main/DataCacheVO.java
    private State state = State.LOADING;

    public long getLastAccessed() {
        return lastAccessed;
    }

    public void setLastAccessed(long lastAccessed) {
        this.lastAccessed = lastAccessed;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public enum State {
        LOADING, SUCCESS, FAIL
    }
}

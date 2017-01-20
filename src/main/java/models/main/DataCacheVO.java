package models.main;

public class DataCacheVO<T> {
    private long lastAccessed = System.currentTimeMillis();
    private T data;
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

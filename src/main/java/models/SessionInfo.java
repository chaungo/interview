package models;

import java.util.Map;

public class SessionInfo {
    Map<String, String> cookies;

    public Map<String, String> getCookies() {
        return cookies;
    }

    public void setCookies(Map<String, String> cookies) {
        this.cookies = cookies;
    }

}

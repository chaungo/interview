package util;

import ninja.session.Session;
import service.HTTPClientUtil;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.util.*;

import static util.Constant.LINK_CRUCIBLE;


public class MyUtill {


    public static Map<String, String> getCookies(Session session) {
        Map<String, String> map = new HashMap<>();
        String cookies[] = session.get("cookies").replace("{", "").replace("}", "").split(", ");

        for (String cookie : cookies) {
            String part[] = cookie.split("=");
            map.put(part[0].trim(), part[1].trim());
        }

        return map;
    }

    private static Map<String, String> getCruCookies(Session session) {
        Map<String, String> map = new HashMap<>();
        String cookies[] = session.get("crucookies").replace("{", "").replace("}", "").split(", ");

        for (String cookie : cookies) {
            String part[] = cookie.split("=");
            map.put(part[0].trim(), part[1].trim());
        }

        return map;
    }

    public static BufferedReader getHttpURLConnection(String url, Session session) throws Exception {
        Proxy proxy = HTTPClientUtil.getInstance().getProxy();
        URL url2 = new URL(url);
        HttpURLConnection myURLConnection;
        if (proxy != null) {
            myURLConnection = (HttpURLConnection) url2.openConnection(proxy);
        } else {
            myURLConnection = (HttpURLConnection) url2.openConnection();
        }


        if (url.contains(LINK_CRUCIBLE)) {
            myURLConnection.setRequestProperty("Cookie", getCruCookies(session).toString());
        } else {
            myURLConnection.setRequestProperty("Cookie", getCookies(session).toString());
        }

        myURLConnection.setRequestMethod("GET");
        return new BufferedReader(new InputStreamReader(myURLConnection.getInputStream()));
    }


    public static boolean isCacheExpired(org.bson.Document document, int timeInHour) {
        try {
            GregorianCalendar latestUpdateTime = new GregorianCalendar(Locale.getDefault());
            latestUpdateTime.setTimeInMillis(document.getLong(Constant.UPDATE_DATE));
            latestUpdateTime.add(Calendar.HOUR, timeInHour);
            GregorianCalendar currentTime = new GregorianCalendar(Locale.getDefault());
            return latestUpdateTime.before(currentTime);
        } catch (Exception e) {
            return true;
        }
    }


}

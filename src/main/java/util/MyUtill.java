package util;

import ninja.session.Session;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import service.HTTPClientUtil;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.util.*;

import static util.Constant.CONNECTION_TIMEOUT;
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

        if (proxy == null) {
            myURLConnection = (HttpURLConnection) url2.openConnection();
        } else {
            myURLConnection = (HttpURLConnection) url2.openConnection(proxy);
        }

        Map<String, String> cookies;
        if (url.contains(LINK_CRUCIBLE)) {
            cookies = getCruCookies(session);
        } else {
            cookies = getCookies(session);
        }

        myURLConnection.setRequestProperty("Cookie", cookies.toString());

        myURLConnection.setRequestMethod("GET");
        return new BufferedReader(new InputStreamReader(myURLConnection.getInputStream()));
    }


    public static String getJsoupConnectionRespondBody(String link, Session session) throws Exception {

        String respond;
        Proxy proxy = HTTPClientUtil.getInstance().getProxy();
        Map<String, String> cookies;

        if (link.contains(LINK_CRUCIBLE)) {
            cookies = getCruCookies(session);
        } else {
            cookies = getCookies(session);
        }

        if (proxy == null) {
            respond = Jsoup.connect(link).cookies(cookies).timeout(CONNECTION_TIMEOUT).ignoreHttpErrors(true).method(Connection.Method.GET).execute().body();
        } else {
            respond = Jsoup.connect(link).proxy(proxy).cookies(cookies).timeout(CONNECTION_TIMEOUT).ignoreHttpErrors(true).method(Connection.Method.GET).execute().body();
        }

        return respond;

    }


    public static boolean isCacheExpired(org.bson.Document document, int timeInHour) {
        try {
            GregorianCalendar latestUpdateTime = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
            latestUpdateTime.setTimeInMillis(document.getLong(Constant.UPDATE_DATE));
            latestUpdateTime.add(Calendar.HOUR, timeInHour);
            GregorianCalendar currentTime = new GregorianCalendar(Locale.getDefault());
            return latestUpdateTime.before(currentTime);
        } catch (Exception e) {
            return true;
        }
    }


}

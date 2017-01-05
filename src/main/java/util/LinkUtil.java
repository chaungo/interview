package util;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;

public class LinkUtil {

    final static Logger logger = Logger.getLogger(LinkUtil.class);

    public static Document getConnection(String link, String token) {
        Document doc = null;
        try {
            doc = Jsoup.connect(link).header("authorization", "Basic " + token).timeout(Constant.TIMEOUT).ignoreContentType(true).get();
        } catch (Exception e) {
            logger.error(String.format("Connect %s with error %s", link, e));
        }
        return doc;
    }

    /**
     * @param link String.format(Constant.LINK_GET_SONAR_STATISTIC, metric, sonarKey)
     * @param ip   Constant.PROXY_IP
     * @param port Constant.PROXY_PORT
     * @return doc
     */
    public static Document getConnectionWithProxy(String link, String ip, int port) {
        Document doc = null;
        try {
            URL url = new URL(link);
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(ip, port));
            HttpURLConnection uc = (HttpURLConnection) url.openConnection(proxy);
            // non-proxy
//				HttpURLConnection uc = (HttpURLConnection)url.openConnection();

            uc.connect();

            String line = null;
            StringBuffer tmp = new StringBuffer();
            BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
            while ((line = in.readLine()) != null) {
                tmp.append(line);
            }

            doc = Jsoup.parse(String.valueOf(tmp));
        } catch (IOException e) {
            logger.error(String.format("Connect %s using proxy %s:%s with error %s", link, ip, port, e));
        }
        return doc;
    }

    public boolean isUserAndPasswordValid(String username, String password) {
        if (username != null && password != null) {
            Response respond = null;
            String authString = username + ":" + password;
            String token = new String(Base64.encodeBase64(authString.getBytes()));
            try {
                respond = Jsoup.connect(Constant.LINK_CRUCIBLE).header("authorization", "Basic " + token).timeout(Constant.TIMEOUT).execute();
            } catch (IOException e) {
                logger.error(e);
            }
            return respond.header("X-AUSERNAME").equals(username);
        }
        return false;
    }
}

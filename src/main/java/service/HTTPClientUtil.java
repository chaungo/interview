package service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import org.apache.http.HttpHost;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;

import manament.log.LoggerWapper;
import models.exception.APIException;
import util.Constant;
import util.PropertiesUtil;

public class HTTPClientUtil {
    private final static LoggerWapper logger = LoggerWapper.getLogger(HTTPClientUtil.class);
    private final static String SLASH = "/";
    private static HTTPClientUtil instance;
    private CloseableHttpClient httpclient;
    private String loginURL;

    private HTTPClientUtil() {
        loginURL = PropertiesUtil.getString(Constant.RESOURCE_BUNLE_HOST_TYPE) + "://"
                + (PropertiesUtil.getString(Constant.RESOURCE_BUNLE_HOST) + PropertiesUtil.getString(Constant.RESOURCE_BUNLE_LOGIN_PATH));
    }

    public synchronized static HTTPClientUtil getInstance() {
        if(instance == null){
            instance = new HTTPClientUtil();
        }
        return instance;
    }

    public CloseableHttpResponse execute(HttpUriRequest request) {
        try{
            return httpclient.execute(request);
        } catch (IOException e){
            logger.fastDebug("cannot execute request", e, new Object());
        }
        return null;
    }

    @SuppressWarnings("unused")
    private void login(CloseableHttpClient httpclient) throws URISyntaxException, ClientProtocolException, IOException {
        URI uri = new URI(loginURL);
        RequestBuilder requestBuilder = RequestBuilder.post().setUri(uri).addParameter("os_username", "hcongle").addParameter("os_password", "hcl49#Tma");
        RequestConfig config = getProxyConfig();
        if(config != null){
            requestBuilder.setConfig(config);
        }

        HttpUriRequest loginRequest = requestBuilder.build();
        logger.info("send request login to:" + uri.toString());
        CloseableHttpResponse response = httpclient.execute(loginRequest);
        response.close();
        logger.info("login successfully");

    }

    public HttpClient getHttpclient() {
        return httpclient;
    }

    public Map<String, String> loginGreenhopper(String username, String password) {
        Map<String, String> sessionCookies = new HashMap<String, String>();
        try{
            Proxy proxy = getProxy();
            logger.fastDebug("Login to %s , proxy:%s", loginURL, proxy.toString());
            Connection req = Jsoup.connect(loginURL).ignoreHttpErrors(true).ignoreContentType(true).data("os_username", username).data("os_password", password)
                    .timeout(PropertiesUtil.getInt(Constant.PARAMERTER_TIMEOUT, 10000)).method(Connection.Method.POST);
            if(proxy != null){
                req.proxy(proxy);
            }
            Response res = req.execute();
            sessionCookies = res.cookies();
            logger.fasttrace("User: %s, cookies:%s", username, sessionCookies);
        } catch (IOException e){
            logger.fastDebug("User: %s, cannot login to %s", e, username, loginURL);
        }
        return sessionCookies;
    }

    public String getLegacyData(String path, Map<String, String> parameters, Map<String, String> cookies) throws APIException {
        return getLegacyData(path, parameters, PropertiesUtil.getInt(Constant.PARAMERTER_TIMEOUT, 60000), cookies);
    }

    public String getLegacyData(String path, Map<String, String> parameters, int timeout, Map<String, String> cookies) throws APIException {
        String data = null;
        String host = PropertiesUtil.getString(Constant.RESOURCE_BUNLE_HOST);
        String scheme = PropertiesUtil.getString(Constant.RESOURCE_BUNLE_HOST_TYPE);

        String url = scheme + ":" + SLASH + SLASH + host + path;
        Connection connection = Jsoup.connect(url).ignoreHttpErrors(true).ignoreContentType(true).timeout(timeout).maxBodySize(0).method(Connection.Method.GET);
        if(cookies!=null && !cookies.isEmpty()){
            connection.cookies(cookies);
        } else{
            throw new APIException("Login session not available, need re-login to greenhopper");
        }
        parameters.forEach(new BiConsumer<String, String>() {
            @Override
            public void accept(String paramName, String value) {
                connection.data(paramName, value);
            }
        });
        Proxy proxy = getProxy();
        logger.fasttrace("getLegacyData(%s , %s) , connecting to %s \nproxy:%s", path, parameters, url, proxy);
        if(proxy != null){
            connection.proxy(proxy);
        }
        try{
            Response re = connection.execute();
            data = re.body();
        } catch (IOException e){
            logger.fastDebug("Cannot connect to %s, %s", e, url, e.getMessage());
            throw new APIException("Cannot connect to " + host + ", " + e.getMessage(), e);
        }
        return data;
    }

    public String getLegacyData_HTTPClient(String path, Map<String, String> parameters) {
        String host = PropertiesUtil.getString(Constant.RESOURCE_BUNLE_HOST);
        String scheme = PropertiesUtil.getString(Constant.RESOURCE_BUNLE_HOST_TYPE);
        URIBuilder builder = new URIBuilder();
        builder.setCharset(StandardCharsets.UTF_8);
        builder.setScheme(scheme).setHost(host).setPath(path);
        builder.setUserInfo("\"");
        parameters.forEach(new BiConsumer<String, String>() {
            @Override
            public void accept(String paramName, String paramValue) {
                builder.setParameter(paramName, paramValue);
            }
        });

        StringBuffer result = new StringBuffer();
        CloseableHttpResponse response = null;
        BufferedReader rd = null;
        try{
            URI uri = builder.build();
            logger.fasttrace("Connecting to URI %s", uri);
            HttpGet httpget = new HttpGet(uri);
            RequestConfig config = getProxyConfig();
            if(config != null){
                httpget.setConfig(config);
            }
            try{
                response = HTTPClientUtil.getInstance().execute(httpget);
                String line = "";
                if(response != null){
                    rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                    while ((line = rd.readLine()) != null){
                        result.append(line);
                    }
                }
            } finally{
                if(response != null){
                    response.close();
                }
                if(rd != null){
                    rd.close();
                }
            }
        } catch (URISyntaxException | IOException e){
            logger.fastDebug("cannot connect to %s", e, host);
        }
        return result.toString();
    }

    public RequestConfig getProxyConfig() {
        RequestConfig config = null;
        String proxyIP = PropertiesUtil.getString(Constant.RESOURCE_BUNLE_PROXY_IP);
        String proxyPortStr = PropertiesUtil.getString(Constant.RESOURCE_BUNLE_PROXY_PORT);
        String proxyType = PropertiesUtil.getString(Constant.RESOURCE_BUNLE_PROXY_TYPE);
        int proxyPort = 0;
        try{
            if(proxyPortStr != null){
                proxyPort = Integer.parseInt(proxyPortStr);
            }
        } catch (NumberFormatException e){
            logger.fasttrace("Incorrect proxy port address %s", e, proxyPortStr);
        }
        if(proxyIP != null && proxyType != null){
            // logger.info("using proxy:" + proxyType + "://" + proxyIP + ":" + proxyPort);
            HttpHost proxy = new HttpHost(proxyIP, proxyPort, proxyType);
            config = RequestConfig.custom().setProxy(proxy).build();
        }
        return config;
    }

    public Proxy getProxy() {
        String proxyIP = PropertiesUtil.getString(Constant.RESOURCE_BUNLE_PROXY_IP);
        String proxyPortStr = PropertiesUtil.getString(Constant.RESOURCE_BUNLE_PROXY_PORT);
        int proxyPort = 0;
        if(proxyIP == null || proxyPortStr == null){
            return null;
        }
        try{
            proxyPort = Integer.parseInt(proxyPortStr);
        } catch (NumberFormatException e){
            logger.fasttrace("Incorrect proxy port address %s", e, proxyPortStr);
            return null;
        }
        Proxy proxy = new Proxy(java.net.Proxy.Type.HTTP, new InetSocketAddress(proxyIP, proxyPort));
        return proxy;
    }

}

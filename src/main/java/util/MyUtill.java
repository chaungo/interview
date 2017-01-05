package util;

import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import controllers.DashboardController;
import models.SessionInfo;
import ninja.session.Session;
import service.HTTPClientUtil;

import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.*;

import static util.Constant.*;


public class MyUtill {


    private static Map<String, String> getCookies(Session session) {
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


    private static Document getJsoupConnectionRespond(String link, Session session) {

        Document respond = null;
        try {
            respond = Jsoup.connect(link).proxy(PROXY_IP, PROXY_PORT).cookies(getCookies(session))
                    .timeout(CONNECTION_TIMEOUT_FOR_GET_STATISTIC).ignoreHttpErrors(true).get();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return respond;

    }

    private static BufferedReader getHttpURLConnection(String url, Session session) throws Exception {
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(PROXY_IP, PROXY_PORT));
        URL url2 = new URL(url);
        HttpURLConnection myURLConnection = (HttpURLConnection) url2.openConnection(proxy);

        if (url.contains("http://tiger.in.alcatel-lucent.com:8060")) {
            myURLConnection.setRequestProperty("Cookie", getCruCookies(session).toString());
        } else {
            myURLConnection.setRequestProperty("Cookie", getCookies(session).toString());
        }

        myURLConnection.setRequestMethod("GET");
        return new BufferedReader(new InputStreamReader(myURLConnection.getInputStream()));
    }


    public static boolean doLogin(String username, String password, Session session) throws Exception {

        Response respond = Jsoup.connect(LOGIN_LINK).data(USERNAME_LOGIN_KEY, username).data(PASSWORD_LOGIN_KEY, password)
                .data(REMEMBER_LOGIN_KEY, "true").method(Method.POST).timeout(CONNECTION_TIMEOUT).execute();

        Map<String, String> cookies = respond.cookies();
        session.put("cookies", cookies.toString());

        if (respond.header("X-AUSERNAME").equals(username)) {
            session.put("username", username);

            Response cruRespond = Jsoup.connect("http://tiger.in.alcatel-lucent.com:8060/login").data("username", username).data("password", password)
                    .data("rememberme", "yes").method(Method.POST).timeout(CONNECTION_TIMEOUT).execute();

            Map<String, String> CruCookies = cruRespond.cookies();
            System.out.println(cookies.toString());

            session.put("crucookies", CruCookies.toString());
            
            //login to greenhopper
            Map<String, String> cookiesMap = HTTPClientUtil.getInstance().loginGreenhopper(username, password);
            if(cookiesMap != null && !cookiesMap.isEmpty()){
                SessionInfo sessionInfo = new SessionInfo();
                sessionInfo.setCookies(cookiesMap);
                String sessionInfoStr = JSONUtil.getInstance().convertToString(sessionInfo);
                session.put(Constant.API_SESSION_INFO, sessionInfoStr);
            }
            
            return true;
        }

        if (respond.header("X-AUSERNAME").equals(LOGININFO_INVALID)) {
            session.clear();
            return false;
        }

        return false;
    }

    public static JSONObject getUserInformation(Session session) throws Exception {
        JSONObject userInfoRS = new JSONObject();

        Document response = Jsoup.connect(String.format(Constant.LINK_GET_JIRA_USER_INFO, session.get("username")))
                .proxy(PROXY_IP, PROXY_PORT).cookies(getCookies(session)).timeout(CONNECTION_TIMEOUT).ignoreContentType(true)
                .ignoreHttpErrors(true).get();

        JSONObject userInfo = new JSONObject(response.body().text());
        userInfoRS.put("alias", userInfo.getString(DisplayName));

        JSONArray groups = userInfo.getJSONObject(Groups).getJSONArray(GroupsItems);

        JSONArray groupNames = new JSONArray();
        userInfoRS.put("role", "");
        for (int i = 0; i < groups.length(); i++) {
            JSONObject group = groups.getJSONObject(i);
            if (group.getString("name").contains("jira-administrators")) {
                session.put("role", "jira-administrators");
                userInfoRS.put("role", "jira-administrators");
            }

            groupNames.put(group.getString("name"));
        }


        userInfoRS.put("groups", groupNames.toString());
        return userInfoRS;
    }


    public static JSONArray getJiraProjectofUserfromServer(Session session) throws Exception {
        JSONArray projectDataArray = new JSONArray();
        String rs = "";
        BufferedReader br = getHttpURLConnection(LINK_GET_JIRA_PROJECTS, session);
        String inputLine;
        while ((inputLine = br.readLine()) != null) {
            rs = rs + inputLine;
        }
        br.close();

        JSONArray dataArray = new JSONArray(rs);

        for (int i = 0; i < dataArray.length(); i++) {
            JSONObject project = dataArray.getJSONObject(i);
            projectDataArray.put(project.getString("name"));
        }

        return projectDataArray;
    }

    public static JSONArray getCruProjectfromServer(Session session) throws Exception {

        JSONArray projectDataArray = new JSONArray();

        MongoClient mongoClient = new MongoClient();
        MongoCollection<org.bson.Document> collection = mongoClient.getDatabase("Interview").getCollection("Gadget");
        org.bson.Document document = collection.find(new org.bson.Document("Name", "AMS Overdue Reviews Report Gadget")).first();
        JSONObject cache;
        try {
            cache = new JSONObject(document.getString("cache"));
        } catch (Exception e) {
            e.printStackTrace();
            cache = new JSONObject();
            cache.put("project", new JSONArray());
            cache.put("user", new JSONArray());
        }

        try {
            if (isCacheExpired(document, 24)) {
                String rs = "";
                BufferedReader br = getHttpURLConnection(LINK_GET_CRU_PROJECTS, session);
                String inputLine;
                while ((inputLine = br.readLine()) != null) {
                    rs = rs + inputLine;
                }
                br.close();
                JSONArray dataArray = new JSONArray(rs);
                if (dataArray.length() != 0) {
                    for (int i = 0; i < dataArray.length(); i++) {
                        JSONObject project = dataArray.getJSONObject(i);
                        JSONObject projectInfo = new JSONObject();
                        projectInfo.put("id", project.getString("id"));
                        projectInfo.put("name", project.getString("displaySecondary"));
                        projectDataArray.put(projectInfo);
                    }
                    cache.remove("project");
                    cache.put("project", projectDataArray);
                    collection.updateOne(new org.bson.Document("Name", "AMS Overdue Reviews Report Gadget"), new org.bson.Document("$set", new org.bson.Document("cache", cache.toString()).append("updateDate", new GregorianCalendar(Locale.getDefault()).getTimeInMillis())));
                } else {
                    projectDataArray = cache.getJSONArray("project");
                }
            } else {
                projectDataArray = cache.getJSONArray("project");
            }
        } catch (Exception e) {
            e.printStackTrace();
            projectDataArray = cache.getJSONArray("project");
        } finally {
            mongoClient.close();
        }

        return projectDataArray;
    }

    public static JSONArray getCruUserfromServer(Session session) throws Exception {

        JSONArray userArray = new JSONArray();

        MongoClient mongoClient = new MongoClient();
        MongoCollection<org.bson.Document> collection = mongoClient.getDatabase("Interview").getCollection("Gadget");
        org.bson.Document document = collection.find(new org.bson.Document("Name", "AMS Overdue Reviews Report Gadget")).first();
        JSONObject cache = new JSONObject(document.getString("cache"));

        try {
            if (isCacheExpired(document, 24)) {
                String rs = "";
                BufferedReader br = getHttpURLConnection(LINK_GET_CRU_USERS, session);
                String inputLine;
                while ((inputLine = br.readLine()) != null) {
                    rs = rs + inputLine;
                }
                br.close();
                JSONArray dataArray = new JSONArray(rs);
                if (dataArray.length() != 0) {
                    for (int i = 0; i < dataArray.length(); i++) {
                        JSONObject user = dataArray.getJSONObject(i);
                        JSONObject userInfo = new JSONObject();
                        userInfo.put("id", user.getString("id"));
                        userInfo.put("name", user.getString("displayPrimary"));
                        userArray.put(userInfo);
                    }
                    cache.remove("user");
                    cache.put("user", userArray);
                    collection.updateOne(new org.bson.Document("Name", "AMS Overdue Reviews Report Gadget"), new org.bson.Document("$set", new org.bson.Document("cache", cache.toString()).append("updateDate", new GregorianCalendar(Locale.getDefault()).getTimeInMillis())));
                } else {
                    userArray = cache.getJSONArray("user");
                }
            } else {
                userArray = cache.getJSONArray("user");
            }
        } catch (Exception e) {
            userArray = cache.getJSONArray("user");
        } finally {
            mongoClient.close();
        }

        return userArray;
    }


    public static JSONObject getReviewfromServer(Session session, String ia, String project) throws Exception {
        JSONArray reviewDataArray = new JSONArray();
        String rs = "";
        BufferedReader br = getHttpURLConnection(String.format(LINK_GET_ODREVIEW_REPORTS, ia, project), session);
        String inputLine;
        while ((inputLine = br.readLine()) != null) {
            rs = rs + inputLine;
        }
        br.close();


        JSONArray array = XML.toJSONObject(rs).getJSONArray("detailedReviewData");


        for (int i = 0; i < array.length(); i++) {
            int lessThan5 = 0;
            int moreThan5Less10 = 0;
            int moreThan10 = 0;








        }


//        JSONObject reviewData = new JSONObject();
//        reviewData.put("creator","");
//        reviewData.put("column1","");
//        reviewData.put("column2","");
//        reviewData.put("column3","");


        return XML.toJSONObject(rs);
    }


    public static JSONArray getIAComponentsRespond(Session session, String url, String iaList) throws Exception {
        JSONArray IAArray = new JSONArray();

        MongoClient mongoClient = new MongoClient();
        MongoCollection<org.bson.Document> collection = mongoClient.getDatabase("Interview").getCollection("Release");
        org.bson.Document document = collection.find(new org.bson.Document("url", url)).first();

        if (isCacheExpired(document, 24)) {

            BufferedReader br = getHttpURLConnection(url, session);

            String inputLine;
            JSONObject IA;
            JSONArray ComponentsJsonArray;
            while ((inputLine = br.readLine()) != null) {
                IA = new JSONObject();
                String[] parts = inputLine.split(",");
                String name = parts[0].trim();

                IA.put("name", name);
                ComponentsJsonArray = new JSONArray();

                for (int i = 1; i < parts.length; i++) {
                    JSONObject component = new JSONObject();
                    component.put("component", parts[i].trim());
                    ComponentsJsonArray.put(component);
                }

                IA.put("Components", ComponentsJsonArray);
                IAArray.put(IA);
            }

            br.close();

            collection.updateOne(new org.bson.Document("url", url), new org.bson.Document("$set", new org.bson.Document("cache", IAArray.toString()).append("updateDate", new GregorianCalendar(Locale.getDefault()).getTimeInMillis())));

        } else {
            IAArray = new JSONArray(document.getString("cache"));
        }

        mongoClient.close();


        if (iaList != null) {
            JSONArray rs = new JSONArray();
            List IANames = Arrays.asList(iaList.split(","));
            for (int i = 0; i < IAArray.length(); i++) {
                if (IANames.contains(IAArray.getJSONObject(i).getString("name"))) {
                    rs.put(IAArray.getJSONObject(i));
                    if (rs.length() == IANames.size()) {
                        break;
                    }
                }
            }
            return rs;
        } else {
            return IAArray;
        }


    }


    private static JSONArray getSonarStatisticRespond(Session session, String metric, String sonarKey) {
        try {
            BufferedReader br = getHttpURLConnection(String.format(LINK_GET_SONAR_STATISTIC, metric, sonarKey), session);
            String inputLine;
            String rs = "";
            while ((inputLine = br.readLine()) != null) {
                rs = rs + inputLine;
            }

            br.close();
            return new JSONArray(rs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static void setProxy() {
        System.setProperty("http.proxyHost", PROXY_IP);
        System.setProperty("http.proxyPort", PROXY_PORT + "");
    }


    public static void insertDashboardGadgettoDB(String dashboardId, String name, String data) {
        MongoClient mongoClient = new MongoClient();
        MongoCollection<org.bson.Document> collection = mongoClient.getDatabase("Interview").getCollection("DashboardGadget");
        org.bson.Document doc = new org.bson.Document("dashboardId", dashboardId).append("type", name).append("data", data);
        collection.insertOne(doc);
        mongoClient.close();
    }

    public static void updateDashboardGadgettoDB(String GadgetId, String data) {
        MongoClient mongoClient = new MongoClient();
        MongoCollection<org.bson.Document> collection = mongoClient.getDatabase("Interview").getCollection("DashboardGadget");
        org.bson.Document doc = new org.bson.Document("data", data).append("cache", "").append("updateDate", 0);
        collection.updateOne(new org.bson.Document("_id", new ObjectId(GadgetId)), new org.bson.Document("$set", doc));
        mongoClient.close();
    }

    public static void deleteDashboardGadgetfromDB(String gadgetId) {
        System.out.println(gadgetId);
        MongoClient mongoClient = new MongoClient();
        MongoCollection<org.bson.Document> gadgetCollection = mongoClient.getDatabase("Interview").getCollection("DashboardGadget");
        FindIterable<org.bson.Document> gadgetIterable = gadgetCollection.find(new org.bson.Document("_id", new ObjectId(gadgetId)));
        gadgetCollection.deleteOne(gadgetIterable.first());

        mongoClient.close();
    }

    public static void clearCacheGadgetfromDB(String gadgetId) {
        MongoClient mongoClient = new MongoClient();
        MongoCollection<org.bson.Document> collection = mongoClient.getDatabase("Interview").getCollection("DashboardGadget");
        org.bson.Document doc = new org.bson.Document("cache", "").append("updateDate", 0);
        collection.updateOne(new org.bson.Document("_id", new ObjectId(gadgetId)), new org.bson.Document("$set", doc));
        mongoClient.close();
    }


    public static JSONArray getDashboardGadgetbyDashboardId(String dashboardId) throws Exception {
        JSONArray dashboardGadgets = new JSONArray();
        MongoClient mongoClient = new MongoClient();
        MongoCollection<org.bson.Document> collection = mongoClient.getDatabase("Interview").getCollection("DashboardGadget");
        for (org.bson.Document document : collection.find(new org.bson.Document("dashboardId", dashboardId))) {
            JSONObject gadget = new JSONObject();
            gadget.put("id", document.get("_id"));
            gadget.put("type", document.get("type"));
            gadget.put("data", new JSONObject(document.getString("data")));
            dashboardGadgets.put(gadget);
        }


        mongoClient.close();
        return dashboardGadgets;
    }


    public static JSONArray getGadgetListfromDB() throws Exception {
        MongoClient mongoClient = new MongoClient();
        MongoCollection<org.bson.Document> gadgetCollection = mongoClient.getDatabase("Interview").getCollection("Gadget");
        FindIterable<org.bson.Document> gadgetIterable = gadgetCollection.find();
        JSONArray gadgets = new JSONArray();
        gadgetIterable.forEach(new Block<org.bson.Document>() {
            @Override
            public void apply(final org.bson.Document document) {
                JSONObject gadget = new JSONObject();
                gadget.put("id", document.get("_id"));
                gadget.put("name", document.get("Name"));
                gadget.put("des", document.get("Description"));
                gadget.put("author", document.get("Author"));
                gadget.put("img", document.get("PictureUrl"));
                gadget.put("addnewUIurl", document.get("addnewUIurl"));
                gadgets.put(gadget);
            }
        });

        mongoClient.close();

        return gadgets;
    }


    public static void getOverdueReviewsReport(String project, String iaName, Session session) {
        String connectionResult = getJsoupConnectionRespond(String.format(Constant.LINK_GET_ODREVIEW_REPORTS, iaName, project), session).body().text();

    }


    public static JSONArray getMetricsFromDB() throws Exception {

        MongoClient mongoClient = new MongoClient();
        MongoCollection<org.bson.Document> metricCollection = mongoClient.getDatabase("Interview").getCollection("Sonar_Metric");
        FindIterable<org.bson.Document> metricIterable = metricCollection.find();
        JSONArray metrics = new JSONArray();
        metricIterable.forEach(new Block<org.bson.Document>() {
            @Override
            public void apply(final org.bson.Document document) {
                JSONObject metric = new JSONObject();
                metric.put("name", document.get("name"));
                metric.put("key", document.get("code"));
                metrics.put(metric);
            }
        });

        mongoClient.close();
        return metrics;
    }

    public static JSONArray getReleasesFromDB(String name) throws Exception {

        MongoClient mongoClient = new MongoClient();
        MongoDatabase database = mongoClient.getDatabase("Interview");
        MongoCollection<org.bson.Document> releaseCollection = database.getCollection("Release");
        FindIterable<org.bson.Document> releaseIterable;
        if (name != null) {
            releaseIterable = releaseCollection.find(new org.bson.Document("name", name));
        } else {
            releaseIterable = releaseCollection.find();
        }

        JSONArray releases = new JSONArray();
        releaseIterable.forEach(new Block<org.bson.Document>() {
            @Override
            public void apply(final org.bson.Document document) {
                JSONObject release = new JSONObject();
                release.put("name", document.getString("name"));
                release.put("url", document.getString("url"));
                releases.put(release);
            }
        });

        mongoClient.close();
        return releases;
    }

    public static JSONArray getPeriod(Session session) throws Exception {

        JSONArray PeriodArray = new JSONArray();
        MongoClient mongoClient = new MongoClient();
        MongoCollection<org.bson.Document> collection = mongoClient.getDatabase("Interview").getCollection("Sonar_Metric");
        org.bson.Document document = collection.find(new org.bson.Document("code", "new_coverage")).first();

        if (isCacheExpired(document, 24)) {

            BufferedReader br = getHttpURLConnection(LINK_GET_JIRA_PERIODS, session);

            String rs = "";
            String inputLine;
            while ((inputLine = br.readLine()) != null) {
                rs = rs + inputLine;
            }

            br.close();


            JSONArray jsonArray = new JSONArray(rs);
            JSONObject jsonObject;
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject = jsonArray.getJSONObject(i);
                if (jsonObject.getString("key").contains("sonar.timemachine.period")) {
                    JSONObject jsonObject1 = new JSONObject();
                    jsonObject1.put("value", jsonObject.getString("value"));
                    jsonObject1.put("key", jsonObject.getString("key").replace("sonar.timemachine.", ""));
                    PeriodArray.put(jsonObject1);
                }
            }


            collection.updateMany(new org.bson.Document(new org.bson.Document("code", "new_coverage")), new org.bson.Document("$set", new org.bson.Document("cache", PeriodArray.toString()).append("updateDate", new GregorianCalendar(Locale.getDefault()).getTimeInMillis())));


        } else {
            PeriodArray = new JSONArray(document.getString("cache"));
        }

        mongoClient.close();

        return PeriodArray;

    }

    private static boolean isCacheExpired(org.bson.Document document, int timeInHour) {
        try {
            GregorianCalendar latestUpdateTime = new GregorianCalendar(Locale.getDefault());
            latestUpdateTime.setTimeInMillis(document.getLong("updateDate"));
            latestUpdateTime.add(Calendar.HOUR, timeInHour);
            GregorianCalendar currentTime = new GregorianCalendar(Locale.getDefault());
            return latestUpdateTime.before(currentTime);
        } catch (Exception e) {
            return true;
        }
    }


    public static JSONObject getSonarStatistic(Session session, JSONObject data, String GadgetId) throws Exception {
        JSONObject result = new JSONObject();

        MongoClient mongoClient = new MongoClient();
        MongoCollection<org.bson.Document> collection = mongoClient.getDatabase("Interview").getCollection("DashboardGadget");
        org.bson.Document document = collection.find(new org.bson.Document("data", data.toString())).first();


        if (isCacheExpired(document, 3)) {
            result.put("id", GadgetId);
            result.put("release", data.getString("Release"));
            result.put("period", data.getString("Period"));
            JSONArray metricList = new JSONArray();

            JSONArray metricsFromDB = getMetricsFromDB();
            List metrics = Arrays.asList(data.getString("Metrics").split(","));

            for (int i = 0; i < metricsFromDB.length(); i++) {
                JSONObject metric = metricsFromDB.getJSONObject(i);
                if (metrics.contains(metric.getString("key"))) {
                    metricList.put(metric);
                }
            }

            result.put("metricList", metricList);

            JSONArray releases = getReleasesFromDB(data.getString("Release"));
            String releaseUrl = releases.getJSONObject(0).getString("url");

            JSONArray IAComponent = new JSONArray();

            try {
                IAComponent = getIAComponentsRespond(session, releaseUrl, data.getString("IANames"));
                //result.put("IAComponent", IAComponent);
            } catch (Exception e) {
                DashboardController.logger.error("Can not get IAComponent from " + releaseUrl, e);
            }


            JSONArray RsIAArray = new JSONArray();
            ArrayList<Thread> threads = new ArrayList<>();
            for (int i = 0; i < IAComponent.length(); i++) {
                getComponentInfo getComponentInfo = new getComponentInfo(IAComponent.getJSONObject(i), data.getString("Metrics"), data.getString("Period"), RsIAArray, session);
                threads.add(getComponentInfo);

            }

            for (Thread thread : threads) {
                thread.start();
            }
            for (Thread thread : threads) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    DashboardController.logger.error("cannot get statistic", e);
                }
            }
            //System.out.println("RsIAArray " + RsIAArray);
            result.put("RsIAArray", RsIAArray);

            collection.updateMany(new org.bson.Document("data", data.toString()), new org.bson.Document("$set", new org.bson.Document("cache", result.toString()).append("updateDate", new GregorianCalendar(Locale.getDefault()).getTimeInMillis())));

        } else {
            result = new JSONObject(document.getString("cache"));

        }


        mongoClient.close();


        return result;

    }

    static class getComponentInfo extends Thread {
        static final String NAME = "name";
        private org.json.JSONObject IAComponent;
        private String mt;
        private Session session;
        private org.json.JSONArray RsComponentArray;
        private String period;
        private JSONArray rs;

        public getComponentInfo(org.json.JSONObject IAComponent, String mt, String period, JSONArray rs, Session session) {
            this.IAComponent = IAComponent;
            this.mt = mt;
            this.session = session;
            this.period = period.replace("period", "");
            this.rs = rs;
        }

        @Override
        public void run() {
            super.run();
            org.json.JSONObject IA = new org.json.JSONObject();
            IA.put(NAME, IAComponent.getString("name"));
            org.json.JSONArray components = IAComponent.getJSONArray("Components");

            RsComponentArray = new org.json.JSONArray();
            ArrayList<Thread> threadArrayList = new ArrayList<>();
            for (int k = 0; k < components.length(); k++) {
                getMetricRs rs = new getMetricRs(components.getJSONObject(k).getString("component"));
                threadArrayList.add(rs);
            }

            for (Thread thread : threadArrayList) {
                thread.start();
            }
            for (Thread thread : threadArrayList) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    DashboardController.logger.error("cannot getComponentInfo", e);
                }
            }


            //System.out.println("RsComponentArray " + RsComponentArray);
            IA.put("Components", RsComponentArray);
            rs.put(IA);
        }


        private class getMetricRs extends Thread {
            String sonarKey;

            public getMetricRs(String sonarKey) {
                this.sonarKey = sonarKey;
            }

            @Override
            public void run() {
                super.run();
                try {
                    org.json.JSONObject RsComponent = new org.json.JSONObject();
                    org.json.JSONArray msr;
                    org.json.JSONArray sonarStatisticArray = getSonarStatisticRespond(session, mt, sonarKey);
                    List<String> metrics = Arrays.asList(mt.split(","));
                    String componentName = sonarStatisticArray.getJSONObject(0).getString(NAME);
                    RsComponent.put("componentName", componentName);
                    msr = sonarStatisticArray.getJSONObject(0).getJSONArray("msr");
                    org.json.JSONObject RsMetricArray = new org.json.JSONObject();
                    for (int p = 0; p < msr.length(); p++) {
                        String value = "";
                        org.json.JSONObject msrJSONObject = msr.getJSONObject(p);
                        try {
                            Double val;
                            if (msrJSONObject.getString("key").equals(MetricKey[2])) {
                                if (period != null) {
                                    val = msrJSONObject.getDouble("var" + period);
                                } else {
                                    val = msrJSONObject.getDouble("var1");
                                }
                            } else {
                                val = msrJSONObject.getDouble("val");
                            }

                            if (val > 100) {
                                val = val / 1000;
                            }

                            value = Math.round(val) + "";

                        } catch (Exception e) {
                            DashboardController.logger.warn("Can not getMetricRs", e);
                            value = "-";
                        }
                        RsMetricArray.put(msrJSONObject.getString("key"), value);
                        for (int i = 0; i < metrics.size(); i++) {
                            if (RsMetricArray.isNull(metrics.get(i))) {
                                RsMetricArray.put(metrics.get(i), "-");
                            }
                        }
                    }

                    RsComponent.put("metricVal", RsMetricArray);
                    RsComponentArray.put(RsComponent);
                } catch (Exception e) {
                    DashboardController.logger.error("Can not getMetricRs", e);
                }
            }

        }
    }


}

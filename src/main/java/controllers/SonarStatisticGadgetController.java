package controllers;

import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import filter.SecureFilter;
import ninja.FilterWith;
import ninja.Result;
import ninja.Results;
import ninja.params.Param;
import ninja.session.Session;
import org.apache.log4j.Logger;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;
import util.Constant;
import util.PropertiesUtil;

import java.io.BufferedReader;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static util.Constant.*;
import static util.MyUtill.getHttpURLConnection;
import static util.MyUtill.isCacheExpired;

/**
 * Created by nnmchau on 1/7/2017.
 */
public class SonarStatisticGadgetController {
    final static Logger logger = Logger.getLogger(SonarStatisticGadgetController.class);

    public static JSONObject getSonarStatistic(Session session, JSONObject data, String GadgetId) throws Exception {

        JSONObject result = new JSONObject();
        MongoClient mongoClient = new MongoClient();
        MongoCollection<Document> collection = mongoClient.getDatabase(PropertiesUtil.getString(Constant.DATABASE_SCHEMA)).getCollection(DASHBOAR_GADGET_COLECCTION);
        org.bson.Document document = collection.find(new org.bson.Document("data", data.toString())).first();


        if (isCacheExpired(document, 2)) {
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

            JSONArray releases = getReleasesFromDB(data.getString(RELEASE_TABLE));
            String releaseUrl = releases.getJSONObject(0).getString(Constant.RELEASE_URL);

            JSONArray IAComponent = new JSONArray();

            try {
                IAComponent = getIAComponentsRespond(session, releaseUrl, data.getString("IANames"));
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

            result.put("RsIAArray", RsIAArray);

            collection.updateMany(new org.bson.Document("data", data.toString()), new org.bson.Document(Constant.MONGODB_SET, new org.bson.Document("cache", result.toString()).append(Constant.UPDATE_DATE, new GregorianCalendar(Locale.getDefault()).getTimeInMillis())));

        } else {
            result = new JSONObject(document.getString("cache"));
        }


        long lastUpateTime;

        try {
            Calendar calendar = new GregorianCalendar(Locale.getDefault());
            Long millis = calendar.getTimeInMillis() - document.getLong(Constant.UPDATE_DATE);
            lastUpateTime = TimeUnit.MILLISECONDS.toMinutes(millis);
        } catch (Exception e) {
            lastUpateTime = 0;
        }


        result.put("id", GadgetId);
        result.put("release", data.getString("Release"));
        result.put("period", data.getString("Period"));
        result.put("lastUpateTime", lastUpateTime);

        mongoClient.close();

        return result;
    }

    public static JSONArray getMetricsFromDB() throws Exception {

        MongoClient mongoClient = new MongoClient();
        MongoCollection<org.bson.Document> metricCollection = mongoClient.getDatabase(PropertiesUtil.getString(Constant.DATABASE_SCHEMA)).getCollection(Constant.METRIC_TABLE);
        FindIterable<Document> metricIterable = metricCollection.find();
        JSONArray metrics = new JSONArray();
        metricIterable.forEach(new Block<Document>() {
            @Override
            public void apply(final org.bson.Document document) {
                JSONObject metric = new JSONObject();
                metric.put("id", document.getObjectId(Constant.MONGODB_ID));
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
        MongoDatabase database = mongoClient.getDatabase(PropertiesUtil.getString(Constant.DATABASE_SCHEMA));
        MongoCollection<org.bson.Document> releaseCollection = database.getCollection(RELEASE_TABLE);
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
                release.put("id", document.getObjectId(Constant.MONGODB_ID));
                release.put("name", document.getString("name"));
                release.put(Constant.RELEASE_URL, document.getString(Constant.RELEASE_URL));
                releases.put(release);
            }
        });

        mongoClient.close();
        return releases;
    }

    public static JSONArray getPeriod(Session session) throws Exception {

        JSONArray PeriodArray = new JSONArray();
        MongoClient mongoClient = new MongoClient();
        MongoCollection<org.bson.Document> collection = mongoClient.getDatabase(PropertiesUtil.getString(Constant.DATABASE_SCHEMA)).getCollection(METRIC_TABLE);
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


            collection.updateMany(new org.bson.Document(new org.bson.Document("code", "new_coverage")), new org.bson.Document(Constant.MONGODB_SET, new org.bson.Document("cache", PeriodArray.toString()).append(Constant.UPDATE_DATE, new GregorianCalendar(Locale.getDefault()).getTimeInMillis())));


        } else {
            PeriodArray = new JSONArray(document.getString("cache"));
        }

        mongoClient.close();

        return PeriodArray;

    }

    public static JSONArray getSonarStatisticRespond(Session session, String metric, String sonarKey) {
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


    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static JSONArray getIAComponentsRespond(Session session, String url, String iaList) throws Exception {
        JSONArray IAArray = new JSONArray();

        MongoClient mongoClient = new MongoClient();
        MongoCollection<org.bson.Document> collection = mongoClient.getDatabase(PropertiesUtil.getString(Constant.DATABASE_SCHEMA)).getCollection("Release");
        org.bson.Document document = collection.find(new org.bson.Document(Constant.RELEASE_URL, url)).first();

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

            collection.updateOne(new org.bson.Document(Constant.RELEASE_URL, url), new org.bson.Document(Constant.MONGODB_SET, new org.bson.Document("cache", IAArray.toString()).append(Constant.UPDATE_DATE, new GregorianCalendar(Locale.getDefault()).getTimeInMillis())));

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

    @FilterWith(SecureFilter.class)
    public Result addNewSonarWidget() {
        return Results.html();
    }

    @FilterWith(SecureFilter.class)
    public Result getIAComponents(Session session, @Param("data") String data) {
        try {
            return Results.text().render(getIAComponentsRespond(session, (new JSONObject(data)).getString(Constant.RELEASE_URL), null));
        } catch (Exception e) {
            logger.error(e);
            return Results.internalServerError();
        }
    }

    @FilterWith(SecureFilter.class)
    public Result getPeriodList(Session session) {
        try {
            return Results.text().render(getPeriod(session));
        } catch (Exception e) {
            logger.error(e);
            return Results.internalServerError();
        }
    }

    @FilterWith(SecureFilter.class)
    public Result getReleaseList() {
        try {
            return Results.text().render(getReleasesFromDB(null));
        } catch (Exception e) {
            logger.error(e);
            return Results.internalServerError();
        }
    }

    @FilterWith(SecureFilter.class)
    public Result getMetricList() {
        try {
            return Results.text().render(getMetricsFromDB());
        } catch (Exception e) {
            logger.error(e);
            return Results.internalServerError();
        }
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
                getComponentInfo.getMetricRs rs = new getComponentInfo.getMetricRs(components.getJSONObject(k).getString("component"));
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
                    RsComponent.put("componentKey", sonarKey);
                    msr = sonarStatisticArray.getJSONObject(0).getJSONArray("msr");
                    org.json.JSONObject RsMetricArray = new org.json.JSONObject();
                    for (int p = 0; p < msr.length(); p++) {
                        String value = "";
                        org.json.JSONObject msrJSONObject = msr.getJSONObject(p);
                        try {
                            Double val;
                            if (msrJSONObject.getString("key").equals(METRIC_KEY[2])) {
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
                            DashboardController.logger.info("No Metric Found");
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
                    DashboardController.logger.warn("No Metric Found");
                }
            }

        }
    }


}

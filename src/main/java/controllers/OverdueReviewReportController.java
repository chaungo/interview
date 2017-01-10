package controllers;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import filter.SecureFilter;
import ninja.FilterWith;
import ninja.Result;
import ninja.Results;
import ninja.session.Session;
import org.apache.log4j.Logger;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;

import java.io.BufferedReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static util.Constant.*;
import static util.MyUtill.getHttpURLConnection;
import static util.MyUtill.isCacheExpired;


/**
 * Created by nnmchau on 1/4/2017.
 */
public class OverdueReviewReportController {

    final static Logger logger = Logger.getLogger(OverdueReviewReportController.class);

    public static JSONArray getCruProjectfromServer(Session session) throws Exception {

        JSONArray projectDataArray = new JSONArray();

        MongoClient mongoClient = new MongoClient();
        MongoCollection<Document> collection = mongoClient.getDatabase("Interview").getCollection("Gadget");
        org.bson.Document document = collection.find(new org.bson.Document("Name", "AMS Overdue Reviews Report Gadget")).first();
        JSONObject cache;
        try {
            cache = new JSONObject(document.getString("cache"));
        } catch (Exception e) {
            logger.warn(e);
            cache = new JSONObject();
            cache.put("project", new JSONArray());
            cache.put("user", new JSONArray());
        }


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

        mongoClient.close();

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


    static class GetReviewThread extends Thread {
        private Session session;
        private JSONObject data;
        private String GadgetId;
        private JSONObject result;

        public GetReviewThread(Session session, JSONObject data, String GadgetId, JSONObject result) {
            this.session = session;
            this.data = data;
            this.GadgetId = GadgetId;
            this.result = result;
        }

        @Override
        public void run() {
            super.run();
            try {
                result.put("id", GadgetId);
                result.put("project", data.getString("Project"));
                MongoClient mongoClient = new MongoClient();
                MongoCollection<org.bson.Document> collection = mongoClient.getDatabase("Interview").getCollection("DashboardGadget");
                org.bson.Document document = collection.find(new org.bson.Document("data", data.toString())).first();

                if (isCacheExpired(document, 3)) {
                    JSONArray ReviewDataArray = getReviewfromServer(session, data.getString("Project"));
                    result.put("ReviewDataArray", ReviewDataArray);

                    collection.updateMany(new org.bson.Document("data", data.toString()), new org.bson.Document("$set", new org.bson.Document("cache", result.toString()).append("updateDate", new GregorianCalendar(Locale.getDefault()).getTimeInMillis())));
                } else {
                    result = new JSONObject(document.getString("cache"));
                }
            } catch (Exception e) {
                logger.error(e);
            }
        }
    }


    public static JSONObject getReview(Session session, JSONObject data, String GadgetId) throws Exception {
        JSONObject result = new JSONObject();
        GetReviewThread getReviewThread = new GetReviewThread(session, data, GadgetId, result);
        getReviewThread.start();
        getReviewThread.join();
        return result;
    }

    public static JSONArray getReviewfromServer(Session session, String project) throws Exception {
        JSONArray reviewDataArray = new JSONArray();
        String rs = "";
        BufferedReader br = getHttpURLConnection(String.format(LINK_GET_ODREVIEW_REPORTS, project), session);
        String inputLine;
        while ((inputLine = br.readLine()) != null) {
            rs = rs + inputLine;
        }
        br.close();


        JSONArray array = XML.toJSONObject(rs).getJSONObject("detailedReviews").getJSONArray("detailedReviewData");


        ArrayList<String> nameList = new ArrayList<>();

        for (int i = 0; i < array.length(); i++) {
            String name = array.getJSONObject(i).getJSONObject("creator").getString("displayName");
            if (!nameList.contains(name)) {
                nameList.add(name);
            }
        }

        for (int i = 0; i < nameList.size(); i++) {
            JSONObject reviewData = new JSONObject();
            int lessThan5 = 0;
            int moreThan5Less10 = 0;
            int moreThan10 = 0;
            for (int j = 0; j < array.length(); j++) {
                JSONObject detailedReviewData = array.getJSONObject(j);
                String name = detailedReviewData.getJSONObject("creator").getString("displayName");
                if (nameList.get(i).equals(name)) {
                    String createDate = detailedReviewData.getString("createDate").substring(0, 10);
                    DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    Date date = formatter.parse(createDate);
                    Date currentDate = new Date();
                    Long dif = currentDate.getTime() - date.getTime();
                    long days = TimeUnit.DAYS.convert(dif, TimeUnit.MILLISECONDS);

                    if (days > 10) {
                        moreThan10++;
                    } else {
                        if (days < 5) {
                            lessThan5++;
                        } else {
                            moreThan5Less10++;
                        }
                    }
                }
            }

            reviewData.put("creator", nameList.get(i));
            reviewData.put("col1", lessThan5);
            reviewData.put("col2", moreThan5Less10);
            reviewData.put("col3", moreThan10);
            ////System.out.println(reviewData.toString());
            reviewDataArray.put(reviewData);
        }


        return reviewDataArray;
    }

    @FilterWith(SecureFilter.class)
    public Result getCruProjectList(Session session) {
        try {
            return Results.text().render(getCruProjectfromServer(session));
        } catch (Exception e) {
            logger.warn(e);
            return Results.internalServerError();
        }
    }

    @FilterWith(SecureFilter.class)
    public Result getCruUserList(Session session) {
        try {
            return Results.text().render(getCruUserfromServer(session));
        } catch (Exception e) {
            logger.error(e);
            return Results.internalServerError();
        }
    }
}

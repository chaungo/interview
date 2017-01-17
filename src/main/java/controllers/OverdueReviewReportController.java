package controllers;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import filter.SecureFilter;
import ninja.FilterWith;
import ninja.Result;
import ninja.Results;
import ninja.session.Session;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;
import util.Constant;
import util.PropertiesUtil;

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

        String rs = "";
        BufferedReader br = getHttpURLConnection(LINK_GET_CRU_PROJECTS, session);
        String inputLine;

        while ((inputLine = br.readLine()) != null) {
            rs = rs + inputLine;
        }

        br.close();
        JSONArray dataArray = new JSONArray(rs);

        for (int i = 0; i < dataArray.length(); i++) {
            JSONObject project = dataArray.getJSONObject(i);
            JSONObject projectInfo = new JSONObject();
            projectInfo.put("id", project.getString("id"));
            projectInfo.put("name", project.getString("displaySecondary"));
            projectDataArray.put(projectInfo);
        }


        return projectDataArray;
    }

    public static JSONArray getCruUserfromServer(Session session) throws Exception {

        JSONArray userArray = new JSONArray();


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
                userInfo.put(Constant.NAME, user.getString("displayPrimary"));
                userArray.put(userInfo);
            }
        }

        return userArray;
    }

    public static JSONObject getReview(Session session, JSONObject data, String GadgetId) throws Exception {
        JSONObject result = new JSONObject();
        MongoClient mongoClient = new MongoClient();
        MongoCollection<org.bson.Document> collection = mongoClient.getDatabase(PropertiesUtil.getString(Constant.DATABASE_SCHEMA)).getCollection(Constant.DASHBOAR_GADGET_COLECCTION);
        org.bson.Document document = collection.find(new org.bson.Document("data", data.toString())).first();

        if (isCacheExpired(document, 3)) {
            JSONArray ReviewDataArray = getReviewfromServer(session, data.getString("Project"));
            result.put("ReviewDataArray", ReviewDataArray);

            collection.updateMany(new org.bson.Document("data", data.toString()), new org.bson.Document(Constant.MONGODB_SET, new org.bson.Document("cache", result.toString()).append(Constant.UPDATE_DATE, new GregorianCalendar(Locale.getDefault()).getTimeInMillis())));
        } else {
            result = new JSONObject(document.getString("cache"));
        }

        result.put("id", GadgetId);
        result.put("project", data.getString("Project"));

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
            String name = array.getJSONObject(i).getJSONObject("creator").getString(Constant.DISPLAY_NAME);
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
                String name = detailedReviewData.getJSONObject("creator").getString(Constant.DISPLAY_NAME);
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
            logger.error(e);
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

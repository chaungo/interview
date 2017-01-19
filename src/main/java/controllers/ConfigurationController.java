package controllers;

import com.google.inject.Singleton;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import filter.AdminSecureFilter;
import filter.SecureFilter;
import ninja.FilterWith;
import ninja.Result;
import ninja.Results;
import ninja.params.Param;
import ninja.session.Session;
import org.apache.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONObject;
import util.AdminUtility;
import util.Constant;
import util.PropertiesUtil;

import java.io.BufferedReader;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Set;

import static util.Constant.*;
import static util.MyUtill.getHttpURLConnection;
import static util.MyUtill.isCacheExpired;

@Singleton
public class ConfigurationController {

    final static Logger logger = Logger.getLogger(ConfigurationController.class);

    public static JSONObject getPeriod(Session session) throws Exception {
        JSONObject res = new JSONObject();
        //todo
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

        res.put("PeriodArray", PeriodArray);
        res.put("CurrentPeriod", document.getString("period"));

        mongoClient.close();

        return res;

    }

    @FilterWith(SecureFilter.class)
    public Result configuration() {
        Set<String> products = AdminUtility.getInstance().getAllProduct();
        Set<String> releases = AdminUtility.getInstance().getAllRelease();
        return Results.html().render("isProductPage", true).render("products", products).render("releases", releases);
    }

    @FilterWith(AdminSecureFilter.class)
    public Result addNewRelease(@Param(NAME) String name, @Param("url") String url) {
        MongoClient mongoClient = new MongoClient();

        MongoCollection<Document> collection = mongoClient.getDatabase(PropertiesUtil.getString(Constant.DATABASE_SCHEMA)).getCollection(RELEASE_TABLE);
        collection.insertOne(new Document(NAME, name).append(Constant.RELEASE_URL, url));
        mongoClient.close();
        return Results.ok();
    }

    @FilterWith(AdminSecureFilter.class)
    public Result updateRelease(@Param("id") String id, @Param("name") String name, @Param("url") String url) {
        MongoClient mongoClient = new MongoClient();
        MongoCollection<Document> collection = mongoClient.getDatabase(PropertiesUtil.getString(Constant.DATABASE_SCHEMA)).getCollection(RELEASE_TABLE);
        collection.updateOne(new Document(MONGODB_ID, new ObjectId(id)), new Document(Constant.MONGODB_SET, new Document("name", name).append(Constant.RELEASE_URL, url)));
        mongoClient.close();
        return Results.ok();
    }

    @FilterWith(AdminSecureFilter.class)
    public Result deleteRelease(@Param("url") String url) {
        MongoClient mongoClient = new MongoClient();
        MongoCollection<Document> collection = mongoClient.getDatabase(PropertiesUtil.getString(Constant.DATABASE_SCHEMA)).getCollection(RELEASE_TABLE);
        collection.deleteOne(new Document(Constant.RELEASE_URL, url));
        mongoClient.close();
        return Results.ok();
    }

    @FilterWith(AdminSecureFilter.class)
    public Result getPeriodList(Session session) {
        try {
            return Results.text().render(getPeriod(session));
        } catch (Exception e) {
            logger.error(e);
            return Results.internalServerError();
        }
    }

    @FilterWith(AdminSecureFilter.class)
    public Result setPeriod(@Param("period") String period) {
        try {
            MongoClient mongoClient = new MongoClient();
            MongoCollection<org.bson.Document> collection = mongoClient.getDatabase(PropertiesUtil.getString(Constant.DATABASE_SCHEMA)).getCollection(Constant.METRIC_TABLE);
            collection.updateMany(new org.bson.Document(new org.bson.Document("code", "new_coverage")), new org.bson.Document(Constant.MONGODB_SET, new org.bson.Document("period", period)));
            mongoClient.close();
            return Results.ok();
        } catch (Exception e) {
            logger.error(e);
            return Results.internalServerError();
        }
    }


}

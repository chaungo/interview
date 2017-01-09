package service;

import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import models.gadget.Gadget;
import models.gadget.GadgetAPI;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONObject;
import util.gadget.GadgetUtility;

import java.util.Iterator;
import java.util.List;

/**
 * Created by nnmchau on 1/7/2017.
 */
public class GadgetService {
    public static void insertDashboardGadgettoDB(String dashboardId, String name, String data) {
        MongoClient mongoClient = new MongoClient();
        MongoCollection<Document> collection = mongoClient.getDatabase("Interview").getCollection("DashboardGadget");
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
        ////System.out.println(gadgetId);
        MongoClient mongoClient = new MongoClient();
        MongoCollection<org.bson.Document> gadgetCollection = mongoClient.getDatabase("Interview").getCollection("DashboardGadget");
        FindIterable<Document> gadgetIterable = gadgetCollection.find(new org.bson.Document("_id", new ObjectId(gadgetId)));
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


    public static List<Gadget> getDashboardGadgetbyDashboardId(String dashboardId) throws Exception {
        List<Gadget> gadgets = GadgetUtility.getInstance().findByDashboardId(dashboardId);
        return gadgets;
    }

    public static JSONArray getGadgetListfromDB() throws Exception {
        JSONArray gadgets = new JSONArray();
        Iterator<GadgetAPI> iterator = GadgetAPI.getIterator();
        while(iterator.hasNext()){
            GadgetAPI gadgetObj = iterator.next();
            JSONObject gadget = new JSONObject();
            gadget.put("name", gadgetObj.getName());
            gadget.put("des", gadgetObj.getDescription());
            gadget.put("author", gadgetObj.getAuthor());
            gadget.put("img", gadgetObj.getPictureUrl());
            gadget.put("addnewUIurl", gadgetObj.getAddnewUIurl());
            gadget.put("type", gadgetObj.getType().toString());
            gadgets.put(gadget);
        }
        return gadgets;
    }
}
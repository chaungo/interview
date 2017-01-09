package controllers;

import com.google.inject.Singleton;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.UpdateOptions;
import filter.AdminSecureFilter;
import filter.SecureFilter;
import ninja.FilterWith;
import ninja.Result;
import ninja.Results;
import ninja.params.Param;
import ninja.params.PathParam;
import org.apache.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Set;
import java.util.TreeSet;

@Singleton
public class ConfigurationController {

    final static Logger logger = Logger.getLogger(ConfigurationController.class);

    @FilterWith(AdminSecureFilter.class)
    public Result configuration() {
        return Results.html();
    }


    @FilterWith(AdminSecureFilter.class)
    public Result addNewRelease(@Param("name") String name, @Param("url") String url) {
        MongoClient mongoClient = new MongoClient();
        MongoCollection<Document> collection = mongoClient.getDatabase("Interview").getCollection("Release");
        collection.insertOne(new Document("name", name).append("url", url));
        mongoClient.close();
        return Results.ok();
    }

    @FilterWith(AdminSecureFilter.class)
    public Result updateRelease(@Param("id") String id, @Param("name") String name, @Param("url") String url) {
        MongoClient mongoClient = new MongoClient();
        MongoCollection<Document> collection = mongoClient.getDatabase("Interview").getCollection("Release");
        collection.updateOne(new Document("_id", new ObjectId(id)), new Document("$set", new Document("name", name).append("url", url)));
        mongoClient.close();
        return Results.ok();
    }

    @FilterWith(AdminSecureFilter.class)
    public Result deleteRelease(@Param("url") String url) {
        MongoClient mongoClient = new MongoClient();
        MongoCollection<Document> collection = mongoClient.getDatabase("Interview").getCollection("Release");
        collection.deleteOne(new Document("url", url));
        mongoClient.close();
        return Results.ok();
    }


    @FilterWith(AdminSecureFilter.class)
    public Result addNewMetric(@Param("name") String name, @Param("key") String key) {
        MongoClient mongoClient = new MongoClient();
        MongoCollection<Document> collection = mongoClient.getDatabase("Interview").getCollection("Sonar_Metric");
        collection.insertOne(new Document("name", name).append("code", key));
        mongoClient.close();
        return Results.ok();
    }

    @FilterWith(AdminSecureFilter.class)
    public Result updateMetric(@Param("id") String id, @Param("name") String name, @Param("key") String key) {
        MongoClient mongoClient = new MongoClient();
        MongoCollection<Document> collection = mongoClient.getDatabase("Interview").getCollection("Sonar_Metric");
        collection.updateOne(new Document("_id", new ObjectId(id)), new Document("$set", new Document("name", name).append("code", key)));
        mongoClient.close();
        return Results.ok();
    }

    @FilterWith(AdminSecureFilter.class)
    public Result deleteMetric(@Param("key") String key) {
        MongoClient mongoClient = new MongoClient();
        MongoCollection<Document> collection = mongoClient.getDatabase("Interview").getCollection("Sonar_Metric");
        collection.deleteOne(new Document("code", key));
        mongoClient.close();
        return Results.ok();
    }

}

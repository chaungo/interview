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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

@Singleton
public class ConfigurationController {

    final static Logger logger = Logger.getLogger(ConfigurationController.class);

    @FilterWith(AdminSecureFilter.class)
    public Result configuration() {
        return Results.html();
    }


    @FilterWith(AdminSecureFilter.class)
    public Result releasePost(@Param("name") String name, @Param("url") String url) {
        MongoClient mongoClient = new MongoClient();
        MongoCollection<Document> collection = mongoClient.getDatabase("Interview").getCollection("Release");
        collection.insertOne(new Document("name", name).append("url", url));
        mongoClient.close();
        return Results.redirect("/release");
    }

    @FilterWith(AdminSecureFilter.class)
    public Result releaseUpdate(@Param("name") String name, @Param("url") String url) {
        MongoClient mongoClient = new MongoClient();
        MongoCollection<Document> collection = mongoClient.getDatabase("Interview").getCollection("Release");
        collection.updateOne(new Document("name", name), new Document("$set", new Document("name", name).append("url", url)), new UpdateOptions().upsert(true));
        mongoClient.close();
        return Results.redirect("/release");
    }

    @FilterWith(AdminSecureFilter.class)
    public Result releaseDelete(@Param("name") String name, @Param("url") String url) {
        MongoClient mongoClient = new MongoClient();
        MongoCollection<Document> collection = mongoClient.getDatabase("Interview").getCollection("Release");
        collection.deleteOne(new Document("name", name).append("url", url));
        mongoClient.close();
        return Results.redirect("/release");
    }

    @FilterWith(AdminSecureFilter.class)
    public Result metric() {
        MongoClient mongoClient = new MongoClient();
        MongoCollection<Document> collection = mongoClient.getDatabase("Interview").getCollection("Sonar_Metric");
        FindIterable<Document> iterable = collection.find();
        List<Map<String, Object>> metrics = new ArrayList<>();

        for (Document document : iterable) {
            Map<String, Object> map = new HashMap<>();
            map.put("name", document.get("name"));
            map.put("code", document.get("code"));
            metrics.add(map);
        }

        logger.info("metrics " + metrics);

        mongoClient.close();
        return Results.html().render("metrics", metrics);
    }

    @FilterWith(AdminSecureFilter.class)
    public Result metricPost(@Param("name") String name, @Param("code") String code) {
        MongoClient mongoClient = new MongoClient();
        MongoCollection<Document> collection = mongoClient.getDatabase("Interview").getCollection("Sonar_Metric");
        collection.insertOne(new Document("name", name).append("code", code));
        mongoClient.close();
        return Results.redirect("/metric");
    }

    @FilterWith(AdminSecureFilter.class)
    public Result metricUpdate(@Param("name") String name, @Param("code") String code) {
        MongoClient mongoClient = new MongoClient();
        MongoCollection<Document> collection = mongoClient.getDatabase("Interview").getCollection("Sonar_Metric");
        collection.updateOne(new Document("name", name), new Document("$set", new Document("name", name).append("code", code)), new UpdateOptions().upsert(true));
        mongoClient.close();
        return Results.redirect("/metric");
    }

    @FilterWith(AdminSecureFilter.class)
    public Result metricDelete(@Param("name") String name, @Param("code") String code) {
        MongoClient mongoClient = new MongoClient();
        MongoCollection<Document> collection = mongoClient.getDatabase("Interview").getCollection("Sonar_Metric");
        collection.deleteOne(new Document("name", name).append("code", code));
        mongoClient.close();
        return Results.redirect("/metric");
    }

    @FilterWith(SecureFilter.class)
    public Result releaseURL(@PathParam("name") String name) {
        MongoClient mongoClient = new MongoClient();
        MongoCollection<Document> collection = mongoClient.getDatabase("Interview").getCollection("Release");
        FindIterable<Document> iterable = collection.find(new Document("name", name));

        String url = null;
        for (Document document : iterable) {
            url = document.getString("url");
        }
        mongoClient.close();
        Set<String> set = new TreeSet<>();
        URL url2;
        try {
            url2 = new URL(url);
            URLConnection conn = url2.openConnection();
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;

            while ((inputLine = br.readLine()) != null) {
                String[] arr = inputLine.split(",");
                set.add(arr[0]);
            }
            br.close();
        } catch (MalformedURLException e) {
            logger.error("MalformedURLException " + e);
        } catch (IOException e) {
            logger.error("IOException " + e);
        }
        logger.info(String.format("Get ia list from url %s \n%s", url, set));
        return Results.text().render(set);
    }

}

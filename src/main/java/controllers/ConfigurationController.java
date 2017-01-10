package controllers;

import com.google.inject.Singleton;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import filter.AdminSecureFilter;
import ninja.FilterWith;
import ninja.Result;
import ninja.Results;
import ninja.params.Param;
import org.apache.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;
import util.Constant;
import util.PropertiesUtil;

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
        MongoCollection<Document> collection = mongoClient.getDatabase(PropertiesUtil.getString(Constant.DATABASE_SCHEMA)).getCollection("Release");
        collection.insertOne(new Document("name", name).append("url", url));
        mongoClient.close();
        return Results.ok();
    }

    @FilterWith(AdminSecureFilter.class)
    public Result updateRelease(@Param("id") String id, @Param("name") String name, @Param("url") String url) {
        MongoClient mongoClient = new MongoClient();
        MongoCollection<Document> collection = mongoClient.getDatabase(PropertiesUtil.getString(Constant.DATABASE_SCHEMA)).getCollection("Release");
        collection.updateOne(new Document("_id", new ObjectId(id)), new Document("$set", new Document("name", name).append("url", url)));
        mongoClient.close();
        return Results.ok();
    }

    @FilterWith(AdminSecureFilter.class)
    public Result deleteRelease(@Param("url") String url) {
        MongoClient mongoClient = new MongoClient();
        MongoCollection<Document> collection = mongoClient.getDatabase(PropertiesUtil.getString(Constant.DATABASE_SCHEMA)).getCollection("Release");
        collection.deleteOne(new Document("url", url));
        mongoClient.close();
        return Results.ok();
    }


}

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
import util.AdminUtility;
import util.Constant;
import util.PropertiesUtil;

import java.util.Set;

import static util.Constant.NAME;
import static util.Constant.MONGODB_ID;
import static util.Constant.RELEASE_TABLE;

@Singleton
public class ConfigurationController {

    final static Logger logger = Logger.getLogger(ConfigurationController.class);

    @FilterWith(AdminSecureFilter.class)
    public Result configuration() {
        Set<String> products = AdminUtility.getInstance().getAllProduct();
        Set<String> cycles = AdminUtility.getInstance().getAllCycle();
        return Results.html().render("isProductPage", true).render("products", products).render("cycles", cycles);
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


}

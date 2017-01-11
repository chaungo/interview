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
import static util.Constant.mongoId;
import static util.Constant.releaseTable;

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

        MongoCollection<Document> collection = mongoClient.getDatabase(PropertiesUtil.getString(Constant.DATABASE_SCHEMA)).getCollection(releaseTable);
        collection.insertOne(new Document(NAME, name).append(Constant.releaseUrl, url));
        mongoClient.close();
        return Results.ok();
    }

    @FilterWith(AdminSecureFilter.class)
    public Result updateRelease(@Param("id") String id, @Param("name") String name, @Param("url") String url) {
        MongoClient mongoClient = new MongoClient();
        MongoCollection<Document> collection = mongoClient.getDatabase(PropertiesUtil.getString(Constant.DATABASE_SCHEMA)).getCollection(releaseTable);
        collection.updateOne(new Document(mongoId, new ObjectId(id)), new Document(Constant.mongoSet, new Document("name", name).append(Constant.releaseUrl, url)));
        mongoClient.close();
        return Results.ok();
    }

    @FilterWith(AdminSecureFilter.class)
    public Result deleteRelease(@Param("url") String url) {
        MongoClient mongoClient = new MongoClient();
        MongoCollection<Document> collection = mongoClient.getDatabase(PropertiesUtil.getString(Constant.DATABASE_SCHEMA)).getCollection(releaseTable);
        collection.deleteOne(new Document(Constant.releaseUrl, url));
        mongoClient.close();
        return Results.ok();
    }


}

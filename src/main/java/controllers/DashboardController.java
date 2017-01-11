package controllers;

import com.google.inject.Singleton;
import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import filter.SecureFilter;
import models.gadget.Gadget;
import models.gadget.Gadget.Type;
import ninja.FilterWith;
import ninja.Result;
import ninja.Results;
import ninja.params.Param;
import ninja.params.SessionParam;
import ninja.session.Session;
import org.apache.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONObject;
import util.Constant;
import util.PropertiesUtil;

import java.util.List;

import static service.GadgetService.getDashboardGadgetbyDashboardId;
import static util.Constant.gadget;

@Singleton
public class DashboardController {

    public final static Logger logger = Logger.getLogger(DashboardController.class);

    @FilterWith(SecureFilter.class)
    public Result getDashboardInfo(@Param("id") String id) {

        if (id == null) {
            return Results.noContent();
        }

        JSONObject info = new JSONObject();
        List<Gadget> dashboardGadgets;
        try {
            dashboardGadgets = getDashboardGadgetbyDashboardId(id);

            info.put(gadget, dashboardGadgets.size());

            int sonarGadget = 0;
            int reviewGadget = 0;

            for (Gadget gadget : dashboardGadgets) {
                Type type = gadget.getType();
                if (Type.AMS_SONAR_STATISTICS_GADGET.equals(type)) {
                    sonarGadget++;
                }
                if (Type.AMS_OVERDUE_REVIEWS.equals(type)) {
                    reviewGadget++;
                }
            }
            //todo

            info.put(Constant.sonarGadget, sonarGadget);

            info.put(Constant.reviewGadget, reviewGadget);

        } catch (Exception e) {
            logger.error("getDashboardInfo ", e);
            return Results.internalServerError();
        }

        return Results.text().render(info);
    }

    @FilterWith(SecureFilter.class)
    public Result getDashboardList(@Param("groups") String groups, @Param("projects") String projects, Session session) {
        try {

            JSONArray userGroup = new JSONArray(groups);
            JSONArray userProject = new JSONArray(projects);

            List<Object> groupsNprojects = userGroup.toList();
            groupsNprojects.addAll(userProject.toList());

            JSONArray dashboardList = new JSONArray();
            MongoClient mongoClient = new MongoClient();
            MongoCollection<Document> collection = mongoClient.getDatabase(PropertiesUtil.getString(Constant.DATABASE_SCHEMA)).getCollection("Dashboard");
            FindIterable<Document> iterable = collection.find();

            iterable.forEach(new Block<Document>() {
                @Override
                public void apply(final Document document) {
                    try {
                        JSONObject dashboard = new JSONObject();


                        String owner = document.getString(Constant.owner);

                        JSONObject privacy = new JSONObject(document.getString(Constant.privacy));


                        Boolean contain = false;
                        try {

                            List<Object> share = privacy.getJSONArray(Constant.share).toList();
                            for (Object o : share) {
                                if (groupsNprojects.contains(o)) {
                                    contain = true;
                                    break;
                                }
                            }

                        } catch (Exception e) {
                            logger.warn(e);
                        }


                        if (contain || owner.equals(session.get("username")) || privacy.getString("status").equals("public")) {

                            dashboard.put("id", document.getObjectId(Constant.mongoId).toHexString());
                            dashboard.put(Constant.owner, owner);
                            dashboard.put("name", document.get("dashboard_name"));
                            dashboard.put(Constant.privacy, privacy);
                            dashboardList.put(dashboard);
                        }


                    } catch (Exception exception) {
                        logger.error(exception);
                    }

                }
            });


            logger.info("DASHBOARDS " + dashboardList);
            mongoClient.close();
            return Results.text().render(dashboardList);
        } catch (Exception e) {
            logger.error(e);
            return Results.internalServerError();
        }
    }

    @FilterWith(SecureFilter.class)
    public Result updateDashboardOption(@Param("id") String dashboardId, @Param("name") String dashboardName, @Param("privacy") String privacy) {
        try {
            MongoClient mongoClient = new MongoClient();
            MongoCollection<org.bson.Document> collection = mongoClient.getDatabase(PropertiesUtil.getString(Constant.DATABASE_SCHEMA)).getCollection("Dashboard");
            org.bson.Document doc = new org.bson.Document("dashboard_name", dashboardName).append(Constant.privacy, privacy);
            collection.updateOne(new org.bson.Document(Constant.mongoId, new ObjectId(dashboardId)), new org.bson.Document("$set", doc));
            mongoClient.close();

            return Results.ok();
        } catch (Exception e) {
            logger.error(e);
            return Results.internalServerError();
        }

    }

    @FilterWith(SecureFilter.class)
    public Result deleteDashboard(@Param("id") String dashboardId, Session session) {
        try {
            MongoClient mongoClient = new MongoClient();
            MongoCollection<org.bson.Document> collection = mongoClient.getDatabase(PropertiesUtil.getString(Constant.DATABASE_SCHEMA)).getCollection("Dashboard");
            Document document = collection.find(new org.bson.Document(Constant.mongoId, new ObjectId(dashboardId))).first();

            if (document.getString("owner").equals(session.get("username"))) {
                collection.deleteOne(new org.bson.Document(Constant.mongoId, new ObjectId(dashboardId)));
                mongoClient.getDatabase(PropertiesUtil.getString(Constant.DATABASE_SCHEMA)).getCollection("DashboardGadget").deleteMany(new org.bson.Document("dashboardId", dashboardId));
            }

            mongoClient.close();

            return Results.ok();
        } catch (Exception e) {
            logger.error(e);
            return Results.internalServerError();
        }
    }

    @FilterWith(SecureFilter.class)
    public Result new_dashboard() {
        return Results.html();
    }


    @FilterWith(SecureFilter.class)
    public Result new_dashboard_post(@SessionParam("username") String username,
                                     @Param("name") String name) {
        try {
            MongoClient mongoClient = new MongoClient();
            MongoCollection<Document> collection = mongoClient.getDatabase(PropertiesUtil.getString(Constant.DATABASE_SCHEMA)).getCollection("Dashboard");

            JSONObject privacy = new JSONObject();
            privacy.put("status", "private");
            privacy.put("share", new JSONArray());

            collection.insertOne(new org.bson.Document(Constant.mongoId, new ObjectId()).append(Constant.owner, username).append("dashboard_name", name).append("privacy", privacy.toString()));
            mongoClient.close();

            return Results.redirect("/");
        } catch (Exception e) {
            logger.error(e);
            return Results.internalServerError();
        }
    }
}

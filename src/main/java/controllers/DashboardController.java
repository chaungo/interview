package controllers;

import com.google.inject.Singleton;
import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import filter.AdminSecureFilter;
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
import static util.Constant.*;
import static util.MyUtill.isAdmin;

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

            info.put(GADGET, dashboardGadgets.size());

            int sonarGadget = 0;
            int reviewGadget = 0;
            int greenHopper = 0;
            for (Gadget gadget : dashboardGadgets) {
                Type type = gadget.getType();
                if (Type.AMS_SONAR_STATISTICS_GADGET.equals(type)) {
                    sonarGadget++;
                } else if (Type.AMS_OVERDUE_REVIEWS.equals(type)) {
                    reviewGadget++;
                } else {
                    greenHopper++;
                }

            }
            //todo


            info.put(Constant.SONAR_GADGET, sonarGadget);
            info.put(Constant.REVIEW_GADGET, reviewGadget);
            info.put(Constant.GREENHOPPER_GADGET, greenHopper);

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

            MongoCollection<Document> collection = mongoClient.getDatabase(PropertiesUtil.getString(Constant.DATABASE_SCHEMA)).getCollection(DASHBOARD_TABLE);
            FindIterable<Document> iterable = collection.find();

            iterable.forEach(new Block<Document>() {
                @Override
                public void apply(final Document document) {
                    try {
                        JSONObject dashboard = new JSONObject();
                        String owner = document.getString(Constant.OWNER);
                        JSONObject privacy = new JSONObject(document.getString(Constant.PRIVACY));
                        Boolean contain = false;
                        try {
                            List<Object> share = privacy.getJSONArray(Constant.SHARE_OPTION).toList();
                            for (Object o : share) {
                                if (groupsNprojects.contains(o)) {
                                    contain = true;
                                    break;
                                }
                            }

                        } catch (Exception e) {
                            logger.warn(e);
                        }

                        if (!isAdmin(session)) {
                            if (contain || owner.equals(session.get(USERNAME)) || privacy.getString(PRIVACY_STATUS).equals(PRIVACY_STATUS_PUBLIC)) {
                                dashboard.put("id", document.getObjectId(Constant.MONGODB_ID).toHexString());
                                dashboard.put(Constant.OWNER, owner);
                                dashboard.put(NAME, document.get(DASHBOARD_NAME_COL));
                                dashboard.put(Constant.PRIVACY, privacy);
                                dashboardList.put(dashboard);
                            }
                        } else {
                            dashboard.put("id", document.getObjectId(Constant.MONGODB_ID).toHexString());
                            dashboard.put(Constant.OWNER, owner);
                            dashboard.put(NAME, document.get(DASHBOARD_NAME_COL));
                            dashboard.put(Constant.PRIVACY, privacy);
                            dashboardList.put(dashboard);
                        }


                    } catch (Exception exception) {
                        logger.error("error when getDashboardList()", exception);
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
            MongoCollection<org.bson.Document> collection = mongoClient.getDatabase(PropertiesUtil.getString(Constant.DATABASE_SCHEMA)).getCollection(DASHBOARD_TABLE);

            org.bson.Document doc = new org.bson.Document(DASHBOARD_NAME_COL, dashboardName).append(Constant.PRIVACY, privacy);
            collection.updateOne(new org.bson.Document(Constant.MONGODB_ID, new ObjectId(dashboardId)), new org.bson.Document(Constant.MONGODB_SET, doc));
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
            MongoCollection<org.bson.Document> collection = mongoClient.getDatabase(PropertiesUtil.getString(Constant.DATABASE_SCHEMA)).getCollection(DASHBOARD_TABLE);
            Document document = collection.find(new org.bson.Document(Constant.MONGODB_ID, new ObjectId(dashboardId))).first();

            if (isAdmin(session)) {
                collection.deleteOne(new org.bson.Document(Constant.MONGODB_ID, new ObjectId(dashboardId)));
                mongoClient.getDatabase(PropertiesUtil.getString(Constant.DATABASE_SCHEMA)).getCollection(DASHBOARD_GADGET_COLECCTION).deleteMany(new org.bson.Document(Constant.DASHBOARD_ID, dashboardId));
            } else {
                if (document.getString(Constant.OWNER).equals(session.get(USERNAME))) {
                    collection.deleteOne(new org.bson.Document(Constant.MONGODB_ID, new ObjectId(dashboardId)));
                    mongoClient.getDatabase(PropertiesUtil.getString(Constant.DATABASE_SCHEMA)).getCollection(DASHBOARD_GADGET_COLECCTION).deleteMany(new org.bson.Document(Constant.DASHBOARD_ID, dashboardId));
                }
            }

            mongoClient.close();

            return Results.ok();
        } catch (Exception e) {
            logger.error(e);
            return Results.internalServerError();
        }
    }

    @FilterWith(AdminSecureFilter.class)
    public Result deleteAllDashboard() {
        try {
            MongoClient mongoClient = new MongoClient();

            MongoCollection<org.bson.Document> collection = mongoClient.getDatabase(PropertiesUtil.getString(Constant.DATABASE_SCHEMA)).getCollection(DASHBOARD_TABLE);
            collection.drop();

            MongoCollection<org.bson.Document> gadgetCollection = mongoClient.getDatabase(PropertiesUtil.getString(Constant.DATABASE_SCHEMA)).getCollection(DASHBOARD_GADGET_COLECCTION);
            gadgetCollection.drop();

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
            MongoCollection<Document> collection = mongoClient.getDatabase(PropertiesUtil.getString(Constant.DATABASE_SCHEMA)).getCollection(DASHBOARD_TABLE);

            JSONObject privacy = new JSONObject();

            privacy.put(PRIVACY_STATUS, PRIVACY_STATUS_PRIVATE);
            privacy.put(SHARE_OPTION, new JSONArray());

            collection.insertOne(new org.bson.Document(Constant.MONGODB_ID, new ObjectId()).append(Constant.OWNER, username).append(DASHBOARD_NAME_COL, name).append(Constant.PRIVACY, privacy.toString()));
            mongoClient.close();

            return Results.redirect("/");
        } catch (Exception e) {
            logger.error(e);
            return Results.internalServerError();
        }
    }
}

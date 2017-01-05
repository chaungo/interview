package controllers;

import filter.SecureFilter;
import ninja.FilterWith;
import ninja.Result;
import ninja.Results;
import ninja.params.Param;
import ninja.session.Session;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import static util.MyUtill.*;

public class ApplicationController {

    final static Logger logger = Logger.getLogger(ApplicationController.class);

    @FilterWith(SecureFilter.class)
    public Result getUserInfo(Session session) {
        try {
            JSONObject info = getUserInformation(session);
            JSONObject userInfo = new JSONObject();
            userInfo.put("displayName", info.getString("alias"));
            userInfo.put("groups", new JSONArray(info.getString("groups")));
            userInfo.put("role", info.getString("role"));
            userInfo.put("name", session.get("username"));
            userInfo.put("projects", getJiraProjectofUserfromServer(session));
            return Results.text().render(userInfo);
        } catch (Exception e) {
            logger.error(e);
            return Results.internalServerError();
        }

    }

    @FilterWith(SecureFilter.class)
    public Result getProjectList(Session session) {
        try {
            return Results.text().render(getJiraProjectofUserfromServer(session));
        } catch (Exception e) {
            logger.error(e);
            return Results.internalServerError();
        }

    }

    @FilterWith(SecureFilter.class)
    public Result index() {
        return Results.html();
    }


    @FilterWith(SecureFilter.class)
    public Result addNewSonarWidget() {
        return Results.html();
    }


    @FilterWith(SecureFilter.class)
    public Result getIAComponents(Session session, @Param("data") String data) {
        try {
            return Results.text().render(getIAComponentsRespond(session, (new JSONObject(data)).getString("url"), null));
        } catch (Exception e) {
            e.printStackTrace();
            return Results.internalServerError();
        }
    }

    @FilterWith(SecureFilter.class)
    public Result getPeriodList(Session session) {
        try {
            return Results.text().render(getPeriod(session));

        } catch (Exception e) {
            e.printStackTrace();
            return Results.internalServerError();
        }
    }


    @FilterWith(SecureFilter.class)
    public Result getReleaseList() {
        try {
            return Results.text().render(getReleasesFromDB(null));
        } catch (Exception e) {
            e.printStackTrace();
            return Results.internalServerError();
        }
    }

    @FilterWith(SecureFilter.class)
    public Result getMetricList() {
        try {
            return Results.text().render(getMetricsFromDB());
        } catch (Exception e) {
            e.printStackTrace();
            return Results.internalServerError();
        }
    }


}

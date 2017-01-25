package controllers;

import filter.SecureFilter;
import ninja.FilterWith;
import ninja.Result;
import ninja.Results;
import ninja.session.Session;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import util.Constant;

import static service.UserService.getJiraProjectofUserfromServer;
import static service.UserService.getUserInformation;
import static util.Constant.*;


public class ApplicationController {

    final static Logger logger = Logger.getLogger(ApplicationController.class);


    @FilterWith(SecureFilter.class)
    public Result getUserInfo(Session session) {
        try {
            JSONObject info = getUserInformation(session);
            JSONObject userInfo = new JSONObject();
            userInfo.put(DISPLAY_NAME, info.getString(Constant.ALIAS));
            userInfo.put(Constant.GROUPS, new JSONArray(info.getString(USER_GROUPS)));
            userInfo.put(ADMIN, info.getBoolean(ADMIN));
            userInfo.put(Constant.NAME, session.get(USERNAME));
            userInfo.put(USER_PROJECTS, getJiraProjectofUserfromServer(session));
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

}

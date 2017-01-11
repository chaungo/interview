package controllers;

import filter.SecureFilter;
import ninja.FilterWith;
import ninja.Result;
import ninja.Results;
import ninja.session.Session;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import service.HTTPClientUtil;
import util.Constant;
import util.MyUtill;

import java.io.BufferedReader;
import java.net.Proxy;

import static util.Constant.*;


public class ApplicationController {

    final static Logger logger = Logger.getLogger(ApplicationController.class);

    public static JSONObject getUserInformation(Session session) throws Exception {
        JSONObject userInfoRS = new JSONObject();
        Proxy proxy = HTTPClientUtil.getInstance().getProxy();
        Connection req = Jsoup
                .connect(String.format(LINK_GET_JIRA_USER_INFO, session.get(Constant.USERNAME)))
                .cookies(MyUtill.getCookies(session)).timeout(CONNECTION_TIMEOUT).ignoreContentType(true)
                .ignoreHttpErrors(true);
        if (proxy != null) {
            req.proxy(proxy);
        }
        Document response = req.get();
        JSONObject userInfo = new JSONObject(response.body().text());
        userInfoRS.put("alias", userInfo.getString(DisplayName));

        JSONArray groups = userInfo.getJSONObject(Groups).getJSONArray(GroupsItems);

        JSONArray groupNames = new JSONArray();
        userInfoRS.put(Constant.ROLE, "");
        for (int i = 0; i < groups.length(); i++) {
            JSONObject group = groups.getJSONObject(i);
            if (group.getString(Constant.NAME).contains("jira-administrators")) {
                session.put(Constant.ROLE, "jira-administrators");
                userInfoRS.put(Constant.ROLE, "jira-administrators");
            }

            groupNames.put(group.getString(Constant.NAME));
        }

        userInfoRS.put(Constant.GROUPS, groupNames.toString());
        return userInfoRS;
    }

    public static JSONArray getJiraProjectofUserfromServer(Session session) throws Exception {
        JSONArray projectDataArray = new JSONArray();
        String rs = "";
        BufferedReader br = MyUtill.getHttpURLConnection(LINK_GET_JIRA_PROJECTS, session);
        String inputLine;
        while ((inputLine = br.readLine()) != null) {
            rs = rs + inputLine;
        }
        br.close();

        JSONArray dataArray = new JSONArray(rs);

        for (int i = 0; i < dataArray.length(); i++) {
            JSONObject project = dataArray.getJSONObject(i);
            projectDataArray.put(project.getString(Constant.NAME));
        }

        return projectDataArray;
    }

    @FilterWith(SecureFilter.class)
    public Result getUserInfo(Session session) {
        try {
            JSONObject info = getUserInformation(session);
            JSONObject userInfo = new JSONObject();
            userInfo.put("displayName", info.getString("alias"));
            userInfo.put(Constant.GROUPS, new JSONArray(info.getString("groups")));
            userInfo.put(Constant.ROLE, info.getString("role"));
            userInfo.put(Constant.NAME, session.get("username"));
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


}

package service;

import ninja.session.Session;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import util.Constant;
import util.MyUtill;

import java.net.Proxy;

import static util.Constant.*;
import static util.Constant.ADMIN;
import static util.MyUtill.getConnectionRespondBody;

/**
 * Created by nnmchau on 1/25/2017.
 */
public class UserService {

    public static JSONObject getUserInformation(Session session) throws Exception {
        JSONObject userInfoRS = new JSONObject();
        Proxy proxy = HTTPClientUtil.getInstance().getProxy();
        Connection req = Jsoup
                .connect(String.format(Constant.LINK_GET_JIRA_USER_INFO, session.get(USERNAME)))
                .cookies(MyUtill.getCookies(session)).timeout(Constant.CONNECTION_TIMEOUT).ignoreContentType(true)
                .ignoreHttpErrors(true);
        if (proxy != null) {
            req.proxy(proxy);
        }
        Document response = req.get();
        JSONObject userInfo = new JSONObject(response.body().text());

        userInfoRS.put(Constant.ALIAS, userInfo.getString(Constant.DISPLAY_NAME));

        JSONArray groups = userInfo.getJSONObject(Constant.GROUPS).getJSONArray(Constant.GROUP_ITEMS);

        JSONArray groupNames = new JSONArray();
        userInfoRS.put(ADMIN, false);

        for (int i = 0; i < groups.length(); i++) {
            JSONObject group = groups.getJSONObject(i);
            if (group.getString(Constant.NAME).contains(ADMIN_GROUP)) {
                session.put(ROLE, ADMIN);
                userInfoRS.put(ADMIN, true);
            }
            groupNames.put(group.getString(Constant.NAME));
        }

        userInfoRS.put(Constant.GROUPS, groupNames.toString());
        return userInfoRS;
    }


    public static JSONArray getJiraProjectofUserfromServer(Session session) throws Exception {
        JSONArray projectDataArray = new JSONArray();

        String rs = getConnectionRespondBody(Constant.LINK_GET_JIRA_PROJECTS, session);

        JSONArray dataArray = new JSONArray(rs);

        for (int i = 0; i < dataArray.length(); i++) {
            JSONObject project = dataArray.getJSONObject(i);
            projectDataArray.put(project.getString(Constant.NAME));
        }

        return projectDataArray;
    }
}

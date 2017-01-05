//package service;
//
//import org.apache.log4j.Logger;
//import org.json.simple.JSONArray;
//import org.json.simple.JSONObject;
//import org.json.simple.parser.JSONParser;
//import org.json.simple.parser.ParseException;
//import org.jsoup.nodes.Document;
//import util.Constant;
//import util.LinkUtil;
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Set;
//import java.util.TreeSet;
//
//public class RESTServiceImpl implements RESTService {
//
//    final static Logger logger = Logger.getLogger(RESTServiceImpl.class);
//
//    private Document doc;
//    private String json;
//    private JSONParser parser;
//
//    public Map<String, Object> getUserInfo(String username) {
//        Map<String, Object> map = new HashMap<>();
//        doc = LinkUtil.getConnection(String.format(Constant.LINK_GET_JIRA_USER_INFO, username), Constant.TOKEN);
//        json = doc.body().text();
//        parser = new JSONParser();
//        try {
//            JSONObject jsonObject = (JSONObject) parser.parse(json);
//            map.put("displayName", jsonObject.get("displayName"));
//            map.put("emailAddress", jsonObject.get("emailAddress"));
//            JSONObject groups = (JSONObject) jsonObject.get("groups");
//            JSONArray items = (JSONArray) groups.get("items");
//            JSONObject item;
//            Set<Object> set = new TreeSet<>();
//            for (int i = 0; i < items.size(); i++) {
//                item = (JSONObject) items.get(i);
//                set.add(item.get("name"));
//            }
//            map.put("groups", set);
//        } catch (ParseException e) {
//            logger.error(String.format("GET_USER_INFO OF %s ERROR ", username, e));
//        }
//        return map;
//    }
//
//    public Set<Object> getProjects() {
//        Set<Object> set = new TreeSet<>();
//        doc = LinkUtil.getConnection(Constant.LINK_GET_JIRA_PROJECTS, Constant.TOKEN);
//        json = doc.body().text();
//        parser = new JSONParser();
//        try {
//            JSONArray items = (JSONArray) parser.parse(json);
//            JSONObject item;
//            for (int i = 0; i < items.size(); i++) {
//                item = (JSONObject) items.get(i);
//                set.add(item.get("name"));
//            }
//        } catch (ParseException e) {
//            logger.error("GET_PROJECTS_ERROR " + e);
//        }
//        return set;
//    }
//
//    public Set<Object> getGroups() {
//        Set<Object> set = new TreeSet<>();
//        doc = LinkUtil.getConnection(Constant.LINK_GET_JIRA_GROUPS, Constant.TOKEN);
//        json = doc.body().text();
//        parser = new JSONParser();
//        try {
//            JSONObject jsonObject = (JSONObject) parser.parse(json);
//            JSONArray groups = (JSONArray) jsonObject.get("groups");
//            JSONObject group;
//            for (int i = 0; i < groups.size(); i++) {
//                group = (JSONObject) groups.get(i);
//                set.add(group.get("name"));
//            }
//        } catch (ParseException e) {
//            logger.error("GET_PROJECTS_ERROR " + e);
//        }
//        return set;
//    }
//
//    public Set<Object> getUsersOfGroup(String groupName) {
//        Set<Object> set = new TreeSet<>();
//        doc = LinkUtil.getConnection(String.format(Constant.LINK_GET_JIRA_USERS_OF_GROUP, groupName), Constant.TOKEN);
//        json = doc.body().text();
//        parser = new JSONParser();
//        try {
//            JSONObject jsonObject = (JSONObject) parser.parse(json);
//            JSONObject users = (JSONObject) jsonObject.get("users");
//            JSONArray items = (JSONArray) users.get("items");
//            JSONObject item;
//            for (int i = 0; i < items.size(); i++) {
//                item = (JSONObject) items.get(i);
//                set.add(item.get("name"));
//            }
//        } catch (ParseException e) {
//            logger.error("GET_USERS_OF_GROUP_ERROR " + e);
//        }
//        return set;
//    }
//
//    public Set<Object> getUsersOfProject(String projectName) {
//        RESTService service = new RESTServiceImpl();
//        Object projectUrl = getProjectURL(projectName);
//        Set<Object> roles = getRolesOfProject(projectUrl.toString());
//        Set<Object> set = new TreeSet<>();
//        for (Object role : roles) {
//            Set<Object> actors = getActorsOfRole(role.toString());
//            for (Object actor : actors) {
//                if (actor.toString().contains("-")) {
//                    Set<Object> users = service.getUsersOfGroup(actor.toString());
//                    for (Object user : users) {
//                        set.add(user);
//                    }
//                } else {
//                    set.add(actor);
//                }
//            }
//        }
//        return set;
//    }
//
//    public Object getProjectURL(Object projectName) {
//        Object projUrl = "";
//        doc = LinkUtil.getConnection(Constant.LINK_GET_JIRA_PROJECTS, Constant.TOKEN);
//        json = doc.body().text();
//        parser = new JSONParser();
//        try {
//            JSONArray projects = (JSONArray) parser.parse(json);
//            JSONObject project;
//            for (int i = 0; i < projects.size(); i++) {
//                project = (JSONObject) projects.get(i);
//                if (project.get("name").equals(projectName)) {
//                    projUrl = project.get("self");
//                }
//            }
//        } catch (ParseException e) {
//            logger.error(String.format("GET PROJECT %s error %s", projectName, e));
//        }
//        return projUrl;
//    }
//
//    public Set<Object> getRolesOfProject(String projectUrl) {
//        Set<Object> set = new TreeSet<>();
//        doc = LinkUtil.getConnection(projectUrl, Constant.TOKEN);
//        String json = doc.body().text();
//        parser = new JSONParser();
//        try {
//            JSONObject project = (JSONObject) parser.parse(json);
//            JSONObject roles = (JSONObject) project.get("roles");
//            if (roles.containsKey("Users")) {
//                set.add(roles.get("Users"));
//            }
//            if (roles.containsKey("Administrators")) {
//                set.add(roles.get("Administrators"));
//            }
//            if (roles.containsKey("Hiring Project Users")) {
//                set.add(roles.get("Hiring Project Users"));
//            }
//            if (roles.containsKey("Developers")) {
//                set.add(roles.get("Developers"));
//            }
//            if (roles.containsKey("Hiring Project Admin")) {
//                set.add(roles.get("Hiring Project Admin"));
//            }
//        } catch (ParseException e) {
//            logger.error(String.format("GET ROLES OF PROJECT_URL %s error %s", projectUrl, e));
//        }
//        return set;
//    }
//
//    public Set<Object> getActorsOfRole(String roleUrl) {
//        Set<Object> set = new TreeSet<>();
//        doc = LinkUtil.getConnection(roleUrl, Constant.TOKEN);
//        String json = doc.body().text();
//        parser = new JSONParser();
//        try {
//            JSONObject role = (JSONObject) parser.parse(json);
//            JSONArray actors = (JSONArray) role.get("actors");
//            JSONObject actor;
//            for (int j = 0; j < actors.size(); j++) {
//                actor = (JSONObject) actors.get(j);
//                if (actor.containsKey("name")) {
//                    set.add(actor.get("name"));
//                }
//            }
//        } catch (ParseException e) {
//            logger.error(String.format("GET ACTORS OF ROLE_URL %s error %s", roleUrl, e));
//        }
//        return set;
//    }
//}

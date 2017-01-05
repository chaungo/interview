package util.gadget;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.result.DeleteResult;

import manament.log.LoggerWapper;
import models.APIIssueVO;
import models.ExecutionIssueVO;
import models.JQLIssueVO;
import models.ProjectVO;
import models.exception.APIErrorCode;
import models.exception.APIException;
import models.gadget.AssigneeVsTestExecution;
import models.gadget.CycleVsTestExecution;
import models.gadget.EpicVsTestExecution;
import models.gadget.Gadget;
import models.gadget.StoryVsTestExecution;
import models.main.GadgetData;
import models.main.JQLSearchResult;
import models.main.Release;
import service.DatabaseUtility;
import service.HTTPClientUtil;
import util.Constant;
import util.JSONUtil;
import util.PropertiesUtil;

public class GadgetUtility extends DatabaseUtility {
    final static LoggerWapper logger = LoggerWapper.getLogger(GadgetUtility.class);
    private static final String TYPE = "type";
    private static List<String> projectsCache = new ArrayList<>();
    private static GadgetUtility INSTANCE = new GadgetUtility();

    protected MongoCollection<Document> collection;

    private GadgetUtility() {
        super();
        collection = db.getCollection(Gadget.class.getSimpleName());
    }

    public static GadgetUtility getInstance() {
        return INSTANCE;
    }

    public static void main(String[] args) throws APIException {
        Set<String> epic = new HashSet<>();
        epic.add("FNMS-96");

        Set<String> story = new HashSet<>();
        story.add("FNMS-1507");

        List<String> metrics = new ArrayList<>();
        metrics.add("unexecuted");
        metrics.add("failed");
        metrics.add("wip");
        metrics.add("blocked");
        metrics.add("passed");
        metrics.add("planned");

        Set<String> products = new HashSet<>();
        products.add("PCC");

        StoryVsTestExecution gadget = new StoryVsTestExecution();
        gadget.setDashboardId("6");
        gadget.setEpic(epic);
        gadget.setMetrics(metrics);
        gadget.setProducts(products);
        gadget.setProjectName("FNMS 557x");
        // gadget.setSelectAllStory(true);
        // gadget.setStories(story);
        gadget.setUser("tducle");
        gadget.setId("5865c9978dbec7462029d419");
        gadget.setRelease(Release.R1_2_0);
        // GadgetUtility.getInstance().insertOrUpdate(gadget);
    }

    public String insertOrUpdate(Gadget gadget) throws APIException {
        if(gadget == null){
            throw new APIException("cannot insert gadget:"+ gadget);
        }
        String id = gadget.getId();
        Gadget existingGadget = null;
        try {
            if (id != null) {
                existingGadget = get(id);
            }
        } catch (APIException e) {
            // ingnore exception
        }
        try {
            Document dbObject = Document.parse(mapper.writeValueAsString(gadget));
            dbObject.remove("id");
            if (existingGadget != null) {
                BasicDBObject updateQuery = new BasicDBObject();
                updateQuery.append("$set", dbObject);
                BasicDBObject searchQuery = new BasicDBObject();
                searchQuery.append("_id", new ObjectId(id));
                logger.fasttrace("update gadget id %s by user:%s", id, gadget.getUser());
                collection.updateOne(searchQuery, updateQuery);
                return id;
            } else {
                logger.fasttrace("insert gadget:%s by user:%s", gadget.getType(), gadget.getUser());
                ObjectId idObj = new ObjectId();
                dbObject.append("_id", idObj);
                collection.insertOne(dbObject);
                return idObj.toString();
            }
        } catch (JsonProcessingException e) {
            logger.fastDebug("error during mapper.writeValueAsString", e);
            throw new APIException("cannot insert gadget", e);
        }
    }

    public Gadget get(String gadgetId) throws APIException {
        Gadget gadget = null;
        BasicDBObject query = getQueryById(gadgetId);
        FindIterable<Document> document = collection.find(query);
        Document dbObj = document.first();

        if (dbObj != null && Gadget.Type.valueOf(((String) dbObj.get("type"))) != null) {
            Gadget.Type type = Gadget.Type.valueOf(((String) dbObj.get("type")));
            try {
                if (type == Gadget.Type.EPIC_US_TEST_EXECUTION) {
                    EpicVsTestExecution epicGadget = mapper.readValue(dbObj.toJson(),
                            EpicVsTestExecution.class);
                    epicGadget.setId(getObjectId(dbObj));
                    gadget = epicGadget;

                } else if (type == Gadget.Type.ASSIGNEE_TEST_EXECUTION) {
                    AssigneeVsTestExecution assigneeGadget = mapper.readValue(dbObj.toJson(),
                            AssigneeVsTestExecution.class);
                    assigneeGadget.setId(getObjectId(dbObj));
                    gadget = assigneeGadget;
                } else if (type == Gadget.Type.TEST_CYCLE_TEST_EXECUTION) {
                    CycleVsTestExecution cycleGadget = mapper.readValue(dbObj.toJson(),
                            CycleVsTestExecution.class);
                    cycleGadget.setId(getObjectId(dbObj));
                    gadget = cycleGadget;
                } else if (type == Gadget.Type.STORY_TEST_EXECUTION) {
                    StoryVsTestExecution storyGadget = mapper.readValue(dbObj.toJson(),
                            StoryVsTestExecution.class);
                    storyGadget.setId(getObjectId(dbObj));
                    gadget = storyGadget;
                }

            } catch (IOException e) {
                logger.fastDebug("Error during loading gadget", e);
                throw new APIException("Error during loading gadget", e);
            }
        } else {
            logger.fasttrace("gadget id %s not found", gadgetId);
        }
        return gadget;
    }

    private BasicDBObject getQueryById(String id) throws APIException {
        BasicDBObject query = new BasicDBObject();
        try {
            query.put("_id", new ObjectId(id));
        } catch (java.lang.IllegalArgumentException e) {
            logger.fasttrace("gadget id %s not found", id);
            throw new APIException("gadget id=" + id + " not found", e);
        }
        return query;
    }

    public List<Gadget> getAll() throws APIException {
        FindIterable<Document> documents = collection.find();
        return parse(documents);
    }

    public List<Gadget> findByDashboardId(String id) throws APIException {
        BasicDBObject searchQuery = new BasicDBObject();
        searchQuery.append(Constant.DASHBOAR_ID, id);
        FindIterable<Document> documents = collection.find(searchQuery);
        return parse(documents);
    }

    private List<Gadget> parse(FindIterable<Document> documents) throws APIException {
        MongoCursor<Document> dbCursor = documents.iterator();
        List<Gadget> gadgets = new ArrayList<Gadget>();
        while (dbCursor.hasNext()) {
            Document document = dbCursor.next();
            if (document != null) {
                if (Gadget.Type.ASSIGNEE_TEST_EXECUTION
                        .equals(Gadget.Type.valueOf((String) document.get(TYPE)))) {
                    AssigneeVsTestExecution assigneeGadget = JSONUtil.getInstance()
                            .convertJSONtoObject(document.toJson(), AssigneeVsTestExecution.class);
                    assigneeGadget.setId(getObjectId(document));
                    gadgets.add(assigneeGadget);
                } else if (Gadget.Type.EPIC_US_TEST_EXECUTION
                        .equals(Gadget.Type.valueOf((String) document.get(TYPE)))) {
                    EpicVsTestExecution epicGadget = JSONUtil.getInstance()
                            .convertJSONtoObject(document.toJson(), EpicVsTestExecution.class);
                    epicGadget.setId(getObjectId(document));
                    gadgets.add(epicGadget);
                } else if (Gadget.Type.TEST_CYCLE_TEST_EXECUTION
                        .equals(Gadget.Type.valueOf((String) document.get(TYPE)))) {
                    CycleVsTestExecution cyclGadget = JSONUtil.getInstance()
                            .convertJSONtoObject(document.toJson(), CycleVsTestExecution.class);
                    cyclGadget.setId(getObjectId(document));
                    gadgets.add(cyclGadget);
                } else if (Gadget.Type.STORY_TEST_EXECUTION
                        .equals(Gadget.Type.valueOf((String) document.get(TYPE)))) {
                    StoryVsTestExecution storyGadget = JSONUtil.getInstance()
                            .convertJSONtoObject(document.toJson(), StoryVsTestExecution.class);
                    storyGadget.setId(getObjectId(document));
                    gadgets.add(storyGadget);
                } else {
                    logger.fastDebug("type %s is not available", document.get(TYPE));
                }
            }
        }
        return gadgets;
    }

    public GadgetData convertToGadgetData(List<ExecutionIssueVO> testExecution) {
        GadgetData gadgetData = new GadgetData();
        if (testExecution != null) {
            testExecution.forEach(new Consumer<ExecutionIssueVO>() {
                @Override
                public void accept(ExecutionIssueVO issue) {
                    switch (issue.getStatus().getName()) {
                    case "PASS":
                        gadgetData.increasePassed(1);
                        gadgetData.getPassed().getIssues().add(issue.getIssueKey());
                        break;
                    case "FAIL":
                        gadgetData.increaseFailed(1);
                        gadgetData.getFailed().getIssues().add(issue.getIssueKey());
                        break;
                    case "UNEXECUTED":
                        gadgetData.increaseUnexecuted(1);
                        gadgetData.getUnexecuted().getIssues().add(issue.getIssueKey());
                        break;
                    case "WIP":
                        gadgetData.increaseWip(1);
                        gadgetData.getWip().getIssues().add(issue.getIssueKey());
                        break;
                    case "BLOCKED":
                        gadgetData.increaseBlocked(1);
                        gadgetData.getBlocked().getIssues().add(issue.getIssueKey());
                        break;
                    default:
                        break;
                    }
                }
            });
        }
        gadgetData.increaseUnplanned(gadgetData.getBlocked().getTotal()
                + gadgetData.getFailed().getTotal() + gadgetData.getPassed().getTotal()
                + gadgetData.getWip().getTotal() + gadgetData.getUnexecuted().getTotal());
        gadgetData.getUnplanned().getIssues().addAll(gadgetData.getBlocked().getIssues());
        gadgetData.getUnplanned().getIssues().addAll(gadgetData.getFailed().getIssues());
        gadgetData.getUnplanned().getIssues().addAll(gadgetData.getPassed().getIssues());
        gadgetData.getUnplanned().getIssues().addAll(gadgetData.getWip().getIssues());
        gadgetData.getUnplanned().getIssues().addAll(gadgetData.getUnexecuted().getIssues());
        return gadgetData;
    }

    public void sortData(List<GadgetData> gadgetDatas) {
        if (gadgetDatas != null && !gadgetDatas.isEmpty()) {
            Collections.sort(gadgetDatas,
                    (d1, d2) -> d1.getKey().getKey().compareToIgnoreCase(d2.getKey().getKey()));
        }
    }

    public JQLIssueVO findIssue(String issueKey,  Map<String, String> cookies) throws APIException {
        JQLSearchResult searchResult = null;
        String query = "issue=%s";
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put(Constant.PARAMERTER_JQL_QUERY, String.format(query, issueKey));
        parameters.put(Constant.PARAMERTER_MAXRESULTS,
                PropertiesUtil.getString(Constant.RESOURCE_BUNLE_SEARCH_MAXRECORDS,
                        Constant.RESOURCE_BUNLE_SEARCH_MAXRECORDS_DEFAULT));
        parameters.put(Constant.PARAMERTER_OFFSET, "0");
        String data = HTTPClientUtil.getInstance().getLegacyData(
                PropertiesUtil.getString(Constant.RESOURCE_BUNLE_SEARCH_PATH), parameters, cookies);
        try {
            searchResult = JSONUtil.getInstance().convertJSONtoObject(data, JQLSearchResult.class);
        } catch (APIException e) {
            // ignore exception, issue not found.
            if (!APIErrorCode.PARSE_JSON.equals(e.getErrorCode())) {
                return null;
            } else {
                throw e;
            }
        }
        return searchResult.getIssues().get(0);
    }

    public List<String> getProjectList(Map<String, String> cookies) throws APIException {
        if (projectsCache.isEmpty()) {
            String data = HTTPClientUtil.getInstance().getLegacyData(
                    PropertiesUtil.getString(Constant.RESOURCE_BUNLE_PROJECT_PATH),
                    new HashMap<String, String>(), cookies);
            List<ProjectVO> projects = JSONUtil.getInstance().convertJSONtoListObject(data,
                    ProjectVO.class);

            Set<String> projectsList = projects.stream().map(p -> p.getName())
                    .filter(t -> t != null && !t.isEmpty()).collect(Collectors.toSet());
            projectsCache = projectsList.stream().sorted(new Comparator<String>() {
                @Override
                public int compare(String p1, String p2) {
                    if (Constant.MAIN_PROJECT.equals(p1)) {
                        return -1;
                    }
                    if (Constant.MAIN_PROJECT.equals(p2)) {
                        return 1;
                    }
                    return p1.compareToIgnoreCase(p2);
                }
            }).collect(Collectors.toList());

        }
        logger.fastDebug(projectsCache.toString());
        return projectsCache;
    }

    public List<JQLIssueVO> filterProduct(List<JQLIssueVO> issues, Set<String> product) {
        if (issues != null && product != null) {
            return issues.stream()
                    .filter(i -> product.contains(i.getFields().getProduct().getValue()))
                    .collect(Collectors.toList());
        }
        return null;
    }

    public void sortIssue(List<APIIssueVO> result) {
        if (result != null && !result.isEmpty()) {
            Collections.sort(result, (d1, d2) -> d1.getKey().compareToIgnoreCase(d2.getKey()));
        }
    }

    public long delete(String id) throws APIException {
        BasicDBObject query = getQueryById(id);
        DeleteResult res = collection.deleteOne(query);
        return res.getDeletedCount();
    }
    
    public void clearCache(){
        projectsCache.clear();
    }
}

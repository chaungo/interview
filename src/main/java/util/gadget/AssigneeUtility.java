package util.gadget;

import handle.executors.ExecutorManagement;
import handle.executors.TestExecutionCallable;
import handle.scheduler.GadgetCacheMap;
import manament.log.LoggerWapper;
import models.*;
import models.exception.APIException;
import models.gadget.AssigneeVsTestExecution;
import models.main.*;
import models.main.DataCacheVO.State;
import service.HTTPClientUtil;
import util.Constant;
import util.JSONUtil;
import util.PropertiesUtil;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AssigneeUtility {
    final static LoggerWapper logger = LoggerWapper.getLogger(AssigneeUtility.class);
    private static final String PLUS = "+";
    private static AssigneeUtility INSTANCE = new AssigneeUtility();
    private static GadgetCacheMap<Set<String>> cycleNameCache = new GadgetCacheMap<>(PropertiesUtil.getInt(Constant.CLEAN_CACHE_TIME, 24)*60, "CycleCacheCleaner");
    private static GadgetCacheMap<Set<AssigneeVO>> assigneesCache = new GadgetCacheMap<>(PropertiesUtil.getInt(Constant.CLEAN_CACHE_TIME, 24)*60, "AssigneeCacheCleaner");

    private AssigneeUtility() {
    }

    public static AssigneeUtility getInstance() {
        return INSTANCE;
    }

    public Map<String, GadgetDataWrapper> getDataAssignee(AssigneeVsTestExecution assigneeGadget, SessionInfo sessionInfo) throws APIException {
        Map<String, GadgetDataWrapper> returnData = new HashMap<>();

        String projectName = assigneeGadget.getProjectName();
//        Set<AssigneeVO> assigneeVOs = findAssigneeList(projectName, assigneeGadget.getRelease(), sessionInfo);
//        Set<String> assignees = assigneeVOs.stream().map(a -> a.getDisplay()).collect(Collectors.toSet());
        Set<String> cycles = assigneeGadget.getCycles();

        if (assigneeGadget.isSelectAllTestCycle()) {
            cycles = getListCycleName(projectName, assigneeGadget.getRelease(), assigneeGadget.getProducts(), sessionInfo);
        }
        if (cycles != null && !cycles.isEmpty()) {
            for (String cycle : cycles) {
                ExecutionsVO executions = findExecution(projectName, cycle, null, sessionInfo.getCookies());
                if (executions != null && executions.getExecutions() != null) {
                    Map<String, List<ExecutionIssueVO>> assigneeMap = executions.getExecutions().stream()
                            .collect(Collectors.groupingBy(ExecutionIssueVO::getAssigneeDisplay));
                    List<GadgetData> gadgetDatas = new ArrayList<GadgetData>();
                    for (String assignee : assigneeMap.keySet()) {
                        GadgetData gadgetData = GadgetUtility.getInstance().convertToGadgetData(assigneeMap.get(assignee));
                        gadgetData.setKey(new APIIssueVO(assignee, null));
                        gadgetDatas.add(gadgetData);
                    }
                    // sorting
                    GadgetUtility.getInstance().sortData(gadgetDatas);

                    GadgetDataWrapper gadgetDataWrapper = new GadgetDataWrapper();
                    gadgetDataWrapper.setIssueData(gadgetDatas);
                    returnData.put(cycle, gadgetDataWrapper);
                }
            }
        } else {
            logger.fastDebug("No Test Cycle in gadget %s", assigneeGadget.getId());
        }
        return returnData;
    }

    public Set<AssigneeVO> findAssigneeList(String projectName, String release, SessionInfo sessionInfo) throws APIException {
        String cacheKey = projectName + PLUS + release + Constant.DELIMITER + sessionInfo.getUsername();
        Set<AssigneeVO> returnData = new HashSet<>();
        DataCacheVO<Set<AssigneeVO>> dataCache = assigneesCache.get(cacheKey);
        boolean found = false;
        if(dataCache != null){
            long begin = System.currentTimeMillis();
            int timeout = PropertiesUtil.getInt(Constant.PARAMERTER_TIMEOUT);

            while (!State.SUCCESS.equals(dataCache.getState())){
                if(begin + timeout < System.currentTimeMillis()){
                    logger.fastDebug("timeout when waiting cache");
                    return returnData;
                }
                try{
                    Thread.sleep(800);
                } catch (InterruptedException e){
                    logger.fastDebug("Thread interrupted", e, new Object());
                }
            }
            returnData = dataCache.getData();
            found = true;
        }

        if(!found){
            DataCacheVO<Set<AssigneeVO>> value = new DataCacheVO<Set<AssigneeVO>>();
            assigneesCache.put(cacheKey, value);
            try{
                ExecutionsVO executions = findAllExecutionIsueeInProject(projectName, release, sessionInfo.getCookies());
                if(executions != null && executions.getExecutions() != null){
                    List<ExecutionIssueVO> excutions = executions.getExecutions();
                    Stream<ExecutionIssueVO> excutionsStream = excutions.stream();
                    returnData = excutionsStream.filter(e -> (e.getAssigneeUserName() != null && !e.getAssigneeUserName().isEmpty()))
                            .map(new Function<ExecutionIssueVO, AssigneeVO>() {
                                @Override
                                public AssigneeVO apply(ExecutionIssueVO issueVO) {
                                    AssigneeVO assigneeVO = new AssigneeVO(issueVO.getAssignee(), issueVO.getAssigneeUserName(), issueVO.getAssigneeDisplay());
                                    return assigneeVO;
                                }
                            }).collect(Collectors.toSet());
                }
            } finally{
                value.setData(returnData);
                value.setState(State.SUCCESS);
            }
        }
        return returnData;

    }

    public ExecutionsVO findExecution(String project, String cyclename, Set<String> assignees, Map<String, String> cookies) throws APIException {
        StringBuilder query = new StringBuilder();
        if (project != null) {
            query.append(String.format("project = \"%s\"", project));
        }
        if (assignees != null && !assignees.isEmpty()) {
            if (project != null) {
                query.append(Constant.AND);
            }
            query.append("(");
            boolean first = true;
            for (String assignee : assignees) {
                if (!first) {
                    query.append(Constant.OR);
                }
                query.append(String.format("assignee=\"%s\"", assignee));
                first = false;
            }
            query.append(")");
        }
        if (cyclename != null) {
            if ((assignees != null && !assignees.isEmpty()) || project != null) {
                query.append(Constant.AND);
            }
            query.append(String.format("cycleName = \"%s\"", cyclename));
        }
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put(Constant.PARAMERTER_ZQL_QUERY, query.toString());
        String data = HTTPClientUtil.getInstance().getLegacyData(PropertiesUtil.getString(Constant.RESOURCE_BUNLE_PATH), parameters, cookies);
        ExecutionsVO executions = JSONUtil.getInstance().convertJSONtoObject(data, ExecutionsVO.class);
        return executions;
    }

    public ExecutionsVO findAllExecutionIsueeInProject(String projectName, String release, Map<String, String> cookies) throws APIException {
        StringBuffer query = new StringBuffer();
        if (projectName == null || projectName.isEmpty()) {
            return null;
        }
        query.append("project = \"" + projectName + "\"");
        if (release != null) {
            query.append(Constant.AND);
            query.append(String.format("fixVersion = %s", release));
        }
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put(Constant.PARAMERTER_ZQL_QUERY, query.toString());
        parameters.put(Constant.PARAMERTER_MAXRECORDS,
                PropertiesUtil.getString(Constant.RESOURCE_BUNLE_SEARCH_MAXRECORDS, Constant.RESOURCE_BUNLE_SEARCH_MAXRECORDS_DEFAULT));
        parameters.put(Constant.PARAMERTER_OFFSET, "0");
        String result = HTTPClientUtil.getInstance().getLegacyData(PropertiesUtil.getString(Constant.RESOURCE_BUNLE_PATH), parameters, cookies);
        ExecutionsVO executions = JSONUtil.getInstance().convertJSONtoObject(result, ExecutionsVO.class);
        return executions;
    }

    public Set<String> getListCycleName(String projectName, String release, Set<String> products, SessionInfo sessionInfo) throws APIException {
        Set<String> returnData = new HashSet<>();
        StringBuffer provisional = new StringBuffer();
        provisional.append(provisional);
        if(release != null){
            provisional.append(PLUS + release);
        }
        if(products != null && !products.isEmpty()){
            provisional.append(PLUS + products);
        }
        String keyProvisional = provisional.toString() + Constant.DELIMITER + sessionInfo.getUsername();
        DataCacheVO<Set<String>> dataCache = cycleNameCache.get(keyProvisional);
        boolean found = false;

        if(dataCache != null){
            long begin = System.currentTimeMillis();
            int timeout = PropertiesUtil.getInt(Constant.PARAMERTER_TIMEOUT);
            
            while (!State.SUCCESS.equals(dataCache.getState())){
                if(begin + timeout < System.currentTimeMillis()){
                    logger.fastDebug("timeout when waiting cache");
                    return returnData;
                }
                try{
                    Thread.sleep(800);
                } catch (InterruptedException e){
                    logger.fastDebug("Thread interrupted", e, new Object());
                }
            }
            returnData = dataCache.getData();
            found =true;
        }
        if(!found){
            DataCacheVO<Set<String>> dataCacheVO = new DataCacheVO<Set<String>>();
            cycleNameCache.put(keyProvisional, dataCacheVO);
            try{
                List<JQLIssueVO> issues = findAllIssueInProject(projectName, release, products, sessionInfo.getCookies());
                List<ExecutionIssueVO> executions = new ArrayList<>();
                if(issues != null){
                    List<TestExecutionCallable> tasks = new ArrayList<>();
                    issues.forEach(i -> tasks.add(
                            new TestExecutionCallable(i, JQLIssuetypeVO.Type.fromString(i.getFields().getIssuetype().getName()), sessionInfo.getCookies())));

                    List<ExecutionIssueResultWapper> taskResult = ExecutorManagement.getInstance().invokeAndGet(tasks);
                    for (ExecutionIssueResultWapper wapper : taskResult){
                        List<ExecutionIssueVO> executionVO = wapper.getExecutionsVO();
                        if(executionVO != null){
                            executions.addAll(executionVO);
                        }
                    }
                    Set<String> cycleNames = executions.stream().map(i -> i.getCycleName()).collect(Collectors.toSet());
                    if(cycleNames != null && !cycleNames.isEmpty()){
                        returnData.addAll(cycleNames);
                    }
                }
            } finally{
                dataCacheVO.setData(returnData);
                dataCacheVO.setState(State.SUCCESS);
            }
        }
        return returnData;
    }

    public void clearSession() {
        cycleNameCache = null;
    }

    public List<JQLIssueVO> findAllIssueInProject(String projectName, String release, Set<String> products, Map<String, String> cookies) throws APIException {
        List<JQLIssueVO> returnData = new ArrayList<>();
        StringBuffer query = new StringBuffer();
        if (projectName == null || projectName.isEmpty()) {
            return returnData;
        }
        query.append("project = \"" + projectName + "\"");
        if (release != null) {
            query.append(Constant.AND);
            query.append(String.format("fixVersion = %s", release));
        }

        if (products != null && !products.isEmpty()) {
            boolean first = true;
            boolean isContainProduct = false;
            for (String product : products) {
                if (product != null && !product.isEmpty()) {
                    if (first) {
                        query.append(Constant.AND);
                        query.append(Constant.OPEN_BRACKET);

                    } else {
                        query.append(Constant.OR);
                    }
                    query.append(String.format("cf[12718] = \"%s\"", product));
                    first = false;
                    isContainProduct = true;
                }
            }
            if (isContainProduct) {
                query.append(Constant.CLOSE_BRACKET);
            }
        }
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put(Constant.PARAMERTER_JQL_QUERY, query.toString());
        parameters.put(Constant.PARAMERTER_MAXRESULTS,
                PropertiesUtil.getString(Constant.RESOURCE_BUNLE_SEARCH_MAXRECORDS, Constant.RESOURCE_BUNLE_SEARCH_MAXRECORDS_DEFAULT));
        parameters.put(Constant.PARAMERTER_OFFSET, "0");
        String result = HTTPClientUtil.getInstance().getLegacyData(PropertiesUtil.getString(Constant.RESOURCE_BUNLE_SEARCH_PATH), parameters, 0, cookies);
        JQLSearchResult jqpIssues = JSONUtil.getInstance().convertJSONtoObject(result, JQLSearchResult.class);
        if (jqpIssues != null && jqpIssues.getIssues() != null) {
            List<JQLIssueVO> issues = jqpIssues.getIssues();
            returnData = issues;
        }
        return returnData;
    }

    public void clearCache() {
        cycleNameCache.cleanAll();
        assigneesCache.cleanAll();;
    }

    public void cleanUserCache(String username) {
        cycleNameCache.cleanUserCache(username);
        assigneesCache.cleanUserCache(username);
    }
}

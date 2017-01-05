package util.gadget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import handle.executors.ExecutorManagement;
import handle.executors.TestExecutionCallable;
import manament.log.LoggerWapper;
import models.APIIssueVO;
import models.AssigneeVO;
import models.ExecutionIssueResultWapper;
import models.ExecutionIssueVO;
import models.JQLIssueVO;
import models.JQLIssuetypeVO;
import models.exception.APIException;
import models.gadget.AssigneeVsTestExecution;
import models.main.ExecutionsVO;
import models.main.GadgetData;
import models.main.GadgetDataWapper;
import models.main.JQLSearchResult;
import models.main.Release;
import service.HTTPClientUtil;
import util.AdminUtility;
import util.Constant;
import util.JSONUtil;
import util.PropertiesUtil;

public class AssigneeUtility {
    final static LoggerWapper logger = LoggerWapper.getLogger(AssigneeUtility.class);
    private static AssigneeUtility INSTANCE = new AssigneeUtility();
    private static Map<String, Set<String>> cycleNameCache = new HashMap<String, Set<String>>();;
    private static Map<String, Set<AssigneeVO>> assigneesCache = new HashMap<String, Set<AssigneeVO>>();
    private static final String PLUS = "+";

    private AssigneeUtility() {
    }

    public static AssigneeUtility getInstance() {
        return INSTANCE;
    }

    public Map<String, GadgetDataWapper> getDataAssignee(AssigneeVsTestExecution assigneeGadget, Map<String, String> cookies) throws APIException {
        Map<String, GadgetDataWapper> returnData = new HashMap<>();

        String projectName = assigneeGadget.getProjectName();
        Set<AssigneeVO> assigneeVOs = findAssigneeList(projectName, assigneeGadget.getRelease(), cookies);
        Set<String> assignees = assigneeVOs.stream().map(a -> a.getDisplay()).collect(Collectors.toSet());
        Set<String> cycles = assigneeGadget.getCycles();

        if(assigneeGadget.isSelectAllTestCycle()){
            cycles = AdminUtility.getInstance().getAllCycle();
        }
        if(cycles != null && !cycles.isEmpty()){
            for (String cycle : cycles){
                ExecutionsVO executions = findExecution(projectName, cycle, assignees, cookies);
                if(executions != null && executions.getExecutions() != null){
                    Map<String, List<ExecutionIssueVO>> assigneeMap = executions.getExecutions().stream()
                            .collect(Collectors.groupingBy(ExecutionIssueVO::getAssigneeDisplay));
                    List<GadgetData> gadgetDatas = new ArrayList<GadgetData>();
                    for (String assignee : assigneeMap.keySet()){
                        GadgetData gadgetData = GadgetUtility.getInstance().convertToGadgetData(assigneeMap.get(assignee));
                        gadgetData.setKey(new APIIssueVO(assignee, null));
                        gadgetDatas.add(gadgetData);
                    }
                    // init empty for assignees that have no test
                    for (String assignee : assignees){
                        if(!assigneeMap.containsKey(assignee)){
                            GadgetData gadgetData = new GadgetData();
                            gadgetData.setKey(new APIIssueVO(assignee, null));
                            gadgetDatas.add(gadgetData);
                        }
                    }
                    // sorting
                    GadgetUtility.getInstance().sortData(gadgetDatas);

                    GadgetDataWapper gadgetDataWrapper = new GadgetDataWapper();
                    gadgetDataWrapper.setIssueData(gadgetDatas);
                    returnData.put(cycle, gadgetDataWrapper);
                }
            }
        } else{
            logger.fastDebug("No Test Cycle in gadget %s", assigneeGadget.getId());
        }
        return returnData;
    }

    public Set<AssigneeVO> findAssigneeList(String projectName, Release release, Map<String, String> cookies) throws APIException {
        if(assigneesCache.get(projectName + PLUS + release) == null || assigneesCache.get(projectName + PLUS + release).isEmpty()){
            ExecutionsVO executions = findAllExecutionIsueeInProject(projectName, release, cookies);
            if(executions != null && executions.getExecutions() != null){
                List<ExecutionIssueVO> excutions = executions.getExecutions();
                Stream<ExecutionIssueVO> excutionsStream = excutions.stream();
                assigneesCache.put(projectName + PLUS + release, excutionsStream
                        .filter(e -> (e.getAssigneeUserName() != null && !e.getAssigneeUserName().isEmpty())).map(new Function<ExecutionIssueVO, AssigneeVO>() {
                            @Override
                            public AssigneeVO apply(ExecutionIssueVO issueVO) {
                                AssigneeVO assigneeVO = new AssigneeVO(issueVO.getAssignee(), issueVO.getAssigneeUserName(), issueVO.getAssigneeDisplay());
                                return assigneeVO;
                            }
                        }).collect(Collectors.toSet()));
            }
        }
        Set<AssigneeVO> assignees = assigneesCache.get(projectName + PLUS + release);
        return assignees != null ? assignees : new HashSet<>();

    }

    public ExecutionsVO findExecution(String project, String cyclename, Set<String> assignees, Map<String, String> cookies) throws APIException {
        StringBuilder query = new StringBuilder();
        query.append(String.format("project = \"%s\"", project));
        if(assignees != null && !assignees.isEmpty()){
            query.append(Constant.AND);
            query.append("(");
            boolean first = true;
            for (String assignee : assignees){
                if(!first){
                    query.append(Constant.OR);
                }
                query.append(String.format("assignee=\"%s\"", assignee));
                first = false;
            }
            query.append(")");
        }
        if(cyclename != null){
            query.append(Constant.AND);
            query.append(String.format("cycleName ~ \"%s\"", cyclename));
        }
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put(Constant.PARAMERTER_ZQL_QUERY, query.toString());
        String data = HTTPClientUtil.getInstance().getLegacyData(PropertiesUtil.getString(Constant.RESOURCE_BUNLE_PATH), parameters, cookies);
        ExecutionsVO executions = JSONUtil.getInstance().convertJSONtoObject(data, ExecutionsVO.class);
        return executions;
    }

    public ExecutionsVO findAllExecutionIsueeInProject(String projectName, Release release, Map<String, String> cookies) throws APIException {
        StringBuffer query = new StringBuffer();
        if(projectName == null || projectName.isEmpty()){
            return null;
        }
        query.append("project = \"" + projectName + "\"");
        if(release != null){
            query.append(Constant.AND);
            query.append(String.format("fixVersion = %s", release.toString()));
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

    public Set<String> getListCycleName(String projectName, Release release, Set<String> products, Map<String, String> cookies) throws APIException {
        Set<String> returnData = new HashSet<>();
        StringBuffer provisional = new StringBuffer();
        provisional.append(provisional);
        if(release != null){
            provisional.append(PLUS + release.toString());
        }
        if(products != null && !products.isEmpty()){
            provisional.append(PLUS + products);
        }
        String keyProvisional = provisional.toString();
        if(cycleNameCache.get(keyProvisional) == null || cycleNameCache.get(keyProvisional).isEmpty()){
            List<JQLIssueVO> issues = findAllIssueInProject(projectName, release, products, cookies);
            List<ExecutionIssueVO> executions = new ArrayList<>();
            if(issues != null){
                List<TestExecutionCallable> tasks = new ArrayList<>();
                issues.forEach(i -> tasks.add(new TestExecutionCallable(i, JQLIssuetypeVO.Type.fromString(i.getFields().getIssuetype().getName()), cookies)));

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
                    cycleNameCache.put(keyProvisional, returnData);
                }
            }
        } else{
            returnData = cycleNameCache.get(keyProvisional);
        }
        return returnData;
    }

    public void clearSession() {
        cycleNameCache = null;
    }

    public List<JQLIssueVO> findAllIssueInProject(String projectName, Release release, Set<String> products, Map<String, String> cookies) throws APIException {
        List<JQLIssueVO> returnData = new ArrayList<>();
        StringBuffer query = new StringBuffer();
        if(projectName == null || projectName.isEmpty()){
            return returnData;
        }
        query.append("project = \"" + projectName + "\"");
        if(release != null){
            query.append(Constant.AND);
            query.append(String.format("fixVersion = %s", release.toString()));
        }

        if(products != null && !products.isEmpty()){
            boolean first = true;
            boolean isContainProduct = false;
            for (String product : products){
                if(product != null && !product.isEmpty()){
                    if(first){
                        query.append(Constant.AND);
                        query.append(Constant.OPEN_BRACKET);

                    } else{
                        query.append(Constant.OR);
                    }
                    query.append(String.format("cf[12718] = \"%s\"", product));
                    first = false;
                    isContainProduct = true;
                }
            }
            if(isContainProduct){
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
        if(jqpIssues != null && jqpIssues.getIssues() != null){
            List<JQLIssueVO> issues = jqpIssues.getIssues();
            returnData = issues;
        }
        return returnData;
    }

    public void clearCache() {
        cycleNameCache.clear();
        assigneesCache.clear();
    }
}

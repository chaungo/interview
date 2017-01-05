package util.gadget;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import handle.executors.ExecutorManagement;
import handle.executors.FindIssueCallable;
import handle.executors.TestExecutionCallable;
import manament.log.LoggerWapper;
import models.APIIssueVO;
import models.ExecutionIssueResultWapper;
import models.ExecutionIssueVO;
import models.JQLIssueLinkVO;
import models.JQLIssueVO;
import models.JQLIssuetypeVO;
import models.JQLIssuetypeVO.Type;
import models.exception.APIException;
import models.gadget.EpicVsTestExecution;
import models.main.ExecutionsVO;
import models.main.GadgetData;
import models.main.JQLSearchResult;
import service.HTTPClientUtil;
import util.Constant;
import util.JSONUtil;
import util.PropertiesUtil;

public class EpicUtility {
    final static LoggerWapper logger = LoggerWapper.getLogger(EpicUtility.class);
    private static final String IS_TESED_BY = "is tested by";
    private static EpicUtility INSTANCE = new EpicUtility();

    private EpicUtility() {

    }

    public static EpicUtility getInstance() {
        return INSTANCE;
    }

    public List<GadgetData> getDataEPic(EpicVsTestExecution epicGadget,  Map<String, String> cookies) throws APIException {
        List<GadgetData> result = new ArrayList<>();
        List<APIIssueVO> epicLinks = null;

        if(epicGadget.isSelectAll()){
            epicLinks = getEpicLinks(epicGadget.getProjectName(), epicGadget.getRelease().toString(), epicGadget.getProducts(), cookies);
        } else{
            Set<String> epics = epicGadget.getEpic();
            List<FindIssueCallable> tasks = new ArrayList<>();
            epics.forEach(e -> tasks.add(new FindIssueCallable(e, cookies)));
            List<Future<JQLIssueVO>> taskReulsts = ExecutorManagement.getInstance().invokeTask(tasks);
            List<JQLIssueVO> epicIssues = ExecutorManagement.getInstance().getResult(taskReulsts);
            epicLinks = epicIssues.stream().filter(e -> e != null && e.getFields() != null).map(new Function<JQLIssueVO, APIIssueVO>() {
                @Override
                public APIIssueVO apply(JQLIssueVO jqlIssue) {
                    APIIssueVO apiIssue = new APIIssueVO(jqlIssue.getKey(), jqlIssue.getSelf(), jqlIssue.getFields().getSummary());
                    apiIssue.setPriority(jqlIssue.getFields().getPriority());
                    return apiIssue;
                }
            }).collect(Collectors.toList());
        }
        if(epicLinks == null){
            return result;
        }
        for (APIIssueVO epic : epicLinks){
            ExecutionIssueResultWapper executionIssues = findAllExecutionIssueInEpic(epic.getKey(), cookies);
            GadgetData gadgetData = GadgetUtility.getInstance().convertToGadgetData(executionIssues.getExecutionsVO());
            gadgetData.setKey(epic);
            gadgetData.increasePlanned(executionIssues.getPlanned().getTotal());
            gadgetData.getPlanned().getIssues().addAll(executionIssues.getPlanned().getIssues());
            result.add(gadgetData);
        }
        GadgetUtility.getInstance().sortData(result);
        return result;
    }

    public ExecutionIssueResultWapper findAllExecutionIssueInEpic(String epic,  Map<String, String> cookies) throws APIException {
        ExecutionIssueResultWapper resultWapper = new ExecutionIssueResultWapper();
        List<JQLIssueVO> issues = new ArrayList<>();
        
//        issues.addAll(findAllIssuesInEpicLink(epic, cookies));
        List<JQLIssueVO> issuesInEpic = findAllIssuesInEpicLink(epic, cookies);
        List<JQLIssueVO> storyInEpic = issuesInEpic.stream().filter(i -> Type.STORY.toString().equalsIgnoreCase(i.getFields().getIssuetype().getName())).collect(Collectors.toList());
        
        issues.addAll(storyInEpic);
        issues.addAll(findAllTestedIssueForEpic(epic, cookies));
        if(issues == null || issues.isEmpty()){
            return resultWapper;
        }
        logger.fasttrace("Total issue in epic %s:%d", epic, issues.size());

        List<TestExecutionCallable> tasks = new ArrayList<TestExecutionCallable>();
        issues.stream().forEach(new Consumer<JQLIssueVO>() {
            @Override
            public void accept(JQLIssueVO issue) {
                Type type = JQLIssuetypeVO.Type.fromString(issue.getFields().getIssuetype().getName());
                // ignore other
                if(type == Type.TEST || type == Type.STORY){
                    tasks.add(new TestExecutionCallable(issue, type, cookies));
                }
            }
        });
        logger.fasttrace("Total Test and Story in epic %s:%d", epic, tasks.size());
        List<ExecutionIssueResultWapper> resultTask = ExecutorManagement.getInstance().invokeAndGet(tasks);
        for (ExecutionIssueResultWapper wapper : resultTask){
            if(wapper != null){
                resultWapper.getExecutionsVO().addAll(wapper.getExecutionsVO());
                if(wapper.getIssue() != null && Type.STORY.equals(wapper.getIssue().getType())){
                    resultWapper.increasePland(wapper.getPlanned().getTotal());
                    resultWapper.getPlanned().getIssues().addAll(wapper.getPlanned().getIssues());
                }
            }
        }
        return resultWapper;
    }

    public List<ExecutionIssueVO> findTestExecutionInIsuee(String issueKey,  Map<String, String> cookies) throws APIException {
        List<ExecutionIssueVO> testExecution = new ArrayList<>();
        String query = "issue=\"%s\"";
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put(Constant.PARAMERTER_ZQL_QUERY, String.format(query, issueKey));
        parameters.put(Constant.PARAMERTER_MAXRECORDS, PropertiesUtil.getString(Constant.RESOURCE_BUNLE_SEARCH_MAXRECORDS, "10000"));
        parameters.put(Constant.PARAMERTER_OFFSET, "0");
        String result = HTTPClientUtil.getInstance().getLegacyData(PropertiesUtil.getString(Constant.RESOURCE_BUNLE_PATH), parameters, cookies);
        ExecutionsVO executions = JSONUtil.getInstance().convertJSONtoObject(result, ExecutionsVO.class);
        if(executions != null){
            if(executions.getExecutions() != null){
                if(executions.getExecutions().size() > 1){
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/MMM/yy");
                    /*
                     * Fetch only the most recent execution
                     * In case that date cannot be parse or date is empty -> take the first test execution
                     * In case that existing an empty date -> take the test which have date.
                     */
                    Optional<ExecutionIssueVO> execution = executions.getExecutions().stream().sorted(new Comparator<ExecutionIssueVO>() {
                        @Override
                        public int compare(ExecutionIssueVO o1, ExecutionIssueVO o2) {
                            LocalDate o1Time = null;
                            LocalDate o2Time = null;
                            int i = 0;
                            boolean cannotParse = false;
                            try{
                                o1Time = LocalDate.parse(o1.getExecutedOn(), formatter);
                            } catch (Exception e){
                                logger.fastDebug("cannot parse date %s:%s", o1.getExecutedOn(), o1.getIssueKey());
                                cannotParse = true;
                                i = 1;
                            }
                            try{
                                o2Time = LocalDate.parse(o2.getExecutedOn(), formatter);
                            } catch (Exception e){
                                logger.fastDebug("cannot parse date %s:%s", o2.getExecutedOn(), o2.getIssueKey());
                                cannotParse = true;
                                i -= 1;
                            }
                            if(cannotParse){
                                return i;
                            }
                            return o2Time.compareTo(o1Time);
                        }
                    }).findFirst();
                    if(execution.isPresent() && execution.get() != null){
                        testExecution.add(execution.get());
                    }
                } else{
                    testExecution.addAll(executions.getExecutions());
                }
            }
        } else{
            logger.fastDebug("data result is not map to ExecutionsVO :%s", result);
        }
        return testExecution;
    }

    public List<JQLIssueVO> findAllIssuesInEpicLink(String epic,  Map<String, String> cookies) throws APIException {
        String query = "\"Epic Link\"=%s";
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put(Constant.PARAMERTER_JQL_QUERY, String.format(query, epic));
        parameters.put(Constant.PARAMERTER_MAXRESULTS,
                PropertiesUtil.getString(Constant.RESOURCE_BUNLE_SEARCH_MAXRECORDS, Constant.RESOURCE_BUNLE_SEARCH_MAXRECORDS_DEFAULT));
        parameters.put(Constant.PARAMERTER_OFFSET, "0");
        String data = HTTPClientUtil.getInstance().getLegacyData(PropertiesUtil.getString(Constant.RESOURCE_BUNLE_SEARCH_PATH), parameters, cookies);
        if(data == null){
            return null;
        }
        JQLSearchResult searchResult = JSONUtil.getInstance().convertJSONtoObject(data, JQLSearchResult.class);
        return searchResult.getIssues();
    }

    public List<JQLIssueVO> findAllTestedIssueForEpic(String epic,  Map<String, String> cookies) throws APIException {
        JQLIssueVO epicIssue = GadgetUtility.getInstance().findIssue(epic, cookies);
        List<JQLIssueLinkVO> issueLinks = epicIssue.getFields().getIssuelinks();
        List<JQLIssueLinkVO> testedByIssue = issueLinks.stream().filter(i -> IS_TESED_BY.equals(i.getType().getInward())).collect(Collectors.toList());

        List<FindIssueCallable> tasks = new ArrayList<FindIssueCallable>();
        testedByIssue.forEach(s -> tasks.add(new FindIssueCallable(s.getInwardIssue().getKey(), cookies)));
        List<JQLIssueVO> testIssues = new ArrayList<>();
        
        List<JQLIssueVO> resultTask = ExecutorManagement.getInstance().invokeAndGet(tasks);
        testIssues.addAll(resultTask);
        return testIssues;
    }

    public List<APIIssueVO> getEpicLinks(String project, String release, Set<String> products,  Map<String, String> cookies) throws APIException {
        List<APIIssueVO> result = null;
        logger.fasttrace("getEpicLinks(%s,%s)", project, release);
        if(project == null){
            throw new APIException("project param cannot be null");
        }
        StringBuilder query = new StringBuilder();
        String projectParam = "project=\"%s\"";
        query.append(String.format(projectParam, project));
        query.append(Constant.AND);
        query.append("type = epic");
        if(release != null){
            String fixVersionParam = "fixVersion=%s";
            query.append(Constant.AND);
            query.append(String.format(fixVersionParam, release));
        }
        if(products != null && !products.isEmpty()){
            query.append(Constant.AND);
            query.append(Constant.OPEN_BRACKET);
            boolean first = true;
            for (String product : products){
                if(product != null && !product.isEmpty()){
                    if(!first){
                        query.append(Constant.OR);
                    }
                    query.append(String.format("cf[12718] = \"%s\"", product));
                    first = false;
                }
            }
            query.append(Constant.CLOSE_BRACKET);
        }
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put(Constant.PARAMERTER_JQL_QUERY, query.toString());
        parameters.put(Constant.PARAMERTER_MAXRESULTS,
                PropertiesUtil.getString(Constant.RESOURCE_BUNLE_SEARCH_MAXRECORDS, Constant.RESOURCE_BUNLE_SEARCH_MAXRECORDS_DEFAULT));
        parameters.put(Constant.PARAMERTER_OFFSET, "0");
        String data = HTTPClientUtil.getInstance().getLegacyData(PropertiesUtil.getString(Constant.RESOURCE_BUNLE_SEARCH_PATH), parameters,  cookies);
        JQLSearchResult searchResult = JSONUtil.getInstance().convertJSONtoObject(data, JQLSearchResult.class);
        if(searchResult != null && searchResult.getIssues() != null){
            result = searchResult.getIssues().stream().filter(i -> i != null && i.getFields() != null).map(new Function<JQLIssueVO, APIIssueVO>() {
                @Override
                public APIIssueVO apply(JQLIssueVO jQLIssue) {
                    APIIssueVO apiIssue = new APIIssueVO(jQLIssue.getKey(), jQLIssue.getSelf(), jQLIssue.getFields().getSummary());
                    apiIssue.setPriority(jQLIssue.getFields().getPriority());
                    return apiIssue;
                }
            }).collect(Collectors.toList());
        } else{
            throw new APIException(data);
        }
        GadgetUtility.getInstance().sortIssue(result);
        return result;
    }

}

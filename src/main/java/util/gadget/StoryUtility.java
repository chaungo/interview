package util.gadget;

import handle.executors.ExecutorManagement;
import handle.executors.FindIssueCallable;
import handle.executors.FindIssueInEpicCallable;
import handle.executors.TestExecutionCallable;
import manament.log.LoggerWapper;
import models.*;
import models.JQLIssuetypeVO.Type;
import models.exception.APIException;
import models.gadget.StoryVsTestExecution;
import models.main.GadgetData;
import models.main.GadgetDataWrapper;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.stream.Collectors;

public class StoryUtility {
    private static final LoggerWapper logger = LoggerWapper.getLogger(StoryUtility.class);
    private static final String INWARD_TEST_BY = "Is a test by";
    private static StoryUtility INSTANCE = new StoryUtility();
    private ConcurrentMap<String, JQLIssueWapper> storyInEpic = new ConcurrentHashMap<>();

    private StoryUtility() {
    }

    public static StoryUtility getInstance() {
        return INSTANCE;
    }

    public Map<String, JQLIssueWapper> findStoryInEpic(List<String> epics, Map<String, String> cookies) throws APIException {
        Map<String, JQLIssueWapper> storiesData = new HashMap<>();
        List<FindIssueInEpicCallable> tasks = new ArrayList<FindIssueInEpicCallable>();
        for (String epic : epics) {
            if (storyInEpic.get(epic) == null) {
                JQLIssueVO epicIssue = GadgetUtility.getInstance().findIssue(epic, cookies);
                if (epicIssue == null) {
                    throw new APIException("Cannot find out epic = " + epic);
                }
                tasks.add(new FindIssueInEpicCallable(epicIssue, cookies));
            } else {
                storiesData.put(epic, storyInEpic.get(epic));
            }
        }
        List<JQLIssueWapper> results = ExecutorManagement.getInstance().getResult(ExecutorManagement.getInstance().invokeTask(tasks));
        if (results != null && !results.isEmpty())
            for (JQLIssueWapper resultWapper : results) {
                if (resultWapper != null) {
                    Set<JQLIssueVO> stories = null;
                    if (resultWapper.getChild() != null && !resultWapper.getChild().isEmpty()) {
                        stories = filter(resultWapper.getChild(), JQLIssuetypeVO.Type.STORY);
                        resultWapper.setChild(stories);
                        storyInEpic.put(resultWapper.getIssue().getKey(), resultWapper);
                        storiesData.put(resultWapper.getIssue().getKey(), resultWapper);
                    }
                }
            }
        return storiesData;
    }

    private Set<JQLIssueVO> filter(Set<JQLIssueVO> set, JQLIssuetypeVO.Type type) {
        return set.stream().filter(i -> type.toString().equalsIgnoreCase(i.getFields().getIssuetype().getName())).collect(Collectors.toSet());
    }

    public List<ExecutionIssueVO> findAllTestExecutionInStory(JQLIssueVO issue, Map<String, String> cookies) throws APIException {
        List<ExecutionIssueVO> result = new ArrayList<>();
        if (JQLIssuetypeVO.Type.STORY.toString().equalsIgnoreCase(issue.getFields().getIssuetype().getName())) {
            List<JQLIssueLinkVO> issueLinks = findAllTestIssueForStory(issue);
            if (issueLinks != null && !issueLinks.isEmpty()) {
                for (JQLIssueLinkVO issueLink : issueLinks) {
                    List<ExecutionIssueVO> executionIssues = EpicUtility.getInstance().findTestExecutionInIsuee(issueLink.getInwardIssue().getKey(), cookies);
                    if (executionIssues != null && !executionIssues.isEmpty()) {
                        result.addAll(executionIssues);
                    }
                }
            }
        }
        return result;
    }

    public List<JQLIssueLinkVO> findAllTestIssueForStory(JQLIssueVO issue) {
        List<JQLIssueLinkVO> testIssue = null;
        if (issue != null && issue.getFields() != null && issue.getFields().getIssuelinks() != null) {
            List<JQLIssueLinkVO> issueLinks = issue.getFields().getIssuelinks();
            testIssue = issueLinks.stream().filter(i -> INWARD_TEST_BY.equals(i.getType().getInward())).collect(Collectors.toList());
        } else {
            logger.fasttrace("cannot findout issuelinks of %s", issue);
        }
        return testIssue;
    }

    public Map<String, GadgetDataWrapper> getDataStory(StoryVsTestExecution storyGadget, Map<String, String> cookies) throws APIException {
        Map<String, GadgetDataWrapper> returnData = new HashMap<>();
        Map<String, JQLIssueWapper> epicWrapperMap = null;
        if (storyGadget.isSelectAllEpic() && storyGadget.isSelectAllStory()) {
            String project = storyGadget.getProjectName();
            String release = storyGadget.getRelease();
            List<APIIssueVO> epicIssues = EpicUtility.getInstance().getEpicLinks(project, release.toString(), storyGadget.getProducts(), cookies);
            Set<String> epics = epicIssues.stream().map(e -> e.getKey()).collect(Collectors.toSet());
            epicWrapperMap = findStoryInEpic(new ArrayList<String>(epics), cookies);
        } else if (storyGadget.isSelectAllStory()) {
            epicWrapperMap = findStoryInEpic(new ArrayList<String>(storyGadget.getEpic()), cookies);
        } else {
            Set<JQLIssueVO> storyIssues = new HashSet<>();
            Set<String> stories = storyGadget.getStories();
            if (stories != null) {
                List<FindIssueCallable> tasks = new ArrayList<FindIssueCallable>();
                stories.forEach(s -> tasks.add(new FindIssueCallable(s, cookies)));

                List<Future<JQLIssueVO>> taskResult = ExecutorManagement.getInstance().invokeTask(tasks);
                List<JQLIssueVO> storyIssuesList = ExecutorManagement.getInstance().getResult(taskResult);
                if (storyIssuesList != null) {
                    storyIssues.addAll(storyIssuesList);
                }
                Map<String, Set<JQLIssueVO>> epicMap = storyIssues.stream().collect(Collectors.groupingBy(new Function<JQLIssueVO, String>() {
                    @Override
                    public String apply(JQLIssueVO s) {
                        if (s != null && s.getFields() != null && s.getFields().getEpicLink() != null) {
                            return s.getFields().getEpicLink();
                        }
                        return "";
                    }
                }, Collectors.toSet()));

                epicWrapperMap = new HashMap<>();
                for (String epicKey : epicMap.keySet()) {
                    JQLIssueVO epicIssue = GadgetUtility.getInstance().findIssue(epicKey, cookies);
                    if (epicIssue == null) {
                        throw new APIException("cannot find out issue = " + epicKey);
                    }
                    epicWrapperMap.put(epicKey, new JQLIssueWapper(epicIssue, epicMap.get(epicKey)));
                }
            }
        }

        if (epicWrapperMap == null || epicWrapperMap.isEmpty()) {
            return returnData;
        }
        Type type = JQLIssuetypeVO.Type.STORY;
        for (String epic : epicWrapperMap.keySet()) {
            List<TestExecutionCallable> tasks = new ArrayList<TestExecutionCallable>();
            JQLIssueWapper storyWapper = epicWrapperMap.get(epic);
            storyWapper.getChild().forEach(s -> tasks.add(new TestExecutionCallable(s, type, cookies)));
            List<ExecutionIssueResultWapper> results = ExecutorManagement.getInstance().invokeAndGet(tasks);

            List<GadgetData> storyDatas = new ArrayList<>();
            for (ExecutionIssueResultWapper wapper : results) {
                GadgetData data = GadgetUtility.getInstance().convertToGadgetData(wapper.getExecutionsVO());
                data.increasePlanned(wapper.getPlanned().getTotal());
                data.getPlanned().getIssues().addAll(wapper.getPlanned().getIssues());
                data.setKey(wapper.getIssue());
                storyDatas.add(data);
            }
            GadgetUtility.getInstance().sortData(storyDatas);

            GadgetDataWrapper dataWrapper = new GadgetDataWrapper();
            dataWrapper.setIssueData(storyDatas);
            dataWrapper.setSummary(storyWapper.getIssue().getFields().getSummary());
            returnData.put(epic, dataWrapper);
        }
        return returnData;
    }

    public void clearCache() {
        storyInEpic.clear();
    }

    public void cleanUserCache(String username) {
        Set<String> keys = storyInEpic.keySet();
        for(String key : keys){
            if(key.contains(username)){
                storyInEpic.remove(key);
            }
        }
    }
}

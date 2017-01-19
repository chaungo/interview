package handle;

import handle.scheduler.GadgetCacheMap;
import manament.log.LoggerWapper;
import models.APIIssueVO;
import models.JQLIssueWapper;
import models.ResultCode;
import models.SessionInfo;
import models.exception.APIException;
import models.exception.ResultsUtil;
import models.gadget.*;
import models.gadget.Gadget.Type;
import models.main.DataCacheVO;
import models.main.DataCacheVO.State;
import models.main.GadgetData;
import models.main.GadgetDataWrapper;
import ninja.Result;
import ninja.Results;
import util.Constant;
import util.JSONUtil;
import util.PropertiesUtil;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class GadgetHandlerImpl extends GadgetHandler {
    final static LoggerWapper logger = LoggerWapper.getLogger(GadgetHandlerImpl.class);

    private static GadgetCacheMap<Map<String, GadgetDataWrapper>> dataGadgetCache = new GadgetCacheMap<Map<String, GadgetDataWrapper>>(PropertiesUtil.getInt(Constant.DATA_CACHE_TIME_TO_LIVE, 10), "DataGadgetCache");


    public GadgetHandlerImpl() {
    }

    @Override
    public Result insertOrUpdateGadget(String type, String data, SessionInfo sessionInfo) throws APIException {
        Gadget gadget = null;
        String gadgetId = "";
        Type gadgetType = Gadget.Type.valueOf(type);
        if (gadgetType == null) {
            throw new APIException("type " + type + " not available");
        }
        if (data == null) {
            throw new APIException("data cannot be null");
        }
        String username = sessionInfo.getUsername();
        // String friendlyname = (String) context.getAttribute("alias");
        boolean toVerify = true;
        List<String> errorMessages = new ArrayList<>();
        if (Gadget.Type.EPIC_US_TEST_EXECUTION.equals(gadgetType)) {
            EpicVsTestExecution epicGadget = JSONUtil.getInstance().convertJSONtoObject(data, EpicVsTestExecution.class);
            epicGadget.setUser(username);
            if (!epicGadget.isSelectAll() && (epicGadget.getEpic() == null || epicGadget.getEpic().isEmpty())) {
                errorMessages.add("Epic link");
            }
            gadget = epicGadget;
        } else if (Gadget.Type.ASSIGNEE_TEST_EXECUTION.equals(gadgetType)) {
            AssigneeVsTestExecution assigneeGadget = JSONUtil.getInstance().convertJSONtoObject(data, AssigneeVsTestExecution.class);
            assigneeGadget.setUser(username);
            if (!assigneeGadget.isSelectAllTestCycle()) {
                toVerify = false;
                if (assigneeGadget.getCycles() == null || assigneeGadget.getCycles().isEmpty()) {
                    errorMessages.add("Cycle name");
                }
            }
            gadget = assigneeGadget;
        } else if (Gadget.Type.TEST_CYCLE_TEST_EXECUTION.equals(gadgetType)) {
            CycleVsTestExecution cycleGadget = JSONUtil.getInstance().convertJSONtoObject(data, CycleVsTestExecution.class);
            cycleGadget.setUser(username);
            if (!cycleGadget.isSelectAllCycle()) {
                toVerify = false;
                if (cycleGadget.getCycles() == null || cycleGadget.getCycles().isEmpty()) {
                    errorMessages.add("Cycle name");
                }
            }
            gadget = cycleGadget;
        } else if (Gadget.Type.STORY_TEST_EXECUTION.equals(gadgetType)) {
            StoryVsTestExecution storyGadget = JSONUtil.getInstance().convertJSONtoObject(data, StoryVsTestExecution.class);
            storyGadget.setUser(username);
            if (!storyGadget.isSelectAllStory() && (storyGadget.getStories() == null || storyGadget.getStories().isEmpty())) {
                errorMessages.add("Story");
            }
            if (!storyGadget.isSelectAllEpic() && (storyGadget.getEpic() == null || storyGadget.getEpic().isEmpty())) {
                errorMessages.add("Epic");
            }
            gadget = storyGadget;
        }
        if (gadget != null) {
            if (toVerify) {
                if (gadget.getDashboardId() == null) {
                    errorMessages.add("dashboardId");
                }
                if (gadget.getProducts() == null || gadget.getProducts().isEmpty()) {
                    errorMessages.add("products");
                }
                if (gadget.getProjectName() == null || gadget.getProjectName().isEmpty()) {
                    errorMessages.add("projectName");
                }
                if (gadget.getRelease() == null) {
                    errorMessages.add("release");
                }
            }
            if (errorMessages.isEmpty()) {
                gadgetId = gadgetService.insertOrUpdate(gadget);
                if (gadgetId.equals(gadget.getId())) {
                    //The case is update. Clean gadget data cache
                    String cacheID = gadgetId + Constant.DELIMITER + sessionInfo.getUsername();
                    dataGadgetCache.remove(cacheID);
                }
            } else {
                StringBuffer error = new StringBuffer();
                errorMessages.forEach(e -> error.append(e).append(", "));
                error.append("cannot be null");
                throw new APIException(error.toString());
            }
        } else {
            throw new APIException("can not map to any Epic gadget");
        }
        return ResultsUtil.convertToResult(ResultCode.SUCCESS, gadgetId);
    }

    @Override
    public Result getGadgets(String id) throws APIException {
        List<Gadget> gadgets = new ArrayList<>();
        if (gadgets != null) {
            gadgets.addAll(gadgetService.findByDashboardId(id));
        }
        return Results.json().render(gadgets);
    }

    @Override
    public Result getDataGadget(String id, SessionInfo sessionInfo) throws APIException {
        Map<String, GadgetDataWrapper> gadgetsData = new HashMap<>();
        boolean found = false;

        String cacheID = id + Constant.DELIMITER + sessionInfo.getUsername();
        if (dataGadgetCache.get(cacheID) != null) {
            DataCacheVO<Map<String, GadgetDataWrapper>> gadgetsDataCache = dataGadgetCache.get(cacheID);
            long begin = System.currentTimeMillis();
            while (DataCacheVO.State.LOADING.equals(gadgetsDataCache.getState())) {
                try {
                    Thread.sleep(800);
                } catch (InterruptedException e) {
                    logger.error("error when getDataGadget", e);
                }
                long now = System.currentTimeMillis();
                if (begin + PropertiesUtil.getInt(Constant.PARAMERTER_TIMEOUT, 60000) < now) {
                    break;
                }
            }

            if (DataCacheVO.State.SUCCESS.equals(gadgetsDataCache.getState())) {
                gadgetsData = gadgetsDataCache.getData();

                found = true;
            }
        }

        if (!found) {
            Gadget gadget = gadgetService.get(id);

            if (gadget != null) {

                DataCacheVO<Map<String, GadgetDataWrapper>> dataCache = new DataCacheVO<Map<String, GadgetDataWrapper>>();
                dataGadgetCache.put(cacheID, dataCache);
                try {
                    if (Gadget.Type.EPIC_US_TEST_EXECUTION.equals(gadget.getType())) {
                        EpicVsTestExecution epicGadget = (EpicVsTestExecution) gadget;
                        String projectName = epicGadget.getProjectName() != null ? epicGadget.getProjectName() : Constant.MAIN_PROJECT;
                        List<GadgetData> epicData = epicService.getDataEPic(epicGadget, sessionInfo.getCookies());
                        epicData.add(getTotal(epicData));
                        GadgetDataWrapper epicDataWapper = new GadgetDataWrapper();
                        epicDataWapper.setIssueData(epicData);
                        epicDataWapper.setSummary(projectName);
                        gadgetsData.put(projectName, epicDataWapper);
                    } else if (Gadget.Type.TEST_CYCLE_TEST_EXECUTION.equals(gadget.getType())) {
                        CycleVsTestExecution cycleGadget = (CycleVsTestExecution) gadget;
                        String projectName = cycleGadget.getProjectName() != null ? cycleGadget.getProjectName() : Constant.MAIN_PROJECT;
                        List<GadgetData> cycleData = cycleService.getDataCycle(cycleGadget, sessionInfo);
                        cycleData.add(getTotal(cycleData));
                        GadgetDataWrapper epicDataWapper = new GadgetDataWrapper();
                        epicDataWapper.setIssueData(cycleData);
                        epicDataWapper.setSummary(projectName);
                        gadgetsData.put(projectName, epicDataWapper);
                    } else if (Gadget.Type.ASSIGNEE_TEST_EXECUTION.equals(gadget.getType())) {
                        AssigneeVsTestExecution assigneeGadget = (AssigneeVsTestExecution) gadget;
                        gadgetsData = assigneeService.getDataAssignee(assigneeGadget, sessionInfo);

                        GadgetDataWrapper summaryTableDataWrapper = new GadgetDataWrapper();
                        summaryTableDataWrapper.setSummary(Constant.SUMMARY_TABLE_TITLE);
                        List<GadgetData> summaryData = new ArrayList<>();
                        summaryTableDataWrapper.setIssueData(summaryData);

                        gadgetsData.forEach(new BiConsumer<String, GadgetDataWrapper>() {
                            @Override
                            public void accept(String key, GadgetDataWrapper value) {
                                //Add total to table
                                value.getIssueData().add(getTotal(value.getIssueData()));

                                //Add summary table
                                Set<String> summaryAssignees = summaryData.stream().map(t -> t.getKey().getKey()).collect(Collectors.toSet());
                                List<GadgetData> issueData = value.getIssueData();
                                issueData.forEach(new Consumer<GadgetData>() {
                                    @Override
                                    public void accept(GadgetData gadgetData) {
                                        String assignee = gadgetData.getKey().getKey();
                                        if (summaryAssignees.contains(assignee)) {
                                            for (GadgetData assigneeData : summaryData) {
                                                if (assigneeData.getKey().getKey().equals(assignee)) {
                                                    addAllValue(gadgetData, assigneeData);
                                                }
                                            }
                                        } else {
                                            GadgetData row = new GadgetData();
                                            row.setKey(gadgetData.getKey());
                                            addAllValue(gadgetData, row);
                                            summaryData.add(row);
                                        }
                                    }
                                });
                            }
                        });
                        gadgetsData.put(Constant.SUMMARY_TABLE_KEY, summaryTableDataWrapper);
                    } else if (Gadget.Type.STORY_TEST_EXECUTION.equals(gadget.getType())) {
                        StoryVsTestExecution storyGadget = (StoryVsTestExecution) gadget;
                        gadgetsData = storyService.getDataStory(storyGadget, sessionInfo.getCookies());
                        gadgetsData.forEach(new BiConsumer<String, GadgetDataWrapper>() {
                            @Override
                            public void accept(String key, GadgetDataWrapper value) {
                                value.getIssueData().add(getTotal(value.getIssueData()));
                            }
                        });
                    } else {
                        throw new APIException(String.format("cannot fetch data for gadgetType = %s", gadget.getType()));
                    }
                } finally {
                    dataCache.setData(gadgetsData);
                    dataCache.setState(State.SUCCESS);
                }
            } else {

                throw new APIException(String.format("gadget id=%s not found", id));
            }

        }
        return ResultsUtil.convertToResult(ResultCode.SUCCESS, gadgetsData);
    }

    private GadgetData getTotal(List<GadgetData> epicData) {
        GadgetData total = new GadgetData();
        APIIssueVO totalKey = new APIIssueVO();
        totalKey.setKey(Constant.TOTAL);
        totalKey.setSummary("");
        total.setKey(totalKey);
        epicData.stream().forEach(new Consumer<GadgetData>() {
            @Override
            public void accept(GadgetData t) {
                addAllValue(t, total);
            }
        });
        return total;
    }

    private void addAllValue(GadgetData from, GadgetData to) {
        to.getBlocked().increase(from.getBlocked().getTotal());
        to.getBlocked().getIssues().addAll(from.getBlocked().getIssues());

        to.getFailed().increase(from.getFailed().getTotal());
        to.getFailed().getIssues().addAll(from.getFailed().getIssues());

        to.getPassed().increase(from.getPassed().getTotal());
        to.getPassed().getIssues().addAll(from.getPassed().getIssues());

        to.getPlanned().increase(from.getPlanned().getTotal());
        to.getPlanned().getIssues().addAll(from.getPlanned().getIssues());

        to.getUnexecuted().increase(from.getUnexecuted().getTotal());
        to.getUnexecuted().getIssues().addAll(from.getUnexecuted().getIssues());

        to.getUnplanned().increase(from.getUnplanned().getTotal());
        to.getUnplanned().getIssues().addAll(from.getUnplanned().getIssues());

        to.getWip().increase(from.getWip().getTotal());
        to.getWip().getIssues().addAll(from.getWip().getIssues());
    }

    @Override
    public Result getStoryInEpic(List<String> epics, SessionInfo sessionInfo) throws APIException {
        Map<String, JQLIssueWapper> storiesIssues = storyService.findStoryInEpic(epics, sessionInfo.getCookies());
        Map<String, Set<String>> storiesInEpic = new HashMap<>();
        storiesIssues.forEach(new BiConsumer<String, JQLIssueWapper>() {
            @Override
            public void accept(String epic, JQLIssueWapper storiesIssue) {
                // Filter issueKey
                storiesInEpic.put(epic, storiesIssue.getChild().stream().map(i -> i.getKey()).collect(Collectors.toSet()));
            }
        });

        return Results.json().render(storiesInEpic);
    }

    @Override
    public Result getProjectList(SessionInfo sessionInfo) throws APIException {
        return Results.json().render(gadgetService.getProjectList(sessionInfo));
    }

    @Override
    public Result deleteGadget(String id) throws APIException {
        return ResultsUtil.convertToResult(ResultCode.SUCCESS, gadgetService.delete(id));
    }

    @Override
    public Result cleanCache(String id, SessionInfo sessionInfo) throws APIException {
        String cacheID = id + Constant.DELIMITER + sessionInfo.getUsername();
        dataGadgetCache.remove(cacheID);
        return ResultsUtil.convertToResult(ResultCode.SUCCESS, cacheID);
    }

    @Override
    public Result cleanAllCache(SessionInfo sessionInfo) {
        dataGadgetCache.cleanUserCache(sessionInfo.getUsername());;
        gadgetService.clearUserCache(sessionInfo.getUsername());
        return ResultsUtil.convertToResult(ResultCode.SUCCESS, "");
    }

}

package handle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import manament.log.LoggerWapper;
import models.JQLIssueWapper;
import models.ResultCode;
import models.SessionInfo;
import models.exception.APIException;
import models.exception.ResultsUtil;
import models.gadget.AssigneeVsTestExecution;
import models.gadget.CycleVsTestExecution;
import models.gadget.EpicVsTestExecution;
import models.gadget.Gadget;
import models.gadget.Gadget.Type;
import models.gadget.StoryVsTestExecution;
import models.main.GadgetData;
import models.main.GadgetDataWapper;
import ninja.Context;
import ninja.Result;
import ninja.Results;
import util.Constant;
import util.JSONUtil;
import util.gadget.GadgetUtility;

public class GadgetHandlerImpl extends GadgetHandler {
    final static LoggerWapper logger = LoggerWapper.getLogger(GadgetHandlerImpl.class);

    public GadgetHandlerImpl() {
        gadgetService = GadgetUtility.getInstance();
    }

    @Override
    public Result insertOrUpdateGadget(String type, String data, Context context) throws APIException {
        Gadget gadget = null;
        String gadgetId="";
        Type gadgetType = Gadget.Type.valueOf(type);
        if(gadgetType == null){
            throw new APIException("type " + type + " not available");
        }
        if(data == null){
            throw new APIException("data cannot be null");
        }
        String username = (String) context.getSession().get(Constant.USERNAME);
        // String friendlyname = (String) context.getAttribute("alias");
        List<String> errorMessages = new ArrayList<>();
        if(Gadget.Type.EPIC_US_TEST_EXECUTION.equals(gadgetType)){
            EpicVsTestExecution epicGadget = JSONUtil.getInstance().convertJSONtoObject(data, EpicVsTestExecution.class);
            epicGadget.setUser(username);
            if(!epicGadget.isSelectAll() && (epicGadget.getEpic() == null || epicGadget.getEpic().isEmpty())){
                errorMessages.add("Epic link");
            }
            gadget = epicGadget;
        } else if(Gadget.Type.ASSIGNEE_TEST_EXECUTION.equals(gadgetType)){
            AssigneeVsTestExecution assigneeGadget = JSONUtil.getInstance().convertJSONtoObject(data, AssigneeVsTestExecution.class);
            assigneeGadget.setUser(username);
            if(!assigneeGadget.isSelectAllTestCycle() && (assigneeGadget.getCycles() == null || assigneeGadget.getCycles().isEmpty())){
                errorMessages.add("Cycle name");
            }
            gadget = assigneeGadget;
        } else if(Gadget.Type.TEST_CYCLE_TEST_EXECUTION.equals(gadgetType)){
            CycleVsTestExecution cycleGadget = JSONUtil.getInstance().convertJSONtoObject(data, CycleVsTestExecution.class);
            cycleGadget.setUser(username);
            if(!cycleGadget.isSelectAllCycle() && (cycleGadget.getCycles() == null || cycleGadget.getCycles().isEmpty())){
                errorMessages.add("Cycle name");
            }
            gadget = cycleGadget;
        } else if(Gadget.Type.STORY_TEST_EXECUTION.equals(gadgetType)){
            StoryVsTestExecution storyGadget = JSONUtil.getInstance().convertJSONtoObject(data, StoryVsTestExecution.class);
            storyGadget.setUser(username);
            if(!storyGadget.isSelectAllStory() && (storyGadget.getStories() == null || storyGadget.getStories().isEmpty())){
                errorMessages.add("Story");
            }
            if(!storyGadget.isSelectAllEpic() && (storyGadget.getEpic() == null || storyGadget.getEpic().isEmpty())){
                errorMessages.add("Epic");
            }
            gadget = storyGadget;
        }
        if(gadget != null){

            if(gadget.getDashboardId() == null){
                errorMessages.add("dashboardId cannot be null");
            }
            if(gadget.getProducts() == null || gadget.getProducts().isEmpty()){
                errorMessages.add("Products");
            }
            if(gadget.getProjectName() == null || gadget.getProjectName().isEmpty()){
                errorMessages.add("Project");
            }
            if(gadget.getRelease() == null){
                errorMessages.add("Release");
            }
            if(errorMessages.isEmpty()){
                gadgetId = gadgetService.insertOrUpdate(gadget);
            } else{
                StringBuffer error = new StringBuffer();
                errorMessages.forEach(e -> error.append(e).append(", "));
                error.append("cannot be null");
                throw new APIException(error.toString());
            }
        } else{
            throw new APIException("can not map to any Epic gadget");
        }

        return Results.json().render("message", "successful").render("data", gadgetId);
    }

    @Override
    public Result getGadgets(String id) throws APIException {
        List<Gadget> gadgets = new ArrayList<>();
        if(gadgets != null){
            gadgets.addAll(gadgetService.findByDashboardId(id));
        }
        return Results.json().render(gadgets);
    }

    @Override
    public Result getDataGadget(String id, SessionInfo sessionInfo) throws APIException {
        Map<String, GadgetDataWapper> gadgetsData = new HashMap<>();
        ;
        Gadget gadget = gadgetService.get(id);
        if(gadget != null){
            if(Gadget.Type.EPIC_US_TEST_EXECUTION.equals(gadget.getType())){
                EpicVsTestExecution epicGadget = (EpicVsTestExecution) gadget;
                List<GadgetData> epicData = epicService.getDataEPic(epicGadget, sessionInfo.getCookies());
                GadgetDataWapper epicDataWapper = new GadgetDataWapper();
                epicDataWapper.setIssueData(epicData);
                epicDataWapper.setSummary(epicGadget.getProjectName());
                gadgetsData.put(epicGadget.getProjectName(), epicDataWapper);
            } else if(Gadget.Type.TEST_CYCLE_TEST_EXECUTION.equals(gadget.getType())){
                CycleVsTestExecution cycleGadget = (CycleVsTestExecution) gadget;
                List<GadgetData> cycleData = cycleService.getDataCycle(cycleGadget, sessionInfo.getCookies());
                GadgetDataWapper epicDataWapper = new GadgetDataWapper();
                epicDataWapper.setIssueData(cycleData);
                epicDataWapper.setSummary(cycleGadget.getProjectName());
                gadgetsData.put(cycleGadget.getProjectName(), epicDataWapper);
            } else if(Gadget.Type.ASSIGNEE_TEST_EXECUTION.equals(gadget.getType())){
                AssigneeVsTestExecution assigneeGadget = (AssigneeVsTestExecution) gadget;
                gadgetsData = assigneeService.getDataAssignee(assigneeGadget, sessionInfo.getCookies());
            } else if(Gadget.Type.STORY_TEST_EXECUTION.equals(gadget.getType())){
                StoryVsTestExecution storyGadget = (StoryVsTestExecution) gadget;
                gadgetsData = storyService.getDataStory(storyGadget, sessionInfo.getCookies());
            }
        } else{
            throw new APIException(String.format("gadget id=%s not found", id));
        }
        return ResultsUtil.convertToResult(ResultCode.SUCCESS, gadgetsData);
    }

    @Override
    public Result getStoryInEpic(List<String> epics, SessionInfo sessionInfo) throws APIException {
        Map<String, JQLIssueWapper> storiesIssues = storyService.findStoryInEpic(epics,sessionInfo.getCookies());
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
        return Results.json().render(gadgetService.getProjectList(sessionInfo.getCookies()));
    }

    @Override
    public Result deleteGadget(String id) throws APIException {
        return ResultsUtil.convertToResult(ResultCode.SUCCESS, gadgetService.delete(id));
    }

}

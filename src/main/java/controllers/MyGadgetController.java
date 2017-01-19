package controllers;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import filter.APIFilter;
import handle.GadgetHandler;
import manament.log.LoggerWapper;
import models.exception.APIException;
import models.exception.ResultsUtil;
import ninja.Context;
import ninja.FilterWith;
import ninja.Result;
import ninja.params.Param;
import util.Constant;
import util.JSONUtil;

import java.util.List;

@Singleton
@FilterWith(APIFilter.class)
public class MyGadgetController {
    final static LoggerWapper logger = LoggerWapper.getLogger(MyGadgetController.class);
    @Inject
    private GadgetHandler handler;

    public MyGadgetController() {
    }

    public Result insertOrUpdateGadget(@Param("type") String type, @Param("data") String data, Context context) {
        try {
            return handler.insertOrUpdateGadget(type, data, ResultsUtil.getSessionInfo(context));
        } catch (APIException e) {
            return ResultsUtil.convertException(e, context);
        }
    }

    public Result deleteGadget(@Param("id") String id, Context context) {
        try {
            logger.fasttrace("deleteGadget(%s) , by user:%s", id, context.getSession().get(Constant.USERNAME));
            return handler.deleteGadget(id);
        } catch (APIException e) {
            return ResultsUtil.convertException(e, context);
        }
    }

    public Result getGadgetsInDashboardId(@Param("dashboardId") String id, Context context) {
        logger.fasttrace("getGadgetsInDashboardId(%s)", id);
        try {
            return handler.getGadgets(id);
        } catch (APIException e) {
            return ResultsUtil.convertException(e, context);
        }

    }

    public Result getDataGadget(@Param("id") String id, Context context) {
        logger.fasttrace("getDataGadget(%s)", id);
        try {
            return handler.getDataGadget(id, ResultsUtil.getSessionInfo(context));
        } catch (APIException e) {
            return ResultsUtil.convertException(e, context);
        }
    }

    public Result getStoryInEpic(@Param("epics") String epic, Context context) {
        logger.fasttrace("getStoryInEpic(%s)", epic);
        try {
            List<String> epics = JSONUtil.getInstance().convertJSONtoListObject(epic, String.class);
            return handler.getStoryInEpic(epics, ResultsUtil.getSessionInfo(context));
        } catch (APIException e) {
            return ResultsUtil.convertException(e, context);
        }
    }


    public Result getProjectList(Context context) {
        logger.fasttrace("getProjectList()");
        try {
            return handler.getProjectList(ResultsUtil.getSessionInfo(context));
        } catch (APIException e) {
            return ResultsUtil.convertException(e, context);
        }
    }

    public Result cleanCache(@Param("id") String id, Context context) {
        logger.fasttrace("cleanCache(%s)", id);
        try {
            return handler.cleanCache(id, ResultsUtil.getSessionInfo(context));
        } catch (APIException e) {
            return ResultsUtil.convertException(e, context);
        }
    }

    public Result cleanAllCache(Context context) {
        logger.fasttrace("cleanAllCache()");
        try {
            return handler.cleanAllCache(ResultsUtil.getSessionInfo(context));
        } catch (APIException e) {
            return ResultsUtil.convertException(e, context);
        }
    }
}

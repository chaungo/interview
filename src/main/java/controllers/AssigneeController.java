package controllers;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import filter.APIFilter;
import handle.AssigneeHandler;
import manament.log.LoggerWapper;
import models.exception.APIException;
import models.exception.ResultsUtil;
import ninja.Context;
import ninja.FilterWith;
import ninja.Result;
import ninja.Results;
import ninja.params.Param;
import util.AdminUtility;
import util.JSONUtil;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Singleton
@FilterWith(APIFilter.class)
public class AssigneeController {
    final static LoggerWapper logger = LoggerWapper.getLogger(AssigneeController.class);

    @Inject
    private AssigneeHandler handler;

    public AssigneeController() {
    }

    public Result getAssigneeList(@Param("project") String projectName, @Param("release") String release, Context context) {
        logger.fasttrace("getAssigneeList(%s,%s)", projectName, release);
        try {
            return handler.getAssigneeList(projectName, release, ResultsUtil.getSessionInfo(context));
        } catch (APIException e) {
            return ResultsUtil.convertException(e, context);
        }
    }

    public Result getListCycleName(@Param("project") String projectName, @Param("release") String release, @Param("products") String productArrays, Context context) {
        logger.fasttrace("getListCycleName(%s,%s,%s)", projectName, release, productArrays);
        try {
            List<String> productList = JSONUtil.getInstance().convertJSONtoListObject(productArrays, String.class);
            Set<String> products = null;
            if (productList != null) {
                products = new HashSet<>(productList);
            }
            return handler.getListCycleName(projectName, release, products, ResultsUtil.getSessionInfo(context));
        } catch (APIException e) {
            return ResultsUtil.convertException(e, context);
        }
    }

    public Result getListExistingCycle(Context context) {
        logger.fasttrace("getListExistingCycle()");
        Set<String> cycles = AdminUtility.getInstance().getAllRelease();
        return Results.json().render(cycles);
    }
}

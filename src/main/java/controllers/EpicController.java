package controllers;

import java.util.List;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import filter.APIFilter;
import handle.EpicHandler;
import manament.log.LoggerWapper;
import models.exception.APIException;
import models.exception.ResultsUtil;
import ninja.Context;
import ninja.FilterWith;
import ninja.Result;
import ninja.params.Param;
import util.JSONUtil;

@Singleton
@FilterWith(APIFilter.class)
public class EpicController {
    final static LoggerWapper logger = LoggerWapper.getLogger(EpicController.class);
    @Inject
    private EpicHandler handler;

    public EpicController() {
    }

    public Result getEpicLinks(@Param("project") String project, @Param("release") String release, @Param("products") String productArrays, Context context) {
        try{
            logger.fasttrace("getEpicLinks(%s,%s,%s)",project, release, productArrays);
            List<String> products = JSONUtil.getInstance().convertJSONtoListObject(productArrays, String.class);
            return handler.getEpicLinks(project, release, products, ResultsUtil.getSessionInfo(context));
        } catch (APIException e){
            return ResultsUtil.convertException(e);
        }
    }
    
}

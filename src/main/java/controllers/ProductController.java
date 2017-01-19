package controllers;

import com.google.inject.Singleton;
import filter.APIFilter;
import manament.log.LoggerWapper;
import models.ResultCode;
import models.exception.APIException;
import models.exception.ResultsUtil;
import ninja.Context;
import ninja.FilterWith;
import ninja.Result;
import ninja.params.Param;
import util.AdminUtility;
import util.Constant;
import util.gadget.GadgetUtility;

import java.util.Set;

@Singleton
@FilterWith(APIFilter.class)
public class ProductController {
    final static LoggerWapper logger = LoggerWapper.getLogger(ProductController.class);

    public Result addProduct(@Param("product") String product) {
        return ResultsUtil.convertToResult(ResultCode.SUCCESS, AdminUtility.getInstance().insertProduct(product));
    }

    public Result deleteProduct(@Param("product") String product) {
        long result = AdminUtility.getInstance().deleteProduct(product);
        return ResultsUtil.convertToResult(ResultCode.SUCCESS, result);
    }

    public Result addRelease(@Param("release") String release, Context context) {
        try {
            return ResultsUtil.convertToResult(ResultCode.SUCCESS, AdminUtility.getInstance().insertRelease(release, ResultsUtil.getSessionInfo(context)));
        } catch (APIException e) {
            return ResultsUtil.convertException(e, context);
        }
    }

    public Result deleteRelease(@Param("release") String release) {
        long result = AdminUtility.getInstance().deleteRelease(release);
        return ResultsUtil.convertToResult(ResultCode.SUCCESS, result);
    }

    public Result clearCache(@Param("release") String release, Context context) {
        String user = (String) context.getSession().get(Constant.USERNAME);
        logger.fastDebug("User: %s perform clearCache", user);
        GadgetUtility.getInstance().clearUserCache(user);
        return ResultsUtil.convertToResult(ResultCode.SUCCESS, "");
    }

    public Result getAllProduct() {
        Set<String> products = AdminUtility.getInstance().getAllProduct();
        return ResultsUtil.convertToResult(ResultCode.SUCCESS, products);
    }

    public Result getAllRelease() {
        Set<String> releases = AdminUtility.getInstance().getAllRelease();
        return ResultsUtil.convertToResult(ResultCode.SUCCESS, releases);
    }
}

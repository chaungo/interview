package controllers;

import com.google.inject.Singleton;
import filter.AdminSecureFilter;
import manament.log.LoggerWapper;
import models.ResultCode;
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
public class ProductController {
    final static LoggerWapper logger = LoggerWapper.getLogger(ProductController.class);

    @FilterWith(AdminSecureFilter.class)
    public Result addProduct(@Param("product") String product) {
        return ResultsUtil.convertToResult(ResultCode.SUCCESS, AdminUtility.getInstance().insertProduct(product));
    }

    @FilterWith(AdminSecureFilter.class)
    public Result deleteProduct(@Param("product") String product) {
        long result = AdminUtility.getInstance().deleteProduct(product);
        return ResultsUtil.convertToResult(ResultCode.SUCCESS, result);
    }

    @FilterWith(AdminSecureFilter.class)
    public Result addCycle(@Param("cycle") String cycle) {
        return ResultsUtil.convertToResult(ResultCode.SUCCESS, AdminUtility.getInstance().insertCycle(cycle));
    }

    @FilterWith(AdminSecureFilter.class)
    public Result deleteCycle(@Param("cycle") String cycle) {
        long result = AdminUtility.getInstance().deleteCycle(cycle);
        return ResultsUtil.convertToResult(ResultCode.SUCCESS, result);
    }

    @FilterWith(AdminSecureFilter.class)
    public Result clearCache(@Param("cycle") String cycle, Context context) {
        String user = (String) context.getSession().get(Constant.USERNAME);
        logger.fastDebug("User: %s perform clearCache", user);
        GadgetUtility.getInstance().clearCache();
        return ResultsUtil.convertToResult(ResultCode.SUCCESS, "");
    }

    public Result getAllProduct() {
        Set<String> products = AdminUtility.getInstance().getAllProduct();
        return ResultsUtil.convertToResult(ResultCode.SUCCESS, products);
    }

}

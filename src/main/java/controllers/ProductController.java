package controllers;

import com.google.inject.Singleton;
import filter.AdminSecureFilter;
import manament.log.LoggerWapper;
import models.ResultCode;
import models.exception.ResultsUtil;
import ninja.FilterWith;
import ninja.Result;
import ninja.Results;
import ninja.params.Param;
import util.AdminUtility;

import java.util.Set;

@Singleton
public class ProductController {
    final static LoggerWapper logger = LoggerWapper.getLogger(ProductController.class);

    @FilterWith(AdminSecureFilter.class)
    public Result addProduct(@Param("product") String product) {
        return Results.json().render("type", "success").render("data", AdminUtility.getInstance().insertProduct(product));
    }

    @FilterWith(AdminSecureFilter.class)
    public Result deleteProduct(@Param("product") String product) {
        long result = AdminUtility.getInstance().deleteProduct(product);
        return Results.json().render("type", "success").render("data", result);
    }

    @FilterWith(AdminSecureFilter.class)
    public Result addCycle(@Param("cycle") String cycle) {
        return Results.json().render("type", "success").render("data", AdminUtility.getInstance().insertCycle(cycle));
    }

    @FilterWith(AdminSecureFilter.class)
    public Result deleteCycle(@Param("cycle") String cycle) {
        long result = AdminUtility.getInstance().deleteCycle(cycle);
        return Results.json().render("type", "success").render("data", result);
    }


}

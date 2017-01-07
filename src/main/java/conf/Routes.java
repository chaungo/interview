package conf;

import controllers.*;
import ninja.AssetsController;
import ninja.Router;
import ninja.application.ApplicationRoutes;

public class Routes implements ApplicationRoutes {
    @Override
    public void init(Router router) {
        router.GET().route("/login").with(LoginLogoutController.class, "login");
        router.POST().route("/login").with(LoginLogoutController.class, "loginPost");
        router.GET().route("/logout").with(LoginLogoutController.class, "logout");
        router.POST().route("/getUserInfo").with(ApplicationController.class, "getUserInfo");
        router.POST().route("/getProjectList").with(ApplicationController.class, "getProjectList");


        router.POST().route("/getGadgetList").with(GadgetController.class, "getGadgetList");

        router.POST().route("/addNewGadget").with(GadgetController.class, "addNewGadget");
        router.POST().route("/updateGadget").with(GadgetController.class, "updateGadget");
        router.POST().route("/clearCacheGadget").with(GadgetController.class, "clearCacheGadget");
        router.POST().route("/deleteGadget").with(GadgetController.class, "deleteGadget");
        router.POST().route("/showSonarStatisticGadget").with(GadgetController.class, "showSonarStatisticGadget");

        router.POST().route("/getIAComponents").with(ApplicationController.class, "getIAComponents");
        router.POST().route("/getMetrics").with(ApplicationController.class, "getMetricList");
        router.POST().route("/getReleaseList").with(ApplicationController.class, "getReleaseList");
        router.POST().route("/getPeriodList").with(ApplicationController.class, "getPeriodList");

        router.POST().route("/getCruProjectList").with(OverdueReviewReportController.class, "getCruProjectList");


        router.GET().route("/release").with(ConfigurationController.class, "release");
        router.GET().route("/release/ialist/{name}").with(ConfigurationController.class, "releaseURL");
        router.POST().route("/release").with(ConfigurationController.class, "releasePost");
        router.POST().route("/release/update").with(ConfigurationController.class, "releaseUpdate");
        router.POST().route("/release/delete").with(ConfigurationController.class, "releaseDelete");

        router.GET().route("/metric").with(ConfigurationController.class, "metric");
        router.POST().route("/metric").with(ConfigurationController.class, "metricPost");
        router.POST().route("/metric/update").with(ConfigurationController.class, "metricUpdate");
        router.POST().route("/metric/delete").with(ConfigurationController.class, "metricDelete");


//        router.GET().route("/dashboard").with(DashboardController.class, "dashboard");
//        router.GET().route("/dashboard/page={id}").with(DashboardController.class, "dashboard");
        router.GET().route("/dashboard/new").with(DashboardController.class, "new_dashboard");
        router.POST().route("/dashboard/new").with(DashboardController.class, "new_dashboard_post");
        router.POST().route("/deleteDashboard").with(DashboardController.class, "deleteDashboard");
//        router.GET().route("/dashboard/find").with(DashboardController.class, "find_dashboard");
//        router.POST().route("/dashboard/find").with(DashboardController.class, "find_dashboard_post");
//        router.GET().route("/dashboard/{id}/update").with(DashboardController.class, "update_dashboard");
//        router.POST().route("/dashboard/{id}/update").with(DashboardController.class, "update_dashboard_post");
//        router.GET().route("/dashboard/{id}/delete").with(DashboardController.class, "delete_dashboard");
//        router.GET().route("/dashboard/{id}/clone").with(DashboardController.class, "clone_dashboard");
        router.POST().route("/getDashboardInfo").with(DashboardController.class, "getDashboardInfo");
        router.POST().route("/getDashboardList").with(DashboardController.class, "getDashboardList");
        router.POST().route("/updateDashboardOption").with(DashboardController.class, "updateDashboardOption");


        router.GET().route("/assets/webjars/{fileName: .*}").with(AssetsController.class, "serveWebJars");
        router.GET().route("/assets/{fileName: .*}").with(AssetsController.class, "serveStatic");

//        router.GET().route("/page={id}").with(ApplicationController.class, "index");

        router.GET().route("/gadget/getData").with(MyGadgetController.class, "getDataGadget");
        router.POST().route("/gadget/save").with(MyGadgetController.class, "insertOrUpdateGadget");
        router.GET().route("/gadget/gadgets").with(MyGadgetController.class, "getGadgetsInDashboardId");
        router.GET().route("/gadget/getStoryInEpic").with(MyGadgetController.class, "getStoryInEpic");

        router.GET().route("/product").with(ProductController.class, "productPage");
        router.POST().route("/product/deleteProduct").with(ProductController.class, "deleteProduct");
        router.POST().route("/product/insertProduct").with(ProductController.class, "addProduct");
        router.POST().route("/product/deleteCycle").with(ProductController.class, "deleteCycle");
        router.POST().route("/product/insertCycle").with(ProductController.class, "addCycle");
        router.GET().route("/product/getall").with(ProductController.class, "getAllProduct");

        router.GET().route("/getEpicLinks").with(EpicController.class, "getEpicLinks");
        router.GET().route("/getassignee").with(AssigneeController.class, "getAssigneeList");
        router.GET().route("/listcycle").with(AssigneeController.class, "getListCycleName");
        router.GET().route("/cycleExisting").with(AssigneeController.class, "getListExistingCycle");

        router.GET().route("/listproject").with(MyGadgetController.class, "getProjectList");
        router.GET().route("/.*").with(ApplicationController.class, "index");
    }

}

package conf;

import controllers.*;
import ninja.AssetsController;
import ninja.Results;
import ninja.Router;
import ninja.application.ApplicationRoutes;

public class Routes implements ApplicationRoutes {
    @Override
    public void init(Router router) {
        router.GET().route("/login").with(LoginLogoutController.class, "login");
        router.POST().route("/loginCru").with(LoginLogoutController.class, "loginCru");
        router.POST().route("/login").with(LoginLogoutController.class, "loginPost");
        router.GET().route("/logout").with(LoginLogoutController.class, "logout");
        router.POST().route("/clearSession").with(LoginLogoutController.class, "clearSession");

        router.POST().route("/getUserInfo").with(ApplicationController.class, "getUserInfo");
        router.POST().route("/getProjectList").with(ApplicationController.class, "getProjectList");

        router.POST().route("/getGadgetList").with(GadgetController.class, "getGadgetList");

        router.POST().route("/addNewGadget").with(GadgetController.class, "addNewGadget");
        router.POST().route("/updateGadget").with(GadgetController.class, "updateGadget");
        router.POST().route("/clearCacheGadget").with(GadgetController.class, "clearCacheGadget");
        router.POST().route("/deleteGadget").with(GadgetController.class, "deleteGadget");

        router.POST().route("/showGadgets").with(GadgetController.class, "showGadgets");
        router.POST().route("/getIAComponents").with(SonarStatisticGadgetController.class, "getIAComponents");
        router.POST().route("/getMetrics").with(SonarStatisticGadgetController.class, "getMetricList");
        router.POST().route("/getReleaseList").with(SonarStatisticGadgetController.class, "getReleaseList");


        router.POST().route("/getCruProjectList").with(OverdueReviewReportController.class, "getCruProjectList");

        router.GET().route("/configuration").with(ConfigurationController.class, "configuration");
        router.POST().route("/addNewRelease").with(ConfigurationController.class, "addNewRelease");
        router.POST().route("/deleteRelease").with(ConfigurationController.class, "deleteRelease");
        router.POST().route("/updateRelease").with(ConfigurationController.class, "updateRelease");
        router.POST().route("/getPeriodList").with(ConfigurationController.class, "getPeriodList");
        router.POST().route("/setPeriod").with(ConfigurationController.class, "setPeriod");


        router.GET().route("/dashboard/new").with(DashboardController.class, "new_dashboard");
        router.POST().route("/dashboard/new").with(DashboardController.class, "new_dashboard_post");
        router.POST().route("/deleteDashboard").with(DashboardController.class, "deleteDashboard");
        router.POST().route("/getDashboardInfo").with(DashboardController.class, "getDashboardInfo");
        router.POST().route("/getDashboardList").with(DashboardController.class, "getDashboardList");
        router.POST().route("/updateDashboardOption").with(DashboardController.class, "updateDashboardOption");
        router.POST().route("/deleteAllDashboard").with(DashboardController.class, "deleteAllDashboard");

        router.GET().route("/assets/webjars/{fileName: .*}").with(AssetsController.class, "serveWebJars");
        router.GET().route("/assets/{fileName: .*}").with(AssetsController.class, "serveStatic");

        router.GET().route("/gadget/getData").with(MyGadgetController.class, "getDataGadget");
        router.GET().route("/gadget/cleanCache").with(MyGadgetController.class, "cleanCache");
        router.POST().route("/gadget/save").with(MyGadgetController.class, "insertOrUpdateGadget");
        router.GET().route("/gadget/gadgets").with(MyGadgetController.class, "getGadgetsInDashboardId");
        router.GET().route("/gadget/getStoryInEpic").with(MyGadgetController.class, "getStoryInEpic");
        router.GET().route("/gadget/delete").with(MyGadgetController.class, "deleteGadget");

        router.POST().route("/product/deleteProduct").with(ProductController.class, "deleteProduct");
        router.POST().route("/product/insertProduct").with(ProductController.class, "addProduct");
        router.POST().route("/product/deleteRelease").with(ProductController.class, "deleteRelease");
        router.POST().route("/product/insertRelease").with(ProductController.class, "addRelease");
        router.GET().route("/product/getall").with(ProductController.class, "getAllProduct");
        router.GET().route("/listRelease").with(ProductController.class, "getAllRelease");

        router.GET().route("/getEpicLinks").with(EpicController.class, "getEpicLinks");
        router.GET().route("/getassignee").with(AssigneeController.class, "getAssigneeList");
        router.GET().route("/listcycle").with(AssigneeController.class, "getListCycleName");
        router.GET().route("/cycleExisting").with(AssigneeController.class, "getListExistingCycle");

        router.GET().route("/listproject").with(MyGadgetController.class, "getProjectList");
        router.GET().route("/clearCache").with(MyGadgetController.class, "cleanCache");
        router.GET().route("/cleanAllCache").with(MyGadgetController.class, "cleanAllCache");

        router.GET().route("/").with(ApplicationController.class, "index");
        router.GET().route("/.*").with(Results.html().template("views/system/404notFound.ftl.html"));

    }

}

package controllers;

import filter.SecureFilter;
import models.gadget.Gadget;
import models.gadget.OverdueReviewsGadget;
import models.gadget.SonarStatisticsGadget;
import ninja.FilterWith;
import ninja.Result;
import ninja.Results;
import ninja.params.Param;
import ninja.session.Session;
import util.JSONUtil;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static service.GadgetService.getDashboardGadgetbyDashboardId;
import static util.MyUtill.*;

/**
 * Created by nnmchau on 1/7/2017.
 */
public class SonarStatisticGadgetController {
    final static Logger logger = Logger.getLogger(SonarStatisticGadgetController.class);

    @FilterWith(SecureFilter.class)
    public Result showSonarStatisticGadget(@Param("id") String dashboardId, Session session) {
        JSONArray sonarStatisticsGadget = new JSONArray();
        JSONArray overdueReviewGadget = new JSONArray();
        JSONArray greenHopperGadgets = new JSONArray();
        JSONObject result = new JSONObject();
        List<Gadget> dashboardGadgets;
        try {
            dashboardGadgets = getDashboardGadgetbyDashboardId(dashboardId);
            if (dashboardGadgets != null) {
                for (Gadget gadget : dashboardGadgets) {
                    Gadget.Type type = gadget.getType();
                    if (Gadget.Type.AMS_SONAR_STATISTICS_GADGET.equals(type)) {
                        sonarStatisticsGadget.put(getSonarStatistic(session, new JSONObject(((SonarStatisticsGadget) gadget).getData()), ((SonarStatisticsGadget) gadget).getId()));
                    } else if (Gadget.Type.AMS_OVERDUE_REVIEWS.equals(type)) {
                        overdueReviewGadget.put(getReview(session, new JSONObject(((OverdueReviewsGadget) gadget).getData()), ((OverdueReviewsGadget) gadget).getId()));
                    } else {
                        greenHopperGadgets.put(new JSONObject(JSONUtil.getInstance().convertToString(gadget)));
                    }

                }
            }
            result.put("AMSSONARStatisticsGadget", sonarStatisticsGadget);
            result.put("AMSOverdueReviewsReportGadget", overdueReviewGadget);
            result.put("GreenHopperGadget", greenHopperGadgets);
        } catch (Exception e) {
            logger.error("show_dashboard ", e);
            return Results.internalServerError();
        }
        return Results.text().render(result);
    }


    @FilterWith(SecureFilter.class)
    public Result addNewSonarWidget() {
        return Results.html();
    }


    @FilterWith(SecureFilter.class)
    public Result getIAComponents(Session session, @Param("data") String data) {
        try {
            return Results.text().render(getIAComponentsRespond(session, (new JSONObject(data)).getString("url"), null));
        } catch (Exception e) {
            e.printStackTrace();
            return Results.internalServerError();
        }
    }

    @FilterWith(SecureFilter.class)
    public Result getPeriodList(Session session) {
        try {
            return Results.text().render(getPeriod(session));

        } catch (Exception e) {
            e.printStackTrace();
            return Results.internalServerError();
        }
    }


    @FilterWith(SecureFilter.class)
    public Result getReleaseList() {
        try {
            return Results.text().render(getReleasesFromDB(null));
        } catch (Exception e) {
            e.printStackTrace();
            return Results.internalServerError();
        }
    }

    @FilterWith(SecureFilter.class)
    public Result getMetricList() {
        try {
            return Results.text().render(getMetricsFromDB());
        } catch (Exception e) {
            e.printStackTrace();
            return Results.internalServerError();
        }
    }


}

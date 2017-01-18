package controllers;

import com.google.inject.Singleton;
import filter.SecureFilter;
import models.gadget.Gadget;
import models.gadget.OverdueReviewsGadget;
import models.gadget.SonarStatisticsGadget;
import ninja.FilterWith;
import ninja.Result;
import ninja.Results;
import ninja.params.Param;
import ninja.session.Session;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import util.JSONUtil;

import java.util.List;

import static controllers.OverdueReviewReportController.getReview;
import static controllers.SonarStatisticGadgetController.getSonarStatistic;
import static service.GadgetService.*;
import static util.Constant.AMS_SONAR_STATISTICS_GADGET_KEY;
import static util.Constant.AMS_OVERDUE_REVIEWS_REPORT_GADGET_KEY;
import static util.Constant.GREENHOPPER_GADGET_KEY;


/**
 * Created by nnmchau on 12/27/2016.
 */
@Singleton
public class GadgetController {

    final static Logger logger = Logger.getLogger(GadgetController.class);


    @FilterWith(SecureFilter.class)
    public Result getGadgetList() {
        try {
            return Results.text().render(getGadgetListfromDB());
        } catch (Exception e) {
            logger.error(e);
            return Results.internalServerError();
        }
    }


    @FilterWith(SecureFilter.class)
    public Result showGadgets(@Param("id") String dashboardId, Session session) {
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

            result.put(AMS_SONAR_STATISTICS_GADGET_KEY, sonarStatisticsGadget);
            result.put(AMS_OVERDUE_REVIEWS_REPORT_GADGET_KEY, overdueReviewGadget);
            result.put(GREENHOPPER_GADGET_KEY, greenHopperGadgets);
        } catch (JSONException | NullPointerException e) {
            JSONObject er = new JSONObject();
            er.put("Err",e);
            e.printStackTrace();
            return Results.text().render(er);
        } catch (Exception e){
            logger.error("show_dashboard ", e);
            return Results.internalServerError();
        }
        return Results.text().render(result);
    }


    @FilterWith(SecureFilter.class)
    public Result addNewGadget(@Param("data") String data) {
        System.out.println(data);
        try {
            JSONObject dataObject = new JSONObject(data);
            String id = dataObject.getString("DashboardId");
            String type = dataObject.getString("GadgetType");
            JSONObject Info = dataObject.getJSONObject("Data");

            insertDashboardGadgettoDB(id, type, Info.toString());
            return Results.ok();
        } catch (Exception e) {
            logger.error(e);
            return Results.internalServerError();
        }
    }

    @FilterWith(SecureFilter.class)
    public Result updateGadget(@Param("data") String data) {
        try {
            JSONObject dataObject = new JSONObject(data);
            String GadgetId = dataObject.getString("DashboardGadgetId");
            JSONObject updateData = dataObject.getJSONObject("Data");
            updateDashboardGadgettoDB(GadgetId, updateData.toString());
            return Results.ok();
        } catch (Exception e) {
            logger.error(e);
            return Results.internalServerError();
        }
    }


    @FilterWith(SecureFilter.class)
    public Result deleteGadget(@Param("GadgetId") String id) {
        try {
            deleteDashboardGadgetfromDB(id);
        } catch (Exception e) {
            logger.error(e);
            return Results.internalServerError();
        }
        return Results.ok();
    }

    @FilterWith(SecureFilter.class)
    public Result clearCacheGadget(@Param("GadgetId") String id) {
        try {
            clearCacheGadgetfromDB(id);
        } catch (Exception e) {
            logger.error(e);
            return Results.internalServerError();
        }
        return Results.ok();
    }
}

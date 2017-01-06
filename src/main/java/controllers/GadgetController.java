package controllers;

import com.google.inject.Singleton;
import filter.SecureFilter;
import models.gadget.Gadget;
import models.gadget.SONARGadget;
import models.gadget.Gadget.Type;
import models.gadget.OverdueReviewsGadget;
import ninja.FilterWith;
import ninja.Result;
import ninja.Results;
import ninja.params.Param;
import ninja.session.Session;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import static util.MyUtill.*;

import java.util.ArrayList;
import java.util.List;

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
            e.printStackTrace();
            return Results.internalServerError();
        }
    }


   /* @FilterWith(SecureFilter.class)
    public Result showSonarStatisticGadget(@Param("id") String dashboardId, Session session) {
        JSONArray sonarStatisticsGadget = new JSONArray();
        JSONArray overdueReviewGadget = new JSONArray();
        JSONObject result = new JSONObject();
        JSONArray DashboardGadgets;
        try {
            DashboardGadgets = getDashboardGadgetbyDashboardId(dashboardId);
            for (int i = 0; i < DashboardGadgets.length(); i++) {
                JSONObject gadget = DashboardGadgets.getJSONObject(i);
                String type = gadget.getString("type");
                if (type.equals("AMS SONAR Statistics Gadget")) {
                    sonarStatisticsGadget.put(getSonarStatistic(session, gadget.getJSONObject("data"), gadget.get("id").toString()));
                }

                if (type.equals("AMS Overdue Reviews Report Gadget")) {
                    overdueReviewGadget.put(getReview(session, gadget.getJSONObject("data"), gadget.get("id").toString()));
                }
            }

            result.put("AMSSONARStatisticsGadget", sonarStatisticsGadget);
            result.put("AMSOverdueReviewsReportGadget", overdueReviewGadget);
        } catch (Exception e) {
            logger.error("show_dashboard ", e);
            return Results.internalServerError();
        }

        return Results.text().render(result);
    }*/
    
    @FilterWith(SecureFilter.class)
    public Result showSonarStatisticGadget(@Param("id") String dashboardId, Session session) {
        JSONArray sonarStatisticsGadget = new JSONArray();
        JSONArray overdueReviewGadget = new JSONArray();
        JSONObject result = new JSONObject();
         List<Gadget> dashboardGadgets;
         List<Gadget> greenHopperGadget = new ArrayList<>();
        try {
            dashboardGadgets = getDashboardGadgetbyDashboardId(dashboardId);
            if(dashboardGadgets!=null){
            for(Gadget gadget : dashboardGadgets){
                Type type = gadget.getType();
                if(Type.AMS_SONAR_STATISTICS_GADGET.equals(type)){
                    sonarStatisticsGadget.put(getSonarStatistic(session, new JSONObject(((SONARGadget)gadget).getData()),  ((SONARGadget)gadget).getId()));
                }else if (Type.AMS_OVERDUE_REVIEWS.equals(type)) {
                    overdueReviewGadget.put(getReview(session, new JSONObject(((OverdueReviewsGadget)gadget).getData()), ((OverdueReviewsGadget)gadget).getId()));
                }else{
                    greenHopperGadget.add(gadget);
                }
                
            }
            }
            result.put("AMSSONARStatisticsGadget", sonarStatisticsGadget);
            result.put("AMSOverdueReviewsReportGadget", overdueReviewGadget);
            result.put("GreenHopperGadget", greenHopperGadget);
        } catch (Exception e) {
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
            e.printStackTrace();
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
            e.printStackTrace();
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

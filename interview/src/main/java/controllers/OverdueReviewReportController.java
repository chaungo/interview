package controllers;

import filter.SecureFilter;
import ninja.FilterWith;
import ninja.Result;
import ninja.Results;
import ninja.session.Session;
import org.apache.log4j.Logger;

import static util.MyUtill.getCruProjectfromServer;
import static util.MyUtill.getCruUserfromServer;

/**
 * Created by nnmchau on 1/4/2017.
 */
public class OverdueReviewReportController {

    final static Logger logger = Logger.getLogger(OverdueReviewReportController.class);

    @FilterWith(SecureFilter.class)
    public Result getCruProjectList(Session session) {
        try {
            return Results.text().render(getCruProjectfromServer(session));
        } catch (Exception e) {
            e.printStackTrace();
            return Results.internalServerError();
        }
    }

    @FilterWith(SecureFilter.class)
    public Result getCruUserList(Session session) {
        try {
            return Results.text().render(getCruUserfromServer(session));
        } catch (Exception e) {
            e.printStackTrace();
            return Results.internalServerError();
        }
    }
}

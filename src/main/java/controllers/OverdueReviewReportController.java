package controllers;

import filter.SecureFilter;
import ninja.FilterWith;
import ninja.Result;
import ninja.Results;
import ninja.session.Session;
import org.apache.log4j.Logger;

import static service.OverdueReviewReportService.getCruProjectfromServer;


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
            logger.error(e);
            return Results.internalServerError();
        }
    }
}

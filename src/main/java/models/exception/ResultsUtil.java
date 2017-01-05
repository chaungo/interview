package models.exception;

import models.ResultCode;
import models.SessionInfo;
import ninja.Context;
import ninja.Result;
import ninja.Results;
import util.Constant;
import util.PropertiesUtil;

public class ResultsUtil {
    public static Result convertException(APIException e) {
        Result result = Results.json();
        result.render("type", "error");
        result.render("data", e.getMessage());
        return result;
    }
    
    public static Result convertToResult(ResultCode type, Object data){
        return Results.json().render("type", type).render("data", data);
    }
    
    public static SessionInfo getSessionInfo(Context context) throws APIException{
        SessionInfo sessionInfo = context.getAttribute(Constant.API_SESSION_INFO_INTERNAL, SessionInfo.class);
        if(sessionInfo ==null || sessionInfo.getCookies() ==null || sessionInfo.getCookies().isEmpty()){
            throw new APIException(PropertiesUtil.getString(Constant.SESSION_ERROR_MESSAGE));
        }
        return sessionInfo;
    }
}

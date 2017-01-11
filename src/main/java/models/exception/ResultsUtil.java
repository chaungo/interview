package models.exception;

import models.ResultCode;
import models.SessionInfo;
import ninja.Context;
import ninja.Result;
import ninja.Results;
import util.Constant;
import util.MessageConstant;
import util.PropertiesUtil;

public class ResultsUtil {
    public static Result convertException(APIException e, Context context) {
        if (APIErrorCode.COOKIES_EXPIRED.equals(e.getErrorCode())) {
            context.getSession().clear();
        }
        Result result = Results.json();
        result.render("type", "error");
        result.render("data", e.getMessage());
        result.render("errorCode", e.getErrorCode().toString());
        return result;
    }

    public static Result convertToResult(ResultCode type, Object data) {
        return Results.json().render("type", type).render("data", data);
    }

    public static SessionInfo getSessionInfo(Context context) throws APIException {
        SessionInfo sessionInfo = context.getAttribute(Constant.API_SESSION_INFO_INTERNAL, SessionInfo.class);
        if (sessionInfo == null || sessionInfo.getCookies() == null || sessionInfo.getCookies().isEmpty()) {
            throw new APIException(PropertiesUtil.getString(MessageConstant.SESSION_ERROR_MESSAGE));
        }
        return sessionInfo;
    }
}

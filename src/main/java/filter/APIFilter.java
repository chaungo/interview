package filter;

import manament.log.LoggerWapper;
import models.SessionInfo;
import models.exception.APIException;
import models.exception.ResultsUtil;
import ninja.Context;
import ninja.Filter;
import ninja.FilterChain;
import ninja.Result;
import util.Constant;
import util.JSONUtil;
import util.PropertiesUtil;

public class APIFilter implements Filter {
    final static LoggerWapper logger = LoggerWapper.getLogger(APIFilter.class);

    @Override
    public Result filter(FilterChain filterChain, Context context) {
        String sessionCookies = context.getSession().get(Constant.API_SESSION_INFO);
        if(sessionCookies != null && !sessionCookies.isEmpty()){
            try{
                SessionInfo sessionInfo = JSONUtil.getInstance().convertJSONtoObject(sessionCookies, SessionInfo.class);
                context.setAttribute(Constant.API_SESSION_INFO_INTERNAL, sessionInfo);
                return filterChain.next(context);
            } catch (APIException e){
                logger.fastDebug("No cookies are available", e, new Object());
            }
        }
        return ResultsUtil.convertException(new APIException(PropertiesUtil.getString(Constant.SESSION_ERROR_MESSAGE)));
    }

}

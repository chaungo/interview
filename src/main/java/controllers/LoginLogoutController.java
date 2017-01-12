package controllers;

import com.google.inject.Singleton;
import models.SessionInfo;
import ninja.Result;
import ninja.Results;
import ninja.params.Param;
import ninja.session.Session;
import org.apache.log4j.Logger;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import service.HTTPClientUtil;
import util.Constant;
import util.JSONUtil;

import java.util.Map;

import static controllers.ApplicationController.getUserInformation;
import static ninja.Results.redirect;
import static util.Constant.*;

@Singleton
public class LoginLogoutController {

    public static final Logger logger = Logger.getLogger(LoginLogoutController.class);


    public static boolean doLogin(String username, String password, Session session) throws Exception {
        boolean success = true;
        try {
            Connection.Response respond = Jsoup.connect(LOGIN_LINK).data(USERNAME_LOGIN_KEY, username).data(PASSWORD_LOGIN_KEY, password)
                    .data(REMEMBER_LOGIN_KEY, "true").method(Connection.Method.POST).timeout(CONNECTION_TIMEOUT).execute();

            Map<String, String> cookies = respond.cookies();
            session.put("cookies", cookies.toString());

            //login to greenhopper
            Map<String, String> cookiesMap = HTTPClientUtil.getInstance().loginGreenhopper(username, password, true);
            if (cookiesMap != null && !cookiesMap.isEmpty()) {
                SessionInfo sessionInfo = new SessionInfo();
                sessionInfo.setCookies(cookiesMap);
                String sessionInfoStr = JSONUtil.getInstance().convertToString(sessionInfo);
                session.put(API_SESSION_INFO, sessionInfoStr);
            } else {
                success = false;
            }

            if (respond.header("X-AUSERNAME").equals(username)) {
                session.put(Constant.USERNAME, username);
                Connection.Response cruRespond = Jsoup.connect(LINK_CRUCIBLE + "/login").data(Constant.USERNAME, username).data(Constant.PASSWORD, password)
                        .data("rememberme", "yes").method(Connection.Method.POST).timeout(CONNECTION_TIMEOUT).execute();
                Map<String, String> CruCookies = cruRespond.cookies();
                session.put("crucookies", CruCookies.toString());

                if (cruRespond.header("X-AUSERNAME").equals(LOGININFO_INVALID)) {
                    success = false;
                }
            }

            if (respond.header("X-AUSERNAME").equals(LOGININFO_INVALID)) {
                success = false;
            }
        } catch (Exception e) {

            success = false;
        }
        if (!success) {
            session.clear();
        }
        return success;

    }

    public Result login(Session session) {
        if (session != null) {
            if (session.get("cookies") != null) {
                return Results.redirect("/");
            }
        }

        return Results.html();
    }

    public Result loginPost(@Param("username") String username,
                            @Param("password") String password,
                            @Param("rememberMe") Boolean rememberMe, Session session) {

        try {
            if (doLogin(username, password, session)) {
                getUserInformation(session);
                if (rememberMe != null && rememberMe) {
                    session.setExpiryTime(EXPIRE_TIME_30DAYS);
                }
                return redirect("/");
            } else {
                return Results.redirect("/login#incorrectinfo");
            }
        } catch (Exception e) {
            session.clear();
            return redirect("/login#connectionfailed");
        }

    }

    public Result logout(Session session) {
        session.clear();
        return redirect("/");
    }

}

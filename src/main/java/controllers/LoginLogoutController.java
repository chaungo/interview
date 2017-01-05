package controllers;

import com.google.inject.Singleton;
import ninja.Result;
import ninja.Results;
import ninja.params.Param;
import ninja.session.Session;
import org.apache.log4j.Logger;

import java.io.IOException;

import static ninja.Results.internalServerError;
import static ninja.Results.redirect;
import static util.Constant.EXPIRE_TIME_30DAYS;
import static util.MyUtill.doLogin;
import static util.MyUtill.getUserInformation;

@Singleton
public class LoginLogoutController {

    public static final Logger logger = Logger.getLogger(LoginLogoutController.class);

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
        } catch (IOException e) {
            return redirect("/login#connectionfailed");
        } catch (Exception ee) {
            return internalServerError();
        }

    }

    public Result logout(Session session) {
        session.clear();
        return redirect("/");
    }
}

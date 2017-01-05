package filter;

import ninja.*;

public class SecureFilter implements Filter {

    /**
     * If a username is saved we assume the session is valid
     */
    public final String USERNAME = "username";


    @Override
    public Result filter(FilterChain chain, Context context) {
        if (context.getSession() == null || context.getSession().get(USERNAME) == null) {
            return Results.redirect("/login");
        } else {
            return chain.next(context);
        }

    }

}

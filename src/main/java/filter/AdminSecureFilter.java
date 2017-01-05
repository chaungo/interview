package filter;

import ninja.*;

public class AdminSecureFilter implements Filter {

    public final String USERNAME = "username";
    public final String ROLE = "role";


    @Override
    public Result filter(FilterChain chain, Context context) {
        if (context.getSession() == null || context.getSession().get(USERNAME) == null) {
            return Results.redirect("/login");
        } else if (context.getSession().get(ROLE) == null) {
            return Results.redirect("/");
        } else {
            return chain.next(context);
        }

    }

}

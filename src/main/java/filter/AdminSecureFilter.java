package filter;

import ninja.*;
import util.Constant;

public class AdminSecureFilter implements Filter {

    @Override
    public Result filter(FilterChain chain, Context context) {
        if (context.getSession() == null || context.getSession().get(Constant.USERNAME) == null) {
            return Results.redirect("/login");
        } else if (context.getSession().get(Constant.ROLE) == null) {
            return Results.redirect("/");
        } else {
            return chain.next(context);
        }

    }

}

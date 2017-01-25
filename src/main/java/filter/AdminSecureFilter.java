package filter;

import ninja.*;

import static util.MyUtill.isAdmin;

public class AdminSecureFilter implements Filter {

    @Override
    public Result filter(FilterChain chain, Context context) {

        if (isAdmin(context.getSession())) {
            return chain.next(context);
        }

        return Results.redirect("/");
    }

}

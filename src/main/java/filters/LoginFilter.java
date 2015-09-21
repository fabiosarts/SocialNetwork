package filters;

import com.google.inject.Inject;
import com.google.inject.Provider;
import controllers.ApplicationController;
import etc.Constants;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import models.User_session;
import ninja.Context;
import ninja.Filter;
import ninja.FilterChain;
import ninja.Result;
import ninja.Results;
import ninja.jpa.UnitOfWork;
import ninja.session.Session;

public class LoginFilter implements Filter {
    @Inject
    Provider<EntityManager> EntityManagerProvider;

    @Override
    @UnitOfWork
    public Result filter(FilterChain fc, Context context) {
        Session session = context.getSession();
        if(context.getSession() == null || session.get(Constants.CookieSession) == null) {
            return Results.redirect("/login");
        } else {
            EntityManager em = EntityManagerProvider.get();

            Query q = em.createQuery("SELECT x FROM User_session x where id='" + session.get(Constants.CookieSession) + "'");
            List<User_session> uSession = q.getResultList();

            if (uSession.size() == 1) {
                return fc.next(context);
            } else {
                return Results.redirect("/login");
            }
        }
    }

}

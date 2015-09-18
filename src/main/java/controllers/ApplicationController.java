/**
 * Copyright (C) 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package controllers;

import com.google.inject.Inject;
import com.google.inject.Provider;
import ninja.Result;
import ninja.Results;

import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;
import filters.LoginFilter;
import java.util.List;
import java.util.UUID;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import models.User;
import models.User_session;
import ninja.Context;
import ninja.FilterWith;
import ninja.jpa.UnitOfWork;
import ninja.params.Param;
import ninja.session.Session;


@Singleton
public class ApplicationController {
    public static String CookieSession = "sessionid";
    
    @Inject
    Provider<EntityManager> EntityManagerProvider;

    @FilterWith(LoginFilter.class)
    public Result index(Context context) {
        return Results.redirect("/news");
    }
    
    @FilterWith(LoginFilter.class)
    public Result news() {
        Result html = Results.html();
        
        
        
        return html;
    }
    
    @Transactional
    public Result login(@Param("email") String pEmail, @Param("secret") String pPassword, Context context) {
        if(context.getMethod() == "POST")
        {
            EntityManager em = EntityManagerProvider.get();
            Session session = context.getSession();

            Query q = em.createQuery("SELECT x FROM User x where email='" + pEmail + "'");
            List<User> user = (List<User>) q.getResultList();

            if(user.size() == 1) {
                if(pPassword.equals(user.get(0).password)) {
                    User_session uSession = new User_session(UUID.randomUUID().toString(), user.get(0).id);
                    em.persist(uSession);
                    session.put(CookieSession, uSession.id);

                    return Results.redirect("/news");
                } else {
                    return Results.redirect("/");
                }
            }
        } else {
            return Results.html();
        }
        
        return Results.redirect("/");
    }
    
    public Result logout(Context context)
    {
        context.getSession().clear();

        return Results.redirect("/");
    }
    
    @Transactional
    public Result register(@Param("email") String pEmail,
            @Param("secret") String pPassword,
            @Param("fullname") String pFullName,
            Context context) {
        Session session = context.getSession();
        EntityManager em = EntityManagerProvider.get();
        
        User user = new User(pEmail, pPassword, pFullName);
        em.persist(user);
        
        return Results.redirect("/");
    }
}

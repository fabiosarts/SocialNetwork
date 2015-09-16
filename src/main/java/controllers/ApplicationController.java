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
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import models.User;
import ninja.params.Param;


@Singleton
public class ApplicationController {
    @Inject
    Provider<EntityManager> EntityManagerProvider;

    public Result index() {

        return Results.html();

    }
    
    public Result news() {
        Result html = Results.html();
        
        
        
        return html;
    }
    
    @Transactional
    public Result login(@Param("email") String pEmail, @Param("secret") String pPassword) {
        Result html = Results.html();
        EntityManager em = EntityManagerProvider.get();
        
        Query q = em.createQuery("SELECT x FROM User x where email='" + pEmail + "'");
        List<User> user = (List<User>) q.getResultList();
        
        if(user.size() == 1) {
            if(pPassword.equals(user.get(0).password)) {
                html = Results.redirect("/news");
                System.out.println("si");
            } else {
                html = Results.redirect("/");
                System.out.println("no");
            }
        }
        
        return html;
    }
}

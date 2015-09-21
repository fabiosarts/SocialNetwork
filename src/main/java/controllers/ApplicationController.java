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
import dao.RelationshipDao;
import dao.UserDao;
import etc.Constants;
import etc.PostType;
import etc.RelationType;
import filters.LoginFilter;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import models.Comment;
import models.Post;
import models.Relationship;
import models.User;
import models.User_session;
import ninja.Context;
import ninja.FilterWith;
import ninja.jpa.UnitOfWork;
import ninja.params.Param;
import ninja.session.Session;


@Singleton
public class ApplicationController {
    @Inject
    Provider<EntityManager> EntityManagerProvider;
    @Inject UserDao userDao;
    @Inject RelationshipDao relationshipDao;

    @FilterWith(LoginFilter.class)
    public Result index(Context context) {
        // Redirect to /news if filter is OK
        return Results.redirect("/news");
    }
    
    @UnitOfWork
    @FilterWith(LoginFilter.class)
    public Result news(Context context) {
        // Initial declarations
        Result html = Results.html();
        EntityManager em = EntityManagerProvider.get();
        Session session = context.getSession();
        
        String strQuery;
        
        // Temporal vars
        // -------------
        Query q;
        
        User actualUser = userDao.getUser(context);
        
        // Get mutual friend list
        List<User> mutualFriends = relationshipDao.getRelationList(actualUser, RelationType.Friends);
        
        // Get mutual friends post
        strQuery = "SELECT x FROM Post x WHERE user_id IN (";
        for(int i = 0; i < mutualFriends.size(); i++) {
            strQuery += "'" + mutualFriends.get(i).getId() + "', ";
        }
        strQuery += "'" + actualUser.getId()+ "') ORDER BY timestamp DESC";
        // Get all post from user's relatives posts
        q = em.createQuery(strQuery);
        List<Post> posts = (List<Post>) q.getResultList();
        
        // Get all comments from post
        // --------------------------
        // Create a query for comments by post IDs, the result should be something like
        // "SELECT * FROM Comment WHERE post_id IN ('8', '7', '3', '2', '1', '6')"
        //
        
        List<Comment> comments = new ArrayList<Comment>();
        strQuery = "SELECT x FROM Comment x WHERE post_id IN (";
        for(int i = 0; i < posts.size(); i++) {
            strQuery += "'" + posts.get(i).getId().toString() + "'";
            
            if(i != posts.size() - 1) {
               strQuery += ", "; 
            }
        }
        strQuery += ") ORDER BY timestamp DESC";
        q = em.createQuery(strQuery);
        comments = (List<Comment>) q.getResultList();
        
        // HTML Rendering stuff
        html.render("user", actualUser);
        html.render("posts", posts); 
        html.render("comments", comments);
        html.render("friends", mutualFriends);
        
        return html;
    }
    
    @Transactional
    public Result login(@Param("email") String pEmail, @Param("secret") String pPassword, Context context) {
        if(context.getMethod() == "POST")
        {
            User canLogin = userDao.canLogin(pEmail, pPassword);
            
            if (canLogin != null) {
                EntityManager em = EntityManagerProvider.get();
                
                User_session uSession = new User_session(canLogin);
                em.persist(uSession);
                context.getSession().put(Constants.CookieSession, uSession.getId());
                return Results.redirect("/news");
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
    
    @Transactional
    @FilterWith(LoginFilter.class)
    public Result post_create (@Param("content") String content, Context context) {
        Session session = context.getSession();
        EntityManager em = EntityManagerProvider.get();
        
        Query q;
        q = em.createQuery("SELECT x FROM User_session x WHERE id='" + session.get(Constants.CookieSession) + "'");
        List<User_session> uSession = (List<User_session>) q.getResultList();
        
        Post newPost = new Post(uSession.get(0).getUser(), PostType.Status.ordinal(), content, new Timestamp(new Date().getTime()));
        
        em.persist(newPost);
        
        return Results.redirect("/");
    }
    
    @Transactional
    @FilterWith(LoginFilter.class)
    public Result post_comment (@Param("post") String Post, @Param("content") String Content, Context context) {
        Session session = context.getSession();
        EntityManager em = EntityManagerProvider.get();
        
        Query q;
        q = em.createQuery("SELECT x FROM User_session x WHERE id='" + session.get(Constants.CookieSession) + "'");
        List<User_session> uSession = (List<User_session>) q.getResultList();
        
        Comment newComment = new Comment(uSession.get(0).getUser(), Long.valueOf(Post), Content, new Timestamp(new Date().getTime()));
        em.persist(newComment);
        
        return Results.redirect("/");
    }
    
    @FilterWith(LoginFilter.class)
    public Result profile (Context context) {
        // Initial declarations
        Result html = Results.html();
        
        User actualUser = userDao.getUser(context);
        List<User> mutualFriends = relationshipDao.getRelationList(actualUser, RelationType.Friends);
        List<User> friendRequest = relationshipDao.getRelationList(actualUser, RelationType.Request);
        
        html.render("user", actualUser);
        html.render("friends", mutualFriends);
        html.render("requests", friendRequest);
        
        return html;
    }
}

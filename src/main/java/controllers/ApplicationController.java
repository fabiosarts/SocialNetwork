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
import dao.PostDao;
import dao.CommentDao;
import etc.Globals;
import etc.PostType;
import etc.RelationType;
import filters.LoginFilter;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import models.Comment;
import models.Post;
import models.Relationship;
import models.User;
import models.User_session;
import ninja.Context;
import ninja.FilterWith;
import ninja.params.Param;
import ninja.params.PathParam;
import ninja.session.Session;


@Singleton
public class ApplicationController {
    @Inject
    Provider<EntityManager> EntityManagerProvider;
    @Inject UserDao userDao;
    @Inject RelationshipDao relationshipDao;
    @Inject PostDao postDao;
    @Inject CommentDao commentDao;

    @FilterWith(LoginFilter.class)
    public Result index(Context context) {
        // Redirect to "news" because filter is OK
        return Results.redirect(Globals.PathMainPage);
    }
    
    @FilterWith(LoginFilter.class)
    public Result news(Context context) {
        // Initial declarations
        Result html = Results.html();
        EntityManager em = EntityManagerProvider.get();
        Session session = context.getSession();
        
        // Temporal vars
        // -------------
        User actualUser = userDao.getUserFromSession(context);
        
        // Get mutual friend list
        List<User> mutualFriends = relationshipDao.getRelationList(actualUser, RelationType.Friends);
        mutualFriends.add(actualUser);
        // Get mutual friends post
        List<Post> posts = postDao.getPostsFromUsers(mutualFriends);
        //List<Comment> comments 
        List<Comment> comments = commentDao.getCommentsByPosts(posts);
        // HTML Rendering stuff
        html.render("user", actualUser);
        html.render("posts", posts); 
        html.render("comments", comments);
        html.render("friends", mutualFriends);
        
        return html;
    }
    
    @Transactional
    public Result login(@Param("email") String pEmail, @Param("secret") String pPassword, Context context) {
        if("POST".equals(context.getMethod()))
        {
            User canLogin = userDao.canLogin(pEmail, pPassword);
            
            if (canLogin != null) {
                EntityManager em = EntityManagerProvider.get();
                
                User_session uSession = new User_session(canLogin);
                em.persist(uSession);
                context.getSession().put(Globals.CookieSession, uSession.getId());
                return Results.redirect(Globals.PathMainPage);
            }
        } else {
            return Results.html();
        }
        
        return Results.redirect(Globals.PathRoot);
    }
    
    public Result logout(Context context)
    {
        context.getSession().clear();

        return Results.redirect(Globals.PathRoot);
    }
    
    @Transactional
    public Result register(@Param("email") String pEmail,
            @Param("secret") String pPassword,
            @Param("fullname") String pFullName,
            @Param("username") String pUsername,
            Context context) {
        Session session = context.getSession();
        EntityManager em = EntityManagerProvider.get();
        
        User user = new User(pUsername, pEmail, pPassword, pFullName);
        em.persist(user);
        
        return Results.redirect(Globals.PathRoot);
    }
    
    @Transactional
    @FilterWith(LoginFilter.class)
    public Result post_create (@Param("content") String content, Context context) {
        Session session = context.getSession();
        EntityManager em = EntityManagerProvider.get();
        
        User actualUser = userDao.getUserFromSession(context);
        
        Post newPost = new Post(actualUser, PostType.Status.ordinal(), content, new Timestamp(new Date().getTime()));
        
        em.persist(newPost);
        
        return Results.redirect(Globals.PathRoot);
    }
    
    @Transactional
    @FilterWith(LoginFilter.class)
    public Result post_comment (@Param("post") String Post, @Param("content") String Content, @Param("returnto") String returnto, Context context) {
        Session session = context.getSession();
        EntityManager em = EntityManagerProvider.get();
        
        User user = userDao.getUserFromSession(context);
        
        Comment newComment = new Comment(user, Long.valueOf(Post), Content, new Timestamp(new Date().getTime()));
        em.persist(newComment);
        
        if(returnto == null)
            return Results.redirect(Globals.PathRoot);
        else
            return Results.redirect(returnto + "#comment_" + newComment.getId());
    }
    
    @FilterWith(LoginFilter.class)
    public Result profile (Context context) {
        // Initial declarations
        Result html = Results.html();
        
        User actualUser = userDao.getUserFromSession(context);
        List<User> mutualFriends = relationshipDao.getRelationList(actualUser, RelationType.Friends);
        List<Relationship> friendRequest = relationshipDao.getFriendRequests(actualUser);
        
        html.render("user", actualUser);
        html.render("friends", mutualFriends);
        html.render("requests", friendRequest);
        
        return html;
    }
    
    @FilterWith(LoginFilter.class)
    public Result friend_add(@PathParam("username") String pUsername, Context context) {
        User actualUser = userDao.getUserFromSession(context);
        User target = userDao.getUserFromUsername(pUsername);
        Relationship relation = relationshipDao.getRelationByUsername(actualUser, target);
        
        if(relation == null) {
            relationshipDao.createNewRelation(actualUser, target);
            return Results.redirect(Globals.PathProfileView + target.getUsername());
        }
        return Results.redirect(Globals.PathError);
    }
    
    @Transactional
    @FilterWith(LoginFilter.class)
    public Result friend_accept (@PathParam("relid") Long relID, Context context) {
        EntityManager em = EntityManagerProvider.get();
        User user = userDao.getUserFromSession(context);
        
        Query q = em.createQuery("SELECT x FROM Relationship x WHERE id=:relid");
        q.setParameter("relid", relID);
        List<Relationship> relation = (List<Relationship>) q.getResultList();
        
        if(relation.size() == 1) {
            if(Objects.equals(relation.get(0).getUser_b().getId(), user.getId())) {
                relation.get(0).setRelation_type(RelationType.Friends.ordinal());
                em.merge(relation.get(0));
                
                return Results.redirect(Globals.PathProfile);
            }
        } //TODO: Else, mostrar un error
        return Results.redirect(Globals.PathRoot);
    }
    
    @Transactional
    @FilterWith(LoginFilter.class)
    public Result friend_reject (@PathParam("relid") Long relID, Context context) {
        EntityManager em = EntityManagerProvider.get();
        User user = userDao.getUserFromSession(context);
        Relationship relation = relationshipDao.getRelationByID(relID);
        
        if(relation != null) {
            if(Objects.equals(relation.getUser_b().getId(), user.getId())) {
                em.remove(relation);
                
                return Results.redirect(Globals.PathProfile);
            }
        }
        return Results.redirect(Globals.PathRoot);
    }
    
    @Transactional
    @FilterWith(LoginFilter.class)
    public Result profile_set (@Param("content") String status, Context context) {
        EntityManager em = EntityManagerProvider.get();
        User user = userDao.getUserFromSession(context);
        
        user.setStatus(status);
        
        return Results.redirect(Globals.PathProfile);
    }
    
    @FilterWith(LoginFilter.class)
    public Result profile_view(@PathParam("profile") String profile, Context context) {
        // Initial declarations
        Result html = Results.html();

        User actualUser = userDao.getUserFromSession(context);
        User targetUser = userDao.getUserFromUsername(profile);
        List<User> mutualFriends = relationshipDao.getRelationList(actualUser, RelationType.Friends);
        Relationship relationship = relationshipDao.getRelationByUsername(actualUser, targetUser);
        
        boolean disable_add = false;
       
        if(relationship != null) {
            if (relationship.getRelation_type() == RelationType.Friends.ordinal() || relationship.getRelation_type() == RelationType.Request.ordinal()) {
                html.render("relation", relationship);
                disable_add = (relationship.getRelation_type() == RelationType.Request.ordinal()) && Objects.equals(relationship.getUser_a().getId(), actualUser.getId());
                
                if(relationship.getRelation_type() == RelationType.Friends.ordinal()) {
                    // Get mutual friends post
                    List<Post> posts = postDao.getPostsFromUsers(new ArrayList<>(Arrays.asList(targetUser)));
                    //List<Comment> comments 
                    List<Comment> comments = commentDao.getCommentsByPosts(posts);

                    html.render("posts", posts);
                    html.render("comments", comments);
                    
                }
            } else {
                return Results.redirect(Globals.PathError);
            }
        } else {
            html.render("relation", new Relationship(actualUser, targetUser, RelationType.None.ordinal()));
        }
        
        html.render("user", actualUser);
        html.render("target", targetUser);
        html.render("friends", mutualFriends);
        html.render("disable_add", disable_add);

        return html;
    }
}

/**
 * Copyright (C) 2012 the original author or authors.
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

package conf;


import ninja.AssetsController;
import ninja.Router;
import ninja.application.ApplicationRoutes;
import controllers.ApplicationController;

public class Routes implements ApplicationRoutes {

    @Override
    public void init(Router router) {  
        
        router.GET().route("/").with(ApplicationController.class, "index");
        
        router.GET().route("/news").with(ApplicationController.class, "news");
        
        router.GET().route("/login").with(ApplicationController.class, "login");
        router.POST().route("/login").with(ApplicationController.class, "login");
        router.GET().route("/logout").with(ApplicationController.class, "logout");
        
        router.POST().route("/register").with(ApplicationController.class, "register");
        
        router.POST().route("/post/create").with(ApplicationController.class, "post_create");
        router.POST().route("/post/comment").with(ApplicationController.class, "post_comment");
        
        router.GET().route("/profile").with(ApplicationController.class, "profile");
        router.POST().route("/profile/set").with(ApplicationController.class, "profile_set");
        
        router.GET().route("/profile/view/{profile}").with(ApplicationController.class, "profile_view");
        
        router.GET().route("/friends/add/{username}").with(ApplicationController.class, "friend_add");
        router.GET().route("/friends/accept/{relid}").with(ApplicationController.class, "friend_accept");
        router.GET().route("/friends/reject/{relid}").with(ApplicationController.class, "friend_reject");
        //router.GET().route("/friends/block/{relid}").with(ApplicationController.class, "friend_reject");
 
        ///////////////////////////////////////////////////////////////////////
        // Assets (pictures / javascript)
        ///////////////////////////////////////////////////////////////////////    
        router.GET().route("/assets/webjars/{fileName: .*}").with(AssetsController.class, "serveWebJars");
        router.GET().route("/assets/{fileName: .*}").with(AssetsController.class, "serveStatic");
        
        ///////////////////////////////////////////////////////////////////////
        // Index / Catchall shows index page
        ///////////////////////////////////////////////////////////////////////
        router.GET().route("/.*").with(ApplicationController.class, "index");
    }

}


package dao;

import com.google.inject.Inject;
import com.google.inject.Provider;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import models.Post;
import models.User;

public class PostDao {
    @Inject
    Provider<EntityManager> EntityManagerProvider;
    
    public List<Post> getPostsFromUsers(List<User> userlist) {
        EntityManager em = EntityManagerProvider.get();
        List<Post> result = new ArrayList<>();
        
        String strQuery = "SELECT x FROM Post x WHERE user_id IN (";
        for (int i = 0; i < userlist.size(); i++) {
            strQuery += "'" + userlist.get(i).getId() + "'";
            if(i != userlist.size() - 1)
                strQuery += ", ";
        }
        strQuery += ") ORDER BY timestamp DESC";
        // Get all post from user's relatives posts
        Query q = em.createQuery(strQuery);
        result = (List<Post>) q.getResultList();
        
        return result;
    }
}

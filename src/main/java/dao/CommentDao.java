package dao;

import com.google.inject.Inject;
import com.google.inject.Provider;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import models.Comment;
import models.Post;
import models.User;

public class CommentDao {
    @Inject
    Provider<EntityManager> EntityManagerProvider;
    
    public List<Comment> getCommentsByPosts(List<Post> posts) {
        if(posts.size() > 0) {
            EntityManager em = EntityManagerProvider.get();
            // Get all comments from post
            // --------------------------
            // Create a query for comments by post IDs, the result should be something like
            // "SELECT * FROM Comment WHERE post_id IN ('8', '7', '3', '2', '1', '6')"
            //

            List<Comment> comments = new ArrayList<Comment>();
            String strQuery = "SELECT x FROM Comment x WHERE post_id IN (";
            for (int i = 0; i < posts.size(); i++) {
                strQuery += "'" + posts.get(i).getId().toString() + "'";

                if (i != posts.size() - 1) {
                    strQuery += ", ";
                }
            }
            strQuery += ") ORDER BY timestamp DESC";

            Query q = em.createQuery(strQuery);
            comments = (List<Comment>) q.getResultList();

            return comments;
        }
        return null;
    }
}
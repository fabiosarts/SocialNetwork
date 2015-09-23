package dao;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import models.User;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;
import etc.Globals;
import java.util.List;
import models.User_session;
import ninja.Context;

public class UserDao {
    @Inject
    Provider<EntityManager> EntityManagerProvider;
    
    @Transactional
    public User canLogin(String email, String password) {
        if(email != null && password != null) {
            EntityManager em = EntityManagerProvider.get();
            
            Query q = em.createQuery("SELECT x FROM User x WHERE email = :emailParam");
            User user = (User) q.setParameter("emailParam", email).getSingleResult();
            
            if(user != null)
            {
                if(user.getPassword().equals(password)) {
                    return user;
                }
            }
        }
        return null;
    }
    
    @Transactional
    public User getUserFromSession(Context context) {
        EntityManager em = EntityManagerProvider.get();
        
        Query q = em.createQuery("SELECT x FROM User_session x WHERE id='" + context.getSession().get(Globals.CookieSession) + "'");
        User_session uSession = (User_session) q.getSingleResult();
        
        return uSession.getUser();
    }
    
    @Transactional
    public User getUserFromUsername(String username) {
        EntityManager em = EntityManagerProvider.get();
        
        Query q = em.createQuery("SELECT x FROM User x WHERE username = :target");
        q.setParameter("target", username);
        List<User> result = q.getResultList();
        
        if(result.size() == 1) {
            return result.get(0);
        }
        return null;
    }
}

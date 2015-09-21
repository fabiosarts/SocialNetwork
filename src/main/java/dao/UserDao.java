package dao;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import models.User;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;
import etc.Constants;
import models.User_session;
import ninja.Context;

@Transactional
public class UserDao {
    @Inject
    Provider<EntityManager> entityManagerProvider;
    
    @Transactional
    public User canLogin(String email, String password) {
        if(email != null && password != null) {
            EntityManager em = entityManagerProvider.get();
            
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
    public User getUser(Context context) {
        EntityManager em = entityManagerProvider.get();
        
        Query q = em.createQuery("SELECT x FROM User_session x WHERE id='" + context.getSession().get(Constants.CookieSession) + "'");
        User_session uSession = (User_session) q.getSingleResult();
        
        return uSession.getUser();
    }
}

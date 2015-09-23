package dao;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import models.User;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;
import etc.RelationType;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import models.Relationship;
import ninja.jpa.UnitOfWork;

public class RelationshipDao {
    @Inject
    Provider<EntityManager> EntityManagerProvider;
    
    @Transactional
    public List<User> getRelationList(User user, RelationType relType) {
        List<User> result = new ArrayList<User>();
        EntityManager em = EntityManagerProvider.get();
        Query q;
        
        q = em.createQuery("SELECT x FROM Relationship x WHERE user_b = :targetParam or user_a = :targetParam");
        q.setParameter("targetParam", user);
        List<Relationship> relationships = (List<Relationship>) q.getResultList();
                
        for(int i = 0; i < relationships.size(); i++) {
            if(relationships.get(i).getRelation_type() == relType.ordinal()) {
                if(relationships.get(i).getUser_a().getId() == user.getId())
                    result.add(relationships.get(i).getUser_b());
                else
                    result.add(relationships.get(i).getUser_a());
            }
        }
        
        return result;
    }
    
    @Transactional
    public List<Relationship> getFriendRequests(User user) {
        List<Relationship> result = new ArrayList<Relationship>();
        EntityManager em = EntityManagerProvider.get();
        
        Query q = em.createQuery("SELECT x FROM Relationship x WHERE user_b = :user AND relation_type = :reltype");
        q.setParameter("user", user);
        q.setParameter("reltype", RelationType.Request.ordinal());
        result = (List<Relationship>) q.getResultList();
        
        return result;
    }
    
    @Transactional
    public Relationship getRelationByUsername(User user, User target) {
        EntityManager em = EntityManagerProvider.get();
        Query q;
        
        q = em.createQuery("SELECT x FROM Relationship x WHERE (user_a = :actualuser AND user_b = :target) OR (user_a = :target AND user_b = :actualuser)");
        q.setParameter("actualuser", user);
        q.setParameter("target", target);
        List<Relationship> qRelation = q.getResultList();
        
        if(qRelation.size() == 1) {
//            if(Objects.equals(qRelation.get(0).getUser_a().getId(), user.getId())) {
//                return qRelation.get(0);
//            } else {
//                return new Relationship(qRelation.get(0).getUser_b(), qRelation.get(0).getUser_a(), qRelation.get(0).getRelation_type());
//            }
            return qRelation.get(0);
        } else {
            return null;
        }
    }
    
    @Transactional
    public Relationship getRelationByID(Long relID) {
        EntityManager em = EntityManagerProvider.get();
        
        Query q = em.createQuery("SELECT x FROM Relationship x WHERE id = :relid");
        q.setParameter("relid", relID);
        List<Relationship> relation = (List<Relationship>) q.getResultList();
        
        if(relation.size() == 1) {
            return relation.get(0);
        }
        return null; //Doesn't exists
    }
    
    @Transactional
    public Relationship createNewRelation(User user_a, User user_b) {
        EntityManager em = EntityManagerProvider.get();
        Relationship newRelation = new Relationship(user_a, user_b, RelationType.Request.ordinal());
        
        em.persist(newRelation);
        
        return newRelation;
    }
}

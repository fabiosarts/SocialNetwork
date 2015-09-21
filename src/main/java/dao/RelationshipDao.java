package dao;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import models.User;

import com.google.inject.Inject;
import com.google.inject.Provider;
import etc.RelationType;
import java.util.ArrayList;
import java.util.List;
import models.Relationship;
import ninja.jpa.UnitOfWork;

@UnitOfWork
public class RelationshipDao {
    @Inject
    Provider<EntityManager> entityManagerProvider;
    
    public List<User> getRelationList(User user, RelationType relType) {
        List<User> result = new ArrayList<User>();
        EntityManager em = entityManagerProvider.get();
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
}

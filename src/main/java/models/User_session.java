package models;

import java.sql.Timestamp;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class User_session {
    @Id
    public String id;
    public long user_id;
    //public Timestamp last_join;

    public User_session(String id, long user_id) {
        this.id = id;
        this.user_id = user_id;
    }

    public User_session() {}
}

package models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;
    public String email;
    public String password;
    public String full_name;
    
    public User() {}

    public User(String email, String password, String full_name) {
        this.email = email;
        this.password = password;
        this.full_name = full_name;
    }
}

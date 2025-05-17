package com.spidrox;
import jakarta.persistence.*;
import java.util.List;

@Entity
public class User {
	@Id @GeneratedValue
    public Long id;

    public String username;

    @OneToMany(mappedBy = "user")
    public List<Task> tasks;
    
}

package com.spidrox;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Task {
    @Id @GeneratedValue
    public Long id;

    public String title;
    public String description;
    public LocalDate dueDate;
    public String priority;
    public String status;

    @ManyToOne
    public User user;
}

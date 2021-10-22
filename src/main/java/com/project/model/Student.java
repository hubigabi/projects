package com.project.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.Set;

@Data
@ToString(callSuper = true)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = {"projects"})
@Entity
@Table(name = "students", indexes = {@Index(name = "idx_index_number", columnList = "index_number")})
public class Student extends User {
    @Column(name = "index_number", nullable = false, length = 20, unique = true)
    private String indexNumber;

    @Column(name = "full_time", nullable = false)
    private Boolean fullTime = Boolean.TRUE;

    @ManyToMany(mappedBy = "students", fetch = FetchType.EAGER)
    private Set<Project> projects;

    public Student(Long ID, String username, String password, String lastName, String firstName, String email, String indexNumber, Boolean fullTime) {
        super(ID, username, password, UserRole.STUDENT, lastName, firstName, email);
        this.indexNumber = indexNumber;
        this.fullTime = fullTime;
    }

    public Student(String username, String password, String lastName, String firstName, String email, String indexNumber, Boolean fullTime) {
        super(username, password, UserRole.STUDENT, lastName, firstName, email);
        this.indexNumber = indexNumber;
        this.fullTime = fullTime;
    }
}



package com.project.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import javax.persistence.*;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"projects"})
@Entity
@Table(name = "students", indexes = {@Index(name = "idx_index_number", columnList = "index_number")})
public class Student extends RepresentationModel<Student> {
    @Id @GeneratedValue private Long ID;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "index_number", nullable = false, length = 20, unique = true)
    private String indexNumber;

    @Column(name = "full_time", nullable = false)
    private Boolean fullTime = Boolean.TRUE;

    @ManyToMany(mappedBy = "students", fetch = FetchType.EAGER)
    private Set<Project> projects;
}



package com.project.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.hateoas.RepresentationModel;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = {"students"})
@Entity
@Table(name = "projects", indexes = {@Index(name = "idx_project_name", columnList = "name")})
public class Project extends RepresentationModel<Project> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ID;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(length = 1000)
    private String description;

    @CreationTimestamp
    @Column(name = "creation_date_time", nullable = false, updatable = false)
    private LocalDateTime creationDateTime;

    @Column(name = "delivery_date")
    private LocalDate deliveryDate;

    @OneToMany(mappedBy = "project", fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"project"})
    private List<Task> tasks;

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Student> students;

    public Project(String name, String description, LocalDate deliveryDate) {
        this.name = name;
        this.description = description;
        this.deliveryDate = deliveryDate;
    }
}



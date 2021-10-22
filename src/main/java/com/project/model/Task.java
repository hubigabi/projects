package com.project.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.hateoas.RepresentationModel;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tasks", indexes = {@Index(name = "idx_task_name", columnList = "name")})
public class Task extends RepresentationModel<Task> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ID;

    @Column(nullable = false, length = 50)
    private String name;

    @Lob
    private String description;

    @Enumerated
    @Column(name = "task_status", nullable = false)
    private TaskStatus taskStatus = TaskStatus.TO_DO;

    @CreationTimestamp
    @Column(name = "addition_date_time", nullable = false, updatable = false)
    private LocalDateTime additionDateTime;

    @ManyToOne
    @JoinColumn(nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Project project;

    public Task(String name, String description, TaskStatus taskStatus) {
        this.name = name;
        this.description = description;
        this.taskStatus = taskStatus;
    }
}

package com.project.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "attachments")
public class Attachment {
    @Id @GeneratedValue private Long ID;

    @ManyToOne
    @JoinColumn(nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Project project;

    @Column(name = "name", nullable = false)
    private String name;

    @Lob
    private String description;

    @Column(name = "path", nullable = false)
    private String path;

    @CreationTimestamp
    @Column(name = "addition_date_time", nullable = false, updatable = false)
    private LocalDateTime additionDateTime;

    public Attachment(Project project, String name, String description, String path) {
        this.project = project;
        this.name = name;
        this.description = description;
        this.path = path;
    }
}

package com.project.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.hateoas.RepresentationModel;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "messages")
public class ChatMessage extends RepresentationModel<ChatMessage> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ID;

    @NotNull
    @Size(min = 1, max = 1000)
    private String message;

    @NotNull
    private LocalDateTime localDateTime;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "project_id", nullable = false)
    @JsonIgnoreProperties({"tasks", "students", "description", "name", "creationDateTime", "deliveryDate", "links"})
    private Project project;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({
            "username", "password", "email", "enabled", "id", "authorities", "accountNonLocked",
            "credentialsNonExpired", "accountNonExpired", "links", "projects"
    })
    private User user;

    public ChatMessage(@NotNull @Size(min = 1, max = 1000) String message, @NotNull LocalDateTime localDateTime) {
        this.message = message;
        this.localDateTime = localDateTime;
    }
}

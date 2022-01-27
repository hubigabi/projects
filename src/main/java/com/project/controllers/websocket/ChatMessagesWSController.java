package com.project.controllers.websocket;

import com.project.model.ChatMessage;
import com.project.model.Project;
import com.project.services.ChatMessagesService;
import com.project.services.ProjectsService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.KeycloakPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@CrossOrigin
@Slf4j(topic = "Chat")
public class ChatMessagesWSController {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class IntermediateMessage {
        private String message;
        private LocalDateTime localDateTime;
        private Long projectId;
    }

    private final ChatMessagesService chatMessagesService;
    private final ProjectsService projectsService;

    @Autowired
    public ChatMessagesWSController(ChatMessagesService chatMessagesService, ProjectsService projectsService) {
        this.chatMessagesService = chatMessagesService;
        this.projectsService = projectsService;
    }

    @MessageMapping("/createMessage/{project_id}")
    @SendTo("/message/{project_id}")
    public ChatMessage create(@DestinationVariable String project_id, IntermediateMessage intermediateMessage, Authentication authentication) {
        log.info(String.format("New message: %s", intermediateMessage));

        Object principal = authentication.getPrincipal();
        String firstName = ((KeycloakPrincipal) principal).getKeycloakSecurityContext().getToken().getGivenName();
        String lastName = ((KeycloakPrincipal) principal).getKeycloakSecurityContext().getToken().getFamilyName();
        Project project = projectsService.findById(intermediateMessage.projectId).orElse(null);

        if (project == null) {
            log.warn("Unknown project!");
            return null;
        }

        ChatMessage chatMessage = new ChatMessage(
                0L,
                intermediateMessage.message,
                intermediateMessage.localDateTime,
                project,
                String.format("%s %s", firstName, lastName)
        );
        return chatMessagesService.create(chatMessage);
    }
}

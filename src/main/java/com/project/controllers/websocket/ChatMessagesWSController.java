package com.project.controllers.websocket;

import com.project.model.ChatMessage;
import com.project.model.Project;
import com.project.model.User;
import com.project.services.ChatMessagesService;
import com.project.services.ProjectsService;
import com.project.services.UsersService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final UsersService usersService;
    private final ProjectsService projectsService;

    @Autowired
    public ChatMessagesWSController(ChatMessagesService chatMessagesService, UsersService usersService,
                                    ProjectsService projectsService) {
        this.chatMessagesService = chatMessagesService;
        this.usersService = usersService;
        this.projectsService = projectsService;
    }

    @MessageMapping("/createMessage/{project_id}")
    @SendTo("/message/{project_id}")
    public ChatMessage create(@DestinationVariable String project_id, IntermediateMessage intermediateMessage, Authentication authentication) {
        log.info(String.format("New message: %s", intermediateMessage));

        User user = (User) authentication.getPrincipal();
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
                user
        );
        return chatMessagesService.create(chatMessage);
    }
}

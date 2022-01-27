package com.project.controllers.api;

import com.project.model.ChatMessage;
import com.project.services.ChatMessagesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequestMapping("/api/chat-messages")
@CrossOrigin
public class ChatMessagesController {

    private final ChatMessagesService chatMessagesService;

    @Autowired
    public ChatMessagesController(ChatMessagesService chatMessagesService) {
        this.chatMessagesService = chatMessagesService;
    }

    @GetMapping()
    public ResponseEntity<CollectionModel<ChatMessage>> findAllChatMessages() {
        List<ChatMessage> allChatMessages = chatMessagesService.findAll();
        allChatMessages.forEach(chatMessage -> chatMessage.addIf(!chatMessage.hasLinks(),
                () -> getLinkToChatMessage(chatMessage)));
        Link link = linkTo(ChatMessagesController.class).withSelfRel();
        return ResponseEntity.ok(CollectionModel.of(allChatMessages, link));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChatMessage> findById(@PathVariable("id") long id) {
        Optional<ChatMessage> optionalChatMessage = chatMessagesService.findById(id);
        return optionalChatMessage.map(chatMessage -> {
            chatMessage.add(getLinkToChatMessage(chatMessage));
            return new ResponseEntity<>(chatMessage, HttpStatus.OK);
        }).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/allByProject/{projectID}")
    public ResponseEntity<CollectionModel<ChatMessage>> findAllByProjectID(@PathVariable("projectID") long projectID) {
        List<ChatMessage> allByProjectID = chatMessagesService.findAllByProjectID(projectID);
        allByProjectID.forEach(chatMessage -> chatMessage.addIf(!chatMessage.hasLinks(),
                () -> getLinkToChatMessage(chatMessage)));
        Link link = linkTo(ChatMessagesController.class).withSelfRel();
        return ResponseEntity.ok(CollectionModel.of(allByProjectID, link));
    }

    private Link getLinkToChatMessage(ChatMessage chatMessage) {
        addLinkToProject(chatMessage);
        return linkTo(ChatMessagesController.class).slash(chatMessage.getID()).withSelfRel();
    }

    private void addLinkToProject(ChatMessage chatMessage) {
        if (chatMessage.getProject() != null) {
            chatMessage.getProject().addIf(!chatMessage.getProject().hasLinks(),
                    () -> linkTo(ProjectsController.class).slash(chatMessage.getProject().getID()).withSelfRel());
        }
    }
}

package com.project.controllers.api;

import com.project.model.ChatMessage;
import com.project.model.Project;
import com.project.services.ChatMessagesService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ChatMessagesControllerTest {

    @Mock
    private ChatMessagesService chatMessagesService;

    @InjectMocks
    private ChatMessagesController chatMessagesController;

    @Test
    void findAllChatMessagesShouldReturnChatMessages() {
        ChatMessage chatMessage1 = new ChatMessage("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.", LocalDateTime.now().minusDays(2));
        ChatMessage chatMessage2 = new ChatMessage("Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.", LocalDateTime.now().minusHours(7));
        List<ChatMessage> chatMessages = Arrays.asList(chatMessage1, chatMessage2);
        when(chatMessagesService.findAll()).thenReturn(chatMessages);

        ResponseEntity<CollectionModel<ChatMessage>> responseEntity = chatMessagesController.findAllChatMessages();

        assertNotNull(responseEntity.getBody());
        Collection<ChatMessage> chatMessageCollection = responseEntity.getBody().getContent();
        assertNotNull(chatMessageCollection);
        assertThat(chatMessageCollection, hasSize(2));
        assertAll(() -> assertTrue(chatMessageCollection.contains(chatMessages.get(0))),
                () -> assertTrue(chatMessageCollection.contains(chatMessages.get(1))));
    }

    @Test
    void findByIdShouldReturnChatMessage() {
        long chatMessageID = 1L;
        ChatMessage chatMessage = new ChatMessage("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.", LocalDateTime.now().minusDays(2));
        chatMessage.setID(chatMessageID);
        when(chatMessagesService.findById(chatMessageID)).thenReturn(Optional.of(chatMessage));

        ResponseEntity<ChatMessage> responseEntity = chatMessagesController.findById(chatMessage.getID());

        assertAll(() -> assertThat(responseEntity.getStatusCodeValue(), is(HttpStatus.OK.value())),
                () -> assertThat(responseEntity.getBody(), is(chatMessage)));
    }

    @Test
    void findByIdShouldReturnNotFound() {
        long chatMessageID = 1L;
        when(chatMessagesService.findById(chatMessageID)).thenReturn(Optional.empty());

        ResponseEntity<ChatMessage> responseEntity = chatMessagesController.findById(chatMessageID);

        assertThat(responseEntity.getStatusCodeValue()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void findAllByProjectIDShouldReturnChatMessages() {
        Long projectID = 1L;
        Project project = new Project("Project 1", "Description for project 1", LocalDate.now().plusDays(15));
        project.setID(projectID);
        ChatMessage chatMessage1 = new ChatMessage("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.", LocalDateTime.now().minusDays(2));
        chatMessage1.setProject(project);
        ChatMessage chatMessage2 = new ChatMessage("Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.", LocalDateTime.now().minusHours(7));
        chatMessage2.setProject(project);
        List<ChatMessage> chatMessages = Arrays.asList(chatMessage1, chatMessage2);
        when(chatMessagesService.findAllByProjectID(projectID)).thenReturn(chatMessages);

        ResponseEntity<CollectionModel<ChatMessage>> responseEntity = chatMessagesController.findAllByProjectID(project.getID());

        assertNotNull(responseEntity.getBody());
        Collection<ChatMessage> chatMessageCollection = responseEntity.getBody().getContent();
        assertNotNull(chatMessageCollection);
        assertAll(() -> assertThat(chatMessageCollection, hasSize(2)),
                () -> assertThat(chatMessageCollection).hasSameElementsAs(chatMessages));
    }
}

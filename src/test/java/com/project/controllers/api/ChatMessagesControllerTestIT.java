package com.project.controllers.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.model.ChatMessage;
import com.project.model.Project;
import com.project.services.ChatMessagesService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(ChatMessagesController.class)
class ChatMessagesControllerTestIT {

    private static final String CHAT_MESSAGE_API_URL = "/api/chat-messages";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ChatMessagesService chatMessagesService;

    @Test
    void findAllChatMessagesShouldReturnChatMessages() throws Exception {
        ChatMessage chatMessage1 = new ChatMessage("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.", LocalDateTime.now().minusDays(2));
        ChatMessage chatMessage2 = new ChatMessage("Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.", LocalDateTime.now().minusHours(7));
        List<ChatMessage> chatMessages = Arrays.asList(chatMessage1, chatMessage2);

        Mockito.when(chatMessagesService.findAll()).thenReturn(chatMessages);

        mockMvc.perform(get(CHAT_MESSAGE_API_URL)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.chatMessageList", hasSize(chatMessages.size())));
    }

    @Test
    void findByIdShouldReturnChatMessage() throws Exception {
        long chatMessageID = 1L;
        ChatMessage chatMessage = new ChatMessage("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.", LocalDateTime.now().minusDays(2));
        chatMessage.setID(chatMessageID);

        Mockito.when(chatMessagesService.findById(chatMessageID)).thenReturn(Optional.of(chatMessage));

        MvcResult mvcResult = mockMvc.perform(get(CHAT_MESSAGE_API_URL + "/{id}", chatMessage.getID()))
                .andExpect(status().isOk()).andReturn();
        ChatMessage response = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ChatMessage.class);

        Assertions.assertEquals(chatMessage, response);
    }

    @Test
    void findByIdShouldReturnNotFound() throws Exception {
        long chatMessageID = 1L;
        Mockito.when(chatMessagesService.findById(chatMessageID)).thenReturn(Optional.empty());

        mockMvc.perform(get(CHAT_MESSAGE_API_URL + "/{id}", chatMessageID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    void findAllByProjectIDShouldReturnChatMessages() throws Exception {
        Long projectID = 1L;
        Project project = new Project("Project 1", "Description for project 1", LocalDate.now().plusDays(15));
        project.setID(projectID);

        ChatMessage chatMessage1 = new ChatMessage("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.", LocalDateTime.now().minusDays(2));
        chatMessage1.setProject(project);
        ChatMessage chatMessage2 = new ChatMessage("Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.", LocalDateTime.now().minusHours(7));
        chatMessage2.setProject(project);
        List<ChatMessage> chatMessages = Arrays.asList(chatMessage1, chatMessage2);

        Mockito.when(chatMessagesService.findAllByProjectID(projectID)).thenReturn(chatMessages);

        mockMvc.perform(get(CHAT_MESSAGE_API_URL + "/allByProject/{projectID}", projectID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.chatMessageList", hasSize(chatMessages.size())));
    }
}

package com.project.services;

import com.project.model.ChatMessage;
import com.project.repositories.ChatMessagesRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j(topic = "Chat-messages service")
public class ChatMessagesService {

    private final ChatMessagesRepository chatMessagesRepository;

    @Autowired
    public ChatMessagesService(ChatMessagesRepository chatMessagesRepository) {
        this.chatMessagesRepository = chatMessagesRepository;
    }

    public Optional<ChatMessage> findById(Long ID) {
        return chatMessagesRepository.findById(ID);
    }

    public List<ChatMessage> findAll() {
        return chatMessagesRepository.findAll();
    }

    public List<ChatMessage> findAllByProjectID(Long projectID) {
        return chatMessagesRepository.findAllByProject_IDOrderByLocalDateTimeDesc(projectID);
    }

    public List<ChatMessage> findAllByUserID(Long userID) {
        return chatMessagesRepository.findAllByUser_IDOrderByLocalDateTimeDesc(userID);
    }

    public ChatMessage create(ChatMessage chatMessage) {
        return chatMessagesRepository.save(chatMessage);
    }

    public ChatMessage update(ChatMessage chatMessage) {
        return chatMessagesRepository.save(chatMessage);
    }

    public boolean existsById(Long ID) {
        return chatMessagesRepository.existsById(ID);
    }

    public long count() {
        return chatMessagesRepository.count();
    }

    public void delete(ChatMessage chatMessage) {
        chatMessagesRepository.delete(chatMessage);
    }

    public void deleteById(Long id) {
        if (existsById(id)) {
            chatMessagesRepository.deleteById(id);
        }
    }
}

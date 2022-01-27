package com.project.repositories;

import com.project.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessagesRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findAllByProject_IDOrderByLocalDateTimeDesc(Long id);
}

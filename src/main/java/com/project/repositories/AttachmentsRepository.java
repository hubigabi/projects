package com.project.repositories;

import com.project.model.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttachmentsRepository extends JpaRepository<Attachment, Long> {
    List<Attachment> findAllByProjectID(Long projectID);
}

package com.project.services;

import com.project.model.Attachment;
import com.project.model.Project;
import com.project.repositories.AttachmentsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

@Service
public class AttachmentsService {
    private static final String BASE_PATH = "./attachments";
    private final AttachmentsRepository attachmentsRepository;

    @Autowired public AttachmentsService(AttachmentsRepository attachmentsRepository) {
        this.attachmentsRepository = attachmentsRepository;
    }

    public Attachment storeAndSave(Project project, MultipartFile multipartFile, String description) {
        String fileName = multipartFile.getOriginalFilename();
        String newFilePath = BASE_PATH + "/" + project.getID();

        try {
            Path newPath = new File(newFilePath).toPath();
            if (!Files.exists(newPath)) {
                Files.createDirectories(newPath);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        try {
            String newFullFilePath = newFilePath + "/" + fileName;
            multipartFile.transferTo(new File(newFullFilePath));
            Attachment attachment = new Attachment(project, fileName, description, newFullFilePath);
            return attachmentsRepository.save(attachment);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void delete(Attachment attachment) {
        try {
            Files.delete(new File(attachment.getPath()).toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        attachmentsRepository.delete(attachment);
    }

    public List<Attachment> findAllByProjectID(Long projectID) {
        return attachmentsRepository.findAllByProjectID(projectID);
    }

    public Optional<Attachment> findById(Long id) {
        return attachmentsRepository.findById(id);
    }
}

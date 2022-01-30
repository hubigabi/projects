package com.project.controllers.templates;

import com.project.model.Attachment;
import com.project.model.Project;
import com.project.services.AttachmentsService;
import com.project.services.ProjectsService;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

@Controller
@RequestMapping("/projects/files")
public class ProjectFilesFrontendController {
    private final ProjectsService projectsService;
    private final AttachmentsService attachmentsService;

    @Autowired public ProjectFilesFrontendController(ProjectsService projectsService, AttachmentsService attachmentsService) {
        this.projectsService = projectsService;
        this.attachmentsService = attachmentsService;
    }

    @GetMapping("")
    public String getList(@RequestParam Long projectId, Model model) {
        if (!projectsService.existsById(projectId)) return "redirect:/projects";

        model.addAttribute("project", projectsService.findById(projectId).get());
        model.addAttribute("attachments", attachmentsService.findAllByProjectID(projectId));

        return "projects/files/list";
    }

    @PostMapping("/upload")
    public String postUpload(@RequestParam Long projectId, @RequestParam MultipartFile file, @RequestParam String description) {
        if (!projectsService.existsById(projectId)) return "redirect:/projects";

        Project project = projectsService.findById(projectId).get();
        attachmentsService.storeAndSave(project, file, description);

        return String.format("redirect:/projects/files?projectId=%d", projectId);
    }

    @GetMapping("/delete")
    public String getDelete(@RequestParam Long projectId, @RequestParam Long attachmentId) {
        if (!projectsService.existsById(projectId)) return "redirect:/projects";

        Optional<Attachment> possibleAttachment = attachmentsService.findById(attachmentId);
        if (possibleAttachment.isPresent()) {
            Attachment attachment = possibleAttachment.get();
            attachmentsService.delete(attachment);
        }

        return String.format("redirect:/projects/files?projectId=%d", projectId);
    }

    @GetMapping("/download")
    public @ResponseBody ResponseEntity<FileSystemResource> getDownload(@RequestParam Long attachmentId) {
        Optional<Attachment> possibleAttachment = attachmentsService.findById(attachmentId);
        if (possibleAttachment.isPresent()) {
            Attachment attachment = possibleAttachment.get();
            FileSystemResource fsRes = new FileSystemResource(attachment.getPath());

            String mimeType = "application/octet-stream";
            try {
                Tika tika = new Tika();
                mimeType = tika.detect(new File(attachment.getPath()));
            } catch (IOException e) {
                e.printStackTrace();
            }

            return ResponseEntity
                    .ok()
                    .header("Content-Disposition", "attachment; filename=\"" + attachment.getName() + "\"")
                    .header("Content-Type", mimeType)
                    .body(fsRes);
        }
        else throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Nie znaleziono żądanego załącznika");
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public String handleMissingServletRequestParameterException() {
        return "redirect:/projects";
    }
}

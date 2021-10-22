package com.project.controllers.templates;

import com.project.model.Project;
import com.project.services.ProjectsService;
import com.project.utils.AllowOnlyLecturer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
@RequestMapping("/projects")
public class ProjectsFrontendController {
    private ProjectsService projectsService;

    @Autowired public ProjectsFrontendController(ProjectsService projectsService) {
        this.projectsService = projectsService;
    }

    @GetMapping("")
    public String getList(Model model) {
        model.addAttribute("projects", projectsService.findAll());
        return "projects/list";
    }

    @PostMapping("")
    @AllowOnlyLecturer
    public String postList(@RequestParam String projectTitle, @RequestParam String projectDescription,
                           @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate projectDeliveryDate) {
        projectsService.create(new Project(
                projectTitle,
                projectDescription,
                projectDeliveryDate
        ));
        return "redirect:/projects";
    }

    @GetMapping("/edit")
    @AllowOnlyLecturer
    public String getEdit(@RequestParam Long projectId, Model model) {
        if (!projectsService.existsById(projectId)) return "redirect:/projects";

        model.addAttribute("project", projectsService.findById(projectId).get());
        return "projects/edit";
    }

    @PostMapping("/edit")
    @AllowOnlyLecturer
    public String postEdit(@RequestParam Long projectId, @RequestParam String projectTitle, @RequestParam String projectDescription,
                           @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate projectDeliveryDate) {
        Project project = projectsService.findById(projectId).get();
        project.setName(projectTitle);
        project.setDescription(projectDescription);
        project.setDeliveryDate(projectDeliveryDate);
        projectsService.update(project);

        return "redirect:/projects";
    }

    @GetMapping("/delete")
    @AllowOnlyLecturer
    public String getDelete(@RequestParam Long projectId, Model model) {
        if (!projectsService.existsById(projectId)) return "redirect:/projects";

        model.addAttribute("project", projectsService.findById(projectId).get());
        return "projects/delete";
    }

    @PostMapping("/delete")
    @AllowOnlyLecturer
    public String postDelete(@RequestParam Long projectId) {
        projectsService.deleteById(projectId);
        return "redirect:/projects";
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public String handleMissingServletRequestParameterException() {
        return "redirect:/projects";
    }
}

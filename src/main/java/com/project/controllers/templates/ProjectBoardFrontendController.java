package com.project.controllers.templates;

import com.project.model.Project;
import com.project.services.ProjectsService;
import com.project.services.TasksService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/projects/board")
public class ProjectBoardFrontendController {

    private final ProjectsService projectsService;
    private final TasksService tasksService;

    @Autowired
    public ProjectBoardFrontendController(ProjectsService projectsService, TasksService tasksService) {
        this.projectsService = projectsService;
        this.tasksService = tasksService;
    }

    @GetMapping("")
    public String getList(@RequestParam Long projectId, Model model) {
        Optional<Project> project = projectsService.findById(projectId);

        if (project.isEmpty()) return "redirect:/projects";

        model.addAttribute("project", project.get());
        model.addAttribute("tasks", tasksService.findAllByProjectID(projectId));

        return "projects/board/board";
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public String handleMissingServletRequestParameterException() {
        return "redirect:/projects";
    }
}

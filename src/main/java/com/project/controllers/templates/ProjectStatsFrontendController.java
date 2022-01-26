package com.project.controllers.templates;

import com.project.model.Project;
import com.project.services.ProjectStatsService;
import com.project.services.ProjectsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
@RequestMapping("/projects/stats")
public class ProjectStatsFrontendController {

    private final ProjectsService projectsService;
    private final ProjectStatsService projectStatsService;

    @Autowired
    public ProjectStatsFrontendController(ProjectsService projectsService,
                                          ProjectStatsService projectStatsService) {
        this.projectsService = projectsService;
        this.projectStatsService = projectStatsService;
    }

    @GetMapping("")
    public String getProjectStats(@RequestParam Long projectId, Model model) {
        Optional<Project> project = projectsService.findById(projectId);

        if (project.isEmpty()) return "redirect:/projects";

        model.addAttribute("project", project.get());
        model.addAttribute("stats", projectStatsService.getProjectStats(projectId));

        return "projects/stats/stats";
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public String handleMissingServletRequestParameterException() {
        return "redirect:/projects";
    }

}

package com.project.controllers.templates;

import com.project.model.Project;
import com.project.model.Task;
import com.project.model.TaskStatus;
import com.project.services.ProjectsService;
import com.project.services.TasksService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/projects/tasks")
public class ProjectTasksFrontendController {
    private ProjectsService projectsService;
    private TasksService tasksService;

    @Autowired public ProjectTasksFrontendController(ProjectsService projectsService, TasksService tasksService) {
        this.projectsService = projectsService;
        this.tasksService = tasksService;
    }

    @GetMapping("")
    public String getList(@RequestParam Long projectId, Model model) {
        if (!projectsService.existsById(projectId)) return "redirect:/projects";

        model.addAttribute("project", projectsService.findById(projectId).get());
        model.addAttribute("tasks", tasksService.findAllByProjectID(projectId));

        return "projects/tasks/list";
    }

    @PostMapping("")
    public String postList(@RequestParam Long projectId, @RequestParam String taskTitle,
                           @RequestParam String taskDescription, @RequestParam TaskStatus taskStatus) {
        if (!projectsService.existsById(projectId)) return "redirect:/projects";

        Project project = projectsService.findById(projectId).get();
        Task task = new Task(
                taskTitle,
                taskDescription,
                taskStatus
        );
        task.setProject(project);
        tasksService.create(task);

        return String.format("redirect:/projects/tasks?projectId=%d", projectId);
    }

    @GetMapping("/edit")
    public String getEdit(@RequestParam Long projectId, @RequestParam Long taskId, Model model) {
        if (!projectsService.existsById(projectId)) return "redirect:/projects";
        else if (!tasksService.existsById(taskId)) return String.format("redirect:/projects/tasks?taskId=%d", projectId);

        model.addAttribute("project", projectsService.findById(projectId).get());
        model.addAttribute("task", tasksService.findById(taskId).get());

        return "projects/tasks/edit";
    }

    @PostMapping("/edit")
    public String postEdit(@RequestParam Long projectId, @RequestParam Long taskId, @RequestParam String taskTitle,
                           @RequestParam String taskDescription, @RequestParam TaskStatus taskStatus) {
        if (!projectsService.existsById(projectId)) return "redirect:/projects";
        else if (!tasksService.existsById(taskId)) return String.format("redirect:/projects/tasks?taskId=%d", projectId);

        Task task = tasksService.findById(taskId).get();
        task.setName(taskTitle);
        task.setDescription(taskDescription);
        task.setTaskStatus(taskStatus);
        tasksService.update(task);

        return String.format("redirect:/projects/tasks?projectId=%d", projectId);
    }

    @GetMapping("/delete")
    public String getDelete(@RequestParam Long projectId, @RequestParam Long taskId, Model model) {
        if (!projectsService.existsById(projectId)) return "redirect:/projects";
        else if (!tasksService.existsById(taskId)) return String.format("redirect:/projects/tasks?taskId=%d", projectId);

        model.addAttribute("project", projectsService.findById(projectId).get());
        model.addAttribute("task", tasksService.findById(taskId).get());

        return "projects/tasks/delete";
    }

    @PostMapping("/delete")
    public String postDelete(@RequestParam Long projectId, @RequestParam Long taskId) {
        if (!projectsService.existsById(projectId)) return "redirect:/projects";
        else if (!tasksService.existsById(taskId)) return String.format("redirect:/projects/tasks?taskId=%d", projectId);

        tasksService.deleteById(taskId);

        return String.format("redirect:/projects/tasks?projectId=%d", projectId);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public String handleMissingServletRequestParameterException() {
        return "redirect:/projects";
    }
}

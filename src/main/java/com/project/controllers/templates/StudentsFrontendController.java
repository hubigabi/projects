package com.project.controllers.templates;

import com.project.model.Project;
import com.project.model.Student;
import com.project.services.ProjectsService;
import com.project.services.StudentsService;
import com.project.utils.AllowOnlyLecturer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/students")
@RolesAllowed("lecturers")
public class StudentsFrontendController {
    private StudentsService studentsService;
    private ProjectsService projectsService;

    @Autowired public StudentsFrontendController(StudentsService studentsService, ProjectsService projectsService) {
        this.studentsService = studentsService;
        this.projectsService = projectsService;
    }

    @GetMapping("")
    public String getList(Model model) {
        model.addAttribute("students", studentsService.findAll());
        return "students/list";
    }

    @GetMapping("/projects")
    public String getProjects(@RequestParam Long studentId, Model model) {
        if (!studentsService.existsById(studentId)) return "redirect:/students";

        List<Project> studentsProjects = projectsService.findAllByStudents_ID(studentId);
        List<Project> remainingProjects = projectsService.findAll().stream()
                .filter(project -> !studentsProjects.contains(project))
                .collect(Collectors.toList());

        model.addAttribute("student", studentsService.findById(studentId).get());
        model.addAttribute("projects", studentsProjects);
        model.addAttribute("remainingProjects", remainingProjects);
        return "students/projects";
    }

    @PostMapping("/projects")
    @AllowOnlyLecturer
    public String postProjects(@RequestParam String action, @RequestParam Long studentId, @RequestParam Long projectId) {
        if (!studentsService.existsById(studentId)) return "redirect:/students";
        if (!projectsService.existsById(projectId))
            return String.format("redirect:/students/projects?studentId=%d", studentId);

        Project project = projectsService.findById(projectId).get();
        switch (action) {
            case "remove":
                project.getStudents().removeIf(s -> s.getID() == studentId);
                break;
            case "add":
                project.getStudents().add(studentsService.findById(studentId).get());
                break;
        }
        projectsService.update(project);

        return String.format("redirect:/students/projects?studentId=%d", studentId);
    }

    @GetMapping("/edit")
    @AllowOnlyLecturer
    public String getEdit(@RequestParam Long studentId, Model model) {
        if (!studentsService.existsById(studentId)) return "redirect:/students";

        model.addAttribute("student", studentsService.findById(studentId).get());
        return "students/edit";
    }

    @PostMapping("/edit")
    @AllowOnlyLecturer
    public String postEdit(@RequestParam Long studentId, @RequestParam String lastName, @RequestParam String firstName,
                           @RequestParam String indexNumber, @RequestParam Boolean fullTime) {
        Student student = studentsService.findById(studentId).get();
        student.setLastName(lastName);
        student.setFirstName(firstName);
        student.setIndexNumber(indexNumber);
        student.setFullTime(fullTime);

        studentsService.update(student);

        return "redirect:/students";
    }

    @GetMapping("/delete")
    @AllowOnlyLecturer
    public String getDelete(@RequestParam Long studentId, Model model) {
        if (!studentsService.existsById(studentId)) return "redirect:/students";

        model.addAttribute("student", studentsService.findById(studentId).get());
        return "students/delete";
    }

    @PostMapping("/delete")
    @AllowOnlyLecturer
    public String postDelete(@RequestParam Long studentId) {
        studentsService.deleteById(studentId);
        return "redirect:/students";
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public String handleMissingServletRequestParameterException() {
        return "redirect:/projects";
    }
}

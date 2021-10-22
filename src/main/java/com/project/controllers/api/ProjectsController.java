package com.project.controllers.api;

import com.project.model.Project;
import com.project.services.ProjectsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequestMapping("/api/projects")
@CrossOrigin
public class ProjectsController {
    private final ProjectsService projectsService;

    @Autowired
    public ProjectsController(ProjectsService projectsService) {
        this.projectsService = projectsService;
    }

    @GetMapping()
    public ResponseEntity<CollectionModel<Project>> findAllProjects() {
        List<Project> allProjects = projectsService.findAll();
        allProjects.forEach(project -> project.addIf(!project.hasLinks(),
                () -> getLinkToProject(project)));
        Link link = linkTo(ProjectsController.class).withSelfRel();
        return ResponseEntity.ok(CollectionModel.of(allProjects, link));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Project> findById(@PathVariable("id") long id) {
        Optional<Project> optionalProject = projectsService.findById(id);
        return optionalProject.map(project -> {
            project.add(getLinkToProject(project));
            return new ResponseEntity<>(project, HttpStatus.OK);
        }).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/allByStudent/{studentID}")
    public ResponseEntity<CollectionModel<Project>> findAllByStudents_ID(@PathVariable("studentID") long studentID) {
        List<Project> allByStudentsID = projectsService.findAllByStudents_ID(studentID);
        allByStudentsID.forEach(project -> project.addIf(!project.hasLinks(),
                () -> getLinkToProject(project)));
        Link link = linkTo(ProjectsController.class).withRel("All projects");
        return ResponseEntity.ok(CollectionModel.of(allByStudentsID, link));
    }

    @PostMapping()
    public ResponseEntity<Project> create(@RequestBody Project project) {
        project = projectsService.create(project);

        if (project != null) {
            return new ResponseEntity<>(project, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping()
    public ResponseEntity<Project> update(@RequestBody Project project) {
        project = projectsService.update(project);

        if (project != null) {
            return new ResponseEntity<>(project, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable("id") long id) {
        projectsService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private Link getLinkToProject(Project project) {
        addLinksToTasks(project);
        addLinksToStudents(project);
        return linkTo(ProjectsController.class).slash(project.getID()).withSelfRel();
    }

    private void addLinksToTasks(Project project) {
        if (project.getTasks() != null) {
            project.getTasks().forEach(task -> task.addIf(!task.hasLinks(),
                    () -> linkTo(TasksController.class).slash(task.getID()).withSelfRel()));
        }
    }

    private void addLinksToStudents(Project project) {
        if (project.getStudents() != null) {
            project.getStudents().forEach(student -> student.addIf(!student.hasLinks(),
                    () -> linkTo(StudentsController.class).slash(student.getID()).withSelfRel()));
        }
    }
}

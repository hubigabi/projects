package com.project.controllers.api;

import com.project.model.Student;
import com.project.model.Task;
import com.project.services.TasksService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin
public class TasksController {
    private final TasksService tasksService;

    @Autowired
    public TasksController(TasksService tasksService) {
        this.tasksService = tasksService;
    }

    @GetMapping()
    public ResponseEntity<CollectionModel<Task>> findAllTasks() {
        List<Task> allTasks = tasksService.findAll();
        allTasks.forEach(task -> task.addIf(!task.hasLinks(),
                () -> getLinkToTask(task)));
        Link link = linkTo(TasksController.class).withSelfRel();
        return ResponseEntity.ok(CollectionModel.of(allTasks, link));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> findById(@PathVariable("id") long id) {
        Optional<Task> optionalTask = tasksService.findById(id);
        return optionalTask.map(task -> {
            task.add(getLinkToTask(task));
            return new ResponseEntity<>(task, HttpStatus.OK);
        }).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/allByProject/{projectID}")
    public ResponseEntity<CollectionModel<Task>> findAllByProjectID(@PathVariable("projectID") long projectID) {
        List<Task> allByProjectID = tasksService.findAllByProjectID(projectID);
        allByProjectID.forEach(task -> task.addIf(!task.hasLinks(),
                () -> getLinkToTask(task)));
        Link link = linkTo(TasksController.class).withRel("All tasks");
        return ResponseEntity.ok(CollectionModel.of(allByProjectID, link));
    }

    @PostMapping()
    public ResponseEntity<Task> create(@RequestBody Task task) {
        task = tasksService.create(task);

        if (task != null) {
            return new ResponseEntity<>(task, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping()
    public ResponseEntity<Task> update(@RequestBody Task task) {
        task = tasksService.update(task);

        if (task != null) {
            return new ResponseEntity<>(task, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable("id") long id) {
        tasksService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private Link getLinkToTask(Task task) {
        if (task.getProject() != null) {
            addLinkToProject(task);
            addLinksToStudents(task);
        }
        return linkTo(TasksController.class).slash(task.getID()).withSelfRel();
    }

    private void addLinkToProject(Task task) {
        task.getProject().addIf(!task.getProject().hasLinks(),
                () -> linkTo(ProjectsController.class).slash(task.getProject().getID()).withSelfRel());
    }

    private void addLinksToStudents(Task task) {
        Set<Student> students = task.getProject().getStudents();
        if (students != null) {
            students.forEach(student -> student.addIf(!student.hasLinks(),
                    () -> linkTo(StudentsController.class).slash(student.getID()).withSelfRel()));
        }
    }
}

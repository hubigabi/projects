package com.project.controllers.api;

import com.project.model.Student;
import com.project.services.StudentsService;
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
@RequestMapping("/api/students")
@CrossOrigin
public class StudentsController {
    private final StudentsService studentsService;

    @Autowired
    public StudentsController(StudentsService studentsService) {
        this.studentsService = studentsService;
    }

    @GetMapping()
    public ResponseEntity<CollectionModel<Student>> findAllStudents() {
        List<Student> allStudents = studentsService.findAll();
        allStudents.forEach(student -> student.addIf(!student.hasLinks(),
                () -> getLinkToStudent(student)));
        Link link = linkTo(StudentsController.class).withSelfRel();
        return ResponseEntity.ok(CollectionModel.of(allStudents, link));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Student> findById(@PathVariable("id") long id) {
        Optional<Student> optionalStudent = studentsService.findById(id);
        return optionalStudent.map(student -> {
            student.add(getLinkToStudent(student));
            return new ResponseEntity<>(student, HttpStatus.OK);
        }).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping()
    public ResponseEntity<Student> create(@RequestBody Student student) {
        student = studentsService.create(student);

        if (student != null) {
            return new ResponseEntity<>(student, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping()
    public ResponseEntity<Student> update(@RequestBody Student student) {
        student = studentsService.update(student);

        if (student != null) {
            return new ResponseEntity<>(student, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable("id") long id) {
        studentsService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private Link getLinkToStudent(Student student) {
        return linkTo(StudentsController.class).slash(student.getID()).withSelfRel();
    }
}

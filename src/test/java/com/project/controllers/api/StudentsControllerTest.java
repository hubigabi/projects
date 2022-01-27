package com.project.controllers.api;

import com.project.model.Student;
import com.project.services.StudentsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StudentsControllerTest {

    @Mock
    private StudentsService studentsService;

    @InjectMocks
    private StudentsController studentsController;

    @Test
    void findAllStudentsShouldReturnStudents() {
        Student student1 = new Student(1L, "Kowalski", "Jan", "92318", true, null);
        Student student2 = new Student(2L, "Nowak", "Anna", "120947", false, null);
        List<Student> students = Arrays.asList(student1, student2);
        when(studentsService.findAll()).thenReturn(students);

        ResponseEntity<CollectionModel<Student>> responseEntity = studentsController.findAllStudents();

        assertNotNull(responseEntity.getBody());
        Collection<Student> studentCollection = responseEntity.getBody().getContent();
        assertNotNull(studentCollection);
        assertThat(studentCollection, hasSize(2));
        assertAll(() -> assertTrue(studentCollection.contains(students.get(0))),
                () -> assertTrue(studentCollection.contains(students.get(1))));
    }

    @Test
    void findByIdShouldReturnStudent() {
        long studentID = 1L;
        Student student = new Student(0L, "Kowalski", "Jan", "92318", true, null);
        student.setID(studentID);
        when(studentsService.findById(studentID)).thenReturn(Optional.of(student));

        ResponseEntity<Student> responseEntity = studentsController.findById(student.getID());

        assertAll(() -> assertThat(responseEntity.getStatusCodeValue(), is(HttpStatus.OK.value())),
                () -> assertThat(responseEntity.getBody(), is(student)));
    }

    @Test
    void findByIdShouldReturnNotFound() {
        long studentID = 1L;
        when(studentsService.findById(studentID)).thenReturn(Optional.empty());

        ResponseEntity<Student> responseEntity = studentsController.findById(studentID);

        assertThat(responseEntity.getStatusCodeValue()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void createShouldReturnStudent() {
        long studentID = 1L;
        Student student = new Student(0L, "Kowalski", "Jan", "92318", true, null);
        student.setID(studentID);
        when(studentsService.create(any(Student.class))).thenReturn(student);

        ResponseEntity<Student> responseEntity = studentsController.create(student);

        assertAll(() -> assertThat(responseEntity.getStatusCodeValue(), is(HttpStatus.CREATED.value())),
                () -> assertThat(responseEntity.getBody(), is(student)));
    }

    @Test
    void updateShouldReturnStudent() {
        long studentID = 1L;
        Student student = new Student(0L, "Kowalski", "Jan", "92318", true, null);
        student.setID(studentID);
        when(studentsService.update(any(Student.class))).thenReturn(student);

        ResponseEntity<Student> responseEntity = studentsController.update(student);

        assertAll(() -> assertThat(responseEntity.getStatusCodeValue(), is(HttpStatus.OK.value())),
                () -> assertThat(responseEntity.getBody(), is(student)));
    }

    @Test
    void deleteByIdShouldReturnNoContent() {
        long studentID = 1L;

        ResponseEntity<Void> responseEntity = studentsController.deleteById(studentID);

        assertThat(responseEntity.getStatusCodeValue()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}

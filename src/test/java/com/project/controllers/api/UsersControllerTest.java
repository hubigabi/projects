package com.project.controllers.api;

import com.project.model.Lecturer;
import com.project.model.Student;
import com.project.model.User;
import com.project.services.UsersService;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UsersControllerTest {

    @Mock
    private UsersService usersService;

    @InjectMocks
    private UsersController usersController;

    @Test
    void findAllUsersShouldReturnUsers() {
        Student student = new Student(1L, "jkowalski", "password123", "Kowalski", "Jan", "jankowalski@example.com", "92318", true);
        Lecturer lecturer = new Lecturer(2L, "adam_wojciechowski", "password1@", "Wojciechowski", "Adam", "adamwojciechowski@example.com", "doktor habilitowany");
        List<User> users = Arrays.asList(student, lecturer);
        when(usersService.findAll()).thenReturn(users);

        ResponseEntity<CollectionModel<User>> responseEntity = usersController.findAllUsers();

        assertNotNull(responseEntity.getBody());
        Collection<User> userCollection = responseEntity.getBody().getContent();
        assertNotNull(userCollection);
        assertThat(userCollection, hasSize(2));
        assertAll(() -> assertTrue(userCollection.contains(users.get(0))),
                () -> assertTrue(userCollection.contains(users.get(1))));
    }

    @Test
    void findByIdShouldReturnStudent() {
        long userID = 1L;
        Student student = new Student("jkowalski", "password123", "Kowalski", "Jan", "jankowalski@example.com", "92318", true);
        student.setID(userID);
        when(usersService.findById(userID)).thenReturn(Optional.of(student));

        ResponseEntity<User> responseEntity = usersController.findById(student.getID());

        assertAll(() -> assertThat(responseEntity.getStatusCodeValue(), is(HttpStatus.OK.value())),
                () -> assertThat(responseEntity.getBody(), is(student)));
    }

    @Test
    void findByIdShouldReturnLecturer() {
        long userID = 1L;
        Lecturer lecturer = new Lecturer("adam_wojciechowski", "password1@", "Wojciechowski", "Adam", "adamwojciechowski@example.com", "doktor habilitowany");
        lecturer.setID(userID);
        when(usersService.findById(userID)).thenReturn(Optional.of(lecturer));

        ResponseEntity<User> responseEntity = usersController.findById(lecturer.getID());

        assertAll(() -> assertThat(responseEntity.getStatusCodeValue(), is(HttpStatus.OK.value())),
                () -> assertThat(responseEntity.getBody(), is(lecturer)));
    }

    @Test
    void findByIdShouldReturnNotFound() {
        long userID = 1L;
        when(usersService.findById(userID)).thenReturn(Optional.empty());

        ResponseEntity<User> responseEntity = usersController.findById(userID);

        assertThat(responseEntity.getStatusCodeValue()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void existsByEmailShouldReturnTrue() {
        Student student = new Student(1L, "anna_nowak", "password98", "Nowak", "Anna", "annanowak@example.com", "120947", false);
        when(usersService.existsByEmail(student.getEmail())).thenReturn(true);

        ResponseEntity<Boolean> responseEntity = usersController.existsByEmail(student.getEmail());

        assertAll(() -> assertThat(responseEntity.getStatusCodeValue(), is(HttpStatus.OK.value())),
                () -> assertThat(responseEntity.getBody(), is(true)));
    }
}

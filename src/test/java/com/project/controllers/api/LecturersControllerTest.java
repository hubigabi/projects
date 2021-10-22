package com.project.controllers.api;

import com.project.model.Lecturer;
import com.project.services.LecturersService;
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
public class LecturersControllerTest {

    @Mock
    private LecturersService lecturersService;

    @InjectMocks
    private LecturersController lecturersController;

    @Test
    void findAllLecturersShouldReturnLecturers() {
        Lecturer lecturer1 = new Lecturer(1L, "adam_wojciechowski", "password1@", "Wojciechowski", "Adam", "adamwojciechowski@example.com", "doktor habilitowany");
        Lecturer lecturer2 = new Lecturer(1L, "justyna_grzybowska", "password!9#", "Grzybowska", "Justyna", "justynagrzybowska@example.com", "profesor nadzwyczajny");
        List<Lecturer> lecturers = Arrays.asList(lecturer1, lecturer2);
        when(lecturersService.findAll()).thenReturn(lecturers);

        ResponseEntity<CollectionModel<Lecturer>> responseEntity = lecturersController.findAllLecturers();

        assertNotNull(responseEntity.getBody());
        Collection<Lecturer> lecturerCollection = responseEntity.getBody().getContent();
        assertNotNull(lecturerCollection);
        assertThat(lecturerCollection, hasSize(2));
        assertAll(() -> assertTrue(lecturerCollection.contains(lecturers.get(0))),
                () -> assertTrue(lecturerCollection.contains(lecturers.get(1))));
    }

    @Test
    void findByIdShouldReturnLecturer() {
        long lecturerID = 1L;
        Lecturer lecturer = new Lecturer("adam_wojciechowski", "password1@", "Wojciechowski", "Adam", "adamwojciechowski@example.com", "doktor habilitowany");
        lecturer.setID(lecturerID);
        when(lecturersService.findById(lecturerID)).thenReturn(Optional.of(lecturer));

        ResponseEntity<Lecturer> responseEntity = lecturersController.findById(lecturer.getID());

        assertAll(() -> assertThat(responseEntity.getStatusCodeValue(), is(HttpStatus.OK.value())),
                () -> assertThat(responseEntity.getBody(), is(lecturer)));
    }

    @Test
    void findByIdShouldReturnNotFound() {
        long lecturerID = 1L;
        when(lecturersService.findById(lecturerID)).thenReturn(Optional.empty());

        ResponseEntity<Lecturer> responseEntity = lecturersController.findById(lecturerID);

        assertThat(responseEntity.getStatusCodeValue()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void createShouldReturnStudent() {
        long lecturerID = 1L;
        Lecturer lecturer = new Lecturer("adam_wojciechowski", "password1@", "Wojciechowski", "Adam", "adamwojciechowski@example.com", "doktor habilitowany");
        lecturer.setID(lecturerID);
        when(lecturersService.create(any(Lecturer.class))).thenReturn(lecturer);

        ResponseEntity<Lecturer> responseEntity = lecturersController.create(lecturer);

        assertAll(() -> assertThat(responseEntity.getStatusCodeValue(), is(HttpStatus.CREATED.value())),
                () -> assertThat(responseEntity.getBody(), is(lecturer)));
    }

    @Test
    void updateShouldReturnStudent() {
        long lecturerID = 1L;
        Lecturer lecturer = new Lecturer("adam_wojciechowski", "password1@", "Wojciechowski", "Adam", "adamwojciechowski@example.com", "doktor habilitowany");
        lecturer.setID(lecturerID);
        when(lecturersService.update(any(Lecturer.class))).thenReturn(lecturer);

        ResponseEntity<Lecturer> responseEntity = lecturersController.update(lecturer);

        assertAll(() -> assertThat(responseEntity.getStatusCodeValue(), is(HttpStatus.OK.value())),
                () -> assertThat(responseEntity.getBody(), is(lecturer)));
    }

    @Test
    void deleteByIdShouldReturnNoContent() {
        long lecturerID = 1L;

        ResponseEntity<Void> responseEntity = lecturersController.deleteById(lecturerID);

        assertThat(responseEntity.getStatusCodeValue()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}

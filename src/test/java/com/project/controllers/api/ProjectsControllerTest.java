package com.project.controllers.api;

import com.project.model.Project;
import com.project.model.Student;
import com.project.services.ProjectsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProjectsControllerTest {

    @Mock
    private ProjectsService projectsService;

    @InjectMocks
    private ProjectsController projectsController;

    @Test
    void findAllShouldReturnProjects() {
        Project p1 = new Project("Project 1", "Description for project 1", LocalDate.now().plusDays(15));
        Project p2 = new Project("Project 2", "Description for project 2", LocalDate.now().plusDays(20));
        List<Project> projects = Arrays.asList(p1, p2);
        when(projectsService.findAll()).thenReturn(projects);

        ResponseEntity<CollectionModel<Project>> responseEntity = projectsController.findAllProjects();

        assertNotNull(responseEntity.getBody());
        Collection<Project> projectCollection = responseEntity.getBody().getContent();
        assertNotNull(projectCollection);
        assertThat(projectCollection, hasSize(2));
        assertAll(() -> assertTrue(projectCollection.contains(projects.get(0))),
                () -> assertTrue(projectCollection.contains(projects.get(1))));
    }

    @Test
    void findByIdShouldReturnProject() {
        long projectID = 1L;
        Project project = new Project("Project 1", "Description for project 1", LocalDate.now().plusDays(15));
        project.setID(projectID);
        when(projectsService.findById(projectID)).thenReturn(Optional.of(project));

        ResponseEntity<Project> responseEntity = projectsController.findById(project.getID());

        assertAll(() -> assertThat(responseEntity.getStatusCodeValue(), is(HttpStatus.OK.value())),
                () -> assertThat(responseEntity.getBody(), is(project)));
    }

    @Test
    void findByIdShouldReturnNotFound() {
        long projectID = 1L;
        when(projectsService.findById(projectID)).thenReturn(Optional.empty());

        ResponseEntity<Project> responseEntity = projectsController.findById(projectID);

        assertThat(responseEntity.getStatusCodeValue()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void findAllByStudentIDShouldReturnProjects() {
        Project p1 = new Project("Project 1", "Description for project 1", LocalDate.now().plusDays(15));
        Project p2 = new Project("Project 2", "Description for project 2", LocalDate.now().plusDays(20));
        List<Project> projects = Arrays.asList(p1, p2);
        Student student = new Student(1L, "anna_nowak", "password98", "Nowak", "Anna", "annanowak@example.com", "120947", false);
        student.setProjects(new HashSet<>(projects));
        when(projectsService.findAllByStudents_ID(student.getID())).thenReturn(projects);

        ResponseEntity<CollectionModel<Project>> responseEntity = projectsController.findAllByStudents_ID(student.getID());

        assertNotNull(responseEntity.getBody());
        Collection<Project> projectCollection = responseEntity.getBody().getContent();
        assertNotNull(projectCollection);
        assertAll(() -> assertThat(projectCollection, hasSize(2)),
                () -> assertThat(projectCollection).hasSameElementsAs(student.getProjects()));
    }

    @Test
    void createShouldReturnProject() {
        long projectID = 1L;
        Project project = new Project("Project 1", "Description for project 1", LocalDate.now().plusDays(15));
        project.setID(projectID);
        when(projectsService.create(any(Project.class))).thenReturn(project);

        ResponseEntity<Project> responseEntity = projectsController.create(project);

        assertAll(() -> assertThat(responseEntity.getStatusCodeValue(), is(HttpStatus.CREATED.value())),
                () -> assertThat(responseEntity.getBody(), is(project)));
    }

    @Test
    void updateShouldReturnProject() {
        long projectID = 1L;
        Project project = new Project("Project 1", "Description for project 1", LocalDate.now().plusDays(15));
        project.setID(projectID);
        when(projectsService.update(any(Project.class))).thenReturn(project);

        ResponseEntity<Project> responseEntity = projectsController.update(project);

        assertAll(() -> assertThat(responseEntity.getStatusCodeValue(), is(HttpStatus.OK.value())),
                () -> assertThat(responseEntity.getBody(), is(project)));
    }

    @Test
    void deleteByIdShouldReturnNoContent() {
        long projectID = 1L;

        ResponseEntity<Void> responseEntity = projectsController.deleteById(projectID);

        assertThat(responseEntity.getStatusCodeValue()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}

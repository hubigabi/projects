package com.project.controllers.api;

import com.project.model.Project;
import com.project.model.Task;
import com.project.model.TaskStatus;
import com.project.services.TasksService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
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
public class TasksControllerTest {

    @Mock
    private TasksService tasksService;

    @InjectMocks
    private TasksController tasksController;

    @Test
    void findAllShouldReturnTasks() {
        Task t1 = new Task("Task 1", "Description for task 1", TaskStatus.IN_PROGRESS);
        Task t2 = new Task("Task 2", "Description for task 2", TaskStatus.TO_DO);
        List<Task> tasks = Arrays.asList(t1, t2);
        when(tasksService.findAll()).thenReturn(tasks);

        ResponseEntity<CollectionModel<Task>> responseEntity = tasksController.findAllTasks();

        assertNotNull(responseEntity.getBody());
        Collection<Task> taskCollection = responseEntity.getBody().getContent();
        assertNotNull(taskCollection);
        assertThat(taskCollection, hasSize(2));
        assertAll(() -> assertTrue(taskCollection.contains(tasks.get(0))),
                () -> assertTrue(taskCollection.contains(tasks.get(1))));
    }

    @Test
    void findByIdShouldReturnTask() {
        long taskID = 1L;
        Task task = new Task("Task 1", "Description for task 1", TaskStatus.IN_PROGRESS);
        task.setID(taskID);
        when(tasksService.findById(taskID)).thenReturn(Optional.of(task));

        ResponseEntity<Task> responseEntity = tasksController.findById(task.getID());

        assertAll(() -> assertThat(responseEntity.getStatusCodeValue(), is(HttpStatus.OK.value())),
                () -> assertThat(responseEntity.getBody(), is(task)));
    }

    @Test
    void findByIdShouldReturnNotFound() {
        long taskID = 1L;
        when(tasksService.findById(taskID)).thenReturn(Optional.empty());

        ResponseEntity<Task> responseEntity = tasksController.findById(taskID);

        assertThat(responseEntity.getStatusCodeValue()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void findAllByProjectIDShouldReturnTasks() {
        Long projectID = 1L;
        Project project = new Project("Project 1", "Description for project 1", LocalDate.now().plusDays(15));
        project.setID(projectID);
        Task t1 = new Task("Task 1", "Description for task 1", TaskStatus.IN_PROGRESS);
        Task t2 = new Task("Task 2", "Description for task 2", TaskStatus.TO_DO);
        List<Task> tasks = Arrays.asList(t1, t2);
        project.setTasks(tasks);
        when(tasksService.findAllByProjectID(projectID)).thenReturn(tasks);

        ResponseEntity<CollectionModel<Task>> responseEntity = tasksController.findAllByProjectID(project.getID());

        assertNotNull(responseEntity.getBody());
        Collection<Task> taskCollection = responseEntity.getBody().getContent();
        assertNotNull(taskCollection);
        assertAll(() -> assertThat(taskCollection, hasSize(2)),
                () -> assertThat(taskCollection).hasSameElementsAs(project.getTasks()));
    }

    @Test
    void createShouldReturnTask() {
        long taskID = 1L;
        Task task = new Task("Task 1", "Description for task 1", TaskStatus.IN_PROGRESS);
        task.setID(taskID);
        when(tasksService.create(any(Task.class))).thenReturn(task);

        ResponseEntity<Task> responseEntity = tasksController.create(task);

        assertAll(() -> assertThat(responseEntity.getStatusCodeValue(), is(HttpStatus.CREATED.value())),
                () -> assertThat(responseEntity.getBody(), is(task)));
    }

    @Test
    void updateShouldReturnTask() {
        long taskID = 1L;
        Task task = new Task("Task 1", "Description for task 1", TaskStatus.IN_PROGRESS);
        task.setID(taskID);
        when(tasksService.update(any(Task.class))).thenReturn(task);

        ResponseEntity<Task> responseEntity = tasksController.update(task);

        assertAll(() -> assertThat(responseEntity.getStatusCodeValue(), is(HttpStatus.OK.value())),
                () -> assertThat(responseEntity.getBody(), is(task)));
    }

    @Test
    void deleteByIdShouldReturnNoContent() {
        long taskID = 1L;

        ResponseEntity<Void> responseEntity = tasksController.deleteById(taskID);

        assertThat(responseEntity.getStatusCodeValue()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}

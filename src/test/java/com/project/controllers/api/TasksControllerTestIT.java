package com.project.controllers.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.model.Project;
import com.project.model.Task;
import com.project.model.TaskStatus;
import com.project.services.TasksService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(TasksController.class)
class TasksControllerTestIT {

    private static final String TASK_API_URL = "/api/tasks";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TasksService tasksService;

    @Test
    void findByIdShouldReturnTask() throws Exception {
        long taskID = 1L;
        Task task = new Task("Task 1", "Description for task 1", TaskStatus.IN_PROGRESS);
        task.setID(taskID);

        Mockito.when(tasksService.findById(taskID)).thenReturn(Optional.of(task));

        ResultActions resultActions = mockMvc.perform(get(TASK_API_URL + "/{id}", task.getID())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is((int) taskID)));

        checkTaskJSONPath(resultActions, task);
    }

    @Test
    void findByIdShouldReturnNotFound() throws Exception {
        long taskID = 1L;
        Mockito.when(tasksService.findById(taskID)).thenReturn(Optional.empty());

        mockMvc.perform(get(TASK_API_URL + "/{id}", taskID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    void findAllShouldReturnTasks() throws Exception {
        Task t1 = new Task("Task 1", "Description for task 1", TaskStatus.IN_PROGRESS);
        Task t2 = new Task("Task 2", "Description for task 2", TaskStatus.TO_DO);
        List<Task> tasks = Arrays.asList(t1, t2);

        Mockito.when(tasksService.findAll()).thenReturn(tasks);

        ResultActions resultActions = mockMvc.perform(get(TASK_API_URL)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.taskList", hasSize(tasks.size())));

        checkTasksJSONPath(resultActions, tasks);
    }

    @Test
    void findAllByProjectIDShouldReturnTasks() throws Exception {
        Long projectID = 1L;
        Project project = new Project("Project 1", "Description for project 1", LocalDate.now().plusDays(15));
        project.setID(projectID);

        Task t1 = new Task("Task 1", "Description for task 1", TaskStatus.IN_PROGRESS);
        t1.setProject(project);
        Task t2 = new Task("Task 2", "Description for task 2", TaskStatus.TO_DO);
        t2.setProject(project);
        List<Task> tasks = Arrays.asList(t1, t2);

        Mockito.when(tasksService.findAllByProjectID(projectID)).thenReturn(tasks);

        ResultActions resultActions = mockMvc.perform(get(TASK_API_URL + "/allByProject/{id}", projectID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.taskList", hasSize(tasks.size())));

        checkTasksJSONPath(resultActions, tasks);
        checkProjectInTasksJSONPath(resultActions, tasks);
    }

    @Test
    void createShouldReturnTask() throws Exception {
        long taskID = 1L;
        Task task = new Task("Task 1", "Description for task 1", TaskStatus.IN_PROGRESS);
        task.setID(taskID);

        Mockito.when(tasksService.create(any(Task.class))).thenReturn(task);

        ResultActions resultActions = mockMvc.perform(post(TASK_API_URL)
                .content(objectMapper.writeValueAsString(task))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        checkTaskJSONPath(resultActions, task);
    }

    @Test
    void updateShouldReturnTask() throws Exception {
        long taskID = 1L;
        Task task = new Task("Task 1", "Description for task 1", TaskStatus.IN_PROGRESS);
        task.setID(taskID);

        Mockito.when(tasksService.update(any(Task.class))).thenReturn(task);

        ResultActions resultActions = mockMvc.perform(put(TASK_API_URL)
                .content(objectMapper.writeValueAsString(task))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        checkTaskJSONPath(resultActions, task);
    }

    @Test
    void deleteByIdShouldReturnNoContent() throws Exception {
        long taskID = 1L;

        mockMvc.perform(delete(TASK_API_URL + "/{id}", taskID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$").doesNotExist());
    }

    private void checkTaskJSONPath(ResultActions resultActions, Task task) throws Exception {
        resultActions
                .andExpect(jsonPath("$.name", is(task.getName())))
                .andExpect(jsonPath("$.description", is(task.getDescription())))
                .andExpect(jsonPath("$.taskStatus", is(task.getTaskStatus().toString())));

    }

    private void checkTasksJSONPath(ResultActions resultActions, List<Task> tasks) throws Exception {
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            String jsonIndexPath = "$._embedded.taskList[" + i + "]";

            resultActions
                    .andExpect(jsonPath(jsonIndexPath + ".name", is(task.getName())))
                    .andExpect(jsonPath(jsonIndexPath + ".description", is(task.getDescription())))
                    .andExpect(jsonPath(jsonIndexPath + ".taskStatus", is(task.getTaskStatus().toString())));
        }
    }

    private void checkProjectInTasksJSONPath(ResultActions resultActions, List<Task> tasks) throws Exception {
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            String jsonIndexPath = "$._embedded.taskList[" + i + "]";

            resultActions
                    .andExpect(jsonPath(jsonIndexPath + ".project.id", is(task.getProject().getID().intValue())));
        }
    }
}

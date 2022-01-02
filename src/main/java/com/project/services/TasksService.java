package com.project.services;

import com.project.model.Task;
import com.project.model.dto.ChangeTaskStatusRequest;
import com.project.model.dto.ChangeTaskStatusResponse;
import com.project.repositories.TasksRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j(topic = "Tasks service")
public class TasksService {

    private final TasksRepository tasksRepository;

    @Autowired
    public TasksService(TasksRepository tasksRepository) {
        this.tasksRepository = tasksRepository;
    }

    public Optional<Task> findById(Long ID) {
        return tasksRepository.findById(ID);
    }

    public List<Task> findAll() {
        return tasksRepository.findAll();
    }

    public List<Task> findAllByProjectID(Long projectID) {
        return tasksRepository.findAllByProjectID(projectID);
    }

    public Task create(Task task) {
        return tasksRepository.save(task);
    }

    public Task update(Task task) {
        return tasksRepository.save(task);
    }

    public boolean existsById(Long ID) {
        return tasksRepository.existsById(ID);
    }

    public long count() {
        return tasksRepository.count();
    }

    public void delete(Task task) {
        tasksRepository.delete(task);
    }

    public void deleteById(Long id) {
        if (existsById(id)) {
            tasksRepository.deleteById(id);
        }
    }

    @Transactional
    public Optional<ChangeTaskStatusResponse> changeTaskStatus(ChangeTaskStatusRequest changeTaskStatusRequest) {
        return tasksRepository.findById(changeTaskStatusRequest.getId())
                .map(task -> {
                    task.setTaskStatus(changeTaskStatusRequest.getTaskStatus());
                    task = tasksRepository.save(task);
                    return mapToChangeTaskStatusResponse(task);
                });
    }

    private ChangeTaskStatusResponse mapToChangeTaskStatusResponse(Task task) {
        ChangeTaskStatusResponse response = new ChangeTaskStatusResponse();
        response.setId(task.getID());
        response.setName(task.getName());
        response.setDescription(task.getDescription());
        response.setTaskStatus(task.getTaskStatus());
        response.setAdditionDateTime(task.getAdditionDateTime());
        return response;
    }
}

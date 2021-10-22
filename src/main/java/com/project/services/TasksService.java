package com.project.services;

import com.project.model.Task;
import com.project.repositories.TasksRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}

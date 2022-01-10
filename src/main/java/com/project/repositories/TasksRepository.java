package com.project.repositories;

import com.project.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TasksRepository extends JpaRepository<Task, Long>, RevisionRepository<Task, Long, Long> {
    List<Task> findAllByProjectID(Long projectID);
}

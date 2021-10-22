package com.project.services;

import com.project.model.Project;
import com.project.repositories.ProjectsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j(topic = "Projects service")
public class ProjectsService {

    private final ProjectsRepository projectsRepository;

    @Autowired
    public ProjectsService(ProjectsRepository projectsRepository) {
        this.projectsRepository = projectsRepository;
    }

    public Optional<Project> findById(Long ID) {
        return projectsRepository.findById(ID);
    }

    public List<Project> findAll() {
        return projectsRepository.findAll();
    }

    public List<Project> findAllByStudents_ID(Long studentID) {
        return projectsRepository.findAllByStudents_ID(studentID);
    }

    public Project create(Project project) {
        return projectsRepository.save(project);
    }

    public Project update(Project project) {
        return projectsRepository.save(project);
    }

    public boolean existsById(Long ID) {
        return projectsRepository.existsById(ID);
    }

    public long count() {
        return projectsRepository.count();
    }

    public void delete(Project project) {
        projectsRepository.delete(project);
    }

    public void deleteById(Long id) {
        if (existsById(id)) {
            projectsRepository.deleteById(id);
        }
    }
}

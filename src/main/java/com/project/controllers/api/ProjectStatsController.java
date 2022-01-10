package com.project.controllers.api;

import com.project.model.stats.ProjectStats;
import com.project.services.ProjectStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stats")
@CrossOrigin
public class ProjectStatsController {

    private final ProjectStatsService statsService;

    @Autowired
    public ProjectStatsController(ProjectStatsService statsService) {
        this.statsService = statsService;
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectStats> getProjectStats(@PathVariable long projectId) {
        ProjectStats projectStats = statsService.getProjectStats(projectId);
        return new ResponseEntity<>(projectStats, HttpStatus.OK);
    }

}

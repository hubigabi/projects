package com.project.services;

import com.project.model.Task;
import com.project.model.TaskStatus;
import com.project.model.stats.FinishedTaskStatus;
import com.project.model.stats.ProjectStats;
import com.project.repositories.TasksRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.RevisionType;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.query.AuditQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Service
@Slf4j(topic = "ProjectStats service")
public class ProjectStatsService {

    private final TasksRepository tasksRepository;
    private final AuditReader auditReader;

    @Autowired
    public ProjectStatsService(TasksRepository tasksRepository, AuditReader auditReader) {
        this.tasksRepository = tasksRepository;
        this.auditReader = auditReader;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private static class TaskRevision {
        private long id;
        private TaskStatus taskStatus;
        private long timestamp;
        private RevisionType revisionType;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private static class TaskStatusStartDate {
        // taskId for testing purposes
        private long taskId;
        private TaskStatus taskStatus;
        private LocalDate startLocalDate;
    }

    public ProjectStats getProjectStats(long projectId) {
        List<Long> tasksIdInProject = tasksRepository.findAllByProjectID(projectId).stream()
                .map(Task::getID)
                .collect(Collectors.toList());

        List<TaskStatus> taskStatuses = Arrays.stream(TaskStatus.values())
                .sorted(Comparator.comparingInt(TaskStatus::getOrder).reversed())
                .collect(Collectors.toList());

        AuditQuery query = auditReader.createQuery()
                .forRevisionsOfEntity(Task.class, false, false)
                .add(AuditEntity.id().in(tasksIdInProject));
        List<Object[]> resultList = query.getResultList();

        Map<Long, List<TaskRevision>> taskRevisionMap = resultList.stream()
                .map(ProjectStatsService::mapToTaskRevision)
                .collect(groupingBy(TaskRevision::getId));

        ArrayList<TaskStatusStartDate> taskStatusStartDateList = new ArrayList<>();

        for (var entry : taskRevisionMap.entrySet()) {
            List<TaskRevision> taskRevisions = entry.getValue();
            taskRevisions = getOnlyNotOutdatedTaskRevisions(taskRevisions);

            Optional<LocalDate> taskStatusStartDate = Optional.empty();
            for (TaskStatus taskStatus : taskStatuses) {
                OptionalLong currentTaskStatusStartTimestamp = taskRevisions.stream()
                        .filter(taskRevision -> taskRevision.getTaskStatus() == taskStatus)
                        .mapToLong(TaskRevision::getTimestamp)
                        .max();

                if (currentTaskStatusStartTimestamp.isPresent()) {
                    taskStatusStartDate = Optional.of(Instant.ofEpochMilli(currentTaskStatusStartTimestamp.getAsLong())
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate());
                }

                taskStatusStartDate.ifPresent(localDate -> taskStatusStartDateList.add(
                        new TaskStatusStartDate(entry.getKey(), taskStatus, localDate)));
            }
        }

        LocalDate firstProjectStatsDay = taskStatusStartDateList.stream()
                .map(TaskStatusStartDate::getStartLocalDate)
                .min(LocalDate::compareTo)
                .orElse(LocalDate.now());

        taskStatusStartDateList.removeIf(taskStatusStartDate -> !taskStatusStartDate.getTaskStatus().isInStats());
        Map<TaskStatus, List<TaskStatusStartDate>> taskStatusStartMap = taskStatusStartDateList.stream().
                collect(groupingBy(TaskStatusStartDate::getTaskStatus));

        ProjectStats projectStats = new ProjectStats();
        projectStats.setDays(firstProjectStatsDay.datesUntil(LocalDate.now().plusDays(1), Period.ofDays(1))
                .collect(Collectors.toList()));
        projectStats.setFinishedTaskStatuses(new ArrayList<>());
        for (var entry : taskStatusStartMap.entrySet()) {
            FinishedTaskStatus finishedTaskStatus = new FinishedTaskStatus();
            finishedTaskStatus.setTaskStatus(entry.getKey());
            finishedTaskStatus.setFinishedTaskStatus(new ArrayList<>());
            int finishedTaskStatusCounter = 0;
            for (LocalDate date = firstProjectStatsDay; !date.isAfter(LocalDate.now()); date = date.plusDays(1)) {
                final LocalDate currentDate = date;

                long currentDateFinishedTaskStatus = entry.getValue().stream()
                        .map(TaskStatusStartDate::getStartLocalDate)
                        .filter(localDate -> localDate.equals(currentDate))
                        .count();
                finishedTaskStatusCounter += currentDateFinishedTaskStatus;

                finishedTaskStatus.getFinishedTaskStatus().add(finishedTaskStatusCounter);
            }
            projectStats.getFinishedTaskStatuses().add(finishedTaskStatus);
        }
        fillProjectStatsWithRemainingTaskStatuses(projectStats);
        projectStats.getFinishedTaskStatuses()
                .sort(Comparator.comparingInt(finishedTaskStatus -> finishedTaskStatus.getTaskStatus().getOrder()));

        return projectStats;
    }

    private ArrayList<TaskRevision> getOnlyNotOutdatedTaskRevisions(List<TaskRevision> outdatedTaskRevisions) {
        outdatedTaskRevisions.sort(Comparator.comparingInt(value -> value.getTaskStatus().getOrder()));
        ArrayList<TaskRevision> actualTaskRevisions = new ArrayList<>();
        while (outdatedTaskRevisions.size() > 0) {
            TaskRevision mostRecentTaskRevision = outdatedTaskRevisions.stream()
                    .max(Comparator.comparingLong(TaskRevision::getTimestamp))
                    .get();

            int startIndexToRemove = outdatedTaskRevisions.indexOf(mostRecentTaskRevision);
            outdatedTaskRevisions.subList(startIndexToRemove, outdatedTaskRevisions.size()).clear();
            actualTaskRevisions.add(mostRecentTaskRevision);
        }
        return actualTaskRevisions;
    }

    private void fillProjectStatsWithRemainingTaskStatuses(ProjectStats projectStats) {
        List<TaskStatus> taskStatusesInStats = Arrays.stream(TaskStatus.values())
                .filter(TaskStatus::isInStats)
                .collect(Collectors.toList());

        List<TaskStatus> taskStatusesFromGeneratedStats = projectStats.getFinishedTaskStatuses().stream()
                .map(FinishedTaskStatus::getTaskStatus)
                .collect(Collectors.toList());
        int daysNumber = projectStats.getDays().size();
        List<Integer> zeros = new ArrayList<>(Collections.nCopies(daysNumber, 0));

        for (TaskStatus taskStatus : taskStatusesInStats) {
            if (!taskStatusesFromGeneratedStats.contains(taskStatus)) {
                FinishedTaskStatus finishedTaskStatus = new FinishedTaskStatus();
                finishedTaskStatus.setTaskStatus(taskStatus);
                finishedTaskStatus.setFinishedTaskStatus(zeros);
                projectStats.getFinishedTaskStatuses().add(finishedTaskStatus);
            }
        }
    }

    private static TaskRevision mapToTaskRevision(Object[] o) {
        TaskRevision taskRevision = new TaskRevision();
        Task task = (Task) o[0];
        taskRevision.setId(task.getID());
        taskRevision.setTaskStatus(task.getTaskStatus());
        DefaultRevisionEntity revisionEntity = (DefaultRevisionEntity) o[1];
        taskRevision.setTimestamp(revisionEntity.getTimestamp());
        taskRevision.setRevisionType((RevisionType) o[2]);
        return taskRevision;
    }

}

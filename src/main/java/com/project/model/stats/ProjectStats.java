package com.project.model.stats;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectStats {
    private List<LocalDate> days;
    private List<FinishedTaskStatus> finishedTaskStatusList;
}

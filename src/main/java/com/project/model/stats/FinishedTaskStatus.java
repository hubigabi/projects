package com.project.model.stats;

import com.project.model.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FinishedTaskStatus {
    private TaskStatus taskStatus;
    private List<Integer> finishedTaskStatus;
}

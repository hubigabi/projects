package com.project.model.dto;

import com.project.model.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangeTaskStatusResponse {
    private Long id;
    private String name;
    private String description;
    private TaskStatus taskStatus;
    private LocalDateTime additionDateTime;
}

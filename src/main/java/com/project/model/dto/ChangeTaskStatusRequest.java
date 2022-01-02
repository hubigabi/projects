package com.project.model.dto;

import com.project.model.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangeTaskStatusRequest {
    private Long id;
    private TaskStatus taskStatus;
}

package com.project.controllers.websocket;

import com.project.model.dto.ChangeTaskStatusRequest;
import com.project.model.dto.ChangeTaskStatusResponse;
import com.project.services.TasksService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@Slf4j(topic = "Board")
public class BoardWSController {

    private final TasksService tasksService;

    @Autowired
    public BoardWSController(TasksService tasksService) {
        this.tasksService = tasksService;
    }

    @MessageMapping("/changeTaskStatus/{project_id}")
    @SendTo("/board/{project_id}")
    public ChangeTaskStatusResponse changeTaskStatus(@DestinationVariable String project_id, ChangeTaskStatusRequest changeTaskStatusRequest) {
        log.info(String.format("New request: %s", changeTaskStatusRequest));
        return tasksService.changeTaskStatus(changeTaskStatusRequest).get();
    }
}

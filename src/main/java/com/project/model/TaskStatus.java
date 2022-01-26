package com.project.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum TaskStatus {
    TO_DO("Do zrobienia", "secondary", true, 1),
    IN_PROGRESS("W trakcie", "primary", true, 2),
    ON_HOLD("Wstrzymano", "warning", false, 3),
    COMPLETED("Ukończono", "success", true, 4);

    @Getter
    String friendlyName;
    @Getter
    String badgeClass;
    @Getter
    boolean inStats;
    @Getter
    int order;
}

package com.project.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum TaskStatus {
    TO_DO("Do zrobienia", "secondary"),
    IN_PROGRESS("W trakcie", "primary"),
    ON_HOLD("Wstrzymano", "warning"),
    COMPLETED("Ukończono", "success");

    @Getter String friendlyName;
    @Getter String badgeClass;
}

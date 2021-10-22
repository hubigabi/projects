package com.project.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@ToString(callSuper = true)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "lecturers")
public class Lecturer extends User {
    @Column(name = "academic_title", nullable = false)
    private String academicTitle;

    public Lecturer(Long ID, String username, String password, String lastName, String firstName, String email, String academicTitle) {
        super(ID, username, password, UserRole.LECTURER, lastName, firstName, email);
        this.academicTitle = academicTitle;
    }

    public Lecturer(String username, String password, String lastName, String firstName, String email, String academicTitle) {
        super(username, password, UserRole.LECTURER, lastName, firstName, email);
        this.academicTitle = academicTitle;
    }
}

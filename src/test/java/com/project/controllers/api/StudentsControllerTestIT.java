package com.project.controllers.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.model.Student;
import com.project.services.StudentsService;
import com.project.services.UsersService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(StudentsController.class)
class StudentsControllerTestIT {

    private static final String STUDENT_API_URL = "/api/students";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private StudentsService studentsService;

    @MockBean
    private UsersService usersService;

    @Test
    void findAllStudentsShouldReturnStudents() throws Exception {
        Student student1 = new Student(1L, "jkowalski", "password123", "Kowalski", "Jan", "jankowalski@example.com", "92318", true);
        Student student2 = new Student(2L, "anna_nowak", "password98", "Nowak", "Anna", "annanowak@example.com", "120947", false);
        List<Student> students = Arrays.asList(student1, student2);

        Mockito.when(studentsService.findAll()).thenReturn(students);

        ResultActions resultActions = mockMvc.perform(get(STUDENT_API_URL)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.studentList", hasSize(students.size())));

        checkStudentsJSONPath(resultActions, students);
    }

    @Test
    void findByIdShouldReturnStudent() throws Exception {
        long studentID = 1L;
        Student student = new Student("jkowalski", "password123", "Kowalski", "Jan", "jankowalski@example.com", "92318", true);
        student.setID(studentID);

        Mockito.when(studentsService.findById(studentID)).thenReturn(Optional.of(student));

        ResultActions resultActions = mockMvc.perform(get(STUDENT_API_URL + "/{id}", student.getID())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is((int) studentID)));

        checkStudentJSONPath(resultActions, student);
    }

    @Test
    void findByIdShouldReturnNotFound() throws Exception {
        long studentID = 1L;
        Mockito.when(studentsService.findById(studentID)).thenReturn(Optional.empty());

        mockMvc.perform(get(STUDENT_API_URL + "/{id}", studentID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    void deleteByIdShouldReturnNoContent() throws Exception {
        long studentID = 1L;

        mockMvc.perform(delete(STUDENT_API_URL + "/{id}", studentID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$").doesNotExist());
    }

    private void checkStudentsJSONPath(ResultActions resultActions, List<Student> students) throws Exception {
        for (int i = 0; i < students.size(); i++) {
            Student student = students.get(i);
            String jsonIndexPath = "$._embedded.studentList[" + i + "]";

            resultActions
                    .andExpect(jsonPath(jsonIndexPath + ".username", is(student.getUsername())))
                    .andExpect(jsonPath(jsonIndexPath + ".password", is(student.getPassword())))
                    .andExpect(jsonPath(jsonIndexPath + ".lastName", is(student.getLastName())))
                    .andExpect(jsonPath(jsonIndexPath + ".firstName", is(student.getFirstName())))
                    .andExpect(jsonPath(jsonIndexPath + ".email", is(student.getEmail())))
                    .andExpect(jsonPath(jsonIndexPath + ".indexNumber", is(student.getIndexNumber())))
                    .andExpect(jsonPath(jsonIndexPath + ".fullTime", is(student.getFullTime())));
        }
    }

    private void checkStudentJSONPath(ResultActions resultActions, Student student) throws Exception {
        resultActions
                .andExpect(jsonPath("$.username", is(student.getUsername())))
                .andExpect(jsonPath("$.password", is(student.getPassword())))
                .andExpect(jsonPath("$.lastName", is(student.getLastName())))
                .andExpect(jsonPath("$.firstName", is(student.getFirstName())))
                .andExpect(jsonPath("$.email", is(student.getEmail())))
                .andExpect(jsonPath("$.indexNumber", is(student.getIndexNumber())))
                .andExpect(jsonPath("$.fullTime", is(student.getFullTime())));
    }
}

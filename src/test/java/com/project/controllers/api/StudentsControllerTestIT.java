package com.project.controllers.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.model.Student;
import com.project.services.StudentsService;
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

    @Test
    void findAllStudentsShouldReturnStudents() throws Exception {
        Student student1 = new Student(1L, "Kowalski", "Jan", "92318", true, null);
        Student student2 = new Student(2L, "Nowak", "Anna", "120947", false, null);
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
        Student student = new Student(0L, "Kowalski", "Jan", "92318", true, null);
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
                    .andExpect(jsonPath(jsonIndexPath + ".lastName", is(student.getLastName())))
                    .andExpect(jsonPath(jsonIndexPath + ".firstName", is(student.getFirstName())))
                    .andExpect(jsonPath(jsonIndexPath + ".indexNumber", is(student.getIndexNumber())))
                    .andExpect(jsonPath(jsonIndexPath + ".fullTime", is(student.getFullTime())));
        }
    }

    private void checkStudentJSONPath(ResultActions resultActions, Student student) throws Exception {
        resultActions
                .andExpect(jsonPath("$.lastName", is(student.getLastName())))
                .andExpect(jsonPath("$.firstName", is(student.getFirstName())))
                .andExpect(jsonPath("$.indexNumber", is(student.getIndexNumber())))
                .andExpect(jsonPath("$.fullTime", is(student.getFullTime())));
    }
}

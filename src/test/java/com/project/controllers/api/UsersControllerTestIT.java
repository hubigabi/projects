package com.project.controllers.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.model.Lecturer;
import com.project.model.Student;
import com.project.model.User;
import com.project.services.UsersService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(UsersController.class)
class UsersControllerTestIT {

    private static final String USER_API_URL = "/api/users";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UsersService usersService;

    @Test
    void findAllUsersShouldReturnUsers() throws Exception {
        Student student = new Student(1L, "jkowalski", "password123", "Kowalski", "Jan", "jankowalski@example.com", "92318", true);
        Lecturer lecturer = new Lecturer(2L, "adam_wojciechowski", "password1@", "Wojciechowski", "Adam", "adamwojciechowski@example.com", "doktor habilitowany");
        List<User> users = Arrays.asList(student, lecturer);

        Mockito.when(usersService.findAll()).thenReturn(users);

        mockMvc.perform(get(USER_API_URL)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.studentList", hasSize(countStudents(users))))
                .andExpect(jsonPath("$._embedded.lecturerList", hasSize(countLecturers(users))));
    }

    @Test
    void findByIdShouldReturnStudent() throws Exception {
        long userID = 1L;
        Student student = new Student("jkowalski", "password123", "Kowalski", "Jan", "jankowalski@example.com", "92318", true);
        student.setID(userID);

        Mockito.when(usersService.findById(userID)).thenReturn(Optional.of(student));

        ResultActions resultActions = mockMvc.perform(get(USER_API_URL + "/{id}", student.getID())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is((int) userID)));

        checkUserJSONPath(resultActions, student);
    }

    @Test
    void findByIdShouldReturnLecturer() throws Exception {
        long userID = 1L;
        Lecturer lecturer = new Lecturer("adam_wojciechowski", "password1@", "Wojciechowski", "Adam", "adamwojciechowski@example.com", "doktor habilitowany");
        lecturer.setID(userID);

        Mockito.when(usersService.findById(userID)).thenReturn(Optional.of(lecturer));

        ResultActions resultActions = mockMvc.perform(get(USER_API_URL + "/{id}", lecturer.getID())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is((int) userID)));

        checkUserJSONPath(resultActions, lecturer);
    }

    @Test
    void findByIdShouldReturnNotFound() throws Exception {
        long userID = 1L;
        Mockito.when(usersService.findById(userID)).thenReturn(Optional.empty());

        mockMvc.perform(get(USER_API_URL + "/{id}", userID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    void existsByEmailShouldReturnTrue() throws Exception {
        Student student = new Student(1L, "anna_nowak", "password98", "Nowak", "Anna", "annanowak@example.com", "120947", false);
        Mockito.when(usersService.existsByEmail(student.getEmail())).thenReturn(true);

        MvcResult mvcResult = mockMvc.perform(get(USER_API_URL + "/existsByEmail/{email}", student.getEmail()))
                .andExpect(status().isOk()).andReturn();
        Boolean isEmail = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Boolean.class);

        Assertions.assertEquals(true, isEmail);
    }

    private int countStudents(List<User> users) {
        int studentsNumber = 0;
        for (User user : users) {
            if (user instanceof Student) {
                studentsNumber++;
            }
        }

        return studentsNumber;
    }

    private int countLecturers(List<User> users) {
        int lecturersNumber = 0;
        for (User user : users) {
            if (user instanceof Lecturer) {
                lecturersNumber++;
            }
        }

        return lecturersNumber;
    }

    private void checkUserJSONPath(ResultActions resultActions, User user) throws Exception {
        resultActions
                .andExpect(jsonPath("$.username", is(user.getUsername())))
                .andExpect(jsonPath("$.password", is(user.getPassword())))
                .andExpect(jsonPath("$.lastName", is(user.getLastName())))
                .andExpect(jsonPath("$.firstName", is(user.getFirstName())))
                .andExpect(jsonPath("$.email", is(user.getEmail())));
    }
}

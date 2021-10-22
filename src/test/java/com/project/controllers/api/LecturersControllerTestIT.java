package com.project.controllers.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.model.Lecturer;
import com.project.services.LecturersService;
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
@WebMvcTest(LecturersController.class)
class LecturersControllerTestIT {

    private static final String LECTURER_API_URL = "/api/lecturers";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LecturersService lecturersService;

    @MockBean
    private UsersService usersService;

    @Test
    void findAllLecturersShouldReturnLecturers() throws Exception {
        Lecturer lecturer1 = new Lecturer(1L, "adam_wojciechowski", "password1@", "Wojciechowski", "Adam", "adamwojciechowski@example.com", "doktor habilitowany");
        Lecturer lecturer2 = new Lecturer(1L, "justyna_grzybowska", "password!9#", "Grzybowska", "Justyna", "justynagrzybowska@example.com", "profesor nadzwyczajny");
        List<Lecturer> lecturers = Arrays.asList(lecturer1, lecturer2);

        Mockito.when(lecturersService.findAll()).thenReturn(lecturers);

        ResultActions resultActions = mockMvc.perform(get(LECTURER_API_URL)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.lecturerList", hasSize(lecturers.size())));

        checkLecturersJSONPath(resultActions, lecturers);
    }

    @Test
    void findByIdShouldReturnLecturer() throws Exception {
        long lecturerID = 1L;
        Lecturer lecturer = new Lecturer("adam_wojciechowski", "password1@", "Wojciechowski", "Adam", "adamwojciechowski@example.com", "doktor habilitowany");
        lecturer.setID(lecturerID);

        Mockito.when(lecturersService.findById(lecturerID)).thenReturn(Optional.of(lecturer));

        ResultActions resultActions = mockMvc.perform(get(LECTURER_API_URL + "/{id}", lecturer.getID())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is((int) lecturerID)));

        checkLecturerJSONPath(resultActions, lecturer);
    }

    @Test
    void findByIdShouldReturnNotFound() throws Exception {
        long lecturerID = 1L;
        Mockito.when(lecturersService.findById(lecturerID)).thenReturn(Optional.empty());

        mockMvc.perform(get(LECTURER_API_URL + "/{id}", lecturerID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    void deleteByIdShouldReturnNoContent() throws Exception {
        long lecturerID = 1L;

        mockMvc.perform(delete(LECTURER_API_URL + "/{id}", lecturerID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$").doesNotExist());
    }

    private void checkLecturersJSONPath(ResultActions resultActions, List<Lecturer> lecturers) throws Exception {
        for (int i = 0; i < lecturers.size(); i++) {
            Lecturer lecturer = lecturers.get(i);
            String jsonIndexPath = "$._embedded.lecturerList[" + i + "]";

            resultActions
                    .andExpect(jsonPath(jsonIndexPath + ".username", is(lecturer.getUsername())))
                    .andExpect(jsonPath(jsonIndexPath + ".password", is(lecturer.getPassword())))
                    .andExpect(jsonPath(jsonIndexPath + ".lastName", is(lecturer.getLastName())))
                    .andExpect(jsonPath(jsonIndexPath + ".firstName", is(lecturer.getFirstName())))
                    .andExpect(jsonPath(jsonIndexPath + ".email", is(lecturer.getEmail())))
                    .andExpect(jsonPath(jsonIndexPath + ".academicTitle", is(lecturer.getAcademicTitle())));
        }
    }

    private void checkLecturerJSONPath(ResultActions resultActions, Lecturer lecturer) throws Exception {
        resultActions
                .andExpect(jsonPath("$.username", is(lecturer.getUsername())))
                .andExpect(jsonPath("$.password", is(lecturer.getPassword())))
                .andExpect(jsonPath("$.lastName", is(lecturer.getLastName())))
                .andExpect(jsonPath("$.firstName", is(lecturer.getFirstName())))
                .andExpect(jsonPath("$.email", is(lecturer.getEmail())))
                .andExpect(jsonPath("$.academicTitle", is(lecturer.getAcademicTitle())));
    }
}

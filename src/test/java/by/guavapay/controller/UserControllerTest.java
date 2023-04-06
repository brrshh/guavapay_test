package by.guavapay.controller;

import by.guavapay.AbstractWebTestConfiguration;
import by.guavapay.dto.AssignCourierDto;
import by.guavapay.dto.UserCreateDto;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerTest extends AbstractWebTestConfiguration {

    @Test
    @WithMockUser(authorities = "SCOPE_ROLE_ADMIN")
    void createCourierUser() throws Exception {
        var userToCreate = new UserCreateDto("test@email.com", "testName", "testLastName", "password1");

        mockMvc.perform(MockMvcRequestBuilders.post("/user/v1/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userToCreate)))
                .andExpect(status().isCreated());

        Mockito.verify(userService, Mockito.atLeastOnce()).createCourier(userToCreate);
        Mockito.verify(userService, Mockito.never()).createUser(any());
    }

    @Test
    @WithMockUser(authorities = "SCOPE_ROLE_USER")
    void shouldFailedToCreateUserByUser() throws Exception {
        var userToCreate = new UserCreateDto("test@email.com", "testName", "testLastName", "password1");

        mockMvc.perform(MockMvcRequestBuilders.post("/user/v1/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userToCreate)))
                .andExpect(status().isUnauthorized());

        Mockito.verify(userService, Mockito.never()).createUser(any());
        Mockito.verify(userService, Mockito.never()).createCourier(any());
    }

    @Test
    @WithMockUser(authorities = "SCOPE_ROLE_COURIER")
    void shouldFailedToCreateUserByCourier() throws Exception {
        var userToCreate = new UserCreateDto("test@email.com", "testName", "testLastName", "password1");

        mockMvc.perform(MockMvcRequestBuilders.post("/user/v1/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userToCreate)))
                .andExpect(status().isUnauthorized());

        Mockito.verify(userService, Mockito.never()).createUser(any());
        Mockito.verify(userService, Mockito.never()).createCourier(any());
    }

    @Test
    void shouldNotFailedToCreateUserByAnonymous() throws Exception {
        var userToCreate = new UserCreateDto("test@email.com", "testName", "testLastName", "password1");

        mockMvc.perform(MockMvcRequestBuilders.post("/user/v1/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userToCreate)))
                .andExpect(status().isCreated());

        Mockito.verify(userService, Mockito.atLeastOnce()).createUser(userToCreate);
        Mockito.verify(userService, Mockito.never()).createCourier(any());
    }

    @ParameterizedTest
    @ValueSource(classes = {
            MethodArgumentTypeMismatchException.class,
            HttpMessageNotReadableException.class,
            ConstraintViolationException.class,
            EmptyResultDataAccessException.class})
    void shouldHandleExceptionOnCreateUser(Class<? extends Throwable> error) throws Exception {
        var userToCreate = new UserCreateDto("test@email.com", "testName", "testLastName", "password1");

        Mockito.doThrow(error).when(userService).createUser(userToCreate);
        mockMvc.perform(MockMvcRequestBuilders.post("/user/v1/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userToCreate)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"errorDescription\":null}"));
    }

    @Test
    @WithMockUser(authorities = "SCOPE_ROLE_ADMIN")
    void assign() throws Exception {
        var dto = new AssignCourierDto(1L, 2L);
        mockMvc.perform(put("/user/v1/assign")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(dto)))
                .andExpect(status().isOk());

        verify(userService, atLeastOnce()).assignCourier(dto.courierId(), dto.parcelId());
    }

    @Test
    void shouldNotAssignForAnonymous() throws Exception {
        var dto = new AssignCourierDto(1L, 2L);
        mockMvc.perform(put("/user/v1/assign")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(dto)))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(userService);
    }

    @Test
    @WithMockUser(authorities = "SCOPE_ROLE_COURIER")
    void shouldNotAssignForCourier() throws Exception {
        var dto = new AssignCourierDto(1L, 2L);
        mockMvc.perform(put("/user/v1/assign")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(dto)))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(userService);
    }

    @Test
    @WithMockUser(authorities = "SCOPE_ROLE_USER")
    void shouldNotAssignForUser() throws Exception {
        var dto = new AssignCourierDto(1L, 2L);
        mockMvc.perform(put("/user/v1/assign")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(dto)))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(userService);
    }
}
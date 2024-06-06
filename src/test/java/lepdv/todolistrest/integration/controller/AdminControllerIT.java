package lepdv.todolistrest.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lepdv.todolistrest.dto.admin.UserForAdminDto;
import lepdv.todolistrest.dto.admin.UserListDto;
import lepdv.todolistrest.dto.auth.CredentialsDto;
import lepdv.todolistrest.dto.task.ResponseTaskDto;
import lepdv.todolistrest.dto.task.TaskListDto;
import lepdv.todolistrest.entity.User;
import lepdv.todolistrest.exception.NotFoundException;
import lepdv.todolistrest.integration.IT;
import lepdv.todolistrest.mapper.Mapper;
import lepdv.todolistrest.service.AdminService;
import lepdv.todolistrest.service.UserService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;

import java.util.List;
import java.util.stream.Stream;

import static com.atlassian.oai.validator.mockmvc.OpenApiValidationMatchers.openApi;
import static java.util.Collections.emptyList;
import static lepdv.todolistrest.Constants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@IT
@AutoConfigureMockMvc(printOnlyOnFailure = false)
@WithMockUser(username = "Admin", authorities = "ADMIN")
@RequiredArgsConstructor
class AdminControllerIT /*extends IntegrationTestBase*/ {

    private final MockMvc mockMvc;
    private final AdminService adminService;
    private final UserService userService;
    private final Mapper mapper = new Mapper(new ModelMapper());
    private final ObjectMapper jsonMapper = JsonMapper.builder()
            .findAndAddModules()
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            .build();




    @Test
    void getAllUsers_shouldGetREWithUserListDto_whenDefaultParams() throws Exception {
        final List<UserForAdminDto> userList = USER_LIST.stream().map(mapper::mapToUserForAdminDto).toList();
        final UserListDto userListDto = new UserListDto(userList);
        final String jsonUserList = jsonMapper.writeValueAsString(userListDto);
        final RequestBuilder request = get("/api/v2/admin/users");

        mockMvc.perform(request)
                .andExpectAll(
                        openApi().isValid("static/oldOpenapi.yaml"),
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(jsonUserList)
                );
    }

    @Test
    void getAllUsers_shouldGetREWithPaginatedUserListDto_whenCustomParams() throws Exception {
        final List<UserForAdminDto> userList = Stream.of(KATYA).map(mapper::mapToUserForAdminDto).toList();
        final UserListDto userListDto = new UserListDto(userList);
        final String jsonUserList = jsonMapper.writeValueAsString(userListDto);
        final RequestBuilder request = get("/api/v2/admin/users?page=1&size=2");

        mockMvc.perform(request)
                .andExpectAll(
                        openApi().isValid("static/oldOpenapi.yaml"),
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(jsonUserList)
                );
    }

    @Test
    void getAllUsers_shouldGetREWithEmptyUserListDto_whenNoUsersByCustomParams() throws Exception {
        final UserListDto userListDto = new UserListDto(emptyList());
        final String jsonUserList = jsonMapper.writeValueAsString(userListDto);
        final RequestBuilder request = get("/api/v2/admin/users?page=999&size=999");

        mockMvc.perform(request)
                .andExpectAll(
                        openApi().isValid("static/oldOpenapi.yaml"),
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(jsonUserList)
                );
    }


    @Test
    void getUserById_shouldGetREWithUserForAdminDto_whenExist() throws Exception {
        final String jsonUserForAdminDto = jsonMapper.writeValueAsString(USER_FOR_ADMIN_DTO);
        final RequestBuilder request = get("/api/v2/admin/users/" + USER.getId());

        mockMvc.perform(request)
                .andExpectAll(
                        openApi().isValid("static/oldOpenapi.yaml"),
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(jsonUserForAdminDto)
                );
    }

    @Test
    void getUserById_shouldGetREWithErrorsBody_whenNotExist() throws Exception {
        final RequestBuilder request = get("/api/v2/admin/users/999");

        mockMvc.perform(request)
                .andExpectAll(
                        openApi().isValid("static/oldOpenapi.yaml"),
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(getUserNotFoundErrorsBody())
                );
    }



    @Test
    void lockUserById_shouldGetREWithLockedUserString_whenExist() throws Exception {
        final String expectedString = "User id=" + USER.getId() + " was locked";
        final RequestBuilder request = put("/api/v2/admin/users/" + USER.getId() + "/lock");

        mockMvc.perform(request)
                .andExpectAll(
                        openApi().isValid("static/oldOpenapi.yaml"),
                        status().isOk(),
                        content().contentType(MediaType.TEXT_PLAIN),
                        content().bytes(expectedString.getBytes())
                );
        User user = userService.getUser(USER.getId());
        assertFalse(user.getIsNonLocked());
    }

    @Test
    void lockUserById_shouldGetREWithErrorsBody_whenNotExist() throws Exception {
        final RequestBuilder request = put("/api/v2/admin/users/999/lock");

        mockMvc.perform(request)
                .andExpectAll(
                        openApi().isValid("static/oldOpenapi.yaml"),
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(getUserNotFoundErrorsBody())
                );
    }

    @Test
    void lockUserById_shouldGetREWithErrorsBody_whenLockedUserIsAdmin() throws Exception {
        final RequestBuilder request = put("/api/v2/admin/users/" + ADMIN.getId() + "/lock");

        mockMvc.perform(request)
                .andExpectAll(
                        openApi().isValid("static/oldOpenapi.yaml"),
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json("""
                                {
                                  "status": 400,
                                    "errors": ["You can't lock Administrator"],
                                    "type": "UnitedException",
                                    "path": "uri=/api/v2/admin/users/1/lock",
                                    "message": "Invalid request"
                                }""")
                );
        User user = userService.getUser(ADMIN.getId());
        assertTrue(user.getIsNonLocked());
    }


    @Test
    void unlockUserById_shouldGetREWithUnlockedUserString_whenExist() throws Exception {
        final String expectedString = "User id=" + USER.getId() + " was unlocked";
        final RequestBuilder request = put("/api/v2/admin/users/" + USER.getId() + "/unlock");
        adminService.lockUser(USER.getId());

        mockMvc.perform(request)
                .andExpectAll(
                        openApi().isValid("static/oldOpenapi.yaml"),
                        status().isOk(),
                        content().contentType(MediaType.TEXT_PLAIN),
                        content().bytes(expectedString.getBytes())
                );
        User user = userService.getUser(USER.getId());
        assertTrue(user.getIsNonLocked());
    }

    @Test
    void unlockUserById_shouldGetREWithErrorsBody_whenNotExist() throws Exception {
        final RequestBuilder request = put("/api/v2/admin/users/999/lock");

        mockMvc.perform(request)
                .andExpectAll(
                        openApi().isValid("static/oldOpenapi.yaml"),
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(getUserNotFoundErrorsBody())
                );
    }


    @Test
    void deleteUserById_shouldGetREWithDeletedUserString_whenUserExist() throws Exception {
        final CredentialsDto credentialsDto = new CredentialsDto("Admin", "Admin");
        final String jsonCredentialsDto = jsonMapper.writeValueAsString(credentialsDto);
        final String expectedString = "User id=" + USER.getId() + " was deleted";
        final RequestBuilder request = delete("/api/v2/admin/users/" + USER.getId() + "/delete")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonCredentialsDto);

        mockMvc.perform(request)
                .andExpectAll(
                        openApi().isValid("static/oldOpenapi.yaml"),
                        status().isOk(),
                        content().contentType(MediaType.TEXT_PLAIN),
                        content().bytes(expectedString.getBytes())
                );
        assertThrows(NotFoundException.class, () -> userService.getUser(USER.getId()));
    }

    @Test
    void deleteUserById_shouldGetREWithErrorsBody_whenUserNotExist() throws Exception {
        final CredentialsDto credentialsDto = new CredentialsDto("Admin", "Admin");
        final String jsonCredentialsDto = jsonMapper.writeValueAsString(credentialsDto);
        final RequestBuilder request = delete("/api/v2/admin/users/999/delete")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonCredentialsDto);

        mockMvc.perform(request)
                .andExpectAll(
                        openApi().isValid("static/oldOpenapi.yaml"),
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(getUserNotFoundErrorsBody())
                );
    }

    @Test
    void deleteUserById_shouldGetREWithErrorsBody_whenUserIsAdmin() throws Exception {
        final CredentialsDto credentialsDto = new CredentialsDto("Admin", "Admin");
        final String jsonCredentialsDto = jsonMapper.writeValueAsString(credentialsDto);
        final RequestBuilder request = delete("/api/v2/admin/users/" + ADMIN.getId() + "/delete")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonCredentialsDto);

        mockMvc.perform(request)
                .andExpectAll(
                        openApi().isValid("static/oldOpenapi.yaml"),
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json("""
                                {
                                  "status": 400,
                                    "errors": ["You can't delete Administrator"],
                                    "type": "UnitedException",
                                    "path": "uri=/api/v2/admin/users/1/delete",
                                    "message": "Invalid request"
                                }""")
                );
        User user = userService.getUser(ADMIN.getId());
        assertNotNull(user);
    }

    @Test
    void deleteUserById_shouldGetREWithErrorsBody_whenIncorrectCredentials() throws Exception {
        final CredentialsDto credentialsDto = new CredentialsDto("Admin", "dummy");
        final String jsonCredentialsDto = jsonMapper.writeValueAsString(credentialsDto);
        final RequestBuilder request = delete("/api/v2/admin/users/" + USER.getId() + "/delete")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonCredentialsDto);

        mockMvc.perform(request)
                .andExpectAll(
                        openApi().isValid("static/oldOpenapi.yaml"),
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json("""
                                {
                                  "status": 400,
                                    "errors": ["Incorrect credentials"],
                                    "type": "UnitedException",
                                    "path": "uri=/api/v2/admin/users/2/delete",
                                    "message": "Invalid request"
                                }""")
                );
        User user = userService.getUser(USER.getId());
        assertNotNull(user);
    }


    @Test
    void getAllTasks_shouldGetREWithTaskListDto_whenDefaultParams() throws Exception {
        final List<ResponseTaskDto> responseTaskDtoList = TASK_LIST.stream().map(mapper::mapToResponseTaskDto).toList();
        final TaskListDto taskListDto = new TaskListDto(responseTaskDtoList);
        final String jsonTaskListDto = jsonMapper.writeValueAsString(taskListDto);
        final RequestBuilder request = get("/api/v2/admin/tasks");

        mockMvc.perform(request)
                .andExpectAll(
                        openApi().isValid("static/oldOpenapi.yaml"),
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(jsonTaskListDto)
                );
    }

    @Test
    void getAllTasks_shouldGetREWithPaginatedTaskListDto_whenCustomParams() throws Exception {
        final List<ResponseTaskDto> responseTaskDtoList = Stream.of(TASK_3, TASK_4).map(mapper::mapToResponseTaskDto).toList();
        final TaskListDto taskListDto = new TaskListDto(responseTaskDtoList);
        final String jsonTaskListDto = jsonMapper.writeValueAsString(taskListDto);
        final RequestBuilder request = get("/api/v2/admin/tasks?page=1&size=2");

        mockMvc.perform(request)
                .andExpectAll(
                        openApi().isValid("static/oldOpenapi.yaml"),
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(jsonTaskListDto)
                );
    }

    @Test
    void getAllTasks_shouldGetREWithEmptyTaskListDto_whenNoTasksByCustomParams() throws Exception {
        final TaskListDto taskListDto = new TaskListDto(emptyList());
        final String jsonTaskListDto = jsonMapper.writeValueAsString(taskListDto);
        final RequestBuilder request = get("/api/v2/admin/tasks?page=999&size=999");

        mockMvc.perform(request)
                .andExpectAll(
                        openApi().isValid("static/oldOpenapi.yaml"),
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(jsonTaskListDto)
                );
    }

    @Test
    void getAllTasks_shouldGetREWithEmptyTaskListDto_whenNotExist() throws Exception {
        final TaskListDto taskListDto = new TaskListDto(emptyList());
        final String jsonTaskListDto = jsonMapper.writeValueAsString(taskListDto);
        final RequestBuilder request = get("/api/v2/admin/tasks?page=0&size=20");
        userService.deleteUserById(USER.getId());
        userService.deleteUserById(KATYA.getId());

        mockMvc.perform(request)
                .andExpectAll(
                        openApi().isValid("static/oldOpenapi.yaml"),
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(jsonTaskListDto)
                );
    }


    @Test
    void getTaskById_shouldGetREWithResponseTaskDto_whenExist() throws Exception {
        final String jsonResponseTaskDto = jsonMapper.writeValueAsString(RESPONSE_TASK_DTO);
        final RequestBuilder request = get("/api/v2/admin/tasks/" + TASK.getId());

        mockMvc.perform(request)
                .andExpectAll(
                        openApi().isValid("static/oldOpenapi.yaml"),
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(jsonResponseTaskDto)
                );
    }

    @Test
    void getTaskById_shouldGetREWithErrorsBody_whenNotExist() throws Exception {
        final RequestBuilder request = get("/api/v2/admin/tasks/999");

        mockMvc.perform(request)
                .andExpectAll(
                        openApi().isValid("static/oldOpenapi.yaml"),
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json("""
                                {
                                  "status": 404,
                                  "errors": ["There is no task with id=999 in database"],
                                  "type": "NotFoundException",
                                  "path": "uri=/api/v2/admin/tasks/999",
                                  "message": "Not Found"
                                }""")
                );
    }



    @NotNull
    private String getUserNotFoundErrorsBody() {
        return """
                {
                  "status": 404,
                  "errors": ["There is no user with id=999 in database"],
                  "type": "NotFoundException",
                  "message": "Not Found"
                }""";
    }


}











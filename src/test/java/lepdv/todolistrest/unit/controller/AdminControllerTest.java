package lepdv.todolistrest.unit.controller;

import lepdv.todolistrest.controller.AdminController;
import lepdv.todolistrest.dto.admin.UserForAdminDto;
import lepdv.todolistrest.dto.admin.UserListDto;
import lepdv.todolistrest.dto.auth.CredentialsDto;
import lepdv.todolistrest.dto.task.ResponseTaskDto;
import lepdv.todolistrest.dto.task.TaskListDto;
import lepdv.todolistrest.mapper.Mapper;
import lepdv.todolistrest.service.AdminService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static lepdv.todolistrest.Constants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class AdminControllerTest {

    @Mock
    private AdminService adminService;
    @InjectMocks
    private AdminController adminController;
    private final Mapper mapper = new Mapper(new ModelMapper());




    @Test
    void getAllUsers_shouldGetREWithUserListDto_whenDefaultParams() {
        final List<UserForAdminDto> userList = USER_LIST.stream().map(mapper::mapToUserForAdminDto).toList();
        final UserListDto userListDto = new UserListDto(userList);
        doReturn(userListDto).when(adminService).getAllUsers(0, 20);

        ResponseEntity<UserListDto> actualResult = adminController.getAllUsers(0, 20);

        verify(adminService).getAllUsers(0, 20);
        assertNotNull(actualResult);
        assertEquals(HttpStatus.OK, actualResult.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, actualResult.getHeaders().getContentType());
        assertEquals(userListDto, actualResult.getBody());
    }

    @Test
    void getAllUsers_shouldGetREWithPaginatedUserListDto_whenCustomParams() {
        final List<UserForAdminDto> userList = Stream.of(KATYA).map(mapper::mapToUserForAdminDto).toList();
        final UserListDto userListDto = new UserListDto(userList);
        doReturn(userListDto).when(adminService).getAllUsers(1, 2);

        ResponseEntity<UserListDto> actualResult = adminController.getAllUsers(1, 2);

        verify(adminService).getAllUsers(1, 2);
        assertNotNull(actualResult);
        assertEquals(HttpStatus.OK, actualResult.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, actualResult.getHeaders().getContentType());
        assertEquals(userListDto, actualResult.getBody());
    }

    @Test
    void getAllUsers_shouldGetREWithEmptyUserListDto_whenNoUsersByCustomParams() {
        final UserListDto userListDto = new UserListDto(emptyList());
        doReturn(userListDto).when(adminService).getAllUsers(999, 999);

        ResponseEntity<UserListDto> actualResult = adminController.getAllUsers(999, 999);

        verify(adminService).getAllUsers(999, 999);
        assertNotNull(actualResult);
        assertEquals(HttpStatus.OK, actualResult.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, actualResult.getHeaders().getContentType());
        assertTrue(actualResult.getBody().getUserList().isEmpty());
        assertEquals(userListDto, actualResult.getBody());
    }



    @Test
    void getUserById_shouldGetREWithUserForAdminDto() {
        doReturn(USER_FOR_ADMIN_DTO).when(adminService).getUserForAdminDto(USER.getId());

        ResponseEntity<?> actualResult = adminController.getUserById(USER.getId());

        verify(adminService).getUserForAdminDto(USER.getId());
        assertNotNull(actualResult);
        assertEquals(HttpStatus.OK, actualResult.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, actualResult.getHeaders().getContentType());
        assertEquals(USER_FOR_ADMIN_DTO, actualResult.getBody());
    }



    @Test
    void lockUserById_shouldGetREWithLockedUserString() {
        final String expectedString = "User id=" + USER.getId() + " was locked";
        doNothing().when(adminService).lockUser(USER.getId());

        ResponseEntity<String> actualResult = adminController.lockUserById(USER.getId());

        verify(adminService).lockUser(USER.getId());
        assertNotNull(actualResult);
        assertEquals(HttpStatus.OK, actualResult.getStatusCode());
        assertEquals(MediaType.TEXT_PLAIN, actualResult.getHeaders().getContentType());
        assertEquals(expectedString, actualResult.getBody());
    }



    @Test
    void unlockUserById_shouldGetREWithUnlockedUserString() {
        final String expectedString = "User id=" + USER.getId() + " was unlocked";
        doNothing().when(adminService).unlockUser(USER.getId());

        ResponseEntity<String> actualResult = adminController.unlockUserById(USER.getId());

        verify(adminService).unlockUser(USER.getId());
        assertNotNull(actualResult);
        assertEquals(HttpStatus.OK, actualResult.getStatusCode());
        assertEquals(MediaType.TEXT_PLAIN, actualResult.getHeaders().getContentType());
        assertEquals(expectedString, actualResult.getBody());
    }



    @Test
    void deleteUserById_shouldGetREWithDeletedUserString() {
        final CredentialsDto credentialsDto = new CredentialsDto("Admin", "Admin");
        final String expectedString = "User id=" + USER.getId() + " was deleted";
        doNothing().when(adminService).deleteUser(credentialsDto, USER.getId());

        ResponseEntity<String> actualResult = adminController.deleteUserById(credentialsDto, USER.getId());

        verify(adminService).deleteUser(credentialsDto, USER.getId());
        assertNotNull(actualResult);
        assertEquals(HttpStatus.OK, actualResult.getStatusCode());
        assertEquals(MediaType.TEXT_PLAIN, actualResult.getHeaders().getContentType());
        assertEquals(expectedString, actualResult.getBody());
    }



    @Test
    void getAllTasks_shouldGetREWithTaskListDto_whenDefaultParams() {
        final List<ResponseTaskDto> responseTaskDtoList = TASK_LIST.stream().map(mapper::mapToResponseTaskDto).toList();
        final TaskListDto taskListDto = new TaskListDto(responseTaskDtoList);
        doReturn(taskListDto).when(adminService).getAllTasks(0, 20);

        ResponseEntity<TaskListDto> actualResult = adminController.getAllTasks(0, 20);

        verify(adminService).getAllTasks(0, 20);
        assertNotNull(actualResult);
        assertEquals(HttpStatus.OK, actualResult.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, actualResult.getHeaders().getContentType());
        assertEquals(taskListDto, actualResult.getBody());
    }

    @Test
    void getAllTasks_shouldGetREWithPaginatedTaskListDto_whenCustomParams() {
        final List<ResponseTaskDto> responseTaskDtoList = Stream.of(TASK_3, TASK_4).map(mapper::mapToResponseTaskDto).toList();
        final TaskListDto taskListDto = new TaskListDto(responseTaskDtoList);
        doReturn(taskListDto).when(adminService).getAllTasks(1, 2);

        ResponseEntity<TaskListDto> actualResult = adminController.getAllTasks(1, 2);

        verify(adminService).getAllTasks(1, 2);
        assertNotNull(actualResult);
        assertEquals(HttpStatus.OK, actualResult.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, actualResult.getHeaders().getContentType());
        assertEquals(taskListDto, actualResult.getBody());
    }

    @Test
    void getAllTasks_shouldGetREWithEmptyTaskListDto_whenNoTasksByCustomParams() {
        final TaskListDto taskListDto = new TaskListDto(emptyList());
        doReturn(taskListDto).when(adminService).getAllTasks(999, 999);

        ResponseEntity<TaskListDto> actualResult = adminController.getAllTasks(999, 999);

        verify(adminService).getAllTasks(999, 999);
        assertNotNull(actualResult);
        assertEquals(HttpStatus.OK, actualResult.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, actualResult.getHeaders().getContentType());
        assertTrue(actualResult.getBody().getTaskList().isEmpty());
        assertEquals(taskListDto, actualResult.getBody());
    }

    @Test
    void getAllTasks_shouldGetREWithEmptyTaskListDto_whenNotExist() {
        final TaskListDto taskListDto = new TaskListDto(emptyList());
        doReturn(taskListDto).when(adminService).getAllTasks(0, 20);

        ResponseEntity<TaskListDto> actualResult = adminController.getAllTasks(0, 20);

        verify(adminService).getAllTasks(0, 20);
        assertNotNull(actualResult);
        assertEquals(HttpStatus.OK, actualResult.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, actualResult.getHeaders().getContentType());
        assertTrue(actualResult.getBody().getTaskList().isEmpty());
        assertEquals(taskListDto, actualResult.getBody());
    }



    @Test
    void getTaskById_shouldGetREWithResponseTaskDto() {
        doReturn(RESPONSE_TASK_DTO).when(adminService).getTaskById(TASK.getId());

        ResponseEntity<ResponseTaskDto> actualResult = adminController.getTaskById(TASK.getId());

        verify(adminService).getTaskById(TASK.getId());
        assertNotNull(actualResult);
        assertEquals(HttpStatus.OK, actualResult.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, actualResult.getHeaders().getContentType());
        assertEquals(RESPONSE_TASK_DTO, actualResult.getBody());

    }



}











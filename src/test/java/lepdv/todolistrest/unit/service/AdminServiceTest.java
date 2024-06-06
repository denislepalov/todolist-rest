package lepdv.todolistrest.unit.service;

import lepdv.todolistrest.dto.admin.UserForAdminDto;
import lepdv.todolistrest.dto.admin.UserListDto;
import lepdv.todolistrest.dto.auth.CredentialsDto;
import lepdv.todolistrest.dto.task.ResponseTaskDto;
import lepdv.todolistrest.dto.task.TaskListDto;
import lepdv.todolistrest.entity.Task;
import lepdv.todolistrest.entity.User;
import lepdv.todolistrest.exception.NotFoundException;
import lepdv.todolistrest.exception.UnitedException;
import lepdv.todolistrest.mapper.Mapper;
import lepdv.todolistrest.service.AdminService;
import lepdv.todolistrest.service.TaskService;
import lepdv.todolistrest.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static java.util.Collections.emptyList;
import static lepdv.todolistrest.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private UserService userService;
    @Mock
    private TaskService taskService;
    @Mock
    private Mapper mapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private AdminService adminService;




    @Test
    void getAllUsers_shouldGetUserListDto_whenDefaultArguments() {
        final Pageable pageable = PageRequest.of(0, 20);
        doReturn(USER_LIST).when(userService).getAllByPageable(pageable);
        doReturn(USER_FOR_ADMIN_DTO).when(mapper).mapToUserForAdminDto(any(User.class));

        UserListDto actualResult = adminService.getAllUsers(0, 20);

        verify(userService).getAllByPageable(pageable);
        verify(mapper, times(3)).mapToUserForAdminDto(any(User.class));
        assertFalse(actualResult.getUserList().isEmpty());
        assertThat(actualResult.getUserList()).hasSize(3);
    }

    @Test
    void getAllUsers_shouldGetPaginatedUserListDto_whenCustomArguments() {
        final Pageable pageable = PageRequest.of(1, 2);
        doReturn(List.of(KATYA)).when(userService).getAllByPageable(pageable);
        doReturn(USER_FOR_ADMIN_DTO).when(mapper).mapToUserForAdminDto(any(User.class));

        UserListDto actualResult = adminService.getAllUsers(1, 2);

        verify(userService).getAllByPageable(pageable);
        verify(mapper).mapToUserForAdminDto(any(User.class));
        assertFalse(actualResult.getUserList().isEmpty());
        assertThat(actualResult.getUserList()).hasSize(1);
    }

    @Test
    void getAllUsers_shouldGetEmptyUserListDto_whenNoUsersByCustomArguments() {
        final Pageable pageable = PageRequest.of(999, 999);
        doReturn(emptyList()).when(userService).getAllByPageable(pageable);

        UserListDto actualResult = adminService.getAllUsers(999, 999);

        verify(userService).getAllByPageable(pageable);
        verifyNoInteractions(mapper);
        assertTrue(actualResult.getUserList().isEmpty());
    }



    @Test
    void getUserForAdminDto_shouldGetUserForAdminDto_whenExist() {
        doReturn(USER).when(userService).getUser(USER.getId());
        doReturn(USER_FOR_ADMIN_DTO).when(mapper).mapToUserForAdminDto(USER);

        UserForAdminDto actualResult = adminService.getUserForAdminDto(USER.getId());

        verify(userService).getUser(USER.getId());
        verify(mapper).mapToUserForAdminDto(USER);
        assertEquals(USER_FOR_ADMIN_DTO, actualResult);
    }

    @Test
    void getUserForAdminDto_shouldThrowException_whenNotExist() {
        doThrow(NotFoundException.class).when(userService).getUser(999L);

        assertThrows(NotFoundException.class, () -> adminService.getUserForAdminDto(999L));
        verify(userService).getUser(999L);
        verifyNoInteractions(mapper);
    }



    @Test
    void lockUser_shouldLockUser_whenExist() {
        final User user = USER.clone();
        doReturn(user).when(userService).getUser(user.getId());

        adminService.lockUser(user.getId());

        verify(userService).getUser(user.getId());
    }

    @Test
    void lockUser_shouldThrowException_whenNotExist() {
        doThrow(NotFoundException.class).when(userService).getUser(999L);

        assertThrows(NotFoundException.class, () -> adminService.lockUser(999L));
        verify(userService).getUser(999L);
    }

    @Test
    void lockUser_shouldThrowException_whenLockedUserIsAdmin() {
        doReturn(ADMIN).when(userService).getUser(ADMIN.getId());

        assertThrows(UnitedException.class, () -> adminService.lockUser(ADMIN.getId()));
        verify(userService).getUser(ADMIN.getId());
    }



    @Test
    void unlockUser_shouldUnlockUser_whenExist() {
        doReturn(USER).when(userService).getUser(USER.getId());

        adminService.unlockUser(USER.getId());

        verify(userService).getUser(USER.getId());
    }

    @Test
    void unlockUser_shouldThrowException_whenNotExist() {
        doThrow(NotFoundException.class).when(userService).getUser(999L);

        assertThrows(NotFoundException.class, () -> adminService.unlockUser(999L));
        verify(userService).getUser(999L);
    }



    @Test
    void deleteUser_shouldDeleteUser_whenUserExist() {
        final CredentialsDto credentialsDto = new CredentialsDto("Admin", "Admin");
        doReturn(ADMIN).when(userService).getAuthUser();
        doReturn(USER).when(userService).getUser(USER.getId());
        doReturn(true).when(passwordEncoder).matches(credentialsDto.getPassword(), ADMIN.getPassword());
        doNothing().when(userService).deleteUserById(USER.getId());

        adminService.deleteUser(credentialsDto, USER.getId());

        verify(userService).getAuthUser();
        verify(userService).getUser(USER.getId());
        verify(passwordEncoder).matches(credentialsDto.getPassword(), ADMIN.getPassword());
        verify(userService).deleteUserById(USER.getId());
    }

    @Test
    void deleteUser_shouldThrowException_whenUserNotExist() {
        final CredentialsDto credentialsDto = new CredentialsDto("Admin", "Admin");
        doReturn(ADMIN).when(userService).getAuthUser();
        doThrow(NotFoundException.class).when(userService).getUser(999L);

        assertThrows(NotFoundException.class, () -> adminService.deleteUser(credentialsDto, 999L));
        verify(userService).getAuthUser();
        verify(userService).getUser(999L);
    }

    @Test
    void deleteUser_shouldThrowException_whenUserIsAdmin() {
        final CredentialsDto credentialsDto = new CredentialsDto("Admin", "Admin");
        doReturn(ADMIN).when(userService).getAuthUser();
        doReturn(ADMIN).when(userService).getUser(ADMIN.getId());
        doReturn(true).when(passwordEncoder).matches(credentialsDto.getPassword(), ADMIN.getPassword());

        assertThrows(UnitedException.class, () -> adminService.deleteUser(credentialsDto, ADMIN.getId()));
        verify(userService).getAuthUser();
        verify(userService).getUser(ADMIN.getId());
        verify(passwordEncoder).matches(credentialsDto.getPassword(), ADMIN.getPassword());
    }

    @Test
    void deleteUser_shouldThrowException_whenIncorrectCredentials() {
        final CredentialsDto credentialsDto = new CredentialsDto("Admin", "dummy");
        doReturn(ADMIN).when(userService).getAuthUser();
        doReturn(USER).when(userService).getUser(USER.getId());
        doReturn(false).when(passwordEncoder).matches(credentialsDto.getPassword(), ADMIN.getPassword());

        assertThrows(UnitedException.class, () -> adminService.deleteUser(credentialsDto, USER.getId()));
        verify(userService).getAuthUser();
        verify(userService).getUser(USER.getId());
        verify(passwordEncoder).matches(credentialsDto.getPassword(), ADMIN.getPassword());
    }



    @Test
    void getAllTasks_shouldGetTaskListDto_whenDefaultArguments() {
        final Pageable pageable = PageRequest.of(0, 20);
        doReturn(TASK_LIST).when(taskService).getAllByPageable(pageable);
        doReturn(RESPONSE_TASK_DTO).when(mapper).mapToResponseTaskDto(any(Task.class));

        TaskListDto actualResult = adminService.getAllTasks(0, 20);

        verify(taskService).getAllByPageable(pageable);
        verify(mapper, times(6)).mapToResponseTaskDto(any(Task.class));
        assertFalse(actualResult.getTaskList().isEmpty());
        assertThat(actualResult.getTaskList()).hasSize(6);
    }

    @Test
    void getAllTasks_shouldGetPaginatedTaskListDto_whenCustomArguments() {
        final Pageable pageable = PageRequest.of(1, 2);
        doReturn(List.of(TASK_3, TASK_4)).when(taskService).getAllByPageable(pageable);
        doReturn(RESPONSE_TASK_DTO).when(mapper).mapToResponseTaskDto(any(Task.class));

        TaskListDto actualResult = adminService.getAllTasks(1, 2);

        verify(taskService).getAllByPageable(pageable);
        verify(mapper, times(2)).mapToResponseTaskDto(any(Task.class));
        assertFalse(actualResult.getTaskList().isEmpty());
        assertThat(actualResult.getTaskList()).hasSize(2);
    }

    @Test
    void getAllTasks_shouldGetEmptyTaskListDto_whenNoTasksByCustomArguments() {
        final Pageable pageable = PageRequest.of(999, 999);
        doReturn(emptyList()).when(taskService).getAllByPageable(pageable);

        TaskListDto actualResult = adminService.getAllTasks(999, 999);

        verify(taskService).getAllByPageable(pageable);
        verifyNoInteractions(mapper);
        assertTrue(actualResult.getTaskList().isEmpty());
    }

    @Test
    void getAllTasks_shouldGetEmptyTaskListDto_whenNotExist() {
        final Pageable pageable = PageRequest.of(0, 20);
        doReturn(emptyList()).when(taskService).getAllByPageable(pageable);

        TaskListDto actualResult = adminService.getAllTasks(0, 20);

        verify(taskService).getAllByPageable(pageable);
        verifyNoInteractions(mapper);
        assertTrue(actualResult.getTaskList().isEmpty());
    }



    @Test
    void getTaskById_shouldGetResponseTaskDto_whenExist() {
        doReturn(RESPONSE_TASK_DTO).when(taskService).getTaskDtoById(TASK.getId());

        ResponseTaskDto actualResult = adminService.getTaskById(TASK.getId());

        verify(taskService).getTaskDtoById(TASK.getId());
        assertEquals(RESPONSE_TASK_DTO, actualResult);
    }

    @Test
    void getTaskById_shouldThrowException_whenNotExist() {
        doThrow(NotFoundException.class).when(taskService).getTaskDtoById(999L);

        assertThrows(NotFoundException.class, () -> adminService.getTaskById(999L));
        verify(taskService).getTaskDtoById(999L);
    }


}
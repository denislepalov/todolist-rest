package lepdv.todolistrest.integration.service;

import lepdv.todolistrest.integration.IT;
import lepdv.todolistrest.dto.admin.UserForAdminDto;
import lepdv.todolistrest.dto.admin.UserListDto;
import lepdv.todolistrest.dto.auth.CredentialsDto;
import lepdv.todolistrest.dto.task.ResponseTaskDto;
import lepdv.todolistrest.dto.task.TaskListDto;
import lepdv.todolistrest.entity.User;
import lepdv.todolistrest.exception.NotFoundException;
import lepdv.todolistrest.exception.UnitedException;
import lepdv.todolistrest.mapper.Mapper;
import lepdv.todolistrest.service.AdminService;
import lepdv.todolistrest.service.UserService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.List;
import java.util.stream.Stream;

import static lepdv.todolistrest.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


@IT
@WithMockUser(username = "Admin", authorities = "ADMIN")
@RequiredArgsConstructor
class AdminServiceIT /*extends IntegrationTestBase*/ {

    private final UserService userService;
    private final Mapper mapper;
    private final AdminService adminService;




    @Test
    void getAllUsers_shouldGetUserList_whenDefaultArguments() {
        final List<UserForAdminDto> expectedResult = USER_LIST.stream()
                .map(mapper::mapToUserForAdminDto)
                .toList();

        UserListDto actualResult = adminService.getAllUsers(0, 20);

        assertFalse(actualResult.getUserList().isEmpty());
        assertThat(actualResult.getUserList()).hasSize(3);
        assertEquals(expectedResult, actualResult.getUserList());
    }

    @Test
    void getAllUsers_shouldGetPaginatedUserList_whenCustomArguments() {
        final List<UserForAdminDto> expectedResult = Stream.of(KATYA)
                .map(mapper::mapToUserForAdminDto)
                .toList();

        UserListDto actualResult = adminService.getAllUsers(1, 2);

        assertFalse(actualResult.getUserList().isEmpty());
        assertThat(actualResult.getUserList()).hasSize(1);
        assertEquals(expectedResult, actualResult.getUserList());
    }

    @Test
    void getAllUsers_shouldGetEmptyUserList_whenNoUsersByCustomArguments() {
        UserListDto actualResult = adminService.getAllUsers(999, 999);

        assertTrue(actualResult.getUserList().isEmpty());
    }



    @Test
    void getUserForAdminDto_shouldGetUserForAdminDto_whenExist() {
        UserForAdminDto actualResult = adminService.getUserForAdminDto(USER.getId());

        assertEquals(USER_FOR_ADMIN_DTO, actualResult);
    }

    @Test
    void getUserForAdminDto_shouldThrowException_whenNotExist() {
        assertThrows(NotFoundException.class, () -> adminService.getUserForAdminDto(999L));
    }



    @Test
    void lockUser_shouldLockUser_whenExist() {
        adminService.lockUser(USER.getId());

        final User lockedUser = userService.getUser(USER.getId());
        assertFalse(lockedUser.getIsNonLocked());
    }

    @Test
    void lockUser_shouldThrowException_whenNotExist() {
        assertThrows(NotFoundException.class, () -> adminService.lockUser(999L));
    }

    @Test
    void lockUser_shouldThrowException_whenLockedUserIsAdmin() {
        assertThrows(UnitedException.class, () -> adminService.lockUser(ADMIN.getId()));
    }



    @Test
    void unlockUser_shouldUnlockUser_whenExist() {
        adminService.lockUser(USER.getId());

        adminService.unlockUser(USER.getId());

        final User unlockUser = userService.getUser(USER.getId());
        assertTrue(unlockUser.getIsNonLocked());
    }

    @Test
    void unlockUser_shouldThrowException_whenNotExist() {
        assertThrows(NotFoundException.class, () -> adminService.unlockUser(999L));
    }



    @Test
    void deleteUser_shouldDeleteUser_whenUserExist() {
        final CredentialsDto credentialsDto = new CredentialsDto("Admin", "Admin");

        adminService.deleteUser(credentialsDto, USER.getId());

        final UserListDto allUsers = adminService.getAllUsers(0, 20);
        assertThat(allUsers.getUserList()).hasSize(2);
        assertThrows(NotFoundException.class, () -> userService.getUser(USER.getId()));
    }

    @Test
    void deleteUser_shouldThrowException_whenUserNotExist() {
        final CredentialsDto credentialsDto = new CredentialsDto("Admin", "Admin");

        assertThrows(NotFoundException.class, () -> adminService.deleteUser(credentialsDto, 999L));
    }

    @Test
    void deleteUser_shouldThrowException_whenUserIsAdmin() {
        final CredentialsDto credentialsDto = new CredentialsDto("Admin", "Admin");

        assertThrows(UnitedException.class, () -> adminService.deleteUser(credentialsDto, ADMIN.getId()));
    }

    @Test
    void deleteUser_shouldThrowException_whenIncorrectCredentials() {
        final CredentialsDto credentialsDto = new CredentialsDto("Admin", "dummy");

        assertThrows(UnitedException.class, () -> adminService.deleteUser(credentialsDto, USER.getId()));
    }



    @Test
    void getAllTasks_shouldGetTaskList_whenDefaultArguments() {
        final List<ResponseTaskDto> expectedResult = TASK_LIST.stream()
                .map(mapper::mapToResponseTaskDto)
                .toList();

        TaskListDto actualResult = adminService.getAllTasks(0, 20);

        assertFalse(actualResult.getTaskList().isEmpty());
        assertThat(actualResult.getTaskList()).hasSize(6);
        assertEquals(expectedResult, actualResult.getTaskList());
    }

    @Test
    void getAllTasks_shouldGetPaginatedTaskList_whenCustomArguments() {
        final List<ResponseTaskDto> expectedResult = Stream.of(TASK_3, TASK_4)
                .map(mapper::mapToResponseTaskDto)
                .toList();

        TaskListDto actualResult = adminService.getAllTasks(1, 2);

        assertFalse(actualResult.getTaskList().isEmpty());
        assertThat(actualResult.getTaskList()).hasSize(2);
        assertEquals(expectedResult, actualResult.getTaskList());
    }

    @Test
    void getAllTasks_shouldGetEmptyTaskList_whenNoTasksByCustomArguments() {
        TaskListDto actualResult = adminService.getAllTasks(999, 999);

        assertTrue(actualResult.getTaskList().isEmpty());
    }

    @Test
    void getAllTasks_shouldGetEmptyTaskList_whenNotExist() {
        userService.deleteUserById(USER.getId());
        userService.deleteUserById(KATYA.getId());

        TaskListDto actualResult = adminService.getAllTasks(0, 20);

        assertTrue(actualResult.getTaskList().isEmpty());
    }



    @Test
    void getTaskById_shouldGetResponseTaskDto_whenExist() {
        ResponseTaskDto actualResult = adminService.getTaskById(TASK.getId());

        assertEquals(RESPONSE_TASK_DTO, actualResult);
    }

    @Test
    void getTaskById_shouldThrowException_whenNotExist() {
        assertThrows(NotFoundException.class, () -> adminService.getTaskById(999L));
    }



}















package lepdv.todolistrest.unit.service;

import lepdv.todolistrest.dto.task.ResponseTaskDto;
import lepdv.todolistrest.dto.task.TaskListDto;
import lepdv.todolistrest.entity.Task;
import lepdv.todolistrest.exception.NotFoundException;
import lepdv.todolistrest.exception.UnitedException;
import lepdv.todolistrest.mapper.Mapper;
import lepdv.todolistrest.repository.TaskRepository;
import lepdv.todolistrest.service.TaskService;
import lepdv.todolistrest.service.UserService;
import lepdv.todolistrest.util.AuthUser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static lepdv.todolistrest.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;
    @Mock
    private UserService userService;
    @Mock
    private Mapper mapper;
    @InjectMocks
    private TaskService taskService;




    @Test
    void create_shouldCreateNewTask() {
        final Task task = Task.builder()
                .description("Ivan task4")
                .dueDate(LocalDate.of(2025, Month.MAY, 11))
                .build();
        doReturn(USER).when(userService).getAuthUser();
        doReturn(task).when(mapper).mapToTask(CREATE_TASK_DTO);
        doReturn(TASK).when(taskRepository).save(task);
        doReturn(RESPONSE_TASK_DTO).when(mapper).mapToResponseTaskDto(TASK);

        ResponseTaskDto actualResult = taskService.create(CREATE_TASK_DTO);

        verify(userService).getAuthUser();
        verify(mapper).mapToTask(CREATE_TASK_DTO);
        verify(taskRepository).save(task);
        verify(mapper).mapToResponseTaskDto(TASK);
        assertEquals(RESPONSE_TASK_DTO, actualResult);
    }



    @Test
    void getAllByPageable_shouldGetTaskList_whenDefaultArguments() {
        final Pageable pageable = PageRequest.of(0, 20);
        doReturn(TASK_LIST).when(taskRepository).findAllBy(pageable);

        List<Task> actualResult = taskService.getAllByPageable(pageable);

        verify(taskRepository).findAllBy(pageable);
        assertEquals(TASK_LIST, actualResult);
    }

    @Test
    void getAllByPageable_shouldGetPaginatedTaskList_whenCustomArguments() {
        final Pageable pageable = PageRequest.of(1, 2);
        final List<Task> paginatedTaskList = List.of(TASK_3, TASK_4);
        doReturn(paginatedTaskList).when(taskRepository).findAllBy(pageable);

        List<Task> actualResult = taskService.getAllByPageable(pageable);

        verify(taskRepository).findAllBy(pageable);
        assertEquals(paginatedTaskList, actualResult);
    }

    @Test
    void getAllByPageable_shouldGetEmptyTaskList_whenNoTasksByCustomArguments() {
        final Pageable pageable = PageRequest.of(999, 999);
        doReturn(emptyList()).when(taskRepository).findAllBy(pageable);

        List<Task> actualResult = taskService.getAllByPageable(pageable);

        verify(taskRepository).findAllBy(pageable);
        assertTrue(actualResult.isEmpty());
    }

    @Test
    void getAllByPageable_shouldGetEmptyTaskList_whenNotExist() {
        final Pageable pageable = PageRequest.of(0, 20);
        doReturn(emptyList()).when(taskRepository).findAllBy(pageable);

        List<Task> actualResult = taskService.getAllByPageable(pageable);

        verify(taskRepository).findAllBy(pageable);
        assertTrue(actualResult.isEmpty());
    }



    @Test
    void getAllByAuthUser_shouldGetTaskListDtoOfAuthUser_whenDefaultArguments() {
        final Pageable pageable = PageRequest.of(0, 20);
        final List<Task> userTaskList = List.of(TASK, TASK_2, TASK_3);
        doReturn(USER).when(userService).getAuthUser();
        doReturn(userTaskList).when(taskRepository).findAllByUserIdOrderById(USER.getId(), pageable);
        doReturn(RESPONSE_TASK_DTO).when(mapper).mapToResponseTaskDto(any(Task.class));

        TaskListDto actualResult = taskService.getAllByAuthUser(0, 20);

        verify(userService).getAuthUser();
        verify(taskRepository).findAllByUserIdOrderById(USER.getId(), pageable);
        verify(mapper, times(3)).mapToResponseTaskDto(any(Task.class));
        assertFalse(actualResult.getTaskList().isEmpty());
        assertThat(actualResult.getTaskList()).hasSize(3);
    }

    @Test
    void getAllByAuthUser_shouldGetPaginatedTaskListDtoOfAuthUser_whenCustomArguments() {
        final Pageable pageable = PageRequest.of(1, 2);
        final List<Task> userTaskList = List.of(TASK_6);
        doReturn(USER).when(userService).getAuthUser();
        doReturn(userTaskList).when(taskRepository).findAllByUserIdOrderById(USER.getId(), pageable);
        doReturn(RESPONSE_TASK_DTO).when(mapper).mapToResponseTaskDto(any(Task.class));

        TaskListDto actualResult = taskService.getAllByAuthUser(1, 2);

        verify(userService).getAuthUser();
        verify(taskRepository).findAllByUserIdOrderById(USER.getId(), pageable);
        verify(mapper).mapToResponseTaskDto(any(Task.class));
        assertFalse(actualResult.getTaskList().isEmpty());
        assertThat(actualResult.getTaskList()).hasSize(1);
    }

    @Test
    void getAllByAuthUser_shouldGetEmptyTaskListDtoOfAuthUser_whenNoTasksByCustomArguments() {
        final Pageable pageable = PageRequest.of(999, 999);
        doReturn(USER).when(userService).getAuthUser();
        doReturn(emptyList()).when(taskRepository).findAllByUserIdOrderById(USER.getId(), pageable);

        TaskListDto actualResult = taskService.getAllByAuthUser(999, 999);

        verify(userService).getAuthUser();
        verify(taskRepository).findAllByUserIdOrderById(USER.getId(), pageable);
        verifyNoInteractions(mapper);
        assertTrue(actualResult.getTaskList().isEmpty());
    }

    @Test
    void getAllByAuthUser_shouldGetEmptyTaskListDtoOfAuthUser_whenNotExist() {
        final Pageable pageable = PageRequest.of(0, 20);
        doReturn(USER).when(userService).getAuthUser();
        doReturn(emptyList()).when(taskRepository).findAllByUserIdOrderById(USER.getId(), pageable);

        TaskListDto actualResult = taskService.getAllByAuthUser(0, 20);

        verify(userService).getAuthUser();
        verify(taskRepository).findAllByUserIdOrderById(USER.getId(), pageable);
        verifyNoInteractions(mapper);
        assertTrue(actualResult.getTaskList().isEmpty());
    }



    @Test
    void getTaskDtoById_shouldGetResponseTaskDto_whenExist() {
        doReturn(Optional.of(TASK)).when(taskRepository).findById(TASK.getId());
        doReturn(USER).when(userService).getAuthUser();
        doReturn(RESPONSE_TASK_DTO).when(mapper).mapToResponseTaskDto(TASK);

        ResponseTaskDto actualResult = taskService.getTaskDtoById(TASK.getId());

        verify(taskRepository).findById(TASK.getId());
        verify(userService).getAuthUser();
        verify(mapper).mapToResponseTaskDto(TASK);
        assertEquals(RESPONSE_TASK_DTO, actualResult);
    }

    @Test
    void getTaskDtoById_shouldThrowException_whenNotExist() {
        doReturn(Optional.empty()).when(taskRepository).findById(999L);

        assertThrows(NotFoundException.class, () -> taskService.getTaskDtoById(999L));

        verify(taskRepository).findById(999L);
    }

    @Test
    void getTaskDtoById_shouldThrowException_whenTaskIsForeign() {
        doReturn(Optional.of(TASK_4)).when(taskRepository).findById(TASK_4.getId());
        doReturn(USER).when(userService).getAuthUser();

        assertThrows(UnitedException.class, () -> taskService.getTaskDtoById(TASK_4.getId()));

        verify(taskRepository).findById(TASK_4.getId());
        verify(userService).getAuthUser();
    }

    @Test
    void getTaskDtoById_shouldGetResponseTaskDto_whenTaskIsForeignButAuthUserIsAdmin() {
        doReturn(Optional.of(TASK)).when(taskRepository).findById(TASK.getId());
        doReturn(ADMIN).when(userService).getAuthUser();
        doReturn(RESPONSE_TASK_DTO).when(mapper).mapToResponseTaskDto(TASK);

        ResponseTaskDto actualResult = taskService.getTaskDtoById(TASK.getId());

        verify(taskRepository).findById(TASK.getId());
        verify(userService).getAuthUser();
        verify(mapper).mapToResponseTaskDto(TASK);
        assertEquals(RESPONSE_TASK_DTO, actualResult);
    }



    @Test
    void update_shouldUpdateTask_whenExist() {
        final Task task = TASK.clone();
        doReturn(Optional.of(task)).when(taskRepository).findById(task.getId());
        doReturn(RESPONSE_TASK_DTO).when(mapper).mapToResponseTaskDto(task);

        ResponseTaskDto actualResult;
        try (MockedStatic<AuthUser> authUserMock = mockStatic(AuthUser.class)) {
            authUserMock.when(AuthUser::getAuthUsername).thenReturn(USER.getUsername());
            actualResult = taskService.update(task.getId(), UPDATE_TASK_DTO);

            authUserMock.verify(AuthUser::getAuthUsername);
        }
        verify(taskRepository).findById(task.getId());
        verify(mapper).mapToResponseTaskDto(task);
        assertEquals(RESPONSE_TASK_DTO, actualResult);
    }

    @Test
    void update_shouldThrowException_whenNotExist() {
        doReturn(Optional.empty()).when(taskRepository).findById(999L);

        assertThrows(NotFoundException.class, () -> taskService.update(999L, UPDATE_TASK_DTO));
        verify(taskRepository).findById(999L);
    }

    @Test
    void update_shouldThrowException_whenTaskIsForeign() {
        doReturn(Optional.of(TASK_4)).when(taskRepository).findById(TASK_4.getId());

        try (MockedStatic<AuthUser> authUserMock = mockStatic(AuthUser.class)) {
            authUserMock.when(AuthUser::getAuthUsername).thenReturn(USER.getUsername());
            assertThrows(UnitedException.class, () -> taskService.update(TASK_4.getId(), UPDATE_TASK_DTO));

            authUserMock.verify(AuthUser::getAuthUsername);
        }
        verify(taskRepository).findById(TASK_4.getId());
    }



    @Test
    void markAsCompleted_shouldMarkTaskAsCompleted_whenExist() {
        final Task task = TASK.clone();
        doReturn(Optional.of(task)).when(taskRepository).findById(task.getId());

        try (MockedStatic<AuthUser> authUserMock = mockStatic(AuthUser.class)) {
            authUserMock.when(AuthUser::getAuthUsername).thenReturn(USER.getUsername());
            taskService.markAsCompleted(task.getId());

            authUserMock.verify(AuthUser::getAuthUsername);
        }
        verify(taskRepository).findById(task.getId());
    }

    @Test
    void markAsCompleted_shouldThrowException_whenNotExist() {
        doReturn(Optional.empty()).when(taskRepository).findById(999L);

        assertThrows(NotFoundException.class, () -> taskService.markAsCompleted(999L));
        verify(taskRepository).findById(999L);
    }

    @Test
    void markAsCompleted_shouldThrowException_whenTaskIsForeign() {
        doReturn(Optional.of(TASK_4)).when(taskRepository).findById(TASK_4.getId());

        try (MockedStatic<AuthUser> authUserMock = mockStatic(AuthUser.class)) {
            authUserMock.when(AuthUser::getAuthUsername).thenReturn(USER.getUsername());
            assertThrows(UnitedException.class, () -> taskService.markAsCompleted(TASK_4.getId()));

            authUserMock.verify(AuthUser::getAuthUsername);
        }
        verify(taskRepository).findById(TASK_4.getId());
    }



    @Test
    void delete_shouldDeleteTask_whenExist() {
        doReturn(Optional.of(TASK)).when(taskRepository).findById(TASK.getId());
        doNothing().when(taskRepository).deleteById(TASK.getId());

        try (MockedStatic<AuthUser> authUserMock = mockStatic(AuthUser.class)) {
            authUserMock.when(AuthUser::getAuthUsername).thenReturn(USER.getUsername());
            taskService.delete(TASK.getId());

            authUserMock.verify(AuthUser::getAuthUsername);
        }
        verify(taskRepository).findById(TASK.getId());
        verify(taskRepository).deleteById(TASK.getId());
    }

    @Test
    void delete_shouldThrowException_whenNotExist() {
        doReturn(Optional.empty()).when(taskRepository).findById(999L);

        assertThrows(NotFoundException.class, () -> taskService.delete(999L));
        verify(taskRepository).findById(999L);
    }

    @Test
    void delete_shouldThrowException_whenTaskIsForeign() {
        doReturn(Optional.of(TASK_4)).when(taskRepository).findById(TASK_4.getId());

        try (MockedStatic<AuthUser> authUserMock = mockStatic(AuthUser.class)) {
            authUserMock.when(AuthUser::getAuthUsername).thenReturn(USER.getUsername());
            assertThrows(UnitedException.class, () -> taskService.delete(TASK_4.getId()));

            authUserMock.verify(AuthUser::getAuthUsername);
        }
        verify(taskRepository).findById(TASK_4.getId());
    }



    @Test
    void getTask_shouldGetTask_whenExist() {
        doReturn(Optional.of(TASK)).when(taskRepository).findById(TASK.getId());

        Task actualResult = taskService.getTask(TASK.getId());

        verify(taskRepository).findById(TASK.getId());
        assertEquals(TASK, actualResult);
    }

    @Test
    void getTask_shouldThrowException_whenNotExist() {
        doReturn(Optional.empty()).when(taskRepository).findById(999L);

        assertThrows(NotFoundException.class, () -> taskService.getTask(999L));
        verify(taskRepository).findById(999L);
    }



}















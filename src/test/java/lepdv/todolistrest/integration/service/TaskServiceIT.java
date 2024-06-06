package lepdv.todolistrest.integration.service;

import lepdv.todolistrest.integration.IT;
import lepdv.todolistrest.dto.task.ResponseTaskDto;
import lepdv.todolistrest.dto.task.TaskListDto;
import lepdv.todolistrest.entity.Task;
import lepdv.todolistrest.exception.NotFoundException;
import lepdv.todolistrest.exception.UnitedException;
import lepdv.todolistrest.mapper.Mapper;
import lepdv.todolistrest.service.TaskService;
import lepdv.todolistrest.service.UserService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.stream.Stream;

import static lepdv.todolistrest.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@IT
@WithMockUser(username = "Ivan", authorities = "USER")
@RequiredArgsConstructor
class TaskServiceIT /*extends IntegrationTestBase*/ {

    private final UserService userService;
    private final Mapper mapper;
    private final TaskService taskService;




    @Test
    void create_shouldCreateNewTask() {
        final ResponseTaskDto expectedResult = ResponseTaskDto.builder()
                .description("Ivan task4")
                .dueDate(LocalDate.of(2026, Month.MAY, 11))
                .build();

        ResponseTaskDto actualResult = taskService.create(CREATE_TASK_DTO);

        assertEquals(7L, actualResult.getId());
        assertEquals(expectedResult.getDescription(), actualResult.getDescription());
        assertEquals(expectedResult.getDueDate(), actualResult.getDueDate());
    }



    @Test
    void getAllByPageable_shouldGetTaskList_whenDefaultArguments() {
        final Pageable pageable = PageRequest.of(0, 20);

        List<Task> actualResult = taskService.getAllByPageable(pageable);

        assertEquals(TASK_LIST, actualResult);
    }

    @Test
    void getAllByPageable_shouldGetPaginatedTaskList_whenCustomArguments() {
        final Pageable pageable = PageRequest.of(1, 2);
        final List<Task> paginatedTaskList = List.of(TASK_3, TASK_4);

        List<Task> actualResult = taskService.getAllByPageable(pageable);

        assertEquals(paginatedTaskList, actualResult);
    }

    @Test
    void getAllByPageable_shouldGetEmptyTaskList_whenNoTasksByCustomArguments() {
        final Pageable pageable = PageRequest.of(999, 999);

        List<Task> actualResult = taskService.getAllByPageable(pageable);

        assertTrue(actualResult.isEmpty());
    }

    @Test
    void getAllByPageable_shouldGetEmptyTaskList_whenNotExist() {
        final Pageable pageable = PageRequest.of(0, 20);
        userService.deleteUserById(USER.getId());
        userService.deleteUserById(KATYA.getId());

        List<Task> actualResult = taskService.getAllByPageable(pageable);

        assertTrue(actualResult.isEmpty());
    }



    @Test
    void getAllByAuthUser_shouldGetTaskListDtoOfAuthUser_whenDefaultArguments() {
        final List<ResponseTaskDto> expectedResult = Stream.of(TASK, TASK_2, TASK_3)
                .map(mapper::mapToResponseTaskDto)
                .toList();

        TaskListDto actualResult = taskService.getAllByAuthUser(0, 20);

        assertFalse(actualResult.getTaskList().isEmpty());
        assertThat(actualResult.getTaskList()).hasSize(3);
        assertEquals(expectedResult, actualResult.getTaskList());
    }

    @Test
    void getAllByAuthUser_shouldGetPaginatedTaskListDtoOfAuthUser_whenCustomArguments() {
        final List<ResponseTaskDto> expectedResult = Stream.of(TASK_3)
                .map(mapper::mapToResponseTaskDto)
                .toList();

        TaskListDto actualResult = taskService.getAllByAuthUser(1, 2);

        assertFalse(actualResult.getTaskList().isEmpty());
        assertThat(actualResult.getTaskList()).hasSize(1);
        assertEquals(expectedResult, actualResult.getTaskList());
    }

    @Test
    void getAllByAuthUser_shouldGetEmptyTaskListDtoOfAuthUser_whenNoTasksByCustomArguments() {
        TaskListDto actualResult = taskService.getAllByAuthUser(999, 999);

        assertTrue(actualResult.getTaskList().isEmpty());
    }

    @Test
    void getAllByAuthUser_shouldGetEmptyTaskListDtoOfAuthUser_whenNotExist() {
        taskService.delete(1L);
        taskService.delete(2L);
        taskService.delete(3L);

        TaskListDto actualResult = taskService.getAllByAuthUser(0, 20);

        assertTrue(actualResult.getTaskList().isEmpty());
    }



    @Test
    void getTaskDtoById_shouldGetTaskDto_whenExist() {
        ResponseTaskDto actualResult = taskService.getTaskDtoById(TASK.getId());

        assertEquals(RESPONSE_TASK_DTO, actualResult);
    }

    @Test
    void getTaskDtoById_shouldThrowException_whenNotExist() {
        assertThrows(NotFoundException.class, () -> taskService.getTaskDtoById(999L));
    }

    @Test
    void getTaskDtoById_shouldThrowException_whenTaskIsForeign() {
        assertThrows(UnitedException.class, () -> taskService.getTaskDtoById(TASK_4.getId()));
    }

    @Test
    @WithMockUser(username = "Admin", authorities = "ADMIN")
    void getTaskDtoById_shouldGetTaskDto_whenTaskIsForeignButAuthUserIsAdmin() {
        ResponseTaskDto actualResult = taskService.getTaskDtoById(TASK.getId());

        assertEquals(RESPONSE_TASK_DTO, actualResult);
    }



    @Test
    void update_shouldUpdateTask_whenExist() {
        final ResponseTaskDto expectedResult = ResponseTaskDto.builder()
                .id(1L)
                .description("Updated Ivan task1")
                .dateOfCreation(LocalDate.of(2023, Month.MAY, 1))
                .dueDate(LocalDate.of(2026, Month.MAY, 31))
                .isCompleted("Not completed")
                .user(USER.getUsername())
                .build();

        ResponseTaskDto actualResult = taskService.update(TASK.getId(), UPDATE_TASK_DTO);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    void update_shouldThrowException_whenNotExist() {
        assertThrows(NotFoundException.class, () -> taskService.update(999L, UPDATE_TASK_DTO));
    }

    @Test
    void update_shouldThrowException_whenTaskIsForeign() {
        assertThrows(UnitedException.class, () -> taskService.update(TASK_4.getId(), UPDATE_TASK_DTO));
    }



    @Test
    void markAsCompleted_shouldMarkTaskAsCompleted_whenExist() {
        taskService.markAsCompleted(TASK.getId());

        final Task markedTask = taskService.getTask(TASK.getId());
        assertEquals("Completed", markedTask.getIsCompleted());
    }

    @Test
    void markAsCompleted_shouldThrowException_whenNotExist() {
        assertThrows(NotFoundException.class, () -> taskService.markAsCompleted(999L));
    }

    @Test
    void markAsCompleted_shouldThrowException_whenTaskIsForeign() {
        assertThrows(UnitedException.class, () -> taskService.markAsCompleted(TASK_4.getId()));
    }



    @Test
    void delete_shouldDeleteTask_whenExist() {
        taskService.delete(TASK.getId());

        assertThrows(NotFoundException.class, () -> taskService.getTask(TASK.getId()));
    }

    @Test
    void delete_shouldThrowException_whenNotExist() {
        assertThrows(NotFoundException.class, () -> taskService.delete(999L));
    }

    @Test
    void delete_shouldThrowException_whenTaskIsForeign() {
        assertThrows(UnitedException.class, () -> taskService.delete(TASK_4.getId()));
    }



    @Test
    void getTask_shouldGetTask_whenExist() {
        Task actualResult = taskService.getTask(TASK.getId());

        assertEquals(TASK, actualResult);
    }

    @Test
    void getTask_shouldThrowException_whenNotExist() {
        assertThrows(NotFoundException.class, () -> taskService.getTask(999L));
    }



}















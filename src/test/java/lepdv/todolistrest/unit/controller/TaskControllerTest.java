package lepdv.todolistrest.unit.controller;

import lepdv.todolistrest.controller.TaskController;
import lepdv.todolistrest.dto.task.ResponseTaskDto;
import lepdv.todolistrest.dto.task.TaskListDto;
import lepdv.todolistrest.mapper.Mapper;
import lepdv.todolistrest.service.TaskService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static lepdv.todolistrest.Constants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class TaskControllerTest {

    @Mock
    private TaskService taskService;
    @InjectMocks
    private TaskController taskController;
    private final Mapper mapper = new Mapper(new ModelMapper());




    @Test
    void createTask_shouldGetREWithResponseTaskDto() {
        doReturn(RESPONSE_TASK_DTO).when(taskService).create(CREATE_TASK_DTO);

        ResponseEntity<ResponseTaskDto> actualResult = taskController.createTask(CREATE_TASK_DTO);

        verify(taskService).create(CREATE_TASK_DTO);
        assertNotNull(actualResult);
        assertEquals(HttpStatus.CREATED, actualResult.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, actualResult.getHeaders().getContentType());
        assertEquals(RESPONSE_TASK_DTO, actualResult.getBody());
    }



    @Test
    void getTodoList_shouldGetREWithTaskListDtoOfAuthUser_whenDefaultParams() {
        final List<ResponseTaskDto> responseTaskDtoList = Stream.of(TASK, TASK_2, TASK_3)
                .map(mapper::mapToResponseTaskDto)
                .toList();
        final TaskListDto taskListDto = new TaskListDto(responseTaskDtoList);
        doReturn(taskListDto).when(taskService).getAllByAuthUser(0, 20);

        ResponseEntity<TaskListDto> actualResult = taskController.getTodoList(0, 20);

        verify(taskService).getAllByAuthUser(0, 20);
        assertNotNull(actualResult);
        assertEquals(HttpStatus.OK, actualResult.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, actualResult.getHeaders().getContentType());
        assertEquals(taskListDto, actualResult.getBody());
    }

    @Test
    void getTodoList_shouldGetREWithPaginatedTaskListDtoOfAuthUser_whenCustomParams() {
        final List<ResponseTaskDto> responseTaskDtoList = Stream.of(TASK_3)
                .map(mapper::mapToResponseTaskDto)
                .toList();
        final TaskListDto taskListDto = new TaskListDto(responseTaskDtoList);
        doReturn(taskListDto).when(taskService).getAllByAuthUser(1, 2);

        ResponseEntity<TaskListDto> actualResult = taskController.getTodoList(1, 2);

        verify(taskService).getAllByAuthUser(1, 2);
        assertNotNull(actualResult);
        assertEquals(HttpStatus.OK, actualResult.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, actualResult.getHeaders().getContentType());
        assertEquals(taskListDto, actualResult.getBody());
    }

    @Test
    void getTodoList_shouldGetREWithEmptyTaskListDtoOfAuthUser_whenNoTasksByCustomParams() {
        final TaskListDto taskListDto = new TaskListDto(emptyList());
        doReturn(taskListDto).when(taskService).getAllByAuthUser(999, 999);

        ResponseEntity<TaskListDto> actualResult = taskController.getTodoList(999, 999);

        verify(taskService).getAllByAuthUser(999, 999);
        assertNotNull(actualResult);
        assertEquals(HttpStatus.OK, actualResult.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, actualResult.getHeaders().getContentType());
        assertEquals(taskListDto, actualResult.getBody());
    }

    @Test
    void getTodoList_shouldGetREWithEmptyTaskListDtoOfAuthUser_whenNotExist() {
        final TaskListDto taskListDto = new TaskListDto(emptyList());
        doReturn(taskListDto).when(taskService).getAllByAuthUser(0, 20);

        ResponseEntity<TaskListDto> actualResult = taskController.getTodoList(0, 20);

        verify(taskService).getAllByAuthUser(0, 20);
        assertNotNull(actualResult);
        assertEquals(HttpStatus.OK, actualResult.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, actualResult.getHeaders().getContentType());
        assertEquals(taskListDto, actualResult.getBody());
    }



    @Test
    void getTaskById_shouldGetREWithResponseTaskDto() {
        doReturn(RESPONSE_TASK_DTO).when(taskService).getTaskDtoById(TASK.getId());

        ResponseEntity<ResponseTaskDto> actualResult = taskController.getTaskById(TASK.getId());

        verify(taskService).getTaskDtoById(TASK.getId());
        assertNotNull(actualResult);
        assertEquals(HttpStatus.OK, actualResult.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, actualResult.getHeaders().getContentType());
        assertEquals(RESPONSE_TASK_DTO, actualResult.getBody());
    }

    @Test
    void getTaskById_shouldGetREWithResponseTaskDto_whenTaskIsForeignButAuthUserIsAdmin() {
        doReturn(RESPONSE_TASK_DTO).when(taskService).getTaskDtoById(TASK.getId());

        ResponseEntity<ResponseTaskDto> actualResult = taskController.getTaskById(TASK.getId());

        verify(taskService).getTaskDtoById(TASK.getId());
        assertNotNull(actualResult);
        assertEquals(HttpStatus.OK, actualResult.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, actualResult.getHeaders().getContentType());
        assertEquals(RESPONSE_TASK_DTO, actualResult.getBody());
    }



    @Test
    void updateTaskById_shouldGetREWithUpdatedResponseTaskDto_whenDataIsValid() {
        final ResponseTaskDto updatedTask = ResponseTaskDto.builder()
                .id(1L)
                .description("Updated Ivan task1")
                .dateOfCreation(LocalDate.of(2026, Month.MAY, 31))
                .dueDate(LocalDate.of(2025, Month.MAY, 30))
                .isCompleted("Not completed")
                .user(USER.getUsername())
                .build();
        doReturn(updatedTask).when(taskService).update(TASK.getId(), UPDATE_TASK_DTO);

        ResponseEntity<ResponseTaskDto> actualResult = taskController.updateTaskById(TASK.getId(), UPDATE_TASK_DTO);

        verify(taskService).update(TASK.getId(), UPDATE_TASK_DTO);
        assertNotNull(actualResult);
        assertEquals(HttpStatus.OK, actualResult.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, actualResult.getHeaders().getContentType());
        assertEquals(updatedTask, actualResult.getBody());
    }



    @Test
    void markAsCompletedById_shouldGetREWithMarkAsCompletedString() {
        final String expectedString = "Task with id=" + TASK.getId() + " was marked as completed";
        doNothing().when(taskService).markAsCompleted(TASK.getId());

        ResponseEntity<String> actualResult = taskController.markAsCompletedById(TASK.getId());

        verify(taskService).markAsCompleted(TASK.getId());
        assertNotNull(actualResult);
        assertEquals(HttpStatus.OK, actualResult.getStatusCode());
        assertEquals(MediaType.TEXT_PLAIN, actualResult.getHeaders().getContentType());
        assertEquals(expectedString, actualResult.getBody());
    }



    @Test
    void deleteTaskById_shouldGetREWithDeleteString() {
        final String expectedString = "Task with id=" + TASK.getId() + " was deleted";
        doNothing().when(taskService).delete(TASK.getId());

        ResponseEntity<String> actualResult = taskController.deleteTaskById(TASK.getId());

        verify(taskService).delete(TASK.getId());
        assertNotNull(actualResult);
        assertEquals(HttpStatus.OK, actualResult.getStatusCode());
        assertEquals(MediaType.TEXT_PLAIN, actualResult.getHeaders().getContentType());
        assertEquals(expectedString, actualResult.getBody());
    }



}









package lepdv.todolistrest.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lepdv.todolistrest.dto.task.CreateTaskDto;
import lepdv.todolistrest.dto.task.ResponseTaskDto;
import lepdv.todolistrest.dto.task.TaskListDto;
import lepdv.todolistrest.dto.task.UpdateTaskDto;
import lepdv.todolistrest.entity.Task;
import lepdv.todolistrest.integration.IT;
import lepdv.todolistrest.mapper.Mapper;
import lepdv.todolistrest.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.stream.Stream;

import static com.atlassian.oai.validator.mockmvc.OpenApiValidationMatchers.openApi;
import static java.util.Collections.emptyList;
import static lepdv.todolistrest.Constants.*;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@IT
@AutoConfigureMockMvc(printOnlyOnFailure = false)
@WithMockUser(username = "Ivan", authorities = "USER")
@RequiredArgsConstructor
class TaskControllerIT /*extends IntegrationTestBase*/ {

    private final TaskService taskService;
    private final MockMvc mockMvc;
    private final Mapper mapper = new Mapper(new ModelMapper());
    private final ObjectMapper jsonMapper = JsonMapper.builder()
            .findAndAddModules()
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            .build();




    @Test
    void createTask_shouldGetREWithResponseTaskDto_whenDataIsValid() throws Exception {
        final String jsonCreateTaskDto = jsonMapper.writeValueAsString(CREATE_TASK_DTO);
        final RequestBuilder request = post("/api/v2/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonCreateTaskDto);

        mockMvc.perform(request)
                .andExpectAll(
                        openApi().isValid("static/oldOpenapi.yaml"),
                        status().isCreated(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json("""
                                {
                                  "id": 7,
                                  "description": "Ivan task4",
                                  "dueDate": "2026-05-11",
                                  "isCompleted": "Not completed",
                                  "user": "Ivan"
                                }
                                """)
                );
        Task createdTask = taskService.getTask(7L);
        assertNotNull(createdTask);
    }

    @Test
    void createTask_shouldGetREWithErrorsBody_whenDataIsInvalid() throws Exception {
        final CreateTaskDto createTaskDto = CreateTaskDto.builder()
                .description("")
                .dueDate(LocalDate.of(2022, Month.MAY, 22))
                .build();
        final String jsonCreateTaskDto = jsonMapper.writeValueAsString(createTaskDto);
        final RequestBuilder request = post("/api/v2/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonCreateTaskDto);

        mockMvc.perform(request)
                .andExpectAll(
                        openApi().isValid("static/oldOpenapi.yaml"),
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json("""
                                {
                                    "status": 400,
                                    "type": "MethodArgumentNotValidException",
                                    "path": "uri=/api/v2/tasks",
                                    "message": "Invalid request"
                                }
                                """)
                );
    }



    @Test
    void getTodoList_shouldGetREWithTaskListDtoOfAuthUser_whenDefaultParams() throws Exception {
        final List<ResponseTaskDto> responseTaskDtoList = Stream.of(TASK, TASK_2, TASK_3)
                .map(mapper::mapToResponseTaskDto)
                .toList();
        final TaskListDto taskListDto = new TaskListDto(responseTaskDtoList);
        final String jsonTaskListDto = jsonMapper.writeValueAsString(taskListDto);
        final RequestBuilder request = get("/api/v2/tasks/todo-list");

        mockMvc.perform(request)
                .andExpectAll(
                        openApi().isValid("static/oldOpenapi.yaml"),
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(jsonTaskListDto)
                );


    }

    @Test
    void getTodoList_shouldGetREWithPaginatedTaskListDtoOfAuthUser_whenCustomParams() throws Exception {
        final List<ResponseTaskDto> responseTaskDtoList = Stream.of(TASK_3)
                .map(mapper::mapToResponseTaskDto)
                .toList();
        final TaskListDto taskListDto = new TaskListDto(responseTaskDtoList);
        final String jsonTaskListDto = jsonMapper.writeValueAsString(taskListDto);
        final RequestBuilder request = get("/api/v2/tasks/todo-list?page=1&size=2");

        mockMvc.perform(request)
                .andExpectAll(
                        openApi().isValid("static/oldOpenapi.yaml"),
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(jsonTaskListDto)
                );
    }

    @Test
    void getTodoList_shouldGetREWithEmptyTaskListDtoOfAuthUser_whenNoTasksByCustomParams() throws Exception {
        final TaskListDto taskListDto = new TaskListDto(emptyList());
        final String jsonTaskListDto = jsonMapper.writeValueAsString(taskListDto);
        final RequestBuilder request = get("/api/v2/tasks/todo-list?page=999&size=999");

        mockMvc.perform(request)
                .andExpectAll(
                        openApi().isValid("static/oldOpenapi.yaml"),
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(jsonTaskListDto)
                );
    }

    @Test
    void getTodoList_shouldGetREWithEmptyTaskListDtoOfAuthUser_whenNotExist() throws Exception {
        final TaskListDto taskListDto = new TaskListDto(emptyList());
        final String jsonTaskListDto = jsonMapper.writeValueAsString(taskListDto);
        final RequestBuilder request = get("/api/v2/tasks/todo-list");
        taskService.delete(1L);
        taskService.delete(2L);
        taskService.delete(3L);

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
        final RequestBuilder request = get("/api/v2/tasks/" + TASK.getId());

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
        final RequestBuilder request = get("/api/v2/tasks/999");

        mockMvc.perform(request)
                .andExpectAll(
                        openApi().isValid("static/oldOpenapi.yaml"),
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(getTaskNotFoundErrorsBody())
                );
    }

    @Test
    void getTaskById_shouldGetREWithErrorsBody_whenTaskIsForeign() throws Exception {
        final RequestBuilder request = get("/api/v2/tasks/" + TASK_4.getId());

        mockMvc.perform(request)
                .andExpectAll(
                        openApi().isValid("static/oldOpenapi.yaml"),
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json("""
                                {
                                  "status": 400,
                                    "errors": ["Task with id=4 belongs to another user"],
                                    "type": "UnitedException",
                                    "path": "uri=/api/v2/tasks/4",
                                    "message": "Invalid request"
                                }
                                """)
                );
    }

    @Test
    @WithMockUser(username = "Admin", authorities = "ADMIN")
    void getTaskById_shouldGetREWithResponseTaskDto_whenTaskIsForeignButAuthUserIsAdmin() throws Exception {
        final String jsonResponseTaskDto = jsonMapper.writeValueAsString(RESPONSE_TASK_DTO);
        final RequestBuilder request = get("/api/v2/tasks/" + TASK.getId());

        mockMvc.perform(request)
                .andExpectAll(
                        openApi().isValid("static/oldOpenapi.yaml"),
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(jsonResponseTaskDto)
                );
    }



    @Test
    void updateTaskById_shouldGetREWithUpdatedRequestTaskDto_whenExistAndValid() throws Exception {
        final ResponseTaskDto responseTaskDto = ResponseTaskDto.builder()
                .id(1L)
                .description("Updated Ivan task1")
                .dateOfCreation(LocalDate.of(2023, Month.MAY, 1))
                .dueDate(LocalDate.of(2026, Month.MAY, 31))
                .isCompleted("Not completed")
                .user(USER.getUsername())
                .build();
        final String jsonUpdateTaskDto = jsonMapper.writeValueAsString(UPDATE_TASK_DTO);
        final String jsonResponseTaskDto = jsonMapper.writeValueAsString(responseTaskDto);
        final RequestBuilder request = put("/api/v2/tasks/" + TASK.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonUpdateTaskDto);

        mockMvc.perform(request)
                .andExpectAll(
                        openApi().isValid("static/oldOpenapi.yaml"),
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(jsonResponseTaskDto)
                );
    }

    @Test
    void updateTaskById_shouldGetREWithErrorsBody_whenNotExist() throws Exception {
        final String jsonUpdateTaskDto = jsonMapper.writeValueAsString(UPDATE_TASK_DTO);
        final RequestBuilder request = put("/api/v2/tasks/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonUpdateTaskDto);

        mockMvc.perform(request)
                .andExpectAll(
                        openApi().isValid("static/oldOpenapi.yaml"),
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(getTaskNotFoundErrorsBody())
                );
    }

    @Test
    void updateTaskById_shouldGetREWithErrorsBody_whenUpdateTaskDtoIsNotValid() throws Exception {
        final UpdateTaskDto updateTaskDto = UpdateTaskDto.builder()
                .description("")
                .dueDate(LocalDate.of(2020, Month.MAY, 31))
                .build();
        final String jsonUpdateTaskDto = jsonMapper.writeValueAsString(updateTaskDto);
        final RequestBuilder request = put("/api/v2/tasks/" + TASK_4.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonUpdateTaskDto);

        mockMvc.perform(request)
                .andExpectAll(
                        openApi().isValid("static/oldOpenapi.yaml"),
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json("""
                                {
                                  "status": 400,
                                  "errors": [
                                    "description: can't be empty",
                                    "dueDate: can't be in Past"
                                  ],
                                  "type": "MethodArgumentNotValidException",
                                  "path": "uri=/api/v2/tasks/4",
                                  "message": "Invalid request"
                                }
                                """)
                );
    }

    @Test
    void updateTaskById_shouldGetREWithErrorsBody_whenTaskIsForeign() throws Exception {
        final String jsonUpdateTaskDto = jsonMapper.writeValueAsString(UPDATE_TASK_DTO);
        final RequestBuilder request = put("/api/v2/tasks/" + TASK_4.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonUpdateTaskDto);

        mockMvc.perform(request)
                .andExpectAll(
                        openApi().isValid("static/oldOpenapi.yaml"),
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json("""
                                {
                                   "status": 400,
                                    "errors": ["Task with id=4 belongs to another user"],
                                    "type": "UnitedException",
                                    "path": "uri=/api/v2/tasks/4",
                                    "message": "Invalid request"
                                }
                                """)
                );
    }



    @Test
    void markAsCompletedById_shouldGetREWithMarkAsCompletedString_whenExist() throws Exception {
        final String expectedString = "Task with id=" + TASK.getId() + " was marked as completed";
        final RequestBuilder request = put("/api/v2/tasks/" + TASK.getId() + "/mark-as-completed");

        mockMvc.perform(request)
                .andExpectAll(
                        openApi().isValid("static/oldOpenapi.yaml"),
                        status().isOk(),
                        content().contentType(MediaType.TEXT_PLAIN),
                        content().bytes(expectedString.getBytes())
                );
    }

    @Test
    void markAsCompletedById_shouldGetREWithErrorsBody_whenNotExist() throws Exception {
        final RequestBuilder request = put("/api/v2/tasks/999/mark-as-completed");

        mockMvc.perform(request)
                .andExpectAll(
                        openApi().isValid("static/oldOpenapi.yaml"),
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(getTaskNotFoundErrorsBody())
                );
    }

    @Test
    void markAsCompletedById_shouldGetREWithErrorsBody_whenTaskIsForeign() throws Exception {
        final RequestBuilder request = put("/api/v2/tasks/" + TASK_4.getId() + "/mark-as-completed");

        mockMvc.perform(request)
                .andExpectAll(
                        openApi().isValid("static/oldOpenapi.yaml"),
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json("""
                                {
                                  "status": 400,
                                  "errors": ["Task with id=4 belongs to another user"],
                                  "type": "UnitedException",
                                  "path": "uri=/api/v2/tasks/4/mark-as-completed",
                                  "message": "Invalid request"
                                }
                                """)
                );
    }



    @Test
    void deleteTaskById_shouldGetREWithDeleteTaskString_whenExist() throws Exception {
        final String expectedString = "Task with id=" + TASK.getId() + " was deleted";
        final RequestBuilder request = delete("/api/v2/tasks/" + TASK.getId());

        mockMvc.perform(request)
                .andExpectAll(
                        openApi().isValid("static/oldOpenapi.yaml"),
                        status().isOk(),
                        content().contentType(MediaType.TEXT_PLAIN),
                        content().bytes(expectedString.getBytes())
                );
    }

    @Test
    void deleteTaskById_shouldGetREWithErrorsBody_whenNotExist() throws Exception {
        final RequestBuilder request = delete("/api/v2/tasks/999");

        mockMvc.perform(request)
                .andExpectAll(
                        openApi().isValid("static/oldOpenapi.yaml"),
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(getTaskNotFoundErrorsBody())
                );
    }

    @Test
    void deleteTaskById_shouldGetREWithErrorsBody_whenTaskIsForeign() throws Exception {
        final RequestBuilder request = delete("/api/v2/tasks/" + TASK_4.getId());

        mockMvc.perform(request)
                .andExpectAll(
                        openApi().isValid("static/oldOpenapi.yaml"),
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json("""
                                {
                                  "status": 400,
                                  "errors": ["Task with id=4 belongs to another user"],
                                  "type": "UnitedException",
                                  "path": "uri=/api/v2/tasks/4",
                                  "message": "Invalid request"
                                }
                                """)
                );
    }



    @NotNull
    private String getTaskNotFoundErrorsBody() {
        return """
                {
                  "status": 404,
                  "errors": ["There is no task with id=999 in database"],
                  "type": "NotFoundException",
                  "message": "Not Found"
                }""";
    }


}
package lepdv.todolistrest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lepdv.todolistrest.dto.task.*;
import lepdv.todolistrest.exception.ErrorsBody;
import lepdv.todolistrest.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.*;


@RestController
@RequiredArgsConstructor
@Tag(name = "task-controller")
@RequestMapping("/api/v2/tasks")
public class TaskController {

    private final TaskService taskService;




    @Operation(
            operationId = "createTask",
            summary = "Create new task",
            tags = "task-controller",
            responses = {
                    @ApiResponse(responseCode = "201", description = "New task is created", content = {
                            @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = ResponseTaskDto.class))}),
                    @ApiResponse(description = "All unusual situations", content = {
                            @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorsBody.class))})
            })
    @PostMapping()
    public ResponseEntity<ResponseTaskDto> createTask(@Valid @RequestBody CreateTaskDto createTaskDto) {
        ResponseTaskDto createdTask = taskService.create(createTaskDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .contentType(APPLICATION_JSON)
                .body(createdTask);
    }



    @Operation(
            operationId = "getTodoList",
            summary = "Returns list of tasks of authenticate user and sorted/filtered based on the query parameters",
            tags = "task-controller",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Found list of tasks of authenticate user", content = {
                            @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = TaskListDto.class))})
            })
    @GetMapping("/todo-list")
    public ResponseEntity<TaskListDto> getTodoList(@RequestParam(required = false, name = "page",
                                                                 defaultValue = "0") int page,
                                                   @RequestParam(required = false, name = "size",
                                                                 defaultValue = "20") int size) {
        TaskListDto taskListDto = taskService.getAllByAuthUser(page, size);
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(APPLICATION_JSON)
                .body(taskListDto);
    }



    @Operation(
            operationId = "getTaskById",
            summary = "Get task by its id",
            tags = "task-controller",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Found the task", content = {
                            @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = ResponseTaskDto.class))}),
                    @ApiResponse(description = "All unusual situations", content = {
                            @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorsBody.class))})
            })
    @GetMapping("/{id}")
    public ResponseEntity<ResponseTaskDto> getTaskById(@PathVariable("id") Long id) {

        ResponseTaskDto task = taskService.getTaskDtoById(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(APPLICATION_JSON)
                .body(task);
    }



    @Operation(
            operationId = "updateTaskById",
            summary = "Update task by its id",
            tags = "task-controller",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Task was updated", content = {
                            @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = ResponseTaskDto.class))}),
                    @ApiResponse(description = "All unusual situations", content = {
                            @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorsBody.class))})
            })
    @PutMapping("/{id}")
    public ResponseEntity<ResponseTaskDto> updateTaskById(@PathVariable("id") Long id,
                                                          @Valid @RequestBody UpdateTaskDto updateTaskDto) {
        ResponseTaskDto updatedTask = taskService.update(id, updateTaskDto);
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(APPLICATION_JSON)
                .body(updatedTask);
    }



    @Operation(
            operationId = "markAsCompletedById",
            summary = "Mark task as completed by its id",
            tags = "task-controller",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Task was marked as completed", content = {
                            @Content(mediaType = TEXT_PLAIN_VALUE, schema = @Schema(implementation = String.class))}),
                    @ApiResponse(description = "All unusual situations", content = {
                            @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorsBody.class))})
            })
    @PutMapping("/{id}/mark-as-completed")
    public ResponseEntity<String> markAsCompletedById(@PathVariable("id") Long id) {

        taskService.markAsCompleted(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(TEXT_PLAIN)
                .body("Task with id=" + id + " was marked as completed");
    }



    @Operation(
            operationId = "deleteTaskById",
            summary = "Delete task by its id",
            tags = "task-controller",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Task was deleted", content = {
                            @Content(mediaType = TEXT_PLAIN_VALUE, schema = @Schema(implementation = String.class))}),
                    @ApiResponse(description = "All unusual situations", content = {
                            @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorsBody.class))})
            })
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTaskById(@PathVariable("id") Long id) {

        taskService.delete(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(TEXT_PLAIN)
                .body("Task with id=" + id + " was deleted");
    }



}

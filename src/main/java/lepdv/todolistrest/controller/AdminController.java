package lepdv.todolistrest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lepdv.todolistrest.dto.admin.UserForAdminDto;
import lepdv.todolistrest.dto.admin.UserListDto;
import lepdv.todolistrest.dto.auth.CredentialsDto;
import lepdv.todolistrest.dto.task.ResponseTaskDto;
import lepdv.todolistrest.dto.task.TaskListDto;
import lepdv.todolistrest.exception.ErrorsBody;
import lepdv.todolistrest.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;


@RestController
@RequiredArgsConstructor
@Tag(name = "admin-controller")
@RequestMapping("/api/v2/admin")
public class AdminController {

    private final AdminService adminService;




    @Operation(
            operationId = "getAllUsers",
            summary = "Returns a list of users and sorted/filtered based on the query parameters",
            tags = "admin-controller",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Found all users", content = {
                            @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = UserListDto.class))})
            })
    @GetMapping("/users")
    public ResponseEntity<UserListDto> getAllUsers(@RequestParam(required = false, name = "page",
                                                   defaultValue = "0") int page,
                                                   @RequestParam(required = false, name = "size",
                                                   defaultValue = "20") int size) {
        UserListDto allUsers = adminService.getAllUsers(page, size);
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(allUsers);
    }



    @Operation(
            operationId = "getUserById",
            summary = "Get user by its id",
            tags = "admin-controller",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Found the user", content = {
                            @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = UserForAdminDto.class))}),
                    @ApiResponse(description = "All unusual situations", content = {
                            @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorsBody.class))})
            })
    @GetMapping( "/users/{id}")
    public ResponseEntity<UserForAdminDto> getUserById(@PathVariable("id") Long id) {

        UserForAdminDto user = adminService.getUserForAdminDto(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(user);
    }



    @Operation(
            operationId = "lockUserById",
            summary = "Lock user by its id",
            tags = "admin-controller",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User was locked", content = {
                            @Content(mediaType = TEXT_PLAIN_VALUE, schema = @Schema(implementation = String.class))}),
                    @ApiResponse(description = "All unusual situations", content = {
                            @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorsBody.class))})
            })
    @PutMapping("/users/{id}/lock")
    public ResponseEntity<String> lockUserById(@PathVariable("id") Long id) {

        adminService.lockUser(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.TEXT_PLAIN)
                .body("User id=" + id + " was locked");
    }



    @Operation(
            operationId = "unlockUserById",
            summary = "Unlock user by its id",
            tags = "admin-controller",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User was unlocked", content = {
                            @Content(mediaType = TEXT_PLAIN_VALUE, schema = @Schema(implementation = String.class))}),
                    @ApiResponse(description = "All unusual situations", content = {
                            @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorsBody.class))})
            })
    @PutMapping("/users/{id}/unlock")
    public ResponseEntity<String> unlockUserById(@PathVariable("id") Long id) {

        adminService.unlockUser(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.TEXT_PLAIN)
                .body("User id=" + id + " was unlocked");
    }



    @Operation(
            operationId = "deleteUserById",
            summary = "Delete user by its id",
            tags = "admin-controller",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User was deleted", content = {
                            @Content(mediaType = TEXT_PLAIN_VALUE, schema = @Schema(implementation = String.class))}),
                    @ApiResponse(description = "All unusual situations", content = {
                            @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorsBody.class))})
            })
    @DeleteMapping("/users/{id}/delete")
    public ResponseEntity<String> deleteUserById(@Valid @RequestBody CredentialsDto credentialsDto,
                                                 @PathVariable("id") Long id) {

        adminService.deleteUser(credentialsDto, id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.TEXT_PLAIN)
                .body("User id=" + id + " was deleted");
    }



    @Operation(
            operationId = "getAllTasks",
            summary = "Returns list of tasks of all users and sorted/filtered based on the query parameters",
            tags = "admin-controller",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Found list of tasks of all users", content = {
                            @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = TaskListDto.class))})
            })
    @GetMapping("/tasks")
    public ResponseEntity<TaskListDto> getAllTasks(@RequestParam(required = false, name = "page",
                                                   defaultValue = "0") int page,
                                                   @RequestParam(required = false, name = "size",
                                                   defaultValue = "20") int size) {
        TaskListDto taskListDto = adminService.getAllTasks(page, size);
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(taskListDto);
    }



    @Operation(
            operationId = "getTaskById",
            summary = "Get task by its id",
            tags = "admin-controller",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Found the task", content = {
                            @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = ResponseTaskDto.class))}),
                    @ApiResponse(description = "All unusual situations", content = {
                            @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorsBody.class))})
            })
    @GetMapping("/tasks/{id}")
    public ResponseEntity<ResponseTaskDto> getTaskById(@PathVariable("id") Long id) {

        ResponseTaskDto task = adminService.getTaskById(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(task);
    }


}

package lepdv.todolistrest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lepdv.todolistrest.dto.auth.CredentialsDto;
import lepdv.todolistrest.dto.user.EditPasswordDto;
import lepdv.todolistrest.dto.user.UserDto;
import lepdv.todolistrest.exception.ErrorsBody;
import lepdv.todolistrest.exception.UnitedException;
import lepdv.todolistrest.service.UserService;
import lepdv.todolistrest.util.UserDtoValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import static lepdv.todolistrest.exception.ErrorMessage.getErrorMessage;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;


@RestController
@RequiredArgsConstructor
@Tag(name = "user-controller")
@RequestMapping("/api/v2/user")
public class UserController {

    private final UserService userService;
    private final UserDtoValidator userDtoValidator;




    @Operation(
            operationId = "getUser",
            summary = "Get authenticated user",
            tags = "user-controller",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Found the user", content = {
                            @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = UserDto.class))})
            })
    @GetMapping()
    public ResponseEntity<UserDto> getUser() {

        UserDto user = userService.getUserDto();
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(user);
    }



    @Operation(
            operationId = "updateUser",
            summary = "Update authenticate user",
            tags = "user-controller",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User was updated", content = {
                            @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = UserDto.class))}),
                    @ApiResponse(description = "All unusual situations", content = {
                            @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorsBody.class))})
            })
    @PutMapping()
    public ResponseEntity<UserDto> updateUser(@Valid @RequestBody UserDto userDto, BindingResult bindingResult) {

        userDtoValidator.validate(userDto, bindingResult);
        if (bindingResult.hasErrors()) {
            throw new UnitedException(getErrorMessage(bindingResult));
        }
        UserDto updatedUser = userService.update(userDto);
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(updatedUser);
    }



    @Operation(
            operationId = "editPassword",
            summary = "Edit user's password",
            tags = "user-controller",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User's password was edited", content = {
                            @Content(mediaType = TEXT_PLAIN_VALUE, schema = @Schema(implementation = String.class))}),
                    @ApiResponse(description = "All unusual situations", content = {
                            @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorsBody.class))})
            })
    @PutMapping("/edit-password")
    public ResponseEntity<String> editPassword(@Valid @RequestBody EditPasswordDto editPasswordDto) {

        userService.editPassword(editPasswordDto);
        String message = "Password of %s was edited".formatted(editPasswordDto.getUsername());
        return  ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.TEXT_PLAIN)
                .body(message);
    }



    @Operation(
            operationId = "deleteAccount",
            summary = "Delete user's account",
            tags = "user-controller",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User's account was deleted", content = {
                            @Content(mediaType = TEXT_PLAIN_VALUE, schema = @Schema(implementation = String.class))}),
                    @ApiResponse(description = "All unusual situations", content = {
                            @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorsBody.class))})
            })
    @DeleteMapping("/delete-account")
    public ResponseEntity<String> deleteAccount(@Valid @RequestBody CredentialsDto credentialsDto) {

        userService.deleteUser(credentialsDto);
        String message = "Account of %s was deleted".formatted(credentialsDto.getUsername());
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.TEXT_PLAIN)
                .body(message);
    }



}

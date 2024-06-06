package lepdv.todolistrest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lepdv.todolistrest.dto.auth.CredentialsDto;
import lepdv.todolistrest.dto.auth.JwtDto;
import lepdv.todolistrest.dto.auth.RegisterDto;
import lepdv.todolistrest.exception.ErrorsBody;
import lepdv.todolistrest.exception.UnitedException;
import lepdv.todolistrest.security.JWTUtil;
import lepdv.todolistrest.service.UserService;
import lepdv.todolistrest.util.RegisterDtoValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static lepdv.todolistrest.exception.ErrorMessage.getErrorMessage;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;


@RestController
@RequiredArgsConstructor
@Tag(name = "auth-controller")
@RequestMapping("/api/v2/authenticate")
public class AuthController {

    private final UserService userService;
    private final RegisterDtoValidator registerDtoValidator;
    private final JWTUtil jwtUtil;
    private final AuthenticationManager authenticationManager;




    @Operation(
            operationId = "performLogin",
            summary = "Perform login by username and password",
            tags = "auth-controller",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Performing login was successful", content = {
                            @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = JwtDto.class))}),
                    @ApiResponse(description = "All unusual situations", content = {
                            @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorsBody.class))})
            })
    @PostMapping("/login")
    public ResponseEntity<JwtDto> performLogin(@Valid @RequestBody CredentialsDto credentialsDto) {

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                credentialsDto.getUsername(), credentialsDto.getPassword());
        try {
            authenticationManager.authenticate(authToken);
        } catch (BadCredentialsException e) {
            throw new UnitedException("Incorrect credentials");
        } catch (LockedException e) {
            throw new UnitedException("User is locked");
        }
        String token = jwtUtil.generateToken(credentialsDto.getUsername());
        JwtDto jwtMapDto = new JwtDto(token);
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(jwtMapDto);
    }



    @Operation(
            operationId = "register",
            summary = "Registration new user",
            tags = "auth-controller",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Registration new user was successful, user is created", content = {
                            @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = JwtDto.class))}),
                    @ApiResponse(description = "All unusual situations", content = {
                            @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorsBody.class))})
            })
    @PostMapping("/registration")
    public ResponseEntity<JwtDto> register(@Valid @RequestBody RegisterDto registerDto, BindingResult bindingResult) {

        registerDtoValidator.validate(registerDto, bindingResult);
        if (bindingResult.hasErrors()) {
            throw new UnitedException(getErrorMessage(bindingResult));
        }
        userService.register(registerDto);
        String token = jwtUtil.generateToken(registerDto.getUsername());
        JwtDto jwtMapDto = new JwtDto(token);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(jwtMapDto);
    }



}

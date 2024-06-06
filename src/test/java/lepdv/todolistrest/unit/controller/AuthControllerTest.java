package lepdv.todolistrest.unit.controller;

import lepdv.todolistrest.controller.AuthController;
import lepdv.todolistrest.dto.auth.CredentialsDto;
import lepdv.todolistrest.dto.auth.JwtDto;
import lepdv.todolistrest.exception.UnitedException;
import lepdv.todolistrest.security.JWTUtil;
import lepdv.todolistrest.service.UserService;
import lepdv.todolistrest.util.RegisterDtoValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.validation.BindingResult;

import static lepdv.todolistrest.Constants.REGISTER_DTO;
import static lepdv.todolistrest.Constants.USER;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private UserService userService;
    @Mock
    private RegisterDtoValidator registerDtoValidator;
    @Mock
    private JWTUtil jwtUtil;
    @Mock
    private AuthenticationManager authenticationManager;
    @InjectMocks
    private AuthController authController;




    @Test
    void performLogin_shouldGetREWithTokenMap_whenCorrectCredentials() {
        final CredentialsDto credentialsDto = new CredentialsDto("Ivan", "Ivan");
        final UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                credentialsDto.getUsername(), credentialsDto.getPassword());
        final UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                USER, USER.getPassword(), USER.getAuthorities());
        final String jwt = "some token";
        JwtDto expectedBody = new JwtDto(jwt);
        doReturn(authentication).when(authenticationManager).authenticate(authToken);
        doReturn(jwt).when(jwtUtil).generateToken(credentialsDto.getUsername());

        ResponseEntity<JwtDto> actualResult = authController.performLogin(credentialsDto);

        verify(authenticationManager).authenticate(authToken);
        verify(jwtUtil).generateToken(credentialsDto.getUsername());
        assertNotNull(actualResult);
        assertEquals(HttpStatus.OK, actualResult.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, actualResult.getHeaders().getContentType());
        assertEquals(expectedBody, actualResult.getBody());
    }

    @Test
    void performLogin_shouldThrowException_whenIncorrectCredentials() {
        final CredentialsDto credentialsDto = new CredentialsDto("Ivan", "dummy");
        final UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                credentialsDto.getUsername(), credentialsDto.getPassword());
        doThrow(BadCredentialsException.class).when(authenticationManager).authenticate(authToken);

        assertThrows(UnitedException.class, () -> authController.performLogin(credentialsDto));

        verify(authenticationManager).authenticate(authToken);
    }

    @Test
    void performLogin_shouldThrowException_whenUserIsLocked() {
        final CredentialsDto credentialsDto = new CredentialsDto("Ivan", "Ivan");
        final UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                credentialsDto.getUsername(), credentialsDto.getPassword());
        doThrow(LockedException.class).when(authenticationManager).authenticate(authToken);

        assertThrows(UnitedException.class, () -> authController.performLogin(credentialsDto));

        verify(authenticationManager).authenticate(authToken);
    }



    @Test
    void register_shouldGetREWithTokenMap_whenDataIsValid() {
        final BindingResult bindingResultMock = mock(BindingResult.class);
        final String jwt = "some token";
        JwtDto expectedBody = new JwtDto(jwt);
        doNothing().when(registerDtoValidator).validate(REGISTER_DTO, bindingResultMock);
        doNothing().when(userService).register(REGISTER_DTO);
        doReturn(jwt).when(jwtUtil).generateToken(REGISTER_DTO.getUsername());

        ResponseEntity<JwtDto> actualResult = authController.register(REGISTER_DTO, bindingResultMock);

        verify(registerDtoValidator).validate(REGISTER_DTO, bindingResultMock);
        verify(userService).register(REGISTER_DTO);
        verify(jwtUtil).generateToken(REGISTER_DTO.getUsername());
        assertNotNull(actualResult);
        assertEquals(HttpStatus.CREATED, actualResult.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, actualResult.getHeaders().getContentType());
        assertEquals(expectedBody, actualResult.getBody());
    }

    @Test
    void register_shouldThrowException_whenDataIsInvalid() {
        final BindingResult bindingResultMock = mock(BindingResult.class);
        doNothing().when(registerDtoValidator).validate(REGISTER_DTO, bindingResultMock);
        doThrow(UnitedException.class).when(bindingResultMock).hasErrors();

        assertThrows(UnitedException.class, () -> authController.register(REGISTER_DTO, bindingResultMock));

        verify(registerDtoValidator).validate(REGISTER_DTO, bindingResultMock);
    }



}
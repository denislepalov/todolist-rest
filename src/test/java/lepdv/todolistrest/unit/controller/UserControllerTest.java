package lepdv.todolistrest.unit.controller;

import lepdv.todolistrest.controller.UserController;
import lepdv.todolistrest.dto.auth.CredentialsDto;
import lepdv.todolistrest.dto.user.EditPasswordDto;
import lepdv.todolistrest.dto.user.UserDto;
import lepdv.todolistrest.exception.UnitedException;
import lepdv.todolistrest.service.UserService;
import lepdv.todolistrest.util.UserDtoValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import java.time.LocalDate;
import java.time.Month;

import static lepdv.todolistrest.Constants.USER_DTO;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;
    @Mock
    private UserDtoValidator userDtoValidator;
    @InjectMocks
    private UserController userController;




    @Test
    void getUser_shouldGetREWithUserDto() {
        doReturn(USER_DTO).when(userService).getUserDto();

        ResponseEntity<UserDto> actualResult = userController.getUser();

        verify(userService).getUserDto();
        assertNotNull(actualResult);
        assertEquals(HttpStatus.OK, actualResult.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, actualResult.getHeaders().getContentType());
        assertEquals(USER_DTO, actualResult.getBody());
    }



    @Test
    void updateUser_shouldGetREWithUpdatedUserDto_whenDataIsValid() {
        final UserDto userDto = UserDto.builder()
                .username("Ivan")
                .fullName("Updated full name")
                .dateOfBirth(LocalDate.of(2000, Month.JANUARY, 1))
                .build();
        BindingResult mockBindingResult = mock(BindingResult.class);
        doNothing().when(userDtoValidator).validate(userDto, mockBindingResult);
        doReturn(false).when(mockBindingResult).hasErrors();
        doReturn(userDto).when(userService).update(userDto);

        ResponseEntity<UserDto> actualResult = userController.updateUser(userDto, mockBindingResult);

        verify(userDtoValidator).validate(userDto, mockBindingResult);
        verify(mockBindingResult).hasErrors();
        verify(userService).update(userDto);
        assertNotNull(actualResult);
        assertEquals(HttpStatus.OK, actualResult.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, actualResult.getHeaders().getContentType());
        assertEquals(userDto, actualResult.getBody());
    }

    @Test
    void updateUser_shouldThrowException_whenDataIsInvalid() {
        final UserDto userDto = UserDto.builder()
                .username("Iv")
                .fullName("Updated full name")
                .dateOfBirth(LocalDate.of(2030, Month.JANUARY, 1))
                .build();
        BindingResult mockBindingResult = mock(BindingResult.class);
        doNothing().when(userDtoValidator).validate(userDto, mockBindingResult);
        doReturn(true).when(mockBindingResult).hasErrors();

        assertThrows(UnitedException.class, () -> userController.updateUser(userDto, mockBindingResult));

        verify(userDtoValidator).validate(userDto, mockBindingResult);
        verify(mockBindingResult).hasErrors();
    }



    @Test
    void editPassword_shouldGetREWithEditPasswordString() {
        final EditPasswordDto editPasswordDto = EditPasswordDto.builder()
                .username("Ivan")
                .oldPassword("Ivan")
                .newPassword("newIvan")
                .build();
        final String message = "Password of %s was edited".formatted(editPasswordDto.getUsername());
        doNothing().when(userService).editPassword(editPasswordDto);

        ResponseEntity<String> actualResult = userController.editPassword(editPasswordDto);

        verify(userService).editPassword(editPasswordDto);
        assertNotNull(actualResult);
        assertEquals(HttpStatus.OK, actualResult.getStatusCode());
        assertEquals(MediaType.TEXT_PLAIN, actualResult.getHeaders().getContentType());
        assertEquals(message, actualResult.getBody());
    }



    @Test
    void deleteAccount_shouldGetREWithDeleteAccountString() {
        final CredentialsDto credentialsDto = CredentialsDto.builder()
                .username("Ivan")
                .password("Ivan")
                .build();
        final String message = "Account of %s was deleted".formatted(credentialsDto.getUsername());
        doNothing().when(userService).deleteUser(credentialsDto);

        ResponseEntity<String> actualResult = userController.deleteAccount(credentialsDto);

        verify(userService).deleteUser(credentialsDto);
        assertNotNull(actualResult);
        assertEquals(HttpStatus.OK, actualResult.getStatusCode());
        assertEquals(MediaType.TEXT_PLAIN, actualResult.getHeaders().getContentType());
        assertEquals(message, actualResult.getBody());
    }



}









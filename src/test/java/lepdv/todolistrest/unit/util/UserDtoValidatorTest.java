package lepdv.todolistrest.unit.util;

import lepdv.todolistrest.dto.user.UserDto;
import lepdv.todolistrest.service.UserService;
import lepdv.todolistrest.util.AuthUser;
import lepdv.todolistrest.util.UserDtoValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.Errors;

import java.util.Optional;

import static lepdv.todolistrest.Constants.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserDtoValidatorTest {

    @Mock
    private UserService userService;
    @InjectMocks
    private UserDtoValidator userDtoValidator;
    private static final Class<UserDto> CLAZZ = UserDto.class;
    private static final Class<String> WRONG_CLAZZ = String.class;


    @Test
    void supports_shouldGetTrue_whenParameterIsValid() {
        boolean actualResult = userDtoValidator.supports(CLAZZ);

        assertTrue(actualResult);
    }

    @Test
    void supports_shouldGetFalse_whenParameterIsInvalid() {
        boolean actualResult = userDtoValidator.supports(WRONG_CLAZZ);

        assertFalse(actualResult);
    }


    @Test
    void validate_shouldNotAddError_whenDataIsValid() {
        final Errors errorsMock = mock(Errors.class);
        doReturn(Optional.of(USER)).when(userService).getByUsername(AuthUser.getAuthUsername());

        userDtoValidator.validate(USER_DTO, errorsMock);

        verify(userService).getByUsername(AuthUser.getAuthUsername());
        verifyNoInteractions(errorsMock);
    }


    @Test
    void validate_shouldAddError_whenDataIsInvalid() {
        final Errors errorsMock = mock(Errors.class);
        final UserDto userDto = UserDto.builder()
                .username(USER.getUsername())
                .build();
        doReturn(Optional.of(KATYA)).when(userService).getByUsername(AuthUser.getAuthUsername());
        doReturn(Optional.of(USER)).when(userService).getByUsername(USER.getUsername());
        doNothing().when(errorsMock).rejectValue("username", "", "such username already exist");

        userDtoValidator.validate(userDto, errorsMock);

        verify(userService).getByUsername(AuthUser.getAuthUsername());
        verify(userService).getByUsername(USER.getUsername());
        verify(errorsMock).rejectValue("username", "", "such username already exist");
    }

}
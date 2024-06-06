package lepdv.todolistrest.unit.util;

import lepdv.todolistrest.dto.auth.RegisterDto;
import lepdv.todolistrest.service.UserService;
import lepdv.todolistrest.util.RegisterDtoValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.Errors;

import java.util.Optional;

import static lepdv.todolistrest.Constants.REGISTER_DTO;
import static lepdv.todolistrest.Constants.USER;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegisterDtoValidatorTest {

    @Mock
    private UserService userService;
    @InjectMocks
    private RegisterDtoValidator registerDtoValidator;
    private static final Class<RegisterDto> CLAZZ = RegisterDto.class;
    private static final Class<String> WRONG_CLAZZ = String.class;




    @Test
    void supports_shouldGetTrue_whenArgumentIsValid() {
        boolean actualResult = registerDtoValidator.supports(CLAZZ);

        assertTrue(actualResult);
    }

    @Test
    void supports_shouldGetFalse_whenArgumentIsInvalid() {
        boolean actualResult = registerDtoValidator.supports(WRONG_CLAZZ);

        assertFalse(actualResult);
    }



    @Test
    void validate_shouldNotAddError_whenDataIsValid() {
        final Errors errorsMock = mock(Errors.class);
        doReturn(Optional.empty()).when(userService).getByUsername(REGISTER_DTO.getUsername());

        registerDtoValidator.validate(REGISTER_DTO, errorsMock);

        verify(userService).getByUsername(REGISTER_DTO.getUsername());
        verifyNoInteractions(errorsMock);
    }

    @Test
    void validate_shouldAddError_whenDataIsInvalid() {
        final Errors errorsMock = mock(Errors.class);
        doReturn(Optional.of(USER)).when(userService).getByUsername(REGISTER_DTO.getUsername());
        doNothing().when(errorsMock).rejectValue("username", "", "such username already exist");

        registerDtoValidator.validate(REGISTER_DTO, errorsMock);

        verify(userService).getByUsername(REGISTER_DTO.getUsername());
        verify(errorsMock).rejectValue("username", "", "such username already exist");
    }


}
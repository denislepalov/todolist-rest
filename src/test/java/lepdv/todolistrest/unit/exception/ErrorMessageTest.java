package lepdv.todolistrest.unit.exception;

import lepdv.todolistrest.exception.ErrorMessage;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class ErrorMessageTest {



    @Test
    void test_cannot_instantiate() {
        assertThrows(InvocationTargetException.class, () -> {
            Constructor<ErrorMessage> constructor = ErrorMessage.class.getDeclaredConstructor();
            assertTrue(Modifier.isPrivate(constructor.getModifiers()));
            constructor.setAccessible(true);
            constructor.newInstance();
        });
    }



    @Test
    void getErrorMessage_shouldErrorMessage() {

        final FieldError error1 = new FieldError("object", "username", "can't be empty");
        final FieldError error2 = new FieldError("object", "password", "can't be empty");
        final FieldError error3 = new FieldError("object", "description", "can't be empty");
        final List<FieldError> errorList = List.of(error1, error2, error3);
        final String expectedResult = "username - can't be empty; password - can't be empty; description - can't be empty; ";
        final BindingResult bindingResultMock = mock(BindingResult.class);
        doReturn(errorList).when(bindingResultMock).getFieldErrors();

        String actualResult = ErrorMessage.getErrorMessage(bindingResultMock);

        verify(bindingResultMock).getFieldErrors();
        assertEquals(expectedResult, actualResult);
    }


}
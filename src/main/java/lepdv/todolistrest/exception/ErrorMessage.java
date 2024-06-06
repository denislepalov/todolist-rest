package lepdv.todolistrest.exception;

import lombok.experimental.UtilityClass;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.List;


@UtilityClass
public class ErrorMessage {

    public static String getErrorMessage(BindingResult bindingResult) {

        StringBuilder errorMassage = new StringBuilder();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();

        for (FieldError error : fieldErrors) {
            errorMassage.append(error.getField())
                    .append(" - ")
                    .append(error.getDefaultMessage())
                    .append("; ");
        }
        return errorMassage.toString();
    }
}

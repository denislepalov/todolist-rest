package lepdv.todolistrest.util;

import lepdv.todolistrest.dto.auth.RegisterDto;
import lepdv.todolistrest.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;


@Component
@RequiredArgsConstructor
public class RegisterDtoValidator implements Validator {

    private final UserService userService;


    @Override
    public boolean supports( Class<?> clazz) {
        return RegisterDto.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        RegisterDto user = (RegisterDto) target;

            if (userService.getByUsername(user.getUsername()).isPresent()) {
                errors.rejectValue("username", "", "such username already exist");
        }
    }
}

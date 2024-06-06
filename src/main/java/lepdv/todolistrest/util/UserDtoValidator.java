package lepdv.todolistrest.util;

import lepdv.todolistrest.dto.user.UserDto;
import lepdv.todolistrest.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;


@Component
@RequiredArgsConstructor
public class UserDtoValidator implements Validator {

    private final UserService userService;


    @Override
    public boolean supports(Class<?> clazz) {
        return UserDto.class.equals(clazz);
    }


    @Override
    public void validate(Object target, Errors errors) {
        UserDto user = (UserDto) target;

        if (user.getUsername() != null) {
            String oldUserName = userService.getByUsername(AuthUser.getAuthUsername()).get().getUsername();
            String newUserName = user.getUsername();

            if (!oldUserName.equals(newUserName) && userService.getByUsername(newUserName).isPresent()) {
                errors.rejectValue("username", "", "such username already exist");
            }
        }
    }
}

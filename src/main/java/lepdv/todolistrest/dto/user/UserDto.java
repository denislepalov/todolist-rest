package lepdv.todolistrest.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {

    @NotBlank(message = "can't be empty")
    @Size(min = 3, max = 100, message = "should be from 3 to 100 symbols")
    private String username;

    @Size(max = 100, message = "can't be more than 100 symbols")
    private String fullName;

    @PastOrPresent(message = "can't be in Future")
    private LocalDate dateOfBirth;

}

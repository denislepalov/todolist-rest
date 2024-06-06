package lepdv.todolistrest.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserForAdminDto {

    private Long id;
    private String username;
    private String fullName;
    private LocalDate dateOfBirth;
    private String role;
    private Boolean isNonLocked;

}

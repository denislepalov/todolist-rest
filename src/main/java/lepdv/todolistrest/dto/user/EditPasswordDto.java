package lepdv.todolistrest.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EditPasswordDto {

    @JsonProperty(required = true)
    @NotBlank(message = "can't be empty")
    @Size(min = 3, max = 100, message = "should be from 3 to 100 symbols")
    private String username;

    @JsonProperty(required = true)
    @NotBlank(message = "can't be empty")
    @Size(min = 3, max = 100, message = "should be from 3 to 100 symbols")
    private String oldPassword;

    @JsonProperty(required = true)
    @NotBlank(message = "can't be empty")
    @Size(min = 3, max = 100, message = "should be from 3 to 100 symbols")
    private String newPassword;


}

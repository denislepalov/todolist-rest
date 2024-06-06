package lepdv.todolistrest.dto.auth;

import io.swagger.annotations.ApiModel;
import io.swagger.v3.oas.annotations.media.Schema;
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
@ApiModel
public class CredentialsDto {


    @Schema(description = "Username", example = "Ivan", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "can't be empty")
    @Size(min = 3, max = 100, message = "should be from 3 to 100 symbols")
    private String username;

    @Schema(description = "Password", example = "Ivan", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "can't be empty")
    @Size(min = 3, max = 100, message = "should be from 3 to 100 symbols")
    private String password;

}

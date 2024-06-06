package lepdv.todolistrest.dto.task;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateTaskDto {

    @JsonProperty(required = true)
    @NotBlank(message = "can't be empty")
    private String description;

    @FutureOrPresent(message = "can't be in Past")
    private LocalDate dueDate;

}

package lepdv.todolistrest.dto.task;

import lepdv.todolistrest.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseTaskDto {

    private Long id;
    private String description;
    private LocalDate dateOfCreation;
    private LocalDate dueDate;
    private String isCompleted;
    private String user;

    public void setUser(User user) { this.user = user.getUsername(); }
}

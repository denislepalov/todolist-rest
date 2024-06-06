package lepdv.todolistrest.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class ErrorsBody {

    private Instant timestamp;
    private Integer status;
    private List<String> errors;
    private String type;
    private String path;
    private String message;



}

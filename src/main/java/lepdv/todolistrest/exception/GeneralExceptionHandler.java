package lepdv.todolistrest.exception;

import io.micrometer.common.util.StringUtils;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;



@Slf4j
@ControllerAdvice
public class GeneralExceptionHandler extends ResponseEntityExceptionHandler {

    public static final String ACCESS_DENIED = "Access denied!";
    public static final String INVALID_REQUEST = "Invalid request";
    public static final String ERROR_MESSAGE_TEMPLATE = "message: %s requested uri: %s";
    public static final String LIST_JOIN_DELIMITER = ",";
    public static final String FIELD_ERROR_SEPARATOR = ": ";
    private static final String ERRORS_FOR_PATH = "errors: {}, for path: {}, exception: {}";
//    private static final String PATH = "path";
//    private static final String ERRORS = "errors";
//    private static final String STATUS = "status";
//    private static final String MESSAGE = "message";
//    private static final String TIMESTAMP = "timestamp";
//    private static final String TYPE = "type";




    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        List<String> validationErrors = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + FIELD_ERROR_SEPARATOR + error.getDefaultMessage())
                .collect(Collectors.toList());
        return getExceptionResponseEntity(exception, HttpStatus.BAD_REQUEST, request, validationErrors);
    }



    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException exception,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        return getExceptionResponseEntity(exception, (HttpStatus) status, request,
                Collections.singletonList(exception.getLocalizedMessage()));
    }



    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<Object> handleConstraintViolation(
            ConstraintViolationException exception, WebRequest request) {
        final List<String> validationErrors = exception.getConstraintViolations().stream().
                map(violation ->
                        violation.getPropertyPath() + FIELD_ERROR_SEPARATOR + violation.getMessage())
                .collect(Collectors.toList());
        return getExceptionResponseEntity(exception, HttpStatus.BAD_REQUEST, request, validationErrors);
    }



    @ExceptionHandler({NotFoundException.class})
    public ResponseEntity<Object> handleNotFound(NotFoundException exception, WebRequest request) {

        return getExceptionResponseEntity(exception, HttpStatus.NOT_FOUND, request,
                Collections.singletonList(exception.getMessage()));
    }



    @ExceptionHandler({UnitedException.class})
    public ResponseEntity<Object> handleUnited(UnitedException exception, WebRequest request) {

        return getExceptionResponseEntity(exception, HttpStatus.BAD_REQUEST, request,
                Collections.singletonList(exception.getMessage()));
    }



    @ExceptionHandler({Exception.class})
    public ResponseEntity<Object> handleAllExceptions(Exception exception, WebRequest request) {

        ResponseStatus responseStatus = exception.getClass().getAnnotation(ResponseStatus.class);
        final HttpStatus status = responseStatus != null ? responseStatus.value() : HttpStatus.INTERNAL_SERVER_ERROR;
        final String localizedMessage = exception.getLocalizedMessage();
        final String path = request.getDescription(false);
        String message = (StringUtils.isNotEmpty(localizedMessage) ? localizedMessage : status.getReasonPhrase());
        logger.error(String.format(ERROR_MESSAGE_TEMPLATE, message, path), exception);
        return getExceptionResponseEntity(exception, status, request, Collections.singletonList(message));
    }



    private ResponseEntity<Object> getExceptionResponseEntity(final Exception exception,
                                                              final HttpStatus status,
                                                              final WebRequest request,
                                                              final List<String> errors) {

        final String path = request.getDescription(false);
//        final Map<String, Object> body = new LinkedHashMap<>();
//        body.put(TIMESTAMP, Instant.now());
//        body.put(STATUS, status.value());
//        body.put(ERRORS, errors);
//        body.put(TYPE, exception.getClass().getSimpleName());
//        body.put(PATH, path);
//        body.put(MESSAGE, getMessageForStatus(status));

        final ErrorsBody body = ErrorsBody.builder()
                .timestamp(Instant.now())
                .status(status.value())
                .errors(errors)
                .type(exception.getClass().getSimpleName())
                .path(path)
                .message(getMessageForStatus(status))
                .build();

        final String errorsMessage = CollectionUtils.isNotEmpty(errors)
                ? errors.stream().filter(StringUtils::isNotEmpty).collect(Collectors.joining(LIST_JOIN_DELIMITER))
                : status.getReasonPhrase();
        log.error(ERRORS_FOR_PATH, errorsMessage, path, exception.getClass().getSimpleName());
        return ResponseEntity.status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body);
    }


    private String getMessageForStatus(HttpStatus status) {
        return switch (status) {
            case UNAUTHORIZED -> ACCESS_DENIED;
            case BAD_REQUEST -> INVALID_REQUEST;
            default -> status.getReasonPhrase();
        };
    }


}



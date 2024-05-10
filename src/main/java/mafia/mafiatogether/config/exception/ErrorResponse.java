package mafia.mafiatogether.config.exception;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public record ErrorResponse(
        LocalDateTime timeStamp,
        String message,
        List<FieldErrorResponse> errorList
) {
    static ErrorResponse create(final String message) {
        return new ErrorResponse(LocalDateTime.now(), message, new ArrayList<>());
    }

    static ErrorResponse create(final String message, List<FieldErrorResponse> errorList) {
        return new ErrorResponse(LocalDateTime.now(), message, errorList);
    }

    public record FieldErrorResponse(String field, String value, String message) {
    }
}

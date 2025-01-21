package mafia.mafiatogether.common.exception;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public record ErrorResponse(
        LocalDateTime localDateTime,
        Integer errorCode,
        String message,
        List<FieldErrorResponse> errorList
) {
    static ErrorResponse create(final Integer errorCode, final String message) {
        return new ErrorResponse(LocalDateTime.now(), errorCode, message, new ArrayList<>());
    }

    static ErrorResponse create(final Integer errorCode, final String message, List<FieldErrorResponse> errorList) {
        return new ErrorResponse(LocalDateTime.now(), errorCode, message, errorList);
    }

    public record FieldErrorResponse(String field, String value, String message) {
    }
}

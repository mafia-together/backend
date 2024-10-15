package mafia.mafiatogether.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mafia.mafiatogether.common.application.ErrorNotificationService;
import mafia.mafiatogether.common.exception.ErrorResponse.FieldErrorResponse;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final Environment environment;
    private final ErrorNotificationService errorNotificationService;

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponse> Exception(Exception e, HttpServletRequest request) {
        final ErrorResponse errorResponse = ErrorResponse.create(
                ExceptionCode.UNEXPECTED_EXCEPTION.getCode(),
                "예기치 않은 예외가 발생 했습니다."
        );

        log.error(e.getMessage(), e);
        notifyException(true, request, e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    @ExceptionHandler({BindException.class, MethodArgumentNotValidException.class})
    protected ResponseEntity<ErrorResponse> BindException(BindException e, HttpServletRequest request) {
        List<FieldErrorResponse> errors = new ArrayList<>();

        List<FieldError> fieldErrors = e.getFieldErrors();
        for (FieldError fieldError : fieldErrors) {
            errors.add(new FieldErrorResponse(
                    fieldError.getField(),
                    fieldError.getRejectedValue() == null ? "" : fieldError.getRejectedValue().toString(),
                    fieldError.getDefaultMessage()
            ));
        }

        final ErrorResponse errorResponse = ErrorResponse.create(
                ExceptionCode.INVALID_REQUEST.getCode(),
                "",
                errors
        );

        log.error(e.getMessage(), e);
        notifyException(false, request, e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(GlobalException.class)
    protected ResponseEntity<ErrorResponse> GlobalException(GlobalException e, HttpServletRequest request) {
        final ErrorResponse errorResponse = ErrorResponse.create(
                e.getCodes(),
                e.getMessage()
        );

        log.error(e.getMessage(), e);
        notifyException(false, request, e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    protected ResponseEntity<Object> HttpRequestMethodNotSupportedException(
            final HttpRequestMethodNotSupportedException e,
            HttpServletRequest request
    ) {
        final ErrorResponse errorResponse = ErrorResponse.create(
                ExceptionCode.MISSING_AUTHENTICATION_HEADER.getCode(),
                "HTTP 메서드를 확인해 주세요"
        );
        notifyException(false, request, e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            final MissingServletRequestParameterException e,
            HttpServletRequest request
    ) {
        final ErrorResponse errorResponse = ErrorResponse.create(
                ExceptionCode.INVALID_CONTENT.getCode(),
                "요청 파라미터를 확인해 주세요"
        );
        notifyException(false, request, e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    private void notifyException(boolean isError, HttpServletRequest request, Exception exception) {
        if (Arrays.asList(environment.getActiveProfiles()).contains("local")) {
            return;
        }

        StringBuilder headersBuilder = new StringBuilder();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            headersBuilder.append(headerName)
                    .append(": ")
                    .append(request.getHeader(headerName))
                    .append("\n");
        }
        String headers = headersBuilder.toString();

        StringBuilder parametersBuilder = new StringBuilder();
        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String paramName = parameterNames.nextElement();
            parametersBuilder.append(paramName)
                    .append(": ")
                    .append(request.getParameter(paramName))
                    .append("\n");
        }
        String parameters = parametersBuilder.toString();

        String fullStackTrace = exception.toString() + "\n";
        for (StackTraceElement element : exception.getStackTrace()) {
            fullStackTrace += element.toString() + "\n";
        }
        if (fullStackTrace.length() > 3000) {
            fullStackTrace = fullStackTrace.substring(0, 3000);
        }

        errorNotificationService.notifyError(
                isError,
                "### 🕖 발생 시간\n" +
                        LocalDateTime.now() + "\n" +
                        "### Profile\n" +
                        Arrays.toString(environment.getActiveProfiles()) + "\n" +
                        "### 📎 요청 URI\n" +
                        request.getRequestURI() + " (" + request.getMethod() + ")\n" +
                        "### 🛠 요청자 정보\n" +
                        "- IP: " + request.getRemoteAddr() + "\n" +
                        "- 사용자: " + (request.getRemoteUser() != null ? request.getRemoteUser() : "Unknown") + "\n" +
                        "- 헤더:\n" +
                        "```\n" +
                        headers +
                        "```\n" +
                        "- 요청 파라미터:\n" +
                        "```\n" +
                        parameters +
                        "```\n" +
                        "### ✅ 예외 정보\n" +
                        "- 예외 클래스: " + exception.getClass().getCanonicalName() + "\n" +
                        "- 예외 메시지: " + (exception.getMessage() != null ? exception.getMessage() : "No message") + "\n" +
                        "- 발생 위치: " + extractExceptionSource(exception) + "\n"
        );

        errorNotificationService.notifyError(
                isError,
                "### 🗂 스택 트레이스 (부분)\n" +
                        "```\n" +
                        fullStackTrace +
                        "```"
        );
    }

    private String extractExceptionSource(Exception exception) {
        StackTraceElement[] stackTrace = exception.getStackTrace();
        if (stackTrace.length > 0) {
            return stackTrace[0].toString();
        }
        return "Unknown Source";
    }

}


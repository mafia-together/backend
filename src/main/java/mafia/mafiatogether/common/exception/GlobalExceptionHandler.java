package mafia.mafiatogether.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mafia.mafiatogether.common.application.ErrorNotificationService;
import mafia.mafiatogether.common.application.dto.ErrorDiscordMessageRequest;
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
    private static final String LOCAL_PROFILE_NAME = "local";

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
        if (Arrays.asList(environment.getActiveProfiles()).contains(LOCAL_PROFILE_NAME)) {
            return;
        }
        String headers = getInformationFromHeader(request);
        String parameters = getInformationFromParameter(request);
        String fullStackTrace = getFullStackTrace(exception);
        ErrorDiscordMessageRequest errorDiscordMessageRequest = new ErrorDiscordMessageRequest(
                environment.getActiveProfiles(),
                request.getRequestURI(),
                request.getMethod(),
                request.getRemoteAddr(),
                request.getRemoteUser(),
                headers,
                parameters,
                exception
        );

        errorNotificationService.notifyError(
                isError,
                errorDiscordMessageRequest
        );
        errorNotificationService.notifyError(
                isError,
                "### 🗂 스택 트레이스 (부분)\n" +
                        "```\n" +
                        fullStackTrace +
                        "```"
        );
    }

    private String getInformationFromHeader(HttpServletRequest request) {
        StringBuilder headersBuilder = new StringBuilder();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            headersBuilder.append(headerName)
                    .append(": ")
                    .append(request.getHeader(headerName))
                    .append("\n");
        }
        return headersBuilder.toString();
    }

    private String getInformationFromParameter(HttpServletRequest request) {
        StringBuilder parametersBuilder = new StringBuilder();
        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String paramName = parameterNames.nextElement();
            parametersBuilder.append(paramName)
                    .append(": ")
                    .append(request.getParameter(paramName))
                    .append("\n");
        }
        return parametersBuilder.toString();
    }

    private String getFullStackTrace(Exception exception) {
        int restrictionMessageLengthOfDiscord = 3000;
        StringBuilder fullStackTrace = new StringBuilder(exception.toString() + "\n");

        for (StackTraceElement element : exception.getStackTrace()) {
            fullStackTrace.append(element.toString()).append("\n");
        }

        int stackTraceLength = Math.min(fullStackTrace.length(), restrictionMessageLengthOfDiscord);
        return fullStackTrace.substring(0, stackTraceLength);
    }

}


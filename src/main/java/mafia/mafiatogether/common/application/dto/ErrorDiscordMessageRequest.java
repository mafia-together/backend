package mafia.mafiatogether.common.application.dto;

import java.time.LocalDateTime;
import java.util.Arrays;

public record ErrorDiscordMessageRequest(
        String[] profile,
        String requestUri,
        String requestMethod,
        String remoteAddr,
        String remoteUser,
        String headers,
        String parameters,
        Exception exception
) {

    public String toErrorMessage() {
        return "### 🕖 발생 시간\n" +
                LocalDateTime.now() + "\n" +
                "### Profile\n" +
                Arrays.toString(profile) + "\n" +
                "### 📎 요청 URI\n" +
                requestUri + " (" + requestMethod + ")\n" +
                "### 🛠 요청자 정보\n" +
                "- IP: " + remoteAddr + "\n" +
                "- 사용자: " + (remoteUser != null ? remoteUser : "Unknown") + "\n" +
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
                "- 발생 위치: " + extractExceptionSource(exception) + "\n";
    }

    private String extractExceptionSource(Exception exception) {
        StackTraceElement[] stackTrace = exception.getStackTrace();
        if (stackTrace.length > 0) {
            return stackTrace[0].toString();
        }
        return "Unknown Source";
    }

}

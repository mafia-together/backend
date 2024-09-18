package mafia.mafiatogether.chat.application.dto.request;

import jakarta.validation.constraints.NotNull;

public record ChatRequest(
        @NotNull
        String content
) {
}
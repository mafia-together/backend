package mafia.mafiatogether.chat.dto.request;

import jakarta.validation.constraints.NotNull;

public record ChatRequest(
        @NotNull
        String contents
) {
}

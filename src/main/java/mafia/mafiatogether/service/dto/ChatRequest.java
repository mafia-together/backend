package mafia.mafiatogether.service.dto;

import jakarta.validation.constraints.NotNull;

public record ChatRequest(
        @NotNull
        String contents
) {
}

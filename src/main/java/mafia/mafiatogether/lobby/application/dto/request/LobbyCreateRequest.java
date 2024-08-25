package mafia.mafiatogether.lobby.application.dto.request;

public record LobbyCreateRequest(
        Integer total,
        Integer mafia,
        Integer doctor,
        Integer police
) {
}

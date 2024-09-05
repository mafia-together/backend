package mafia.mafiatogether.lobby.application.dto.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class DeleteLobbyEvent {
    private final String code;
}

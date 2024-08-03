package mafia.mafiatogether.game.application.dto.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class DeleteGameEvent {

    private final String code;
}

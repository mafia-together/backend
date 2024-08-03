package mafia.mafiatogether.game.application.dto.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import mafia.mafiatogether.game.domain.PlayerCollection;

@Getter
@RequiredArgsConstructor
public class CreatePlayerJobEvent {

    private final String code;
    private final PlayerCollection playerCollection;
}

package mafia.mafiatogether.game.application.dto.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ClearVoteEvent {

    private final String code;
}

package mafia.mafiatogether.vote.application.dto.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AllPlayerVotedEvent {

    private final String code;
}

package mafia.mafiatogether.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RoomUpdateEvent {

    private final Room room;
}

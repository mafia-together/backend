package mafia.mafiatogether.domain;

import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Room {

    private final List<Player> players;
    private final Status status;
    private final RoomInfo roomInfo;

    public static Room create(final RoomInfo roomInfo) {
        return new Room(
                new ArrayList<>(),
                Status.WAIT,
                roomInfo
        );
    }
}

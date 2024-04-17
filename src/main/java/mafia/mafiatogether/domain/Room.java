package mafia.mafiatogether.domain;

import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Room {

    private final List<Player> players;
    private Status status;
    private final RoomInfo roomInfo;
    private final Chat chat;

    public static Room create(final RoomInfo roomInfo) {
        return new Room(
                new ArrayList<>(),
                Status.WAIT,
                roomInfo,
                Chat.chat()
        );
    }

    public void modifyStatus(final Status status) {
        this.status = status;
    }

    public void joinPlayer(final Player player) {
        players.add(player);
    }
}

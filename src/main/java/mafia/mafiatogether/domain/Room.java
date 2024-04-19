package mafia.mafiatogether.domain;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Room {

    private final Map<String, Player> players;
    private Status status;
    private final RoomInfo roomInfo;
    private final Chat chat;

    public static Room create(final RoomInfo roomInfo) {
        return new Room(
                new ConcurrentHashMap<>(),
                Status.WAIT,
                roomInfo,
                Chat.chat()
        );
    }

    public void modifyStatus(final Status status) {
        this.status = status;
    }

    public void joinPlayer(final Player player) {
        players.put(player.getName(), player);
    }

    public Player findPlayer(final String name) {
        if (!players.containsKey(name)) {
            throw new IllegalArgumentException("존재하지 않는 플레이어입니다.");
        }
        return players.get(name);
    }
}

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
                new ConcurrentHashMap<>(),
                Status.WAIT,
                roomInfo,
                Chat.chat()
        );
    }

    public void modifyStatus(final Status status) {
        if (this.status.equals(Status.WAIT)) {
            distributeRole();
        }
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
        players.put(player.getName(), player);
    }

    private void distributeRole() {
        List<Player> playerNames = players.values().stream().toList();
        Collections.shuffle(playerNames);
        Queue<Role> roles = new LinkedList<>();
        for (int i = 0; i < roomInfo.getMafia(); i++) {
            roles.add(Role.MAFIA);
        }
        for (int i = 0; i < roomInfo.getPolice(); i++) {
            roles.add(Role.POLICE);
        }
        for (int i = 0; i < roomInfo.getDoctor(); i++) {
            roles.add(Role.DOCTOR);
        }
        for (Player player : playerNames) {
            if (roles.isEmpty()) {
                break;
            }
            player.modifyRole(roles.poll());
        }
    }
}

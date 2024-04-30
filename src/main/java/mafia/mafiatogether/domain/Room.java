package mafia.mafiatogether.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import mafia.mafiatogether.domain.role.Citizen;
import mafia.mafiatogether.domain.role.Doctor;
import mafia.mafiatogether.domain.role.Mafia;
import mafia.mafiatogether.domain.role.Police;
import mafia.mafiatogether.domain.role.Role;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Room {

    private final List<Player> waitingRoom;
    private final Map<String, Role> players;
    private Status status;
    private final RoomInfo roomInfo;
    private final Chat chat;

    public static Room create(final RoomInfo roomInfo) {
        return new Room(
                new ArrayList<>(),
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
        waitingRoom.add(player);
    }

    private void distributeRole() {
        Collections.shuffle(waitingRoom);
        List<Role> roles = createRoles();

        for (Player player : waitingRoom) {
            for (Role role : roles) {
                if (role.isPlayerOverLimit()) {
                    continue;
                }
                players.put(player.getName(), role);
                player.modifyRole(role);
                break;
            }
        }
    }

    private List<Role> createRoles() {
        final int totalPlayers = roomInfo.getTotal();
        final int mafiaCount = roomInfo.getMafia();
        final int policeCount = roomInfo.getPolice();
        final int doctorCount = roomInfo.getDoctor();

        final Mafia mafia = new Mafia(mafiaCount);
        final Police police = new Police(policeCount);
        final Doctor doctor = new Doctor(doctorCount);
        final Citizen citizen = new Citizen(totalPlayers - mafiaCount - policeCount - doctorCount);

        return List.of(mafia, police, doctor, citizen);
    }

    public Player getPlayer(final String name) {
        if (status == Status.WAIT) {
            for (Player player : waitingRoom) {
                if (player.getName().equals(name)) {
                    return player;
                }
            }
        }
        return players.get(name).getPlayer(name);
    }


    public String executeAbility(final String player, final Player target) {
        Role role = players.get(player);
        return role.executeAbility(target);
    }
}

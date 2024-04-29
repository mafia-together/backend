package mafia.mafiatogether.domain;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
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

//    public static Room create(final RoomInfo roomInfo) {
//        return new Room(
//                new HashMap<>(),
//                Status.WAIT,
//                roomInfo,
//                Chat.chat()
//        );
//    }

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

        Mafia mafia = new Mafia(roomInfo.getMafia());
        Police police = new Police(roomInfo.getPolice());
        Doctor doctor = new Doctor(roomInfo.getDoctor());
        Citizen citizen = new Citizen(
                roomInfo.getTotal() - roomInfo.getMafia() - roomInfo.getPolice() - roomInfo.getDoctor()
        );

        List<Role> roles = List.of(mafia, police, doctor, citizen);

        for (Role role : roles) {
            if(role.isOverSize()){
                continue;
            }
            for (Player player : waitingRoom) {
                player.modifyRole(role);
            }
        }
    }

    public Player getPlayer(final String name){
        Role role = players.get(name);
        return role.getPlayer(name);
    }


    public String executeAbility(final String player, final Player target) {
        Role role = players.get(player);

        return role.executeAbility(target);
    }
}

package mafia.mafiatogether.game.domain;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import mafia.mafiatogether.config.exception.ExceptionCode;
import mafia.mafiatogether.config.exception.PlayerException;
import mafia.mafiatogether.room.domain.Participant;
import mafia.mafiatogether.room.domain.ParticipantCollection;

@Getter
@AllArgsConstructor
public class PlayerCollection {

    private List<Player> players;

    public PlayerCollection() {
        this.players = new ArrayList<>();
    }

    public static PlayerCollection creat(ParticipantCollection participants) {
        final PlayerCollection playerCollection = new PlayerCollection();
        for (Participant participant : participants.getParticipants()) {
            playerCollection.getPlayers().add(Player.create(participant.getName()));
        }
        return playerCollection;
    }

    public Player findByName(String name) {
        return players.stream()
                .filter(player -> player.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new PlayerException(ExceptionCode.INVALID_PLAYER));
    }

    public Integer getTotalPlayers() {
        return players.size();
    }

    public long getAliveMafiaCount() {
        return players.stream()
                .filter(player -> player.isMafia())
                .filter(Player::isAlive)
                .count();
    }

    public long getAliveNotMafiaCount() {
        return players.stream()
                .filter(player -> !player.isMafia())
                .filter(Player::isAlive)
                .count();
    }

    public Long getAlivePlayerCount() {
        return players.stream()
                .filter(player -> player.isAlive())
                .count();
    }
}

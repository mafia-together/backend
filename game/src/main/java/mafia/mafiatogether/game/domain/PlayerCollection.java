package mafia.mafiatogether.game.domain;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import mafia.mafiatogether.common.exception.ExceptionCode;
import mafia.mafiatogether.common.exception.PlayerException;
import mafia.mafiatogether.job.domain.jobtype.JobType;
import mafia.mafiatogether.lobby.domain.Participant;
import mafia.mafiatogether.lobby.domain.ParticipantCollection;

@Getter
@AllArgsConstructor
public class PlayerCollection {

    private List<Player> players;

    public PlayerCollection() {
        this.players = new ArrayList<>();
    }

    public static PlayerCollection create(ParticipantCollection participants) {
        final PlayerCollection playerCollection = new PlayerCollection();
        for (Participant participant : participants.getParticipants()) {
            playerCollection.getPlayers().add(Player.create(participant.getName()));
        }
        return playerCollection;
    }

    public Player findByName(final String name) {
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
                .filter(Player::isMafia)
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
                .filter(Player::isAlive)
                .count();
    }

    public JobType getWinnerJobType() {
        if (getAliveMafiaCount() == 0) {
            return JobType.CITIZEN;
        }
        return JobType.MAFIA;
    }

    public List<Player> getMafias() {
        return players.stream()
                .filter(Player::isMafia)
                .toList();
    }

    public List<Player> getNotMafias() {
        return players.stream()
                .filter(player -> !player.isMafia())
                .toList();
    }

    public void killTarget(final String target) {
        if (target == null || target.isBlank()) {
            return;
        }
        final Player targetPlayer = findByName(target);
        targetPlayer.kill();
    }

    public boolean contains(final String name) {
        return players.stream().anyMatch(player -> player.getName().equals(name));
    }
}

package mafia.mafiatogether.lobby.domain;

import java.time.Clock;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import mafia.mafiatogether.config.exception.ExceptionCode;
import mafia.mafiatogether.config.exception.GameException;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@RedisHash("lobby")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Lobby {

    @Id
    private String code;
    private ParticipantCollection participants;
    private LobbyInfo lobbyInfo;
    private Participant master;
    private Long lastUpdateTime;

    public Lobby(){
        this.participants = new ParticipantCollection();
    }

    public static Lobby create(final String code, final LobbyInfo lobbyInfo) {
        return new Lobby(
                code,
                new ParticipantCollection(),
                lobbyInfo,
                Participant.NONE,
                Clock.systemDefaultZone().millis()
        );
    }

    public static Lobby create(final LobbyInfo lobbyInfo) {
        return new Lobby(
                null,
                new ParticipantCollection(),
                lobbyInfo,
                Participant.NONE,
                Clock.systemDefaultZone().millis()
        );
    }

    public void joinPlayer(final String name) {
        final Participant participant = new Participant(name);
        participants.put(participant, lobbyInfo.getTotal());
        if (master.equals(Participant.NONE)) {
            master = participant;
        }
    }

    public void validateToStart() {
        if (lobbyInfo.getTotal() != participants.size()) {
            throw new GameException(ExceptionCode.NOT_ENOUGH_PLAYER);
        }
    }

    public void updateLastUpdateTime() {
        this.lastUpdateTime = Clock.systemDefaultZone().millis();
    }

    public boolean isParticipantExist(final String name) {
        return participants.contains(name);
    }
}

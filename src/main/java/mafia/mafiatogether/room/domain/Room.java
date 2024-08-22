package mafia.mafiatogether.room.domain;

import java.time.Instant;
import lombok.Getter;
import mafia.mafiatogether.config.exception.ExceptionCode;
import mafia.mafiatogether.config.exception.RoomException;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@RedisHash("room")
public class Room {

    @Id
    private String code;
    private ParticipantCollection participants;
    private RoomInfo roomInfo;
    private Participant master;
    private Long lastUpdateTime;

    private Room(
            final String code,
            final ParticipantCollection participants,
            final RoomInfo roomInfo,
            final Participant master
    ) {
        this.code = code;
        this.participants = participants;
        this.roomInfo = roomInfo;
        this.master = master;
        this.lastUpdateTime = Instant.now().getEpochSecond();
    }

    public Room() {
        this.participants = new ParticipantCollection();
    }

    public static Room create(final String code, final RoomInfo roomInfo) {
        return new Room(
                code,
                new ParticipantCollection(),
                roomInfo,
                Participant.NONE
        );
    }

    public static Room create(final RoomInfo roomInfo) {
        return new Room(
                null,
                new ParticipantCollection(),
                roomInfo,
                Participant.NONE
        );
    }

    public void joinPlayer(final String name) {
        final Participant participant = new Participant(name);
        participants.put(participant, roomInfo.getTotal());
        if (master.equals(Participant.NONE)) {
            master = participant;
        }
    }

    public void validateToStart() {
        if (roomInfo.getTotal() != participants.size()) {
            throw new RoomException(ExceptionCode.NOT_ENOUGH_PLAYER);
        }
    }

    public void updateTimeNow() {
        this.lastUpdateTime = Instant.now().getEpochSecond();
    }
}

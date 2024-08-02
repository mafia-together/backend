package mafia.mafiatogether.room.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import mafia.mafiatogether.config.exception.ExceptionCode;
import mafia.mafiatogether.config.exception.RoomException;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Room {

    private String code;
    private ParticipantCollection participants;
    private final RoomInfo roomInfo;
    private Participant master;

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
}

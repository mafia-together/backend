package mafia.mafiatogether.room.domain;

import java.util.List;
import lombok.Getter;
import mafia.mafiatogether.config.exception.ExceptionCode;
import mafia.mafiatogether.config.exception.RoomException;

@Getter
public class ParticipantCollection {

    private List<Participant> participants;

    public boolean contains(String name){
        return participants.stream()
                .filter(participant -> participant.getName().equals(name))
                .findFirst()
                .isPresent();
    }

    public int size(){
        return participants.size();
    }

    public void put(Participant participant){
        participants.add(participant);
    }

    public Participant getParticipant(String name){
        if (name.isBlank()) {
            return Participant.NONE;
        }
        return participants.stream()
                .filter(participant -> participant.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new RoomException(ExceptionCode.INVALID_PLAYER));
    }
}

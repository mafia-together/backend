package mafia.mafiatogether.lobby.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import mafia.mafiatogether.config.exception.ExceptionCode;
import mafia.mafiatogether.config.exception.GameException;

@Getter
public class ParticipantCollection {

    private List<Participant> participants;

    public ParticipantCollection() {
        participants = new ArrayList<>();
    }

    public int size() {
        return participants.size();
    }

    public void put(Participant participant, int total) {
        validateParticipant(participant, total);
        participants.add(participant);
    }

    private void validateParticipant(Participant participant, int total) {
        if (participants.size() >= total) {
            throw new GameException(ExceptionCode.ROOM_FULL);
        }
        Optional<Participant> sameName = participants.stream()
                .filter(p -> p.getName().equals(participant.getName()))
                .findFirst();
        if (sameName.isPresent()){
            throw new GameException(ExceptionCode.INVALID_NAMES);
        }
    }
}

package mafia.mafiatogether.room.dto.request;

import mafia.mafiatogether.room.domain.RoomInfo;

public record RoomCreateRequest(
        Integer total,
        Integer mafia,
        Integer doctor,
        Integer police
) {

    public RoomInfo toDomain() {
        return RoomInfo.of(total, mafia, doctor, police);
    }
}

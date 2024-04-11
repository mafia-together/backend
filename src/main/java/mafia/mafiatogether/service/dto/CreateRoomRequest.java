package mafia.mafiatogether.service.dto;

import mafia.mafiatogether.domain.RoomInfo;

public record CreateRoomRequest(
        Integer total,
        Integer mafia,
        Integer doctor,
        Integer police
) {

    public RoomInfo toDomain() {
        return new RoomInfo(total, mafia, doctor, police);
    }
}

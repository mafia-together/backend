package mafia.mafiatogether.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RoomInfo {

    private final int total;
    private final int mafia;
    private final int doctor;
    private final int police;
}

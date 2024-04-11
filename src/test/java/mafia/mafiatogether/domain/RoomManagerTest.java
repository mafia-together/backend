package mafia.mafiatogether.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class RoomManagerTest {

    @Test
    void 중복되지_않은_방을_생성할_수_있다() {
        RoomManager roomManager = new RoomManager();
        RoomInfo roomInfo = new RoomInfo(5, 1, 1, 1);
        String roomA = roomManager.create(roomInfo);
        String roomB = roomManager.create(roomInfo);

        Assertions.assertThat(roomA).isNotEqualTo(roomB);
    }
}

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

    @Test
    void 방_코드를_검증할_수_있다() {
        //given
        RoomManager roomManager = new RoomManager();
        RoomInfo roomInfo = new RoomInfo(5, 1, 1, 1);
        String roomA = roomManager.create(roomInfo);

        //when
        boolean result = roomManager.validateCode(roomA);

        //then
        Assertions.assertThat(result).isEqualTo(false);
    }
}

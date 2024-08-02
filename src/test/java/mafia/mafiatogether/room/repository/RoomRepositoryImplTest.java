package mafia.mafiatogether.room.repository;

import mafia.mafiatogether.room.domain.RoomInfo;
import mafia.mafiatogether.room.domain.RoomRepositoryImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
class RoomRepositoryImplTest {

    @Test
    void 중복되지_않은_방을_생성할_수_있다() {
        RoomRepositoryImpl roomRepositoryImpl = new RoomRepositoryImpl();
        RoomInfo roomInfo = new RoomInfo(5, 1, 1, 1);
        String roomA = roomRepositoryImpl.create(roomInfo);
        String roomB = roomRepositoryImpl.create(roomInfo);

        Assertions.assertThat(roomA).isNotEqualTo(roomB);
    }

    @Test
    void 방_코드를_검증할_수_있다() {
        //given
        RoomRepositoryImpl roomRepositoryImpl = new RoomRepositoryImpl();
        RoomInfo roomInfo = new RoomInfo(5, 1, 1, 1);
        String roomA = roomRepositoryImpl.create(roomInfo);

        //when
        boolean result = roomRepositoryImpl.validateCode(roomA);

        //then
        Assertions.assertThat(result).isTrue();
    }
}

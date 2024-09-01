package mafia.mafiatogether.room.domain;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.Instant;
import java.util.List;
import mafia.mafiatogether.room.application.RoomRemoveService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class RoomTest {

    @MockBean
    private RoomRepository roomRepository;
    @Autowired
    private RoomRemoveService roomRemoveService;

    @Test
    void 스케줄러_동작_테스트() {
        Room mockRoom = Mockito.mock(Room.class);
        Mockito.when(mockRoom.getLastUpdateTime()).thenReturn(Instant.now().getEpochSecond() - 3700);
        Mockito.when(roomRepository.findAll()).thenReturn(List.of(mockRoom));

        roomRemoveService.remove();

        verify(roomRepository, times(1)).delete(mockRoom);
    }
}

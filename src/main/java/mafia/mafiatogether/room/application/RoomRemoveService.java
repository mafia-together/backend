package mafia.mafiatogether.room.application;

import java.time.Clock;
import lombok.RequiredArgsConstructor;
import mafia.mafiatogether.room.domain.Room;
import mafia.mafiatogether.room.domain.RoomRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoomRemoveService {

    private static final int ONE_HOUR = 3600;
    private final RoomRepository roomRepository;

    @Scheduled(cron = "0 */10 * * * *")
    public void remove() {
        for (Room room : roomRepository.findAll()) {

            if (Clock.systemDefaultZone().millis() - room.getLastUpdateTime() > ONE_HOUR) {
                roomRepository.delete(room);
            }
        }
    }
}

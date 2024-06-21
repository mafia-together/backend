package mafia.mafiatogether.listener;

import lombok.RequiredArgsConstructor;
import mafia.mafiatogether.domain.Room;
import mafia.mafiatogether.domain.RoomUpdateEvent;
import mafia.mafiatogether.repository.RoomRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoomEventListener {

    private final RoomRepository roomRepository;

    @EventListener
    public void updateRoom(final RoomUpdateEvent roomUpdateEvent){
        final Room room = roomUpdateEvent.getRoom();
        roomRepository.save(room);
    }
}

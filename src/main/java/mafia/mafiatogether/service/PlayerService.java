package mafia.mafiatogether.service;

import lombok.RequiredArgsConstructor;
import mafia.mafiatogether.domain.Player;
import mafia.mafiatogether.domain.Room;
import mafia.mafiatogether.domain.RoomManager;
import mafia.mafiatogether.service.dto.RoleResponse;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlayerService {

    private final RoomManager roomManager;

    public RoleResponse getPlayerRole(final String code, final String name) {
        final Room room = roomManager.findByCode(code);
        final Player player = room.getPlayers().get(name);
        return new RoleResponse(player.getRole());
    }
}

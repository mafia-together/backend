package mafia.mafiatogether.service;

import lombok.RequiredArgsConstructor;
import mafia.mafiatogether.domain.Player;
import mafia.mafiatogether.domain.Room;
import mafia.mafiatogether.domain.RoomManager;
import mafia.mafiatogether.domain.role.Mafia;
import mafia.mafiatogether.domain.role.RoleSymbol;
import mafia.mafiatogether.service.dto.MafiaTargetResponse;
import mafia.mafiatogether.service.dto.PlayerExecuteAbilityRequest;
import mafia.mafiatogether.service.dto.PlayerExecuteAbilityResponse;
import mafia.mafiatogether.service.dto.RoleResponse;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlayerService {

    private final RoomManager roomManager;

    public RoleResponse getPlayerRole(final String code, final String name) {
        final Room room = roomManager.findByCode(code);
        final Player player = room.getPlayer(name);
        return new RoleResponse(player.getRoleSymbol().name());
    }

    public PlayerExecuteAbilityResponse executeAbility(
            final String code,
            final String name,
            final PlayerExecuteAbilityRequest request
    ) {
        final Room room = roomManager.findByCode(code);
        final Player target = room.getPlayer(request.target());
        final Player player = room.getPlayer(name);
        final String result = room.executeAbility(name, target);

        return new PlayerExecuteAbilityResponse(player.getRoleSymbol().name(), result);
    }

    public MafiaTargetResponse getTarget(
            final String code,
            final String name
    ) {
        final Room room = roomManager.findByCode(code);
        final Player player = room.getPlayer(name);

        if(player.getRoleSymbol()!= RoleSymbol.MAFIA){
            throw new IllegalArgumentException("마피아 직업이 아닙니다.");
        }

        Mafia mafia = (Mafia) player.getRole();
        Player target = mafia.getTarget();

        return new MafiaTargetResponse(target.getName());
    }
}

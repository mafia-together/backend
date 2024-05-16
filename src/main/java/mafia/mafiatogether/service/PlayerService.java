package mafia.mafiatogether.service;

import lombok.RequiredArgsConstructor;
import mafia.mafiatogether.domain.Player;
import mafia.mafiatogether.domain.Room;
import mafia.mafiatogether.domain.RoomManager;
import mafia.mafiatogether.service.dto.JobResponse;
import mafia.mafiatogether.service.dto.MafiaTargetResponse;
import mafia.mafiatogether.service.dto.PlayerExecuteAbilityRequest;
import mafia.mafiatogether.service.dto.PlayerExecuteAbilityResponse;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlayerService {

    private final RoomManager roomManager;

    public JobResponse getPlayerJob(final String code, final String name) {
        final Room room = roomManager.findByCode(code);
        final Player player = room.getPlayer(name);
        return new JobResponse(player.getJobType().name());
    }

    public PlayerExecuteAbilityResponse executeSkill(
            final String code,
            final String name,
            final PlayerExecuteAbilityRequest request
    ) {
        final Room room = roomManager.findByCode(code);
        final Player player = room.getPlayer(name);
        final String result = room.executeSkill(name, request.target());

        return new PlayerExecuteAbilityResponse(player.getJobType().name(), result);
    }

    public MafiaTargetResponse getTarget(
            final String code,
            final String name
    ) {
        final Room room = roomManager.findByCode(code);
        return new MafiaTargetResponse(room.getJobsTarget(name));
    }
}

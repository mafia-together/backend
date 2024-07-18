package mafia.mafiatogether.job.application;

import lombok.RequiredArgsConstructor;
import mafia.mafiatogether.job.domain.Player;
import mafia.mafiatogether.room.domain.Room;
import mafia.mafiatogether.room.domain.RoomManager;
import mafia.mafiatogether.job.application.dto.response.JobResponse;
import mafia.mafiatogether.job.application.dto.response.MafiaTargetResponse;
import mafia.mafiatogether.job.application.dto.request.PlayerExecuteAbilityRequest;
import mafia.mafiatogether.job.application.dto.response.PlayerExecuteAbilityResponse;
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

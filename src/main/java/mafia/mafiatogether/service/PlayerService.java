package mafia.mafiatogether.service;

import lombok.RequiredArgsConstructor;
import mafia.mafiatogether.config.exception.ExceptionCode;
import mafia.mafiatogether.config.exception.RoomException;
import mafia.mafiatogether.domain.Player;
import mafia.mafiatogether.domain.Room;
import mafia.mafiatogether.repository.RoomRepository;
import mafia.mafiatogether.service.dto.JobResponse;
import mafia.mafiatogether.service.dto.MafiaTargetResponse;
import mafia.mafiatogether.service.dto.PlayerExecuteAbilityRequest;
import mafia.mafiatogether.service.dto.PlayerExecuteAbilityResponse;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlayerService {

    private final RoomRepository roomRepository;

    public JobResponse getPlayerJob(final String code, final String name) {
        final Room room = roomRepository.findById(code)
                .orElseThrow(() -> new RoomException(ExceptionCode.INVALID_NOT_FOUND_ROOM_CODE));
        final Player player = room.getPlayer(name);
        return new JobResponse(player.getJobType().name());
    }

    public PlayerExecuteAbilityResponse executeSkill(
            final String code,
            final String name,
            final PlayerExecuteAbilityRequest request
    ) {
        final Room room = roomRepository.findById(code)
                .orElseThrow(() -> new RoomException(ExceptionCode.INVALID_NOT_FOUND_ROOM_CODE));
        final Player player = room.getPlayer(name);
        final String result = room.executeSkill(name, request.target());
        roomRepository.save(room);
        return new PlayerExecuteAbilityResponse(player.getJobType().name(), result);
    }

    public MafiaTargetResponse getTarget(
            final String code,
            final String name
    ) {
        final Room room = roomRepository.findById(code)
                .orElseThrow(() -> new RoomException(ExceptionCode.INVALID_NOT_FOUND_ROOM_CODE));
        return new MafiaTargetResponse(room.getJobsTarget(name));
    }
}

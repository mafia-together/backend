package mafia.mafiatogether.job.presentation;

import lombok.RequiredArgsConstructor;
import mafia.mafiatogether.config.PlayerInfo;
import mafia.mafiatogether.job.sevice.PlayerService;
import mafia.mafiatogether.job.dto.response.MafiaTargetResponse;
import mafia.mafiatogether.job.dto.request.PlayerExecuteAbilityRequest;
import mafia.mafiatogether.job.dto.response.PlayerExecuteAbilityResponse;
import mafia.mafiatogether.job.dto.PlayerInfoDto;
import mafia.mafiatogether.job.dto.response.JobResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/players")
@RequiredArgsConstructor
public class PlayerController {

    private final PlayerService playerService;

    @GetMapping("/my/job")
    public ResponseEntity<JobResponse> getJob(@PlayerInfo PlayerInfoDto playerInfoDto) {
        return ResponseEntity.ok(playerService.getPlayerJob(playerInfoDto.code(), playerInfoDto.name()));
    }

    @PostMapping("/skill")
    public ResponseEntity<PlayerExecuteAbilityResponse> executeSkill(
            @PlayerInfo PlayerInfoDto playerInfoDto,
            @RequestBody PlayerExecuteAbilityRequest request
    ) {
        return ResponseEntity.ok(playerService.executeSkill(
                playerInfoDto.code(),
                playerInfoDto.name(),
                request
        ));
    }

    @GetMapping("/skill")
    public ResponseEntity<MafiaTargetResponse> getTarget(
            @PlayerInfo PlayerInfoDto playerInfoDto
    ) {
        return ResponseEntity.ok(playerService.getTarget(
                playerInfoDto.code(),
                playerInfoDto.name()
        ));
    }
}

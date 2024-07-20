package mafia.mafiatogether.job.ui;

import lombok.RequiredArgsConstructor;
import mafia.mafiatogether.global.config.PlayerInfo;
import mafia.mafiatogether.job.application.PlayerService;
import mafia.mafiatogether.job.application.dto.response.MafiaTargetResponse;
import mafia.mafiatogether.job.application.dto.request.PlayerExecuteAbilityRequest;
import mafia.mafiatogether.job.application.dto.response.PlayerExecuteAbilityResponse;
import mafia.mafiatogether.job.application.dto.PlayerInfoDto;
import mafia.mafiatogether.job.application.dto.response.JobResponse;
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

package mafia.mafiatogether.controller;

import lombok.RequiredArgsConstructor;
import mafia.mafiatogether.service.PlayerService;
import mafia.mafiatogether.service.dto.MafiaTargetResponse;
import mafia.mafiatogether.service.dto.PlayerExecuteAbilityRequest;
import mafia.mafiatogether.service.dto.PlayerExecuteAbilityResponse;
import mafia.mafiatogether.service.dto.PlayerInfoDto;
import mafia.mafiatogether.service.dto.JobResponse;
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

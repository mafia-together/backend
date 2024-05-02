package mafia.mafiatogether.controller;

import lombok.RequiredArgsConstructor;
import mafia.mafiatogether.service.PlayerService;
import mafia.mafiatogether.service.dto.MafiaTargetResponse;
import mafia.mafiatogether.service.dto.PlayerExecuteAbilityRequest;
import mafia.mafiatogether.service.dto.PlayerExecuteAbilityResponse;
import mafia.mafiatogether.service.dto.PlayerInfoDto;
import mafia.mafiatogether.service.dto.RoleResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/player")
@RequiredArgsConstructor
public class PlayerController {

    private final PlayerService playerService;

    @GetMapping("/role")
    public ResponseEntity<RoleResponse> getRole(@PlayerInfo PlayerInfoDto playerInfoDto) {
        return ResponseEntity.ok(playerService.getPlayerRole(playerInfoDto.code(), playerInfoDto.name()));
    }

    @PostMapping("/night")
    public ResponseEntity<PlayerExecuteAbilityResponse> executeAbility(
            @PlayerInfo PlayerInfoDto playerInfoDto,
            @RequestBody PlayerExecuteAbilityRequest request
    ) {
        return ResponseEntity.ok(playerService.executeAbility(
                playerInfoDto.code(),
                playerInfoDto.name(),
                request
        ));
    }

    @GetMapping("/night")
    public ResponseEntity<MafiaTargetResponse> getTarget(
            @PlayerInfo PlayerInfoDto playerInfoDto
    ) {
        return ResponseEntity.ok(playerService.getTarget(

                playerInfoDto.code(),
                playerInfoDto.name()
        ));
    }
}

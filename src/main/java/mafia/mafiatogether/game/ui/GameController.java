package mafia.mafiatogether.game.ui;

import lombok.RequiredArgsConstructor;
import mafia.mafiatogether.config.PlayerInfo;
import mafia.mafiatogether.game.application.GameService;
import mafia.mafiatogether.config.PlayerInfoDto;
import mafia.mafiatogether.game.application.dto.request.GameStartRequest;
import mafia.mafiatogether.game.application.dto.response.GameInfoResponse;
import mafia.mafiatogether.game.application.dto.response.GameResultResponse;
import mafia.mafiatogether.game.application.dto.response.GameStatusResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/games")
public class GameController {

    private final GameService gameService;

    @GetMapping("/status")
    public ResponseEntity<GameStatusResponse> findStatus(
            @PlayerInfo final PlayerInfoDto playerInfoDto
    ) {
        return ResponseEntity.ok(gameService.findStatus(playerInfoDto.code()));
    }

    @PatchMapping("/status")
    public ResponseEntity<Void> modifyStatus(
            @PlayerInfo final PlayerInfoDto playerInfoDto,
            @RequestBody final GameStartRequest request
    ) {
        gameService.modifyStatus(playerInfoDto.code());
        return ResponseEntity.ok().build();
    }


    @GetMapping("/result")
    public ResponseEntity<GameResultResponse> findResult(
            @PlayerInfo final PlayerInfoDto playerInfoDto
    ) {
        return ResponseEntity.ok(gameService.findResult(playerInfoDto.code()));
    }

    @GetMapping("/info")
    public ResponseEntity<GameInfoResponse> findGameInfo(
            @PlayerInfo PlayerInfoDto playerInfoDto
    ) {
        return ResponseEntity.ok(gameService.findGameInfo(playerInfoDto.code(), playerInfoDto.name()));
    }
}

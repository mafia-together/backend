package mafia.mafiatogether.game.ui;

import lombok.RequiredArgsConstructor;
import mafia.mafiatogether.config.PlayerInfo;
import mafia.mafiatogether.config.PlayerInfoDto;
import mafia.mafiatogether.game.application.GameService;
import mafia.mafiatogether.game.application.dto.response.GameExistResponse;
import mafia.mafiatogether.game.application.dto.response.GameInfoResponse;
import mafia.mafiatogether.game.application.dto.response.GameResultResponse;
import mafia.mafiatogether.game.application.dto.response.GameStatusResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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

    @PostMapping("/start")
    public ResponseEntity<Void> startGame(
            @PlayerInfo final PlayerInfoDto playerInfoDto
    ) {
        gameService.startGame(playerInfoDto.code());
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


    @GetMapping("/exist")
    public ResponseEntity<GameExistResponse> isGameExist(
            @PlayerInfo final PlayerInfoDto playerInfoDto
    ) {
        return ResponseEntity.ok(
                new GameExistResponse(gameService.isValid(playerInfoDto.code(), playerInfoDto.name()))
        );
    }
}

package mafia.mafiatogether.game.ui;

import java.io.IOException;
import lombok.RequiredArgsConstructor;
import mafia.mafiatogether.common.annotation.PlayerInfo;
import mafia.mafiatogether.game.application.GameService;
import mafia.mafiatogether.common.resolver.PlayerInfoDto;
import mafia.mafiatogether.game.application.dto.response.GameExistResponse;
import mafia.mafiatogether.game.application.dto.response.GameInfoResponse;
import mafia.mafiatogether.game.application.dto.response.GameResultResponse;
import mafia.mafiatogether.game.application.dto.response.GameStatusResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

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

    @GetMapping(path = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> subscribe(
            @PlayerInfo final PlayerInfoDto playerInfoDto
    ) throws IOException {
        return ResponseEntity.ok(gameService.subscribe(playerInfoDto.code()));
    }


    @GetMapping("/valid")
    public ResponseEntity<GameExistResponse> isValid(
            @PlayerInfo final PlayerInfoDto playerInfoDto
    ) {
        return ResponseEntity.ok(
                new GameExistResponse(gameService.isValid(playerInfoDto.code(), playerInfoDto.name()))
        );
    }
}

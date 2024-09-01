package mafia.mafiatogether.lobby.ui;

import java.util.Base64;
import lombok.RequiredArgsConstructor;
import mafia.mafiatogether.config.PlayerInfo;
import mafia.mafiatogether.config.PlayerInfoDto;
import mafia.mafiatogether.lobby.application.LobbyService;
import mafia.mafiatogether.lobby.application.dto.request.LobbyCreateRequest;
import mafia.mafiatogether.lobby.application.dto.response.LobbyAuthResponse;
import mafia.mafiatogether.lobby.application.dto.response.LobbyCodeResponse;
import mafia.mafiatogether.lobby.application.dto.response.LobbyValidateResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/lobbies")
public class LobbyController {

    private final LobbyService lobbyService;

    @PostMapping
    public ResponseEntity<LobbyCodeResponse> create(@RequestBody final LobbyCreateRequest request) {
        return ResponseEntity.ok(lobbyService.create(request));
    }

    @GetMapping
    public ResponseEntity<LobbyAuthResponse> join(
            @RequestParam("code") final String code,
            @RequestParam("name") final String name
    ) {
        lobbyService.join(code, name);
        String encodedStr = Base64.getEncoder().encodeToString((code + ":" + name).getBytes());
        return ResponseEntity.ok(new LobbyAuthResponse(encodedStr));
    }

    @GetMapping("/code")
    public ResponseEntity<LobbyCodeResponse> findCode(
            @PlayerInfo PlayerInfoDto playerInfoDto
    ) {
        return ResponseEntity.ok(new LobbyCodeResponse(playerInfoDto.code()));
    }

    @GetMapping("/code/exist")
    public ResponseEntity<LobbyValidateResponse> validateCode(
            @RequestParam("code") final String code
    ) {
        return ResponseEntity.ok(lobbyService.validateCode(code));
    }
}

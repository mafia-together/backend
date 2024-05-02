package mafia.mafiatogether.controller;

import lombok.RequiredArgsConstructor;
import mafia.mafiatogether.service.VoteService;
import mafia.mafiatogether.service.dto.PlayerInfoDto;
import mafia.mafiatogether.service.dto.VoteRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/vote")
public class VoteController {

    private final VoteService voteService;

    @PostMapping
    public ResponseEntity<Void> votePlayer(
            @PlayerInfo final PlayerInfoDto playerInfoDto,
            @RequestBody final VoteRequest voteRequest
    ) {
        voteService.votePlayer(playerInfoDto.code(), playerInfoDto.name(), voteRequest.name());
        return ResponseEntity.noContent().build();
    }
}

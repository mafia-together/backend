package mafia.mafiatogether.vote.ui;

import lombok.RequiredArgsConstructor;
import mafia.mafiatogether.config.PlayerInfo;
import mafia.mafiatogether.vote.application.VoteService;
import mafia.mafiatogether.job.application.dto.PlayerInfoDto;
import mafia.mafiatogether.vote.application.dto.request.VoteRequest;
import mafia.mafiatogether.vote.application.dto.response.VoteResultResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
        voteService.votePlayer(playerInfoDto.code(), playerInfoDto.name(), voteRequest.target());
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<VoteResultResponse> getResult(
            @PlayerInfo final PlayerInfoDto playerInfoDto
    ) {
        final VoteResultResponse voteResultResponse = voteService.getResult(playerInfoDto.code());
        return ResponseEntity.ok(voteResultResponse);
    }
}

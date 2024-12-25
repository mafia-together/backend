package mafia.mafiatogether.job.ui;

import lombok.RequiredArgsConstructor;
import mafia.mafiatogether.chat.annotation.SendToChatWithRedis;
import mafia.mafiatogether.chat.domain.Message;
import mafia.mafiatogether.common.annotation.PlayerInfo;
import mafia.mafiatogether.common.resolver.PlayerInfoDto;
import mafia.mafiatogether.job.application.JobService;
import mafia.mafiatogether.job.application.dto.request.JobExecuteAbilityRequest;
import mafia.mafiatogether.job.application.dto.response.JobExecuteAbilityResponse;
import mafia.mafiatogether.job.application.dto.response.JobResponse;
import mafia.mafiatogether.job.application.dto.response.JobResultResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/jobs")
public class JobController {

    private final JobService jobService;

    @GetMapping("/my")
    public ResponseEntity<JobResponse> getJob(@PlayerInfo PlayerInfoDto playerInfoDto) {
        return ResponseEntity.ok(jobService.getPlayerJob(playerInfoDto.code(), playerInfoDto.name()));
    }

    @MessageMapping("/skill/{code}/{name}")
    @SendToChatWithRedis("/sub/mafia/{code}")
    public Message executeSkill(
            @DestinationVariable("code") String code,
            @DestinationVariable("name") String name,
            @Payload JobExecuteAbilityRequest request
    ) {
        JobExecuteAbilityResponse response = jobService.executeSkill(code, name, request);
        return Message.ofChat(response.job(), response.result());
    }

    @PostMapping("/skill")
    public ResponseEntity<JobExecuteAbilityResponse> executeSkill(
            @PlayerInfo PlayerInfoDto playerInfoDto,
            @RequestBody JobExecuteAbilityRequest request
    ) {
        return ResponseEntity.ok(jobService.executeSkill(
                playerInfoDto.code(),
                playerInfoDto.name(),
                request
        ));
    }

    @GetMapping("/skill/result")
    public ResponseEntity<JobResultResponse> findNightResult(
            @PlayerInfo final PlayerInfoDto playerInfoDto
    ) {
        return ResponseEntity.ok(jobService.findJobResult(playerInfoDto.code()));
    }
}

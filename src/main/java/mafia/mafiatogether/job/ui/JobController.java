package mafia.mafiatogether.job.ui;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Base64;
import lombok.RequiredArgsConstructor;
import mafia.mafiatogether.common.annotation.PlayerInfo;
import mafia.mafiatogether.common.resolver.PlayerInfoDto;
import mafia.mafiatogether.job.application.JobService;
import mafia.mafiatogether.job.application.dto.request.JobExecuteAbilityRequest;
import mafia.mafiatogether.job.application.dto.response.JobExecuteAbilityResponse;
import mafia.mafiatogether.job.application.dto.response.JobResponse;
import mafia.mafiatogether.job.application.dto.response.JobResultResponse;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
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

    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;
    private final JobService jobService;

    @GetMapping("/my")
    public ResponseEntity<JobResponse> getJob(@PlayerInfo PlayerInfoDto playerInfoDto) {
        return ResponseEntity.ok(jobService.getPlayerJob(playerInfoDto.code(), playerInfoDto.name()));
    }

    @MessageMapping("/jobs/skill")
    public void executeWebSocketSkill(
            @PlayerInfo PlayerInfoDto playerInfoDto,
            @Payload JobExecuteAbilityRequest request
    ) throws JsonProcessingException {
        JobExecuteAbilityResponse response = jobService.executeSkill(playerInfoDto.code(), playerInfoDto.name(),
                request);

        stringRedisTemplate.convertAndSend(
                String.format("/sub/jobs/skill/%s/%s", response.job().toLowerCase(),playerInfoDto.code()),
                objectMapper.writeValueAsString(response)
        );
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

package mafia.mafiatogether.job.ui;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import mafia.mafiatogether.common.annotation.PlayerInfo;
import mafia.mafiatogether.common.resolver.PlayerInfoDto;
import mafia.mafiatogether.job.application.JobService;
import mafia.mafiatogether.job.application.dto.request.JobExecuteAbilityRequest;
import mafia.mafiatogether.job.application.dto.response.JobResponse;
import mafia.mafiatogether.job.application.dto.response.MafiaTargetResponse;
import mafia.mafiatogether.job.application.dto.response.JobExecuteAbilityResponse;
import mafia.mafiatogether.job.application.dto.response.JobResultResponse;
import org.springframework.data.redis.core.StringRedisTemplate;
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
    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    @GetMapping("/my")
    public ResponseEntity<JobResponse> getJob(@PlayerInfo PlayerInfoDto playerInfoDto) {
        return ResponseEntity.ok(jobService.getPlayerJob(playerInfoDto.code(), playerInfoDto.name()));
    }

    @MessageMapping("/skill/{code}/{name}")
    public void executeSkillV2(
            @DestinationVariable("code") String code,
            @DestinationVariable("name") String name,
            @Payload JobExecuteAbilityRequest request
    ) throws JsonProcessingException {
        JobExecuteAbilityResponse response = jobService.executeSkill(code, name, request);
        String message = objectMapper.writeValueAsString(response);
        stringRedisTemplate.convertAndSend("/sub/jobs/skill/" + response.job().toLowerCase() + "/" + code, message);
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

    @GetMapping("/skill")
    public ResponseEntity<MafiaTargetResponse> getTarget(
            @PlayerInfo PlayerInfoDto playerInfoDto
    ) {
        return ResponseEntity.ok(jobService.getTarget(
                playerInfoDto.code(),
                playerInfoDto.name()
        ));
    }

    @GetMapping("/skill/result")
    public ResponseEntity<JobResultResponse> findNightResult(
            @PlayerInfo final PlayerInfoDto playerInfoDto
    ) {
        return ResponseEntity.ok(jobService.findJobResult(playerInfoDto.code()));
    }
}

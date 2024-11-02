package mafia.mafiatogether.job.ui;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mafia.mafiatogether.job.application.JobService;
import mafia.mafiatogether.job.application.dto.request.JobExecuteAbilityRequest;
import mafia.mafiatogether.job.application.dto.response.JobExecuteAbilityResponse;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class JobV2Controller {

    private final StringRedisTemplate stringRedisTemplate;
    private final JobService jobService;
    private final ObjectMapper objectMapper;

    @MessageMapping("/jab/skill/{code}/{name}")
    public void executeSkill(
            @DestinationVariable("code") String code,
            @DestinationVariable("name") String name,
            @Payload JobExecuteAbilityRequest request
    ) throws JsonProcessingException {
        JobExecuteAbilityResponse response = jobService.executeSkill(code, name, request);
        String message = objectMapper.writeValueAsString(response);
        stringRedisTemplate.convertAndSend("/sub/jab/skill/" + response.job().toLowerCase() + "/" + code, message);
    }

}

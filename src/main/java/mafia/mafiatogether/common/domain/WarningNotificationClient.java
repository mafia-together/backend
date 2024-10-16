package mafia.mafiatogether.common.domain;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(
        name = "discord-warning-notification-client",
        url = "${discord.warning.webhook-url}"
)
@Component
public interface WarningNotificationClient {

    @PostMapping(produces = {"application/json"}, consumes = {"application/json"})
    void notifyWarning(@RequestBody DiscordMessage message);

}

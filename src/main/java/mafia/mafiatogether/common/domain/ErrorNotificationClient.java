package mafia.mafiatogether.common.domain;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(
        name = "discord-error-notification-client",
        url = "${discord.error.webhook-url}"
)
public interface ErrorNotificationClient {

    @PostMapping(produces = {"application/json"}, consumes = {"application/json"})
    void notifyError(@RequestBody DiscordMessage message);

}

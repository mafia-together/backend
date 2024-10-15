package mafia.mafiatogether.common.application;

import lombok.RequiredArgsConstructor;
import mafia.mafiatogether.common.domain.DiscordMessage;
import mafia.mafiatogether.common.domain.ErrorNotificationClient;
import mafia.mafiatogether.common.domain.WarningNotificationClient;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ErrorNotificationService {

    private final ErrorNotificationClient errorNotificationClient;
    private final WarningNotificationClient warningNotificationClient;

    public void notifyError(boolean isError, String exceptionMessage) {
        if (isError) {
            errorNotificationClient.notifyError(DiscordMessage.createErrorDiscordMessage(exceptionMessage));
            return;
        }

        warningNotificationClient.notifyWarning(DiscordMessage.createWarningDiscordMessage(exceptionMessage));
    }

}

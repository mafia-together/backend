package mafia.mafiatogether.lobby.application;

import java.time.Clock;
import lombok.RequiredArgsConstructor;
import mafia.mafiatogether.lobby.domain.Lobby;
import mafia.mafiatogether.lobby.domain.LobbyRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LobbyRemoveService {

    private static final int TWELVE_HOUR = 43200;
    private final LobbyRepository lobbyRepository;

    @Scheduled(cron = "0 */10 * * * *")
    public void remove() {
        for (Lobby lobby : lobbyRepository.findAll()) {
            if (Clock.systemDefaultZone().millis() - lobby.getLastUpdateTime() > TWELVE_HOUR) {
                lobbyRepository.delete(lobby);
            }
        }
    }
}

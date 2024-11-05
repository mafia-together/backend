package mafia.mafiatogether.lobby.application;

import lombok.RequiredArgsConstructor;
import mafia.mafiatogether.common.application.SseEventPublisher;
import mafia.mafiatogether.lobby.application.dto.event.ParticipantJoinEvent;
import mafia.mafiatogether.lobby.application.dto.response.LobbyInfoResponse;
import mafia.mafiatogether.lobby.domain.Lobby;
import mafia.mafiatogether.lobby.domain.Participant;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class LobbyEventListener {

    private static final String LOBBY_EVENT_NAME = "lobbyInfo";
    private final SseEventPublisher sseEventPublisher;

    @EventListener
    public void handleJoinEvent(final ParticipantJoinEvent participantJoinEvent) {
        Lobby lobby = participantJoinEvent.lobby();
        String ignoreName = participantJoinEvent.name();
        List<Participant> participants = lobby.getParticipants()
                .getParticipants();


        for (Participant participant : participants) {
            String eachParticipantName = participant.getName();
            sendEvent(lobby, eachParticipantName, ignoreName);
        }
    }

    private void sendEvent(Lobby lobby, String participantName, String ignoreName) {
        if (participantName.equals(ignoreName)) {
            return;
        }
        final String code = lobby.getCode();
        final LobbyInfoResponse lobbyInfoResponse = LobbyInfoResponse.of(lobby, participantName);
        sseEventPublisher.publishEventByCodeAndName(code, participantName, LOBBY_EVENT_NAME, lobbyInfoResponse);
    }
}

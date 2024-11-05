package mafia.mafiatogether.lobby.application;

import lombok.RequiredArgsConstructor;
import mafia.mafiatogether.common.domain.SseEmitterRepository;
import mafia.mafiatogether.lobby.application.dto.event.ParticipantJoinEvent;
import mafia.mafiatogether.lobby.application.dto.response.LobbyInfoResponse;
import mafia.mafiatogether.lobby.domain.Lobby;
import mafia.mafiatogether.lobby.domain.Participant;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class LobbyEventListener {

    private static final String LOBBY_EVENT_NAME = "lobbyInfo";
    private final SseEmitterRepository sseEmitterRepository;

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
        String code = lobby.getCode();
        SseEmitter sseEmitter = sseEmitterRepository.findByCodeAndName(code, participantName);
        try {
            sseEmitter.send(getSseEvent(lobby, participantName));
        } catch (IOException e) {
            sseEmitter.completeWithError(e);
        }
    }

    private SseEmitter.SseEventBuilder getSseEvent(Lobby lobby, String eachParticipantName) {
        return SseEmitter.event()
                .name(LOBBY_EVENT_NAME)
                .data(LobbyInfoResponse.of(lobby, eachParticipantName));
    }

}

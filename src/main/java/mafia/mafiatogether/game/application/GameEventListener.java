package mafia.mafiatogether.game.application;

import lombok.RequiredArgsConstructor;
import mafia.mafiatogether.chat.domain.Chat;
import mafia.mafiatogether.chat.domain.ChatRepository;
import mafia.mafiatogether.common.exception.ExceptionCode;
import mafia.mafiatogether.common.exception.GameException;
import mafia.mafiatogether.game.application.dto.event.*;
import mafia.mafiatogether.game.application.dto.response.GameStatusResponse;
import mafia.mafiatogether.game.domain.Game;
import mafia.mafiatogether.game.domain.GameRepository;
import mafia.mafiatogether.game.domain.Player;
import mafia.mafiatogether.game.domain.SseEmitterRepository;
import mafia.mafiatogether.game.domain.status.StatusType;
import mafia.mafiatogether.job.domain.JobTarget;
import mafia.mafiatogether.job.domain.JobTargetRepository;
import mafia.mafiatogether.job.domain.PlayerJob;
import mafia.mafiatogether.job.domain.PlayerJobRepository;
import mafia.mafiatogether.lobby.application.dto.event.DeleteLobbyEvent;
import mafia.mafiatogether.lobby.domain.Lobby;
import mafia.mafiatogether.lobby.domain.LobbyRepository;
import mafia.mafiatogether.vote.application.dto.event.AllPlayerVotedEvent;
import mafia.mafiatogether.vote.domain.Vote;
import mafia.mafiatogether.vote.domain.VoteRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter.SseEventBuilder;

import java.io.IOException;
import java.time.Clock;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GameEventListener {

    private final GameRepository gameRepository;
    private final VoteRepository voteRepository;
    private final LobbyRepository lobbyRepository;
    private final JobTargetRepository jobTargetRepository;
    private final PlayerJobRepository playerJobRepository;
    private final ChatRepository chatRepository;
    private final SseEmitterRepository sseEmitterRepository;

    @EventListener
    public void listenVoteExecuteEvent(final VoteExecuteEvent voteExecuteEvent) {
        final Game game = gameRepository.findById(voteExecuteEvent.code())
                .orElseThrow(() -> new GameException(ExceptionCode.INVALID_NOT_FOUND_ROOM_CODE));
        final Vote vote = voteRepository.findById(voteExecuteEvent.code())
                .orElseThrow(() -> new GameException(ExceptionCode.INVALID_NOT_FOUND_ROOM_CODE));
        final String target = vote.countVotes();
        game.executeTarget(target);
        gameRepository.save(game);
    }

    @EventListener
    public void listenClearVoteEvent(final ClearVoteEvent clearVoteEvent) {
        final Vote vote = voteRepository.findById(clearVoteEvent.code())
                .orElseThrow(() -> new GameException(ExceptionCode.INVALID_NOT_FOUND_ROOM_CODE));
        vote.clearVoteTargets();
        voteRepository.save(vote);
    }

    @EventListener
    public void listenJobExecuteEvent(final JobExecuteEvent jobExecuteEvent) {
        final Game game = gameRepository.findById(jobExecuteEvent.code())
                .orElseThrow(() -> new GameException(ExceptionCode.INVALID_NOT_FOUND_ROOM_CODE));
        final JobTarget jobTarget = jobTargetRepository.findById(jobExecuteEvent.code())
                .orElseThrow(() -> new GameException(ExceptionCode.INVALID_NOT_FOUND_ROOM_CODE));
        final String target = jobTarget.findTarget();
        game.executeTarget(target);
        gameRepository.save(game);
    }

    @EventListener
    public void listenClearJobTargetEvent(final ClearJobTargetEvent clearJobTargetEvent) {
        final JobTarget jobTarget = jobTargetRepository.findById(clearJobTargetEvent.code())
                .orElseThrow(() -> new GameException(ExceptionCode.INVALID_NOT_FOUND_ROOM_CODE));
        jobTarget.clearJobTargets();
        jobTargetRepository.save(jobTarget);
    }

    @EventListener
    public void listenStartGameEvent(final StartGameEvent startGameEvent) {
        final PlayerJob playerJob = new PlayerJob(startGameEvent.code(), new HashMap<>());
        for (Player player : startGameEvent.playerCollection().getPlayers()) {
            playerJob.add(player.getName(), player.getJob());
        }
        final Chat chat = new Chat(startGameEvent.code(), new ArrayList<>());
        final Vote vote = new Vote(startGameEvent.code(), new HashMap<>());
        final JobTarget jobTarget = new JobTarget(startGameEvent.code(), new HashMap<>());
        playerJobRepository.save(playerJob);
        chatRepository.save(chat);
        voteRepository.save(vote);
        jobTargetRepository.save(jobTarget);
    }

    @EventListener
    public void listenDeleteGameEvent(final DeleteGameEvent deleteGameEvent) {
        playerJobRepository.deleteById(deleteGameEvent.code());
        jobTargetRepository.deleteById(deleteGameEvent.code());
        chatRepository.deleteById(deleteGameEvent.code());
        voteRepository.deleteById(deleteGameEvent.code());
        sseEmitterRepository.deleteByCode(deleteGameEvent.code());
        gameRepository.deleteById(deleteGameEvent.code());

        final Lobby room = lobbyRepository.findById(deleteGameEvent.code())
                .orElseThrow(() -> new GameException(ExceptionCode.INVALID_NOT_FOUND_ROOM_CODE));
        room.updateLastUpdateTime();
    }

    @EventListener
    public void listenAllPlayerVoteEvent(final AllPlayerVotedEvent allPlayerVotedEvent) throws IOException {
        final Game game = gameRepository.findById(allPlayerVotedEvent.code())
                .orElseThrow(() -> new GameException(ExceptionCode.INVALID_NOT_FOUND_ROOM_CODE));
        if (!game.getStatus().getType().equals(StatusType.DAY)) {
            return;
        }
        final Vote vote = voteRepository.findById(allPlayerVotedEvent.code())
                .orElseThrow(() -> new GameException(ExceptionCode.INVALID_NOT_FOUND_ROOM_CODE));
        if (game.getAlivePlayerCount() == vote.getVotedCount()) {
            game.skipStatus(Clock.systemDefaultZone().millis());
            sendStatusChangeEventToSseClient(allPlayerVotedEvent.code(), game.getStatus().getType());
            gameRepository.save(game);
        }
    }

    @EventListener
    public void listenDeleteLobbyEvent(final DeleteLobbyEvent deleteLobbyEvent) {
        playerJobRepository.deleteById(deleteLobbyEvent.code());
        jobTargetRepository.deleteById(deleteLobbyEvent.code());
        chatRepository.deleteById(deleteLobbyEvent.code());
        voteRepository.deleteById(deleteLobbyEvent.code());
        sseEmitterRepository.deleteByCode(deleteLobbyEvent.code());
        gameRepository.deleteById(deleteLobbyEvent.code());
        lobbyRepository.deleteById(deleteLobbyEvent.code());
    }

    @EventListener
    public void listenGameStatusChangeEvent(final GameStatusChangeEvent gameStatusChangeEvent) throws IOException {
        sendStatusChangeEventToSseClient(gameStatusChangeEvent.code(), gameStatusChangeEvent.statusType());
    }

    private void sendStatusChangeEventToSseClient(final String code, final StatusType statusType) throws IOException {
        List<SseEmitter> emitters = sseEmitterRepository.findByCode(code);
        for (SseEmitter emitter : emitters) {
            emitter.send(getSseEvent(statusType));
        }
    }

    private SseEventBuilder getSseEvent(StatusType statusType) {
        return SseEmitter.event()
                .name("gameStatus")
                .data(new GameStatusResponse(statusType));
    }
}

package mafia.mafiatogether.game.application;

import java.time.Clock;
import java.util.ArrayList;
import java.util.HashMap;
import lombok.RequiredArgsConstructor;
import mafia.mafiatogether.chat.domain.Chat;
import mafia.mafiatogether.chat.domain.ChatRepository;
import mafia.mafiatogether.config.exception.ExceptionCode;
import mafia.mafiatogether.config.exception.RoomException;
import mafia.mafiatogether.game.application.dto.event.ClearJobTargetEvent;
import mafia.mafiatogether.game.application.dto.event.ClearVoteEvent;
import mafia.mafiatogether.game.application.dto.event.DeleteGameEvent;
import mafia.mafiatogether.game.application.dto.event.JobExecuteEvent;
import mafia.mafiatogether.game.application.dto.event.StartGameEvent;
import mafia.mafiatogether.game.application.dto.event.VoteExecuteEvent;
import mafia.mafiatogether.game.domain.Game;
import mafia.mafiatogether.game.domain.GameRepository;
import mafia.mafiatogether.game.domain.Player;
import mafia.mafiatogether.game.domain.status.StatusType;
import mafia.mafiatogether.job.domain.PlayerJob;
import mafia.mafiatogether.job.domain.PlayerJobRepository;
import mafia.mafiatogether.job.domain.JobTarget;
import mafia.mafiatogether.job.domain.JobTargetRepository;
import mafia.mafiatogether.vote.application.dto.event.AllPlayerVotedEvent;
import mafia.mafiatogether.vote.domain.Vote;
import mafia.mafiatogether.vote.domain.VoteRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GameEventListener {

    private final GameRepository gameRepository;
    private final VoteRepository voteRepository;
    private final JobTargetRepository jobTargetRepository;
    private final PlayerJobRepository playerJobRepository;
    private final ChatRepository chatRepository;

    @EventListener
    public void listenVoteExecuteEvent(final VoteExecuteEvent voteExecuteEvent) {
        final Game game = gameRepository.findById(voteExecuteEvent.getCode())
                .orElseThrow(() -> new RoomException(ExceptionCode.INVALID_NOT_FOUND_ROOM_CODE));
        final Vote vote = voteRepository.findById(voteExecuteEvent.getCode())
                .orElseThrow(() -> new RoomException(ExceptionCode.INVALID_NOT_FOUND_ROOM_CODE));
        final String target = vote.countVotes();
        game.executeTarget(target);
        gameRepository.save(game);
    }

    @EventListener
    public void listenClearVoteEvent(final ClearVoteEvent clearVoteEvent) {
        final Vote vote = voteRepository.findById(clearVoteEvent.getCode())
                .orElseThrow(() -> new RoomException(ExceptionCode.INVALID_NOT_FOUND_ROOM_CODE));
        vote.clearVoteTargets();
        voteRepository.save(vote);
    }

    @EventListener
    public void listenJobExecuteEvent(final JobExecuteEvent jobExecuteEvent) {
        final Game game = gameRepository.findById(jobExecuteEvent.getCode())
                .orElseThrow(() -> new RoomException(ExceptionCode.INVALID_NOT_FOUND_ROOM_CODE));
        final JobTarget jobTarget = jobTargetRepository.findById(jobExecuteEvent.getCode())
                .orElseThrow(() -> new RoomException(ExceptionCode.INVALID_NOT_FOUND_ROOM_CODE));
        final String target = jobTarget.findTarget();
        game.executeTarget(target);
        gameRepository.save(game);
    }

    @EventListener
    public void listenClearJobTargetEvent(final ClearJobTargetEvent clearJobTargetEvent) {
        final JobTarget jobTarget = jobTargetRepository.findById(clearJobTargetEvent.getCode())
                .orElseThrow(() -> new RoomException(ExceptionCode.INVALID_NOT_FOUND_ROOM_CODE));
        jobTarget.clearJobTargets();
        jobTargetRepository.save(jobTarget);
    }

    @EventListener
    public void listenStartGameEvent(final StartGameEvent startGameEvent) {
        final PlayerJob playerJob = new PlayerJob(startGameEvent.getCode(), new HashMap<>());
        for (Player player : startGameEvent.getPlayerCollection().getPlayers()) {
            playerJob.add(player.getName(), player.getJob());
        }
        final Chat chat = new Chat(startGameEvent.getCode(), new ArrayList<>());
        final Vote vote = new Vote(startGameEvent.getCode(), new HashMap<>());
        final JobTarget jobTarget = new JobTarget(startGameEvent.getCode(), new HashMap<>());
        playerJobRepository.save(playerJob);
        chatRepository.save(chat);
        voteRepository.save(vote);
        jobTargetRepository.save(jobTarget);
    }

    @EventListener
    public void listenDeleteGameEvent(final DeleteGameEvent deleteGameEvent) {
        playerJobRepository.deleteById(deleteGameEvent.getCode());
        jobTargetRepository.deleteById(deleteGameEvent.getCode());
        chatRepository.deleteById(deleteGameEvent.getCode());
        voteRepository.deleteById(deleteGameEvent.getCode());
        gameRepository.deleteById(deleteGameEvent.getCode());
    }

    @EventListener
    public void listenAllPlayerVoteEvent(final AllPlayerVotedEvent allPlayerVotedEvent) {
        final Game game = gameRepository.findById(allPlayerVotedEvent.getCode())
                .orElseThrow(() -> new RoomException(ExceptionCode.INVALID_NOT_FOUND_ROOM_CODE));
        if (!game.getStatus().getType().equals(StatusType.DAY)) {
            return;
        }
        final Vote vote = voteRepository.findById(allPlayerVotedEvent.getCode())
                .orElseThrow(() -> new RoomException(ExceptionCode.INVALID_NOT_FOUND_ROOM_CODE));
        if (game.getAlivePlayerCount() == vote.getVotedCount()) {
            game.skipStatus(Clock.systemDefaultZone().millis());
            gameRepository.save(game);
        }
    }
}

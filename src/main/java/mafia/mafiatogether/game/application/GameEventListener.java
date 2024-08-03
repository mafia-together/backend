package mafia.mafiatogether.game.application;

import java.time.Clock;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import mafia.mafiatogether.chat.domain.Chat;
import mafia.mafiatogether.chat.domain.ChatRepository;
import mafia.mafiatogether.config.exception.ExceptionCode;
import mafia.mafiatogether.config.exception.RoomException;
import mafia.mafiatogether.game.application.dto.event.ClearJobTargetEvent;
import mafia.mafiatogether.game.application.dto.event.ClearVoteEvent;
import mafia.mafiatogether.game.application.dto.event.StartGameEvent;
import mafia.mafiatogether.game.application.dto.event.DeleteGameEvent;
import mafia.mafiatogether.game.domain.Game;
import mafia.mafiatogether.game.domain.GameRepository;
import mafia.mafiatogether.game.application.dto.event.JobExecuteEvent;
import mafia.mafiatogether.game.domain.Player;
import mafia.mafiatogether.game.application.dto.event.VoteExecuteEvent;
import mafia.mafiatogether.game.domain.status.StatusType;
import mafia.mafiatogether.job.domain.JobTarget;
import mafia.mafiatogether.job.domain.JobTargetRepository;
import mafia.mafiatogether.job.domain.PlayerJob;
import mafia.mafiatogether.job.domain.PlayerJobRepository;
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
    public void listenVoteExecuteEvent(final VoteExecuteEvent voteExecuteEvent){
        final Game game = gameRepository.findById(voteExecuteEvent.getCode())
                .orElseThrow(() -> new RoomException(ExceptionCode.INVALID_NOT_FOUND_ROOM_CODE));
        List<Vote> votes = voteRepository.findAllByCode(voteExecuteEvent.getCode());
        final String target = Vote.countVotes(votes);
        game.executeTarget(target);
        gameRepository.save(game);
    }

    @EventListener
    public void listenClearVoteEvent(final ClearVoteEvent clearVoteEvent){
        voteRepository.deleteAllByCode(clearVoteEvent.getCode());
    }

    @EventListener
    public void listenJobExecuteEvent(final JobExecuteEvent jobExecuteEvent){
        final Game game = gameRepository.findById(jobExecuteEvent.getCode())
                .orElseThrow(() -> new RoomException(ExceptionCode.INVALID_NOT_FOUND_ROOM_CODE));
        List<JobTarget> jobTargets = jobTargetRepository.findAllByCode(jobExecuteEvent.getCode());
        final String target = JobTarget.findTarget(jobTargets);
        game.executeTarget(target);
        gameRepository.save(game);
    }

    @EventListener
    public void listenClearJobTargetEvent(final ClearJobTargetEvent clearJobTargetEvent){
        jobTargetRepository.deleteAllByCode(clearJobTargetEvent.getCode());
    }

    @EventListener
    public void listenStartGameEvent(final StartGameEvent startGameEvent){
        for (Player player : startGameEvent.getPlayerCollection().getPlayers()){
            playerJobRepository.save(new PlayerJob(startGameEvent.getCode(), player.getName(), player.getJob()));
        }
        final Chat chat = new Chat(startGameEvent.getCode(), new ArrayList<>());
        chatRepository.save(chat);
    }

    @EventListener
    public void listenDeleteGameEvent(final DeleteGameEvent deleteGameEvent){
        playerJobRepository.deleteAllByCode(deleteGameEvent.getCode());
        jobTargetRepository.deleteAllByCode(deleteGameEvent.getCode());
        chatRepository.deleteById(deleteGameEvent.getCode());
        voteRepository.deleteAllByCode(deleteGameEvent.getCode());
        gameRepository.deleteById(deleteGameEvent.getCode());
    }

    @EventListener
    public void listenAllPlayerVoteEvent(final AllPlayerVotedEvent allPlayerVotedEvent){
        final Game game = gameRepository.findById(allPlayerVotedEvent.getCode())
                .orElseThrow(() -> new RoomException(ExceptionCode.INVALID_NOT_FOUND_ROOM_CODE));
        if (!game.getStatus().getType().equals(StatusType.DAY)){
            return;
        }
        final int votedCount = voteRepository.findAllByCode(allPlayerVotedEvent.getCode()).size();
        if (game.getAlivePlayerCount() == votedCount){
            game.skipStatus(Clock.systemDefaultZone().millis());
            gameRepository.save(game);
        }
    }
}

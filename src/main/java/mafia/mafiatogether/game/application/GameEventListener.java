package mafia.mafiatogether.game.application;

import java.util.List;
import lombok.RequiredArgsConstructor;
import mafia.mafiatogether.chat.domain.ChatRepository;
import mafia.mafiatogether.config.exception.ExceptionCode;
import mafia.mafiatogether.config.exception.RoomException;
import mafia.mafiatogether.game.application.dto.event.ClearJobTargetEvent;
import mafia.mafiatogether.game.application.dto.event.CreatePlayerJobEvent;
import mafia.mafiatogether.game.application.dto.event.DeleteGameEvent;
import mafia.mafiatogether.game.domain.Game;
import mafia.mafiatogether.game.domain.GameRepository;
import mafia.mafiatogether.game.application.dto.event.JobExecuteEvent;
import mafia.mafiatogether.game.domain.Player;
import mafia.mafiatogether.game.application.dto.event.VoteExecuteEvent;
import mafia.mafiatogether.job.domain.JobTarget;
import mafia.mafiatogether.job.domain.JobTargetRepository;
import mafia.mafiatogether.job.domain.PlayerJob;
import mafia.mafiatogether.job.domain.PlayerJobRepository;
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
        voteRepository.deleteAllByCode(voteExecuteEvent.getCode());
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
    public void listenCreatPlayerJobEvent(final CreatePlayerJobEvent createPlayerJobEvent){
        for (Player player : createPlayerJobEvent.getPlayerCollection().getPlayers()){
            playerJobRepository.save(new PlayerJob(createPlayerJobEvent.getCode(), player.getName(), player.getJob()));
        }
    }

    @EventListener
    public void listenDeleteGameEvent(final DeleteGameEvent deleteGameEvent){
        playerJobRepository.deleteAllByCode(deleteGameEvent.getCode());
        jobTargetRepository.deleteAllByCode(deleteGameEvent.getCode());
        chatRepository.deleteById(deleteGameEvent.getCode());
        voteRepository.deleteAllByCode(deleteGameEvent.getCode());
        gameRepository.deleteById(deleteGameEvent.getCode());
    }
}

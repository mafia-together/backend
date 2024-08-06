package mafia.mafiatogether.game.application;

import java.time.Clock;
import java.util.ArrayList;
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
import mafia.mafiatogether.job.domain.Skill;
import mafia.mafiatogether.job.domain.SkillRepository;
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
    private final SkillRepository skillRepository;
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
        final Skill skill = skillRepository.findById(jobExecuteEvent.getCode())
                .orElseThrow(() -> new RoomException(ExceptionCode.INVALID_NOT_FOUND_ROOM_CODE));
        final String target = skill.findTarget();
        game.executeTarget(target);
        gameRepository.save(game);
    }

    @EventListener
    public void listenClearJobTargetEvent(final ClearJobTargetEvent clearJobTargetEvent) {
        final Skill skill = skillRepository.findById(clearJobTargetEvent.getCode())
                .orElseThrow(() -> new RoomException(ExceptionCode.INVALID_NOT_FOUND_ROOM_CODE));
        skill.clearJobTargets();
        skillRepository.save(skill);
    }

    @EventListener
    public void listenStartGameEvent(final StartGameEvent startGameEvent) {
        for (Player player : startGameEvent.getPlayerCollection().getPlayers()) {
            playerJobRepository.save(new PlayerJob(startGameEvent.getCode(), player.getName(), player.getJob()));
        }
        final Chat chat = new Chat(startGameEvent.getCode(), new ArrayList<>());
        final Vote vote = new Vote(startGameEvent.getCode(), new ArrayList<>());
        final Skill skill = new Skill(startGameEvent.getCode(), new ArrayList<>());
        chatRepository.save(chat);
        voteRepository.save(vote);
        skillRepository.save(skill);
    }

    @EventListener
    public void listenDeleteGameEvent(final DeleteGameEvent deleteGameEvent) {
        playerJobRepository.deleteAllByCode(deleteGameEvent.getCode());
        skillRepository.deleteById(deleteGameEvent.getCode());
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

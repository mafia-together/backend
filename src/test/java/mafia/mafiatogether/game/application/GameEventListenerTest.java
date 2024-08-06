package mafia.mafiatogether.game.application;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.time.Clock;
import java.util.List;
import mafia.mafiatogether.chat.domain.ChatRepository;
import mafia.mafiatogether.game.domain.Game;
import mafia.mafiatogether.game.domain.GameRepository;
import mafia.mafiatogether.game.domain.Player;
import mafia.mafiatogether.game.domain.status.StatusType;
import mafia.mafiatogether.global.ControllerTest;
import mafia.mafiatogether.job.application.PlayerService;
import mafia.mafiatogether.job.application.dto.request.PlayerExecuteAbilityRequest;
import mafia.mafiatogether.job.domain.PlayerJob;
import mafia.mafiatogether.job.domain.PlayerJobRepository;
import mafia.mafiatogether.job.domain.Skill;
import mafia.mafiatogether.job.domain.SkillRepository;
import mafia.mafiatogether.job.domain.jobtype.JobType;
import mafia.mafiatogether.room.domain.Room;
import mafia.mafiatogether.room.domain.RoomInfo;
import mafia.mafiatogether.room.domain.RoomRepository;
import mafia.mafiatogether.vote.application.VoteService;
import mafia.mafiatogether.vote.domain.VoteRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("NonAsciiCharacters")
class GameEventListenerTest extends ControllerTest {

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private VoteService voteService;

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private PlayerService playerService;

    @Autowired
    private SkillRepository skillRepository;

    @Autowired
    private PlayerJobRepository playerJobRepository;

    @Autowired
    private ChatRepository chatRepository;

    private static final String CODE = "1234567890";
    private static final String PLAYER1_NAME = "player1";
    private static final String PLAYER2_NAME = "player2";
    private static final String PLAYER3_NAME = "player3";
    private static final String PLAYER4_NAME = "player4";
    private static final String PLAYER5_NAME = "player5";

    private Game game;

    @BeforeEach
    void setTest() {
        Room room = Room.create(CODE, RoomInfo.of(5, 2, 1, 1));
        room.joinPlayer(PLAYER1_NAME);
        room.joinPlayer(PLAYER2_NAME);
        room.joinPlayer(PLAYER3_NAME);
        room.joinPlayer(PLAYER4_NAME);
        room.joinPlayer(PLAYER5_NAME);
        roomRepository.save(room);
        game = Game.create(room, Clock.systemDefaultZone().millis());
        game.distributeRole();
        gameRepository.save(game);
    }

    @Test
    void 투표결과확인상태가_종료될때_투표결과가_반영된다() {
        // given
        final String target = PLAYER1_NAME;
        game.skipStatus(Clock.systemDefaultZone().millis()); // NOTICE
        game.skipStatus(Clock.systemDefaultZone().millis()); // DAY
        game.skipStatus(Clock.systemDefaultZone().millis());// VOTE
        gameRepository.save(game);

        // when
        voteService.votePlayer(CODE, PLAYER1_NAME, PLAYER5_NAME);
        voteService.votePlayer(CODE, PLAYER2_NAME, PLAYER5_NAME);
        voteService.votePlayer(CODE, PLAYER3_NAME, target);
        voteService.votePlayer(CODE, PLAYER4_NAME, target);
        voteService.votePlayer(CODE, PLAYER5_NAME, target);
        game = gameRepository.findById(CODE).get();
        game.skipStatus(Clock.systemDefaultZone().millis());
        gameRepository.save(game);

        // then
        final Player targetPlayer = gameRepository.findById(CODE).get().getPlayer(target);
        Assertions.assertThat(targetPlayer.isAlive()).isFalse();
    }

    @Test
    void 밤상태가_종료될떄_마피아의_스킬_결과가_반영된다() {
        // given
        final String mafia = game.getPlayers().getMafias().get(0).getName();
        final String target = PLAYER1_NAME;
        game.skipStatus(Clock.systemDefaultZone().millis()); // NOTICE
        game.skipStatus(Clock.systemDefaultZone().millis()); // DAY
        game.skipStatus(Clock.systemDefaultZone().millis());// VOTE
        game.skipStatus(Clock.systemDefaultZone().millis());// VOTE_RESULT
        game.skipStatus(Clock.systemDefaultZone().millis());// NIGHT_INTRO
        game.skipStatus(Clock.systemDefaultZone().millis());// NIGHT
        gameRepository.save(game);

        // when
        playerService.executeSkill(CODE, mafia, new PlayerExecuteAbilityRequest(target));
        game = gameRepository.findById(CODE).get();
        game.skipStatus(Clock.systemDefaultZone().millis()); // DAY_INTRO
        gameRepository.save(game);

        // then

        final Player targetPlayer = gameRepository.findById(CODE).get().getPlayer(target);
        Assertions.assertThat(targetPlayer.isAlive()).isFalse();
    }

    @Test
    void 밤상태가_종료될떄_의사의_스킬_결과가_반영된다() {
        // given
        final String mafia = game.getPlayers().getMafias().get(0).getName();
        final String doctor = game.getPlayers().getPlayers().stream()
                .filter(player -> player.getJobType().equals(JobType.DOCTOR))
                .map(player -> player.getName())
                .findFirst()
                .get();
        final String target = PLAYER1_NAME;
        game.skipStatus(Clock.systemDefaultZone().millis()); // NOTICE
        game.skipStatus(Clock.systemDefaultZone().millis()); // DAY
        game.skipStatus(Clock.systemDefaultZone().millis());// VOTE
        game.skipStatus(Clock.systemDefaultZone().millis());// VOTE_RESULT
        game.skipStatus(Clock.systemDefaultZone().millis());// NIGHT_INTRO
        game.skipStatus(Clock.systemDefaultZone().millis());// NIGHT
        gameRepository.save(game);

        // when
        playerService.executeSkill(CODE, mafia, new PlayerExecuteAbilityRequest(target));
        playerService.executeSkill(CODE, doctor, new PlayerExecuteAbilityRequest(target));
        game = gameRepository.findById(CODE).get();
        game.skipStatus(Clock.systemDefaultZone().millis()); // DAY_INTRO
        gameRepository.save(game);

        // then

        final Player targetPlayer = gameRepository.findById(CODE).get().getPlayer(target);
        Assertions.assertThat(targetPlayer.isAlive()).isTrue();
    }

    @Test
    void 공지_상태_종료_이후_밤_투표가_초기화된다() {
        // given
        final String mafia = game.getPlayers().getMafias().get(0).getName();
        game.skipStatus(Clock.systemDefaultZone().millis()); // NOTICE
        game.skipStatus(Clock.systemDefaultZone().millis()); // DAY
        game.skipStatus(Clock.systemDefaultZone().millis());// VOTE
        game.skipStatus(Clock.systemDefaultZone().millis());// VOTE_RESULT
        game.skipStatus(Clock.systemDefaultZone().millis());// NIGHT_INTRO
        game.skipStatus(Clock.systemDefaultZone().millis());// NIGHT
        gameRepository.save(game);
        playerService.executeSkill(CODE, mafia, new PlayerExecuteAbilityRequest(PLAYER1_NAME));
        game.skipStatus(Clock.systemDefaultZone().millis());// DAY_INTRO
        game.skipStatus(Clock.systemDefaultZone().millis());// NOTICE
        gameRepository.save(game);

        // when
        game.skipStatus(Clock.systemDefaultZone().millis()); // DAY
        gameRepository.save(game);

        // then
        final Skill actual = skillRepository.findById(CODE).get();
        assertSoftly(
                softly -> {
                    for (JobType jobType : JobType.values()) {
                        softly.assertThat(actual.findJobTargetBy(jobType)).isNull();
                    }
                }
        );
    }

    @Test
    void 직업_분배_이후_플레이어_직업이_저장된다() {
        // given & when : setTest()
        // then
        final List<PlayerJob> actual = playerJobRepository.findByCode(CODE);
        Assertions.assertThat(actual).hasSize(5);
    }

    @Test
    void 게임_종료_이후_게임이_삭제된다() {
        // given
        game.skipStatus(Clock.systemDefaultZone().millis()); // NOTICE
        game.skipStatus(Clock.systemDefaultZone().millis()); // DAY
        game.skipStatus(Clock.systemDefaultZone().millis());// VOTE
        game.skipStatus(Clock.systemDefaultZone().millis());// VOTE_RESULT
        game.getPlayer(PLAYER1_NAME).kill();
        game.getPlayer(PLAYER2_NAME).kill();
        game.getPlayer(PLAYER3_NAME).kill();
        gameRepository.save(game);
        game.skipStatus(Clock.systemDefaultZone().millis()); // end

        // when
        game.skipStatus(Clock.systemDefaultZone().millis()); // endOver
        gameRepository.save(game);

        // then
        final boolean gameExists = gameRepository.existsById(CODE);
        final boolean voteExists = voteRepository.existsById(CODE);
        final boolean chatExists = chatRepository.existsById(CODE);
        final boolean jobTargetExists = skillRepository.existsById(CODE);
        final boolean playerJobExists = playerJobRepository.findByCode(CODE).size() != 0;

        Assertions.assertThat(gameExists).isFalse();
        Assertions.assertThat(voteExists).isFalse();
        Assertions.assertThat(chatExists).isFalse();
        Assertions.assertThat(jobTargetExists).isFalse();
        Assertions.assertThat(playerJobExists).isFalse();
    }

    @Test
    void 모든_플레이어가_투표시_상태가_변경된다() {
        // given
        final String target = PLAYER1_NAME;
        game.skipStatus(Clock.systemDefaultZone().millis()); // NOTICE
        game.skipStatus(Clock.systemDefaultZone().millis()); // DAY
        gameRepository.save(game);

        // when
        voteService.votePlayer(CODE, PLAYER1_NAME, PLAYER5_NAME);
        voteService.votePlayer(CODE, PLAYER2_NAME, PLAYER5_NAME);
        voteService.votePlayer(CODE, PLAYER3_NAME, target);
        voteService.votePlayer(CODE, PLAYER4_NAME, target);
        voteService.votePlayer(CODE, PLAYER5_NAME, target);
        game = gameRepository.findById(CODE).get();

        // then
        final StatusType actual = gameRepository.findById(CODE).get().getStatus().getType();
        Assertions.assertThat(actual).isEqualTo(StatusType.VOTE);
    }
}

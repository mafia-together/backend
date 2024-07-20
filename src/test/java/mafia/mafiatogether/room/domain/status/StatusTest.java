package mafia.mafiatogether.room.domain.status;

import static mafia.mafiatogether.global.Fixture.DAY_END_TIME;
import static mafia.mafiatogether.global.Fixture.DAY_INTRO_END_TIME;
import static mafia.mafiatogether.global.Fixture.DAY_INTRO_TIME;
import static mafia.mafiatogether.global.Fixture.DAY_TIME;
import static mafia.mafiatogether.global.Fixture.NEXT_DAY;
import static mafia.mafiatogether.global.Fixture.NIGHT_END_TIME;
import static mafia.mafiatogether.global.Fixture.NIGHT_INTRO_END_TIME;
import static mafia.mafiatogether.global.Fixture.NIGHT_INTRO_TIME;
import static mafia.mafiatogether.global.Fixture.NIGHT_TIME;
import static mafia.mafiatogether.global.Fixture.NOTICE_END_TIME;
import static mafia.mafiatogether.global.Fixture.NOTICE_TIME;
import static mafia.mafiatogether.global.Fixture.VOTE_END_TIME;
import static mafia.mafiatogether.global.Fixture.VOTE_RESULT_END_TIME;
import static mafia.mafiatogether.global.Fixture.VOTE_RESULT_TIME;
import static mafia.mafiatogether.global.Fixture.VOTE_TIME;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import mafia.mafiatogether.chat.domain.Chat;
import mafia.mafiatogether.chat.domain.Message;
import mafia.mafiatogether.job.domain.JobType;
import mafia.mafiatogether.room.domain.Room;
import mafia.mafiatogether.room.domain.RoomInfo;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
class StatusTest {

    private static final String PLAYER1 = "A";
    private static final String PLAYER2 = "B";
    private static final String PLAYER3 = "C";

    private Room room;

    @BeforeEach
    void setRoom() {
        room = Room.create(new RoomInfo(3, 1, 0, 1), DAY_INTRO_TIME);
        room.joinPlayer(PLAYER1);
        room.joinPlayer(PLAYER2);
        room.joinPlayer(PLAYER3);
    }

    @Test
    void 게임이_진행되며_상태가_바뀐다() {
        // given
        room.modifyStatus(StatusType.DAY, DAY_INTRO_TIME);

        // when & then
        assertSoftly(
                softly -> {
                    softly.assertThat(room.getStatusType(DAY_INTRO_END_TIME)).isEqualTo(StatusType.DAY_INTRO);
                    softly.assertThat(room.getStatusType(NOTICE_TIME)).isEqualTo(StatusType.NOTICE);
                    softly.assertThat(room.getStatusType(NOTICE_END_TIME)).isEqualTo(StatusType.NOTICE);
                    softly.assertThat(room.getStatusType(DAY_TIME)).isEqualTo(StatusType.DAY);
                    softly.assertThat(room.getStatusType(DAY_END_TIME)).isEqualTo(StatusType.DAY);
                    softly.assertThat(room.getStatusType(VOTE_TIME)).isEqualTo(StatusType.VOTE);
                    softly.assertThat(room.getStatusType(VOTE_END_TIME)).isEqualTo(StatusType.VOTE);
                    softly.assertThat(room.getStatusType(VOTE_RESULT_TIME)).isEqualTo(StatusType.VOTE_RESULT);
                    softly.assertThat(room.getStatusType(VOTE_RESULT_END_TIME)).isEqualTo(StatusType.VOTE_RESULT);
                    softly.assertThat(room.getStatusType(NIGHT_INTRO_TIME)).isEqualTo(StatusType.NIGHT_INTRO);
                    softly.assertThat(room.getStatusType(NIGHT_INTRO_END_TIME)).isEqualTo(StatusType.NIGHT_INTRO);
                    softly.assertThat(room.getStatusType(NIGHT_TIME)).isEqualTo(StatusType.NIGHT);
                    softly.assertThat(room.getStatusType(NIGHT_END_TIME)).isEqualTo(StatusType.NIGHT);
                    softly.assertThat(room.getStatusType(NEXT_DAY)).isEqualTo(StatusType.DAY_INTRO);
                }
        );
    }

    @Test
    void 투표결과_게임종료_조건달성시_게임이_종료된다() {
        // given
        final Long endTime = VOTE_RESULT_END_TIME + 2_000L;

        room.modifyStatus(StatusType.DAY, DAY_INTRO_TIME);
        room.getStatusType(NOTICE_TIME);
        room.getStatusType(DAY_TIME);
        room.getStatusType(VOTE_TIME);
        room.getPlayer(PLAYER1).kill();
        room.getPlayer(PLAYER2).kill();
        room.getStatusType(VOTE_RESULT_TIME);

        // when & then
        Assertions.assertThat(room.getStatusType(endTime)).isEqualTo(StatusType.END);
    }

    @Test
    void 밤_이후_게임종료_조건달성시_게임이_종료된다() {
        // given
        final Long endTime = NIGHT_END_TIME + 1_000L;
        room.modifyStatus(StatusType.DAY, DAY_INTRO_TIME);
        room.getStatusType(NOTICE_TIME);
        room.getStatusType(DAY_TIME);
        room.getStatusType(VOTE_TIME);
        room.getStatusType(VOTE_RESULT_TIME);
        room.getStatusType(NIGHT_INTRO_TIME);
        room.getStatusType(NIGHT_TIME);
        room.getPlayer(PLAYER1).kill();
        room.getPlayer(PLAYER2).kill();

        // when & then
        Assertions.assertThat(room.getStatusType(endTime)).isEqualTo(StatusType.END);

    }

    @Test
    void 종료상태_일정_시간_이후_대기상태가_된며_방이_초기화된다() {
        // given
        final Chat chat = room.getChat();
        final Long endTime = NIGHT_END_TIME + 1_000L;
        final Long endEndTime = endTime + 29_000L;
        final Long waitTime = endEndTime + 1_000L;

        room.modifyStatus(StatusType.DAY, DAY_INTRO_TIME);
        room.getStatusType(NOTICE_TIME);
        room.getStatusType(DAY_TIME);
        room.getStatusType(VOTE_TIME);
        chat.save(Message.of(room.getPlayer(PLAYER1), "contents"));
        room.getPlayer(PLAYER1).kill();
        room.getPlayer(PLAYER2).kill();
        room.getStatusType(VOTE_RESULT_TIME);
        room.getStatusType(endTime);

        // when & then
        assertSoftly(
                softly -> {
                    softly.assertThat(room.getStatusType(endEndTime)).isEqualTo(StatusType.END);
                    softly.assertThat(room.getStatusType(waitTime)).isEqualTo(StatusType.WAIT);
                    softly.assertThat(room.getPlayer(PLAYER1).isAlive()).isTrue();
                    softly.assertThat(room.getPlayer(PLAYER1).getJobType()).isEqualTo(JobType.CITIZEN);
                    softly.assertThat(room.getPlayer(PLAYER2).isAlive()).isTrue();
                    softly.assertThat(room.getPlayer(PLAYER2).getJobType()).isEqualTo(JobType.CITIZEN);
                    softly.assertThat(room.getPlayer(PLAYER3).isAlive()).isTrue();
                    softly.assertThat(room.getPlayer(PLAYER3).getJobType()).isEqualTo(JobType.CITIZEN);
                    softly.assertThat(chat.getMessages()).hasSize(0);
                }
        );
    }

    @Test
    void 투표상태_종료_이후_투표결과가_집계된다() {
        // given
        room.modifyStatus(StatusType.DAY, DAY_INTRO_TIME);
        room.getStatusType(NOTICE_TIME);
        room.getStatusType(DAY_TIME);
        room.getStatusType(VOTE_TIME);
        room.votePlayer(PLAYER1, PLAYER3, VOTE_TIME);
        room.votePlayer(PLAYER2, PLAYER3, VOTE_TIME);
        room.getStatusType(VOTE_RESULT_TIME);

        // then
        Assertions.assertThat(room.getVoteResult()).isEqualTo(PLAYER3);
    }

    @Test
    void 투표결과상태_종료_이후_투표결과_및_채팅이_초기화된다() {
        // given
        room.modifyStatus(StatusType.DAY, DAY_INTRO_TIME);
        room.getStatusType(NOTICE_TIME);
        room.getStatusType(DAY_TIME);
        room.getStatusType(VOTE_TIME);
        room.votePlayer(PLAYER1, PLAYER3, VOTE_TIME);
        room.votePlayer(PLAYER2, PLAYER3, VOTE_TIME);
        room.getStatusType(VOTE_RESULT_TIME);
        room.getStatusType(NIGHT_INTRO_TIME);

        // when & then
        Assertions.assertThat(room.getVoteResult()).isBlank();
    }

    @Test
    void 살아있는_모든_사람이_투표시_상태가_변경된다() {
        // given
        room.modifyStatus(StatusType.DAY, DAY_INTRO_TIME);
        room.getStatusType(NOTICE_TIME);
        room.getStatusType(DAY_TIME);
        room.getPlayer(PLAYER3).kill();
        room.votePlayer(PLAYER1, PLAYER3, DAY_TIME);
        room.votePlayer(PLAYER2, PLAYER3, DAY_TIME);

        // then
        Assertions.assertThat(room.getStatusType(DAY_TIME)).isEqualTo(StatusType.VOTE);
    }

    @Test
    void NOTICE_상태가_아닐경우_밤결과_조회에_실패한다() {
        room.modifyStatus(StatusType.DAY, DAY_INTRO_TIME);
        room.getStatusType(DAY_INTRO_END_TIME);
        room.getStatusType(NOTICE_TIME);
        room.getStatusType(NOTICE_END_TIME);
        room.getStatusType(DAY_TIME);
        room.getStatusType(DAY_END_TIME);
        room.getStatusType(VOTE_TIME);
        room.getStatusType(VOTE_END_TIME);
        room.getStatusType(VOTE_RESULT_TIME);
        room.getStatusType(VOTE_RESULT_END_TIME);
        room.getStatusType(NIGHT_INTRO_TIME);
        room.getStatusType(NIGHT_INTRO_END_TIME);
        room.getStatusType(NIGHT_TIME);
        room.getStatusType(NIGHT_END_TIME);
        room.getStatusType(NEXT_DAY);
        assertSoftly(
                softly -> {
                    softly.assertThat(room.getStatusType(NEXT_DAY + 3000L)).isEqualTo(StatusType.NOTICE);
                    softly.assertThatCode(() -> room.getNightResult()).doesNotThrowAnyException();
                    room.getStatusType(NEXT_DAY + 6000L);
                    softly.assertThatThrownBy(() -> room.getNightResult());
                }
        );
    }

    @Test
    void 다섯명_게임에서_기권표_일경우_무효표처리된다() {
        // given
        Room room1 = Room.create(new RoomInfo(5, 2, 0, 0), DAY_INTRO_TIME);
        room1.joinPlayer("p1");
        room1.joinPlayer("p2");
        room1.joinPlayer("p3");
        room1.joinPlayer("p4");
        room1.joinPlayer("p5");
        room1.modifyStatus(StatusType.DAY, DAY_INTRO_TIME);
        room1.getStatusType(NOTICE_TIME);
        room1.getStatusType(DAY_TIME);

        // when
        room1.votePlayer("p1", "", DAY_TIME);
        room1.votePlayer("p2", "", DAY_TIME);
        room1.votePlayer("p3", "", DAY_TIME);
        room1.votePlayer("p4", "", DAY_TIME);
        room1.votePlayer("p5", "", DAY_TIME);

        // then
        room1.getStatusType(DAY_TIME);
        room1.getStatusType(DAY_TIME + 11_000L);
        Assertions.assertThat(room1.getStatusType(DAY_TIME + 14_000L)).isEqualTo(StatusType.NIGHT_INTRO);
    }
}

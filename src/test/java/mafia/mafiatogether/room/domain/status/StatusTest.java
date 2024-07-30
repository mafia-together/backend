package mafia.mafiatogether.room.domain.status;

import static mafia.mafiatogether.global.Fixture.dayEndTime;
import static mafia.mafiatogether.global.Fixture.dayIntroEndTime;
import static mafia.mafiatogether.global.Fixture.dayIntroTime;
import static mafia.mafiatogether.global.Fixture.dayTime;
import static mafia.mafiatogether.global.Fixture.nextDay;
import static mafia.mafiatogether.global.Fixture.nightEndTime;
import static mafia.mafiatogether.global.Fixture.nightIntroEndTime;
import static mafia.mafiatogether.global.Fixture.nightIntroTime;
import static mafia.mafiatogether.global.Fixture.nightTime;
import static mafia.mafiatogether.global.Fixture.noticeEndTime;
import static mafia.mafiatogether.global.Fixture.noticeTime;
import static mafia.mafiatogether.global.Fixture.voteEndTime;
import static mafia.mafiatogether.global.Fixture.voteResultEndTime;
import static mafia.mafiatogether.global.Fixture.voteResultTime;
import static mafia.mafiatogether.global.Fixture.voteTime;
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
        room = Room.create(new RoomInfo(3, 1, 0, 1), dayIntroTime);
        room.joinPlayer(PLAYER1);
        room.joinPlayer(PLAYER2);
        room.joinPlayer(PLAYER3);
    }

    @Test
    void 게임이_진행되며_상태가_바뀐다() {
        // given
        room.modifyStatus(StatusType.DAY, dayIntroTime);

        // when & then
        assertSoftly(
                softly -> {
                    softly.assertThat(room.getStatusType(dayIntroEndTime)).isEqualTo(StatusType.DAY_INTRO);
                    softly.assertThat(room.getStatusType(noticeTime)).isEqualTo(StatusType.NOTICE);
                    softly.assertThat(room.getStatusType(noticeEndTime)).isEqualTo(StatusType.NOTICE);
                    softly.assertThat(room.getStatusType(dayTime)).isEqualTo(StatusType.DAY);
                    softly.assertThat(room.getStatusType(dayEndTime)).isEqualTo(StatusType.DAY);
                    softly.assertThat(room.getStatusType(voteTime)).isEqualTo(StatusType.VOTE);
                    softly.assertThat(room.getStatusType(voteEndTime)).isEqualTo(StatusType.VOTE);
                    softly.assertThat(room.getStatusType(voteResultTime)).isEqualTo(StatusType.VOTE_RESULT);
                    softly.assertThat(room.getStatusType(voteResultEndTime)).isEqualTo(StatusType.VOTE_RESULT);
                    softly.assertThat(room.getStatusType(nightIntroTime)).isEqualTo(StatusType.NIGHT_INTRO);
                    softly.assertThat(room.getStatusType(nightIntroEndTime)).isEqualTo(StatusType.NIGHT_INTRO);
                    softly.assertThat(room.getStatusType(nightTime)).isEqualTo(StatusType.NIGHT);
                    softly.assertThat(room.getStatusType(nightEndTime)).isEqualTo(StatusType.NIGHT);
                    softly.assertThat(room.getStatusType(nextDay)).isEqualTo(StatusType.DAY_INTRO);
                }
        );
    }

    @Test
    void 투표결과_게임종료_조건달성시_게임이_종료된다() {
        // given
        final Long endTime = voteResultEndTime + 2_000L;

        room.modifyStatus(StatusType.DAY, dayIntroTime);
        room.getStatusType(noticeTime);
        room.getStatusType(dayTime);
        room.getStatusType(voteTime);
        room.getPlayer(PLAYER1).kill();
        room.getPlayer(PLAYER2).kill();
        room.getStatusType(voteResultTime);

        // when & then
        Assertions.assertThat(room.getStatusType(endTime)).isEqualTo(StatusType.END);
    }

    @Test
    void 밤_이후_게임종료_조건달성시_게임이_종료된다() {
        // given
        final Long endTime = nightEndTime + 1_000L;
        room.modifyStatus(StatusType.DAY, dayIntroTime);
        room.getStatusType(noticeTime);
        room.getStatusType(dayTime);
        room.getStatusType(voteTime);
        room.getStatusType(voteResultTime);
        room.getStatusType(nightIntroTime);
        room.getStatusType(nightTime);
        room.getPlayer(PLAYER1).kill();
        room.getPlayer(PLAYER2).kill();

        // when & then
        Assertions.assertThat(room.getStatusType(endTime)).isEqualTo(StatusType.END);

    }

    @Test
    void 종료상태_일정_시간_이후_대기상태가_된며_방이_초기화된다() {
        // given
        final Chat chat = room.getChat();
        final Long endTime = nightEndTime + 1_000L;
        final Long endEndTime = endTime + 29_000L;
        final Long waitTime = endEndTime + 1_000L;

        room.modifyStatus(StatusType.DAY, dayIntroTime);
        room.getStatusType(noticeTime);
        room.getStatusType(dayTime);
        room.getStatusType(voteTime);
        chat.save(Message.of(room.getPlayer(PLAYER1), "contents"));
        room.getPlayer(PLAYER1).kill();
        room.getPlayer(PLAYER2).kill();
        room.getStatusType(voteResultTime);
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
        room.modifyStatus(StatusType.DAY, dayIntroTime);
        room.getStatusType(noticeTime);
        room.getStatusType(dayTime);
        room.getStatusType(voteTime);
        room.votePlayer(PLAYER1, PLAYER3, voteTime);
        room.votePlayer(PLAYER2, PLAYER3, voteTime);
        room.getStatusType(voteResultTime);

        // then
        Assertions.assertThat(room.getVoteResult()).isEqualTo(PLAYER3);
    }

    @Test
    void 투표결과상태_종료_이후_투표결과_및_채팅이_초기화된다() {
        // given
        room.modifyStatus(StatusType.DAY, dayIntroTime);
        room.getStatusType(noticeTime);
        room.getStatusType(dayTime);
        room.getStatusType(voteTime);
        room.votePlayer(PLAYER1, PLAYER3, voteTime);
        room.votePlayer(PLAYER2, PLAYER3, voteTime);
        room.getStatusType(voteResultTime);
        room.getStatusType(nightIntroTime);

        // when & then
        Assertions.assertThat(room.getVoteResult()).isBlank();
    }

    @Test
    void 살아있는_모든_사람이_투표시_상태가_변경된다() {
        // given
        room.modifyStatus(StatusType.DAY, dayIntroTime);
        room.getStatusType(noticeTime);
        room.getStatusType(dayTime);
        room.getPlayer(PLAYER3).kill();
        room.votePlayer(PLAYER1, PLAYER3, dayTime);
        room.votePlayer(PLAYER2, PLAYER3, dayTime);

        // then
        Assertions.assertThat(room.getStatusType(dayTime)).isEqualTo(StatusType.VOTE);
    }

    @Test
    void NOTICE_상태가_아닐경우_밤결과_조회에_실패한다() {
        room.modifyStatus(StatusType.DAY, dayIntroTime);
        room.getStatusType(dayIntroEndTime);
        room.getStatusType(noticeTime);
        room.getStatusType(noticeEndTime);
        room.getStatusType(dayTime);
        room.getStatusType(dayEndTime);
        room.getStatusType(voteTime);
        room.getStatusType(voteEndTime);
        room.getStatusType(voteResultTime);
        room.getStatusType(voteResultEndTime);
        room.getStatusType(nightIntroTime);
        room.getStatusType(nightIntroEndTime);
        room.getStatusType(nightTime);
        room.getStatusType(nightEndTime);
        room.getStatusType(nextDay);
        assertSoftly(
                softly -> {
                    softly.assertThat(room.getStatusType(nextDay + 3000L)).isEqualTo(StatusType.NOTICE);
                    softly.assertThatCode(() -> room.getNightResult()).doesNotThrowAnyException();
                    room.getStatusType(nextDay + 6000L);
                    softly.assertThatThrownBy(() -> room.getNightResult());
                }
        );
    }

    @Test
    void 다섯명_게임에서_기권표_일경우_무효표처리된다() {
        // given
        Room room1 = Room.create(new RoomInfo(5, 2, 0, 0), dayIntroTime);
        room1.joinPlayer("p1");
        room1.joinPlayer("p2");
        room1.joinPlayer("p3");
        room1.joinPlayer("p4");
        room1.joinPlayer("p5");
        room1.modifyStatus(StatusType.DAY, dayIntroTime);
        room1.getStatusType(noticeTime);
        room1.getStatusType(dayTime);

        // when
        room1.votePlayer("p1", "", dayTime);
        room1.votePlayer("p2", "", dayTime);
        room1.votePlayer("p3", "", dayTime);
        room1.votePlayer("p4", "", dayTime);
        room1.votePlayer("p5", "", dayTime);

        // then
        room1.getStatusType(dayTime);
        room1.getStatusType(dayTime + 11_000L);
        Assertions.assertThat(room1.getStatusType(dayTime + 14_000L)).isEqualTo(StatusType.NIGHT_INTRO);
    }
}

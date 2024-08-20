package mafia.mafiatogether.game.ui;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.util.Base64;
import java.util.Map;
import mafia.mafiatogether.config.exception.ErrorResponse;
import mafia.mafiatogether.config.exception.ExceptionCode;
import mafia.mafiatogether.game.domain.Game;
import mafia.mafiatogether.game.domain.Player;
import mafia.mafiatogether.game.domain.status.StatusType;
import mafia.mafiatogether.global.ControllerTest;
import mafia.mafiatogether.game.application.dto.response.PlayerResponse;
import mafia.mafiatogether.job.domain.jobtype.JobType;
import mafia.mafiatogether.game.application.dto.response.RoomInfoResponse;
import mafia.mafiatogether.game.application.dto.response.RoomStatusResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@SuppressWarnings("NonAsciiCharacters")
class GameControllerTest extends ControllerTest {

    @BeforeEach
    void setTest(){
        setRoom();
    }

    @Test
    void 대기방을_상태를_확인할_수_있다() {
        //given
        final String basic = Base64.getEncoder().encodeToString((CODE + ":" + PLAYER1_NAME).getBytes());

        //when
        final RoomStatusResponse response = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Basic " + basic)
                .when().get("/games/status")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(RoomStatusResponse.class);

        //then
        Assertions.assertThat(response.statusType()).isEqualTo(StatusType.WAIT);
    }

    @Test
    void 게임의_상태를_확인할_수_있다() {
        //given
        final String basic = Base64.getEncoder().encodeToString((CODE + ":" + PLAYER1_NAME).getBytes());
        setGame();

        //when
        final RoomStatusResponse response = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Basic " + basic)
                .when().get("/games/status")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(RoomStatusResponse.class);

        //then
        Assertions.assertThat(response.statusType()).isEqualTo(StatusType.DAY_INTRO);
    }

    @Test
    void 방을_상태를_변경할_수_있다() {
        //given & when
        setGame();

        //then
        StatusType actual = gameRepository.findById(CODE).get().getStatus().getType();
        Assertions.assertThat(actual).isEqualTo(StatusType.DAY_INTRO);
    }

    @Test
    void 인원부족시_게임을_시작할_수_없다() {
        //given
        String basic = Base64.getEncoder().encodeToString((CODE + ":" + "player1").getBytes());

        //when
        final ErrorResponse response = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(Map.of("statusType", StatusType.DAY_INTRO))
                .header("Authorization", "Basic " + basic)
                .when().patch("/games/status")
                .then().log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                .as(ErrorResponse.class);

        // then
        Assertions.assertThat(response.errorCode()).isEqualTo(ExceptionCode.NOT_ENOUGH_PLAYER.getCode());
    }

    @Test
    void 대기방에서_방장이_방의_정보를_찾는다(){
        // given
        final String basic = Base64.getEncoder().encodeToString((CODE + ":" + PLAYER1_NAME).getBytes());

        // when
        final RoomInfoResponse response = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Basic " + basic)
                .when().get("/games/info")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(RoomInfoResponse.class);

        /// then
        assertSoftly(
                softly ->{
                    softly.assertThat(response.isMaster()).isTrue();
                    softly.assertThat(response.players()).hasSize(4);
                }
        );
    }

    @Test
    void 생존한_사람이_방의_정보를_찾는다() {
        // given
        setGame();
        Game game = gameRepository.findById(CODE).get();
        final Player citizen = findPlayer(game, JobType.CITIZEN);
        final String basic = Base64.getEncoder().encodeToString((CODE + ":" + citizen.getName()).getBytes());

        // when & then
        final RoomInfoResponse response = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Basic " + basic)
                .when().get("/games/info")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(RoomInfoResponse.class);

        PlayerResponse power = response.players()
                .stream().filter(player -> player.name().equals(citizen.getName()))
                .findFirst()
                .get();

        PlayerResponse chunsik = response.players()
                .stream().filter(player -> !player.name().equals(citizen.getName()))
                .findFirst()
                .get();

        assertSoftly(
                softly -> {
                    softly.assertThat(response.startTime()).isNotNull();
                    softly.assertThat(response.endTime()).isNotNull();
                    softly.assertThat(response.myName()).isNotNull();
                    softly.assertThat(response.isAlive()).isTrue();
                    softly.assertThat(power.job()).isEqualTo(JobType.CITIZEN);
                    softly.assertThat(chunsik.job()).isNull();
                }
        );
    }

    @Test
    void 죽은사람이_방의_정보를_찾는다() {
        // given
        setGame();
        Game game = gameRepository.findById(CODE).get();
        final Player citizen = findPlayer(game, JobType.CITIZEN);
        citizen.kill();
        gameRepository.save(game);
        final String basic = Base64.getEncoder().encodeToString((CODE + ":" + citizen.getName()).getBytes());

        // when & then
        final RoomInfoResponse response = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Basic " + basic)
                .when().get("/games/info")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(RoomInfoResponse.class);

        assertSoftly(
                softly -> {
                    softly.assertThat(response.startTime()).isNotNull();
                    softly.assertThat(response.endTime()).isNotNull();
                    softly.assertThat(response.isAlive()).isFalse();
                    softly.assertThat(response.players().getFirst().job()).isNotNull();
                }
        );
    }

    @Test
    void 마피아가_방의_정보를_찾는다() {
        // given
        setGame();
        Game game = gameRepository.findById(CODE).get();
        final Player mafia = findPlayer(game, JobType.MAFIA);
        final String mafiaBasic = Base64.getEncoder().encodeToString((CODE + ":" + mafia.getName()).getBytes());
        final Player doctor = findPlayer(game, JobType.DOCTOR);
        final String doctorBasic = Base64.getEncoder().encodeToString((CODE + ":" + doctor.getName()).getBytes());
        final Player citizen = findPlayer(game, JobType.CITIZEN);
        final String citizenBasic = Base64.getEncoder().encodeToString((CODE + ":" + citizen.getName()).getBytes());

        // when & then
        final Long mafiaCount = countMafiaResponse(mafiaBasic);
        final Long doctorCount = countMafiaResponse(doctorBasic);
        final Long citizenCount = countMafiaResponse(citizenBasic);
        assertSoftly(
                softly -> {
                    softly.assertThat(mafiaCount).isEqualTo(2);
                    softly.assertThat(doctorCount).isEqualTo(0);
                    softly.assertThat(citizenCount).isEqualTo(0);
                }
        );
    }

    private Player findPlayer(Game game, JobType jobType) {
        return game.getPlayers().getPlayers().stream()
                .filter(player -> player.getJobType().equals(jobType))
                .findFirst()
                .get();
    }

    private long countMafiaResponse(final String basic) {
        final RoomInfoResponse roomInfoResponse = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Basic " + basic)
                .when().get("/games/info")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(RoomInfoResponse.class);

        return roomInfoResponse.players().stream()
                .filter(response -> response.job() != null && response.job().equals(JobType.MAFIA))
                .count();
    }
}

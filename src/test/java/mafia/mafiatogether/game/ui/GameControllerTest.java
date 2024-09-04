package mafia.mafiatogether.game.ui;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.hamcrest.Matchers.equalTo;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.util.Base64;
import java.util.Map;
import mafia.mafiatogether.config.exception.ErrorResponse;
import mafia.mafiatogether.config.exception.ExceptionCode;
import mafia.mafiatogether.game.application.dto.response.GameInfoResponse;
import mafia.mafiatogether.game.application.dto.response.GameStatusResponse;
import mafia.mafiatogether.game.application.dto.response.PlayerResponse;
import mafia.mafiatogether.game.domain.Game;
import mafia.mafiatogether.game.domain.Player;
import mafia.mafiatogether.game.domain.status.StatusType;
import mafia.mafiatogether.global.ControllerTest;
import mafia.mafiatogether.job.domain.jobtype.JobType;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@SuppressWarnings("NonAsciiCharacters")
class GameControllerTest extends ControllerTest {

    @BeforeEach
    void setTest() {
        setLobby();
    }

    @Test
    void 대기방을_상태를_확인할_수_있다() {
        //given
        final String basic = Base64.getEncoder().encodeToString((CODE + ":" + PLAYER1_NAME).getBytes());

        //when
        final GameStatusResponse response = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Basic " + basic)
                .when().get("/games/status")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(GameStatusResponse.class);

        //then
        Assertions.assertThat(response.statusType()).isEqualTo(StatusType.WAIT);
    }

    @Test
    void 게임의_상태를_확인할_수_있다() {
        //given
        final String basic = Base64.getEncoder().encodeToString((CODE + ":" + PLAYER1_NAME).getBytes());
        setGame();

        //when
        final GameStatusResponse response = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Basic " + basic)
                .when().get("/games/status")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(GameStatusResponse.class);

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
                .when().post("/games/start")
                .then().log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                .as(ErrorResponse.class);

        // then
        Assertions.assertThat(response.errorCode()).isEqualTo(ExceptionCode.NOT_ENOUGH_PLAYER.getCode());
    }

    @Test
    void 대기방에서_방장이_방의_정보를_찾는다() {
        // given
        final String basic = Base64.getEncoder().encodeToString((CODE + ":" + PLAYER1_NAME).getBytes());

        // when
        final GameInfoResponse response = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Basic " + basic)
                .when().get("/games/info")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(GameInfoResponse.class);

        /// then
        assertSoftly(
                softly -> {
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
        final GameInfoResponse response = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Basic " + basic)
                .when().get("/games/info")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(GameInfoResponse.class);

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
        final GameInfoResponse response = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Basic " + basic)
                .when().get("/games/info")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(GameInfoResponse.class);

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
        final GameInfoResponse gameInfoResponse = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Basic " + basic)
                .when().get("/games/info")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(GameInfoResponse.class);

        return gameInfoResponse.players().stream()
                .filter(response -> response.job() != null && response.job().equals(JobType.MAFIA))
                .count();
    }

    @Test
    void 게임이_진행되고_있을때_참가한_유저는_유효한지_검증한다() {
        // given
        setGame();
        final String basic = Base64.getEncoder().encodeToString((CODE + ":" + PLAYER1_NAME).getBytes());

        // when & then
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Basic " + basic)
                .when().get("/games/exist")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .body("exist", equalTo(true));
    }

    @Test
    void 로비만_존재하고_게임이_진행되지_않을때_참가한_유저가_유효한지_검증한다() {
        // given
        final String basic = Base64.getEncoder().encodeToString((CODE + ":" + PLAYER1_NAME).getBytes());

        // when & then
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Basic " + basic)
                .when().get("/games/exist")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .body("exist", equalTo(true));
    }

    @Test
    void 유저가_요청한_방이_없는지_검증한다() {
        // given
        final String code = "123456789fail";
        final String basic = Base64.getEncoder().encodeToString((code + ":" + PLAYER1_NAME).getBytes());

        // when & then
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Basic " + basic)
                .when().get("/games/exist")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .body("exist", equalTo(false));
    }

    @Test
    void 유저가_요청한_방에_존재하는지_검증한다() {
        // given
        final String invalidPlayer = "invalidPlayer";
        final String basic = Base64.getEncoder().encodeToString((CODE + ":" + invalidPlayer).getBytes());

        // when & then
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Basic " + basic)
                .when().get("/games/exist")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .body("exist", equalTo(false));
    }


    @Test
    void 유저가_요청한_게임에_존재하는지_검증한다() {
        // given
        setGame();
        final String invalidPlayer = "invalidPlayer";
        final String basic = Base64.getEncoder().encodeToString((CODE + ":" + invalidPlayer).getBytes());

        // when & then
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Basic " + basic)
                .when().get("/games/exist")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .body("exist", equalTo(false));
    }
}

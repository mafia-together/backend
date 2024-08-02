package mafia.mafiatogether.game.ui;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.util.Base64;
import java.util.Map;
import mafia.mafiatogether.config.exception.ErrorResponse;
import mafia.mafiatogether.config.exception.ExceptionCode;
import mafia.mafiatogether.game.domain.Game;
import mafia.mafiatogether.game.domain.GameRepository;
import mafia.mafiatogether.game.domain.Player;
import mafia.mafiatogether.game.domain.status.StatusType;
import mafia.mafiatogether.global.ControllerTest;
import mafia.mafiatogether.job.application.dto.response.PlayerResponse;
import mafia.mafiatogether.job.domain.JobType;
import mafia.mafiatogether.room.application.dto.response.RoomInfoResponse;
import mafia.mafiatogether.room.application.dto.response.RoomStatusResponse;
import mafia.mafiatogether.room.domain.Room;
import mafia.mafiatogether.room.domain.RoomInfo;
import mafia.mafiatogether.room.domain.RoomRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

@SuppressWarnings("NonAsciiCharacters")
class GameControllerTest extends ControllerTest {

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private RoomRepository roomRepository;

    private static final String code = "1234567890";
    private static final String PLAYER1_NAME = "player1";
    private static final String PLAYER2_NAME = "player2";
    private static final String PLAYER3_NAME = "player3";
    private static final String PLAYER4_NAME = "player4";
    private static final String PLAYER5_NAME = "player5";

    @BeforeEach
    void setGame() {
        Room room = Room.create(code, RoomInfo.of(5, 2, 1, 1));
        room.joinPlayer(PLAYER1_NAME);
        room.joinPlayer(PLAYER2_NAME);
        room.joinPlayer(PLAYER3_NAME);
        room.joinPlayer(PLAYER4_NAME);
        roomRepository.save(room);
    }

    @AfterEach
    void clearTest() {
        roomRepository.deleteById(code);
        gameRepository.deleteById(code);
    }

    @Test
    void 대기방을_상태를_확인할_수_있다() {
        //given
        final String basic = Base64.getEncoder().encodeToString((code + ":" + "power").getBytes());

        //when
        final RoomStatusResponse response = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Basic " + basic)
                .when().get("/rooms/status")
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
        final String basic = Base64.getEncoder().encodeToString((code + ":" + PLAYER1_NAME).getBytes());
        startGame();

        //when
        final RoomStatusResponse response = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Basic " + basic)
                .when().get("/rooms/status")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(RoomStatusResponse.class);

        //then
        Assertions.assertThat(response.statusType()).isEqualTo(StatusType.DAY_INTRO);
    }

    private void startGame() {
        Room room = roomRepository.findByCode(code);
        room.joinPlayer(PLAYER5_NAME);
        String basic = Base64.getEncoder().encodeToString((code + ":" + "player1").getBytes());
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(Map.of("statusType", StatusType.DAY_INTRO))
                .header("Authorization", "Basic " + basic)
                .when().patch("/rooms/status")
                .then().log().all()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    void 방을_상태를_변경할_수_있다() {
        //given & when
        startGame();

        //then
        StatusType actual = gameRepository.findById(code).get().getStatus().getType();
        Assertions.assertThat(actual).isEqualTo(StatusType.DAY_INTRO);
    }

    @Test
    void 인원부족시_게임을_시작할_수_없다() {
        //given
        String basic = Base64.getEncoder().encodeToString((code + ":" + "player1").getBytes());

        //when
        final ErrorResponse response = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(Map.of("statusType", StatusType.DAY_INTRO))
                .header("Authorization", "Basic " + basic)
                .when().patch("/rooms/status")
                .then().log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                .as(ErrorResponse.class);

        // then
        Assertions.assertThat(response.errorCode()).isEqualTo(ExceptionCode.NOT_ENOUGH_PLAYER.getCode());
    }

    @Test
    void 생존한_사람이_방의_정보를_찾는다() {
        // given
        startGame();
        Game game = gameRepository.findById(code).get();
        final Player citizen = findPlayer(game, JobType.CITIZEN);
        final String basic = Base64.getEncoder().encodeToString((code + ":" + citizen.getName()).getBytes());

        // when & then
        final RoomInfoResponse response = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Basic " + basic)
                .when().get("/rooms/info")
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
        startGame();
        Game game = gameRepository.findById(code).get();
        final Player citizen = findPlayer(game, JobType.CITIZEN);
        citizen.kill();
        gameRepository.save(game);
        final String basic = Base64.getEncoder().encodeToString((code + ":" + citizen.getName()).getBytes());

        // when & then
        final RoomInfoResponse response = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Basic " + basic)
                .when().get("/rooms/info")
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
        startGame();
        Game game = gameRepository.findById(code).get();
        final Player mafia = findPlayer(game, JobType.MAFIA);
        final String mafiaBasic = Base64.getEncoder().encodeToString((code + ":" + mafia.getName()).getBytes());
        final Player doctor = findPlayer(game, JobType.DOCTOR);
        final String doctorBasic = Base64.getEncoder().encodeToString((code + ":" + doctor.getName()).getBytes());
        final Player citizen = findPlayer(game, JobType.CITIZEN);
        final String citizenBasic = Base64.getEncoder().encodeToString((code + ":" + citizen.getName()).getBytes());

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
                .when().get("/rooms/info")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(RoomInfoResponse.class);

        return roomInfoResponse.players().stream()
                .filter(response -> response.job() != null && response.job().equals(JobType.MAFIA))
                .count();
    }
}

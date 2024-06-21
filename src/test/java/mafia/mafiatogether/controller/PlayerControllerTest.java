package mafia.mafiatogether.controller;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.hamcrest.Matchers.equalTo;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.time.Clock;
import java.util.Base64;
import java.util.Map;
import mafia.mafiatogether.config.exception.ErrorResponse;
import mafia.mafiatogether.config.exception.ExceptionCode;
import mafia.mafiatogether.domain.Player;
import mafia.mafiatogether.domain.Room;
import mafia.mafiatogether.domain.RoomInfo;
import mafia.mafiatogether.domain.job.JobType;
import mafia.mafiatogether.domain.status.StatusType;
import mafia.mafiatogether.redis.RedisTestConfig;
import mafia.mafiatogether.repository.RoomRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;

@Import(RedisTestConfig.class)
@SuppressWarnings("NonAsciiCharacters")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class PlayerControllerTest {

    @Autowired
    private RoomRepository roomRepository;

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    private static final String CODE = "code";

    @BeforeEach
    void setRoom() {
        Room room = Room.create(CODE, RoomInfo.of(4, 1, 1, 1), Clock.systemDefaultZone().millis());
        room.joinPlayer("t1");
        room.joinPlayer("t2");
        room.joinPlayer("t3");
        room.joinPlayer("t4");

        room.modifyStatus(StatusType.DAY, Clock.systemDefaultZone().millis());

        roomRepository.save(room);
    }

    @Test
    void 직업_기술을_사용한다() {
        // given
        Room room = roomRepository.findById(CODE).get();
        Player mafia = room.getPlayers().values().stream()
                .filter(player -> player.getJobType().equals(JobType.MAFIA))
                .findFirst()
                .get();
        String basic = Base64.getEncoder().encodeToString((CODE + ":" + mafia.getName()).getBytes());

        // when & then
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(Map.of("target", "t2"))
                .header("Authorization", "Basic " + basic)
                .when().post("/players/skill")
                .then().log().all()
                .statusCode(HttpStatus.OK.value());

        final Room actualRoom = roomRepository.findById(CODE).get();
        Assertions.assertThat(actualRoom.getJobsTarget(mafia.getName())).isEqualTo("t2");
    }

    @Test
    void 초기_마피아_타겟은_NULL_값이다() {
        // given
        Room room = roomRepository.findById(CODE).get();

        Player mafia = room.getPlayers().values().stream()
                .filter(player -> player.getJobType().equals(JobType.MAFIA))
                .findFirst()
                .get();
        String basic = Base64.getEncoder().encodeToString((CODE + ":" + mafia.getName()).getBytes());

        // when & then
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(Map.of("target", ""))
                .header("Authorization", "Basic " + basic)
                .when().get("/players/skill")
                .then().log().all()
                .statusCode(HttpStatus.OK.value());

        final Room actualRoom = roomRepository.findById(CODE).get();
        Assertions.assertThat(actualRoom.getJobsTarget(mafia.getName())).isNull();
    }

    @Test
    void 마피아가_빈문자열을_보낼시_아무도_죽지않는다() {
        // given
        Room room = roomRepository.findById(CODE).get();

        Player mafia = room.getPlayers().values().stream()
                .filter(player -> player.getJobType().equals(JobType.MAFIA))
                .findFirst()
                .get();
        String basic = Base64.getEncoder().encodeToString((CODE + ":" + mafia.getName()).getBytes());

        // when & then
        final String initTargetName = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(Map.of("target", ""))
                .header("Authorization", "Basic " + basic)
                .when().get("/players/skill")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .body()
                .jsonPath()
                .getString("name");
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(Map.of("target", ""))
                .header("Authorization", "Basic " + basic)
                .when().post("/players/skill")
                .then().log().all()
                .statusCode(HttpStatus.OK.value());
        final String targetName = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(Map.of("target", ""))
                .header("Authorization", "Basic " + basic)
                .when().get("/players/skill")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .body()
                .jsonPath()
                .getString("name");

        final Room actualRoom = roomRepository.findById(CODE).get();
        assertSoftly(
                softly -> {
                    softly.assertThat(initTargetName).isNull();
                    softly.assertThat(actualRoom.getJobsTarget(mafia.getName())).isBlank();
                    softly.assertThat(targetName).isBlank();
                }
        );
    }

    @Test
    void 이미_죽은사람에게_직업_기술_사용시_실패한다() {
        // given
        Room room = roomRepository.findById(CODE).get();

        Player mafia = room.getPlayers().values().stream()
                .filter(player -> player.getJobType().equals(JobType.MAFIA))
                .findFirst()
                .get();
        Player dead = room.getPlayers().values().stream()
                .filter(player -> !player.equals(mafia))
                .findFirst()
                .get();
        dead.kill();

        roomRepository.save(room);
        String basic = Base64.getEncoder().encodeToString((CODE + ":" + mafia.getName()).getBytes());

        // when & then
        final ErrorResponse response = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(Map.of("target", dead.getName()))
                .header("Authorization", "Basic " + basic)
                .when().post("/players/skill")
                .then().log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                .as(ErrorResponse.class);

        Assertions.assertThat(response.errorCode()).isEqualTo(ExceptionCode.NOT_ALIVE_PLAYER.getCode());
    }

    @Test
    void 방에_없는_사람에게_직업_기술_사용시_실패한다() {
        // given
        Room room = roomRepository.findById(CODE).get();

        Player mafia = room.getPlayers().values().stream()
                .filter(player -> player.getJobType().equals(JobType.MAFIA))
                .findFirst()
                .get();

        String basic = Base64.getEncoder().encodeToString((CODE + ":" + mafia.getName()).getBytes());

        // when & then
        final ErrorResponse response = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(Map.of("target", "t5"))
                .header("Authorization", "Basic " + basic)
                .when().post("/players/skill")
                .then().log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                .as(ErrorResponse.class);

        Assertions.assertThat(response.errorCode()).isEqualTo(ExceptionCode.INVALID_PLAYER.getCode());
    }

    @Test
    void 직업을_조회한다() {
        //given
        Room room = roomRepository.findById(CODE).get();
        final Player citizen = room.getPlayers().values().stream()
                .filter(player -> player.getJobType().equals(JobType.CITIZEN))
                .findFirst()
                .get();
        String basic = Base64.getEncoder().encodeToString((CODE + ":" + citizen.getName()).getBytes());

        // when & then
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Basic " + basic)
                .when().get("/players/my/job")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .body("job", equalTo("CITIZEN"));
    }
}

package mafia.mafiatogether.controller;

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
import mafia.mafiatogether.domain.RoomManager;
import mafia.mafiatogether.domain.job.JobType;
import mafia.mafiatogether.domain.status.StatusType;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

@SuppressWarnings("NonAsciiCharacters")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class PlayerControllerTest {

    @Autowired
    private RoomManager roomManager;

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Test
    void 직업_기술을_사용한다() {
        // given
        String code = roomManager.create(new RoomInfo(3, 1, 0, 0));
        Room room = roomManager.findByCode(code);
        room.joinPlayer("t1");
        room.joinPlayer("t2");
        room.joinPlayer("t3");

        room.modifyStatus(StatusType.DAY, Clock.systemDefaultZone());

        Player mafia = room.getPlayers().values().stream()
                .filter(player -> player.getJobSymbol().equals(JobType.MAFIA))
                .findFirst()
                .get();
        String basic = Base64.getEncoder().encodeToString((code + ":" + mafia.getName()).getBytes());

        // when & then
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(Map.of("target", "t2"))
                .header("Authorization", "Basic " + basic)
                .when().post("/players/skill")
                .then().log().all()
                .statusCode(HttpStatus.OK.value());
        Assertions.assertThat(room.getJobsTarget(mafia.getName())).isEqualTo("t2");
    }

    @Test
    void 이미_죽은사람에게_직업_기술_사용시_실패한다() {
        // given
        String code = roomManager.create(new RoomInfo(3, 1, 0, 0));
        Room room = roomManager.findByCode(code);
        room.joinPlayer("t1");
        room.joinPlayer("t2");
        room.joinPlayer("t3");

        room.modifyStatus(StatusType.DAY, Clock.systemDefaultZone());

        Player mafia = room.getPlayers().values().stream()
                .filter(player -> player.getJobSymbol().equals(JobType.MAFIA))
                .findFirst()
                .get();
        Player dead = room.getPlayers().values().stream()
                .filter(player -> !player.equals(mafia))
                .findFirst()
                .get();
        dead.kill();

        String basic = Base64.getEncoder().encodeToString((code + ":" + mafia.getName()).getBytes());

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
        String code = roomManager.create(new RoomInfo(3, 1, 0, 0));
        Room room = roomManager.findByCode(code);
        room.joinPlayer("t1");
        room.joinPlayer("t2");
        room.joinPlayer("t3");

        room.modifyStatus(StatusType.DAY, Clock.systemDefaultZone());

        Player mafia = room.getPlayers().values().stream()
                .filter(player -> player.getJobSymbol().equals(JobType.MAFIA))
                .findFirst()
                .get();

        String basic = Base64.getEncoder().encodeToString((code + ":" + mafia.getName()).getBytes());

        // when & then
        final ErrorResponse response = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(Map.of("target", "t4"))
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
        String code = roomManager.create(new RoomInfo(3, 1, 0, 0));
        Room room = roomManager.findByCode(code);
        room.joinPlayer("t1");
        room.joinPlayer("t2");
        room.joinPlayer("t3");

        room.modifyStatus(StatusType.DAY, Clock.systemDefaultZone());
        final Player citizen = room.getPlayers().values().stream()
                .filter(player -> player.getJobSymbol().equals(JobType.CITIZEN))
                .findFirst()
                .get();
        String basic = Base64.getEncoder().encodeToString((code + ":" + citizen.getName()).getBytes());

        // when & then
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Basic " + basic)
                .when().get("/players/my/job")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .body("role", equalTo("CITIZEN"));
    }
}

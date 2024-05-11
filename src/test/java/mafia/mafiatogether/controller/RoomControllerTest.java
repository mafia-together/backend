package mafia.mafiatogether.controller;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.time.Clock;
import java.util.Base64;
import mafia.mafiatogether.domain.Player;
import mafia.mafiatogether.domain.Room;
import mafia.mafiatogether.domain.RoomInfo;
import mafia.mafiatogether.domain.RoomManager;
import mafia.mafiatogether.domain.status.StatusType;
import mafia.mafiatogether.service.dto.RoomCodeResponse;
import mafia.mafiatogether.service.dto.RoomCreateRequest;
import mafia.mafiatogether.service.dto.RoomInfoResponse;
import mafia.mafiatogether.service.dto.RoomModifyRequest;
import mafia.mafiatogether.service.dto.RoomStatusResponse;
import org.assertj.core.api.Assertions;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

@SuppressWarnings("NonAsciiCharacters")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class RoomControllerTest {

    @Autowired
    private RoomManager roomManager;

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }


    @Test
    void 방을_생성할_수_있다() {
        //given
        final RoomCreateRequest request = new RoomCreateRequest(5, 1, 1, 1);

        //when
        final RoomCodeResponse response = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/rooms")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(RoomCodeResponse.class);

        //then
        Assertions.assertThat(response.code()).isNotBlank();
    }

    @Test
    void 방을_상태를_확인할_수_있다() {
        //given
        final String code = roomManager.create(new RoomInfo(5, 1, 1, 1));
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
    void 방을_상태를_변경할_수_있다() {
        //given
        String code = roomManager.create(new RoomInfo(5, 1, 1, 1));
        String basic = Base64.getEncoder().encodeToString((code + ":" + "player1").getBytes());
        RoomModifyRequest request = new RoomModifyRequest(StatusType.DAY);
        Room room = roomManager.findByCode(code);
        room.joinPlayer(Player.create("player1"));
        room.joinPlayer(Player.create("player2"));
        room.joinPlayer(Player.create("player3"));
        room.joinPlayer(Player.create("player4"));
        room.joinPlayer(Player.create("player5"));

        //when
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(request)
                .header("Authorization", "Basic " + basic)
                .when().patch("/rooms/status")
                .then().log().all()
                .statusCode(HttpStatus.OK.value());

        //then
        StatusType actual = room.getStatusType(Clock.systemDefaultZone());
        Assertions.assertThat(actual).isEqualTo(StatusType.DAY);
    }

    @Test
    void 방에_참가할_수_있다() {
        //given
        final String code = roomManager.create(new RoomInfo(5, 1, 1, 1));

        //when
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .when().get("/rooms?code=" + code + "&name=power")
                .then().log().all()
                .statusCode(HttpStatus.OK.value());

        //then
        Room room = roomManager.findByCode(code);
        Assertions.assertThat(room.getPlayer("power")).isNotNull();
    }

    @Test
    void 방의_코드를_찾는다() {
        // given
        final String code = roomManager.create(new RoomInfo(5, 1, 1, 1));
        final String basic = Base64.getEncoder().encodeToString((code + ":" + "power").getBytes());

        // when & then
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Basic " + basic)
                .when().get("/rooms/code")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .body("code", Matchers.equalTo(code));
    }

    @Test
    void 생존한_사람이_방의_정보를_찾는다() {
        // given
        final String code = roomManager.create(new RoomInfo(5, 1, 1, 1));
        final String basic = Base64.getEncoder().encodeToString((code + ":" + "power").getBytes());
        final Room room = roomManager.findByCode(code);
        room.joinPlayer(Player.create("power"));

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
                    softly.assertThat(response.isAlive()).isTrue();
                    softly.assertThat(response.isMaster()).isTrue();
                    softly.assertThat(response.players().getFirst().job()).isNull();
                }
        );
    }

    @Test
    void 죽은사람이_방의_정보를_찾는다() {
        // given
        final String code = roomManager.create(new RoomInfo(5, 1, 1, 1));
        final String basic = Base64.getEncoder().encodeToString((code + ":" + "power").getBytes());
        final Room room = roomManager.findByCode(code);
        room.joinPlayer(Player.create("power"));
        room.getPlayer("power").execute();

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
                    softly.assertThat(response.isMaster()).isTrue();
                    softly.assertThat(response.players().getFirst().job()).isNotNull();
                }
        );
    }
}

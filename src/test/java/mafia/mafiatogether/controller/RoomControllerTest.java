package mafia.mafiatogether.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.util.Base64;
import mafia.mafiatogether.domain.Room;
import mafia.mafiatogether.domain.RoomInfo;
import mafia.mafiatogether.domain.RoomManager;
import mafia.mafiatogether.domain.Status;
import mafia.mafiatogether.service.dto.RoomCodeResponse;
import mafia.mafiatogether.service.dto.RoomCreateRequest;
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
        RoomCreateRequest request = new RoomCreateRequest(5, 1, 1, 1);

        //when
        RoomCodeResponse response = RestAssured.given().log().all()
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
        String code = roomManager.create(new RoomInfo(5, 1, 1, 1));
        String basic = Base64.getEncoder().encodeToString((code + ":" + "power").getBytes());

        //when
        RoomStatusResponse response = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Basic " + basic)
                .when().get("/rooms/status")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(RoomStatusResponse.class);

        //then
        Assertions.assertThat(response.status()).isEqualTo(Status.WAIT);
    }

    @Test
    void 방을_상태를_변경할_수_있다() {
        //given
        String code = roomManager.create(new RoomInfo(5, 1, 1, 1));
        String basic = Base64.getEncoder().encodeToString((code + ":" + "power").getBytes());
        RoomModifyRequest request = new RoomModifyRequest(Status.START);

        //when
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(request)
                .header("Authorization", "Basic " + basic)
                .when().patch("/rooms/status")
                .then().log().all()
                .statusCode(HttpStatus.OK.value());

        //then
        Room room = roomManager.findByCode(code);
        Assertions.assertThat(room.getStatus()).isEqualTo(Status.START);
    }

    @Test
    void 방에_참가할_수_있다() {
        //given
        String code = roomManager.create(new RoomInfo(5, 1, 1, 1));

        //when
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .when().get("/rooms?code=" + code + "&name=power")
                .then().log().all()
                .statusCode(HttpStatus.OK.value());

        //then
        Room room = roomManager.findByCode(code);
        Assertions.assertThat(room.getPlayers()).containsKey("power");
    }

    @Test
    void 방의_코드를_찾는다() {
        // given
        String code = roomManager.create(new RoomInfo(5, 1, 1, 1));
        String basic = Base64.getEncoder().encodeToString((code + ":" + "power").getBytes());

        // when & then
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Basic " + basic)
                .when().get("/rooms/code")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .body("code", Matchers.equalTo(code));
    }
}

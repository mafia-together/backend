package mafia.mafiatogether.controller;

import static org.hamcrest.Matchers.equalTo;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.util.Base64;
import mafia.mafiatogether.domain.Player;
import mafia.mafiatogether.domain.RoomInfo;
import mafia.mafiatogether.domain.RoomManager;
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
    void 직업을_조회한다() {
        //given
        String code = roomManager.create(new RoomInfo(5, 1, 1, 1));
        String basic = Base64.getEncoder().encodeToString((code + ":" + "power").getBytes());
        roomManager.findByCode(code).joinPlayer(Player.create("power"));

        //when
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Basic " + basic)
                .when().get("/player/role")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .body("role", equalTo("CITIZEN"));
    }
}

package mafia.mafiatogether.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.util.Base64;
import java.util.Map;
import mafia.mafiatogether.domain.Player;
import mafia.mafiatogether.domain.Room;
import mafia.mafiatogether.domain.RoomInfo;
import mafia.mafiatogether.domain.RoomManager;
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
class VoteControllerTest {

    @Autowired
    private RoomManager roomManager;

    @LocalServerPort
    private int port;

    private static String code;
    private Room room;
    private Player player1;
    private Player player2;
    private Player player3;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @BeforeEach
    void setRoom() {
        code = roomManager.create(new RoomInfo(3, 1, 1, 1));
        room = roomManager.findByCode(code);
        player1 = Player.create("power");
        player2 = Player.create("metthew");
        player3 = Player.create("dali");
        room.joinPlayer(player1);
        room.joinPlayer(player2);
        room.joinPlayer(player3);
    }

    @Test
    void 투표를_할_수_있다() {
        // given
        final String basic = Base64.getEncoder().encodeToString((code + ":" + player1.getName()).getBytes());

        // when & then
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Basic " + basic)
                .body(Map.of("name", player2.getName()))
                .when().post("/vote")
                .then().log().all()
                .statusCode(HttpStatus.NO_CONTENT.value());
        Assertions.assertThat(player1.getVote()).isEqualTo(player2);
    }
}

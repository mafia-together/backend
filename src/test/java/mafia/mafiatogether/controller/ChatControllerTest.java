package mafia.mafiatogether.controller;

import static org.hamcrest.Matchers.hasSize;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Map;
import mafia.mafiatogether.domain.Message;
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
public class ChatControllerTest {

    @Autowired
    private RoomManager roomManager;

    private String code;
    private Room room;
    private Player player;

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @BeforeEach
    void setRoom() {
        code = roomManager.create(new RoomInfo(5, 1, 1, 1));
        room = roomManager.findByCode(code);
        player = new Player("power");
        room.joinPlayer(player);
    }

    @Test
    void 채팅_내역을_조회할_수_있다() {
        // given
        final String basic = Base64.getEncoder().encodeToString((code + ":" + "power").getBytes());
        room.getChat().save(new Message(player, "contents1", Timestamp.valueOf(LocalDateTime.now())));
        room.getChat().save(new Message(player, "contents2", Timestamp.valueOf(LocalDateTime.now())));

        // when
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Basic " + basic)
                .when().get("/chat")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .body("contents", hasSize(2));

    }

    @Test
    void 채팅_전송을_할_수_있다() {
        // given
        final String basic = Base64.getEncoder().encodeToString((code + ":" + "power").getBytes());

        // when
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Basic " + basic)
                .body(Map.of("contents", "contents"))
                .when().post("/chat")
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value());

        // then
        Assertions.assertThat(room.getChat().getMessages().stream().map(Message::getContents))
                .contains("contents");
    }
}

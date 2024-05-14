package mafia.mafiatogether.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import mafia.mafiatogether.domain.Message;
import mafia.mafiatogether.domain.Player;
import mafia.mafiatogether.domain.Room;
import mafia.mafiatogether.domain.RoomInfo;
import mafia.mafiatogether.domain.RoomManager;
import mafia.mafiatogether.service.dto.ChatResponse;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
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

    private static String code;
    private Room room;
    private Player player1;
    private Player player2;

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
        player1 = Player.create("power");
        player2 = Player.create("metthew");
        room.joinPlayer(player1.getName());
        room.joinPlayer(player2.getName());
    }

    @Test
    void 채팅_내역을_조회할_수_있다() {
        // given
        final String basic = Base64.getEncoder().encodeToString((code + ":" + "power").getBytes());
        room.getChat().save(new Message(player1, "contents1", Timestamp.valueOf(LocalDateTime.now())));
        room.getChat().save(new Message(player2, "contents2", Timestamp.valueOf(LocalDateTime.now())));

        // when & then
        List<ChatResponse> responses = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Basic " + basic)
                .when().get("/chat")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .body()
                .jsonPath().getList(".", ChatResponse.class);

        SoftAssertions.assertSoftly(
                softAssertions -> {
                    softAssertions.assertThat(responses.get(0).name()).isEqualTo(player1.getName());
                    softAssertions.assertThat(responses.get(0).contents()).isEqualTo("contents1");
                    softAssertions.assertThat(responses.get(1).name()).isEqualTo(player2.getName());
                    softAssertions.assertThat(responses.get(1).contents()).isEqualTo("contents2");
                }
        );
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

    @ParameterizedTest(name = "{0} 채팅 전송에 실패한다.")
    @MethodSource("failCaseProvider")
    void 채팅_전송에_실패한다(
            final String testCase,
            final String code,
            final String name
    ) {
        // given
        final String basic = Base64.getEncoder().encodeToString((code + ":" + name).getBytes());

        // when & then
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Basic " + basic)
                .body(Map.of("contents", "contents"))
                .when().post("/chat")
                .then().log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    static Stream<Arguments> failCaseProvider() {
        return Stream.of(
                Arguments.of("방에 존재하지 않는 유저의 경우", code, "dali"),
                Arguments.of("존재하지 않는 방의 경우", "testCode", "metthew")
        );
    }
}

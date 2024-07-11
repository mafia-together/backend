package mafia.mafiatogether.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.time.Clock;
import java.util.Base64;
import java.util.Map;
import mafia.mafiatogether.job.domain.Player;
import mafia.mafiatogether.room.domain.Room;
import mafia.mafiatogether.room.domain.RoomInfo;
import mafia.mafiatogether.room.repository.RoomManager;
import mafia.mafiatogether.vote.domain.Vote;
import mafia.mafiatogether.vote.dto.response.VoteResultResponse;
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
        room.joinPlayer(player1.getName());
        room.joinPlayer(player2.getName());
        room.joinPlayer(player3.getName());
    }

    @Test
    void 투표를_할_수_있다() {
        // given
        final String basic = Base64.getEncoder().encodeToString((code + ":" + player1.getName()).getBytes());

        // when & then
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Basic " + basic)
                .body(Map.of("target", player2.getName()))
                .when().post("/vote")
                .then().log().all()
                .statusCode(HttpStatus.NO_CONTENT.value());

        Vote vote = room.getVote();
        vote.executeVote();
        Assertions.assertThat(vote.getVoteResult()).isEqualTo(player2.getName());
    }

    @Test
    void 투표에_기권_할_수_있다() {
        // given
        final String basic = Base64.getEncoder().encodeToString((code + ":" + player1.getName()).getBytes());

        // when & then
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Basic " + basic)
                .body(Map.of("target", ""))
                .when().post("/vote")
                .then().log().all()
                .statusCode(HttpStatus.NO_CONTENT.value());

        final Vote vote = room.getVote();
        vote.executeVote();
        Assertions.assertThat(vote.getVoteResult()).isBlank();
    }

    @Test
    void 투표_결과를_조회한다() {
        // given
        final String basic = Base64.getEncoder().encodeToString((code + ":" + player1.getName()).getBytes());
        room.votePlayer(player1.getName(), player2.getName(), Clock.systemDefaultZone().millis());
        room.votePlayer(player2.getName(), player2.getName(), Clock.systemDefaultZone().millis());
        room.votePlayer(player3.getName(), player1.getName(), Clock.systemDefaultZone().millis());
        room.executeVote();

        // when & then
        final VoteResultResponse voteResultResponse = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Basic " + basic)
                .when().get("/vote")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .body()
                .as(VoteResultResponse.class);
        Assertions.assertThat(voteResultResponse.dead()).isEqualTo(player2.getName());
    }
}

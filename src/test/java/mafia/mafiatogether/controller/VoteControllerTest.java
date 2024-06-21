package mafia.mafiatogether.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.time.Clock;
import java.util.Base64;
import java.util.Map;
import mafia.mafiatogether.domain.Player;
import mafia.mafiatogether.domain.Room;
import mafia.mafiatogether.domain.RoomInfo;
import mafia.mafiatogether.domain.Vote;
import mafia.mafiatogether.redis.RedisTestConfig;
import mafia.mafiatogether.repository.RoomRepository;
import mafia.mafiatogether.service.dto.VoteResultResponse;
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
class VoteControllerTest {

    @Autowired
    private RoomRepository roomRepository;

    @LocalServerPort
    private int port;

    private static final String CODE = "CODE";
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
        room = Room.create(CODE, RoomInfo.of(3, 1, 1, 1), Clock.systemDefaultZone().millis());
        player1 = Player.create("power");
        player2 = Player.create("metthew");
        player3 = Player.create("dali");
        room.joinPlayer(player1.getName());
        room.joinPlayer(player2.getName());
        room.joinPlayer(player3.getName());
        roomRepository.save(room);
    }

    @Test
    void 투표를_할_수_있다() {
        // given
        final String basic = Base64.getEncoder().encodeToString((CODE + ":" + player1.getName()).getBytes());

        // when & then
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Basic " + basic)
                .body(Map.of("target", player2.getName()))
                .when().post("/vote")
                .then().log().all()
                .statusCode(HttpStatus.NO_CONTENT.value());

        Vote vote = roomRepository.findById(CODE).get().getVote();
        vote.executeVote();
        Assertions.assertThat(vote.getVoteResult()).isEqualTo(player2.getName());
    }

    @Test
    void 투표에_기권_할_수_있다() {
        // given
        final String basic = Base64.getEncoder().encodeToString((CODE + ":" + player1.getName()).getBytes());

        // when & then
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Basic " + basic)
                .body(Map.of("target", ""))
                .when().post("/vote")
                .then().log().all()
                .statusCode(HttpStatus.NO_CONTENT.value());

        Vote vote = roomRepository.findById(CODE).get().getVote();
        vote.executeVote();
        Assertions.assertThat(vote.getVoteResult()).isBlank();
    }

    @Test
    void 투표_결과를_조회한다() {
        // given
        final String basic = Base64.getEncoder().encodeToString((CODE + ":" + player1.getName()).getBytes());
        room.votePlayer(player1.getName(), player2.getName(), Clock.systemDefaultZone().millis());
        room.votePlayer(player2.getName(), player2.getName(), Clock.systemDefaultZone().millis());
        room.votePlayer(player3.getName(), player1.getName(), Clock.systemDefaultZone().millis());
        room.executeVote();
        roomRepository.save(room);

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

package mafia.mafiatogether.vote.ui;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.time.Clock;
import java.util.Base64;
import java.util.Map;
import mafia.mafiatogether.game.domain.Game;
import mafia.mafiatogether.game.domain.GameRepository;
import mafia.mafiatogether.global.ControllerTest;
import mafia.mafiatogether.room.domain.Room;
import mafia.mafiatogether.room.domain.RoomInfo;
import mafia.mafiatogether.vote.application.dto.response.VoteResultResponse;
import mafia.mafiatogether.vote.domain.VoteRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

@SuppressWarnings("NonAsciiCharacters")
class VoteControllerTest extends ControllerTest {

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private VoteRepository voteRepository;

    private final static String CODE = "1234567890";
    private final static String PLAYER1_NAME = "player1";
    private final static String PLAYER2_NAME = "player2";
    private final static String PLAYER3_NAME = "player3";
    private final static String PLAYER4_NAME = "player4";
    private final static String PLAYER5_NAME = "player5";

    @BeforeEach
    void setTest() {
        final Room room = Room.create(CODE, RoomInfo.of(5, 2, 1, 1));
        room.joinPlayer(PLAYER1_NAME);
        room.joinPlayer(PLAYER2_NAME);
        room.joinPlayer(PLAYER3_NAME);
        room.joinPlayer(PLAYER4_NAME);
        room.joinPlayer(PLAYER5_NAME);
        final Game game = Game.create(room, Clock.systemDefaultZone().millis());
        game.distributeRole();
        gameRepository.save(game);
    }

    @AfterEach
    void clearTest() {
        voteRepository.deleteById(CODE);
        gameRepository.deleteById(CODE);
    }

    @Test
    void 투표를_할_수_있다() {
        // given
        final String basic = Base64.getEncoder().encodeToString((CODE + ":" + PLAYER1_NAME).getBytes());
        final String expect = PLAYER2_NAME;

        // when & then
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Basic " + basic)
                .body(Map.of("target", expect))
                .when().post("/vote")
                .then().log().all()
                .statusCode(HttpStatus.NO_CONTENT.value());

        String actual = voteRepository.findById(CODE).get().countVotes();
        Assertions.assertThat(actual).isEqualTo(expect);
    }

    @Test
    void 투표에_기권_할_수_있다() {
        // given
        final String basic = Base64.getEncoder().encodeToString((CODE + ":" + PLAYER1_NAME).getBytes());

        // when & then
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Basic " + basic)
                .body(Map.of("target", ""))
                .when().post("/vote")
                .then().log().all()
                .statusCode(HttpStatus.NO_CONTENT.value());

        String actual = voteRepository.findById(CODE).get().countVotes();
        Assertions.assertThat(actual).isBlank();
    }

    @Test
    void 투표_결과를_조회한다() {
        // given
        final String basic = Base64.getEncoder().encodeToString((CODE + ":" + PLAYER1_NAME).getBytes());
        final String expect = PLAYER1_NAME;
        voteTarget(PLAYER1_NAME, PLAYER5_NAME);
        voteTarget(PLAYER2_NAME, PLAYER5_NAME);
        voteTarget(PLAYER3_NAME, expect);
        voteTarget(PLAYER4_NAME, expect);
        voteTarget(PLAYER5_NAME, expect);

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

        Assertions.assertThat(voteResultResponse.dead()).isEqualTo(expect);
    }

    private void voteTarget(final String name, final String target) {
        final String basic = Base64.getEncoder().encodeToString((CODE + ":" + name).getBytes());
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Basic " + basic)
                .body(Map.of("target", target))
                .when().post("/vote")
                .then().log().all()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }
}

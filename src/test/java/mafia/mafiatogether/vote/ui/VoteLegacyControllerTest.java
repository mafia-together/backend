package mafia.mafiatogether.vote.ui;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import mafia.mafiatogether.global.ControllerTest;
import mafia.mafiatogether.vote.application.dto.response.VoteResultResponse;
import mafia.mafiatogether.vote.domain.Vote;
import mafia.mafiatogether.vote.domain.VoteRepositoryImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

@SuppressWarnings("NonAsciiCharacters")
class VoteLegacyControllerTest extends ControllerTest {

    @Autowired
    private VoteRepositoryImpl voteRepository;

    private final static String code = "1234567890";
    private final static String PLAYER1_NAME = "player1";
    private final static String PLAYER2_NAME = "player2";
    private final static String PLAYER3_NAME = "player3";
    private final static String PLAYER4_NAME = "player4";
    private final static String PLAYER5_NAME = "player5";

    @AfterEach
    void clearTest() {
        voteRepository.deleteAllByCode(code);
    }

    @Test
    void 투표를_할_수_있다() {
        // given
        final String basic = Base64.getEncoder().encodeToString((code + ":" + PLAYER1_NAME).getBytes());
        final String expect = PLAYER2_NAME;

        // when & then
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Basic " + basic)
                .body(Map.of("target", expect))
                .when().post("/vote")
                .then().log().all()
                .statusCode(HttpStatus.NO_CONTENT.value());

        Vote actual = voteRepository.findAllByCode(code).stream()
                .filter(vote -> vote.getName().equals(PLAYER1_NAME))
                .findFirst()
                .get();
        Assertions.assertThat(actual.getTarget()).isEqualTo(expect);
    }

    @Test
    void 투표에_기권_할_수_있다() {
        // given
        final String basic = Base64.getEncoder().encodeToString((code + ":" + PLAYER1_NAME).getBytes());

        // when & then
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Basic " + basic)
                .body(Map.of("target", ""))
                .when().post("/vote")
                .then().log().all()
                .statusCode(HttpStatus.NO_CONTENT.value());

        Vote actual = voteRepository.findAllByCode(code).stream()
                .filter(vote -> vote.getName().equals(PLAYER1_NAME))
                .findFirst()
                .get();
        Assertions.assertThat(actual.getTarget()).isBlank();
    }

    @Test
    void 투표_결과를_조회한다() {
        // given
        final String basic = Base64.getEncoder().encodeToString((code + ":" + PLAYER1_NAME).getBytes());
        final String expect = PLAYER1_NAME;
        List<Vote> v = voteRepository.getVotes();
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
        final String basic = Base64.getEncoder().encodeToString((code + ":" + name).getBytes());
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Basic " + basic)
                .body(Map.of("target", target))
                .when().post("/vote")
                .then().log().all()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }
}

package mafia.mafiatogether.vote.ui;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.util.Base64;
import java.util.Map;
import mafia.mafiatogether.global.ControllerTest;
import mafia.mafiatogether.vote.application.dto.response.VoteResultResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@SuppressWarnings("NonAsciiCharacters")
class VoteControllerTest extends ControllerTest {

    @BeforeEach
    void setTest(){
        setRoom();
        setGame();
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
                .when().post("/votes")
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
                .when().post("/votes")
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
                .when().get("/votes")
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
                .when().post("/votes")
                .then().log().all()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }
}

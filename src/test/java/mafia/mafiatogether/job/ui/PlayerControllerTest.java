package mafia.mafiatogether.job.ui;

import static org.hamcrest.Matchers.equalTo;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.util.Base64;
import java.util.Map;
import mafia.mafiatogether.config.exception.ExceptionCode;
import mafia.mafiatogether.global.ControllerTest;
import mafia.mafiatogether.job.domain.JobTarget;
import mafia.mafiatogether.job.domain.PlayerJobRepository;
import mafia.mafiatogether.job.domain.SkillRepository;
import mafia.mafiatogether.job.domain.jobtype.JobType;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

@SuppressWarnings("NonAsciiCharacters")
class PlayerControllerTest extends ControllerTest {

    @Autowired
    private SkillRepository skillRepository;

    @Autowired
    private PlayerJobRepository playerJobRepository;

    @BeforeEach
    void setTest() {
        setRoom();
        setGame();
    }


    @AfterEach
    void clearTest() {
        skillRepository.deleteById(CODE);
        playerJobRepository.deleteAllByCode(CODE);
    }

    @Test
    void 직업_기술을_사용한다() {
        // given
        String basic = Base64.getEncoder().encodeToString((CODE + ":" + MAFIA1).getBytes());

        // when & then
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(Map.of("target", CITIZEN))
                .header("Authorization", "Basic " + basic)
                .when().post("/players/skill")
                .then().log().all()
                .statusCode(HttpStatus.OK.value());

        final JobTarget actual = skillRepository.findById(CODE).get().findJobTargetBy(JobType.MAFIA);
        Assertions.assertThat(actual.getTarget()).isEqualTo(CITIZEN);
    }

    @Test
    void 초기_마피아_타겟은_NULL_값이다() {
        // given
        String basic = Base64.getEncoder().encodeToString((CODE + ":" + MAFIA1).getBytes());

        // when & then
        final String actual = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Basic " + basic)
                .when().get("/players/skill")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .body().jsonPath().getString("target");

        Assertions.assertThat(actual).isNull();
    }

    @Test
    void 마피아가_빈문자열을_보낼시_아무도_죽지않는다() {
        // given
        executeSkill(MAFIA1, CITIZEN);

        // when
        executeSkill(MAFIA1, "");

        // then
        final String actual = skillRepository.findById(CODE).get().findJobTargetBy(JobType.MAFIA).getTarget();
        Assertions.assertThat(actual).isBlank();

    }

    private void executeSkill(final String name, final String target) {
        String basic = Base64.getEncoder().encodeToString((CODE + ":" + name).getBytes());

        // when & then
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(Map.of("target", target))
                .header("Authorization", "Basic " + basic)
                .when().post("/players/skill")
                .then().log().all();
    }

    @Test
    void 방에_없는_사람에게_직업_기술_사용시_실패한다() {
        // given
        final String target = "not in room";
        String basic = Base64.getEncoder().encodeToString((CODE + ":" + MAFIA1).getBytes());

        // when
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(Map.of("target", target))
                .header("Authorization", "Basic " + basic)
                .when().post("/players/skill")
                .then().log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("errorCode", equalTo(ExceptionCode.INVALID_PLAYER.getCode()));
    }

    @Test
    void 직업을_조회한다() {
        //given
        String basic = Base64.getEncoder().encodeToString((CODE + ":" + CITIZEN).getBytes());

        // when & then
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Basic " + basic)
                .when().get("/players/my/job")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .body("job", equalTo("CITIZEN"));
    }
}

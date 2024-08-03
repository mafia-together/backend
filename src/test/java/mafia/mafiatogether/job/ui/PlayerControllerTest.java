package mafia.mafiatogether.job.ui;

import static org.hamcrest.Matchers.equalTo;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.util.Base64;
import java.util.Map;
import mafia.mafiatogether.config.exception.ExceptionCode;
import mafia.mafiatogether.global.ControllerTest;
import mafia.mafiatogether.job.domain.JobTarget;
import mafia.mafiatogether.job.domain.JobTargetRepository;
import mafia.mafiatogether.job.domain.PlayerJob;
import mafia.mafiatogether.job.domain.PlayerJobRepository;
import mafia.mafiatogether.job.domain.jobtype.Citizen;
import mafia.mafiatogether.job.domain.jobtype.Doctor;
import mafia.mafiatogether.job.domain.jobtype.JobType;
import mafia.mafiatogether.job.domain.jobtype.Mafia;
import mafia.mafiatogether.job.domain.jobtype.Police;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

@SuppressWarnings("NonAsciiCharacters")
class PlayerControllerTest extends ControllerTest {

    @Autowired
    private JobTargetRepository jobTargetRepository;

    @Autowired
    private PlayerJobRepository playerJobRepository;

    private static final String CODE = "1234567890";
    private static final String MAFIA1 = "mafia1";
    private static final String MAFIA2 = "mafia2";
    private static final String DOCTOR = "doctor";
    private static final String POLICE = "police";
    private static final String CITIZEN = "citizen";

    @BeforeEach
    void setTest() {
        final PlayerJob mafia1 = new PlayerJob(CODE, MAFIA1, new Mafia());
        final PlayerJob mafia2 = new PlayerJob(CODE, MAFIA2, new Mafia());
        final PlayerJob doctor = new PlayerJob(CODE, DOCTOR, new Doctor());
        final PlayerJob police = new PlayerJob(CODE, POLICE, new Police());
        final PlayerJob citizen = new PlayerJob(CODE, CITIZEN, new Citizen());
        playerJobRepository.save(mafia1);
        playerJobRepository.save(mafia2);
        playerJobRepository.save(doctor);
        playerJobRepository.save(police);
        playerJobRepository.save(citizen);
    }

    @AfterEach
    void clearTest() {
        jobTargetRepository.deleteAllByCode(CODE);
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

        final JobTarget actual = jobTargetRepository.findByCodeAndJobType(CODE, JobType.MAFIA).get();
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
        final String actual = jobTargetRepository.findByCodeAndJobType(CODE, JobType.MAFIA)
                .get()
                .getTarget();
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

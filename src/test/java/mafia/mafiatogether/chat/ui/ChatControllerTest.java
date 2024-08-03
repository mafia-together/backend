package mafia.mafiatogether.chat.ui;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.time.Clock;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import mafia.mafiatogether.chat.application.dto.response.ChatResponse;
import mafia.mafiatogether.chat.domain.Chat;
import mafia.mafiatogether.chat.domain.ChatRepository;
import mafia.mafiatogether.chat.domain.Message;
import mafia.mafiatogether.game.domain.Player;
import mafia.mafiatogether.global.ControllerTest;
import mafia.mafiatogether.job.domain.PlayerJob;
import mafia.mafiatogether.job.domain.PlayerJobRepository;
import mafia.mafiatogether.job.domain.jobtype.Citizen;
import mafia.mafiatogether.job.domain.jobtype.Doctor;
import mafia.mafiatogether.job.domain.jobtype.JobType;
import mafia.mafiatogether.job.domain.jobtype.Mafia;
import mafia.mafiatogether.job.domain.jobtype.Police;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

@SuppressWarnings("NonAsciiCharacters")
public class ChatControllerTest extends ControllerTest {

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private PlayerJobRepository playerJobRepository;

    private static final String CODE = "1234567890";
    private static final String MAFIA1 = "mafia1";
    private static final String MAFIA2 = "mafia2";
    private static final String POLICE = "police";
    private static final String DOCTOR = "doctor";
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

        Chat chat = new Chat(CODE, new ArrayList<>());
        chat.saveMessage(new Message(MAFIA1, "contents1", Clock.systemDefaultZone().millis()));
        chat.saveMessage(new Message(MAFIA2, "contents2", Clock.systemDefaultZone().millis()));
        chat.saveMessage(new Message(POLICE, "contents3", Clock.systemDefaultZone().millis()));
        chat.saveMessage(new Message(DOCTOR, "contents4", Clock.systemDefaultZone().millis()));
        chat.saveMessage(new Message(CITIZEN, "contents5", Clock.systemDefaultZone().millis()));
        chatRepository.save(chat);
    }

    @AfterEach
    void clearTest() {
        playerJobRepository.deleteAllByCode(CODE);
        chatRepository.deleteById(CODE);
    }

    @ParameterizedTest(name = "{0}이 조회시 다른 사람 직업은 전부 null로 반환된다.")
    @MethodSource("citizenPlayerProvider")
    void 채팅_내역을_조회할_수_있다(
            String testCase,
            Player player,
            JobType jobType
    ) {
        // given
        final String basic = Base64.getEncoder().encodeToString((CODE + ":" + player.getName()).getBytes());

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

        JobType playerJob = responses.stream()
                .filter(ChatResponse::isOwner)
                .findFirst()
                .get()
                .job();
        Long countOtherJobs = responses.stream()
                .filter(response -> response.job() != null && !response.isOwner())
                .count();
        SoftAssertions.assertSoftly(
                softAssertions -> {
                    softAssertions.assertThat(playerJob).isEqualTo(jobType);
                    softAssertions.assertThat(countOtherJobs).isEqualTo(0);
                }
        );
    }

    static Stream<Arguments> citizenPlayerProvider() {
        return Stream.of(
                Arguments.of("시민", CITIZEN, JobType.CITIZEN),
                Arguments.of("경찰", POLICE, JobType.POLICE),
                Arguments.of("의사", DOCTOR, JobType.DOCTOR)
        );
    }

    @Test
    void 마피아_채팅_조회시_다른_마피아들을_확인할_수_있다() {
        // given
        final String basic = Base64.getEncoder().encodeToString((CODE + ":" + MAFIA1).getBytes());

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

        JobType playerJob = responses.stream()
                .filter(ChatResponse::isOwner)
                .findFirst()
                .get()
                .job();
        JobType otherMafia = responses.stream()
                .filter(response -> response.name().equals(MAFIA2))
                .findFirst()
                .get()
                .job();
        Long countOtherJobs = responses.stream()
                .filter(response -> response.job() != null && response.job() != JobType.MAFIA)
                .count();

        SoftAssertions.assertSoftly(
                softAssertions -> {
                    softAssertions.assertThat(playerJob).isEqualTo(JobType.MAFIA);
                    softAssertions.assertThat(otherMafia).isEqualTo(JobType.MAFIA);
                    softAssertions.assertThat(countOtherJobs).isEqualTo(0);
                }
        );
    }

    @Test
    void 채팅_전송을_할_수_있다() {
        // given
        final String basic = Base64.getEncoder().encodeToString((CODE + ":" + CITIZEN).getBytes());

        // when
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Basic " + basic)
                .body(Map.of("contents", "contents"))
                .when().post("/chat")
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value());

        // then
        final Long actual = chatRepository.findById(CODE).get().getMessages().stream().count();
        Assertions.assertThat(actual).isEqualTo(6);
    }
}

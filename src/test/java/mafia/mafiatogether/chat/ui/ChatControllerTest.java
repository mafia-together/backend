package mafia.mafiatogether.chat.ui;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.time.Clock;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import mafia.mafiatogether.chat.application.dto.response.ChatResponse;
import mafia.mafiatogether.chat.domain.Chat;
import mafia.mafiatogether.chat.domain.ChatRepository;
import mafia.mafiatogether.chat.domain.Message;
import mafia.mafiatogether.global.ControllerTest;
import mafia.mafiatogether.job.domain.jobtype.JobType;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

@SuppressWarnings("NonAsciiCharacters")
class ChatControllerTest extends ControllerTest {

    @Autowired
    private ChatRepository chatRepository;

    @BeforeEach
    void setTest() {
        setLobby();
        setGame();

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
        chatRepository.deleteById(CODE);
    }

    @Test
    void 시민이_채팅_내역을_조회할_수_있다() {
        // given
        final String basic = Base64.getEncoder().encodeToString((CODE + ":" + CITIZEN).getBytes());

        // when & then
        List<ChatResponse> responses = getChatResponses(basic);

        assuredResponse(responses, JobType.CITIZEN);
    }

    @Test
    void 의사이_채팅_내역을_조회할_수_있다() {
        // given
        final String basic = Base64.getEncoder().encodeToString((CODE + ":" + DOCTOR).getBytes());

        // when & then
        List<ChatResponse> responses = getChatResponses(basic);

        assuredResponse(responses, JobType.DOCTOR);
    }

    @Test
    void 경찰이_채팅_내역을_조회할_수_있다() {
        // given
        final String basic = Base64.getEncoder().encodeToString((CODE + ":" + POLICE).getBytes());

        // when & then
        List<ChatResponse> responses = getChatResponses(basic);

        assuredResponse(responses, JobType.POLICE);
    }

    private static List<ChatResponse> getChatResponses(String basic) {
        List<ChatResponse> responses = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Basic " + basic)
                .when().get("/chat")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .body()
                .jsonPath().getList(".", ChatResponse.class);
        return responses;
    }

    private static void assuredResponse(List<ChatResponse> responses, JobType jobType) {
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

    @Test
    void 마피아_채팅_조회시_다른_마피아들을_확인할_수_있다() {
        // given
        final String basic = Base64.getEncoder().encodeToString((CODE + ":" + MAFIA1).getBytes());

        // when & then
        List<ChatResponse> responses = getChatResponses(basic);

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
        Long countMafia = responses.stream()
                .filter(response -> response.job() != null && response.job().equals(JobType.MAFIA))
                .count();

        SoftAssertions.assertSoftly(
                softAssertions -> {
                    softAssertions.assertThat(playerJob).isEqualTo(JobType.MAFIA);
                    softAssertions.assertThat(otherMafia).isEqualTo(JobType.MAFIA);
                    softAssertions.assertThat(countOtherJobs).isEqualTo(0);
                    softAssertions.assertThat(countMafia).isEqualTo(2);
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
                .body(Map.of("content", "content"))
                .when().post("/chat")
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value());

        // then
        final Long actual = chatRepository.findById(CODE).get().getMessages().stream().count();
        Assertions.assertThat(actual).isEqualTo(6);
    }
}

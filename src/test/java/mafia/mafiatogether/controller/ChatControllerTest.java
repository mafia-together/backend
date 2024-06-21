package mafia.mafiatogether.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import mafia.mafiatogether.domain.Message;
import mafia.mafiatogether.domain.Player;
import mafia.mafiatogether.domain.Room;
import mafia.mafiatogether.domain.job.JobType;
import mafia.mafiatogether.service.dto.ChatResponse;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpStatus;

@SuppressWarnings("NonAsciiCharacters")
public class ChatControllerTest extends ControllerTest {

    @ParameterizedTest(name = "{0}이 조회시 다른 사람 직업은 전부 null로 반환된다.")
    @MethodSource("citizenPlayerProvider")
    void 채팅_내역을_조회할_수_있다(
            String testCase,
            JobType jobType
    ) {
        // given
        final Room room = roomRepository.findById(CODE).get();
        final Player player = room.getPlayers().values().stream()
                .filter(p -> p.getJobType().equals(jobType))
                .findFirst()
                .get();
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
                .filter(response -> response.isOwner())
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
                Arguments.of("시민", JobType.CITIZEN),
                Arguments.of("경찰", JobType.POLICE),
                Arguments.of("의사", JobType.DOCTOR)
        );
    }

    @Test
    void 마피아_채팅_조회시_다른_마피아들을_확인할_수_있다() {
        // given
        final Room room = roomRepository.findById(CODE).get();
        final Player mafia = room.getPlayers().values().stream()
                .filter(player -> player.isMafia())
                .findFirst()
                .get();
        final Player otherMafia = room.getPlayers().values().stream()
                .filter(player -> player.isMafia() && !player.equals(mafia))
                .findFirst()
                .get();
        final String basic = Base64.getEncoder().encodeToString((CODE + ":" + mafia.getName()).getBytes());

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
                .filter(response -> response.isOwner())
                .findFirst()
                .get()
                .job();
        JobType otherMafiaJob = responses.stream()
                .filter(response -> response.name().equals(otherMafia.getName()))
                .findFirst()
                .get()
                .job();
        Long countOtherJobs = responses.stream()
                .filter(response -> response.job() != null && response.job() != JobType.MAFIA)
                .count();

        SoftAssertions.assertSoftly(
                softAssertions -> {
                    softAssertions.assertThat(playerJob).isEqualTo(JobType.MAFIA);
                    softAssertions.assertThat(otherMafiaJob).isEqualTo(JobType.MAFIA);
                    softAssertions.assertThat(countOtherJobs).isEqualTo(0);
                }
        );
    }

    @Test
    void 채팅_전송을_할_수_있다() {
        // given
        final String basic = Base64.getEncoder().encodeToString((CODE + ":" + player1.getName()).getBytes());

        // when
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Basic " + basic)
                .body(Map.of("contents", "contents"))
                .when().post("/chat")
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value());

        // then
        Room room = roomRepository.findById(CODE).get();
        Assertions.assertThat(room.getChat().getMessages().stream().map(Message::getContents))
                .contains("contents");
    }

    @ParameterizedTest(name = "{0} 채팅 전송에 실패한다.")
    @MethodSource("failCaseProvider")
    void 채팅_전송에_실패한다(
            final String testCase,
            final String code,
            final String name,
            final Optional<String> contents
    ) {
        // given
        final String basic = Base64.getEncoder().encodeToString((code + ":" + name).getBytes());

        // when & then
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Basic " + basic)
                .body(Map.of("contents", contents))
                .when().post("/chat")
                .then().log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    static Stream<Arguments> failCaseProvider() {
        return Stream.of(
                Arguments.of("방에 존재하지 않는 유저의 경우", CODE, "p-1", Optional.of("contents")),
                Arguments.of("존재하지 않는 방의 경우", "wrongCode", "p1", Optional.of("contents")),
                Arguments.of("문자열이 비어있는 경우", CODE, "p1", Optional.empty())
        );
    }
}

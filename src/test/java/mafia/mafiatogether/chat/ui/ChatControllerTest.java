package mafia.mafiatogether.chat.ui;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import mafia.mafiatogether.chat.domain.Message;
import mafia.mafiatogether.chat.application.dto.response.ChatResponse;
import mafia.mafiatogether.global.ControllerTest;
import mafia.mafiatogether.job.domain.Citizen;
import mafia.mafiatogether.job.domain.Doctor;
import mafia.mafiatogether.job.domain.JobType;
import mafia.mafiatogether.job.domain.Mafia;
import mafia.mafiatogether.game.domain.Player;
import mafia.mafiatogether.job.domain.Police;
import mafia.mafiatogether.room.domain.Room;
import mafia.mafiatogether.room.domain.RoomInfo;
import mafia.mafiatogether.room.domain.RoomRepository;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
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
    private RoomRepository roomRepository;

    private static String code;
    private Room room;
    private Player player1;
    private Player player2;
    private Player player3;
    private Player player4;
    private Player player5;

    @BeforeEach
    void setRoom() {
        code = roomRepository.create(new RoomInfo(5, 1, 1, 1));
        room = roomRepository.findByCode(code);
        room.joinPlayer("p1");
        room.joinPlayer("p2");
        room.joinPlayer("p3");
        room.joinPlayer("p4");
        room.joinPlayer("p5");
        player1 = room.getPlayer("p1");
        player2 = room.getPlayer("p2");
        player3 = room.getPlayer("p3");
        player4 = room.getPlayer("p4");
        player5 = room.getPlayer("p5");
        player1.modifyJob(new Citizen());
        player2.modifyJob(new Police());
        player3.modifyJob(new Doctor());
        player4.modifyJob(new Mafia());
        player5.modifyJob(new Mafia());

        room.getChat().save(new Message(player1, "contents1", Timestamp.valueOf(LocalDateTime.now())));
        room.getChat().save(new Message(player2, "contents2", Timestamp.valueOf(LocalDateTime.now())));
        room.getChat().save(new Message(player3, "contents3", Timestamp.valueOf(LocalDateTime.now())));
        room.getChat().save(new Message(player4, "contents4", Timestamp.valueOf(LocalDateTime.now())));
        room.getChat().save(new Message(player5, "contents5", Timestamp.valueOf(LocalDateTime.now())));
    }

    @ParameterizedTest(name = "{0}이 조회시 다른 사람 직업은 전부 null로 반환된다.")
    @MethodSource("citizenPlayerProvider")
    void 채팅_내역을_조회할_수_있다(
            String testCase,
            Player player,
            JobType jobType
    ) {
        // given
        final String basic = Base64.getEncoder().encodeToString((code + ":" + player.getName()).getBytes());

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
                Arguments.of("시민", "p1", JobType.CITIZEN),
                Arguments.of("경찰", "p2", JobType.POLICE),
                Arguments.of("의사", "p3", JobType.DOCTOR)
        );
    }

    @Test
    void 마피아_채팅_조회시_다른_마피아들을_확인할_수_있다() {
        // given
        final String basic = Base64.getEncoder().encodeToString((code + ":" + player4.getName()).getBytes());

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
                .filter(response -> response.name().equals(player5.getName()))
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
        final String basic = Base64.getEncoder().encodeToString((code + ":" + player1.getName()).getBytes());

        // when
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Basic " + basic)
                .body(Map.of("contents", "contents"))
                .when().post("/chat")
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value());

        // then
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
                Arguments.of("방에 존재하지 않는 유저의 경우", code, "p-1", Optional.of("contents")),
                Arguments.of("존재하지 않는 방의 경우", "testCode", "p1", Optional.of("contents")),
                Arguments.of("문자열이 비어있는 경우", code, "p1", Optional.empty())
        );
    }
}

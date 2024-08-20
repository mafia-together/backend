package mafia.mafiatogether.lobby.ui;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.util.Base64;
import java.util.List;
import java.util.stream.Stream;
import mafia.mafiatogether.config.exception.ErrorResponse;
import mafia.mafiatogether.config.exception.ExceptionCode;
import mafia.mafiatogether.global.ControllerTest;
import mafia.mafiatogether.lobby.application.dto.request.LobbyCreateRequest;
import mafia.mafiatogether.lobby.application.dto.response.LobbyCodeResponse;
import mafia.mafiatogether.lobby.domain.Lobby;
import mafia.mafiatogether.lobby.domain.LobbyInfo;
import mafia.mafiatogether.lobby.domain.LobbyRepository;
import org.assertj.core.api.Assertions;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

@SuppressWarnings("NonAsciiCharacters")
class LobbyControllerTest extends ControllerTest {

    @Autowired
    private LobbyRepository lobbyRepository;

    @BeforeEach
    void setTest(){
        final Lobby lobby = Lobby.create(CODE, LobbyInfo.of(3,1,1,1));
        lobbyRepository.save(lobby);
    }

    @AfterEach
    void clearTest(){
        lobbyRepository.deleteById(CODE);
    }

    @Test
    void 방을_생성할_수_있다() {
        //given
        final LobbyCreateRequest request = new LobbyCreateRequest(5, 1, 1, 1);

        //when
        final LobbyCodeResponse response = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/lobbies")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(LobbyCodeResponse.class);

        //then
        Assertions.assertThat(response.code()).isNotBlank();
    }

    @ParameterizedTest(name = "{0}일 때 방 생성에 실패한다")
    @MethodSource("provideRoomCreateFailCase")
    void 방_생성에_실패한다(
            final String testCase,
            final int total,
            final int mafia
    ) {
        //given
        final LobbyCreateRequest request = new LobbyCreateRequest(total, mafia, 1, 1);

        //when
        final ErrorResponse response = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/lobbies")
                .then().log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                .as(ErrorResponse.class);

        // then
        Assertions.assertThat(response.errorCode()).isEqualTo(ExceptionCode.INVALID_ROOM_INFORMATION.getCode());
    }

    static Stream<Arguments> provideRoomCreateFailCase() {
        return Stream.of(
                Arguments.of("마피아 수가 허용인원 수보다 많을때", 3, 2),
                Arguments.of("총인원수가 3 미만일때", 2, 1),
                Arguments.of("마피아 수가 0일때", 3, 0)
        );
    }

    @Test
    void 방에_참가할_수_있다() {
        //when
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .when().get("/lobbies?code=" + CODE + "&name=power")
                .then().log().all()
                .statusCode(HttpStatus.OK.value());

        //then
        Lobby lobby = lobbyRepository.findById(CODE).get();
        List<String> actual = lobby.getParticipants().getParticipants().stream()
                .map(participant -> participant.getName())
                .toList();
        Assertions.assertThat(actual).contains("power");
    }

    @Test
    void 처음_참가한_참가자가_방장이다() {
        // given
        final String expect = "master";

        //when
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .when().get("/lobbies?code=" + CODE + "&name=" + expect)
                .then().log().all()
                .statusCode(HttpStatus.OK.value());
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .when().get("/lobbies?code=" + CODE + "&name=not_master")
                .then().log().all()
                .statusCode(HttpStatus.OK.value());

        //then
        String actual = lobbyRepository.findById(CODE).get().getMaster().getName();
        Assertions.assertThat(actual).isEqualTo(expect);
    }

    @Test
    void 방이_꽉_차_있을때_참가에_실패한다() {
        //given
        final Lobby lobby = lobbyRepository.findById(CODE).get();
        lobby.joinPlayer("A");
        lobby.joinPlayer("C");
        lobby.joinPlayer("D");
        lobbyRepository.save(lobby);
        //when
        final ErrorResponse response = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .when().get("/lobbies?code=" + CODE + "&name=E")
                .then().log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                .as(ErrorResponse.class);

        //then
        Assertions.assertThat(response.errorCode()).isEqualTo(ExceptionCode.ROOM_FULL.getCode());
    }

    @Test
    void 방에_이미_존재한느_이름으로_참가할때_참가에_실패한다() {
        //given
        final Lobby lobby = lobbyRepository.findById(CODE).get();
        lobby.joinPlayer("A");
        lobbyRepository.save(lobby);
        //when
        final ErrorResponse response = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .when().get("/lobbies?code=" + CODE + "&name=A")
                .then().log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                .as(ErrorResponse.class);

        //then
        Assertions.assertThat(response.errorCode()).isEqualTo(ExceptionCode.INVALID_NAMES.getCode());
    }

    @Test
    void 방의_코드를_찾는다() {
        // given
        final String basic = Base64.getEncoder().encodeToString((CODE + ":" + "power").getBytes());

        // when & then
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Basic " + basic)
                .when().get("/lobbies/code")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .body("code", Matchers.equalTo(CODE));
    }

    @Test
    void 방의_코드를_검증_할_수_있다() {
        // when & then
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .when().get("/lobbies/code/exist?code=" + CODE)
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .body("exist", Matchers.equalTo(true));
    }
}

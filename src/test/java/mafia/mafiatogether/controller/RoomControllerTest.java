package mafia.mafiatogether.controller;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.time.Clock;
import java.util.Base64;
import java.util.stream.Stream;
import mafia.mafiatogether.config.exception.ErrorResponse;
import mafia.mafiatogether.config.exception.ExceptionCode;
import mafia.mafiatogether.domain.Player;
import mafia.mafiatogether.domain.Room;
import mafia.mafiatogether.domain.RoomInfo;
import mafia.mafiatogether.domain.RoomManager;
import mafia.mafiatogether.domain.job.JobType;
import mafia.mafiatogether.domain.status.StatusType;
import mafia.mafiatogether.service.dto.RoomCodeResponse;
import mafia.mafiatogether.service.dto.RoomCreateRequest;
import mafia.mafiatogether.service.dto.RoomInfoResponse;
import mafia.mafiatogether.service.dto.RoomModifyRequest;
import mafia.mafiatogether.service.dto.RoomStatusResponse;
import org.assertj.core.api.Assertions;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

@SuppressWarnings("NonAsciiCharacters")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class RoomControllerTest {

    @Autowired
    private RoomManager roomManager;

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }


    @Test
    void 방을_생성할_수_있다() {
        //given
        final RoomCreateRequest request = new RoomCreateRequest(5, 1, 1, 1);

        //when
        final RoomCodeResponse response = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/rooms")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(RoomCodeResponse.class);

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
        final RoomCreateRequest request = new RoomCreateRequest(total, mafia, 1, 1);

        //when
        final ErrorResponse response = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/rooms")
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
    void 방을_상태를_확인할_수_있다() {
        //given
        final String code = roomManager.create(new RoomInfo(5, 1, 1, 1));
        final String basic = Base64.getEncoder().encodeToString((code + ":" + "power").getBytes());

        //when
        final RoomStatusResponse response = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Basic " + basic)
                .when().get("/rooms/status")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(RoomStatusResponse.class);

        //then
        Assertions.assertThat(response.statusType()).isEqualTo(StatusType.WAIT);
    }

    @Test
    void 방을_상태를_변경할_수_있다() {
        //given
        String code = roomManager.create(new RoomInfo(5, 1, 1, 1));
        String basic = Base64.getEncoder().encodeToString((code + ":" + "player1").getBytes());
        RoomModifyRequest request = new RoomModifyRequest(StatusType.DAY);
        Room room = roomManager.findByCode(code);
        room.joinPlayer("player1");
        room.joinPlayer("player2");
        room.joinPlayer("player3");
        room.joinPlayer("player4");
        room.joinPlayer("player5");

        //when
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(request)
                .header("Authorization", "Basic " + basic)
                .when().patch("/rooms/status")
                .then().log().all()
                .statusCode(HttpStatus.OK.value());

        //then
        StatusType actual = room.getStatusType(Clock.systemDefaultZone().millis());
        Assertions.assertThat(actual).isEqualTo(StatusType.DAY_INTRO);
    }

    @Test
    void 인원부족시_게임을_시작할_수_없다() {
        //given
        String code = roomManager.create(new RoomInfo(3, 1, 1, 1));
        String basic = Base64.getEncoder().encodeToString((code + ":" + "player1").getBytes());
        RoomModifyRequest request = new RoomModifyRequest(StatusType.DAY);
        Room room = roomManager.findByCode(code);
        room.joinPlayer("player1");
        room.joinPlayer("player2");

        //when
        final ErrorResponse response = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(request)
                .header("Authorization", "Basic " + basic)
                .when().patch("/rooms/status")
                .then().log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                .as(ErrorResponse.class);

        // then
        Assertions.assertThat(response.errorCode()).isEqualTo(ExceptionCode.NOT_ENOUGH_PLAYER.getCode());
    }

    @Test
    void 방에_참가할_수_있다() {
        //given
        final String code = roomManager.create(new RoomInfo(5, 1, 1, 1));

        //when
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .when().get("/rooms?code=" + code + "&name=power")
                .then().log().all()
                .statusCode(HttpStatus.OK.value());

        //then
        Room room = roomManager.findByCode(code);
        Assertions.assertThat(room.getPlayer("power")).isNotNull();
    }

    @Test
    void 방이_꽉_차_있을때_참가에_실패한다() {
        //given
        final String code = roomManager.create(new RoomInfo(3, 1, 1, 1));
        final Room room = roomManager.findByCode(code);
        room.joinPlayer("A");
        room.joinPlayer("C");
        room.joinPlayer("D");
        //when
        final ErrorResponse response = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .when().get("/rooms?code=" + code + "&name=E")
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
        final String code = roomManager.create(new RoomInfo(3, 1, 1, 1));
        final Room room = roomManager.findByCode(code);
        room.joinPlayer("A");
        //when
        final ErrorResponse response = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .when().get("/rooms?code=" + code + "&name=A")
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
        final String code = roomManager.create(new RoomInfo(5, 1, 1, 1));
        final String basic = Base64.getEncoder().encodeToString((code + ":" + "power").getBytes());

        // when & then
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Basic " + basic)
                .when().get("/rooms/code")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .body("code", Matchers.equalTo(code));
    }

    @Test
    void 방의_코드를_검증_할_수_있다() {
        // given
        final String code = roomManager.create(new RoomInfo(5, 1, 1, 1));

        // when & then
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .when().get("/rooms/code/exist?code=" + code)
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .body("exist", Matchers.equalTo(false));
    }

    @Test
    void 생존한_사람이_방의_정보를_찾는다() {
        // given
        final String code = roomManager.create(new RoomInfo(5, 1, 1, 1));
        final String basic = Base64.getEncoder().encodeToString((code + ":" + "power").getBytes());
        final Room room = roomManager.findByCode(code);
        room.joinPlayer("power");

        // when & then
        final RoomInfoResponse response = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Basic " + basic)
                .when().get("/rooms/info")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(RoomInfoResponse.class);

        assertSoftly(
                softly -> {
                    softly.assertThat(response.startTime()).isNotNull();
                    softly.assertThat(response.endTime()).isNotNull();
                    softly.assertThat(response.myName()).isNotNull();
                    softly.assertThat(response.isAlive()).isTrue();
                    softly.assertThat(response.isMaster()).isTrue();
                    softly.assertThat(response.players().getFirst().job()).isNull();
                }
        );
    }

    @Test
    void 죽은사람이_방의_정보를_찾는다() {
        // given
        final String code = roomManager.create(new RoomInfo(5, 1, 1, 1));
        final String basic = Base64.getEncoder().encodeToString((code + ":" + "power").getBytes());
        final Room room = roomManager.findByCode(code);
        room.joinPlayer("power");
        room.getPlayer("power").kill();

        // when & then
        final RoomInfoResponse response = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Basic " + basic)
                .when().get("/rooms/info")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(RoomInfoResponse.class);

        assertSoftly(
                softly -> {
                    softly.assertThat(response.startTime()).isNotNull();
                    softly.assertThat(response.endTime()).isNotNull();
                    softly.assertThat(response.isAlive()).isFalse();
                    softly.assertThat(response.isMaster()).isTrue();
                    softly.assertThat(response.players().getFirst().job()).isNotNull();
                }
        );
    }

    @Test
    void 마피아가_방의_정보를_찾는다() {
        // given
        final String code = roomManager.create(new RoomInfo(5, 2, 1, 0));
        final Room room = roomManager.findByCode(code);
        room.joinPlayer("p1");
        room.joinPlayer("p2");
        room.joinPlayer("p3");
        room.joinPlayer("p4");
        room.joinPlayer("p5");
        room.modifyStatus(StatusType.DAY, Clock.systemDefaultZone());
        final Player mafia = findPlayer(room, JobType.MAFIA);
        final String mafiaBasic = Base64.getEncoder().encodeToString((code + ":" + mafia.getName()).getBytes());
        final Player doctor = findPlayer(room, JobType.DOCTOR);
        final String doctorBasic = Base64.getEncoder().encodeToString((code + ":" + doctor.getName()).getBytes());
        final Player citizen = findPlayer(room, JobType.CITIZEN);
        final String citizenBasic = Base64.getEncoder().encodeToString((code + ":" + citizen.getName()).getBytes());

        // when & then
        final Long mafiaCount = countMafiaResponse(mafiaBasic);
        final Long doctorCount = countMafiaResponse(doctorBasic);
        final Long citizenCount = countMafiaResponse(citizenBasic);
        assertSoftly(
                softly -> {
                    softly.assertThat(mafiaCount).isEqualTo(2);
                    softly.assertThat(doctorCount).isEqualTo(0);
                    softly.assertThat(citizenCount).isEqualTo(0);
                }
        );
    }

    private Player findPlayer(Room room, JobType jobType) {
        return room.getPlayers().values().stream()
                .filter(player -> player.getJobType().equals(jobType))
                .findFirst()
                .get();
    }

    private long countMafiaResponse(final String basic) {
        final RoomInfoResponse roomInfoResponse = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Basic " + basic)
                .when().get("/rooms/info")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(RoomInfoResponse.class);

        return roomInfoResponse.players().stream()
                .filter(response -> response.job() != null && response.job().equals(JobType.MAFIA))
                .count();
    }
}

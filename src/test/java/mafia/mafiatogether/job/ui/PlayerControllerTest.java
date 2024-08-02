package mafia.mafiatogether.job.ui;

import mafia.mafiatogether.global.ControllerTest;
import mafia.mafiatogether.job.domain.JobTargetRepository;
import mafia.mafiatogether.room.domain.Room;
import mafia.mafiatogether.room.domain.RoomInfo;
import mafia.mafiatogether.room.domain.RoomRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("NonAsciiCharacters")
class PlayerControllerTest extends ControllerTest {
//
//    @Autowired
//    private RoomRepository roomRepository;
//
//    @Autowired
//    private JobTargetRepository jobTargetRepository;
//
//
//    @Test
//    void 직업_기술을_사용한다() {
//        // given
//        String code = roomRepository.create(new RoomInfo(3, 1, 0, 0));
//        Room room = roomRepository.findByCode(code);
//        room.joinPlayer("t1");
//        room.joinPlayer("t2");
//        room.joinPlayer("t3");
//
//        room.modifyStatus(StatusType.DAY, Clock.systemDefaultZone().millis());
//
//        Player mafia = room.getPlayers().values().stream()
//                .filter(player -> player.getJobType().equals(JobType.MAFIA))
//                .findFirst()
//                .get();
//        String basic = Base64.getEncoder().encodeToString((code + ":" + mafia.getName()).getBytes());
//
//        // when & then
//        RestAssured.given().log().all()
//                .contentType(ContentType.JSON)
//                .body(Map.of("target", "t2"))
//                .header("Authorization", "Basic " + basic)
//                .when().post("/players/skill")
//                .then().log().all()
//                .statusCode(HttpStatus.OK.value());
//        Assertions.assertThat(room.getJobsTarget(mafia.getName())).isEqualTo("t2");
//    }
//
//    @Test
//    void 초기_마피아_타겟은_NULL_값이다() {
//        // given
//        String code = roomRepository.create(new RoomInfo(3, 1, 0, 0));
//        Room room = roomRepository.findByCode(code);
//        room.joinPlayer("t1");
//        room.joinPlayer("t2");
//        room.joinPlayer("t3");
//
//        room.modifyStatus(StatusType.DAY, Clock.systemDefaultZone().millis());
//
//        Player mafia = room.getPlayers().values().stream()
//                .filter(player -> player.getJobType().equals(JobType.MAFIA))
//                .findFirst()
//                .get();
//        String basic = Base64.getEncoder().encodeToString((code + ":" + mafia.getName()).getBytes());
//
//        // when & then
//        RestAssured.given().log().all()
//                .contentType(ContentType.JSON)
//                .body(Map.of("target", ""))
//                .header("Authorization", "Basic " + basic)
//                .when().get("/players/skill")
//                .then().log().all()
//                .statusCode(HttpStatus.OK.value());
//        Assertions.assertThat(room.getJobsTarget(mafia.getName())).isNull();
//    }
//
//    @Test
//    void 마피아가_빈문자열을_보낼시_아무도_죽지않는다() {
//        // given
//        String code = roomRepository.create(new RoomInfo(3, 1, 0, 0));
//        Room room = roomRepository.findByCode(code);
//        room.joinPlayer("t1");
//        room.joinPlayer("t2");
//        room.joinPlayer("t3");
//
//        room.modifyStatus(StatusType.DAY, Clock.systemDefaultZone().millis());
//
//        Player mafia = room.getPlayers().values().stream()
//                .filter(player -> player.getJobType().equals(JobType.MAFIA))
//                .findFirst()
//                .get();
//        String basic = Base64.getEncoder().encodeToString((code + ":" + mafia.getName()).getBytes());
//
//        // when & then
//        final String initTargetName = RestAssured.given().log().all()
//                .contentType(ContentType.JSON)
//                .body(Map.of("target", ""))
//                .header("Authorization", "Basic " + basic)
//                .when().get("/players/skill")
//                .then().log().all()
//                .statusCode(HttpStatus.OK.value())
//                .extract()
//                .body()
//                .jsonPath()
//                .getString("name");
//        RestAssured.given().log().all()
//                .contentType(ContentType.JSON)
//                .body(Map.of("target", ""))
//                .header("Authorization", "Basic " + basic)
//                .when().post("/players/skill")
//                .then().log().all()
//                .statusCode(HttpStatus.OK.value());
//        final String targetName = RestAssured.given().log().all()
//                .contentType(ContentType.JSON)
//                .body(Map.of("target", ""))
//                .header("Authorization", "Basic " + basic)
//                .when().get("/players/skill")
//                .then().log().all()
//                .statusCode(HttpStatus.OK.value())
//                .extract()
//                .body()
//                .jsonPath()
//                .getString("name");
//        assertSoftly(
//                softly -> {
//                    softly.assertThat(initTargetName).isNull();
//                    softly.assertThat(room.getJobsTarget(mafia.getName())).isBlank();
//                    softly.assertThat(targetName).isBlank();
//                }
//        );
//    }
//
//    @Test
//    void 이미_죽은사람에게_직업_기술_사용시_실패한다() {
//        // given
//        String code = roomRepository.create(new RoomInfo(3, 1, 0, 0));
//        Room room = roomRepository.findByCode(code);
//        room.joinPlayer("t1");
//        room.joinPlayer("t2");
//        room.joinPlayer("t3");
//
//        room.modifyStatus(StatusType.DAY, Clock.systemDefaultZone().millis());
//
//        Player mafia = room.getPlayers().values().stream()
//                .filter(player -> player.getJobType().equals(JobType.MAFIA))
//                .findFirst()
//                .get();
//        Player dead = room.getPlayers().values().stream()
//                .filter(player -> !player.equals(mafia))
//                .findFirst()
//                .get();
//        dead.kill();
//
//        String basic = Base64.getEncoder().encodeToString((code + ":" + mafia.getName()).getBytes());
//
//        // when & then
//        final ErrorResponse response = RestAssured.given().log().all()
//                .contentType(ContentType.JSON)
//                .body(Map.of("target", dead.getName()))
//                .header("Authorization", "Basic " + basic)
//                .when().post("/players/skill")
//                .then().log().all()
//                .statusCode(HttpStatus.BAD_REQUEST.value())
//                .extract()
//                .as(ErrorResponse.class);
//
//        Assertions.assertThat(response.errorCode()).isEqualTo(ExceptionCode.NOT_ALIVE_PLAYER.getCode());
//    }
//
//    @Test
//    void 방에_없는_사람에게_직업_기술_사용시_실패한다() {
//        // given
//        String code = roomRepository.create(new RoomInfo(3, 1, 0, 0));
//        Room room = roomRepository.findByCode(code);
//        room.joinPlayer("t1");
//        room.joinPlayer("t2");
//        room.joinPlayer("t3");
//
//        room.modifyStatus(StatusType.DAY, Clock.systemDefaultZone().millis());
//
//        Player mafia = room.getPlayers().values().stream()
//                .filter(player -> player.getJobType().equals(JobType.MAFIA))
//                .findFirst()
//                .get();
//
//        String basic = Base64.getEncoder().encodeToString((code + ":" + mafia.getName()).getBytes());
//
//        // when & then
//        final ErrorResponse response = RestAssured.given().log().all()
//                .contentType(ContentType.JSON)
//                .body(Map.of("target", "t4"))
//                .header("Authorization", "Basic " + basic)
//                .when().post("/players/skill")
//                .then().log().all()
//                .statusCode(HttpStatus.BAD_REQUEST.value())
//                .extract()
//                .as(ErrorResponse.class);
//
//        Assertions.assertThat(response.errorCode()).isEqualTo(ExceptionCode.INVALID_PLAYER.getCode());
//    }
//
//    @Test
//    void 직업을_조회한다() {
//        //given
//        String code = roomRepository.create(new RoomInfo(3, 1, 0, 0));
//        Room room = roomRepository.findByCode(code);
//        room.joinPlayer("t1");
//        room.joinPlayer("t2");
//        room.joinPlayer("t3");
//
//        room.modifyStatus(StatusType.DAY, Clock.systemDefaultZone().millis());
//        final Player citizen = room.getPlayers().values().stream()
//                .filter(player -> player.getJobType().equals(JobType.CITIZEN))
//                .findFirst()
//                .get();
//        String basic = Base64.getEncoder().encodeToString((code + ":" + citizen.getName()).getBytes());
//
//        // when & then
//        RestAssured.given().log().all()
//                .contentType(ContentType.JSON)
//                .header("Authorization", "Basic " + basic)
//                .when().get("/players/my/job")
//                .then().log().all()
//                .statusCode(HttpStatus.OK.value())
//                .body("job", equalTo("CITIZEN"));
//    }
}

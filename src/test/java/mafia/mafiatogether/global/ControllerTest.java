package mafia.mafiatogether.global;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.util.Base64;
import java.util.Map;
import mafia.mafiatogether.game.domain.GameRepository;
import mafia.mafiatogether.game.domain.PlayerCollection;
import mafia.mafiatogether.game.domain.status.StatusType;
import mafia.mafiatogether.job.domain.jobtype.JobType;
import mafia.mafiatogether.room.domain.Room;
import mafia.mafiatogether.room.domain.RoomInfo;
import mafia.mafiatogether.room.domain.RoomRepository;
import mafia.mafiatogether.vote.domain.VoteRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;

@Import(RedisTestConfig.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public abstract class ControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    protected RoomRepository roomRepository;

    @Autowired
    protected GameRepository gameRepository;

    @Autowired
    protected VoteRepository voteRepository;

    protected final static String CODE = "1234567890";
    protected final static String PLAYER1_NAME = "player1";
    protected final static String PLAYER2_NAME = "player2";
    protected final static String PLAYER3_NAME = "player3";
    protected final static String PLAYER4_NAME = "player4";
    protected final static String PLAYER5_NAME = "player5";
    protected String MAFIA1;
    protected String MAFIA2;
    protected String DOCTOR;
    protected String POLICE;
    protected String CITIZEN;

    @BeforeEach
    void setPort() {
        RestAssured.port = port;
    }

    protected void setRoom() {
        final Room room = Room.create(CODE, RoomInfo.of(5, 2, 1, 1));
        room.joinPlayer(PLAYER1_NAME);
        room.joinPlayer(PLAYER2_NAME);
        room.joinPlayer(PLAYER3_NAME);
        room.joinPlayer(PLAYER4_NAME);
        roomRepository.save(room);
    }

    protected void setGame() {
        Room room = roomRepository.findById(CODE).get();
        room.joinPlayer(PLAYER5_NAME);
        roomRepository.save(room);
        String basic = Base64.getEncoder().encodeToString((CODE + ":" + "player1").getBytes());
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(Map.of("statusType", StatusType.DAY_INTRO))
                .header("Authorization", "Basic " + basic)
                .when().patch("/rooms/status")
                .then().log().all()
                .statusCode(HttpStatus.OK.value());
        final PlayerCollection players = gameRepository.findById(CODE).get().getPlayers();
        MAFIA1 = players.getMafias().getFirst().getName();
        MAFIA2 = players.getMafias().getLast().getName();
        DOCTOR = findPlayerNameByJobType(players, JobType.DOCTOR);
        POLICE = findPlayerNameByJobType(players, JobType.POLICE);
        CITIZEN = findPlayerNameByJobType(players, JobType.CITIZEN);
    }

    private String findPlayerNameByJobType(PlayerCollection players, JobType jobType) {
        return players.getPlayers().stream()
                .filter(player -> player.getJobType().equals(jobType))
                .findFirst().get()
                .getName();
    }
    @AfterEach
    void clearTest() {
        voteRepository.deleteById(CODE);
        gameRepository.deleteById(CODE);
    }
}

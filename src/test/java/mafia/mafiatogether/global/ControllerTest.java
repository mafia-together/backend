package mafia.mafiatogether.global;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import mafia.mafiatogether.game.domain.GameRepository;
import mafia.mafiatogether.game.domain.PlayerCollection;
import mafia.mafiatogether.game.domain.status.StatusType;
import mafia.mafiatogether.job.domain.jobtype.JobType;
import mafia.mafiatogether.lobby.domain.Lobby;
import mafia.mafiatogether.lobby.domain.LobbyInfo;
import mafia.mafiatogether.lobby.domain.LobbyRepository;
import mafia.mafiatogether.vote.domain.VoteRepository;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.util.Base64;
import java.util.Map;

public abstract class ControllerTest extends RedisTestContainerSpringBootTest{

    @Autowired
    protected LobbyRepository lobbyRepository;

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


    protected void setLobby() {
        final Lobby lobby = Lobby.create(CODE, LobbyInfo.of(5, 2, 1, 1));
        lobby.joinPlayer(PLAYER1_NAME);
        lobby.joinPlayer(PLAYER2_NAME);
        lobby.joinPlayer(PLAYER3_NAME);
        lobby.joinPlayer(PLAYER4_NAME);
        lobbyRepository.save(lobby);
    }

    protected void setGame() {
        Lobby lobby = lobbyRepository.findById(CODE).get();
        lobby.joinPlayer(PLAYER5_NAME);
        lobbyRepository.save(lobby);
        String basic = Base64.getEncoder().encodeToString((CODE + ":" + "player1").getBytes());
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(Map.of("statusType", StatusType.DAY_INTRO))
                .header("Authorization", "Basic " + basic)
                .when().post("/games/start")
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

package mafia.mafiatogether.controller;

import io.restassured.RestAssured;
import java.time.Clock;
import mafia.mafiatogether.domain.Message;
import mafia.mafiatogether.domain.Player;
import mafia.mafiatogether.domain.Room;
import mafia.mafiatogether.domain.RoomInfo;
import mafia.mafiatogether.domain.status.StatusType;
import mafia.mafiatogether.redis.RedisTestConfig;
import mafia.mafiatogether.repository.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;

@Import(RedisTestConfig.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public abstract class ControllerTest {

    @Autowired
    protected RoomRepository roomRepository;

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    protected static final String CODE = "code";
    protected static Player player1;
    protected static Player player2;
    protected static Player player3;
    protected static Player player4;
    protected static Player player5;

    @BeforeEach
    void setRoom() {
        Room room = Room.create(CODE, RoomInfo.of(5, 2, 1, 1), Clock.systemDefaultZone().millis());
        room.joinPlayer("t1");
        room.joinPlayer("t2");
        room.joinPlayer("t3");
        room.joinPlayer("t4");
        room.joinPlayer("t5");
        player1 = room.getPlayer("t1");
        player2 = room.getPlayer("t2");
        player3 = room.getPlayer("t3");
        player4 = room.getPlayer("t4");
        player5 = room.getPlayer("t5");

        room.modifyStatus(StatusType.DAY, Clock.systemDefaultZone().millis());

        room.getChat().save(new Message(player1, "contents1", Clock.systemDefaultZone().millis()));
        room.getChat().save(new Message(player2, "contents2", Clock.systemDefaultZone().millis()));
        room.getChat().save(new Message(player3, "contents3", Clock.systemDefaultZone().millis()));
        room.getChat().save(new Message(player4, "contents4", Clock.systemDefaultZone().millis()));
        room.getChat().save(new Message(player5, "contents5", Clock.systemDefaultZone().millis()));
        roomRepository.save(room);
    }
}

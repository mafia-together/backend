package mafia.mafiatogether.global;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

@Import(RedisTestConfig.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {"application.scheduling-enable=false"})
public abstract class RedisTestContainerSpringBootTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setPort() {
        RestAssured.port = port;
    }
}

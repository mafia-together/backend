package mafia.mafiatogether;

import mafia.mafiatogether.global.RedisTestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(RedisTestConfig.class)
@SpringBootTest
class MafiaTogetherApplicationTests {

    @Test
    void contextLoads() {
    }

}
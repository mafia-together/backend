package mafia.mafiatogether.global;

import org.springframework.boot.test.context.TestConfiguration;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;

@TestConfiguration
public class RedisTestConfig {

    private static final String REDIS_DOCKER_IMAGE = "redis";

    static {
        GenericContainer<?> REDIS_CONTAINER = new GenericContainer<>(DockerImageName.parse(REDIS_DOCKER_IMAGE))
                .withExposedPorts(6379)
                .withStartupTimeout(Duration.ofSeconds(60))
                .withReuse(true);

        REDIS_CONTAINER.start();

        System.setProperty("spring.data.redis.host", REDIS_CONTAINER.getHost());
        System.setProperty("spring.data.redis.port", REDIS_CONTAINER.getMappedPort(6379).toString());
    }
}

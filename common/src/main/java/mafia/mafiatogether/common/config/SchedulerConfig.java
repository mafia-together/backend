package mafia.mafiatogether.common.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@ConditionalOnProperty(value = "application.scheduling-enable", havingValue = "true", matchIfMissing = false)
public class SchedulerConfig {
}

package mafia.mafiatogether;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MafiaTogetherApplication {

    public static void main(String[] args) {
        SpringApplication.run(MafiaTogetherApplication.class, args);
    }

}

package mafia.mafiatogether.domain;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import mafia.mafiatogether.domain.job.JobType;

@Getter
@RequiredArgsConstructor
public class Message {

    private final Player player;
    private final String contents;
    private final Timestamp timestamp;

    public static Message of(final Player player, final String contents) {
        return new Message(player, contents, Timestamp.valueOf(LocalDateTime.now()));
    }

    public String getName() {
        return player.getName();
    }

    public boolean isOwner(final String name) {
        return player.getName().equals(name);
    }

    public JobType getJobTypeForMafia() {
        return player.getJobType() == JobType.MAFIA ? JobType.MAFIA : null;
    }

    public JobType getJobTypeForCitizen(final String name) {
        return isOwner(name) ? player.getJobType() : null;
    }
}

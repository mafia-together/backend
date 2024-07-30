package mafia.mafiatogether.job.application.dto.response;

import mafia.mafiatogether.job.domain.Player;
import mafia.mafiatogether.job.domain.JobType;

public record PlayerResponse(
        String name,
        Boolean isAlive,
        JobType job
) {

    public static PlayerResponse forDead(final Player player) {
        return new PlayerResponse(
                player.getName(),
                player.isAlive(),
                player.getJobType()
        );
    }

    public static PlayerResponse forMyJob(final Player player) {
        return new PlayerResponse(
                player.getName(),
                player.isAlive(),
                player.getJobType()
        );
    }

    public static PlayerResponse forAlive(final Player player) {
        return new PlayerResponse(
                player.getName(),
                player.isAlive(),
                null
        );
    }

    public static PlayerResponse forMafia(final Player player) {
        return new PlayerResponse(
                player.getName(),
                player.isAlive(),
                player.isMafia() ? JobType.MAFIA : null
        );
    }
}

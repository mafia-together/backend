package mafia.mafiatogether.service.dto;

import mafia.mafiatogether.domain.Player;
import mafia.mafiatogether.domain.job.JobType;

public record PlayerResponse(
        String name,
        Boolean isAlive,
        JobType job
) {

    public static PlayerResponse forDead(final Player player) {
        return new PlayerResponse(
                player.getName(),
                player.isAlive(),
                player.getJobSymbol()
        );
    }

    public static PlayerResponse forAlive(final Player player) {
        return new PlayerResponse(
                player.getName(),
                player.isAlive(),
                null
        );
    }
}

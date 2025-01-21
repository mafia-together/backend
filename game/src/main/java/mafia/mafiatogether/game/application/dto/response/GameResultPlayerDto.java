package mafia.mafiatogether.game.application.dto.response;

import mafia.mafiatogether.game.domain.Player;

public record GameResultPlayerDto(
        String name,
        Boolean isAlive,
        String job
) {
    public static GameResultPlayerDto of(
            final Player player
    ) {
        return new GameResultPlayerDto(
                player.getName(),
                player.isAlive(),
                player.getJobType().name()
        );
    }
}

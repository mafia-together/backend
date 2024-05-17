package mafia.mafiatogether.service.dto;

import mafia.mafiatogether.domain.Player;

public record RoomResultPlayerDto(
        String name,
        Boolean isAlive,
        String job
) {
    public static RoomResultPlayerDto of(
            final Player player
    ) {
        return new RoomResultPlayerDto(
                player.getName(),
                player.isAlive(),
                player.getJobType().name()
        );
    }
}
